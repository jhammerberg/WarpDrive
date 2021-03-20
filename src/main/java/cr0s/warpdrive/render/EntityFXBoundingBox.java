package cr0s.warpdrive.render;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

@OnlyIn(Dist.CLIENT)
public class EntityFXBoundingBox extends Particle {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("warpdrive", "textures/particle/bounding_box.png");
	
	private final Vector3 min;
	private final Vector3 max;
	
	public EntityFXBoundingBox(final World world, final Vector3 position, final Vector3 min, final Vector3 max,
	                           final float red, final float green, final float blue, final int age) {
		super(world, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
		
		this.setColor(red, green, blue);
		this.setSize(0.02F, 0.02F);
		this.canCollide = false;
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.min = min;
		this.max = max;
		this.maxAge = age;
	}
	
	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (age++ >= maxAge) {
			setExpired();
		}
	}
	
	@Override
	public void renderParticle(@Nonnull final IVertexBuilder vertexBuffer, @Nonnull final ActiveRenderInfo renderInfo, final float partialTicks) {
		RenderSystem.pushMatrix();
		
		// final float rot = (world.provider.getGameTime() % (360 / rotationSpeed) + partialTicks) * rotationSpeed;
		
        // alpha starts at 50%, vanishing to 10% during last ticks
		float alpha = 0.45F;
		if (maxAge - age <= 2) {
			alpha = 0.35F; // 0.45F - (1 - (maxAge - age)) * 0.35F;
		} else if (age < 1) {
			alpha = 0.10F;
		}
		
		// get brightness factors
		final int brightnessForRender = getBrightnessForRender(partialTicks);
		final int brightnessHigh = brightnessForRender >> 16 & 65535;
		final int brightnessLow  = Math.max(240, brightnessForRender & 65535);
		
		// final double relativeTime = world.getGameTime() + partialTicks;
		// final double uOffset = (float) (-relativeTime * 0.3D - MathHelper.floor(-relativeTime * 0.15D));
		// final double vOffset = (float) (-relativeTime * 0.2D - MathHelper.floor(-relativeTime * 0.1D));
		
		// box position
		final double relativeTime = Math.abs(world.getGameTime() % 64L + partialTicks) / 64.0D;
		final double sizeOffset = 0.01F * (1.0F + (float) Math.sin(relativeTime * Math.PI * 2));
		final double xMin = min.x - posX - sizeOffset;
		final double xMax = max.x - posX + sizeOffset;
		final double yMin = min.y - posY - sizeOffset;
		final double yMax = max.y - posY + sizeOffset;
		final double zMin = min.z - posZ - sizeOffset;
		final double zMax = max.z - posZ + sizeOffset;
		
		// texture coordinates
		final float uvScale = 1.0F;
		final float uv_xMin = (float) xMin / uvScale + 0.5F;
		final float uv_xMax = (float) xMax / uvScale + 0.5F;
		final float uv_yMin = (float) yMin / uvScale + 0.5F;
		final float uv_yMax = (float) yMax / uvScale + 0.5F;
		final float uv_zMin = (float) zMin / uvScale + 0.5F;
		final float uv_zMax = (float) zMax / uvScale + 0.5F;
		
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		RenderSystem.disableCull();
		
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		RenderSystem.depthMask(false);
		
		final Vec3d vec3d = renderInfo.getProjectedView();
		final float xx = (float)(MathHelper.lerp(partialTicks, prevPosX, posX) - vec3d.getX());
		final float yy = (float)(MathHelper.lerp(partialTicks, prevPosY, posY) - vec3d.getY());
		final float zz = (float)(MathHelper.lerp(partialTicks, prevPosZ, posZ) - vec3d.getZ());
		RenderSystem.translatef(xx, yy, zz);
		
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
		
		// x planes
		vertexBuffer.pos(xMin, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMin, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMin, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMax, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMax, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		
		vertexBuffer.pos(xMax, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMin, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMin, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMax, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_yMax, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		
		// y planes
		vertexBuffer.pos(xMin, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		
		vertexBuffer.pos(xMin, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_zMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_zMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		
		// z planes
		vertexBuffer.pos(xMin, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_yMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_yMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_yMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMin, zMin).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_yMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		
		vertexBuffer.pos(xMin, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_yMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMin, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMin, uv_yMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMax, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_yMax).lightmap(brightnessHigh, brightnessLow).endVertex();
		vertexBuffer.pos(xMax, yMin, zMax).color(particleRed, particleGreen, particleBlue, alpha).tex(uv_xMax, uv_yMin).lightmap(brightnessHigh, brightnessLow).endVertex();
		tessellator.draw();
		
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