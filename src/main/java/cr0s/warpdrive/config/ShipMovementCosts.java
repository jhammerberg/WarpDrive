package cr0s.warpdrive.config;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.EventWarpDrive.Ship.MovementCosts;
import cr0s.warpdrive.api.computer.IShipController;
import cr0s.warpdrive.data.EnumShipMovementType;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.MinecraftForge;

public class ShipMovementCosts {
	
	public final int maximumDistance_blocks;
	public final int energyRequired;
	public final int warmup_seconds;
	public final int sickness_seconds;
	public final int cooldown_seconds;
	
	public ShipMovementCosts(final World world, final BlockPos blockPos, 
	                         final IShipController shipController, final EnumShipMovementType shipMovementType,
	                         final int mass, final int distance) {
		final Factors factorsForJumpParameters = WarpDriveConfig.SHIP_MOVEMENT_COSTS_FACTORS[shipMovementType.ordinal()];
		final int maximumDistance_blocks = Commons.clamp(0, 30000000, evaluate(mass, distance, factorsForJumpParameters.maximumDistance));
		final int energyRequired   = Commons.clamp(0, Integer.MAX_VALUE, evaluate(mass, distance, factorsForJumpParameters.energyRequired));
		final int warmup_seconds   = Commons.clamp(0, 3600, evaluate(mass, distance, factorsForJumpParameters.warmup));
		final int sickness_seconds = Commons.clamp(0, 3600, evaluate(mass, distance, factorsForJumpParameters.sickness));
		final int cooldown_seconds = Commons.clamp(0, 3600, evaluate(mass, distance, factorsForJumpParameters.cooldown));
		
		// post event allowing other mods to adjust it
		final MovementCosts movementCosts = new MovementCosts(world, blockPos,
		                                                      shipController, shipMovementType.getName(), mass, distance,
		                                                      maximumDistance_blocks, energyRequired, warmup_seconds, sickness_seconds, cooldown_seconds);
		MinecraftForge.EVENT_BUS.post(movementCosts);
		
		this.maximumDistance_blocks = movementCosts.getMaximumDistance_blocks();
		this.energyRequired   = movementCosts.getEnergyRequired();
		this.warmup_seconds   = movementCosts.getWarmup_seconds();
		this.sickness_seconds = movementCosts.getSickness_seconds();
		this.cooldown_seconds = movementCosts.getCooldown_seconds();
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(String.format("Ship movement %s with mass %d over %d blocks is capped to %d blocks, will cost %d EU, %d s warm-up, %d s sickness, %d s cool down",
			                                    shipMovementType, mass, distance,
			                                    this.maximumDistance_blocks, this.energyRequired,
			                                    this.warmup_seconds, this.sickness_seconds, this.cooldown_seconds));
		}
	}
	
	private static int evaluate(final int mass, final int distance, final double[] factors) {
		if (factors.length != 5) {
			return Integer.MAX_VALUE;
		}
		final double value = factors[0]
		                   + factors[1] * mass
		                   + factors[2] * distance
		                   + factors[3] * Math.log(Math.max(1.0D, mass)) * (factors[4] != 0.0D ? Math.exp(distance / factors[4]) : 1.0D);
		return (int) Math.ceil(value);
	}
	
	public static class Factors {
		
		private ConfigValue<double[]> config_maximumDistance;
		private ConfigValue<double[]> config_energyRequired;
		private ConfigValue<double[]> config_warmup;
		private ConfigValue<double[]> config_sickness;
		private ConfigValue<double[]> config_cooldown;
		
		public double[] maximumDistance;
		public double[] energyRequired;
		public double[] warmup;
		public double[] sickness;
		public double[] cooldown;
		
		Factors(final double[] maximumDistanceDefault,
		        final double[] energyRequiredDefault,
		        final double[] warmupDefault,
		        final double[] sicknessDefault,
		        final double[] cooldownDefault) {
			maximumDistance = maximumDistanceDefault;
			energyRequired = energyRequiredDefault;
			warmup = warmupDefault;
			sickness = sicknessDefault;
			cooldown = cooldownDefault;
		}
		
		public void build(@Nonnull final ForgeConfigSpec.Builder builder, final String prefixKey, final String comment) {
			config_maximumDistance = builder
					.comment("Maximum jump length value in blocks " + comment + ".")
					.translation("warpdrive.config.ship_movement_costs." + prefixKey + "_max_jump_distance")
					.define(prefixKey + "_max_jump_distance", maximumDistance);
			config_energyRequired = builder
					.comment("Energy required measured in internal units " + comment + ".")
					.translation("warpdrive.config.ship_movement_costs." + prefixKey + "_energyRequired_factors")
					.define(prefixKey + "_energyRequired_factors", energyRequired);
			config_warmup = builder
					.comment("Warmup seconds to wait before starting jump " + comment + ".")
					.translation("warpdrive.config.ship_movement_costs." + prefixKey + "_warmup_seconds")
					.define(prefixKey + "_warmup_seconds", warmup);
			config_sickness = builder
					.comment("Motion sickness duration measured in seconds " + comment + ".")
					.translation("warpdrive.config.ship_movement_costs." + prefixKey + "_sickness_seconds")
					.define(prefixKey + "_sickness_seconds", sickness);
			config_cooldown = builder
					.comment("Cooldown seconds to wait after jumping " + comment + ".")
					.translation("warpdrive.config.ship_movement_costs." + prefixKey + "_cooldown_interval_seconds")
					.define(prefixKey + "_cooldown_interval_seconds", cooldown);
		}
		
		public void load() {
			maximumDistance = config_maximumDistance.get();			
			energyRequired = config_energyRequired.get();
			warmup = config_warmup.get();
			sickness = config_sickness.get();
			cooldown = config_cooldown.get();
		}
	}
}
