package cr0s.warpdrive.world;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class HyperSpaceDimension extends AbstractVoidDimension {
	
	public HyperSpaceDimension(@Nonnull final World world, @Nonnull DimensionType dimensionType) {
		super(world, dimensionType);
	}
	
	@Override
	public boolean canRespawnHere() {
		return true;
	}
	
	@Override
	public boolean isSurfaceWorld() {
		return true;
	}
	
	@Override
	public void setAllowedSpawnTypes(final boolean allowHostile, final boolean allowPeaceful) {
		super.setAllowedSpawnTypes(true, true);
	}
	
	@Override
	public float calculateCelestialAngle(final long time, final float partialTick) {
		// returns the clock angle: 0 is noon, 0.5 is midnight on the vanilla clock
		// daylight is required to enable IC2, NuclearCraft and EnderIO solar panels
		// we want no solar power in hyperspace => permanent midnight
		return 0.5F;
	}
	
	@Override
	public float getLightBrightness(int lightLevel) {
		return super.getLightBrightness(lightLevel);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSkyColored() {
		return true;
	}
	
	@Override
	public boolean isDaytime() {
		// true is required to enable GregTech solar boiler and Mekanism solar panels
		// we want no solar power in hyperspace => false
		return false;
	}
}