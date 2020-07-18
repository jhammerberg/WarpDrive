package cr0s.warpdrive.data;

import cr0s.warpdrive.WarpDrive;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum WarpDriveArmorMaterial implements IArmorMaterial {
	RUBBER      (EnumTier.BASIC   , "rubber"      ,  6, new int[] { 1, 2, 3, 1 }, 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F,
	             () -> Ingredient.fromItems(WarpDrive.itemComponents[EnumComponentType.RUBBER.ordinal()]) ),
	CERAMIC     (EnumTier.ADVANCED, "ceramic"     , 18, new int[] { 2, 6, 5, 2 },  9, SoundEvents.ITEM_ARMOR_EQUIP_IRON   , 1.0F,
	             () -> Ingredient.fromItems(WarpDrive.itemComponents[EnumComponentType.CERAMIC.ordinal()]) ),
	CARBON_FIBER(EnumTier.SUPERIOR, "carbon_fiber", 40, new int[] { 3, 6, 8, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.5F,
	             () -> Ingredient.fromItems(WarpDrive.itemComponents[EnumComponentType.CARBON_FIBER.ordinal()]) );
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
	private final EnumTier enumTier;
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final LazyValue<Ingredient> repairMaterial;
	
	WarpDriveArmorMaterial(@Nonnull final EnumTier enumTier, @Nonnull final String name,
	                       final int maxDamageFactor, @Nonnull final int[] damageReductionAmounts, final int enchantability,
	                       @Nonnull final SoundEvent equipSound, final float toughness,
	                       @Nonnull final Supplier<Ingredient> repairMaterialSupplier) {
		this.enumTier = enumTier;
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmounts;
		this.enchantability = enchantability;
		this.soundEvent = equipSound;
		this.toughness = toughness;
		this.repairMaterial = new LazyValue<>(repairMaterialSupplier);
	}
	
	public EnumTier getTier() {
		return enumTier;
	}
	
	@Override
	public int getDurability(@Nonnull final EquipmentSlotType equipmentSlotType) {
		return MAX_DAMAGE_ARRAY[equipmentSlotType.getIndex()] * this.maxDamageFactor;
	}
	
	@Override
	public int getDamageReductionAmount(@Nonnull final EquipmentSlotType equipmentSlotType) {
		return this.damageReductionAmountArray[equipmentSlotType.getIndex()];
	}
	
	@Override
	public int getEnchantability() {
		return this.enchantability;
	}
	
	@Nonnull
	@Override
	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}
	
	@Nonnull
	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.getValue();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Nonnull
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public float getToughness() {
		return this.toughness;
	}
}