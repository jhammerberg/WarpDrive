package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.EnumTransporterState;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockTransporterCore extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumTransporterState> VARIANT = EnumProperty.create("variant", EnumTransporterState.class);
	
	public BlockTransporterCore(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(VARIANT, EnumTransporterState.DISABLED)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityTransporterCore();
	}
}