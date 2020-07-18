package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.SoundEvents;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;

import javax.annotation.Nonnull;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockChiller extends BlockAbstractAccelerator {
	
	private static final float BOUNDING_TOLERANCE = 0.05F;
	private static final VoxelShape SHAPE_CHILLER_COLLISION = makeCuboidShape(BOUNDING_TOLERANCE, 0.0D, BOUNDING_TOLERANCE,
	                                                                          1.0D - BOUNDING_TOLERANCE, 1.0D, 1.0D - BOUNDING_TOLERANCE );
	
	public BlockChiller(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier, null);
		
		setDefaultState(getDefaultState()
				                .with(BlockProperties.ACTIVE, false)
		               );
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader, @Nonnull final BlockPos blockPos,
	                                    @Nonnull final ISelectionContext selectionContext) {
		return SHAPE_CHILLER_COLLISION;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEntityCollision(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final Entity entity) {
		super.onEntityCollision(blockState, world, blockPos, entity);
		if (world.isRemote()) {
			return;
		}
		
		onEntityEffect(world, blockPos, entity);
	}
	
	@Override
	public void onEntityWalk(@Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final Entity entity) {
		super.onEntityWalk(world, blockPos, entity);
		if (world.isRemote()) {
			return;
		}
		
		onEntityEffect(world, blockPos, entity);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockClicked(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final PlayerEntity entityPlayer) {
		super.onBlockClicked(blockState, world, blockPos, entityPlayer);
		if (world.isRemote()) {
			return;
		}
		
		onEntityEffect(world, blockPos, entityPlayer);
	}
	
	private void onEntityEffect(@Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final Entity entity) {
		if ( !entity.isAlive()
		  || !(entity instanceof LivingEntity) ) {
			return;
		}
		if (!world.getBlockState(blockPos).get(BlockProperties.ACTIVE)) {
			return;
		}
		if (!entity.isImmuneToFire()) {
			entity.setFire(1);
		}
		entity.attackEntityFrom(WarpDrive.damageWarm, 1 + enumTier.getIndex());
		
		final Vector3 v3Entity = new Vector3(entity);
		final Vector3 v3Chiller = new Vector3(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
		final Vector3 v3Direction = new Vector3(entity).subtract(v3Chiller).normalize();
		v3Chiller.translateFactor(v3Direction, 0.6D);
		v3Entity.translateFactor(v3Direction, -0.6D);
		
		// visual effect
		v3Direction.scale(0.20D);
		PacketHandler.sendSpawnParticlePacket(world, "snowShovel", (byte) 5, v3Entity, v3Direction,
			0.90F + 0.10F * world.rand.nextFloat(), 0.35F + 0.25F * world.rand.nextFloat(), 0.30F + 0.15F * world.rand.nextFloat(),
			0.0F, 0.0F, 0.0F, 32);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final Random random) {
		if (!blockState.get(BlockProperties.ACTIVE)) {
			return;
		}
		
		// sound effect
		final int countNearby = 17
		                - (world.getBlockState(blockPos.east()  ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.west()  ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.north() ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.south() ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.east(2) ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.west(2) ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.north(2)).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.south(2)).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.up(2).east()   ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.up(2).west()   ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.up(2).north()  ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.up(2).south()  ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.down(2).east() ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.down(2).west() ).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.down(2).north()).getBlock() == this ? 1 : 0)
		                - (world.getBlockState(blockPos.down(2).south()).getBlock() == this ? 1 : 0);
		if (world.rand.nextInt(17) < countNearby) {
			world.playSound(null, blockPos,
				SoundEvents.CHILLER, SoundCategory.AMBIENT, 1.0F, 1.0F);
		}
		
		// particle effect, loosely based on redstone ore
		if (world.rand.nextInt(8) != 1) {
			final double dOffset = 0.0625D;
			
			for (int l = 0; l < 6; ++l) {
				double dX = (float) blockPos.getX() + random.nextFloat();
				double dY = (float) blockPos.getY() + random.nextFloat();
				double dZ = (float) blockPos.getZ() + random.nextFloat();
				boolean isValidSide = false;
				
				if (l == 0 && !world.getBlockState(blockPos.up()).isOpaqueCube(world, blockPos.up())) {
					dY = blockPos.getY() + 1.0D + dOffset;
					isValidSide = true;
				}
				
				if (l == 1 && !world.getBlockState(blockPos.down()).isOpaqueCube(world, blockPos.down())) {
					dY = blockPos.getY() - dOffset;
					isValidSide = true;
				}
				
				if (l == 2 && !world.getBlockState(blockPos.south()).isOpaqueCube(world, blockPos.south())) {
					dZ = blockPos.getZ() + 1.0D + dOffset;
					isValidSide = true;
				}
				
				if (l == 3 && !world.getBlockState(blockPos.north()).isOpaqueCube(world, blockPos.north())) {
					dZ = blockPos.getZ() - dOffset;
					isValidSide = true;
				}
				
				if (l == 4 && !world.getBlockState(blockPos.east()).isOpaqueCube(world, blockPos.east())) {
					dX = blockPos.getX() + 1.0D + dOffset;
					isValidSide = true;
				}
				
				if (l == 5 && !world.getBlockState(blockPos.west()).isOpaqueCube(world, blockPos.west())) {
					dX = blockPos.getX() - dOffset;
					isValidSide = true;
				}
				
				if (isValidSide) {
					world.addParticle(RedstoneParticleData.REDSTONE_DUST, dX, dY, dZ, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
	
}
