package cr0s.warpdrive.api.computer;

import javax.annotation.Nonnull;

public interface IMachine extends IInterfaced {
	
	String[] name(@Nonnull final Object[] arguments);
	
	boolean getIsEnabled();
	
	Object[] enable(@Nonnull final Object[] arguments);
	
	Object[] getAssemblyStatus();
	
	boolean isAssemblyValid();
}
