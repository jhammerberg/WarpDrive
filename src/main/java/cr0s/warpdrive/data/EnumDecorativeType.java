package cr0s.warpdrive.data;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum EnumDecorativeType implements IStringSerializable {
	
	PLAIN               ("plain"              ),
	GRATED              ("grated"             ),
	GLASS               ("glass"              ),
	STRIPES_BLACK_DOWN  ("stripes_black_down" ),
	STRIPES_BLACK_UP    ("stripes_black_up"   ),
	STRIPES_YELLOW_DOWN ("stripes_yellow_down"),
	STRIPES_YELLOW_UP   ("stripes_yellow_up"  ),
	;
	
	private final String name;
	
	EnumDecorativeType(final String name) {
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
