package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

public class TileEntity {
	private static Map nameToClassMap = new HashMap();
	private static Map classToNameMap = new HashMap();
	protected World worldObj;
	public int xCoord;
	public int yCoord;
	public int zCoord;
	protected boolean tileEntityInvalid;
	public int blockMetadata = -1;
	public Block blockType;

	private static void addMapping(Class var0, String var1) {
		if(nameToClassMap.containsKey(var1)) {
			throw new IllegalArgumentException("Duplicate id: " + var1);
		} else {
			nameToClassMap.put(var1, var0);
			classToNameMap.put(var0, var1);
		}
	}

	public World getWorldObj() {
		return this.worldObj;
	}

	public void setWorldObj(World var1) {
		this.worldObj = var1;
	}

	public boolean func_70309_m() {
		return this.worldObj != null;
	}

	public void readFromNBT(NBTTagCompound var1) {
		this.xCoord = var1.getInteger("x");
		this.yCoord = var1.getInteger("y");
		this.zCoord = var1.getInteger("z");
	}

	public void writeToNBT(NBTTagCompound var1) {
		String var2 = (String)classToNameMap.get(this.getClass());
		if(var2 == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			var1.setString("id", var2);
			var1.setInteger("x", this.xCoord);
			var1.setInteger("y", this.yCoord);
			var1.setInteger("z", this.zCoord);
		}
	}

	public void updateEntity() {
	}

	public static TileEntity createAndLoadEntity(NBTTagCompound var0) {
		TileEntity var1 = null;

		try {
			Class var2 = (Class)nameToClassMap.get(var0.getString("id"));
			if(var2 != null) {
				var1 = (TileEntity)var2.newInstance();
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

		if(var1 != null) {
			var1.readFromNBT(var0);
		} else {
			MinecraftServer.getServer().getLogAgent().logWarning("Skipping TileEntity with id " + var0.getString("id"));
		}

		return var1;
	}

	public int getBlockMetadata() {
		if(this.blockMetadata == -1) {
			this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		}

		return this.blockMetadata;
	}

	public void onInventoryChanged() {
		if(this.worldObj != null) {
			this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
			if(this.getBlockType() != null) {
				this.worldObj.func_96440_m(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
			}
		}

	}

	public double getDistanceFrom(double var1, double var3, double var5) {
		double var7 = (double)this.xCoord + 0.5D - var1;
		double var9 = (double)this.yCoord + 0.5D - var3;
		double var11 = (double)this.zCoord + 0.5D - var5;
		return var7 * var7 + var9 * var9 + var11 * var11;
	}

	public double getMaxRenderDistanceSquared() {
		return 4096.0D;
	}

	public Block getBlockType() {
		if(this.blockType == null) {
			this.blockType = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
		}

		return this.blockType;
	}

	public Packet getDescriptionPacket() {
		return null;
	}

	public boolean isInvalid() {
		return this.tileEntityInvalid;
	}

	public void invalidate() {
		this.tileEntityInvalid = true;
	}

	public void validate() {
		this.tileEntityInvalid = false;
	}

	public boolean receiveClientEvent(int var1, int var2) {
		return false;
	}

	public void updateContainingBlockInfo() {
		this.blockType = null;
		this.blockMetadata = -1;
	}

	public void func_85027_a(CrashReportCategory var1) {
		var1.addCrashSectionCallable("Name", new CallableTileEntityName(this));
		CrashReportCategory.func_85068_a(var1, this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, this.getBlockMetadata());
		var1.addCrashSectionCallable("Actual block type", new CallableTileEntityID(this));
		var1.addCrashSectionCallable("Actual block data value", new CallableTileEntityData(this));
	}

	static Map getClassToNameMap() {
		return classToNameMap;
	}

	static {
		addMapping(TileEntityFurnace.class, "Furnace");
		addMapping(TileEntityChest.class, "Chest");
		addMapping(TileEntityEnderChest.class, "EnderChest");
		addMapping(TileEntityRecordPlayer.class, "RecordPlayer");
		addMapping(TileEntityDispenser.class, "Trap");
		addMapping(TileEntityDropper.class, "Dropper");
		addMapping(TileEntitySign.class, "Sign");
		addMapping(TileEntityMobSpawner.class, "MobSpawner");
		addMapping(TileEntityNote.class, "Music");
		addMapping(TileEntityPiston.class, "Piston");
		addMapping(TileEntityBrewingStand.class, "Cauldron");
		addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
		addMapping(TileEntityEndPortal.class, "Airportal");
		addMapping(TileEntityCommandBlock.class, "Control");
		addMapping(TileEntityBeacon.class, "Beacon");
		addMapping(TileEntitySkull.class, "Skull");
		addMapping(TileEntityDaylightDetector.class, "DLDetector");
		addMapping(TileEntityHopper.class, "Hopper");
		addMapping(TileEntityComparator.class, "Comparator");
	}
}
