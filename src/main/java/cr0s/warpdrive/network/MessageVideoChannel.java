package cr0s.warpdrive.network;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageVideoChannel implements IMessage {
	
	private BlockPos blockPos;
	private int videoChannel;

	@SuppressWarnings("unused")
	public MessageVideoChannel() {
		// required on receiving side
	}
	
	public MessageVideoChannel(final BlockPos blockPos, final int videoChannel) {
		this.blockPos = blockPos;
		this.videoChannel = videoChannel;
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		blockPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		videoChannel = buffer.readInt();
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		buffer.writeInt(blockPos.getX());
		buffer.writeInt(blockPos.getY());
		buffer.writeInt(blockPos.getZ());
		buffer.writeInt(videoChannel);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void handle(final World world) {
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity != null) {
			if (tileEntity instanceof IVideoChannel) {
				((IVideoChannel) tileEntity).setVideoChannel(videoChannel);
			} else {
				WarpDrive.logger.error(String.format("Received video channel packet: invalid tile entity %s",
				                                     Commons.format(world, blockPos)));
			}
		} else {
			WarpDrive.logger.error(String.format("Received video channel packet: no tile entity %s",
			                                     Commons.format(world, blockPos)));
		}
 	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public IMessage process(@Nonnull final Context context) {
		// skip in case player just logged in
		final World world = Minecraft.getInstance().world;
		if (world == null) {
			WarpDrive.logger.error("WorldObj is null, ignoring video channel packet");
			return null;
		}
		
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info(String.format("Received video channel packet %s videoChannel %d",
			                                    Commons.format(world, blockPos), videoChannel));
		}
		
		handle(world);
		
		return null;	// no response
	}
}
