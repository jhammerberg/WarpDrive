package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockParticlesInjector extends BlockAcceleratorControlPoint {
	
	public BlockParticlesInjector(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier, true);
	}
}