package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockForceFieldRelay extends BlockAbstractForceField {
	
	public static final EnumProperty<EnumForceFieldUpgrade> UPGRADE = EnumProperty.create("upgrade", EnumForceFieldUpgrade.class);
	
	private static final VoxelShape AABB_RELAY = makeCuboidShape(0.000D, 0.000D, 0.000D, 1.000D, 0.625D, 1.000D);
	
	public BlockForceFieldRelay(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(Material.IRON),
		      registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.FACING_HORIZONTAL, Direction.NORTH)
				                .with(UPGRADE, EnumForceFieldUpgrade.NONE)
		               );
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return AABB_RELAY;
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityForceFieldRelay();
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
		if (!(tileEntity instanceof TileEntityForceFieldRelay)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityForceFieldRelay tileEntityForceFieldRelay = (TileEntityForceFieldRelay) tileEntity;
		
		// sneaking with an empty hand or an upgrade item in hand to dismount current upgrade
		if (entityPlayer.isSneaking()) {
			final EnumForceFieldUpgrade enumForceFieldUpgrade = tileEntityForceFieldRelay.getUpgrade();
			if (enumForceFieldUpgrade != EnumForceFieldUpgrade.NONE) {
				if (!entityPlayer.isCreative()) {
					// dismount the upgrade item
					final ItemStack itemStackDrop = ItemForceFieldUpgrade.getItemStackNoCache(enumForceFieldUpgrade, 1);
					final ItemEntity entityItem = new ItemEntity(world, entityPlayer.getPosX(), entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ(), itemStackDrop);
					entityItem.setNoPickupDelay();
					final boolean isSuccess = world.addEntity(entityItem);
					if (!isSuccess) {
						Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.spawn_denied",
						                                                       entityItem ));
						return ActionResultType.CONSUME;
					}
				}
				
				tileEntityForceFieldRelay.setUpgrade(EnumForceFieldUpgrade.NONE);
				// upgrade dismounted
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.dismounted",
				                                                       new TranslationTextComponent(enumForceFieldUpgrade.name()) ));
				return ActionResultType.CONSUME;
				
			} else {
				// no more upgrades to dismount
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.no_upgrade_to_dismount"));
				return ActionResultType.CONSUME;
			}
			
		} else if (itemStackHeld.isEmpty()) {// no sneaking and no item in hand to show status
			Commons.addChatMessage(entityPlayer, tileEntityForceFieldRelay.getStatus());
			return ActionResultType.CONSUME;
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldUpgrade) {
			// validate type
			if (EnumForceFieldUpgrade.get(itemStackHeld.getDamage()).maxCountOnRelay <= 0) {
				// invalid upgrade type
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.invalid_upgrade_for_relay"));
				return ActionResultType.CONSUME;
			}
			
			if (!entityPlayer.isCreative()) {
				// validate quantity
				if (itemStackHeld.getCount() < 1) {
					// not enough upgrade items
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.not_enough_upgrades"));
					return ActionResultType.CONSUME;
				}
				
				// update player inventory
				itemStackHeld.shrink(1);
				
				// dismount the current upgrade item
				if (tileEntityForceFieldRelay.getUpgrade() != EnumForceFieldUpgrade.NONE) {
					final ItemStack itemStackDrop = ItemForceFieldUpgrade.getItemStackNoCache(tileEntityForceFieldRelay.getUpgrade(), 1);
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
			
			// mount the new upgrade item
			final EnumForceFieldUpgrade enumForceFieldUpgrade = EnumForceFieldUpgrade.get(itemStackHeld.getDamage());
			tileEntityForceFieldRelay.setUpgrade(enumForceFieldUpgrade);
			// upgrade mounted
			Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.mounted",
			                                                       new TranslationTextComponent(enumForceFieldUpgrade.name()) ));
			return ActionResultType.CONSUME;
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}
