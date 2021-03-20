package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.brigadier.CommandDispatcher;

public class CommandRender {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("wrender")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2)
				                                && commandSource.getEntity() instanceof ServerPlayerEntity)
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              commandContext.getSource().asPlayer()))
		                   );
	}
	
	@SuppressWarnings("deprecation")
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final ServerPlayerEntity serverPlayerEntity) {
		
		// evaluate sub command
		final World world = serverPlayerEntity.getEntityWorld();
		BlockPos blockPos = serverPlayerEntity.getPosition();
		
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock().isAir(blockState, world, blockPos)) {
			blockPos = blockPos.down();
			blockState = world.getBlockState(blockPos);
		}
		
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleCorrect(), "Dumping render details %s",
		                                                      Commons.format(world, blockPos) ), true);
		final Block block = blockState.getBlock();
		
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "Blockstate is %s",
		                                                      Commons.getChatValue(blockState.toString()) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "Light opacity is %s",
		                                                      Commons.getChatValue(blockState.getOpacity(world, blockPos)) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "isAir is %s",
		                                                      Commons.getChatValue(block.isAir(blockState, world, blockPos)) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "isNormalCube is %s",
		                                                      Commons.getChatValue(block.isNormalCube(blockState, world, blockPos)) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "isSolid is %s / causesSuffocation is %s",
		                                                      Commons.getChatValue(block.isSolid(blockState)),
		                                                      Commons.getChatValue(block.causesSuffocation(blockState, world, blockPos)) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "Material isOpaque %s / Material blocksMovement %s",
		                                                      Commons.getChatValue(blockState.getMaterial().isOpaque()),
		                                                      Commons.getChatValue(blockState.getMaterial().blocksMovement()) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "Material isLiquid %s / Material isSolid %s",
		                                                      Commons.getChatValue(blockState.getMaterial().isLiquid()),
		                                                      Commons.getChatValue(blockState.getMaterial().isSolid()) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "isOpaqueCube is %s  / renderType is %s",
		                                                      Commons.getChatValue(blockState.isOpaqueCube(world, blockPos)),
		                                                      Commons.getChatValue(block.getRenderType(blockState).toString()) ), true);
		commandSource.sendFeedback(new WarpDriveText().append(Commons.getStyleNormal(), "isSideSolid D %s, U %s, N %s, S %s, W %s, E %s",
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.DOWN)),
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.UP)),
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.NORTH)),
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.SOUTH)),
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.WEST)),
		                                                      Commons.getChatValue(blockState.isSolidSide(world, blockPos, Direction.EAST)) ), true);
		
		return 1;
	}
}