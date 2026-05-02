package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableType3 implements Callable {
	final IntegratedServer theIntegratedServer;

	CallableType3(IntegratedServer var1) {
		this.theIntegratedServer = var1;
	}

	public String getType() {
		return "Integrated Server (map_client.txt)";
	}

	public Object call() {
		return this.getType();
	}
}
