package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet103SetSlot extends Packet {
	public int windowId;
	public int itemSlot;
	public ItemStack myItemStack;

	public Packet103SetSlot() {
	}

	public Packet103SetSlot(int var1, int var2, ItemStack var3) {
		this.windowId = var1;
		this.itemSlot = var2;
		this.myItemStack = var3 == null ? var3 : var3.copy();
	}

	public void processPacket(NetHandler var1) {
		var1.handleSetSlot(this);
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.windowId = var1.readByte();
		this.itemSlot = var1.readShort();
		this.myItemStack = readItemStack(var1);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeByte(this.windowId);
		var1.writeShort(this.itemSlot);
		writeItemStack(this.myItemStack, var1);
	}

	public int getPacketSize() {
		return 8;
	}
}
