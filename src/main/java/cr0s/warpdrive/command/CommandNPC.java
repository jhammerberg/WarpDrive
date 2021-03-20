package cr0s.warpdrive.command;

import cr0s.warpdrive.entity.EntityNPC;

import javax.annotation.Nonnull;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class CommandNPC {
	
	public static void register(@Nonnull final CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("wnpc")
				        .requires(commandSource -> commandSource.hasPermissionLevel(2)
				                                && commandSource.getEntity() != null )
				        .then(Commands.argument("position", Vec3Argument.vec3())
				                      .then(Commands.argument("name", StringArgumentType.string())
				                                    .then(Commands.argument("scale", FloatArgumentType.floatArg(0.001F, 10.0F))
				                                                  .then(Commands.argument("texture", StringArgumentType.string())
				                                                                .then(Commands.argument("nbt", NBTCompoundTagArgument.nbt())
				                                                                              .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                                                    Vec3Argument.getVec3(commandContext, "position"),
				                                                                                                                    StringArgumentType.getString(commandContext, "name"),
				                                                                                                                    FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                                                                    StringArgumentType.getString(commandContext, "texture"),
				                                                                                                                    NBTCompoundTagArgument.getNbt(commandContext, "nbt") ))
				                                                                     )
				                                                                .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                                      Vec3Argument.getVec3(commandContext, "position"),
				                                                                                                      StringArgumentType.getString(commandContext, "name"),
				                                                                                                      FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                                                      StringArgumentType.getString(commandContext, "texture"),
				                                                                                                      new CompoundNBT() ))
				                                                       )
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        Vec3Argument.getVec3(commandContext, "position"),
				                                                                                        StringArgumentType.getString(commandContext, "name"),
				                                                                                        FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                                        "",
				                                                                                        new CompoundNBT() ))
				                                         )
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          Vec3Argument.getVec3(commandContext, "position"),
				                                                                          StringArgumentType.getString(commandContext, "name"),
				                                                                          1.0F,
				                                                                          "",
				                                                                          new CompoundNBT() ))
				                           )
				             )
				        .then(Commands.argument("name", StringArgumentType.string())
				                      .then(Commands.argument("scale", FloatArgumentType.floatArg(0.001F, 10.0F))
				                                    .then(Commands.argument("texture", StringArgumentType.string())
				                                                  .then(Commands.argument("nbt", NBTCompoundTagArgument.nbt())
				                                                                .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                                      commandContext.getSource().getPos(),
				                                                                                                      StringArgumentType.getString(commandContext, "name"),
				                                                                                                      FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                                                      StringArgumentType.getString(commandContext, "texture"),
				                                                                                                      NBTCompoundTagArgument.getNbt(commandContext, "nbt") ))
				                                                       )
				                                                  .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                                        commandContext.getSource().getPos(),
				                                                                                        StringArgumentType.getString(commandContext, "name"),
				                                                                                        FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                                        StringArgumentType.getString(commandContext, "texture"),
				                                                                                        new CompoundNBT() ))
				                                         )
				                                    .executes((commandContext) -> execute(commandContext.getSource(),
				                                                                          commandContext.getSource().getPos(),
				                                                                          StringArgumentType.getString(commandContext, "name"),
				                                                                          FloatArgumentType.getFloat(commandContext, "scale"),
				                                                                          "",
				                                                                          new CompoundNBT() ))
				                           )
				                      .executes((commandContext) -> execute(commandContext.getSource(),
				                                                            commandContext.getSource().getPos(),
				                                                            StringArgumentType.getString(commandContext, "name"),
				                                                            1.0F,
				                                                            "",
				                                                            new CompoundNBT() ))
				             )
				        
				        .then(Commands.literal("help")
				                      .executes((commandContext) -> help(commandContext.getSource(),
				                                                         commandContext.getRootNode().getName() ))
				             )
				        .executes((commandContext) -> help(commandContext.getSource(),
				                                           commandContext.getRootNode().getName() )
				                 )
		                   );
	}
	
	private static int help(@Nonnull final CommandSource commandSource, @Nonnull final String name) {
		commandSource.sendFeedback(new StringTextComponent( "/" + name + " <name> (<scale>) (<texture>) ({<nbt>})"
		                                                  + "\nName may contain space using _ character" ), false);
		return 0;
	}
	
	private static int execute(@Nonnull final CommandSource commandSource,
	                           @Nonnull final Vec3d vec3d,
	                           @Nonnull final String name,
	                           final float scale,
	                           @Nonnull final String texturePath,
	                           @Nonnull final CompoundNBT compoundNBT) {
		final World world = commandSource.getWorld();
		
		// spawn the entity
		final EntityNPC entityNPC = new EntityNPC(EntityNPC.TYPE, world);
		entityNPC.setPosition(vec3d.x, vec3d.y, vec3d.z);
		entityNPC.setCustomName(new StringTextComponent(name.replace("_", " ")));
		entityNPC.setSizeScale(scale);
		entityNPC.setTextureString(texturePath);
		if (!compoundNBT.isEmpty()) {
			entityNPC.deserializeNBT(compoundNBT);
		}
		world.addEntity(entityNPC);
		commandSource.sendFeedback(new TranslationTextComponent("Added NPC %1$s",
		                                                        entityNPC ), true);
		
		return 1;
	}
	
}