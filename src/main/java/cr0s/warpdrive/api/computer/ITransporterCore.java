package cr0s.warpdrive.api.computer;

import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IGlobalRegionProvider;

import javax.annotation.Nonnull;

public interface ITransporterCore extends IEnergyConsumer, IBeamFrequency, IGlobalRegionProvider {
	
	Object[] state();
	
	Object[] remoteLocation(@Nonnull final Object[] arguments);
	
	Object[] lock(@Nonnull final Object[] arguments);
	
	Object[] energyFactor(@Nonnull final Object[] arguments);
	
	Object[] getLockStrength();
	
	Object[] energize(@Nonnull final Object[] arguments);
}
