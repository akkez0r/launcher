package net.minecraft.src;

import java.util.List;

public class ItemCoal extends Item {
	public ItemCoal(int var1) {
		super(var1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	public String getUnlocalizedName(ItemStack var1) {
		return var1.getItemDamage() == 1 ? "item.charcoal" : "item.coal";
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
		var3.add(new ItemStack(var1, 1, 1));
	}
}
