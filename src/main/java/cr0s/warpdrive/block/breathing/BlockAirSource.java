package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.util.Direction;

public class BlockAirSource extends BlockAbstractAir {
	
	public BlockAirSource(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
}