package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class Container {
	public List inventoryItemStacks = new ArrayList();
	public List inventorySlots = new ArrayList();
	public int windowId = 0;
	private short transactionID = 0;
	private int field_94535_f = -1;
	private int field_94536_g = 0;
	private final Set field_94537_h = new HashSet();
	protected List crafters = new ArrayList();
	private Set playerList = new HashSet();

	protected Slot addSlotToContainer(Slot var1) {
		var1.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(var1);
		this.inventoryItemStacks.add((Object)null);
		return var1;
	}

	public void addCraftingToCrafters(ICrafting var1) {
		if(this.crafters.contains(var1)) {
			throw new IllegalArgumentException("Listener already listening");
		} else {
			this.crafters.add(var1);
			var1.sendContainerAndContentsToPlayer(this, this.getInventory());
			this.detectAndSendChanges();
		}
	}

	public void removeCraftingFromCrafters(ICrafting var1) {
		this.crafters.remove(var1);
	}

	public List getInventory() {
		ArrayList var1 = new ArrayList();

		for(int var2 = 0; var2 < this.inventorySlots.size(); ++var2) {
			var1.add(((Slot)this.inventorySlots.get(var2)).getStack());
		}

		return var1;
	}

	public void detectAndSendChanges() {
		for(int var1 = 0; var1 < this.inventorySlots.size(); ++var1) {
			ItemStack var2 = ((Slot)this.inventorySlots.get(var1)).getStack();
			ItemStack var3 = (ItemStack)this.inventoryItemStacks.get(var1);
			if(!ItemStack.areItemStacksEqual(var3, var2)) {
				var3 = var2 == null ? null : var2.copy();
				this.inventoryItemStacks.set(var1, var3);

				for(int var4 = 0; var4 < this.crafters.size(); ++var4) {
					((ICrafting)this.crafters.get(var4)).sendSlotContents(this, var1, var3);
				}
			}
		}

	}

	public boolean enchantItem(EntityPlayer var1, int var2) {
		return false;
	}

	public Slot getSlotFromInventory(IInventory var1, int var2) {
		for(int var3 = 0; var3 < this.inventorySlots.size(); ++var3) {
			Slot var4 = (Slot)this.inventorySlots.get(var3);
			if(var4.isSlotInInventory(var1, var2)) {
				return var4;
			}
		}

		return null;
	}

	public Slot getSlot(int var1) {
		return (Slot)this.inventorySlots.get(var1);
	}

	public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
		Slot var3 = (Slot)this.inventorySlots.get(var2);
		return var3 != null ? var3.getStack() : null;
	}

	public ItemStack slotClick(int var1, int var2, int var3, EntityPlayer var4) {
		ItemStack var5 = null;
		InventoryPlayer var6 = var4.inventory;
		int var9;
		ItemStack var17;
		if(var3 == 5) {
			int var7 = this.field_94536_g;
			this.field_94536_g = func_94532_c(var2);
			if((var7 != 1 || this.field_94536_g != 2) && var7 != this.field_94536_g) {
				this.func_94533_d();
			} else if(var6.getItemStack() == null) {
				this.func_94533_d();
			} else if(this.field_94536_g == 0) {
				this.field_94535_f = func_94529_b(var2);
				if(func_94528_d(this.field_94535_f)) {
					this.field_94536_g = 1;
					this.field_94537_h.clear();
				} else {
					this.func_94533_d();
				}
			} else if(this.field_94536_g == 1) {
				Slot var8 = (Slot)this.inventorySlots.get(var1);
				if(var8 != null && func_94527_a(var8, var6.getItemStack(), true) && var8.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize > this.field_94537_h.size() && this.func_94531_b(var8)) {
					this.field_94537_h.add(var8);
				}
			} else if(this.field_94536_g == 2) {
				if(!this.field_94537_h.isEmpty()) {
					var17 = var6.getItemStack().copy();
					var9 = var6.getItemStack().stackSize;
					Iterator var10 = this.field_94537_h.iterator();

					while(var10.hasNext()) {
						Slot var11 = (Slot)var10.next();
						if(var11 != null && func_94527_a(var11, var6.getItemStack(), true) && var11.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize >= this.field_94537_h.size() && this.func_94531_b(var11)) {
							ItemStack var12 = var17.copy();
							int var13 = var11.getHasStack() ? var11.getStack().stackSize : 0;
							func_94525_a(this.field_94537_h, this.field_94535_f, var12, var13);
							if(var12.stackSize > var12.getMaxStackSize()) {
								var12.stackSize = var12.getMaxStackSize();
							}

							if(var12.stackSize > var11.getSlotStackLimit()) {
								var12.stackSize = var11.getSlotStackLimit();
							}

							var9 -= var12.stackSize - var13;
							var11.putStack(var12);
						}
					}

					var17.stackSize = var9;
					if(var17.stackSize <= 0) {
						var17 = null;
					}

					var6.setItemStack(var17);
				}

				this.func_94533_d();
			} else {
				this.func_94533_d();
			}
		} else if(this.field_94536_g != 0) {
			this.func_94533_d();
		} else {
			Slot var16;
			int var19;
			ItemStack var22;
			if((var3 == 0 || var3 == 1) && (var2 == 0 || var2 == 1)) {
				if(var1 == -999) {
					if(var6.getItemStack() != null && var1 == -999) {
						if(var2 == 0) {
							var4.dropPlayerItem(var6.getItemStack());
							var6.setItemStack((ItemStack)null);
						}

						if(var2 == 1) {
							var4.dropPlayerItem(var6.getItemStack().splitStack(1));
							if(var6.getItemStack().stackSize == 0) {
								var6.setItemStack((ItemStack)null);
							}
						}
					}
				} else if(var3 == 1) {
					if(var1 < 0) {
						return null;
					}

					var16 = (Slot)this.inventorySlots.get(var1);
					if(var16 != null && var16.canTakeStack(var4)) {
						var17 = this.transferStackInSlot(var4, var1);
						if(var17 != null) {
							var9 = var17.itemID;
							var5 = var17.copy();
							if(var16 != null && var16.getStack() != null && var16.getStack().itemID == var9) {
								this.retrySlotClick(var1, var2, true, var4);
							}
						}
					}
				} else {
					if(var1 < 0) {
						return null;
					}

					var16 = (Slot)this.inventorySlots.get(var1);
					if(var16 != null) {
						var17 = var16.getStack();
						ItemStack var20 = var6.getItemStack();
						if(var17 != null) {
							var5 = var17.copy();
						}

						if(var17 == null) {
							if(var20 != null && var16.isItemValid(var20)) {
								var19 = var2 == 0 ? var20.stackSize : 1;
								if(var19 > var16.getSlotStackLimit()) {
									var19 = var16.getSlotStackLimit();
								}

								var16.putStack(var20.splitStack(var19));
								if(var20.stackSize == 0) {
									var6.setItemStack((ItemStack)null);
								}
							}
						} else if(var16.canTakeStack(var4)) {
							if(var20 == null) {
								var19 = var2 == 0 ? var17.stackSize : (var17.stackSize + 1) / 2;
								var22 = var16.decrStackSize(var19);
								var6.setItemStack(var22);
								if(var17.stackSize == 0) {
									var16.putStack((ItemStack)null);
								}

								var16.onPickupFromSlot(var4, var6.getItemStack());
							} else if(var16.isItemValid(var20)) {
								if(var17.itemID == var20.itemID && var17.getItemDamage() == var20.getItemDamage() && ItemStack.areItemStackTagsEqual(var17, var20)) {
									var19 = var2 == 0 ? var20.stackSize : 1;
									if(var19 > var16.getSlotStackLimit() - var17.stackSize) {
										var19 = var16.getSlotStackLimit() - var17.stackSize;
									}

									if(var19 > var20.getMaxStackSize() - var17.stackSize) {
										var19 = var20.getMaxStackSize() - var17.stackSize;
									}

									var20.splitStack(var19);
									if(var20.stackSize == 0) {
										var6.setItemStack((ItemStack)null);
									}

									var17.stackSize += var19;
								} else if(var20.stackSize <= var16.getSlotStackLimit()) {
									var16.putStack(var20);
									var6.setItemStack(var17);
								}
							} else if(var17.itemID == var20.itemID && var20.getMaxStackSize() > 1 && (!var17.getHasSubtypes() || var17.getItemDamage() == var20.getItemDamage()) && ItemStack.areItemStackTagsEqual(var17, var20)) {
								var19 = var17.stackSize;
								if(var19 > 0 && var19 + var20.stackSize <= var20.getMaxStackSize()) {
									var20.stackSize += var19;
									var17 = var16.decrStackSize(var19);
									if(var17.stackSize == 0) {
										var16.putStack((ItemStack)null);
									}

									var16.onPickupFromSlot(var4, var6.getItemStack());
								}
							}
						}

						var16.onSlotChanged();
					}
				}
			} else if(var3 == 2 && var2 >= 0 && var2 < 9) {
				var16 = (Slot)this.inventorySlots.get(var1);
				if(var16.canTakeStack(var4)) {
					var17 = var6.getStackInSlot(var2);
					boolean var18 = var17 == null || var16.inventory == var6 && var16.isItemValid(var17);
					var19 = -1;
					if(!var18) {
						var19 = var6.getFirstEmptyStack();
						var18 |= var19 > -1;
					}

					if(var16.getHasStack() && var18) {
						var22 = var16.getStack();
						var6.setInventorySlotContents(var2, var22.copy());
						if((var16.inventory != var6 || !var16.isItemValid(var17)) && var17 != null) {
							if(var19 > -1) {
								var6.addItemStackToInventory(var17);
								var16.decrStackSize(var22.stackSize);
								var16.putStack((ItemStack)null);
								var16.onPickupFromSlot(var4, var22);
							}
						} else {
							var16.decrStackSize(var22.stackSize);
							var16.putStack(var17);
							var16.onPickupFromSlot(var4, var22);
						}
					} else if(!var16.getHasStack() && var17 != null && var16.isItemValid(var17)) {
						var6.setInventorySlotContents(var2, (ItemStack)null);
						var16.putStack(var17);
					}
				}
			} else if(var3 == 3 && var4.capabilities.isCreativeMode && var6.getItemStack() == null && var1 >= 0) {
				var16 = (Slot)this.inventorySlots.get(var1);
				if(var16 != null && var16.getHasStack()) {
					var17 = var16.getStack().copy();
					var17.stackSize = var17.getMaxStackSize();
					var6.setItemStack(var17);
				}
			} else if(var3 == 4 && var6.getItemStack() == null && var1 >= 0) {
				var16 = (Slot)this.inventorySlots.get(var1);
				if(var16 != null && var16.getHasStack() && var16.canTakeStack(var4)) {
					var17 = var16.decrStackSize(var2 == 0 ? 1 : var16.getStack().stackSize);
					var16.onPickupFromSlot(var4, var17);
					var4.dropPlayerItem(var17);
				}
			} else if(var3 == 6 && var1 >= 0) {
				var16 = (Slot)this.inventorySlots.get(var1);
				var17 = var6.getItemStack();
				if(var17 != null && (var16 == null || !var16.getHasStack() || !var16.canTakeStack(var4))) {
					var9 = var2 == 0 ? 0 : this.inventorySlots.size() - 1;
					var19 = var2 == 0 ? 1 : -1;

					for(int var21 = 0; var21 < 2; ++var21) {
						for(int var23 = var9; var23 >= 0 && var23 < this.inventorySlots.size() && var17.stackSize < var17.getMaxStackSize(); var23 += var19) {
							Slot var24 = (Slot)this.inventorySlots.get(var23);
							if(var24.getHasStack() && func_94527_a(var24, var17, true) && var24.canTakeStack(var4) && this.func_94530_a(var17, var24) && (var21 != 0 || var24.getStack().stackSize != var24.getStack().getMaxStackSize())) {
								int var14 = Math.min(var17.getMaxStackSize() - var17.stackSize, var24.getStack().stackSize);
								ItemStack var15 = var24.decrStackSize(var14);
								var17.stackSize += var14;
								if(var15.stackSize <= 0) {
									var24.putStack((ItemStack)null);
								}

								var24.onPickupFromSlot(var4, var15);
							}
						}
					}
				}

				this.detectAndSendChanges();
			}
		}

		return var5;
	}

	public boolean func_94530_a(ItemStack var1, Slot var2) {
		return true;
	}

	protected void retrySlotClick(int var1, int var2, boolean var3, EntityPlayer var4) {
		this.slotClick(var1, var2, 1, var4);
	}

	public void onCraftGuiClosed(EntityPlayer var1) {
		InventoryPlayer var2 = var1.inventory;
		if(var2.getItemStack() != null) {
			var1.dropPlayerItem(var2.getItemStack());
			var2.setItemStack((ItemStack)null);
		}

	}

	public void onCraftMatrixChanged(IInventory var1) {
		this.detectAndSendChanges();
	}

	public void putStackInSlot(int var1, ItemStack var2) {
		this.getSlot(var1).putStack(var2);
	}

	public void putStacksInSlots(ItemStack[] var1) {
		for(int var2 = 0; var2 < var1.length; ++var2) {
			this.getSlot(var2).putStack(var1[var2]);
		}

	}

	public void updateProgressBar(int var1, int var2) {
	}

	public short getNextTransactionID(InventoryPlayer var1) {
		++this.transactionID;
		return this.transactionID;
	}

	public boolean isPlayerNotUsingContainer(EntityPlayer var1) {
		return !this.playerList.contains(var1);
	}

	public void setPlayerIsPresent(EntityPlayer var1, boolean var2) {
		if(var2) {
			this.playerList.remove(var1);
		} else {
			this.playerList.add(var1);
		}

	}

	public abstract boolean canInteractWith(EntityPlayer var1);

	protected boolean mergeItemStack(ItemStack var1, int var2, int var3, boolean var4) {
		boolean var5 = false;
		int var6 = var2;
		if(var4) {
			var6 = var3 - 1;
		}

		Slot var7;
		ItemStack var8;
		if(var1.isStackable()) {
			while(var1.stackSize > 0 && (!var4 && var6 < var3 || var4 && var6 >= var2)) {
				var7 = (Slot)this.inventorySlots.get(var6);
				var8 = var7.getStack();
				if(var8 != null && var8.itemID == var1.itemID && (!var1.getHasSubtypes() || var1.getItemDamage() == var8.getItemDamage()) && ItemStack.areItemStackTagsEqual(var1, var8)) {
					int var9 = var8.stackSize + var1.stackSize;
					if(var9 <= var1.getMaxStackSize()) {
						var1.stackSize = 0;
						var8.stackSize = var9;
						var7.onSlotChanged();
						var5 = true;
					} else if(var8.stackSize < var1.getMaxStackSize()) {
						var1.stackSize -= var1.getMaxStackSize() - var8.stackSize;
						var8.stackSize = var1.getMaxStackSize();
						var7.onSlotChanged();
						var5 = true;
					}
				}

				if(var4) {
					--var6;
				} else {
					++var6;
				}
			}
		}

		if(var1.stackSize > 0) {
			if(var4) {
				var6 = var3 - 1;
			} else {
				var6 = var2;
			}

			while(!var4 && var6 < var3 || var4 && var6 >= var2) {
				var7 = (Slot)this.inventorySlots.get(var6);
				var8 = var7.getStack();
				if(var8 == null) {
					var7.putStack(var1.copy());
					var7.onSlotChanged();
					var1.stackSize = 0;
					var5 = true;
					break;
				}

				if(var4) {
					--var6;
				} else {
					++var6;
				}
			}
		}

		return var5;
	}

	public static int func_94529_b(int var0) {
		return var0 >> 2 & 3;
	}

	public static int func_94532_c(int var0) {
		return var0 & 3;
	}

	public static int func_94534_d(int var0, int var1) {
		return var0 & 3 | (var1 & 3) << 2;
	}

	public static boolean func_94528_d(int var0) {
		return var0 == 0 || var0 == 1;
	}

	protected void func_94533_d() {
		this.field_94536_g = 0;
		this.field_94537_h.clear();
	}

	public static boolean func_94527_a(Slot var0, ItemStack var1, boolean var2) {
		boolean var3 = var0 == null || !var0.getHasStack();
		if(var0 != null && var0.getHasStack() && var1 != null && var1.isItemEqual(var0.getStack()) && ItemStack.areItemStackTagsEqual(var0.getStack(), var1)) {
			var3 |= var0.getStack().stackSize + (var2 ? 0 : var1.stackSize) <= var1.getMaxStackSize();
		}

		return var3;
	}

	public static void func_94525_a(Set var0, int var1, ItemStack var2, int var3) {
		switch(var1) {
		case 0:
			var2.stackSize = MathHelper.floor_float((float)var2.stackSize / (float)var0.size());
			break;
		case 1:
			var2.stackSize = 1;
		}

		var2.stackSize += var3;
	}

	public boolean func_94531_b(Slot var1) {
		return true;
	}

	public static int calcRedstoneFromInventory(IInventory var0) {
		if(var0 == null) {
			return 0;
		} else {
			int var1 = 0;
			float var2 = 0.0F;

			for(int var3 = 0; var3 < var0.getSizeInventory(); ++var3) {
				ItemStack var4 = var0.getStackInSlot(var3);
				if(var4 != null) {
					var2 += (float)var4.stackSize / (float)Math.min(var0.getInventoryStackLimit(), var4.getMaxStackSize());
					++var1;
				}
			}

			var2 /= (float)var0.getSizeInventory();
			return MathHelper.floor_float(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
		}
	}
}
