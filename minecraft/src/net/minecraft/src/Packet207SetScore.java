package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet207SetScore extends Packet {
	public String itemName = "";
	public String scoreName = "";
	public int value = 0;
	public int updateOrRemove = 0;

	public Packet207SetScore() {
	}

	public Packet207SetScore(Score var1, int var2) {
		this.itemName = var1.func_96653_e();
		this.scoreName = var1.func_96645_d().getName();
		this.value = var1.func_96652_c();
		this.updateOrRemove = var2;
	}

	public Packet207SetScore(String var1) {
		this.itemName = var1;
		this.scoreName = "";
		this.value = 0;
		this.updateOrRemove = 1;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.itemName = readString(var1, 16);
		this.updateOrRemove = var1.readByte();
		if(this.updateOrRemove != 1) {
			this.scoreName = readString(var1, 16);
			this.value = var1.readInt();
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.itemName, var1);
		var1.writeByte(this.updateOrRemove);
		if(this.updateOrRemove != 1) {
			writeString(this.scoreName, var1);
			var1.writeInt(this.value);
		}

	}

	public void processPacket(NetHandler var1) {
		var1.handleSetScore(this);
	}

	public int getPacketSize() {
		return 2 + this.itemName.length() + 2 + this.scoreName.length() + 4 + 1;
	}
}
