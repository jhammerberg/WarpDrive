package li.cil.oc.api.network;

public interface Message {
	Node source();
	
	String name();
	
	Object[] data();
	
	void cancel();
}