package net.minecraft.src;

import java.util.Random;

public class BlockDeadBush extends BlockFlower {
	protected BlockDeadBush(int var1) {
		super(var1, Material.vine);
		float var2 = 0.4F;
		this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.8F, 0.5F + var2);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int var1) {
		return var1 == Block.sand.blockID;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return -1;
	}

	public void harvestBlock(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
		if(!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().itemID == Item.shears.itemID) {
			var2.addStat(StatList.mineBlockStatArray[this.blockID], 1);
			this.dropBlockAsItem_do(var1, var3, var4, var5, new ItemStack(Block.deadBush, 1, var6));
		} else {
			super.harvestBlock(var1, var2, var3, var4, var5, var6);
		}

	}
}
