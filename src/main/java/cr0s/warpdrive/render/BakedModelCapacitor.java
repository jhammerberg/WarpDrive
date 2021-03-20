package cr0s.warpdrive.render;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.energy.BlockCapacitor;
import cr0s.warpdrive.block.energy.TileEntityCapacitor;
import cr0s.warpdrive.data.EnumDisabledInputOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;

public class BakedModelCapacitor extends BakedModelAbstractBase {
	
	public BakedModelCapacitor() {
	}
	
	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return itemBlockOverrideList;
	}
	
	public IBakedModel getOriginalBakedModel() {
		return bakedModelOriginal;
	}
	
	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable final BlockState blockState, @Nullable final Direction side, @Nonnull final Random rand,
	                                @Nonnull final IModelData modelData) {
		assert modelResourceLocation != null;
		assert bakedModelOriginal != null;
		
		if (blockState != null) {
			final EnumDisabledInputOutput enumDisabledInputOutput = getEnumDisabledInputOutput(modelData, side);
			if (enumDisabledInputOutput == null) {
				if (Commons.throttleMe("BakedModelCapacitor::getQuads invalid IModelData")) {
					new RuntimeException(String.format("%s Invalid IModelData for %s side %s\n%s",
					                                   this, blockState, side, formatDetails() ))
							.printStackTrace(WarpDrive.printStreamError);
				}
			}
			final BlockState blockStateToRender = blockState.with(BlockCapacitor.CONFIG, enumDisabledInputOutput != null ? enumDisabledInputOutput : EnumDisabledInputOutput.DISABLED);
			
			// remap to the json model representing the proper state
			final BlockModelShapes blockModelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
			final IBakedModel bakedModelWrapped = blockModelShapes.getModel(blockStateToRender);
			final IBakedModel bakedModelToRender = ((BakedModelCapacitor) bakedModelWrapped).getOriginalBakedModel();
			return bakedModelToRender.getQuads(blockStateToRender, side, rand, modelData);
		}
		return getDefaultQuads(side, rand, modelData);
	}
	
	public EnumDisabledInputOutput getEnumDisabledInputOutput(@Nonnull final IModelData modelData, @Nullable final Direction facing) {
		if (facing == null) {
			return EnumDisabledInputOutput.DISABLED;
		}
		switch (facing) {
		case DOWN : return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_DOWN);
		case UP   : return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_UP);
		case NORTH: return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_NORTH);
		case SOUTH: return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_SOUTH);
		case WEST : return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_WEST);
		case EAST : return modelData.getData(TileEntityCapacitor.MODEL_PROPERTY_EAST);
		default: return EnumDisabledInputOutput.DISABLED;
		}
	}
	
	public List<BakedQuad> getDefaultQuads(final Direction side, @Nonnull final Random rand, @Nonnull IModelData modelData) {
		final BlockState blockState = Blocks.FIRE.getDefaultState();
		return Minecraft.getInstance().getBlockRendererDispatcher()
		       .getModelForState(blockState).getQuads(blockState, side, rand, modelData);
	}
}