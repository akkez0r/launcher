package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet4UpdateTime extends Packet {
	public long worldAge;
	public long time;

	public Packet4UpdateTime() {
	}

	public Packet4UpdateTime(long var1, long var3) {
		this.worldAge = var1;
		this.time = var3;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.worldAge = var1.readLong();
		this.time = var1.readLong();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeLong(this.worldAge);
		var1.writeLong(this.time);
	}

	public void processPacket(NetHandler var1) {
		var1.handleUpdateTime(this);
	}

	public int getPacketSize() {
		return 16;
	}

	public boolean isRealPacket() {
		return true;
	}

	public boolean containsSameEntityIDAs(Packet var1) {
		return true;
	}

	public boolean canProcessAsync() {
		return true;
	}
}
