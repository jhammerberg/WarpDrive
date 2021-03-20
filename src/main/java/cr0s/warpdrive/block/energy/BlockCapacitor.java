package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IWarpTool;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumDisabledInputOutput;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.event.ModelHandler;
import cr0s.warpdrive.render.BakedModelCapacitor;

import ic2.api.energy.tile.IExplosionPowerOverride;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockCapacitor extends BlockAbstractContainer implements IExplosionPowerOverride {
	
	public static final IProperty<EnumDisabledInputOutput> CONFIG = EnumProperty.create("config", EnumDisabledInputOutput.class);
	
	// TODO MC1.15 Capacitor rendering
	public static final IProperty<EnumDisabledInputOutput> DOWN  = EnumProperty.create("down" , EnumDisabledInputOutput.class);
	public static final IProperty<EnumDisabledInputOutput> UP    = EnumProperty.create("up"   , EnumDisabledInputOutput.class);
	public static final IProperty<EnumDisabledInputOutput> NORTH = EnumProperty.create("north", EnumDisabledInputOutput.class);
	public static final IProperty<EnumDisabledInputOutput> SOUTH = EnumProperty.create("south", EnumDisabledInputOutput.class);
	public static final IProperty<EnumDisabledInputOutput> WEST  = EnumProperty.create("west" , EnumDisabledInputOutput.class);
	public static final IProperty<EnumDisabledInputOutput> EAST  = EnumProperty.create("east" , EnumDisabledInputOutput.class);
	
	public BlockCapacitor(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getStateContainer().getBaseState()
				                .with(CONFIG, EnumDisabledInputOutput.DISABLED)
		               );
	}
	
	@Override
	protected void fillStateContainer(@Nonnull final Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(CONFIG);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		super.modelInitialisation();
		
		// register (smart) baked model
		final ResourceLocation registryName = getRegistryName();
		assert registryName != null;
		for (final EnumDisabledInputOutput enumDisabledInputOutput : CONFIG.getAllowedValues()) {
			final String variant = String.format("%s=%s",
			                                     CONFIG.getName(), enumDisabledInputOutput);
			ModelHandler.registerBakedModel(new ModelResourceLocation(registryName, variant), BakedModelCapacitor.class);
		}
	}
	
	// IExplosionPowerOverride overrides
	@Override
	public boolean shouldExplode() {
		return false;
	}
	
	@Override
	public float getExplosionPower(final int tier, final float defaultPower) {
		return defaultPower;
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
		
		// get context
		final ItemStack itemStackHeld = entityPlayer.getHeldItem(enumHand);
		final TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityCapacitor)) {
			return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
		}
		final TileEntityCapacitor tileEntityCapacitor = (TileEntityCapacitor) tileEntity;
		final Direction directionFace = blockRaytraceResult.getFace();
		
		if ( !itemStackHeld.isEmpty()
		  && itemStackHeld.getItem() instanceof IWarpTool ) {
			if (entityPlayer.isSneaking()) {
				tileEntityCapacitor.setMode(directionFace, tileEntityCapacitor.getMode(directionFace).getPrevious());
			} else {
				tileEntityCapacitor.setMode(directionFace, tileEntityCapacitor.getMode(directionFace).getNext());
			}
			final ItemStack itemStack = new ItemStack(Item.BLOCK_TO_ITEM.getOrDefault(this, Items.AIR));
			switch (tileEntityCapacitor.getMode(directionFace)) {
			case INPUT:
				Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(itemStack)
				                                            .appendSibling(new TranslationTextComponent("warpdrive.energy.side.changed_to_input",
				                                                                                        directionFace.name() )));
				return ActionResultType.CONSUME;
			case OUTPUT:
				Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(itemStack)
				                                            .appendSibling(new TranslationTextComponent("warpdrive.energy.side.changed_to_output",
				                                                                                        directionFace.name() )));
				return ActionResultType.CONSUME;
			case DISABLED:
			default:
				Commons.addChatMessage(entityPlayer, Commons.getChatPrefix(itemStack)
				                                            .appendSibling(new TranslationTextComponent("warpdrive.energy.side.changed_to_disabled",
				                                                                                        directionFace.name() )));
				return ActionResultType.CONSUME;
			}
		}
		
		return super.onBlockActivated(blockState, world, blockPos, entityPlayer, enumHand, blockRaytraceResult);
	}
}