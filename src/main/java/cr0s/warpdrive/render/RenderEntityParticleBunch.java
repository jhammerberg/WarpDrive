package cr0s.warpdrive.render;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.entity.EntityParticleBunch;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

@OnlyIn(Dist.CLIENT)
public class RenderEntityParticleBunch extends EntityRenderer<EntityParticleBunch> {
	
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("minecraft:dirt");
	
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_SIZE_X          = { 0.00,   0.8,   1.0,   8.0,  10.0,  80.0, 100.0 };
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_SIZE_Y          = { 0.12, 0.080, 0.060, 0.050, 0.040, 0.030, 0.020 };
	
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_RED_INSIDE_Y    = { 0.40,  0.60,  0.70,  0.80,  0.60,  0.20,  0.20 };
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_GREEN_INSIDE_Y  = { 0.40,  0.50,  0.40,  0.20,  0.20,  0.30,  0.40 };
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_BLUE_INSIDE_Y   = { 0.20,  0.20,  0.50,  0.60,  0.60,  0.70,  0.80 };
	
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_RED_OUTSIDE_Y   = { 0.70,  0.90,  0.80,  0.90,  0.80,  0.65,  0.45 };
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_GREEN_OUTSIDE_Y = { 0.80,  1.00,  0.90,  0.80,  0.60,  0.75,  1.00 };
	public static final double[]  PARTICLE_BUNCH_ENERGY_TO_BLUE_OUTSIDE_Y  = { 0.20,  0.30,  0.50,  0.60,  0.60,  0.80,  0.90 };
	
	public RenderEntityParticleBunch(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull final EntityParticleBunch entityParticleBunch) {
		return TEXTURE_LOCATION;
	}
	
	@Override
	public void render(@Nonnull final EntityParticleBunch entityParticleBunch, float entityYaw, final float partialTicks,
	                   @Nonnull final MatrixStack matrixStack, @Nonnull final IRenderTypeBuffer buffer, final int packedLightIn) {
		// adjust render distance
		final int maxRenderDistanceSquared;
		if (Minecraft.getInstance().gameSettings.fancyGraphics) {
			maxRenderDistanceSquared = 128 * 128;
		} else {
			maxRenderDistanceSquared = 20 * 20;
		}
		/* TODO MC1.15 particle bunch rendering
		if ((x * x + y * y + z * z) > maxRenderDistanceSquared) {
			return;
		}
		*/
		
		// compute parameters
		final double energy = entityParticleBunch.getEnergy();
		final float size = (float) Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_SIZE_Y, energy);
		final int rayCount_base = 45;
		
		// common render parameters
		RenderSystem.pushMatrix();
		// TODO MC1.15 particle bunch rendering
		// RenderSystem.translated(x, y, z);
		RenderSystem.scalef(size, size, size);
		// lightmap already done by caller, see getBrightnessForRender()
		
		renderStar(entityParticleBunch.ticksExisted + partialTicks, entityParticleBunch.getEntityId(), rayCount_base,
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_RED_INSIDE_Y   , energy)),
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_GREEN_INSIDE_Y , energy)),
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_BLUE_INSIDE_Y  , energy)),
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_RED_OUTSIDE_Y  , energy)),
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_GREEN_OUTSIDE_Y, energy)),
	        (int) (255.0F * Commons.interpolate(PARTICLE_BUNCH_ENERGY_TO_SIZE_X, PARTICLE_BUNCH_ENERGY_TO_BLUE_OUTSIDE_Y , energy)) );
        
        // restore
		RenderSystem.popMatrix();
	}
	
	// Loosely based on ender dragon death effect
	private static void renderStar(final float ticksExisted, final long seed, final int rayCount_base,
						   final int redIn, final int greenIn, final int blueIn,
						   final int redOut, final int greenOut, final int blueOut ) {
		final Random random = new Random(seed);
		
		// compute rotation cycle
		final int tickRotationPeriod = 220 + 2 * random.nextInt(30);
		int tickRotation = (int) (ticksExisted % tickRotationPeriod);
		if (tickRotation >= tickRotationPeriod / 2) {
			tickRotation = tickRotationPeriod - tickRotation - 1;
		}
		final float cycleRotation = 2 * tickRotation / (float) tickRotationPeriod;
	    
		// compute number of rays
		// final int rayCount = 45 + (int) ((cycleRotation + cycleRotation * cycleRotation) * 15.0F);
		final int rayCount = rayCount_base + random.nextInt(10);
		
		// drawing preparation
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexBuffer = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();
		RenderSystem.disableTexture();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.enableCull();
		RenderSystem.depthMask(false);
		
		for (int i = 0; i < rayCount; i++) {
			// compute boost pulsation cycle
			final int tickBoostPeriod = 15 + 2 * random.nextInt(10);
			int tickBoost = (int) (ticksExisted % tickBoostPeriod);
			if (tickBoost >= tickBoostPeriod / 2) {
				tickBoost = tickBoostPeriod - tickBoost - 1;
			}
			final float cycleBoost = 2 * tickBoost / (float) tickBoostPeriod;
			float boost = 0.0F;
			if (cycleBoost > 0.6F) {
				boost = (cycleBoost - 0.6F) / 0.4F;
			}
			
			// compute branch orientation
			RenderSystem.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			RenderSystem.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef(random.nextFloat() * 360.0F + cycleRotation * 90F, 0.0F, 0.0F, 1.0F);
			vertexBuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			final float rayLength = random.nextFloat() * 15.0F + 5.0F + boost *  5.0F;
			final float rayWidth  = random.nextFloat() *  2.0F + 1.0F + boost *  1.0F;
			vertexBuffer.pos( 0.0D             ,      0.0D,  0.0D           ).color(redIn, greenIn, blueIn, (int) (190.0F + 64.0F * (1.0F - boost))).endVertex();
			vertexBuffer.pos(-0.866D * rayWidth, rayLength, -0.5D * rayWidth).color(redOut, greenOut, blueOut, 0).endVertex();
			vertexBuffer.pos( 0.866D * rayWidth, rayLength, -0.5D * rayWidth).color(redOut, greenOut, blueOut, 0).endVertex();
			vertexBuffer.pos( 0.000D           , rayLength,  1.0D * rayWidth).color(redOut, greenOut, blueOut, 0).endVertex();
			vertexBuffer.pos(-0.866D * rayWidth, rayLength, -0.5D * rayWidth).color(redOut, greenOut, blueOut, 0).endVertex();
			tessellator.draw();
		}
		
		// drawing closure
		RenderSystem.depthMask(true);
		RenderSystem.disableCull();
		RenderSystem.disableBlend();
		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.enableTexture();
		RenderSystem.enableAlphaTest();
		RenderHelper.enableStandardItemLighting();
	}
}