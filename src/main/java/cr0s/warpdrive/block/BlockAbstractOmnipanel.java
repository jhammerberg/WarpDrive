package cr0s.warpdrive.block;

import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.event.ModelBakeEventHandler;
import cr0s.warpdrive.render.BakedModelOmnipanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockAbstractOmnipanel extends BlockAbstractBase {
	
	public static final float CENTER_MIN = 7.0F / 16.0F;
	public static final float CENTER_MAX = 9.0F / 16.0F;
	
	protected static VoxelShape AABB_XN_YN = makeCuboidShape(0.0F, 0.0F, CENTER_MIN, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_XP_YN = makeCuboidShape(CENTER_MIN, 0.0F, CENTER_MIN, 1.0F, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_XN_YP = makeCuboidShape(0.0F, CENTER_MIN, CENTER_MIN, CENTER_MAX, 1.0F, CENTER_MAX);
	protected static VoxelShape AABB_XP_YP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, 1.0F, 1.0F, CENTER_MAX);
	
	protected static VoxelShape AABB_ZN_YN = makeCuboidShape(CENTER_MIN, 0.0F, 0.0F, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_ZP_YN = makeCuboidShape(CENTER_MIN, 0.0F, CENTER_MIN, CENTER_MAX, CENTER_MAX, 1.0F);
	protected static VoxelShape AABB_ZN_YP = makeCuboidShape(CENTER_MIN, CENTER_MIN, 0.0F, CENTER_MAX, 1.0F, CENTER_MAX);
	protected static VoxelShape AABB_ZP_YP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, CENTER_MAX, 1.0F, 1.0F);
	
	protected static VoxelShape AABB_XN_ZN = makeCuboidShape(0.0F, CENTER_MIN, 0.0F, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_XP_ZN = makeCuboidShape(CENTER_MIN, CENTER_MIN, 0.0F, 1.0F, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_XN_ZP = makeCuboidShape(0.0F, CENTER_MIN, CENTER_MIN, CENTER_MAX, CENTER_MAX, 1.0F);
	protected static VoxelShape AABB_XP_ZP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, 1.0F, CENTER_MAX, 1.0F);
	
	protected static VoxelShape AABB_YN = makeCuboidShape(CENTER_MIN, 0.0F, CENTER_MIN, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_YP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, CENTER_MAX, 1.0F, CENTER_MAX);
	protected static VoxelShape AABB_ZN = makeCuboidShape(CENTER_MIN, CENTER_MIN, 0.0F, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_ZP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, CENTER_MAX, CENTER_MAX, 1.0F);
	protected static VoxelShape AABB_XN = makeCuboidShape(0.0F, CENTER_MIN, CENTER_MIN, CENTER_MAX, CENTER_MAX, CENTER_MAX);
	protected static VoxelShape AABB_XP = makeCuboidShape(CENTER_MIN, CENTER_MIN, CENTER_MIN, 1.0F, CENTER_MAX, CENTER_MAX);
	
	public static final IProperty<Boolean> CAN_CONNECT_Y_NEG  = BooleanProperty.create("canConnectY_neg");
	public static final IProperty<Boolean> CAN_CONNECT_Y_POS  = BooleanProperty.create("canConnectY_pos");
	public static final IProperty<Boolean> CAN_CONNECT_Z_NEG  = BooleanProperty.create("canConnectZ_neg");
	public static final IProperty<Boolean> CAN_CONNECT_Z_POS  = BooleanProperty.create("canConnectZ_pos");
	public static final IProperty<Boolean> CAN_CONNECT_X_NEG  = BooleanProperty.create("canConnectX_neg");
	public static final IProperty<Boolean> CAN_CONNECT_X_POS  = BooleanProperty.create("canConnectX_pos");
	
	public static final IProperty<Boolean> HAS_XN_YN  = BooleanProperty.create("hasXnYn");
	public static final IProperty<Boolean> HAS_XP_YN  = BooleanProperty.create("hasXpYn");
	public static final IProperty<Boolean> HAS_XN_YP  = BooleanProperty.create("hasXnYp");
	public static final IProperty<Boolean> HAS_XP_YP  = BooleanProperty.create("hasXpYp");
	public static final IProperty<Boolean> HAS_XN_ZN  = BooleanProperty.create("hasXnZn");
	public static final IProperty<Boolean> HAS_XP_ZN  = BooleanProperty.create("hasXpZn");
	public static final IProperty<Boolean> HAS_XN_ZP  = BooleanProperty.create("hasXnZp");
	public static final IProperty<Boolean> HAS_XP_ZP  = BooleanProperty.create("hasXpZp");
	public static final IProperty<Boolean> HAS_ZN_YN  = BooleanProperty.create("hasZnYn");
	public static final IProperty<Boolean> HAS_ZP_YN  = BooleanProperty.create("hasZpYn");
	public static final IProperty<Boolean> HAS_ZN_YP  = BooleanProperty.create("hasZnYp");
	public static final IProperty<Boolean> HAS_ZP_YP  = BooleanProperty.create("hasZpYp");
	
	public BlockAbstractOmnipanel(@Nonnull final Block.Properties blockProperties, @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties, registryName, enumTier);
		setDefaultState(getDefaultState()
				.with(CAN_CONNECT_Y_NEG, false)
				.with(CAN_CONNECT_Y_POS, false)
				.with(CAN_CONNECT_Z_NEG, false)
				.with(CAN_CONNECT_Z_POS, false)
				.with(CAN_CONNECT_X_NEG, false)
				.with(CAN_CONNECT_X_POS, false)
				.with(HAS_XN_YN, false)
				.with(HAS_XP_YN, false)
				.with(HAS_XN_YP, false)
				.with(HAS_XP_YP, false)
				.with(HAS_XN_ZN, false)
				.with(HAS_XP_ZN, false)
				.with(HAS_XN_ZP, false)
				.with(HAS_XP_ZP, false)
				.with(HAS_ZN_YN, false)
				.with(HAS_ZP_YN, false)
				.with(HAS_ZN_YP, false)
				.with(HAS_ZP_YP, false) );
	}
	
	@Nonnull
	// TODO MC1.15 Omnipanel rendering
	public BlockState getExtendedState(@Nonnull final BlockState blockState, final IWorldReader worldReader, final BlockPos blockPos) {
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(blockPos);
		
		// get direct connections
		final int maskConnectY_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN);
		final int maskConnectY_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP);
		final int maskConnectZ_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH);
		final int maskConnectZ_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH);
		final int maskConnectX_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ()    ), Direction.WEST);
		final int maskConnectX_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ()    ), Direction.EAST);
		
		final boolean canConnectY_neg = maskConnectY_neg > 0;
		final boolean canConnectY_pos = maskConnectY_pos > 0;
		final boolean canConnectZ_neg = maskConnectZ_neg > 0;
		final boolean canConnectZ_pos = maskConnectZ_pos > 0;
		final boolean canConnectX_neg = maskConnectX_neg > 0;
		final boolean canConnectX_pos = maskConnectX_pos > 0;
		final boolean canConnectNone = !canConnectY_neg && !canConnectY_pos && !canConnectZ_neg && !canConnectZ_pos && !canConnectX_neg && !canConnectX_pos;
		
		// get diagonal connections
		final boolean canConnectXn_Y_neg = (maskConnectX_neg > 1 && maskConnectY_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN) > 0;
		final boolean canConnectXn_Y_pos = (maskConnectX_neg > 1 && maskConnectY_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP) > 0;
		final boolean canConnectXn_Z_neg = (maskConnectX_neg > 1 && maskConnectZ_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH) > 0;
		final boolean canConnectXn_Z_pos = (maskConnectX_neg > 1 && maskConnectZ_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH) > 0;
		final boolean canConnectZn_Y_neg = (maskConnectZ_neg > 1 && maskConnectY_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ() - 1), Direction.DOWN) > 0;
		final boolean canConnectZn_Y_pos = (maskConnectZ_neg > 1 && maskConnectY_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ() - 1), Direction.UP) > 0;
		
		final boolean canConnectXp_Y_neg = (maskConnectX_pos > 1 && maskConnectY_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN) > 0;
		final boolean canConnectXp_Y_pos = (maskConnectX_pos > 1 && maskConnectY_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP) > 0;
		final boolean canConnectXp_Z_neg = (maskConnectX_pos > 1 && maskConnectZ_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH) > 0;
		final boolean canConnectXp_Z_pos = (maskConnectX_pos > 1 && maskConnectZ_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH) > 0;
		final boolean canConnectZp_Y_neg = (maskConnectZ_pos > 1 && maskConnectY_neg > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ() + 1), Direction.DOWN) > 0;
		final boolean canConnectZp_Y_pos = (maskConnectZ_pos > 1 && maskConnectY_pos > 1) || getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ() + 1), Direction.UP) > 0;
		
		// get panels
		final boolean hasXnYn = canConnectNone || (canConnectX_neg && canConnectY_neg && canConnectXn_Y_neg);
		final boolean hasXpYn = canConnectNone || (canConnectX_pos && canConnectY_neg && canConnectXp_Y_neg);
		final boolean hasXnYp = canConnectNone || (canConnectX_neg && canConnectY_pos && canConnectXn_Y_pos);
		final boolean hasXpYp = canConnectNone || (canConnectX_pos && canConnectY_pos && canConnectXp_Y_pos);
		
		final boolean hasXnZn = canConnectNone || (canConnectX_neg && canConnectZ_neg && canConnectXn_Z_neg);
		final boolean hasXpZn = canConnectNone || (canConnectX_pos && canConnectZ_neg && canConnectXp_Z_neg);
		final boolean hasXnZp = canConnectNone || (canConnectX_neg && canConnectZ_pos && canConnectXn_Z_pos);
		final boolean hasXpZp = canConnectNone || (canConnectX_pos && canConnectZ_pos && canConnectXp_Z_pos);
		
		final boolean hasZnYn = canConnectNone || (canConnectZ_neg && canConnectY_neg && canConnectZn_Y_neg);
		final boolean hasZpYn = canConnectNone || (canConnectZ_pos && canConnectY_neg && canConnectZp_Y_neg);
		final boolean hasZnYp = canConnectNone || (canConnectZ_neg && canConnectY_pos && canConnectZn_Y_pos);
		final boolean hasZpYp = canConnectNone || (canConnectZ_pos && canConnectY_pos && canConnectZp_Y_pos);
		
		// build extended state
		return blockState
				.with(CAN_CONNECT_Y_NEG, canConnectY_neg)
				.with(CAN_CONNECT_Y_POS, canConnectY_pos)
				.with(CAN_CONNECT_Z_NEG, canConnectZ_neg)
				.with(CAN_CONNECT_Z_POS, canConnectZ_pos)
				.with(CAN_CONNECT_X_NEG, canConnectX_neg)
				.with(CAN_CONNECT_X_POS, canConnectX_pos)
				.with(HAS_XN_YN, hasXnYn)
				.with(HAS_XP_YN, hasXpYn)
				.with(HAS_XN_YP, hasXnYp)
				.with(HAS_XP_YP, hasXpYp)
				.with(HAS_XN_ZN, hasXnZn)
				.with(HAS_XP_ZN, hasXpZn)
				.with(HAS_XN_ZP, hasXnZp)
				.with(HAS_XP_ZP, hasXpZp)
				.with(HAS_ZN_YN, hasZnYn)
				.with(HAS_ZP_YN, hasZpYn)
				.with(HAS_ZN_YP, hasZnYp)
				.with(HAS_ZP_YP, hasZpYp);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		super.modelInitialisation();
		
		// register (smart) baked model
		final ResourceLocation registryName = getRegistryName();
		assert registryName != null;
		ModelBakeEventHandler.registerBakedModel(new ModelResourceLocation(registryName.toString()), BakedModelOmnipanel.class);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return super.getCollisionShape(blockState, blockReader, blockPos, selectionContext);
	}
	
	@SuppressWarnings("deprecation")
	// TODO MC1.15 Omnipanel collision
	public void addCollisionBoxToList(final BlockState blockState, final @Nonnull World world, final @Nonnull BlockPos blockPos,
	                                  @Nonnull final AxisAlignedBB entityBox, final @Nonnull List<AxisAlignedBB> collidingBoxes,
	                                  final @Nullable Entity entity, final boolean isActualState) {
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(blockPos);
		
		// get direct connections
		final int maskConnectY_neg = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN);
		final int maskConnectY_pos = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP);
		final int maskConnectZ_neg = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH);
		final int maskConnectZ_pos = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH);
		final int maskConnectX_neg = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ()    ), Direction.WEST);
		final int maskConnectX_pos = getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ()    ), Direction.EAST);
		
		final boolean canConnectY_neg = maskConnectY_neg > 0;
		final boolean canConnectY_pos = maskConnectY_pos > 0;
		final boolean canConnectZ_neg = maskConnectZ_neg > 0;
		final boolean canConnectZ_pos = maskConnectZ_pos > 0;
		final boolean canConnectX_neg = maskConnectX_neg > 0;
		final boolean canConnectX_pos = maskConnectX_pos > 0;
		final boolean canConnectNone = !canConnectY_neg && !canConnectY_pos && !canConnectZ_neg && !canConnectZ_pos && !canConnectX_neg && !canConnectX_pos;
		
		// get diagonal connections
		final boolean canConnectXn_Y_neg = (maskConnectX_neg > 1 && maskConnectY_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN) > 0;
		final boolean canConnectXn_Y_pos = (maskConnectX_neg > 1 && maskConnectY_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP) > 0;
		final boolean canConnectXn_Z_neg = (maskConnectX_neg > 1 && maskConnectZ_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH) > 0;
		final boolean canConnectXn_Z_pos = (maskConnectX_neg > 1 && maskConnectZ_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH) > 0;
		final boolean canConnectZn_Y_neg = (maskConnectZ_neg > 1 && maskConnectY_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ() - 1), Direction.DOWN) > 0;
		final boolean canConnectZn_Y_pos = (maskConnectZ_neg > 1 && maskConnectY_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ() - 1), Direction.UP) > 0;
		
		final boolean canConnectXp_Y_neg = (maskConnectX_pos > 1 && maskConnectY_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN) > 0;
		final boolean canConnectXp_Y_pos = (maskConnectX_pos > 1 && maskConnectY_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP) > 0;
		final boolean canConnectXp_Z_neg = (maskConnectX_pos > 1 && maskConnectZ_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH) > 0;
		final boolean canConnectXp_Z_pos = (maskConnectX_pos > 1 && maskConnectZ_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH) > 0;
		final boolean canConnectZp_Y_neg = (maskConnectZ_pos > 1 && maskConnectY_neg > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ() + 1), Direction.DOWN) > 0;
		final boolean canConnectZp_Y_pos = (maskConnectZ_pos > 1 && maskConnectY_pos > 1) || getConnectionMask(world, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ() + 1), Direction.UP) > 0;
		
		// get panels
		final boolean hasXnYn = canConnectNone || (canConnectX_neg && canConnectY_neg && canConnectXn_Y_neg);
		final boolean hasXpYn = canConnectNone || (canConnectX_pos && canConnectY_neg && canConnectXp_Y_neg);
		final boolean hasXnYp = canConnectNone || (canConnectX_neg && canConnectY_pos && canConnectXn_Y_pos);
		final boolean hasXpYp = canConnectNone || (canConnectX_pos && canConnectY_pos && canConnectXp_Y_pos);
		
		final boolean hasXnZn = canConnectNone || (canConnectX_neg && canConnectZ_neg && canConnectXn_Z_neg);
		final boolean hasXpZn = canConnectNone || (canConnectX_pos && canConnectZ_neg && canConnectXp_Z_neg);
		final boolean hasXnZp = canConnectNone || (canConnectX_neg && canConnectZ_pos && canConnectXn_Z_pos);
		final boolean hasXpZp = canConnectNone || (canConnectX_pos && canConnectZ_pos && canConnectXp_Z_pos);
		
		final boolean hasZnYn = canConnectNone || (canConnectZ_neg && canConnectY_neg && canConnectZn_Y_neg);
		final boolean hasZpYn = canConnectNone || (canConnectZ_pos && canConnectY_neg && canConnectZp_Y_neg);
		final boolean hasZnYp = canConnectNone || (canConnectZ_neg && canConnectY_pos && canConnectZn_Y_pos);
		final boolean hasZpYp = canConnectNone || (canConnectZ_pos && canConnectY_pos && canConnectZp_Y_pos);
		
		{// z plane
			if (hasXnYn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XN_YN);
			}
			
			if (hasXpYn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XP_YN);
			}
			
			if (hasXnYp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XN_YP);
			}
			
			if (hasXpYp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XP_YP);
			}
		}
		
		{// x plane
			if (hasZnYn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZN_YN);
			}
			
			if (hasZpYn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZP_YN);
			}
			
			if (hasZnYp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZN_YP);
			}
			
			if (hasZpYp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZP_YP);
			}
		}
		
		{// z plane
			if (hasXnZn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XN_ZN);
			}
			
			if (hasXpZn) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XP_ZN);
			}
			
			if (hasXnZp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XN_ZP);
			}
			
			if (hasXpZp) {
				addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XP_ZP);
			}
		}
		
		// central nodes
		if (canConnectY_neg && !hasXnYn && !hasXpYn && !hasZnYn && !hasZpYn) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_YN);
		}
		if (canConnectY_pos && !hasXnYp && !hasXpYp && !hasZnYp && !hasZpYp) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_YP);
		}
		if (canConnectZ_neg && !hasXnZn && !hasXpZn && !hasZnYn && !hasZnYp) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZN);
		}
		if (canConnectZ_pos && !hasXnZp && !hasXpZp && !hasZpYn && !hasZpYp) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_ZP);
		}
		if (canConnectX_neg && !hasXnYn && !hasXnYp && !hasXnZn && !hasXnZp) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XN);
		}
		if (canConnectX_pos && !hasXpYn && !hasXpYp && !hasXpZn && !hasXpZp) {
			addCollisionBoxToList(blockPos, entityBox, collidingBoxes, AABB_XP);
		}
	}
	
	// TODO MC1.15 Omnipanel collision
	private void addCollisionBoxToList(BlockPos blockPos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, VoxelShape aabbZnYn) {
		// need to find the proper parent for this
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	// TODO MC1.15 Omnipanel collision
	public AxisAlignedBB getBoundingBox(@Nonnull final BlockState blockState, @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos) {
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(blockPos);
		
		// get direct connections
		final int maskConnectY_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() - 1, blockPos.getZ()    ), Direction.DOWN);
		final int maskConnectY_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY() + 1, blockPos.getZ()    ), Direction.UP);
		final int maskConnectZ_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() - 1), Direction.NORTH);
		final int maskConnectZ_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX()    , blockPos.getY()    , blockPos.getZ() + 1), Direction.SOUTH);
		final int maskConnectX_neg = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() - 1, blockPos.getY()    , blockPos.getZ()    ), Direction.WEST);
		final int maskConnectX_pos = getConnectionMask(worldReader, mutableBlockPos.setPos(blockPos.getX() + 1, blockPos.getY()    , blockPos.getZ()    ), Direction.EAST);
		
		final boolean canConnectY_neg = maskConnectY_neg > 0;
		final boolean canConnectY_pos = maskConnectY_pos > 0;
		final boolean canConnectZ_neg = maskConnectZ_neg > 0;
		final boolean canConnectZ_pos = maskConnectZ_pos > 0;
		final boolean canConnectX_neg = maskConnectX_neg > 0;
		final boolean canConnectX_pos = maskConnectX_pos > 0;
		final boolean canConnectNone = !canConnectY_neg && !canConnectY_pos && !canConnectZ_neg && !canConnectZ_pos && !canConnectX_neg && !canConnectX_pos;
		
		// x axis
		final float xMin = canConnectNone || canConnectX_neg ? 0.0F : CENTER_MIN;
		final float xMax = canConnectNone || canConnectX_pos ? 1.0F : CENTER_MAX;
		
		// y axis
		final float yMin = canConnectNone || canConnectY_neg ? 0.0F : CENTER_MIN;
		final float yMax = canConnectNone || canConnectY_pos ? 1.0F : CENTER_MAX;
		
		// z axis
		final float zMin = canConnectNone || canConnectZ_neg ? 0.0F : CENTER_MIN;
		final float zMax = canConnectNone || canConnectZ_pos ? 1.0F : CENTER_MAX;
		
		return new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	public int getConnectionMask(final IWorldReader worldReader, final BlockPos blockPos, final Direction facing) {
		final BlockState blockState = worldReader.getBlockState(blockPos);
		return 3;
		// TODO MC1.15 Omnipanel collision 
		/*
		return ( false blockState.isFullCube()
		      || blockState.getBlock() instanceof BlockAbstractOmnipanel
		      || blockState.getMaterial() == Material.GLASS
		      || blockState.getBlock() instanceof PaneBlock ? 1 : 0 )
		     + (blockState.isSideSolid(worldReader, blockPos, facing.getOpposite()) ? 2 : 0);
		     
		 */
	}
}