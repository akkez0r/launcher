package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2ClientProtocol extends Packet {
	private static final int CMC_ACCESS_TOKEN_MAX_LEN = 4096;

	private int protocolVersion;
	private String username;
	private String serverHost;
	private int serverPort;
	private String cmcAccessToken = "";

	public Packet2ClientProtocol() {
	}

	public Packet2ClientProtocol(int var1, String var2, String var3, int var4) {
		this(var1, var2, var3, var4, "");
	}

	public Packet2ClientProtocol(int var1, String var2, String var3, int var4, String var5) {
		this.protocolVersion = var1;
		this.username = var2;
		this.serverHost = var3;
		this.serverPort = var4;
		this.cmcAccessToken = var5 != null ? var5 : "";
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.protocolVersion = var1.readByte();
		this.username = readString(var1, 16);
		this.serverHost = readString(var1, 255);
		this.serverPort = var1.readInt();
		this.cmcAccessToken = readString(var1, CMC_ACCESS_TOKEN_MAX_LEN);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeByte(this.protocolVersion);
		writeString(this.username, var1);
		writeString(this.serverHost, var1);
		var1.writeInt(this.serverPort);
		writeString(this.cmcAccessToken, var1);
	}

	public void processPacket(NetHandler var1) {
		var1.handleClientProtocol(this);
	}

	public int getPacketSize() {
		return 1 + (2 + this.username.length() * 2) + (2 + this.serverHost.length() * 2) + 4
			+ (2 + this.cmcAccessToken.length() * 2);
	}

	public int getProtocolVersion() {
		return this.protocolVersion;
	}

	public String getUsername() {
		return this.username;
	}

	public String getCmcAccessToken() {
		return this.cmcAccessToken;
	}
}
