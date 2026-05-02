package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableServerType implements Callable {
	final DedicatedServer theDedicatedServer;

	CallableServerType(DedicatedServer var1) {
		this.theDedicatedServer = var1;
	}

	public String callServerType() {
		return "Dedicated Server (map_server.txt)";
	}

	public Object call() {
		return this.callServerType();
	}
}
