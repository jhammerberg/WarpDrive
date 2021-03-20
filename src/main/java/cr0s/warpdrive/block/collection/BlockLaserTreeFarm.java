package cr0s.warpdrive.block.collection;

import cr0s.warpdrive.block.BlockAbstractContainer;

import cr0s.warpdrive.data.EnumLaserTreeFarmMode;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;

public class BlockLaserTreeFarm extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumLaserTreeFarmMode> MODE = EnumProperty.create("mode", EnumLaserTreeFarmMode.class);
	
	public BlockLaserTreeFarm(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);

		setDefaultState(getStateContainer().getBaseState()
				                .with(MODE, EnumLaserTreeFarmMode.INACTIVE)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(MODE);
	}
}