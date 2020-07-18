package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatMysticalAgriculture implements IBlockTransformer {
	
	private static Class<?> classBlockInferiumFurnace;
	private static Class<?> classBlockIntermediumFurnace;
	private static Class<?> classBlockPrudentiumFurnace;
	private static Class<?> classBlockSuperiumFurnace;
	private static Class<?> classBlockSupremiumFurnace;
	private static Class<?> classBlockUltimateFurnace;
	
	public static void register() {
		try {
			// nota: code is duplicated in the mod, there's no common class. Alternately, we could use a 12x registryName map approach here
			classBlockInferiumFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockInferiumFurnace");
			classBlockIntermediumFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockIntermediumFurnace");
			classBlockPrudentiumFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockPrudentiumFurnace");
			classBlockSuperiumFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockSuperiumFurnace");
			classBlockSupremiumFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockSupremiumFurnace");
			classBlockUltimateFurnace = Class.forName("com.blakebr0.mysticalagriculture.blocks.furnace.BlockUltimateFurnace");
			
			WarpDriveConfig.registerBlockTransformer("MysticalAgriculture", new CompatMysticalAgriculture());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockInferiumFurnace.isInstance(blockState.getBlock())
		    || classBlockIntermediumFurnace.isInstance(blockState.getBlock())
		    || classBlockPrudentiumFurnace.isInstance(blockState.getBlock())
		    || classBlockSuperiumFurnace.isInstance(blockState.getBlock())
		    || classBlockSupremiumFurnace.isInstance(blockState.getBlock())
		    || classBlockUltimateFurnace.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                          final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
	}
	
	//                                               0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] rotFacing       = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// furnaces
		switch (rotationSteps) {
		case 1:
			return rotFacing[metadata];
		case 2:
			return rotFacing[rotFacing[metadata]];
		case 3:
			return rotFacing[rotFacing[rotFacing[metadata]]];
		default:
			return blockState;
		}
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		// nothing to do
	}
}
