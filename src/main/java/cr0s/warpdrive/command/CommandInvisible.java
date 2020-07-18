package cr0s.warpdrive.command;

import cr0s.warpdrive.WarpDrive;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class CommandInvisible extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "invisible";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return "/invisible [player]";
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		PlayerEntity player = commandSource instanceof PlayerEntity ? (PlayerEntity) commandSource : null;
		
		if (args.length >= 1) {
			WarpDrive.logger.info(String.format("/invisible: setting invisible to %s", args[0]));
			
			// get an online player by name
			final List<ServerPlayerEntity> entityPlayers = server.getPlayerList().getPlayers();
			for (final ServerPlayerEntity entityPlayer : entityPlayers) {
				if ( entityPlayer.getName().getUnformattedComponentText().equalsIgnoreCase(args[0])
				  || entityPlayer.getDisplayName().getUnformattedComponentText().equalsIgnoreCase(args[0]) ) {
					player = entityPlayer;
				}
			}
		}
		
		if (player == null) {
			return;
		}
		
		// Toggle invisibility
		player.setInvisible(!player.isInvisible());
	}
}
