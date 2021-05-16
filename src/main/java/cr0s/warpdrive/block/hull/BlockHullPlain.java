package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.data.EnumHullPlainType;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHullPlain extends BlockAbstractHull implements IDamageReceiver {
	
	final EnumHullPlainType hullPlainType;
	
	public BlockHullPlain(@Nonnull final String registryName,
	                      @Nonnull final EnumTier enumTier, @Nonnull final EnumHullPlainType hullPlainType, @Nonnull final DyeColor dyeColor) {
		super(getDefaultProperties(Material.ROCK, dyeColor.getMapColor())
				      .sound(SoundType.STONE)
				      .lightValue(10),
		      registryName, enumTier, dyeColor);
		
		this.hullPlainType = hullPlainType;
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
			world.setBlockState(blockPos, WarpDrive.blockHulls_plain[enumTier.getIndex() - 1][hullPlainType.ordinal()][indexColor]
			                              .getDefaultState(), 2);
		}
		return 0;
	}
}