package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class CommandBed extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "wbed";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return getName() + " (<playerName>)"
		       + "\nplayerName: name of the player home to find. Exact casing is required.";
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		// parse arguments
		ServerPlayerEntity[] entityPlayerMPs = null;
		if (args.length == 0) {
			if (commandSource instanceof ServerPlayerEntity) {
				entityPlayerMPs = new ServerPlayerEntity[1];
				entityPlayerMPs[0] = (ServerPlayerEntity) commandSource;
			} else {
				Commons.addChatMessage(commandSource, getPrefix().appendSibling(new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.player_required")));
				return;
			}
			
		} else if (args.length == 1) {
			if ( args[0].equalsIgnoreCase("help")
			  || args[0].equalsIgnoreCase("?") ) {
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				return;
			}
			if ( commandSource instanceof ServerPlayerEntity
			  && !((ServerPlayerEntity) commandSource).isCreative() ) {
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				return;
			}
			
			final ServerPlayerEntity[] entityPlayerMPs_found = Commons.getOnlinePlayerByNameOrSelector(commandSource, args[0]);
			if (entityPlayerMPs_found != null) {
				entityPlayerMPs = entityPlayerMPs_found;
			} else if (commandSource instanceof ServerPlayerEntity) {
				entityPlayerMPs = new ServerPlayerEntity[1];
				entityPlayerMPs[0] = (ServerPlayerEntity) commandSource;
			} else {
				Commons.addChatMessage(commandSource, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.player_not_found",
				                                                        args[0] ));
				return;
			}
		}
		
		assert entityPlayerMPs != null;
		for (final ServerPlayerEntity entityServerPlayer : entityPlayerMPs) {
			final BlockPos bedLocation = entityServerPlayer.getBedLocation(entityServerPlayer.world.getDimension().getType());
			if (bedLocation == null) {
				Commons.addChatMessage(entityServerPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.no_bed_to_teleport_to_self",
				                                                         Commons.format(entityServerPlayer.world) ));
				if (args.length != 0) {
					Commons.addChatMessage(commandSource, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.no_bed_to_teleport_to_other",
					                                                        entityServerPlayer.getName().getFormattedText(),
					                                                        Commons.format(entityServerPlayer.world) ));
				}
				continue;
			}
			
			final Block block = entityServerPlayer.world.getBlockState(bedLocation).getBlock();
			if (!(block instanceof BedBlock)) {
				Commons.addChatMessage(entityServerPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.lost_bed_can_t_teleport_self",
				                                                         Commons.format(entityServerPlayer.world) ));
				if (args.length != 0) {
					Commons.addChatMessage(commandSource, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.command.lost_bed_can_t_teleport_other",
					                                                        entityServerPlayer.getName().getFormattedText(),
					                                                        Commons.format(entityServerPlayer.world) ));
				}
				continue;
			}
			
			entityServerPlayer.setPositionAndUpdate(bedLocation.getX() + 0.5D, bedLocation.getY() + 0.5D, bedLocation.getZ() + 0.5D);
			
			if (args.length == 0) {
				Commons.addChatMessage(entityServerPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_to_x",
				                                                         Commons.format(entityServerPlayer.world, bedLocation) ));
			} else {
				Commons.addChatMessage(entityServerPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_by_x_to_y",
				                                                         commandSource,
				                                                         Commons.format(entityServerPlayer.world, bedLocation) ));
				Commons.addChatMessage(commandSource, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.command.teleporting_player_x_to_y",
				                                                        entityServerPlayer.getName().getFormattedText(),
				                                                        Commons.format(entityServerPlayer.world, bedLocation) ));
			}
		}
	}
}
