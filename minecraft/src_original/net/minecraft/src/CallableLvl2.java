package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLvl2 implements Callable {
	final World theWorld;

	CallableLvl2(World var1) {
		this.theWorld = var1;
	}

	public String getPlayerEntities() {
		return this.theWorld.playerEntities.size() + " total; " + this.theWorld.playerEntities.toString();
	}

	public Object call() {
		return this.getPlayerEntities();
	}
}
