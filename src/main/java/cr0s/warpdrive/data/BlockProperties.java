package cr0s.warpdrive.data;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;

public class BlockProperties {
	
	// Common block properties
	public static final BooleanProperty                      ACTIVE               = BooleanProperty.create("active");
	public static final BooleanProperty                      CONNECTED            = BooleanProperty.create("connected");
	public static final DirectionProperty                    FACING               = DirectionProperty.create("facing");
	public static final DirectionProperty                    FACING_HORIZONTAL    = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final EnumProperty<EnumHorizontalSpinning> HORIZONTAL_SPINNING  = EnumProperty.create("spinning", EnumHorizontalSpinning.class);
	
}
