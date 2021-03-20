package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnergyWrapper;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.item.ItemComponent;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class TileEntityChunkLoader extends TileEntityAbstractChunkLoading {
	
	// global properties
	private static final UpgradeSlot upgradeSlotEfficiency = new UpgradeSlot("chunk_loader.efficiency",
	                                                                         ItemComponent.getItemStackNoCache(EnumComponentType.SUPERCONDUCTOR, 1),
	                                                                         5);
	private static final UpgradeSlot upgradeSlotRange = new UpgradeSlot("chunk_loader.range",
	                                                                    ItemComponent.getItemStackNoCache(EnumComponentType.EMERALD_CRYSTAL, 1),
	                                                                    WarpDriveConfig.CHUNK_LOADER_MAX_RADIUS);
	
	// persistent properties
	// fuel status is needed before first tick
	private boolean isPowered = false;
	
	// computed properties
	// (none)
	
	public TileEntityChunkLoader(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveChunkLoader";
		addMethods(new String[] {
				"range",
		});
		doRequireUpgradeToInterface();
		
		registerUpgradeSlot(upgradeSlotEfficiency);
		registerUpgradeSlot(upgradeSlotRange);
	}
	
	@Override
	public boolean energy_canInput(final Direction from) {
		return true;
	}
	
	@Override
	protected void onUpgradeChanged(@Nonnull final UpgradeSlot upgradeSlot, final int countNew, final boolean isAdded) {
		super.onUpgradeChanged(upgradeSlot, countNew, isAdded);
		if (isAdded) {
			final int range_max = getMaxRange();
			setRange(range_max);
		}
	}
	
	private int getMaxRange() {
		return getValidUpgradeCount(upgradeSlotRange);
	}
	
	private double getEnergyFactor() {
		final int upgradeCount = getValidUpgradeCount(upgradeSlotEfficiency);
		return 1.0D - 0.1D * upgradeCount;
	}
	
	public int calculateEnergyRequired() {
		return (int) Math.ceil(getEnergyFactor() * chunkloading_getArea() * WarpDriveConfig.CHUNK_LOADER_ENERGY_PER_CHUNK);
	}
	
	@Override
	public boolean shouldChunkLoad() {
		return isEnabled && isPowered;
	}
	
	@Override
	protected void onConstructed() {
		super.onConstructed();
		
		energy_setParameters(WarpDriveConfig.CHUNK_LOADER_MAX_ENERGY_STORED,
		                     1024, 0,
		                     "MV", 2, "MV", 0);
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		refreshChunkLoading();
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if (world.isRemote()) {
			return;
		}
		
		isPowered = energy_consume(calculateEnergyRequired(), !isEnabled);
		
		updateBlockState(null, BlockProperties.ACTIVE, isEnabled && isPowered);
	}
	
	private void setRange(final int range) {
		// compute new values
		final int range_max = getMaxRange();
		range_requested = Commons.clamp(0, range_max, Math.abs(range));
		
		refreshChunkLoading();
	}
	
	// Forge overrides
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		setRange(tagCompound.getInt("range"));
		isPowered = tagCompound.getBoolean("isPowered");
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		tagCompound.putInt("range", range_requested);
		tagCompound.putBoolean("isPowered", isPowered);
		return tagCompound;
	}
	
	// Common OC/CC methods
	public Object[] range(@Nonnull final Object[] arguments) {
		if (arguments.length == 4) {
			setRange(Commons.toInt(arguments[0]));
		}
		return new Object[] { range_requested };
	}
	
	@Override
	public Object[] getEnergyRequired() {
		final String units = energy_getDisplayUnits();
		return new Object[] {
				true,
				EnergyWrapper.convert(calculateEnergyRequired(), units) };
	}
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] range(final Context context, final Arguments arguments) {
		return range(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "range":
			return range(arguments);
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
}
