package cr0s.warpdrive.client;

import javax.annotation.Nonnull;
import java.util.Random;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public abstract class ItemGroupAbstractBase extends ItemGroup {
	
	protected static Random random = new Random();
	
	private final long period;
	
	private ItemStack itemStack = ItemStack.EMPTY;
	private long timeLastChange;
	
	public ItemGroupAbstractBase(final String label, final long period) {
		super(label);
		
		this.period = period;
	}
	
	@Nonnull
	@Override
	public ItemStack getIcon() {
		final long timeCurrent = System.currentTimeMillis();
		if (timeLastChange < timeCurrent) {
			timeLastChange = timeCurrent + period;
			itemStack = createIcon();
		}
		return itemStack;
	}
}
