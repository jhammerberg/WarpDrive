package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatMekanism implements IBlockTransformer {
	
	private static Class<?> tileEntityBasicBlock;
	private static Class<?> tileEntityBoundingBlock;
	private static Class<?> tileEntityGlowPanel;
	private static Class<?> tileEntitySidedPipe;
	
	public static void register() {
		try {
			tileEntityBasicBlock = Class.forName("mekanism.common.tile.prefab.TileEntityBasicBlock");
			tileEntityBoundingBlock = Class.forName("mekanism.common.tile.TileEntityBoundingBlock");
			// (not needed: mekanism.common.tile.TileEntityCardboardBox)
			tileEntityGlowPanel = Class.forName("mekanism.common.tile.TileEntityGlowPanel");
			tileEntitySidedPipe = Class.forName("mekanism.common.tile.transmitter.TileEntitySidedPipe");
			
			WarpDriveConfig.registerBlockTransformer("Mekanism", new CompatMekanism());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return tileEntityBasicBlock.isInstance(tileEntity)
		    || tileEntityBoundingBlock.isInstance(tileEntity)
		    || tileEntityGlowPanel.isInstance(tileEntity)
		    || tileEntitySidedPipe.isInstance(tileEntity);
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
	
	private static final int[] rotFacing           = {  0,  1,  5,  4,  2,  3 };
	
	private static final Map<String, String> rotConnectionNames;
	static {
		final Map<String, String> map = new HashMap<>();
		map.put("connection2", "connection5");
		map.put("connection5", "connection3");
		map.put("connection3", "connection4");
		map.put("connection4", "connection2");
		rotConnectionNames = Collections.unmodifiableMap(map);
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// basic blocks
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
		
		// glowstone panels
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
		
		// sided pipes, including duct/pipe/cable/etc.
		final HashMap<String, INBT> mapRotated = new HashMap<>(rotConnectionNames.size());
		for (final String key : rotConnectionNames.keySet()) {
			if (nbtTileEntity.contains(key)) {
				final INBT nbtBase = nbtTileEntity.get(key);
				nbtTileEntity.remove(key);
				switch (rotationSteps) {
				case 1:
					mapRotated.put(rotConnectionNames.get(key), nbtBase);
					break;
				case 2:
					mapRotated.put(rotConnectionNames.get(rotConnectionNames.get(key)), nbtBase);
					break;
				case 3:
					mapRotated.put(rotConnectionNames.get(rotConnectionNames.get(rotConnectionNames.get(key))), nbtBase);
					break;
				default:
					mapRotated.put(key, nbtBase);
					break;
				}
			}
		}
		for (final Map.Entry<String, INBT> entry : mapRotated.entrySet()) {
			nbtTileEntity.put(entry.getKey(), entry.getValue());
		}
		
		// bounding blocks
		if ( nbtTileEntity.contains("mainX")
		  && nbtTileEntity.contains("mainY")
		  && nbtTileEntity.contains("mainZ") ) {
			final BlockPos mainTarget = transformation.apply(nbtTileEntity.getInt("mainX"), nbtTileEntity.getInt("mainY"), nbtTileEntity.getInt("mainZ"));
			nbtTileEntity.putInt("mainX", mainTarget.getX());
			nbtTileEntity.putInt("mainY", mainTarget.getY());
			nbtTileEntity.putInt("mainZ", mainTarget.getZ());
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
