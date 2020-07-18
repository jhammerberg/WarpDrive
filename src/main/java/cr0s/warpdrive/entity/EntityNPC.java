package cr0s.warpdrive.entity;

import cr0s.warpdrive.WarpDrive;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class EntityNPC extends MobEntity {
	
	private static final DataParameter<String> DATA_PARAMETER_TEXTURE = EntityDataManager.createKey(EntityNPC.class, DataSerializers.STRING);
	private static final DataParameter<Float> DATA_PARAMETER_SIZE_SCALE = EntityDataManager.createKey(EntityNPC.class, DataSerializers.FLOAT);
	
	public static final EntityType<EntityNPC> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	// (none)
	
	static {
		TYPE = EntityType.Builder.create(EntityNPC::new, EntityClassification.MONSTER)
				       .setTrackingRange(200)
				       .setUpdateInterval(1)
				       .setShouldReceiveVelocityUpdates(false)
				       .build("entity_npc");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_npc");
	}
	
	public EntityNPC(@Nonnull final EntityType<EntityNPC> entityType, @Nonnull final World world) {
		super(entityType, world);
		
		setCanPickUpLoot(false);
		setNoAI(true);
		setCustomName(new StringTextComponent("WarpDrive NPC"));
		setCustomNameVisible(true);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(DATA_PARAMETER_TEXTURE, "Fennec");
		dataManager.register(DATA_PARAMETER_SIZE_SCALE, 1.0F);
	}
	
	public void setTextureString(@Nonnull final String textureString) {
		dataManager.set(DATA_PARAMETER_TEXTURE, textureString);
	}
	
	public String getTextureString() {
		return dataManager.get(DATA_PARAMETER_TEXTURE);
	}
	
	@Override
	public float getRenderScale() {
		return getSizeScale();
	}
	
	public void setSizeScale(final float sizeScale) {
		dataManager.set(DATA_PARAMETER_SIZE_SCALE, sizeScale);
	}
	
	public float getSizeScale() {
		return dataManager.get(DATA_PARAMETER_SIZE_SCALE);
	}
	
	@Override
	public void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		
		setTextureString(tagCompound.getString("texture"));
		setSizeScale(tagCompound.getFloat("sizeScale"));
	}
	
	@Override
	public void writeAdditional(@Nonnull final CompoundNBT tagCompound) {
		super.writeAdditional(tagCompound);
		
		tagCompound.putString("texture", getTextureString());
		tagCompound.putFloat("sizeScale", getSizeScale());
	}
	
	// always save this entity, even when it's dead
	
	@Override
	public boolean writeUnlessRemoved(@Nonnull final CompoundNBT compound) {
		final String entityString = this.getEntityString();
		if (entityString != null) {
			compound.putString("id", entityString);
			writeWithoutTypeId(compound);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean isMovementBlocked() {
		return true;
	}
}
