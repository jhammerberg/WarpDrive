package cr0s.warpdrive.block;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;

public class BlockLaserMedium extends BlockAbstractContainer {
	
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 7);
	
	public BlockLaserMedium(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(LEVEL, 0)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LEVEL);
	}
}