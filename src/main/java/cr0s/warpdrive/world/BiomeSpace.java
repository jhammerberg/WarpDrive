package cr0s.warpdrive.world;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;

public class BiomeSpace extends Biome {
	
    public BiomeSpace(final Biome.Builder biomeBuilder) {
        super(biomeBuilder);
        
        carvers.clear();
	    features.clear();
        flowers.clear();
        structures.clear();
        
        setRegistryName("Space");
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
