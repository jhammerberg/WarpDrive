package cr0s.warpdrive.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class FakeChunk extends Chunk {
	
	private final World world;
	
	public FakeChunk(final World world, final ChunkPos chunkPos) {
		super(world, chunkPos, null);
		
		this.world = world;
	}
	
	@Override
	public int getHeight() {
		return 1;
	}
	
	@Nonnull
	@Override
	public BlockState getBlockState(@Nonnull final BlockPos blockPos) {
		return world.getBlockState(blockPos);
	}
	
	@Override
	public void addEntity(@Nonnull final Entity entity) {
		// no operation
	}
	
	@Override
	public void removeEntity(@Nonnull final Entity entity) {
		// no operation
	}
	
	@Override
	public void removeEntityAtIndex(@Nonnull final Entity entity, final int index) {
		// no operation
	}
	
	@Nullable
	@Override
	public TileEntity getTileEntity(@Nonnull final BlockPos blockPos, @Nonnull final CreateEntityType creationMode) {
		return world.getTileEntity(blockPos);
	}
	
	@Override
	public void addTileEntity(@Nonnull final TileEntity tileEntity) {
		// no operation
	}
	
	@Override
	public void addTileEntity(@Nonnull final BlockPos blockPos, @Nonnull final TileEntity tileEntity) {
		// no operation
	}
	
	@Override
	public void removeTileEntity(@Nonnull final BlockPos blockPos) {
		// no operation
	}
	
	@Override
	public void markDirty() {
		// no operation
	}
	
	@Override
	public boolean isModified() {
		return false;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean isEmptyBetween(final int startY, final int endY) {
		return false;
	}
}