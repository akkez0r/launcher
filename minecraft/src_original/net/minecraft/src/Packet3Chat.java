package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet3Chat extends Packet {
	public static int maxChatLength = 119;
	public String message;
	private boolean isServer;

	public Packet3Chat() {
		this.isServer = true;
	}

	public Packet3Chat(String var1) {
		this(var1, true);
	}

	public Packet3Chat(String var1, boolean var2) {
		this.isServer = true;
		if(var1.length() > maxChatLength) {
			var1 = var1.substring(0, maxChatLength);
		}

		this.message = var1;
		this.isServer = var2;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.message = readString(var1, maxChatLength);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.message, var1);
	}

	public void processPacket(NetHandler var1) {
		var1.handleChat(this);
	}

	public int getPacketSize() {
		return 2 + this.message.length() * 2;
	}

	public boolean getIsServer() {
		return this.isServer;
	}

	public boolean canProcessAsync() {
		return !this.message.startsWith("/");
	}
}
