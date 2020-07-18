package cr0s.warpdrive.event;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.forcefield.BlockForceField;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumGlobalRegionType;
import cr0s.warpdrive.data.GlobalRegion;
import cr0s.warpdrive.data.GlobalRegionManager;
import cr0s.warpdrive.data.OfflineAvatarManager;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerHandler {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLoadFromFile(@Nonnull final PlayerEvent.LoadFromFile event) {
		OfflineAvatarManager.onPlayerLoggedIn(event.getPlayer());
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(@Nonnull final PlayerLoggedOutEvent event) {
		if (WarpDriveConfig.OFFLINE_AVATAR_ENABLE) {
			OfflineAvatarManager.onPlayerLoggedOut(event.getPlayer());
		}
	}
	
	@SubscribeEvent
	public void onBreakSpeed(@Nonnull final BreakSpeed event) {
		final PlayerEntity entityPlayer = event.getPlayer();
		final BlockPos blockPos = event.getPos();
		
		// check for lock
		doCancelEventDuringJump(event, blockPos);
		if (event.isCanceled()) {
			return;
		}
		
		// check for maintenance boost
		final BlockState blockState = event.getState();
		if ( !(blockState.getBlock() instanceof IBlockBase)
		  || blockState.getBlock() instanceof BlockForceField
		  || blockState.getBlockHardness(entityPlayer.world, blockPos) < WarpDriveConfig.HULL_HARDNESS[1] ) {
			return;
		}
		final GlobalRegion globalRegion = GlobalRegionManager.getNearest(EnumGlobalRegionType.SHIP, entityPlayer.world, blockPos);
		if ( globalRegion == null
		  || !globalRegion.contains(blockPos) ) {
			return;
		}
		
		// skip enabled or invalid ship cores
		final TileEntity tileEntity = entityPlayer.world.getTileEntity(globalRegion.getBlockPos());
		if (!(tileEntity instanceof TileEntityShipCore)) {
			WarpDrive.logger.error(String.format("Unable to adjust harvest speed due to invalid tile entity for global region, expecting TileEntityShipCore, got %s",
			                                     this ));
			return;
		}
		final TileEntityShipCore tileEntityShipCoreClosest = (TileEntityShipCore) tileEntity;
		if ( !tileEntityShipCoreClosest.isAssemblyValid()
		  || !tileEntityShipCoreClosest.isUnderMaintenance() ) {
			return;
		}
		
		// skip overlapping tier ship cores with same or higher tiers
		final TileEntityShipCore tileEntityShipCoreIntersect = GlobalRegionManager.getIntersectingShipCore(tileEntityShipCoreClosest);
		if (tileEntityShipCoreIntersect == null) {
			final int indexTier = ((IBlockBase) blockState.getBlock()).getTier().getIndex();
			event.setNewSpeed(100.0F * indexTier);
		}
	}
	
	@SubscribeEvent
	public void onEntityItemPickup(@Nonnull final EntityItemPickupEvent event) {
		doCancelEventDuringJump(event, event.getItem().getPosition());
	}
	
	@SubscribeEvent
	public void onRightClickBlock(@Nonnull final RightClickBlock event) {
		doCancelEventDuringJump(event, event.getPos());
	}
	
	private void doCancelEventDuringJump(@Nonnull final PlayerEvent event, @Nonnull final BlockPos blockPos) {
		assert event.isCancelable();
		if (event.isCanceled()) {
			return;
		}
		
		if (AbstractSequencer.isLocked(blockPos)) {
			event.setCanceled(true);
		}
	}
}
