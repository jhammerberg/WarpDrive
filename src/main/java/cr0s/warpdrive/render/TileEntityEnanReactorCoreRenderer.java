package cr0s.warpdrive.render;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.energy.TileEntityEnanReactorCore;
import cr0s.warpdrive.client.SpriteManager;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.client.model.geometry.IModelGeometry;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

public class TileEntityEnanReactorCoreRenderer extends TileEntityRenderer<TileEntityEnanReactorCore> {
	
	private IBakedModel bakedModelCore;
	private IBakedModel bakedModelMatter;
	private IBakedModel bakedModelSurface;
	private IBakedModel bakedModelShield;
	private static List<BakedQuad> quadsCore;
	private static List<BakedQuad> quadsMatter;
	private static List<BakedQuad> quadsSurface;
	private static List<BakedQuad> quadsShield;
	
	public TileEntityEnanReactorCoreRenderer(@Nonnull final TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/energy/reactor_core-crystal"));
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/energy/reactor_core-grip"));
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/energy/reactor_matter"));
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/energy/reactor_surface"));
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/energy/reactor_shield"));
	}
	
	private void updateQuads() {
		// Since we cannot bake in preInit() we do lazy baking of the models as soon as we need it for rendering
		if (bakedModelCore == null) {
			final ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "block/energy/reactor_core.obj");
			final IModelGeometry<?> model = RenderCommons.getModel(resourceLocation);
			// TODO MC1.15 reactor core rendering
			// bakedModelCore = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		}
		quadsCore = bakedModelCore.getQuads(null, null, null, null);
		
		if (bakedModelMatter == null) {
			final ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "block/energy/reactor_matter.obj");
			final IModelGeometry<?> model = RenderCommons.getModel(resourceLocation);
			// MC1.15 reactor core rendering
			// bakedModelMatter = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		}
		quadsMatter = bakedModelMatter.getQuads(null, null, null, null);
		
		if (bakedModelSurface == null) {
			final ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "block/energy/reactor_surface.obj");
			final IModelGeometry<?> model = RenderCommons.getModel(resourceLocation);
			// MC1.15 reactor core rendering
			// bakedModelSurface = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		}
		quadsSurface = bakedModelSurface.getQuads(null, null, null, null);
		
		if (bakedModelShield == null) {
			final ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "block/energy/reactor_shield.obj");
			final IModelGeometry<?> model = RenderCommons.getModel(resourceLocation);
			// MC1.15 reactor core rendering
			// bakedModelShield = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		}
		quadsShield = bakedModelShield.getQuads(null, null, null, null);
	}
	
	@Override
	public void render(@Nonnull final TileEntityEnanReactorCore tileEntityEnanReactorCore,
	                   final float partialTicks, @Nonnull final MatrixStack matrixStack,
	                   @Nonnull final IRenderTypeBuffer renderTypeBuffer, final int combinedLightIn, final int combinedOverlayIn) {
		if ( tileEntityEnanReactorCore.getWorld() == null
		  || !tileEntityEnanReactorCore.getWorld().isAreaLoaded(tileEntityEnanReactorCore.getPos(), 3) ) {
			return;
		}
		if (quadsCore == null) {
			updateQuads();
		}
		final Tessellator tessellator = Tessellator.getInstance();
		RenderSystem.pushLightingAttributes();
		RenderSystem.pushMatrix();
		
		final double yCore = tileEntityEnanReactorCore.client_yCore + partialTicks * tileEntityEnanReactorCore.client_yCoreSpeed_mPerTick;
		matrixStack.push();
		matrixStack.translate(0.5D, yCore, 0.5D);
		
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.enableBlend();
		// RenderSystem.disableCull();
		RenderHelper.disableStandardItemLighting();
		
		// render the core
		RenderSystem.enableLighting();
		Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		final BufferBuilder worldRenderer = tessellator.getBuffer();
		
		RenderSystem.pushMatrix();
		
		final float rotationCore = tileEntityEnanReactorCore.client_rotationCore_deg + partialTicks * tileEntityEnanReactorCore.client_rotationSpeedCore_degPerTick;
		RenderSystem.rotatef(rotationCore, 0.0F, 1.0F, 0.0F);
		
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		RenderCommons.renderModelTESR(quadsCore, worldRenderer, tileEntityEnanReactorCore.getWorld().getLightSubtracted(tileEntityEnanReactorCore.getPos(), 15));
		tessellator.draw();
		
		RenderSystem.popMatrix();
		
		RenderSystem.disableLighting();
		
		// render the matter plasma
		if (tileEntityEnanReactorCore.client_radiusMatter_m > 0.0F) {
			final float radiusMatter = tileEntityEnanReactorCore.client_radiusMatter_m + partialTicks * tileEntityEnanReactorCore.client_radiusSpeedMatter_mPerTick;
			final float heightMatter = Math.max(1.0F, radiusMatter * 1.70F);
			
			// matter model, slightly smaller
			RenderSystem.pushMatrix();
			
			RenderSystem.scalef(radiusMatter * 0.95F, heightMatter * 0.90F, radiusMatter * 0.95F);
			final float rotationMatter = tileEntityEnanReactorCore.client_rotationMatter_deg + (partialTicks - 0.75F) * tileEntityEnanReactorCore.client_rotationSpeedMatter_degPerTick;
			RenderSystem.rotatef(rotationMatter, 0.0F, 1.0F, 0.0F);
			
			worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			RenderCommons.renderModelTESR(quadsMatter, worldRenderer, tileEntityEnanReactorCore.getWorld().getLightSubtracted(tileEntityEnanReactorCore.getPos(), 15));
			tessellator.draw();
			
			RenderSystem.popMatrix();
			
			// surface model (transparent surface)
			RenderSystem.pushMatrix();
			
			RenderSystem.scalef(radiusMatter, heightMatter, radiusMatter);
			final float rotationSurface = tileEntityEnanReactorCore.client_rotationSurface_deg + partialTicks * tileEntityEnanReactorCore.client_rotationSpeedSurface_degPerTick;
			RenderSystem.rotatef(rotationSurface, 0.0F, 1.0F, 0.0F);
			
			worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			RenderCommons.renderModelTESR(quadsSurface, worldRenderer, tileEntityEnanReactorCore.getWorld().getLightSubtracted(tileEntityEnanReactorCore.getPos(), 15));
			tessellator.draw();
			
			RenderSystem.popMatrix();
		}
		
		// render the shield
		if (tileEntityEnanReactorCore.client_radiusShield_m > 0.0F) {
			// shield model, slightly bigger
			final float radiusShield = tileEntityEnanReactorCore.client_radiusShield_m + partialTicks * tileEntityEnanReactorCore.client_radiusSpeedShield_mPerTick;
			final float heightShield = Math.max(0.75F, radiusShield * 0.70F);
			RenderSystem.scalef(radiusShield, heightShield, radiusShield);
			RenderSystem.rotatef(rotationCore, 0.0F, 1.0F, 0.0F);
			
			worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			RenderCommons.renderModelTESR(quadsShield, worldRenderer, tileEntityEnanReactorCore.getWorld().getLightSubtracted(tileEntityEnanReactorCore.getPos(), 15));
			tessellator.draw();
		}
		
		matrixStack.pop();
		
		RenderHelper.enableStandardItemLighting();
		RenderSystem.disableBlend();
		// RenderSystem.enableCull();
		RenderSystem.popMatrix();
		RenderSystem.popAttributes();
	}
	
	@Override
	public boolean isGlobalRenderer(@Nonnull final TileEntityEnanReactorCore tileEntityEnanReactorCore) {
		return true;
	}
}
