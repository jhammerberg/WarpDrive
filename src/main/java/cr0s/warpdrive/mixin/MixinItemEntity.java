package cr0s.warpdrive.mixin;

import cr0s.warpdrive.data.GravityManager;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity
		extends Entity {
	
	public MixinItemEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
	}
	
	@Redirect(at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;" ),
	          method = "tick", remap = false, expect = 1 )
	@Nonnull
	private Vec3d addGravityToMotionOutsideWater(@Nonnull final Vec3d vec3d, final double x, final double y, final double z) {
		final double gravity = GravityManager.getItemGravity((ItemEntity) (Object) this);
		return vec3d.add(x, - gravity, z);
	}
	
	@Redirect(at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/util/math/Vec3d;mul(DDD)Lnet/minecraft/util/math/Vec3d;" ),
	          method = "tick", remap = false, expect = 2 )
	@Nonnull
	private Vec3d mulMotion(@Nonnull final Vec3d vec3d, final double x, final double y, final double z) {
		if (y == 0.98D) {
			final double gravity2 = GravityManager.getItemGravity2((ItemEntity) (Object) this);
			return vec3d.mul(x, gravity2, z);
		} else if (y == -0.5D) {
			return vec3d.mul(x, y, z);
		} else {
			throw new RuntimeException("Invalid injection");
		}
	}
}