package net.minecraft.src;

import java.util.Random;

public class BlockMelon extends Block {
	private Icon theIcon;

	protected BlockMelon(int var1) {
		super(var1, Material.pumpkin);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 != 1 && var1 != 0 ? this.blockIcon : this.theIcon;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.melon.itemID;
	}

	public int quantityDropped(Random var1) {
		return 3 + var1.nextInt(5);
	}

	public int quantityDroppedWithBonus(int var1, Random var2) {
		int var3 = this.quantityDropped(var2) + var2.nextInt(1 + var1);
		if(var3 > 9) {
			var3 = 9;
		}

		return var3;
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("melon_side");
		this.theIcon = var1.registerIcon("melon_top");
	}
}
