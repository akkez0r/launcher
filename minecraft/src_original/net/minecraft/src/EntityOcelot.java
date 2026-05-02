package net.minecraft.src;

public class EntityOcelot extends EntityTameable {
	private EntityAITempt aiTempt;

	public EntityOcelot(World var1) {
		super(var1);
		this.texture = "/mob/ozelot.png";
		this.setSize(0.6F, 0.8F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, this.aiTempt = new EntityAITempt(this, 0.18F, Item.fishRaw.itemID, true));
		this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityPlayer.class, 16.0F, 0.23F, 0.4F));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, 0.3F, 10.0F, 5.0F));
		this.tasks.addTask(6, new EntityAIOcelotSit(this, 0.4F));
		this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
		this.tasks.addTask(8, new EntityAIOcelotAttack(this));
		this.tasks.addTask(9, new EntityAIMate(this, 0.23F));
		this.tasks.addTask(10, new EntityAIWander(this, 0.23F));
		this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityChicken.class, 14.0F, 750, false));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(18, Byte.valueOf((byte)0));
	}

	public void updateAITick() {
		if(this.getMoveHelper().isUpdating()) {
			float var1 = this.getMoveHelper().getSpeed();
			if(var1 == 0.18F) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if(var1 == 0.4F) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}

	}

	protected boolean canDespawn() {
		return !this.isTamed();
	}

	public String getTexture() {
		switch(this.getTameSkin()) {
		case 0:
			return "/mob/ozelot.png";
		case 1:
			return "/mob/cat_black.png";
		case 2:
			return "/mob/cat_red.png";
		case 3:
			return "/mob/cat_siamese.png";
		default:
			return super.getTexture();
		}
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 10;
	}

	protected void fall(float var1) {
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("CatType", this.getTameSkin());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.setTameSkin(var1.getInteger("CatType"));
	}

	protected String getLivingSound() {
		return this.isTamed() ? (this.isInLove() ? "mob.cat.purr" : (this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
	}

	protected String getHurtSound() {
		return "mob.cat.hitt";
	}

	protected String getDeathSound() {
		return "mob.cat.hitt";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return Item.leather.itemID;
	}

	public boolean attackEntityAsMob(Entity var1) {
		return var1.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else {
			this.aiSit.setSitting(false);
			return super.attackEntityFrom(var1, var2);
		}
	}

	protected void dropFewItems(boolean var1, int var2) {
	}

	public boolean interact(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.getCurrentItem();
		if(this.isTamed()) {
			if(var1.username.equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(var2)) {
				this.aiSit.setSitting(!this.isSitting());
			}
		} else if(this.aiTempt.func_75277_f() && var2 != null && var2.itemID == Item.fishRaw.itemID && var1.getDistanceSqToEntity(this) < 9.0D) {
			if(!var1.capabilities.isCreativeMode) {
				--var2.stackSize;
			}

			if(var2.stackSize <= 0) {
				var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
			}

			if(!this.worldObj.isRemote) {
				if(this.rand.nextInt(3) == 0) {
					this.setTamed(true);
					this.setTameSkin(1 + this.worldObj.rand.nextInt(3));
					this.setOwner(var1.username);
					this.playTameEffect(true);
					this.aiSit.setSitting(true);
					this.worldObj.setEntityState(this, (byte)7);
				} else {
					this.playTameEffect(false);
					this.worldObj.setEntityState(this, (byte)6);
				}
			}

			return true;
		}

		return super.interact(var1);
	}

	public EntityOcelot spawnBabyAnimal(EntityAgeable var1) {
		EntityOcelot var2 = new EntityOcelot(this.worldObj);
		if(this.isTamed()) {
			var2.setOwner(this.getOwnerName());
			var2.setTamed(true);
			var2.setTameSkin(this.getTameSkin());
		}

		return var2;
	}

	public boolean isBreedingItem(ItemStack var1) {
		return var1 != null && var1.itemID == Item.fishRaw.itemID;
	}

	public boolean canMateWith(EntityAnimal var1) {
		if(var1 == this) {
			return false;
		} else if(!this.isTamed()) {
			return false;
		} else if(!(var1 instanceof EntityOcelot)) {
			return false;
		} else {
			EntityOcelot var2 = (EntityOcelot)var1;
			return !var2.isTamed() ? false : this.isInLove() && var2.isInLove();
		}
	}

	public int getTameSkin() {
		return this.dataWatcher.getWatchableObjectByte(18);
	}

	public void setTameSkin(int var1) {
		this.dataWatcher.updateObject(18, Byte.valueOf((byte)var1));
	}

	public boolean getCanSpawnHere() {
		if(this.worldObj.rand.nextInt(3) == 0) {
			return false;
		} else {
			if(this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox)) {
				int var1 = MathHelper.floor_double(this.posX);
				int var2 = MathHelper.floor_double(this.boundingBox.minY);
				int var3 = MathHelper.floor_double(this.posZ);
				if(var2 < 63) {
					return false;
				}

				int var4 = this.worldObj.getBlockId(var1, var2 - 1, var3);
				if(var4 == Block.grass.blockID || var4 == Block.leaves.blockID) {
					return true;
				}
			}

			return false;
		}
	}

	public String getEntityName() {
		return this.func_94056_bM() ? this.func_94057_bL() : (this.isTamed() ? "entity.Cat.name" : super.getEntityName());
	}

	public void initCreature() {
		if(this.worldObj.rand.nextInt(7) == 0) {
			for(int var1 = 0; var1 < 2; ++var1) {
				EntityOcelot var2 = new EntityOcelot(this.worldObj);
				var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
				var2.setGrowingAge(-24000);
				this.worldObj.spawnEntityInWorld(var2);
			}
		}

	}

	public EntityAgeable createChild(EntityAgeable var1) {
		return this.spawnBabyAnimal(var1);
	}
}
