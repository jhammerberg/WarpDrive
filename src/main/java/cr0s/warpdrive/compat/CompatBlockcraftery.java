package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatBlockcraftery implements IBlockTransformer {
	
	private static Class<?> classBlockCornerBase;
	private static Class<?> classBlockSlantBase;
	
	public static void register() {
		try {
			classBlockCornerBase = Class.forName("epicsquid.mysticallib.block.BlockCornerBase"); // 0-3 direction | 4 up => 0 1 2 3
			classBlockSlantBase = Class.forName("epicsquid.mysticallib.block.BlockSlantBase"); // 0-3 direction | 4 5 6 vertical
			
			WarpDriveConfig.registerBlockTransformer("Blockcraftery", new CompatBlockcraftery());
		} catch(final ClassNotFoundException | RuntimeException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockCornerBase.isInstance(blockState.getBlock())
		    || classBlockSlantBase.isInstance(blockState.getBlock());
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
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] rotCorner           = {  1,  2,  3,  0,  5,  6,  7,  4,  8,  9, 10, 11, 12, 13, 14, 15 }; // 0 1 2 3 / 4 5 6 7
	private static final byte[] rotSlope            = {  3,  2,  0,  1,  5,  6,  7,  4, 11, 10,  8,  9, 12, 13, 14, 15 }; // 0 3 1 2 / 4 5 6 7 / 8 11 9 10
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// corner
		if (classBlockCornerBase.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotCorner[metadata];
			case 2:
				return rotCorner[rotCorner[metadata]];
			case 3:
				return rotCorner[rotCorner[rotCorner[metadata]]];
			default:
				return blockState;
			}
		}
		
		// slope
		if (classBlockSlantBase.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotSlope[metadata];
			case 2:
				return rotSlope[rotSlope[metadata]];
			case 3:
				return rotSlope[rotSlope[rotSlope[metadata]]];
			default:
				return blockState;
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
