package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockParticlesInjector extends BlockAcceleratorControlPoint {
	
	public BlockParticlesInjector(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier, true);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityParticlesInjector();
	}
}
