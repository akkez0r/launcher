package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLevelDimension implements Callable {
	final WorldInfo worldInfoInstance;

	CallableLevelDimension(WorldInfo var1) {
		this.worldInfoInstance = var1;
	}

	public String callLevelDimension() {
		return String.valueOf(WorldInfo.func_85122_i(this.worldInfoInstance));
	}

	public Object call() {
		return this.callLevelDimension();
	}
}
