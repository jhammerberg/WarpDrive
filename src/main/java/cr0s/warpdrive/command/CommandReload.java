package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import com.mojang.brigadier.CommandDispatcher;

public class CommandReload {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("wreload")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .executes((commandContext) -> execute(commandContext.getSource()))
		                   );
	}
	
	private static int execute(@Nonnull final CommandSource commandSource) {
		WarpDriveConfig.reload(commandSource.getServer());
		commandSource.sendFeedback(new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.configuration_reloaded"), true);
		commandSource.sendFeedback(new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.liability_warning"), true);
		return 1;
	}
}