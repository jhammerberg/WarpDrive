package cr0s.warpdrive.data;

import javax.annotation.Nonnull;
import java.util.HashMap;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public enum EnumHorizontalSpinning implements IStringSerializable {
	
	DOWN_NORTH(Direction.DOWN , "down_north", Direction.NORTH),
	DOWN_SOUTH(Direction.DOWN , "down_south", Direction.SOUTH),
	DOWN_WEST (Direction.DOWN , "down_west" , Direction.WEST ),
	DOWN_EAST (Direction.DOWN , "down_east" , Direction.EAST ),
	UP_NORTH  (Direction.UP   , "up_north"  , Direction.NORTH),
	UP_SOUTH  (Direction.UP   , "up_south"  , Direction.SOUTH),
	UP_WEST   (Direction.UP   , "up_west"   , Direction.WEST ),
	UP_EAST   (Direction.UP   , "up_east"   , Direction.EAST ),
	NORTH     (Direction.NORTH, "north"     , Direction.NORTH),
	SOUTH     (Direction.SOUTH, "south"     , Direction.SOUTH),
	WEST      (Direction.WEST , "west"      , Direction.WEST ),
	EAST      (Direction.EAST , "east"      , Direction.EAST );
	
	public final Direction facing;
	public final String     name;
	public final Direction spinning;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumHorizontalSpinning> ID_MAP = new HashMap<>();
	
	static {
		length = EnumHorizontalSpinning.values().length;
		for (final EnumHorizontalSpinning cameraType : values()) {
			ID_MAP.put(cameraType.ordinal(), cameraType);
		}
	}
	
	EnumHorizontalSpinning(@Nonnull final Direction facing, @Nonnull final String name, @Nonnull final Direction spinning) {
		this.facing = facing;
		this.name = name;
		this.spinning = spinning;
	}
	
	public static EnumHorizontalSpinning get(final int id) {
		return ID_MAP.get(id);
	}
	
	public static EnumHorizontalSpinning get(@Nonnull final Direction facing, @Nonnull final Direction spinning) throws RuntimeException {
		// enforce spinning for vertical orientations
		final Direction spinningCorrected;
		if (facing.getYOffset() != 0) {
			spinningCorrected = spinning;
		} else {
			spinningCorrected = facing;
		}
		
		// find the right combo
		for (final EnumHorizontalSpinning enumHorizontalSpinning : EnumHorizontalSpinning.values()) {
			if ( enumHorizontalSpinning.facing.equals(facing)
			  && enumHorizontalSpinning.spinning.equals(spinningCorrected) ) {
				return enumHorizontalSpinning;
			}
		}
		throw new RuntimeException(String.format("There's no HorizontalSpinning with facing %s spinning %s",
		                                         facing, spinning ));
	}
	
	@Nonnull
	@Override
	public String getName() { return name; }
}
