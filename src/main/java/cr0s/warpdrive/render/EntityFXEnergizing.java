package cr0s.warpdrive.render;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
public class EntityFXEnergizing extends AbstractEntityFX {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("warpdrive", "textures/particle/energy_grey.png");
	
	private final double radius;
	private final double length;
	private final int countSteps;
	private final float rotYaw;
	private final float rotPitch;
	private float prevYaw;
	private float prevPitch;
	
	public EntityFXEnergizing(final World world, final Vector3 position, final Vector3 target,
	                          final float red, final float green, final float blue,
	                          final int age, final float radius) {
		super(world, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
		
		setColor(red, green, blue);
		setSize(0.02F, 0.02F);
		canCollide = false;
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		
		this.radius = radius;
		
		final float xd = (float) (posX - target.x);
		final float yd = (float) (posY - target.y);
		final float zd = (float) (posZ - target.z);
		length = new Vector3(this).distanceTo(target);
		final double lengthXZ = MathHelper.sqrt(xd * xd + zd * zd);
		rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
		rotPitch = (float) (Math.atan2(yd, lengthXZ) * 180.0D / Math.PI);
		prevYaw = rotYaw;
		prevPitch = rotPitch;
		maxAge = age;
		
		// kill the particle if it's too far away
		// reduce cylinder resolution when fancy graphic are disabled
		final Entity entityRender = Minecraft.getInstance().getRenderViewEntity();
		int visibleDistance = 300;
		
		if (!Minecraft.getInstance().gameSettings.fancyGraphics) {
			visibleDistance = 100;
			countSteps = 1;
		} else {
			countSteps = 6;
		}
		
		if ( entityRender == null
		  || entityRender.getDistanceSq(posX, posY, posZ) > visibleDistance * visibleDistance ) {
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
		
		final double factorFadeIn = Math.min((age + partialTicks) / 20.0F, 1.0F);
		
		// alpha starts at 50%, vanishing to 10% during last ticks
		float alpha = 0.5F;
		if (maxAge - age <= 4) {
			alpha = 0.5F - (4 - (maxAge - age)) * 0.1F;
		} else {
			// add random flickering
			final double timeAlpha = ((getSeed() ^ 0x47C8) & 0xFFFF) + age + partialTicks + 0.0167D;
			alpha += Math.pow(Math.sin(timeAlpha * 0.37D) + Math.sin(0.178D + timeAlpha * 0.17D), 2.0D) * 0.05D;
		}
		
		// get brightness factors
		final int brightnessForRender = getBrightnessForRender(partialTicks);
		final int brightnessHigh = brightnessForRender >> 16 & 65535;
		final int brightnessLow  = Math.max(240, brightnessForRender & 65535);
		
		// texture clock is offset to de-synchronize particles
		final double timeTexture = (getSeed() & 0xFFFF) + age + partialTicks;
		
		// repeated a pixel column, changing periodically, to animate the texture
		final float uOffset = ((int) Math.floor(timeTexture * 0.5D) % 16) / 16.0F;
		
		// add vertical noise
		final float vOffset = (float) Math.pow(Math.sin(timeTexture * 0.20F), 2.0F) * 0.005F;
		
		
		// bind our texture, repeating on both axis
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// rendering on both sides
		RenderSystem.disableCull();
		
		// alpha transparency, don't update depth mask
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		RenderSystem.depthMask(false);
		
		// animated translation
		final Vec3d vec3d = renderInfo.getProjectedView();
		final float xx = (float)(MathHelper.lerp(partialTicks, prevPosX, posX) - vec3d.getX());
		final float yy = (float)(MathHelper.lerp(partialTicks, prevPosY, posY) - vec3d.getY());
		final float zz = (float)(MathHelper.lerp(partialTicks, prevPosZ, posZ) - vec3d.getZ());
		RenderSystem.translatef(xx, yy, zz);
		
		// animated rotation
		final float rotYaw = prevYaw + (this.rotYaw - prevYaw) * partialTicks;
		final float rotPitch = prevPitch + (this.rotPitch - prevPitch) * partialTicks;
		final float rotSpin = 0.0F;
		RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(180.0F + rotYaw, 0.0F, 0.0F, -1.0F);
		RenderSystem.rotatef(rotPitch, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(rotSpin, 0.0F, 1.0F, 0.0F);
		
		// actual parameters
		final double radius = this.radius * factorFadeIn;
		final double yMin = length * (0.5D - factorFadeIn / 2.0D);
		final double yMax = length * (0.5D + factorFadeIn / 2.0D);
		final float uMin = uOffset;
		final float uMax = uMin + 1.0F / 32.0F;
		
		final float vMin = -1.0F + vOffset;
		final float vMax = (float) (vMin + length * factorFadeIn);
		
		// start drawing
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
		
		// loop covering 45 deg, using symmetry to cover a full circle
		final double angleMax = Math.PI / 4.0D;
		final double angleStep = angleMax / countSteps;
		double angle = 0.0D;
		double cosPrev = radius * Math.cos(angle);
		double sinPrev = radius * Math.sin(angle);
		for (int indexStep = 1; indexStep <= countSteps; indexStep++) {
			angle += angleStep;
			final double cosNext = radius * Math.cos(angle);
			final double sinNext = radius * Math.sin(angle);
			
			// cos sin
			vertexBuffer.pos( cosPrev, yMax,  sinPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosPrev, yMin,  sinPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosNext, yMin,  sinNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosNext, yMax,  sinNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos(-cosPrev, yMax,  sinPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosPrev, yMin,  sinPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosNext, yMin,  sinNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosNext, yMax,  sinNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos( cosPrev, yMax, -sinPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosPrev, yMin, -sinPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosNext, yMin, -sinNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( cosNext, yMax, -sinNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos(-cosPrev, yMax, -sinPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosPrev, yMin, -sinPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosNext, yMin, -sinNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-cosNext, yMax, -sinNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			// sin cos
			vertexBuffer.pos( sinPrev, yMax,  cosPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinPrev, yMin,  cosPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinNext, yMin,  cosNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinNext, yMax,  cosNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos(-sinPrev, yMax,  cosPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinPrev, yMin,  cosPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinNext, yMin,  cosNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinNext, yMax,  cosNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos( sinPrev, yMax, -cosPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinPrev, yMin, -cosPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinNext, yMin, -cosNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos( sinNext, yMax, -cosNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			vertexBuffer.pos(-sinPrev, yMax, -cosPrev).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinPrev, yMin, -cosPrev).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinNext, yMin, -cosNext).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			vertexBuffer.pos(-sinNext, yMax, -cosNext).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, alpha).lightmap(brightnessHigh, brightnessLow).endVertex();
			
			cosPrev = cosNext;
			sinPrev = sinNext;
		}
		
		// draw
		tessellator.draw();
		
		// restore OpenGL state
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.popMatrix();
	}
	
	@Override
	@Nonnull
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}
}