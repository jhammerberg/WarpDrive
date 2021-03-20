package li.cil.oc.api.network;

import li.cil.oc.api.Persistable;

public interface ManagedEnvironment extends Environment, Persistable {
	boolean canUpdate();
	
	void update();
}