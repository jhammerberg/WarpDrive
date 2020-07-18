package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.computer.IEnergyConsumer;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityAbstractEnergyConsumer extends TileEntityAbstractEnergy implements IEnergyConsumer {
	
	// persistent properties
	// (none)
	
	// computed properties
	// (none)
	
	public TileEntityAbstractEnergyConsumer(@Nonnull TileEntityType<? extends TileEntityAbstractEnergyConsumer> tileEntityType) {
		super(tileEntityType);
		
		addMethods(new String[] {
				"getEnergyRequired",
				});
	}
	
	// Common OC/CC methods
	@Override
	public abstract Object[] getEnergyRequired();
	
	// OpenComputers callback methods
	@Callback(direct = true)
	public Object[] getEnergyRequired(final Context context, final Arguments arguments) {
		OC_convertArgumentsAndLogCall(context, arguments);
		return getEnergyRequired();
	}
	
	// ComputerCraft IDynamicPeripheral methods
	@Override
	protected Object[] CC_callMethod(@Nonnull final String methodName, @Nonnull final Object[] arguments) {
		switch (methodName) {
		case "getEnergyRequired":
			return getEnergyRequired();
			
		default:
			return super.CC_callMethod(methodName, arguments);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s '%s' %s",
		                     getClass().getSimpleName(),
		                     name,
		                     Commons.format(world, pos) );
	}
}
