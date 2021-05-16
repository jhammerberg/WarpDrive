package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockHullGlass extends BlockAbstractHull implements IDamageReceiver {
	
	public BlockHullGlass(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final DyeColor dyeColor) {
		super(getDefaultProperties(Material.GLASS, dyeColor.getMapColor())
				.sound(SoundType.GLASS)
				.lightValue(10)
				.notSolid(),
		      registryName, enumTier, dyeColor);
	}
	
	@Override
	public boolean propagatesSkylightDown(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader,
	                                      @Nonnull final BlockPos blockPos) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(@Nonnull final BlockState blockState, @Nonnull final BlockState blockStateAdjacent, @Nonnull final Direction side) {
		return blockStateAdjacent.getBlock() == this
		    || super.isSideInvisible(blockState, blockStateAdjacent, side);
	}
	
	@Override
	public int applyDamage(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                       @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		if (damageLevel <= 0) {
			return 0;
		}
		if (enumTier == EnumTier.BASIC) {
			world.removeBlock(blockPos, false);
		} else {
			world.setBlockState(blockPos, WarpDrive.blockHulls_glass[enumTier.getIndex() - 1][indexColor]
			                              .getDefaultState(), 2);
		}
		return 0;
	}
}