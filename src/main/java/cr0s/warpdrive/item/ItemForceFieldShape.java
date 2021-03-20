package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.force_field.BlockForceFieldProjector;
import cr0s.warpdrive.block.force_field.BlockForceFieldRelay;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.data.EnumTier;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemForceFieldShape extends ItemAbstractBase {	
	
	private static final ItemStack[] itemStackCache = new ItemStack[EnumForceFieldShape.length];
	
	private final EnumForceFieldShape forceFieldShape;
	
	public ItemForceFieldShape(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final EnumForceFieldShape forceFieldShape) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain),
		      registryName,
		      enumTier );
		
		this.forceFieldShape = forceFieldShape;
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final EnumForceFieldShape forceFieldShape) {
		final int indexShape = forceFieldShape.ordinal();
		if (itemStackCache[indexShape] == null) {
			itemStackCache[indexShape] = new ItemStack(WarpDrive.itemForceFieldShapes[indexShape], 1);
		}
		return itemStackCache[indexShape];
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumForceFieldShape forceFieldShape, final int amount) {
		return new ItemStack(WarpDrive.itemForceFieldShapes[forceFieldShape.ordinal()], amount);
	}
	
	@Nonnull
	public EnumForceFieldShape getShape() {
		return forceFieldShape;
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
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		Commons.addTooltip(list, "\n");
		
		Commons.addTooltip(list, new TranslationTextComponent("item.warpdrive.force_field.shape.tooltip.usage").getFormattedText());
	}
}