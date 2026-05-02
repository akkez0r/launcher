package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class DedicatedServerCommandThread extends Thread {
	final DedicatedServer server;

	DedicatedServerCommandThread(DedicatedServer var1) {
		this.server = var1;
	}

	public void run() {
		BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));

		try {
			while(!this.server.isServerStopped() && this.server.isServerRunning()) {
				String var2 = var1.readLine();
				if(var2 == null) {
					break;
				}

				this.server.addPendingCommand(var2, this.server);
			}
		} catch (IOException var4) {
			var4.printStackTrace();
		}

	}
}
