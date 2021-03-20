package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirContainerItem;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ItemComponent extends ItemAbstractBase implements IAirContainerItem {
	
	private static final ItemStack[] itemStackCache = new ItemStack[EnumComponentType.length];
	
	private final EnumComponentType componentType;
	
	public ItemComponent(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final EnumComponentType componentType) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain),
		      registryName,
		      enumTier );
		
		this.componentType = componentType;
	}
	
	@Nonnull
	public static Item getItem(@Nonnull final EnumComponentType componentType) {
		final int indexType = componentType.ordinal();
		return WarpDrive.itemComponents[indexType];
	}
	
	@Nonnull
	public static ItemStack getItemStack(@Nonnull final EnumComponentType componentType) {
		final int indexType = componentType.ordinal();
		if (itemStackCache[indexType] == null) {
			itemStackCache[indexType] = new ItemStack(WarpDrive.itemComponents[indexType], 1);
		}
		return itemStackCache[indexType];
	}
	
	@Nonnull
	public static ItemStack getItemStackNoCache(@Nonnull final EnumComponentType enumComponentType, final int amount) {
		return new ItemStack(WarpDrive.itemComponents[enumComponentType.ordinal()], amount);
	}
	
	public EnumComponentType getComponentType() {
		return componentType;
	}
	
	// IAirContainerItem overrides for empty air canister
	@Override
	public boolean canContainAir(final ItemStack itemStack) {
		return componentType == EnumComponentType.AIR_CANISTER;
	}
	
	@Override
	public int getMaxAirStorage(final ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirTanks[0].getMaxAirStorage(itemStack);
		} else {
			return 0;
		}
	}
	
	@Override
	public int getCurrentAirStorage(final ItemStack itemStack) {
		return 0;
	}
	
	@Override
	public ItemStack consumeAir(final ItemStack itemStack) {
		WarpDrive.logger.error(String.format("%s consumeAir() with itemStack %s",
		                                     this, itemStack));
		throw new RuntimeException("Invalid call to consumeAir() on non or empty container");
	}
	
	@Override
	public int getAirTicksPerConsumption(final ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirTanks[0].getAirTicksPerConsumption(new ItemStack(WarpDrive.itemAirTanks[0]));
		} else {
			return 0;
		}
	}
	
	@Override
	public ItemStack getFullAirContainer(final ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirTanks[0].getFullAirContainer(new ItemStack(WarpDrive.itemAirTanks[0]));
		}
		return null;
	}
	
	@Override
	public ItemStack getEmptyAirContainer(final ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirTanks[0].getEmptyAirContainer(new ItemStack(WarpDrive.itemAirTanks[0]));
		}
		return null;
	}
	
	@Override
	public boolean doesSneakBypassUse(@Nonnull final ItemStack itemStack,
	                                  @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos,
	                                  @Nonnull final PlayerEntity player) {
		final Block block = worldReader.getBlockState(blockPos).getBlock();
		
		return block instanceof BlockAbstractContainer
		    || super.doesSneakBypassUse(itemStack, worldReader, blockPos, player);
	}
}