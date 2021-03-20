package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;

public abstract class BlockAbstractAir extends BlockAbstractBase {
	
	public static final IntegerProperty CONCENTRATION = IntegerProperty.create("concentration", 0, 15);
	
	BlockAbstractAir(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(Block.Properties.create(Material.AIR)
		                      .hardnessAndResistance(0.0F)
		                      .doesNotBlockMovement()
		                      .noDrops(),
		      registryName, enumTier );
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean causesSuffocation(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
	
	@Override
	public boolean isAir(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return VoxelShapes.empty();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isReplaceable(@Nonnull final BlockState blockState, @Nonnull final BlockItemUseContext blockItemUseContext) {
		return true;
	}
	
	@Override
	public boolean canDropFromExplosion(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final Explosion explosion) {
		// TODO MC1.15 do we really need to say it despite the noDrops property?
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.DESTROY;
	}
	
	/* TODO MC1.15 breathing debug rendering
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldSideBeRendered(@Nonnull final BlockState blockState, @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos, @Nonnull final Direction facing) {
		if (WarpDriveConfig.BREATHING_AIR_BLOCK_DEBUG) {
			return facing == Direction.DOWN || facing == Direction.UP;
		}
		
		final BlockPos blockPosSide = blockPos.offset(facing);
		final Block blockSide = worldReader.getBlockState(blockPosSide).getBlock();
		if (blockSide instanceof BlockAbstractAir) {
			return false;
		}
		
		return blockSide == Blocks.AIR;
	}
	*/
}