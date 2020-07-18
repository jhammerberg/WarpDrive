package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractHorizontalSpinningContainer;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEnvironmentalSensor extends BlockAbstractHorizontalSpinningContainer {
	
	private static final VoxelShape SHAPE_DOWN   = makeCuboidShape(0.1250D, 0.6875D, 0.1250D, 0.8750D, 1.0000D, 0.8750D);
	private static final VoxelShape SHAPE_UP     = makeCuboidShape(0.1250D, 0.0000D, 0.1250D, 0.8750D, 0.3125D, 0.8750D);
	private static final VoxelShape SHAPE_NORTH  = makeCuboidShape(0.1250D, 0.1250D, 0.6875D, 0.8750D, 0.8750D, 1.0000D);
	private static final VoxelShape SHAPE_SOUTH  = makeCuboidShape(0.1250D, 0.1250D, 0.0000D, 0.8750D, 0.8750D, 0.3125D);
	private static final VoxelShape SHAPE_WEST   = makeCuboidShape(0.6875D, 0.1250D, 0.1250D, 1.0000D, 0.8750D, 0.8750D);
	private static final VoxelShape SHAPE_EAST   = makeCuboidShape(0.0000D, 0.1250D, 0.1250D, 0.3125D, 0.8750D, 0.8750D);
	private static final VoxelShape SHAPE_FULL   = VoxelShapes.fullCube();
	
	public BlockEnvironmentalSensor(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityEnvironmentalSensor();
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		switch (blockState.get(BlockProperties.HORIZONTAL_SPINNING).facing) {
		case DOWN : return SHAPE_DOWN;
		case UP   : return SHAPE_UP;
		case NORTH: return SHAPE_NORTH;
		case SOUTH: return SHAPE_SOUTH;
		case WEST : return SHAPE_WEST;
		case EAST : return SHAPE_EAST;
		default: return SHAPE_FULL;
		}
	}
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}