package cr0s.warpdrive.entity;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.network.PacketHandler;

import javax.annotation.Nonnull;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySeat extends Entity {
	
	public static final EntityType<EntitySeat> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	private BlockPos blockPos;
	
	static {
		TYPE = EntityType.Builder.create(EntitySeat::new, EntityClassification.MISC)
		                         .size(0.25F, 0.25F)
		                         .setTrackingRange(200)
		                         .setUpdateInterval(1)
		                         .setShouldReceiveVelocityUpdates(false)
		                         .build("entity_seat");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_seat");
	}
	
	public EntitySeat(@Nonnull final EntityType<EntitySeat> entityType, @Nonnull final World world) {
		super(entityType, world);
	}
	
	@Override
	protected void registerData() {
		// no operation, abstract parent
	}
	
	@Override
	public double getMountedYOffset() {
		return -0.25;
	}
	
	@Nonnull
	@Override
	public BlockPos getPosition() {
		if ( blockPos == null
		  || blockPos.getX() != (int) getPosX()
		  || blockPos.getY() != (int) getPosY()
		  || blockPos.getZ() != (int) getPosZ() ) {
			if (blockPos != null) {
				WarpDrive.logger.error(String.format("EntitySeat has moved unexpectedly from %s to (%.3f %.3f %.3f): %s",
				                                     blockPos, getPosX(), getPosY(), getPosZ(), this ));
			}
			blockPos = new BlockPos(getPosX(), getPosY(), getPosZ());
		}
		return blockPos;
	}
	
	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
	
	@Override
	public void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		// no operation, abstract parent
	}
	
	@Override
	public void writeAdditional(@Nonnull final CompoundNBT tagCompound) {
		// no operation, abstract parent
	}
	
	@Override
	public void tick() {
		super.tick();
		
		final BlockPos blockPos = getPosition();
		
		if (!world.isAirBlock(blockPos)) {
			remove();
			return;
		}
		
		// remove when no longer mounted or passenger is too far away
		final List<Entity> passengers = getPassengers();
		if (passengers.isEmpty()) {
			remove();
		}
		for (final Entity entityPassenger : passengers) {
			if ( entityPassenger.isSneaking()
			  || entityPassenger.getDistanceSq(this) >= 1.0D ) {
				remove();
			}
		}
	}
	
	@Override
	public void remove(final boolean keepData) {
		super.remove(keepData);
		
		// enforce server synchronization
		if (world.isRemote()) {
			PacketHandler.sendUnseating();
		}
	}
	
	// ensure we remain static
	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}
	
	@Override
	public void applyEntityCollision(@Nonnull final Entity entity) {
		// no collision
	}
	
	@Override
	public void addVelocity(final double x, final double y, final double z) {
		// no motion
	}
	
	// ensure no rendering happens
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRender3d(final double x, final double y, final double z) {
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRenderDist(final double distance) {
		return false;
	}
}
