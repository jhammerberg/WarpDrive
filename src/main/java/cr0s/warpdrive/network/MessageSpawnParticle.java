package cr0s.warpdrive.network;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler.IMessage;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageSpawnParticle implements IMessage {
	
	private String type;
	private byte quantity;
	private Vector3 origin;
	private Vector3 direction;
	private float baseRed;
	private float baseGreen;
	private float baseBlue;
	private float fadeRed;
	private float fadeGreen;
	private float fadeBlue;
	
	@SuppressWarnings("unused")
	public MessageSpawnParticle() {
		// required on receiving side
	}
	
	MessageSpawnParticle(final String type, final byte quantity, final Vector3 origin, final Vector3 direction,
	                     final float baseRed, final float baseGreen, final float baseBlue,
	                     final float fadeRed, final float fadeGreen, final float fadeBlue) {
		this.type = type;
		this.quantity = quantity;
		this.origin = origin;
		this.direction = direction;
		this.baseRed = baseRed;
		this.baseGreen = baseGreen;
		this.baseBlue = baseBlue;
		this.fadeRed = fadeRed;
		this.fadeGreen = fadeGreen;
		this.fadeBlue = fadeBlue;
	}
	
	@Override
	public void encode(@Nonnull final PacketBuffer buffer) {
		final int typeSize = buffer.readByte();
		type = buffer.toString(buffer.readerIndex(), typeSize, StandardCharsets.US_ASCII);
		buffer.skipBytes(typeSize);
		
		quantity = buffer.readByte();
		
		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double z = buffer.readDouble();
		origin = new Vector3(x, y, z);
		
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		direction = new Vector3(x, y, z);
		
		baseRed = buffer.readFloat();
		baseGreen = buffer.readFloat();
		baseBlue = buffer.readFloat();
		fadeRed = buffer.readFloat();
		fadeGreen = buffer.readFloat();
		fadeBlue = buffer.readFloat();
	}
	
	@Override
	public void decode(@Nonnull final PacketBuffer buffer) {
		buffer.writeByte(type.length());
		buffer.writeBytes(type.getBytes(StandardCharsets.US_ASCII), 0, type.length());
		buffer.writeByte(quantity);
		buffer.writeDouble(origin.x);
		buffer.writeDouble(origin.y);
		buffer.writeDouble(origin.z);
		buffer.writeDouble(direction.x);
		buffer.writeDouble(direction.y);
		buffer.writeDouble(direction.z);
		buffer.writeFloat(baseRed);
		buffer.writeFloat(baseGreen);
		buffer.writeFloat(baseBlue);
		buffer.writeFloat(fadeRed);
		buffer.writeFloat(fadeGreen);
		buffer.writeFloat(fadeBlue);
	}
	
	private int integerFromRGB(final float red, final float green, final float blue) {
		return (Math.round(red * 255.0F) << 16)
			+  (Math.round(green * 255.0F) << 8)
			+   Math.round(blue * 255.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void handle(final World world) {
		// Directly spawn particle as per RenderGlobal.doSpawnParticle, bypassing range check
		// adjust color as needed
		final Minecraft mc = Minecraft.getInstance();
		final Entity entity = mc.getRenderViewEntity();
		if (entity == null || mc.worldRenderer == null || mc.particles == null) {
			return;
		}
		if ( mc.gameSettings.particles == ParticleStatus.MINIMAL
		  && world.rand.nextInt(3) != 0 ) {
			return;
		}
		
		Particle particle;
		final double noiseLevel = direction.getMagnitude() * 0.35D;
		for (int index = 0; index < quantity; index++) {
			final Vector3 directionRandomized = new Vector3(
					direction.x + noiseLevel * (world.rand.nextFloat() - world.rand.nextFloat()),
					direction.y + noiseLevel * (world.rand.nextFloat() - world.rand.nextFloat()),
					direction.z + noiseLevel * (world.rand.nextFloat() - world.rand.nextFloat()));
			switch (type) {
			default:
				if (Commons.throttleMe("invalidParticleType " + type)) {
					WarpDrive.logger.error(String.format("Invalid particle type '%s' at %s",
					                                     type, origin.toString() ));
				}
				// no break: continue to a default huge explosion
				
			case "explosionHuge":
				particle = mc.particles.addParticle(ParticleTypes.EXPLOSION_EMITTER,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "explosionLarge":
				particle = mc.particles.addParticle(ParticleTypes.EXPLOSION,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "explosionNormal":
				particle = mc.particles.addParticle(ParticleTypes.POOF,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "fireworksSpark":
				particle = mc.particles.addParticle(ParticleTypes.FIREWORK,
						origin.x, origin.y, origin.z,
						directionRandomized.x, directionRandomized.y, directionRandomized.z);
				// @TODO M1.15: do our own spark particle
				/*
				if (particle instanceof FireworkParticle.Spark) {
					((FireworkParticle.Spark) particle).setColorFade(integerFromRGB(fadeRed, fadeGreen, fadeBlue));
				}
				*/
				break;
			
			case "flame":
				particle = mc.particles.addParticle(ParticleTypes.FLAME,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "snowball":
				particle = mc.particles.addParticle(ParticleTypes.ITEM_SNOWBALL,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "snowShovel":
				particle = mc.particles.addParticle(ParticleTypes.POOF,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "mobSpell":
				particle = mc.particles.addParticle(ParticleTypes.INSTANT_EFFECT,
						origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
				
			case "cloud":
				particle = mc.particles.addParticle(ParticleTypes.CLOUD,
					origin.x, origin.y, origin.z, directionRandomized.x, directionRandomized.y, directionRandomized.z);
				break;
			
			case "jammed":// jammed machine particle reusing vanilla angryVillager particle
				// as of MC1.7.10, direction vector is ignored by upstream
				final Direction directionFacing = Commons.getHorizontalDirectionFromEntity(Minecraft.getInstance().player);
				if (directionFacing.getXOffset() != 0) {
					particle = mc.particles.addParticle(ParticleTypes.ANGRY_VILLAGER,
					        origin.x + 0.51D * directionFacing.getXOffset(),
					        origin.y - 0.50D + world.rand.nextDouble(),
					        origin.z - 0.50D + world.rand.nextDouble(),
					        directionRandomized.x, directionRandomized.y, directionRandomized.z);
				} else {
					particle = mc.particles.addParticle(ParticleTypes.ANGRY_VILLAGER,
					        origin.x - 0.50D + world.rand.nextDouble(),
					        origin.y - 0.50D + world.rand.nextDouble(),
					        origin.z + 0.51D * directionFacing.getZOffset(),
					        directionRandomized.x, directionRandomized.y, directionRandomized.z);
				}
				assert particle != null;
				// @TODO MC1.15 angry particle texture
				//  particle.setParticleTextureIndex(81);
				particle.setAlphaF(0.5F);
				particle.setMaxAge(100);
				break;
			} 
			
			if (particle == null) {
				continue;
			}
			
			if (baseRed >= 0.0F && baseGreen >= 0.0F && baseBlue >= 0.0F) {
				particle.setColor(baseRed, baseGreen, baseBlue);
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public IMessage process(@Nonnull final Context context) {
		// skip in case player just logged in
		if (Minecraft.getInstance().world == null) {
			WarpDrive.logger.error("WorldObj is null, ignoring particle packet");
			return null;
		}
		
		if (WarpDriveConfig.LOGGING_EFFECTS) {
			WarpDrive.logger.info(String.format("Received particle effect '%s' x %d from %s towards %s as RGB %.2f %.2f %.2f fading to %.2f %.2f %.2f",
				type, quantity, origin, direction,
				baseRed, baseGreen, baseBlue,
				fadeRed, fadeGreen, fadeBlue));
		}
		
		handle(Minecraft.getInstance().world);
		
		return null;	// no response
	}
}
