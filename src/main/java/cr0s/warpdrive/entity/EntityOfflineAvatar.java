package cr0s.warpdrive.entity;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.GlobalPosition;
import cr0s.warpdrive.data.OfflineAvatarManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class EntityOfflineAvatar extends MobEntity {
	
	private static final DataParameter<Optional<UUID>>  DATA_PLAYER_UUID = EntityDataManager.createKey(EntityOfflineAvatar.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<String>          DATA_PLAYER_NAME = EntityDataManager.createKey(EntityOfflineAvatar.class, DataSerializers.STRING);
	public static final EntityType<EntityOfflineAvatar> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	private GlobalPosition cache_globalPosition;
	private boolean isDirtyGlobalPosition = true;
	private int tickUpdateGlobalPosition = 0;
	
	static {
		TYPE = EntityType.Builder.create(EntityOfflineAvatar::new, EntityClassification.AMBIENT)
				       .size(0.6F * WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE, 1.8F  * WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE)
				       .setTrackingRange(200)
				       .setUpdateInterval(1)
				       .setShouldReceiveVelocityUpdates(false)
				       .build("entity_offline_avatar");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_offline_avatar");
	}
	
	public EntityOfflineAvatar(@Nonnull final EntityType<EntityOfflineAvatar> entityType, @Nonnull final World world) {
		super(entityType, world);
		
		setCanPickUpLoot(false);
		setNoAI(true);
		setCustomName(new StringTextComponent("Offline avatar"));
		setCustomNameVisible(WarpDriveConfig.OFFLINE_AVATAR_ALWAYS_RENDER_NAME_TAG);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		
		dataManager.register(DATA_PLAYER_UUID, Optional.empty());
		dataManager.register(DATA_PLAYER_NAME, "");
	}
	
	public void setPlayer(@Nonnull final UUID uuidPlayer, @Nonnull final String namePlayer) {
		dataManager.set(DATA_PLAYER_UUID, Optional.of(uuidPlayer));
		dataManager.set(DATA_PLAYER_NAME, namePlayer);
	}
	
	@Nullable
	public UUID getPlayerUUID() {
		return dataManager.get(DATA_PLAYER_UUID).orElse(null);
	}
	
	public String getPlayerName() {
		return dataManager.get(DATA_PLAYER_NAME);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (world.isRemote()) {
			return;
		}
		
		// detect position change
		if ( cache_globalPosition == null
		  || cache_globalPosition.distance2To(this) > 1.0D ) {
			isDirtyGlobalPosition = true;
			cache_globalPosition = new GlobalPosition(this);
		}
		
		// update registry
		if (isDirtyGlobalPosition) {
			tickUpdateGlobalPosition = 0;
		}
		tickUpdateGlobalPosition--;
		if (tickUpdateGlobalPosition <= 0) {
			tickUpdateGlobalPosition = WarpDriveConfig.G_REGISTRY_UPDATE_INTERVAL_TICKS;
			isDirtyGlobalPosition = false;
			
			final UUID uuidPlayer = getPlayerUUID();
			if (uuidPlayer == null) {
				// cleanup invalid entities
				if (ticksExisted > 5) {
					WarpDrive.logger.error(String.format("Removing invalid EntityOfflineAvatar with no UUID %s",
					                                     this ));
					remove();
				}
				
			} else {
				// update registry
				// note: since offline avatars can be killed while keeping last known position, the removal is handled by the manager
				OfflineAvatarManager.update(this);
			}
		}
	}
	
	@Override
	public void remove(final boolean keepData) {
		super.remove(keepData);
		
		if (WarpDriveConfig.OFFLINE_AVATAR_FORGET_ON_DEATH) {
			OfflineAvatarManager.remove(this);
		}
	}
	
	@Override
	public float getRenderScale() {
		return WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE;
	}
	
	@Override
	public void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		
		final UUID uuidPlayer = tagCompound.getUniqueId("player");
		final String namePlayer = tagCompound.getString("playerName");
		if ( uuidPlayer == null
		  || namePlayer.isEmpty() ) {
			WarpDrive.logger.error(String.format("Removing on reading invalid offline avatar in %s",
			                                     tagCompound ));
			remove();
			return;
		}
		setPlayer(uuidPlayer, namePlayer);
	}
	
	@Override
	public void writeAdditional(@Nonnull final CompoundNBT tagCompound) {
		super.writeAdditional(tagCompound);
		
		final UUID uuidPlayer = getPlayerUUID();
		if (uuidPlayer == null) {
			WarpDrive.logger.error(String.format("Removing on writing invalid offline avatar in %s",
			                                     tagCompound ));
			remove();
			return;
		}
		tagCompound.putUniqueId("player", uuidPlayer);
		tagCompound.putString("playerName", getPlayerName());
	}
	
	@Override
	public boolean canBeLeashedTo(@Nonnull final PlayerEntity entityPlayer) {
		return false;
	}
	
	@Override
	protected boolean isMovementBlocked() {
		return true;
	}
}
