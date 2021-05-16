package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.hull.BlockHullSlab.EnumType;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public class ItemBlockHullSlab extends ItemBlockHull {
	
	private final Block blockSlab;
	
	public <T extends Block & IBlockBase> ItemBlockHullSlab(final T blockSlab) {
		super(blockSlab);
		
		this.blockSlab = blockSlab;
	}
	
	@Nonnull
	@Override
	public String getTranslationKey(@Nonnull final ItemStack itemstack) {
		return getTranslationKey();
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(@Nonnull final ItemUseContext context) {
		final PlayerEntity entityPlayer = context.getPlayer();
		final World world = context.getWorld();
		final BlockPos blockPos = context.getPos();
		final Hand hand = context.getHand();
		final Direction facing = context.getFace();
		
		if (entityPlayer == null) {
			return ActionResultType.FAIL;
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(hand);
		if (itemStackHeld.isEmpty()) {
			return ActionResultType.FAIL;
		}
		
		// check if clicked block can be interacted with
		// TODO MC1.15 HullSlab
		final BlockState blockStateItem = blockSlab.getDefaultState(); // getStateFromMeta(itemStackHeld.getDamage());
		final int metadataItem = itemStackHeld.getDamage();
		final EnumType variantItem = blockStateItem.get(BlockHullSlab.TYPE);
		
		final BlockState blockStateWorld = world.getBlockState(blockPos);
		final EnumType variantWorld = blockStateWorld.getBlock() == blockSlab ? blockStateWorld.get(BlockHullSlab.TYPE) : EnumType.FULL_X;
		
		if ( blockStateWorld.getBlock() == blockSlab
		  && !variantItem.getIsDouble()
		  && !variantWorld.getIsDouble() ) {
			if (!entityPlayer.canPlayerEdit(blockPos, facing, itemStackHeld)) {
				return ActionResultType.FAIL;
			}
			
			// try to merge slabs when right-clicking directly the inner face
			if (variantWorld.getFacing() == facing.getOpposite()) {
				final VoxelShape boundingBox = blockStateWorld.getCollisionShape(world, blockPos, ISelectionContext.dummy());
				if ( !boundingBox.isEmpty()
					// TODO MC1.15 HullSlab
					//  && world.checkNoEntityCollision(boundingBox)
				) {
					final EnumType variantNew;
					switch (facing) {
					default:
					case DOWN:
					case UP:
						variantNew = EnumType.FULL_Y;
						break;
					case NORTH:
					case SOUTH:
						variantNew = EnumType.FULL_Z;
						break;
					case WEST:
					case EAST:
						variantNew = EnumType.FULL_X;
						break;
					}
					world.setBlockState(blockPos, blockSlab.getDefaultState().with(BlockHullSlab.TYPE, variantNew), 3);
					
					final SoundType soundtype = blockSlab.getSoundType(blockStateWorld, world, blockPos, entityPlayer);
					world.playSound(entityPlayer, blockPos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
					                (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					itemStackHeld.shrink(1);
				}
				
				return ActionResultType.SUCCESS;
			}
			
		} else {
			// check is closer block can be interacted with
			final BlockPos blockPosSide = blockPos.offset(facing);
			final BlockState blockStateSide = world.getBlockState(blockPosSide);
			final EnumType variantSide = blockStateSide.getBlock() == blockSlab ? blockStateSide.get(BlockHullSlab.TYPE) : EnumType.FULL_X;
			
			if ( blockStateSide.getBlock() == blockSlab
			  && !variantItem.getIsDouble()
			  && !variantSide.getIsDouble() ) {
				if (!entityPlayer.canPlayerEdit(blockPosSide, facing, itemStackHeld)) {
					return ActionResultType.FAIL;
				}
				
				// try to place ignoring the existing block
				final BlockState blockStatePlaced = blockSlab.getStateForPlacement(new BlockItemUseContext(context));
				final Direction enumFacingPlaced = blockStatePlaced.get(BlockHullSlab.TYPE).getFacing().getOpposite();
				
				// try to merge slabs when right-clicking on a side block
				if (enumFacingPlaced == blockStateSide.get(BlockHullSlab.TYPE).getFacing()) {
					final VoxelShape boundingBox = blockStateWorld.getCollisionShape(world, blockPosSide, ISelectionContext.dummy());
					if ( !boundingBox.isEmpty()
					// TODO MC1.15 HullSlab
					//  && world.checkNoEntityCollision(boundingBox)
					    ) {
						final EnumType variantNew;
						switch (enumFacingPlaced) {
						default:
						case DOWN:
						case UP:
							variantNew = EnumType.FULL_Y;
							break;
						case NORTH:
						case SOUTH:
							variantNew = EnumType.FULL_Z;
							break;
						case WEST:
						case EAST:
							variantNew = EnumType.FULL_X;
							break;
						}
						world.setBlockState(blockPosSide, blockSlab.getDefaultState().with(BlockHullSlab.TYPE, variantNew), 3);
						
						final SoundType soundtype = blockSlab.getSoundType(blockStateWorld, world, blockPosSide, entityPlayer);
						world.playSound(entityPlayer, blockPosSide, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
						                (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
						itemStackHeld.shrink(1);
					}
					
					return ActionResultType.SUCCESS;
				}
				
			}
		}
		
		return super.onItemUse(context);
	}
	
	@Override
	protected boolean canPlace(@Nonnull final BlockItemUseContext context, @Nonnull final BlockState blockState) {
		final World world = context.getWorld();
		final BlockPos blockPos = context.getPos();
		final Direction facing = context.getFace();
		
		// check if clicked block can be interacted with
		final BlockState blockStateItem = blockState; // TODO MC1.15 Hull slabs
		final EnumType variantItem = blockStateItem.get(BlockHullSlab.TYPE);
		
		final BlockState blockStateWorld = world.getBlockState(blockPos);
		final EnumType variantWorld = blockStateWorld.getBlock() == blockSlab ? blockStateWorld.get(BlockHullSlab.TYPE) : EnumType.FULL_X;
		
		if ( blockStateWorld.getBlock() == blockSlab
		  && !variantItem.getIsDouble()
		  && !variantWorld.getIsDouble() ) {
			return true;
		}
		
		// check the block on our side
		final BlockPos blockPosSide = blockPos.offset(facing);
		final BlockState blockStateSide = world.getBlockState(blockPosSide);
		if (blockStateSide.getBlock() == blockSlab) {
			return true;
		}
		
		// default behavior
		return super.canPlace(context, blockState);
	}
}