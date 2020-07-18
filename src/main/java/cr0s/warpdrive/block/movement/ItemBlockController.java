package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.api.computer.ICoreSignature;
import cr0s.warpdrive.api.computer.IMultiBlockCoreOrController;
import cr0s.warpdrive.block.ItemBlockAbstractBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemBlockController extends ItemBlockAbstractBase {
	
	@Nonnull
	protected static Item.Properties getDefaultProperties() {
		return ItemBlockAbstractBase.getDefaultProperties()
		                            .maxStackSize(1);
	}
	
	public <T extends Block & IBlockBase> ItemBlockController(final T block) {
		this(block, getDefaultProperties());
	}
	
	public <T extends Block & IBlockBase> ItemBlockController(final T block, @Nonnull final Item.Properties itemProperties) {
		super(block, itemProperties);
	}
	
	@Nonnull
	protected static String getName(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemBlockController)) {
			return "";
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			return "";
		}
		final String name = tagCompound.getString(ICoreSignature.NAME_TAG);
		final UUID uuid = new UUID(tagCompound.getLong(ICoreSignature.UUID_MOST_TAG), tagCompound.getLong(ICoreSignature.UUID_LEAST_TAG));
		if (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L) {
			return "";
		}
		return name;
	}
	
	@Nullable
	protected static UUID getSignature(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemBlockController)) {
			return null;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			return null;
		}
		final UUID uuid = new UUID(tagCompound.getLong(ICoreSignature.UUID_MOST_TAG), tagCompound.getLong(ICoreSignature.UUID_LEAST_TAG));
		if (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L) {
			return null;
		}
		return uuid;
	}
	
	protected static ItemStack setNameAndSignature(@Nonnull final ItemStack itemStack, @Nullable final String name, @Nullable final UUID uuid) {
		if (!(itemStack.getItem() instanceof ItemBlockController)) {
			return itemStack;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		if ( name == null
		  || name.isEmpty() ) {
			tagCompound.remove(ICoreSignature.NAME_TAG);
		} else {
			tagCompound.putString(ICoreSignature.NAME_TAG, name);
		}
		if (uuid == null || (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L)) {
			tagCompound.remove(ICoreSignature.UUID_MOST_TAG);
			tagCompound.remove(ICoreSignature.UUID_LEAST_TAG);
		} else {
			tagCompound.putLong(ICoreSignature.UUID_MOST_TAG, uuid.getMostSignificantBits());
			tagCompound.putLong(ICoreSignature.UUID_LEAST_TAG, uuid.getLeastSignificantBits());
		}
		itemStack.setTag(tagCompound);
		return itemStack;
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
		final BlockState blockState = world.getBlockState(blockPos);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		
		if (!(tileEntity instanceof IMultiBlockCoreOrController)) {
			return super.onItemUse(context);
		}
		if (!entityPlayer.canPlayerEdit(blockPos, facing, itemStackHeld)) {
			return ActionResultType.FAIL;
		}
		
		final UUID uuidSignatureFromItem = getSignature(itemStackHeld);
		final String nameSignatureFromItem = getName(itemStackHeld);
		final UUID uuidSignatureFromBlock = ((IMultiBlockCoreOrController) tileEntity).getSignatureUUID();
		final String nameSignatureFromBlock = ((IMultiBlockCoreOrController) tileEntity).getSignatureName();
		final ITextComponent nameItem = itemStackHeld.getDisplayName();
		final String nameBlock = Commons.format(blockState, world, blockPos);
		if (entityPlayer.isSneaking()) {// get block signature
			if ( uuidSignatureFromBlock == null
			  || nameSignatureFromBlock == null
			  || nameSignatureFromBlock.isEmpty() ) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.core_signature.get_missing",
				                                                       null, nameItem, nameBlock ));
				
			} else if (uuidSignatureFromBlock.equals(uuidSignatureFromItem)) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.core_signature.get_same",
				                                                       nameSignatureFromItem, nameItem, nameBlock ));
				
			} else {
				setNameAndSignature(itemStackHeld, nameSignatureFromBlock, uuidSignatureFromBlock);
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.core_signature.get",
				                                                       nameSignatureFromBlock, nameItem, nameBlock ));
				world.playSound(entityPlayer.getPosX() + 0.5D, entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ() + 0.5D,
				                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS,
				                1.0F, 1.8F + 0.2F * world.rand.nextFloat(), false);
			}
			
		} else {// set block signature
			if (uuidSignatureFromItem == null) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.core_signature.set_missing",
				                                                       null, nameItem, nameBlock ));
				
			} else if (uuidSignatureFromItem.equals(uuidSignatureFromBlock)) {
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.core_signature.set_same",
				                                                       nameSignatureFromItem, nameItem, nameBlock ));
				
			} else {
				final boolean isSuccess = ((IMultiBlockCoreOrController) tileEntity).setSignature(uuidSignatureFromItem, nameSignatureFromItem);
				if (isSuccess) {
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.core_signature.set",
					                                                       nameSignatureFromItem, nameItem, nameBlock));
					world.playSound(entityPlayer.getPosX() + 0.5D, entityPlayer.getPosY() + 0.5D, entityPlayer.getPosZ() + 0.5D,
					                SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS,
					                1.0F, 1.2F + 0.2F * world.rand.nextFloat(), false);
				} else {
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.core_signature.set_not_supported",
					                                                       null, nameItem, nameBlock ));
				}
			}
		}
		
		return ActionResultType.SUCCESS;
	}
}
