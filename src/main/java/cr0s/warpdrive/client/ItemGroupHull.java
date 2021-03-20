package cr0s.warpdrive.client;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroupHull extends ItemGroupAbstractBase {
	
	public ItemGroupHull(final String label) {
		super(label, 1618);
	}
	
	@Nonnull
	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		final int indexTier = 1 + random.nextInt(EnumTier.length - 1);
		final int indexColor = random.nextInt(16);
		ItemStack itemStack;
		switch (random.nextInt(7)) {
		case 0:
			itemStack = new ItemStack(WarpDrive.blockHulls_plain[indexTier][0][indexColor], 1);
			break;
		case 1:
			itemStack = new ItemStack(WarpDrive.blockHulls_plain[indexTier][1][indexColor], 1);
			break;
		case 2:
			itemStack = new ItemStack(WarpDrive.blockHulls_glass[indexTier][indexColor], 1);
			break;
		case 3:
			itemStack = new ItemStack(WarpDrive.blockHulls_slab[indexTier][0][indexColor], 1);
			itemStack.setDamage(0);
			break;
		case 4:
			itemStack = new ItemStack(WarpDrive.blockHulls_slab[indexTier][0][indexColor], 1);
			itemStack.setDamage(2);
			break;
		case 5:
			itemStack = new ItemStack(WarpDrive.blockHulls_stairs[indexTier][0][indexColor], 1);
			break;
		case 6:
			itemStack = new ItemStack(WarpDrive.blockHulls_omnipanel[indexTier][indexColor], 1);
			break;
		default:
			itemStack = new ItemStack(Blocks.OBSIDIAN, 1);
		}
		return itemStack;
    }
}