package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.weapon.TileEntityLaser;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageTargeting implements IMessage {
	
	private int x;
	private int y;
	private int z;
	private float yaw;
	private float pitch;

	@SuppressWarnings("unused")
	public MessageTargeting() {
		// required on receiving side
	}
	
	public MessageTargeting(final int x, final int y, final int z, final float yaw, final float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		yaw = buffer.readFloat();
		pitch = buffer.readFloat();
	}

	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeFloat(yaw);
		buffer.writeFloat(pitch);
	}
	
	private void handle(final World world) {
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileEntityLaser) {
			final TileEntityLaser laser = (TileEntityLaser) tileEntity;
			laser.initiateBeamEmission(yaw, pitch);
		}
	}
	
	@Override
	public IMessage process(@Nonnull final Context context) {
		if (WarpDriveConfig.LOGGING_TARGETING) {
			WarpDrive.logger.info(String.format("Received target packet: (%d %d %d) yaw: %.1f pitch: %.1f",
			                                    x, y, z,
			                                    yaw, pitch));
		}
		
		handle(context.getSender().world);
        
		return null;	// no response
	}
}
