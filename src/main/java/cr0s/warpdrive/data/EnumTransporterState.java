package cr0s.warpdrive.data;

import javax.annotation.Nonnull;

import net.minecraft.util.IStringSerializable;

public enum EnumTransporterState implements IStringSerializable {
	
	DISABLED      ("disabled"),    // disabled
	IDLE          ("idle"),        // enabling, waiting for lock
	ACQUIRING     ("acquiring"),   // acquiring lock
	ENERGIZING    ("energizing");  // transferring entities
	
	private final String name;
	
	EnumTransporterState(final String name) {
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
