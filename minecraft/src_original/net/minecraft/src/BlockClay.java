package net.minecraft.src;

import java.util.Random;

public class BlockClay extends Block {
	public BlockClay(int var1) {
		super(var1, Material.clay);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.clay.itemID;
	}

	public int quantityDropped(Random var1) {
		return 4;
	}
}
