package cr0s.warpdrive.world;

import cr0s.warpdrive.FastSetBlockState;
import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.Filler;
import cr0s.warpdrive.config.GenericSet;
import cr0s.warpdrive.config.structures.OrbInstance;
import cr0s.warpdrive.data.JumpBlock;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/*
 2014-06-07 21:41:45 [Infos] [STDOUT] Generating star (class 0) at -579 257 1162
 2014-06-07 21:41:45 [Infos] [Minecraft-Client] [CHAT] /generate: generating star at -579, 257, 1162
 2014-06-07 21:41:45 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:41:45 [Infos] [STDOUT] [ESG] Saved 310248 blocks
 2014-06-07 21:41:45 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 95.646ms, total: 95.646ms
 2014-06-07 21:41:45 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:41:45 [Infos] [STDOUT] [ESG] Saved 23706 blocks
 2014-06-07 21:41:45 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 15.427ms, total: 15.427ms

 2014-06-07 21:42:03 [Infos] [STDOUT] Generating star (class 1) at -554 257 1045
 2014-06-07 21:42:03 [Infos] [Minecraft-Client] [CHAT] /generate: generating star at -554, 257, 1045
 2014-06-07 21:42:03 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:42:03 [Infos] [STDOUT] [ESG] Saved 1099136 blocks
 2014-06-07 21:42:03 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 37.404ms, total: 37.404ms
 2014-06-07 21:42:03 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:42:03 [Infos] [STDOUT] [ESG] Saved 50646 blocks
 2014-06-07 21:42:03 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 34.369ms, total: 34.369ms

 2014-06-07 21:42:39 [Infos] [STDOUT] Generating star (class 2) at -404 257 978
 2014-06-07 21:42:39 [Infos] [Minecraft-Client] [CHAT] /generate: generating star at -404, 257, 978
 2014-06-07 21:42:39 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:42:39 [Infos] [STDOUT] [ESG] Saved 2144432 blocks
 2014-06-07 21:42:39 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 85.523ms, total: 85.523ms
 2014-06-07 21:42:39 [Infos] [STDOUT] [ESG] Saving blocks...
 2014-06-07 21:42:40 [Infos] [STDOUT] [ESG] Saved 76699 blocks
 2014-06-07 21:42:40 [Infos] [STDOUT] [PROF] {EntitySphereGen.saveSphereBlocks} self: 9.286ms, total: 9.286ms

 */
public final class EntitySphereGen extends Entity {
	
	public static final EntityType<EntitySphereGen> TYPE;
	
	// persistent properties
	// (none)
	
	// computed properties
	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	private int radius;
	private BlockState blockStateGas;
	
	private static final int BLOCKS_PER_TICK = 5000;
	
	private static final int STATE_SAVING = 0;
	private static final int STATE_SETUP = 1;
	private static final int STATE_DELETE = 2;
	private static final int STATE_STOP = 3;
	private int state = STATE_DELETE;
	private int ticksDelay = 0;
	
	private int currentIndex = 0;
	private int pregenSize = 0;
	
	private ArrayList<JumpBlock> blocks;
	private ArrayList<Boolean> isSurfaces;
	private OrbInstance orbInstance;
	private boolean replace;
	
	static {
		TYPE = EntityType.Builder.<EntitySphereGen>create(EntitySphereGen::new, EntityClassification.MISC)
				       .setTrackingRange(200)
				       .setUpdateInterval(1)
				       .setShouldReceiveVelocityUpdates(false)
				       .disableSummoning()
				       .build("entity_sphere_generator");
		TYPE.setRegistryName(WarpDrive.MODID, "entity_sphere_generator");
	}
	
	public EntitySphereGen(@Nonnull final EntityType<EntitySphereGen> entityType, @Nonnull final World world) {
		super(entityType, world);
	}
	
	public EntitySphereGen(final World world, final int x, final int y, final int z,
	                       final OrbInstance orbInstance, final boolean replace) {
		super(TYPE, world);
		
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.setPosition(x, y, z);
		this.orbInstance = orbInstance;
		this.blockStateGas = WarpDrive.blockGas[world.rand.nextInt(12)].getDefaultState();
		this.replace = replace;
		
		constructionFinalizer();
	}
	
	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
	
	public void killEntity() {
		this.state = STATE_STOP;
		final int minY_clamped = Math.max(0, yCoord - radius);
		final int maxY_clamped = Math.min(255, yCoord + radius);
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
		for (int x = xCoord - radius; x <= xCoord + radius; x++) {
			for (int z = zCoord - radius; z <= zCoord + radius; z++) {
				for (int y = minY_clamped; y <= maxY_clamped; y++) {
					mutableBlockPos.setPos(x, y, z);
					final BlockState blockState = world.getBlockState(mutableBlockPos);
					if (blockState.getBlock() != Blocks.AIR) {
						world.notifyBlockUpdate(mutableBlockPos, blockState, blockState, 3);
					}
				}
			}
		}
		remove(false);
	}
	
	@Override
	public void tick() {
		if (world.isRemote()) {
			return;
		}
		 
		if (ticksDelay > 0) {
			ticksDelay--;
			return;
		}
		
		switch (state) {
		case STATE_SAVING:
			tickScheduleBlocks();
			this.state = STATE_SETUP;
			break;
		
		case STATE_SETUP:
			if (currentIndex >= blocks.size() - 1)
				this.state = STATE_DELETE;
			else
				tickPlaceBlocks();
			break;
		
		case STATE_DELETE:
			currentIndex = 0;
			killEntity();
			break;
		
		default:
			WarpDrive.logger.error(String.format("%s Invalid state %s. Killing entity...",
			                                     this, state));
			killEntity();
			break;
		}
	}
	
	private void tickPlaceBlocks() {
		final int blocksToMove = Math.min(BLOCKS_PER_TICK, blocks.size() - currentIndex);
		LocalProfiler.start("[EntitySphereGen] Placing blocks from " + currentIndex + " to " + (currentIndex + blocksToMove) + "/" + blocks.size());
		
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
		for (int index = 0; index < blocksToMove; index++) {
			if (currentIndex >= blocks.size())
				break;
			final JumpBlock jumpBlock = blocks.get(currentIndex);
			mutableBlockPos.setPos(jumpBlock.x, jumpBlock.y, jumpBlock.z);
			if (isSurfaces.get(currentIndex) && jumpBlock.x % 4 == 0 && jumpBlock.z % 4 == 0) {
				world.setBlockState(mutableBlockPos, jumpBlock.blockState, 2);
			} else {
				FastSetBlockState.setBlockStateNoLight(world, mutableBlockPos, jumpBlock.blockState, 2);
			}
			currentIndex++;
		}
		
		LocalProfiler.stop();
	}
	
	private void tickScheduleBlocks() {
		LocalProfiler.start("[EntitySphereGen] Saving blocks, radius " + radius);
		
		// square radius from center of block
		final double sqRadiusHigh = (radius + 0.5D) * (radius + 0.5D);
		final double sqRadiusLow = (radius - 0.5D) * (radius - 0.5D);
		
		// sphere
		final int ceilRadius = radius + 1;
		
		// Pass the cube and check points for sphere equation x^2 + y^2 + z^2 = r^2
		for (int x = 0; x <= ceilRadius; x++) {
			final double x2 = (x + 0.5D) * (x + 0.5D);
			for (int y = 0; y <= ceilRadius; y++) {
				final double x2y2 = x2 + (y + 0.5D) * (y + 0.5D);
				for (int z = 0; z <= ceilRadius; z++) {
					final double dSqRange = x2y2 + (z + 0.5D) * (z + 0.5D); // Square distance from current position to center
					
					// Skip too far blocks
					if (dSqRange > sqRadiusHigh) {
						continue;
					}
					final boolean isSurface = dSqRange > sqRadiusLow;
					
					// Add blocks to memory
					final int intSqRadius = (int) Math.round(dSqRange);
					final GenericSet<Filler> orbShell = orbInstance.getFillerSetFromSquareRange(intSqRadius);
					
					// WarpDrive.logger.info(String.format("dSqRange %.3f sqRadiusHigh %.3f %.3f",
					//                                     dSqRange, sqRadiusHigh, sqRadiusLow));
					// note: placing block is faster from bottom to top due to skylight computations
					addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord + x, yCoord - y, zCoord + z));
					if (x != 0) {
						addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord - x, yCoord - y, zCoord + z));
					}
					if (y != 0) {
						addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord + x, yCoord + y, zCoord + z));
						if (x != 0) {
							addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord - x, yCoord + y, zCoord + z));
						}
					}
					if (z != 0) {
						addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord + x, yCoord - y, zCoord - z));
						if (x != 0) {
							addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord - x, yCoord - y, zCoord - z));
						}
						if (y != 0) {
							addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord + x, yCoord + y, zCoord - z));
							if (x != 0) {
								addBlock(isSurface, new JumpBlock(orbShell.getRandomUnit(rand), xCoord - x, yCoord + y, zCoord - z));
							}
						}
					}
				}
			}
		}
		if (blocks != null && blocks.size() > pregenSize) {
			WarpDrive.logger.warn(String.format("[EntitySphereGen] Saved %s blocks (estimated to %d)",
			                                    blocks.size(), pregenSize));
		}
		LocalProfiler.stop();
	}
	
	private void addBlock(final boolean isSurface, final JumpBlock jumpBlock) {
		if (blocks == null) {
			return;
		}
		// Replace water with random gas (ship in moon)
		if (world.getBlockState(new BlockPos(jumpBlock.x, jumpBlock.y, jumpBlock.z)).getBlock() == Blocks.WATER) {
			if (world.rand.nextInt(50) != 1) {
				jumpBlock.blockState = blockStateGas;
			}
			blocks.add(jumpBlock);
			isSurfaces.add(isSurface);
			return;
		}
		// Do not replace existing blocks if fillingSphere is true
		if (!replace && !world.isAirBlock(new BlockPos(jumpBlock.x, jumpBlock.y, jumpBlock.z))) {
			return;
		}
		blocks.add(jumpBlock);
		isSurfaces.add(isSurface);
	}
	
	@Override
	protected void registerData() {
		noClip = true;
	}
	
	private void constructionFinalizer() {
		radius = orbInstance.getTotalThickness();
		pregenSize = (int) Math.ceil(Math.PI * 4.0F / 3.0F * Math.pow(radius + 1, 3));
		blocks = new ArrayList<>(this.pregenSize);
		isSurfaces = new ArrayList<>(this.pregenSize);
		
		state = STATE_SAVING;
		ticksDelay = world.rand.nextInt(60);
	}
	
	@Override
	public void readAdditional(@Nonnull final CompoundNBT tagCompound) {
		xCoord = tagCompound.getInt("warpdrive:xCoord");
		yCoord = tagCompound.getInt("warpdrive:yCoord");
		zCoord = tagCompound.getInt("warpdrive:zCoord");
		orbInstance = new OrbInstance(tagCompound.getCompound("warpdrive:orbInstance"));
		blockStateGas = NBTUtil.readBlockState(tagCompound.getCompound("warpdrive:blockStateGas"));
		replace = tagCompound.getBoolean("warpdrive:replace");
		
		constructionFinalizer();
		WarpDrive.logger.info(String.format("%s Reloaded from NBT",
		                                    this));
	}
	
	@Override
	public void writeAdditional(final CompoundNBT tagCompound) {
		tagCompound.putInt("warpdrive:xCoord", xCoord);
		tagCompound.putInt("warpdrive:yCoord", yCoord);
		tagCompound.putInt("warpdrive:zCoord", zCoord);
		tagCompound.put("warpdrive:orbInstance", orbInstance.write(new CompoundNBT()));
		tagCompound.put("warpdrive:blockStateGas", NBTUtil.writeBlockState(blockStateGas));
		tagCompound.putBoolean("warpdrive:replace", replace);
	}
	
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return false;
	}
}