package cr0s.warpdrive.render;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractOmnipanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;

public class BakedModelOmnipanel extends BakedModelAbstractBase {
	
	public BakedModelOmnipanel() {
		super();
	}
	
	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return itemBlockOverrideList;
	}
	
	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable final BlockState blockState, @Nullable final Direction enumFacing, @Nonnull final Random rand, @Nonnull IModelData modelData) {
		assert modelResourceLocation != null;
		assert bakedModelOriginal != null;
		
		final BlockState blockStateActual;
		if (blockState != null) {
			blockStateActual = blockState;
			
		} else {
			// first, ensure we've a fallback
			if (blockStateDefault == null) {
				blockStateDefault = WarpDrive.blockHulls_omnipanel[0][0].getDefaultState()
						.with(BlockAbstractOmnipanel.CAN_CONNECT_Y_NEG, false)
						.with(BlockAbstractOmnipanel.CAN_CONNECT_Y_POS, false)
						.with(BlockAbstractOmnipanel.CAN_CONNECT_Z_NEG, false)
						.with(BlockAbstractOmnipanel.CAN_CONNECT_Z_POS, false)
						.with(BlockAbstractOmnipanel.CAN_CONNECT_X_NEG, false)
						.with(BlockAbstractOmnipanel.CAN_CONNECT_X_POS, false)
						.with(BlockAbstractOmnipanel.HAS_XN_YN, true)
						.with(BlockAbstractOmnipanel.HAS_XP_YN, true)
						.with(BlockAbstractOmnipanel.HAS_XN_YP, true)
						.with(BlockAbstractOmnipanel.HAS_XP_YP, true)
						.with(BlockAbstractOmnipanel.HAS_XN_ZN, true)
						.with(BlockAbstractOmnipanel.HAS_XP_ZN, true)
						.with(BlockAbstractOmnipanel.HAS_XN_ZP, true)
						.with(BlockAbstractOmnipanel.HAS_XP_ZP, true)
						.with(BlockAbstractOmnipanel.HAS_ZN_YN, true)
						.with(BlockAbstractOmnipanel.HAS_ZP_YN, true)
						.with(BlockAbstractOmnipanel.HAS_ZN_YP, true)
						.with(BlockAbstractOmnipanel.HAS_ZP_YP, true);
			}
			
			blockStateActual = blockStateDefault;
		}
		
		// normal model has no facing
		if (enumFacing != null) {
			return EMPTY;
		}
		
		// get color
		final int color = Minecraft.getInstance().getBlockColors().getColor(blockStateActual, null, null, tintIndexOriginal);
		
		// pre-compute coordinates
		final float dX_min = 0.0F;
		final float dX_max = 1.0F;
		final float dY_min = 0.0F;
		final float dY_max = 1.0F;
		final float dZ_min = 0.0F;
		final float dZ_max = 1.0F;
		final float dX_neg = BlockAbstractOmnipanel.CENTER_MIN;
		final float dX_pos = BlockAbstractOmnipanel.CENTER_MAX;
		final float dY_neg = BlockAbstractOmnipanel.CENTER_MIN;
		final float dY_pos = BlockAbstractOmnipanel.CENTER_MAX;
		final float dZ_neg = BlockAbstractOmnipanel.CENTER_MIN;
		final float dZ_pos = BlockAbstractOmnipanel.CENTER_MAX;
		
		final float dU_min = spriteOriginal.getMinU();
		final float dU_neg = spriteOriginal.getInterpolatedU(7.0F);
		final float dU_pos = spriteOriginal.getInterpolatedU(9.0F);
		final float dU_max = spriteOriginal.getMaxU();
		
		final float dV_min = spriteOriginal.getMinV();
		final float dV_neg = spriteOriginal.getInterpolatedV(7.0F);
		final float dV_pos = spriteOriginal.getInterpolatedV(9.0F);
		final float dV_max = spriteOriginal.getMaxV();
		
		// get direct connections
		final boolean canConnectY_neg = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_Y_NEG);
		final boolean canConnectY_pos = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_Y_POS);
		final boolean canConnectZ_neg = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_Z_NEG);
		final boolean canConnectZ_pos = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_Z_POS);
		final boolean canConnectX_neg = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_X_NEG);
		final boolean canConnectX_pos = blockStateActual.get(BlockAbstractOmnipanel.CAN_CONNECT_X_POS);
		final boolean canConnectNone = !canConnectY_neg && !canConnectY_pos && !canConnectZ_neg && !canConnectZ_pos && !canConnectX_neg && !canConnectX_pos;
		
		// get panels
		final boolean hasXnYn = blockStateActual.get(BlockAbstractOmnipanel.HAS_XN_YN);
		final boolean hasXpYn = blockStateActual.get(BlockAbstractOmnipanel.HAS_XP_YN);
		final boolean hasXnYp = blockStateActual.get(BlockAbstractOmnipanel.HAS_XN_YP);
		final boolean hasXpYp = blockStateActual.get(BlockAbstractOmnipanel.HAS_XP_YP);
		
		final boolean hasXnZn = blockStateActual.get(BlockAbstractOmnipanel.HAS_XN_ZN);
		final boolean hasXpZn = blockStateActual.get(BlockAbstractOmnipanel.HAS_XP_ZN);
		final boolean hasXnZp = blockStateActual.get(BlockAbstractOmnipanel.HAS_XN_ZP);
		final boolean hasXpZp = blockStateActual.get(BlockAbstractOmnipanel.HAS_XP_ZP);
		
		final boolean hasZnYn = blockStateActual.get(BlockAbstractOmnipanel.HAS_ZN_YN);
		final boolean hasZpYn = blockStateActual.get(BlockAbstractOmnipanel.HAS_ZP_YN);
		final boolean hasZnYp = blockStateActual.get(BlockAbstractOmnipanel.HAS_ZN_YP);
		final boolean hasZpYp = blockStateActual.get(BlockAbstractOmnipanel.HAS_ZP_YP);
		
		final List<BakedQuad> quads = new ArrayList<>();
		
		{// z plane
			if (hasXnYn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
				             dX_neg, dY_min, dZ_neg, dU_neg, dV_max,
				             dX_min, dY_min, dZ_neg, dU_min, dV_max,
				             dX_min, dY_neg, dZ_neg, dU_min, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_min, dY_neg, dZ_pos, dU_min, dV_pos,
				             dX_min, dY_min, dZ_pos, dU_min, dV_max,
				             dX_neg, dY_min, dZ_pos, dU_neg, dV_max,
				             dX_neg, dY_neg, dZ_pos, dU_neg, dV_pos);
			} else {
				if (canConnectX_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
					             dX_neg, dY_neg, dZ_pos, dU_neg, dV_neg,
					             dX_min, dY_neg, dZ_pos, dU_min, dV_neg,
					             dX_min, dY_neg, dZ_neg, dU_min, dV_pos);
				}
				if (canConnectY_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
					             dX_neg, dY_min, dZ_neg, dU_neg, dV_max,
					             dX_neg, dY_min, dZ_pos, dU_pos, dV_max,
					             dX_neg, dY_neg, dZ_pos, dU_pos, dV_pos);
				}
			}
			
			if (hasXpYn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_max, dY_neg, dZ_neg, dU_max, dV_pos,
				             dX_max, dY_min, dZ_neg, dU_max, dV_max,
				             dX_pos, dY_min, dZ_neg, dU_pos, dV_max,
				             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos,
				             dX_pos, dY_min, dZ_pos, dU_pos, dV_max,
				             dX_max, dY_min, dZ_pos, dU_max, dV_max,
				             dX_max, dY_neg, dZ_pos, dU_max, dV_pos);
			} else {
				if (canConnectX_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_max, dY_neg, dZ_neg, dU_max, dV_pos,
					             dX_max, dY_neg, dZ_pos, dU_max, dV_neg,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_neg,
					             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos);
				}
				if (canConnectY_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos,
					             dX_pos, dY_min, dZ_pos, dU_pos, dV_max,
					             dX_pos, dY_min, dZ_neg, dU_neg, dV_max,
					             dX_pos, dY_neg, dZ_neg, dU_neg, dV_pos);
				}
			}
			
			if (hasXnYp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_max, dZ_neg, dU_neg, dV_min,
				             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
				             dX_min, dY_pos, dZ_neg, dU_min, dV_neg,
				             dX_min, dY_max, dZ_neg, dU_min, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_min, dY_max, dZ_pos, dU_min, dV_min,
				             dX_min, dY_pos, dZ_pos, dU_min, dV_neg,
				             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg,
				             dX_neg, dY_max, dZ_pos, dU_neg, dV_min);
			} else {
				if (canConnectX_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_pos,
					             dX_min, dY_pos, dZ_neg, dU_min, dV_pos,
					             dX_min, dY_pos, dZ_pos, dU_min, dV_neg);
				}
				if (canConnectY_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_neg, dY_max, dZ_pos, dU_pos, dV_min,
					             dX_neg, dY_max, dZ_neg, dU_neg, dV_min,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg);
				}
			}
			
			if (hasXpYp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_max, dY_max, dZ_neg, dU_max, dV_min,
				             dX_max, dY_pos, dZ_neg, dU_max, dV_neg,
				             dX_pos, dY_pos, dZ_neg, dU_pos, dV_neg,
				             dX_pos, dY_max, dZ_neg, dU_pos, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_max, dZ_pos, dU_pos, dV_min,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_max, dY_pos, dZ_pos, dU_max, dV_neg,
				             dX_max, dY_max, dZ_pos, dU_max, dV_min);
			} else {
				if (canConnectX_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_pos, dZ_neg, dU_pos, dV_pos,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_max, dY_pos, dZ_pos, dU_max, dV_neg,
					             dX_max, dY_pos, dZ_neg, dU_max, dV_pos);
				}
				if (canConnectY_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_pos, dZ_neg, dU_neg, dV_neg,
					             dX_pos, dY_max, dZ_neg, dU_neg, dV_min,
					             dX_pos, dY_max, dZ_pos, dU_pos, dV_min,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg);
				}
			}
		}
		
		{// x plane
			if (hasZnYn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_min, dU_min, dV_pos,
				             dX_neg, dY_min, dZ_min, dU_min, dV_max,
				             dX_neg, dY_min, dZ_neg, dU_neg, dV_max,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_neg, dU_neg, dV_pos,
				             dX_pos, dY_min, dZ_neg, dU_neg, dV_max,
				             dX_pos, dY_min, dZ_min, dU_min, dV_max,
				             dX_pos, dY_neg, dZ_min, dU_min, dV_pos);
			} else {
				if (canConnectZ_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_min, dU_neg, dV_max,
					             dX_pos, dY_neg, dZ_min, dU_pos, dV_max,
					             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				}
				if (canConnectY_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos,
					             dX_pos, dY_min, dZ_neg, dU_pos, dV_max,
					             dX_neg, dY_min, dZ_neg, dU_neg, dV_max,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				}
			}
			
			if (hasZpYn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_pos, dU_pos, dV_pos,
				             dX_neg, dY_min, dZ_pos, dU_pos, dV_max,
				             dX_neg, dY_min, dZ_max, dU_max, dV_max,
				             dX_neg, dY_neg, dZ_max, dU_max, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_max, dU_max, dV_pos,
				             dX_pos, dY_min, dZ_max, dU_max, dV_max,
				             dX_pos, dY_min, dZ_pos, dU_pos, dV_max,
				             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos);
			} else {
				if (canConnectZ_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_pos, dU_neg, dV_neg,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_neg,
					             dX_pos, dY_neg, dZ_max, dU_pos, dV_min,
					             dX_neg, dY_neg, dZ_max, dU_neg, dV_min);
				}
				if (canConnectY_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_pos, dU_neg, dV_pos,
					             dX_neg, dY_min, dZ_pos, dU_neg, dV_max,
					             dX_pos, dY_min, dZ_pos, dU_pos, dV_max,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos);
				}
			}
			
			if (hasZnYp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_max, dZ_min, dU_min, dV_min,
				             dX_neg, dY_pos, dZ_min, dU_min, dV_neg,
				             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
				             dX_neg, dY_max, dZ_neg, dU_neg, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_max, dZ_neg, dU_neg, dV_min,
				             dX_pos, dY_pos, dZ_neg, dU_neg, dV_neg,
				             dX_pos, dY_pos, dZ_min, dU_min, dV_neg,
				             dX_pos, dY_max, dZ_min, dU_min, dV_min);
			} else {
				if (canConnectZ_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_pos, dZ_min, dU_pos, dV_max,
					             dX_neg, dY_pos, dZ_min, dU_neg, dV_max,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_pos,
					             dX_pos, dY_pos, dZ_neg, dU_pos, dV_pos);
				}
				if (canConnectY_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
					             dX_neg, dY_max, dZ_neg, dU_neg, dV_min,
					             dX_pos, dY_max, dZ_neg, dU_pos, dV_min,
					             dX_pos, dY_pos, dZ_neg, dU_pos, dV_neg);
				}
			}
			
			if (hasZpYp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_max, dZ_pos, dU_pos, dV_min,
				             dX_neg, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_neg, dY_pos, dZ_max, dU_max, dV_neg,
				             dX_neg, dY_max, dZ_max, dU_max, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_max, dZ_max, dU_max, dV_min,
				             dX_pos, dY_pos, dZ_max, dU_max, dV_neg,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_pos, dY_max, dZ_pos, dU_pos, dV_min);
			} else {
				if (canConnectZ_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_pos, dZ_max, dU_neg, dV_min,
					             dX_pos, dY_pos, dZ_max, dU_pos, dV_min,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg);
				}
				if (canConnectY_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_pos, dY_max, dZ_pos, dU_pos, dV_min,
					             dX_neg, dY_max, dZ_pos, dU_neg, dV_min,
					             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg);
				}
			}
		}
		
		{// z plane
			if (hasXnZn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_min, dY_neg, dZ_neg, dU_min, dV_pos,
				             dX_min, dY_neg, dZ_min, dU_min, dV_max,
				             dX_neg, dY_neg, dZ_min, dU_neg, dV_max,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_pos, dZ_neg, dU_neg, dV_pos,
				             dX_neg, dY_pos, dZ_min, dU_neg, dV_max,
				             dX_min, dY_pos, dZ_min, dU_min, dV_max,
				             dX_min, dY_pos, dZ_neg, dU_min, dV_pos);
			} else {
				if (canConnectX_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_min, dY_neg, dZ_neg, dU_min, dV_pos,
					             dX_min, dY_pos, dZ_neg, dU_min, dV_neg,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				}
				if (canConnectZ_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
					             dX_neg, dY_pos, dZ_min, dU_min, dV_neg,
					             dX_neg, dY_neg, dZ_min, dU_min, dV_pos,
					             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos);
				}
			}
			
			if (hasXpZn) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos,
				             dX_pos, dY_neg, dZ_min, dU_pos, dV_max,
				             dX_max, dY_neg, dZ_min, dU_max, dV_max,
				             dX_max, dY_neg, dZ_neg, dU_max, dV_pos);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_max, dY_pos, dZ_neg, dU_max, dV_pos,
				             dX_max, dY_pos, dZ_min, dU_max, dV_max,
				             dX_pos, dY_pos, dZ_min, dU_pos, dV_max,
				             dX_pos, dY_pos, dZ_neg, dU_pos, dV_pos);
			} else {
				if (canConnectX_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos,
					             dX_pos, dY_pos, dZ_neg, dU_pos, dV_neg,
					             dX_max, dY_pos, dZ_neg, dU_max, dV_neg,
					             dX_max, dY_neg, dZ_neg, dU_max, dV_pos);
				}
				if (canConnectZ_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_neg, dZ_neg, dU_neg, dV_pos,
					             dX_pos, dY_neg, dZ_min, dU_min, dV_pos,
					             dX_pos, dY_pos, dZ_min, dU_min, dV_neg,
					             dX_pos, dY_pos, dZ_neg, dU_neg, dV_neg);
				}
			}
			
			if (hasXnZp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_min, dY_neg, dZ_max, dU_min, dV_min,
				             dX_min, dY_neg, dZ_pos, dU_min, dV_neg,
				             dX_neg, dY_neg, dZ_pos, dU_neg, dV_neg,
				             dX_neg, dY_neg, dZ_max, dU_neg, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_pos, dZ_max, dU_neg, dV_min,
				             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg,
				             dX_min, dY_pos, dZ_pos, dU_min, dV_neg,
				             dX_min, dY_pos, dZ_max, dU_min, dV_min);
			} else {
				if (canConnectX_neg) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_min, dY_pos, dZ_pos, dU_min, dV_neg,
					             dX_min, dY_neg, dZ_pos, dU_min, dV_pos,
					             dX_neg, dY_neg, dZ_pos, dU_neg, dV_pos,
					             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg);
				}
				if (canConnectZ_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_neg, dY_neg, dZ_pos, dU_pos, dV_pos,
					             dX_neg, dY_neg, dZ_max, dU_max, dV_pos,
					             dX_neg, dY_pos, dZ_max, dU_max, dV_neg,
					             dX_neg, dY_pos, dZ_pos, dU_pos, dV_neg);
				}
			}
			
			if (hasXpZp) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_max, dU_pos, dV_min,
				             dX_pos, dY_neg, dZ_pos, dU_pos, dV_neg,
				             dX_max, dY_neg, dZ_pos, dU_max, dV_neg,
				             dX_max, dY_neg, dZ_max, dU_max, dV_min);
				
				addBakedQuad(quads, spriteOriginal, color,
				             dX_max, dY_pos, dZ_max, dU_max, dV_min,
				             dX_max, dY_pos, dZ_pos, dU_max, dV_neg,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_pos, dY_pos, dZ_max, dU_pos, dV_min);
			} else {
				if (canConnectX_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_max, dY_neg, dZ_pos, dU_max, dV_pos,
					             dX_max, dY_pos, dZ_pos, dU_max, dV_neg,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos);
				}
				if (canConnectZ_pos) {
					addBakedQuad(quads, spriteOriginal, color,
					             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
					             dX_pos, dY_pos, dZ_max, dU_max, dV_neg,
					             dX_pos, dY_neg, dZ_max, dU_max, dV_pos,
					             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos);
				}
			}
		}
		
		if (canConnectNone) {
			// x min
			addBakedQuad(quads, spriteOriginal, color,
			             dX_min, dY_max, dZ_neg, dU_neg, dV_min,
			             dX_min, dY_min, dZ_neg, dU_neg, dV_max,
			             dX_min, dY_min, dZ_pos, dU_pos, dV_max,
			             dX_min, dY_max, dZ_pos, dU_pos, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_min, dY_pos, dZ_max, dU_max, dV_neg,
			             dX_min, dY_pos, dZ_pos, dU_pos, dV_neg,
			             dX_min, dY_neg, dZ_pos, dU_pos, dV_pos,
			             dX_min, dY_neg, dZ_max, dU_max, dV_pos);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_min, dY_pos, dZ_neg, dU_neg, dV_neg,
			             dX_min, dY_pos, dZ_min, dU_min, dV_neg,
			             dX_min, dY_neg, dZ_min, dU_min, dV_pos,
			             dX_min, dY_neg, dZ_neg, dU_neg, dV_pos);
			
			// x max
			addBakedQuad(quads, spriteOriginal, color,
			             dX_max, dY_max, dZ_pos, dU_pos, dV_min,
			             dX_max, dY_min, dZ_pos, dU_pos, dV_max,
			             dX_max, dY_min, dZ_neg, dU_neg, dV_max,
			             dX_max, dY_max, dZ_neg, dU_neg, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_max, dY_neg, dZ_max, dU_max, dV_pos,
			             dX_max, dY_neg, dZ_pos, dU_pos, dV_pos,
			             dX_max, dY_pos, dZ_pos, dU_pos, dV_neg,
			             dX_max, dY_pos, dZ_max, dU_max, dV_neg);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_max, dY_neg, dZ_neg, dU_neg, dV_pos,
			             dX_max, dY_neg, dZ_min, dU_min, dV_pos,
			             dX_max, dY_pos, dZ_min, dU_min, dV_neg,
			             dX_max, dY_pos, dZ_neg, dU_neg, dV_neg);
			
			// z min
			addBakedQuad(quads, spriteOriginal, color,
			             dX_pos, dY_max, dZ_min, dU_pos, dV_min,
			             dX_pos, dY_min, dZ_min, dU_pos, dV_max,
			             dX_neg, dY_min, dZ_min, dU_neg, dV_max,
			             dX_neg, dY_max, dZ_min, dU_neg, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_neg, dY_pos, dZ_min, dU_neg, dV_neg,
			             dX_neg, dY_neg, dZ_min, dU_neg, dV_pos,
			             dX_min, dY_neg, dZ_min, dU_min, dV_pos,
			             dX_min, dY_pos, dZ_min, dU_min, dV_neg);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_max, dY_pos, dZ_min, dU_max, dV_neg,
			             dX_max, dY_neg, dZ_min, dU_max, dV_pos,
			             dX_pos, dY_neg, dZ_min, dU_pos, dV_pos,
			             dX_pos, dY_pos, dZ_min, dU_pos, dV_neg);
			
			// z max
			addBakedQuad(quads, spriteOriginal, color,
			             dX_neg, dY_max, dZ_max, dU_neg, dV_min,
			             dX_neg, dY_min, dZ_max, dU_neg, dV_max,
			             dX_pos, dY_min, dZ_max, dU_pos, dV_max,
			             dX_pos, dY_max, dZ_max, dU_pos, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_min, dY_pos, dZ_max, dU_min, dV_neg,
			             dX_min, dY_neg, dZ_max, dU_min, dV_pos,
			             dX_neg, dY_neg, dZ_max, dU_neg, dV_pos,
			             dX_neg, dY_pos, dZ_max, dU_neg, dV_neg);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_pos, dY_pos, dZ_max, dU_pos, dV_neg,
			             dX_pos, dY_neg, dZ_max, dU_pos, dV_pos,
			             dX_max, dY_neg, dZ_max, dU_max, dV_pos,
			             dX_max, dY_pos, dZ_max, dU_max, dV_neg);
			
			// y min
			addBakedQuad(quads, spriteOriginal, color,
			             dX_neg, dY_min, dZ_max, dU_neg, dV_min,
			             dX_neg, dY_min, dZ_min, dU_neg, dV_max,
			             dX_pos, dY_min, dZ_min, dU_pos, dV_max,
			             dX_pos, dY_min, dZ_max, dU_pos, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_min, dY_min, dZ_pos, dU_min, dV_neg,
			             dX_min, dY_min, dZ_neg, dU_min, dV_pos,
			             dX_neg, dY_min, dZ_neg, dU_neg, dV_pos,
			             dX_neg, dY_min, dZ_pos, dU_neg, dV_neg);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_pos, dY_min, dZ_pos, dU_pos, dV_neg,
			             dX_pos, dY_min, dZ_neg, dU_pos, dV_pos,
			             dX_max, dY_min, dZ_neg, dU_max, dV_pos,
			             dX_max, dY_min, dZ_pos, dU_max, dV_neg);
			
			// y max
			addBakedQuad(quads, spriteOriginal, color,
			             dX_pos, dY_max, dZ_max, dU_pos, dV_min,
			             dX_pos, dY_max, dZ_min, dU_pos, dV_max,
			             dX_neg, dY_max, dZ_min, dU_neg, dV_max,
			             dX_neg, dY_max, dZ_max, dU_neg, dV_min);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_neg, dY_max, dZ_pos, dU_neg, dV_neg,
			             dX_neg, dY_max, dZ_neg, dU_neg, dV_pos,
			             dX_min, dY_max, dZ_neg, dU_min, dV_pos,
			             dX_min, dY_max, dZ_pos, dU_min, dV_neg);
			
			addBakedQuad(quads, spriteOriginal, color,
			             dX_max, dY_max, dZ_pos, dU_max, dV_neg,
			             dX_max, dY_max, dZ_neg, dU_max, dV_pos,
			             dX_pos, dY_max, dZ_neg, dU_pos, dV_pos,
			             dX_pos, dY_max, dZ_pos, dU_pos, dV_neg);
		} else {
			
			// center cube
			if (!canConnectY_neg) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
				             dX_pos, dY_neg, dZ_neg, dU_neg, dV_neg,
				             dX_pos, dY_neg, dZ_pos, dU_pos, dV_neg,
				             dX_neg, dY_neg, dZ_pos, dU_pos, dV_pos);
			}
			if (!canConnectY_pos) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_pos, dZ_pos, dU_pos, dV_pos,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_pos, dY_pos, dZ_neg, dU_neg, dV_neg,
				             dX_neg, dY_pos, dZ_neg, dU_neg, dV_pos);
			}
			if (!canConnectZ_neg) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
				             dX_neg, dY_pos, dZ_neg, dU_neg, dV_neg,
				             dX_pos, dY_pos, dZ_neg, dU_pos, dV_neg,
				             dX_pos, dY_neg, dZ_neg, dU_pos, dV_pos);
			}
			if (!canConnectZ_pos) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_neg, dZ_pos, dU_pos, dV_pos,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_neg, dY_pos, dZ_pos, dU_neg, dV_neg,
				             dX_neg, dY_neg, dZ_pos, dU_neg, dV_pos);
			}
			if (!canConnectX_neg) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_neg, dY_neg, dZ_neg, dU_neg, dV_pos,
				             dX_neg, dY_neg, dZ_pos, dU_neg, dV_neg,
				             dX_neg, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_neg, dY_pos, dZ_neg, dU_pos, dV_pos);
			}
			if (!canConnectX_pos) {
				addBakedQuad(quads, spriteOriginal, color,
				             dX_pos, dY_pos, dZ_neg, dU_pos, dV_pos,
				             dX_pos, dY_pos, dZ_pos, dU_pos, dV_neg,
				             dX_pos, dY_neg, dZ_pos, dU_neg, dV_neg,
				             dX_pos, dY_neg, dZ_neg, dU_neg, dV_pos);
			}
		}
		
		return quads;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}
	
	@Override
	public boolean isGui3d() {
		return true;
	}
	
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
}