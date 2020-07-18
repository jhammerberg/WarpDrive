package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockBedrockGlass extends BlockAbstractBase {
	
	public BlockBedrockGlass(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(Material.FIRE)
				.hardnessAndResistance(-1.0F, 6000000.0F)
				.sound(SoundType.STONE)
		      .noDrops(),
		      registryName, enumTier);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean causesSuffocation(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
	
	@Override
	public boolean isAir(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isReplaceable(@Nonnull final BlockState blockState, @Nonnull final BlockItemUseContext blockItemUseContext) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
}