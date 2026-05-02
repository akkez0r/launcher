package net.minecraft.src;

public class EntityWolf extends EntityTameable {
	private float field_70926_e;
	private float field_70924_f;
	private boolean isShaking;
	private boolean field_70928_h;
	private float timeWolfIsShaking;
	private float prevTimeWolfIsShaking;

	public EntityWolf(World var1) {
		super(var1);
		this.texture = "/mob/wolf.png";
		this.setSize(0.6F, 0.8F);
		this.moveSpeed = 0.3F;
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, this.moveSpeed, true));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, this.moveSpeed, 10.0F, 2.0F));
		this.tasks.addTask(6, new EntityAIMate(this, this.moveSpeed));
		this.tasks.addTask(7, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySheep.class, 16.0F, 200, false));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public void setAttackTarget(EntityLiving var1) {
		super.setAttackTarget(var1);
		if(var1 instanceof EntityPlayer) {
			this.setAngry(true);
		}

	}

	protected void updateAITick() {
		this.dataWatcher.updateObject(18, Integer.valueOf(this.getHealth()));
	}

	public int getMaxHealth() {
		return this.isTamed() ? 20 : 8;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(18, new Integer(this.getHealth()));
		this.dataWatcher.addObject(19, new Byte((byte)0));
		this.dataWatcher.addObject(20, new Byte((byte)BlockCloth.getBlockFromDye(1)));
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.wolf.step", 0.15F, 1.0F);
	}

	public String getTexture() {
		return this.isTamed() ? "/mob/wolf_tame.png" : (this.isAngry() ? "/mob/wolf_angry.png" : super.getTexture());
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setBoolean("Angry", this.isAngry());
		var1.setByte("CollarColor", (byte)this.getCollarColor());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.setAngry(var1.getBoolean("Angry"));
		if(var1.hasKey("CollarColor")) {
			this.setCollarColor(var1.getByte("CollarColor"));
		}

	}

	protected boolean canDespawn() {
		return this.isAngry() && !this.isTamed();
	}

	protected String getLivingSound() {
		return this.isAngry() ? "mob.wolf.growl" : (this.rand.nextInt(3) == 0 ? (this.isTamed() && this.dataWatcher.getWatchableObjectInt(18) < 10 ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark");
	}

	protected String getHurtSound() {
		return "mob.wolf.hurt";
	}

	protected String getDeathSound() {
		return "mob.wolf.death";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return -1;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.worldObj.isRemote && this.isShaking && !this.field_70928_h && !this.hasPath() && this.onGround) {
			this.field_70928_h = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
			this.worldObj.setEntityState(this, (byte)8);
		}

	}

	public void onUpdate() {
		super.onUpdate();
		this.field_70924_f = this.field_70926_e;
		if(this.func_70922_bv()) {
			this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
		} else {
			this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
		}

		if(this.func_70922_bv()) {
			this.numTicksToChaseTarget = 10;
		}

		if(this.isWet()) {
			this.isShaking = true;
			this.field_70928_h = false;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else if((this.isShaking || this.field_70928_h) && this.field_70928_h) {
			if(this.timeWolfIsShaking == 0.0F) {
				this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
			this.timeWolfIsShaking += 0.05F;
			if(this.prevTimeWolfIsShaking >= 2.0F) {
				this.isShaking = false;
				this.field_70928_h = false;
				this.prevTimeWolfIsShaking = 0.0F;
				this.timeWolfIsShaking = 0.0F;
			}

			if(this.timeWolfIsShaking > 0.4F) {
				float var1 = (float)this.boundingBox.minY;
				int var2 = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

				for(int var3 = 0; var3 < var2; ++var3) {
					float var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.worldObj.spawnParticle("splash", this.posX + (double)var4, (double)(var1 + 0.8F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

	}

	public boolean getWolfShaking() {
		return this.isShaking;
	}

	public float getShadingWhileShaking(float var1) {
		return 12.0F / 16.0F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * var1) / 2.0F * 0.25F;
	}

	public float getShakeAngle(float var1, float var2) {
		float var3 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * var1 + var2) / 1.8F;
		if(var3 < 0.0F) {
			var3 = 0.0F;
		} else if(var3 > 1.0F) {
			var3 = 1.0F;
		}

		return MathHelper.sin(var3 * (float)Math.PI) * MathHelper.sin(var3 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
	}

	public float getInterestedAngle(float var1) {
		return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * var1) * 0.15F * (float)Math.PI;
	}

	public float getEyeHeight() {
		return this.height * 0.8F;
	}

	public int getVerticalFaceSpeed() {
		return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else {
			Entity var3 = var1.getEntity();
			this.aiSit.setSitting(false);
			if(var3 != null && !(var3 instanceof EntityPlayer) && !(var3 instanceof EntityArrow)) {
				var2 = (var2 + 1) / 2;
			}

			return super.attackEntityFrom(var1, var2);
		}
	}

	public boolean attackEntityAsMob(Entity var1) {
		int var2 = this.isTamed() ? 4 : 2;
		return var1.attackEntityFrom(DamageSource.causeMobDamage(this), var2);
	}

	public boolean interact(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.getCurrentItem();
		if(this.isTamed()) {
			if(var2 != null) {
				if(Item.itemsList[var2.itemID] instanceof ItemFood) {
					ItemFood var3 = (ItemFood)Item.itemsList[var2.itemID];
					if(var3.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectInt(18) < 20) {
						if(!var1.capabilities.isCreativeMode) {
							--var2.stackSize;
						}

						this.heal(var3.getHealAmount());
						if(var2.stackSize <= 0) {
							var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
						}

						return true;
					}
				} else if(var2.itemID == Item.dyePowder.itemID) {
					int var4 = BlockCloth.getBlockFromDye(var2.getItemDamage());
					if(var4 != this.getCollarColor()) {
						this.setCollarColor(var4);
						if(!var1.capabilities.isCreativeMode && --var2.stackSize <= 0) {
							var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
						}

						return true;
					}
				}
			}

			if(var1.username.equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(var2)) {
				this.aiSit.setSitting(!this.isSitting());
				this.isJumping = false;
				this.setPathToEntity((PathEntity)null);
			}
		} else if(var2 != null && var2.itemID == Item.bone.itemID && !this.isAngry()) {
			if(!var1.capabilities.isCreativeMode) {
				--var2.stackSize;
			}

			if(var2.stackSize <= 0) {
				var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
			}

			if(!this.worldObj.isRemote) {
				if(this.rand.nextInt(3) == 0) {
					this.setTamed(true);
					this.setPathToEntity((PathEntity)null);
					this.setAttackTarget((EntityLiving)null);
					this.aiSit.setSitting(true);
					this.setEntityHealth(20);
					this.setOwner(var1.username);
					this.playTameEffect(true);
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

	public void handleHealthUpdate(byte var1) {
		if(var1 == 8) {
			this.field_70928_h = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else {
			super.handleHealthUpdate(var1);
		}

	}

	public float getTailRotation() {
		return this.isAngry() ? (float)Math.PI * 0.49F : (this.isTamed() ? (0.55F - (float)(20 - this.dataWatcher.getWatchableObjectInt(18)) * 0.02F) * (float)Math.PI : (float)Math.PI * 0.2F);
	}

	public boolean isBreedingItem(ItemStack var1) {
		return var1 == null ? false : (!(Item.itemsList[var1.itemID] instanceof ItemFood) ? false : ((ItemFood)Item.itemsList[var1.itemID]).isWolfsFavoriteMeat());
	}

	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public boolean isAngry() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
	}

	public void setAngry(boolean var1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(16);
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 2)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -3)));
		}

	}

	public int getCollarColor() {
		return this.dataWatcher.getWatchableObjectByte(20) & 15;
	}

	public void setCollarColor(int var1) {
		this.dataWatcher.updateObject(20, Byte.valueOf((byte)(var1 & 15)));
	}

	public EntityWolf spawnBabyAnimal(EntityAgeable var1) {
		EntityWolf var2 = new EntityWolf(this.worldObj);
		String var3 = this.getOwnerName();
		if(var3 != null && var3.trim().length() > 0) {
			var2.setOwner(var3);
			var2.setTamed(true);
		}

		return var2;
	}

	public void func_70918_i(boolean var1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(19);
		if(var1) {
			this.dataWatcher.updateObject(19, Byte.valueOf((byte)1));
		} else {
			this.dataWatcher.updateObject(19, Byte.valueOf((byte)0));
		}

	}

	public boolean canMateWith(EntityAnimal var1) {
		if(var1 == this) {
			return false;
		} else if(!this.isTamed()) {
			return false;
		} else if(!(var1 instanceof EntityWolf)) {
			return false;
		} else {
			EntityWolf var2 = (EntityWolf)var1;
			return !var2.isTamed() ? false : (var2.isSitting() ? false : this.isInLove() && var2.isInLove());
		}
	}

	public boolean func_70922_bv() {
		return this.dataWatcher.getWatchableObjectByte(19) == 1;
	}

	public EntityAgeable createChild(EntityAgeable var1) {
		return this.spawnBabyAnimal(var1);
	}
}
