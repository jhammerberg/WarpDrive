package cr0s.warpdrive.api.computer;

import javax.annotation.Nonnull;

public interface ITransporterBeacon extends IEnergyConsumer {
	
	Boolean[] isActive(@Nonnull final Object[] arguments);
	
	boolean isActive();
	
	void energizeDone();
}
