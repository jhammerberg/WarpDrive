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

public class CompatEvilCraft implements IBlockTransformer {
	
	private static Class<?> classBlockConfigurableBlockContainer;
	
	public static void register() {
		try {
			classBlockConfigurableBlockContainer = Class.forName("org.cyclops.cyclopscore.config.configurable.ConfigurableBlockContainer");
			WarpDriveConfig.registerBlockTransformer("evilcraft", new CompatEvilCraft());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockConfigurableBlockContainer.isInstance(blockState.getBlock());
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
	
	private static final int[]  rotRotation       = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// tile entity rotations
		if (nbtTileEntity.contains("rotatable")) {
			if (!nbtTileEntity.getBoolean("rotatable")) {
				return blockState;
			}
			final int rotation = nbtTileEntity.getInt("rotation");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("rotation", rotRotation[rotation]);
				return blockState;
			case 2:
				nbtTileEntity.putInt("rotation", rotRotation[rotRotation[rotation]]);
				return blockState;
			case 3:
				nbtTileEntity.putInt("rotation", rotRotation[rotRotation[rotRotation[rotation]]]);
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
