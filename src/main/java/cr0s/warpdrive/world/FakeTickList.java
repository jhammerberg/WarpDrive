package cr0s.warpdrive.world;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;

class FakeTickList<T> implements ITickList<T> {
	
	@Override
	public boolean isTickScheduled(@Nonnull final BlockPos blockPos, @Nonnull final T item) {
		return false;
	}
	
	@Override
	public void scheduleTick(@Nonnull final BlockPos blockPos, @Nonnull final T item, final int scheduledTime, @Nonnull final TickPriority tickPriority) {
		// no operation
	}
	
	@Override
	public boolean isTickPending(@Nonnull final BlockPos blockPos, @Nonnull final T item) {
		return false;
	}
	
	@Override
	public void addAll(@Nonnull final Stream<NextTickListEntry<T>> stream) {
		// no operation
	}
}
