package cr0s.warpdrive.mixin;

import cr0s.warpdrive.data.CelestialObjectManager;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.world.border.WorldBorder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
		implements AutoCloseable, IResourceManagerReloadListener {
	
	@Coerce
	@Redirect(at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/client/world/ClientWorld;getWorldBorder()Lnet/minecraft/world/border/WorldBorder;" ),
	          method = "renderWorldBorder", remap = false, expect = 1 )
	private WorldBorder getWorldBorder(@Nonnull final ClientWorld world) {
		return CelestialObjectManager.World_getWorldBorder(world);
	}
}