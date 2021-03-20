package cr0s.warpdrive.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;

public interface IControlChannel {
	
	int CONTROL_CHANNEL_INVALID = -1;
	int CONTROL_CHANNEL_MIN = 0;
	int CONTROL_CHANNEL_MAX = 0xFFFFFFF;    // 268435455
	String CONTROL_CHANNEL_TAG = "controlChannel";
	
	static boolean isValid(final int controlChannel) {
		return controlChannel <  CONTROL_CHANNEL_MAX
		    && controlChannel >= CONTROL_CHANNEL_MIN;
	}
	
	// read beam frequency from NBT
	static int readControlChannel(@Nullable final CompoundNBT tagCompound) {
		if ( tagCompound != null
		  && tagCompound.contains(CONTROL_CHANNEL_TAG) ) {
			return tagCompound.getInt(CONTROL_CHANNEL_TAG);
		}
		return CONTROL_CHANNEL_INVALID;
	}
	
	// write beam frequency to NBT
	@Nonnull
	static CompoundNBT writeControlChannel(@Nullable final CompoundNBT tagCompound, final int beamFrequency) {
		final CompoundNBT tagCompoundToReturn = tagCompound == null ? new CompoundNBT() : tagCompound;
		tagCompoundToReturn.putInt(CONTROL_CHANNEL_TAG, Math.min(CONTROL_CHANNEL_MAX, Math.max(CONTROL_CHANNEL_MIN, beamFrequency)));
		return tagCompoundToReturn;
	}
	
	// get control channel, return -1 if invalid 
	int getControlChannel();
	
	// set control channel
	void setControlChannel(final int controlChannel);
}