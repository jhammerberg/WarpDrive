package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.data.EnumDisabledInputOutput;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.item.ItemComponent;

import javax.annotation.Nonnull;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

public class TileEntityCapacitor extends TileEntityAbstractEnergy {
	
	// global properties
	private static final String TAG_MODE_SIDE = "modeSide";
	
	private static final EnumDisabledInputOutput[] MODE_DEFAULT_SIDES = {
			EnumDisabledInputOutput.INPUT,
			EnumDisabledInputOutput.INPUT,
			EnumDisabledInputOutput.OUTPUT,
			EnumDisabledInputOutput.OUTPUT,
			EnumDisabledInputOutput.OUTPUT,
			EnumDisabledInputOutput.OUTPUT };
	
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_DOWN  = new ModelProperty<>();
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_UP    = new ModelProperty<>();
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_NORTH = new ModelProperty<>();
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_SOUTH = new ModelProperty<>();
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_WEST  = new ModelProperty<>();
	public static final ModelProperty<EnumDisabledInputOutput> MODEL_PROPERTY_EAST  = new ModelProperty<>();
	
	private static final UpgradeSlot upgradeSlotEfficiency = new UpgradeSlot("capacitor.efficiency",
	                                                                         ItemComponent.getItemStackNoCache(EnumComponentType.SUPERCONDUCTOR, 1),
	                                                                         WarpDriveConfig.CAPACITOR_EFFICIENCY_PER_UPGRADE.length - 1);
	
	// persistent properties
	private EnumDisabledInputOutput[] modeSide = MODE_DEFAULT_SIDES.clone();
	
	public TileEntityCapacitor(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveCapacitor";
		doRequireUpgradeToInterface();
		
		registerUpgradeSlot(upgradeSlotEfficiency);
	}
	
	@Override
	protected void onConstructed() {
		super.onConstructed();
		
		energy_setParameters(WarpDriveConfig.CAPACITOR_MAX_ENERGY_STORED_BY_TIER[enumTier.getIndex()],
		                     WarpDriveConfig.CAPACITOR_FLUX_RATE_INPUT_BY_TIER[enumTier.getIndex()],
		                     WarpDriveConfig.CAPACITOR_FLUX_RATE_OUTPUT_BY_TIER[enumTier.getIndex()],
		                     WarpDriveConfig.CAPACITOR_IC2_SINK_TIER_NAME_BY_TIER[enumTier.getIndex()], 2,
		                     WarpDriveConfig.CAPACITOR_IC2_SOURCE_TIER_NAME_BY_TIER[enumTier.getIndex()], 2);
	}
	
	private double getEfficiency() {
		final int upgradeCount = getValidUpgradeCount(upgradeSlotEfficiency);
		return WarpDriveConfig.CAPACITOR_EFFICIENCY_PER_UPGRADE[upgradeCount];
	}
	
	@Override
	public long energy_getEnergyStored() {
		if (enumTier == EnumTier.CREATIVE) {
			return WarpDriveConfig.CAPACITOR_MAX_ENERGY_STORED_BY_TIER[0] / 2L;
		} else {
			return super.energy_getEnergyStored();
		}
	}
	
	@Override
	public int energy_getPotentialOutput() {
		if (enumTier == null) {
			if (Commons.throttleMe("TileEntityCapacitor.notier")) {
				new RuntimeException(String.format("%s no tier defined yet, probably an invalid call, please report to mod author",
				                                   this ))
						.printStackTrace(WarpDrive.printStreamError);
			}
			return (int) Math.round(energy_getEnergyStored() * getEfficiency());
		}
		return (int) Math.round(Math.min(energy_getEnergyStored() * getEfficiency(), WarpDriveConfig.CAPACITOR_FLUX_RATE_OUTPUT_BY_TIER[enumTier.getIndex()]));
	}
	
	@Override
	public boolean energy_consume(final long amount_internal, final boolean simulate) {
		if (enumTier == EnumTier.CREATIVE) {
			return true;
		}
		final long amountWithLoss = Math.round(amount_internal / getEfficiency());
		if (energy_getEnergyStored() >= amountWithLoss) {
			if (!simulate) {
				super.energy_consume(amountWithLoss);
			}
			return true;
		}
		return false;
	}
	@Override
	public void energy_consume(final long amount_internal) {
		if (enumTier == EnumTier.CREATIVE) {
			return;
		}
		final long amountWithLoss = Math.round(amount_internal > 0 ? amount_internal / getEfficiency() : amount_internal * getEfficiency());
		super.energy_consume(amountWithLoss);
	}
	
	@Override
	public boolean energy_canInput(final Direction from) {
		if (from != null) {
			return modeSide[from.ordinal()] == EnumDisabledInputOutput.INPUT;
		} else {
			for (final Direction enumFacing : Direction.values()) {
				if (modeSide[enumFacing.ordinal()] == EnumDisabledInputOutput.INPUT) {
					return true;
				}
			}
			return false;
		}
	}
	
	@Override
	public boolean energy_canOutput(final Direction to) {
		if (to != null) {
			return modeSide[to.ordinal()] == EnumDisabledInputOutput.OUTPUT;
		} else {
			for (final Direction enumFacing : Direction.values()) {
				if (modeSide[enumFacing.ordinal()] == EnumDisabledInputOutput.OUTPUT) {
					return true;
				}
			}
			return false;
		}
	}
	
	protected EnumDisabledInputOutput getMode(final Direction facing) {
		return modeSide[facing.ordinal()];
	}
	
	void setMode(final Direction facing, final EnumDisabledInputOutput enumDisabledInputOutput) {
		modeSide[facing.ordinal()] = enumDisabledInputOutput;
		markDirty();
		energy_refreshConnections();
	}
	
	// Forge overrides
	@Nonnull
	@Override
	public IModelData getModelData() {// TODO MC1.15 Capacitor rendering
		final IModelData modelData = new ModelDataMap.Builder()
		            .withProperty(MODEL_PROPERTY_DOWN )
		            .withProperty(MODEL_PROPERTY_UP   )
		            .withProperty(MODEL_PROPERTY_NORTH)
		            .withProperty(MODEL_PROPERTY_SOUTH)
		            .withProperty(MODEL_PROPERTY_WEST )
		            .withProperty(MODEL_PROPERTY_EAST ).build();
		modelData.setData(MODEL_PROPERTY_DOWN , modeSide[0]);
		modelData.setData(MODEL_PROPERTY_UP   , modeSide[1]);
		modelData.setData(MODEL_PROPERTY_NORTH, modeSide[2]);
		modelData.setData(MODEL_PROPERTY_SOUTH, modeSide[3]);
		modelData.setData(MODEL_PROPERTY_WEST , modeSide[4]);
		modelData.setData(MODEL_PROPERTY_EAST , modeSide[5]);
		return modelData;
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		final byte[] bytes = new byte[Direction.values().length];
		for (final Direction enumFacing : Direction.values()) {
			bytes[enumFacing.ordinal()] = (byte) modeSide[enumFacing.ordinal()].getIndex();
		}
		tagCompound.putByteArray(TAG_MODE_SIDE, bytes);
		return tagCompound;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		final byte[] bytes = tagCompound.getByteArray(TAG_MODE_SIDE);
		if (bytes.length != 6) {
			modeSide = MODE_DEFAULT_SIDES.clone();
		} else {
			boolean isUpdated = false;
			for (final Direction enumFacing : Direction.values()) {
				isUpdated |= modeSide[enumFacing.ordinal()] != EnumDisabledInputOutput.get(bytes[enumFacing.ordinal()]);
				modeSide[enumFacing.ordinal()] = EnumDisabledInputOutput.get(bytes[enumFacing.ordinal()]);
			}
			// refresh client side rendering has needed
			if ( isUpdated
			  && world != null
			  && world.isRemote() ) {
				world.markBlockRangeForRenderUpdate(pos, Blocks.AIR.getDefaultState(), getBlockState());
			}
		}
	}
	
	@Override
	public CompoundNBT writeItemDropNBT(CompoundNBT tagCompound) {
		tagCompound = super.writeItemDropNBT(tagCompound);
		return tagCompound;
	}
	
	@Override
	public String toString() {
		if (enumTier == null) {
			return String.format("%s %s",
			                     getClass().getSimpleName(),
			                     Commons.format(world, pos));
		} else {
			return String.format("%s %s %8d",
			                     getClass().getSimpleName(),
			                     Commons.format(world, pos),
			                     energy_getEnergyStored());
		}
	}
}