package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.TileEntityAbstractBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityCamouflage extends TileEntityAbstractBase {
	
	private static final String TAG_CAMOUFLAGE = "camouflage";
	
	public static final ModelProperty<BlockState> MODEL_PROPERTY_NULL  = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_DOWN  = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_UP    = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_NORTH = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_SOUTH = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_WEST  = new ModelProperty<>();
	public static final ModelProperty<BlockState> MODEL_PROPERTY_EAST  = new ModelProperty<>();
	
	public enum NullableDirection {
		NULL  (null           , MODEL_PROPERTY_NULL ),
		DOWN  (Direction.DOWN , MODEL_PROPERTY_DOWN ),
		UP    (Direction.UP   , MODEL_PROPERTY_UP   ),
		NORTH (Direction.NORTH, MODEL_PROPERTY_NORTH),
		SOUTH (Direction.SOUTH, MODEL_PROPERTY_SOUTH),
		WEST  (Direction.WEST , MODEL_PROPERTY_WEST ),
		EAST  (Direction.EAST , MODEL_PROPERTY_EAST );
		
		public final Direction direction;
		public final ModelProperty<BlockState> modelProperty;
		
		NullableDirection(@Nullable final Direction direction, @Nonnull ModelProperty<BlockState> modelProperty) {
			this.direction = direction;
			this.modelProperty = modelProperty;
		}
		
		public static NullableDirection from(@Nullable final Direction direction) {
			if (direction == null) {
				return NULL;
			}
			switch(direction) {
			case DOWN  : return DOWN ;
			case UP    : return UP   ;
			case NORTH : return NORTH;
			case SOUTH : return SOUTH;
			case WEST  : return WEST ;
			case EAST  : return EAST ;
			default: return NULL;
			}
		}
	}
	
	// persistent properties
	private final BlockState[] blockStateCamouflage = new BlockState[NullableDirection.values().length];
	
	// computed properties
	
	
	public TileEntityCamouflage(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
	}
	
	@Override
	public void tick() {
		super.tick();
		assert world != null;
		
	}
	
	@Nullable
	protected BlockState getCamouflage(@Nullable final Direction side) {
		return blockStateCamouflage[NullableDirection.from(side).ordinal()];
	}
	
	protected void setCamouflage(@Nullable final BlockState blockState, @Nullable final Direction side) {
		// nothing set yet => changing everything
		if (blockStateCamouflage[NullableDirection.NULL.ordinal()] == null) {
			Arrays.fill(blockStateCamouflage, blockState);
			markDirty();
			return;
		}
		// clearing an already unset face => clear everything
		final int indexSide = NullableDirection.from(side).ordinal();
		if ( blockState == null
		  && blockStateCamouflage[indexSide] == null ) {
			Arrays.fill(blockStateCamouflage, null);
			markDirty();
			return;
		}
		
		// changing a single face
		if (blockStateCamouflage[indexSide] != blockState) {
			this.blockStateCamouflage[indexSide] = blockState;
			markDirty();
		}
	}
	
	// Forge overrides
	@Nonnull
	@Override
	public IModelData getModelData() {
		final IModelData modelData = new ModelDataMap.Builder()
											 .withProperty(MODEL_PROPERTY_NULL )
				                             .withProperty(MODEL_PROPERTY_DOWN )
				                             .withProperty(MODEL_PROPERTY_UP   )
				                             .withProperty(MODEL_PROPERTY_NORTH)
				                             .withProperty(MODEL_PROPERTY_SOUTH)
				                             .withProperty(MODEL_PROPERTY_WEST )
				                             .withProperty(MODEL_PROPERTY_EAST ).build();
		for (final NullableDirection nullableDirection : NullableDirection.values()) {
			modelData.setData(nullableDirection.modelProperty, blockStateCamouflage[nullableDirection.ordinal()]);
		}
		return modelData;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		if (tagCompound.contains(TAG_CAMOUFLAGE, NBT.TAG_COMPOUND)) {
			final CompoundNBT tagCompoundCamouflage = tagCompound.getCompound(TAG_CAMOUFLAGE);
			for (final NullableDirection nullableDirection : NullableDirection.values()) {
				blockStateCamouflage[nullableDirection.ordinal()] = Commons.readBlockStateFromNBT(tagCompoundCamouflage.getCompound(nullableDirection.name()));
				if (blockStateCamouflage[nullableDirection.ordinal()].getBlock() == Blocks.AIR) {
					blockStateCamouflage[nullableDirection.ordinal()] = null;
				}
			}
		} else {
			Arrays.fill(blockStateCamouflage, null);
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		if ( blockStateCamouflage[NullableDirection.NULL.ordinal()] != null
		  && blockStateCamouflage[NullableDirection.NULL.ordinal()].getBlock() != Blocks.AIR ) {
			final CompoundNBT tagCompoundCamouflage = new CompoundNBT();
			for (final NullableDirection nullableDirection : NullableDirection.values()) {
				if (blockStateCamouflage[nullableDirection.ordinal()] != null) {
					tagCompoundCamouflage.put(nullableDirection.name(), Commons.writeBlockStateToNBT(blockStateCamouflage[nullableDirection.ordinal()]));
				}
			}
			tagCompound.put(TAG_CAMOUFLAGE, tagCompoundCamouflage);
		}
		return tagCompound;
	}
}