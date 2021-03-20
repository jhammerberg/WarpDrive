package cr0s.warpdrive.data;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class BlockProperties {
	
	// Common block properties
	public static final BooleanProperty                      ACTIVE               = BooleanProperty.create("active");
	public static final DirectionProperty                    FACING               = BlockStateProperties.FACING;
	public static final DirectionProperty                    FACING_HORIZONTAL    = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final EnumProperty<EnumHorizontalSpinning> HORIZONTAL_SPINNING  = EnumProperty.create("spinning", EnumHorizontalSpinning.class);
	
}