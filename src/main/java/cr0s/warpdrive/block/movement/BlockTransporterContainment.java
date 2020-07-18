package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockTransporterContainment extends BlockAbstractBase {
	
	protected static final VoxelShape VS_HALF_DOWN   = VoxelShapes.create(0.00D, 0.00D, 0.00D, 1.00D, 0.50D, 1.00D);
	
	public BlockTransporterContainment(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null),
		      registryName, enumTier );
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return VS_HALF_DOWN;
	}
}