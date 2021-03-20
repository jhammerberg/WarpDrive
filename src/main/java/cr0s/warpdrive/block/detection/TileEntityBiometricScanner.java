package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.TileEntityAbstractMachine;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.SoundEvents;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityBiometricScanner extends TileEntityAbstractMachine {
	
	// persistent properties
	private UUID uuidLastPlayer = null;
	private String nameLastPlayer = "";
	
	// computed properties
	private int tickUpdate;
	private AxisAlignedBB aabbRange = null;
	private int tickScanning = -1; // < 0 when IDLE, >= 0 when SCANNING
	
	public TileEntityBiometricScanner(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveBiometricScanner";
		addMethods(new String[] {
			"getScanResults"
		});
		CC_scripts = Collections.singletonList("scan");
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		
		assert world != null;
		final BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof BlockBiometricScanner) {
			final Direction enumFacing = blockState.get(BlockProperties.FACING);
			final float radius = WarpDriveConfig.BIOMETRIC_SCANNER_RANGE_BLOCKS / 2.0F;
			final Vector3 v3Center = new Vector3(
					pos.getX() + 0.5F + (radius + 0.5F) * enumFacing.getXOffset(),
					pos.getY() + 0.5F + (radius + 0.5F) * enumFacing.getYOffset(),
					pos.getZ() + 0.5F + (radius + 0.5F) * enumFacing.getZOffset() );
			aabbRange = new AxisAlignedBB(
					v3Center.x - radius, v3Center.y - radius, v3Center.z - radius,
					v3Center.x + radius, v3Center.y + radius, v3Center.z + radius );
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		tickUpdate--;
		if (tickUpdate < 0) {
			tickUpdate = WarpDriveConfig.G_PARAMETERS_UPDATE_INTERVAL_TICKS;
			
			final BlockState blockState = world.getBlockState(pos);
			updateBlockState(blockState, BlockProperties.ACTIVE, isEnabled);
		}
		
		if ( isEnabled
		  && tickScanning >= 0 ) {
			tickScanning--;
			
			// check for exclusive player presence
			final List<ServerPlayerEntity> playersInRange = world.getEntitiesWithinAABB(ServerPlayerEntity.class, aabbRange,
			                                                                        entityServerPlayer -> entityServerPlayer != null
			                                                                                       && entityServerPlayer.isAlive()
			                                                                                       && !entityServerPlayer.isSpectator());
			boolean isJammed = false;
			boolean isPresent = false;
			for (final ServerPlayerEntity entityServerPlayer : playersInRange) {
				if (entityServerPlayer.getUniqueID().equals(uuidLastPlayer)) {
					isPresent = true;
				} else {
					isJammed = true;
					PacketHandler.sendSpawnParticlePacket(world, "jammed", (byte) 5,
					                                      new Vector3(entityServerPlayer.getPosX(), entityServerPlayer.getPosY(), entityServerPlayer.getPosZ()),
					                                      new Vector3(0.0D, 0.0D, 0.0D),
					                                      1.0F, 1.0F, 1.0F,
					                                      1.0F, 1.0F, 1.0F,
					                                      32 );
				}
			}
			if ( !isPresent
			  || isJammed ) {
				PacketHandler.sendSpawnParticlePacket(world, "jammed", (byte) 5,
				                                      new Vector3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D),
				                                      new Vector3(0.0D, 0.0D, 0.0D),
				                                      1.0F, 1.0F, 1.0F,
				                                      1.0F, 1.0F, 1.0F,
				                                      32 );
				tickScanning = -1;
				uuidLastPlayer = null;
				nameLastPlayer = "";
				sendEvent("biometricScanAborted");
				
			} else if (tickScanning < 0) {
				sendEvent("biometricScanDone", uuidLastPlayer.toString(), nameLastPlayer);
				world.playSound(null, pos, SoundEvents.DING, SoundCategory.BLOCKS, 1.0F, 1.0F);
				
			} else if (tickScanning == WarpDriveConfig.BIOMETRIC_SCANNER_DURATION_TICKS - 1) {
				PacketHandler.sendScanningPacket(world,
				                                 (int) aabbRange.minX, (int) aabbRange.minY, (int) aabbRange.minZ,
				                                 (int) aabbRange.maxX, (int) aabbRange.maxY, (int) aabbRange.maxZ,
				                                 0.3F, 0.0F, 1.0F, WarpDriveConfig.BIOMETRIC_SCANNER_DURATION_TICKS);
			}
		}
	}
	
	public boolean startScanning(@Nonnull final PlayerEntity entityPlayer, @Nonnull final WarpDriveText textReason) {
		if (!isEnabled) {
			textReason.append(Commons.getStyleWarning(), "warpdrive.machine.is_enabled.get.disabled",
			                  (int) Math.ceil(tickScanning / 20.0F) );
			return false;
		}
		if (tickScanning >= 0) {
			textReason.append(Commons.getStyleWarning(), "warpdrive.biometric_scanner.start_scanning.in_progress",
			                  (int) Math.ceil(tickScanning / 20.0F) );
			return false;
		}
		
		uuidLastPlayer = entityPlayer.getUniqueID();
		nameLastPlayer = entityPlayer.getName().getString();
		tickScanning = WarpDriveConfig.BIOMETRIC_SCANNER_DURATION_TICKS;
		textReason.append(Commons.getStyleCorrect(), "warpdrive.biometric_scanner.start_scanning.started");
		return true;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		if (tagCompound.hasUniqueId("uuidLastPlayer")) {
			uuidLastPlayer = tagCompound.getUniqueId("uuidLastPlayer");
			nameLastPlayer = tagCompound.getString("nameLastPlayer");
		} else {
			uuidLastPlayer = null;
			nameLastPlayer = "";
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		// only save if scanning has concluded
		if ( tickScanning < 0
		  && uuidLastPlayer != null
		  && uuidLastPlayer.getMostSignificantBits() != 0L
		  && uuidLastPlayer.getLeastSignificantBits() != 0L ) {
			tagCompound.putUniqueId("uuidLastPlayer", uuidLastPlayer);
			tagCompound.putString("nameLastPlayer", nameLastPlayer);
		} else {
			tagCompound.remove("uuidLastPlayer");
			tagCompound.remove("nameLastPlayer");
		}
		
		return tagCompound;
	}
	
	// TileEntityAbstractBase overrides
	@Nonnull
	private WarpDriveText getScanStatus() {
		if (tickScanning >= 0) {
			return new WarpDriveText(Commons.getStyleWarning(), "warpdrive.biometric_scanner.status_line.scan_in_progress",
			                         (int) Math.ceil(tickScanning / 20.0F) );
		}
		if (uuidLastPlayer == null) {
			return new WarpDriveText(Commons.getStyleWarning(), "warpdrive.biometric_scanner.status_line.invalid");
		}
		return new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.biometric_scanner.status_line.valid",
		                         nameLastPlayer, uuidLastPlayer );
	}
	
	@Override
	public WarpDriveText getStatus() {
		final WarpDriveText textScanStatus = getScanStatus();
		if (textScanStatus.isEmpty()) {
			return super.getStatus();
		} else {
			return super.getStatus()
			            .append(textScanStatus);
		}
	}
	
	// Common OC/CC methods
	public Object[] getScanResults() {
		if (tickScanning >= 0) {
			return new Object[] { false, "Scan is in progress..." };
		}
		if (uuidLastPlayer == null) {
			return new Object[] { false, "No results available." };
		}
		return new Object[] { true, uuidLastPlayer, nameLastPlayer };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] getScanResults(final Context context, final Arguments arguments) {
		OC_convertArgumentsAndLogCall(context, arguments);
		return getScanResults();
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "getScanResults":
			return getScanResults();
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
}