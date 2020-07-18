package cr0s.warpdrive.command;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.entity.EntityNPC;

import javax.annotation.Nonnull;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandNPC extends AbstractCommand {
	
	@Nonnull
	@Override
	public String getName() {
		return "wnpc";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull final ICommandSource commandSource) {
		return String.format("/%s <name> (<scale>) (<texture>) ({<nbt>})\nName may contain space using _ character",
		                     getName() );
	}
	
	@Override
	public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSource commandSource, @Nonnull final String[] args) {
		final World world = commandSource.getEntityWorld();
		final BlockPos blockPos = commandSource.getPosition();
		
		//noinspection ConstantConditions
		if (world == null || blockPos == null) {
			Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("warpdrive.command.invalid_location").setStyle(Commons.getStyleWarning())));
			return;
		}
		
		if (args.length < 1 || args.length > 4) {
			Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
			return;
		}
		
		if (EffectiveSide.get() != LogicalSide.SERVER) {
			return;
		}
		
		// get default parameter values
		final String name = args[0].replace("_", " ");
		float scale = 1.0F;
		String texturePath = "";
		String stringNBT = "";
		int indexArg = 1;
		
		// parse arguments
		if (args.length > indexArg) {
			try {
				scale = Commons.toFloat(args[indexArg]);
				indexArg++;
			} catch (final NumberFormatException exception) {
				// skip to next argument
			}
		}
		
		if (args.length > indexArg) {
			texturePath = args[indexArg];
			indexArg++;
		}
		
		if (args.length > indexArg) {
			stringNBT = args[indexArg];
			indexArg++;
		}
		
		if (args.length > indexArg) {
			Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
			return;
		}
		
		// spawn the entity
		final EntityNPC entityNPC = new EntityNPC(world);
		entityNPC.setPosition(blockPos.getX() + 0.5D, blockPos.getY() + 0.1D, blockPos.getZ() + 0.5D);
		entityNPC.setCustomName(name);
		entityNPC.setSizeScale(scale);
		entityNPC.setTextureString(texturePath);
		if (!stringNBT.isEmpty()) {
			final CompoundNBT tagCompound;
			try {
				tagCompound = JsonToNBT.getTagFromJson(stringNBT);
			} catch (final CommandSyntaxException exception) {
				WarpDrive.logger.error(exception.getMessage());
				Commons.addChatMessage(commandSource, new StringTextComponent(getUsage(commandSource)));
				return;
			}
			entityNPC.deserializeNBT(tagCompound);
		}
		world.addEntity(entityNPC);
		Commons.addChatMessage(commandSource, getPrefix().appendSibling(new TranslationTextComponent("Added NPC %1$s",
		                                                                                             entityNPC )));
	}
	
}
