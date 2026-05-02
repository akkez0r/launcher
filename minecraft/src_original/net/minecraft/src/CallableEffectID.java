package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEffectID implements Callable {
	final PotionEffect field_102034_a;
	final EntityLiving field_102033_b;

	CallableEffectID(EntityLiving var1, PotionEffect var2) {
		this.field_102033_b = var1;
		this.field_102034_a = var2;
	}

	public String func_102032_a() {
		return this.field_102034_a.getPotionID() + "";
	}

	public Object call() {
		return this.func_102032_a();
	}
}
