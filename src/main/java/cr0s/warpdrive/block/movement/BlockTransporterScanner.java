package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class BlockTransporterScanner extends BlockAbstractBase {
	
	protected static final VoxelShape SHAPE_HALF_DOWN = VoxelShapes.create(0.00D, 0.00D, 0.00D, 1.00D, 0.50D, 1.00D);
	
	public BlockTransporterScanner(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.ACTIVE);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                           @Nonnull final ISelectionContext selectionContext) {
		return SHAPE_HALF_DOWN;
	}
	
	// return null or empty collection if it's invalid
	public Collection<BlockPos> getValidContainment(final World world, final BlockPos blockPos) {
		final ArrayList<BlockPos> vContainments = new ArrayList<>(8);
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(blockPos);
		boolean isScannerPosition = true;
		for (int x = blockPos.getX() - 1; x <= blockPos.getX() + 1; x++)  {
			for (int z = blockPos.getZ() - 1; z <= blockPos.getZ() + 1; z++) {
				// check base block is containment or scanner in checker pattern
				mutableBlockPos.setPos(x, blockPos.getY(), z);
				final Block blockBase = world.getBlockState(mutableBlockPos).getBlock();
				if ( !(blockBase instanceof BlockTransporterContainment)
				  && (!isScannerPosition || !(blockBase instanceof BlockTransporterScanner)) ) {
					return null;
				}
				isScannerPosition = !isScannerPosition;
				
				// check 2 above blocks are air
				mutableBlockPos.move(Direction.UP);
				if (!world.isAirBlock(mutableBlockPos)) {
					return null;
				}
				mutableBlockPos.move(Direction.UP);
				if (!world.isAirBlock(mutableBlockPos)) {
					return null;
				}
				
				// save containment position
				if (blockBase instanceof BlockTransporterContainment) {
					vContainments.add(new BlockPos(x, blockPos.getY(), z));
				}
			}
		}
		return vContainments;
	}
}