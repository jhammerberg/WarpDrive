package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;

public class BlockEnanReactorLaser extends BlockAbstractContainer {
	
	public BlockEnanReactorLaser(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				.hardnessAndResistance(5.0F, 60.0F),
		      registryName, enumTier);
		
		ignoreFacingOnPlacement = true;
		setDefaultState(getStateContainer().getBaseState()
		                                   .with(BlockProperties.ACTIVE, false)
		                                   .with(BlockProperties.FACING_HORIZONTAL, Direction.NORTH)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.ACTIVE);
		builder.add(BlockProperties.FACING_HORIZONTAL);
	}
}