package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.data.EnumTier;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockElectromagnetPlain extends BlockAbstractAccelerator {
	
	protected BlockElectromagnetPlain(@Nonnull final Block.Properties blockProperties,
	                                  @Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		super(blockProperties, registryName, enumTier);
	}
	
	public BlockElectromagnetPlain(@Nonnull final String registryName, @Nonnull final EnumTier enumTier) {
		this(getDefaultProperties(Material.IRON), registryName, enumTier);
	}
}