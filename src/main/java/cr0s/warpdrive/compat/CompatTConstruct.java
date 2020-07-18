package cr0s.warpdrive.compat;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;

public class CompatTConstruct implements IBlockTransformer {
	
	private static Class<?> classBlockChannel;
	private static Class<?> classBlockFaucet;
	
	private static Class<?> classBlockCasting;
	private static Class<?> classBlockToolForge;
	private static Class<?> classBlockToolTable;
	private static Class<?> classBlockRack;
	
	private static Class<?> classBlockSearedFurnaceController;
	private static Class<?> classBlockSmelteryController;
	private static Class<?> classBlockTinkerTankController;
	
	private static Class<?> classBlockSlimeChannel;
	
	private static Class<?> classBlockEnumSmeltery;
	private static Class<?> classBlockSmelteryIO;
	
	private static Class<?> classBlockStairsBase;
	private static Class<?> classEnumBlockSlab;
	
	public static void register() {
		try {
			classBlockChannel = Class.forName("slimeknights.tconstruct.smeltery.block.BlockChannel");
			classBlockFaucet = Class.forName("slimeknights.tconstruct.smeltery.block.BlockFaucet");
			
			classBlockCasting = Class.forName("slimeknights.tconstruct.smeltery.block.BlockCasting");
			classBlockToolForge = Class.forName("slimeknights.tconstruct.tools.common.block.BlockToolForge");
			classBlockToolTable = Class.forName("slimeknights.tconstruct.tools.common.block.BlockToolTable");
			classBlockRack = Class.forName("slimeknights.tconstruct.gadgets.block.BlockRack");
			
			classBlockSearedFurnaceController = Class.forName("slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController");
			classBlockSmelteryController = Class.forName("slimeknights.tconstruct.smeltery.block.BlockSmelteryController");
			classBlockTinkerTankController = Class.forName("slimeknights.tconstruct.smeltery.block.BlockTinkerTankController");
			
			classBlockSlimeChannel = Class.forName("slimeknights.tconstruct.gadgets.block.BlockSlimeChannel");
			
			classBlockEnumSmeltery = Class.forName("slimeknights.tconstruct.smeltery.block.BlockEnumSmeltery");
			classBlockSmelteryIO = Class.forName("slimeknights.tconstruct.smeltery.block.BlockSmelteryIO"); // Smeltery Drain
			
			classBlockStairsBase = Class.forName("slimeknights.mantle.block.BlockStairsBase");
			classEnumBlockSlab = Class.forName("slimeknights.mantle.block.EnumBlockSlab");
			WarpDriveConfig.registerBlockTransformer("tconstruct", new CompatTConstruct());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockChannel.isInstance(blockState.getBlock())
		    || classBlockFaucet.isInstance(blockState.getBlock())
		       
		    || classBlockCasting.isInstance(blockState.getBlock())
		    || classBlockToolForge.isInstance(blockState.getBlock())
		    || classBlockToolTable.isInstance(blockState.getBlock())
		    || classBlockRack.isInstance(blockState.getBlock())
		       
			|| classBlockSearedFurnaceController.isInstance(blockState.getBlock())
		    || classBlockSmelteryController.isInstance(blockState.getBlock())
		    || classBlockTinkerTankController.isInstance(blockState.getBlock())
		       
		    || classBlockSlimeChannel.isInstance(blockState.getBlock())
		       
		    || classBlockEnumSmeltery.isInstance(blockState.getBlock())
//		    || classBlockSmelteryIO.isInstance(blockState.getBlock())       (derived from classBlockEnumSmeltery, no point to test it here)
		       
		    || classBlockStairsBase.isInstance(blockState.getBlock())
		    || classEnumBlockSlab.isInstance(blockState.getBlock());
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
	
	
	/*
	As of Mantle-1.12-1.3.3.39 + TConstruct-1.12.2-2.11.0.106
	
	Derived from Block
	slimeknights.tconstruct.shared.block.BlockGlow
		=> use default rotation handler
	slimeknights.tconstruct.gadgets.block.BlockPunji
		=> use default rotation handler
	
	Derived from BlockContainer
	slimeknights.tconstruct.smeltery.block.BlockChannel                     minecraft:tconstruct.channel
		connections byte[4]  (index rotation 0 1 2 3)
			0 (no connection), 1 (input), 2 (output)
		is_flowing  byte[4]  (we're assuming it's the same as connections)
	slimeknights.tconstruct.smeltery.block.BlockFaucet                      minecraft:tconstruct.faucet
		direction   2 5 3 4
		metadata    2 5 3 4
	slimeknights.tconstruct.gadgets.block.BlockWoodenHopper                 TileEntityHopper
		metadata    2 5 3 4
		=> use default rotation handler
	
	Derived from BlockInventory
	slimeknights.tconstruct.smeltery.block.BlockCasting                     minecraft:tconstruct.casting_basin / TileCasting
		ForgeData.facing    int     2 5 3 4
	Derived from BlockInventory > BlockTable
	slimeknights.tconstruct.tools.common.block.BlockToolForge               TileToolForge
		ForgeData.facing    int     2 5 3 4
	slimeknights.tconstruct.tools.common.block.BlockToolTable               TileCraftingStation, TileStencilTable, TilePartBuilder, TileToolStation, TilePatternChest, TilePartChest
		ForgeData.facing    int     2 5 3 4
	slimeknights.tconstruct.gadgets.block.BlockRack                         minecraft:tconstruct.item_rack / minecraft:tconstruct.drying_rack
		ForgeData.facing    int     2 5 3 4
		metadata    0 14 / 1 15 / 2 6 4 8 / 3 7 5 9 / 10 12 / 11 13
	
	Derived from BlockMultiblockController
	slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController     minecraft:tconstruct.seared_furnace
		active  boolean true (it's formed) / false (invalid structure)
		minPos.X/Y/Z    int
		maxPos.X/Y/Z    int
		tanks           List<Compound>
			X/Y/Z    int
		metadata    0 1 2 3
	slimeknights.tconstruct.smeltery.block.BlockSmelteryController          minecraft:tconstruct.smeltery_controller
		active  boolean true (it's formed) / false (invalid structure)
		insidePos.X/Y/Z int  (inside, Y value seems random)
		minPos.X/Y/Z    int  (the inner side content)
		maxPos.X/Y/Z    int  (the inner side content)
		tanks           List<Compound>
			X/Y/Z    int
		metadata    2 5 3 4
	slimeknights.tconstruct.smeltery.block.BlockTinkerTankController        minecraft:tconstruct.tinker_tank
		active  boolean true (it's formed) / false (invalid structure)
		minPos.X/Y/Z    int
		maxPos.X/Y/Z    int
		metadata    2 5 3 4
	
	Derived from slimeknights.mantle.block.EnumBlock
	slimeknights.tconstruct.gadgets.block.BlockSlimeChannel                 minecraft:tconstruct.slime_channel
		ForgeData.side        2 5 3 4
		ForgeData.direction   0 2 4 6 / 1 3 5 7
		metadata 0x7 type / 0x8 powered
	
	Derived from slimeknights.mantle.block.EnumBlock
	             > slimeknights.tconstruct.smeltery.block.BlockEnumSmeltery
	slimeknights.tconstruct.smeltery.block.BlockSeared                      minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata (type)
	slimeknights.tconstruct.smeltery.block.BlockSearedGlass                 minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata (type)
	slimeknights.tconstruct.smeltery.block.BlockSmelteryIO                  minecraft:tconstruct.smeltery_drain
		masterState int     (Block.getStateId())
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata    0 4 8 12 / 1 5 9 13 / 2 6 10 14 / 3 7 11 15
	slimeknights.tconstruct.smeltery.block.BlockTank                        minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
	
	Derived from slimeknights.mantle.block.BlockStairsBase
	slimeknights.tconstruct.smeltery.block.BlockSearedStairs                minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata (see vanilla BlockStairs)
	
	Derived from slimeknights.mantle.block.EnumBlockSlab
	BlockSearedSlab                                                         minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata (type)
	BlockSearedSlab2                                                        minecraft:tconstruct.smeltery_component
		hasMaster   boolean true (it's formed) / false (no controller)
		xCenter/yCenter/zCenter int (the controller)
		metadata (type)
	*/
	
	//                                                  0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final int[]  mrotStair          = {  2,  3,  1,  0,  6,  7,  5,  4,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  mrotFacing         = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  mrotHorizontal     = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  mrotDrain          = {  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  0,  1,  2,  3 };
	private static final int[]  mrotRack           = { 14, 15,  6,  7,  8,  9,  4,  5,  2,  3, 12, 13, 10, 11,  0,  1 };
	private static final int[]  rotSlimeDirection  = {  2,  3,  4,  5,  6,  7,  0,  1,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		// *** NBT transformation
		if (nbtTileEntity != null) {
			// ForgeData compound
			if (nbtTileEntity.contains("ForgeData", NBT.TAG_COMPOUND)) {
				final CompoundNBT nbtForgeData = nbtTileEntity.getCompound("ForgeData");
				
				// facing from Tables, Castings and Racks is just facing
				if (nbtForgeData.contains("facing")) {
					final int facing = nbtForgeData.getInt("facing");
					switch (rotationSteps) {
					case 1:
						nbtForgeData.putInt("facing", mrotFacing[facing]);
						break;
					case 2:
						nbtForgeData.putInt("facing", mrotFacing[mrotFacing[facing]]);
						break;
					case 3:
						nbtForgeData.putInt("facing", mrotFacing[mrotFacing[mrotFacing[facing]]]);
						break;
					default:
						break;
					}
				}
				
				// side from slime channels is just facing
				if ( nbtForgeData.contains("side")
				  && nbtForgeData.contains("direction") ) {
					final int side = nbtForgeData.getInt("side");
					switch (rotationSteps) {
					case 1:
						nbtForgeData.putInt("side", mrotFacing[side]);
						break;
					case 2:
						nbtForgeData.putInt("side", mrotFacing[mrotFacing[side]]);
						break;
					case 3:
						nbtForgeData.putInt("side", mrotFacing[mrotFacing[mrotFacing[side]]]);
						break;
					default:
						break;
					}
					
					// direction from slime channels is power and horizontal rotation
					if (side == 0 || side == 1) {
						final int direction = nbtForgeData.getInt("direction");
						switch (rotationSteps) {
						case 1:
							nbtForgeData.putInt("direction", rotSlimeDirection[direction]);
							break;
						case 2:
							nbtForgeData.putInt("direction", rotSlimeDirection[rotSlimeDirection[direction]]);
							break;
						case 3:
							nbtForgeData.putInt("direction", rotSlimeDirection[rotSlimeDirection[rotSlimeDirection[direction]]]);
							break;
						default:
							break;
						}
					}
				}
			}
			
			// Channel connections
			if (nbtTileEntity.contains("connections")) {
				final byte[] bytesOldConnections = nbtTileEntity.getByteArray("connections");
				final byte[] bytesNewConnections = bytesOldConnections.clone();
				for (int sideOld = 0; sideOld < 4; sideOld++) {
					final byte byteConnection = bytesOldConnections[sideOld];
					bytesNewConnections[(sideOld + rotationSteps) % 4] = byteConnection;
				}
				nbtTileEntity.putByteArray("connections", bytesNewConnections);
			}
			
			// smeltery components
			if (nbtTileEntity.getBoolean("hasMaster")) {// (defined and non-zero means there's a master/controller)
				if ( nbtTileEntity.contains("xCenter", NBT.TAG_INT)
				  && nbtTileEntity.contains("yCenter", NBT.TAG_INT) 
				  && nbtTileEntity.contains("zCenter", NBT.TAG_INT) ) {
					final BlockPos blockPosCenter = transformation.apply(
							nbtTileEntity.getInt("xCenter"),
							nbtTileEntity.getInt("yCenter"),
							nbtTileEntity.getInt("zCenter") );
					nbtTileEntity.putInt("xCenter", blockPosCenter.getX());
					nbtTileEntity.putInt("yCenter", blockPosCenter.getY());
					nbtTileEntity.putInt("zCenter", blockPosCenter.getZ());
				} else {
					WarpDrive.logger.warn(String.format("Missing center coordinates for 'smeltery' component %s:%s %s",
					                                    block, metadata, nbtTileEntity));
				}
			}
			
			// controllers
			if (nbtTileEntity.getBoolean("active")) {// (defined and non-zero means the structure is valid)
				// mandatory min/max position of the inner volume
				if ( nbtTileEntity.contains("minPos", NBT.TAG_COMPOUND)
				  && nbtTileEntity.contains("maxPos", NBT.TAG_COMPOUND) ) {
					final CompoundNBT nbtMinOldPos = nbtTileEntity.getCompound("minPos");
					final CompoundNBT nbtMaxOldPos = nbtTileEntity.getCompound("maxPos");
					
					if ( nbtMinOldPos.contains("X", NBT.TAG_INT)
				      && nbtMinOldPos.contains("Y", NBT.TAG_INT)
				      && nbtMinOldPos.contains("Z", NBT.TAG_INT)
					  && nbtMaxOldPos.contains("X", NBT.TAG_INT)
					  && nbtMaxOldPos.contains("Y", NBT.TAG_INT)
					  && nbtMaxOldPos.contains("Z", NBT.TAG_INT) ) {
						final BlockPos blockPosNew1 = transformation.apply(
								nbtMinOldPos.getInt("X"),
								nbtMinOldPos.getInt("Y"),
								nbtMinOldPos.getInt("Z") );
						final BlockPos blockPosNew2 = transformation.apply(
								nbtMaxOldPos.getInt("X"),
								nbtMaxOldPos.getInt("Y"),
								nbtMaxOldPos.getInt("Z") );
						
						nbtMinOldPos.putInt("X", Math.min(blockPosNew1.getX(), blockPosNew2.getX()));
						nbtMinOldPos.putInt("Y", Math.min(blockPosNew1.getY(), blockPosNew2.getY()));
						nbtMinOldPos.putInt("Z", Math.min(blockPosNew1.getZ(), blockPosNew2.getZ()));
						nbtMaxOldPos.putInt("X", Math.max(blockPosNew1.getX(), blockPosNew2.getX()));
						nbtMaxOldPos.putInt("Y", Math.max(blockPosNew1.getY(), blockPosNew2.getY()));
						nbtMaxOldPos.putInt("Z", Math.max(blockPosNew1.getZ(), blockPosNew2.getZ()));
					} else {
						WarpDrive.logger.warn(String.format("Missing X/Y/Z components for inner volume of controller %s:%s %s",
						                                    block, metadata, nbtTileEntity));
					}
				} else {
					WarpDrive.logger.warn(String.format("Missing minPos/maxPos compound data for component %s:%s %s",
					                                    block, metadata, nbtTileEntity));
				}
				
				// optional list of tank's absolute position
				if (nbtTileEntity.contains("tanks", NBT.TAG_LIST)) {
					final ListNBT listTanks = nbtTileEntity.getList("tanks", NBT.TAG_COMPOUND);
					for (int index = 0; index < listTanks.size(); index++) {
						final CompoundNBT nbtValue = (CompoundNBT) listTanks.get(index);
						
						if ( nbtValue.contains("X", NBT.TAG_INT)
						  && nbtValue.contains("Y", NBT.TAG_INT)
						  && nbtValue.contains("Z", NBT.TAG_INT) ) {
							final BlockPos blockPosNew = transformation.apply(
									nbtValue.getInt("X"),
									nbtValue.getInt("Y"),
									nbtValue.getInt("Z") );
							
							nbtValue.putInt("X", blockPosNew.getX());
							nbtValue.putInt("Y", blockPosNew.getY());
							nbtValue.putInt("Z", blockPosNew.getZ());
						} else {
							WarpDrive.logger.warn(String.format("Missing X/Y/Z components for tank#%d of controller %s:%s %s",
							                                    index, block, metadata, nbtTileEntity));
						}
						listTanks.set(index, nbtValue);
					}
				}
				
				// optional insidePos absolute position
				if (nbtTileEntity.contains("insidePos", NBT.TAG_COMPOUND)) {
					final CompoundNBT nbtInsidePos = nbtTileEntity.getCompound("insidePos");
					if ( nbtInsidePos.contains("X", NBT.TAG_INT)
					  && nbtInsidePos.contains("Y", NBT.TAG_INT)
					  && nbtInsidePos.contains("Z", NBT.TAG_INT) ) {
						final BlockPos blockPosNew = transformation.apply(
								nbtInsidePos.getInt("X"),
								nbtInsidePos.getInt("Y"),
								nbtInsidePos.getInt("Z") );
						
						nbtInsidePos.putInt("X", blockPosNew.getX());
						nbtInsidePos.putInt("Y", blockPosNew.getY());
						nbtInsidePos.putInt("Z", blockPosNew.getZ());
					} else {
						WarpDrive.logger.warn(String.format("Missing X/Y/Z components for insidePos of controller %s:%s %s",
						                                    block, metadata, nbtTileEntity));
					}
				}
			}
		}
		
		// *** metadata rotation
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// Rack is custom type & facing
		if (classBlockRack.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotRack[metadata];
			case 2:
				return mrotRack[mrotRack[metadata]];
			case 3:
				return mrotRack[mrotRack[mrotRack[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Seared furnace is horizontal facing
		if (classBlockSearedFurnaceController.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotHorizontal[metadata];
			case 2:
				return mrotHorizontal[mrotHorizontal[metadata]];
			case 3:
				return mrotHorizontal[mrotHorizontal[mrotHorizontal[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Faucet, Smeltery controller, Tinker Tank are just facing
		if ( classBlockFaucet.isInstance(blockState.getBlock())
		  || classBlockSmelteryController.isInstance(blockState.getBlock())
		  || classBlockTinkerTankController.isInstance(blockState.getBlock()) ) {
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
		
		// Smeltery drain is type & horizontal facing
		if (classBlockSmelteryIO.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotDrain[metadata];
			case 2:
				return mrotDrain[mrotDrain[metadata]];
			case 3:
				return mrotDrain[mrotDrain[mrotDrain[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Stairs is like vanilla
		if (classBlockStairsBase.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotStair[metadata];
			case 2:
				return mrotStair[mrotStair[metadata]];
			case 3:
				return mrotStair[mrotStair[mrotStair[metadata]]];
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
