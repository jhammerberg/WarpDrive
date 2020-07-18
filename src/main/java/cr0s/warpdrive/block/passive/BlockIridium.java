package cr0s.warpdrive.block.passive;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockIridium extends BlockAbstractBase {
	
	public BlockIridium(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				.hardnessAndResistance(3.4F, 360.0F * 3 / 5),
		      registryName, enumTier );
	}
}