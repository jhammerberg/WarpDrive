package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IGlobalRegionProvider;
import cr0s.warpdrive.block.TileEntityAbstractEnergyCoreOrController;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumGlobalRegionType;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.render.EntityFXBoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityJumpGateCore extends TileEntityAbstractEnergyCoreOrController implements IGlobalRegionProvider {
	
	private static final int BOUNDING_BOX_INTERVAL_TICKS = 60;
	
	// persistent properties
	private int maxX, maxY, maxZ;
	private int minX, minY, minZ;
	
	// computed properties
	protected boolean showBoundingBox = false;
	private int ticksBoundingBoxUpdate = 0;
	
	private long timeLastScanDone = -1;
	private JumpGateScanner jumpGateScanner = null;
	public int volume;
	public double occupancy;
	
	
	public TileEntityJumpGateCore(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveJumpGate";
		// addMethods(new String[] {});
		// CC_scripts = Collections.singletonList("startup");
	}
	
	@OnlyIn(Dist.CLIENT)
	private void doShowBoundingBox() {
		ticksBoundingBoxUpdate--;
		if (ticksBoundingBoxUpdate > 0) {
			return;
		}
		ticksBoundingBoxUpdate = BOUNDING_BOX_INTERVAL_TICKS;
		
		final Vector3 vector3 = new Vector3(this);
		vector3.translate(0.5D);
		
		Minecraft.getInstance().particles.addEffect(
				new EntityFXBoundingBox(world, vector3,
				                        new Vector3(minX - 0.0D, minY - 0.0D, minZ - 0.0D),
				                        new Vector3(maxX + 1.0D, maxY + 1.0D, maxZ + 1.0D),
				                        1.0F, 0.8F, 0.3F, BOUNDING_BOX_INTERVAL_TICKS + 1));
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if (world.isRemote()) {
			if (showBoundingBox) {
				doShowBoundingBox();
			}
			return;
		}
		
		// scan ship content progressively
		if (timeLastScanDone <= 0L) {
			timeLastScanDone = world.getGameTime();
			jumpGateScanner = new JumpGateScanner(world, minX, minY, minZ, maxX, maxY, maxZ);
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				WarpDrive.logger.info(String.format("%s scanning started",
				                                    this));
			}
		}
		if (jumpGateScanner != null) {
			if (!jumpGateScanner.tick()) {
				// still scanning => skip state handling
				return;
			}
			
			volume = maxX - minX * maxY - minY * maxZ - minZ;
			occupancy = volume / (float) jumpGateScanner.volumeUsed;
			jumpGateScanner = null;
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				WarpDrive.logger.info(String.format("%s scanning done: volume %d, occupancy %.3f",
				                                    this, volume, occupancy));
			}
		}
	}
	
	@Override
	protected void doUpdateParameters(final boolean isDirty) {
		// no operation
	}
	
	public boolean isBusy() {
		return timeLastScanDone < 0 || jumpGateScanner != null;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		minX = tagCompound.getInt("minX");
		maxX = tagCompound.getInt("maxX");
		minY = tagCompound.getInt("minY");
		maxY = tagCompound.getInt("maxY");
		minZ = tagCompound.getInt("minZ");
		maxZ = tagCompound.getInt("maxZ");
		volume = tagCompound.getInt("volume");
		occupancy = tagCompound.getDouble("occupancy");
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		tagCompound.putInt("minX", minX);
		tagCompound.putInt("maxX", maxX);
		tagCompound.putInt("minY", minY);
		tagCompound.putInt("maxY", maxY);
		tagCompound.putInt("minZ", minZ);
		tagCompound.putInt("maxZ", maxZ);
		tagCompound.putInt("volume", volume);
		tagCompound.putDouble("occupancy", occupancy);
		
		return tagCompound;
	}
	
	// IGlobalRegionProvider overrides
	@Override
	public EnumGlobalRegionType getGlobalRegionType() {
		return EnumGlobalRegionType.JUMP_GATE;
	}
	
	@Override
	public AxisAlignedBB getGlobalRegionArea() {
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public int getMass() {
		return volume;
	}
	
	@Override
	public double getIsolationRate() {
		return 0.0D;
	}
	
	@Override
	public boolean onBlockUpdatingInArea(@Nullable final Entity entity, final BlockPos blockPos, final BlockState blockState) {
		// no operation
		return true;
	}
	
	// Common OC/CC methods
	
	@Override
	public Object[] getEnergyRequired() {
		return new Object[0];
	}
	
	public Object[] area(@Nonnull final Object[] arguments) {
		try {
			if (arguments.length == 6) {
				final int sizeMax = WarpDriveConfig.JUMP_GATE_SIZE_MAX_PER_SIDE_BY_TIER[enumTier.getIndex()];
				final int minX_new = Commons.clamp(pos.getX() - sizeMax, pos.getX() + sizeMax, Math.abs(Commons.toInt(arguments[0])));
				final int minY_new = Commons.clamp(pos.getY() - sizeMax, pos.getY() + sizeMax, Math.abs(Commons.toInt(arguments[1])));
				final int minZ_new = Commons.clamp(pos.getZ() - sizeMax, pos.getZ() + sizeMax, Math.abs(Commons.toInt(arguments[2])));
				final int maxX_new = Commons.clamp(pos.getX() - sizeMax, pos.getX() + sizeMax, Math.abs(Commons.toInt(arguments[3])));
				final int maxY_new = Commons.clamp(pos.getY() - sizeMax, pos.getY() + sizeMax, Math.abs(Commons.toInt(arguments[4])));
				final int maxZ_new = Commons.clamp(pos.getZ() - sizeMax, pos.getZ() + sizeMax, Math.abs(Commons.toInt(arguments[5])));
				if ( minX_new != minX
				  || minY_new != minY
				  || minZ_new != minZ
				  || maxX_new != maxX
				  || maxY_new != maxY
				  || maxZ_new != maxZ ) {
					minX = minX_new;
					minY = minY_new;
					minZ = minZ_new;
					maxX = maxX_new;
					maxY = maxY_new;
					maxZ = maxZ_new;
					// force a new scan
					timeLastScanDone = -1;
					jumpGateScanner = null;
				}
			}
		} catch (final Exception exception) {
			if (WarpDriveConfig.LOGGING_LUA) {
				WarpDrive.logger.info(String.format("%s Invalid arguments to area(): %s",
				                                    this, Commons.format(arguments)));
			}
		}
		
		return new Integer[] { minX, minY, minZ, maxX, maxY, maxZ };
	}
}