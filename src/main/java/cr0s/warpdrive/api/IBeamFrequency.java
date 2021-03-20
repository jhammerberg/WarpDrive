package cr0s.warpdrive.api;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;

public interface IBeamFrequency {
	
	int BEAM_FREQUENCY_INVALID = -1;
	int BEAM_FREQUENCY_SCANNING = 1420;
	int BEAM_FREQUENCY_MIN = 0;
	int BEAM_FREQUENCY_MAX = 65000;
	String BEAM_FREQUENCY_TAG = "beamFrequency";
	
	static boolean isValid(final int beamFrequency) {
		return beamFrequency <= BEAM_FREQUENCY_MAX
		    && beamFrequency >  BEAM_FREQUENCY_MIN; 
	}
	
	// read beam frequency from NBT
	static int readBeamFrequency(@Nullable final CompoundNBT tagCompound) {
		if ( tagCompound != null
		  && tagCompound.contains(BEAM_FREQUENCY_TAG) ) {
			return tagCompound.getInt(BEAM_FREQUENCY_TAG);
		}
		return BEAM_FREQUENCY_INVALID;
	}
	
	// write beam frequency to NBT
	@Nonnull
	static CompoundNBT writeBeamFrequency(@Nullable final CompoundNBT tagCompound, final int beamFrequency) {
		final CompoundNBT tagCompoundToReturn = tagCompound == null ? new CompoundNBT() : tagCompound;
		tagCompoundToReturn.putInt(BEAM_FREQUENCY_TAG, Math.min(BEAM_FREQUENCY_MAX, Math.max(BEAM_FREQUENCY_MIN, beamFrequency)));
		return tagCompoundToReturn;
	}
	
	// get beam frequency, return -1 if invalid 
	int getBeamFrequency();
	
	// sets beam frequency
	void setBeamFrequency(final int beamFrequency);
	
	@Nonnull
	static Vector3 getBeamColor(final int beamFrequency) {
		final float r, g, b;
		if (beamFrequency <= BEAM_FREQUENCY_MIN) { // invalid frequency
			r = 1.0F;
			g = 0.0F;
			b = 0.0F;
		} else if (beamFrequency <= 10000) { // red
			r = 1.0F;
			g = 0.0F;
			b = 0.0F + 0.5f * beamFrequency / 10000F;
		} else if (beamFrequency <= 20000) { // orange
			r = 1.0F;
			g = 0.0F + 1.0F * (beamFrequency - 10000F) / 10000F;
			b = 0.5F - 0.5F * (beamFrequency - 10000F) / 10000F;
		} else if (beamFrequency <= 30000) { // yellow
			r = 1.0F - 1.0F * (beamFrequency - 20000F) / 10000F;
			g = 1.0F;
			b = 0.0F;
		} else if (beamFrequency <= 40000) { // green
			r = 0.0F;
			g = 1.0F - 1.0F * (beamFrequency - 30000F) / 10000F;
			b = 0.0F + 1.0F * (beamFrequency - 30000F) / 10000F;
		} else if (beamFrequency <= 50000) { // blue
			r = 0.0F + 0.5F * (beamFrequency - 40000F) / 10000F;
			g = 0.0F;
			b = 1.0F - 0.5F * (beamFrequency - 40000F) / 10000F;
		} else if (beamFrequency <= 60000) { // violet
			r = 0.5F + 0.5F * (beamFrequency - 50000F) / 10000F;
			g = 0.0F;
			b = 0.5F - 0.5F * (beamFrequency - 50000F) / 10000F;
		} else if (beamFrequency <= BEAM_FREQUENCY_MAX) { // rainbow
			final int component = Math.round(4096F * (beamFrequency - 60000F) / (BEAM_FREQUENCY_MAX - 60000F));
			r = 1.0F - 0.5F * (component & 0xF);
			g = 0.5F + 0.5F * (component >> 4 & 0xF);
			b = 0.5F + 0.5F * (component >> 8 & 0xF);
		} else { // invalid frequency
			r = 1.0F;
			g = 0.0F;
			b = 0.0F;
		}
		return new Vector3(r, g, b);
	}
}