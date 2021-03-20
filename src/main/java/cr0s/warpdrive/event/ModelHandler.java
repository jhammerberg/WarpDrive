package cr0s.warpdrive.event;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IMyBakedModel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModelHandler {
	
	public static final ModelHandler INSTANCE = new ModelHandler();
	private static final List<ResourceLocation> resourceLocationSpecialModels = new ArrayList<>(64);
	private static final Map<ModelResourceLocation, Class<? extends IMyBakedModel>> modelResourceLocationToBakedModel = new HashMap<>(64);
	
	private ModelHandler() {
		
	}
	
	public static void registerSpecialModel(final ResourceLocation resourceLocation) {
		resourceLocationSpecialModels.add(resourceLocation);
	}
	
	public static void registerBakedModel(final ModelResourceLocation modelResourceLocation, final Class<? extends IMyBakedModel> classBakedModel) {
		modelResourceLocationToBakedModel.put(modelResourceLocation, classBakedModel);
	}
	
	// Called before loading the models
	@SubscribeEvent
	public void onModelRegistry(@Nonnull final ModelRegistryEvent event) {
		for (final ResourceLocation resourceLocation : resourceLocationSpecialModels) {
			ModelLoader.addSpecialModel(resourceLocation);
		}
	}
	
	// Called after all the other baked block models have been added to the modelRegistry, before BlockModelShapes caches the models.
	@SubscribeEvent
	public void onModelBake(@Nonnull final ModelBakeEvent event) {
		for (final Entry<ModelResourceLocation, Class<? extends IMyBakedModel>> entry : modelResourceLocationToBakedModel.entrySet()) {
			final IBakedModel bakedModelExisting = event.getModelRegistry().get(entry.getKey());
			if (bakedModelExisting == null) {
				WarpDrive.logger.warn(String.format("Unable to update baked model for missing %s",
				                                    entry.getKey()));
				continue;
			}
			
			final IMyBakedModel bakedModelNew;
			
			// add a custom baked model wrapping around automatically registered models (from JSON)
			try {
				bakedModelNew = entry.getValue().newInstance();
				bakedModelNew.setModelResourceLocation(entry.getKey());
				bakedModelNew.setOriginalBakedModel(bakedModelExisting);
			} catch (final Exception exception) {
				exception.printStackTrace(WarpDrive.printStreamError);
				WarpDrive.logger.error(String.format("Failed to update baked model through %s of %s",
				                                     entry.getKey(), entry.getValue()));
				continue;
			}
			
			event.getModelRegistry().put(entry.getKey(), bakedModelNew);
		}
	}
}