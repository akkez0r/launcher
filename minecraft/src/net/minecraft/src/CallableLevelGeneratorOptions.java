package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLevelGeneratorOptions implements Callable {
	final WorldInfo worldInfoInstance;

	CallableLevelGeneratorOptions(WorldInfo var1) {
		this.worldInfoInstance = var1;
	}

	public String callLevelGeneratorOptions() {
		return WorldInfo.getWorldGeneratorOptions(this.worldInfoInstance);
	}

	public Object call() {
		return this.callLevelGeneratorOptions();
	}
}
