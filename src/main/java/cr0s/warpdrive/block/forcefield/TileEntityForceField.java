package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.VectorI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class TileEntityForceField extends TileEntity {
	
	public static TileEntityType<TileEntityForceField> TYPE;
	
	private BlockPos blockPosProjector;
	
	// cache parameters used for rendering, force projector check for others
	private int cache_beamFrequency;
	public BlockState cache_blockStateCamouflage;
	protected int cache_colorMultiplierCamouflage;
	protected int cache_lightCamouflage;
	
	// number of projectors check ignored before self-destruction
	private int gracePeriod_calls = 3;
	
	public TileEntityForceField() {
		super(TYPE);
	}
	
	// saved properties
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		if (tagCompound.contains("projector")) {// are we server side and is it a valid force field block?
			blockPosProjector = Commons.createBlockPosFromNBT(tagCompound.getCompound("projector"));
			cache_beamFrequency = tagCompound.getInt(IBeamFrequency.BEAM_FREQUENCY_TAG);
		} else {
			blockPosProjector = null;
			cache_beamFrequency = -1;
		}
		if (tagCompound.contains("camouflage")) {
			final CompoundNBT nbtCamouflage = tagCompound.getCompound("camouflage");
			try {
				cache_blockStateCamouflage = Commons.readBlockStateFromNBT(nbtCamouflage.get("state"));
				cache_colorMultiplierCamouflage = nbtCamouflage.getInt("color");
				cache_lightCamouflage = nbtCamouflage.getByte("light");
				if (Dictionary.BLOCKS_NOCAMOUFLAGE.contains(cache_blockStateCamouflage.getBlock())) {
					cache_blockStateCamouflage = null;
					cache_colorMultiplierCamouflage = 0;
					cache_lightCamouflage = 0;
				}
			} catch (final Exception exception) {
				exception.printStackTrace(WarpDrive.printStreamError);
				cache_blockStateCamouflage = null;
				cache_colorMultiplierCamouflage = 0;
				cache_lightCamouflage = 0;
			}
		} else {
			cache_blockStateCamouflage = null;
			cache_colorMultiplierCamouflage = 0;
			cache_lightCamouflage = 0;
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		
		if (blockPosProjector != null) {
			tagCompound.put("projector", Commons.writeBlockPosToNBT(blockPosProjector, new CompoundNBT()));
			tagCompound.putInt(IBeamFrequency.BEAM_FREQUENCY_TAG, cache_beamFrequency);
			if (cache_blockStateCamouflage != null && cache_blockStateCamouflage.getBlock() != Blocks.AIR) {
				final CompoundNBT nbtCamouflage = new CompoundNBT();
				assert cache_blockStateCamouflage.getBlock().getRegistryName() != null;
				nbtCamouflage.put("state", Commons.writeBlockStateToNBT(cache_blockStateCamouflage));
				nbtCamouflage.putInt("color", cache_colorMultiplierCamouflage);
				nbtCamouflage.putByte("light", (byte) cache_lightCamouflage);
				tagCompound.put("camouflage", nbtCamouflage);
			}
		}
		return tagCompound;
	}
	
	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		final CompoundNBT tagCompound = write(super.getUpdateTag());
		
		tagCompound.remove("projector");
		tagCompound.remove(IBeamFrequency.BEAM_FREQUENCY_TAG);
		
		return tagCompound;
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(@Nonnull final NetworkManager networkManager, @Nonnull final SUpdateTileEntityPacket packet) {
		final CompoundNBT tagCompound = packet.getNbtCompound();
		read(tagCompound);
	}
	
	public void setProjector(final BlockPos blockPos) {
		blockPosProjector = blockPos.toImmutable();
		final ForceFieldSetup forceFieldSetup = getForceFieldSetup();
		if (forceFieldSetup != null) {
			cache_beamFrequency = forceFieldSetup.beamFrequency;
			cache_blockStateCamouflage = forceFieldSetup.getCamouflageBlockState();
			cache_colorMultiplierCamouflage = forceFieldSetup.getCamouflageColorMultiplier();
			cache_lightCamouflage = forceFieldSetup.getCamouflageLight();
		}
		assert world != null;
		final BlockState blockState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, blockState, blockState, 3);
	}
	
	public TileEntityForceFieldProjector getProjector(@Nullable final TileEntityForceFieldProjector tileEntityForceFieldProjectorCandidate) {
		assert world != null;
		if (blockPosProjector != null) {
			// test candidate to save a call to getTileEntity()
			if ( tileEntityForceFieldProjectorCandidate != null
			  && blockPosProjector.equals(tileEntityForceFieldProjectorCandidate.getPos()) ) {
				return tileEntityForceFieldProjectorCandidate;
			}
			
			final TileEntity tileEntity = world.getTileEntity(blockPosProjector);
			if (tileEntity instanceof TileEntityForceFieldProjector) {
				final TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector) tileEntity;
				if (world.isRemote()) {
					return tileEntityForceFieldProjector;
					
				} else if (tileEntityForceFieldProjector.isPartOfForceField(new VectorI(this))) {
					if (tileEntityForceFieldProjector.isActive()) {
						return tileEntityForceFieldProjector;
					} else {
						// projector is disabled or out of power
						world.removeBlock(pos,false);
						if (WarpDriveConfig.LOGGING_FORCE_FIELD) {
							WarpDrive.logger.info(String.format("Removed a force field from an offline projector %s",
							                                    Commons.format(world, pos)));
						}
					}
				}
			}
		}
		
		if (!world.isRemote()) {
			gracePeriod_calls--;
			if (gracePeriod_calls < 0) {
				world.removeBlock(pos,false);
				if (WarpDriveConfig.LOGGING_FORCE_FIELD) {
					WarpDrive.logger.info(String.format("Removed a force field with no projector defined %s",
					                                    Commons.format(world, pos)));
				}
			}
		}
		
		return null;
	}
	
	public ForceFieldSetup getForceFieldSetup() {
		final TileEntityForceFieldProjector tileEntityForceFieldProjector = getProjector(null);
		if (tileEntityForceFieldProjector == null) {
			return null;
		}
		return tileEntityForceFieldProjector.getForceFieldSetup();
	}
}
