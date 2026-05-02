package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet {
	public int healthMP;
	public int food;
	public float foodSaturation;

	public Packet8UpdateHealth() {
	}

	public Packet8UpdateHealth(int var1, int var2, float var3) {
		this.healthMP = var1;
		this.food = var2;
		this.foodSaturation = var3;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.healthMP = var1.readShort();
		this.food = var1.readShort();
		this.foodSaturation = var1.readFloat();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeShort(this.healthMP);
		var1.writeShort(this.food);
		var1.writeFloat(this.foodSaturation);
	}

	public void processPacket(NetHandler var1) {
		var1.handleUpdateHealth(this);
	}

	public int getPacketSize() {
		return 8;
	}

	public boolean isRealPacket() {
		return true;
	}

	public boolean containsSameEntityIDAs(Packet var1) {
		return true;
	}
}
