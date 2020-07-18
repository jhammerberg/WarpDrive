package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.block.BlockAbstractOmnipanel;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockAirShield extends BlockAbstractOmnipanel {
	
	protected final DyeColor dyeColor;
	
	public BlockAirShield(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final DyeColor dyeColor) {
		super(getDefaultProperties(Material.WOOL),
		      registryName, enumTier );
		this.dyeColor = dyeColor;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean causesSuffocation(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
	
	@Override
	public void addCollisionBoxToList(final BlockState blockState, final @Nonnull World world, final @Nonnull BlockPos blockPos,
	                                  final @Nonnull AxisAlignedBB entityBox, final @Nonnull List<AxisAlignedBB> collidingBoxes,
	                                  final @Nullable Entity entity, final boolean isActualState) {
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return VoxelShapes.empty();
	}
}