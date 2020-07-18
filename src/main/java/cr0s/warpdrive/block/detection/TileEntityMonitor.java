package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.block.TileEntityAbstractMachine;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityMonitor extends TileEntityAbstractMachine implements IVideoChannel {
	
	public static TileEntityType<TileEntityMonitor> TYPE;
	
	private int videoChannel = -1;
	
	private static final int PACKET_SEND_INTERVAL_TICKS = 60 * 20;
	private int packetSendTicks = 10;
	
	public TileEntityMonitor() {
		super(TYPE);
		
		peripheralName = "warpdriveMonitor";
		addMethods(new String[] {
			"videoChannel"
		});
		doRequireUpgradeToInterface();
	}
	
	@Override
	public void tick() {
		super.tick();
		assert world != null;
		
		if (!world.isRemote()) {
			packetSendTicks--;
			if (packetSendTicks <= 0) {
				packetSendTicks = PACKET_SEND_INTERVAL_TICKS;
				PacketHandler.sendVideoChannelPacket(world, pos, videoChannel);
			}
		}
	}
	
	@Override
	public int getVideoChannel() {
		return videoChannel;
	}
	
	@Override
	public void setVideoChannel(final int parVideoChannel) {
		if ( videoChannel != parVideoChannel
		  && IVideoChannel.isValid(parVideoChannel) ) {
			videoChannel = parVideoChannel;
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info(this + " Monitor video channel set to " + videoChannel);
			}
			markDirty();
			// force update through main thread since CC & OC are running outside the main thread
			packetSendTicks = 0;
		}
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		videoChannel = tagCompound.getInt("frequency") + tagCompound.getInt(VIDEO_CHANNEL_TAG);
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		tagCompound.putInt(VIDEO_CHANNEL_TAG, videoChannel);
		return tagCompound;
	}
	
	// Common OC/CC methods
	public Object[] videoChannel(@Nonnull final Object[] arguments) {
		if (arguments.length == 1) {
			setVideoChannel(Commons.toInt(arguments[0]));
		}
		return new Integer[] { getVideoChannel() };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] videoChannel(final Context context, final Arguments arguments) {
		return videoChannel(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "videoChannel":
			return videoChannel(arguments);
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s",
		                     getClass().getSimpleName(),
		                     videoChannel,
		                     Commons.format(world, pos));
	}
}