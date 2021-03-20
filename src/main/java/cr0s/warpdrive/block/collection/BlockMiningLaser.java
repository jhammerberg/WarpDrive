package cr0s.warpdrive.block.collection;

import cr0s.warpdrive.block.BlockAbstractContainer;

import cr0s.warpdrive.data.EnumMiningLaserMode;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;

public class BlockMiningLaser extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumMiningLaserMode> MODE = EnumProperty.create("mode", EnumMiningLaserMode.class);
	
	public BlockMiningLaser(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);

		setDefaultState(getStateContainer().getBaseState()
				                .with(MODE, EnumMiningLaserMode.INACTIVE)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(MODE);
	}
}