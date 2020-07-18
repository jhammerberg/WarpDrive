package cr0s.warpdrive.render;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.IVertexBuilder;

@OnlyIn(Dist.CLIENT)
public class EntityFXDot extends AbstractEntityFX {
	
	private final Vector3 v3Acceleration;
	private final double friction;
	
	public EntityFXDot(@Nonnull final World world, @Nonnull final Vector3 v3Position,
	                   @Nonnull final Vector3 v3Motion, @Nonnull final Vector3 v3Acceleration, final double friction,
	                   final int age) {
		super(world, v3Position.x, v3Position.y, v3Position.z, 0.0D, 0.0D, 0.0D);
		
		this.setSize(0.02F, 0.02F);
		this.canCollide = false;
		this.motionX = v3Motion.x;
		this.motionY = v3Motion.y;
		this.motionZ = v3Motion.z;
		this.v3Acceleration = v3Acceleration;
		this.friction = friction;
		this.maxAge = age;
		
		// defaults to vanilla water drip
		// setSprite(113);
		
		// refresh bounding box
		setPosition(v3Position.x, v3Position.y, v3Position.z);
	}
	
	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (age++ >= maxAge) {
			setExpired();
		}
		
		move(motionX, motionY, motionZ);
		motionX = (motionX + v3Acceleration.x) * friction;
		motionY = (motionY + v3Acceleration.y) * friction;
		motionZ = (motionZ + v3Acceleration.z) * friction;
	}
	
	@Override
	@Nonnull
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public int getBrightnessForRender(final float partialTick) {
		return 0xF00000;
	}
	
	@Override
	public void renderParticle(@Nonnull final IVertexBuilder vertexBuffer, @Nonnull final ActiveRenderInfo renderInfo, final float partialTicks) {
		final float minU = this.getMinU();
		final float maxU = this.getMaxU();
		final float minV = this.getMinV();
		final float maxV = this.getMaxV();
		final float scale = 0.1F * particleScale; // getScale(partialTicks);
		
		final Vec3d vec3d = renderInfo.getProjectedView();
		final float x = (float) (MathHelper.lerp(partialTicks, prevPosX, posX) - vec3d.getX());
		final float y = (float) (MathHelper.lerp(partialTicks, prevPosY, posY) - vec3d.getY());
		final float z = (float) (MathHelper.lerp(partialTicks, prevPosZ, posZ) - vec3d.getZ());
		
		// alpha increase during first tick and decays during last 2 ticks
		float alpha = particleAlpha;
		final int ageLeft = maxAge - age;
		if (age < 1) {
			alpha = particleAlpha * partialTicks;
		} else if (ageLeft < 2) {
			if (ageLeft < 1) {
				alpha = particleAlpha * (0.5F - partialTicks / 2.0F);
			} else {
				alpha = particleAlpha * (1.0F - partialTicks / 2.0F);
			}
		}
		
		// start drawing (done by caller in layers 0 to 3)
		// vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
		
		// get brightness factors
		final int brightnessForRender = getBrightnessForRender(partialTicks);
		
		// compute relative texture coordinates
		final Quaternion quaternion;
		if (particleAngle == 0.0F) {
			quaternion = renderInfo.getRotation();
		} else {
			quaternion = new Quaternion(renderInfo.getRotation());
			final float particleAngleActual = MathHelper.lerp(partialTicks, prevParticleAngle, particleAngle);
			quaternion.multiply(Vector3f.ZP.rotation(particleAngleActual));
		}
		
		final Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f1.transform(quaternion);
		final Vector3f[] matrix = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F),
		                                              new Vector3f(-1.0F,  1.0F, 0.0F),
		                                              new Vector3f( 1.0F,  1.0F, 0.0F),
		                                              new Vector3f( 1.0F, -1.0F, 0.0F) };
		
		for(int i = 0; i < 4; ++i) {
			Vector3f vector3f = matrix[i];
			vector3f.transform(quaternion);
			vector3f.mul(scale);
			vector3f.add(x, y, z);
		}
		
		vertexBuffer.pos(matrix[0].getX(), matrix[0].getY(), matrix[0].getZ()).tex(maxU, maxV).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessForRender).endVertex();
		vertexBuffer.pos(matrix[1].getX(), matrix[1].getY(), matrix[1].getZ()).tex(maxU, minV).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessForRender).endVertex();
		vertexBuffer.pos(matrix[2].getX(), matrix[2].getY(), matrix[2].getZ()).tex(minU, minV).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessForRender).endVertex();
		vertexBuffer.pos(matrix[3].getX(), matrix[3].getY(), matrix[3].getZ()).tex(minU, maxV).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessForRender).endVertex();
	}
}