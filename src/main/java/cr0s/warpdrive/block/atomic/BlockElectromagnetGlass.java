package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;

public class BlockElectromagnetGlass extends BlockElectromagnetPlain {
	
	public BlockElectromagnetGlass(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(registryName, enumTier, Material.GLASS);
	}
}
