package cr0s.warpdrive.config;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.FastSetBlockState;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IXmlRepresentableUnit;
import cr0s.warpdrive.data.JumpBlock;

import javax.annotation.Nonnull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockPos;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.w3c.dom.Element;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Represents a single filler block.
 **/
public class Filler implements IXmlRepresentableUnit {
	
	public static final Filler DEFAULT;
	static {
		DEFAULT = new Filler();
		DEFAULT.name           = "-default-";
		DEFAULT.blockState     = Blocks.AIR.getDefaultState();
		DEFAULT.tagCompound    = null;
	}
	
	private String name;
	public BlockState blockState;
	public CompoundNBT tagCompound = null;
	
	private int cache_hashCode;
	
	@Nonnull
	@Override
	public String getName() {
		return name;
	}
	
	public Filler() {
	}
	
	@Override
	public boolean loadFromXmlElement(final Element element) throws InvalidXmlException {
		
		// Check there is a block name
		if (!element.hasAttribute("block")) {
			throw new InvalidXmlException(String.format("Filler %s is missing a block attribute!",
			                                            element));
		}
		
		final String stringBlockState = element.getAttribute("blockState");
		
		blockState = Commons.readBlockStateFromString(stringBlockState);
		if (blockState.getBlock() == Blocks.AIR) {
			WarpDrive.logger.warn(String.format("Skipping missing block %s",
			                                    stringBlockState ));
			return false;
		}
		
		// Get nbt attribute, default to null/none
		tagCompound = null;
		final String stringNBT = element.getAttribute("nbt");
		if (!stringNBT.isEmpty()) {
			try {
				tagCompound = JsonToNBT.getTagFromJson(stringNBT);
			} catch (final CommandSyntaxException exception) {
				WarpDrive.logger.error(exception.getMessage());
				throw new InvalidXmlException(String.format("Invalid nbt for block %s: %s",
				                                            stringBlockState, stringNBT));
			}
		}
		
		name = stringBlockState + (tagCompound == null ? "" : "{" + tagCompound + "}");
		
		return true;
	}
	
	public boolean loadFromName(final String nameToLoad) {
		final Pattern patternNameToLoadWithoutNBT = Pattern.compile("(.*)@(\\d*)");
		final Pattern patternNameToLoadWithNBT = Pattern.compile("(.*)@(\\d*)(\\{.*)");
		final boolean hasNBT = nameToLoad.contains("{");
		final Matcher matcher = hasNBT ? patternNameToLoadWithNBT.matcher(nameToLoad) : patternNameToLoadWithoutNBT.matcher(nameToLoad);
		if (!matcher.matches()) {
			throw new RuntimeException(String.format("Failed to load filler from name %s: unrecognized format",
			                                         nameToLoad));
		}
		
		final String stringBlockState = matcher.group(1);
		blockState = Commons.readBlockStateFromString(stringBlockState);
		if (blockState.getBlock() == Blocks.AIR) {
			WarpDrive.logger.warn(String.format("Failed to load filler from name %s: block %s is missing",
			                                    nameToLoad, stringBlockState ));
			return false;
		}
		
		// Get nbt attribute, default to null/none
		tagCompound = null;
		final String stringNBT = hasNBT ? matcher.group(3) : "";
		if (!stringNBT.isEmpty()) {
			try {
				tagCompound = JsonToNBT.getTagFromJson(stringNBT);
			} catch (final CommandSyntaxException exception) {
				WarpDrive.logger.error(exception.getMessage());
				throw new RuntimeException(String.format("Failed to load filler from name %s: invalid nbt %s",
				                                         nameToLoad, stringNBT));
			}
		}
		
		name = stringBlockState + (tagCompound == null ? "" : "{" + tagCompound + "}");
		
		return true;
	}
	
	public void setBlock(final World world, final BlockPos blockPos) {
		try {
			FastSetBlockState.setBlockStateNoLight(world, blockPos, blockState, 2);
		} catch (final Throwable throwable) {
			WarpDrive.logger.error(String.format("Throwable detected in Filler.setBlock(%s), check your configuration for that block!",
			                                     getName() ));
			throw throwable;
		}
		
		if (tagCompound != null) {
			// get tile entity
			final TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity == null) {
				WarpDrive.logger.error(String.format("No TileEntity found for Filler %s %s, unable to apply NBT properties",
				                                     getName(),
				                                     Commons.format(world, blockPos) ));
				return;
			}
			
			// save default NBT
			final CompoundNBT nbtTagCompoundTileEntity = new CompoundNBT();
			tileEntity.write(nbtTagCompoundTileEntity);
			
			// overwrite with customization
			for (final String key : tagCompound.keySet()) {
				final INBT value = tagCompound.get(key);
				assert value != null;
				nbtTagCompoundTileEntity.put(key, value);
			}
			
			// reload
			tileEntity.onChunkUnloaded();
			tileEntity.read(nbtTagCompoundTileEntity);
			tileEntity.validate();
			tileEntity.markDirty();
			
			JumpBlock.refreshBlockStateOnClient(world, blockPos);
		}
	}
	
	@Override
	public IXmlRepresentableUnit constructor() {
		return new Filler();
	}
	
	@Override
	public boolean equals(final Object object) {
		return object instanceof Filler
			&& (blockState == null || blockState.equals(((Filler) object).blockState))
			&& (tagCompound == null || tagCompound.equals(((Filler)object).tagCompound));
	}
	
	@Override
	public String toString() {
		return "Filler(" + blockState + ")";
	}
	
	@Override
	public int hashCode() {
		if (cache_hashCode == 0) {
			cache_hashCode = blockState.toString().hashCode() + (tagCompound == null ? 0 : tagCompound.hashCode() * 4096 * 16);
		}
		return cache_hashCode;
	}
}
