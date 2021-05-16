package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.command.Commands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

public class CommandInvisible {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("invisible")
				        .requires(commandSource -> commandSource.hasPermissionLevel(4))
				        .then(Commands.argument("players", EntityArgument.players())
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            EntityArgument.getPlayers(commandContext, "players") ))
				             )
				
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getNodes().get(0).getNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              Collections.singleton(commandContext.getSource().asPlayer()) ))
		                                                                  );
		dispatcher.register(Commands.literal("bed").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent("/" + name + " (<playerName>)\n"
		                                                   + "playerName: name of the players to toggle invisibility from."), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final Collection<ServerPlayerEntity> serverPlayerEntities) {
		for (final ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
			commandSource.sendFeedback(new StringTextComponent(String.format("Toggling invisibility for %s",
			                                                                 serverPlayerEntity.getDisplayName().getFormattedText() )), true);
			final boolean wasInvisible = serverPlayerEntity.isInvisible();
			serverPlayerEntity.setInvisible(!wasInvisible);
			final boolean isInvisible = serverPlayerEntity.isInvisible();
			if (isInvisible) {
				Commons.addChatMessage(serverPlayerEntity, new StringTextComponent("You're now invisible"));
			} else {
				Commons.addChatMessage(serverPlayerEntity, new StringTextComponent("You're now visible"));
			}
		}
		return serverPlayerEntities.size();
	}
}