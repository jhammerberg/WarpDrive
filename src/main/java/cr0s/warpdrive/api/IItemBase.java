package cr0s.warpdrive.api;

import cr0s.warpdrive.data.EnumTier;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public interface IItemBase {
	
	// wrapper for Forge ItemExpireEvent
	void onEntityExpireEvent(final ItemEntity entityItem, final ItemStack itemStack);
	
	@Nonnull
	EnumTier getTier(final ItemStack itemStack);
	
	// getRarity is defined in Item
	
	@OnlyIn(Dist.CLIENT)
	void modelInitialisation();
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	ModelResourceLocation getModelResourceLocation(final ItemStack itemStack);
}