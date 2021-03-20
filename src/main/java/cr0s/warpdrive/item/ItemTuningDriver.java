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

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
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
	
	public enum Mode implements IStringSerializable {
		VIDEO_CHANNEL(0.0F),
		BEAM_FREQUENCY(1.0F),
		CONTROL_CHANNEL(2.0F);
		
		public static final String TAG = "mode";
		private final String name;
		private final float  propertyValue;
		
		// cached values
		public static final int length;
		private static final HashMap<String, Mode> ID_MAP = new HashMap<>();
		
		static {
			length = Mode.values().length;
			for (final Mode mode : values()) {
				ID_MAP.put(mode.name, mode);
			}
		}
		
		Mode(final float propertyValue) {
			this.name = toString().toLowerCase();
			this.propertyValue = propertyValue;
		}
		
		public static Mode get(final String name) {
			return ID_MAP.get(name);
		}
		
		@Nonnull
		@Override
		public String getName() {
			return name;
		}
		
		public float getPropertyValue() {
			return propertyValue;
		}
	}
	
	public ItemTuningDriver(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1),
		      registryName,
		      enumTier );
		
		addPropertyOverride(new ResourceLocation(WarpDrive.MODID, "tuning_mode"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			@Override
			public float call(@Nonnull final ItemStack itemStack, @Nullable final World world, @Nullable final LivingEntity entity) {
				return getMode(itemStack).getPropertyValue();
			}
		});
	}
	
	@Override
	public void fillItemGroup(@Nonnull final ItemGroup group, @Nonnull final NonNullList<ItemStack> items) {
		// super.fillItemGroup(group, items);
		if (this.isInGroup(group)) {
			for(final Mode mode : Mode.values()) {
				items.add(getItemStackNoCache(mode));
			}
		}
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final Mode mode) {
		final ItemStack itemStack = new ItemStack(WarpDrive.itemTuningDriver, 1);
		final CompoundNBT tagCompound = new CompoundNBT();
		tagCompound.putString(Mode.TAG, mode.getName());
		itemStack.setTag(tagCompound);
		return itemStack;
	}
	
	@Nonnull
	@Override
	public String getTranslationKey(@Nonnull final ItemStack itemStack) {
		return getTranslationKey() + "." + getMode(itemStack).getName();
	}
	
	@Nonnull
	public static Mode getMode(@Nonnull final ItemStack itemStack) {
		Mode mode = null;
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound != null) {
			mode = Mode.get(tagCompound.getString(Mode.TAG));
		}
		if (mode == null) {
			mode = Mode.VIDEO_CHANNEL;
		}
		return mode;
	}
	
	public static void setMode(@Nonnull final ItemStack itemStack, @Nonnull Mode mode) {
		CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			tagCompound = new CompoundNBT();
		}
		tagCompound.putString(Mode.TAG, mode.getName());
	}
	
	public static int getVideoChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return IVideoChannel.VIDEO_CHANNEL_INVALID;
		}
		return IVideoChannel.readVideoChannel(itemStack.getTag());
	}
	
	@Nonnull
	public static ItemStack setVideoChannel(@Nonnull final ItemStack itemStack, final int videoChannel) {
		if ( !(itemStack.getItem() instanceof ItemTuningDriver)
		  || !IVideoChannel.isValid(videoChannel) ) {
			return itemStack;
		}
		itemStack.setTag(IVideoChannel.writeVideoChannel(itemStack.getTag(), videoChannel));
		return itemStack;
	}
	
	public static int getBeamFrequency(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return IBeamFrequency.BEAM_FREQUENCY_INVALID;
		}
		return IBeamFrequency.readBeamFrequency(itemStack.getTag());
	}
	
	@Nonnull
	public static ItemStack setBeamFrequency(@Nonnull final ItemStack itemStack, final int beamFrequency) {
		if ( !(itemStack.getItem() instanceof ItemTuningDriver)
		  || !IBeamFrequency.isValid(beamFrequency)) {
			return itemStack;
		}
		itemStack.setTag(IBeamFrequency.writeBeamFrequency(itemStack.getTag(), beamFrequency));
		return itemStack;
	}
	
	public static int getControlChannel(@Nonnull final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningDriver)) {
			return IControlChannel.CONTROL_CHANNEL_INVALID;
		}
		return IControlChannel.readControlChannel(itemStack.getTag());
	}
	
	@Nonnull
	public static ItemStack setControlChannel(@Nonnull final ItemStack itemStack, final int controlChannel) {
		if ( !(itemStack.getItem() instanceof ItemTuningDriver)
		  || !IControlChannel.isValid(controlChannel)) {
			return itemStack;
		}
		itemStack.setTag(IControlChannel.writeControlChannel(itemStack.getTag(), controlChannel));
		return itemStack;
	}
	
	@Nonnull
	public static ItemStack setValue(@Nonnull final ItemStack itemStack, final int value) {
		switch (getMode(itemStack)) {
		case VIDEO_CHANNEL  : return setVideoChannel(itemStack, value);
		case BEAM_FREQUENCY : return setBeamFrequency(itemStack, value);
		case CONTROL_CHANNEL: return setControlChannel(itemStack, value);
		default             : return itemStack;
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
		
		// sneak right click in creative to set random values
		// right click in air to change mode
		if (entityPlayer.isSneaking() && entityPlayer.isCreative()) {
			switch (getMode(itemStackHeld)) {
			case VIDEO_CHANNEL:
				setVideoChannel(itemStackHeld, 1 + world.rand.nextInt(IVideoChannel.VIDEO_CHANNEL_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.video_channel.get",
					entityPlayer.getName().getFormattedText(),
					getVideoChannel(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			case BEAM_FREQUENCY:
				setBeamFrequency(itemStackHeld, 1 + world.rand.nextInt(IBeamFrequency.BEAM_FREQUENCY_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.beam_frequency.get",
					entityPlayer.getName().getFormattedText(),
					getBeamFrequency(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			case CONTROL_CHANNEL:
				setControlChannel(itemStackHeld, world.rand.nextInt(IControlChannel.CONTROL_CHANNEL_MAX));
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.control_channel.get",
					entityPlayer.getName().getFormattedText(),
					getControlChannel(itemStackHeld)));
				return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
			
			default:
				return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
			}
			
		} else {
			switch (getMode(itemStackHeld)) {
			case VIDEO_CHANNEL:
				setMode(itemStackHeld, Mode.BEAM_FREQUENCY);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			case BEAM_FREQUENCY:
				setMode(itemStackHeld, Mode.CONTROL_CHANNEL);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			case CONTROL_CHANNEL:
				setMode(itemStackHeld, Mode.VIDEO_CHANNEL);
				entityPlayer.setHeldItem(hand, itemStackHeld);
				break;
			
			default:
				setMode(itemStackHeld, Mode.VIDEO_CHANNEL);
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
		
		switch (getMode(itemStackHeld)) {
		case VIDEO_CHANNEL:
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
							                                               ((IVideoChannel) tileEntity).getVideoChannel() ));
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
			
		case BEAM_FREQUENCY:
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
							                                               ((IBeamFrequency) tileEntity).getBeamFrequency() ));
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
			
		case CONTROL_CHANNEL:
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
							                                               ((IControlChannel) tileEntity).getControlChannel() ));
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
		switch (getMode(itemStack)) {
		case VIDEO_CHANNEL:
			textTooltip.append(null, "warpdrive.video_channel.tooltip",
			                   new WarpDriveText(Commons.getStyleValue(), getVideoChannel(itemStack)) );
			break;
		case BEAM_FREQUENCY:
			textTooltip.append(null, "warpdrive.beam_frequency.tooltip",
			                   new WarpDriveText(Commons.getStyleValue(), getBeamFrequency(itemStack)) );
			break;
		case CONTROL_CHANNEL:
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