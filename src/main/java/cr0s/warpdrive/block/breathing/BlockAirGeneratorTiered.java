package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.api.IAirContainerItem;
import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockAirGeneratorTiered extends BlockAbstractRotatingContainer {
	
	public BlockAirGeneratorTiered(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null),
		      registryName, enumTier);
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityAirGeneratorTiered();
	}
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if ( world.isRemote()
		  || enumHand != Hand.MAIN_HAND ) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityAirGeneratorTiered) {
			final TileEntityAirGeneratorTiered airGenerator = (TileEntityAirGeneratorTiered) tileEntity;
			if (!itemStackHeld.isEmpty()) {
				final Item itemHeld = itemStackHeld.getItem();
				if (itemHeld instanceof IAirContainerItem) {
					final IAirContainerItem airContainerItem = (IAirContainerItem) itemHeld;
					if ( airContainerItem.canContainAir(itemStackHeld)
					  && airGenerator.energy_consume(WarpDriveConfig.BREATHING_ENERGY_PER_CANISTER, true) ) {
						// save current held item, as the decrStackSize() call will clear the original
						final ItemStack itemStackCopy = itemStackHeld.copy();
						entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
						final ItemStack toAdd = airContainerItem.getFullAirContainer(itemStackCopy);
						if (toAdd != null) {
							if (!entityPlayer.inventory.addItemStackToInventory(toAdd)) {
								final ItemEntity entityItem = new ItemEntity(entityPlayer.world, entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), toAdd);
								entityPlayer.world.addEntity(entityItem);
							}
							((ServerPlayerEntity) entityPlayer).sendContainerToPlayer(entityPlayer.container);
							airGenerator.energy_consume(WarpDriveConfig.BREATHING_ENERGY_PER_CANISTER, false);
						}
						return ActionResultType.CONSUME;
					}
				}
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}
