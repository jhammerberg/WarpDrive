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

public class CompatIndustrialCraft2 implements IBlockTransformer {
	
	private static Class<?> classIC2tileEntity;
	private static boolean isExperimental = false;
	
	public static void register() {
		try {
			try {
				// first, try IC2 Experimental
				classIC2tileEntity = Class.forName("ic2.core.block.TileEntityBlock");
				isExperimental = true;
			} catch (final ClassNotFoundException exception) {
				// then, try IC2 Classic
				classIC2tileEntity = Class.forName("ic2.core.block.base.tile.TileEntityBlock");
			}
			WarpDriveConfig.registerBlockTransformer("ic2", new CompatIndustrialCraft2());
		} catch (final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classIC2tileEntity.isInstance(tileEntity);
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z, final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
	}
	
	private static final short[] mrotFacing    = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		if (nbtTileEntity.getBoolean("targetSet")) {
			final int targetX = nbtTileEntity.getInt("targetX");
			final int targetY = nbtTileEntity.getInt("targetY");
			final int targetZ = nbtTileEntity.getInt("targetZ");
			if (transformation.isInside(targetX, targetY, targetZ)) {
				final BlockPos chunkCoordinates = transformation.apply(targetX, targetY, targetZ);
				nbtTileEntity.putInt("targetX", chunkCoordinates.getX());
				nbtTileEntity.putInt("targetY", chunkCoordinates.getY());
				nbtTileEntity.putInt("targetZ", chunkCoordinates.getZ());
			}
		}
		
		if ( rotationSteps == 0
		  || !nbtTileEntity.contains("facing")) {
			return blockState;
		}
		
		final short facing = nbtTileEntity.getShort("facing");
		switch (rotationSteps) {
		case 1:
			nbtTileEntity.putShort("facing", mrotFacing[facing]);
			return blockState;
		case 2:
			nbtTileEntity.putShort("facing", mrotFacing[mrotFacing[facing]]);
			return blockState;
		case 3:
			nbtTileEntity.putShort("facing", mrotFacing[mrotFacing[mrotFacing[facing]]]);
			return blockState;
		default:
			return blockState;
		}
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		// IC2 Classic has its own approach to detect energy blocks and connect to them
		// we need to force a reconnection by simulating chunk unloading and a new 'first tick'
		if ( !isExperimental
		  && tileEntity != null ) {
			tileEntity.onChunkUnloaded();
			tileEntity.onLoad();
		}
	}
}
