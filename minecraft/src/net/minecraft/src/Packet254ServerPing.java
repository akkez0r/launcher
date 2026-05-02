package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet254ServerPing extends Packet {
	public int readSuccessfully = 0;

	public void readPacketData(DataInputStream var1) throws IOException {
		try {
			this.readSuccessfully = var1.readByte();
		} catch (Throwable var3) {
			this.readSuccessfully = 0;
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
	}

	public void processPacket(NetHandler var1) {
		var1.handleServerPing(this);
	}

	public int getPacketSize() {
		return 0;
	}
}
