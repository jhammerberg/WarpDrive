package cr0s.warpdrive.entity;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLaserExploder extends Entity {
	
	public static final EntityType<EntityLaserExploder> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	private int lastUpdateTicks = 0;
	private static final int UPDATE_TICKS_TIMEOUT = 20;
	
	static {
		TYPE = EntityType.Builder.<EntityLaserExploder>create(EntityLaserExploder::new, EntityClassification.MISC)
				       .setTrackingRange(8)
				       .setUpdateInterval(1000)
				       .setShouldReceiveVelocityUpdates(false)
				       .build("entity_laser_exploder");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_laser_exploder");
	}
	
	public EntityLaserExploder(@Nonnull final EntityType<EntityLaserExploder> entityType, @Nonnull final World world) {
		super(entityType, world);
		
		if (WarpDriveConfig.LOGGING_WEAPON) {
			WarpDrive.logger.info(String.format("%s created in dimension %s",
			                                    this, Commons.format(world)));
		}
	}
	
	public EntityLaserExploder(final World world, final BlockPos blockPos) {
		this(TYPE, world);
		
		setPosition(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
	}
	
	@Override
	public boolean isInvulnerableTo(@Nonnull final DamageSource source) {
		return true;
	}
	
	@Override
	public void tick() {
		// do not call super: this is a virtual entity which has no reason to move or change properties
		
		if (world.isRemote()) {
			return;
		}
		
		lastUpdateTicks++;
		if (lastUpdateTicks > UPDATE_TICKS_TIMEOUT) {
			remove(false);
		}
	}
	
	@Override
	protected void registerData() {
		noClip = true;
	}
	
	@Override
	public float getEyeHeight(@Nonnull final Pose pose) {
		return 2.0F;
	}
	
	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
	
	@Override
	public void remove(boolean keepData) {
		super.remove(keepData);
		if (WarpDriveConfig.LOGGING_WEAPON) {
			WarpDrive.logger.info(this + " dead");
		}
	}
	
	@Override
	protected void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		// not applicable
	}
	
	@Override
	protected void writeAdditional(@Nonnull final CompoundNBT tagCompound) {
		// not applicable
	}
	
	// prevent saving entity to chunk
	@Override
	public boolean writeUnlessPassenger(@Nonnull final CompoundNBT tagCompound) {
		return false;
	}
	
	@Override
	public boolean writeUnlessRemoved(@Nonnull final CompoundNBT tagCompound) {
		return false;
	}
	
	@Nonnull
	@Override
	public String toString() {
		return String.format("%s/%d %s",
		                     getClass().getSimpleName(),
		                     getEntityId(),
		                     Commons.format(world, getPosX(), getPosY(), getPosZ()));
	}
}