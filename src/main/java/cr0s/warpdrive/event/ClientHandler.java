package cr0s.warpdrive.event;

import cr0s.warpdrive.WarpDrive;

import javax.annotation.Nonnull;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientHandler {
	
	@SubscribeEvent
	public void onClientTick(@Nonnull final ClientTickEvent event) {
		if (event.side != LogicalSide.CLIENT || event.phase != Phase.END) {
			return;
		}
		
		WarpDrive.cloaks.onClientTick();
	}
}