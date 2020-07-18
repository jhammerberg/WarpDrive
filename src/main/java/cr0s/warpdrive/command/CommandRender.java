package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class CommandRender extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "wrender";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return "/" + getName();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		// parse arguments
		if (args.length > 0) {
			Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
			return;
		}
		
		final ServerPlayerEntity entityPlayer = commandSource instanceof ServerPlayerEntity ? (ServerPlayerEntity) commandSource : null;
		
		// validate context
		if (entityPlayer == null) {
			Commons.addChatMessage(commandSource, new WarpDriveText().append(getPrefix())
			                                                         .append(Commons.getStyleWarning(), "warpdrive.command.player_required") );
			return;
		}
		
		// evaluate sub command
		final World world = entityPlayer.getEntityWorld();
		BlockPos blockPos = entityPlayer.getPosition();
		
		//noinspection ConstantConditions
		if (world == null || blockPos == null) {
			Commons.addChatMessage(commandSource, new WarpDriveText().append(getPrefix())
			                                                         .append(Commons.getStyleWarning(), "warpdrive.command.invalid_location") );
			return;
		}
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock().isAir(blockState, world, blockPos)) {
			blockPos = blockPos.down();
			blockState = world.getBlockState(blockPos);
		}
		
		Commons.addChatMessage(commandSource, new WarpDriveText().append(getPrefix())
		                                                         .appendInLine(Commons.getStyleCorrect(), "Dumping render details %s",
		                                                                       Commons.format(world, blockPos) ) );
		final Block block = blockState.getBlock();
		
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "Blockstate is %s",
		                                                                 Commons.getChatValue(blockState.toString()) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "Light opacity is %s",
		                                                                 Commons.getChatValue(blockState.getOpacity(world, blockPos)) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "isAir is %s",
		                                                                 Commons.getChatValue(block.isAir(blockState, world, blockPos)) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "isNormalCube is %s",
		                                                                 Commons.getChatValue(block.isNormalCube(blockState, world, blockPos)) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "isSolid is %s / causesSuffocation is %s",
		                                                                 Commons.getChatValue(block.isSolid(blockState)),
		                                                                 Commons.getChatValue(block.causesSuffocation(blockState, world, blockPos)) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "Material isOpaque %s / Material blocksMovement %s",
		                                                                 Commons.getChatValue(blockState.getMaterial().isOpaque()),
		                                                                 Commons.getChatValue(blockState.getMaterial().blocksMovement()) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "Material isLiquid %s / Material isSolid %s",
		                                                                 Commons.getChatValue(blockState.getMaterial().isLiquid()),
		                                                                 Commons.getChatValue(blockState.getMaterial().isSolid()) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "isOpaqueCube is %s  / renderType is %s",
		                                                                 Commons.getChatValue(blockState.isOpaqueCube(world, blockPos)),
		                                                                 Commons.getChatValue(block.getRenderType(blockState).toString()) ));
		Commons.addChatMessage(commandSource, new WarpDriveText().append(Commons.getStyleNormal(), "isSideSolid D %s, U %s, N %s, S %s, W %s, E %s",
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.DOWN)),
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.UP)),
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.NORTH)),
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.SOUTH)),
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.WEST)),
		                                                                 Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.EAST)) ));
	}
}
