package cr0s.warpdrive.compat;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatBotania implements IBlockTransformer {
	
	private static Class<?> classBlockMod;
	private static Class<?> classBlockAvatar;
	private static Class<?> classBlockFelPumpkin;
	private static Class<?> classBlockSpecialFlower;
	private static Class<?> classBlockRedString;
	private static Class<?> classBlockTinyPotato;
	
	public static void register() {
		try {
			classBlockMod           = Class.forName("vazkii.botania.common.block.BlockMod");
			classBlockAvatar        = Class.forName("vazkii.botania.common.block.BlockAvatar");
			classBlockFelPumpkin    = Class.forName("vazkii.botania.common.block.BlockFelPumpkin");
			classBlockSpecialFlower = Class.forName("vazkii.botania.common.block.BlockSpecialFlower");
			classBlockRedString     = Class.forName("vazkii.botania.common.block.string.BlockRedString");
			classBlockTinyPotato    = Class.forName("vazkii.botania.common.block.decor.BlockTinyPotato");
			
			WarpDriveConfig.registerBlockTransformer("botania", new CompatBotania());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockMod.isInstance(blockState.getBlock())
			|| classBlockSpecialFlower.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                             final Block block, final int blockMeta, final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		// nothing to do
	}
	
	// -----------------------------------------    {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   mrotFacing       = { 0, 1, 5, 4, 2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   mrotFelPumpkin   = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		if ( classBlockAvatar.isInstance(blockState.getBlock())
		  || classBlockRedString.isInstance(blockState.getBlock()) ) {
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
		if ( classBlockFelPumpkin.isInstance(blockState.getBlock())
		  || classBlockTinyPotato.isInstance(blockState.getBlock()) ) {// 0 1 2 3
			switch (rotationSteps) {
			case 1:
				return mrotFelPumpkin[metadata];
			case 2:
				return mrotFelPumpkin[mrotFelPumpkin[metadata]];
			case 3:
				return mrotFelPumpkin[mrotFelPumpkin[mrotFelPumpkin[metadata]]];
			default:
				return blockState;
			}
		}
		
		if ( nbtTileEntity != null
		  && nbtTileEntity.contains("bindX")
		  && nbtTileEntity.contains("bindY") 
		  && nbtTileEntity.contains("bindZ") ) {
			final BlockPos targetBind = transformation.apply(nbtTileEntity.getInt("bindX"), nbtTileEntity.getInt("bindY"), nbtTileEntity.getInt("bindZ"));
			nbtTileEntity.putInt("bindX", targetBind.getX());
			nbtTileEntity.putInt("bindY", targetBind.getY());
			nbtTileEntity.putInt("bindZ", targetBind.getZ());
		}
		
		if ( nbtTileEntity != null
		  && nbtTileEntity.contains("subTileCmp") ) {
			final CompoundNBT nbtSubTileCmp = nbtTileEntity.getCompound("subTileCmp");
			if ( nbtSubTileCmp.contains("collectorX")
			  && nbtSubTileCmp.contains("collectorY")
			  && nbtSubTileCmp.contains("collectorZ") ) {
				final BlockPos targetCollector = transformation.apply(nbtSubTileCmp.getInt("collectorX"), nbtSubTileCmp.getInt("collectorY"), nbtSubTileCmp.getInt("collectorZ"));
				nbtSubTileCmp.putInt("collectorX", targetCollector.getX());
				nbtSubTileCmp.putInt("collectorY", targetCollector.getY());
				nbtSubTileCmp.putInt("collectorZ", targetCollector.getZ());
			}
		}
		
		if ( nbtTileEntity != null
		  && nbtTileEntity.contains("rotationX") ) {
			final float rotationX = nbtTileEntity.getInt("rotationX");
			nbtTileEntity.putFloat("rotationX", (rotationX + 270.0F * rotationSteps) % 360.0F);
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
