package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockWarpIsolation extends BlockAbstractBase {
	
	public BlockWarpIsolation(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				      .hardnessAndResistance(3.5F), registryName, enumTier);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final IBlockReader blockReader,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, blockReader, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent(getTranslationKey() + ".tooltip.usage",
		                                                      WarpDriveConfig.RADAR_MIN_ISOLATION_BLOCKS,
		                                                      Math.round(WarpDriveConfig.RADAR_MIN_ISOLATION_EFFECT * 100.0D),
		                                                      WarpDriveConfig.RADAR_MAX_ISOLATION_BLOCKS,
		                                                      Math.round(WarpDriveConfig.RADAR_MAX_ISOLATION_EFFECT * 100.0D),
		                                                      WarpDriveConfig.RADAR_MAX_ISOLATION_RANGE + 1).getFormattedText());
	}
}