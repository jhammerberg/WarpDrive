package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractContainer;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockCloakingCore extends BlockAbstractContainer {
	
	public BlockCloakingCore(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityCloakingCore();
	}
}
