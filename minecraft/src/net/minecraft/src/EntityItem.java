package net.minecraft.src;

import java.util.Iterator;

public class EntityItem extends Entity {
	public int age;
	public int delayBeforeCanPickup;
	private int health;
	public float hoverStart;

	public EntityItem(World var1, double var2, double var4, double var6) {
		super(var1);
		this.age = 0;
		this.health = 5;
		this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(var2, var4, var6);
		this.rotationYaw = (float)(Math.random() * 360.0D);
		this.motionX = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
		this.motionY = (double)0.2F;
		this.motionZ = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
	}

	public EntityItem(World var1, double var2, double var4, double var6, ItemStack var8) {
		this(var1, var2, var4, var6);
		this.setEntityItemStack(var8);
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public EntityItem(World var1) {
		super(var1);
		this.age = 0;
		this.health = 5;
		this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
	}

	protected void entityInit() {
		this.getDataWatcher().addObjectByDataType(10, 5);
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.delayBeforeCanPickup > 0) {
			--this.delayBeforeCanPickup;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double)0.04F;
		this.noClip = this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		boolean var1 = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
		if(var1 || this.ticksExisted % 25 == 0) {
			if(this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava) {
				this.motionY = (double)0.2F;
				this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
			}

			if(!this.worldObj.isRemote) {
				this.searchForOtherItemsNearby();
			}
		}

		float var2 = 0.98F;
		if(this.onGround) {
			var2 = 0.1F * 0.1F * 58.8F;
			int var3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
			if(var3 > 0) {
				var2 = Block.blocksList[var3].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double)var2;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)var2;
		if(this.onGround) {
			this.motionY *= -0.5D;
		}

		++this.age;
		if(!this.worldObj.isRemote && this.age >= 6000) {
			this.setDead();
		}

	}

	private void searchForOtherItemsNearby() {
		Iterator var1 = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

		while(var1.hasNext()) {
			EntityItem var2 = (EntityItem)var1.next();
			this.combineItems(var2);
		}

	}

	public boolean combineItems(EntityItem var1) {
		if(var1 == this) {
			return false;
		} else if(var1.isEntityAlive() && this.isEntityAlive()) {
			ItemStack var2 = this.getEntityItem();
			ItemStack var3 = var1.getEntityItem();
			if(var3.getItem() != var2.getItem()) {
				return false;
			} else if(var3.hasTagCompound() ^ var2.hasTagCompound()) {
				return false;
			} else if(var3.hasTagCompound() && !var3.getTagCompound().equals(var2.getTagCompound())) {
				return false;
			} else if(var3.getItem().getHasSubtypes() && var3.getItemDamage() != var2.getItemDamage()) {
				return false;
			} else if(var3.stackSize < var2.stackSize) {
				return var1.combineItems(this);
			} else if(var3.stackSize + var2.stackSize > var3.getMaxStackSize()) {
				return false;
			} else {
				var3.stackSize += var2.stackSize;
				var1.delayBeforeCanPickup = Math.max(var1.delayBeforeCanPickup, this.delayBeforeCanPickup);
				var1.age = Math.min(var1.age, this.age);
				var1.setEntityItemStack(var3);
				this.setDead();
				return true;
			}
		} else {
			return false;
		}
	}

	public void setAgeToCreativeDespawnTime() {
		this.age = 4800;
	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	protected void dealFireDamage(int var1) {
		this.attackEntityFrom(DamageSource.inFire, var1);
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else if(this.getEntityItem() != null && this.getEntityItem().itemID == Item.netherStar.itemID && var1.isExplosion()) {
			return false;
		} else {
			this.setBeenAttacked();
			this.health -= var2;
			if(this.health <= 0) {
				this.setDead();
			}

			return false;
		}
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		var1.setShort("Health", (short)((byte)this.health));
		var1.setShort("Age", (short)this.age);
		if(this.getEntityItem() != null) {
			var1.setCompoundTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
		}

	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		this.health = var1.getShort("Health") & 255;
		this.age = var1.getShort("Age");
		NBTTagCompound var2 = var1.getCompoundTag("Item");
		this.setEntityItemStack(ItemStack.loadItemStackFromNBT(var2));
		if(this.getEntityItem() == null) {
			this.setDead();
		}

	}

	public void onCollideWithPlayer(EntityPlayer var1) {
		if(!this.worldObj.isRemote) {
			ItemStack var2 = this.getEntityItem();
			int var3 = var2.stackSize;
			if(this.delayBeforeCanPickup == 0 && var1.inventory.addItemStackToInventory(var2)) {
				if(var2.itemID == Block.wood.blockID) {
					var1.triggerAchievement(AchievementList.mineWood);
				}

				if(var2.itemID == Item.leather.itemID) {
					var1.triggerAchievement(AchievementList.killCow);
				}

				if(var2.itemID == Item.diamond.itemID) {
					var1.triggerAchievement(AchievementList.diamonds);
				}

				if(var2.itemID == Item.blazeRod.itemID) {
					var1.triggerAchievement(AchievementList.blazeRod);
				}

				this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				var1.onItemPickup(this, var3);
				if(var2.stackSize <= 0) {
					this.setDead();
				}
			}

		}
	}

	public String getEntityName() {
		return StatCollector.translateToLocal("item." + this.getEntityItem().getItemName());
	}

	public boolean canAttackWithItem() {
		return false;
	}

	public void travelToDimension(int var1) {
		super.travelToDimension(var1);
		if(!this.worldObj.isRemote) {
			this.searchForOtherItemsNearby();
		}

	}

	public ItemStack getEntityItem() {
		ItemStack var1 = this.getDataWatcher().getWatchableObjectItemStack(10);
		if(var1 == null) {
			if(this.worldObj != null) {
				this.worldObj.getWorldLogAgent().logSevere("Item entity " + this.entityId + " has no item?!");
			}

			return new ItemStack(Block.stone);
		} else {
			return var1;
		}
	}

	public void setEntityItemStack(ItemStack var1) {
		this.getDataWatcher().updateObject(10, var1);
		this.getDataWatcher().setObjectWatched(10);
	}
}
