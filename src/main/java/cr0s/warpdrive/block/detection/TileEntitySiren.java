package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.block.TileEntityAbstractMachine;
import cr0s.warpdrive.client.SirenSound;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.SoundEvents;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySiren extends TileEntityAbstractMachine {
	
	public enum EnumSirenState {
		STARTING, STARTED, STOPPING, STOPPED
	}
	
	// persistent properties
	// (none)
	
	// computed properties
	private EnumSirenState enumSirenState = EnumSirenState.STOPPED;
	private boolean isIndustrial = false;
	private float range = 0.0F;
	private int tickUpdate = 0;
	
	@OnlyIn(Dist.CLIENT)
	private SirenSound sound;
	
	public TileEntitySiren(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveSiren";
		doRequireUpgradeToInterface();
	}
	
	@Override
	protected void onFirstTick() {
		super.onFirstTick();
		assert world != null;
		
		range = WarpDriveConfig.SIREN_RANGE_BLOCKS_BY_TIER[enumTier.getIndex()];
		
		final BlockState blockState = world.getBlockState(pos);
		isIndustrial = ((BlockSiren) blockState.getBlock()).getIsIndustrial();
	}
	
	@Override
	public void tick() {
		super.tick();
		
		/* Updating the sound too quickly breaks Minecraft's sounds handler.
		 * Therefore, we only update our sound once every 0.5 seconds.
		 * It's less responsive like this, but doesn't completely freak out when
		 * spamming the redstone on and off. */
		
		tickUpdate--;
		if (tickUpdate <= 0) {
			tickUpdate = 10;
		} else {
			return;
		}
		
		if ( world == null
		  || !world.isRemote() ) {
		    return;
        }
		if (sound == null) {
		    setSound();
        }
		
		// Siren sound logic.
		switch (enumSirenState) {
		case STOPPED:
			if (isPlaying()) {// (recover)
				enumSirenState = EnumSirenState.STOPPING;
			} else if (isPowered()) {
				enumSirenState = EnumSirenState.STARTING;
			}
			break;
			
		case STARTING:
			if (startSound()) {
				enumSirenState = EnumSirenState.STARTED;
			} else {
				enumSirenState = EnumSirenState.STOPPING;
			}
			break;
			
		case STARTED:
			if (!isPowered()) {
				enumSirenState = EnumSirenState.STOPPING;
			} else if (!isPlaying()) {
				enumSirenState = EnumSirenState.STARTING;
			}
			break;
			
		case STOPPING:
			if (isPlaying()) {
				stopSound();
			} else {
				enumSirenState = EnumSirenState.STOPPED;
			}
			break;
   
		default:
			if (isPlaying()) {
				enumSirenState = EnumSirenState.STOPPING;
			} else {
				enumSirenState = EnumSirenState.STOPPED;
			}
			break;
		}
	}
    
	// Stops the siren when the chunk is unloaded.
	@Override
	public void onChunkUnloaded() {
		if ( world != null
		  && world.isRemote()
		  && isPlaying() ) {
		    stopSound();
        }
		super.onChunkUnloaded();
	}
    
	// Stops the siren when the TileEntity object is invalidated.
	@Override
	public void remove() {
		if ( world != null
		  && world.isRemote()
		  && isPlaying() ) {
		    stopSound();
        }
		super.remove();
	}
    
	// Create a new SirenSound object that the siren will use.
	@OnlyIn(Dist.CLIENT)
	private void setSound() {
		sound = new SirenSound(isIndustrial ? SoundEvents.SIREN_INDUSTRIAL : SoundEvents.SIREN_RAID, range, pos);
	}
    
	// Forces the siren to start playing its sound;
	@OnlyIn(Dist.CLIENT)
    private boolean startSound() {
		if (!isPlaying()) {
			try {
				Minecraft.getInstance().getSoundHandler().play(sound);
				return true;
			} catch (final IllegalArgumentException exception) {
				return false;
			}
		} else {
			return true;
		}
	}
    
	// Forces the siren to stop playing its sound.
	@OnlyIn(Dist.CLIENT)
	void stopSound() {
		Minecraft.getInstance().getSoundHandler().stop(sound);
	}
    
	// Checks if the siren is currently playing its sound.
	@OnlyIn(Dist.CLIENT)
	boolean isPlaying() {
		return Minecraft.getInstance().getSoundHandler().isPlaying(sound);
	}
    
	// Checks if the siren is being powered by redstone.
	private boolean isPowered() {
		assert world != null;
		return world.getRedstonePowerFromNeighbors(pos) > 0;
	}
}