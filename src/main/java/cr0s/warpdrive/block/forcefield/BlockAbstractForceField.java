package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.Block;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class BlockAbstractForceField extends BlockAbstractContainer {
	
	BlockAbstractForceField(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties,
		      registryName, enumTier);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
	
	@Override
	public void onEMP(@Nonnull final World world, @Nonnull final BlockPos blockPos, final float efficiency) {
		super.onEMP(world, blockPos, efficiency * (1.0F - 0.2F * (enumTier.getIndex() - 1)));
	}
}
