package cr0s.warpdrive.compat;

import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.common.blocks.BlockIEMultiblock;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorLV;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorRedstone;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.FastSetBlockState;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;

public class CompatImmersiveEngineering implements IBlockTransformer {
	
	private static Class<?> classTileEntityIEBase;
	
	public static void register() {
		try {
			classTileEntityIEBase = Class.forName("blusunrize.immersiveengineering.common.blocks.TileEntityIEBase");
			// note: this also covers ImmersiveTech and ImmersivePetroleum
			
			WarpDriveConfig.registerBlockTransformer("ImmersiveEngineering", new CompatImmersiveEngineering());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return tileEntity instanceof IImmersiveConnectable || classTileEntityIEBase.isInstance(tileEntity);
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                          final BlockState blockState, final TileEntity tileEntity) {
		if (tileEntity instanceof IImmersiveConnectable) {
			final BlockPos node = tileEntity.getPos();
			final Collection<Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(tileEntity.getWorld(), node);
			if (connections != null) {
				final ListNBT nbtImmersiveEngineering = new ListNBT();
				for (final Connection connection : connections) {
					nbtImmersiveEngineering.add(connection.write());
				}
				ImmersiveNetHandler.INSTANCE.clearConnectionsOriginatingFrom(node, tileEntity.getWorld());
				return nbtImmersiveEngineering;
			}
		}
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// since the mod forcefully drops original blocks, it's better to disassemble the whole structure prior to removal
		if ( block instanceof BlockIEMultiblock
		  && tileEntity instanceof TileEntityMultiblockPart ) {
			((TileEntityMultiblockPart<?>) tileEntity).disassemble();
		}
		// same goes with connectors loosing attachment on each others (for example, an MV connector placed on a MV transformer)
		if ( tileEntity instanceof TileEntityConnectorLV
		  || tileEntity instanceof TileEntityConnectorRedstone) {
			final BlockPos blockPos = tileEntity.getPos();
			world.removeTileEntity(blockPos);
			FastSetBlockState.setBlockStateNoLight(world, blockPos, Blocks.STONE.getDefaultState(), 2);
		}
	}
	
	/*
	generic
	    facing int vanilla facing (optional)
	immersiveengineering:capacitormv
		sideConfig_0 to 5 integer 0, 1, 2
	immersiveengineering:floodlight
		facing int vanilla facing
		side int vanilla facing
	immersiveengineering:fluidpump
		sideConfig integer[6] 0, -1 or 1
	immersiveengineering:fluidsorter
		facing int vanilla facing
		filter_0 to 5 list<TagCompound>
	immersiveengineering:watermill
		facing int vanilla facing
		offset integer[2] a b offset to core
	immersiveengineering:alloysmelter
	immersiveengineering:cokeoven
	immersiveengineering:crusher
	immersiveengineering:metalpress
	immersivepetroleum:pumpjack
	immersivetech:boiler
	immersivetech:distiller
	immersivetech:solartower
		facing int vanilla facing
		offset integer[3] x y z offset to core
	conveyor 'upgrades'
		facing int vanilla facing
		conveyorBeltSubtypeNBT.direction int (always 0 ?)
		conveyorBeltSubtypeNBT.extraDirection int 2 - 5 vanilla facing (optional)
	*/
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final int[]  rotFacing           = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// Capacitor
		if (nbtTileEntity.contains("sideConfig_0")) {
			final HashMap<String, INBT> mapNewSideConfig = new HashMap<>(6);
			
			for (int facing = 0; facing < 6; facing++) {
				// rotate the key name
				final String tagOldName = String.format("sideConfig_%d", facing);
				final String tagNewName;
				switch (rotationSteps) {
				case 1:
					tagNewName = String.format("sideConfig_%d", rotFacing[facing]);
					break;
				case 2:
					tagNewName = String.format("sideConfig_%d", rotFacing[rotFacing[facing]]);
					break;
				case 3:
					tagNewName = String.format("sideConfig_%d", rotFacing[rotFacing[rotFacing[facing]]]);
					break;
				default:
					tagNewName = tagOldName;
					break;
				}
				
				// rotate config name
				final INBT tagListOldSideConfig = nbtTileEntity.get(tagOldName);
				mapNewSideConfig.put(tagNewName, tagListOldSideConfig);
				nbtTileEntity.remove(tagOldName);
			}
			
			// apply the new side configurations
			for (final Entry<String, INBT> entry : mapNewSideConfig.entrySet()) {
				nbtTileEntity.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Conveyor
		if (nbtTileEntity.contains("conveyorBeltSubtypeNBT")) {
			final CompoundNBT conveyorBeltSubtypeNBT = nbtTileEntity.getCompound("conveyorBeltSubtypeNBT");
			if (conveyorBeltSubtypeNBT.contains("extractDirection")) {
				final int extraDirection = conveyorBeltSubtypeNBT.getInt("extractDirection");
				switch (rotationSteps) {
				case 1:
					conveyorBeltSubtypeNBT.putInt("extractDirection", rotFacing[extraDirection]);
					break;
				case 2:
					conveyorBeltSubtypeNBT.putInt("extractDirection", rotFacing[rotFacing[extraDirection]]);
					break;
				case 3:
					conveyorBeltSubtypeNBT.putInt("extractDirection", rotFacing[rotFacing[rotFacing[extraDirection]]]);
					break;
				default:
					break;
				}
			}
		}
		
		// Floodlight
		if (nbtTileEntity.contains("side")) {
			final int side = nbtTileEntity.getInt("side");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("side", rotFacing[side]);
				break;
			case 2:
				nbtTileEntity.putInt("side", rotFacing[rotFacing[side]]);
				break;
			case 3:
				nbtTileEntity.putInt("side", rotFacing[rotFacing[rotFacing[side]]]);
				break;
			default:
				break;
			}
		}
		
		// FluidPump
		if (nbtTileEntity.contains("sideConfig")) {
			final int[] intOldSideConfig = nbtTileEntity.getIntArray("sideConfig");
			final int[] intNewSideConfig = new int[6];
			
			for (int facingOld = 0; facingOld < 6; facingOld++) {
				// rotate the key name
				final int facingNew;
				switch (rotationSteps) {
				case 1:
					facingNew = rotFacing[facingOld];
					break;
				case 2:
					facingNew = rotFacing[rotFacing[facingOld]];
					break;
				case 3:
					facingNew = rotFacing[rotFacing[rotFacing[facingOld]]];
					break;
				default:
					facingNew = facingOld;
					break;
				}
				
				// rotate config name
				intNewSideConfig[facingNew] = intOldSideConfig[facingOld];
			}
			
			// apply the new side configurations
			nbtTileEntity.putIntArray("sideConfig", intNewSideConfig);
		}
		
		// FluidSorter
		if (nbtTileEntity.contains("filter_0")) {
			final HashMap<String, INBT> mapNewFilter = new HashMap<>(6);
			
			for (int facing = 0; facing < 6; facing++) {
				// rotate the key name
				final String tagOldName = String.format("filter_%d", facing);
				final String tagNewName;
				switch (rotationSteps) {
				case 1:
					tagNewName = String.format("filter_%d", rotFacing[facing]);
					break;
				case 2:
					tagNewName = String.format("filter_%d", rotFacing[rotFacing[facing]]);
					break;
				case 3:
					tagNewName = String.format("filter_%d", rotFacing[rotFacing[rotFacing[facing]]]);
					break;
				default:
					tagNewName = tagOldName;
					break;
				}
				
				// rotate filter
				final INBT tagListOldFilter = nbtTileEntity.get(tagOldName);
				mapNewFilter.put(tagNewName, tagListOldFilter);
				nbtTileEntity.remove(tagOldName);
			}
			
			// apply the new filters
			for (final Entry<String, INBT> entry : mapNewFilter.entrySet()) {
				nbtTileEntity.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Watermill and other multi-blocks
		if (nbtTileEntity.contains("offset", NBT.TAG_INT_ARRAY)) {
			// we're transforming relative coordinates, so we'll need the block old and new coordinates of this block
			final BlockPos blockPosOld = new BlockPos(
					nbtTileEntity.getInt("x"),
					nbtTileEntity.getInt("y"),
					nbtTileEntity.getInt("z"));
			final BlockPos blockPosNew = transformation.apply(blockPosOld);
			
			// facing 4 or 5 (WEST or EAST) is X axis, offset is along Z axis
			// facing 2 or 3 (NORTH or SOUTH) is Z axis, offset is along X axis
			final int facing = nbtTileEntity.getInt("facing");
			final Direction enumFacingOld = Direction.byIndex(facing);
			final Direction enumFacingNew;
			switch (rotationSteps) {
			case 1:
				enumFacingNew = Direction.byIndex(rotFacing[facing]);
				break;
			case 2:
				enumFacingNew = Direction.byIndex(rotFacing[rotFacing[facing]]);
				break;
			case 3:
				enumFacingNew = Direction.byIndex(rotFacing[rotFacing[rotFacing[facing]]]);
				break;
			default:
				enumFacingNew = enumFacingOld;
				break;
			}
			
			
			final int[] intOffsets = nbtTileEntity.getIntArray("offset");
			if (intOffsets.length == 2) {// watermill use 2 offset relative to facing
				final int x = blockPosOld.getX() + (enumFacingOld.getAxis() == Axis.Z ? intOffsets[0] : 0);
				final int y = blockPosOld.getY() + intOffsets[1];
				final int z = blockPosOld.getZ() + (enumFacingOld.getAxis() == Axis.X ? intOffsets[0] : 0);
				if (transformation.isInside(x, y, z)) {
					final BlockPos targetStabilizer = transformation.apply(x, y, z);
					intOffsets[0] = enumFacingNew.getAxis() == Axis.Z ? targetStabilizer.getX() - blockPosNew.getX()
					                                                  : targetStabilizer.getZ() - blockPosNew.getZ();
					intOffsets[1] = targetStabilizer.getY() - blockPosNew.getY();
				} else {// (outside ship)
					// remove the link
					intOffsets[0] = 0;
					intOffsets[1] = 0;
					// note: this may cause a dup bug but its TileEntities can't be cut by a ship during jump anyway.
				}
			} else if (nbtTileEntity.getBoolean("formed")) {// other multi-blocks uses 3 offset along the world x y z axis
				final int x = blockPosOld.getX() + intOffsets[0];
				final int y = blockPosOld.getY() + intOffsets[1];
				final int z = blockPosOld.getZ() + intOffsets[2];
				if (transformation.isInside(x, y, z)) {
					final BlockPos targetStabilizer = transformation.apply(x, y, z);
					intOffsets[0] = targetStabilizer.getX() - blockPosNew.getX();
					intOffsets[1] = targetStabilizer.getY() - blockPosNew.getY();
					intOffsets[2] = targetStabilizer.getZ() - blockPosNew.getZ();
				} else {// (outside ship)
					// remove the link
					intOffsets[0] = 0;
					intOffsets[1] = 0;
					intOffsets[2] = 0;
				}
			} else {
				WarpDrive.logger.error(String.format("Unexpected context for offset int array in %s for %s",
				                                     nbtTileEntity, block));
			}
		}
		
		// generic facing property is rotated last so other transformer can access its original value
		if (nbtTileEntity.contains("facing")) {
			final int facing = nbtTileEntity.getInt("facing");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putInt("facing", rotFacing[facing]);
				break;
			case 2:
				nbtTileEntity.putInt("facing", rotFacing[rotFacing[facing]]);
				break;
			case 3:
				nbtTileEntity.putInt("facing", rotFacing[rotFacing[rotFacing[facing]]]);
				break;
			default:
				break;
			}
		}
		
		return blockState;
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final BlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final INBT nbtBase) {
		if ( nbtBase == null
		  || nbtBase.isEmpty() ) {
			return;
		}
		if (!(nbtBase instanceof ListNBT)) {
			WarpDrive.logger.warn(String.format("Invalid external data for %s %s with TileEntity %s: %s",
			                                    blockState, Commons.format(world, blockPos), tileEntity, nbtBase ));
			return;
		}
		final ListNBT nbtImmersiveEngineering = (ListNBT) nbtBase;
		final World targetWorld = transformation.getTargetWorld();
		if (nbtImmersiveEngineering.isEmpty()) {
			return;
		}
		if (tileEntity == null) {
			WarpDrive.logger.warn(String.format("Invalid null tile entity for %s %s with external data %s",
			                                    blockState, Commons.format(world, blockPos), nbtBase ));
			return;
		}
		
		// powerPathList
		for (int indexConnectionToAdd = 0; indexConnectionToAdd < nbtImmersiveEngineering.size(); indexConnectionToAdd++) {
			final Connection connectionToAdd = Connection.read(nbtImmersiveEngineering.getCompound(indexConnectionToAdd));
			connectionToAdd.start = transformation.apply(connectionToAdd.start);
			connectionToAdd.end = transformation.apply(connectionToAdd.end);
			final BlockPos node = tileEntity.getPos();
			final Collection<Connection> connectionActuals = ImmersiveNetHandler.INSTANCE.getConnections(tileEntity.getWorld(), node);
			boolean existing = false;
			if (connectionActuals != null) {
				for (final Connection connectionActual : connectionActuals) {
					if ( connectionActual.start.equals(connectionToAdd.start)
					  && connectionActual.end.equals(connectionToAdd.end) ) {
						existing = true;
						break;
					} else if (
					     connectionActual.start.equals(connectionToAdd.end)
					  && connectionActual.end.equals(connectionToAdd.start) ) {
						existing = true;
						break;
					}
				}
			}
			if (!existing) {
				ImmersiveNetHandler.INSTANCE.addConnection(targetWorld, connectionToAdd.start, connectionToAdd);
			}
		}
	}
}
