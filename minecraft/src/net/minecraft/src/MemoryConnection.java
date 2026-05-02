package net.minecraft.src;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryConnection implements INetworkManager {
	private static final SocketAddress mySocketAddress = new InetSocketAddress("127.0.0.1", 0);
	private final List readPacketCache = Collections.synchronizedList(new ArrayList());
	private final ILogAgent field_98214_c;
	private MemoryConnection pairedConnection;
	private NetHandler myNetHandler;
	private boolean shuttingDown = false;
	private String shutdownReason = "";
	private Object[] field_74439_g;
	private boolean gamePaused = false;

	public MemoryConnection(ILogAgent var1, NetHandler var2) {
		this.myNetHandler = var2;
		this.field_98214_c = var1;
	}

	public void setNetHandler(NetHandler var1) {
		this.myNetHandler = var1;
	}

	public void addToSendQueue(Packet var1) {
		if(!this.shuttingDown) {
			this.pairedConnection.processOrCachePacket(var1);
		}
	}

	public void closeConnections() {
		this.pairedConnection = null;
		this.myNetHandler = null;
	}

	public boolean isConnectionActive() {
		return !this.shuttingDown && this.pairedConnection != null;
	}

	public void wakeThreads() {
	}

	public void processReadPackets() {
		int var1 = 2500;

		while(var1-- >= 0 && !this.readPacketCache.isEmpty()) {
			Packet var2 = (Packet)this.readPacketCache.remove(0);
			var2.processPacket(this.myNetHandler);
		}

		if(this.readPacketCache.size() > var1) {
			this.field_98214_c.logWarning("Memory connection overburdened; after processing 2500 packets, we still have " + this.readPacketCache.size() + " to go!");
		}

		if(this.shuttingDown && this.readPacketCache.isEmpty()) {
			this.myNetHandler.handleErrorMessage(this.shutdownReason, this.field_74439_g);
		}

	}

	public SocketAddress getSocketAddress() {
		return mySocketAddress;
	}

	public void serverShutdown() {
		this.shuttingDown = true;
	}

	public void networkShutdown(String var1, Object... var2) {
		this.shuttingDown = true;
		this.shutdownReason = var1;
		this.field_74439_g = var2;
	}

	public int packetSize() {
		return 0;
	}

	public void pairWith(MemoryConnection var1) {
		this.pairedConnection = var1;
		var1.pairedConnection = this;
	}

	public boolean isGamePaused() {
		return this.gamePaused;
	}

	public void setGamePaused(boolean var1) {
		this.gamePaused = var1;
	}

	public MemoryConnection getPairedConnection() {
		return this.pairedConnection;
	}

	public void processOrCachePacket(Packet var1) {
		if(var1.canProcessAsync() && this.myNetHandler.canProcessPacketsAsync()) {
			var1.processPacket(this.myNetHandler);
		} else {
			this.readPacketCache.add(var1);
		}

	}
}
