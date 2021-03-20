package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirContainerItem;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumAirTankTier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemAirTank extends ItemAbstractBase implements IAirContainerItem {
	
	protected EnumAirTankTier enumAirTankTier;
	
	public ItemAirTank(@Nonnull final String registryName, @Nonnull final EnumAirTankTier enumAirTankTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(WarpDriveConfig.BREATHING_AIR_TANK_CAPACITY_BY_TIER[enumAirTankTier.getIndex()]),
		      registryName,
		      enumAirTankTier.getTier() );
		
		this.enumAirTankTier = enumAirTankTier;
	}
	
	@Override
	public boolean canContainAir(final ItemStack itemStack) {
		if ( itemStack == null
		  || itemStack.getItem() != this ) {
			return false;
		}
		return itemStack.getDamage() > 0;
	}
	
	@Override
	public int getMaxAirStorage(final ItemStack itemStack) {
		if ( itemStack == null
		  || itemStack.getItem() != this ) {
			return 0;
		}
		return itemStack.getMaxDamage();
	}
	
	@Override
	public int getCurrentAirStorage(final ItemStack itemStack) {
		if ( itemStack == null
		  || itemStack.getItem() != this ) {
			return 0;
		}
		return getMaxDamage(itemStack) - itemStack.getDamage();
	}
	
	@Override
	public ItemStack consumeAir(final ItemStack itemStack) {
		if ( itemStack == null
		  || itemStack.getItem() != this ) {
			return itemStack;
		}
		itemStack.setDamage(Math.min(getMaxDamage(itemStack), itemStack.getDamage() + 1)); // bypass unbreaking enchantment
		return itemStack;
	}
	
	@Override
	public int getAirTicksPerConsumption(final ItemStack itemStack) {
		if ( itemStack != null
		  && itemStack.getItem() != this ) {
			return 0;
		}
		return WarpDriveConfig.BREATHING_AIR_TANK_BREATH_DURATION_TICKS;
	}
	
	@Override
	public ItemStack getEmptyAirContainer(final ItemStack itemStack) {
		if ( itemStack != null
		  && itemStack.getItem() != this ) {
			return itemStack;
		}
		final ItemStack itemStackEmpty = new ItemStack(this, 1);
		itemStackEmpty.setDamage(getMaxDamage(itemStackEmpty));
		return itemStackEmpty;
	}
	
	@Override
	public ItemStack getFullAirContainer(final ItemStack itemStack) {
		if ( itemStack != null
		  && itemStack.getItem() != this ) {
			return itemStack;
		}
		return new ItemStack(this, 1);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.breathing.air_tank.tooltip.usage").getFormattedText());
	}
}