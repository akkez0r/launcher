package net.minecraft.src;

public class EntityMinecartChest extends EntityMinecartContainer {
	public EntityMinecartChest(World var1) {
		super(var1);
	}

	public EntityMinecartChest(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6);
	}

	public void killMinecart(DamageSource var1) {
		super.killMinecart(var1);
		this.dropItemWithOffset(Block.chest.blockID, 1, 0.0F);
	}

	public int getSizeInventory() {
		return 27;
	}

	public int getMinecartType() {
		return 1;
	}

	public Block getDefaultDisplayTile() {
		return Block.chest;
	}

	public int getDefaultDisplayTileOffset() {
		return 8;
	}
}
