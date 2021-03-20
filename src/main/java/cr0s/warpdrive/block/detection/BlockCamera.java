package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockCamera extends BlockAbstractRotatingContainer {
	
	public BlockCamera(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
	}
}