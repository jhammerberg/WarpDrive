package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
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
import net.minecraft.item.DyeColor;
import net.minecraft.item.Rarity;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockHullSlab extends BlockAbstractBase implements IBlockBase, IDamageReceiver {
	
	// Metadata values are
	// 0-5 for plain slabs orientations
	// 6-11 for tiled slabs orientations
	// 12 for plain double slab
	// 13-15 for tiled double slabs
	
	private static final VoxelShape SHAPE_HALF_DOWN   = makeCuboidShape(0.00D, 0.00D, 0.00D, 1.00D, 0.50D, 1.00D);
	private static final VoxelShape SHAPE_HALF_UP     = makeCuboidShape(0.00D, 0.50D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_NORTH  = makeCuboidShape(0.00D, 0.00D, 0.00D, 1.00D, 1.00D, 0.50D);
	private static final VoxelShape SHAPE_HALF_SOUTH  = makeCuboidShape(0.00D, 0.00D, 0.50D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_EAST   = makeCuboidShape(0.00D, 0.00D, 0.00D, 0.50D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_HALF_WEST   = makeCuboidShape(0.50D, 0.00D, 0.00D, 1.00D, 1.00D, 1.00D);
	private static final VoxelShape SHAPE_FULL        = VoxelShapes.fullCube();
	
	public static final EnumProperty<EnumVariant> VARIANT = EnumProperty.create("variant", EnumVariant.class);
	
	final EnumTier enumTier;
	final int      indexColor;
	
	public BlockHullSlab(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final DyeColor enumDyeColor, @Nonnull final BlockState blockStateHull) {
		super(Block.Properties.from(blockStateHull.getBlock()),
		      registryName, enumTier);
		
		this.enumTier = enumTier;
		this.indexColor = enumDyeColor.getId();
		
		setDefaultState(getDefaultState()
				                .with(VARIANT, EnumVariant.PLAIN_DOWN)
		               );
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
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return getBlockBoundsFromState(blockState);
	}
	
	private VoxelShape getBlockBoundsFromState(final BlockState blockState) {
		if (blockState == null) {
			return SHAPE_FULL;
		}
		return blockState.get(VARIANT).getVoxelShape();
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
		if (blockState.get(VARIANT).getIsDouble()) {
			return blockState;
		}
		
		// horizontal slab?
		if (metadata == 0) {
			// reuse vanilla logic
			final EnumVariant variant = (facing != Direction.DOWN && (facing == Direction.UP || hitY <= 0.5F) ? EnumVariant.PLAIN_DOWN : EnumVariant.PLAIN_UP);
			return blockState.with(VARIANT, variant);
		} else if (metadata == 6) {
			// reuse vanilla logic
			final EnumVariant variant = (facing != Direction.DOWN && (facing == Direction.UP || hitY <= 0.5F) ? EnumVariant.TILED_DOWN : EnumVariant.TILED_UP);
			return blockState.with(VARIANT, variant);
		}
		// vertical slab?
		if (metadata == 2) {
			if (facing != Direction.DOWN && facing != Direction.UP) {
				switch(facing) {
				case NORTH: return blockState.with(VARIANT, EnumVariant.PLAIN_SOUTH);
				case SOUTH: return blockState.with(VARIANT, EnumVariant.PLAIN_NORTH);
				case WEST: return blockState.with(VARIANT, EnumVariant.PLAIN_EAST);
				case EAST: return blockState.with(VARIANT, EnumVariant.PLAIN_WEST);
				}
			}
			// is X the furthest away from center?
			if (Math.abs(hitX - 0.5F) > Math.abs(hitZ - 0.5F)) {
				// west (4) vs east (5)
				final EnumVariant variant = hitX > 0.5F ? EnumVariant.PLAIN_EAST : EnumVariant.PLAIN_WEST;
				return blockState.with(VARIANT, variant);
			}
			// north (2) vs south (3)
			final EnumVariant variant = hitZ > 0.5F ? EnumVariant.PLAIN_SOUTH : EnumVariant.PLAIN_NORTH;
			return blockState.with(VARIANT, variant);
		}
		if (metadata == 8) {
			if (facing != Direction.DOWN && facing != Direction.UP) {
				switch(facing) {
				case NORTH: return blockState.with(VARIANT, EnumVariant.TILED_SOUTH);
				case SOUTH: return blockState.with(VARIANT, EnumVariant.TILED_NORTH);
				case WEST: return blockState.with(VARIANT, EnumVariant.TILED_EAST);
				case EAST: return blockState.with(VARIANT, EnumVariant.TILED_WEST);
				}
			}
			// is X the furthest away from center?
			if (Math.abs(hitX - 0.5F) > Math.abs(hitZ - 0.5F)) {
				// west (4) vs east (5)
				final EnumVariant variant = hitX > 0.5F ? EnumVariant.TILED_EAST : EnumVariant.TILED_WEST;
				return blockState.with(VARIANT, variant);
			}
			// north (2) vs south (3)
			final EnumVariant variant = hitZ > 0.5F ? EnumVariant.TILED_SOUTH : EnumVariant.TILED_NORTH;
			return blockState.with(VARIANT, variant);
		}
		return getStateById(metadata);
	}
	
	@Nonnull
	@Override
	public EnumTier getTier() {
		return enumTier;
	}
	
	@Nonnull
	@Override
	public Rarity getRarity() {
		return getTier().getRarity();
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockHullSlab(this);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		// no operation
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
			world.setBlockState(blockPos, WarpDrive.blockHulls_slab[enumTier.getIndex() - 1][indexColor]
			                              .getDefaultState()
			                              .with(VARIANT, blockState.get(VARIANT)), 2);
		}
		return 0;
	}
	
	public enum EnumVariant implements IStringSerializable {
		PLAIN_DOWN  ("plain_down"  , false, true , Direction.DOWN , SHAPE_HALF_DOWN),
		PLAIN_UP    ("plain_up"    , false, true , Direction.UP   , SHAPE_HALF_UP),
		PLAIN_NORTH ("plain_north" , false, true , Direction.NORTH, SHAPE_HALF_NORTH),
		PLAIN_SOUTH ("plain_south" , false, true , Direction.SOUTH, SHAPE_HALF_SOUTH),
		PLAIN_WEST  ("plain_west"  , false, true , Direction.WEST , SHAPE_HALF_EAST),
		PLAIN_EAST  ("plain_east"  , false, true , Direction.EAST , SHAPE_HALF_WEST),
		
		TILED_DOWN  ("tiled_down"  , false, false, Direction.DOWN , SHAPE_HALF_DOWN),
		TILED_UP    ("tiled_up"    , false, false, Direction.UP   , SHAPE_HALF_UP),
		TILED_NORTH ("tiled_north" , false, false, Direction.NORTH, SHAPE_HALF_NORTH),
		TILED_SOUTH ("tiled_south" , false, false, Direction.SOUTH, SHAPE_HALF_SOUTH),
		TILED_WEST  ("tiled_west"  , false, false, Direction.WEST , SHAPE_HALF_EAST),
		TILED_EAST  ("tiled_east"  , false, false, Direction.EAST , SHAPE_HALF_WEST),
		
		PLAIN_FULL  ("plain_full"  , true , true , Direction.DOWN , SHAPE_FULL),
		TILED_FULL_X("tiled_full_x", true , false, Direction.DOWN , SHAPE_FULL),
		TILED_FULL_Y("tiled_full_y", true , false, Direction.DOWN , SHAPE_FULL),
		TILED_FULL_Z("tiled_full_z", true , false, Direction.DOWN , SHAPE_FULL);
		
		private final String name;
		private final boolean isDouble;
		private final boolean isPlain;
		private final Direction facing;
		private final VoxelShape voxelShape;
		
		// cached values
		public static final int length;
		private static final HashMap<Integer, EnumVariant> ID_MAP = new HashMap<>();
		
		static {
			length = EnumVariant.values().length;
			for (final EnumVariant variant : values()) {
				ID_MAP.put(variant.ordinal(), variant);
			}
		}
		
		EnumVariant(@Nonnull final String name, final boolean isDouble, final boolean isPlain, @Nonnull final Direction facing,
		            @Nonnull final VoxelShape voxelShape) {
			this.name = name;
			this.isDouble = isDouble;
			this.isPlain = isPlain;
			this.facing = facing;
			this.voxelShape = voxelShape;
		}
		
		public static EnumVariant get(final int metadata) {
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
		
		public boolean getIsPlain()
		{
			return isPlain;
		}
		
		public Direction getFacing() {
			return facing;
		}
		
		public VoxelShape getVoxelShape() {
			return voxelShape;
		}
	}
}
