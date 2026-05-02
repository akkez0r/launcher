package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ThreadMinecraftServer extends Thread {
	final MinecraftServer theServer;

	public ThreadMinecraftServer(MinecraftServer var1, String var2) {
		super(var2);
		this.theServer = var1;
	}

	public void run() {
		this.theServer.run();
	}
}
