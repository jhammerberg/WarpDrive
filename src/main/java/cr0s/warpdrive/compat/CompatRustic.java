package cr0s.warpdrive.compat;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.config.WarpDriveConfig;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompatRustic implements IBlockTransformer {
	
	private static Class<?> classBlockApiary;
	private static Class<?> classBlockBrewingBarrel;
	private static Class<?> classBlockCabinet;
	private static Class<?> classBlockCandle;
	private static Class<?> classBlockChair;
	private static Class<?> classBlockClayWallDiag;
	private static Class<?> classBlockCondenser;
	private static Class<?> classBlockCondenserAdvanced;
	private static Class<?> classBlockGargoyle;
	private static Class<?> classBlockLantern;
	private static Class<?> classBlockRetort;
	private static Class<?> classBlockRopeBase;
	private static Class<?> classBlockStakeTied;
	
	private static HashSet<Block> setBlockRope;
	
	public static void register() {
		try {
			classBlockApiary = Class.forName("rustic.common.blocks.BlockApiary");
			classBlockBrewingBarrel = Class.forName("rustic.common.blocks.BlockBrewingBarrel");
			classBlockCabinet = Class.forName("rustic.common.blocks.BlockCabinet");
			classBlockCandle = Class.forName("rustic.common.blocks.BlockCandle");
			classBlockChair = Class.forName("rustic.common.blocks.BlockChair");
			classBlockClayWallDiag = Class.forName("rustic.common.blocks.BlockClayWallDiag");
			classBlockCondenser = Class.forName("rustic.common.blocks.BlockCondenser");
			classBlockCondenserAdvanced = Class.forName("rustic.common.blocks.BlockCondenserAdvanced");
			classBlockGargoyle = Class.forName("rustic.common.blocks.BlockGargoyle");
			classBlockLantern = Class.forName("rustic.common.blocks.BlockLantern");
			classBlockRetort = Class.forName("rustic.common.blocks.BlockRetort");
			classBlockRopeBase = Class.forName("rustic.common.blocks.BlockRopeBase"); // rope and chain
			classBlockStakeTied = Class.forName("rustic.common.blocks.crops.BlockStakeTied");
			
			final Block blockRope = IBlockTransformer.getBlockOrThrowException("rustic:rope");
			setBlockRope = new HashSet<>(1);
			setBlockRope.add(blockRope);
			
			WarpDriveConfig.registerBlockTransformer("Rustic", new CompatRustic());
		} catch(final ClassNotFoundException | RuntimeException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockApiary.isInstance(blockState.getBlock())
		    || classBlockBrewingBarrel.isInstance(blockState.getBlock())
		    || classBlockCabinet.isInstance(blockState.getBlock())
		    || classBlockCandle.isInstance(blockState.getBlock())
		    || classBlockChair.isInstance(blockState.getBlock())
		    || classBlockClayWallDiag.isInstance(blockState.getBlock())
		    || classBlockCondenser.isInstance(blockState.getBlock())
		    || classBlockCondenserAdvanced.isInstance(blockState.getBlock())
		    || classBlockGargoyle.isInstance(blockState.getBlock())
		    || classBlockLantern.isInstance(blockState.getBlock())
		    || classBlockRetort.isInstance(blockState.getBlock())
		    || classBlockRopeBase.isInstance(blockState.getBlock())
		    || classBlockStakeTied.isInstance(blockState.getBlock());
	}
	
	@Override
	public boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason) {
		return true;
	}
	
	@Override
	public INBT saveExternals(final World world, final int x, final int y, final int z,
	                          final BlockState blockState, final TileEntity tileEntity) {
		if (classBlockStakeTied.isInstance(blockState.getBlock())) {
			return StringNBT.valueOf("stake_tied");
		}
		return null;
	}
	
	@Override
	public void removeExternals(final World world, final int x, final int y, final int z,
	                            final BlockState blockState, final TileEntity tileEntity) {
		if (classBlockStakeTied.isInstance(blockState.getBlock())) {// @TODO Rustic mod is forcing drops here, not sure how to work around it without ASM => anchor block
			final BlockPos blockPos = new BlockPos(x, y, z);
			// get all horizontal ropes
			final Set<BlockPos> setBlockPosHorizontalRopes = Commons.getConnectedBlocks(world, blockPos, Commons.DIRECTIONS_HORIZONTAL, setBlockRope, 16);
			for (final BlockPos blockPosHorizontalRope : setBlockPosHorizontalRopes) {
				// get all vertical/hanging ropes
				final Set<BlockPos> setBlockPosVerticalRopes = Commons.getConnectedBlocks(world, blockPosHorizontalRope, Commons.DIRECTIONS_VERTICAL, setBlockRope, 16);
				for (final BlockPos blockPosVerticalRope : setBlockPosVerticalRopes) {
					final boolean isDone = world.removeBlock(blockPosVerticalRope, false);
					if (!isDone) {
						WarpDrive.logger.error(String.format("Failed to remove hanging rope at %s",
						                                     blockPosVerticalRope));
					}
				}
				final boolean isDone = world.removeBlock(blockPosHorizontalRope, false);
				if (!isDone) {
					WarpDrive.logger.error(String.format("Failed to remove tension rope at %s",
					                                     blockPosHorizontalRope));
				}
			}
			final boolean isDone = world.removeBlock(blockPos, false);
			if (!isDone) {
				WarpDrive.logger.error(String.format("Failed to remove tied rope at %s",
				                                     blockPos));
			}
		}
	}
	
	//                                                   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15
	private static final byte[] rotCabinet          = {  2,  3,  1,  0,  6,  7,  5,  4, 10, 11,  9,  8, 14, 15, 13, 12 };
	private static final byte[] rotCandle           = {  0,  3,  4,  2,  1,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] rotCondenser        = {  2,  3,  1,  0,  6,  7,  5,  4,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] rotFacing           = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] rotFacingHorizontal = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] rotRetort           = {  2,  3,  1,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] rotRopeBase         = {  0,  2,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// horizontal facing: 0 1 2 3
		if ( classBlockApiary.isInstance(blockState.getBlock())
		  || classBlockBrewingBarrel.isInstance(blockState.getBlock())
		  || classBlockChair.isInstance(blockState.getBlock())
		  || classBlockClayWallDiag.isInstance(blockState.getBlock())
		  || classBlockGargoyle.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return rotFacingHorizontal[metadata];
			case 2:
				return rotFacingHorizontal[rotFacingHorizontal[metadata]];
			case 3:
				return rotFacingHorizontal[rotFacingHorizontal[rotFacingHorizontal[metadata]]];
			default:
				return blockState;
			}
		}
		
		// cabinet facing: 0x3 horizontal rotation, 0x4 top, 0x8 mirror
		if (classBlockCabinet.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotCabinet[metadata];
			case 2:
				return rotCabinet[rotCabinet[metadata]];
			case 3:
				return rotCabinet[rotCabinet[rotCabinet[metadata]]];
			default:
				return blockState;
			}
		}
		
		// candle facing is hardcoded
		if (classBlockCandle.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotCandle[metadata];
			case 2:
				return rotCandle[rotCandle[metadata]];
			case 3:
				return rotCandle[rotCandle[rotCandle[metadata]]];
			default:
				return blockState;
			}
		}
		
		// Condenser rotation: 0x3 is 5 - vanilla facing, 0x4 is bottom, 0x8 is 0
		if ( classBlockCondenser.isInstance(blockState.getBlock())
		  || classBlockCondenserAdvanced.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return rotCondenser[metadata];
			case 2:
				return rotCondenser[rotCondenser[metadata]];
			case 3:
				return rotCondenser[rotCondenser[rotCondenser[metadata]]];
			default:
				return blockState;
			}
		}
		
		// vanilla facing
		if (classBlockLantern.isInstance(blockState.getBlock())) {
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
		
		// retort facing: 0x3 is 5 - vanilla facing
		if (classBlockRetort.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotRetort[metadata];
			case 2:
				return rotRetort[rotRetort[metadata]];
			case 3:
				return rotRetort[rotRetort[rotRetort[metadata]]];
			default:
				return blockState;
			}
		}
		
		// rope & chain facing: 0x3 is Y, X or Z axis
		if (classBlockRopeBase.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return rotRopeBase[metadata];
			case 2:
				return rotRopeBase[rotRopeBase[metadata]];
			case 3:
				return rotRopeBase[rotRopeBase[rotRopeBase[metadata]]];
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
