package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.TileEntityAbstractMachine;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumCameraType;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.item.ItemComponent;
import cr0s.warpdrive.network.PacketHandler;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityCamera extends TileEntityAbstractMachine implements IVideoChannel {
	
	public static TileEntityType<TileEntityCamera> TYPE;
	
	private int videoChannel = -1;

	private static final int REGISTRY_UPDATE_INTERVAL_TICKS = 15 * 20;
	private static final int PACKET_SEND_INTERVAL_TICKS = 60 * 20;
	
	private static final UpgradeSlot upgradeSlotRecognitionRange = new UpgradeSlot("camera.recognition_range",
	                                                                               ItemComponent.getItemStackNoCache(EnumComponentType.DIAMOND_CRYSTAL, 1),
	                                                                               WarpDriveConfig.CAMERA_RANGE_UPGRADE_MAX_QUANTITY );
	
	// persistent properties
	private final CopyOnWriteArrayList<Result> results = new CopyOnWriteArrayList<>();
	
	// computed properties
	private Vec3d vCamera = null;
	private int packetSendTicks = 10;
	private int registryUpdateTicks = 20;
	private boolean hasImageRecognition = false;
	
	private AxisAlignedBB aabbRange = null;
	private int tickSensing = 0;
	
	
	private static final class Result {
		
		public Vector3 position;
		public Vector3 motion;
		public String type;
		public UUID uniqueId;
		public String name;
		private boolean isUpdated;
		
		Result(@Nonnull final Vector3 position, @Nonnull final Vector3 motion, @Nonnull final String type,
		       @Nonnull final UUID uniqueId, @Nonnull final String name) {
			this.position = position;
			this.motion = motion;
			this.type = type;
			this.uniqueId = uniqueId;
			this.name = name;
			this.isUpdated = false;
		}
		
		Result(@Nonnull final Entity entity) {
			this(new Vector3(entity.getPosX(),
			                 entity.getPosY() + entity.getEyeHeight(),
			                 entity.getPosZ() ),
			     new Vector3(entity.getMotion()),
			     Dictionary.getId(entity),
			     entity.getUniqueID(),
			     entity.getName().getUnformattedComponentText() );
			// seen it was created from an entity, it's already updated
			isUpdated = true;
		}
		
		void markForUpdate() {
			isUpdated = false;
		}
		
		void update(@Nonnull final Entity entity) {
			uniqueId = entity.getUniqueID();
			position.x = entity.getPosX();
			position.y = entity.getPosY() + entity.getEyeHeight();
			position.z = entity.getPosZ();
			motion.x = entity.getMotion().x;
			motion.y = entity.getMotion().y;
			motion.z = entity.getMotion().z;
			isUpdated = true;
		}
		
		boolean isUpdated() {
			return isUpdated;
		}
		
		@Override
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (object instanceof Entity) {
				final Entity entity = (Entity) object;
				// note: getting an entity type is fairly slow, so we do it as late as possible
				return (uniqueId == null || entity.getUniqueID().equals(uniqueId))
				       && entity.getName().getUnformattedComponentText().equals(name)
				       && Dictionary.getId(entity).equals(type);
			}
			if (getClass() != object.getClass()) {
				return false;
			}
			final Result that = (Result) object;
			return (uniqueId == null || that.uniqueId == null || that.uniqueId.equals(uniqueId))
			       && that.name.equals(name)
			       && that.type.equals(type);
		}
		
		@Override
		public int hashCode() {
			return type.hashCode() + name.hashCode();
		}
	}
	
	public TileEntityCamera() {
		super(TYPE);
		
		peripheralName = "warpdriveCamera";
		addMethods(new String[] {
			"videoChannel",
			"getResults",
			"getResultsCount",
			"getResult"
		});
		doRequireUpgradeToInterface();
		CC_scripts = Collections.singletonList("recognize");
		
		registerUpgradeSlot(upgradeSlotRecognitionRange);
	}
	
	@Override
	public void tick() {
		super.tick();
		assert world != null;
		
		// Update video channel on clients (recovery mechanism, no need to go too fast)
		if (!world.isRemote()) {
			packetSendTicks--;
			if (packetSendTicks <= 0) {
				packetSendTicks = PACKET_SEND_INTERVAL_TICKS;
				PacketHandler.sendVideoChannelPacket(world, pos, videoChannel);
			}
		} else {
			registryUpdateTicks--;
			if (registryUpdateTicks <= 0) {
				registryUpdateTicks = REGISTRY_UPDATE_INTERVAL_TICKS;
				if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
					WarpDrive.logger.info(this + " Updating registry (" + videoChannel + ")");
				}
				WarpDrive.cameras.updateInRegistry(world, pos, videoChannel, EnumCameraType.SIMPLE_CAMERA);
			}
			return;
		}
		
		
		if ( isEnabled
		  && hasImageRecognition ) {
			tickSensing--;
			if (tickSensing < 0) {
				tickSensing = WarpDriveConfig.CAMERA_IMAGE_RECOGNITION_INTERVAL_TICKS;
				
				// clear the markers
				for (final Result result : results) {
					result.markForUpdate();
				}
				
				// check for exclusive living entity presence
				int countAdded = 0;
				final int countOld = results.size();
				final List<Entity> entitiesInRange = world.getEntitiesWithinAABB(Entity.class, aabbRange,
				                                                                 entity -> entity != null
				                                                                        && entity.isAlive()
				                                                                        && !entity.isInvisible()
				                                                                        && ( !(entity instanceof PlayerEntity)
				                                                                          || !entity.isSpectator() ));
				for (final Entity entity : entitiesInRange) {
					// check for line of sight
					final Vec3d vEntity = new Vec3d(entity.getPosX(),
					                                entity.getPosY(),
					                                entity.getPosZ() );
					final RayTraceResult rayTraceResult = world.rayTraceBlocks(
							new RayTraceContext(vCamera, vEntity, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
					if (rayTraceResult.getType() != Type.BLOCK) {
						continue;
					}
					
					// check for existing results
					boolean isNew = true;
					for (final Result result : results) {
						if (result.equals(entity)) {
							result.update(entity);
							isNew = false;
							break;
						}
					}
					
					// add new result
					if (isNew) {
						countAdded++;
						results.add(new Result(entity));
					}
				}
				
				// clear old results
				results.removeIf(result -> !result.isUpdated());
				final int countRemoved = countOld + countAdded - results.size();
				
				// trigger LUA event
				if ( countAdded > 0
				  || countRemoved > 0 ) {
					sendEvent("opticalSensorResultsChanged", countAdded, countRemoved);
				}
			}
		}
	}
	
	@Override
	protected void doUpdateParameters(final boolean isDirty) {
		super.doUpdateParameters(isDirty);
		assert world != null;
		
		final BlockState blockState = world.getBlockState(pos);
		updateBlockState(blockState, BlockProperties.ACTIVE, isEnabled);
		
		final int range = WarpDriveConfig.CAMERA_RANGE_BASE_BLOCKS
		                + WarpDriveConfig.CAMERA_RANGE_UPGRADE_BLOCKS * getUpgradeCount(upgradeSlotRecognitionRange);
		hasImageRecognition = range > 0;
		
		if ( hasImageRecognition
		  && blockState.getBlock() instanceof BlockCamera ) {
			final Direction enumFacing = blockState.get(BlockProperties.FACING);
			final float radius = range / 2.0F;
			vCamera = new Vec3d(
					pos.getX() + 0.5F + 0.6F * enumFacing.getXOffset(),
					pos.getY() + 0.5F + 0.6F * enumFacing.getYOffset(),
					pos.getZ() + 0.5F + 0.6F * enumFacing.getZOffset() );
			final Vec3d vCenter = new Vec3d(
					pos.getX() + 0.5F + (radius + 0.5F) * enumFacing.getXOffset(),
					pos.getY() + 0.5F + (radius + 0.5F) * enumFacing.getYOffset(),
					pos.getZ() + 0.5F + (radius + 0.5F) * enumFacing.getZOffset() );
			aabbRange = new AxisAlignedBB(
					vCenter.x - radius, vCenter.y - radius, vCenter.z - radius,
					vCenter.x + radius, vCenter.y + radius, vCenter.z + radius );
		}
	}
	
	@Override
	public int getVideoChannel() {
		return videoChannel;
	}
	
	@Override
	public void setVideoChannel(final int parVideoChannel) {
		if ( videoChannel != parVideoChannel
		  && IVideoChannel.isValid(parVideoChannel) ) {
			videoChannel = parVideoChannel;
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info(this + " Video channel set to " + videoChannel);
			}
			markDirty();
			// force update through main thread since CC & OC are running outside the main thread
			packetSendTicks = 0;
			registryUpdateTicks = 0;
		}
	}
	
	@Override
	public void remove() {
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info(this + " removed");
		}
		WarpDrive.cameras.removeFromRegistry(world, pos);
		super.remove();
	}
	
	@Override
	public void onChunkUnloaded() {
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info(this + " onChunkUnloaded");
		}
		WarpDrive.cameras.removeFromRegistry(world, pos);
		super.onChunkUnloaded();
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		videoChannel = tagCompound.getInt("frequency") + tagCompound.getInt(VIDEO_CHANNEL_TAG);
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info(this + " readFromNBT");
		}
		
		final ListNBT tagList = tagCompound.getList("results", NBT.TAG_COMPOUND);
		for (final INBT tagResult : tagList) {
			final CompoundNBT tagCompoundResult = (CompoundNBT) tagResult;
			try {
				final Result result = new Result(
						new Vector3(tagCompoundResult.getDouble("posX"), tagCompoundResult.getDouble("posY"), tagCompoundResult.getDouble("posZ")),
						new Vector3(tagCompoundResult.getDouble("motionX"), tagCompoundResult.getDouble("motionY"), tagCompoundResult.getDouble("motionZ")),
						tagCompoundResult.getString("type"),
						Objects.requireNonNull(tagCompoundResult.getUniqueId("uniqueId")),
						tagCompoundResult.getString("name") );
				results.add(result);
			} catch (final Exception exception) {
				WarpDrive.logger.error(String.format("%s Exception while reading previous result %s",
				                                     this, tagCompoundResult ));
				exception.printStackTrace(WarpDrive.printStreamError);
			}
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		tagCompound.putInt(VIDEO_CHANNEL_TAG, videoChannel);
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info(this + " writeToNBT");
		}
		
		if (!results.isEmpty()) {
			final ListNBT tagList = new ListNBT();
			for (final Result result : results) {
				final CompoundNBT tagCompoundResult = new CompoundNBT();
				tagCompoundResult.putDouble("posX", result.position.x);
				tagCompoundResult.putDouble("posY", result.position.y);
				tagCompoundResult.putDouble("posZ", result.position.z);
				tagCompoundResult.putDouble("motionX", result.motion.x);
				tagCompoundResult.putDouble("motionY", result.motion.y);
				tagCompoundResult.putDouble("motionZ", result.motion.z);
				tagCompoundResult.putString("type", result.type);
				if (result.uniqueId != null) {
					tagCompoundResult.putUniqueId("uniqueId", result.uniqueId);
				}
				if (result.name != null) {
					tagCompoundResult.putString("name", result.name);
				}
				tagList.add(tagCompoundResult);
			}
			tagCompound.put("results", tagList);
		} else {
			tagCompound.remove("results");
		}
		
		return tagCompound;
	}
	
	// TileEntityAbstractBase overrides
	@Nonnull
	private WarpDriveText getSensorStatus() {
		if (!hasImageRecognition) {
			return new WarpDriveText();
		}
		if (results.isEmpty()) {
			return new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.optical_sensor.status_line.no_result");
		}
		return new WarpDriveText(Commons.getStyleCorrect(), "warpdrive.optical_sensor.status_line.result_count",
		                         results.size() );
	}
	
	@Override
	public WarpDriveText getStatus() {
		final WarpDriveText textScanStatus = getSensorStatus();
		if (textScanStatus.getUnformattedComponentText().isEmpty()) {
			return super.getStatus();
		} else {
			return super.getStatus()
			            .append(textScanStatus);
		}
	}
	
	// Common OC/CC methods
	public Object[] videoChannel(@Nonnull final Object[] arguments) {
		if (arguments.length == 1) {
			setVideoChannel(Commons.toInt(arguments[0]));
		}
		return new Integer[] { getVideoChannel() };
	}
	
	private Object[] getResults() {
		if (results == null) {
			return null;
		}
		final Object[] objectResults = new Object[results.size()];
		int index = 0;
		for (final Result result : results) {
			objectResults[index++] = new Object[] {
					result.type,
					result.name == null ? "" : result.name,
					result.position.x, result.position.y, result.position.z,
					result.motion.x, result.motion.y, result.motion.z };
		}
		return objectResults;
	}
	
	private Object[] getResultsCount() {
		if (results != null) {
			return new Integer[] { results.size() };
		}
		return new Integer[] { -1 };
	}
	
	private Object[] getResult(@Nonnull final Object[] arguments) {
		if (arguments.length == 1 && (results != null)) {
			final int index;
			try {
				index = Commons.toInt(arguments[0]);
			} catch(final Exception exception) {
				return new Object[] { false, COMPUTER_ERROR_TAG, COMPUTER_ERROR_TAG, 0, 0, 0, 0, 0, 0 };
			}
			if (index >= 0 && index < results.size()) {
				final Result result = results.get(index);
				if (result != null) {
					return new Object[] {
							true,
							result.type,
							result.name == null ? "" : result.name,
							result.position.x, result.position.y, result.position.z,
							result.motion.x, result.motion.y, result.motion.z };
				}
			}
		}
		return new Object[] { false, COMPUTER_ERROR_TAG, COMPUTER_ERROR_TAG, 0, 0, 0, 0, 0, 0 };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] videoChannel(final Context context, final Arguments arguments) {
		return videoChannel(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	@Callback(direct = true)
	public Object[] getResults(final Context context, final Arguments arguments) {
		OC_convertArgumentsAndLogCall(context, arguments);
		return getResults();
	}
	
	@Callback(direct = true)
	public Object[] getResultsCount(final Context context, final Arguments arguments) {
		OC_convertArgumentsAndLogCall(context, arguments);
		return getResultsCount();
	}
	
	@Callback(direct = true)
	public Object[] getResult(final Context context, final Arguments arguments) {
		return getResult(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "videoChannel":
			return videoChannel(arguments);
			
		case "getResults":
			return getResults();
			
		case "getResultsCount":
			return getResultsCount();
			
		case "getResult":
			return getResult(arguments);
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s",
		                     getClass().getSimpleName(), 
		                     videoChannel,
		                     Commons.format(world, pos) );
	}
}