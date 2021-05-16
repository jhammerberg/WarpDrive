package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockAbstractLamp extends BlockAbstractBase {
	
	BlockAbstractLamp(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				      .doesNotBlockMovement()
				      .hardnessAndResistance(WarpDriveConfig.HULL_HARDNESS[1], WarpDriveConfig.HULL_BLAST_RESISTANCE[1])
				      .sound(SoundType.METAL),
		      registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.ACTIVE);
		builder.add(BlockProperties.FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(@Nonnull final BlockState blockState) {
		if (blockState.get(BlockProperties.ACTIVE)) {
			return 14;
		} else {
			return 0;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public BlockState rotate(@Nonnull final BlockState blockState, @Nonnull final Rotation rot) {
		return blockState.with(BlockProperties.FACING, rot.rotate(blockState.get(BlockProperties.FACING)));
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public BlockState mirror(@Nonnull final BlockState blockState, @Nonnull final Mirror mirror) {
		return blockState.rotate(mirror.toRotation(blockState.get(BlockProperties.FACING)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isValidPosition(@Nonnull final BlockState blockState, @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos) {
		final Direction direction = blockState.get(BlockProperties.FACING);
		return hasEnoughSolidSide(worldReader, blockPos.offset(direction.getOpposite()), direction);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public BlockState updatePostPlacement(@Nonnull final BlockState blockStateCurrent, @Nonnull final Direction facing,
	                                      @Nonnull final BlockState blockStateFacing, @Nonnull final IWorld world,
	                                      @Nonnull final BlockPos blockPosCurrent, @Nonnull final BlockPos blockPosFacing) {
		final Direction directionCurrent = blockStateCurrent.get(BlockProperties.FACING);
		if ( facing.getOpposite() == directionCurrent
		  && !blockStateCurrent.isValidPosition(world, blockPosCurrent) ) {
			// find a new attachment
			for (final Direction directionCheck : Direction.values()) {
				if (directionCheck == directionCurrent) {
					continue;
				}
				final BlockState blockStateCheck = blockStateCurrent.with(BlockProperties.FACING, directionCheck);
				if (blockStateCheck.isValidPosition(world, blockPosCurrent) ) {
					// new attachment found => apply
					return blockStateCheck;
				}
			}
			
			// can't find an attachment => drop
			return Blocks.AIR.getDefaultState();
		}
		return blockStateCurrent;
	}
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if (enumHand != Hand.MAIN_HAND) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		if (world.isRemote()) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		// non-sneaking to toggle lamp on/state
		if (!entityPlayer.isSneaking()) {
			final boolean isActivated = !blockState.get(BlockProperties.ACTIVE);
			world.setBlockState(blockPos, blockState.with(BlockProperties.ACTIVE, isActivated));
			// (visual feedback only, no message to player)
			return ActionResultType.CONSUME;
		}
		
		// (visual feedback only: no status reported while sneaking)
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final IBlockReader blockReader,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, blockReader, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.decoration.lamp.tooltip.usage").getFormattedText());
	}
}