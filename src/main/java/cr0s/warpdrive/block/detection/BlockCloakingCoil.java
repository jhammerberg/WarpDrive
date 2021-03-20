package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCloakingCoil extends BlockAbstractBase {
	
	// Metadata values
	// 0 = not linked
	// 1 = inner coil passive
	// 2-7 = outer coil passive
	// 8 = (not used)
	// 9 = inner coil active
	// 10-15 = outer coil active
	
	public static final EnumProperty<EnumCoilType> COIL_TYPE = EnumProperty.create("coil_type", EnumCoilType.class);
	
	public BlockCloakingCoil(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				.hardnessAndResistance(3.5F), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
		                                   .with(COIL_TYPE, EnumCoilType.DISCONNECTED)
		                                   .with(BlockProperties.ACTIVE, false)
		                                   .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(COIL_TYPE);
		builder.add(BlockProperties.ACTIVE);
		builder.add(BlockProperties.FACING);
	}
	
	public static void setBlockState(@Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final BlockState blockStateActual,
	                                 final EnumCoilType coilType, final boolean isActive, final Direction enumFacing) {
		BlockState blockStateNew = blockStateActual.with(COIL_TYPE, coilType)
		                                           .with(BlockProperties.ACTIVE, isActive);
		if (enumFacing != null) {
			blockStateNew = blockStateNew.with(BlockProperties.FACING, enumFacing);
		}
		if (blockStateActual != blockStateNew) {
			world.setBlockState(blockPos, blockStateNew);
		}
	}
	
	public enum EnumCoilType implements IStringSerializable {
		
		DISCONNECTED("disconnected"),
		INNER       ("inner"),
		OUTER       ("outer");
		
		private final String name;
		
		EnumCoilType(final String name) {
			this.name = name;
		}
		
		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}
