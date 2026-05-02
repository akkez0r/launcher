package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableScreenName implements Callable {
	final EntityRenderer entityRender;

	CallableScreenName(EntityRenderer var1) {
		this.entityRender = var1;
	}

	public String callScreenName() {
		return EntityRenderer.getRendererMinecraft(this.entityRender).currentScreen.getClass().getCanonicalName();
	}

	public Object call() {
		return this.callScreenName();
	}
}
