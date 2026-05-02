package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableMinecraftVersion implements Callable {
	final CrashReport theCrashReport;

	CallableMinecraftVersion(CrashReport var1) {
		this.theCrashReport = var1;
	}

	public String minecraftVersion() {
		return "1.5.2";
	}

	public Object call() {
		return this.minecraftVersion();
	}
}
