package cr0s.warpdrive.block.collection;

import cr0s.warpdrive.block.BlockAbstractContainer;

import cr0s.warpdrive.data.EnumMiningLaserMode;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockMiningLaser extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumMiningLaserMode> MODE = EnumProperty.create("mode", EnumMiningLaserMode.class);
	
	public BlockMiningLaser(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);

		setDefaultState(getDefaultState()
				                .with(MODE, EnumMiningLaserMode.INACTIVE)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityMiningLaser();
	}
}