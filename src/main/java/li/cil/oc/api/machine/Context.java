package li.cil.oc.api.machine;

import li.cil.oc.api.network.Node;

public interface Context {
	Node node();
	
	boolean canInteract(String var1);
	
	boolean isRunning();
	
	boolean isPaused();
	
	boolean start();
	
	boolean pause(double var1);
	
	boolean stop();
	
	void consumeCallBudget(double var1);
	
	boolean signal(String var1, Object... var2);
}
