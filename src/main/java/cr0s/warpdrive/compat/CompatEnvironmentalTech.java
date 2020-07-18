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

public class CompatEnvironmentalTech implements IBlockTransformer {
	
	private static Class<?> classETBlockSlave;
	
	public static void register() {
		try {
			classETBlockSlave = Class.forName("com.valkyrieofnight.et.m_multiblocks.block.ETBlockSlave");
			
			WarpDriveConfig.registerBlockTransformer("Environmental Tech", new CompatEnvironmentalTech());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classETBlockSlave.isInstance(blockState.getBlock());
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
	
	// (no rotation, only offset to controller)
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		if ( nbtTileEntity.contains("has_controller")
		  && nbtTileEntity.contains("cx")
		  && nbtTileEntity.contains("cy")
		  && nbtTileEntity.contains("cz") ) {
			final boolean hasController = nbtTileEntity.getBoolean("has_controller");
			if (hasController) {
				final BlockPos target = transformation.apply(nbtTileEntity.getInt("cx"), nbtTileEntity.getInt("cy"), nbtTileEntity.getInt("cz"));
				nbtTileEntity.putInt("cx", target.getX());
				nbtTileEntity.putInt("cy", target.getY());
				nbtTileEntity.putInt("cz", target.getZ());
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
