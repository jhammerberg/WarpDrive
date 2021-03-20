package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.ItemBlockAbstractBase;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemBlockHull extends ItemBlockAbstractBase {
	
	@Nonnull
	protected static Item.Properties getDefaultProperties() {
		return ItemBlockAbstractBase.getDefaultProperties()
				       .group(WarpDrive.itemGroupHull);
	}
	
	public <T extends Block & IBlockBase> ItemBlockHull(final T block) {
		super(block, getDefaultProperties());
	}
}