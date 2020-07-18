package cr0s.warpdrive;

import cr0s.warpdrive.block.TileEntityAbstractInterfaced;
import cr0s.warpdrive.config.WarpDriveConfig;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

import javax.annotation.Nonnull;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.util.LazyOptional;

public class WarpDrivePeripheralHandler implements IPeripheralProvider {
	
	public void register() {
		ComputerCraftAPI.registerPeripheralProvider(this);
	}
	
	@Nonnull
	@Override
	public LazyOptional<IPeripheral> getPeripheral(@Nonnull final World world, @Nonnull final BlockPos blockPos, @Nonnull final Direction side) {
		// ensure we only cover our own blocks
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(blockPos));
		if (tileEntity instanceof TileEntityAbstractInterfaced) {
			if (WarpDriveConfig.LOGGING_LUA) {
				WarpDrive.logger.info(String.format("[CC] IPeripheralProvider.getPeripheral %s %s %s",
				                                    Commons.format(world, blockPos), side, tileEntity ));
			}
			return LazyOptional.of(() -> (IPeripheral) tileEntity);
		}
		return LazyOptional.empty();
	}
}