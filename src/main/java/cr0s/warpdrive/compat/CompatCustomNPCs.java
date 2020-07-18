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

public class CompatCustomNPCs implements IBlockTransformer {
	
	// CustomNPCs
	private static Class<?> classBlockBorder;
	private static Class<?> classBlockCarpentryBench;
	private static Class<?> classBlockBuilder;
	private static Class<?> classBlockMailbox;
	private static Class<?> classBlockNpcRedstone;
	
	public static void register() {
		try {
			// customNPC
			classBlockBorder         = Class.forName("noppes.npcs.blocks.BlockBorder");
			classBlockBuilder        = Class.forName("noppes.npcs.blocks.BlockBuilder");
			classBlockCarpentryBench = Class.forName("noppes.npcs.blocks.BlockCarpentryBench");
			classBlockMailbox        = Class.forName("noppes.npcs.blocks.BlockMailbox");
			classBlockNpcRedstone    = Class.forName("noppes.npcs.blocks.BlockNpcRedstone");
			
			WarpDriveConfig.registerBlockTransformer("CustomNPCs", new CompatCustomNPCs());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockBorder.isInstance(blockState.getBlock())
		    || classBlockBuilder.isInstance(blockState.getBlock())
		    || classBlockCarpentryBench.isInstance(blockState.getBlock())
		    || classBlockMailbox.isInstance(blockState.getBlock())
		    || classBlockNpcRedstone.isInstance(blockState.getBlock());
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
	
	// Transformation handling required as of CustomNPCs_1.12.2(30Jan19):
	// noppes.npcs.blocks.BlockInterface
	//      noppes.npcs.blocks.BlockBorder
	//          meta    ROTATION 0 1 2 3
	//          int     BorderRotation  0 1 2 3
	//      noppes.npcs.blocks.BlockBuilder
	//          meta    ROTATION 0 1 2 3
	//          int     Rotation (relative to block => ignore it?)
	//      noppes.npcs.blocks.BlockCarpentryBench
	//          meta    ROTATION 0 1 2 3
	//      noppes.npcs.blocks.BlockCopy
	//          meta    -none-
	//      noppes.npcs.blocks.BlockMailbox
	//          meta    ROTATION 0 1 2 3 | TYPE 0 4 8
	//      noppes.npcs.BlockNpcRedstone
	//          meta    -none-
	//          int     BlockOnRangeX/BlockOnRangeZ
	//          int     BlockOffRangeX/BlockOffRangeZ
	//      noppes.npcs.blocks.BlockWaypoint
	//          meta    -none-
	
	// BlockDoor
	//      noppes.npcs.blocks.BlockNpcDoorInterface
	//          noppes.npcs.blocks.BlockScriptedDoor
	//              meta    (same as vanilla)
	
	
	// -----------------------------------------          {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   mrot4                  = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   mrotMailbox            = {  1,  2,  3,  0,  5,  6,  7,  4,  9, 10, 11,  8, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		if ( nbtTileEntity != null
		  && nbtTileEntity.contains("BorderRotation") ) {
			final int BorderRotation = nbtTileEntity.getInt("BorderRotation");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("BorderRotation", mrot4[BorderRotation]);
				break;
			case 2:
				nbtTileEntity.putInt("BorderRotation", mrot4[mrot4[BorderRotation]]);
				break;
			case 3:
				nbtTileEntity.putInt("BorderRotation", mrot4[mrot4[mrot4[BorderRotation]]]);
				break;
			default:
				break;
			}
		}
		
		if ( nbtTileEntity != null
		  && nbtTileEntity.contains("BlockOnRangeX") ) {
			final int BlockOnRangeX = nbtTileEntity.getInt("BlockOnRangeX");
			final int BlockOnRangeZ = nbtTileEntity.getInt("BlockOnRangeZ");
			final int BlockOffRangeX = nbtTileEntity.getInt("BlockOffRangeX");
			final int BlockOffRangeZ = nbtTileEntity.getInt("BlockOffRangeZ");
			switch (rotationSteps) {
			case 1:
			case 3:
				nbtTileEntity.putInt("BlockOnRangeX", BlockOnRangeZ);
				nbtTileEntity.putInt("BlockOnRangeZ", BlockOnRangeX);
				nbtTileEntity.putInt("BlockOffRangeX", BlockOffRangeZ);
				nbtTileEntity.putInt("BlockOffRangeZ", BlockOffRangeX);
				break;
				
			default:
				break;
			}
		}
		
		
		if ( classBlockBorder.isInstance(blockState.getBlock())
		  || classBlockBuilder.isInstance(blockState.getBlock())
		  || classBlockCarpentryBench.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return mrot4[metadata];
			case 2:
				return mrot4[mrot4[metadata]];
			case 3:
				return mrot4[mrot4[mrot4[metadata]]];
			default:
				return blockState;
			}
		}
		if (classBlockMailbox.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotMailbox[metadata];
			case 2:
				return mrotMailbox[mrotMailbox[metadata]];
			case 3:
				return mrotMailbox[mrotMailbox[mrotMailbox[metadata]]];
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
