package net.minecraft.src;

import java.util.Calendar;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob {
	private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 0.25F, 20, 60, 15.0F);
	private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.31F, false);

	public EntitySkeleton(World var1) {
		super(var1);
		this.texture = "/mob/skeleton.png";
		this.moveSpeed = 0.25F;
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(3, new EntityAIFleeSun(this, this.moveSpeed));
		this.tasks.addTask(5, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
		if(var1 != null && !var1.isRemote) {
			this.setCombatTask();
		}

	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(13, new Byte((byte)0));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 20;
	}

	protected String getLivingSound() {
		return "mob.skeleton.say";
	}

	protected String getHurtSound() {
		return "mob.skeleton.hurt";
	}

	protected String getDeathSound() {
		return "mob.skeleton.death";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.skeleton.step", 0.15F, 1.0F);
	}

	public boolean attackEntityAsMob(Entity var1) {
		if(super.attackEntityAsMob(var1)) {
			if(this.getSkeletonType() == 1 && var1 instanceof EntityLiving) {
				((EntityLiving)var1).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
			}

			return true;
		} else {
			return false;
		}
	}

	public int getAttackStrength(Entity var1) {
		if(this.getSkeletonType() == 1) {
			ItemStack var2 = this.getHeldItem();
			int var3 = 4;
			if(var2 != null) {
				var3 += var2.getDamageVsEntity(this);
			}

			return var3;
		} else {
			return super.getAttackStrength(var1);
		}
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	public void onLivingUpdate() {
		if(this.worldObj.isDaytime() && !this.worldObj.isRemote) {
			float var1 = this.getBrightness(1.0F);
			if(var1 > 0.5F && this.rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
				boolean var2 = true;
				ItemStack var3 = this.getCurrentItemOrArmor(4);
				if(var3 != null) {
					if(var3.isItemStackDamageable()) {
						var3.setItemDamage(var3.getItemDamageForDisplay() + this.rand.nextInt(2));
						if(var3.getItemDamageForDisplay() >= var3.getMaxDamage()) {
							this.renderBrokenItemStack(var3);
							this.setCurrentItemOrArmor(4, (ItemStack)null);
						}
					}

					var2 = false;
				}

				if(var2) {
					this.setFire(8);
				}
			}
		}

		if(this.worldObj.isRemote && this.getSkeletonType() == 1) {
			this.setSize(0.72F, 2.34F);
		}

		super.onLivingUpdate();
	}

	public void onDeath(DamageSource var1) {
		super.onDeath(var1);
		if(var1.getSourceOfDamage() instanceof EntityArrow && var1.getEntity() instanceof EntityPlayer) {
			EntityPlayer var2 = (EntityPlayer)var1.getEntity();
			double var3 = var2.posX - this.posX;
			double var5 = var2.posZ - this.posZ;
			if(var3 * var3 + var5 * var5 >= 2500.0D) {
				var2.triggerAchievement(AchievementList.snipeSkeleton);
			}
		}

	}

	protected int getDropItemId() {
		return Item.arrow.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3;
		int var4;
		if(this.getSkeletonType() == 1) {
			var3 = this.rand.nextInt(3 + var2) - 1;

			for(var4 = 0; var4 < var3; ++var4) {
				this.dropItem(Item.coal.itemID, 1);
			}
		} else {
			var3 = this.rand.nextInt(3 + var2);

			for(var4 = 0; var4 < var3; ++var4) {
				this.dropItem(Item.arrow.itemID, 1);
			}
		}

		var3 = this.rand.nextInt(3 + var2);

		for(var4 = 0; var4 < var3; ++var4) {
			this.dropItem(Item.bone.itemID, 1);
		}

	}

	protected void dropRareDrop(int var1) {
		if(this.getSkeletonType() == 1) {
			this.entityDropItem(new ItemStack(Item.skull.itemID, 1, 1), 0.0F);
		}

	}

	protected void addRandomArmor() {
		super.addRandomArmor();
		this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
	}

	public String getTexture() {
		return this.getSkeletonType() == 1 ? "/mob/skeleton_wither.png" : super.getTexture();
	}

	public void initCreature() {
		if(this.worldObj.provider instanceof WorldProviderHell && this.getRNG().nextInt(5) > 0) {
			this.tasks.addTask(4, this.aiAttackOnCollide);
			this.setSkeletonType(1);
			this.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
		} else {
			this.tasks.addTask(4, this.aiArrowAttack);
			this.addRandomArmor();
			this.func_82162_bC();
		}

		this.setCanPickUpLoot(this.rand.nextFloat() < pickUpLootProability[this.worldObj.difficultySetting]);
		if(this.getCurrentItemOrArmor(4) == null) {
			Calendar var1 = this.worldObj.getCurrentDate();
			if(var1.get(2) + 1 == 10 && var1.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
				this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
				this.equipmentDropChances[4] = 0.0F;
			}
		}

	}

	public void setCombatTask() {
		this.tasks.removeTask(this.aiAttackOnCollide);
		this.tasks.removeTask(this.aiArrowAttack);
		ItemStack var1 = this.getHeldItem();
		if(var1 != null && var1.itemID == Item.bow.itemID) {
			this.tasks.addTask(4, this.aiArrowAttack);
		} else {
			this.tasks.addTask(4, this.aiAttackOnCollide);
		}

	}

	public void attackEntityWithRangedAttack(EntityLiving var1, float var2) {
		EntityArrow var3 = new EntityArrow(this.worldObj, this, var1, 1.6F, (float)(14 - this.worldObj.difficultySetting * 4));
		int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
		int var5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
		var3.setDamage((double)(var2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting * 0.11F));
		if(var4 > 0) {
			var3.setDamage(var3.getDamage() + (double)var4 * 0.5D + 0.5D);
		}

		if(var5 > 0) {
			var3.setKnockbackStrength(var5);
		}

		if(EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
			var3.setFire(100);
		}

		this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.worldObj.spawnEntityInWorld(var3);
	}

	public int getSkeletonType() {
		return this.dataWatcher.getWatchableObjectByte(13);
	}

	public void setSkeletonType(int var1) {
		this.dataWatcher.updateObject(13, Byte.valueOf((byte)var1));
		this.isImmuneToFire = var1 == 1;
		if(var1 == 1) {
			this.setSize(0.72F, 2.34F);
		} else {
			this.setSize(0.6F, 1.8F);
		}

	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		if(var1.hasKey("SkeletonType")) {
			byte var2 = var1.getByte("SkeletonType");
			this.setSkeletonType(var2);
		}

		this.setCombatTask();
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setByte("SkeletonType", (byte)this.getSkeletonType());
	}

	public void setCurrentItemOrArmor(int var1, ItemStack var2) {
		super.setCurrentItemOrArmor(var1, var2);
		if(!this.worldObj.isRemote && var1 == 0) {
			this.setCombatTask();
		}

	}
}
