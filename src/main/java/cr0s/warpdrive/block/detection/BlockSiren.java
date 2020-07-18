package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.BlockAbstractHorizontalSpinningContainer;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockSiren extends BlockAbstractHorizontalSpinningContainer {
	
	private static final VoxelShape SHAPE_INDUSTRIAL_DOWN_NORTH  = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_DOWN_SOUTH  = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_DOWN_WEST   = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	private static final VoxelShape SHAPE_INDUSTRIAL_DOWN_EAST   = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	private static final VoxelShape SHAPE_INDUSTRIAL_UP_NORTH    = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_UP_SOUTH    = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_UP_WEST     = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	private static final VoxelShape SHAPE_INDUSTRIAL_UP_EAST     = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	private static final VoxelShape SHAPE_INDUSTRIAL_NORTH       = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_SOUTH       = makeCuboidShape(0.0000D, 0.1875D, 0.1875D, 1.0000D, 0.8125D, 0.8125D);
	private static final VoxelShape SHAPE_INDUSTRIAL_WEST        = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	private static final VoxelShape SHAPE_INDUSTRIAL_EAST        = makeCuboidShape(0.1875D, 0.1875D, 0.0000D, 0.8125D, 0.8125D, 1.0000D);
	
	private static final VoxelShape SHAPE_MILITARY_DOWN    = makeCuboidShape(0.0000D, 0.3125D, 0.0000D, 1.0000D, 0.6875D, 1.0000D);
	private static final VoxelShape SHAPE_MILITARY_UP      = makeCuboidShape(0.0000D, 0.3125D, 0.0000D, 1.0000D, 0.6875D, 1.0000D);
	private static final VoxelShape SHAPE_MILITARY_NORTH   = makeCuboidShape(0.0000D, 0.3125D, 0.4375D, 1.0000D, 0.6875D, 0.8125D);
	private static final VoxelShape SHAPE_MILITARY_SOUTH   = makeCuboidShape(0.0000D, 0.3125D, 0.1875D, 1.0000D, 0.6875D, 0.5625D);
	private static final VoxelShape SHAPE_MILITARY_WEST    = makeCuboidShape(0.4375D, 0.3125D, 0.0000D, 0.8125D, 0.6875D, 1.0000D);
	private static final VoxelShape SHAPE_MILITARY_EAST    = makeCuboidShape(0.1875D, 0.3125D, 0.0000D, 0.5625D, 0.6875D, 1.0000D);
	private static final VoxelShape SHAPE_FULL             = VoxelShapes.fullCube();
	
	private final boolean isIndustrial;
	
	public BlockSiren(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, final boolean isIndustrial) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		this.isIndustrial = isIndustrial;
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntitySiren();
	}
	
	public boolean getIsIndustrial() {
		return isIndustrial;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		if (isIndustrial) {
			switch (blockState.get(BlockProperties.HORIZONTAL_SPINNING)) {
			case DOWN_NORTH : return SHAPE_INDUSTRIAL_DOWN_NORTH;
			case DOWN_SOUTH : return SHAPE_INDUSTRIAL_DOWN_SOUTH;
			case DOWN_WEST  : return SHAPE_INDUSTRIAL_DOWN_WEST;
			case DOWN_EAST  : return SHAPE_INDUSTRIAL_DOWN_EAST;
			case UP_NORTH   : return SHAPE_INDUSTRIAL_UP_NORTH;
			case UP_SOUTH   : return SHAPE_INDUSTRIAL_UP_SOUTH;
			case UP_WEST    : return SHAPE_INDUSTRIAL_UP_WEST;
			case UP_EAST    : return SHAPE_INDUSTRIAL_UP_EAST;
			case NORTH      : return SHAPE_INDUSTRIAL_NORTH;
			case SOUTH      : return SHAPE_INDUSTRIAL_SOUTH;
			case WEST       : return SHAPE_INDUSTRIAL_WEST;
			case EAST       : return SHAPE_INDUSTRIAL_EAST;
			default: return SHAPE_FULL;
			}
		} else {
			switch (blockState.get(BlockProperties.HORIZONTAL_SPINNING).facing) {
			case DOWN : return SHAPE_MILITARY_DOWN;
			case UP   : return SHAPE_MILITARY_UP;
			case NORTH: return SHAPE_MILITARY_NORTH;
			case SOUTH: return SHAPE_MILITARY_SOUTH;
			case WEST : return SHAPE_MILITARY_WEST;
			case EAST : return SHAPE_MILITARY_EAST;
			default: return SHAPE_FULL;
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final IBlockReader blockReader,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, blockReader, list, advancedItemTooltips);
		
		final int range = MathHelper.floor(WarpDriveConfig.SIREN_RANGE_BLOCKS_BY_TIER[enumTier.getIndex()]);
		final String unlocalizedName_withoutTier = getTranslationKey().replace("." + enumTier.getName(), "");
		Commons.addTooltip(list, new TranslationTextComponent(unlocalizedName_withoutTier + ".tooltip.usage",
		                                                      new WarpDriveText(Commons.getStyleValue(), range) ).getFormattedText());
	}
}
