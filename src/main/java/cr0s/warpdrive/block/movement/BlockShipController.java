package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumShipCommand;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;

public class BlockShipController extends BlockAbstractContainer {
	
	public static final EnumProperty<EnumShipCommand> COMMAND = EnumProperty.create("command", EnumShipCommand.class);
	
	public BlockShipController(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(COMMAND, EnumShipCommand.OFFLINE)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(COMMAND);
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockController(this);
	}
}