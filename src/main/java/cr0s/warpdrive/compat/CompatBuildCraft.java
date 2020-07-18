package cr0s.warpdrive.compat;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;

public class CompatBuildCraft implements IBlockTransformer {
	
	private static Class<?> classBlockBCBase_Neptune;
	private static Class<?> classBlockPipeHolder;
	
	// disable volume and path related machines (most of them don't make sense in the lore)
	private static Class<?> classBlockArchitectTable;
	private static Class<?> classBlockBuilder;
	private static Class<?> classBlockFiller;
	private static Class<?> classBlockQuarry;
	private static Class<?> classBlockReplacer;
	private static Class<?> classBlockZonePlanner;
	
	public static void register() {
		try {
			classBlockBCBase_Neptune = Class.forName("buildcraft.lib.block.BlockBCBase_Neptune");
			classBlockPipeHolder = Class.forName("buildcraft.transport.block.BlockPipeHolder");
			
			classBlockArchitectTable = Class.forName("buildcraft.builders.block.BlockArchitectTable"); // id is buildcraftbuilders:architect
			classBlockBuilder = Class.forName("buildcraft.builders.block.BlockBuilder"); // id is buildcraftbuilders:builder
			classBlockFiller = Class.forName("buildcraft.builders.block.BlockFiller"); // id is buildcraftbuilders:filler
			classBlockQuarry = Class.forName("buildcraft.builders.block.BlockQuarry"); // id is buildcraftbuilders:quarry
			classBlockReplacer = Class.forName("buildcraft.builders.block.BlockReplacer"); // id is buildcraftbuilders:replacer
			classBlockZonePlanner = Class.forName("buildcraft.robotics.block.BlockZonePlanner"); // id is buildcraftrobotics:zone_planner
			
			WarpDriveConfig.registerBlockTransformer("BuildCraft", new CompatBuildCraft());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockBCBase_Neptune.isInstance(blockState.getBlock())
		    || classBlockPipeHolder.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		if ( classBlockArchitectTable.isInstance(blockState.getBlock())
		  || classBlockBuilder.isInstance(blockState.getBlock())
		  || classBlockFiller.isInstance(blockState.getBlock())
		  || classBlockQuarry.isInstance(blockState.getBlock())
		  || classBlockReplacer.isInstance(blockState.getBlock())
		  || classBlockZonePlanner.isInstance(blockState.getBlock()) ) {
			reason.append(Commons.getStyleWarning(), "warpdrive.compat.guide.block_detected_on_board",
			              new TranslationTextComponent(block.getTranslationKey()));
			return false;
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
	
	//                                                  0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final int[]   rotFacing         = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   rotHorizontal     = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   rotHorizontalOr4  = {  1,  2,  3,  0,  5,  6,  7,  8,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   rotWire           = {  4,  0,  6,  2,  5,  1,  7,  3,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	private static final Map<String, String> rotNames;
	private static final Map<Integer, Integer> maskFacing;
	static {
		final Map<String, String> stringMap = new HashMap<>();
		stringMap.put("DOWN", "DOWN");
		stringMap.put("UP", "UP");
		stringMap.put("NORTH", "EAST");
		stringMap.put("EAST", "SOUTH");
		stringMap.put("SOUTH", "WEST");
		stringMap.put("WEST", "NORTH");
		
		stringMap.put("down", "down");
		stringMap.put("up", "up");
		stringMap.put("north", "east");
		stringMap.put("east", "south");
		stringMap.put("south", "west");
		stringMap.put("west", "north");
		rotNames = Collections.unmodifiableMap(stringMap);
		
		final Map<Integer, Integer> integerMap = new HashMap<>();
		integerMap.put(0x01, 0x01); // down
		integerMap.put(0x02, 0x02); // up
		integerMap.put(0x04, 0x20); // north -> east
		integerMap.put(0x08, 0x10); // south -> west
		integerMap.put(0x10, 0x04); // west -> north
		integerMap.put(0x20, 0x08); // east -> south
		maskFacing = Collections.unmodifiableMap(integerMap);
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		if (nbtTileEntity == null) {
			return blockState;
		}
		
		final String idTileEntity = nbtTileEntity.getString("id");
		switch(idTileEntity) {
		// no rotation
		/*
		case "buildcraftfactory:autoworkbench_item":
		case "buildcraftfactory:tank":
		case "buildcraftsilicon:advanced_crafting_table":
		case "buildcraftsilicon:assembly_table":
		case "buildcraftsilicon:integration_table":
		case "buildcrafttransport:filtered_buffer":
		case decoration...
		case spring oil...
		case plastic color...
		case water gel...
			break;
		*/
			
		// engines
		case "buildcraftcore:engine.wood" :
		case "buildcraftenergy:engine.stone" :
		case "buildcraftenergy:engine.iron" :
		case "buildcraftcore:engine.creative" :
			if (nbtTileEntity.contains("currentDirection")) {
				final String nameDirection = nbtTileEntity.getString("currentDirection");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putString("currentDirection", rotNames.get(nameDirection));
					return blockState;
				case 2:
					nbtTileEntity.putString("currentDirection", rotNames.get(rotNames.get(nameDirection)));
					return blockState;
				case 3:
					nbtTileEntity.putString("currentDirection", rotNames.get(rotNames.get(rotNames.get(nameDirection))));
					return blockState;
				default:
					return blockState;
				}
			}
			return blockState;
			
		// vanilla facing
		case "buildcraftcore:marker.volume":
		case "buildcraftcore:marker.path":
		case "buildcraftfactory:chute":
		case "buildcraftsilicon:laser":
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
			
		// horizontal facing
		case "buildcraftbuilders:builder":
		case "buildcraftbuilders:filler":
		case "buildcraftbuilders:library":
		case "buildcraftbuilders:quarry":
		case "buildcraftbuilders:replacer":
		case "buildcraftfactory:distiller":
		case "buildcraftfactory:heat_exchange":
		case "buildcraftfactory:mining_well":
		case "buildcraftrobotics:zone_planner":
			switch (rotationSteps) {
			case 1:
				return rotHorizontal[metadata];
			case 2:
				return rotHorizontal[rotHorizontal[metadata]];
			case 3:
				return rotHorizontal[rotHorizontal[rotHorizontal[metadata]]];
			default:
				return blockState;
			}
			
		// horizontal facing with 'active' flag
		case "buildcraftbuilders:architect":
			switch (rotationSteps) {
			case 1:
				return rotHorizontalOr4[metadata];
			case 2:
				return rotHorizontalOr4[rotHorizontalOr4[metadata]];
			case 3:
				return rotHorizontalOr4[rotHorizontalOr4[rotHorizontalOr4[metadata]]];
			default:
				return blockState;
			}
			
		// Flood gate uses a bitmask in the same order as Direction enum
		case "buildcraftfactory:flood_gate":
			final int openSidesOld = nbtTileEntity.getByte("openSides");
			int openSidesNew = 0x00;
			for (final int maskOld : maskFacing.keySet()) {
				final boolean isClosed = (openSidesOld & maskOld) == 0;
				if (isClosed) {// fast path: skip what's already closed/0
					continue;
				}
				switch (rotationSteps) {
				case 1:
					openSidesNew |= maskFacing.get(maskOld);
					break;
				case 2:
					openSidesNew |= maskFacing.get(maskFacing.get(maskOld));
					break;
				case 3:
					openSidesNew |= maskFacing.get(maskFacing.get(maskFacing.get(maskOld)));
					break;
				default:
					openSidesNew |= maskOld;
					break;
				}
			}
			nbtTileEntity.putByte("openSides", (byte) openSidesNew);
			break;
			
		// pipes are whole different story
		case "buildcrafttransport:pipe_holder" :
			// redstone is all 0 or all 1, so we'll skip it for now
			// final int[] redstoneOld = nbtTileEntity.getIntArray("redstone");
			
			// behaviours are found in pipe.beh
			if (nbtTileEntity.contains("pipe")) {
				final CompoundNBT tagCompoundPipe = nbtTileEntity.getCompound("pipe");
				if (tagCompoundPipe.contains("beh")) {
					final CompoundNBT tagCompoundBehaviour = tagCompoundPipe.getCompound("beh");
					
					// directional behaviour is pipe.beh.currentDir = NORTH SOUTH WEST EAST
					if (tagCompoundBehaviour.contains("currentDir")) {
						final String currentDirOld = tagCompoundBehaviour.getString("currentDir");
						final String currentDirNew;
						switch (rotationSteps) {
						case 1:
							currentDirNew = rotNames.get(currentDirOld);
							break;
						case 2:
							currentDirNew = rotNames.get(rotNames.get(currentDirOld));
							break;
						case 3:
							currentDirNew = rotNames.get(rotNames.get(rotNames.get(currentDirOld)));
							break;
						default:
							currentDirNew = currentDirOld;
							break;
						}
						tagCompoundBehaviour.putString("currentDir", currentDirNew);
					}
					
					// filter behaviour is pipe.beh.filters.items (compound list of 54 elements)
					// indexes for first position are 36 18 45 27 => facing = index / 9, position = index % 9
					if (tagCompoundBehaviour.contains("filters")) {
						final CompoundNBT tagCompoundFilters = tagCompoundBehaviour.getCompound("filters");
						final ListNBT tagListFilterItemsOld = tagCompoundFilters.getList("items", NBT.TAG_COMPOUND);
						final int count = tagListFilterItemsOld.size();
						final NBTBase[] filterItemsNew = new NBTBase[count];
						for (int indexOld = 0; indexOld < count; indexOld++) {
							// compute new index
							final int position = indexOld % 9;
							final int facingOld = (indexOld - position) / 9;
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
							final int indexNew = facingNew * 9 + position;
							
							// save value at new position in temporary array
							filterItemsNew[indexNew] = tagListFilterItemsOld.getCompound(indexOld);
						}
						// rebuild list in order
						final ListNBT tagListFilterItemsNew = new ListNBT();
						for (int indexNew = 0; indexNew < count; indexNew++) {
							tagListFilterItemsNew.add(filterItemsNew[indexNew]);
						}
						tagCompoundFilters.put("items", tagListFilterItemsNew);
					}
				}
			}
			
			// wires are array of pairs (position, type)
			// position is 0 4 5 1 / 2 6 7 3
			if (nbtTileEntity.contains("wireManager")) {
				final CompoundNBT tagCompoundWireManager = nbtTileEntity.getCompound("wireManager");
				if (tagCompoundWireManager.contains("parts")) {
					final int[] partsOld = tagCompoundWireManager.getIntArray("parts");
					if (partsOld.length > 0) {
						final int[] partsNew = new int[partsOld.length];
						for (int index = 0; index < partsOld.length; index += 2) {
							final int positionOld = partsOld[index];
							final int positionNew;
							switch (rotationSteps) {
							case 1:
								positionNew = rotWire[positionOld];
								break;
							case 2:
								positionNew = rotWire[rotWire[positionOld]];
								break;
							case 3:
								positionNew = rotWire[rotWire[rotWire[positionOld]]];
								break;
							default:
								positionNew = positionOld;
								break;
							}
							partsNew[index] = positionNew;
							partsNew[index + 1] = partsOld[index + 1];
							// nota: unlike the mod itself, we're not reordering the positions
						}
						tagCompoundWireManager.putIntArray("parts", partsNew);
					}
				}
			}
			
			// plugs, gates and facades are compound names by face in lower case (north south west east)
			if (nbtTileEntity.contains("plugs")) {
				final CompoundNBT tagCompoundPlugs = nbtTileEntity.getCompound("plugs");
				
				final Map<String, INBT> mapNew = new HashMap<>(rotNames.size());
				for (final String nameOld : rotNames.keySet()) {
					if (!tagCompoundPlugs.contains(nameOld)) {
						continue;
					}
					final INBT tagValue = tagCompoundPlugs.get(nameOld);
					final String nameNew;
					switch (rotationSteps) {
					case 1:
						nameNew = rotNames.get(nameOld);
						break;
					case 2:
						nameNew = rotNames.get(rotNames.get(nameOld));
						break;
					case 3:
						nameNew = rotNames.get(rotNames.get(rotNames.get(nameOld)));
						break;
					default:
						nameNew = nameOld;
						break;
					}
					mapNew.put(nameNew, tagValue);
					tagCompoundPlugs.remove(nameOld);
				}
				for (final Entry<String, INBT> entry : mapNew.entrySet()) {
					tagCompoundPlugs.put(entry.getKey(), entry.getValue());
				}
			}
			
			return blockState;
			
		// pump needs to be reset
		case "buildcraftfactory:pump" :
			nbtTileEntity.remove("currentPos");
			nbtTileEntity.putInt("progress", 0);
			nbtTileEntity.putInt("wantedLength", 0);
			return blockState;
			
		default:
			break;
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
