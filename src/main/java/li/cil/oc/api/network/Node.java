package li.cil.oc.api.network;

import li.cil.oc.api.Persistable;

public interface Node extends Persistable {
	Environment host();
	
	String address();
	
	Network network();
	
	Iterable<Node> reachableNodes();
	
	void connect(Node var1);
	
	void disconnect(Node var1);
	
	void remove();
	
	void sendToReachable(String var1, Object... var2);
}
