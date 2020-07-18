package cr0s.warpdrive.world;

import cr0s.warpdrive.WarpDrive;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public final class EntityStarCore extends Entity {
	
	public static final EntityType<EntityStarCore> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	private int radius;
	
	private static final int KILL_RADIUS = 60;
	private static final int BURN_RADIUS = 200;
	//private final int ROCKET_INTERCEPT_RADIUS = 100; //disabled
	private boolean isLogged = false;
	
	private static final int ENTITY_ACTION_INTERVAL = 10; // ticks
	
	private int ticks = 0;
	
	static {
		TYPE = EntityType.Builder.<EntityStarCore>create(EntityStarCore::new, EntityClassification.MISC)
				       .setTrackingRange(300)
				       .setUpdateInterval(1)
				       .setShouldReceiveVelocityUpdates(false)
				       .build("entity_star_sore");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_star_sore");
	}
	
	public EntityStarCore(final EntityType<EntityStarCore> entityType, final World world) {
		super(entityType, world);
	}
	
	public EntityStarCore(final World world, final int x, final int y, final int z, final int radius) {
		super(TYPE, world);
		
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.setPosition(x, y, z);
		this.radius = radius;
	}
	
	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
	
	private void actionToEntitiesNearStar() {
		final int xMax, yMax, zMax;
		final int xMin, yMin, zMin;
		final int MAX_RANGE = radius + KILL_RADIUS + BURN_RADIUS;// + ROCKET_INTERCEPT_RADIUS;
		final int KILL_RANGESQ = (radius + KILL_RADIUS) * (radius + KILL_RADIUS);
		final int BURN_RANGESQ = (radius + KILL_RADIUS + BURN_RADIUS) * (radius + KILL_RADIUS + BURN_RADIUS);
		xMin = xCoord - MAX_RANGE;
		xMax = xCoord + MAX_RANGE;
		
		zMin = zCoord - MAX_RANGE;
		zMax = zCoord + MAX_RANGE;
		
		yMin = yCoord - MAX_RANGE;
		yMax = yCoord + MAX_RANGE;
		final AxisAlignedBB aabb = new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax);
		final List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, aabb);
		
		if (!isLogged) {
			isLogged = true;
			WarpDrive.logger.info(this + " Capture range " + MAX_RANGE + " X " + xMin + " to " + xMax + " Y " + yMin + " to " + yMax + " Z " + zMin + " to " + zMax);
		}
		for (final Object object : list) {
			if (!(object instanceof Entity)) {
				continue;
			}
			
			if (object instanceof LivingEntity) {
				final LivingEntity entityLivingBase = (LivingEntity) object;
				
				//System.out.println("Found: " + entity.getEntityName() + " distance: " + entity.getDistanceToEntity(this));
				
				// creative bypass
				if (entityLivingBase.isInvulnerableTo(WarpDrive.damageWarm)) {
					continue;
				}
				if (entityLivingBase instanceof PlayerEntity) {
					final PlayerEntity entityPlayer = (PlayerEntity) entityLivingBase;
					if (entityPlayer.abilities.disableDamage) {
						continue;
					}
				}
				
				final double distanceSq = entityLivingBase.getDistanceSq(this);
				if (distanceSq <= KILL_RANGESQ) {
					// 100% kill, ignores any protection
					entityLivingBase.attackEntityFrom(DamageSource.ON_FIRE, 9000);
					entityLivingBase.attackEntityFrom(DamageSource.GENERIC, 9000);
					if (entityLivingBase.isAlive()) {
						WarpDrive.logger.warn(String.format("Forcing entity death due to star proximity: %s", entityLivingBase));
						entityLivingBase.remove();
					}
				} else if (distanceSq <= BURN_RANGESQ) {
					// burn entity
					if (!entityLivingBase.isImmuneToFire()) {
						entityLivingBase.setFire(3);
					}
					entityLivingBase.attackEntityFrom(DamageSource.ON_FIRE, 1);
				}
			}/* else { // Intercept ICBM rocket and kill

				   Entity entity = (Entity) o;
				   if (entity.getDistanceToEntity(this) <= (this.radius + ROCKET_INTERCEPT_RADIUS)) {
				       System.out.println("[SC] Intercepted entity: " + entity.getEntityName());
				       world.removeEntity(entity);
				   }
				}*/
		}
	}
	
	@Override
	public void tick() {
		if (world.isRemote()) {
			return;
		}
		
		if (++ticks > ENTITY_ACTION_INTERVAL) {
			ticks = 0;
			actionToEntitiesNearStar();
		}
	}
	
	@Override
	protected void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		xCoord = tagCompound.getInt("x");
		yCoord = tagCompound.getInt("y");
		zCoord = tagCompound.getInt("z");
		radius = tagCompound.getInt("radius");
	}
	
	@Override
	protected void registerData() {
		noClip = true;
	}
	
	@Override
	protected void writeAdditional(final CompoundNBT tagCompound) {
		tagCompound.putInt("x", xCoord);
		tagCompound.putInt("y", yCoord);
		tagCompound.putInt("z", zCoord);
		tagCompound.putInt("radius", radius);
	}
	
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return false;
	}
}