package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IItemTransporterBeacon;
import cr0s.warpdrive.api.computer.ITransporterCore;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnergyWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlockTransporterBeacon extends ItemBlockController implements IItemTransporterBeacon {
	
	@Nonnull
	protected static Item.Properties getDefaultProperties() {
		return ItemBlockController.getDefaultProperties()
		                          .maxStackSize(1)
		                          .maxDamage(100 * 8);
	}
	
	public <T extends Block & IBlockBase> ItemBlockTransporterBeacon(final T block) {
		super(block, getDefaultProperties());
		
		addPropertyOverride(new ResourceLocation(WarpDrive.MODID, "active"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			@Override
			public float call(@Nonnull final ItemStack itemStack, @Nullable final World world, @Nullable final LivingEntity entity) {
				final boolean isActive = isActive(itemStack);
				return isActive ? 1.0F : 0.0F;
			}
		});
	}
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getModelResourceLocation(@Nonnull final ItemStack itemStack) {
		// suffix registry name to grab the item model so we can use overrides
		final ResourceLocation resourceLocation = getRegistryName();
		assert resourceLocation != null;
		return new ModelResourceLocation(resourceLocation.toString() + "-item", "inventory");
	}
	
	private static int getEnergy(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemBlockTransporterBeacon)) {
			return 0;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			return 0;
		}
		if (tagCompound.contains(EnergyWrapper.TAG_ENERGY)) {
			return tagCompound.getInt(EnergyWrapper.TAG_ENERGY);
		}
		return 0;
	}
	
	private static ItemStack setEnergy(@Nonnull final ItemStack itemStack, final int energy) {
		if (!(itemStack.getItem() instanceof ItemBlockTransporterBeacon)) {
			return itemStack;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		tagCompound.putInt(EnergyWrapper.TAG_ENERGY, energy);
		itemStack.setTag(tagCompound);
		return itemStack;
	}
	
	private static ItemStack updateDamage(@Nonnull final ItemStack itemStack, final int energy, final boolean isActive) {
		final int maxDamage = itemStack.getMaxDamage();
		final int metadataEnergy = maxDamage - maxDamage * energy / WarpDriveConfig.TRANSPORTER_BEACON_MAX_ENERGY_STORED;
		final int metadataNew = (metadataEnergy & ~0x3) + (isActive ? 2 : 0);
		if (metadataNew != itemStack.getDamage()) {
			itemStack.setDamage(metadataNew);
			return itemStack;
		} else {
			return null;
		}
	}
	
	// ITransporterBeacon overrides
	@Override
	public boolean isActive(@Nonnull final ItemStack itemStack) {
		return getEnergy(itemStack) > WarpDriveConfig.TRANSPORTER_BEACON_ENERGY_PER_TICK;
	}
	
	// Item overrides
	@Override
	public void inventoryTick(@Nonnull final ItemStack itemStack, @Nonnull final World world, @Nonnull final Entity entity,
	                          final int indexSlot, final boolean isHeld) {
		if (entity instanceof PlayerEntity) {
			final PlayerEntity entityPlayer = (PlayerEntity) entity;
			final ItemStack itemStackCheck = entityPlayer.inventory.getStackInSlot(indexSlot);
			if (itemStackCheck != itemStack) {
				WarpDrive.logger.error(String.format("Invalid item selection: possible dup tentative from %s",
				                                     entityPlayer));
				return;
			}
			
			// consume energy
			final int energy =  getEnergy(itemStack) - WarpDriveConfig.TRANSPORTER_BEACON_ENERGY_PER_TICK;
			if ( isHeld
			  && energy >= 0 ) {
				final ItemStack itemStackNew = setEnergy(itemStack, energy);
				updateDamage(itemStackNew, energy, true);
				((PlayerEntity) entity).inventory.setInventorySlotContents(indexSlot, itemStackNew);
				
			} else if (itemStack.getDamage() != 0) {// (still shows with energy but has none)
				final ItemStack itemStackNew = updateDamage(itemStack, energy, false);
				if (itemStackNew != null) {
					((PlayerEntity) entity).inventory.setInventorySlotContents(indexSlot, itemStackNew);
				}
			}
		}
		super.inventoryTick(itemStack, world, entity, indexSlot, isHeld);
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(@Nonnull final ItemUseContext context) {
		final PlayerEntity entityPlayer = context.getPlayer();
		final World world = context.getWorld();
		final BlockPos blockPos = context.getPos();
		final Hand hand = context.getHand();
		final Direction facing = context.getFace();
		
		if ( world.isRemote()
		  || entityPlayer == null ) {
			return ActionResultType.FAIL;
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(hand);
		if (itemStackHeld.isEmpty()) {
			return ActionResultType.FAIL;
		}
		
		// check if clicked block can be interacted with
		// final Block block = world.getBlock(x, y, z);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		
		if (!(tileEntity instanceof ITransporterCore)) {
			return super.onItemUse(context);
		}
		if (!entityPlayer.canPlayerEdit(blockPos, facing, itemStackHeld)) {
			return ActionResultType.FAIL;
		}
		
		final UUID uuidBeacon = getSignature(itemStackHeld);
		final String nameBeacon = getName(itemStackHeld);
		final UUID uuidTransporter = ((ITransporterCore) tileEntity).getSignatureUUID();
		if (entityPlayer.isSneaking()) {// update transporter signature
			final String nameTransporter = ((ITransporterCore) tileEntity).getSignatureName();
			
			if ( uuidTransporter == null
			  || nameTransporter == null
			  || nameTransporter.isEmpty() ) {
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.get_missing"));
				
			} else if (uuidTransporter.equals(uuidBeacon)) {
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.get_same",
				                                                                  nameTransporter));
				
			} else {
				final ItemStack itemStackNew = setNameAndSignature(itemStackHeld, nameTransporter, uuidTransporter);
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.get",
				                                                                  nameTransporter));
				world.playSound(entityPlayer.getPosX() + 0.5D, entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ() + 0.5D,
				                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS,
				                1.0F, 1.8F + 0.2F * world.rand.nextFloat(), false);
			}
			
		} else {// apply signature to transporter
			final Object[] remoteLocation = ((ITransporterCore) tileEntity).remoteLocation(new Object[] { });
			UUID uuidRemoteLocation;
			if ( remoteLocation == null
			  || remoteLocation.length != 1
			  || !(remoteLocation[0] instanceof String) ) {
				uuidRemoteLocation = null;
			} else {
				try {
					uuidRemoteLocation = UUID.fromString((String) remoteLocation[0]);
				} catch (final IllegalArgumentException exception) {// it's a player name
					uuidRemoteLocation = null;
				}
			}
			
			if (uuidBeacon == null) {
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.set_missing",
				                                                                  nameBeacon));
				
			} else if (uuidBeacon.equals(uuidTransporter)) {
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.set_self",
				                                                                  nameBeacon));
				
			} else if (uuidBeacon.equals(uuidRemoteLocation)) {
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.set_same",
				                                                                  nameBeacon));
				
			} else {
				((ITransporterCore) tileEntity).remoteLocation(new Object[] { uuidBeacon });
				Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.transporter_signature.set",
				                                                                  nameBeacon));
				world.playSound(entityPlayer.getPosX() + 0.5D, entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ() + 0.5D,
				                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS,
				                1.0F, 1.2F + 0.2F * world.rand.nextFloat(), false);
			}
		}
		
		return ActionResultType.SUCCESS;
	}
}
