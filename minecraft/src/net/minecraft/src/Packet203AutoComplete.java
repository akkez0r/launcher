package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet203AutoComplete extends Packet {
	private String text;

	public Packet203AutoComplete() {
	}

	public Packet203AutoComplete(String var1) {
		this.text = var1;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.text = readString(var1, Packet3Chat.maxChatLength);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.text, var1);
	}

	public void processPacket(NetHandler var1) {
		var1.handleAutoComplete(this);
	}

	public int getPacketSize() {
		return 2 + this.text.length() * 2;
	}

	public String getText() {
		return this.text;
	}

	public boolean isRealPacket() {
		return true;
	}

	public boolean containsSameEntityIDAs(Packet var1) {
		return true;
	}
}
