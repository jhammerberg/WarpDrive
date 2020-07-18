package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockParticlesCollider extends BlockAbstractAccelerator {
	
	public BlockParticlesCollider(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier, null);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
}
