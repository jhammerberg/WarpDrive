package cr0s.warpdrive.event;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import icbm.classic.api.caps.IEMPReceiver;
import icbm.classic.api.explosion.IBlast;
import icbm.classic.api.ICBMClassicAPI;

public class EMPReceiver implements IEMPReceiver, ICapabilityProvider {
	
	public static final ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "EMPReceiver");
	
	private final TileEntityAbstractBase tileEntityAbstractBase;
	
	@SubscribeEvent
	public static void onAttachCapability(final AttachCapabilitiesEvent<TileEntity> event) {
		final TileEntity tileEntity = event.getObject();
		if (tileEntity instanceof TileEntityAbstractBase) {
			event.addCapability(resourceLocation, new EMPReceiver((TileEntityAbstractBase) tileEntity));
		}
	}
	
	private EMPReceiver(final TileEntityAbstractBase tileEntityAbstractBase) {
		super();
		this.tileEntityAbstractBase = tileEntityAbstractBase;
	}
	
	@Override
	public float applyEmpAction(final World world, final double x, final double y, final double z,
	                            final IBlast blastEMP, final float power, final boolean doAction) {
		if (!doAction) {
			return power;
		}
		
		// directly access the exploder since ICBM has a stack-overflow on blastEMP.getBlastSource()
		final Entity exploder = blastEMP instanceof Explosion ? ((Explosion) blastEMP).getExplosivePlacedBy() : null;
		if (WarpDriveConfig.LOGGING_WEAPON) {
			WarpDrive.logger.info(String.format("EMP received %s from %s with source %s and radius %.1f",
			                                    Commons.format(world, x, y, z),
			                                    blastEMP, exploder, blastEMP.getBlastRadius()));
		}
		// EMP explosive = 3k Energy, 50 radius
		// EMP tower = 3k Energy, 60 radius adjustable
		if (blastEMP.getBlastRadius() == 50.0F) {
			tileEntityAbstractBase.onEMP(0.70F);
		} else if (blastEMP.getBlastRadius() > 0.0F) {// compensate tower stacking effect
			tileEntityAbstractBase.onEMP(Math.min(1.0F, blastEMP.getBlastRadius() / 60.0F) * 0.02F);
		} else {
			if (Commons.throttleMe("EMPReceiver Invalid radius")) {
				WarpDrive.logger.warn(String.format("EMP received @ %s from %s with source %s and unsupported radius %.1f",
				                                    Commons.format(world, x, y, z),
				                                    blastEMP, exploder, blastEMP.getBlastRadius()));
				Commons.dumpAllThreads();
			}
			tileEntityAbstractBase.onEMP(0.02F);
		}
		
		return power;
	}
	
	@Override
	public boolean shouldEmpSubObjects(final World world, final double x, final double y, final double z) {
		return true;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, @Nullable final Direction facing) {
		return capability == ICBMClassicAPI.EMP_CAPABILITY ? (LazyOptional<T>) LazyOptional.of(() -> this) : LazyOptional.empty();
	}
}