package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.api.IBlockBase;

import javax.annotation.Nonnull;

public class TileEntityParticlesInjector extends TileEntityAcceleratorControlPoint {
	
	public TileEntityParticlesInjector(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveParticlesInjector";
	}
}