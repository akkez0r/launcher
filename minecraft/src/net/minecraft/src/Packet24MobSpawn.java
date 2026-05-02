package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet24MobSpawn extends Packet {
	public int entityId;
	public int type;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int velocityX;
	public int velocityY;
	public int velocityZ;
	public byte yaw;
	public byte pitch;
	public byte headYaw;
	private DataWatcher metaData;
	private List metadata;

	public Packet24MobSpawn() {
	}

	public Packet24MobSpawn(EntityLiving var1) {
		this.entityId = var1.entityId;
		this.type = (byte)EntityList.getEntityID(var1);
		this.xPosition = var1.myEntitySize.multiplyBy32AndRound(var1.posX);
		this.yPosition = MathHelper.floor_double(var1.posY * 32.0D);
		this.zPosition = var1.myEntitySize.multiplyBy32AndRound(var1.posZ);
		this.yaw = (byte)((int)(var1.rotationYaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(var1.rotationPitch * 256.0F / 360.0F));
		this.headYaw = (byte)((int)(var1.rotationYawHead * 256.0F / 360.0F));
		double var2 = 3.9D;
		double var4 = var1.motionX;
		double var6 = var1.motionY;
		double var8 = var1.motionZ;
		if(var4 < -var2) {
			var4 = -var2;
		}

		if(var6 < -var2) {
			var6 = -var2;
		}

		if(var8 < -var2) {
			var8 = -var2;
		}

		if(var4 > var2) {
			var4 = var2;
		}

		if(var6 > var2) {
			var6 = var2;
		}

		if(var8 > var2) {
			var8 = var2;
		}

		this.velocityX = (int)(var4 * 8000.0D);
		this.velocityY = (int)(var6 * 8000.0D);
		this.velocityZ = (int)(var8 * 8000.0D);
		this.metaData = var1.getDataWatcher();
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.entityId = var1.readInt();
		this.type = var1.readByte() & 255;
		this.xPosition = var1.readInt();
		this.yPosition = var1.readInt();
		this.zPosition = var1.readInt();
		this.yaw = var1.readByte();
		this.pitch = var1.readByte();
		this.headYaw = var1.readByte();
		this.velocityX = var1.readShort();
		this.velocityY = var1.readShort();
		this.velocityZ = var1.readShort();
		this.metadata = DataWatcher.readWatchableObjects(var1);
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeInt(this.entityId);
		var1.writeByte(this.type & 255);
		var1.writeInt(this.xPosition);
		var1.writeInt(this.yPosition);
		var1.writeInt(this.zPosition);
		var1.writeByte(this.yaw);
		var1.writeByte(this.pitch);
		var1.writeByte(this.headYaw);
		var1.writeShort(this.velocityX);
		var1.writeShort(this.velocityY);
		var1.writeShort(this.velocityZ);
		this.metaData.writeWatchableObjects(var1);
	}

	public void processPacket(NetHandler var1) {
		var1.handleMobSpawn(this);
	}

	public int getPacketSize() {
		return 26;
	}

	public List getMetadata() {
		if(this.metadata == null) {
			this.metadata = this.metaData.getAllWatched();
		}

		return this.metadata;
	}
}
