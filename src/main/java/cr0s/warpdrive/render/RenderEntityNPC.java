package cr0s.warpdrive.render;

import cr0s.warpdrive.entity.EntityNPC;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderEntityNPC extends LivingRenderer<EntityNPC, BipedModel<EntityNPC>> {
	
	public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
	
	public RenderEntityNPC(@Nonnull final EntityRendererManager renderManager) {
		super(renderManager, new BipedModel<>(1.0F), 0.5F);
		
		/*
		@TODO: a redesign needed so that we dispatch to original renderers instead of reimplementing them
		
		// textures/entity/steve.png
		// textures/entity/alex.png
		final boolean useSmallArms = true;
		final ModelBiped modelPlayer = new ModelPlayer(0.0F, useSmallArms);
		mainModel = modelPlayer;
		addLayer(new LayerBipedArmor(this));
		addLayer(new LayerHeldItem(this));
		addLayer(new LayerArrow(this));
		addLayer(new LayerCustomHead(modelPlayer.bipedHead));
		addLayer(new LayerElytra(this));
		
		// textures/entity/zombie/zombie.png
		mainModel = new ModelZombie();
		addLayer(new LayerBipedArmor(this) {
			@Override
			protected void initArmor() {
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor = new ModelZombie(1.0F, true);
			}
		});
		/**/
	}
	
	@Override
	protected void preRenderCallback(@Nonnull final EntityNPC entityNPC, @Nonnull final MatrixStack matrixStack, final float partialTickTime) {
		super.preRenderCallback(entityNPC, matrixStack, partialTickTime);
		
		final float sizeScale = entityNPC.getSizeScale();
		RenderSystem.scalef(sizeScale, sizeScale, sizeScale);
	}
	
	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull final EntityNPC entityNPC) {
		final String textureString = entityNPC.getTextureString();
		if ( !textureString.isEmpty()
		  && ( textureString.contains(":")
			|| textureString.contains("/") ) ) {
			return new ResourceLocation(textureString);
		}
		
		return LOCATION_MISSING_TEXTURE;
	}
	
	@Override
	public void render(@Nonnull final EntityNPC entityNPC, float entityYaw, final float partialTicks,
	                   @Nonnull final MatrixStack matrixStack, @Nonnull final IRenderTypeBuffer buffer, final int packedLightIn) {
		super.render(entityNPC, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
	}
}
