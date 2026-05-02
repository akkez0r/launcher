package net.minecraft.src;

import java.util.List;

final class CreativeTabTools extends CreativeTabs {
	CreativeTabTools(int var1, String var2) {
		super(var1, var2);
	}

	public int getTabIconItemIndex() {
		return Item.axeIron.itemID;
	}

	public void displayAllReleventItems(List var1) {
		super.displayAllReleventItems(var1);
		this.func_92116_a(var1, new EnumEnchantmentType[]{EnumEnchantmentType.digger});
	}
}
