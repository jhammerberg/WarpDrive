package cr0s.warpdrive.mixin;

import javax.annotation.Nonnull;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public abstract class MixinClientPlayNetHandler
		implements IClientPlayNetHandler {
	
	@Inject(at = @At(value = "INVOKE",
	                 target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
	                 shift = Shift.BEFORE),
	        method = "handleUpdateTileEntity", cancellable = true, remap = false, expect = 1 )
	private void handleUpdateTileEntity(@Nonnull final SUpdateTileEntityPacket packet, @Nonnull final CallbackInfo callback) {
		callback.cancel();
	}
	
	@Redirect(at = @At(value = "INVOKE",
	                   target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V" ),
	          method = "handleUpdateTileEntity", remap = false, expect = 1 )
	private void handleUpdateTileEntity(@Nonnull final Logger logger, @Nonnull final String message, @Nonnull final Object param1, @Nonnull final Object param2) {
		// do nothing
	}
}