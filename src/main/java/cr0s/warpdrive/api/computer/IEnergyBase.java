package cr0s.warpdrive.api.computer;

import javax.annotation.Nonnull;

public interface IEnergyBase extends IInterfaced {
	
	Object[] energyDisplayUnits(@Nonnull final Object[] arguments);
	
	Object[] getEnergyStatus();
}
