package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.material.Material;

public class BlockVoidShellPlain extends BlockAbstractAccelerator {
	
	public BlockVoidShellPlain(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nullable Material material) {
		super(registryName, enumTier, material);
	}
}
