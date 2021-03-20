package cr0s.warpdrive;

import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;

public class FastSetBlockState {
	
	// This code is a straight copy from Vanilla net.minecraft.world.World.setBlockState to remove lighting computations
	public static boolean setBlockStateNoLight(@Nonnull final World world, @Nonnull final BlockPos blockPosPassed, @Nonnull final BlockState blockStateNew, final int flags) {
		assert !world.captureBlockSnapshots;
		if (!Commons.isSafeThread()) {
			throw new ConcurrentModificationException(String.format("setBlockstate %s to %s 0x%x",
			                                                        Commons.format(world, blockPosPassed), blockStateNew, flags));
		}
		
		if (!WarpDriveConfig.G_ENABLE_FAST_SET_BLOCKSTATE) {
			return world.setBlockState(blockPosPassed, blockStateNew, flags);
		}
		
		if (World.isOutsideBuildHeight(blockPosPassed)) {
			return false;
		} else if (!world.isRemote() && world.getWorldInfo().getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
			return false;
		} else {
			final Chunk chunk = world.getChunkAt(blockPosPassed);
			
			/*
			final BlockPos blockPos = blockPosPassed.toImmutable(); // Forge - prevent mutable BlockPos leaks
			BlockSnapshot blockSnapshot = null;
			if (world.captureBlockSnapshots && !world.isRemote()) {
				blockSnapshot = BlockSnapshot.getBlockSnapshot(world, blockPos, flags);
				world.capturedBlockSnapshots.add(blockSnapshot);
			}
			final BlockState blockStateOld = world.getBlockState(blockPos);
			final int lightOld = blockStateOld.getLightValue(world, blockPos);
			final int opacityOld = blockStateOld.getLightOpacity(world, blockPos);
			/**/
			final BlockPos blockPos = blockPosPassed instanceof BlockPos.Mutable ? blockPosPassed.toImmutable() : blockPosPassed; // Forge - prevent mutable BlockPos leaks
			
			
			// final BlockState blockStateEffective = chunk.setBlockState(blockPos, blockStateNew, (flags & 64) != 0);
			final BlockState blockStateEffective = chunk_setBlockState(chunk, blockPos, blockStateNew, (flags & 64) != 0);
			
			if (blockStateEffective == null) {
				/*
                if (blockSnapshot != null) {
                    this.capturedBlockSnapshots.remove(blockSnapshot);
                }
				/**/
				return false;
			} else {
				/*
				BlockState blockstate1 = world.getBlockState(blockPos);
				if ( blockstate1 != blockStateEffective
				  && ( blockstate1.getOpacity(world, blockPos) != opacityOld
				    || blockstate1.getLightValue(world, blockPos) != lightOld
				    || blockstate1.isTransparent()
				    || blockStateEffective.isTransparent())) {
					world.profiler.startSection("queueCheckLight");
					world.getChunkProvider().getLightManager().checkBlock(blockPos);
					world.profiler.endSection();
				}
				
				if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
					world.markAndNotifyBlock(blockPos, chunk, blockStateEffective, blockStateNew, flags);
				}
				/**/
				return true;
			}
		}
	}
	
	// This code is a straight copy from Vanilla net.minecraft.world.chunk.Chunk.setBlockState to remove lighting computations
	@Nullable
	public static BlockState chunk_setBlockState(@Nonnull final Chunk chunk, final BlockPos pos, final BlockState state, boolean isMoving) {
		// report properties as locals
		final World world = chunk.getWorld();
		final ChunkSection[] sections = chunk.getSections();
		
		final int i = pos.getX() & 15;
		final int j = pos.getY();
		final int k = pos.getZ() & 15;
		ChunkSection chunksection = sections[j >> 4];
		if (chunksection == Chunk.EMPTY_SECTION) {
			if (state.isAir()) {
				return null;
			}
			
			chunksection = new ChunkSection(j >> 4 << 4);
			sections[j >> 4] = chunksection;
		}
		
		boolean flag = chunksection.isEmpty();
		BlockState blockstate = chunksection.setBlockState(i, j & 15, k, state);
		if (blockstate == state) {
			return null;
		} else {
			final Block block = state.getBlock();
			final Block block1 = blockstate.getBlock();
			chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, state);
			chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, state);
			chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, state);
			chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).update(i, j, k, state);
			boolean flag1 = chunksection.isEmpty();
			if (flag != flag1) {
				world.getChunkProvider().getLightManager().func_215567_a(pos, flag1);
			}
			
			if (!world.isRemote) {
				blockstate.onReplaced(world, pos, state, isMoving);
			} else if ((block1 != block || !state.hasTileEntity()) && blockstate.hasTileEntity()) {
				world.removeTileEntity(pos);
			}
			
			if (chunksection.getBlockState(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (blockstate.hasTileEntity()) {
					TileEntity tileentity = chunk.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
					if (tileentity != null) {
						tileentity.updateContainingBlockInfo();
					}
				}
				
				if (!world.isRemote) {
					state.onBlockAdded(world, pos, blockstate, isMoving);
				}
				
				if (state.hasTileEntity()) {
					TileEntity tileentity1 = chunk.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
					if (tileentity1 == null) {
						tileentity1 = state.createTileEntity(world);
						world.setTileEntity(pos, tileentity1);
					} else {
						tileentity1.updateContainingBlockInfo();
					}
				}
				
				chunk.markDirty();
				return blockstate;
			}
		}
	}
}
