package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumDecorativeType;

import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

public class BlockDecorative extends BlockAbstractBase {
	
	public static final EnumProperty<EnumDecorativeType> TYPE = EnumProperty.create("type", EnumDecorativeType.class);
	private static ItemStack[] itemStackCache;
	
	public BlockDecorative(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
		      .hardnessAndResistance(1.5F),
		      registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(TYPE, EnumDecorativeType.PLAIN)
		               );
		itemStackCache = new ItemStack[EnumDecorativeType.values().length];
	}
	
	@Nonnull
	public static ItemStack getItemStack(final EnumDecorativeType enumDecorativeType) {
		if (enumDecorativeType != null) {
			final int indexType = enumDecorativeType.ordinal();
			if (itemStackCache[indexType] == null) {
				itemStackCache[indexType] = new ItemStack(WarpDrive.blockDecoratives[indexType], 1);
			}
			return itemStackCache[indexType];
		}
		return ItemStack.EMPTY;
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumDecorativeType enumDecorativeType, final int amount) {
		return new ItemStack(WarpDrive.blockDecoratives[enumDecorativeType.ordinal()], amount);
	}
}
