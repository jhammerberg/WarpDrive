package cr0s.warpdrive.network;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler.IMessage;
import cr0s.warpdrive.render.EntityFXBeam;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageBeamEffect implements IMessage {
	
	private Vector3 source;
	private Vector3 target;
	private float red;
	private float green;
	private float blue;
	private int age;

	@SuppressWarnings("unused")
	public MessageBeamEffect() {
		// required on receiving side
	}
	
	public MessageBeamEffect(final Vector3 source, final Vector3 target, final float red, final float green, final float blue, final int age) {
		this.source = source;
		this.target = target;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.age = age;
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double z = buffer.readDouble();
		source = new Vector3(x, y, z);
		
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		target = new Vector3(x, y, z);
		
		red = buffer.readFloat();
		green = buffer.readFloat();
		blue = buffer.readFloat();
		age = buffer.readShort();
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		buffer.writeDouble(source.x);
		buffer.writeDouble(source.y);
		buffer.writeDouble(source.z);
		buffer.writeDouble(target.x);
		buffer.writeDouble(target.y);
		buffer.writeDouble(target.z);
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
		buffer.writeShort(Math.min(32767, age));
	}
	
	@OnlyIn(Dist.CLIENT)
	private void handle(final World world) {
		Minecraft.getInstance().particles.addEffect(new EntityFXBeam(world, source.clone(), target.clone(), red, green, blue, age));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public IMessage process(@Nonnull final Context context) {
		// skip in case player just logged in
		if (Minecraft.getInstance().world == null) {
			WarpDrive.logger.error("WorldObj is null, ignoring beam packet");
			return null;
		}
		
		if (WarpDriveConfig.LOGGING_EFFECTS) {
			WarpDrive.logger.info(String.format("Received beam packet from %s to %s as RGB %.3f %.3f %.3f age %d",
			                                    source, target,
			                                    red, green, blue,
			                                    age));
		}
		
        handle(Minecraft.getInstance().world);
        
		return null;	// no response
	}
}
