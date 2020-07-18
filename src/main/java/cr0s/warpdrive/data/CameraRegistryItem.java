package cr0s.warpdrive.data;

import cr0s.warpdrive.api.IVideoChannel;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class CameraRegistryItem {
	
	public DimensionType dimensionType;
	public BlockPos blockPos;
	public int videoChannel;
	public EnumCameraType type;
	
	public CameraRegistryItem(final World world, final BlockPos blockPos, final int videoChannel, final EnumCameraType enumCameraType) {
		this.videoChannel = videoChannel;
		this.blockPos = blockPos;
		this.dimensionType = world.getDimension().getType();
		this.type = enumCameraType;
	}
	
	public boolean isTileEntity(final TileEntity tileEntity) {
		assert tileEntity.getWorld() != null;
		return tileEntity instanceof IVideoChannel
		    && videoChannel == ((IVideoChannel) tileEntity).getVideoChannel()
			&& blockPos.equals(tileEntity.getPos())
		    && dimensionType == tileEntity.getWorld().getDimension().getType();
	}
}