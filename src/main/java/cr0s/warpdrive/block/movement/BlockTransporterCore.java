package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.EnumTransporterState;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;

public class BlockTransporterCore extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumTransporterState> VARIANT = EnumProperty.create("variant", EnumTransporterState.class);
	
	public BlockTransporterCore(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(VARIANT, EnumTransporterState.DISABLED)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(VARIANT);
	}
}