package cr0s.warpdrive.block.collection;

import cr0s.warpdrive.CommonProxy;
import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractLaser;
import cr0s.warpdrive.data.FluidWrapper;
import cr0s.warpdrive.data.InventoryWrapper;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public abstract class TileEntityAbstractMiner extends TileEntityAbstractLaser {
	
	// machine type
	protected Direction      laserOutputSide = Direction.NORTH;
	
	// machine state
	protected boolean		 enableSilktouch = false;
	protected Item           itemTool = Items.DIAMOND_PICKAXE;
	protected ItemStack      itemStackToolNoSilktouch = null;
	protected ItemStack      itemStackToolWithSilktouch = null;
	
	// pre-computation
	protected Vector3        laserOutput = null;
	
	public TileEntityAbstractMiner(@Nonnull TileEntityType<? extends TileEntityAbstractMiner> tileEntityType) {
		super(tileEntityType);
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		laserOutput = new Vector3(this).translate(0.5D).translate(laserOutputSide, 0.5D);
	}
	
	protected void harvestBlock(@Nonnull final BlockPos blockPos, @Nonnull final BlockState blockState) {
		assert world != null;
		if (blockState.getBlock().isAir(blockState, world, blockPos)) {
			return;
		}
		if (FluidWrapper.isFluid(blockState)) {
			// Evaporate fluid
			world.playSound(null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
					2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			
			// remove without updating neighbours
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
			
		} else {
			final NonNullList<ItemStack> itemStackDrops = getItemStackFromBlock(blockPos, blockState);
			
			final PlayerEntity entityPlayer = CommonProxy.getFakePlayer(null, (ServerWorld) world, blockPos);
			net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(itemStackDrops, getWorld(), blockPos, blockState,
			                                                               0, 1.0f, true, entityPlayer);
			
			if (InventoryWrapper.addToConnectedInventories(world, pos, itemStackDrops)) {
				setIsEnabled(false);
			}
			// standard harvest block effect
			world.playEvent(2001, blockPos, Block.getStateId(blockState));
			
			// remove while updating neighbours
			world.removeBlock(blockPos, false); // setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
		}
	}
	
	@Nullable
	private NonNullList<ItemStack> getItemStackFromBlock(final BlockPos blockPos, final BlockState blockState) {
		assert world != null;
		if (blockState == null) {
			WarpDrive.logger.error(String.format("%s Invalid block %s",
			                                     this, Commons.format(world, blockPos)));
			return null;
		}
		final NonNullList<ItemStack> itemStackDrops = NonNullList.create();
		try {
			if (itemStackToolNoSilktouch == null) {
				itemStackToolNoSilktouch = new ItemStack(itemTool, 1);
				itemStackToolNoSilktouch.addEnchantment(Enchantments.UNBREAKING, 1000);
				itemStackToolWithSilktouch = new ItemStack(itemTool, 1);
				itemStackToolWithSilktouch.addEnchantment(Enchantments.UNBREAKING, 1000);
				itemStackToolWithSilktouch.addEnchantment(Enchantments.SILK_TOUCH, 1);
			}
			final ItemStack itemStackTool = enableSilktouch ? itemStackToolWithSilktouch : itemStackToolNoSilktouch;
			
			final TileEntity tileEntity = world.getTileEntity(blockPos);
			
			itemStackDrops.addAll(Block.getDrops(blockState, (ServerWorld) world, blockPos, tileEntity, null, itemStackTool));
		} catch (final Exception exception) {// protect in case the mined block is corrupted
			exception.printStackTrace(WarpDrive.printStreamError);
			return null;
		}
		
		return itemStackDrops;
	}
	
	// NBT DATA
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		enableSilktouch = tagCompound.getBoolean("enableSilktouch");
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		tagCompound.putBoolean("enableSilktouch", enableSilktouch);
		return tagCompound;
	}
}
