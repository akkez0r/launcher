package net.minecraft.src;

public class FlatLayerInfo {
	private int layerCount;
	private int layerFillBlock;
	private int layerFillBlockMeta;
	private int layerMinimumY;

	public FlatLayerInfo(int var1, int var2) {
		this.layerCount = 1;
		this.layerFillBlock = 0;
		this.layerFillBlockMeta = 0;
		this.layerMinimumY = 0;
		this.layerCount = var1;
		this.layerFillBlock = var2;
	}

	public FlatLayerInfo(int var1, int var2, int var3) {
		this(var1, var2);
		this.layerFillBlockMeta = var3;
	}

	public int getLayerCount() {
		return this.layerCount;
	}

	public int getFillBlock() {
		return this.layerFillBlock;
	}

	public int getFillBlockMeta() {
		return this.layerFillBlockMeta;
	}

	public int getMinY() {
		return this.layerMinimumY;
	}

	public void setMinY(int var1) {
		this.layerMinimumY = var1;
	}

	public String toString() {
		String var1 = Integer.toString(this.layerFillBlock);
		if(this.layerCount > 1) {
			var1 = this.layerCount + "x" + var1;
		}

		if(this.layerFillBlockMeta > 0) {
			var1 = var1 + ":" + this.layerFillBlockMeta;
		}

		return var1;
	}
}
