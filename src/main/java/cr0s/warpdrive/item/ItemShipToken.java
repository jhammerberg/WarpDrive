package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumTier;

import java.util.List;
import java.util.Random;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemShipToken extends ItemAbstractBase {	
	
	private static ItemStack[] itemStackCache;
	public static final int[] TOKEN_IDs = { 0, 1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 35, 40, 41, 42, 43, 44, 45 };
	
	public ItemShipToken(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, final int tokenId) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain),
		      registryName,
		      enumTier );
		
		itemStackCache = new ItemStack[TOKEN_IDs.length];
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final Random random) {
		return getItemStack(TOKEN_IDs[random.nextInt(TOKEN_IDs.length)]);
	}
	
	@Nonnull
	public static ItemStack getItemStack(final int tokenId) {
		for (int index = 0; index < TOKEN_IDs.length; index++) {
			if (tokenId == TOKEN_IDs[index]) {
				if (itemStackCache[index] == null) {
					itemStackCache[index] = new ItemStack(WarpDrive.mapItemShipTokens.get(index));
				}
				return itemStackCache[index];
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(final int tokenId, final int amount) {
		for (int index = 0; index < TOKEN_IDs.length; index++) {
			if (tokenId == TOKEN_IDs[index]) {
				return new ItemStack(WarpDrive.mapItemShipTokens.get(index), amount);
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Nonnull
	public static String getSchematicName(@Nonnull final ItemStack itemStack) {
		String schematicName = "" + itemStack.getDamage();
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound != null && tagCompound.contains("shipName")) {
			schematicName = tagCompound.getString("shipName");
		}
		return schematicName;
	}
	
	public static void setSchematicName(@Nonnull final ItemStack itemStack, @Nonnull final String schematicName) {
		if (!itemStack.hasTag()) {
			itemStack.setTag(new CompoundNBT());
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		assert tagCompound != null;
		tagCompound.putString("shipName", schematicName);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.building.ship_token.tooltip.usage",
		                                                      getSchematicName(itemStack)).getFormattedText());
	}
}