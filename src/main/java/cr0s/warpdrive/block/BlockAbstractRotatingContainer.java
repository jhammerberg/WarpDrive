package cr0s.warpdrive.block;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;

public abstract class BlockAbstractRotatingContainer extends BlockAbstractContainer {
	
	protected BlockAbstractRotatingContainer(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties, registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
}
