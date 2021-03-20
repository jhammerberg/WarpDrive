package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemBlockAbstractBase extends BlockItem implements IItemBase {
	
	@Nonnull
	protected static Item.Properties getDefaultProperties() {
		return new Item.Properties()
				.group(WarpDrive.itemGroupMain);
	}
	
	public <T extends Block & IBlockBase> ItemBlockAbstractBase(@Nonnull final T block, @Nonnull final Item.Properties itemProperties) {
		super(block, itemProperties
				             .rarity(block.getRarity()) );
	}
	
	public <T extends Block & IBlockBase> ItemBlockAbstractBase(@Nonnull final T block) {
		super(block, getDefaultProperties()
				             .rarity(block.getRarity()) );
	}
	
	@Nonnull
	@Override
	public EnumTier getTier(@Nonnull final ItemStack itemStack) {
		if ( !(getBlock() instanceof IBlockBase) ) {
			assert false;
			return EnumTier.BASIC;
		}
		return ((IBlockBase) getBlock()).getTier();
	}
	
	@Nonnull
	@Override
	public Rarity getRarity(@Nonnull final ItemStack itemStack) {
		final Rarity rarityDefault = super.getRarity(itemStack);
		if ( !(getBlock() instanceof IBlockBase) ) {
			return rarityDefault;
		}
		final Rarity rarityBlock = ((IBlockBase) getBlock()).getRarity();
		return rarityBlock.ordinal() > rarityDefault.ordinal() ? rarityBlock : rarityDefault;
	}
	
	public ITextComponent getStatus(final World world, @Nonnull final ItemStack itemStack) {
		final BlockState blockState = getBlock().getDefaultState();
		
		final TileEntity tileEntity = getBlock().createTileEntity(blockState, world);
		if (tileEntity instanceof TileEntityAbstractBase) {
			return ((TileEntityAbstractBase) tileEntity).getStatus(itemStack, blockState);
			
		} else {// (not a tile entity provider)
			return new StringTextComponent("");
		}
	}
	
	@Override
	public void onEntityExpireEvent(final ItemEntity entityItem, final ItemStack itemStack) {
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		final String tooltipItemStack = getTranslationKey(itemStack) + ".tooltip";
		if (I18n.hasKey(tooltipItemStack)) {
			Commons.addTooltip(list, new TranslationTextComponent(tooltipItemStack).getFormattedText());
		}
		
		final String tooltipName = getTranslationKey() + ".tooltip";
		if ((!tooltipItemStack.equals(tooltipName)) && I18n.hasKey(tooltipName)) {
			Commons.addTooltip(list, new TranslationTextComponent(tooltipName).getFormattedText());
		}
		
		String tooltipNameWithoutTier = tooltipName;
		for (final EnumTier enumTier : EnumTier.values()) {
			tooltipNameWithoutTier = tooltipNameWithoutTier.replace("." + enumTier.getName(), "");
		}
		if ((!tooltipNameWithoutTier.equals(tooltipItemStack)) && I18n.hasKey(tooltipNameWithoutTier)) {
			Commons.addTooltip(list, new TranslationTextComponent(tooltipNameWithoutTier).getFormattedText());
		}
		
		Commons.addTooltip(list, getStatus(world, itemStack).getFormattedText());
		
		super.addInformation(itemStack, world, list, advancedItemTooltips);
	}
	
	@Nonnull
	@Override
	public String toString() {
		return String.format("%s@%s {%s} %s",
		                     getClass().getSimpleName(),
		                     Integer.toHexString(hashCode()),
		                     ForgeRegistries.ITEMS.getKey(this),
		                     getTranslationKey());
	}
}