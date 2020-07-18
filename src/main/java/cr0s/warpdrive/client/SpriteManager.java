package cr0s.warpdrive.client;

import javax.annotation.Nonnull;
import java.util.HashSet;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpriteManager {
	
	public static final SpriteManager INSTANCE = new SpriteManager();
	private static final HashSet<ResourceLocation> resourceLocationTextures = new HashSet<>(16);
	
	public static void add(@Nonnull final ResourceLocation resourceLocationTexture) {
		resourceLocationTextures.add(resourceLocationTexture);
	}
	
	@SubscribeEvent
	public void onPreTextureStitchEvent(@Nonnull final TextureStitchEvent.Pre eventPreTextureStitch) {
		for (final ResourceLocation resourceLocationTexture : resourceLocationTextures) {
			eventPreTextureStitch.addSprite(resourceLocationTexture);
		}
	}
}