package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {
	public int xCh;
	public int zCh;
	public int yChMin;
	public int yChMax;
	private byte[] chunkData;
	private byte[] compressedChunkData;
	public boolean includeInitialize;
	private int tempLength;
	private static byte[] temp = new byte[196864];

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(Chunk var1, boolean var2, int var3) {
		this.isChunkDataPacket = true;
		this.xCh = var1.xPosition;
		this.zCh = var1.zPosition;
		this.includeInitialize = var2;
		Packet51MapChunkData var4 = getMapChunkData(var1, var2, var3);
		Deflater var5 = new Deflater(-1);
		this.yChMax = var4.chunkHasAddSectionFlag;
		this.yChMin = var4.chunkExistFlag;

		try {
			this.compressedChunkData = var4.compressedData;
			var5.setInput(var4.compressedData, 0, var4.compressedData.length);
			var5.finish();
			this.chunkData = new byte[var4.compressedData.length];
			this.tempLength = var5.deflate(this.chunkData);
		} finally {
			var5.end();
		}

	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.xCh = var1.readInt();
		this.zCh = var1.readInt();
		this.includeInitialize = var1.readBoolean();
		this.yChMin = var1.readShort();
		this.yChMax = var1.readShort();
		this.tempLength = var1.readInt();
		if(temp.length < this.tempLength) {
			temp = new byte[this.tempLength];
		}

		var1.readFully(temp, 0, this.tempLength);
		int var2 = 0;

		int var3;
		for(var3 = 0; var3 < 16; ++var3) {
			var2 += this.yChMin >> var3 & 1;
		}

		var3 = 12288 * var2;
		if(this.includeInitialize) {
			var3 += 256;
		}

		this.compressedChunkData = new byte[var3];
		Inflater var4 = new Inflater();
		var4.setInput(temp, 0, this.tempLength);

		try {
			var4.inflate(this.compressedChunkData);
		} catch (DataFormatException var9) {
			throw new IOException("Bad compressed data format");
		} finally {
			var4.end();
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeInt(this.xCh);
		var1.writeInt(this.zCh);
		var1.writeBoolean(this.includeInitialize);
		var1.writeShort((short)(this.yChMin & '\uffff'));
		var1.writeShort((short)(this.yChMax & '\uffff'));
		var1.writeInt(this.tempLength);
		var1.write(this.chunkData, 0, this.tempLength);
	}

	public void processPacket(NetHandler var1) {
		var1.handleMapChunk(this);
	}

	public int getPacketSize() {
		return 17 + this.tempLength;
	}

	public byte[] getCompressedChunkData() {
		return this.compressedChunkData;
	}

	public static Packet51MapChunkData getMapChunkData(Chunk var0, boolean var1, int var2) {
		int var3 = 0;
		ExtendedBlockStorage[] var4 = var0.getBlockStorageArray();
		int var5 = 0;
		Packet51MapChunkData var6 = new Packet51MapChunkData();
		byte[] var7 = temp;
		if(var1) {
			var0.sendUpdates = true;
		}

		int var8;
		for(var8 = 0; var8 < var4.length; ++var8) {
			if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && (var2 & 1 << var8) != 0) {
				var6.chunkExistFlag |= 1 << var8;
				if(var4[var8].getBlockMSBArray() != null) {
					var6.chunkHasAddSectionFlag |= 1 << var8;
					++var5;
				}
			}
		}

		for(var8 = 0; var8 < var4.length; ++var8) {
			if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && (var2 & 1 << var8) != 0) {
				byte[] var9 = var4[var8].getBlockLSBArray();
				System.arraycopy(var9, 0, var7, var3, var9.length);
				var3 += var9.length;
			}
		}

		NibbleArray var10;
		for(var8 = 0; var8 < var4.length; ++var8) {
			if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && (var2 & 1 << var8) != 0) {
				var10 = var4[var8].getMetadataArray();
				System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
				var3 += var10.data.length;
			}
		}

		for(var8 = 0; var8 < var4.length; ++var8) {
			if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && (var2 & 1 << var8) != 0) {
				var10 = var4[var8].getBlocklightArray();
				System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
				var3 += var10.data.length;
			}
		}

		if(!var0.worldObj.provider.hasNoSky) {
			for(var8 = 0; var8 < var4.length; ++var8) {
				if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && (var2 & 1 << var8) != 0) {
					var10 = var4[var8].getSkylightArray();
					System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
					var3 += var10.data.length;
				}
			}
		}

		if(var5 > 0) {
			for(var8 = 0; var8 < var4.length; ++var8) {
				if(var4[var8] != null && (!var1 || !var4[var8].isEmpty()) && var4[var8].getBlockMSBArray() != null && (var2 & 1 << var8) != 0) {
					var10 = var4[var8].getBlockMSBArray();
					System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
					var3 += var10.data.length;
				}
			}
		}

		if(var1) {
			byte[] var11 = var0.getBiomeArray();
			System.arraycopy(var11, 0, var7, var3, var11.length);
			var3 += var11.length;
		}

		var6.compressedData = new byte[var3];
		System.arraycopy(var7, 0, var6.compressedData, 0, var3);
		return var6;
	}
}
