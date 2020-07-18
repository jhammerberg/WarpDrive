package cr0s.warpdrive.api;

import net.minecraft.entity.LivingEntity;

public interface IBreathingHelmet {
	
	// Called when checking armors, before checking for air containers
	boolean canBreath(LivingEntity entityLivingBase);
} 