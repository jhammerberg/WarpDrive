package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.CameraRegistryItem;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.render.ClientCameraHandler;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockMonitor extends BlockAbstractRotatingContainer {
	
	public BlockMonitor(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
	}

	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		// Monitor is only reacting client side
		if ( !world.isRemote()
		  || enumHand != Hand.MAIN_HAND ) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		
		if ( itemStackHeld.isEmpty()
		  && blockRaytraceResult.getFace() == blockState.get(BlockProperties.FACING) ) {
			final TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityMonitor) {
				// validate video channel
				final int videoChannel = ((TileEntityMonitor) tileEntity).getVideoChannel();
				if ( !IVideoChannel.isValid(videoChannel) ) {
					Commons.addChatMessage(entityPlayer, ((TileEntityMonitor) tileEntity).getStatus());
					return ActionResultType.CONSUME;
				}
				
				// validate camera
				final CameraRegistryItem camera = WarpDrive.cameras.getCameraByVideoChannel(world, videoChannel);
				if ( camera == null
				  || entityPlayer.isSneaking() ) {
					Commons.addChatMessage(entityPlayer, ((TileEntityMonitor) tileEntity).getStatus());
					return ActionResultType.CONSUME;
				}
				
				Commons.addChatMessage(entityPlayer, new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.monitor.viewing_camera",
				                                                       new WarpDriveText(Commons.getStyleValue(), videoChannel),
				                                                       camera.blockPos.getX(),
				                                                       camera.blockPos.getY(),
				                                                       camera.blockPos.getZ() ));
				ClientCameraHandler.setupViewpoint(
						camera.type, entityPlayer, entityPlayer.rotationYaw, entityPlayer.rotationPitch,
						blockPos, blockState,
						camera.blockPos, world.getBlockState(camera.blockPos));
				return ActionResultType.CONSUME;
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}