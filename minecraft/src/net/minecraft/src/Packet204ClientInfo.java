package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet204ClientInfo extends Packet {
	private String language;
	private int renderDistance;
	private int chatVisisble;
	private boolean chatColours;
	private int gameDifficulty;
	private boolean showCape;

	public Packet204ClientInfo() {
	}

	public Packet204ClientInfo(String var1, int var2, int var3, boolean var4, int var5, boolean var6) {
		this.language = var1;
		this.renderDistance = var2;
		this.chatVisisble = var3;
		this.chatColours = var4;
		this.gameDifficulty = var5;
		this.showCape = var6;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.language = readString(var1, 7);
		this.renderDistance = var1.readByte();
		byte var2 = var1.readByte();
		this.chatVisisble = var2 & 7;
		this.chatColours = (var2 & 8) == 8;
		this.gameDifficulty = var1.readByte();
		this.showCape = var1.readBoolean();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.language, var1);
		var1.writeByte(this.renderDistance);
		var1.writeByte(this.chatVisisble | (this.chatColours ? 1 : 0) << 3);
		var1.writeByte(this.gameDifficulty);
		var1.writeBoolean(this.showCape);
	}

	public void processPacket(NetHandler var1) {
		var1.handleClientInfo(this);
	}

	public int getPacketSize() {
		return 7;
	}

	public String getLanguage() {
		return this.language;
	}

	public int getRenderDistance() {
		return this.renderDistance;
	}

	public int getChatVisibility() {
		return this.chatVisisble;
	}

	public boolean getChatColours() {
		return this.chatColours;
	}

	public int getDifficulty() {
		return this.gameDifficulty;
	}

	public boolean getShowCape() {
		return this.showCape;
	}

	public boolean isRealPacket() {
		return true;
	}

	public boolean containsSameEntityIDAs(Packet var1) {
		return true;
	}
}
