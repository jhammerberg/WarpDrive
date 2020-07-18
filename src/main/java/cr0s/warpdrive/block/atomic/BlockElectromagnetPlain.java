package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;

public class BlockElectromagnetPlain extends BlockAbstractAccelerator {
	
	public BlockElectromagnetPlain(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final Material material) {
		super(registryName, enumTier, material);
	}
}
