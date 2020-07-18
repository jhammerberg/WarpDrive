package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.data.CelestialObject;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.data.VectorI;

import javax.annotation.Nonnull;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class CommandSpace extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "space";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return "/space (<playerName>) ([overworld|nether|end|theend|space|hyper|hyperspace|<dimensionId>])";
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		// set defaults
		DimensionType dimensionTypeTarget = null;
		
		ServerPlayerEntity[] entityPlayerMPs = null;
		if (commandSource instanceof ServerPlayerEntity) {
			entityPlayerMPs = new ServerPlayerEntity[1];
			entityPlayerMPs[0] = (ServerPlayerEntity) commandSource;
		}
		
		// parse arguments
		//noinspection StatementWithEmptyBody
		if (args.length == 0) {
			// nop
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				Commons.addChatMessage(commandSource,  new StringTextComponent(getUsage(commandSource)));
				return;
			}
			
			final ServerPlayerEntity[] entityPlayerMPs_found = Commons.getOnlinePlayerByNameOrSelector(commandSource, args[0]);
			if (entityPlayerMPs_found != null) {
				entityPlayerMPs = entityPlayerMPs_found;
			} else if (commandSource instanceof PlayerEntity) {
				dimensionTypeTarget = CelestialObjectManager.getDimensionType(args[0], (PlayerEntity) commandSource);
			} else {
				Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.player_not_found", args[0]).setStyle(Commons.getStyleWarning())));
				return;
			}
			
		} else if (args.length == 2) {
			final ServerPlayerEntity[] entityPlayerMPs_found = Commons.getOnlinePlayerByNameOrSelector(commandSource, args[0]);
			if (entityPlayerMPs_found != null) {
				entityPlayerMPs = entityPlayerMPs_found;
			} else {
				Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.player_not_found", args[0]).setStyle(Commons.getStyleWarning())));
				return;
			}
			dimensionTypeTarget = CelestialObjectManager.getDimensionType(args[1], entityPlayerMPs[0]);
			
		} else {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.too_many_arguments", args.length).setStyle(Commons.getStyleWarning())));
			return;
		}
		
		// check player
		if (entityPlayerMPs == null || entityPlayerMPs.length <= 0) {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.player_not_found", args[0]).setStyle(Commons.getStyleWarning())));
			return;
		}
		
		for (final ServerPlayerEntity entityServerPlayer : entityPlayerMPs) {
			// toggle between overworld and space if no dimension was provided
			int xTarget = MathHelper.floor(entityServerPlayer.getPosX());
			int yTarget = Math.min(255, Math.max(0, MathHelper.floor(entityServerPlayer.getPosY())));
			int zTarget = MathHelper.floor(entityServerPlayer.getPosZ());
			final CelestialObject celestialObjectCurrent = CelestialObjectManager.get(entityServerPlayer.world, (int) entityServerPlayer.getPosX(), (int) entityServerPlayer.getPosZ());
			if (dimensionTypeTarget == null) {
				if (celestialObjectCurrent == null) {
					Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.player_in_unknown_dimension",
					                                                                                             entityServerPlayer.getName().getFormattedText(),
					                                                                                             entityServerPlayer.world.getDimension().getType().getRegistryName() )
							                                                                .setStyle(Commons.getStyleWarning())));
					Commons.addChatMessage(commandSource, new TranslationTextComponent("warpdrive.command.specify_explicit_dimension")
							                                      .setStyle(Commons.getStyleCorrect()));
					continue;
				}
				if ( celestialObjectCurrent.isSpace()
				  || celestialObjectCurrent.isHyperspace() ) {
					// in space or hyperspace => move to closest child
					final CelestialObject celestialObjectChild = CelestialObjectManager.getClosestChild(entityServerPlayer.world, (int) entityServerPlayer.getPosX(), (int) entityServerPlayer.getPosZ());
					if (celestialObjectChild == null) {
						dimensionTypeTarget = null;
					} else if (celestialObjectChild.isVirtual()) {
						Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.player_can_t_go_virtual",
						                                                                                             entityServerPlayer.getName().getFormattedText(),
						                                                                                             celestialObjectChild.getDisplayName() )
								                                                                .setStyle(Commons.getStyleWarning()) ));
						Commons.addChatMessage(commandSource, new TranslationTextComponent("warpdrive.command.specify_explicit_dimension")
								                                      .setStyle(Commons.getStyleCorrect()));
						continue;
					} else {
						dimensionTypeTarget = DimensionType.byName(celestialObjectChild.dimensionId);
						final VectorI vEntry = celestialObjectChild.getEntryOffset();
						xTarget += vEntry.x;
						yTarget += vEntry.y;
						zTarget += vEntry.z;
					}
				} else {
					// on a planet => move to space
					if ( celestialObjectCurrent.parent == null
					  || celestialObjectCurrent.parent.isVirtual() ) {
						dimensionTypeTarget = DimensionType.getById(0);
						
					} else {
						dimensionTypeTarget = DimensionType.byName(celestialObjectCurrent.parent.dimensionId);
						final VectorI vEntry = celestialObjectCurrent.getEntryOffset();
						xTarget -= vEntry.x;
						yTarget -= vEntry.y;
						zTarget -= vEntry.z;
					}
				}
				assert dimensionTypeTarget != null;
				
			} else {
				// adjust offset when it's directly above or below us
				if ( celestialObjectCurrent != null
				  && celestialObjectCurrent.parent != null
				  && celestialObjectCurrent.parent.dimensionId.equals(dimensionTypeTarget.getRegistryName()) ) {// moving to parent explicitly
					final VectorI vEntry = celestialObjectCurrent.getEntryOffset();
					xTarget -= vEntry.x;
					yTarget -= vEntry.y;
					zTarget -= vEntry.z;
				} else {
					final CelestialObject celestialObjectChild = CelestialObjectManager.getClosestChild(entityServerPlayer.world, (int) entityServerPlayer.getPosX(), (int) entityServerPlayer.getPosZ());
					if ( celestialObjectChild != null
					  && celestialObjectChild.dimensionId.equals(dimensionTypeTarget.getRegistryName()) ) {// moving to child explicitly
						final VectorI vEntry = celestialObjectChild.getEntryOffset();
						xTarget += vEntry.x;
						yTarget += vEntry.y;
						zTarget += vEntry.z;
					}
				}
			}
			
			// get target celestial object
			final CelestialObject celestialObjectTarget = CelestialObjectManager.get(false, dimensionTypeTarget.getRegistryName(), xTarget, zTarget);
			
			// force to center if we're outside the border
			if ( celestialObjectTarget != null
			  && !celestialObjectTarget.isInsideBorder(xTarget, zTarget) ) {
				// outside 
				xTarget = celestialObjectTarget.dimensionCenterX;
				zTarget = celestialObjectTarget.dimensionCenterZ;
			}
			
			// get target world
			final ServerWorld worldTarget = Commons.getOrCreateWorldServer(dimensionTypeTarget);
			if (worldTarget == null) {
				Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.undefined_dimension",
				                                                                                             dimensionTypeTarget.getRegistryName()).setStyle(Commons.getStyleWarning())));
				continue;
			}
			
			// inform player
			final ITextComponent textComponent = new TranslationTextComponent("warpdrive.command.teleporting_player_x_to_y",
			                                                                  entityServerPlayer.getName().getFormattedText(),
			                                                                  Commons.format(worldTarget)).setStyle(Commons.getStyleCorrect());
			Commons.addChatMessage(commandSource, textComponent);
			WarpDrive.logger.info(textComponent.getUnformattedComponentText());
			if (commandSource != entityServerPlayer) {
				Commons.addChatMessage(entityServerPlayer, new TranslationTextComponent("warpdrive.command.teleporting_by_x_to_y",
						commandSource.getName(), Commons.format(worldTarget), dimensionTypeTarget.getRegistryName()).setStyle(Commons.getStyleCorrect()));
			}
			
			// find a good spot
			
			if ( (worldTarget.isAirBlock(new BlockPos(xTarget, yTarget - 1, zTarget)) && !entityServerPlayer.abilities.allowFlying)
			  || !worldTarget.isAirBlock(new BlockPos(xTarget, yTarget    , zTarget))
			  || !worldTarget.isAirBlock(new BlockPos(xTarget, yTarget + 1, zTarget)) ) {// non solid ground and can't fly, or inside blocks
				yTarget = worldTarget.getTopSolidOrLiquidBlock(new BlockPos(xTarget, yTarget, zTarget)).getY() + 1;
				if (yTarget == 0) {
					yTarget = 128;
				} else {
					for (int safeY = yTarget - 3; safeY > Math.max(1, yTarget - 20); safeY--) {
						if (!worldTarget.isAirBlock(new BlockPos(xTarget, safeY - 1, zTarget))
						  && worldTarget.isAirBlock(new BlockPos(xTarget, safeY    , zTarget))
						  && worldTarget.isAirBlock(new BlockPos(xTarget, safeY + 1, zTarget))) {
							yTarget = safeY;
							break;
						}
					}
				}
			}
			
			// actual teleportation
			Commons.moveEntity(entityServerPlayer, worldTarget, new Vector3(xTarget + 0.5D, yTarget + 0.2D, zTarget + 0.5D));
		}
	}
}
