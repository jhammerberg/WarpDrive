package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
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
	
	public static final BooleanProperty OUTER = BooleanProperty.create("outer");
	
	public BlockCloakingCoil(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null)
				.hardnessAndResistance(3.5F), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.CONNECTED, false)
				                .with(BlockProperties.ACTIVE, false)
				                .with(OUTER, false)
				                .with(BlockProperties.FACING, Direction.DOWN)
		               );
	}
	
	public static void setBlockState(@Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final BlockState blockStateActual,
	                                 final boolean isConnected, final boolean isActive, final boolean isOuter, final Direction enumFacing) {
		BlockState blockStateNew = blockStateActual.with(BlockProperties.CONNECTED, isConnected)
		                                           .with(BlockProperties.ACTIVE, isActive)
		                                           .with(OUTER, isOuter);
		if (enumFacing != null) {
			blockStateNew = blockStateNew.with(BlockProperties.FACING, enumFacing);
		}
		if (blockStateActual != blockStateNew) {
			world.setBlockState(blockPos, blockStateNew);
		}
	}
}
