package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Packet {
	public static IntHashMap packetIdToClassMap = new IntHashMap();
	private static Map packetClassToIdMap = new HashMap();
	private static Set clientPacketIdList = new HashSet();
	private static Set serverPacketIdList = new HashSet();
	protected ILogAgent field_98193_m;
	public final long creationTimeMillis = System.currentTimeMillis();
	public static long receivedID;
	public static long receivedSize;
	public static long sentID;
	public static long sentSize;
	public boolean isChunkDataPacket = false;

	static void addIdClassMapping(int var0, boolean var1, boolean var2, Class var3) {
		if(packetIdToClassMap.containsItem(var0)) {
			throw new IllegalArgumentException("Duplicate packet id:" + var0);
		} else if(packetClassToIdMap.containsKey(var3)) {
			throw new IllegalArgumentException("Duplicate packet class:" + var3);
		} else {
			packetIdToClassMap.addKey(var0, var3);
			packetClassToIdMap.put(var3, Integer.valueOf(var0));
			if(var1) {
				clientPacketIdList.add(Integer.valueOf(var0));
			}

			if(var2) {
				serverPacketIdList.add(Integer.valueOf(var0));
			}

		}
	}

	public static Packet getNewPacket(ILogAgent var0, int var1) {
		try {
			Class var2 = (Class)packetIdToClassMap.lookup(var1);
			return var2 == null ? null : (Packet)var2.newInstance();
		} catch (Exception var3) {
			var3.printStackTrace();
			var0.logSevere("Skipping packet with id " + var1);
			return null;
		}
	}

	public static void writeByteArray(DataOutputStream var0, byte[] var1) throws IOException {
		var0.writeShort(var1.length);
		var0.write(var1);
	}

	public static byte[] readBytesFromStream(DataInputStream var0) throws IOException {
		short var1 = var0.readShort();
		if(var1 < 0) {
			throw new IOException("Key was smaller than nothing!  Weird key!");
		} else {
			byte[] var2 = new byte[var1];
			var0.readFully(var2);
			return var2;
		}
	}

	public final int getPacketId() {
		return ((Integer)packetClassToIdMap.get(this.getClass())).intValue();
	}

	public static Packet readPacket(ILogAgent var0, DataInputStream var1, boolean var2, Socket var3) throws IOException {
		boolean var4 = false;
		Packet var5 = null;
		int var6 = var3.getSoTimeout();

		int var9;
		try {
			var9 = var1.read();
			if(var9 == -1) {
				return null;
			}

			if(var2 && !serverPacketIdList.contains(Integer.valueOf(var9)) || !var2 && !clientPacketIdList.contains(Integer.valueOf(var9))) {
				throw new IOException("Bad packet id " + var9);
			}

			var5 = getNewPacket(var0, var9);
			if(var5 == null) {
				throw new IOException("Bad packet id " + var9);
			}

			var5.field_98193_m = var0;
			if(var5 instanceof Packet254ServerPing) {
				var3.setSoTimeout(1500);
			}

			var5.readPacketData(var1);
			++receivedID;
			receivedSize += (long)var5.getPacketSize();
		} catch (EOFException var8) {
			var0.logSevere("Reached end of stream");
			return null;
		}

		PacketCount.countPacket(var9, (long)var5.getPacketSize());
		++receivedID;
		receivedSize += (long)var5.getPacketSize();
		var3.setSoTimeout(var6);
		return var5;
	}

	public static void writePacket(Packet var0, DataOutputStream var1) throws IOException {
		var1.write(var0.getPacketId());
		var0.writePacketData(var1);
		++sentID;
		sentSize += (long)var0.getPacketSize();
	}

	public static void writeString(String var0, DataOutputStream var1) throws IOException {
		if(var0.length() > Short.MAX_VALUE) {
			throw new IOException("String too big");
		} else {
			var1.writeShort(var0.length());
			var1.writeChars(var0);
		}
	}

	public static String readString(DataInputStream var0, int var1) throws IOException {
		short var2 = var0.readShort();
		if(var2 > var1) {
			throw new IOException("Received string length longer than maximum allowed (" + var2 + " > " + var1 + ")");
		} else if(var2 < 0) {
			throw new IOException("Received string length is less than zero! Weird string!");
		} else {
			StringBuilder var3 = new StringBuilder();

			for(int var4 = 0; var4 < var2; ++var4) {
				var3.append(var0.readChar());
			}

			return var3.toString();
		}
	}

	public abstract void readPacketData(DataInputStream var1) throws IOException;

	public abstract void writePacketData(DataOutputStream var1) throws IOException;

	public abstract void processPacket(NetHandler var1);

	public abstract int getPacketSize();

	public boolean isRealPacket() {
		return false;
	}

	public boolean containsSameEntityIDAs(Packet var1) {
		return false;
	}

	public boolean canProcessAsync() {
		return false;
	}

	public String toString() {
		String var1 = this.getClass().getSimpleName();
		return var1;
	}

	public static ItemStack readItemStack(DataInputStream var0) throws IOException {
		ItemStack var1 = null;
		short var2 = var0.readShort();
		if(var2 >= 0) {
			byte var3 = var0.readByte();
			short var4 = var0.readShort();
			var1 = new ItemStack(var2, var3, var4);
			var1.stackTagCompound = readNBTTagCompound(var0);
		}

		return var1;
	}

	public static void writeItemStack(ItemStack var0, DataOutputStream var1) throws IOException {
		if(var0 == null) {
			var1.writeShort(-1);
		} else {
			var1.writeShort(var0.itemID);
			var1.writeByte(var0.stackSize);
			var1.writeShort(var0.getItemDamage());
			NBTTagCompound var2 = null;
			if(var0.getItem().isDamageable() || var0.getItem().getShareTag()) {
				var2 = var0.stackTagCompound;
			}

			writeNBTTagCompound(var2, var1);
		}

	}

	public static NBTTagCompound readNBTTagCompound(DataInputStream var0) throws IOException {
		short var1 = var0.readShort();
		if(var1 < 0) {
			return null;
		} else {
			byte[] var2 = new byte[var1];
			var0.readFully(var2);
			return CompressedStreamTools.decompress(var2);
		}
	}

	protected static void writeNBTTagCompound(NBTTagCompound var0, DataOutputStream var1) throws IOException {
		if(var0 == null) {
			var1.writeShort(-1);
		} else {
			byte[] var2 = CompressedStreamTools.compress(var0);
			var1.writeShort((short)var2.length);
			var1.write(var2);
		}

	}

	static {
		addIdClassMapping(0, true, true, Packet0KeepAlive.class);
		addIdClassMapping(1, true, true, Packet1Login.class);
		addIdClassMapping(2, false, true, Packet2ClientProtocol.class);
		addIdClassMapping(3, true, true, Packet3Chat.class);
		addIdClassMapping(4, true, false, Packet4UpdateTime.class);
		addIdClassMapping(5, true, false, Packet5PlayerInventory.class);
		addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
		addIdClassMapping(7, false, true, Packet7UseEntity.class);
		addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
		addIdClassMapping(9, true, true, Packet9Respawn.class);
		addIdClassMapping(10, true, true, Packet10Flying.class);
		addIdClassMapping(11, true, true, Packet11PlayerPosition.class);
		addIdClassMapping(12, true, true, Packet12PlayerLook.class);
		addIdClassMapping(13, true, true, Packet13PlayerLookMove.class);
		addIdClassMapping(14, false, true, Packet14BlockDig.class);
		addIdClassMapping(15, false, true, Packet15Place.class);
		addIdClassMapping(16, true, true, Packet16BlockItemSwitch.class);
		addIdClassMapping(17, true, false, Packet17Sleep.class);
		addIdClassMapping(18, true, true, Packet18Animation.class);
		addIdClassMapping(19, false, true, Packet19EntityAction.class);
		addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
		addIdClassMapping(22, true, false, Packet22Collect.class);
		addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
		addIdClassMapping(24, true, false, Packet24MobSpawn.class);
		addIdClassMapping(25, true, false, Packet25EntityPainting.class);
		addIdClassMapping(26, true, false, Packet26EntityExpOrb.class);
		addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
		addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
		addIdClassMapping(30, true, false, Packet30Entity.class);
		addIdClassMapping(31, true, false, Packet31RelEntityMove.class);
		addIdClassMapping(32, true, false, Packet32EntityLook.class);
		addIdClassMapping(33, true, false, Packet33RelEntityMoveLook.class);
		addIdClassMapping(34, true, false, Packet34EntityTeleport.class);
		addIdClassMapping(35, true, false, Packet35EntityHeadRotation.class);
		addIdClassMapping(38, true, false, Packet38EntityStatus.class);
		addIdClassMapping(39, true, false, Packet39AttachEntity.class);
		addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
		addIdClassMapping(41, true, false, Packet41EntityEffect.class);
		addIdClassMapping(42, true, false, Packet42RemoveEntityEffect.class);
		addIdClassMapping(43, true, false, Packet43Experience.class);
		addIdClassMapping(51, true, false, Packet51MapChunk.class);
		addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
		addIdClassMapping(53, true, false, Packet53BlockChange.class);
		addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
		addIdClassMapping(55, true, false, Packet55BlockDestroy.class);
		addIdClassMapping(56, true, false, Packet56MapChunks.class);
		addIdClassMapping(60, true, false, Packet60Explosion.class);
		addIdClassMapping(61, true, false, Packet61DoorChange.class);
		addIdClassMapping(62, true, false, Packet62LevelSound.class);
		addIdClassMapping(63, true, false, Packet63WorldParticles.class);
		addIdClassMapping(70, true, false, Packet70GameEvent.class);
		addIdClassMapping(71, true, false, Packet71Weather.class);
		addIdClassMapping(100, true, false, Packet100OpenWindow.class);
		addIdClassMapping(101, true, true, Packet101CloseWindow.class);
		addIdClassMapping(102, false, true, Packet102WindowClick.class);
		addIdClassMapping(103, true, false, Packet103SetSlot.class);
		addIdClassMapping(104, true, false, Packet104WindowItems.class);
		addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
		addIdClassMapping(106, true, true, Packet106Transaction.class);
		addIdClassMapping(107, true, true, Packet107CreativeSetSlot.class);
		addIdClassMapping(108, false, true, Packet108EnchantItem.class);
		addIdClassMapping(130, true, true, Packet130UpdateSign.class);
		addIdClassMapping(131, true, false, Packet131MapData.class);
		addIdClassMapping(132, true, false, Packet132TileEntityData.class);
		addIdClassMapping(200, true, false, Packet200Statistic.class);
		addIdClassMapping(201, true, false, Packet201PlayerInfo.class);
		addIdClassMapping(202, true, true, Packet202PlayerAbilities.class);
		addIdClassMapping(203, true, true, Packet203AutoComplete.class);
		addIdClassMapping(204, false, true, Packet204ClientInfo.class);
		addIdClassMapping(205, false, true, Packet205ClientCommand.class);
		addIdClassMapping(206, true, false, Packet206SetObjective.class);
		addIdClassMapping(207, true, false, Packet207SetScore.class);
		addIdClassMapping(208, true, false, Packet208SetDisplayObjective.class);
		addIdClassMapping(209, true, false, Packet209SetPlayerTeam.class);
		addIdClassMapping(250, true, true, Packet250CustomPayload.class);
		addIdClassMapping(252, true, true, Packet252SharedKey.class);
		addIdClassMapping(253, true, false, Packet253ServerAuthData.class);
		addIdClassMapping(254, false, true, Packet254ServerPing.class);
		addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
	}
}
