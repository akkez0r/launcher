package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEffectIsAmbient implements Callable {
	final PotionEffect field_102046_a;
	final EntityLiving field_102045_b;

	CallableEffectIsAmbient(EntityLiving var1, PotionEffect var2) {
		this.field_102045_b = var1;
		this.field_102046_a = var2;
	}

	public String func_102044_a() {
		return this.field_102046_a.getIsAmbient() + "";
	}

	public Object call() {
		return this.func_102044_a();
	}
}
