package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageClientUnseating implements IMessage {
		
	@SuppressWarnings("unused")
	public MessageClientUnseating() {
		// required on receiving side
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		// no operation
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		// no operation
	}
	
	private void handle(@Nonnull final ServerPlayerEntity entityServerPlayer) {
		entityServerPlayer.stopRiding();
	}
	
	@Override
	public IMessage process(@Nonnull final Context context) {
		assert context.getSender() != null;
		if (WarpDrive.isDev) {
			WarpDrive.logger.info(String.format("Received client unseating packet from %s",
			                                    context.getSender().getName() ));
		}
		context.enqueueWork(() -> handle(context.getSender()));
        
		return null;	// no response
	}
}
