package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBreathingHelmet;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.client.ClientProxy;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemWarpArmor extends ArmorItem implements IItemBase, IBreathingHelmet {
	
	public static final String[] suffixes = {  "boots", "leggings", "chestplate", "helmet" };
	
	protected final EnumTier enumTier;
	protected String translationKey;
	
	public ItemWarpArmor(@Nonnull final String registryName, @Nonnull final EnumTier enumTier,
	                     @Nonnull final IArmorMaterial armorMaterial, @Nonnull final EquipmentSlotType entityEquipmentSlot) {
		super(armorMaterial,
		      entityEquipmentSlot,
		      new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1) );
		
		this.enumTier = enumTier;
		translationKey = "warpdrive.armor." + enumTier.getName() + "." + suffixes[entityEquipmentSlot.getIndex()];
		setRegistryName(registryName);
		WarpDrive.register(this);
	}
	
	@Nonnull
	@Override
	public String getArmorTexture(@Nonnull final ItemStack itemStack, final Entity entity, final EquipmentSlotType slot, @Nullable final String renderingType) {
		return "warpdrive:textures/armor/armor_" + (slot == EquipmentSlotType.LEGS ? 2 : 1) + ".png";
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
	public void onEntityExpireEvent(final ItemEntity entityItem, final ItemStack itemStack) {
	}
	
	@Override
	public boolean canBreath(final LivingEntity entityLivingBase) {
		return slot == EquipmentSlotType.HEAD;
	}
}