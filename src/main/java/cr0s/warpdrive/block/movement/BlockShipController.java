package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumShipCommand;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockShipController extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumShipCommand> COMMAND = EnumProperty.create("command", EnumShipCommand.class);
	
	public BlockShipController(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(COMMAND, EnumShipCommand.OFFLINE)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityShipController();
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockController(this);
	}
}