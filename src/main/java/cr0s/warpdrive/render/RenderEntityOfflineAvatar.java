package cr0s.warpdrive.render;

import cr0s.warpdrive.client.PlayerTextureManager;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.entity.EntityOfflineAvatar;

import javax.annotation.Nonnull;

import java.util.UUID;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderEntityOfflineAvatar extends BipedRenderer<EntityOfflineAvatar, PlayerModel<EntityOfflineAvatar>> {
	
	public RenderEntityOfflineAvatar(@Nonnull final EntityRendererManager renderManager) {
		super(renderManager, new PlayerModel<>(WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE, true), WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE);
	}
	
	@Override
	protected void preRenderCallback(@Nonnull final EntityOfflineAvatar entityOfflineAvatar, @Nonnull final MatrixStack matrixStack, final float partialTickTime) {
		super.preRenderCallback(entityOfflineAvatar, matrixStack, partialTickTime);
		
		RenderSystem.scalef(WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE,
		                    WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE,
		                    WarpDriveConfig.OFFLINE_AVATAR_MODEL_SCALE);
	}
	
	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull final EntityOfflineAvatar entityOfflineAvatar) {
		final UUID uuidPlayer = entityOfflineAvatar.getPlayerUUID();
		final String namePlayer = entityOfflineAvatar.getPlayerName();
		if (uuidPlayer != null) {
			return PlayerTextureManager.getPlayerSkin(uuidPlayer, namePlayer);
		}
		
		return PlayerTextureManager.RESOURCE_LOCATION_DEFAULT;
	}
	
	@Override
	protected boolean canRenderName(@Nonnull final EntityOfflineAvatar entityOfflineAvatar) {
		return entityOfflineAvatar.getAlwaysRenderNameTagForRender();
	}
	
	@Override
	public void render(@Nonnull final EntityOfflineAvatar entityOfflineAvatar, float entityYaw, final float partialTicks,
	                   @Nonnull final MatrixStack matrixStack, @Nonnull final IRenderTypeBuffer buffer, final int packedLightIn) {
		super.render(entityOfflineAvatar, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
	}
}
