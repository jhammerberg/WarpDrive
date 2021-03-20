package cr0s.warpdrive.item;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IParticleContainerItem;
import cr0s.warpdrive.api.Particle;
import cr0s.warpdrive.api.ParticleRegistry;
import cr0s.warpdrive.api.ParticleStack;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.event.ModelHandler;
import cr0s.warpdrive.render.BakedModelElectromagneticCell;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemElectromagneticCell extends ItemAbstractBase implements IParticleContainerItem {
	
	public ItemElectromagneticCell(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .containerItem(WarpDrive.itemElectromagneticCell[enumTier.getIndex()]) // just for reference, it's null at this point...
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
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		super.modelInitialisation();
		
		final ResourceLocation registryName = getRegistryName();
		assert registryName != null;
		
		// register particle variants
		final Map<String, Particle> particles = ParticleRegistry.getRegisteredParticles();
		for(final Particle particle : particles.values()) {
			final String variant = particle.getTranslationKey().replace("warpdrive.particle.", "");
			final ResourceLocation resourceLocationModel = new ResourceLocation(registryName.getNamespace(), registryName.getPath() + "-" + variant);
			ModelHandler.registerSpecialModel(new ModelResourceLocation(resourceLocationModel, "inventory"));
		}
		
		// register (smart) baked model
		ModelHandler.registerBakedModel(new ModelResourceLocation(registryName, "inventory"), BakedModelElectromagneticCell.class);
	}
	
	@Override
	public void fillItemGroup(@Nonnull final ItemGroup group, @Nonnull final NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
		if (this.isInGroup(group)) {
			// items.add(getItemStackNoCache(null, 0));
			final int capacity10PC = WarpDriveConfig.ELECTROMAGNETIC_CELL_CAPACITY_BY_TIER[enumTier.getIndex()] / 10;
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC));
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC * 3));
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC * 5));
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC * 7));
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC * 9));
			items.add(getItemStackNoCache(ParticleRegistry.ION, capacity10PC * 10));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC * 3));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC * 5));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC * 7));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC * 9));
			items.add(getItemStackNoCache(ParticleRegistry.PROTON, capacity10PC * 10));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC * 3));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC * 5));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC * 7));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC * 9));
			items.add(getItemStackNoCache(ParticleRegistry.ANTIMATTER, capacity10PC * 10));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC * 3));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC * 5));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC * 7));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC * 9));
			items.add(getItemStackNoCache(ParticleRegistry.STRANGE_MATTER, capacity10PC * 10));
		}
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumTier enumTier, @Nullable final Particle particle, final int amount) {
		return WarpDrive.itemElectromagneticCell[enumTier.getIndex()].getItemStackNoCache(particle, amount);
	}
	
	@Nonnull
	public ItemStack getItemStackNoCache(@Nullable final Particle particle, final int amount) {
		final ItemStack itemStack = new ItemStack(WarpDrive.itemElectromagneticCell[enumTier.getIndex()], 1);
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
			final long tickToConsume = tagCompound.getLong(IParticleContainerItem.TAG_TICK_TO_CONSUME);
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
		if (!(itemStack.getItem() instanceof ItemElectromagneticCell)) {
			WarpDrive.logger.error(String.format("Invalid ItemStack passed, expecting ItemElectromagneticCell: %s",
			                                     itemStack));
			return itemStack.getDamage();
		}
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			return 0;
		}
		final ItemElectromagneticCell itemElectromagneticCell = (ItemElectromagneticCell) itemStack.getItem();
		final int type = particleStack.getParticle().getColorIndex() % 5;
		final double ratio = particleStack.getAmount() / (double) itemElectromagneticCell.getCapacity(itemStack);
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
		return WarpDriveConfig.ELECTROMAGNETIC_CELL_CAPACITY_BY_TIER[enumTier.getIndex()];
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
	public int getEntityLifespan(final ItemStack itemStack, final World world) {
		final ParticleStack particleStack = getParticleStack(itemStack);
		if ( particleStack == null
		  || particleStack.isEmpty() ) {
			return super.getEntityLifespan(itemStack, world);
		}
		final int lifespan = particleStack.getEntityLifespan();
		if (lifespan < 0) {
			return super.getEntityLifespan(itemStack, world);
		}
		// less content means more stable, so we scale lifespan with emptiness, up to doubling it
		return (2 - particleStack.getAmount() / getCapacity(itemStack)) * lifespan;
	}
	
	@Override
	public void onEntityExpireEvent(final ItemEntity entityItem, final ItemStack itemStack) {
		final ParticleStack particleStack = getParticleStack(itemStack);
		if ( particleStack == null
		  || particleStack.isEmpty() ) {
			super.onEntityExpireEvent(entityItem, itemStack);
			return;
		}
		particleStack.onWorldEffect(entityItem.world, new Vector3(entityItem));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final World world,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		if (!(itemStack.getItem() instanceof  ItemElectromagneticCell)) {
			WarpDrive.logger.error(String.format("Invalid ItemStack passed, expecting ItemElectromagneticCell: %s",
			                                     itemStack));
			return;
		}
		final ItemElectromagneticCell itemElectromagneticCell = (ItemElectromagneticCell) itemStack.getItem();
		final ParticleStack particleStack = itemElectromagneticCell.getParticleStack(itemStack);
		final String tooltip;
		if ( particleStack == null
		  || particleStack.getParticle() == null ) {
			tooltip = new TranslationTextComponent("item.warpdrive.atomic.electromagnetic_cell.tooltip.empty").getFormattedText();
			Commons.addTooltip(list, tooltip);
			
		} else {
			final Particle particle = particleStack.getParticle();
			
			tooltip = new TranslationTextComponent("item.warpdrive.atomic.electromagnetic_cell.tooltip.filled",
			                                       new WarpDriveText(Commons.getStyleValue(), particleStack.getAmount()),
			                                       new WarpDriveText(Commons.getStyleValue(), particle.getLocalizedName()) ).getFormattedText();
			Commons.addTooltip(list, tooltip);
			
			final String particleTooltip = particle.getLocalizedTooltip();
			if (!particleTooltip.isEmpty()) {
				Commons.addTooltip(list, particleTooltip);
			}
		}
	}
}