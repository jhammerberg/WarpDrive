package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.Commons;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTransporterBeacon extends BlockAbstractContainer {
	
	private static final double BOUNDING_RADIUS = 3.0D / 32.0D;
	private static final double BOUNDING_HEIGHT = 21.0D / 32.0D;
	private static final VoxelShape SHAPE_BEACON = makeCuboidShape(0.5D - BOUNDING_RADIUS, 0.0D, 0.5D - BOUNDING_RADIUS,
	                                                                     0.5D + BOUNDING_RADIUS, BOUNDING_HEIGHT, 0.5D + BOUNDING_RADIUS );
	
	public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");
	
	public BlockTransporterBeacon(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
		      .hardnessAndResistance(0.5F),
		      registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
				                .with(DEPLOYED, false)
		               );
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockTransporterBeacon(this);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return SHAPE_BEACON;
	}
	
	@Override
	public int getLightValue(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		final boolean isActive = blockState.get(BlockProperties.ACTIVE);
		return isActive ? 6 : 0;
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityTransporterBeacon();
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
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityTransporterBeacon)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityTransporterBeacon tileEntityTransporterBeacon = (TileEntityTransporterBeacon) tileEntity;
		
		if (itemStackHeld.isEmpty()) {
			if (!entityPlayer.isSneaking()) {// non-sneaking with an empty hand
				Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(this)
				                                            .appendSibling(new StringTextComponent(tileEntityTransporterBeacon.stateTransporter)));
				return ActionResultType.CONSUME;
				
			} else {// sneaking with an empty hand
				final boolean isEnabledOld = tileEntityTransporterBeacon.getIsEnabled();
				tileEntityTransporterBeacon.setIsEnabled(!isEnabledOld);
				final boolean isEnabledNew = tileEntityTransporterBeacon.getIsEnabled();
				if (isEnabledOld != isEnabledNew) {
					if (isEnabledNew) {
						Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(this)
						                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.set.enabled")));
					} else {
						Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(this)
						                                            .appendSibling(new TranslationTextComponent("warpdrive.machine.is_enabled.set.disabled")));
					}
				}
				return ActionResultType.CONSUME;
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}
