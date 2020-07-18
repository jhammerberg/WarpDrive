package cr0s.warpdrive.block.passive;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockHighlyAdvancedMachine extends BlockAbstractBase {
	
	public BlockHighlyAdvancedMachine(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
		      .hardnessAndResistance(5.0F),
		      registryName, enumTier );
	}
}