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

public class CompatThermalExpansion implements IBlockTransformer {
	
	private static Class<?> classBlockTEBase;
	
	public static void register() {
		try {
			classBlockTEBase = Class.forName("cofh.thermalexpansion.block.BlockTEBase");
			
			WarpDriveConfig.registerBlockTransformer("thermalexpansion", new CompatThermalExpansion());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockTEBase.isInstance(blockState.getBlock());
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
	
	private static final short[] mrot            = {  0,  1,  5,  4,  2,  3 };
	
	private static final int[]   rotFacing       = {  0,  1,  5,  4,  2,  3 };
	
	private byte[] rotate_byteArray(final byte rotationSteps, final byte[] data) {
		final byte[] newData = data.clone();
		for (int index = 0; index < data.length; index++) {
			switch (rotationSteps) {
			case 1:
				newData[mrot[index]] = data[index];
				break;
			case 2:
				newData[mrot[mrot[index]]] = data[index];
				break;
			case 3:
				newData[mrot[mrot[mrot[index]]]] = data[index];
				break;
			default:
				break;
			}
		}
		return newData;
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// machines
		if (nbtTileEntity.contains("Facing")) {
			final int facing = nbtTileEntity.getInt("Facing");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("Facing", rotFacing[facing]);
				break;
			case 2:
				nbtTileEntity.putInt("Facing", rotFacing[rotFacing[facing]]);
				break;
			case 3:
				nbtTileEntity.putInt("Facing", rotFacing[rotFacing[rotFacing[facing]]]);
				break;
			default:
				break;
			}
		}
		
		if (nbtTileEntity.contains("SideCache")) {
			nbtTileEntity.putByteArray("SideCache", rotate_byteArray(rotationSteps, nbtTileEntity.getByteArray("SideCache")));
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
