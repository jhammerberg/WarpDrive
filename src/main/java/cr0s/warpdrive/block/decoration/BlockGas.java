package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.data.EnumGasColor;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockGas extends BlockAbstractBase {
	
	public static final EnumProperty<EnumGasColor> COLOR = EnumProperty.create("color", EnumGasColor.class);
	
	public BlockGas(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
		      .noDrops()
		      .hardnessAndResistance(0.0F),
		      registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(COLOR, EnumGasColor.RED)
		               );
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean causesSuffocation(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
	
	@Override
	public boolean isAir(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return VoxelShapes.empty();
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.DESTROY;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockAdded(@Nonnull final BlockState blockStateNew, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                         @Nonnull final BlockState blockStateOld, final boolean isMoving) {
		// Gas blocks are only allowed in space
		if (CelestialObjectManager.hasAtmosphere(world, blockPos.getX(), blockPos.getZ())) {
			world.removeBlock(blockPos, false);
		}
	}
}