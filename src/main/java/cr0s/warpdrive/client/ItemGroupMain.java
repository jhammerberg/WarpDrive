package cr0s.warpdrive.client;

import cr0s.warpdrive.item.ItemShipToken;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ItemGroupMain extends ItemGroupAbstractBase {
	
	public ItemGroupMain(final String label) {
		super(label, 2861);
	}
	
	@Nonnull
	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		return ItemShipToken.getItemStack(random);
    }
}