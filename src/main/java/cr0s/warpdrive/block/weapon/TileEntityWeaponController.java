package cr0s.warpdrive.block.weapon;

import cr0s.warpdrive.block.TileEntityAbstractInterfaced;

import javax.annotation.Nonnull;
import java.util.Collections;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityWeaponController extends TileEntityAbstractInterfaced {
	
	public static TileEntityType<TileEntityWeaponController> TYPE;
	
	public TileEntityWeaponController() {
		super(TYPE);
		
		peripheralName = "warpdriveWeaponController";
		CC_scripts = Collections.singletonList("startup");
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull final CompoundNBT tagCompound) {
		return super.write(tagCompound);
	}
}