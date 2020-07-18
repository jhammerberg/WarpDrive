package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatBiblioCraft implements IBlockTransformer {
	
	private static Class<?> classBiblioBlock;
	
	public static void register() {
		try {
			classBiblioBlock = Class.forName("jds.bibliocraft.blocks.BiblioBlock");
			
			WarpDriveConfig.registerBlockTransformer("BiblioCraft", new CompatBiblioCraft());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBiblioBlock.isInstance(blockState.getBlock());
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
	
	private static final int[]  rotAngle       = {  1,  2,  3,  0 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// tile entity rotations
		if (nbtTileEntity.contains("angle")) {
			final int angle = nbtTileEntity.getInt("angle");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("angle", rotAngle[angle]);
				return blockState;
			case 2:
				nbtTileEntity.putInt("angle", rotAngle[rotAngle[angle]]);
				return blockState;
			case 3:
				nbtTileEntity.putInt("angle", rotAngle[rotAngle[rotAngle[angle]]]);
				return blockState;
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
