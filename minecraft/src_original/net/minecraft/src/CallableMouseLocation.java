package net.minecraft.src;

import java.util.concurrent.Callable;
import org.lwjgl.input.Mouse;

class CallableMouseLocation implements Callable {
	final int field_90026_a;
	final int field_90024_b;
	final EntityRenderer theEntityRenderer;

	CallableMouseLocation(EntityRenderer var1, int var2, int var3) {
		this.theEntityRenderer = var1;
		this.field_90026_a = var2;
		this.field_90024_b = var3;
	}

	public String callMouseLocation() {
		return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[]{Integer.valueOf(this.field_90026_a), Integer.valueOf(this.field_90024_b), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
	}

	public Object call() {
		return this.callMouseLocation();
	}
}
