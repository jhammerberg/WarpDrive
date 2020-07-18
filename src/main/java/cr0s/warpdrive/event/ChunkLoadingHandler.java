package cr0s.warpdrive.event;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

public class ChunkLoadingHandler {
	
	/* Forge wrappers */
	public static void registerTicket(@Nonnull final ServerWorld world, @Nonnull final BlockPos blockPos, final int range) {
		registerTicket(world, new ChunkPos(blockPos), range);
	}
	public static void registerTicket(@Nonnull final ServerWorld world, @Nonnull final ChunkPos chunkPos, final int range) {
		world.getChunkProvider().registerTicket(TicketType.FORCED, chunkPos, range, chunkPos);
		
		if (WarpDriveConfig.LOGGING_CHUNK_LOADING) {
			WarpDrive.logger.info(String.format("Forcing chunk loading %s",
			                                    Commons.format(world, chunkPos.asBlockPos()) ));
		}
	}
	
	public static void releaseTicket(@Nonnull final ServerWorld world, @Nonnull final BlockPos blockPos, final int range) {
		releaseTicket(world, new ChunkPos(blockPos), range);
	}
	public static void releaseTicket(@Nonnull final ServerWorld world, @Nonnull final ChunkPos chunkPos, final int range) {
		world.getChunkProvider().releaseTicket(TicketType.FORCED, chunkPos, range, chunkPos);
		
		if (WarpDriveConfig.LOGGING_CHUNK_LOADING) {
			WarpDrive.logger.info(String.format("Un-forcing chunk loading %s",
			                                    Commons.format(world, chunkPos.asBlockPos()) ));
		}
	}
}