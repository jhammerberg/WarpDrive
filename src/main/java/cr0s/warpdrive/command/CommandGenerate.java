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

import java.util.Collection;
import java.util.Collections;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;

public class CommandGenerate {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("wgenerate")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .then(Commands.argument("structureGroup", StringArgumentType.string())
				                      .then(Commands.argument("structureName", StringArgumentType.string())
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          Collections.singleton(commandContext.getSource().asPlayer()),
				                                                                          StringArgumentType.getString(commandContext, "structureGroup"),
				                                                                          StringArgumentType.getString(commandContext, "structureName") ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            Collections.singleton(commandContext.getSource().asPlayer()),
				                                                            StringArgumentType.getString(commandContext, "structureGroup"),
				                                                            "" ))
				             )
				        .then(Commands.argument("entities", EntityArgument.entities())
				                      .then(Commands.argument("structureGroup", StringArgumentType.string())
				                                    .then(Commands.argument("structureName", StringArgumentType.string())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        EntityArgument.getEntities(commandContext, "entities"),
				                                                                                        StringArgumentType.getString(commandContext, "structureGroup"),
				                                                                                        StringArgumentType.getString(commandContext, "structureName") ))
				                                         )
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          EntityArgument.getEntities(commandContext, "entities"),
				                                                                          StringArgumentType.getString(commandContext, "structureGroup"),
				                                                                          "" ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            EntityArgument.getEntities(commandContext, "entities"),
				                                                            "ship",
				                                                            "" ))
				             )
				        .then(Commands.argument("position", BlockPosArgument.blockPos())
				                      .requires(commandSource -> commandSource.getEntity() != null)
				                      .then(Commands.argument("structureGroup", StringArgumentType.string())
				                                    .then(Commands.argument("structureName", StringArgumentType.string())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        commandContext.getSource().getEntity().world,
				                                                                                        BlockPosArgument.getBlockPos(commandContext, "position"),
				                                                                                        StringArgumentType.getString(commandContext, "structureGroup"),
				                                                                                        StringArgumentType.getString(commandContext, "structureName") ))
				                                         )
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          commandContext.getSource().getEntity().world,
				                                                                          BlockPosArgument.getBlockPos(commandContext, "position"),
				                                                                          StringArgumentType.getString(commandContext, "structureGroup"),
				                                                                          "" ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            Collections.singleton(commandContext.getSource().asPlayer()),
				                                                            "ship",
				                                                            "" ))
				             )
				                      
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              EntityArgument.getEntities(commandContext, "entities"),
				                                              "ship",
				                                              "" ))
		                                                                  );
		dispatcher.register(Commands.literal("generate").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent(String.format( "/%s <structure group> (<structure name>)\n"
		                                                                + "Structure groups are ship, station, astfield, %s",
		                                                                 name, StructureManager.getGroups().replace("\"", "") )), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final Collection<? extends Entity> entities,
	                           @Nonnull final String structureGroup,
	                           @Nonnull final String structureName) {
		for (final Entity entity : entities) {
			final World world = entity.getEntityWorld();
			BlockPos blockPos = entity.getPosition();
			
			execute(commandSource, world, blockPos, structureGroup, structureName);
		}
		return entities.size();
	}
	
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final World world,
	                           @Nonnull final BlockPos blockPos,
	                           @Nonnull final String structureGroup,
	                           @Nonnull final String structureName) {
		
		// Reject command, if target is not in space
		if (!CelestialObjectManager.isInSpace(world, blockPos.getX(), blockPos.getZ()) && (!"ship".equals(structureGroup))) {
			commandSource.sendErrorMessage(new TranslationTextComponent("warpdrive.command.only_in_space").setStyle(Commons.getStyleWarning()));
			return 0;
		}
		
		switch (structureGroup) {
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
			generateStructure(commandSource, EnumStructureGroup.ASTEROIDS_FIELDS.getName(), structureName, world, blockPos);
			break;
		
		case "gascloud":
			generateStructure(commandSource, EnumStructureGroup.GAS_CLOUDS.getName(), structureName, world, blockPos);
			break;
		
		default:
			generateStructure(commandSource, structureName, structureName, world, blockPos);
			break;
		}
		return 1;
	}
	
	private static void generateStructure(@Nonnull final CommandSource commandSource,
	                                      @Nonnull final String group, @Nonnull final String name,
	                                      @Nonnull final World world, @Nonnull final BlockPos blockPos) {
		final AbstractStructure structure = StructureManager.getStructure(world.rand, group, name);
		if (structure == null) {
			commandSource.sendErrorMessage(new TranslationTextComponent("Invalid %1$s:%2$s, try one of the followings:\n%3$s",
			                                                            group, name, StructureManager.getStructureNames(group) )
					                               .setStyle(Commons.getStyleWarning()) );
		} else {
			WarpDrive.logger.info(String.format("Generating %s:%s %s",
			                                    group, structure.getName(), Commons.format(world, blockPos)));
			structure.place(world, world.rand, blockPos);
			
			// do a weak attempt to extract player (ideally, it should be delayed after generation, but that's too complicated)
			if (commandSource.getEntity() instanceof ServerPlayerEntity) {
				int newY = blockPos.getY() + 1;
				while ( newY < 256
				     && !world.isAirBlock(new BlockPos(blockPos.getX(), newY, blockPos.getZ())) ) {
					newY++;
				}
				final ServerPlayerEntity entityServerPlayer = (ServerPlayerEntity) commandSource.getEntity();
				entityServerPlayer.setPosition(entityServerPlayer.getPosX(), newY, entityServerPlayer.getPosZ());
			}
		}
	}
}