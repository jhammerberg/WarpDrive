package cr0s.warpdrive.block;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumHorizontalSpinning;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;

public abstract class BlockAbstractHorizontalSpinningContainer extends BlockAbstractContainer {
	
	protected BlockAbstractHorizontalSpinningContainer(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties, registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.HORIZONTAL_SPINNING, EnumHorizontalSpinning.NORTH)
		               );
	}
}