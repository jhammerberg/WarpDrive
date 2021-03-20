package li.cil.oc.api.network;

public interface Environment {
	Node node();
	
	void onConnect(Node var1);
	
	void onDisconnect(Node var1);
	
	void onMessage(Message var1);
}
