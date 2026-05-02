package net.minecraft.src;

public class EntityMinecartFurnace extends EntityMinecart {
	private int fuel = 0;
	public double pushX;
	public double pushZ;

	public EntityMinecartFurnace(World var1) {
		super(var1);
	}

	public EntityMinecartFurnace(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6);
	}

	public int getMinecartType() {
		return 2;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte)0));
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.fuel > 0) {
			--this.fuel;
		}

		if(this.fuel <= 0) {
			this.pushX = this.pushZ = 0.0D;
		}

		this.setMinecartPowered(this.fuel > 0);
		if(this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
			this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

	}

	public void killMinecart(DamageSource var1) {
		super.killMinecart(var1);
		if(!var1.isExplosion()) {
			this.entityDropItem(new ItemStack(Block.furnaceIdle, 1), 0.0F);
		}

	}

	protected void updateOnTrack(int var1, int var2, int var3, double var4, double var6, int var8, int var9) {
		super.updateOnTrack(var1, var2, var3, var4, var6, var8, var9);
		double var10 = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if(var10 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
			var10 = (double)MathHelper.sqrt_double(var10);
			this.pushX /= var10;
			this.pushZ /= var10;
			if(this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
				this.pushX = 0.0D;
				this.pushZ = 0.0D;
			} else {
				this.pushX = this.motionX;
				this.pushZ = this.motionZ;
			}
		}

	}

	protected void applyDrag() {
		double var1 = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if(var1 > 1.0E-4D) {
			var1 = (double)MathHelper.sqrt_double(var1);
			this.pushX /= var1;
			this.pushZ /= var1;
			double var3 = 0.05D;
			this.motionX *= (double)0.8F;
			this.motionY *= 0.0D;
			this.motionZ *= (double)0.8F;
			this.motionX += this.pushX * var3;
			this.motionZ += this.pushZ * var3;
		} else {
			this.motionX *= (double)0.98F;
			this.motionY *= 0.0D;
			this.motionZ *= (double)0.98F;
		}

		super.applyDrag();
	}

	public boolean interact(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.getCurrentItem();
		if(var2 != null && var2.itemID == Item.coal.itemID) {
			if(--var2.stackSize == 0) {
				var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
			}

			this.fuel += 3600;
		}

		this.pushX = this.posX - var1.posX;
		this.pushZ = this.posZ - var1.posZ;
		return true;
	}

	protected void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setDouble("PushX", this.pushX);
		var1.setDouble("PushZ", this.pushZ);
		var1.setShort("Fuel", (short)this.fuel);
	}

	protected void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.pushX = var1.getDouble("PushX");
		this.pushZ = var1.getDouble("PushZ");
		this.fuel = var1.getShort("Fuel");
	}

	protected boolean isMinecartPowered() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	protected void setMinecartPowered(boolean var1) {
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) | 1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) & -2)));
		}

	}

	public Block getDefaultDisplayTile() {
		return Block.furnaceBurning;
	}

	public int getDefaultDisplayTileData() {
		return 2;
	}
}
