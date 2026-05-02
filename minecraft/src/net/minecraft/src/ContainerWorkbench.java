package net.minecraft.src;

public class ContainerWorkbench extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World worldObj;
	private int posX;
	private int posY;
	private int posZ;

	public ContainerWorkbench(InventoryPlayer var1, World var2, int var3, int var4, int var5) {
		this.worldObj = var2;
		this.posX = var3;
		this.posY = var4;
		this.posZ = var5;
		this.addSlotToContainer(new SlotCrafting(var1.player, this.craftMatrix, this.craftResult, 0, 124, 35));

		int var6;
		int var7;
		for(var6 = 0; var6 < 3; ++var6) {
			for(var7 = 0; var7 < 3; ++var7) {
				this.addSlotToContainer(new Slot(this.craftMatrix, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
			}
		}

		for(var6 = 0; var6 < 3; ++var6) {
			for(var7 = 0; var7 < 9; ++var7) {
				this.addSlotToContainer(new Slot(var1, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
			}
		}

		for(var6 = 0; var6 < 9; ++var6) {
			this.addSlotToContainer(new Slot(var1, var6, 8 + var6 * 18, 142));
		}

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory var1) {
		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
	}

	public void onCraftGuiClosed(EntityPlayer var1) {
		super.onCraftGuiClosed(var1);
		if(!this.worldObj.isRemote) {
			for(int var2 = 0; var2 < 9; ++var2) {
				ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
				if(var3 != null) {
					var1.dropPlayerItem(var3);
				}
			}

		}
	}

	public boolean canInteractWith(EntityPlayer var1) {
		return this.worldObj.getBlockId(this.posX, this.posY, this.posZ) != Block.workbench.blockID ? false : var1.getDistanceSq((double)this.posX + 0.5D, (double)this.posY + 0.5D, (double)this.posZ + 0.5D) <= 64.0D;
	}

	public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
		ItemStack var3 = null;
		Slot var4 = (Slot)this.inventorySlots.get(var2);
		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();
			if(var2 == 0) {
				if(!this.mergeItemStack(var5, 10, 46, true)) {
					return null;
				}

				var4.onSlotChange(var5, var3);
			} else if(var2 >= 10 && var2 < 37) {
				if(!this.mergeItemStack(var5, 37, 46, false)) {
					return null;
				}
			} else if(var2 >= 37 && var2 < 46) {
				if(!this.mergeItemStack(var5, 10, 37, false)) {
					return null;
				}
			} else if(!this.mergeItemStack(var5, 10, 46, false)) {
				return null;
			}

			if(var5.stackSize == 0) {
				var4.putStack((ItemStack)null);
			} else {
				var4.onSlotChanged();
			}

			if(var5.stackSize == var3.stackSize) {
				return null;
			}

			var4.onPickupFromSlot(var1, var5);
		}

		return var3;
	}

	public boolean func_94530_a(ItemStack var1, Slot var2) {
		return var2.inventory != this.craftResult && super.func_94530_a(var1, var2);
	}
}
