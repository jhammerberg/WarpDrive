package cr0s.warpdrive.data;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.entity.EntityOfflineAvatar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.minecraftforge.common.util.Constants;

public class OfflineAvatarManager {
	
	private static final HashMap<UUID, GlobalPosition> registry = new HashMap<>(512);
	
	public static void update(@Nonnull final EntityOfflineAvatar entityOfflineAvatar) {
		// validate context
		if (!Commons.isSafeThread()) {
			WarpDrive.logger.error(String.format("Non-threadsafe call to OfflineAvatarManager:update outside main thread, for %s",
			                                     entityOfflineAvatar ));
			return;
		}
		if (entityOfflineAvatar.getPlayerUUID() == null) {
			WarpDrive.logger.error(String.format("Ignoring update for invalid EntityOfflineAvatar with no UUID %s",
			                                     entityOfflineAvatar ));
			return;
		}
		
		// add new entry
		// or update existing entry
		final GlobalPosition globalPositionActual = registry.get(entityOfflineAvatar.getPlayerUUID());
		if ( globalPositionActual == null
		  || globalPositionActual.x != (int) Math.floor(entityOfflineAvatar.getPosX())
		  || globalPositionActual.y != (int) Math.floor(entityOfflineAvatar.getPosY())
		  || globalPositionActual.z != (int) Math.floor(entityOfflineAvatar.getPosZ())
		  || !globalPositionActual.dimensionId.equals(entityOfflineAvatar.world.getDimension().getType().getRegistryName()) ) {
			final GlobalPosition globalPositionUpdated = new GlobalPosition(entityOfflineAvatar);
			registry.put(entityOfflineAvatar.getPlayerUUID(), globalPositionUpdated);
			if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
				if (globalPositionActual == null) {
					WarpDrive.logger.info(String.format("Added offline avatar for %s (%s) %s",
					                                    entityOfflineAvatar.getPlayerName(), entityOfflineAvatar.getPlayerUUID(),
					                                    Commons.format(globalPositionUpdated) ));
				} else {
					WarpDrive.logger.info(String.format("Updated offline avatar for %s (%s) from %s to %s",
					                                    entityOfflineAvatar.getPlayerName(), entityOfflineAvatar.getPlayerUUID(),
					                                    Commons.format(globalPositionActual), Commons.format(globalPositionUpdated) ));
				}
			}
		}
	}
	
	public static void remove(@Nonnull final EntityOfflineAvatar entityOfflineAvatar) {
		// validate context
		if (!Commons.isSafeThread()) {
			WarpDrive.logger.error(String.format("Non-threadsafe call to OfflineAvatarManager:remove outside main thread, for %s",
			                                     entityOfflineAvatar ));
			return;
		}
		if (entityOfflineAvatar.getPlayerUUID() == null) {
			WarpDrive.logger.error(String.format("Ignoring removal for invalid EntityOfflineAvatar with no UUID %s",
			                                     entityOfflineAvatar ));
			return;
		}
		
		// remove existing entry, if coordinates are matching
		final GlobalPosition globalPosition = registry.get(entityOfflineAvatar.getPlayerUUID());
		if ( globalPosition != null
		  && globalPosition.x == (int) Math.floor(entityOfflineAvatar.getPosX())
		  && globalPosition.y == (int) Math.floor(entityOfflineAvatar.getPosY())
		  && globalPosition.z == (int) Math.floor(entityOfflineAvatar.getPosZ())
		  && globalPosition.dimensionId.equals(entityOfflineAvatar.world.getDimension().getType().getRegistryName()) ) {
			registry.remove(entityOfflineAvatar.getPlayerUUID());
			if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
				WarpDrive.logger.info(String.format("Removed offline avatar registration for %s (%s) %s",
				                                    entityOfflineAvatar.getPlayerName(), entityOfflineAvatar.getPlayerUUID(),
				                                    entityOfflineAvatar ));
			}
		}
	}
	
	private static void remove(@Nonnull final PlayerEntity entityPlayer) {
		// update registry
		final GlobalPosition globalPosition = registry.remove(entityPlayer.getUniqueID());
		if ( globalPosition != null
		  && WarpDriveConfig.LOGGING_OFFLINE_AVATAR ) {
			WarpDrive.logger.info(String.format("Removed offline avatar registration for %s (%s) %s",
			                                    entityPlayer.getName().getFormattedText(), entityPlayer.getUniqueID(),
			                                    Commons.format(globalPosition) ));
		}
		
		// remove supporting entity
		final List<EntityOfflineAvatar> entityOfflineAvatars = entityPlayer.world.getLoadedEntitiesWithinAABB(
				EntityOfflineAvatar.class,
				new AxisAlignedBB(entityPlayer.getPosX() - 64, entityPlayer.getPosY() - 64, entityPlayer.getPosZ() - 64,
				                  entityPlayer.getPosX() + 64, entityPlayer.getPosY() + 64, entityPlayer.getPosZ() + 64 ),
				entity -> entity != null
				       && entity.isAlive()
				       && entityPlayer.getUniqueID().equals(entity.getPlayerUUID()) );
		for (final EntityOfflineAvatar entityOfflineAvatar : entityOfflineAvatars) {
			entityOfflineAvatar.remove();
		}
	}
	
	@Nullable
	public static GlobalPosition get(@Nonnull final UUID uuidPlayer) {
		// validate context
		if (!Commons.isSafeThread()) {
			WarpDrive.logger.error(String.format("Non-threadsafe call to OfflineAvatarManager:get outside main thread, for %s",
			                                     uuidPlayer ));
			return null;
		}
		
		// get existing entry
		return registry.get(uuidPlayer);
	}
	
	public static void read(@Nullable final CompoundNBT tagCompound) {
		if ( tagCompound == null
		  || !tagCompound.contains("offlineAvatars") ) {
			registry.clear();
			return;
		}
		
		// read all entries in a pre-build local collections using known stats to avoid re-allocations
		final ListNBT tagList = tagCompound.getList("offlineAvatars", Constants.NBT.TAG_COMPOUND);
		final HashMap<UUID, GlobalPosition> registryLocal = new HashMap<>(tagList.size());
		for (int index = 0; index < tagList.size(); index++) {
			final CompoundNBT tagCompoundItem = tagList.getCompound(index);
			final UUID uuid = tagCompoundItem.getUniqueId("");
			final GlobalPosition globalPosition = new GlobalPosition(tagCompoundItem);
			registryLocal.put(uuid, globalPosition);
		}
		
		// transfer to main one
		registry.clear();
		registry.putAll(registryLocal);
		for (final Entry<UUID, GlobalPosition> entry : registryLocal.entrySet()) {
			registry.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static void write(@Nonnull final CompoundNBT tagCompound) {
		final ListNBT tagList = new ListNBT();
		for (final Entry<UUID, GlobalPosition> entry : registry.entrySet()) {
			final CompoundNBT tagCompoundItem = new CompoundNBT();
			tagCompoundItem.putUniqueId("", entry.getKey());
			entry.getValue().write(tagCompoundItem);
			tagList.add(tagCompoundItem);
		}
		tagCompound.put("offlineAvatars", tagList);
	}
	
	public static void onPlayerLoggedOut(@Nonnull final PlayerEntity entityPlayer) {
		// skip dead players
		if (!entityPlayer.isAlive()) {
			if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
				WarpDrive.logger.info(String.format("Skipping offline avatar for dead player %s",
				                                    entityPlayer ));
				
			}
			return;
		}
		
		// skip players away from a ship
		final World world = entityPlayer.world;
		final BlockPos blockPos = entityPlayer.getPosition();
		if (WarpDriveConfig.OFFLINE_AVATAR_CREATE_ONLY_ABOARD_SHIPS) {
			final GlobalRegion globalRegionNearestShip = GlobalRegionManager.getNearest(EnumGlobalRegionType.SHIP, world, blockPos);
			if ( globalRegionNearestShip == null
			  || !globalRegionNearestShip.contains(blockPos) ) {
				if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
					WarpDrive.logger.info(String.format("Skipping offline avatar for off board player %s",
					                                    entityPlayer ));
				}
				return;
			}
		}
		
		// spawn an offline avatar entity at location
		// note: we don't check if one is already there since all avatars will be removed after successful reconnection anyway
		WarpDrive.logger.debug(String.format("Spawning offline avatar for %s",
		                                     entityPlayer ));
		final EntityOfflineAvatar entityOfflineAvatar = new EntityOfflineAvatar(EntityOfflineAvatar.TYPE, world);
		entityOfflineAvatar.setPositionAndRotation(blockPos.getX() + 0.5D, blockPos.getY() + 0.1D, blockPos.getZ() + 0.5D,
		                                           entityPlayer.rotationYaw, entityPlayer.rotationPitch );
		entityOfflineAvatar.setCustomName(entityPlayer.getDisplayName());
		entityOfflineAvatar.setPlayer(entityPlayer.getUniqueID(), entityPlayer.getName().getString());
		entityOfflineAvatar.setInvisible(entityPlayer.isSpectator());
		entityOfflineAvatar.setInvulnerable(entityPlayer.isCreative() || entityPlayer.isSpectator());
		// copy equipment with a marker to remember those aren't 'legit' items
		for (final EquipmentSlotType entityEquipmentSlot : EquipmentSlotType.values()) {
			final ItemStack itemStack = entityPlayer.getItemStackFromSlot(entityEquipmentSlot).copy();
			if ( !itemStack.isEmpty()
			  && !Dictionary.ITEMS_EXCLUDED_AVATAR.contains(itemStack.getItem()) ) {
				if (!itemStack.hasTag()) {
					itemStack.setTag(new CompoundNBT());
				}
				assert itemStack.getTag() != null;
				itemStack.getTag().putBoolean("isFakeItem", true);
				entityOfflineAvatar.setItemStackToSlot(entityEquipmentSlot, itemStack);
				entityOfflineAvatar.setDropChance(entityEquipmentSlot, 0.0F);
			}
		}
		final boolean isSuccess = world.addEntity(entityOfflineAvatar);
		if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
			if (isSuccess) {
				WarpDrive.logger.info(String.format("Spawned offline avatar for %s",
				                                    entityPlayer ));
			} else {
				WarpDrive.logger.error(String.format("Failed to spawn offline avatar for %s",
				                                     entityPlayer ));
			}
		}
	}
	
	public static void onPlayerLoggedIn(@Nonnull final PlayerEntity entityPlayer) {
		assert !entityPlayer.isAddedToWorld();
		
		// skip if we have no record
		final GlobalPosition globalPosition = registry.get(entityPlayer.getUniqueID());
		if (globalPosition == null) {
			return;
		}
		
		// skip if player is already close by
		if (globalPosition.dimensionId.equals(entityPlayer.dimension.getRegistryName())) {
			final double distance = entityPlayer.getDistanceSq(globalPosition.x + 0.5D, globalPosition.y, globalPosition.z + 0.5D);
			if (distance < WarpDriveConfig.OFFLINE_AVATAR_MAX_RANGE_FOR_REMOVAL * WarpDriveConfig.OFFLINE_AVATAR_MAX_RANGE_FOR_REMOVAL) {
				if (WarpDriveConfig.OFFLINE_AVATAR_DELAY_FOR_REMOVAL_TICKS == 0) {
					remove(entityPlayer);
				}
				return;
			}
		}
		
		// teleport player
		// note: this is done before server loads the player's world
		if (WarpDriveConfig.LOGGING_OFFLINE_AVATAR) {
			WarpDrive.logger.info(String.format("Relocating player %s (%s) %s to their offline avatar %s",
			                                    entityPlayer.getName().getFormattedText(), entityPlayer.getUniqueID(),
			                                    Commons.format(entityPlayer), Commons.format(globalPosition) ));
		}
		entityPlayer.dimension = DimensionType.byName(globalPosition.dimensionId);
		entityPlayer.setPosition(globalPosition.x + 0.5D, globalPosition.y + 0.1D, globalPosition.z + 0.5D);
		if (WarpDriveConfig.OFFLINE_AVATAR_DELAY_FOR_REMOVAL_TICKS == 0) {
			remove(entityPlayer);
		}
	}
	
	private static boolean isInRange(@Nonnull final PlayerEntity entityPlayer, @Nonnull final GlobalPosition globalPosition) {
		final float dX = (float) (entityPlayer.getPosX() - globalPosition.x);
		final float dY = (float) (entityPlayer.getPosY() - globalPosition.y);
		final float dZ = (float) (entityPlayer.getPosZ() - globalPosition.z);
		final float distance = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
		return distance >= WarpDriveConfig.OFFLINE_AVATAR_MIN_RANGE_FOR_REMOVAL
		    && distance <= WarpDriveConfig.OFFLINE_AVATAR_MAX_RANGE_FOR_REMOVAL;
	}
	
	public static void onTick(@Nonnull final PlayerEntity entityPlayer) {
		if ( WarpDriveConfig.OFFLINE_AVATAR_DELAY_FOR_REMOVAL_TICKS == 0
		  || entityPlayer.ticksExisted == 0
		  || (entityPlayer.ticksExisted % WarpDriveConfig.OFFLINE_AVATAR_DELAY_FOR_REMOVAL_TICKS) != 0 ) {
			return;
		}
		
		final GlobalPosition globalPosition = registry.get(entityPlayer.getUniqueID());
		if (globalPosition == null) {
			return;
		}
		if ( globalPosition.dimensionId.equals(entityPlayer.world.getDimension().getType().getRegistryName())
		  && isInRange(entityPlayer, globalPosition) ) {// (actually online in close proximity)
			remove(entityPlayer);
		}
	}
}