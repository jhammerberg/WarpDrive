package cr0s.warpdrive.data;

import javax.annotation.Nonnull;
import java.util.HashMap;

import net.minecraft.util.IStringSerializable;

public enum EnumShipCoreState implements IStringSerializable {
	
	IDLE          ("idle"),           // Ready for next command
	EXECUTE        ("execute"),         // Computing parameters
	WARMING_UP    ("warming_up");     // Warm up phase
	
	private final String name;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumShipCoreState> ID_MAP = new HashMap<>();
	
	static {
		length = EnumShipCoreState.values().length;
		for (final EnumShipCoreState shipCoreState : values()) {
			ID_MAP.put(shipCoreState.ordinal(), shipCoreState);
		}
	}
	
	EnumShipCoreState(final String name) {
		this.name = name;
	}
	
	public static EnumShipCoreState get(final int id) {
		return ID_MAP.get(id);
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
