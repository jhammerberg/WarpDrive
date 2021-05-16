package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockElectromagnetGlass extends BlockElectromagnetPlain {
	
	public BlockElectromagnetGlass(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(Material.GLASS)
				      .notSolid(),
		      registryName, enumTier);
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
}