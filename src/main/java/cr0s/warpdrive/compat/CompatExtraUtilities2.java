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

public class CompatExtraUtilities2 implements IBlockTransformer {
	
	// horizontal facing
	private static Class<?> classBlockIndexer;
	private static Class<?> classBlockMachine;
	private static Class<?> classBlockPlayerChest;
	private static Class<?> classBlockScreen;
	private static Class<?> classBlockWardChunkLoader;
	private static Class<?> classBlockXUBlockFull;
	private static Class<?> classBlockXUBlockStaticRotation;
	
	// vanilla facing
	private static Class<?> classBlockAdvInteractor;
	private static Class<?> classBlockOneWay;
	private static Class<?> classBlockPowerTransmitter;
	private static Class<?> classBlockQuarryProxy;
	private static Class<?> classBlockSpike;
	private static Class<?> classBlockSpotlight;
	
	// tile entity facing
	private static Class<?> classBlockTransferHolder;
	
	public static void register() {
		try {
			// horizontal facing
			classBlockIndexer = Class.forName("com.rwtema.extrautils2.transfernodes.BlockIndexer");
			classBlockMachine = Class.forName("com.rwtema.extrautils2.machine.BlockMachine");
			classBlockPlayerChest = Class.forName("com.rwtema.extrautils2.blocks.BlockPlayerChest");
			classBlockScreen = Class.forName("com.rwtema.extrautils2.blocks.BlockScreen");
			classBlockWardChunkLoader = Class.forName("com.rwtema.extrautils2.blocks.BlockWardChunkLoader");
			classBlockXUBlockFull = Class.forName("com.rwtema.extrautils2.backend.XUBlockFull");
			classBlockXUBlockStaticRotation = Class.forName("com.rwtema.extrautils2.backend.XUBlockStaticRotation");
			
			// vanilla facing
			classBlockAdvInteractor = Class.forName("com.rwtema.extrautils2.blocks.BlockAdvInteractor");
			classBlockOneWay = Class.forName("com.rwtema.extrautils2.blocks.BlockOneWay");
			classBlockPowerTransmitter = Class.forName("com.rwtema.extrautils2.power.energy.BlockPowerTransmitter");
			classBlockQuarryProxy = Class.forName("com.rwtema.extrautils2.quarry.BlockQuarryProxy");
			classBlockSpike = Class.forName("com.rwtema.extrautils2.blocks.BlockSpike");
			classBlockSpotlight = Class.forName("com.rwtema.extrautils2.blocks.BlockSpotlight");
			
			// tile entity facing
			classBlockTransferHolder = Class.forName("com.rwtema.extrautils2.transfernodes.BlockTransferHolder");
			
			WarpDriveConfig.registerBlockTransformer("ExtraUtilities2", new CompatExtraUtilities2());
		} catch(final ClassNotFoundException | RuntimeException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return classBlockIndexer.isInstance(blockState.getBlock())
		    || classBlockMachine.isInstance(blockState.getBlock())
		    || classBlockPlayerChest.isInstance(blockState.getBlock())
		    || classBlockScreen.isInstance(blockState.getBlock())
		    || classBlockWardChunkLoader.isInstance(blockState.getBlock())
		    || classBlockXUBlockFull.isInstance(blockState.getBlock())
		    || classBlockXUBlockStaticRotation.isInstance(blockState.getBlock())
		    
		    || classBlockQuarryProxy.isInstance(blockState.getBlock())
		    || classBlockOneWay.isInstance(blockState.getBlock())
		    || classBlockPowerTransmitter.isInstance(blockState.getBlock())
		    || classBlockAdvInteractor.isInstance(blockState.getBlock())
		    || classBlockSpotlight.isInstance(blockState.getBlock())
		    || classBlockSpike.isInstance(blockState.getBlock())
				
		    || classBlockTransferHolder.isInstance(blockState.getBlock());
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
	private static final byte[] rotFacingHorizontal = {  3,  2,  0,  1,  7,  6,  4,  5, 11, 10,  8,  9, 15, 14, 12, 13 };
	private static final Map<String, String> rotPipeTagName;
	static {
		final Map<String, String> map = new HashMap<>();
		map.put("Grocket_2", "Grocket_5");
		map.put("Grocket_5", "Grocket_3");
		map.put("Grocket_3", "Grocket_4");
		map.put("Grocket_4", "Grocket_2");
		map.put("Type_2", "Type_5");
		map.put("Type_5", "Type_3");
		map.put("Type_3", "Type_4");
		map.put("Type_4", "Type_2");
		rotPipeTagName = Collections.unmodifiableMap(map);
	}
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return blockState;
		}
		
		// horizontal facing: 0 3 1 2 / 4 7 5 6 / 8 11 9 10 / 12 15 13 14
		if ( classBlockIndexer.isInstance(blockState.getBlock())
		  || classBlockMachine.isInstance(blockState.getBlock())
		  || classBlockPlayerChest.isInstance(blockState.getBlock())
		  || classBlockScreen.isInstance(blockState.getBlock())
		  || classBlockWardChunkLoader.isInstance(blockState.getBlock())
		  || classBlockXUBlockFull.isInstance(blockState.getBlock())
		  || classBlockXUBlockStaticRotation.isInstance(blockState.getBlock()) ) {
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
		
		// vanilla facing
		if ( classBlockQuarryProxy.isInstance(blockState.getBlock())
		  || classBlockOneWay.isInstance(blockState.getBlock())
		  || classBlockPowerTransmitter.isInstance(blockState.getBlock())
		  || classBlockAdvInteractor.isInstance(blockState.getBlock())
		  || classBlockSpotlight.isInstance(blockState.getBlock())
		  || classBlockSpike.isInstance(blockState.getBlock()) ) {
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
		
		// pipe without attachments: 64 blockstates over 4 block ids to define blocked directions
		// @TODO support EU2 restricted/blocked pipes
		
		// Grocket (pipe attachments)
		if (classBlockTransferHolder.isInstance(blockState.getBlock())) {
			final Map<String, INBT> map = new HashMap<>();
			for (final String key : rotPipeTagName.keySet()) {
				if (nbtTileEntity.contains(key)) {
					final INBT tagBase = nbtTileEntity.get(key);
					switch (rotationSteps) {
					case 1:
						map.put(rotPipeTagName.get(key), tagBase);
						break;
					case 2:
						map.put(rotPipeTagName.get(rotPipeTagName.get(key)), tagBase);
						break;
					case 3:
						map.put(rotPipeTagName.get(rotPipeTagName.get(rotPipeTagName.get(key))), tagBase);
						break;
					default:
						map.put(key, tagBase);
						break;
					}
					nbtTileEntity.remove(key);
				}
			}
			if (!map.isEmpty()) {
				for (final Entry<String, INBT> entry : map.entrySet()) {
					nbtTileEntity.put(entry.getKey(), entry.getValue());
				}
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
