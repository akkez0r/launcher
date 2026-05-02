package net.minecraft.src;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class CallableClientProfiler implements Callable {
	final Minecraft theMinecraft;

	public CallableClientProfiler(Minecraft var1) {
		this.theMinecraft = var1;
	}

	public String callClientProfilerInfo() {
		return this.theMinecraft.mcProfiler.profilingEnabled ? this.theMinecraft.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
	}

	public Object call() {
		return this.callClientProfilerInfo();
	}
}
