package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumDecorativeType;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.GameData;

public class BlockDecorative extends BlockAbstractBase {
	
	private static final ItemStack[] itemStackCache = new ItemStack[EnumDecorativeType.values().length];
	
	private final boolean isGlass;
	
	public BlockDecorative(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final EnumDecorativeType enumDecorativeType) {
		super(enumDecorativeType == EnumDecorativeType.GLASS ?
		      getDefaultProperties(Material.GLASS)
				      .sound(SoundType.GLASS)
				      .notSolid()
				      .hardnessAndResistance(1.5F)
		                                                     :
		      getDefaultProperties(Material.IRON)
				      .hardnessAndResistance(1.5F),
		      registryName, enumTier);
		
		isGlass = enumDecorativeType == EnumDecorativeType.GLASS;
	}
	
	@Nonnull
	public static Item getItem(@Nonnull final EnumDecorativeType enumDecorativeType) {
		final int indexType = enumDecorativeType.ordinal();
		return GameData.getBlockItemMap().get(WarpDrive.blockDecoratives[indexType]);
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final EnumDecorativeType enumDecorativeType) {
		final int indexType = enumDecorativeType.ordinal();
		if (itemStackCache[indexType] == null) {
			itemStackCache[indexType] = new ItemStack(WarpDrive.blockDecoratives[indexType], 1);
		}
		return itemStackCache[indexType];
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumDecorativeType enumDecorativeType, final int amount) {
		return new ItemStack(WarpDrive.blockDecoratives[enumDecorativeType.ordinal()], amount);
	}
	
	@Override
	public boolean propagatesSkylightDown(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader,
	                                      @Nonnull final BlockPos blockPos) {
		return isGlass
		    || super.propagatesSkylightDown(blockState, blockReader, blockPos);
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(@Nonnull final BlockState blockState, @Nonnull final BlockState blockStateAdjacent, @Nonnull final Direction side) {
		// note: in theory, this method is only called when block is notSolid(), but we still check, just in case...
		return (isGlass && blockStateAdjacent.getBlock() == this)
		    || super.isSideInvisible(blockState, blockStateAdjacent, side);
	}
}