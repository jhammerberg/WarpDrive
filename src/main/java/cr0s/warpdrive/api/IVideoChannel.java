package cr0s.warpdrive.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;

public interface IVideoChannel {
	
	int VIDEO_CHANNEL_INVALID = -1;
	int VIDEO_CHANNEL_MIN = 0;
	int VIDEO_CHANNEL_MAX = 0xFFFFFFF;    // 268435455
	String VIDEO_CHANNEL_TAG = "videoChannel";
	
	static boolean isValid(final int videoChannel) {
		return videoChannel <= VIDEO_CHANNEL_MAX
		    && videoChannel >  VIDEO_CHANNEL_MIN;
	}
	
	// read video channel from NBT
	static int readVideoChannel(@Nullable final CompoundNBT tagCompound) {
		if ( tagCompound != null
		  && tagCompound.contains(IVideoChannel.VIDEO_CHANNEL_TAG) ) {
			return tagCompound.getInt(IVideoChannel.VIDEO_CHANNEL_TAG);
		}
		return VIDEO_CHANNEL_INVALID;
	}
	
	// write video channel to NBT
	@Nonnull
	static CompoundNBT writeVideoChannel(@Nullable final CompoundNBT tagCompound, final int videoChannel) {
		final CompoundNBT tagCompoundToReturn = tagCompound == null ? new CompoundNBT() : tagCompound;
		tagCompoundToReturn.putInt(IVideoChannel.VIDEO_CHANNEL_TAG, Math.min(VIDEO_CHANNEL_MAX, Math.max(VIDEO_CHANNEL_MIN, videoChannel)));
		return tagCompoundToReturn;
	}
	
	// get video channel, return -1 if invalid 
	int getVideoChannel();
	
	// set video channel
	void setVideoChannel(final int videoChannel);
}