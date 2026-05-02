package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEntityTracker implements Callable {
	final int field_96570_a;
	final EntityTracker theEntityTracker;

	CallableEntityTracker(EntityTracker var1, int var2) {
		this.theEntityTracker = var1;
		this.field_96570_a = var2;
	}

	public String func_96568_a() {
		String var1 = "Once per " + this.field_96570_a + " ticks";
		if(this.field_96570_a == Integer.MAX_VALUE) {
			var1 = "Maximum (" + var1 + ")";
		}

		return var1;
	}

	public Object call() {
		return this.func_96568_a();
	}
}
