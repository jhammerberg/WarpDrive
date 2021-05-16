package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

public abstract class BlockAbstractHull extends BlockAbstractBase implements IDamageReceiver {
	
	protected final int     indexColor;
	
	public BlockAbstractHull(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName,
	                         @Nonnull final EnumTier enumTier, @Nonnull final DyeColor dyeColor) {
		super(blockProperties
				      .hardnessAndResistance(WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()],
				                             WarpDriveConfig.HULL_BLAST_RESISTANCE[enumTier.getIndex()])
				      .harvestTool(ToolType.PICKAXE)
				      .harvestLevel(WarpDriveConfig.HULL_HARVEST_LEVEL[enumTier.getIndex()]),
		      registryName, enumTier);
		
		this.indexColor = dyeColor.getId();
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockHull(this);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canEntitySpawn(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader,
	                              @Nonnull final BlockPos blockPos, @Nonnull final EntityType<?> entityType) {
		return false;
	}
	
	@Override
	public float getBlockHardness(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                              @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		// TODO: adjust hardness to damage type/color
		return WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()];
	}
}