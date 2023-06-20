package cr0s.warpdrive.compat;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatJSG implements IBlockTransformer {
	
	private static Class<?> classStargateAbstractBaseTile;
	private static Class<?> classStargateAbstractMemberTile;
	private static Class<?> classDHDAbstractTile;
	private static Class<?> classDHDAbstractBlock;
	private static Class<?> classStargateClassicBaseTile;
	private static Class<?> classStargateAbstractBaseBlock;
	private static Class<?> classStargateAbstractMemberBlock;
	private static Method methodStargateClassicBaseTile_sgStateDescription;
	
	public static void register() {
		try {
			classStargateAbstractBaseTile = Class.forName("tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile");
			classStargateAbstractMemberTile = Class.forName("tauri.dev.jsg.tileentity.stargate.StargateAbstractMemberTile");
			classDHDAbstractTile = Class.forName("tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile");
			classDHDAbstractBlock = Class.forName("tauri.dev.jsg.block.dialhomedevice.DHDAbstractBlock");
			classStargateClassicBaseTile = Class.forName("tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile");
			classStargateAbstractBaseBlock = Class.forName("tauri.dev.jsg.block.stargate.StargateAbstractBaseBlock");
			classStargateAbstractMemberBlock = Class.forName("tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock");
			methodStargateClassicBaseTile_sgStateDescription = classStargateAbstractBaseTile.getMethod("getStargateState");
			WarpDriveConfig.registerBlockTransformer("jsg", new CompatJSG());
		} catch(final ClassNotFoundException | NoSuchMethodException | SecurityException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final Block block, final int metadata, final TileEntity tileEntity) {
		if (classStargateAbstractMemberTile.isInstance(tileEntity)
	     || classDHDAbstractTile.isInstance(tileEntity)
		 || classStargateAbstractBaseTile.isInstance(tileEntity)
		 || classDHDAbstractBlock.isInstance(block)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isJumpReady(final Block block, final int metadata, final TileEntity tileEntity, final WarpDriveText reason) {
		if (classStargateClassicBaseTile.isInstance(tileEntity)) {
			try {
				final Object object = methodStargateClassicBaseTile_sgStateDescription.invoke(tileEntity);
				final String state = object.toString();
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
	public NBTBase saveExternals(final World world, final int x, final int y, final int z, final Block block, final int blockMeta, final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final Block block, final int blockMeta, final TileEntity tileEntity) {
		// nothing to do
	}
	
	//                                                          0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] BaseRotation               = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	//private static final byte[] DHDRotation 			   = {  3,  0,  1,  2,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };

	@Override
	public int rotate(final Block block, final int metadata, final NBTTagCompound nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();

		// Translate position of the linked base for member blocks
		if (nbtTileEntity.hasKey("basePos")) {
			//Convert the long to a BlockPos then apply the transformation
			final BlockPos basePos = transformation.apply(BlockPos.fromLong(nbtTileEntity.getLong("basePos")));
			nbtTileEntity.setLong("basePos", basePos.toLong());
		}

		// Rotation for base block

		if (classStargateAbstractBaseBlock.isInstance(block)
		 || classStargateAbstractMemberBlock.isInstance(block)) {
			switch (rotationSteps) {
			case 1:
				return BaseRotation[metadata];
			case 2:
				return BaseRotation[BaseRotation[metadata]];
			case 3:
				return BaseRotation[BaseRotation[BaseRotation[metadata]]];
			default:
				return metadata;
			}
		}
		
		// Rotation for DHD block. The DHD uses a non-standard 16 step rotation system with a custom block property which is why this code is so messy

		//get the rotation property
		final PropertyInteger propertyRotation = PropertyInteger.create("rotation", 0, 15);
		//get the blockstate (from metadata so lem doesn't get mad)
		final IBlockState blockState = block.getStateFromMeta(metadata);
		//check if the block has the rotation property
		if (blockState.getProperties().containsKey(propertyRotation)) {
			//get the rotation value
			final int DHDRotation = blockState.getValue(propertyRotation);
			switch (rotationSteps) {
			case 1: //Wrap around if rotation is greater than 15
				if (DHDRotation+4 > 15) {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation-12));
				} else {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation+4));
				}
			case 2:
				if (DHDRotation+8 > 15) {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation-8));
				} else {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation+8));
				}
			case 3:
				if (DHDRotation+12 > 15) {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation-4));
				} else {
					return block.getMetaFromState(blockState.withProperty(propertyRotation, DHDRotation+12));
				}
			default:
				break;
			}
		}
		return metadata;
	}
	
	@Override
	public void restoreExternals(final World world, final BlockPos blockPos,
	                             final IBlockState blockState, final TileEntity tileEntity,
	                             final ITransformation transformation, final NBTBase nbtBase) {
		// nothing to do
	}
}
