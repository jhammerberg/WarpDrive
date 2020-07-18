package cr0s.warpdrive.block.atomic;

import net.minecraft.tileentity.TileEntityType;

public class TileEntityParticlesInjector extends TileEntityAcceleratorControlPoint {
	
	public static TileEntityType<TileEntityParticlesInjector> TYPE;
	
	public TileEntityParticlesInjector() {
		super(TYPE);
		
		peripheralName = "warpdriveParticlesInjector";
	}
}
