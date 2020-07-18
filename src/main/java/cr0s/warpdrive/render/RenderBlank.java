package cr0s.warpdrive.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RenderBlank implements IRenderHandler {
	
	private static RenderBlank INSTANCE = null;
	
	public static RenderBlank getInstance() {
	    if (INSTANCE == null) {
	        INSTANCE = new RenderBlank();
	    }
	    return INSTANCE;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(final int ticks, final float partialTicks, final ClientWorld world, final Minecraft mc) {
	}
}
