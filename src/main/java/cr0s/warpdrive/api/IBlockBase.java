package cr0s.warpdrive.api;

import cr0s.warpdrive.data.EnumTier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockBase {
	
	@Nonnull
	EnumTier getTier();
	
	@Nonnull
	Rarity getRarity();
	
    @Nullable
    BlockItem createItemBlock();
	
	@OnlyIn(Dist.CLIENT)
	default void modelInitialisation() {
    	// no operation
    }
}