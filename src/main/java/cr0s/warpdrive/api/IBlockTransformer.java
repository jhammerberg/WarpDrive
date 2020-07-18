package cr0s.warpdrive.api;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.registries.ForgeRegistries;

public interface IBlockTransformer {
	// This interface only applies to server side, it won't be used client side.
	
	// Return true if this transformer is applicable to that block.
	boolean isApplicable(final BlockState blockState, final TileEntity tileEntity);
	
	// Called when preparing to save a ship structure.
	// Use this to prevent jump during critical events/animations.
	boolean isJumpReady(final BlockState blockState, final TileEntity tileEntity, final WarpDriveText reason);
	
	// Called when saving a ship structure.
	// Use this to save external data in the ship schematic.
	// You don't need to save Block and TileEntity data here, it's already covered.
	// Warning: do NOT assume that the ship will be removed!
	INBT saveExternals(final World world, final int x, final int y, final int z,
	                   final BlockState blockState, final TileEntity tileEntity);
	
	// Called when removing the original ship structure, if saveExternals() returned non-null for that block.
	// Use this to prevents drops, clear energy networks, etc.
	// Block and TileEntity will be removed right after this call. 
	// When moving, the new ship is placed first.
	void removeExternals(final World world, final int x, final int y, final int z,
	                     final BlockState blockState, final TileEntity tileEntity);
	
	// Called when restoring a ship in the world.
	// Use this to apply metadata & NBT rotation, right before block & tile entity placement.
	// Use priority placement to ensure dependent blocks are placed first.
	// Warning: do NOT place the block or tile entity!
	BlockState rotate(final BlockState blockState, final CompoundNBT nbtTileEntity, final ITransformation transformation);
	
	// Called when placing back a ship in the world, if saveExternals() returned non-null for that block.
	// Use this to restore external data from the ship schematic, right after block & tile entity placement.
	// Use this to send custom client notification messages after the block was placed.
	// Use priority placement to ensure dependent blocks are placed first.
	void restoreExternals(final World world, final BlockPos blockPos,
	                      final BlockState blockState, final TileEntity tileEntity,
	                      final ITransformation transformation, final INBT nbtBase);
	
	// Support method to disable compatibility module on error
	static Block getBlockOrThrowException(final String blockId) {
		final ResourceLocation resourceLocation = new ResourceLocation(blockId);
		final Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
		if (block == Blocks.AIR) {
			throw new RuntimeException(String.format("Invalid %s version, please report to mod author, %s is missing",
			                                         resourceLocation.getNamespace(), blockId));
		}
		return block;
	}
	
	// default rotation using Direction enum properties used in many blocks
	@SuppressWarnings("unchecked")
	static BlockState rotateFirstDirectionProperty(@Nonnull final BlockState blockState, final byte rotationSteps) {
		// find first property using Direction enum
		EnumProperty<Direction> propertyFacing = null;
		for (final IProperty<?> propertyKey : blockState.getProperties()) {
			if ( propertyKey instanceof EnumProperty<?>
			  && propertyKey.getValueClass() == Direction.class ) {
				propertyFacing = (EnumProperty<Direction>) propertyKey;
				break;
			}
		}
		if (propertyFacing != null) {
			final Direction facingOld = blockState.get(propertyFacing);
			// skip vertical facings
			if ( facingOld == Direction.DOWN
			  || facingOld == Direction.UP ) {
				return blockState;
			}
			
			// turn horizontal facings
			final Direction facingNew;
			switch (rotationSteps) {
			case 1:
				facingNew = facingOld.rotateY();
				break;
			case 2:
				facingNew = facingOld.rotateY().rotateY();
				break;
			case 3:
				facingNew = facingOld.rotateY().rotateY().rotateY();
				break;
			default:
				facingNew = facingOld;
				break;
			}
			return blockState.with(propertyFacing, facingNew);
		}
		
		return blockState;
	}
}