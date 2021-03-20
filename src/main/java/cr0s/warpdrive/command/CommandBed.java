package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

public class CommandBed {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("wbed")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .then(Commands.argument("players", EntityArgument.players())
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            EntityArgument.getPlayers(commandContext, "players") ))
				             )
				        
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              Collections.singleton(commandContext.getSource().asPlayer()) ))
		                                                                  );
		dispatcher.register(Commands.literal("bed").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent( "/" + name + " (<playerName>)\n"
		                                                  + "playerName: name of the player home to find. Exact casing is required."), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final Collection<ServerPlayerEntity> serverPlayerEntities) {
		// parse arguments
		for (final ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
			final BlockPos bedLocation = serverPlayerEntity.getBedLocation(serverPlayerEntity.world.getDimension().getType());
			if (bedLocation == null) {
				Commons.addChatMessage(serverPlayerEntity, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.no_bed_to_teleport_to_self",
				                                                             Commons.format(serverPlayerEntity.world) ));
				if (commandSource.getEntity() != serverPlayerEntity) {
					commandSource.sendErrorMessage(new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.no_bed_to_teleport_to_other",
					                                                 serverPlayerEntity.getName().getFormattedText(),
					                                                 Commons.format(serverPlayerEntity.world)));
				}
				continue;
			}
			
			final Block block = serverPlayerEntity.world.getBlockState(bedLocation).getBlock();
			if (!(block instanceof BedBlock)) {
				Commons.addChatMessage(serverPlayerEntity, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.lost_bed_can_t_teleport_self",
				                                                         Commons.format(serverPlayerEntity.world) ));
				if (commandSource.getEntity() != serverPlayerEntity) {
					commandSource.sendErrorMessage(new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.lost_bed_can_t_teleport_other",
					                                                 serverPlayerEntity.getName().getFormattedText(),
					                                                 Commons.format(serverPlayerEntity.world) ));
				}
				continue;
			}
			
			serverPlayerEntity.setPositionAndUpdate(bedLocation.getX() + 0.5D, bedLocation.getY() + 0.5D, bedLocation.getZ() + 0.5D);
			
			if (commandSource.getEntity() == serverPlayerEntity) {
				commandSource.sendFeedback(new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_to_x",
				                                             Commons.format(serverPlayerEntity.world, bedLocation) ),
				                           true );
			} else {
				Commons.addChatMessage(serverPlayerEntity, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_by_x_to_y",
				                                                             commandSource,
				                                                             Commons.format(serverPlayerEntity.world, bedLocation) ));
				commandSource.sendFeedback(new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_player_x_to_y",
				                                             serverPlayerEntity.getName().getFormattedText(),
				                                             Commons.format(serverPlayerEntity.world, bedLocation) ),
				                           true );
			}
		}
		
		return serverPlayerEntities.size();
	}
}
