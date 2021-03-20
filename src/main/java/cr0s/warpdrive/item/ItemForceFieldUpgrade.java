package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IForceFieldUpgrade;
import cr0s.warpdrive.api.IForceFieldUpgradeEffector;
import cr0s.warpdrive.block.force_field.BlockForceFieldProjector;
import cr0s.warpdrive.block.force_field.BlockForceFieldRelay;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemForceFieldUpgrade extends ItemAbstractBase implements IForceFieldUpgrade {
	
	private static final ItemStack[] itemStackCache = new ItemStack[EnumForceFieldUpgrade.length];
	
	private final EnumForceFieldUpgrade forceFieldUpgrade;
	
	public ItemForceFieldUpgrade(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final EnumForceFieldUpgrade forceFieldUpgrade) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain),
		      registryName,
		      enumTier );
		
		this.forceFieldUpgrade = forceFieldUpgrade;
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final EnumForceFieldUpgrade forceFieldUpgrade) {
		final int indexUpgrade = forceFieldUpgrade.ordinal();
		if (itemStackCache[indexUpgrade] == null) {
			itemStackCache[indexUpgrade] = new ItemStack(WarpDrive.itemForceFieldUpgrades[indexUpgrade], 1);
		}
		return itemStackCache[indexUpgrade];
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumForceFieldUpgrade forceFieldUpgrade, final int amount) {
		return new ItemStack(WarpDrive.itemForceFieldUpgrades[forceFieldUpgrade.ordinal()], amount);
	}
	
	@Override
	public boolean doesSneakBypassUse(@Nonnull final ItemStack itemStack,
	                                  @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos,
	                                  @Nonnull final PlayerEntity player) {
		final Block block = worldReader.getBlockState(blockPos).getBlock();
		return block instanceof BlockForceFieldRelay
		    || block instanceof BlockForceFieldProjector
		    || super.doesSneakBypassUse(itemStack, worldReader, blockPos, player);
	}
	
	public EnumForceFieldUpgrade getUpgrade() {
		return forceFieldUpgrade;
	}
	
	@Override
	public IForceFieldUpgradeEffector getUpgradeEffector(final Object container) {
		if (container instanceof ItemStack) {
			return forceFieldUpgrade.getUpgradeEffector(container);
		}
		assert false;
		return null;
	}
	
	@Override
	public float getUpgradeValue(final Object container) {
		if (container instanceof ItemStack) {
			return forceFieldUpgrade.getUpgradeValue(container);
		}
		assert false;
		return 0;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		Commons.addTooltip(list, "\n");
		
		if (forceFieldUpgrade.maxCountOnProjector > 0) {
			Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.force_field.upgrade.tooltip.usage.projector").getFormattedText());
		}
		if (forceFieldUpgrade.maxCountOnRelay > 0) {
			Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.force_field.upgrade.tooltip.usage.relay").getFormattedText());
		}
		Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.force_field.upgrade.tooltip.usage.dismount").getFormattedText());
	}
}