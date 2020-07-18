package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockAcceleratorControlPoint extends BlockAbstractAccelerator {
	
	public BlockAcceleratorControlPoint(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, final boolean isSubBlock) {
		super(registryName, enumTier, null);
		
		if (isSubBlock) {
			return;
		}
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityAcceleratorControlPoint();
	}
}
