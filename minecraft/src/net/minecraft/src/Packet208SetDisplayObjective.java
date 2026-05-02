package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet208SetDisplayObjective extends Packet {
	public int scoreboardPosition;
	public String scoreName;

	public Packet208SetDisplayObjective() {
	}

	public Packet208SetDisplayObjective(int var1, ScoreObjective var2) {
		this.scoreboardPosition = var1;
		if(var2 == null) {
			this.scoreName = "";
		} else {
			this.scoreName = var2.getName();
		}

	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.scoreboardPosition = var1.readByte();
		this.scoreName = readString(var1, 16);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeByte(this.scoreboardPosition);
		writeString(this.scoreName, var1);
	}

	public void processPacket(NetHandler var1) {
		var1.handleSetDisplayObjective(this);
	}

	public int getPacketSize() {
		return 3 + this.scoreName.length();
	}
}
