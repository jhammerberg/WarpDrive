package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IControlChannel;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.api.IWarpTool;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.energy.BlockCapacitor;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTuningFork extends ItemAbstractBase implements IWarpTool {
	
	public ItemTuningFork(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1),
		      registryName,
		      enumTier );
	}
	
	public static int getVideoChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningFork)) {
			return -1;
		}
		return (itemStack.getDamage() % 16) + 100;
	}
	
	public static int getBeamFrequency(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningFork)) {
			return -1;
		}
		return ((itemStack.getDamage() % 16) + 1) * 10;
	}
	
	public static int getControlChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningFork)) {
			return -1;
		}
		return ((itemStack.getDamage() % 16) + 2);
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(@Nonnull final ItemUseContext context) {
		final World world = context.getWorld();
		if (world.isRemote()) {
			return ActionResultType.FAIL;
		}
		// get context
		final PlayerEntity entityPlayer = context.getPlayer();
		if (entityPlayer == null) {
			return ActionResultType.FAIL;
		}
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(context.getHand());
		final TileEntity tileEntity = world.getTileEntity(context.getPos());
		if (tileEntity == null) {
			return ActionResultType.FAIL;
		}
		
		final boolean hasVideoChannel = tileEntity instanceof IVideoChannel;
		final boolean hasBeamFrequency = tileEntity instanceof IBeamFrequency;
		final boolean hasControlChannel = tileEntity instanceof IControlChannel;
		if (!hasVideoChannel && !hasBeamFrequency && !hasControlChannel) {
			return ActionResultType.FAIL;
		}
		if (hasVideoChannel && !(entityPlayer.isSneaking() && hasBeamFrequency)) {
			((IVideoChannel)tileEntity).setVideoChannel(getVideoChannel(itemStackHeld));
			Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.video_channel.set",
			                                                                  tileEntity.getBlockState().getBlock().getNameTextComponent(),
			                                                                  getVideoChannel(itemStackHeld)));
			world.playSound(entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
			
		} else if (hasControlChannel && !(entityPlayer.isSneaking() && hasBeamFrequency)) {
			((IControlChannel)tileEntity).setControlChannel(getControlChannel(itemStackHeld));
			Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.control_channel.set",
			                                                                  tileEntity.getBlockState().getBlock().getNameTextComponent(),
			                                                                  getControlChannel(itemStackHeld)));
			world.playSound(entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
			
		} else {
			// assert hasBeamFrequency;
			((IBeamFrequency)tileEntity).setBeamFrequency(getBeamFrequency(itemStackHeld));
			Commons.addChatMessage(entityPlayer, new TranslationTextComponent("warpdrive.beam_frequency.set",
			                                                                  tileEntity.getBlockState().getBlock().getNameTextComponent(),
			                                                                  getBeamFrequency(itemStackHeld)));
			world.playSound(entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean doesSneakBypassUse(@Nonnull final ItemStack itemStack,
	                                  @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos,
	                                  @Nonnull final PlayerEntity player) {
		final Block block = worldReader.getBlockState(blockPos).getBlock();
		return block instanceof BlockCapacitor || super.doesSneakBypassUse(itemStack, worldReader, blockPos, player);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		final WarpDriveText textTooltip = new WarpDriveText();
		textTooltip.append(null, "warpdrive.video_channel.tooltip",
		               new WarpDriveText(Commons.getStyleValue(), getVideoChannel(itemStack)) );
		textTooltip.append(null, "warpdrive.beam_frequency.tooltip",
		               new WarpDriveText(Commons.getStyleValue(), getBeamFrequency(itemStack)) );
		textTooltip.append(null, "warpdrive.control_channel.tooltip",
		               new WarpDriveText(Commons.getStyleValue(), getControlChannel(itemStack)) );
		textTooltip.appendLineBreak();
		textTooltip.append(null, "item.warpdrive.tool.tuning_fork.tooltip.usage");
		
		Commons.addTooltip(list, textTooltip.getFormattedText());
	}
}