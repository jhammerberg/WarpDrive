package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumHullPlainType;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockHullSlab extends BlockAbstractBase implements IBlockBase, IDamageReceiver {
	
	// Metadata values are
	// 0-5 for plain slabs orientations
	// 6-11 for tiled slabs orientations
	// 12 for plain double slab
	// 13-15 for tiled double slabs
	
	private static final VoxelShape SHAPE_HALF_DOWN   = VoxelShapes.create(0.00D, 0.00D, 0.00D, 1.00D, 0.50D, 1.00D);
	private static final VoxelShape SHAPE_HALF_UP     = VoxelShapes.create(0.00D, 0.50D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_NORTH  = VoxelShapes.create(0.00D, 0.00D, 0.00D, 1.00D, 1.00D, 0.50D);
	private static final VoxelShape SHAPE_HALF_SOUTH  = VoxelShapes.create(0.00D, 0.00D, 0.50D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_EAST   = VoxelShapes.create(0.00D, 0.00D, 0.00D, 0.50D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_WEST   = VoxelShapes.create(0.50D, 0.00D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_FULL        = VoxelShapes.fullCube();
	
	public static final EnumProperty<EnumType> TYPE = EnumProperty.create("type", EnumType.class);
	
	final EnumHullPlainType hullPlainType;
	final int               indexColor;
	
	public BlockHullSlab(@Nonnull final String registryName,
	                     @Nonnull final BlockState blockStateHull) {
		super(Block.Properties.from(blockStateHull.getBlock()),
		      registryName, ((BlockHullPlain) blockStateHull.getBlock()).getTier());
		
		this.hullPlainType = ((BlockHullPlain) blockStateHull.getBlock()).hullPlainType;
		this.indexColor    = ((BlockHullPlain) blockStateHull.getBlock()).indexColor;
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(TYPE, EnumType.DOWN)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TYPE);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                           @Nonnull final ISelectionContext selectionContext) {
		return blockState.get(TYPE).getVoxelShape();
	}
	
	@Override
	public BlockState getStateForPlacement(@Nonnull final BlockItemUseContext context) {
		final Direction facing = context.getFace();
		final float hitX = (float) context.getHitVec().x - context.getPos().getX();
		final float hitY = (float) context.getHitVec().y - context.getPos().getY();
		final float hitZ = (float) context.getHitVec().z - context.getPos().getZ();
		
		final int metadata = context.getItem().getDamage();
		final BlockState blockState = getDefaultState();
		
		// full block?
		if (blockState.get(TYPE).getIsDouble()) {
			return blockState;
		}
		
		// horizontal slab?
		if (metadata == 0) {
			// reuse vanilla logic
			final EnumType variant = (facing != Direction.DOWN && (facing == Direction.UP || hitY <= 0.5F) ? EnumType.DOWN : EnumType.UP);
			return blockState.with(TYPE, variant);
		}
		// vertical slab?
		if (metadata == 2) {
			if (facing != Direction.DOWN && facing != Direction.UP) {
				switch(facing) {
				case NORTH: return blockState.with(TYPE, EnumType.SOUTH);
				case SOUTH: return blockState.with(TYPE, EnumType.NORTH);
				case WEST : return blockState.with(TYPE, EnumType.EAST );
				case EAST : return blockState.with(TYPE, EnumType.WEST );
				}
			}
			// is X the furthest away from center?
			if (Math.abs(hitX - 0.5F) > Math.abs(hitZ - 0.5F)) {
				// west (4) vs east (5)
				final EnumType variant = hitX > 0.5F ? EnumType.EAST : EnumType.WEST;
				return blockState.with(TYPE, variant);
			}
			// north (2) vs south (3)
			final EnumType variant = hitZ > 0.5F ? EnumType.SOUTH : EnumType.NORTH;
			return blockState.with(TYPE, variant);
		}
		return getStateById(metadata);
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockHullSlab(this);
	}
	
	@Override
	public float getBlockHardness(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                              @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		// TODO: adjust hardness to damage type/color
		return WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()];
	}
	
	@Override
	public int applyDamage(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                       @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		if (damageLevel <= 0) {
			return 0;
		}
		if (enumTier == EnumTier.BASIC) {
			world.removeBlock(blockPos, false);
		} else {
			world.setBlockState(blockPos, WarpDrive.blockHulls_slab[enumTier.getIndex() - 1][hullPlainType.ordinal()][indexColor]
			                              .getDefaultState()
			                              .with(TYPE, blockState.get(TYPE)), 2);
		}
		return 0;
	}
	
	public enum EnumType implements IStringSerializable {
		DOWN  ("down"  , false, Direction.DOWN , SHAPE_HALF_DOWN ),
		UP    ("up"    , false, Direction.UP   , SHAPE_HALF_UP   ),
		NORTH ("north" , false, Direction.NORTH, SHAPE_HALF_NORTH),
		SOUTH ("south" , false, Direction.SOUTH, SHAPE_HALF_SOUTH),
		WEST  ("west"  , false, Direction.WEST , SHAPE_HALF_EAST ),
		EAST  ("east"  , false, Direction.EAST , SHAPE_HALF_WEST ),
		
		FULL_X("full_x", true , Direction.DOWN , SHAPE_FULL),
		FULL_Y("full_y", true , Direction.DOWN , SHAPE_FULL),
		FULL_Z("full_z", true , Direction.DOWN , SHAPE_FULL);
		
		private final String name;
		private final boolean isDouble;
		private final Direction facing;
		private final VoxelShape voxelShape;
		
		// cached values
		public static final int length;
		private static final HashMap<Integer, EnumType> ID_MAP = new HashMap<>();
		
		static {
			length = EnumType.values().length;
			for (final EnumType variant : values()) {
				ID_MAP.put(variant.ordinal(), variant);
			}
		}
		
		EnumType(@Nonnull final String name, final boolean isDouble, @Nonnull final Direction facing,
		         @Nonnull final VoxelShape voxelShape) {
			this.name = name;
			this.isDouble = isDouble;
			this.facing = facing;
			this.voxelShape = voxelShape;
		}
		
		public static EnumType get(final int metadata) {
			return ID_MAP.get(metadata);
		}
		
		@Nonnull
		@Override
		public String getName()
		{
			return name;
		}
		
		public boolean getIsDouble()
		{
			return isDouble;
		}
		
		public Direction getFacing() {
			return facing;
		}
		
		public VoxelShape getVoxelShape() {
			return voxelShape;
		}
	}
}