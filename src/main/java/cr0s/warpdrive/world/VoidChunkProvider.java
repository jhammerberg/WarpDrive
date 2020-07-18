package cr0s.warpdrive.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VoidChunkProvider extends ChunkGenerator<GenerationSettings> {
	
	private final List<Biome.SpawnListEntry> listSpawn = new ArrayList<>(0);
	
	public VoidChunkProvider(final World world, final BiomeProvider biomeProvider, final GenerationSettings generationSettings) {
		super(world, biomeProvider, generationSettings);
	}
	
	@Override
	public void generateCarvers(@Nonnull final BiomeManager biomeManager, @Nonnull final IChunk chunk, @Nonnull final Carving carvingStage) {
		// no operation
	}
	
	@Override
	public void generateStructureStarts(@Nonnull final IWorld world, @Nonnull final IChunk chunk) {
		// no operation
	}
	
	@Override
	public void generateStructures(@Nonnull final BiomeManager biomeManager, @Nonnull final IChunk chunk,
	                               @Nonnull final ChunkGenerator<?> chunkGenerator, @Nonnull final TemplateManager templateManager) {
		// no operation
	}
	
	@Override
	public void makeBase(@Nonnull final IWorld world, @Nonnull final IChunk chunk) {
		
	}
	
	@Override
	public int getHeight(final int x, final int z, @Nonnull final Type heightmapType) {
		return 0;
	}
	
	@Override
	public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull final EntityClassification creatureType, @Nonnull final BlockPos blockPos) {
		return listSpawn;
	}
	
	@Nullable
	@Override
	public BlockPos findNearestStructure(@Nonnull final World world, @Nonnull final String structureName,
	                                     @Nonnull final BlockPos blockPos, final int radius, final boolean skipExistingChunks) {
		return null;
	}
	
	@Override
	public void generateSurface(@Nonnull final WorldGenRegion worldGenRegion, @Nonnull final IChunk chunk) {
		
	}
	
	@Override
	public int getGroundHeight() {
		return 0;
	}
}