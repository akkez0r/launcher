package net.minecraft.src;

class InventoryRepair extends InventoryBasic {
	final ContainerRepair theContainer;

	InventoryRepair(ContainerRepair var1, String var2, boolean var3, int var4) {
		super(var2, var3, var4);
		this.theContainer = var1;
	}

	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.theContainer.onCraftMatrixChanged(this);
	}

	public boolean isStackValidForSlot(int var1, ItemStack var2) {
		return true;
	}
}
