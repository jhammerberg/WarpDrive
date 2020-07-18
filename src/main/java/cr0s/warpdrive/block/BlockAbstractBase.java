package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.TileEntityAbstractBase.UpgradeSlot;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.render.ClientCameraHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class BlockAbstractBase extends Block implements IBlockBase {
	
	protected final EnumTier enumTier;
	protected boolean ignoreFacingOnPlacement = false;
	
	@Nonnull
	protected static Block.Properties getDefaultProperties(@Nonnull final Material material, @Nonnull final MaterialColor materialColor) {
		return Block.Properties.create(material, materialColor)
		                       .hardnessAndResistance(5.0F, 6.0F)
		                       .sound(SoundType.METAL);
	}
	
	@Nonnull
	protected static Block.Properties getDefaultProperties(@Nullable final Material material) {
		final Material materialToUse = material == null ? Material.IRON : material;
		return getDefaultProperties(materialToUse, materialToUse.getColor());
	}
	
	protected BlockAbstractBase(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties);
		
		this.enumTier = enumTier;
		
		setRegistryName(registryName);
		WarpDrive.register(this);
	}
	
	protected BlockAbstractBase(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null));
		
		this.enumTier = enumTier;
		
		setRegistryName(registryName);
		WarpDrive.register(this);
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockAbstractBase(this);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		// no operation
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(@Nonnull final BlockItemUseContext blockItemUseContext) {
		final BlockState blockState = super.getStateForPlacement(blockItemUseContext);
		if (blockState == null) {
			return null;
		}
		
		final boolean isRotating = !ignoreFacingOnPlacement
		                        && blockState.getProperties().contains(BlockProperties.FACING);
		if (isRotating) {
			if (blockState.isOpaqueCube(blockItemUseContext.getWorld(), blockItemUseContext.getPos())) {
				final Direction enumFacing = Commons.getFacingFromEntity(blockItemUseContext.getPlayer());
				return blockState.with(BlockProperties.FACING, enumFacing);
			} else {
				return blockState.with(BlockProperties.FACING, blockItemUseContext.getFace());
			}
		}
		return blockState;
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, @Nonnull final IWorld world, @Nonnull final BlockPos blockPos, @Nonnull final Rotation axis) {
		// already handled by vanilla
		return super.rotate(blockState, world, blockPos, axis);
	}
	
	@Nonnull
	@Override
	public EnumTier getTier() {
		return enumTier;
	}
	
	@Nonnull
	@Override
	public Rarity getRarity() {
		return getTier().getRarity();
	}
	
	public static ActionResultType onCommonBlockActivated(
			@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
			@Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
			@Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if (enumHand != Hand.MAIN_HAND) {
			return ActionResultType.PASS;
		}
		if ( world.isRemote()
		  && ClientCameraHandler.isOverlayEnabled ) {
			return ActionResultType.PASS;
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityAbstractBase)) {
			return ActionResultType.PASS;
		}
		final TileEntityAbstractBase tileEntityAbstractBase = (TileEntityAbstractBase) tileEntity;
		final boolean hasVideoChannel = tileEntity instanceof IVideoChannel;
		
		// video channel is reported client side, everything else is reported server side
		// we still need to process the event server side for machines that are not full blocks, so in that case we return true
		if ( world.isRemote()
		  && !hasVideoChannel ) {
			return tileEntityAbstractBase instanceof TileEntityAbstractMachine
			    && itemStackHeld.getItem() == Items.REDSTONE_TORCH ? ActionResultType.CONSUME : ActionResultType.PASS;
		}
		
		UpgradeSlot upgradeSlot = tileEntityAbstractBase.getUpgradeSlot(itemStackHeld);
		
		// sneaking with an empty hand or an upgrade item in hand to dismount current upgrade
		if ( !world.isRemote()
		  && entityPlayer.isSneaking() ) {
			// using an upgrade item or an empty hand means dismount upgrade
			if ( tileEntityAbstractBase.isUpgradeable()
			  && ( itemStackHeld.isEmpty()
			    || upgradeSlot != null ) ) {
				// find a valid upgrade to dismount
				if ( upgradeSlot == null
				  || !tileEntityAbstractBase.hasUpgrade(upgradeSlot) ) {
					upgradeSlot = tileEntityAbstractBase.getFirstUpgradeOfType(itemStackHeld.isEmpty() ? Item.class : itemStackHeld.getItem().getClass(), null);
				}
				
				if (upgradeSlot == null) {
					// no more upgrades to dismount
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.no_upgrade_to_dismount"));
					return ActionResultType.CONSUME;
				}
				
				if (!entityPlayer.isCreative()) {
					// dismount the current upgrade item
					final ItemStack itemStackDrop = new ItemStack(upgradeSlot.itemStack.getItem(), upgradeSlot.itemStack.getCount());
					final ItemEntity entityItem = new ItemEntity(world, entityPlayer.getPosX(), entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ(), itemStackDrop);
					entityItem.setNoPickupDelay();
					final boolean isSuccess = world.addEntity(entityItem);
					if (!isSuccess) {
						Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.spawn_denied",
						                                                       entityItem ));
						return ActionResultType.CONSUME;
					}
				}
				
				tileEntityAbstractBase.dismountUpgrade(upgradeSlot);
				// upgrade dismounted
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.dismounted",
				                                                       new TranslationTextComponent(upgradeSlot.itemStack.getTranslationKey() + ".name") ));
				return ActionResultType.CONSUME;
			}
			
		} else if ( !entityPlayer.isSneaking()
		         && itemStackHeld.isEmpty() ) {// no sneaking and no item in hand => show status
			Commons.addChatMessage(entityPlayer, tileEntityAbstractBase.getStatus());
			return ActionResultType.CONSUME;
			
		} else if ( !world.isRemote()
		         && tileEntityAbstractBase.isUpgradeable()
		         && upgradeSlot != null ) {// no sneaking and an upgrade in hand => mounting an upgrade
			// validate quantity already installed
			if (tileEntityAbstractBase.getUpgradeMaxCount(upgradeSlot) < tileEntityAbstractBase.getUpgradeCount(upgradeSlot) + 1) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(),"warpdrive.upgrade.result.too_many_upgrades",
				                                                       tileEntityAbstractBase.getUpgradeMaxCount(upgradeSlot) ));
				return ActionResultType.CONSUME;
			}
			// validate dependency
			if (!tileEntityAbstractBase.canMountUpgrade(upgradeSlot)) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(),"warpdrive.upgrade.result.invalid_upgrade"));
				return ActionResultType.CONSUME;
			}
			
			if (!entityPlayer.isCreative()) {
				// validate quantity
				final int countRequired = upgradeSlot.itemStack.getCount();
				if (itemStackHeld.getCount() < countRequired) {
					// not enough upgrade items
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.upgrade.result.not_enough_upgrades"));
					return ActionResultType.CONSUME;
				}
				
				// update player inventory
				itemStackHeld.shrink(countRequired);
			}
			
			// mount the new upgrade item
			tileEntityAbstractBase.mountUpgrade(upgradeSlot);
			// upgrade mounted
			Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.upgrade.result.mounted",
			                                                       new TranslationTextComponent(upgradeSlot.itemStack.getTranslationKey() + ".name") ));
			return ActionResultType.CONSUME;
			
		} else if ( !world.isRemote()
		         && tileEntityAbstractBase instanceof TileEntityAbstractMachine
		         && itemStackHeld.getItem() == Items.REDSTONE_TORCH ) {// redstone torch on a machine to toggle it on/off
			final TileEntityAbstractMachine tileEntityAbstractMachine = (TileEntityAbstractMachine) tileEntityAbstractBase;
			final boolean isEnabledOld = tileEntityAbstractMachine.getIsEnabled();
			tileEntityAbstractMachine.setIsEnabled(!isEnabledOld);
			final boolean isEnabledNew = tileEntityAbstractMachine.getIsEnabled();
			if (isEnabledOld != isEnabledNew) {
				if (isEnabledNew) {
					Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(blockState)
					                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.set.enabled")));
				} else {
					Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(blockState)
					                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.set.disabled")));
				}
			} else {
				if (isEnabledNew) {
					Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(blockState)
					                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.get.enabled")));
				} else {
					Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(blockState)
					                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.get.disabled")));
				}
			}
			return ActionResultType.CONSUME;
			
		} else if ( !world.isRemote()
		         && itemStackHeld.getItem() == Items.DIAMOND
		         && entityPlayer.isCreative() ) {// diamond on an block to set debug values
			tileEntityAbstractBase.setDebugValues();
			Commons.addChatMessage(entityPlayer, tileEntityAbstractBase.getStatus());
			return ActionResultType.CONSUME;
		}
		
		return ActionResultType.PASS;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		final ActionResultType result = BlockAbstractBase.onCommonBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		if (result == ActionResultType.CONSUME) {
			return result;
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}
