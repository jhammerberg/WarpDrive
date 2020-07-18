package cr0s.warpdrive.api;

import cr0s.warpdrive.data.EnumTier;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBlockBase {
	
	@Nonnull
	EnumTier getTier();
	
	@Nonnull
	Rarity getRarity();
	
    @Nullable
    BlockItem createItemBlock();
    
    void modelInitialisation();
}
