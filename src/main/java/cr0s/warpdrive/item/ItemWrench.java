package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IWarpTool;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;

import javax.annotation.Nonnull;

import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends ItemAbstractBase implements IWarpTool {
	
	public ItemWrench(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(new Item.Properties()
				      .group(WarpDrive.itemGroupMain)
				      .maxDamage(0)
				      .maxStackSize(1),
		      registryName,
		      enumTier );
		
		setTranslationKey("warpdrive.tool.wrench");
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(@Nonnull final ItemUseContext context) {
		final World world = context.getWorld();
		if (world.isRemote()) {
			return ActionResultType.FAIL;
		}
		
		// get context
		final PlayerEntity entityPlayer = context.getPlayer();
		if (entityPlayer == null) {
			return ActionResultType.FAIL;
		}
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(context.getHand());
		final BlockPos blockPos = context.getPos();
		final BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock().isAir(blockState, world, blockPos)) {
			return ActionResultType.FAIL;
		}
		final Direction facing = context.getFace();
		
		// note: we allow sneaking usage so we can rotate blocks with GUIs
		
		// compute effect position
		final Vector3 vFace = new Vector3(blockPos).translate(0.5D);
		
		// @TODO: confirm if both are really needed
		if ( !entityPlayer.canPlayerEdit(blockPos, facing, itemStackHeld)
		  || !world.isBlockModifiable(entityPlayer, blockPos) ) {
			PacketHandler.sendSpawnParticlePacket(world, "jammed", (byte) 5,
			                                      vFace,
			                                      new Vector3(0.0D, 0.0D, 0.0D),
			                                      1.0F, 1.0F, 1.0F,
			                                      1.0F, 1.0F, 1.0F,
			                                      6 );
			return ActionResultType.FAIL;
		}
		
		BlockState blockStateRotated = blockState.rotate(world, blockPos, Rotation.CLOCKWISE_90);
		if (blockState != blockStateRotated) {
			if (!world.setBlockState(blockPos, blockStateRotated)) {
				PacketHandler.sendSpawnParticlePacket(world, "jammed", (byte) 5,
				                                      vFace,
				                                      new Vector3(0.0D, 0.0D, 0.0D),
				                                      1.0F, 1.0F, 1.0F,
				                                      1.0F, 1.0F, 1.0F,
				                                      6 );
				return ActionResultType.FAIL;
			}
		}
		
		// no chat message
		
		// standard place sound effect
		final SoundType soundType = blockState.getBlock().getSoundType(blockState, world, blockPos, null);
		world.playSound(null, blockPos, soundType.getPlaceSound(), SoundCategory.BLOCKS,
		                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
		
		world.notifyNeighborsOfStateChange(blockPos, blockState.getBlock());
		
		entityPlayer.swingArm(context.getHand());
		
		return ActionResultType.SUCCESS;
	}
}
