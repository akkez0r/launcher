package net.minecraft.src;

public class ItemBook extends Item {
	public ItemBook(int var1) {
		super(var1);
	}

	public boolean isItemTool(ItemStack var1) {
		return var1.stackSize == 1;
	}

	public int getItemEnchantability() {
		return 1;
	}
}
