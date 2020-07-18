package cr0s.warpdrive.block.breathing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockColorAirShield implements IBlockColor {
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public int getColor(@Nonnull final BlockState blockState, @Nullable final ILightReader lightReader, @Nullable final BlockPos blockPos, final int tintIndex) {
		switch (((BlockAirShield) blockState.getBlock()).dyeColor) { // getMaterialColor(lightReader, blockPos)) {
		case WHITE:
			return 0xFFFFFF;
		case ORANGE:
			return 0xFF5A02;
		case MAGENTA:
			return 0xF269FF;
		case LIGHT_BLUE:
			return 0x80AAFF;
		case YELLOW:
			return 0xFFEE3C;
		case LIME:
			return 0x90E801;
		case PINK:
			return 0xFB0680;
		case GRAY: // silver
			return 0x2C2C2C;
		case LIGHT_GRAY:
			return 0x686868;
		case CYAN:
		default: // SciFi cyan
			return 0x0FD7FF;
		case PURPLE:
			return 0x5D1072;
		case BLUE:
			return 0x4351CC;
		case BROWN:
			return 0x99572E;
		case GREEN:
			return 0x75993C;
		case RED:
			return 0xCC4d41;
		case BLACK:
			return 0x080808;
		}
	}
}