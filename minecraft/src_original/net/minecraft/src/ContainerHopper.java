package net.minecraft.src;

public class ContainerHopper extends Container {
	private final IInventory field_94538_a;

	public ContainerHopper(InventoryPlayer var1, IInventory var2) {
		this.field_94538_a = var2;
		var2.openChest();
		byte var3 = 51;

		int var4;
		for(var4 = 0; var4 < var2.getSizeInventory(); ++var4) {
			this.addSlotToContainer(new Slot(var2, var4, 44 + var4 * 18, 20));
		}

		for(var4 = 0; var4 < 3; ++var4) {
			for(int var5 = 0; var5 < 9; ++var5) {
				this.addSlotToContainer(new Slot(var1, var5 + var4 * 9 + 9, 8 + var5 * 18, var4 * 18 + var3));
			}
		}

		for(var4 = 0; var4 < 9; ++var4) {
			this.addSlotToContainer(new Slot(var1, var4, 8 + var4 * 18, 58 + var3));
		}

	}

	public boolean canInteractWith(EntityPlayer var1) {
		return this.field_94538_a.isUseableByPlayer(var1);
	}

	public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
		ItemStack var3 = null;
		Slot var4 = (Slot)this.inventorySlots.get(var2);
		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();
			if(var2 < this.field_94538_a.getSizeInventory()) {
				if(!this.mergeItemStack(var5, this.field_94538_a.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			} else if(!this.mergeItemStack(var5, 0, this.field_94538_a.getSizeInventory(), false)) {
				return null;
			}

			if(var5.stackSize == 0) {
				var4.putStack((ItemStack)null);
			} else {
				var4.onSlotChanged();
			}
		}

		return var3;
	}

	public void onCraftGuiClosed(EntityPlayer var1) {
		super.onCraftGuiClosed(var1);
		this.field_94538_a.closeChest();
	}
}
