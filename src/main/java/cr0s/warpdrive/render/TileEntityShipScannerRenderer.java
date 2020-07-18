package cr0s.warpdrive.render;

import cr0s.warpdrive.block.building.TileEntityShipScanner;
import cr0s.warpdrive.client.SpriteManager;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

public class TileEntityShipScannerRenderer extends TileEntityRenderer<TileEntityShipScanner> {
	
	private static List<BakedQuad> bakedQuads;
	
	public TileEntityShipScannerRenderer(@Nonnull final TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
		SpriteManager.add(new ResourceLocation("warpdrive:blocks/building/ship_scanner-border"));
	}
	
	@Override
	public void render(@Nonnull final TileEntityShipScanner tileEntityShipScanner,
	                   final float partialTicks, @Nonnull final MatrixStack matrixStack,
	                   @Nonnull final IRenderTypeBuffer renderTypeBuffer, final int combinedLightIn, final int combinedOverlayIn) {
		if ( tileEntityShipScanner.getWorld() == null
		  || !tileEntityShipScanner.getWorld().isAreaLoaded(tileEntityShipScanner.getPos(), 1)) {
			return;
		}
		if (bakedQuads == null) {
			bakedQuads = new BakedModelShipScanner().getQuads(null, null, tileEntityShipScanner.getWorld().rand);
		}
		final Tessellator tessellator = Tessellator.getInstance();
		RenderSystem.pushLightingAttributes();
		RenderSystem.pushMatrix();
		
		RenderSystem.translated(0.5D, 0.5D, 0.5D);
		
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		// RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderHelper.disableStandardItemLighting();
		RenderSystem.disableLighting();
		
		Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		final BufferBuilder worldRenderer = tessellator.getBuffer();
		matrixStack.push();
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		
		RenderCommons.renderModelTESR(bakedQuads, worldRenderer, tileEntityShipScanner.getWorld().getLightSubtracted(tileEntityShipScanner.getPos(), 15));
		
		tessellator.draw();
		matrixStack.pop();
		
		RenderHelper.enableStandardItemLighting();
		RenderSystem.enableDepthTest();
		// RenderSystem.enableCull();
		RenderSystem.disableBlend();
		
		RenderSystem.popMatrix();
		RenderSystem.popAttributes();
	}
	
	@Override
	public boolean isGlobalRenderer(@Nonnull final TileEntityShipScanner tileEntityShipScanner) {
		return true;
	}
}