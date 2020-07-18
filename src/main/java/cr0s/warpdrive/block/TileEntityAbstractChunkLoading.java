package cr0s.warpdrive.block;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.event.ChunkLoadingHandler;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

public abstract class TileEntityAbstractChunkLoading extends TileEntityAbstractEnergyConsumer {
	
	// persistent properties
	protected int range_requested = 0;
	
	// computed properties
	private int range_registered = -1;
	private boolean isRefreshNeeded = true;
	
	TileEntityAbstractChunkLoading(@Nonnull TileEntityType<? extends TileEntityAbstractChunkLoading> tileEntityType) {
		super(tileEntityType);
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		if (range_requested < 0) {
			WarpDrive.logger.warn(String.format("%s No range defined, assuming current chunk",
			                                    this ));
			range_requested = 0;
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		final boolean shouldChunkLoad = shouldChunkLoad();
		if ( isRefreshNeeded
		  || shouldChunkLoad != areChunksLoaded() ) {
			refreshLoading(isRefreshNeeded, shouldChunkLoad);
			isRefreshNeeded = false;
		}
	}
	
	public abstract boolean shouldChunkLoad();
	
	public final boolean areChunksLoaded() {
		return range_registered >= 0;
	}
	
	public void refreshChunkLoading() {
		isRefreshNeeded = true;
	}
	
	private void refreshLoading(final boolean isRefreshNeeded, final boolean shouldChunkLoad ) {
		assert world != null;
		
		// skip if there's no explicit change
		if ( range_requested == range_registered
		  && !isRefreshNeeded ) {
			return;
		}
		
		if (shouldChunkLoad) {
			if (areChunksLoaded()) {
				ChunkLoadingHandler.releaseTicket((ServerWorld) world, pos, range_registered);
				range_registered = -1;
			}
			ChunkLoadingHandler.registerTicket((ServerWorld) world, pos, range_requested);
			range_registered = range_requested;
			
		} else if (areChunksLoaded()) {
			ChunkLoadingHandler.releaseTicket((ServerWorld) world, pos, range_registered);
			range_registered = -1;
		}
	}
	
	public int chunkloading_getArea() {
		return (range_requested + 1)
		     * (range_requested + 1);
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		tagCompound.putInt("range", range_requested);
		return tagCompound;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		if (tagCompound.contains("minChunkX")) {
			range_requested = tagCompound.getInt("range");
		}
	}
	
	@Override
	public void remove() {
		if (areChunksLoaded()) {
			assert world != null;
			ChunkLoadingHandler.releaseTicket((ServerWorld) world, pos, range_registered);
			range_registered = -1;
		}
		super.remove();
	}
}
