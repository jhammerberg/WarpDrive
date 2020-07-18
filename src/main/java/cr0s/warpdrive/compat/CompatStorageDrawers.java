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

public class CompatStorageDrawers implements IBlockTransformer {
	
	private static Class<?> classBlockController;    // controller = metadata rotation              com.jaquadro.minecraft.storagedrawers.block.BlockController
	private static Class<?> classBlockDrawers;       // basic/custom/compacting drawers = byte Dir  com.jaquadro.minecraft.storagedrawers.block.BlockDrawers
	private static Class<?> classBlockFramingTable;  // framing table = metadata rotation + side    com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable
	
	public static void register() {
		try {
			classBlockController = Class.forName("com.jaquadro.minecraft.storagedrawers.block.BlockController");
			classBlockDrawers = Class.forName("com.jaquadro.minecraft.storagedrawers.block.BlockDrawers");
			classBlockFramingTable = Class.forName("com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable");
			
			WarpDriveConfig.registerBlockTransformer("StorageDrawers", new CompatStorageDrawers());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockController.isInstance(blockState.getBlock())
		    || classBlockDrawers.isInstance(blockState.getBlock())
		    || classBlockFramingTable.isInstance(blockState.getBlock());
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
	private static final byte[] rotFramingTable = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 13, 12, 10, 11, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// controller
		if (classBlockController.isInstance(blockState.getBlock())) {
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
		
		// basic/custom/compacting drawers
		if (nbtTileEntity.contains("Dir")) {
			final byte facing = nbtTileEntity.getByte("Dir");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putByte("Dir", rotFacing[facing]);
				break;
			case 2:
				nbtTileEntity.putByte("Dir", rotFacing[rotFacing[facing]]);
				break;
			case 3:
				nbtTileEntity.putByte("Dir", rotFacing[rotFacing[rotFacing[facing]]]);
				break;
			default:
				break;
			}
			return blockState;
		}
		
		// framing table
		if (classBlockFramingTable.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotFramingTable[metadata];
			case 2:
				return rotFramingTable[rotFramingTable[metadata]];
			case 3:
				return rotFramingTable[rotFramingTable[rotFramingTable[metadata]]];
			default:
				return blockState;
			}
		}
		
		return blockState;
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		// nothing to do
	}
}
