package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IGlobalRegionProvider;
import cr0s.warpdrive.api.computer.ICoreSignature;
import cr0s.warpdrive.api.computer.IEnergyConsumer;
import cr0s.warpdrive.api.computer.IMultiBlockCoreOrController;
import cr0s.warpdrive.api.computer.IMultiBlockCore;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.GlobalRegionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;

public abstract class TileEntityAbstractEnergyCoreOrController extends TileEntityAbstractEnergyConsumer implements IMultiBlockCoreOrController, IEnergyConsumer {
	
	// persistent properties
	public UUID uuid = null;
	
	// computed properties
	private boolean isDirtyGlobalRegion = true;
	private int tickUpdateGlobalRegion = 0;
	
	public TileEntityAbstractEnergyCoreOrController(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		// (abstract) peripheralName = "xxx";
		// addMethods(new String[] {
		// 		});
	}
	
	@Override
	public void tick() {
		super.tick();
		
		assert world != null;
		if ( world.isRemote()
		  && !(this instanceof TileEntityShipCore) ) {
			return;
		}
		
		// update registration upon request or periodically to recover whatever may have desynchronized it
		if (this instanceof IGlobalRegionProvider) {
			if (isDirtyGlobalRegion) {
				tickUpdateGlobalRegion = 0;
			}
			tickUpdateGlobalRegion--;
			if (tickUpdateGlobalRegion <= 0) {
				tickUpdateGlobalRegion = WarpDriveConfig.G_REGISTRY_UPDATE_INTERVAL_TICKS;
				final boolean isDirty = isDirtyGlobalRegion;
				isDirtyGlobalRegion = false;
				
				doRegisterGlobalRegion(isDirty);
			}
		}
	}
	
	protected void markDirtyGlobalRegion() {
		assert this instanceof IGlobalRegionProvider;
		isDirtyGlobalRegion = true;
	}
	
	protected void doRegisterGlobalRegion(final boolean isDirty) {
		if (uuid == null || (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L)) {
			uuid = UUID.randomUUID();
		}
		
		GlobalRegionManager.updateInRegistry((IGlobalRegionProvider) this);
	}
	
	@Override
	public void remove() {
		assert world != null;
		if ( !world.isRemote()
		  && this instanceof IGlobalRegionProvider ) {
			GlobalRegionManager.removeFromRegistry((IGlobalRegionProvider) this);
		}
		
		super.remove();
	}
	
	@Override
	public void onCoreUpdated(@Nonnull final IMultiBlockCore multiblockCore) {
		assert multiblockCore instanceof TileEntityAbstractEnergyCoreOrController;
		name = ((TileEntityAbstractEnergyCoreOrController) multiblockCore).name;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		uuid = new UUID(tagCompound.getLong(ICoreSignature.UUID_MOST_TAG), tagCompound.getLong(ICoreSignature.UUID_LEAST_TAG));
		if (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L) {
			uuid = UUID.randomUUID();
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		if ( uuid != null
		  && uuid.getMostSignificantBits() != 0L
		  && uuid.getLeastSignificantBits() != 0L ) {
			tagCompound.putLong(ICoreSignature.UUID_MOST_TAG, uuid.getMostSignificantBits());
			tagCompound.putLong(ICoreSignature.UUID_LEAST_TAG, uuid.getLeastSignificantBits());
		}
		
		return tagCompound;
	}
	
	// writeItemDropNBT
	
	@Nullable
	@Override
	public UUID getSignatureUUID() {
		return uuid;
	}
	
	@Override
	public String getSignatureName() {
		return name;
	}
	
	@Override
	public boolean setSignature(final UUID uuidSignature, final String nameSignature) {
		if (this instanceof IMultiBlockCore) {
			return false;
		}
		
		uuid = uuidSignature;
		name = nameSignature;
		return true;
	}
	
	// Common OC/CC methods
	// (none)
	
	// OpenComputers callback methods
	// (none)
	
	// ComputerCraft IDynamicPeripheral methods
	// (none)
	
	@Override
	public String toString() {
		return String.format("%s '%s' %s %s",
		                     getClass().getSimpleName(),
		                     name,
		                     Commons.format(world, pos),
		                     computer_isConnected() ? "Connected" : "Disconnected" );
	}
}
