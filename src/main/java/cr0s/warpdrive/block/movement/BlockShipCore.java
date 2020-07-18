package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockShipCore extends BlockAbstractContainer {
	
	public BlockShipCore(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(BlockProperties.FACING_HORIZONTAL, Direction.NORTH)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityShipCore();
	}
	
	/* TODO MC1.15 ship core dismounting exploit fix
	@Override
	public void getDrops(@Nonnull final NonNullList<ItemStack> itemStacks, @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos,
	                     @Nonnull final BlockState blockState, final int fortune) {
		final TileEntity tileEntity = worldReader.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityShipCore) {
			if (((TileEntityShipCore) tileEntity).jumpCount == 0) {
				super.getDrops(itemStacks, worldReader, blockPos, blockState, fortune);
				return;
			}
		}
		if (worldReader instanceof ServerWorld) {
			final ServerWorld worldServer = (ServerWorld) worldReader;
			final PlayerEntity entityPlayer = CommonProxy.getFakePlayer(null, worldServer, blockPos);
			// trigger explosion
			final TNTEntity entityTNTPrimed = new TNTEntity(worldServer,
				blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F, entityPlayer);
			entityTNTPrimed.setFuse(10 + worldServer.rand.nextInt(10));
			worldServer.addEntity(entityTNTPrimed);
			
			// get a chance to get the drops
			itemStacks.add(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 1));
			if (fortune > 0 && worldServer.rand.nextBoolean()) {
				itemStacks.add(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 1));
			}
			if (fortune > 1 && worldServer.rand.nextBoolean()) {
				itemStacks.add(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 1));
			}
			if (fortune > 1 & worldServer.rand.nextBoolean()) {
				itemStacks.add(ItemComponent.getItemStackNoCache(EnumComponentType.POWER_INTERFACE, 1));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(@Nonnull final BlockState blockState, @Nonnull final PlayerEntity entityPlayer,
	                                            @Nonnull final World world, @Nonnull final BlockPos blockPos) {
		boolean willBreak = true;
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityShipCore) {
			if (((TileEntityShipCore)tileEntity).jumpCount == 0) {
				willBreak = false;
			}
		}
		return (willBreak ? 0.02F : 1.0F) * super.getPlayerRelativeBlockHardness(blockState, entityPlayer, world, blockPos);
	}
	*/
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if (enumHand != Hand.MAIN_HAND) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityShipCore)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityShipCore tileEntityShipCore = (TileEntityShipCore) tileEntity;
		
		if (itemStackHeld.isEmpty()) {
			if ( world.isRemote()
			  && entityPlayer.isSneaking() ) {
				tileEntityShipCore.showBoundingBox = !tileEntityShipCore.showBoundingBox;
				if (tileEntityShipCore.showBoundingBox) {
					world.playSound(null, blockPos, SoundEvents.LASER_LOW, SoundCategory.BLOCKS, 4.0F, 2.0F);
				} else {
					world.playSound(null, blockPos, SoundEvents.LASER_LOW, SoundCategory.BLOCKS, 4.0F, 1.4F);
				}
				Commons.addChatMessage(entityPlayer, tileEntityShipCore.getBoundingBoxStatus());
				return ActionResultType.CONSUME;
				
			} else if ( !world.isRemote()
			         && !entityPlayer.isSneaking() ) {
				Commons.addChatMessage(entityPlayer, tileEntityShipCore.getStatus());
				return ActionResultType.CONSUME;
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
	
	@Override
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final IBlockReader blockReader,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, blockReader, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent("tile.warpdrive.movement.ship_core.tooltip.constrains",
		                                                      new WarpDriveText(Commons.getStyleValue(), WarpDriveConfig.SHIP_SIZE_MAX_PER_SIDE_BY_TIER[enumTier.getIndex()]),
		                                                      new WarpDriveText(Commons.getStyleValue(), WarpDriveConfig.SHIP_MASS_MIN_BY_TIER[enumTier.getIndex()]),
		                                                      new WarpDriveText(Commons.getStyleValue(), WarpDriveConfig.SHIP_MASS_MAX_BY_TIER[enumTier.getIndex()]),
		                                                      new WarpDriveText(Commons.getStyleValue(), WarpDriveConfig.SHIP_MASS_MAX_ON_PLANET_SURFACE),
		                                                      new WarpDriveText(Commons.getStyleValue(), WarpDriveConfig.SHIP_MASS_MIN_FOR_HYPERSPACE) ).getFormattedText());
	}
}