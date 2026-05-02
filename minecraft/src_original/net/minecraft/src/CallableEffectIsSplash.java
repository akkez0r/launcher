package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEffectIsSplash implements Callable {
	final PotionEffect field_102043_a;
	final EntityLiving field_102042_b;

	CallableEffectIsSplash(EntityLiving var1, PotionEffect var2) {
		this.field_102042_b = var1;
		this.field_102043_a = var2;
	}

	public String func_102041_a() {
		return this.field_102043_a.isSplashPotionEffect() + "";
	}

	public Object call() {
		return this.func_102041_a();
	}
}
