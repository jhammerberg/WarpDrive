package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.data.EnumGlobalRegionType;
import cr0s.warpdrive.data.GlobalRegionManager;
import cr0s.warpdrive.data.GlobalRegion;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class CommandFind extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "wfind";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return "/" + getName() + " (<shipName>)"
		       + "\nshipName: name of the ship to find. Exact casing is preferred.";
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		// parse arguments
		final String nameToken;
		final ServerPlayerEntity entityPlayer = commandSource instanceof ServerPlayerEntity ? (ServerPlayerEntity) commandSource : null;
		if (args.length == 0) {
			if (entityPlayer == null) {
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				return;
			}
			final GlobalRegion globalRegion = GlobalRegionManager.getNearest(EnumGlobalRegionType.SHIP, entityPlayer.world, entityPlayer.getPosition());
			if (globalRegion != null) {
				Commons.addChatMessage(commandSource, new StringTextComponent(String.format("Ship '%s' found in %s",
				                                                                            globalRegion.name,
				                                                                            globalRegion.getFormattedLocation())));
			} else {
				Commons.addChatMessage(commandSource, new StringTextComponent(String.format("No ship found in %s",
				                                                                            Commons.format(entityPlayer.world))));
			}
			return;
			
		} else if (args.length == 1) {
			if ( args[0].equalsIgnoreCase("help")
			  || args[0].equalsIgnoreCase("?") ) {
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				return;
			}
			nameToken = args[0];
			
		} else {
			final StringBuilder nameBuilder = new StringBuilder();
			for (final String param : args) {
				if (nameBuilder.length() > 0) {
					nameBuilder.append(" ");
				}
				nameBuilder.append(param);
			}
			nameToken = nameBuilder.toString();
		}
		
		final String result = GlobalRegionManager.listByKeyword(EnumGlobalRegionType.SHIP, nameToken);
		Commons.addChatMessage(commandSource, new StringTextComponent(result));
	}
}
