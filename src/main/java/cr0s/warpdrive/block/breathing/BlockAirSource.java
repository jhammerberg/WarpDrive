package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;

public class BlockAirSource extends BlockAbstractAir {
	
	public BlockAirSource(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.FACING);
	}
}