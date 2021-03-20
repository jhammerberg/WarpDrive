package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.block.BlockAbstractOmnipanel;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockAirShield extends BlockAbstractOmnipanel {
	
	protected final DyeColor dyeColor;
	
	public BlockAirShield(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final DyeColor dyeColor) {
		super(getDefaultProperties(Material.WOOL)
				.doesNotBlockMovement(),
		      registryName, enumTier );
		this.dyeColor = dyeColor;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean causesSuffocation(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
}