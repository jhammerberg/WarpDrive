package cr0s.warpdrive.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class MutableBlockItemUseContext extends BlockItemUseContext {
	
	private ItemStack itemStack;
	
	public MutableBlockItemUseContext(@Nonnull final World world, @Nullable final PlayerEntity entityPlayer, @Nonnull final Hand hand,
	                                  @Nonnull final ItemStack itemStack, @Nonnull final BlockRayTraceResult rayTraceResultIn) {
		super(world, entityPlayer, hand, itemStack, rayTraceResultIn);
		this.itemStack = itemStack;
	}
	
	public void setItemStack(@Nonnull final ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	@Nonnull
	@Override
	public ItemStack getItem() {
		return itemStack;
	}
}
