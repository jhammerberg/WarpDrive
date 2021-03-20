package cr0s.warpdrive.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.Biomes;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorld extends World {
	
	private final FakeChunk fakeChunk;
	private final FakeTickList<Block> fakeTickListBlock;
	private final FakeTickList<Fluid> fakeTickListFluid;
	private final Scoreboard scoreboard;
	private BlockState blockState;
	private TileEntity tileEntity;
	
	
	public FakeWorld(final BlockState blockState, final boolean isRemote) {
		super(new WorldInfo(new CompoundNBT(), null, 1, null),
		      FakeDimensionType.INSTANCE,
		      (world, dimension) -> null,
		      null,
		      isRemote);
		fakeChunk = new FakeChunk(this, new ChunkPos(0, 0));
		fakeTickListBlock = new FakeTickList<>();
		fakeTickListFluid = new FakeTickList<>();
		scoreboard = new Scoreboard();
		this.blockState = blockState;
	}
	
	@Nonnull
	@Override
	public ITickList<Block> getPendingBlockTicks() {
		return fakeTickListBlock;
	}
	
	@Nonnull
	@Override
	public ITickList<Fluid> getPendingFluidTicks() {
		return fakeTickListFluid;
	}
	
	@Nullable
	@Override
	public AbstractChunkProvider getChunkProvider() {
		return null;
	}
	
	@Nonnull
	@Override
	public String getProviderName() {
		return "-null-";
	}
	
	@Nullable
	@Override
	public MapData getMapData(@Nonnull final String mapName) {
		return null;
	}
	
	@Override
	public void registerMapData(@Nonnull final MapData mapData) {
		// no operation
	}
	
	@Override
	public int getNextMapId() {
		return 0;
	}
	
	@Override
	public void sendBlockBreakProgress(final int breakerId, @Nonnull final BlockPos blockPos, final int progress) {
		// no operation
	}
	
	@Nonnull
	@Override
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	@Override
	public RecipeManager getRecipeManager() {
		return null;
	}
	
	@Override
	public NetworkTagManager getTags() {
		return null;
	}
	
	@Nonnull
	@Override
	public Biome getBiome(@Nonnull final BlockPos blockPos) {
		return Biomes.PLAINS;
	}
	
	@Nonnull
	@Override
	public Biome getNoiseBiomeRaw(final int x, final int y, final int z) {
		return Biomes.PLAINS;
	}
	
	@Override
	public boolean isBlockLoaded(@Nonnull final BlockPos blockPos) {
		return true;
	}
	
	@Override
	public boolean chunkExists(final int chunkX, final int chunkZ) {
		return true;
	}
	
	@Override
	public void playEvent(@Nullable final PlayerEntity entityPlayer, final int type, @Nonnull final BlockPos blockPos, final int data) {
		
	}
	
	@Nonnull
	@Override
	public Chunk getChunk(final int chunkX, final int chunkZ) {
		return fakeChunk;
	}
	
	@Nonnull
	@Override
	public BlockState getBlockState(@Nonnull final BlockPos blockPos) {
		if (blockPos == BlockPos.ZERO) {
			return blockState;
		}
		return Blocks.AIR.getDefaultState();
	}
	
	@Override
	public void playSound(@Nullable final PlayerEntity player, final double x, final double y, final double z,
	                      @Nonnull final SoundEvent soundEvent, @Nonnull final SoundCategory soundCategory, final float volume, final float pitch) {
		// no operation
	}
	
	@Override
	public void playMovingSound(@Nullable final PlayerEntity entityPlayer, @Nonnull final Entity entity,
	                            @Nonnull final SoundEvent soundEvent, @Nonnull final SoundCategory soundCategory, final float volume, final float pitch) {
		// no operation
	}
	
	@Override
	public boolean setBlockState(@Nonnull final BlockPos blockPos, @Nonnull final BlockState blockState) {
		if (blockPos == BlockPos.ZERO) {
			this.blockState = blockState;
			tileEntity = null;
		}
		return true;
	}
	
	@Override
	public void notifyBlockUpdate(@Nonnull final BlockPos blockPos, @Nonnull final BlockState oldState, @Nonnull final BlockState newState, final int flags) {
		// no operation
	}
	
	@Nullable
	@Override
	public TileEntity getTileEntity(@Nonnull final BlockPos blockPos) {
		if ( blockPos == BlockPos.ZERO
		  && blockState.getBlock().hasTileEntity(blockState) ) {
			if (tileEntity == null) {
				tileEntity = blockState.getBlock().createTileEntity(blockState, this);
				if (tileEntity != null) {
					tileEntity.setWorldAndPos(this, blockPos);
					tileEntity.validate();
				}
			}
			return tileEntity;
		}
		return null;
	}
	
	@Nullable
	@Override
	public Entity getEntityByID(int id) {
		return null;
	}
	
	@Override
	public long getGameTime() {
		return System.currentTimeMillis();
	}
	
	@Override
	public boolean canSeeSky(@Nonnull final BlockPos blockPos) {
		return true;
	}
	
	@Override
	public int getLight(@Nonnull final BlockPos blockPos) {
		return 0;
	}
	
	@Override
	public int getHeight() {
		return getSeaLevel() + 1;
	}
	
	@Override
	public int getLightFor(@Nonnull final LightType type, @Nonnull final BlockPos blockPos) {
		return type.defaultLightValue;
	}
	
	@Nonnull
	@Override
	public BlockPos getHeight(@Nonnull final Type heightmapType, @Nonnull final BlockPos blockPos) {
		return blockPos;
	}
	
	@Nonnull
	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return new ArrayList<>(0);
	}
}
