package cr0s.warpdrive.world;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenSmallShip extends Feature<NoFeatureConfig> {
	
	private final boolean isCorrupted;
	private final boolean isCreative;
	
	public WorldGenSmallShip(final boolean isCorrupted, final boolean isCreative) {
		super(NoFeatureConfig::deserialize);
		
		this.isCorrupted = isCorrupted;
		this.isCreative = isCreative;
	}
	
	@Override
	public boolean place(@Nonnull final IWorld worldInterface, @Nonnull final ChunkGenerator<? extends GenerationSettings> generator, @Nonnull final Random rand,
	                     @Nonnull final BlockPos blockPos, @Nonnull final NoFeatureConfig config) {
		return place((World) worldInterface, rand, blockPos);
	}
	
	public boolean place(@Nonnull final World world, @Nonnull final Random rand, @Nonnull final BlockPos blockPos) {
		final WorldGenStructure genStructure = new WorldGenStructure(isCorrupted, rand);
		final boolean hasGlassRoof = rand.nextBoolean();
		final boolean hasWings = rand.nextBoolean();
		final int x = blockPos.getX() - 5;
		final int y = blockPos.getY() - 3;
		final int z = blockPos.getZ() - 7;
		
		// Ship is facing West: X- is forward, Z+ is left
		genStructure.setHullPlain(world, x, y + 1, z + 4);
		genStructure.setHullPlain(world, x, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 6);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 8);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 6);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 7);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 6);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 7);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 4);
		genStructure.setFullBlockLight(world, x + 4, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 8);
		genStructure.setFullBlockLight(world, x + 4, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 10);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 4);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 3);
		genStructure.setFullBlockLight(world, x + 5, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 7), Blocks.RED_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 8), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 9);
		genStructure.setFullBlockLight(world, x + 5, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 11);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 3);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 5);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 6), Blocks.RED_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 7), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 8), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 3, z + 2);
		world.setBlockState(new BlockPos(x + 6, y + 3, z + 3), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.WEST), 2);
		genStructure.fillInventoryWithLoot(world, rand, x + 6, y + 3, z + 3, "ship");
		world.setBlockState(new BlockPos(x + 6, y + 3, z + 11), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH), 2);
		genStructure.fillInventoryWithLoot(world, rand, x + 6, y + 3, z + 11, "ship");
		genStructure.setHullPlain(world, x + 6, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 4, z + 2);
		world.setBlockState(new BlockPos(x + 6, y + 4, z + 3), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.WEST), 2);
		genStructure.fillInventoryWithLoot(world, rand, x + 6, y + 4, z + 3, "ship");
		world.setBlockState(new BlockPos(x + 6, y + 4, z + 11), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH), 2);
		genStructure.fillInventoryWithLoot(world, rand, x + 6, y + 4, z + 11, "ship");
		genStructure.setHullPlain(world, x + 6, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 6, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 5);
		if (hasGlassRoof) {
			genStructure.setHullGlass(world, x + 6, y + 7, z + 6);
			genStructure.setHullGlass(world, x + 6, y + 7, z + 7);
			genStructure.setHullGlass(world, x + 6, y + 7, z + 8);
		} else {
			genStructure.setHullPlain(world, x + 6, y + 7, z + 6);
			genStructure.setHullPlain(world, x + 6, y + 7, z + 7);
			genStructure.setHullPlain(world, x + 6, y + 7, z + 8);
		}
		genStructure.setHullPlain(world, x + 6, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 7), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 8), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 9), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 5);
		if (hasGlassRoof) {
			genStructure.setHullGlass(world, x + 7, y + 7, z + 6);
			genStructure.setHullGlass(world, x + 7, y + 7, z + 7);
			genStructure.setHullGlass(world, x + 7, y + 7, z + 8);
		} else {
			genStructure.setHullPlain(world, x + 7, y + 7, z + 6);
			genStructure.setHullPlain(world, x + 7, y + 7, z + 7);
			genStructure.setHullPlain(world, x + 7, y + 7, z + 8);
		}
		genStructure.setHullPlain(world, x + 7, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 5);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 6), Blocks.RED_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 7), Blocks.RED_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 8), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 5);
		if (hasGlassRoof) {
			genStructure.setHullGlass(world, x + 8, y + 7, z + 6);
			genStructure.setHullGlass(world, x + 8, y + 7, z + 7);
			genStructure.setHullGlass(world, x + 8, y + 7, z + 8);
		} else {
			genStructure.setHullPlain(world, x + 8, y + 7, z + 6);
			genStructure.setHullPlain(world, x + 8, y + 7, z + 7);
			genStructure.setHullPlain(world, x + 8, y + 7, z + 8);
		}
		genStructure.setHullPlain(world, x + 8, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 4);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 5), Blocks.RED_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 6), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 7), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 9);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 10), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 3);
		if (!isCorrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 9, y + 6, z + 7), WarpDrive.blockAirGeneratorTiered[1].getDefaultState().with(BlockProperties.FACING, Direction.WEST), 2);
		}
		genStructure.setHullPlain(world, x + 9, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 4);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 5), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 6), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 8);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 9), Blocks.LIGHT_GRAY_WOOL.getDefaultState(), 2);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 10), Blocks.RED_WOOL.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 10, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 10, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 10, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 10, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 6);
		genStructure.setWiring(world, x + 10, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 10, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 7);
		world.setBlockState(new BlockPos(x + 11, y + 3, z + 9), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState(), 2);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 11, y + 4, z + 2);
		genStructure.setComputerFloppy(world, x + 11, y + 4, z + 6);
		genStructure.setComputerScreen(world, x + 11, y + 4, z + 7);
		genStructure.setComputerKeyboard(world, x + 11, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 11, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 11, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 11, y + 5, z + 6);
		genStructure.setComputerCore(world, x + 11, y + 5, z + 7);
		genStructure.setHullGlass(world, x + 11, y + 5, z + 8);
		genStructure.setHullGlass(world, x + 11, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 5);
		genStructure.setWiring(world, x + 11, y + 6, z + 6);
		genStructure.setWiring(world, x + 11, y + 6, z + 7);
		genStructure.setWiring(world, x + 11, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 5);
		genStructure.setSolarPanel(world, x + 11, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 11, y + 7, z + 7);
		genStructure.setSolarPanel(world, x + 11, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 8, z + 10);
		if (hasWings) {
			genStructure.setHullPlain(world, x + 11, y + 8, z + 3);
			genStructure.setHullPlain(world, x + 11, y + 8, z + 11);
		} else {
			genStructure.setHullPlain(world, x + 11, y + 9, z + 4);
			genStructure.setHullPlain(world, x + 11, y + 9, z + 10);
		}
		genStructure.setHullPlain(world, x + 12, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 6);
		genStructure.setWiring(world, x + 12, y + 2, z + 7);
		genStructure.setWiring(world, x + 12, y + 2, z + 8);
		if (!isCorrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 12, y + 2, z + 9), WarpDrive.blockLift.getDefaultState());
			if (isCreative) {// fill with energy
				final TileEntity tileEntity = world.getTileEntity(new BlockPos(x + 12, y + 2, z + 9));
				if (tileEntity instanceof TileEntityAbstractEnergy) {
					((TileEntityAbstractEnergy) tileEntity).energy_consume(-((TileEntityAbstractEnergy) tileEntity).energy_getMaxStorage());
				}
			}
		}
		genStructure.setHullPlain(world, x + 12, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 6);
		genStructure.setWiring(world, x + 12, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 2);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 2);
		genStructure.setWallLight(world, x + 12, y + 5, z + 4);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 6);
		if (!isCorrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 12, y + 5, z + 7),
			                    WarpDrive.blockShipCores[1].getDefaultState().with(BlockProperties.FACING_HORIZONTAL, Direction.WEST));
			if (isCreative) {// fill with energy
				final TileEntity tileEntity = world.getTileEntity(new BlockPos(x + 12, y + 5, z + 7));
				if (tileEntity instanceof TileEntityAbstractEnergy) {
					((TileEntityAbstractEnergy) tileEntity).energy_consume( - ((TileEntityAbstractEnergy) tileEntity).energy_getMaxStorage() / 2);
				}
			}
		}
		genStructure.setHullGlass(world, x + 12, y + 5, z + 8);
		genStructure.setWallLight(world, x + 12, y + 5, z + 10);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 5);
		genStructure.setWiring(world, x + 12, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 12, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 2);
		if (rand.nextBoolean()) {
			genStructure.setHullPlain(world, x + 13, y + 3, z + 3);
			genStructure.setHullGlass(world, x + 13, y + 4, z + 3);
		} else if (!isCorrupted || rand.nextBoolean()) {
//			world.setBlockState(new BlockPos(x + 13, y + 3, z + 3), WarpDrive.blockAirShields[0].getDefaultState());
//			world.setBlockState(new BlockPos(x + 13, y + 4, z + 3), WarpDrive.blockAirShields[0].getDefaultState());
		}
		genStructure.setHullPlain(world, x + 13, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 6);
		genStructure.setWiring(world, x + 13, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 10);
		if (rand.nextBoolean()) {
			genStructure.setHullPlain(world, x + 13, y + 3, z + 11);
			genStructure.setHullGlass(world, x + 13, y + 4, z + 11);
		} else if (!isCorrupted || rand.nextBoolean()) {
//			world.setBlockState(new BlockPos(x + 13, y + 3, z + 11), WarpDrive.blockAirShields[0].getDefaultState());
//			world.setBlockState(new BlockPos(x + 13, y + 4, z + 11), WarpDrive.blockAirShields[0].getDefaultState());
		}
		genStructure.setHullPlain(world, x + 13, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 2);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 6);
		genStructure.setWiring(world, x + 13, y + 4, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 6);
		genStructure.setWiring(world, x + 13, y + 5, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 6);
		genStructure.setWiring(world, x + 13, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 4, z + 4);
		genStructure.setPropulsion(world, x + 14, y + 4, z + 5);
		genStructure.setPropulsion(world, x + 14, y + 4, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 4, z + 7);
		genStructure.setPropulsion(world, x + 14, y + 4, z + 8);
		genStructure.setPropulsion(world, x + 14, y + 4, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 5, z + 4);
		genStructure.setPropulsion(world, x + 14, y + 5, z + 5);
		genStructure.setPropulsion(world, x + 14, y + 5, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 5, z + 7);
		genStructure.setPropulsion(world, x + 14, y + 5, z + 8);
		genStructure.setPropulsion(world, x + 14, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 15, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 4, z + 7);
		genStructure.setPropulsion(world, x + 15, y + 5, z + 7);
		genStructure.setHullPlain(world, x + 15, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 15, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 16, y + 4, z + 7);
		genStructure.setPropulsion(world, x + 16, y + 5, z + 7);
		genStructure.setHullPlain(world, x + 16, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 17, y + 5, z + 7);
		spawnNPC(world, x + 9, y + 3, z + 5);
		return true;
	}
	
	private static void spawnNPC(final World world, final int x, final int y, final int z) {
		final int countMobs = 2 + world.rand.nextInt(10);
		
		if (world.rand.nextBoolean()) {// Villagers
			for (int idx = 0; idx < countMobs; idx++) {
				final VillagerEntity entityVillager = new VillagerEntity(EntityType.VILLAGER, world);
				entityVillager.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
				final ItemStack itemStackHelmet = new ItemStack(WarpDrive.itemWarpArmor[EnumTier.BASIC.getIndex()][3], 1);
				itemStackHelmet.setDamage(1);
				entityVillager.setItemStackToSlot(EquipmentSlotType.HEAD, itemStackHelmet);
				world.addEntity(entityVillager);
			}
		} else if (world.rand.nextBoolean()) {// Zombies
			for (int idx = 0; idx < countMobs; idx++) {
				final ZombieEntity entityZombie = new ZombieEntity(world);
				entityZombie.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
				world.addEntity(entityZombie);
			}
		} else {// Zombie pigmen
			for (int idx = 0; idx < countMobs; idx++) {
				final ZombiePigmanEntity entityZombie = new ZombiePigmanEntity(EntityType.ZOMBIE_PIGMAN, world);
				entityZombie.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
				world.addEntity(entityZombie);
			}
		}
	}
}
