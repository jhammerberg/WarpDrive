package cr0s.warpdrive.api;

import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;

public class ParticleStack {
	
	private final Particle particle;
	private int amount;
	private CompoundNBT tagCompound;
	
	public ParticleStack(@Nonnull final Particle particle, final int amount) {
		if (!ParticleRegistry.isParticleRegistered(particle)) {
			LogManager.getLogger().fatal("Failed attempt to create a particleStack for an unregistered Particle {} (type {})",
			                             particle.getRegistryName(), particle.getClass().getName() );
			throw new IllegalArgumentException("Cannot create a particleStack from an unregistered particle");
		}
		this.amount = amount;
		this.particle = particle;
	}
	
	public ParticleStack(final Particle particle, final int amount, final CompoundNBT nbt) {
		this(particle, amount);
		
		if (nbt != null) {
			tagCompound = nbt.copy();
		}
	}
	
	public ParticleStack(final ParticleStack stack, final int amount) {
		this(stack.getParticle(), amount, stack.tagCompound);
	}
	
	/**
	 * Return null if stack is invalid.
	 */
	public static ParticleStack loadFromNBT(final CompoundNBT tagCompound) {
		if (tagCompound == null) {
			return null;
		}
		final String particleName = tagCompound.getString("name");
		
		if (particleName.isEmpty() || ParticleRegistry.getParticle(particleName) == null) {
			return null;
		}
		final ParticleStack stack = new ParticleStack(ParticleRegistry.getParticle(particleName), tagCompound.getInt("amount"));
		
		if (tagCompound.contains("tag")) {
			stack.tagCompound = tagCompound.getCompound("tag");
		}
		return stack;
	}
	
	public CompoundNBT write(@Nonnull final CompoundNBT tagCompound) {
		tagCompound.putString("name", ParticleRegistry.getParticleName(getParticle()));
		tagCompound.putInt("amount", amount);
		
		if (this.tagCompound != null) {
			tagCompound.put("tag", this.tagCompound);
		}
		return tagCompound;
	}
	
	public final Particle getParticle() {
		return particle;
	}
	
	public boolean isEmpty() { return particle == null || amount <= 0; }
	
	public final int getAmount() {
		return amount;
	}
	
	public final void fill(final int amountAdded) {
		amount += amountAdded;
	}
	
	public int getEntityLifespan() {
		if (particle == null) {
			return -1;
		}
		return particle.getEntityLifespan();
	}
	
	public void onWorldEffect(@Nonnull final World world, @Nonnull final Vector3 v3Position) {
		if (particle == null) {
			return;
		}
		particle.onWorldEffect(world, v3Position, amount);
	}
	
	public String getLocalizedName() {
		return this.getParticle().getLocalizedName();
	}
	
	public String getTranslationKey() {
		return this.getParticle().getTranslationKey();
	}
	
	public ParticleStack copy() {
		return new ParticleStack(getParticle(), amount, tagCompound);
	}
	
	public ParticleStack copy(final int amount) {
		return new ParticleStack(getParticle(), amount, tagCompound);
	}
	
	public boolean isParticleEqual(final ParticleStack other) {
		return other != null && getParticle() == other.getParticle() && isParticleStackTagEqual(other);
	}
	
	private boolean isParticleStackTagEqual(final ParticleStack other) {
		return tagCompound == null ? other.tagCompound == null : other.tagCompound != null && tagCompound.equals(other.tagCompound);
	}
	
	public static boolean areParticleStackTagsEqual(final ParticleStack stack1, final ParticleStack stack2) {
		return stack1 == null && stack2 == null || (!(stack1 == null || stack2 == null) && stack1.isParticleStackTagEqual(stack2));
	}
	
	public boolean containsParticle(final ParticleStack other) {
		return isParticleEqual(other) && amount >= other.amount;
	}
	
	public boolean isParticleStackIdentical(final ParticleStack other) {
		return isParticleEqual(other) && amount == other.amount;
	}
	
	public boolean isParticleEqual(final ItemStack other) {
		if (other == null) {
			return false;
		}
		
		if (other.getItem() instanceof IParticleContainerItem) {
			return isParticleEqual(((IParticleContainerItem) other.getItem()).getParticleStack(other));
		}
		
		return false;
	}
	
	@Override
	public final int hashCode() {
		int code = 1;
		code = 31 * code + getParticle().hashCode();
		code = 31 * code + amount;
		if (tagCompound != null) {
			code = 31 * code + tagCompound.hashCode();
		}
		return code;
	}
	
	@Override
	public final boolean equals(final Object object) {
		return object instanceof ParticleStack && isParticleEqual((ParticleStack) object);
	}
}
