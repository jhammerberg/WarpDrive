package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumDecorativeType;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraftforge.registries.GameData;

public class BlockDecorative extends BlockAbstractBase {
	
	private static final ItemStack[] itemStackCache = new ItemStack[EnumDecorativeType.values().length];
	
	public BlockDecorative(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
		      .hardnessAndResistance(1.5F),
		      registryName, enumTier);
	}
	
	@Nonnull
	public static Item getItem(@Nonnull final EnumDecorativeType enumDecorativeType) {
		final int indexType = enumDecorativeType.ordinal();
		return GameData.getBlockItemMap().get(WarpDrive.blockDecoratives[indexType]);
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final EnumDecorativeType enumDecorativeType) {
		final int indexType = enumDecorativeType.ordinal();
		if (itemStackCache[indexType] == null) {
			itemStackCache[indexType] = new ItemStack(WarpDrive.blockDecoratives[indexType], 1);
		}
		return itemStackCache[indexType];
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumDecorativeType enumDecorativeType, final int amount) {
		return new ItemStack(WarpDrive.blockDecoratives[enumDecorativeType.ordinal()], amount);
	}
}