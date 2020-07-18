package cr0s.warpdrive.world;

import javax.annotation.Nonnull;

import java.util.function.BiFunction;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager.IBiomeReader;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.OverworldDimension;

public class FakeDimensionType extends DimensionType {
	
	private static final BiomeMagnifierPlains biomeMagnifierPlains = new BiomeMagnifierPlains();
	public static final FakeDimensionType INSTANCE = new FakeDimensionType();
	
	protected FakeDimensionType() {
		super(0, "fakeWorld", "fakeFolder",
		      OverworldDimension::new, false, biomeMagnifierPlains);
	}
	
	// constructor just for parameters reference
	protected FakeDimensionType(final int id, final String suffix, final String directory,
	                            final BiFunction<World, DimensionType, ? extends Dimension> factory,
	                            final boolean hasSkyLight, final IBiomeMagnifier biomeMagnifier) {
		super(id, suffix, directory, factory, hasSkyLight, biomeMagnifier);
	}
	
	static class BiomeMagnifierPlains implements IBiomeMagnifier {
		
		@Nonnull
		@Override
		public Biome getBiome(final long seed, final int x, final int y, final int z, @Nonnull final IBiomeReader biomeReader) {
			return Biomes.PLAINS;
		}
	}
}
