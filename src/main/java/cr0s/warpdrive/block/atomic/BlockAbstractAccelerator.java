package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public abstract class BlockAbstractAccelerator extends BlockAbstractBase implements IBlockBase {
	
	BlockAbstractAccelerator(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nullable final Material material) {
		super(getDefaultProperties(material)
				      .hardnessAndResistance(4 + enumTier.getIndex(), 2 + 2 * enumTier.getIndex()),
		      registryName, enumTier);
	}
	
	@Override
	public boolean canCreatureSpawn(@Nonnull final BlockState state, @Nonnull final IBlockReader world, @Nonnull final BlockPos pos,
	                                final PlacementType type, @Nullable final EntityType<?> entityType) {
		return false;
	}
}
