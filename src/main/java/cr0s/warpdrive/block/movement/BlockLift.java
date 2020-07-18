package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumLiftMode;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockLift extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumLiftMode> MODE = EnumProperty.create("mode", EnumLiftMode.class);
	
	public BlockLift(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(MODE, EnumLiftMode.INACTIVE)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityLift();
	}
}