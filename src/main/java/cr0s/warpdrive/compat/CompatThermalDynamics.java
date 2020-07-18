package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.Constants;

public class CompatThermalDynamics implements IBlockTransformer {
	
	private static Class<?> classBlockTDBase;
	
	public static void register() {
		try {
			classBlockTDBase = Class.forName("cofh.thermaldynamics.block.BlockTDBase");
			
			WarpDriveConfig.registerBlockTransformer("ThermalDynamics", new CompatThermalDynamics());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockTDBase.isInstance(blockState.getBlock());
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
	
	//                                              0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final int[] rotSide         = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	private void rotateComponent(final CompoundNBT nbtTileEntity, final byte rotationSteps, final String nameComponents) {
		if (nbtTileEntity.contains(nameComponents)) {
			final ListNBT nbtOldComponents = nbtTileEntity.getList(nameComponents, Constants.NBT.TAG_COMPOUND);
			final ListNBT nbtNewComponents = new ListNBT();
			for (int index = 0; index < nbtOldComponents.size(); index++) {
				final CompoundNBT nbtOldComponent = nbtOldComponents.getCompound(index);
				final CompoundNBT nbtNewComponent = nbtOldComponent.copy();
				final int side = nbtOldComponent.getInt("side");
				switch (rotationSteps) {
				case 1:
					nbtNewComponent.putInt("side", rotSide[side]);
					break;
				case 2:
					nbtNewComponent.putInt("side", rotSide[rotSide[side]]);
					break;
				case 3:
					nbtNewComponent.putInt("side", rotSide[rotSide[rotSide[side]]]);
					break;
				default:
					// nbtNewComponent.putInt("side", side);
					break;
				}
				nbtNewComponents.add(nbtNewComponent);
			}
			nbtTileEntity.put(nameComponents, nbtNewComponents);
		}
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || nbtTileEntity == null) {
			return blockState;
		}
		
		// Ducts attachments (servos)
		rotateComponent(nbtTileEntity, rotationSteps, "Attachments");
		
		// Ducts covers (facades)
		rotateComponent(nbtTileEntity, rotationSteps, "Covers");
		
		// Ducts connections
		if (nbtTileEntity.contains("Connections")) {
			final byte[] bytesOldConnections = nbtTileEntity.getByteArray("Connections");
			final byte[] bytesNewConnections = bytesOldConnections.clone();
			for (int sideOld = 0; sideOld < 6; sideOld++) {
				final byte byteConnection = bytesOldConnections[sideOld];
				switch (rotationSteps) {
				case 1:
					bytesNewConnections[rotSide[sideOld]] = byteConnection;
					break;
				case 2:
					bytesNewConnections[rotSide[rotSide[sideOld]]] = byteConnection;
					break;
				case 3:
					bytesNewConnections[rotSide[rotSide[rotSide[sideOld]]]] = byteConnection;
					break;
				default:
					// bytesNewConnections[sideOld] = byteConnection;
					break;
				}
			}
			nbtTileEntity.putByteArray("Connections", bytesNewConnections);
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
