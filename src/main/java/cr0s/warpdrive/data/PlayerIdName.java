package cr0s.warpdrive.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerIdName {
	
	private final UUID uuid;
	private String name;
	
	public PlayerIdName(@Nonnull final PlayerEntity entityPlayer) {
		this.uuid = entityPlayer.getUniqueID();
		this.name = entityPlayer.getName().getString();
	}
	
	private PlayerIdName(@Nonnull final UUID uuid, @Nonnull final String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(@Nonnull final String name) {
		this.name = name;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	/**
	 * Return null if NBT is invalid.
	 */
	@Nullable
	public static PlayerIdName loadFromNBT(final CompoundNBT tagCompound) {
		if (tagCompound == null) {
			return null;
		}
		if ( !tagCompound.keySet().contains("UUID")
		  || !tagCompound.keySet().contains("name") ) {
			return null;
		}
		final UUID uuid = UUID.fromString(tagCompound.getString("UUID"));
		final String name = tagCompound.getString("name");
		return new PlayerIdName(uuid, name);
	}
	
	public CompoundNBT writeToNBT(@Nonnull final CompoundNBT tagCompound) {
		tagCompound.putString("UUID", uuid.toString());
		tagCompound.putString("name", name);
		return tagCompound;
	}
}
