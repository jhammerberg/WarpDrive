package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.ItemBlockAbstractBase;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlockHull extends ItemBlockAbstractBase {
	
	@Nonnull
	protected static Item.Properties getDefaultProperties() {
		return ItemBlockAbstractBase.getDefaultProperties()
				       .group(WarpDrive.itemGroupHull);
	}
	
	public <T extends Block & IBlockBase> ItemBlockHull(final T block) {
		super(block, getDefaultProperties());
	}
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getModelResourceLocation(final ItemStack itemStack) {
		if (getBlock() instanceof BlockHullStairs) {
			final ResourceLocation resourceLocation = getRegistryName();
			assert resourceLocation != null;
			final String variant = "facing=east,half=bottom,shape=straight";
			return new ModelResourceLocation(resourceLocation, variant);
		}
		return super.getModelResourceLocation(itemStack);
	}
}
