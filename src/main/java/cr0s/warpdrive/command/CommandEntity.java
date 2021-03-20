package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.WarpDriveText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;

public class CommandEntity {
	
	private static final List<String> entitiesNoRemoval = Arrays.asList(
			"item.EntityItemFrame_",
			"item.EntityPainting_"
			);
	private static final List<String> entitiesNoCount = Arrays.asList(
			"item.EntityItemFrame_",
			"item.EntityPainting_"
			);
	
	private static final Style styleFound  = new Style().setColor(TextFormatting.WHITE);
	private static final Style styleNumber = new Style().setColor(TextFormatting.WHITE);
	private static final Style styleFactor = new Style().setColor(TextFormatting.DARK_GRAY);
	private static final Style styleName   = new Style().setColor(TextFormatting.LIGHT_PURPLE);
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("wentity")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .then(Commands.argument("radius", IntegerArgumentType.integer(0))
				                      .then(Commands.argument("filter", StringArgumentType.string())
				                                    .then(Commands.argument("kill", BooleanArgumentType.create())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        IntegerArgumentType.getInteger(commandContext, "radius"),
				                                                                                        StringArgumentType.getString(commandContext, "filter"),
				                                                                                        BooleanArgumentType.getBoolean(commandContext, "kill") )))
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          IntegerArgumentType.getInteger(commandContext, "radius"),
				                                                                          StringArgumentType.getString(commandContext, "filter"),
				                                                                          false ))
				                           )
				                      .then(Commands.literal("*")
				                                    .then(Commands.argument("kill", BooleanArgumentType.create())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        IntegerArgumentType.getInteger(commandContext, "radius"),
				                                                                                        "*",
				                                                                                        BooleanArgumentType.getBoolean(commandContext, "kill") )))
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          IntegerArgumentType.getInteger(commandContext, "radius"),
				                                                                          "*",
				                                                                          false ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            IntegerArgumentType.getInteger(commandContext, "radius"),
				                                                            "*",
				                                                            false ))
				             )
				        .then(Commands.literal("*")
				                      .then(Commands.argument("filter", StringArgumentType.string())
				                                    .then(Commands.argument("kill", BooleanArgumentType.create())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        0,
				                                                                                        StringArgumentType.getString(commandContext, "filter"),
				                                                                                        BooleanArgumentType.getBoolean(commandContext, "kill") )))
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          0,
				                                                                          StringArgumentType.getString(commandContext, "filter"),
				                                                                          false ))
				                           )
				                      .then(Commands.literal("*")
				                                    .then(Commands.argument("kill", BooleanArgumentType.create())
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        0,
				                                                                                        "*",
				                                                                                        BooleanArgumentType.getBoolean(commandContext, "kill") )))
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          0,
				                                                                          "*",
				                                                                          false ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            0,
				                                                            "*",
				                                                            false ))
				             )
				        
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              20,
				                                              "*",
				                                              false ))
		                                                                  );
		dispatcher.register(Commands.literal("entity").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent("/" + name + " (<radius> (<filter> (<kill?>)))\n"
			+ "radius: * or 0 to check all loaded in current world, 1+ blocks around player\n"
			+ "filter: * to get all, anything else is a case insensitive string\n"
			+ "kill: yes/y/1 to kill, anything else is ignored"), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource, final int radius, @Nonnull final String filter, final boolean doKill) {
		
		commandSource.sendFeedback(new StringTextComponent(String.format("Entity radius %d filter '*%s*' kill %s", radius, filter, doKill)), true);
		
		final List<Entity> entities;
		if (radius <= 0) {
			final ServerWorld world;
			if (commandSource.getEntity() != null) {
				world = (ServerWorld) commandSource.getEntity().world;
			} else {
				world = Commons.getOrCreateWorldServer(DimensionType.OVERWORLD);
			}
			assert world != null;
			entities = world.getEntities().collect(Collectors.toList());
			
		} else {
			if (commandSource.getEntity() == null) {
				commandSource.sendErrorMessage(new StringTextComponent("warpdrive.command.player_required"));
				return 0;
			}
			final Entity entity = commandSource.getEntity();
			entities = entity.world.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(
					Math.floor(entity.getPosX()    ), Math.floor(entity.getPosY()    ), Math.floor(entity.getPosZ()    ),
					Math.floor(entity.getPosX() + 1), Math.floor(entity.getPosY() + 1), Math.floor(entity.getPosZ() + 1)).grow(radius, radius, radius));
		}
		final HashMap<String, Integer> counts = new HashMap<>(entities.size());
		int count = 0;
		for (final Entity entity : entities) {
			String name = entity.getClass().getCanonicalName();
			if (name == null) {
				name = "-null-";
			} else {
				name = name.replaceAll("net\\.minecraft\\.entity\\.", "") + "_";
			}
			if (filter.isEmpty() && !entitiesNoCount.isEmpty()) {
				boolean isCountable = true;
				for (final String entityNoCount : entitiesNoCount) {
					if (name.contains(entityNoCount)) {
						isCountable = false;
						break;
					}
				}
				if (!isCountable) {
					continue;
				}
			}
			if (filter.isEmpty() || name.contains(filter)) {
				// update statistics
				count++;
				if (!counts.containsKey(name)) {
					counts.put(name, 1);
				} else {
					counts.put(name, counts.get(name) + 1);
				}
				if (!filter.isEmpty()) {
					if (count == 1) {
						commandSource.sendFeedback(new WarpDriveText(styleFound, "warpdrive.command.found_title"), true);
					}
					commandSource.sendFeedback(new WarpDriveText(styleFound, "warpdrive.command.found_line",
					                                             entity), true);
				}
				// remove entity
				if (doKill && !entity.isInvulnerableTo(WarpDrive.damageAsphyxia)) {
					if (!entitiesNoRemoval.isEmpty()) {
						boolean isRemovable = true;
						for (final String entityNoRemoval : entitiesNoRemoval) {
							if (name.contains(entityNoRemoval)) {
								isRemovable = false;
								break;
							}
						}
						if (!isRemovable) {
							continue;
						}
					}
					entity.remove();
				}
			}
		}
		if (count == 0) {
			commandSource.sendFeedback(new TranslationTextComponent("warpdrive.command.no_matching_entity",
			                                                        radius).setStyle(Commons.getStyleWarning()), true);
			return 0;
		}
		
		ITextComponent textComponent = new TranslationTextComponent("warpdrive.command.x_matching_entities", count, radius).setStyle(Commons.getStyleCorrect());
		commandSource.sendFeedback(textComponent, true);
		if (counts.size() < 10) {
			for (final Entry<String, Integer> entry : counts.entrySet()) {
				textComponent = new StringTextComponent(entry.getValue().toString()).setStyle(styleNumber)
				                                                                    .appendSibling(new StringTextComponent("x").setStyle(styleFactor))
				                                                                    .appendSibling(new StringTextComponent(entry.getKey()).setStyle(styleName));
				textComponent.getStyle().setColor(TextFormatting.WHITE);
				commandSource.sendFeedback(textComponent, true);
			}
		} else {
			textComponent = new StringTextComponent("");
			boolean isFirst = true;
			for (final Entry<String, Integer> entry : counts.entrySet()) {
				if (isFirst) {
					isFirst = false;
				} else {
					textComponent.appendSibling(new StringTextComponent(", ").setStyle(styleFactor));
				}
				textComponent.appendSibling(new StringTextComponent(entry.getValue().toString()).setStyle(styleNumber))
				             .appendSibling(new StringTextComponent("x").setStyle(styleFactor))
				             .appendSibling(new StringTextComponent(entry.getKey()).setStyle(styleName));
			}
			commandSource.sendFeedback(textComponent, true);
		}
		return count;
	}
}