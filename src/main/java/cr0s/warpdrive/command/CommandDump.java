package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.data.InventoryWrapper;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import net.minecraftforge.server.command.EnumArgument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

public class CommandDump {
	
	private enum EnumInventoryType {
		
		CONTAINER   (),
		ENDERCHEST  (),
		HAND        (),
		PLAYER      ();
		
		EnumInventoryType() { }
	}
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		final CommandNode<CommandSource> commandNode = dispatcher.register(
				Commands.literal("wdump")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2))
				        .then(Commands.argument("inventoryType", EnumArgument.enumArgument(EnumInventoryType.class))
				                      .then(Commands.argument("players", EntityArgument.players())
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          commandContext.getArgument("inventoryType", EnumInventoryType.class),
				                                                                          EntityArgument.getPlayers(commandContext, "players") ))
				                           )
				             )
				        .then(Commands.argument("players", EntityArgument.players())
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            EnumInventoryType.CONTAINER,
				                                                            EntityArgument.getPlayers(commandContext, "players") ))
				             )
				        
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> execute(commandContext.getSource(),
				                                              EnumInventoryType.CONTAINER,
				                                              Collections.singleton(commandContext.getSource().asPlayer()) )
				                 )
		                                                                  );
		dispatcher.register(Commands.literal("dump").redirect(commandNode));
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent("/" + name + " (<inventory type>) (<player selector>)\n"
		       + "Write loot table in console for selected inventory type of selected player\n"
		       + "Inventory types are:\n"
		       + "- container: any item container below or next to player\n"
		       + "- enderchest: player's enderchest\n"
		       + "- hand: player's main hand\n"
		       + "- player: player's inventory"), false);
		return 0;
	}
		
	private static int execute(@Nonnull final CommandSource commandSource,
	                            @Nonnull final EnumInventoryType inventoryType,
	                            @Nonnull final Collection<ServerPlayerEntity> serverPlayerEntities) {
		
		// evaluate sub command
		for(final ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
			final Object inventory;
			switch (inventoryType) {
			case CONTAINER:
				final World world = serverPlayerEntity.getEntityWorld();
				final BlockPos blockPos = serverPlayerEntity.getPosition();
				
				//noinspection ConstantConditions
				if (world == null || blockPos == null) {
					commandSource.sendErrorMessage(new TranslationTextComponent("warpdrive.command.invalid_location") );
					continue;
				}
				
				final Collection<Object> inventories = InventoryWrapper.getConnectedInventories(world, blockPos);
				if (inventories.isEmpty()) {
					commandSource.sendErrorMessage(new TranslationTextComponent("warpdrive.command.no_container") );
					continue;
				}
				inventory = inventories.iterator().next();
				commandSource.sendFeedback(new StringTextComponent(String.format("Dumping content from container %s:",
				                                                                 Commons.format(world, blockPos) )), true);
				break;
				
			case ENDERCHEST:
				inventory = serverPlayerEntity.getInventoryEnderChest();
				commandSource.sendFeedback(new StringTextComponent(String.format("Dumping content from %s enderchest:",
				                                                                 serverPlayerEntity.getDisplayName() )), true);
				break;
				
			case HAND:
				inventory = serverPlayerEntity.getHeldItemMainhand();
				commandSource.sendFeedback(new StringTextComponent(String.format("Dumping content from %s main hand:",
				                                                                 serverPlayerEntity.getDisplayName() )), true);
				break;
				
			case PLAYER:
				inventory = serverPlayerEntity.inventory;
				commandSource.sendFeedback(new StringTextComponent(String.format("Dumping content from %s inventory:",
				                                                                 serverPlayerEntity.getDisplayName() )), true);
				break;
				
			default:
				commandSource.sendErrorMessage(new TranslationTextComponent("warpdrive.command.invalid_parameter",
				                                                            inventoryType.toString() ));
				return 0;
			}
			
			// actually dump
			final int size = InventoryWrapper.getSize(inventory);
			if (size == 0) {
				commandSource.sendFeedback(new TranslationTextComponent("warpdrive.command.empty_inventory"), true);
			}
			for (int indexSlot = 0; indexSlot < size; indexSlot++) {
				final ItemStack itemStack = InventoryWrapper.getStackInSlot(inventory, indexSlot);
				if (itemStack != ItemStack.EMPTY && !itemStack.isEmpty()) {
					final ResourceLocation uniqueIdentifier = itemStack.getItem().getRegistryName();
					assert uniqueIdentifier != null;
					final String stringDamage = itemStack.getDamage() == 0 ? "" : String.format(" damage=\"%d\"", itemStack.getDamage());
					final String stringNBT = !itemStack.hasTag() ? "" : String.format(" nbt=\"%s\"", itemStack.getTag());
					commandSource.sendFeedback(new StringTextComponent(String.format(
							"Slot %3d is <loot item=\"%s:%s\"%s minQuantity=\"%d\" minQuantity=\"%d\"%s weight=\"1\" /><!-- %s -->",
							indexSlot,
							uniqueIdentifier.getNamespace(), uniqueIdentifier.getPath(),
							stringDamage,
							itemStack.getCount(), itemStack.getCount(),
							stringNBT,
							itemStack.getDisplayName() )), true);
				}
			}
		}
		
		return serverPlayerEntities.size();
	}
}
