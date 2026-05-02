package net.minecraft.src;

public class ContainerChest extends Container {
	private IInventory lowerChestInventory;
	private int numRows;

	public ContainerChest(IInventory var1, IInventory var2) {
		this.lowerChestInventory = var2;
		this.numRows = var2.getSizeInventory() / 9;
		var2.openChest();
		int var3 = (this.numRows - 4) * 18;

		int var4;
		int var5;
		for(var4 = 0; var4 < this.numRows; ++var4) {
			for(var5 = 0; var5 < 9; ++var5) {
				this.addSlotToContainer(new Slot(var2, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18));
			}
		}

		for(var4 = 0; var4 < 3; ++var4) {
			for(var5 = 0; var5 < 9; ++var5) {
				this.addSlotToContainer(new Slot(var1, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3));
			}
		}

		for(var4 = 0; var4 < 9; ++var4) {
			this.addSlotToContainer(new Slot(var1, var4, 8 + var4 * 18, 161 + var3));
		}

	}

	public boolean canInteractWith(EntityPlayer var1) {
		return this.lowerChestInventory.isUseableByPlayer(var1);
	}

	public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
		ItemStack var3 = null;
		Slot var4 = (Slot)this.inventorySlots.get(var2);
		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();
			if(var2 < this.numRows * 9) {
				if(!this.mergeItemStack(var5, this.numRows * 9, this.inventorySlots.size(), true)) {
					return null;
				}
			} else if(!this.mergeItemStack(var5, 0, this.numRows * 9, false)) {
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
		this.lowerChestInventory.closeChest();
	}

	public IInventory getLowerChestInventory() {
		return this.lowerChestInventory;
	}
}
