package cr0s.warpdrive.render;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.data.ModelProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;

public class BakedModelCamouflage extends BakedModelAbstractBase {
	
	public BakedModelCamouflage() {
	}
	
	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable final BlockState blockState, @Nullable final Direction side, @Nonnull final Random rand, @Nonnull IModelData modelData) {
		assert modelResourceLocation != null;
		assert bakedModelOriginal != null;
		
		final BlockState blockStateReference = modelData.getData(ModelProperties.CAMOUFLAGE);
		if (blockStateReference != null) {
			try {
				// Retrieve the IBakedModel of the copied block and return it.
				final BlockModelShapes blockModelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
				final IBakedModel bakedModelResult = blockModelShapes.getModel(blockStateReference);
				return bakedModelResult.getQuads(blockStateReference, side, rand, modelData);
			} catch(final Exception exception) {
				exception.printStackTrace(WarpDrive.printStreamError);
				WarpDrive.logger.error(String.format("Failed to render camouflage for block state %s, updating dictionary with %s = NOCAMOUFLAGE dictionary to prevent further errors",
				                                     blockStateReference,
				                                     blockStateReference.getBlock().getRegistryName()));
				Dictionary.BLOCKS_NOCAMOUFLAGE.add(blockStateReference.getBlock());
			}
		}
		return bakedModelOriginal.getQuads(blockState, side, rand, modelData);
	}
}