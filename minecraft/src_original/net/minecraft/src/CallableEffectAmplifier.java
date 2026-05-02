package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEffectAmplifier implements Callable {
	final PotionEffect field_102040_a;
	final EntityLiving field_102039_b;

	CallableEffectAmplifier(EntityLiving var1, PotionEffect var2) {
		this.field_102039_b = var1;
		this.field_102040_a = var2;
	}

	public String func_102038_a() {
		return this.field_102040_a.getAmplifier() + "";
	}

	public Object call() {
		return this.func_102038_a();
	}
}
