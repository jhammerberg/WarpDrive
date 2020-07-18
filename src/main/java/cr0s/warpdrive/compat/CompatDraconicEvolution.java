package cr0s.warpdrive.compat;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants.NBT;

public class CompatDraconicEvolution implements IBlockTransformer {
	
	// common block for fast detection
	private static Class<?> classBlockBlockDE;
	
	// block anchors by lore (portal)
	private static Class<?> classBlockDislocatorReceptacle;
	private static Class<?> classBlockPortal;
	
	// blocks with only metadata
	private static Class<?> classBlockFlowGate;
	private static Class<?> classBlockGenerator;
	private static Class<?> classBlockGrinder;
	private static Class<?> classBlockPotentiometer;
	
	// blocks with just rotation
	private static Class<?> classBlockDislocatorPedestal;
	private static Class<?> classBlockDraconiumChest;
	private static Class<?> classBlockPlacedItem;
	
	// blocks with rotation and position(s)
	// private static Class<?> classBlockCraftingInjector;
	// private static Class<?> classBlockEnergyCrystal;
	// private static Class<?> classBlockEnergyStorageCore;
	// private static Class<?> classBlockEnergyPylon;
	private static Class<?> classBlockInvisECoreBlock;
	// private static Class<?> classBlockParticleGenerator;
	// private static Class<?> classBlockReactorComponent;
	// private static Class<?> classBlockReactorCore;
	
	public static void register() {
		try {
			classBlockBlockDE = Class.forName("com.brandon3055.brandonscore.blocks.BlockBCore");
			
			// *** block anchors by lore (portal)
			classBlockDislocatorReceptacle = Class.forName("com.brandon3055.draconicevolution.blocks.DislocatorReceptacle");
			classBlockPortal = Class.forName("com.brandon3055.draconicevolution.blocks.Portal");
			
			// *** blocks with only metadata
			classBlockFlowGate = Class.forName("com.brandon3055.draconicevolution.blocks.machines.FlowGate");
			classBlockGenerator = Class.forName("com.brandon3055.draconicevolution.blocks.machines.Generator");
			classBlockGrinder = Class.forName("com.brandon3055.draconicevolution.blocks.machines.Grinder");
			classBlockPotentiometer = Class.forName("com.brandon3055.draconicevolution.blocks.Potentiometer");
			
			// *** blocks with just rotation
			classBlockDislocatorPedestal = Class.forName("com.brandon3055.draconicevolution.blocks.DislocatorPedestal");
			classBlockDraconiumChest = Class.forName("com.brandon3055.draconicevolution.blocks.DraconiumChest");
			classBlockPlacedItem = Class.forName("com.brandon3055.draconicevolution.blocks.PlacedItem");
			
			// *** blocks with rotation and position(s)
			// classBlockCraftingInjector = Class.forName("com.brandon3055.draconicevolution.blocks.machines.CraftingInjector");
			// classBlockEnergyCrystal = Class.forName("com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal");
			// classBlockEnergyStorageCore = Class.forName("com.brandon3055.draconicevolution.blocks.machines.EnergyStorageCore");
			// classBlockEnergyPylon = Class.forName("com.brandon3055.draconicevolution.blocks.machines.EnergyPylon");
			classBlockInvisECoreBlock = Class.forName("com.brandon3055.draconicevolution.blocks.InvisECoreBlock");
			// classBlockParticleGenerator = Class.forName("com.brandon3055.draconicevolution.blocks.ParticleGenerator");
			// classBlockReactorComponent = Class.forName("com.brandon3055.draconicevolution.blocks.reactor.ReactorComponent");
			// classBlockReactorCore = Class.forName("com.brandon3055.draconicevolution.blocks.reactor.ReactorCore");
			
			WarpDriveConfig.registerBlockTransformer("DraconicEvolution", new CompatDraconicEvolution());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockBlockDE.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		if ( classBlockDislocatorReceptacle.isInstance(blockState.getBlock())
		  || classBlockPortal.isInstance(blockState.getBlock()) ) {
			reason.append(Commons.getStyleWarning(), "warpdrive.compat.guide.draconic_evolution_portal");
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
	
	/*
	// *** block anchors by lore (portal)
	com.brandon3055.draconicevolution.blocks.DislocatorReceptacle
		list<int>    BCManagedData.CRYSTAL_LINK_POS (x y z to be clarified)
		list<int>    BCManagedData.CRYSTAL_POS (x y z to be clarified)
		list<int>    BCManagedData.SPAWN_POS (x y z to be clarified)
	com.brandon3055.draconicevolution.blocks.Portal
		list<int>    BCManagedData.masterPos (x y z offset from dislocator to this block)
	
	// *** blocks with only metadata
	com.brandon3055.draconicevolution.blocks.machines.FlowGate
		metadata    0 / 1 3 2 4 / 5 / (6 7 ?) / 8 / 9 11 10 12 / 13 / (14 15 ?)
	com.brandon3055.draconicevolution.blocks.machines.Generator
	com.brandon3055.draconicevolution.blocks.machines.Grinder
		metadata    0 1 5 3 4 2
	com.brandon3055.draconicevolution.blocks.Potentiometer
		metadata 0 / 1 3 2 4 / 5
	
	// *** blocks with just rotation
	com.brandon3055.draconicevolution.blocks.DislocatorPedestal
		int   BCManagedData.rotation int -7 to 8 clockwise => ((old + 8 + 4) % 16) - 8
	com.brandon3055.draconicevolution.blocks.DraconiumChest
		byte  BCManagedData.facing  0 1 5 3 4 2
	com.brandon3055.draconicevolution.blocks.PlacedItem
	    int   BCManagedData.rotation0 + 4 or -4 only when metadata is 0 or 1
		int   Facing 0 1 5 3 4 2
		metadata    0 1 5 3 4 2
	
	// *** blocks with rotation and position(s)
	com.brandon3055.draconicevolution.blocks.machines.CraftingInjector
		list<int>    BCManagedData.lastCorePos x y z (defaults to 0 0 0, absolute position)
	com.brandon3055.draconicevolution.blocks.energynet.EnergyCrystal
		int          BCManagedData.facing 0 1 5 3 4 2 (optional)
		list<byte[]> LinkedCrystals (x y z offset from another crystal to this block)
	com.brandon3055.draconicevolution.blocks.machines.EnergyStorageCore
		bool         BCManagedData.stabilizersOK 1 when offsets are valid
		list<int>    BCManagedData.stabOffset0/1/2/3 x y z (defaults to 0 -1 0, offset from stabilizer to this block)
	com.brandon3055.draconicevolution.blocks.machines.EnergyPylon
		list<int>    BCManagedData.coreOffset (defaults to 0 -1 0, x y z offset from core to this block)
		bool         BCManagedData.structureValid 1 when offsets are valid
	com.brandon3055.draconicevolution.blocks.InvisECoreBlock
		list<int>    BCManagedData.coreOffset (defaults to ? ? ?, x y z offset from core to this block)
	com.brandon3055.draconicevolution.blocks.ParticleGenerator
		list<int>    BCManagedData.coreOffset (defaults to 0 -1 0, x y z offset from core to this block)
		bool         BCManagedData.hasCoreLock 1 when offsets are valid
	com.brandon3055.draconicevolution.blocks.reactor.ReactorComponent
		list<int>    BCManagedData.coreOffset (defaults to 0 0 0, x y z offset from core to this block)
		bool         BCManagedData.isBound 1 when offset is valid
	com.brandon3055.draconicevolution.blocks.reactor.ReactorCore
		list<int>    BCManagedData.componentPosition0/1/2/3/4/5 (defaults to 0 0 0, x y z offset from component to this block)
		(0 to 5 are ordered like Direction enum)
	*/
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] byteFacing          = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  intFacing           = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]  rotFlowGate         = {  0,  3,  4,  2,  1,  5,  6,  7,  8, 11, 12, 10,  9, 13, 14, 15 };
	private static final int[]  rotPotentiometer    = {  0,  3,  4,  2,  1,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 && nbtTileEntity == null) {
			return blockState;
		}
		
		// *** blocks with only metadata
		// FlowGate
		if (classBlockFlowGate.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotFlowGate[metadata];
			case 2:
				return rotFlowGate[rotFlowGate[metadata]];
			case 3:
				return rotFlowGate[rotFlowGate[rotFlowGate[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Generator & Grinder
		if ( classBlockGenerator.isInstance(blockState.getBlock())
		  || classBlockGrinder.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return intFacing[metadata];
			case 2:
				return intFacing[intFacing[metadata]];
			case 3:
				return intFacing[intFacing[intFacing[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Potentiometer
		if (classBlockPotentiometer.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotPotentiometer[metadata];
			case 2:
				return rotPotentiometer[rotPotentiometer[metadata]];
			case 3:
				return rotPotentiometer[rotPotentiometer[rotPotentiometer[metadata]]];
			default:
				return blockState;
			}
		}
		
		
		final CompoundNBT tagCompoundBCManagedData;
		if (nbtTileEntity != null && nbtTileEntity.contains("BCManagedData")) {
			tagCompoundBCManagedData = nbtTileEntity.getCompound("BCManagedData");
		} else {
			tagCompoundBCManagedData = null;
		}
		
		// *** blocks with just rotation
		// Dislocator pedestal
		if (classBlockDislocatorPedestal.isInstance(blockState.getBlock())) {
			if (tagCompoundBCManagedData == null) {
				return blockState;
			}
			if (rotationSteps > 0) {
				final int rotationOld = tagCompoundBCManagedData.getInt("rotation");
				final int rotationNew = ((rotationOld + 8 + 4 * rotationSteps) % 16) - 8;
				tagCompoundBCManagedData.putInt("rotation", rotationNew);
			}
			return blockState;
		}
		
		// Draconium chest
		if (classBlockDraconiumChest.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotPotentiometer[metadata];
			case 2:
				return rotPotentiometer[rotPotentiometer[metadata]];
			case 3:
				return rotPotentiometer[rotPotentiometer[rotPotentiometer[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Placed item rotates by metadata vertically, by NBT horizontally
		if (classBlockPlacedItem.isInstance(blockState.getBlock())) {
			if (tagCompoundBCManagedData == null) {
				return blockState;
			}
			if (metadata == 0 || metadata == 1) {// placed horizontally
				final int rotationOld = tagCompoundBCManagedData.getInt("rotation0");
				final int rotationNew;
				if (metadata == 0) {
					rotationNew = (rotationOld + 4 * rotationSteps) % 16;
				} else {
					rotationNew = (rotationOld + 12 * rotationSteps) % 16;
				}
				tagCompoundBCManagedData.putInt("rotation0", rotationNew);
				return blockState;
			}
			
			// (placed vertically)
			final byte facing = nbtTileEntity.getByte("Facing");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.putByte("Facing", byteFacing[facing]);
				return intFacing[metadata];
			case 2:
				nbtTileEntity.putByte("Facing", byteFacing[byteFacing[facing]]);
				return intFacing[intFacing[metadata]];
			case 3:
				nbtTileEntity.putByte("Facing", byteFacing[byteFacing[byteFacing[facing]]]);
				return intFacing[intFacing[intFacing[metadata]]];
			default:
				return blockState;
			}
		}
		
		// from there on, we need BCManagedData, so skip the other blocks altogether
		if (tagCompoundBCManagedData == null) {
			return blockState;
		}
		
		// *** blocks with rotation and position(s)
		// common optional "facing" property for EnergyCrystal and ReactorComponent
		if (tagCompoundBCManagedData.contains("facing")) {
			final int facing = tagCompoundBCManagedData.getInt("facing");
			switch (rotationSteps) {
			case 1:
				tagCompoundBCManagedData.putInt("facing", intFacing[facing]);
				break;
			case 2:
				tagCompoundBCManagedData.putInt("facing", intFacing[intFacing[facing]]);
				break;
			case 3:
				tagCompoundBCManagedData.putInt("facing", intFacing[intFacing[intFacing[facing]]]);
				break;
			default:
				break;
			}
		}
		
		// common optional "CoreDirection" property for ParticleGenerator
		if (tagCompoundBCManagedData.contains("CoreDirection")) {
			final int facing = tagCompoundBCManagedData.getInt("CoreDirection");
			switch (rotationSteps) {
			case 1:
				tagCompoundBCManagedData.putInt("CoreDirection", intFacing[facing]);
				break;
			case 2:
				tagCompoundBCManagedData.putInt("CoreDirection", intFacing[intFacing[facing]]);
				break;
			case 3:
				tagCompoundBCManagedData.putInt("CoreDirection", intFacing[intFacing[intFacing[facing]]]);
				break;
			default:
				break;
			}
		}
		
		// absolute coordinate "lastCorePos" for CraftingInjector
		if (tagCompoundBCManagedData.contains("lastCorePos")) {
			final ListNBT tagListLastCorePos = tagCompoundBCManagedData.getList("lastCorePos", NBT.TAG_INT);
			// There's "isValid" flag and it's an absolute coordinate defaulting to 0 0 0.
			// After jump, it would load 'random' chunks, so we're checking if it's inside the ship before transforming.
			// Position can be far outside the ship, so we'll reset to default if it's outside the ship.
			final int x = tagListLastCorePos.getIntAt(0);
			final int y = tagListLastCorePos.getIntAt(1);
			final int z = tagListLastCorePos.getIntAt(2);
			if (transformation.isInside(x, y, z)) {
				final BlockPos targetLink = transformation.apply(x, y, z);
				tagListLastCorePos.set(0, new IntNBT(targetLink.getX()));
				tagListLastCorePos.set(1, new IntNBT(targetLink.getY()));
				tagListLastCorePos.set(2, new IntNBT(targetLink.getZ()));
			} else {
				tagListLastCorePos.set(0, new IntNBT(0));
				tagListLastCorePos.set(1, new IntNBT(0));
				tagListLastCorePos.set(2, new IntNBT(0));
			}
		}
		
		// from now on we're transforming relative coordinates, so we'll need the block old and new coordinates of this block
		final BlockPos blockPosOld = new BlockPos(
				nbtTileEntity.getInt("x"),
				nbtTileEntity.getInt("y"),
				nbtTileEntity.getInt("z"));
		final BlockPos blockPosNew = transformation.apply(blockPosOld);
		
		// EnergyCrystal
		if (nbtTileEntity.contains("LinkedCrystals")) {
			final ListNBT tagListOldLinkedCrystals = nbtTileEntity.getList("LinkedCrystals", NBT.TAG_BYTE_ARRAY);
			final int countLinks = tagListOldLinkedCrystals.size();
			if (countLinks > 0) {
				final ListNBT tagListNewLinkedCrystals = new ListNBT();
				for (int index = 0; index < countLinks; index++) {
					final ByteArrayNBT listLinkedCrystal = (ByteArrayNBT) tagListOldLinkedCrystals.get(index);
					final byte[] byteLink = listLinkedCrystal.getByteArray();
					final int x = blockPosOld.getX() - byteLink[0];
					final int y = blockPosOld.getY() - byteLink[1];
					final int z = blockPosOld.getZ() - byteLink[2];
					if (transformation.isInside(x, y, z)) {
						final BlockPos targetLink = transformation.apply(x, y, z);
						byteLink[0] = (byte) (blockPosNew.getX() - targetLink.getX());
						byteLink[1] = (byte) (blockPosNew.getY() - targetLink.getY());
						byteLink[2] = (byte) (blockPosNew.getZ() - targetLink.getZ());
						tagListNewLinkedCrystals.add(listLinkedCrystal);
					} else {// (outside ship)
						// remove the link
						byteLink[0] = (byte) 0;
						byteLink[1] = (byte) 0;
						byteLink[2] = (byte) 0;
					}
				}
				nbtTileEntity.put("LinkedCrystals", tagListNewLinkedCrystals);
			}
		}
		
		// EnergyStorageCore
		if (tagCompoundBCManagedData.getBoolean("stabilizersOK")) {
			for (int index = 0; index < 4; index++) {
				final String tagName = String.format("stabOffset%d", index);
				final ListNBT tagListOffset = tagCompoundBCManagedData.getList(tagName, NBT.TAG_INT);
				final int x = blockPosOld.getX() - tagListOffset.getIntAt(0);
				final int y = blockPosOld.getY() - tagListOffset.getIntAt(1);
				final int z = blockPosOld.getZ() - tagListOffset.getIntAt(2);
				if (transformation.isInside(x, y, z)) {
					final BlockPos targetStabilizer = transformation.apply(x, y, z);
					tagListOffset.set(0, new IntNBT(blockPosNew.getX() - targetStabilizer.getX()));
					tagListOffset.set(1, new IntNBT(blockPosNew.getY() - targetStabilizer.getY()));
					tagListOffset.set(2, new IntNBT(blockPosNew.getZ() - targetStabilizer.getZ()));
				} else {// (outside ship)
					// remove the link
					tagListOffset.set(0, new IntNBT(0));
					tagListOffset.set(1, new IntNBT(0));
					tagListOffset.set(2, new IntNBT(0));
				}
			}
		}
		
		// EnergyPylon, InvisECoreBlock, ParticleGenerator, ReactorComponent
		if (tagCompoundBCManagedData.contains("coreOffset")) {
			final ListNBT tagListOffset = tagCompoundBCManagedData.getList("coreOffset", NBT.TAG_INT);
			if ( tagCompoundBCManagedData.getBoolean("structureValid")
			  || classBlockInvisECoreBlock.isInstance(blockState.getBlock())
			  || tagCompoundBCManagedData.getBoolean("hasCoreLock")
			  || tagCompoundBCManagedData.getBoolean("isBound") ) {
				final int x = blockPosOld.getX() - tagListOffset.getIntAt(0);
				final int y = blockPosOld.getY() - tagListOffset.getIntAt(1);
				final int z = blockPosOld.getZ() - tagListOffset.getIntAt(2);
				if (transformation.isInside(x, y, z)) {
					final BlockPos targetStabilizer = transformation.apply(x, y, z);
					tagListOffset.set(0, new IntNBT(blockPosNew.getX() - targetStabilizer.getX()));
					tagListOffset.set(1, new IntNBT(blockPosNew.getY() - targetStabilizer.getY()));
					tagListOffset.set(2, new IntNBT(blockPosNew.getZ() - targetStabilizer.getZ()));
				} else {// (outside ship)
					// remove the link
					tagListOffset.set(0, new IntNBT(0));
					tagListOffset.set(1, new IntNBT(0));
					tagListOffset.set(2, new IntNBT(0));
				}
			} else {// (not bound or invalid)
				// remove the link
				tagListOffset.set(0, new IntNBT(0));
				tagListOffset.set(1, new IntNBT(0));
				tagListOffset.set(2, new IntNBT(0));
			}
		}
		
		// ReactorCore
		if (tagCompoundBCManagedData.contains("componentPosition0")) {
			final HashMap<String, ListNBT> mapNewPosition = new HashMap<>(6);
			
			for (int facing = 0; facing < 6; facing++) {
				// rotate the key name
				final String tagOldName = String.format("componentPosition%d", facing);
				final String tagNewName;
				switch (rotationSteps) {
				case 1:
					tagNewName = String.format("componentPosition%d", intFacing[facing]);
					break;
				case 2:
					tagNewName = String.format("componentPosition%d", intFacing[intFacing[facing]]);
					break;
				case 3:
					tagNewName = String.format("componentPosition%d", intFacing[intFacing[intFacing[facing]]]);
					break;
				default:
					tagNewName = tagOldName;
					break;
				}
				
				// get current offset
				final ListNBT tagListOffset = tagCompoundBCManagedData.getList(tagOldName, NBT.TAG_INT);
				
				// transform as needed
				if ( tagListOffset.getIntAt(0) != 0
				  || tagListOffset.getIntAt(1) != 0
				  || tagListOffset.getIntAt(2) != 0 ) {
					final int x = blockPosOld.getX() - tagListOffset.getIntAt(0);
					final int y = blockPosOld.getY() - tagListOffset.getIntAt(1);
					final int z = blockPosOld.getZ() - tagListOffset.getIntAt(2);
					if (transformation.isInside(x, y, z)) {
						final BlockPos targetComponent = transformation.apply(x, y, z);
						tagListOffset.set(0, new IntNBT(blockPosNew.getX() - targetComponent.getX()));
						tagListOffset.set(1, new IntNBT(blockPosNew.getY() - targetComponent.getY()));
						tagListOffset.set(2, new IntNBT(blockPosNew.getZ() - targetComponent.getZ()));
					} else {// (outside ship)
						// remove the link
						tagListOffset.set(0, new IntNBT(0));
						tagListOffset.set(1, new IntNBT(0));
						tagListOffset.set(2, new IntNBT(0));
					}
				}
				
				// save the new value
				mapNewPosition.put(tagNewName, tagListOffset);
				tagCompoundBCManagedData.remove(tagOldName);
			}
			
			// apply the new position
			for (final Entry<String, ListNBT> entry : mapNewPosition.entrySet()) {
				tagCompoundBCManagedData.put(entry.getKey(), entry.getValue());
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
