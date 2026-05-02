package net.minecraft.src;

public class NextTickListEntry implements Comparable {
	private static long nextTickEntryID = 0L;
	public int xCoord;
	public int yCoord;
	public int zCoord;
	public int blockID;
	public long scheduledTime;
	public int field_82754_f;
	private long tickEntryID = nextTickEntryID++;

	public NextTickListEntry(int var1, int var2, int var3, int var4) {
		this.xCoord = var1;
		this.yCoord = var2;
		this.zCoord = var3;
		this.blockID = var4;
	}

	public boolean equals(Object var1) {
		if(!(var1 instanceof NextTickListEntry)) {
			return false;
		} else {
			NextTickListEntry var2 = (NextTickListEntry)var1;
			return this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord && Block.isAssociatedBlockID(this.blockID, var2.blockID);
		}
	}

	public int hashCode() {
		return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256;
	}

	public NextTickListEntry setScheduledTime(long var1) {
		this.scheduledTime = var1;
		return this;
	}

	public void func_82753_a(int var1) {
		this.field_82754_f = var1;
	}

	public int comparer(NextTickListEntry var1) {
		return this.scheduledTime < var1.scheduledTime ? -1 : (this.scheduledTime > var1.scheduledTime ? 1 : (this.field_82754_f != var1.field_82754_f ? this.field_82754_f - var1.field_82754_f : (this.tickEntryID < var1.tickEntryID ? -1 : (this.tickEntryID > var1.tickEntryID ? 1 : 0))));
	}

	public String toString() {
		return this.blockID + ": (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + "), " + this.scheduledTime + ", " + this.field_82754_f + ", " + this.tickEntryID;
	}

	public int compareTo(Object var1) {
		return this.comparer((NextTickListEntry)var1);
	}
}
