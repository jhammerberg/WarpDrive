package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.data.EnumGlobalRegionType;
import cr0s.warpdrive.data.GlobalRegionManager;
import cr0s.warpdrive.data.GlobalRegion;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;

public class CommandFind {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("wfind")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .then(Commands.argument("nameToken", StringArgumentType.string())
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            StringArgumentType.getString(commandContext, "nameToken") ))
				             )
				        
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              null ))
		                                                                  );
		dispatcher.register(Commands.literal("find").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent( "/" + name + " (<shipName>)\n"
		                                                  + "shipName: name of the ship to find. Exact casing is preferred.\n"
		                                                  + "Defaults to the current ship." ), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource, @Nullable final String nameToken) {
		// parse arguments
		final ServerPlayerEntity serverPlayerEntity = commandSource.getEntity() instanceof ServerPlayerEntity ? (ServerPlayerEntity) commandSource.getEntity() : null;
		if (nameToken == null) {
			if (serverPlayerEntity == null) {
				commandSource.sendErrorMessage(new StringTextComponent("warpdrive.command.player_required"));
				return 0;
			}
			final GlobalRegion globalRegion = GlobalRegionManager.getNearest(EnumGlobalRegionType.SHIP, serverPlayerEntity.world, serverPlayerEntity.getPosition());
			if (globalRegion != null) {
				commandSource.sendFeedback(new StringTextComponent(String.format("Ship '%s' found in %s",
				                                                                 globalRegion.name,
				                                                                 globalRegion.getFormattedLocation())), true);
			} else {
				commandSource.sendFeedback(new StringTextComponent(String.format("No ship found in %s",
				                                                                 Commons.format(serverPlayerEntity.world))), true);
			}
			return 1;
			
		}
		
		final String result = GlobalRegionManager.listByKeyword(EnumGlobalRegionType.SHIP, nameToken);
		commandSource.sendFeedback(new StringTextComponent(result), true);
		return 1;
	}
}