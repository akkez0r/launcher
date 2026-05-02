package net.minecraft.src;

public class ItemSeeds extends Item {
	private int blockType;
	private int soilBlockID;

	public ItemSeeds(int var1, int var2, int var3) {
		super(var1);
		this.blockType = var2;
		this.soilBlockID = var3;
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		if(var7 != 1) {
			return false;
		} else if(var2.canPlayerEdit(var4, var5, var6, var7, var1) && var2.canPlayerEdit(var4, var5 + 1, var6, var7, var1)) {
			int var11 = var3.getBlockId(var4, var5, var6);
			if(var11 == this.soilBlockID && var3.isAirBlock(var4, var5 + 1, var6)) {
				var3.setBlock(var4, var5 + 1, var6, this.blockType);
				--var1.stackSize;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
