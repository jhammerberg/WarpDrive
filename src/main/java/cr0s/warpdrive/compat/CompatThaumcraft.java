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

public class CompatThaumcraft implements IBlockTransformer {
	
	private static Class<?> interfaceIBlockFacing;
	private static Class<?> interfaceIBlockFacingHorizontal;
	
	private static Class<?> classBlockPillar;
	private static Class<?> classBlockChestHungry;
	
	private static Class<?> classBlockBannerTC;
	private static Class<?> classBlockMirror;
	private static Class<?> classBlockAlembic;
	private static Class<?> classBlockJar;
	private static Class<?> classBlockTube;
	
	public static void register() {
		try {
			interfaceIBlockFacing = Class.forName("thaumcraft.common.blocks.IBlockFacing");
			interfaceIBlockFacingHorizontal = Class.forName("thaumcraft.common.blocks.IBlockFacingHorizontal");
			
			classBlockPillar = Class.forName("thaumcraft.common.blocks.basic.BlockPillar");
			classBlockChestHungry = Class.forName("thaumcraft.common.blocks.devices.BlockHungryChest");
			
			classBlockBannerTC = Class.forName("thaumcraft.common.blocks.basic.BlockBannerTC");
			classBlockMirror = Class.forName("thaumcraft.common.blocks.devices.BlockMirror");
			classBlockAlembic = Class.forName("thaumcraft.common.blocks.essentia.BlockAlembic");
			classBlockJar = Class.forName("thaumcraft.common.blocks.essentia.BlockJar");
			classBlockTube = Class.forName("thaumcraft.common.blocks.essentia.BlockTube");
			
			WarpDriveConfig.registerBlockTransformer("thaumcraft", new CompatThaumcraft());
		} catch(final ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final BlockState blockState, final TileEntity tileEntity) {
		return interfaceIBlockFacing.isInstance(blockState.getBlock())
			|| interfaceIBlockFacingHorizontal.isInstance(blockState.getBlock())
		    || classBlockPillar.isInstance(blockState.getBlock())
		    || classBlockChestHungry.isInstance(blockState.getBlock())
		    || classBlockBannerTC.isInstance(blockState.getBlock())
		    || classBlockMirror.isInstance(blockState.getBlock())
		    || classBlockAlembic.isInstance(blockState.getBlock())
		    || classBlockJar.isInstance(blockState.getBlock())
		    || classBlockTube.isInstance(blockState.getBlock());
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
	
	// As of 1.12.2-6.1.BETA24
	// Blocks
	// IBlockFacing                       (metadata) -Direction enum-               thaumcraft.common.blocks.IBlockFacing
	// IBlockFacingHorizontal             (metadata) -Direction enum-               thaumcraft.common.blocks.IBlockFacingHorizontal
	// BlockPillar                        (metadata) -Direction enum-               thaumcraft.common.blocks.basic.BlockPillar
	// BlockHungryChest                   (metadata) -Direction enum-               thaumcraft.common.blocks.devices.BlockHungryChest
	//
	// TileEntities
	// TileAlembic                        facing (byte) -Direction enum-            thaumcraft.common.blocks.essentia.BlockAlembic
	// TileBanner                         facing (byte) -Direction enum-            thaumcraft.common.blocks.basic.BlockBannerTC
	// TileJarFillable                    facing (byte) -Direction enum-            thaumcraft.common.blocks.essentia.BlockJar
	// TileMirror, TileMirrorEssentia     linkX/Y/Z, linkDim (int)                  thaumcraft.common.blocks.devices.BlockMirror
	// TileTube, TileTubeBuffer           side (int) -Direction enum-               thaumcraft.common.blocks.essentia.BlockTube
	//
	// Entities
	// EntityArcaneBore                   faceing (byte)                            thaumcraft.common.entities.construct.EntityArcaneBore
	// SealHarvest ?                      taskface (byte)                           thaumcraft:harvest
	// SealEntity ?                       face (byte)
	//
	// Items
	// ItemHandMirror                     linkX/Y/Z, linkDim (int)                  thaumcraft.common.items.tools.ItemHandMirror
	// BlockMirrorItem                    linkX/Y/Z, linkDim (int)                  thaumcraft.common.blocks.devices.BlockMirrorItem
	
	// As of 1.7.10-x
	// Vanilla supported: stairs
	// Not rotating: arcane workbench, deconstruction table, crystals, candles, crucible, alchemical centrifuge
	
	// Transformation handling required:
	// Tile Hungry chest: (metadata) 2 5 3 4						mrotHungryChest thaumcraft.common.blocks.BlockChestHungry
	// Tile jar: facing (byte) 2 5 3 4								rotForgeByte	thaumcraft.common.blocks.BlockJar
	// Tile vis relay: orientation (short) 0 / 1 / 2 5 3 4			rotForgeShort	thaumcraft.common.blocks.BlockMetalDevice
	// Tile arcane lamp: orientation (int) 2 5 3 4					rotForgeInt		thaumcraft.common.blocks.BlockMetalDevice
	// Tile syphon (Arcane alembic): facing (byte) 2 5 3 4			rotForgeByte	thaumcraft.common.blocks.BlockMetalDevice
	// Tile mirror: (metadata) 0 / 1 / 2 5 3 4 / 6 / 7 / 8 11 9 10	mrotMirror		thaumcraft.common.blocks.BlockMirror
	// Tile mirror: linkX/Y/Z (int)									n/a				thaumcraft.common.blocks.BlockMirror
	// Tile table: (metadata) 0 1 / 2 5 3 4 / 6 9 7 8				mrotTable		thaumcraft.common.blocks.BlockTable
	// Tile tube, Tile tube valve: side (int) 0 / 1 / 2 5 3 4		rotForgeInt		thaumcraft.common.blocks.BlockTube
	// Tile essentia crystalizer: face (byte) 0 / 1 / 2 5 3 4		rotForgeByte	thaumcraft.common.blocks.BlockTube
	// Tile bellows: orientation (byte) 0 / 1 / 2 5 3 4				rotForgeByte	thaumcraft.common.blocks.BlockWoodenDevice
	// Tile arcane bore base: orientation (int) 2 5 3 4				rotForgeInt		thaumcraft.common.blocks.BlockWoodenDevice
	// Tile banner: facing (byte) 0 4 8 12							rotBanner		thaumcraft.common.blocks.BlockWoodenDevice
	
	// -----------------------------------------        {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   mrotFacingEnable     = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 13, 12, 10, 11, 14, 15 };
	private static final int[]   mrotFacingHorizontal = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[]  rotForgeByte         = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final int[]   rotForgeInt          = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[]  rotBanner            = {  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  0,  1,  2,  3 };
	
	@Override
	public BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation) {
		final byte rotationSteps = transformation.getRotationSteps();
		
		if ( interfaceIBlockFacing.isInstance(blockState.getBlock())
		  || interfaceIBlockFacingHorizontal.isInstance(blockState.getBlock())
		  || classBlockChestHungry.isInstance(blockState.getBlock()) ) {
			switch (rotationSteps) {
			case 1:
				return mrotFacingEnable[metadata];
			case 2:
				return mrotFacingEnable[mrotFacingEnable[metadata]];
			case 3:
				return mrotFacingEnable[mrotFacingEnable[mrotFacingEnable[metadata]]];
			default:
				return blockState;
			}
		}
		
		if (classBlockPillar.isInstance(blockState.getBlock())) {
			switch (rotationSteps) {
			case 1:
				return mrotFacingHorizontal[metadata];
			case 2:
				return mrotFacingHorizontal[mrotFacingHorizontal[metadata]];
			case 3:
				return mrotFacingHorizontal[mrotFacingHorizontal[mrotFacingHorizontal[metadata]]];
			default:
				return blockState;
			}
		}
		
		if ( classBlockAlembic.isInstance(blockState.getBlock())
		  || classBlockJar.isInstance(blockState.getBlock()) ) {
			if (nbtTileEntity.contains("facing")) {
				final short facing = nbtTileEntity.getByte("facing");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putByte("facing", rotForgeByte[facing]);
					return blockState;
				case 2:
					nbtTileEntity.putByte("facing", rotForgeByte[rotForgeByte[facing]]);
					return blockState;
				case 3:
					nbtTileEntity.putByte("facing", rotForgeByte[rotForgeByte[rotForgeByte[facing]]]);
					return blockState;
				default:
					return blockState;
				}
			}
		}
		
		if (classBlockBannerTC.isInstance(blockState.getBlock())) {
			if (nbtTileEntity.contains("facing")) {
				final short facing = nbtTileEntity.getByte("facing");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putByte("facing", rotBanner[facing]);
					return blockState;
				case 2:
					nbtTileEntity.putByte("facing", rotBanner[rotBanner[facing]]);
					return blockState;
				case 3:
					nbtTileEntity.putByte("facing", rotBanner[rotBanner[rotBanner[facing]]]);
					return blockState;
				default:
					return blockState;
				}
			}
		}
		
		if (classBlockTube.isInstance(blockState.getBlock())) {
			if (nbtTileEntity.contains("side")) {
				final int side = nbtTileEntity.getInt("side");
				switch (rotationSteps) {
				case 1:
					nbtTileEntity.putInt("side", rotForgeInt[side]);
					return blockState;
				case 2:
					nbtTileEntity.putInt("side", rotForgeInt[rotForgeInt[side]]);
					return blockState;
				case 3:
					nbtTileEntity.putInt("side", rotForgeInt[rotForgeInt[rotForgeInt[side]]]);
					return blockState;
				default:
					return blockState;
				}
			}
		}
		
		if (classBlockMirror.isInstance(blockState.getBlock())) {
			if (nbtTileEntity.contains("linkX") && nbtTileEntity.contains("linkY") && nbtTileEntity.contains("linkZ") && nbtTileEntity.contains("linkDim")) {
				// final int dimensionId = nbtTileEntity.getInt("linkDim");
				final BlockPos targetLink = transformation.apply(nbtTileEntity.getInt("linkX"), nbtTileEntity.getInt("linkY"), nbtTileEntity.getInt("linkZ"));
				nbtTileEntity.putInt("linkX", targetLink.getX());
				nbtTileEntity.putInt("linkY", targetLink.getY());
				nbtTileEntity.putInt("linkZ", targetLink.getZ());
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
