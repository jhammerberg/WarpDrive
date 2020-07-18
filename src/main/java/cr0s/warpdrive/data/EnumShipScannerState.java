package cr0s.warpdrive.data;

import javax.annotation.Nonnull;

import net.minecraft.util.IStringSerializable;

public enum EnumShipScannerState implements IStringSerializable {
	
	IDLE          ("idle"),           // Ready for next command
	SCANNING      ("scanning"),       // Scanning a ship
	DEPLOYING     ("online");         // Deploying a ship
	
	private final String name;
	
	EnumShipScannerState(final String name) {
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
