package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.event.ModelHandler;
import cr0s.warpdrive.render.BakedModelCamouflage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockCamouflage extends BlockAbstractContainer {
	
	public BlockCamouflage(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(Material.WOOL)
				      .hardnessAndResistance(1.0F)
				      .notSolid(), registryName, enumTier);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		super.modelInitialisation();
		
		// register (smart) baked model
		final ResourceLocation registryName = getRegistryName();
		assert registryName != null;
		ModelHandler.registerBakedModel(new ModelResourceLocation(registryName, ""), BakedModelCamouflage.class);
	}
	
	@Override
	public SoundType getSoundType(@Nonnull final BlockState blockState, @Nonnull final IWorldReader worldReader, @Nonnull final BlockPos blockPos,
	                              @Nullable final Entity entity) {
		final TileEntity tileEntity = worldReader.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityCamouflage)) {
			return super.getSoundType(blockState, worldReader, blockPos, entity);
		}
		final TileEntityCamouflage tileEntityCamouflage = (TileEntityCamouflage) tileEntity;
		final BlockState blockStateCamouflage = tileEntityCamouflage.getCamouflage(null);
		if (blockStateCamouflage != null) {
			return blockStateCamouflage.getBlock().getSoundType(blockState, worldReader, blockPos, entity);
		}
		return super.getSoundType(blockState, worldReader, blockPos, entity);
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(@Nonnull final BlockState blockState, @Nonnull final BlockState blockStateAdjacent, @Nonnull final Direction side) {
		return blockStateAdjacent.getBlock() == this
		    || super.isSideInvisible(blockState, blockStateAdjacent, side);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable final IBlockReader blockReader,
	                           @Nonnull final List<ITextComponent> list, @Nonnull final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, blockReader, list, advancedItemTooltips);
		
		Commons.addTooltip(list, new TranslationTextComponent(getTranslationKey() + ".tooltip.usage").getFormattedText());
	}
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                                         @Nonnull final PlayerEntity entityPlayer, @Nonnull final Hand enumHand,
	                                         @Nonnull final BlockRayTraceResult blockRaytraceResult) {
		if ( world.isRemote()
		  || enumHand != Hand.MAIN_HAND ) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityCamouflage)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityCamouflage tileEntityCamouflage = (TileEntityCamouflage) tileEntity;
		
		if (!entityPlayer.isSneaking()) {
			if (!(itemStackHeld.getItem() instanceof BlockItem)) {
				entityPlayer.sendStatusMessage(new StringTextComponent("A block is required"), true);
				return ActionResultType.FAIL;
			}
			
			// get the blockstate candidate
			final Block block = ((BlockItem) itemStackHeld.getItem()).getBlock();
			final BlockState blockStateToSet = block.getStateForPlacement(new BlockItemUseContext(new ItemUseContext(entityPlayer, enumHand, blockRaytraceResult)));
			if (blockStateToSet == null) {
				entityPlayer.sendStatusMessage(new StringTextComponent("Invalid block placement"), true);
				return ActionResultType.FAIL;
			}
			
			// skip blacklisted blocks
			if (Dictionary.BLOCKS_NOCAMOUFLAGE.contains(block)) {
				entityPlayer.sendStatusMessage(new StringTextComponent("NOCAMOUFLAGE tag found, unable to proceed."), true);
				return ActionResultType.FAIL;
			}
			
			// skip unsupported rendering options and update blacklist accordingly
			@SuppressWarnings("deprecation") // KISS: don't create a fake world just to get the render type
			final BlockRenderType renderType = block.getRenderType(blockStateToSet);
			if ( !Block.isOpaque(blockStateToSet.getCollisionShape(world, blockPos, ISelectionContext.dummy()))
			  || renderType != BlockRenderType.MODEL ) {
//				Dictionary.BLOCKS_NOCAMOUFLAGE.add(block);
				entityPlayer.sendStatusMessage(new StringTextComponent("Unsupported block rendering properties."), true);
//				return ActionResultType.FAIL;
			}
			
			// actually set the camouflage
			tileEntityCamouflage.setCamouflage(blockStateToSet, blockRaytraceResult.getFace());
			
			// audio feedback
			@SuppressWarnings("deprecation") // KISS: don't create a fake world just to get the sound type
			final SoundType type = block.getSoundType(blockStateToSet);
			world.playSound(null, blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D,
			                type.getPlaceSound(), SoundCategory.BLOCKS, type.getVolume(), type.getPitch());
			
			return ActionResultType.SUCCESS;
			
		} else if (itemStackHeld.isEmpty()) {
			tileEntityCamouflage.setCamouflage(null, blockRaytraceResult.getFace());
			return ActionResultType.SUCCESS;
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}