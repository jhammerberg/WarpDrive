package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.structures.AbstractStructure;
import cr0s.warpdrive.config.structures.StructureManager;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.data.EnumStructureGroup;
import cr0s.warpdrive.world.WorldGenSmallShip;
import cr0s.warpdrive.world.WorldGenStation;

import javax.annotation.Nonnull;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class CommandGenerate extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "generate";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return String.format("/%s <structure group> (<structure name>)\nStructure groups are ship, station, astfield, %s",
		                     getName(),
		                     StructureManager.getGroups().replace("\"", "") );
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		final World world = commandSource.getEntityWorld();
		BlockPos blockPos = commandSource.getPosition();
		
		//noinspection ConstantConditions
		if (world == null || blockPos == null) {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.invalid_location").setStyle(Commons.getStyleWarning())));
			return;
		}
		
		if (args.length <= 0 || args.length == 3 || args.length > 5) {
			Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
			return;
		}
		
		if (args.length > 3) {
			blockPos = new BlockPos(
			    AdjustAxis(blockPos.getX(), args[args.length - 3]),
			    AdjustAxis(blockPos.getY(), args[args.length - 2]),
			    AdjustAxis(blockPos.getZ(), args[args.length - 1]));
		}
		
		final String structure = args[0];
		
		// Reject command, if player is not in space
		if (!CelestialObjectManager.isInSpace(world, blockPos.getX(), blockPos.getZ()) && (!"ship".equals(structure))) {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.only_in_space").setStyle(Commons.getStyleWarning())));
			return;
		}
		
		if (EffectiveSide.get() == LogicalSide.SERVER) {
			final String name = (args.length > 1) ? args[1] : null;
			switch (structure) {
			case "":
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				break;
				
			case "ship":
				WarpDrive.logger.info(String.format("/generate: generating NPC ship %s",
				                                    Commons.format(world, blockPos)));
				new WorldGenSmallShip(false, true).place(world, world.rand, blockPos);
				break;
				
			case "station":
				WarpDrive.logger.info(String.format("/generate: generating station %s",
				                                    Commons.format(world, blockPos)));
				new WorldGenStation(false).place(world, world.rand, blockPos);
				break;
				
			case "astfield":
				generateStructure(commandSource, EnumStructureGroup.ASTEROIDS_FIELDS.getName(), name, world, blockPos);
				break;
				
			case "gascloud":
				generateStructure(commandSource, EnumStructureGroup.GAS_CLOUDS.getName(), name, world, blockPos);
				break;
				
			default:
				generateStructure(commandSource, structure, name, world, blockPos);
				break;
			}
		}
	}
	
	private int AdjustAxis(final int axis, final String param) {
		if (param.isEmpty() || param.equals("~")) {
			return axis;
		}
		
		if (param.charAt(0) == '~') {
			return axis + Integer.parseInt(param.substring(1));
		} else {
			return Integer.parseInt(param);
		}
	}
	
	private void generateStructure(final ICommandSource commandSource, final String group, final String name, final World world, final BlockPos blockPos) {
		final AbstractStructure structure = StructureManager.getStructure(world.rand, group, name);
		if (structure == null) {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("Invalid %1$s:%2$s, try one of the followings:\n%3$s",
			                                                                                             group, name, StructureManager.getStructureNames(group)).setStyle(Commons.getStyleWarning())));
		} else {
			WarpDrive.logger.info(String.format("/generate: Generating %s:%s %s",
			                                    group, structure.getName(), Commons.format(world, blockPos)));
			structure.place(world, world.rand, blockPos);
			
			// do a weak attempt to extract player (ideally, it should be delayed after generation, but that's too complicated)
			if (commandSource instanceof ServerPlayerEntity) {
				int newY = blockPos.getY() + 1;
				while ( newY < 256
				     && !world.isAirBlock(new BlockPos(blockPos.getX(), newY, blockPos.getZ())) ) {
					newY++;
				}
				final ServerPlayerEntity entityServerPlayer = (ServerPlayerEntity) commandSource;
				entityServerPlayer.setPosition(entityServerPlayer.getPosX(), newY, entityServerPlayer.getPosZ());
			}
		}
	}
}
