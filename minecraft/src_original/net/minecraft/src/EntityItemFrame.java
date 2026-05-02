package net.minecraft.src;

public class EntityItemFrame extends EntityHanging {
	private float itemDropChance = 1.0F;

	public EntityItemFrame(World var1) {
		super(var1);
	}

	public EntityItemFrame(World var1, int var2, int var3, int var4, int var5) {
		super(var1, var2, var3, var4, var5);
		this.setDirection(var5);
	}

	protected void entityInit() {
		this.getDataWatcher().addObjectByDataType(2, 5);
		this.getDataWatcher().addObject(3, Byte.valueOf((byte)0));
	}

	public int func_82329_d() {
		return 9;
	}

	public int func_82330_g() {
		return 9;
	}

	public boolean isInRangeToRenderDist(double var1) {
		double var3 = 16.0D;
		var3 *= 64.0D * this.renderDistanceWeight;
		return var1 < var3 * var3;
	}

	public void dropItemStack() {
		this.entityDropItem(new ItemStack(Item.itemFrame), 0.0F);
		ItemStack var1 = this.getDisplayedItem();
		if(var1 != null && this.rand.nextFloat() < this.itemDropChance) {
			var1 = var1.copy();
			var1.setItemFrame((EntityItemFrame)null);
			this.entityDropItem(var1, 0.0F);
		}

	}

	public ItemStack getDisplayedItem() {
		return this.getDataWatcher().getWatchableObjectItemStack(2);
	}

	public void setDisplayedItem(ItemStack var1) {
		var1 = var1.copy();
		var1.stackSize = 1;
		var1.setItemFrame(this);
		this.getDataWatcher().updateObject(2, var1);
		this.getDataWatcher().setObjectWatched(2);
	}

	public int getRotation() {
		return this.getDataWatcher().getWatchableObjectByte(3);
	}

	public void setItemRotation(int var1) {
		this.getDataWatcher().updateObject(3, Byte.valueOf((byte)(var1 % 4)));
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		if(this.getDisplayedItem() != null) {
			var1.setCompoundTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
			var1.setByte("ItemRotation", (byte)this.getRotation());
			var1.setFloat("ItemDropChance", this.itemDropChance);
		}

		super.writeEntityToNBT(var1);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		NBTTagCompound var2 = var1.getCompoundTag("Item");
		if(var2 != null && !var2.hasNoTags()) {
			this.setDisplayedItem(ItemStack.loadItemStackFromNBT(var2));
			this.setItemRotation(var1.getByte("ItemRotation"));
			if(var1.hasKey("ItemDropChance")) {
				this.itemDropChance = var1.getFloat("ItemDropChance");
			}
		}

		super.readEntityFromNBT(var1);
	}

	public boolean interact(EntityPlayer var1) {
		if(this.getDisplayedItem() == null) {
			ItemStack var2 = var1.getHeldItem();
			if(var2 != null && !this.worldObj.isRemote) {
				this.setDisplayedItem(var2);
				if(!var1.capabilities.isCreativeMode && --var2.stackSize <= 0) {
					var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
				}
			}
		} else if(!this.worldObj.isRemote) {
			this.setItemRotation(this.getRotation() + 1);
		}

		return true;
	}
}
