package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractContainer;

import cr0s.warpdrive.data.EnumRadarMode;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockRadar extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumRadarMode> MODE = EnumProperty.create("mode", EnumRadarMode.class);
	
	public BlockRadar(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(MODE, EnumRadarMode.INACTIVE)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityRadar();
	}
}
