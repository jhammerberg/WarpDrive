package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.client.ClientProxy;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemAbstractBase extends Item implements IItemBase {
	
	protected final EnumTier enumTier;
	protected String translationKey;
	
	public ItemAbstractBase(@Nonnull final Item.Properties itemProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(itemProperties
				      .rarity(enumTier.getRarity()) );
		
		this.enumTier = enumTier;
		setRegistryName(registryName);
		WarpDrive.register(this);
	}
	
	@Nonnull
	@Override
	public String getTranslationKey() {
		return translationKey == null ? super.getTranslationKey() : translationKey;
	}
	
	public void setTranslationKey(@Nonnull final String translationKey) {
		this.translationKey = translationKey;
	}
	
	@Override
	public void onEntityExpireEvent(final ItemEntity entityItem, final ItemStack itemStack) {
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		ClientProxy.modelInitialisation(this);
	}
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getModelResourceLocation(final ItemStack itemStack) {
		return ClientProxy.getModelResourceLocation(itemStack);
	}
	
	@Nonnull
	@Override
	public EnumTier getTier(final ItemStack itemStack) {
		return enumTier;
	}
	
	@Nonnull
	@Override
	public Rarity getRarity(@Nonnull final ItemStack itemStack) {
		return getTier(itemStack).getRarity();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		final String tooltipName1 = getTranslationKey(itemStack) + ".tooltip";
		if (I18n.hasKey(tooltipName1)) {
			Commons.addTooltip(list, new TranslationTextComponent(tooltipName1).getFormattedText());
		}
		
		final String tooltipName2 = getTranslationKey() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && I18n.hasKey(tooltipName2)) {
			Commons.addTooltip(list, new TranslationTextComponent(tooltipName2).getFormattedText());
		}
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
