
package li.cil.oc.api;

import li.cil.oc.api.fs.Label;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;

public final class FileSystem {
	public static li.cil.oc.api.fs.FileSystem fromClass(Class<?> clazz, String domain, String root) {
		return null;
	}
	
	public static li.cil.oc.api.fs.FileSystem fromSaveDirectory(String root, long capacity, boolean buffered) {
		return null;
	}
	
	public static li.cil.oc.api.fs.FileSystem fromSaveDirectory(String root, long capacity) {
		return null;
	}
	
	public static li.cil.oc.api.fs.FileSystem fromMemory(long capacity) {
		return null;
	}
	
	public static li.cil.oc.api.fs.FileSystem asReadOnly(li.cil.oc.api.fs.FileSystem fileSystem) {
		return null;
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, Label label, EnvironmentHost host, String accessSound, int speed) {
		return null;
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, String label, EnvironmentHost host, String accessSound, int speed) {
		return null;
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, Label label, EnvironmentHost host, String accessSound) {
		return asManagedEnvironment(fileSystem, (Label)label, host, accessSound, 1);
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, String label, EnvironmentHost host, String accessSound) {
		return asManagedEnvironment(fileSystem, (String)label, host, accessSound, 1);
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, Label label) {
		return asManagedEnvironment(fileSystem, (Label)label, (EnvironmentHost)null, (String)null, 1);
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem, String label) {
		return asManagedEnvironment(fileSystem, (String)label, (EnvironmentHost)null, (String)null, 1);
	}
	
	public static ManagedEnvironment asManagedEnvironment(li.cil.oc.api.fs.FileSystem fileSystem) {
		return asManagedEnvironment(fileSystem, (Label)((Label)null), (EnvironmentHost)null, (String)null, 1);
	}
	
	private FileSystem() {
	}
}
