package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet100OpenWindow extends Packet {
	public int windowId;
	public int inventoryType;
	public String windowTitle;
	public int slotsCount;
	public boolean useProvidedWindowTitle;

	public Packet100OpenWindow() {
	}

	public Packet100OpenWindow(int var1, int var2, String var3, int var4, boolean var5) {
		this.windowId = var1;
		this.inventoryType = var2;
		this.windowTitle = var3;
		this.slotsCount = var4;
		this.useProvidedWindowTitle = var5;
	}

	public void processPacket(NetHandler var1) {
		var1.handleOpenWindow(this);
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.windowId = var1.readByte() & 255;
		this.inventoryType = var1.readByte() & 255;
		this.windowTitle = readString(var1, 32);
		this.slotsCount = var1.readByte() & 255;
		this.useProvidedWindowTitle = var1.readBoolean();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeByte(this.windowId & 255);
		var1.writeByte(this.inventoryType & 255);
		writeString(this.windowTitle, var1);
		var1.writeByte(this.slotsCount & 255);
		var1.writeBoolean(this.useProvidedWindowTitle);
	}

	public int getPacketSize() {
		return 4 + this.windowTitle.length();
	}
}
