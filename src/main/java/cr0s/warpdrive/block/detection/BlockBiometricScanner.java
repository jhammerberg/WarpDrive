package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

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

public class BlockBiometricScanner extends BlockAbstractRotatingContainer {
	
	public BlockBiometricScanner(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
	}
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if (world.isRemote()) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		if (enumHand != Hand.MAIN_HAND) {
			return ActionResultType.PASS;
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		
		if ( itemStackHeld.isEmpty()
		  && blockRaytraceResult.getFace() == blockState.get(BlockProperties.FACING) ) {
			final TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityBiometricScanner) {
				final TileEntityBiometricScanner tileEntityBiometricScanner = (TileEntityBiometricScanner) tileEntity;
				final WarpDriveText textReason = new WarpDriveText();
				final boolean isSuccess = tileEntityBiometricScanner.startScanning(entityPlayer, textReason);
				Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(blockState)
				                                            .appendSibling(textReason) );
				return ActionResultType.CONSUME;
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}