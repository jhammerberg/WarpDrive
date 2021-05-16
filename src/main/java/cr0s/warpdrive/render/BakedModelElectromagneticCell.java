package cr0s.warpdrive.render;

import cr0s.warpdrive.api.ParticleStack;
import cr0s.warpdrive.item.ItemElectromagneticCell;

import javax.annotation.Nonnull;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BakedModelElectromagneticCell extends BakedModelAbstractBase {
	
	private final HashMap<String,ModelResourceLocation> particleKeyToModelLocation = new HashMap<>(5);
	
	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return itemOverrideList;
	}
	
	protected ItemOverrideList itemOverrideList = new ItemOverrideList() {
		@Override
		public IBakedModel getModelWithOverrides(@Nonnull final IBakedModel bakedModel, @Nonnull final ItemStack itemStack,
		                                         final World world, final LivingEntity entity) {
			if (itemStack.getItem() instanceof ItemElectromagneticCell) {
				// get the particle type
				final ParticleStack particleStack = ((ItemElectromagneticCell) itemStack.getItem()).getParticleStack(itemStack);
				if (particleStack == null) {
					return bakedModel;
				}
				final String particleKey = particleStack.getParticle().getTranslationKey();
				// cache the model name to save from reallocating strings every frames
				ModelResourceLocation modelResourceLocationNew = particleKeyToModelLocation.get(particleKey);
				if (modelResourceLocationNew == null) {
					final String variant = particleKey.replace("warpdrive.particle.", "");
					modelResourceLocationNew = new ModelResourceLocation(modelResourceLocation.toString().replace("#", "-" + variant + "#"));
					particleKeyToModelLocation.put(particleKey, modelResourceLocationNew);
				}
				// retrieve the baked model for that particle type
				final ModelManager modelManager = Minecraft.getInstance().getModelManager();
				final IBakedModel bakedModelNew = modelManager.getModel(modelResourceLocationNew);
				// apply that baked model's overrides
				return bakedModelNew.getOverrides().getModelWithOverrides(bakedModelNew, itemStack, world, entity);
			} else {
				return bakedModelOriginal.getOverrides().getModelWithOverrides(bakedModel, itemStack, world, entity);
			}
		}
	};
}