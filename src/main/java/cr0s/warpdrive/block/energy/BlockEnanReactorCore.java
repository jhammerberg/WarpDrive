package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumTier;

import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockEnanReactorCore extends BlockAbstractContainer {
	
	public static final IntegerProperty ENERGY = IntegerProperty.create("energy", 0, 3);
	public static final IntegerProperty INSTABILITY = IntegerProperty.create("stability", 0, 3);
	
	public BlockEnanReactorCore(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(getDefaultProperties(null), registryName, enumTier);
		
		setDefaultState(getDefaultState()
				                .with(ENERGY, 0)
				                .with(INSTABILITY, 0)
		               );
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull final BlockState blockState, @Nonnull final IBlockReader blockReader) {
		return new TileEntityEnanReactorCore();
	}
	
	/* TODO MC1.15 reactor core TESR
	@OnlyIn(Dist.CLIENT)
	@Override
	public void modelInitialisation() {
		super.modelInitialisation();
		
		if (enumTier != EnumTier.BASIC) {
			// Bind our TESR to our tile entity
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnanReactorCore.class, new TileEntityEnanReactorCoreRenderer());
		}
	}
	*/
}