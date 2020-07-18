package cr0s.warpdrive.compat;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatSGCraft implements IBlockTransformer {
	
	private static Class<?> classBaseTileEntity;
	private static Class<?> classDHDBlock;
	private static Class<?> classSGBaseBlock;
	private static Class<?> classSGBaseTE;
	private static Method methodSGBaseTE_sgStateDescription;
	
	public static void register() {
		try {
			classBaseTileEntity = Class.forName("gcewing.sg.BaseTileEntity");
			classDHDBlock = Class.forName("gcewing.sg.block.DHDBlock");
			classSGBaseBlock = Class.forName("gcewing.sg.block.SGBaseBlock");
			classSGBaseTE = Class.forName("gcewing.sg.tileentity.SGBaseTE");
			methodSGBaseTE_sgStateDescription = classSGBaseTE.getMethod("sgStateDescription");
			
			WarpDriveConfig.registerBlockTransformer("SGCraft", new CompatSGCraft());
		} catch(final ClassNotFoundException | NoSuchMethodException | SecurityException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBaseTileEntity.isInstance(tileEntity);
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		if (classSGBaseTE.isInstance(tileEntity)) {
			try {
				final Object object = methodSGBaseTE_sgStateDescription.invoke(tileEntity);
				final String state = (String)object;
				if (!state.equalsIgnoreCase("Idle")) {
					reason.append(Commons.getStyleWarning(), "warpdrive.compat.guide.stargate_is_active", state);
					return false;
				}
			} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				exception.printStackTrace();
			}
		}
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
	
	//                                                        0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] mrotSGBase               = {  3,  2,  0,  1,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  rotFacingDirectionOfBase = {  3,  0,  1,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		// Link between stargate controller and DHD
		if (nbtTileEntity.contains("isLinkedToStargate")) {
			if ( nbtTileEntity.getBoolean("isLinkedToStargate")
			  && nbtTileEntity.contains("linkedX") && nbtTileEntity.contains("linkedY") && nbtTileEntity.contains("linkedZ") ) {
				if (transformation.isInside(nbtTileEntity.getInt("linkedX"), nbtTileEntity.getInt("linkedY"), nbtTileEntity.getInt("linkedZ"))) {
					final BlockPos targetLink = transformation.apply(nbtTileEntity.getInt("linkedX"), nbtTileEntity.getInt("linkedY"), nbtTileEntity.getInt("linkedZ"));
					nbtTileEntity.putInt("linkedX", targetLink.getX());
					nbtTileEntity.putInt("linkedY", targetLink.getY());
					nbtTileEntity.putInt("linkedZ", targetLink.getZ());
				} else {
					nbtTileEntity.putBoolean("isLinkedToController", false);
					nbtTileEntity.putInt("linkedX", 0);
					nbtTileEntity.putInt("linkedY", 0);
					nbtTileEntity.putInt("linkedZ", 0);
				}
			}
		}
		if (nbtTileEntity.contains("isLinkedToController")) {
			if ( nbtTileEntity.getBoolean("isLinkedToController")
			  && nbtTileEntity.contains("linkedX") && nbtTileEntity.contains("linkedY") && nbtTileEntity.contains("linkedZ") ) {
				if (transformation.isInside(nbtTileEntity.getInt("linkedX"), nbtTileEntity.getInt("linkedY"), nbtTileEntity.getInt("linkedZ"))) {
					final BlockPos targetLink = transformation.apply(nbtTileEntity.getInt("linkedX"), nbtTileEntity.getInt("linkedY"), nbtTileEntity.getInt("linkedZ"));
					nbtTileEntity.putInt("linkedX", targetLink.getX());
					nbtTileEntity.putInt("linkedY", targetLink.getY());
					nbtTileEntity.putInt("linkedZ", targetLink.getZ());
				} else {
					nbtTileEntity.putBoolean("isLinkedToController", false);
					nbtTileEntity.putInt("linkedX", 0);
					nbtTileEntity.putInt("linkedY", 0);
					nbtTileEntity.putInt("linkedZ", 0);
				}
			}
		}
		
		// Reference of ring blocks to the controller block
		if (nbtTileEntity.contains("isMerged")) {
			if ( nbtTileEntity.getBoolean("isMerged")
			  && nbtTileEntity.contains("baseX") && nbtTileEntity.contains("baseY") && nbtTileEntity.contains("baseZ")) {
				final BlockPos targetLink = transformation.apply(nbtTileEntity.getInt("baseX"), nbtTileEntity.getInt("baseY"), nbtTileEntity.getInt("baseZ"));
				nbtTileEntity.putInt("baseX", targetLink.getX());
				nbtTileEntity.putInt("baseY", targetLink.getY());
				nbtTileEntity.putInt("baseZ", targetLink.getZ());
			}
		}
		
		// Ring renderer orientation
		if (nbtTileEntity.contains("facingDirectionOfBase")) {
			final int facing = nbtTileEntity.getByte("facingDirectionOfBase");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("facingDirectionOfBase", rotFacingDirectionOfBase[facing]);
				break;
			case 2:
				nbtTileEntity.putInt("facingDirectionOfBase", rotFacingDirectionOfBase[rotFacingDirectionOfBase[facing]]);
				break;
			case 3:
				nbtTileEntity.putInt("facingDirectionOfBase", rotFacingDirectionOfBase[rotFacingDirectionOfBase[rotFacingDirectionOfBase[facing]]]);
				break;
			default:
				break;
			}
		}
		
		if ( classDHDBlock.isInstance(blockState.getBlock())
		  || classSGBaseBlock.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return mrotSGBase[metadata];
			case 2:
				return mrotSGBase[mrotSGBase[metadata]];
			case 3:
				return mrotSGBase[mrotSGBase[mrotSGBase[metadata]]];
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
