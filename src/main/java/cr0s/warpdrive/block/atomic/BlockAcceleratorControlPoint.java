package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer.Builder;

import javax.annotation.Nonnull;

public class BlockAcceleratorControlPoint extends BlockAbstractAccelerator {
	
	protected BlockAcceleratorControlPoint(@Nonnull final Block.Properties blockProperties,
	                                    @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties, registryName, enumTier);
	}
	
	public BlockAcceleratorControlPoint(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		this(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.ACTIVE);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}