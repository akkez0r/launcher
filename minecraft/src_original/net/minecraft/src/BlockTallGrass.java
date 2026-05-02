package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockTallGrass extends BlockFlower {
	private static final String[] grassTypes = new String[]{"deadbush", "tallgrass", "fern"};
	private Icon[] iconArray;

	protected BlockTallGrass(int var1) {
		super(var1, Material.vine);
		float var2 = 0.4F;
		this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.8F, 0.5F + var2);
	}

	public Icon getIcon(int var1, int var2) {
		if(var2 >= this.iconArray.length) {
			var2 = 0;
		}

		return this.iconArray[var2];
	}

	public int getBlockColor() {
		double var1 = 0.5D;
		double var3 = 1.0D;
		return ColorizerGrass.getGrassColor(var1, var3);
	}

	public int getRenderColor(int var1) {
		return var1 == 0 ? 16777215 : ColorizerFoliage.getFoliageColorBasic();
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		return var5 == 0 ? 16777215 : var1.getBiomeGenForCoords(var2, var4).getBiomeGrassColor();
	}

	public int idDropped(int var1, Random var2, int var3) {
		return var2.nextInt(8) == 0 ? Item.seeds.itemID : -1;
	}

	public int quantityDroppedWithBonus(int var1, Random var2) {
		return 1 + var2.nextInt(var1 * 2 + 1);
	}

	public void harvestBlock(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
		if(!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().itemID == Item.shears.itemID) {
			var2.addStat(StatList.mineBlockStatArray[this.blockID], 1);
			this.dropBlockAsItem_do(var1, var3, var4, var5, new ItemStack(Block.tallGrass, 1, var6));
		} else {
			super.harvestBlock(var1, var2, var3, var4, var5, var6);
		}

	}

	public int getDamageValue(World var1, int var2, int var3, int var4) {
		return var1.getBlockMetadata(var2, var3, var4);
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		for(int var4 = 1; var4 < 3; ++var4) {
			var3.add(new ItemStack(var1, 1, var4));
		}

	}

	public void registerIcons(IconRegister var1) {
		this.iconArray = new Icon[grassTypes.length];

		for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
			this.iconArray[var2] = var1.registerIcon(grassTypes[var2]);
		}

	}
}
