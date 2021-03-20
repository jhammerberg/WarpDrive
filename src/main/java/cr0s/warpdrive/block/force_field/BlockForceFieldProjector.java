package cr0s.warpdrive.block.force_field;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.TileEntityAbstractBase.UpgradeSlot;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.item.ItemForceFieldShape;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockForceFieldProjector extends BlockAbstractForceField {
	
	public static final EnumProperty<EnumForceFieldShape> SHAPE = EnumProperty.create("shape", EnumForceFieldShape.class);
	
	private static final VoxelShape SHAPE_DOWN  = VoxelShapes.create(0.00D, 0.27D, 0.00D, 1.00D, 0.73D, 1.00D);
	private static final VoxelShape SHAPE_UP    = VoxelShapes.create(0.00D, 0.27D, 0.00D, 1.00D, 0.73D, 1.00D);
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(0.00D, 0.00D, 0.27D, 1.00D, 1.00D, 0.73D);
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(0.00D, 0.00D, 0.27D, 1.00D, 1.00D, 0.73D);
	private static final VoxelShape SHAPE_WEST  = VoxelShapes.create(0.27D, 0.00D, 0.00D, 0.73D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_EAST  = VoxelShapes.create(0.27D, 0.00D, 0.00D, 0.73D, 1.00D, 1.00D);
	
	public final boolean isDoubleSided;
	
	public BlockForceFieldProjector(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, final boolean isDoubleSided) {
		super(getDefaultProperties(Material.IRON)
				      .hardnessAndResistance(WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()], WarpDriveConfig.HULL_BLAST_RESISTANCE[enumTier.getIndex()]),
		      registryName, enumTier);
		
		this.isDoubleSided = isDoubleSided;
		setDefaultState(getStateContainer().getBaseState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.FACING, Direction.DOWN)
				                .with(SHAPE, EnumForceFieldShape.NONE)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockProperties.ACTIVE);
		builder.add(BlockProperties.FACING);
		builder.add(SHAPE);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                           @Nonnull final ISelectionContext selectionContext) {
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
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if (world.isRemote()) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		if (enumHand != Hand.MAIN_HAND) {
			return ActionResultType.PASS;
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityForceFieldProjector)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector) tileEntity;
		final Direction enumFacing = blockRaytraceResult.getFace();
		
		final UpgradeSlot upgradeSlot = tileEntityForceFieldProjector.getUpgradeSlot(itemStackHeld);
		
		// sneaking with an empty hand or an upgrade/shape item in hand to dismount current upgrade/shape
		if (entityPlayer.isSneaking()) {
			// using an upgrade item or no shape defined means dismount upgrade, otherwise dismount shape
			if ( upgradeSlot != null
			  || (tileEntityForceFieldProjector.getShape() == EnumForceFieldShape.NONE)
			  || ( enumFacing != blockState.get(BlockProperties.FACING)
			    && ( !tileEntityForceFieldProjector.isDoubleSided
			      || enumFacing.getOpposite() != blockState.get(BlockProperties.FACING) ) ) ) {
				// user base handler
				return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
				
			} else {// default to dismount shape
				if (tileEntityForceFieldProjector.getShape() != EnumForceFieldShape.NONE) {
					if ( enumFacing == blockState.get(BlockProperties.FACING)
					  || ( tileEntityForceFieldProjector.isDoubleSided
					    && enumFacing.getOpposite() == blockState.get(BlockProperties.FACING) ) ) {
						if (!entityPlayer.isCreative()) {
							// dismount the shape item(s)
							final ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityForceFieldProjector.getShape(), tileEntityForceFieldProjector.isDoubleSided ? 2 : 1);
							final ItemEntity entityItem = new ItemEntity(world, entityPlayer.getPosX(), entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ(), itemStackDrop);
							entityItem.setNoPickupDelay();
							final boolean isSuccess = world.addEntity(entityItem);
							if (!isSuccess) {
								Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.spawn_denied",
								                                                       entityItem ));
								return ActionResultType.CONSUME;
							}
						}
						
						tileEntityForceFieldProjector.setShape(EnumForceFieldShape.NONE);
						// shape dismounted
						Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.shape_dismounted"));
						
					} else {
						// wrong side
						Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.wrong_shape_side"));
					}
					
				} else {
					// no shape to dismount
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.no_shape_to_dismount"));
				}
				return ActionResultType.CONSUME;
			}
			
		} else if (itemStackHeld.isEmpty()) {// no sneaking and no item in hand => show status
			Commons.addChatMessage(entityPlayer, tileEntityForceFieldProjector.getStatus());
			return ActionResultType.CONSUME;
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldShape) {// no sneaking and shape in hand => mounting a shape
			if ( enumFacing == blockState.get(BlockProperties.FACING)
			  || ( tileEntityForceFieldProjector.isDoubleSided
			    && enumFacing.getOpposite() == blockState.get(BlockProperties.FACING) ) ) {
				if (!entityPlayer.isCreative()) {
					// validate quantity
					if (itemStackHeld.getCount() < (tileEntityForceFieldProjector.isDoubleSided ? 2 : 1)) {
						// not enough shape items
						Commons.addChatMessage(entityPlayer, new TranslationTextComponent(
							tileEntityForceFieldProjector.isDoubleSided ?
								"warpdrive.upgrade.result.not_enough_shapes.double" : "warpdrive.upgrade.result.not_enough_shapes.single"));
						return ActionResultType.CONSUME;
					}
					
					// update player inventory
					itemStackHeld.shrink( tileEntityForceFieldProjector.isDoubleSided ? 2 : 1 );
					
					// dismount the current shape item(s)
					if (tileEntityForceFieldProjector.getShape() != EnumForceFieldShape.NONE) {
						final ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityForceFieldProjector.getShape(), tileEntityForceFieldProjector.isDoubleSided ? 2 : 1);
						final ItemEntity entityItem = new ItemEntity(world, entityPlayer.getPosX(), entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ(), itemStackDrop);
						entityItem.setNoPickupDelay();
						final boolean isSuccess = world.addEntity(entityItem);
						if (!isSuccess) {
							Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.spawn_denied",
							                                                       entityItem ));
							return ActionResultType.CONSUME;
						}
					}
				}
				
				// mount the new shape item(s)
				tileEntityForceFieldProjector.setShape(((ItemForceFieldShape) itemStackHeld.getItem()).getShape());
				// shape mounted
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.shape_mounted"));
				
			} else {
				// wrong side
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.wrong_shape_side"));
			}
			return ActionResultType.CONSUME;
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldUpgrade) {// no sneaking and an upgrade in hand => mounting an upgrade
			// validate type
			if (upgradeSlot == null) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.invalid_upgrade_for_projector"));
				return ActionResultType.CONSUME;
			}
			
			// revert to common upgrade handler
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}