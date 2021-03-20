package cr0s.warpdrive.block.force_field;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IForceFieldUpgrade;
import cr0s.warpdrive.api.IForceFieldUpgradeEffector;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Style;

import javax.annotation.Nonnull;

public class TileEntityForceFieldRelay extends TileEntityAbstractForceField implements IForceFieldUpgrade {
	
	// persistent properties
	private EnumForceFieldUpgrade upgrade = EnumForceFieldUpgrade.NONE;
	
	public TileEntityForceFieldRelay(@Nonnull final IBlockBase blockBase) {
		super(blockBase);
		
		peripheralName = "warpdriveForceFieldRelay";
		doRequireUpgradeToInterface();
	}
	
	// onFirstTick
	// update
	
	protected EnumForceFieldUpgrade getUpgrade() {
		if (upgrade == null) {
			return EnumForceFieldUpgrade.NONE;
		}
		return upgrade;
	}
	
	protected void setUpgrade(@Nonnull final EnumForceFieldUpgrade upgrade) {
		if (this.upgrade != upgrade) {
			this.upgrade = upgrade;
			if (hasWorld()) {
				updateBlockState(null, BlockForceFieldRelay.UPGRADE, upgrade);
			}
		}
	}
	
	@Override
	protected WarpDriveText getUpgradeStatus(final boolean isAnimated) {
		final WarpDriveText warpDriveText = new WarpDriveText(null, "warpdrive.upgrade.status_line.header");
		final EnumForceFieldUpgrade enumForceFieldUpgrade = getUpgrade();
		final String keyName = ItemForceFieldUpgrade.getItemStack(enumForceFieldUpgrade).getTranslationKey();
		final int value = enumForceFieldUpgrade == EnumForceFieldUpgrade.NONE ? 0 : 1;
		final Style style = value == 0 ? Commons.getStyleDisabled() : Commons.getStyleCorrect();
		warpDriveText.append(Commons.getStyleDisabled(), "- %1$s/%2$s x %3$s",
		                     new WarpDriveText(Commons.getStyleValue(), "%1$s", value),
		                     1,
		                     new WarpDriveText(style, keyName) );
		return warpDriveText;
	}
	
	@Override
	public void read(@Nonnull final CompoundNBT tagCompound) {
		super.read(tagCompound);
		
		setUpgrade(EnumForceFieldUpgrade.get(tagCompound.getString("upgrade")));
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tagCompound) {
		tagCompound = super.write(tagCompound);
		tagCompound.putString("upgrade",getUpgrade().name());
		return tagCompound;
	}
	
	@Override
	public Object[] getEnergyRequired() {
		return new Object[] { false, "No energy consumption" };
	}
	
	@Override
	public IForceFieldUpgradeEffector getUpgradeEffector(final Object container) {
		return isEnabled ? getUpgrade() : null;
	}
	
	@Override
	public float getUpgradeValue(final Object container) {
		return isEnabled ? getUpgrade().getUpgradeValue(container) * (1.0F + (enumTier.getIndex() - 1) * ForceFieldSetup.FORCEFIELD_UPGRADE_BOOST_FACTOR_PER_RELAY_TIER) : 0.0F;
	}
}