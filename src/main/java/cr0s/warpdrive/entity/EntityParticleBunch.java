package cr0s.warpdrive.entity;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.SoundEvents;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityParticleBunch extends Entity {
	
	private static final int ACCELERATION_SOUND_UPDATE_TICKS = 10;
	
	private static final double[] PARTICLE_BUNCH_ENERGY_TO_X       = { 0.1D, 1.0D, 10.0D, 100.0D };
	private static final double[] PARTICLE_BUNCH_ENERGY_TO_SOUND_Y = { 0.0D, 1.0D,  2.0D,   3.0D };
	private static final SoundEvent[] PARTICLE_BUNCH_SOUNDS = { SoundEvents.ACCELERATING_LOW, SoundEvents.ACCELERATING_MEDIUM, SoundEvents.ACCELERATING_HIGH };
	
	public static final EntityType<EntityParticleBunch> TYPE;
	
	// persistent properties
	private static final DataParameter<Float> DATA_PARAMETER_ENERGY = EntityDataManager.createKey(EntityParticleBunch.class, DataSerializers.FLOAT);
	public Vector3 vectorNextPosition = new Vector3(0.0D, 0.0D, 0.0D);
	public Vector3 vectorTurningPoint = null;
	
	// computed properties
	private int lastUpdateTicks = 0;
	private static final int UPDATE_TICKS_TIMEOUT = 20;
	private int soundTicks;
	
	static {
		TYPE = EntityType.Builder.<EntityParticleBunch>create(EntityParticleBunch::new, EntityClassification.MISC)
		                         .size(2.0F, 2.0F)
		                         .setTrackingRange(300)
		                         .setUpdateInterval(1)
		                         .setShouldReceiveVelocityUpdates(false)
		                         .build("entity_particle_bunch");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_particle_bunch");
	}
	
	public EntityParticleBunch(@Nonnull final EntityType<EntityParticleBunch> entityType, @Nonnull final World world) {
		super(entityType, world);
		
		if (WarpDriveConfig.LOGGING_ACCELERATOR) {
			WarpDrive.logger.info(String.format("%s created in dimension %s",
			                                    this, Commons.format(world)));
		}
	}
	
	public EntityParticleBunch(@Nonnull final World world, final double x, final double y, final double z) {
		super(TYPE, world);
		
		this.setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
		
		if (WarpDriveConfig.LOGGING_ACCELERATOR) {
			WarpDrive.logger.info(this + " created");
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public float getBrightness() {
		return 1.0F;
	}
	
	@Override
	public boolean isInvulnerableTo(@Nonnull final DamageSource source) {
		return true;
	}
	
	public void onRefreshFromSimulation(final double newEnergy, final Vector3 vectorNewPosition, final Vector3 vectorNewTurningPoint) {
		setPosition(vectorNextPosition.x, vectorNextPosition.y, vectorNextPosition.z);
		setEnergy((float) newEnergy);
		vectorNextPosition = vectorNewPosition;
		vectorTurningPoint = vectorNewTurningPoint;
		lastUpdateTicks = 0;
	}
	
	public float getEnergy() {
		return this.dataManager.get(DATA_PARAMETER_ENERGY);
	}
	
	public void setEnergy(final float energy) {
		this.dataManager.set(DATA_PARAMETER_ENERGY, energy);
	}
	
	@Override
	public void tick() {
		// do not call super: this is a visual entity only, accelerator core controls its position/motion
		
		if (world.isRemote()) {
			return;
		}
		
		lastUpdateTicks++;
		if (lastUpdateTicks > UPDATE_TICKS_TIMEOUT) {
			remove();
		}
		
		// apply sound effects
		soundTicks--;
		if (soundTicks < 0) {
			final double factor = Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_X, PARTICLE_BUNCH_ENERGY_TO_SOUND_Y, getEnergy());
			final int indexSound = (int) Math.floor(factor);
			final SoundEvent soundEvent = PARTICLE_BUNCH_SOUNDS[ Commons.clamp(0, PARTICLE_BUNCH_SOUNDS.length - 1, indexSound) ];
			final float pitch = 0.6F + 0.4F * (float) (factor - indexSound);
			
			soundTicks = (int) Math.floor(ACCELERATION_SOUND_UPDATE_TICKS * pitch);
			world.playSound(null, getPosX(), getPosY(), getPosZ(), soundEvent, SoundCategory.HOSTILE, 1.0F, pitch);
		}
	}
	
	@Override
	protected void registerData() {
		dataManager.register(DATA_PARAMETER_ENERGY, 0.0F);
		
		noClip = true;
		soundTicks = 0;
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
	public void remove(final boolean keepData) {
		super.remove(keepData);
		if (WarpDriveConfig.LOGGING_ACCELERATOR) {
			WarpDrive.logger.info(this + " dead");
		}
	}
	
	@Override
	protected void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		// energy = tagCompound.getInt("energy");
		vectorNextPosition = Vector3.createFromNBT(tagCompound.getCompound("nextPosition"));
		if (tagCompound.contains("turningPoint")) {
			vectorTurningPoint = Vector3.createFromNBT(tagCompound.getCompound("turningPoint"));
		}
	}
	
	@Override
	protected void writeAdditional(@Nonnull final CompoundNBT tagCompound) {
		// tagCompound.putDouble("energy", energy);
		tagCompound.put("nextPosition", vectorNextPosition.write(new CompoundNBT()));
		if (vectorTurningPoint != null) {
			tagCompound.put("turningPoint", vectorTurningPoint.write(new CompoundNBT()));
		}
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