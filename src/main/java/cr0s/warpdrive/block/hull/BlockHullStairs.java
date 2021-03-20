package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumHullPlainType;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHullStairs extends StairsBlock implements IBlockBase, IDamageReceiver {
	
	protected final EnumTier enumTier;
	final EnumHullPlainType  hullPlainType;
	private final int        indexColor;
	
	public BlockHullStairs(@Nonnull final String registryName, @Nonnull final BlockState blockStateHull) {
		super(() -> blockStateHull, Block.Properties.from(blockStateHull.getBlock()));
		
		this.enumTier      = ((BlockHullPlain) blockStateHull.getBlock()).getTier();
		this.hullPlainType = ((BlockHullPlain) blockStateHull.getBlock()).hullPlainType;
		this.indexColor    = ((BlockHullPlain) blockStateHull.getBlock()).indexColor;
		setRegistryName(registryName);
		WarpDrive.register(this, new ItemBlockHull(this));
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
	
	@Nonnull
	@Override
	public EnumTier getTier() {
		return enumTier;
	}
	
	@Nonnull
	@Override
	public Rarity getRarity() {
		return getTier().getRarity();
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockHull(this);
	}
	
	@Override
	public float getBlockHardness(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                              @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		// TODO: adjust hardness to damage type/color
		return WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()];
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
			world.setBlockState(blockPos, WarpDrive.blockHulls_stairs[enumTier.getIndex() - 1][hullPlainType.ordinal()][indexColor]
			                              .getDefaultState()
			                              .with(FACING, blockState.get(FACING)), 2);
		}
		return 0;
	}
}