package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableItemName implements Callable {
	final ItemStack theItemStack;
	final InventoryPlayer playerInventory;

	CallableItemName(InventoryPlayer var1, ItemStack var2) {
		this.playerInventory = var1;
		this.theItemStack = var2;
	}

	public String callItemDisplayName() {
		return this.theItemStack.getDisplayName();
	}

	public Object call() {
		return this.callItemDisplayName();
	}
}
