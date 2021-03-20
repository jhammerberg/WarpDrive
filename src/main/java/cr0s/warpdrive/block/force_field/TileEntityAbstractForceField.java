package cr0s.warpdrive.block.force_field;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.TileEntityAbstractEnergyConsumer;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.ForceFieldRegistry;
import cr0s.warpdrive.data.Vector3;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public abstract class TileEntityAbstractForceField extends TileEntityAbstractEnergyConsumer implements IBeamFrequency {
	
	// persistent properties
	protected int beamFrequency = -1;
	protected boolean isConnected = false;
	
	// computed properties
	protected Vector3 vRGB;
	
	public TileEntityAbstractForceField(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		addMethods(new String[] {
			"beamFrequency"
		});
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		
		if (IBeamFrequency.isValid(beamFrequency)) {
			ForceFieldRegistry.updateInRegistry(this);
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		// Frequency is not set
		final boolean new_isConnected = IBeamFrequency.isValid(beamFrequency);
		if (isConnected != new_isConnected) {
			isConnected = new_isConnected;
			markDirty();
		}
	}
	
	@Override
	public void remove() {
		ForceFieldRegistry.removeFromRegistry(this);
		super.remove();
	}
	
	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		// reload chunks as needed
		// ForceFieldRegistry.removeFromRegistry(this);
	}
	
	@Override
	public int getBeamFrequency() {
		return beamFrequency;
	}
	
	@Override
	public void setBeamFrequency(final int parBeamFrequency) {
		if ( beamFrequency != parBeamFrequency
		  && IBeamFrequency.isValid(parBeamFrequency) ) {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info(String.format("%s Beam frequency set from %d to %d",
				                                    this, beamFrequency, parBeamFrequency));
			}
			if (hasWorld()) {
				ForceFieldRegistry.removeFromRegistry(this);
			}
			beamFrequency = parBeamFrequency;
			vRGB = IBeamFrequency.getBeamColor(beamFrequency);
		}
		markDirty();
		if (hasWorld()) {
			ForceFieldRegistry.updateInRegistry(this);
		}
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		setBeamFrequency(tagCompound.getInt(IBeamFrequency.BEAM_FREQUENCY_TAG));
		isConnected = tagCompound.getBoolean("isConnected");
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		tagCompound.putInt(IBeamFrequency.BEAM_FREQUENCY_TAG, beamFrequency);
		tagCompound.putBoolean("isConnected", isConnected);
		return tagCompound;
	}
	
	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		final CompoundNBT tagCompound = super.getUpdateTag();
		
		tagCompound.remove(IBeamFrequency.BEAM_FREQUENCY_TAG);
		
		return tagCompound;
	}
	
	// Common OC/CC methods
	public Object[] beamFrequency(@Nonnull final Object[] arguments) {
		if ( arguments.length == 1
		  && arguments[0] != null ) {
			final int beamFrequencyRequested;
			try {
				beamFrequencyRequested = Commons.toInt(arguments[0]);
			} catch (final Exception exception) {
				final String message = String.format("%s LUA error on beamFrequency(): Boolean expected for 1st argument %s",
				                                     this, arguments[0]);
				if (WarpDriveConfig.LOGGING_LUA) {
					WarpDrive.logger.error(message);
				}
				return new Object[] { beamFrequency, message };
			}
			setBeamFrequency(beamFrequencyRequested);
		}
		return new Object[] { beamFrequency };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] beamFrequency(final Context context, final Arguments arguments) {
		return beamFrequency(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "beamFrequency":
			return beamFrequency(arguments);
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s Beam '%d' %s",
		                     getClass().getSimpleName(),
		                     beamFrequency,
		                     Commons.format(world, pos));
	}
}
