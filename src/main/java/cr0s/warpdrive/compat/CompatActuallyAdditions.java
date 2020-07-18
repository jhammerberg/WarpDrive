package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatActuallyAdditions implements IBlockTransformer {
	
	private static Class<?> classBlockInputter;
	
	public static void register() {
		try {
			classBlockInputter = Class.forName("de.ellpeck.actuallyadditions.mod.blocks.BlockInputter");
			
			WarpDriveConfig.registerBlockTransformer("ActuallyAdditions", new CompatActuallyAdditions());
		} catch(final ClassNotFoundException | RuntimeException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockInputter.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                          final BlockState blockState, final TileEntity tileEntity) {
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
	}
	
	/*
	inputter & advanced inputter
		SideToPull -1 / 0 / 1 / 2 3 4 5
		SideToPut  -1 / 0 / 1 / 2 3 4 5
	 */
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] rotInputter         = {  0,  1,  3,  4,  5,  2 }; // -1 / 0 / 1 / 2 3 4 5
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// inputter
		if (nbtTileEntity.contains("SideToPull")) {
			final int side = nbtTileEntity.getInt("SideToPull");
			if (side > 1) {
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putInt("SideToPull", rotInputter[side]);
					break;
				case 2:
					nbtTileEntity.putInt("SideToPull", rotInputter[rotInputter[side]]);
					break;
				case 3:
					nbtTileEntity.putInt("SideToPull", rotInputter[rotInputter[rotInputter[side]]]);
					break;
				default:
					break;
				}
			}
		}
		if (nbtTileEntity.contains("SideToPut")) {
			final int side = nbtTileEntity.getInt("SideToPut");
			if (side > 1) {
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putInt("SideToPut", rotInputter[side]);
					break;
				case 2:
					nbtTileEntity.putInt("SideToPut", rotInputter[rotInputter[side]]);
					break;
				case 3:
					nbtTileEntity.putInt("SideToPut", rotInputter[rotInputter[rotInputter[side]]]);
					break;
				default:
					break;
				}
			}
		}
		
		return blockState;
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		// nothing to do
	}
}
