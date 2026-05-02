package net.minecraft.src;

public class TileEntityFurnace extends TileEntity implements ISidedInventory {
	private static final int[] field_102010_d = new int[]{0};
	private static final int[] field_102011_e = new int[]{2, 1};
	private static final int[] field_102009_f = new int[]{1};
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;
	private String field_94130_e;

	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	public ItemStack getStackInSlot(int var1) {
		return this.furnaceItemStacks[var1];
	}

	public ItemStack decrStackSize(int var1, int var2) {
		if(this.furnaceItemStacks[var1] != null) {
			ItemStack var3;
			if(this.furnaceItemStacks[var1].stackSize <= var2) {
				var3 = this.furnaceItemStacks[var1];
				this.furnaceItemStacks[var1] = null;
				return var3;
			} else {
				var3 = this.furnaceItemStacks[var1].splitStack(var2);
				if(this.furnaceItemStacks[var1].stackSize == 0) {
					this.furnaceItemStacks[var1] = null;
				}

				return var3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int var1) {
		if(this.furnaceItemStacks[var1] != null) {
			ItemStack var2 = this.furnaceItemStacks[var1];
			this.furnaceItemStacks[var1] = null;
			return var2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.furnaceItemStacks[var1] = var2;
		if(var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
			var2.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return this.isInvNameLocalized() ? this.field_94130_e : "container.furnace";
	}

	public boolean isInvNameLocalized() {
		return this.field_94130_e != null && this.field_94130_e.length() > 0;
	}

	public void func_94129_a(String var1) {
		this.field_94130_e = var1;
	}

	public void readFromNBT(NBTTagCompound var1) {
		super.readFromNBT(var1);
		NBTTagList var2 = var1.getTagList("Items");
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");
			if(var5 >= 0 && var5 < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}

		this.furnaceBurnTime = var1.getShort("BurnTime");
		this.furnaceCookTime = var1.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
		if(var1.hasKey("CustomName")) {
			this.field_94130_e = var1.getString("CustomName");
		}

	}

	public void writeToNBT(NBTTagCompound var1) {
		super.writeToNBT(var1);
		var1.setShort("BurnTime", (short)this.furnaceBurnTime);
		var1.setShort("CookTime", (short)this.furnaceCookTime);
		NBTTagList var2 = new NBTTagList();

		for(int var3 = 0; var3 < this.furnaceItemStacks.length; ++var3) {
			if(this.furnaceItemStacks[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.furnaceItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		var1.setTag("Items", var2);
		if(this.isInvNameLocalized()) {
			var1.setString("CustomName", this.field_94130_e);
		}

	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public int getCookProgressScaled(int var1) {
		return this.furnaceCookTime * var1 / 200;
	}

	public int getBurnTimeRemainingScaled(int var1) {
		if(this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}

		return this.furnaceBurnTime * var1 / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	public void updateEntity() {
		boolean var1 = this.furnaceBurnTime > 0;
		boolean var2 = false;
		if(this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		if(!this.worldObj.isRemote) {
			if(this.furnaceBurnTime == 0 && this.canSmelt()) {
				this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
				if(this.furnaceBurnTime > 0) {
					var2 = true;
					if(this.furnaceItemStacks[1] != null) {
						--this.furnaceItemStacks[1].stackSize;
						if(this.furnaceItemStacks[1].stackSize == 0) {
							Item var3 = this.furnaceItemStacks[1].getItem().getContainerItem();
							this.furnaceItemStacks[1] = var3 != null ? new ItemStack(var3) : null;
						}
					}
				}
			}

			if(this.isBurning() && this.canSmelt()) {
				++this.furnaceCookTime;
				if(this.furnaceCookTime == 200) {
					this.furnaceCookTime = 0;
					this.smeltItem();
					var2 = true;
				}
			} else {
				this.furnaceCookTime = 0;
			}

			if(var1 != this.furnaceBurnTime > 0) {
				var2 = true;
				BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if(var2) {
			this.onInventoryChanged();
		}

	}

	private boolean canSmelt() {
		if(this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().itemID);
			return var1 == null ? false : (this.furnaceItemStacks[2] == null ? true : (!this.furnaceItemStacks[2].isItemEqual(var1) ? false : (this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit() && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize() ? true : this.furnaceItemStacks[2].stackSize < var1.getMaxStackSize())));
		}
	}

	public void smeltItem() {
		if(this.canSmelt()) {
			ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().itemID);
			if(this.furnaceItemStacks[2] == null) {
				this.furnaceItemStacks[2] = var1.copy();
			} else if(this.furnaceItemStacks[2].itemID == var1.itemID) {
				++this.furnaceItemStacks[2].stackSize;
			}

			--this.furnaceItemStacks[0].stackSize;
			if(this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}

		}
	}

	public static int getItemBurnTime(ItemStack var0) {
		if(var0 == null) {
			return 0;
		} else {
			int var1 = var0.getItem().itemID;
			Item var2 = var0.getItem();
			if(var1 < 256 && Block.blocksList[var1] != null) {
				Block var3 = Block.blocksList[var1];
				if(var3 == Block.woodSingleSlab) {
					return 150;
				}

				if(var3.blockMaterial == Material.wood) {
					return 300;
				}
			}

			return var2 instanceof ItemTool && ((ItemTool)var2).getToolMaterialName().equals("WOOD") ? 200 : (var2 instanceof ItemSword && ((ItemSword)var2).getToolMaterialName().equals("WOOD") ? 200 : (var2 instanceof ItemHoe && ((ItemHoe)var2).getMaterialName().equals("WOOD") ? 200 : (var1 == Item.stick.itemID ? 100 : (var1 == Item.coal.itemID ? 1600 : (var1 == Item.bucketLava.itemID ? 20000 : (var1 == Block.sapling.blockID ? 100 : (var1 == Item.blazeRod.itemID ? 2400 : 0)))))));
		}
	}

	public static boolean isItemFuel(ItemStack var0) {
		return getItemBurnTime(var0) > 0;
	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public boolean isStackValidForSlot(int var1, ItemStack var2) {
		return var1 == 2 ? false : (var1 == 1 ? isItemFuel(var2) : true);
	}

	public int[] getAccessibleSlotsFromSide(int var1) {
		return var1 == 0 ? field_102011_e : (var1 == 1 ? field_102010_d : field_102009_f);
	}

	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		return this.isStackValidForSlot(var1, var2);
	}

	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return var3 != 0 || var1 != 1 || var2.itemID == Item.bucketEmpty.itemID;
	}
}
