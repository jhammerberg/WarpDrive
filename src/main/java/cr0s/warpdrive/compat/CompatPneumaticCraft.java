package cr0s.warpdrive.compat;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatPneumaticCraft implements IBlockTransformer {
	
	private static Class<?> classBlockPneumaticCraft;
	private static Method   methodBlockPneumaticCraft_isRotatable; // many blocks are rotatable, many are not => it's more efficient to read the property to differentiate them
	
	private static Class<?> classBlockPneumaticDoor;
	private static Class<?> classBlockPressureChamberWall;
	private static Class<?> classBlockPressureChamberValve;
	
	public static void register() {
		try {
			classBlockPneumaticCraft = Class.forName("me.desht.pneumaticcraft.common.block.BlockPneumaticCraft");
			methodBlockPneumaticCraft_isRotatable = classBlockPneumaticCraft.getMethod("isRotatable");
			
			classBlockPneumaticDoor = Class.forName("me.desht.pneumaticcraft.common.block.BlockPneumaticDoor");
			classBlockPressureChamberWall = Class.forName("me.desht.pneumaticcraft.common.block.BlockPressureChamberWall");
			classBlockPressureChamberValve = Class.forName("me.desht.pneumaticcraft.common.block.BlockPressureChamberValve");
			
			WarpDriveConfig.registerBlockTransformer("pneumaticcraft", new CompatPneumaticCraft());
		} catch(final ClassNotFoundException | NoSuchMethodException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockPneumaticCraft.isInstance(blockState.getBlock());
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
	
	//                                                     0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] mrotFacing            = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 }; // Chamber interface & Omnidirectional hopper
	private static final byte[] mrotChamberWall       = {  0,  1,  3,  2,  4,  8,  5,  6,  7,  9, 10, 11, 12, 13, 14, 15 }; // Chamber wall
	private static final byte[] mrotChamberValve      = {  0,  1,  5,  4,  2,  3,  6,  7, 11, 10,  8,  9, 12, 13, 14, 15 }; // Chamber valve
	private static final byte[] mrotPneumaticDoor     = {  0,  1,  5,  4,  2,  3,  6,  7, 11, 10,  8,  9, 12, 13, 14, 15 }; // Pressure door
	private static final byte[] mrotTextRotation      = {  1,  2,  3,  0 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if ( rotationSteps == 0
		  && !nbtTileEntity.contains("valveX")
		  && !nbtTileEntity.contains("multiBlockX")) {
			return blockState;
		}
		
		// Aphorism signs
		// @todo the sign has no text after ship movement in single player until chunk is reloaded?
		if (nbtTileEntity.contains("textRot")) {
			if (metadata == 0 || metadata == 1) {// sign is horizontal, only the text needs to be rotated
				final int textRotation = nbtTileEntity.getInt("textRot");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putInt("textRot", mrotTextRotation[textRotation]);
					return blockState;
				case 2:
					nbtTileEntity.putInt("textRot", mrotTextRotation[mrotTextRotation[textRotation]]);
					return blockState;
				case 3:
					nbtTileEntity.putInt("textRot", mrotTextRotation[mrotTextRotation[mrotTextRotation[textRotation]]]);
					return blockState;
				default:
					return blockState;
				}
			} else {// sign is vertical, only the block itself is rotating
				switch (rotationSteps) {
				case 1:
					return mrotFacing[metadata];
				case 2:
					return mrotFacing[mrotFacing[metadata]];
				case 3:
					return mrotFacing[mrotFacing[mrotFacing[metadata]]];
				default:
					return blockState;
				}
			}
		}
		
		// Omnidirectional hoppers
		if (nbtTileEntity.contains("inputDir")) {
			final int inputDir = nbtTileEntity.getInt("inputDir");
			final int outputDir = nbtTileEntity.getInt("outputDir");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("inputDir", mrotFacing[inputDir]);
				nbtTileEntity.putInt("outputDir", mrotFacing[outputDir]);
				return mrotFacing[metadata];
			case 2:
				nbtTileEntity.putInt("inputDir", mrotFacing[mrotFacing[inputDir]]);
				nbtTileEntity.putInt("outputDir", mrotFacing[mrotFacing[outputDir]]);
				return mrotFacing[mrotFacing[metadata]];
			case 3:
				nbtTileEntity.putInt("inputDir", mrotFacing[mrotFacing[mrotFacing[inputDir]]]);
				nbtTileEntity.putInt("outputDir", mrotFacing[mrotFacing[mrotFacing[outputDir]]]);
				return mrotFacing[mrotFacing[mrotFacing[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Pneumatic door is facing + top/down on modulo (6 or 8 ?)
		if (classBlockPneumaticDoor.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotPneumaticDoor[metadata];
			case 2:
				return mrotPneumaticDoor[mrotPneumaticDoor[metadata]];
			case 3:
				return mrotPneumaticDoor[mrotPneumaticDoor[mrotPneumaticDoor[metadata]]];
			default:
				return blockState;
			}
		}
		
		// pressure chamber blocks (wall, glass, valve, interface)
		if (nbtTileEntity.contains("valveX")) {
			final BlockPos target = transformation.apply(
				nbtTileEntity.getInt("valveX"),
				nbtTileEntity.getInt("valveY"),
				nbtTileEntity.getInt("valveZ"));
			nbtTileEntity.putInt("valveX", target.getX());
			nbtTileEntity.putInt("valveY", target.getY());
			nbtTileEntity.putInt("valveZ", target.getZ());
			// use default metadata rotation
		}
		
		// pressure chamber valve
		if (nbtTileEntity.contains("multiBlockX")) {
			// multiBlockXYZ only makes sense when size is non null, even if they are part of the multiblock (yes, it's weird)
			final int multiBlockSize = nbtTileEntity.getInt("multiBlockSize");
			if (multiBlockSize != 0) {
				final BlockPos sourceMin = new BlockPos(
						nbtTileEntity.getInt("multiBlockX"),
						nbtTileEntity.getInt("multiBlockY"),
						nbtTileEntity.getInt("multiBlockZ"));
				final BlockPos sourceMax = new BlockPos(
						sourceMin.getX() + multiBlockSize - 1,
						sourceMin.getY() + multiBlockSize - 1,
						sourceMin.getZ() + multiBlockSize - 1);
				final BlockPos target1 = transformation.apply(sourceMin);
				final BlockPos target2 = transformation.apply(sourceMax);
				nbtTileEntity.putInt("multiBlockX", Math.min(target1.getX(), target2.getX()));
				nbtTileEntity.putInt("multiBlockY", Math.min(target1.getY(), target2.getY()));
				nbtTileEntity.putInt("multiBlockZ", Math.min(target1.getZ(), target2.getZ()));
			}
			
			// Valves coordinates to each valves
			final ListNBT tagListOld = nbtTileEntity.getList("Valves", 10);
			final ListNBT tagListNew = new ListNBT();
			for (int index = 0; index < tagListOld.size(); index++) {
				final CompoundNBT tagCompound = tagListOld.getCompound(index);
				if (tagCompound != null) {
					final BlockPos coordinates = transformation.apply(
						tagCompound.getInt("xCoord"),
						tagCompound.getInt("yCoord"),
						tagCompound.getInt("zCoord"));
					tagCompound.putInt("xCoord", coordinates.getX());
					tagCompound.putInt("yCoord", coordinates.getY());
					tagCompound.putInt("zCoord", coordinates.getZ());
					tagListNew.add(tagCompound);
				}
			}
			nbtTileEntity.put("Valves", tagListNew);
			// use default metadata rotation
		}
		
		// elevator base, pipe
		if (nbtTileEntity.contains("sideConnected0")) {
			final byte[] connectedOldSides = new byte[6];
			for (int side = 2; side < 6; side++) {
				connectedOldSides[side] = nbtTileEntity.getByte("sideConnected" + side);
			}
			for (int side = 2; side < 6; side++) {
			final byte connected = connectedOldSides[side];
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putByte("sideConnected" + mrotFacing[side], connected);
					break;
				case 2:
					nbtTileEntity.putByte("sideConnected" + mrotFacing[mrotFacing[side]], connected);
					break;
				case 3:
					nbtTileEntity.putByte("sideConnected" + mrotFacing[mrotFacing[mrotFacing[side]]], connected);
					break;
				default:
					break;
				}
			}
			if (nbtTileEntity.contains("sideClosed0")) {
				final byte[] closedOldSides = new byte[6];
				for (int side = 2; side < 6; side++) {
					closedOldSides[side] = nbtTileEntity.getByte("sideClosed" + side);
				}
				for (int side = 2; side < 6; side++) {
					final byte connected = closedOldSides[side];
					switch (rotationSteps) {
					case 1:
						nbtTileEntity.putByte("sideClosed" + mrotFacing[side], connected);
						break;
					case 2:
						nbtTileEntity.putByte("sideClosed" + mrotFacing[mrotFacing[side]], connected);
						break;
					case 3:
						nbtTileEntity.putByte("sideClosed" + mrotFacing[mrotFacing[mrotFacing[side]]], connected);
						break;
					default:
						break;
					}
				}
			}
		}
		
		
		// Pressure chamber wall has its own logic: NONE,  CENTER,  XEDGE,  ZEDGE,  YEDGE,  XMIN_YMIN_ZMIN,  XMIN_YMIN_ZMAX,  XMIN_YMAX_ZMIN,  XMIN_YMAX_ZMAX
		if (classBlockPressureChamberWall.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotChamberWall[metadata];
			case 2:
				return mrotChamberWall[mrotChamberWall[metadata]];
			case 3:
				return mrotChamberWall[mrotChamberWall[mrotChamberWall[metadata]]];
			default:
				return blockState;
			}
		}
		
		if (classBlockPressureChamberValve.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotChamberValve[metadata];
			case 2:
				return mrotChamberValve[mrotChamberValve[metadata]];
			case 3:
				return mrotChamberValve[mrotChamberValve[mrotChamberValve[metadata]]];
			default:
				return blockState;
			}
		}
		
		// all other tile entities we need to check the Rotatable state
		// this includes many blocks like security station, programmer, pneumatic dynamo, charging station, air cannon, elevator caller, air compressor, etc.
		final boolean isRotatable;
		try {
			final Object object = methodBlockPneumaticCraft_isRotatable.invoke(block);
			if (object instanceof Boolean) {
				isRotatable = (Boolean) object;
			} else {
				WarpDrive.logger.error(String.format("Block %s has invalid non-Boolean return value to isRotatable: %s",
				                                     block.getRegistryName(), object));
				return blockState;
			}
		} catch (final IllegalAccessException | InvocationTargetException exception) {
			exception.printStackTrace();
			return blockState;
		}
		WarpDrive.logger.info(String.format("Block %s isRotatable %s",
		                                    block.getRegistryName(), isRotatable));
		if (isRotatable) {
			switch (rotationSteps) {
			case 1:
				return mrotFacing[metadata];
			case 2:
				return mrotFacing[mrotFacing[metadata]];
			case 3:
				return mrotFacing[mrotFacing[mrotFacing[metadata]]];
			default:
				return blockState;
			}
		} else {
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
