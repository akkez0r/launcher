package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet15Place extends Packet {
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private int direction;
	private ItemStack itemStack;
	private float xOffset;
	private float yOffset;
	private float zOffset;

	public Packet15Place() {
	}

	public Packet15Place(int var1, int var2, int var3, int var4, ItemStack var5, float var6, float var7, float var8) {
		this.xPosition = var1;
		this.yPosition = var2;
		this.zPosition = var3;
		this.direction = var4;
		this.itemStack = var5 != null ? var5.copy() : null;
		this.xOffset = var6;
		this.yOffset = var7;
		this.zOffset = var8;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.xPosition = var1.readInt();
		this.yPosition = var1.read();
		this.zPosition = var1.readInt();
		this.direction = var1.read();
		this.itemStack = readItemStack(var1);
		this.xOffset = (float)var1.read() / 16.0F;
		this.yOffset = (float)var1.read() / 16.0F;
		this.zOffset = (float)var1.read() / 16.0F;
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeInt(this.xPosition);
		var1.write(this.yPosition);
		var1.writeInt(this.zPosition);
		var1.write(this.direction);
		writeItemStack(this.itemStack, var1);
		var1.write((int)(this.xOffset * 16.0F));
		var1.write((int)(this.yOffset * 16.0F));
		var1.write((int)(this.zOffset * 16.0F));
	}

	public void processPacket(NetHandler var1) {
		var1.handlePlace(this);
	}

	public int getPacketSize() {
		return 19;
	}

	public int getXPosition() {
		return this.xPosition;
	}

	public int getYPosition() {
		return this.yPosition;
	}

	public int getZPosition() {
		return this.zPosition;
	}

	public int getDirection() {
		return this.direction;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public float getXOffset() {
		return this.xOffset;
	}

	public float getYOffset() {
		return this.yOffset;
	}

	public float getZOffset() {
		return this.zOffset;
	}
}
