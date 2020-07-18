package cr0s.warpdrive.render;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.energy.BlockCapacitor;
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
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;

public class BakedModelCapacitor extends BakedModelAbstractBase {
	
	public BakedModelCapacitor() {
	}
	
	public IBakedModel getOriginalBakedModel() {
		return bakedModelOriginal;
	}
	
	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable final BlockState blockState, @Nullable final Direction side, @Nonnull final Random rand, @Nonnull IModelData modelData) {
		assert modelResourceLocation != null;
		assert bakedModelOriginal != null;
		
		final BlockState blockStateActual;
		if (blockState == null) {
			// dead code until we have different blocks for each tiers to support item rendering and 1.13+
			if (blockStateDefault == null) {
				blockStateDefault = WarpDrive.blockCapacitors[0].getDefaultState()
				        .with(BlockCapacitor.DOWN , EnumDisabledInputOutput.INPUT)
				        .with(BlockCapacitor.UP   , EnumDisabledInputOutput.INPUT)
				        .with(BlockCapacitor.NORTH, EnumDisabledInputOutput.OUTPUT)
				        .with(BlockCapacitor.SOUTH, EnumDisabledInputOutput.OUTPUT)
				        .with(BlockCapacitor.WEST , EnumDisabledInputOutput.OUTPUT)
				        .with(BlockCapacitor.EAST , EnumDisabledInputOutput.OUTPUT);
			}
			blockStateActual = blockStateDefault;
		} else {
			blockStateActual = null;
		}
		if (blockStateActual != null) {
			final EnumDisabledInputOutput enumDisabledInputOutput = getEnumDisabledInputOutput(blockStateActual, side);
			if (enumDisabledInputOutput == null) {
				if (Commons.throttleMe("BakedModelCapacitor invalid extended")) {
					new RuntimeException(String.format("%s Invalid extended property for %s side %s\n%s",
					                                   this, blockStateActual, side, formatDetails() ))
							.printStackTrace(WarpDrive.printStreamError);
				}
				return getDefaultQuads(side, rand, modelData);
			}
			final BlockState blockStateToRender = blockStateActual.with(BlockCapacitor.CONFIG, enumDisabledInputOutput);
			
			// remap to the json model representing the proper state
			final BlockModelShapes blockModelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
			final IBakedModel bakedModelWrapped = blockModelShapes.getModel(blockStateToRender);
			final IBakedModel bakedModelToRender = ((BakedModelCapacitor) bakedModelWrapped).getOriginalBakedModel();
			return bakedModelToRender.getQuads(blockStateToRender, side, rand, modelData);
		}
		return getDefaultQuads(side, rand, modelData);
	}
	
	public EnumDisabledInputOutput getEnumDisabledInputOutput(final BlockState blockState, @Nullable final Direction facing) {
		if (facing == null) {
			return EnumDisabledInputOutput.DISABLED;
		}
		switch (facing) {
		case DOWN : return blockState.get(BlockCapacitor.DOWN);
		case UP   : return blockState.get(BlockCapacitor.UP);
		case NORTH: return blockState.get(BlockCapacitor.NORTH);
		case SOUTH: return blockState.get(BlockCapacitor.SOUTH);
		case WEST : return blockState.get(BlockCapacitor.WEST);
		case EAST : return blockState.get(BlockCapacitor.EAST);
		default: return EnumDisabledInputOutput.DISABLED;
		}
	}
	
	public List<BakedQuad> getDefaultQuads(final Direction side, @Nonnull final Random rand, @Nonnull IModelData modelData) {
		final BlockState blockState = Blocks.FIRE.getDefaultState();
		return Minecraft.getInstance().getBlockRendererDispatcher()
		       .getModelForState(blockState).getQuads(blockState, side, rand, modelData);
	}
	
	private String formatDetails() {
		return String.format("modelResourceLocation %s\nbakedModelOriginal %s\nextendedBlockStateDefault %s]",
		                     modelResourceLocation,
		                     bakedModelOriginal,
		                     blockStateDefault);
	}
}