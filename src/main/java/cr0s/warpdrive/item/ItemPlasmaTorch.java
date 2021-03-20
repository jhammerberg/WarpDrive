package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IParticleContainerItem;
import cr0s.warpdrive.api.Particle;
import cr0s.warpdrive.api.ParticleStack;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPlasmaTorch extends ItemAbstractBase implements IParticleContainerItem {
	
	public ItemPlasmaTorch(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1),
		      registryName,
		      enumTier );
		
		addPropertyOverride(new ResourceLocation(WarpDrive.MODID, "fill"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			@Override
			public float call(@Nonnull final ItemStack itemStack, @Nullable final World world, @Nullable final LivingEntity entity) {
				final ParticleStack particleStack = getParticleStack(itemStack);
				if (particleStack != null) {
					return (float) particleStack.getAmount() / getCapacity(itemStack);
				}
				return 0.0F;
			}
		});
	}
	
	/*
	@Nonnull
	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getModelResourceLocation(final ItemStack itemStack) {
		String variant = "empty";
		final ParticleStack particleStack = getParticleStack(itemStack);
		if (particleStack != null) {
			variant = particleStack.getTranslationKey().replace("warpdrive.particle.", "");
		}
		ResourceLocation resourceLocation = getRegistryName();
		assert resourceLocation != null;
		resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "-" + variant);
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	/**/
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumTier enumTier, @Nullable final Particle particle, final int amount) {
		return WarpDrive.itemPlasmaTorch[enumTier.getIndex()].getItemStackNoCache(particle, amount);
	}
	
	@Nonnull
	public ItemStack getItemStackNoCache(@Nullable final Particle particle, final int amount) {
		final ItemStack itemStack = new ItemStack(this, 1);
		ParticleStack particleStack = null;
		if ( particle != null
		   && amount != 0 ) {
			particleStack = new ParticleStack(particle, amount);
			final CompoundNBT tagCompound = new CompoundNBT();
			tagCompound.put(IParticleContainerItem.TAG_PARTICLE, particleStack.write(new CompoundNBT()));
			itemStack.setTag(tagCompound);
		}
		updateDamageLevel(itemStack, particleStack);
		return itemStack;
	}
	
	@Override
	public boolean hasContainerItem(@Nonnull final ItemStack itemStack) {
		return true;
	}
	
	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull final ItemStack itemStackFilled) {
		final ParticleStack particleStack = getParticleStack(itemStackFilled);
		if (particleStack != null) {
			final int amountToConsume = getAmountToConsume(itemStackFilled);
			if (amountToConsume > 0) {
				final int amountLeft = particleStack.getAmount() - amountToConsume;
				if (amountLeft <= 0) {
					return getItemStackNoCache(null, 0);
				}
				return getItemStackNoCache(particleStack.getParticle(), amountLeft);
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setAmountToConsume(@Nonnull final ItemStack itemStack, final int amountToConsume) {
		final ParticleStack particleStack = getParticleStack(itemStack);
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			return;
		}
		CompoundNBT tagCompound = itemStack.getTag();
		if (amountToConsume > 0) {
			if (tagCompound == null) {
				tagCompound = new CompoundNBT();
			}
			tagCompound.putInt(IParticleContainerItem.TAG_AMOUNT_TO_CONSUME, amountToConsume);
			tagCompound.putLong(IParticleContainerItem.TAG_TICK_TO_CONSUME, System.currentTimeMillis());
			
		} else if (tagCompound != null) {
			tagCompound.remove(IParticleContainerItem.TAG_AMOUNT_TO_CONSUME);
			tagCompound.remove(IParticleContainerItem.TAG_TICK_TO_CONSUME);
			if (tagCompound.isEmpty()) {
				itemStack.setTag(null);
			}
		}
	}
	
	private int getAmountToConsume(@Nonnull final ItemStack itemStack) {
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound != null) {
			// when taking a recipe output, the recipe is matched again before checking for recipients, so we can assume it's in the same tick
			final long tickToConsume = tagCompound.getInt(IParticleContainerItem.TAG_TICK_TO_CONSUME);
			if (System.currentTimeMillis() - tickToConsume < 50L) {
				return tagCompound.getInt(IParticleContainerItem.TAG_AMOUNT_TO_CONSUME);
			} else {
				tagCompound.remove(IParticleContainerItem.TAG_AMOUNT_TO_CONSUME);
				tagCompound.remove(IParticleContainerItem.TAG_TICK_TO_CONSUME);
			}
		}
		return 0;
	}
	
	private static int getDamageLevel(@Nonnull final ItemStack itemStack, final ParticleStack particleStack) {
		if (!(itemStack.getItem() instanceof ItemPlasmaTorch)) {
			WarpDrive.logger.error(String.format("Invalid ItemStack passed, expecting ItemPlasmaTorch: %s",
			                                     itemStack));
			return itemStack.getDamage();
		}
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			return 0;
		}
		final ItemPlasmaTorch itemPlasmaTorch = (ItemPlasmaTorch) itemStack.getItem();
		final int type = particleStack.getParticle().getColorIndex() % 5;
		final double ratio = particleStack.getAmount() / (double) itemPlasmaTorch.getCapacity(itemStack);
		final int offset = (ratio < 0.2) ? 0 : (ratio < 0.4) ? 1 : (ratio < 0.6) ? 2 : (ratio < 0.8) ? 3 : (ratio < 1.0) ? 4 : 5;
		return (1 + type * 6 + offset);
	}
	
	private static void updateDamageLevel(@Nonnull final ItemStack itemStack, final ParticleStack particleStack) {
		itemStack.setDamage(getDamageLevel(itemStack, particleStack));
	}
	
	@Override
	public ParticleStack getParticleStack(@Nonnull final ItemStack itemStack) {
		if ( itemStack.getCount() != 1
		  || itemStack.getItem() != this ) {
			return null;
		}
		final CompoundNBT tagCompound = itemStack.getTag();
		if (tagCompound == null) {
			return null;
		}
		if (!tagCompound.contains(IParticleContainerItem.TAG_PARTICLE)) {
			return null;
		}
		return ParticleStack.loadFromNBT(tagCompound.getCompound(IParticleContainerItem.TAG_PARTICLE));
	}
	
	@Override
	public int getCapacity(final ItemStack container) {
		return WarpDriveConfig.PLASMA_TORCH_CAPACITY_BY_TIER[enumTier.getIndex()];
	}
	
	@Override
	public boolean isEmpty(final ItemStack itemStack) {
		final ParticleStack particleStack = getParticleStack(itemStack);
		return particleStack == null
		    || particleStack.isEmpty();
	}
	
	@Override
	public int fill(final ItemStack itemStack, final ParticleStack resource, final boolean doFill) {
		if (itemStack.getCount() != 1) {
			return 0;
		}
		ParticleStack particleStack = getParticleStack(itemStack);
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			particleStack = new ParticleStack(resource.getParticle(), 0);
		} else if ( !particleStack.isParticleEqual(resource)
		         || particleStack.getAmount() >= getCapacity(itemStack) ) {
			return 0;
		}
		final int transfer = Math.min(resource.getAmount(), getCapacity(itemStack) - particleStack.getAmount());
		if (doFill) {
			particleStack.fill(transfer);
			
			final CompoundNBT tagCompound = itemStack.hasTag() ? itemStack.getTag() : new CompoundNBT();
			assert tagCompound != null;
			tagCompound.put(IParticleContainerItem.TAG_PARTICLE, particleStack.write(new CompoundNBT()));
			if (!itemStack.hasTag()) {
				itemStack.setTag(tagCompound);
			}
			updateDamageLevel(itemStack, particleStack);
		}
		return transfer;
	}
	
	@Override
	public ParticleStack drain(final ItemStack itemStack, final ParticleStack resource, final boolean doDrain) {
		final ParticleStack particleStack = getParticleStack(itemStack);
		if ( particleStack == null
		  || particleStack.getParticle() == null
		  || !particleStack.isParticleEqual(resource) 
		  || particleStack.getAmount() <= 0 ) {
			return null;
		}
		final int transfer = Math.min(resource.getAmount(), particleStack.getAmount());
		if (doDrain) {
			particleStack.fill(-transfer);
			
			final CompoundNBT tagCompound = itemStack.hasTag() ? itemStack.getTag() : new CompoundNBT();
			assert tagCompound != null;
			tagCompound.put(IParticleContainerItem.TAG_PARTICLE, particleStack.write(new CompoundNBT()));
			if (!itemStack.hasTag()) {
				itemStack.setTag(tagCompound);
			}
			updateDamageLevel(itemStack, particleStack);
		}
		return resource.copy(transfer);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		if (!(itemStack.getItem() instanceof  ItemPlasmaTorch)) {
			WarpDrive.logger.error(String.format("Invalid ItemStack passed, expecting ItemPlasmaTorch: %s",
			                                     itemStack));
			return;
		}
		final ItemPlasmaTorch itemPlasmaTorch = (ItemPlasmaTorch) itemStack.getItem();
		final ParticleStack particleStack = itemPlasmaTorch.getParticleStack(itemStack);
		final String tooltip;
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			tooltip = new TranslationTextComponent("item.warpdrive.tool.plasma_torch.tooltip.empty").getFormattedText();
			Commons.addTooltip(list, tooltip);
			
		} else {
			final Particle particle = particleStack.getParticle();
			
			tooltip = new TranslationTextComponent("item.warpdrive.tool.plasma_torch.tooltip.filled",
			                                       particleStack.getAmount(), particle.getLocalizedName()).getFormattedText();
			Commons.addTooltip(list, tooltip);
			
			final String particleTooltip = particle.getLocalizedTooltip();
			if (!particleTooltip.isEmpty()) {
				Commons.addTooltip(list, particleTooltip);
			}
		}
	}
}