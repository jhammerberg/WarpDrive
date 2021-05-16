package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.data.CelestialObject;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageClientSync implements IMessage {
	
	private CompoundNBT tagCompound;
	
	@SuppressWarnings("unused")
	public MessageClientSync() {
		// required on receiving side
	}
	
	public MessageClientSync(final CelestialObject celestialObject) {
		tagCompound = new CompoundNBT();
		tagCompound.put("celestialObjects"     , CelestialObjectManager.writeClientSync(celestialObject));
		tagCompound.put("items_breathingHelmet", Dictionary.writeItemsToNBT(Dictionary.ITEMS_BREATHING_HELMET));
		tagCompound.put("items_flyInSpace"     , Dictionary.writeItemsToNBT(Dictionary.ITEMS_FLYINSPACE));
		tagCompound.put("items_noFallDamage"   , Dictionary.writeItemsToNBT(Dictionary.ITEMS_NOFALLDAMAGE));
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		tagCompound = buffer.readCompoundTag();
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		buffer.writeCompoundTag(tagCompound);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public IMessage process(@Nonnull final Context context) {
		// skip in case player just logged in
		// if (Minecraft.getInstance().world == null) {
		// 	WarpDrive.logger.error("WorldObj is null, ignoring client synchronization packet");
		// 	return null;
		// }
		
		if (WarpDriveConfig.LOGGING_CLIENT_SYNCHRONIZATION) {
			WarpDrive.logger.info(String.format("Received client synchronization packet: %s",
			                                    tagCompound));
		}
		
		try {
			CelestialObjectManager.readClientSync(tagCompound.getList("celestialObjects", NBT.TAG_COMPOUND));
			Dictionary.ITEMS_BREATHING_HELMET = Dictionary.readItemsFromNBT(tagCompound.getList("items_breathingHelmet", NBT.TAG_STRING));
			Dictionary.ITEMS_FLYINSPACE       = Dictionary.readItemsFromNBT(tagCompound.getList("items_flyInSpace"     , NBT.TAG_STRING));
			Dictionary.ITEMS_NOFALLDAMAGE     = Dictionary.readItemsFromNBT(tagCompound.getList("items_noFallDamage"   , NBT.TAG_STRING));
		} catch (final Exception exception) {
			exception.printStackTrace(WarpDrive.printStreamError);
			WarpDrive.logger.error(String.format("Fails to parse client synchronization packet %s",
			                                     tagCompound ));
		}
		
		return new MessageClientValidation();
	}
}
