package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockIC2reactorLaserCooler extends BlockAbstractRotatingContainer {
	
	public BlockIC2reactorLaserCooler(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		ignoreFacingOnPlacement = true;
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityIC2reactorLaserMonitor();
	}
}
