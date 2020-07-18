package cr0s.warpdrive.render;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

@OnlyIn(Dist.CLIENT)
public class EntityFXBeam extends Particle {
	
	private static final int ROTATION_SPEED = 20;
	private static final float END_MODIFIER = 1.0F;
	private static final ResourceLocation TEXTURE = new ResourceLocation("warpdrive", "textures/particle/energy_grey.png");
	
	private float length = 0.0F;
	private float rotYaw = 0.0F;
	private float rotPitch = 0.0F;
	private float prevYaw = 0.0F;
	private float prevPitch = 0.0F;
	private float prevSize = 0.0F;
	
	public EntityFXBeam(final World world, final Vector3 position, final Vector3 target,
	                    final float red, final float green, final float blue,
	                    final int age) {
		super(world, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
		
		this.setColor(red, green, blue);
		this.setSize(0.02F, 0.02F);
		this.canCollide = false;
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		
		final float xd = (float) (this.posX - target.x);
		final float yd = (float) (this.posY - target.y);
		final float zd = (float) (this.posZ - target.z);
		this.length = (float) position.distanceTo(target);
		final double lengthXZ = MathHelper.sqrt(xd * xd + zd * zd);
		this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
		this.rotPitch = (float) (Math.atan2(yd, lengthXZ) * 180.0D / Math.PI);
		this.prevYaw = this.rotYaw;
		this.prevPitch = this.rotPitch;
		this.maxAge = age;
		
		// kill the particle if it's too far away
		final Entity entityRender = Minecraft.getInstance().getRenderViewEntity();
		int visibleDistance = 300;
		
		if (!Minecraft.getInstance().gameSettings.fancyGraphics) {
			visibleDistance = 100;
		}
		
		if ( entityRender != null
		  && entityRender.getDistanceSq(posX, posY, posZ) > visibleDistance * visibleDistance ) {
			maxAge = 0;
		}
	}
	
	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevYaw = rotYaw;
		prevPitch = rotPitch;
		
		if (age++ >= maxAge) {
			setExpired();
		}
	}
	
	@Override
	public void renderParticle(@Nonnull final IVertexBuilder vertexBuffer, @Nonnull final ActiveRenderInfo renderInfo, final float partialTicks) {
		RenderSystem.pushMatrix();
		
		final float rot = world.getGameTime() % (360 / ROTATION_SPEED) * ROTATION_SPEED + ROTATION_SPEED * partialTicks;
		
		final float sizeTarget = Math.min(age / 4.0F, 1.0F);
		final float size = prevSize + (sizeTarget - prevSize) * partialTicks;
		
		// alpha starts at 50%, vanishing to 10% during last ticks
		float alpha = 0.5F;
		if (maxAge - age <= 4) {
			alpha = 0.5F - (4 - (maxAge - age)) * 0.1F;
		}
		
		// get brightness factors
		final int brightnessForRender = getBrightnessForRender(partialTicks);
		final int brightnessHigh = brightnessForRender >> 16 & 65535;
		final int brightnessLow  = Math.max(240, brightnessForRender & 65535);
		
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		RenderSystem.disableCull();
		
		final float relativeTime = world.getGameTime() + partialTicks;
		final float vOffset = -relativeTime * 0.2F - MathHelper.floor(-relativeTime * 0.1F);
		
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		RenderSystem.depthMask(false);
		
		final Vec3d vec3d = renderInfo.getProjectedView();
		final float xx = (float)(MathHelper.lerp(partialTicks, prevPosX, posX) - vec3d.getX());
		final float yy = (float)(MathHelper.lerp(partialTicks, prevPosY, posY) - vec3d.getY());
		final float zz = (float)(MathHelper.lerp(partialTicks, prevPosZ, posZ) - vec3d.getZ());
		RenderSystem.translatef(xx, yy, zz);
		
		final float rotYaw = prevYaw + (this.rotYaw - prevYaw) * partialTicks;
		final float rotPitch = prevPitch + (this.rotPitch - prevPitch) * partialTicks;
		RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(180.0F + rotYaw, 0.0F, 0.0F, -1.0F);
		RenderSystem.rotatef(rotPitch, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(rot, 0.0F, 1.0F, 0.0F);
		
		final double xMinStart = -0.15D * size;
		final double xMaxStart = 0.15D * size;
		final double xMinEnd = -0.15D * size * END_MODIFIER;
		final double xMaxEnd = 0.15D * size * END_MODIFIER;
		final double yMax = length * size;
		final float uMin = 0.0F;
		final float uMax = 1.0F;
		
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
		for (int t = 0; t < 3; t++) {
			final float vMin = -1.0F + vOffset + t / 3.0F;
			final float vMax = vMin + length * size;
			RenderSystem.rotatef(60.0F, 0.0F, 1.0F, 0.0F);
			vertexBuffer.pos(xMinEnd  , yMax, 0.0D).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(xMinStart, 0.0D, 0.0D).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(xMaxStart, 0.0D, 0.0D).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(xMaxEnd  , yMax, 0.0D).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			tessellator.draw();
		}
		
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.popMatrix();
		prevSize = size;
		Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
	}
	
	@Override
	@Nonnull
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}
}