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
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTuningDriver extends ItemAbstractBase implements IWarpTool {
	
	public static final int MODE_VIDEO_CHANNEL = 0;
	public static final int MODE_BEAM_FREQUENCY = 1;
	public static final int MODE_CONTROL_CHANNEL = 2;
	
	public ItemTuningDriver(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1),
		      registryName,
		      enumTier );
		
		setTranslationKey("warpdrive.tool.tuning_driver");
	}
	
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getModelResourceLocation(@Nonnull final ItemStack itemStack) {
		final int damage = itemStack.getDamage();
		ResourceLocation resourceLocation = getRegistryName();
		assert resourceLocation != null;
		switch (damage) {
		case MODE_VIDEO_CHANNEL:
			resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "-video_channel");
			break;
		case MODE_BEAM_FREQUENCY:
			resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "-beam_frequency");
			break;
		case MODE_CONTROL_CHANNEL:
			resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "-control_channel");
			break;
		default:
			resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "-invalid");
			break;
		}
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	
	@Nonnull
	@Override
	public String getTranslationKey(@Nonnull final ItemStack itemStack) {
		final int damage = itemStack.getDamage();
		switch (damage) {
		case MODE_VIDEO_CHANNEL  : return getTranslationKey() + ".video_channel";
		case MODE_BEAM_FREQUENCY : return getTranslationKey() + ".beam_frequency";
		case MODE_CONTROL_CHANNEL: return getTranslationKey() + ".control_channel";
		default: return getTranslationKey();
		}
	}
	
	public static int getVideoChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return -1;
		}
		if (!itemStack.hasTag()) {
			return -1;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		assert tagCompound != null;
		if (tagCompound.contains(IVideoChannel.VIDEO_CHANNEL_TAG)) {
			return tagCompound.getInt(IVideoChannel.VIDEO_CHANNEL_TAG);
		}
		return -1;
	}
	
	@Nonnull
	public static ItemStack setVideoChannel(@Nonnull final ItemStack itemStack, final int videoChannel) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver) || videoChannel == -1) {
			return itemStack;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		tagCompound.putInt(IVideoChannel.VIDEO_CHANNEL_TAG, videoChannel);
		itemStack.setTag(tagCompound);
		return itemStack;
	}
	
	public static int getBeamFrequency(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return -1;
		}
		if (!itemStack.hasTag()) {
			return -1;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if ( tagCompound != null
		  && tagCompound.contains(IBeamFrequency.BEAM_FREQUENCY_TAG) ) {
			return tagCompound.getInt(IBeamFrequency.BEAM_FREQUENCY_TAG);
		}
		return -1;
	}
	
	@Nonnull
	public static ItemStack setBeamFrequency(@Nonnull final ItemStack itemStack, final int beamFrequency) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver) || beamFrequency == -1) {
			return itemStack;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		tagCompound.putInt(IBeamFrequency.BEAM_FREQUENCY_TAG, beamFrequency);
		itemStack.setTag(tagCompound);
		return itemStack;
	}
	
	public static int getControlChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return -1;
		}
		if (!itemStack.hasTag()) {
			return -1;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if ( tagCompound != null
		  && tagCompound.contains(IControlChannel.CONTROL_CHANNEL_TAG) ) {
			return tagCompound.getInt(IControlChannel.CONTROL_CHANNEL_TAG);
		}
		return -1;
	}
	
	@Nonnull
	public static ItemStack setControlChannel(@Nonnull final ItemStack itemStack, final int controlChannel) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver) || controlChannel == -1) {
			return itemStack;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		tagCompound.putInt(IControlChannel.CONTROL_CHANNEL_TAG, controlChannel);
		itemStack.setTag(tagCompound);
		return itemStack;
	}
	
	@Nonnull
	public static ItemStack setValue(@Nonnull final ItemStack itemStack, final int dye) {
		switch (itemStack.getDamage()) {
		case MODE_VIDEO_CHANNEL  : return setVideoChannel(itemStack, dye);
		case MODE_BEAM_FREQUENCY : return setBeamFrequency(itemStack, dye);
		case MODE_CONTROL_CHANNEL: return setControlChannel(itemStack, dye);
		default                  : return itemStack;
		}
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull final World world, @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand hand) {
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(hand);
		
		if ( world.isRemote()
		  || !(itemStackHeld.getItem() instanceof ItemTuningDriver) ) {
			return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
		}
		// check if a block is in players reach 
		final RayTraceResult movingObjectPosition = Item.rayTrace(world, entityPlayer, RayTraceContext.FluidMode.NONE);
		if (movingObjectPosition.getType() != Type.MISS) {
			return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
		}
		
		if (entityPlayer.isSneaking() && entityPlayer.isCreative()) {
			switch (itemStackHeld.getDamage()) {
			case MODE_VIDEO_CHANNEL:
				setVideoChannel(itemStackHeld, 1 + world.rand.nextInt(IVideoChannel.VIDEO_CHANNEL_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.video_channel.get",
					entityPlayer.getName().getFormattedText(),
					getVideoChannel(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			case MODE_BEAM_FREQUENCY:
				setBeamFrequency(itemStackHeld, 1 + world.rand.nextInt(IBeamFrequency.BEAM_FREQUENCY_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.beam_frequency.get",
					entityPlayer.getName().getFormattedText(),
					getBeamFrequency(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			case MODE_CONTROL_CHANNEL:
				setControlChannel(itemStackHeld, world.rand.nextInt(IControlChannel.CONTROL_CHANNEL_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.control_channel.get",
					entityPlayer.getName().getFormattedText(),
					getControlChannel(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			default:
				return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
			}
			
		} else {
			switch (itemStackHeld.getDamage()) {
			case MODE_VIDEO_CHANNEL:
				itemStackHeld.setDamage(MODE_BEAM_FREQUENCY);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			case MODE_BEAM_FREQUENCY:
				itemStackHeld.setDamage(MODE_CONTROL_CHANNEL);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			case MODE_CONTROL_CHANNEL:
				itemStackHeld.setDamage(MODE_VIDEO_CHANNEL);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			default:
				itemStackHeld.setDamage(MODE_VIDEO_CHANNEL);
				break;
			}
			world.playSound(entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
			return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
		}
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
		
		switch (itemStackHeld.getDamage()) {
		case MODE_VIDEO_CHANNEL:
			if (tileEntity instanceof IVideoChannel) {
				if (entityPlayer.isSneaking()) {
					setVideoChannel(itemStackHeld, ((IVideoChannel) tileEntity).getVideoChannel());
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.video_channel.get",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getVideoChannel(itemStackHeld) ));
				} else {
					((IVideoChannel) tileEntity).setVideoChannel(getVideoChannel(itemStackHeld));
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.video_channel.set",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getVideoChannel(itemStackHeld) ));
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
			
		case MODE_BEAM_FREQUENCY:
			if (tileEntity instanceof IBeamFrequency) {
				if (entityPlayer.isSneaking()) {
					setBeamFrequency(itemStackHeld, ((IBeamFrequency) tileEntity).getBeamFrequency());
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.beam_frequency.get",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getBeamFrequency(itemStackHeld) ));
				} else {
					((IBeamFrequency) tileEntity).setBeamFrequency(getBeamFrequency(itemStackHeld));
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.beam_frequency.set",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getBeamFrequency(itemStackHeld) ));
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
			
		case MODE_CONTROL_CHANNEL:
			if (tileEntity instanceof IControlChannel) {
				if (entityPlayer.isSneaking()) {
					setControlChannel(itemStackHeld, ((IControlChannel) tileEntity).getControlChannel());
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.control_channel.get",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getControlChannel(itemStackHeld) ));
				} else {
					((IControlChannel) tileEntity).setControlChannel(getControlChannel(itemStackHeld));
					Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.control_channel.set",
							tileEntity.getBlockState().getBlock().getNameTextComponent(),
							getControlChannel(itemStackHeld) ));
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
			
		default:
			return ActionResultType.FAIL;
		}
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
		switch (itemStack.getDamage()) {
		case MODE_VIDEO_CHANNEL:
			textTooltip.append(null, "warpdrive.video_channel.tooltip",
			                   new WarpDriveText(Commons.getStyleValue(), getVideoChannel(itemStack)) );
			break;
		case MODE_BEAM_FREQUENCY:
			textTooltip.append(null, "warpdrive.beam_frequency.tooltip",
			                   new WarpDriveText(Commons.getStyleValue(), getBeamFrequency(itemStack)) );
			break;
		case MODE_CONTROL_CHANNEL:
			textTooltip.append(null, "warpdrive.control_channel.tooltip",
			                   new WarpDriveText(Commons.getStyleValue(), getControlChannel(itemStack)) );
			break;
		default:
			textTooltip.append(new StringTextComponent("I'm broken :("));
			break;
		}
		
		textTooltip.appendLineBreak();
		textTooltip.append(null, "item.warpdrive.tool.tuning_driver.tooltip.usage");
		
		Commons.addTooltip(list, textTooltip.getFormattedText());
	}
}
