package cr0s.warpdrive.data;

import javax.annotation.Nonnull;

import net.minecraft.util.IStringSerializable;

public enum EnumStickerType implements IStringSerializable {
	
	// directions
	ARROW_UP            ("arrow_up"        ),
	ARROW_DOWN          ("arrow_down"      ),
	ARROW_LEFT          ("arrow_left"      ),
	ARROW_RIGHT         ("arrow_right"     ),
	ARROW_VERTICAL      ("arrow_vertical"  ),
	ARROW_HORIZONTAL    ("arrow_horizontal"),
	ARROW_LEFT_TURN     ("arrow_left_turn" ),
	ARROW_RIGHT_TURN    ("arrow_right_turn"),
	ARROW_Y             ("arrow_y"         ),
	ARROW_CROSS         ("arrow_cross"     ),
	
	// hazards
	COLD                ("cold"            ),
	CORROSIVE           ("corrosive"       ),    // skin irritation or corrosion, metal corrosion
	ELECTRIC            ("electric"        ),
	ENVIRONMENT         ("environment"     ),    // dead fish
	EXPLOSIVE           ("explosive"       ),
	FLAMMABLE           ("flammable"       ),    // flame
	HEALTH              ("health"          ),
	HEAT                ("heat"            ),
	INFECTIOUS          ("infectious"      ),
	LASER               ("laser"           ),
	NOISE               ("noise"           ),   // noise reduction headset
	OXIDIZING           ("oxidizing"       ),   // flame over circle
	PRESSURE            ("pressure"        ),   // gaz pressure
	RADIATION           ("radiation"       ),
	TOXIC               ("toxic"           ),   // acute toxicity is skull and crossbones
	WARNING             ("warning"         ),   // exclamation mark
	;
	
	private final String name;
	
	EnumStickerType(final String name) {
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
