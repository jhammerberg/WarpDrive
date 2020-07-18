package cr0s.warpdrive;

import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.event.EMPReceiver;
import cr0s.warpdrive.event.ItemHandler;
import cr0s.warpdrive.event.LivingHandler;
import cr0s.warpdrive.event.PlayerHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.WeakHashMap;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

public class CommonProxy {
	
	private static final WeakHashMap<GameProfile, WeakReference<PlayerEntity>> fakePlayers = new WeakHashMap<>(100);
	
	@Nullable
	private static ServerPlayerEntity getPlayer(@Nonnull final ServerWorld world, final UUID uuidPlayer) {
		for (final ServerPlayerEntity entityServerPlayer : world.getServer().getPlayerList().getPlayers()) {
			if (entityServerPlayer.getUniqueID() == uuidPlayer) {
				return entityServerPlayer;
			}
		}
		return null;
	}
	
	@Nonnull
	public static PlayerEntity getFakePlayer(@Nullable final UUID uuidPlayer, @Nonnull final ServerWorld world, @Nonnull final BlockPos blockPos) {
		final PlayerEntity entityPlayer = uuidPlayer == null ? null : getPlayer(world, uuidPlayer);
		final GameProfile gameProfile = entityPlayer == null ? WarpDrive.gameProfile : entityPlayer.getGameProfile();
		WeakReference<PlayerEntity> weakFakePlayer = fakePlayers.get(gameProfile);
		PlayerEntity entityFakePlayer = (weakFakePlayer == null) ? null : weakFakePlayer.get();
		if (entityFakePlayer == null) {
			entityFakePlayer = FakePlayerFactory.get(world, gameProfile);
			((ServerPlayerEntity) entityFakePlayer).interactionManager.setGameType(GameType.SURVIVAL);
			weakFakePlayer = new WeakReference<>(entityFakePlayer);
			fakePlayers.put(gameProfile, weakFakePlayer);
		} else {
			entityFakePlayer.world = world;
		}
		entityFakePlayer.setPosition(blockPos.getX() + 0.5D,
		                             blockPos.getY() + 0.5D,
		                             blockPos.getZ() + 0.5D );
		
		return entityFakePlayer;
	}
	
	public static boolean isBlockBreakCanceled(final UUID uuidPlayer, final BlockPos blockPosSource,
	                                           @Nonnull final World world, final BlockPos blockPosEvent) {
		if (world.isRemote() || !(world instanceof ServerWorld)) {
			return false;
		}
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info(String.format("isBlockBreakCanceled by %s %s to block %s",
			                                    uuidPlayer, Commons.format(world, blockPosSource), Commons.format(world, blockPosEvent)));
		}
		
		final BlockState blockState = world.getBlockState(blockPosEvent);
		if (!blockState.getBlock().isAir(blockState, world, blockPosEvent)) {
			final BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(
			    world, blockPosEvent, world.getBlockState(blockPosEvent),
				getFakePlayer(uuidPlayer, (ServerWorld) world, blockPosSource));
			MinecraftForge.EVENT_BUS.post(breakEvent);
			if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
				WarpDrive.logger.info(String.format("isBlockBreakCanceled player %s isCanceled %s",
				                                    breakEvent.getPlayer(), breakEvent.isCanceled()));
			}
			return breakEvent.isCanceled();
		}
		return false;
	}
	
	public static boolean isBlockPlaceCanceled(final UUID uuidPlayer, final BlockPos blockPosSource,
	                                           @Nonnull final World world, final BlockPos blockPosEvent, final BlockState blockState) {
		if (world.isRemote() || !(world instanceof ServerWorld)) {
			return false;
		}
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info(String.format("isBlockPlaceCanceled by %s %s to block %s %s",
			                                    uuidPlayer, Commons.format(world, blockPosSource),
			                                    Commons.format(world, blockPosEvent), blockState));
		}
		final BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(
				new BlockSnapshot(world, blockPosEvent, blockState),
				Blocks.AIR.getDefaultState(),
				getFakePlayer(uuidPlayer, (ServerWorld) world, blockPosSource) );
		
		MinecraftForge.EVENT_BUS.post(placeEvent);
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info(String.format("isBlockPlaceCanceled player %s isCanceled %s",
			                                    placeEvent.getEntity(), placeEvent.isCanceled()));
		}
		return placeEvent.isCanceled();
	}
	
	public void onForgePreInitialisation() {
	
	}
	
	public void onModelInitialisation(final Object object) {
	
	}
	
	public void onForgeInitialisation() {
		// event handlers
		MinecraftForge.EVENT_BUS.register(new ItemHandler());
		MinecraftForge.EVENT_BUS.register(new LivingHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerHandler());
		MinecraftForge.EVENT_BUS.register(EMPReceiver.class);
	}
}