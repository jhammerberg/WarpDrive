package cr0s.warpdrive.compat;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatUndergroundBiomes implements IBlockTransformer {
	
	private static Class<?> classBlockUBStoneButton;
	private static Class<?> classBlockUBStoneStairs;
	
	public static void register() {
		try {
			classBlockUBStoneButton = Class.forName("exterminatorjeff.undergroundbiomes.common.block.button.UBStoneButton");
			classBlockUBStoneStairs = Class.forName("exterminatorjeff.undergroundbiomes.common.block.stairs.UBStoneStairs");
			
			WarpDriveConfig.registerBlockTransformer("UndergroundBiomesConstructs", new CompatUndergroundBiomes());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockUBStoneButton.isInstance(blockState.getBlock())
		    || classBlockUBStoneStairs.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                          final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
	}
	
	private static final Map<String, String> rotFacingNames;
	static {
		final Map<String, String> map = new HashMap<>();
		map.put("north", "east");
		map.put("east", "south");
		map.put("south", "west");
		map.put("west", "north");
		rotFacingNames = Collections.unmodifiableMap(map);
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		return blockState;
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return;
		}
		
		// Stairs and buttons are using different blocks id when rotating
		final ResourceLocation registryNameOld = blockState.getBlock().getRegistryName();
		assert registryNameOld != null;
		final String pathOld = registryNameOld.getPath();
		final int indexSeparator = pathOld.lastIndexOf('_');
		if (indexSeparator < 0) {
			WarpDrive.logger.error(String.format("Invalid registry name for UndergroundBiomes blocks, unable to proceed with rotation: %s",
			                                     registryNameOld));
			return;
		}
		final String facing = pathOld.substring(indexSeparator + 1);
		if (!rotFacingNames.containsKey(facing)) {// up or down => no rotation needed
			return;
		}
		final String pathNoFacing = pathOld.substring(0, indexSeparator + 1);
		final String facingNew;
		switch (rotationSteps) {
		case 1:
			facingNew = rotFacingNames.get(facing);
			break;
		case 2:
			facingNew = rotFacingNames.get(rotFacingNames.get(facing));
			break;
		case 3:
			facingNew = rotFacingNames.get(rotFacingNames.get(rotFacingNames.get(facing)));
			break;
		default:
			facingNew = facing;
			break;
		}
		final ResourceLocation registryNameNew = new ResourceLocation(registryNameOld.getNamespace(), pathNoFacing + facingNew);
		final Block blockNew = ForgeRegistries.BLOCKS.getValue(registryNameNew);
		if (blockNew == Blocks.AIR) {
			WarpDrive.logger.error(String.format("Invalid new registry name for UndergroundBiomes, unable to proceed with rotation: old is %s, new is %s",
			                                     registryNameOld, registryNameNew));
			return;
		}
		final BlockState blockStateNew = blockNew.getStateFromMeta(blockState.getBlock().getMetaFromState(blockState));
		world.setBlockState(blockPos, blockStateNew, 2);
	}
}
