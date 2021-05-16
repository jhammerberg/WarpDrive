package cr0s.warpdrive.world;

import javax.annotation.Nonnull;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class BiomeSpace extends Biome {
	
    public BiomeSpace(final String registryName) {
        super(new Biome.Builder()
		              .surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)
		              .precipitation(Biome.RainType.NONE)
		              .category(Category.NONE)
		              .depth(0.0F).scale(0.0F)
		              .temperature(2.0F).downfall(0.0F)
		              .waterColor(0x000000).waterFogColor(0x000000)
		              .parent(null) );
        carvers.clear();
	    features.clear();
        flowers.clear();
        structures.clear();
        
        setRegistryName(registryName);
    }
	
	@Override
	public void decorate(@Nonnull final Decoration stage, @Nonnull final ChunkGenerator<? extends GenerationSettings> chunkGenerator,
	                     @Nonnull final IWorld worldIn, final long seed, @Nonnull final SharedSeedRandom random, @Nonnull final BlockPos blockPos) {
		// no operation
	}
	
	@Override
	public float getSpawningChance() {
        return 0.0F;
    }
	
	@Override
	public boolean doesSnowGenerate(@Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos) {
		return false;
	}
}