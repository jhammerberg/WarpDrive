package cr0s.warpdrive.compat;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatIndustrialForegoing implements IBlockTransformer {
	
	private static Class<?> classAxisAlignedBlock;
	private static Class<?> classBlockConveyor;
	private static Class<?> classBlockLabel;
	
	public static void register() {
		try {
			classAxisAlignedBlock = Class.forName("net.ndrei.teslacorelib.blocks.AxisAlignedBlock");
			classBlockConveyor = Class.forName("com.buuz135.industrial.proxy.block.BlockConveyor");
			classBlockLabel = Class.forName("com.buuz135.industrial.proxy.block.BlockLabel");
			
			WarpDriveConfig.registerBlockTransformer("IndustrialForegoing", new CompatIndustrialForegoing());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classAxisAlignedBlock.isInstance(blockState.getBlock())
		    || classBlockConveyor.isInstance(blockState.getBlock())
		    || classBlockLabel.isInstance(blockState.getBlock());
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
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] rotFacing           = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	private static final Map<String, String> rotFacingNames;
	static {
		final Map<String, String> map = new HashMap<>();
		map.put("north", "east");
		map.put("east", "south");
		map.put("south", "west");
		map.put("west", "north");
		rotFacingNames = Collections.unmodifiableMap(map);
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		if (classBlockConveyor.isInstance(blockState.getBlock())) {
			// facing property
			if (nbtTileEntity.contains("Facing")) {
				final String facing = nbtTileEntity.getString("Facing");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putString("Facing", rotFacingNames.get(facing));
					break;
				case 2:
					nbtTileEntity.putString("Facing", rotFacingNames.get(rotFacingNames.get(facing)));
					break;
				case 3:
					nbtTileEntity.putString("Facing", rotFacingNames.get(rotFacingNames.get(rotFacingNames.get(facing))));
					break;
				default:
					break;
				}
			}
			
			// upgrades
			if (nbtTileEntity.contains("Upgrades")) {
				final CompoundNBT tagCompoundUpgrades = nbtTileEntity.getCompound("Upgrades");
				final Map<String, INBT> map = new HashMap<>();
				for (final String key : rotFacingNames.keySet()) {
					if (tagCompoundUpgrades.contains(key)) {
						final INBT tagBase = tagCompoundUpgrades.get(key);
						switch (rotationSteps) {
						case 1:
							map.put(rotFacingNames.get(key), tagBase);
							break;
						case 2:
							map.put(rotFacingNames.get(rotFacingNames.get(key)), tagBase);
							break;
						case 3:
							map.put(rotFacingNames.get(rotFacingNames.get(rotFacingNames.get(key))), tagBase);
							break;
						default:
							map.put(key, tagBase);
							break;
						}
						tagCompoundUpgrades.remove(key);
					}
				}
				if (!map.isEmpty()) {
					for (final Entry<String, INBT> entry : map.entrySet()) {
						tagCompoundUpgrades.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		
		// vanilla facing
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
