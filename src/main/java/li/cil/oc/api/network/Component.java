package li.cil.oc.api.network;

import java.util.Collection;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

public interface Component extends Node {
	String name();
	
	Visibility visibility();
	
	void setVisibility(Visibility var1);
	
	boolean canBeSeenFrom(Node var1);
	
	Collection<String> methods();
	
	Callback annotation(String var1);
	
	Object[] invoke(String var1, Context var2, Object... var3) throws Exception;
}