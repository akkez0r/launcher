package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapData extends WorldSavedData {
	public int xCenter;
	public int zCenter;
	public byte dimension;
	public byte scale;
	public byte[] colors = new byte[16384];
	public List playersArrayList = new ArrayList();
	private Map playersHashMap = new HashMap();
	public Map playersVisibleOnMap = new LinkedHashMap();

	public MapData(String var1) {
		super(var1);
	}

	public void readFromNBT(NBTTagCompound var1) {
		this.dimension = var1.getByte("dimension");
		this.xCenter = var1.getInteger("xCenter");
		this.zCenter = var1.getInteger("zCenter");
		this.scale = var1.getByte("scale");
		if(this.scale < 0) {
			this.scale = 0;
		}

		if(this.scale > 4) {
			this.scale = 4;
		}

		short var2 = var1.getShort("width");
		short var3 = var1.getShort("height");
		if(var2 == 128 && var3 == 128) {
			this.colors = var1.getByteArray("colors");
		} else {
			byte[] var4 = var1.getByteArray("colors");
			this.colors = new byte[16384];
			int var5 = (128 - var2) / 2;
			int var6 = (128 - var3) / 2;

			for(int var7 = 0; var7 < var3; ++var7) {
				int var8 = var7 + var6;
				if(var8 >= 0 || var8 < 128) {
					for(int var9 = 0; var9 < var2; ++var9) {
						int var10 = var9 + var5;
						if(var10 >= 0 || var10 < 128) {
							this.colors[var10 + var8 * 128] = var4[var9 + var7 * var2];
						}
					}
				}
			}
		}

	}

	public void writeToNBT(NBTTagCompound var1) {
		var1.setByte("dimension", this.dimension);
		var1.setInteger("xCenter", this.xCenter);
		var1.setInteger("zCenter", this.zCenter);
		var1.setByte("scale", this.scale);
		var1.setShort("width", (short)128);
		var1.setShort("height", (short)128);
		var1.setByteArray("colors", this.colors);
	}

	public void updateVisiblePlayers(EntityPlayer var1, ItemStack var2) {
		if(!this.playersHashMap.containsKey(var1)) {
			MapInfo var3 = new MapInfo(this, var1);
			this.playersHashMap.put(var1, var3);
			this.playersArrayList.add(var3);
		}

		if(!var1.inventory.hasItemStack(var2)) {
			this.playersVisibleOnMap.remove(var1.getCommandSenderName());
		}

		for(int var5 = 0; var5 < this.playersArrayList.size(); ++var5) {
			MapInfo var4 = (MapInfo)this.playersArrayList.get(var5);
			if(!var4.entityplayerObj.isDead && (var4.entityplayerObj.inventory.hasItemStack(var2) || var2.isOnItemFrame())) {
				if(!var2.isOnItemFrame() && var4.entityplayerObj.dimension == this.dimension) {
					this.func_82567_a(0, var4.entityplayerObj.worldObj, var4.entityplayerObj.getCommandSenderName(), var4.entityplayerObj.posX, var4.entityplayerObj.posZ, (double)var4.entityplayerObj.rotationYaw);
				}
			} else {
				this.playersHashMap.remove(var4.entityplayerObj);
				this.playersArrayList.remove(var4);
			}
		}

		if(var2.isOnItemFrame()) {
			this.func_82567_a(1, var1.worldObj, "frame-" + var2.getItemFrame().entityId, (double)var2.getItemFrame().xPosition, (double)var2.getItemFrame().zPosition, (double)(var2.getItemFrame().hangingDirection * 90));
		}

	}

	private void func_82567_a(int var1, World var2, String var3, double var4, double var6, double var8) {
		int var10 = 1 << this.scale;
		float var11 = (float)(var4 - (double)this.xCenter) / (float)var10;
		float var12 = (float)(var6 - (double)this.zCenter) / (float)var10;
		byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5D));
		byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
		byte var16 = 63;
		byte var15;
		if(var11 >= (float)(-var16) && var12 >= (float)(-var16) && var11 <= (float)var16 && var12 <= (float)var16) {
			var8 += var8 < 0.0D ? -8.0D : 8.0D;
			var15 = (byte)((int)(var8 * 16.0D / 360.0D));
			if(this.dimension < 0) {
				int var17 = (int)(var2.getWorldInfo().getWorldTime() / 10L);
				var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
			}
		} else {
			if(Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F) {
				this.playersVisibleOnMap.remove(var3);
				return;
			}

			var1 = 6;
			var15 = 0;
			if(var11 <= (float)(-var16)) {
				var13 = (byte)((int)((double)(var16 * 2) + 2.5D));
			}

			if(var12 <= (float)(-var16)) {
				var14 = (byte)((int)((double)(var16 * 2) + 2.5D));
			}

			if(var11 >= (float)var16) {
				var13 = (byte)(var16 * 2 + 1);
			}

			if(var12 >= (float)var16) {
				var14 = (byte)(var16 * 2 + 1);
			}
		}

		this.playersVisibleOnMap.put(var3, new MapCoord(this, (byte)var1, var13, var14, var15));
	}

	public byte[] getUpdatePacketData(ItemStack var1, World var2, EntityPlayer var3) {
		MapInfo var4 = (MapInfo)this.playersHashMap.get(var3);
		return var4 == null ? null : var4.getPlayersOnMap(var1);
	}

	public void setColumnDirty(int var1, int var2, int var3) {
		super.markDirty();

		for(int var4 = 0; var4 < this.playersArrayList.size(); ++var4) {
			MapInfo var5 = (MapInfo)this.playersArrayList.get(var4);
			if(var5.field_76209_b[var1] < 0 || var5.field_76209_b[var1] > var2) {
				var5.field_76209_b[var1] = var2;
			}

			if(var5.field_76210_c[var1] < 0 || var5.field_76210_c[var1] < var3) {
				var5.field_76210_c[var1] = var3;
			}
		}

	}

	public void updateMPMapData(byte[] var1) {
		int var2;
		if(var1[0] == 0) {
			var2 = var1[1] & 255;
			int var3 = var1[2] & 255;

			for(int var4 = 0; var4 < var1.length - 3; ++var4) {
				this.colors[(var4 + var3) * 128 + var2] = var1[var4 + 3];
			}

			this.markDirty();
		} else if(var1[0] == 1) {
			this.playersVisibleOnMap.clear();

			for(var2 = 0; var2 < (var1.length - 1) / 3; ++var2) {
				byte var7 = (byte)(var1[var2 * 3 + 1] >> 4);
				byte var8 = var1[var2 * 3 + 2];
				byte var5 = var1[var2 * 3 + 3];
				byte var6 = (byte)(var1[var2 * 3 + 1] & 15);
				this.playersVisibleOnMap.put("icon-" + var2, new MapCoord(this, var7, var8, var5, var6));
			}
		} else if(var1[0] == 2) {
			this.scale = var1[1];
		}

	}

	public MapInfo func_82568_a(EntityPlayer var1) {
		MapInfo var2 = (MapInfo)this.playersHashMap.get(var1);
		if(var2 == null) {
			var2 = new MapInfo(this, var1);
			this.playersHashMap.put(var1, var2);
			this.playersArrayList.add(var2);
		}

		return var2;
	}
}
