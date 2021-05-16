package cr0s.warpdrive.mixin;

import cr0s.warpdrive.data.CloakManager;

import javax.annotation.Nonnull;

import java.util.function.BiFunction;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld
		extends World {
	
	protected MixinClientWorld(WorldInfo info, DimensionType dimType,
	                           BiFunction<World, Dimension, AbstractChunkProvider> provider, IProfiler profilerIn, boolean remote) {
		super(info, dimType, provider, profilerIn, remote);
		throw new RuntimeException("Invalid injection");
	}
	
	@Inject(at = @At("HEAD"),
	        method = "invalidateRegionAndSetBlock", cancellable = true, remap = false, expect = 1 )
	private void invalidateRegionAndSetBlock(@Nonnull final BlockPos blockPos, @Nonnull final BlockState blockState, @Nonnull final CallbackInfo callback) {
		CloakManager.WorldClient_invalidateRegionAndSetBlock_setBlockState(blockPos, blockState, 19);
		callback.cancel();
	}
}