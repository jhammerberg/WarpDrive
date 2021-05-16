package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.event.ChunkHandler;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

class CloakScanner {
	
	// inputs
	private final World world;
	private final int minX, minY, minZ;
	private final int maxX, maxY, maxZ;
	private final boolean isFullyTransparent;
	
	// execution
	private int x;
	private int y;
	private int z;
	private int xChunk;
	private int zChunk;
	private long indexChunk;
	private int volumeChunk;
	private final BlockPos.Mutable mutableBlockPos;
	
	// output
	public final Long2IntOpenHashMap chunkIndexVolume;
	
	CloakScanner(@Nonnull final World world,
	             final int minX, final int minY, final int minZ,
	             final int maxX, final int maxY, final int maxZ,
	             final boolean isFullyTransparent,
	             final boolean doLoadChunks) {
		this.world = world;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.isFullyTransparent = isFullyTransparent;
		
		// fix a list of chunks to scan so we get proper values in case a chunk gets loaded or unloaded during the scan
		final int minChunkX = minX >> 4;
		final int maxChunkX = maxX >> 4;
		final int minChunkZ = minZ >> 4;
		final int maxChunkZ = maxZ >> 4;
		chunkIndexVolume = new Long2IntOpenHashMap((maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1));
		for(xChunk = minChunkX; xChunk <= maxChunkX; xChunk++) {
			for(zChunk = minChunkZ; zChunk <= maxChunkZ; zChunk++) {
				if ( doLoadChunks
				  || ChunkHandler.isLoaded(world, xChunk, zChunk) ) {
					indexChunk = ChunkPos.asLong(xChunk, zChunk);
					chunkIndexVolume.put(indexChunk, 0);
				}
			}
		}
		
		// initialize the loop state
		x = this.minX;
		y = this.minY;
		z = this.minZ;
		xChunk = x >> 4;
		zChunk = z >> 4;
		indexChunk = ChunkPos.asLong(xChunk, zChunk);
		volumeChunk = chunkIndexVolume.getOrDefault(indexChunk, -1);
		mutableBlockPos = new BlockPos.Mutable(x, y, z);
	}
	
	public boolean tick() {
		int countBlocks = 0;
		
		try {
			while (countBlocks < WarpDriveConfig.CLOAKING_VOLUME_SCAN_BLOCKS_PER_TICK) {
				if (volumeChunk < 0) {
					// first chunk isn't in our list, skip it
					y = maxY;
				} else {
					mutableBlockPos.setPos(x, y, z);
					final BlockState blockState = world.getBlockState(mutableBlockPos);
					countBlocks++;
					
					if (isFullyTransparent) {
						if (blockState.getBlock() != Blocks.AIR) {
							volumeChunk++;
						}
					} else {
						if (!blockState.getBlock().isAir(blockState, world, mutableBlockPos)) {
							volumeChunk++;
						}
					}
				}
				
				// iterate y first to stay in same chunk, then z, then x
				y++;
				if (y > maxY) {
					y = minY;
					// iterate z and x while we're in unlisted chunks
					do {
						z++;
						if (z > maxZ) {
							z = minZ;
							x++;
							if (x > maxX) {
								return true;
							}
							xChunk = x >> 4;
						}
						zChunk = z >> 4;
						
						// update chunk data as needed
						final long indexChunkNew = ChunkPos.asLong(xChunk, zChunk);
						if (indexChunkNew != indexChunk) {
							if (volumeChunk > 0) {
								chunkIndexVolume.put(indexChunk, volumeChunk);
							}
							indexChunk = indexChunkNew;
							volumeChunk = chunkIndexVolume.getOrDefault(indexChunk, -1);
						}
					} while(volumeChunk < 0);
				}
			}
		} catch (final Exception exception) {
			exception.printStackTrace(WarpDrive.printStreamError);
			WarpDrive.logger.error(String.format("Exception was encountered around %s",
			                                     Commons.format(world, x, y, z) ));
		}
		return false;
	}
}
