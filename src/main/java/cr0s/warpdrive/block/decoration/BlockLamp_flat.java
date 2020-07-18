package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockLamp_flat extends BlockAbstractLamp {
	
	private static final VoxelShape SHAPE_DOWN  = makeCuboidShape(0.00D, 0.84D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_UP    = makeCuboidShape(0.00D, 0.00D, 0.00D, 1.00D, 0.16D, 1.00D);
	private static final VoxelShape SHAPE_NORTH = makeCuboidShape(0.00D, 0.00D, 0.84D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_SOUTH = makeCuboidShape(0.00D, 0.00D, 0.00D, 1.00D, 1.00D, 0.16D);
	private static final VoxelShape SHAPE_WEST  = makeCuboidShape(0.84D, 0.00D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_EAST  = makeCuboidShape(0.00D, 0.00D, 0.00D, 0.16D, 1.00D, 1.00D);
	
	public BlockLamp_flat(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier);
	}
	
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull ISelectionContext selectionContext) {
		switch (blockState.get(BlockProperties.FACING)) {
			case DOWN : return SHAPE_DOWN ;
			case UP   : return SHAPE_UP   ;
			case NORTH: return SHAPE_NORTH;
			case SOUTH: return SHAPE_SOUTH;
			case WEST : return SHAPE_WEST ;
			case EAST : return SHAPE_EAST ;
			default   : return SHAPE_UP;
		}
	}
}