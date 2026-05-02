package net.minecraft.src;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class EntityLiving extends Entity {
	private static final float[] enchantmentProbability = new float[]{0.0F, 0.0F, 0.1F, 0.2F};
	private static final float[] armorEnchantmentProbability = new float[]{0.0F, 0.0F, 0.25F, 0.5F};
	private static final float[] armorProbability = new float[]{0.0F, 0.0F, 0.05F, 0.07F};
	public static final float[] pickUpLootProability = new float[]{0.0F, 0.1F, 0.15F, 0.45F};
	public int maxHurtResistantTime = 20;
	public float field_70769_ao;
	public float field_70770_ap;
	public float renderYawOffset = 0.0F;
	public float prevRenderYawOffset = 0.0F;
	public float rotationYawHead = 0.0F;
	public float prevRotationYawHead = 0.0F;
	protected float field_70768_au;
	protected float field_70766_av;
	protected float field_70764_aw;
	protected float field_70763_ax;
	protected boolean field_70753_ay = true;
	protected String texture = "/mob/char.png";
	protected boolean field_70740_aA = true;
	protected float field_70741_aB = 0.0F;
	protected String entityType = null;
	protected float field_70743_aD = 1.0F;
	protected int scoreValue = 0;
	protected float field_70745_aF = 0.0F;
	public float landMovementFactor = 0.1F;
	public float jumpMovementFactor = 0.02F;
	public float prevSwingProgress;
	public float swingProgress;
	protected int health = this.getMaxHealth();
	public int prevHealth;
	protected int carryoverDamage;
	public int livingSoundTime;
	public int hurtTime;
	public int maxHurtTime;
	public float attackedAtYaw = 0.0F;
	public int deathTime = 0;
	public int attackTime = 0;
	public float prevCameraPitch;
	public float cameraPitch;
	protected boolean dead = false;
	protected int experienceValue;
	public int field_70731_aW = -1;
	public float field_70730_aX = (float)(Math.random() * (double)0.9F + (double)0.1F);
	public float prevLimbYaw;
	public float limbYaw;
	public float limbSwing;
	protected EntityPlayer attackingPlayer = null;
	protected int recentlyHit = 0;
	private EntityLiving entityLivingToAttack = null;
	private int revengeTimer = 0;
	private EntityLiving lastAttackingEntity = null;
	public int arrowHitTimer = 0;
	protected HashMap activePotionsMap = new HashMap();
	private boolean potionsNeedUpdate = true;
	private int field_70748_f;
	private EntityLookHelper lookHelper;
	private EntityMoveHelper moveHelper;
	private EntityJumpHelper jumpHelper;
	private EntityBodyHelper bodyHelper;
	private PathNavigate navigator;
	protected final EntityAITasks tasks;
	protected final EntityAITasks targetTasks;
	private EntityLiving attackTarget;
	private EntitySenses senses;
	private float AIMoveSpeed;
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
	private float maximumHomeDistance = -1.0F;
	private ItemStack[] equipment = new ItemStack[5];
	protected float[] equipmentDropChances = new float[5];
	private ItemStack[] previousEquipment = new ItemStack[5];
	public boolean isSwingInProgress = false;
	public int swingProgressInt = 0;
	private boolean canPickUpLoot = false;
	private boolean persistenceRequired = false;
	protected final CombatTracker field_94063_bt = new CombatTracker(this);
	protected int newPosRotationIncrements;
	protected double newPosX;
	protected double newPosY;
	protected double newPosZ;
	protected double newRotationYaw;
	protected double newRotationPitch;
	float field_70706_bo = 0.0F;
	protected int lastDamage = 0;
	protected int entityAge = 0;
	protected float moveStrafing;
	protected float moveForward;
	protected float randomYawVelocity;
	protected boolean isJumping = false;
	protected float defaultPitch = 0.0F;
	protected float moveSpeed = 0.7F;
	private int jumpTicks = 0;
	private Entity currentTarget;
	protected int numTicksToChaseTarget = 0;

	public EntityLiving(World var1) {
		super(var1);
		this.preventEntitySpawning = true;
		this.tasks = new EntityAITasks(var1 != null && var1.theProfiler != null ? var1.theProfiler : null);
		this.targetTasks = new EntityAITasks(var1 != null && var1.theProfiler != null ? var1.theProfiler : null);
		this.lookHelper = new EntityLookHelper(this);
		this.moveHelper = new EntityMoveHelper(this);
		this.jumpHelper = new EntityJumpHelper(this);
		this.bodyHelper = new EntityBodyHelper(this);
		this.navigator = new PathNavigate(this, var1, (float)this.func_96121_ay());
		this.senses = new EntitySenses(this);
		this.field_70770_ap = (float)(Math.random() + 1.0D) * 0.01F;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.field_70769_ao = (float)Math.random() * 12398.0F;
		this.rotationYaw = (float)(Math.random() * (double)((float)Math.PI) * 2.0D);
		this.rotationYawHead = this.rotationYaw;

		for(int var2 = 0; var2 < this.equipmentDropChances.length; ++var2) {
			this.equipmentDropChances[var2] = 0.085F;
		}

		this.stepHeight = 0.5F;
	}

	protected int func_96121_ay() {
		return 16;
	}

	public EntityLookHelper getLookHelper() {
		return this.lookHelper;
	}

	public EntityMoveHelper getMoveHelper() {
		return this.moveHelper;
	}

	public EntityJumpHelper getJumpHelper() {
		return this.jumpHelper;
	}

	public PathNavigate getNavigator() {
		return this.navigator;
	}

	public EntitySenses getEntitySenses() {
		return this.senses;
	}

	public Random getRNG() {
		return this.rand;
	}

	public EntityLiving getAITarget() {
		return this.entityLivingToAttack;
	}

	public EntityLiving getLastAttackingEntity() {
		return this.lastAttackingEntity;
	}

	public void setLastAttackingEntity(Entity var1) {
		if(var1 instanceof EntityLiving) {
			this.lastAttackingEntity = (EntityLiving)var1;
		}

	}

	public int getAge() {
		return this.entityAge;
	}

	public float getRotationYawHead() {
		return this.rotationYawHead;
	}

	public void setRotationYawHead(float var1) {
		this.rotationYawHead = var1;
	}

	public float getAIMoveSpeed() {
		return this.AIMoveSpeed;
	}

	public void setAIMoveSpeed(float var1) {
		this.AIMoveSpeed = var1;
		this.setMoveForward(var1);
	}

	public boolean attackEntityAsMob(Entity var1) {
		this.setLastAttackingEntity(var1);
		return false;
	}

	public EntityLiving getAttackTarget() {
		return this.attackTarget;
	}

	public void setAttackTarget(EntityLiving var1) {
		this.attackTarget = var1;
	}

	public boolean canAttackClass(Class var1) {
		return EntityCreeper.class != var1 && EntityGhast.class != var1;
	}

	public void eatGrassBonus() {
	}

	protected void updateFallState(double var1, boolean var3) {
		if(!this.isInWater()) {
			this.handleWaterMovement();
		}

		if(var3 && this.fallDistance > 0.0F) {
			int var4 = MathHelper.floor_double(this.posX);
			int var5 = MathHelper.floor_double(this.posY - (double)0.2F - (double)this.yOffset);
			int var6 = MathHelper.floor_double(this.posZ);
			int var7 = this.worldObj.getBlockId(var4, var5, var6);
			if(var7 == 0) {
				int var8 = this.worldObj.blockGetRenderType(var4, var5 - 1, var6);
				if(var8 == 11 || var8 == 32 || var8 == 21) {
					var7 = this.worldObj.getBlockId(var4, var5 - 1, var6);
				}
			}

			if(var7 > 0) {
				Block.blocksList[var7].onFallenUpon(this.worldObj, var4, var5, var6, this, this.fallDistance);
			}
		}

		super.updateFallState(var1, var3);
	}

	public boolean isWithinHomeDistanceCurrentPosition() {
		return this.isWithinHomeDistance(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
	}

	public boolean isWithinHomeDistance(int var1, int var2, int var3) {
		return this.maximumHomeDistance == -1.0F ? true : this.homePosition.getDistanceSquared(var1, var2, var3) < this.maximumHomeDistance * this.maximumHomeDistance;
	}

	public void setHomeArea(int var1, int var2, int var3, int var4) {
		this.homePosition.set(var1, var2, var3);
		this.maximumHomeDistance = (float)var4;
	}

	public ChunkCoordinates getHomePosition() {
		return this.homePosition;
	}

	public float getMaximumHomeDistance() {
		return this.maximumHomeDistance;
	}

	public void detachHome() {
		this.maximumHomeDistance = -1.0F;
	}

	public boolean hasHome() {
		return this.maximumHomeDistance != -1.0F;
	}

	public void setRevengeTarget(EntityLiving var1) {
		this.entityLivingToAttack = var1;
		this.revengeTimer = this.entityLivingToAttack != null ? 100 : 0;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(8, Integer.valueOf(this.field_70748_f));
		this.dataWatcher.addObject(9, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(6, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(5, "");
	}

	public boolean canEntityBeSeen(Entity var1) {
		return this.worldObj.rayTraceBlocks(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), this.worldObj.getWorldVec3Pool().getVecFromPool(var1.posX, var1.posY + (double)var1.getEyeHeight(), var1.posZ)) == null;
	}

	public String getTexture() {
		return this.texture;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public boolean canBePushed() {
		return !this.isDead;
	}

	public float getEyeHeight() {
		return this.height * 0.85F;
	}

	public int getTalkInterval() {
		return 80;
	}

	public void playLivingSound() {
		String var1 = this.getLivingSound();
		if(var1 != null) {
			this.playSound(var1, this.getSoundVolume(), this.getSoundPitch());
		}

	}

	public void onEntityUpdate() {
		this.prevSwingProgress = this.swingProgress;
		super.onEntityUpdate();
		this.worldObj.theProfiler.startSection("mobBaseTick");
		if(this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
			this.livingSoundTime = -this.getTalkInterval();
			this.playLivingSound();
		}

		if(this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
			this.attackEntityFrom(DamageSource.inWall, 1);
		}

		if(this.isImmuneToFire() || this.worldObj.isRemote) {
			this.extinguish();
		}

		boolean var1 = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;
		if(this.isEntityAlive() && this.isInsideOfMaterial(Material.water) && !this.canBreatheUnderwater() && !this.activePotionsMap.containsKey(Integer.valueOf(Potion.waterBreathing.id)) && !var1) {
			this.setAir(this.decreaseAirSupply(this.getAir()));
			if(this.getAir() == -20) {
				this.setAir(0);

				for(int var2 = 0; var2 < 8; ++var2) {
					float var3 = this.rand.nextFloat() - this.rand.nextFloat();
					float var4 = this.rand.nextFloat() - this.rand.nextFloat();
					float var5 = this.rand.nextFloat() - this.rand.nextFloat();
					this.worldObj.spawnParticle("bubble", this.posX + (double)var3, this.posY + (double)var4, this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
				}

				this.attackEntityFrom(DamageSource.drown, 2);
			}

			this.extinguish();
		} else {
			this.setAir(300);
		}

		this.prevCameraPitch = this.cameraPitch;
		if(this.attackTime > 0) {
			--this.attackTime;
		}

		if(this.hurtTime > 0) {
			--this.hurtTime;
		}

		if(this.hurtResistantTime > 0) {
			--this.hurtResistantTime;
		}

		if(this.health <= 0) {
			this.onDeathUpdate();
		}

		if(this.recentlyHit > 0) {
			--this.recentlyHit;
		} else {
			this.attackingPlayer = null;
		}

		if(this.lastAttackingEntity != null && !this.lastAttackingEntity.isEntityAlive()) {
			this.lastAttackingEntity = null;
		}

		if(this.entityLivingToAttack != null) {
			if(!this.entityLivingToAttack.isEntityAlive()) {
				this.setRevengeTarget((EntityLiving)null);
			} else if(this.revengeTimer > 0) {
				--this.revengeTimer;
			} else {
				this.setRevengeTarget((EntityLiving)null);
			}
		}

		this.updatePotionEffects();
		this.field_70763_ax = this.field_70764_aw;
		this.prevRenderYawOffset = this.renderYawOffset;
		this.prevRotationYawHead = this.rotationYawHead;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
		this.worldObj.theProfiler.endSection();
	}

	protected void onDeathUpdate() {
		++this.deathTime;
		if(this.deathTime == 20) {
			int var1;
			if(!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && !this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
				var1 = this.getExperiencePoints(this.attackingPlayer);

				while(var1 > 0) {
					int var2 = EntityXPOrb.getXPSplit(var1);
					var1 -= var2;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
				}
			}

			this.setDead();

			for(var1 = 0; var1 < 20; ++var1) {
				double var8 = this.rand.nextGaussian() * 0.02D;
				double var4 = this.rand.nextGaussian() * 0.02D;
				double var6 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var8, var4, var6);
			}
		}

	}

	protected int decreaseAirSupply(int var1) {
		int var2 = EnchantmentHelper.getRespiration(this);
		return var2 > 0 && this.rand.nextInt(var2 + 1) > 0 ? var1 : var1 - 1;
	}

	protected int getExperiencePoints(EntityPlayer var1) {
		if(this.experienceValue > 0) {
			int var2 = this.experienceValue;
			ItemStack[] var3 = this.getLastActiveItems();

			for(int var4 = 0; var4 < var3.length; ++var4) {
				if(var3[var4] != null && this.equipmentDropChances[var4] <= 1.0F) {
					var2 += 1 + this.rand.nextInt(3);
				}
			}

			return var2;
		} else {
			return this.experienceValue;
		}
	}

	protected boolean isPlayer() {
		return false;
	}

	public void spawnExplosionParticle() {
		for(int var1 = 0; var1 < 20; ++var1) {
			double var2 = this.rand.nextGaussian() * 0.02D;
			double var4 = this.rand.nextGaussian() * 0.02D;
			double var6 = this.rand.nextGaussian() * 0.02D;
			double var8 = 10.0D;
			this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var2 * var8, this.posY + (double)(this.rand.nextFloat() * this.height) - var4 * var8, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var6 * var8, var2, var4, var6);
		}

	}

	public void updateRidden() {
		super.updateRidden();
		this.field_70768_au = this.field_70766_av;
		this.field_70766_av = 0.0F;
		this.fallDistance = 0.0F;
	}

	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.yOffset = 0.0F;
		this.newPosX = var1;
		this.newPosY = var3;
		this.newPosZ = var5;
		this.newRotationYaw = (double)var7;
		this.newRotationPitch = (double)var8;
		this.newPosRotationIncrements = var9;
	}

	public void onUpdate() {
		super.onUpdate();
		if(!this.worldObj.isRemote) {
			int var1;
			for(var1 = 0; var1 < 5; ++var1) {
				ItemStack var2 = this.getCurrentItemOrArmor(var1);
				if(!ItemStack.areItemStacksEqual(var2, this.previousEquipment[var1])) {
					((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet5PlayerInventory(this.entityId, var1, var2));
					this.previousEquipment[var1] = var2 == null ? null : var2.copy();
				}
			}

			var1 = this.getArrowCountInEntity();
			if(var1 > 0) {
				if(this.arrowHitTimer <= 0) {
					this.arrowHitTimer = 20 * (30 - var1);
				}

				--this.arrowHitTimer;
				if(this.arrowHitTimer <= 0) {
					this.setArrowCountInEntity(var1 - 1);
				}
			}
		}

		this.onLivingUpdate();
		double var12 = this.posX - this.prevPosX;
		double var3 = this.posZ - this.prevPosZ;
		float var5 = (float)(var12 * var12 + var3 * var3);
		float var6 = this.renderYawOffset;
		float var7 = 0.0F;
		this.field_70768_au = this.field_70766_av;
		float var8 = 0.0F;
		if(var5 > 0.0025000002F) {
			var8 = 1.0F;
			var7 = (float)Math.sqrt((double)var5) * 3.0F;
			var6 = (float)Math.atan2(var3, var12) * 180.0F / (float)Math.PI - 90.0F;
		}

		if(this.swingProgress > 0.0F) {
			var6 = this.rotationYaw;
		}

		if(!this.onGround) {
			var8 = 0.0F;
		}

		this.field_70766_av += (var8 - this.field_70766_av) * 0.3F;
		this.worldObj.theProfiler.startSection("headTurn");
		if(this.isAIEnabled()) {
			this.bodyHelper.func_75664_a();
		} else {
			float var9 = MathHelper.wrapAngleTo180_float(var6 - this.renderYawOffset);
			this.renderYawOffset += var9 * 0.3F;
			float var10 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
			boolean var11 = var10 < -90.0F || var10 >= 90.0F;
			if(var10 < -75.0F) {
				var10 = -75.0F;
			}

			if(var10 >= 75.0F) {
				var10 = 75.0F;
			}

			this.renderYawOffset = this.rotationYaw - var10;
			if(var10 * var10 > 2500.0F) {
				this.renderYawOffset += var10 * 0.2F;
			}

			if(var11) {
				var7 *= -1.0F;
			}
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("rangeChecks");

		while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		while(this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
			this.prevRenderYawOffset -= 360.0F;
		}

		while(this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
			this.prevRenderYawOffset += 360.0F;
		}

		while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while(this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
			this.prevRotationYawHead -= 360.0F;
		}

		while(this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
			this.prevRotationYawHead += 360.0F;
		}

		this.worldObj.theProfiler.endSection();
		this.field_70764_aw += var7;
	}

	public void heal(int var1) {
		if(this.health > 0) {
			this.setEntityHealth(this.getHealth() + var1);
			if(this.health > this.getMaxHealth()) {
				this.setEntityHealth(this.getMaxHealth());
			}

			this.hurtResistantTime = this.maxHurtResistantTime / 2;
		}
	}

	public abstract int getMaxHealth();

	public int getHealth() {
		return this.health;
	}

	public void setEntityHealth(int var1) {
		this.health = var1;
		if(var1 > this.getMaxHealth()) {
			var1 = this.getMaxHealth();
		}

	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else if(this.worldObj.isRemote) {
			return false;
		} else {
			this.entityAge = 0;
			if(this.health <= 0) {
				return false;
			} else if(var1.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
				return false;
			} else {
				if((var1 == DamageSource.anvil || var1 == DamageSource.fallingBlock) && this.getCurrentItemOrArmor(4) != null) {
					this.getCurrentItemOrArmor(4).damageItem(var2 * 4 + this.rand.nextInt(var2 * 2), this);
					var2 = (int)((float)var2 * (12.0F / 16.0F));
				}

				this.limbYaw = 1.5F;
				boolean var3 = true;
				if((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F) {
					if(var2 <= this.lastDamage) {
						return false;
					}

					this.damageEntity(var1, var2 - this.lastDamage);
					this.lastDamage = var2;
					var3 = false;
				} else {
					this.lastDamage = var2;
					this.prevHealth = this.health;
					this.hurtResistantTime = this.maxHurtResistantTime;
					this.damageEntity(var1, var2);
					this.hurtTime = this.maxHurtTime = 10;
				}

				this.attackedAtYaw = 0.0F;
				Entity var4 = var1.getEntity();
				if(var4 != null) {
					if(var4 instanceof EntityLiving) {
						this.setRevengeTarget((EntityLiving)var4);
					}

					if(var4 instanceof EntityPlayer) {
						this.recentlyHit = 100;
						this.attackingPlayer = (EntityPlayer)var4;
					} else if(var4 instanceof EntityWolf) {
						EntityWolf var5 = (EntityWolf)var4;
						if(var5.isTamed()) {
							this.recentlyHit = 100;
							this.attackingPlayer = null;
						}
					}
				}

				if(var3) {
					this.worldObj.setEntityState(this, (byte)2);
					if(var1 != DamageSource.drown) {
						this.setBeenAttacked();
					}

					if(var4 != null) {
						double var9 = var4.posX - this.posX;

						double var7;
						for(var7 = var4.posZ - this.posZ; var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D) {
							var9 = (Math.random() - Math.random()) * 0.01D;
						}

						this.attackedAtYaw = (float)(Math.atan2(var7, var9) * 180.0D / (double)((float)Math.PI)) - this.rotationYaw;
						this.knockBack(var4, var2, var9, var7);
					} else {
						this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
					}
				}

				if(this.health <= 0) {
					if(var3) {
						this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(var1);
				} else if(var3) {
					this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
				}

				return true;
			}
		}
	}

	protected float getSoundPitch() {
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
	}

	public void performHurtAnimation() {
		this.hurtTime = this.maxHurtTime = 10;
		this.attackedAtYaw = 0.0F;
	}

	public int getTotalArmorValue() {
		int var1 = 0;
		ItemStack[] var2 = this.getLastActiveItems();
		int var3 = var2.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];
			if(var5 != null && var5.getItem() instanceof ItemArmor) {
				int var6 = ((ItemArmor)var5.getItem()).damageReduceAmount;
				var1 += var6;
			}
		}

		return var1;
	}

	protected void damageArmor(int var1) {
	}

	protected int applyArmorCalculations(DamageSource var1, int var2) {
		if(!var1.isUnblockable()) {
			int var3 = 25 - this.getTotalArmorValue();
			int var4 = var2 * var3 + this.carryoverDamage;
			this.damageArmor(var2);
			var2 = var4 / 25;
			this.carryoverDamage = var4 % 25;
		}

		return var2;
	}

	protected int applyPotionDamageCalculations(DamageSource var1, int var2) {
		int var3;
		int var4;
		int var5;
		if(this.isPotionActive(Potion.resistance)) {
			var3 = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
			var4 = 25 - var3;
			var5 = var2 * var4 + this.carryoverDamage;
			var2 = var5 / 25;
			this.carryoverDamage = var5 % 25;
		}

		if(var2 <= 0) {
			return 0;
		} else {
			var3 = EnchantmentHelper.getEnchantmentModifierDamage(this.getLastActiveItems(), var1);
			if(var3 > 20) {
				var3 = 20;
			}

			if(var3 > 0 && var3 <= 20) {
				var4 = 25 - var3;
				var5 = var2 * var4 + this.carryoverDamage;
				var2 = var5 / 25;
				this.carryoverDamage = var5 % 25;
			}

			return var2;
		}
	}

	protected void damageEntity(DamageSource var1, int var2) {
		if(!this.isEntityInvulnerable()) {
			var2 = this.applyArmorCalculations(var1, var2);
			var2 = this.applyPotionDamageCalculations(var1, var2);
			int var3 = this.getHealth();
			this.health -= var2;
			this.field_94063_bt.func_94547_a(var1, var3, var2);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected String getLivingSound() {
		return null;
	}

	protected String getHurtSound() {
		return "damage.hit";
	}

	protected String getDeathSound() {
		return "damage.hit";
	}

	public void knockBack(Entity var1, int var2, double var3, double var5) {
		this.isAirBorne = true;
		float var7 = MathHelper.sqrt_double(var3 * var3 + var5 * var5);
		float var8 = 0.4F;
		this.motionX /= 2.0D;
		this.motionY /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= var3 / (double)var7 * (double)var8;
		this.motionY += (double)var8;
		this.motionZ -= var5 / (double)var7 * (double)var8;
		if(this.motionY > (double)0.4F) {
			this.motionY = (double)0.4F;
		}

	}

	public void onDeath(DamageSource var1) {
		Entity var2 = var1.getEntity();
		EntityLiving var3 = this.func_94060_bK();
		if(this.scoreValue >= 0 && var3 != null) {
			var3.addToPlayerScore(this, this.scoreValue);
		}

		if(var2 != null) {
			var2.onKillEntity(this);
		}

		this.dead = true;
		if(!this.worldObj.isRemote) {
			int var4 = 0;
			if(var2 instanceof EntityPlayer) {
				var4 = EnchantmentHelper.getLootingModifier((EntityLiving)var2);
			}

			if(!this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
				this.dropFewItems(this.recentlyHit > 0, var4);
				this.dropEquipment(this.recentlyHit > 0, var4);
				if(this.recentlyHit > 0) {
					int var5 = this.rand.nextInt(200) - var4;
					if(var5 < 5) {
						this.dropRareDrop(var5 <= 0 ? 1 : 0);
					}
				}
			}
		}

		this.worldObj.setEntityState(this, (byte)3);
	}

	protected void dropRareDrop(int var1) {
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.getDropItemId();
		if(var3 > 0) {
			int var4 = this.rand.nextInt(3);
			if(var2 > 0) {
				var4 += this.rand.nextInt(var2 + 1);
			}

			for(int var5 = 0; var5 < var4; ++var5) {
				this.dropItem(var3, 1);
			}
		}

	}

	protected int getDropItemId() {
		return 0;
	}

	protected void fall(float var1) {
		super.fall(var1);
		int var2 = MathHelper.ceiling_float_int(var1 - 3.0F);
		if(var2 > 0) {
			if(var2 > 4) {
				this.playSound("damage.fallbig", 1.0F, 1.0F);
			} else {
				this.playSound("damage.fallsmall", 1.0F, 1.0F);
			}

			this.attackEntityFrom(DamageSource.fall, var2);
			int var3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - (double)0.2F - (double)this.yOffset), MathHelper.floor_double(this.posZ));
			if(var3 > 0) {
				StepSound var4 = Block.blocksList[var3].stepSound;
				this.playSound(var4.getStepSound(), var4.getVolume() * 0.5F, var4.getPitch() * (12.0F / 16.0F));
			}
		}

	}

	public void moveEntityWithHeading(float var1, float var2) {
		double var3;
		if(!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
			if(!this.handleLavaMovement() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
				float var8 = 0.91F;
				if(this.onGround) {
					var8 = 546.0F * 0.1F * 0.1F * 0.1F;
					int var4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
					if(var4 > 0) {
						var8 = Block.blocksList[var4].slipperiness * 0.91F;
					}
				}

				float var9 = 0.16277136F / (var8 * var8 * var8);
				float var5;
				if(this.onGround) {
					if(this.isAIEnabled()) {
						var5 = this.getAIMoveSpeed();
					} else {
						var5 = this.landMovementFactor;
					}

					var5 *= var9;
				} else {
					var5 = this.jumpMovementFactor;
				}

				this.moveFlying(var1, var2, var5);
				var8 = 0.91F;
				if(this.onGround) {
					var8 = 546.0F * 0.1F * 0.1F * 0.1F;
					int var6 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
					if(var6 > 0) {
						var8 = Block.blocksList[var6].slipperiness * 0.91F;
					}
				}

				if(this.isOnLadder()) {
					float var10 = 0.15F;
					if(this.motionX < (double)(-var10)) {
						this.motionX = (double)(-var10);
					}

					if(this.motionX > (double)var10) {
						this.motionX = (double)var10;
					}

					if(this.motionZ < (double)(-var10)) {
						this.motionZ = (double)(-var10);
					}

					if(this.motionZ > (double)var10) {
						this.motionZ = (double)var10;
					}

					this.fallDistance = 0.0F;
					if(this.motionY < -0.15D) {
						this.motionY = -0.15D;
					}

					boolean var7 = this.isSneaking() && this instanceof EntityPlayer;
					if(var7 && this.motionY < 0.0D) {
						this.motionY = 0.0D;
					}
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.isCollidedHorizontally && this.isOnLadder()) {
					this.motionY = 0.2D;
				}

				if(this.worldObj.isRemote && (!this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ) || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded)) {
					if(this.posY > 0.0D) {
						this.motionY = -0.1D;
					} else {
						this.motionY = 0.0D;
					}
				} else {
					this.motionY -= 0.08D;
				}

				this.motionY *= (double)0.98F;
				this.motionX *= (double)var8;
				this.motionZ *= (double)var8;
			} else {
				var3 = this.posY;
				this.moveFlying(var1, var2, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
				this.motionY -= 0.02D;
				if(this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + var3, this.motionZ)) {
					this.motionY = (double)0.3F;
				}
			}
		} else {
			var3 = this.posY;
			this.moveFlying(var1, var2, this.isAIEnabled() ? 0.04F : 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.8F;
			this.motionY *= (double)0.8F;
			this.motionZ *= (double)0.8F;
			this.motionY -= 0.02D;
			if(this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + var3, this.motionZ)) {
				this.motionY = (double)0.3F;
			}
		}

		this.prevLimbYaw = this.limbYaw;
		var3 = this.posX - this.prevPosX;
		double var11 = this.posZ - this.prevPosZ;
		float var12 = MathHelper.sqrt_double(var3 * var3 + var11 * var11) * 4.0F;
		if(var12 > 1.0F) {
			var12 = 1.0F;
		}

		this.limbYaw += (var12 - this.limbYaw) * 0.4F;
		this.limbSwing += this.limbYaw;
	}

	public boolean isOnLadder() {
		int var1 = MathHelper.floor_double(this.posX);
		int var2 = MathHelper.floor_double(this.boundingBox.minY);
		int var3 = MathHelper.floor_double(this.posZ);
		int var4 = this.worldObj.getBlockId(var1, var2, var3);
		return var4 == Block.ladder.blockID || var4 == Block.vine.blockID;
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		if(this.health < Short.MIN_VALUE) {
			this.health = Short.MIN_VALUE;
		}

		var1.setShort("Health", (short)this.health);
		var1.setShort("HurtTime", (short)this.hurtTime);
		var1.setShort("DeathTime", (short)this.deathTime);
		var1.setShort("AttackTime", (short)this.attackTime);
		var1.setBoolean("CanPickUpLoot", this.canPickUpLoot());
		var1.setBoolean("PersistenceRequired", this.persistenceRequired);
		NBTTagList var2 = new NBTTagList();

		for(int var3 = 0; var3 < this.equipment.length; ++var3) {
			NBTTagCompound var4 = new NBTTagCompound();
			if(this.equipment[var3] != null) {
				this.equipment[var3].writeToNBT(var4);
			}

			var2.appendTag(var4);
		}

		var1.setTag("Equipment", var2);
		NBTTagList var6;
		if(!this.activePotionsMap.isEmpty()) {
			var6 = new NBTTagList();
			Iterator var7 = this.activePotionsMap.values().iterator();

			while(var7.hasNext()) {
				PotionEffect var5 = (PotionEffect)var7.next();
				var6.appendTag(var5.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}

			var1.setTag("ActiveEffects", var6);
		}

		var6 = new NBTTagList();

		for(int var8 = 0; var8 < this.equipmentDropChances.length; ++var8) {
			var6.appendTag(new NBTTagFloat(var8 + "", this.equipmentDropChances[var8]));
		}

		var1.setTag("DropChances", var6);
		var1.setString("CustomName", this.func_94057_bL());
		var1.setBoolean("CustomNameVisible", this.func_94062_bN());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		this.health = var1.getShort("Health");
		if(!var1.hasKey("Health")) {
			this.health = this.getMaxHealth();
		}

		this.hurtTime = var1.getShort("HurtTime");
		this.deathTime = var1.getShort("DeathTime");
		this.attackTime = var1.getShort("AttackTime");
		this.setCanPickUpLoot(var1.getBoolean("CanPickUpLoot"));
		this.persistenceRequired = var1.getBoolean("PersistenceRequired");
		if(var1.hasKey("CustomName") && var1.getString("CustomName").length() > 0) {
			this.func_94058_c(var1.getString("CustomName"));
		}

		this.func_94061_f(var1.getBoolean("CustomNameVisible"));
		NBTTagList var2;
		int var3;
		if(var1.hasKey("Equipment")) {
			var2 = var1.getTagList("Equipment");

			for(var3 = 0; var3 < this.equipment.length; ++var3) {
				this.equipment[var3] = ItemStack.loadItemStackFromNBT((NBTTagCompound)var2.tagAt(var3));
			}
		}

		if(var1.hasKey("ActiveEffects")) {
			var2 = var1.getTagList("ActiveEffects");

			for(var3 = 0; var3 < var2.tagCount(); ++var3) {
				NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
				PotionEffect var5 = PotionEffect.readCustomPotionEffectFromNBT(var4);
				this.activePotionsMap.put(Integer.valueOf(var5.getPotionID()), var5);
			}
		}

		if(var1.hasKey("DropChances")) {
			var2 = var1.getTagList("DropChances");

			for(var3 = 0; var3 < var2.tagCount(); ++var3) {
				this.equipmentDropChances[var3] = ((NBTTagFloat)var2.tagAt(var3)).data;
			}
		}

	}

	public boolean isEntityAlive() {
		return !this.isDead && this.health > 0;
	}

	public boolean canBreatheUnderwater() {
		return false;
	}

	public void setMoveForward(float var1) {
		this.moveForward = var1;
	}

	public void setJumping(boolean var1) {
		this.isJumping = var1;
	}

	public void onLivingUpdate() {
		if(this.jumpTicks > 0) {
			--this.jumpTicks;
		}

		if(this.newPosRotationIncrements > 0) {
			double var1 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
			double var3 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
			double var5 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
			double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
			this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.newPosRotationIncrements);
			this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(var1, var3, var5);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else if(!this.isClientWorld()) {
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
		}

		if(Math.abs(this.motionX) < 0.005D) {
			this.motionX = 0.0D;
		}

		if(Math.abs(this.motionY) < 0.005D) {
			this.motionY = 0.0D;
		}

		if(Math.abs(this.motionZ) < 0.005D) {
			this.motionZ = 0.0D;
		}

		this.worldObj.theProfiler.startSection("ai");
		if(this.isMovementBlocked()) {
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		} else if(this.isClientWorld()) {
			if(this.isAIEnabled()) {
				this.worldObj.theProfiler.startSection("newAi");
				this.updateAITasks();
				this.worldObj.theProfiler.endSection();
			} else {
				this.worldObj.theProfiler.startSection("oldAi");
				this.updateEntityActionState();
				this.worldObj.theProfiler.endSection();
				this.rotationYawHead = this.rotationYaw;
			}
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("jump");
		if(this.isJumping) {
			if(!this.isInWater() && !this.handleLavaMovement()) {
				if(this.onGround && this.jumpTicks == 0) {
					this.jump();
					this.jumpTicks = 10;
				}
			} else {
				this.motionY += (double)0.04F;
			}
		} else {
			this.jumpTicks = 0;
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("travel");
		this.moveStrafing *= 0.98F;
		this.moveForward *= 0.98F;
		this.randomYawVelocity *= 0.9F;
		float var11 = this.landMovementFactor;
		this.landMovementFactor *= this.getSpeedModifier();
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
		this.landMovementFactor = var11;
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("push");
		if(!this.worldObj.isRemote) {
			this.func_85033_bc();
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("looting");
		if(!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
			List var2 = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
			Iterator var12 = var2.iterator();

			label159:
			while(true) {
				EntityItem var4;
				int var6;
				ItemStack var13;
				do {
					do {
						do {
							if(!var12.hasNext()) {
								break label159;
							}

							var4 = (EntityItem)var12.next();
						} while(var4.isDead);
					} while(var4.getEntityItem() == null);

					var13 = var4.getEntityItem();
					var6 = getArmorPosition(var13);
				} while(var6 <= -1);

				boolean var14 = true;
				ItemStack var8 = this.getCurrentItemOrArmor(var6);
				if(var8 != null) {
					if(var6 == 0) {
						if(var13.getItem() instanceof ItemSword && !(var8.getItem() instanceof ItemSword)) {
							var14 = true;
						} else if(var13.getItem() instanceof ItemSword && var8.getItem() instanceof ItemSword) {
							ItemSword var15 = (ItemSword)var13.getItem();
							ItemSword var16 = (ItemSword)var8.getItem();
							if(var15.func_82803_g() != var16.func_82803_g()) {
								var14 = var15.func_82803_g() > var16.func_82803_g();
							} else {
								var14 = var13.getItemDamage() > var8.getItemDamage() || var13.hasTagCompound() && !var8.hasTagCompound();
							}
						} else {
							var14 = false;
						}
					} else if(var13.getItem() instanceof ItemArmor && !(var8.getItem() instanceof ItemArmor)) {
						var14 = true;
					} else if(var13.getItem() instanceof ItemArmor && var8.getItem() instanceof ItemArmor) {
						ItemArmor var9 = (ItemArmor)var13.getItem();
						ItemArmor var10 = (ItemArmor)var8.getItem();
						if(var9.damageReduceAmount != var10.damageReduceAmount) {
							var14 = var9.damageReduceAmount > var10.damageReduceAmount;
						} else {
							var14 = var13.getItemDamage() > var8.getItemDamage() || var13.hasTagCompound() && !var8.hasTagCompound();
						}
					} else {
						var14 = false;
					}
				}

				if(var14) {
					if(var8 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[var6]) {
						this.entityDropItem(var8, 0.0F);
					}

					this.setCurrentItemOrArmor(var6, var13);
					this.equipmentDropChances[var6] = 2.0F;
					this.persistenceRequired = true;
					this.onItemPickup(var4, 1);
					var4.setDead();
				}
			}
		}

		this.worldObj.theProfiler.endSection();
	}

	protected void func_85033_bc() {
		List var1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)0.2F, 0.0D, (double)0.2F));
		if(var1 != null && !var1.isEmpty()) {
			for(int var2 = 0; var2 < var1.size(); ++var2) {
				Entity var3 = (Entity)var1.get(var2);
				if(var3.canBePushed()) {
					this.collideWithEntity(var3);
				}
			}
		}

	}

	protected void collideWithEntity(Entity var1) {
		var1.applyEntityCollision(this);
	}

	protected boolean isAIEnabled() {
		return false;
	}

	protected boolean isClientWorld() {
		return !this.worldObj.isRemote;
	}

	protected boolean isMovementBlocked() {
		return this.health <= 0;
	}

	public boolean isBlocking() {
		return false;
	}

	protected void jump() {
		this.motionY = (double)0.42F;
		if(this.isPotionActive(Potion.jump)) {
			this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
		}

		if(this.isSprinting()) {
			float var1 = this.rotationYaw * ((float)Math.PI / 180.0F);
			this.motionX -= (double)(MathHelper.sin(var1) * 0.2F);
			this.motionZ += (double)(MathHelper.cos(var1) * 0.2F);
		}

		this.isAirBorne = true;
	}

	protected boolean canDespawn() {
		return true;
	}

	protected void despawnEntity() {
		if(!this.persistenceRequired) {
			EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, -1.0D);
			if(var1 != null) {
				double var2 = var1.posX - this.posX;
				double var4 = var1.posY - this.posY;
				double var6 = var1.posZ - this.posZ;
				double var8 = var2 * var2 + var4 * var4 + var6 * var6;
				if(this.canDespawn() && var8 > 16384.0D) {
					this.setDead();
				}

				if(this.entityAge > 600 && this.rand.nextInt(800) == 0 && var8 > 1024.0D && this.canDespawn()) {
					this.setDead();
				} else if(var8 < 1024.0D) {
					this.entityAge = 0;
				}
			}

		}
	}

	protected void updateAITasks() {
		++this.entityAge;
		this.worldObj.theProfiler.startSection("checkDespawn");
		this.despawnEntity();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("sensing");
		this.senses.clearSensingCache();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("targetSelector");
		this.targetTasks.onUpdateTasks();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("goalSelector");
		this.tasks.onUpdateTasks();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("navigation");
		this.navigator.onUpdateNavigation();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("mob tick");
		this.updateAITick();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("controls");
		this.worldObj.theProfiler.startSection("move");
		this.moveHelper.onUpdateMoveHelper();
		this.worldObj.theProfiler.endStartSection("look");
		this.lookHelper.onUpdateLook();
		this.worldObj.theProfiler.endStartSection("jump");
		this.jumpHelper.doJump();
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.endSection();
	}

	protected void updateAITick() {
	}

	protected void updateEntityActionState() {
		++this.entityAge;
		this.despawnEntity();
		this.moveStrafing = 0.0F;
		this.moveForward = 0.0F;
		float var1 = 8.0F;
		if(this.rand.nextFloat() < 0.02F) {
			EntityPlayer var2 = this.worldObj.getClosestPlayerToEntity(this, (double)var1);
			if(var2 != null) {
				this.currentTarget = var2;
				this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
			} else {
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
			}
		}

		if(this.currentTarget != null) {
			this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());
			if(this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double)(var1 * var1)) {
				this.currentTarget = null;
			}
		} else {
			if(this.rand.nextFloat() < 0.05F) {
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
			}

			this.rotationYaw += this.randomYawVelocity;
			this.rotationPitch = this.defaultPitch;
		}

		boolean var4 = this.isInWater();
		boolean var3 = this.handleLavaMovement();
		if(var4 || var3) {
			this.isJumping = this.rand.nextFloat() < 0.8F;
		}

	}

	protected void updateArmSwingProgress() {
		int var1 = this.getArmSwingAnimationEnd();
		if(this.isSwingInProgress) {
			++this.swingProgressInt;
			if(this.swingProgressInt >= var1) {
				this.swingProgressInt = 0;
				this.isSwingInProgress = false;
			}
		} else {
			this.swingProgressInt = 0;
		}

		this.swingProgress = (float)this.swingProgressInt / (float)var1;
	}

	public int getVerticalFaceSpeed() {
		return 40;
	}

	public void faceEntity(Entity var1, float var2, float var3) {
		double var4 = var1.posX - this.posX;
		double var8 = var1.posZ - this.posZ;
		double var6;
		if(var1 instanceof EntityLiving) {
			EntityLiving var10 = (EntityLiving)var1;
			var6 = var10.posY + (double)var10.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
		} else {
			var6 = (var1.boundingBox.minY + var1.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
		}

		double var14 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
		float var12 = (float)(Math.atan2(var8, var4) * 180.0D / (double)((float)Math.PI)) - 90.0F;
		float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / (double)((float)Math.PI)));
		this.rotationPitch = this.updateRotation(this.rotationPitch, var13, var3);
		this.rotationYaw = this.updateRotation(this.rotationYaw, var12, var2);
	}

	private float updateRotation(float var1, float var2, float var3) {
		float var4 = MathHelper.wrapAngleTo180_float(var2 - var1);
		if(var4 > var3) {
			var4 = var3;
		}

		if(var4 < -var3) {
			var4 = -var3;
		}

		return var1 + var4;
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	}

	protected void kill() {
		this.attackEntityFrom(DamageSource.outOfWorld, 4);
	}

	public float getSwingProgress(float var1) {
		float var2 = this.swingProgress - this.prevSwingProgress;
		if(var2 < 0.0F) {
			++var2;
		}

		return this.prevSwingProgress + var2 * var1;
	}

	public Vec3 getPosition(float var1) {
		if(var1 == 1.0F) {
			return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
		} else {
			double var2 = this.prevPosX + (this.posX - this.prevPosX) * (double)var1;
			double var4 = this.prevPosY + (this.posY - this.prevPosY) * (double)var1;
			double var6 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var1;
			return this.worldObj.getWorldVec3Pool().getVecFromPool(var2, var4, var6);
		}
	}

	public Vec3 getLookVec() {
		return this.getLook(1.0F);
	}

	public Vec3 getLook(float var1) {
		float var2;
		float var3;
		float var4;
		float var5;
		if(var1 == 1.0F) {
			var2 = MathHelper.cos(-this.rotationYaw * ((float)Math.PI / 180.0F) - (float)Math.PI);
			var3 = MathHelper.sin(-this.rotationYaw * ((float)Math.PI / 180.0F) - (float)Math.PI);
			var4 = -MathHelper.cos(-this.rotationPitch * ((float)Math.PI / 180.0F));
			var5 = MathHelper.sin(-this.rotationPitch * ((float)Math.PI / 180.0F));
			return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var3 * var4), (double)var5, (double)(var2 * var4));
		} else {
			var2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * var1;
			var3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * var1;
			var4 = MathHelper.cos(-var3 * ((float)Math.PI / 180.0F) - (float)Math.PI);
			var5 = MathHelper.sin(-var3 * ((float)Math.PI / 180.0F) - (float)Math.PI);
			float var6 = -MathHelper.cos(-var2 * ((float)Math.PI / 180.0F));
			float var7 = MathHelper.sin(-var2 * ((float)Math.PI / 180.0F));
			return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var5 * var6), (double)var7, (double)(var4 * var6));
		}
	}

	public float getRenderSizeModifier() {
		return 1.0F;
	}

	public MovingObjectPosition rayTrace(double var1, float var3) {
		Vec3 var4 = this.getPosition(var3);
		Vec3 var5 = this.getLook(var3);
		Vec3 var6 = var4.addVector(var5.xCoord * var1, var5.yCoord * var1, var5.zCoord * var1);
		return this.worldObj.rayTraceBlocks(var4, var6);
	}

	public int getMaxSpawnedInChunk() {
		return 4;
	}

	public void handleHealthUpdate(byte var1) {
		if(var1 == 2) {
			this.limbYaw = 1.5F;
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.hurtTime = this.maxHurtTime = 10;
			this.attackedAtYaw = 0.0F;
			this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.attackEntityFrom(DamageSource.generic, 0);
		} else if(var1 == 3) {
			this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.health = 0;
			this.onDeath(DamageSource.generic);
		} else {
			super.handleHealthUpdate(var1);
		}

	}

	public boolean isPlayerSleeping() {
		return false;
	}

	public Icon getItemIcon(ItemStack var1, int var2) {
		return var1.getIconIndex();
	}

	protected void updatePotionEffects() {
		Iterator var1 = this.activePotionsMap.keySet().iterator();

		while(var1.hasNext()) {
			Integer var2 = (Integer)var1.next();
			PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);

			try {
				if(!var3.onUpdate(this)) {
					if(!this.worldObj.isRemote) {
						var1.remove();
						this.onFinishedPotionEffect(var3);
					}
				} else if(var3.getDuration() % 600 == 0) {
					this.onChangedPotionEffect(var3);
				}
			} catch (Throwable var11) {
				CrashReport var5 = CrashReport.makeCrashReport(var11, "Ticking mob effect instance");
				CrashReportCategory var6 = var5.makeCategory("Mob effect being ticked");
				var6.addCrashSectionCallable("Effect Name", new CallableEffectName(this, var3));
				var6.addCrashSectionCallable("Effect ID", new CallableEffectID(this, var3));
				var6.addCrashSectionCallable("Effect Duration", new CallableEffectDuration(this, var3));
				var6.addCrashSectionCallable("Effect Amplifier", new CallableEffectAmplifier(this, var3));
				var6.addCrashSectionCallable("Effect is Splash", new CallableEffectIsSplash(this, var3));
				var6.addCrashSectionCallable("Effect is Ambient", new CallableEffectIsAmbient(this, var3));
				throw new ReportedException(var5);
			}
		}

		int var12;
		if(this.potionsNeedUpdate) {
			if(!this.worldObj.isRemote) {
				if(this.activePotionsMap.isEmpty()) {
					this.dataWatcher.updateObject(9, Byte.valueOf((byte)0));
					this.dataWatcher.updateObject(8, Integer.valueOf(0));
					this.setInvisible(false);
				} else {
					var12 = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
					this.dataWatcher.updateObject(9, Byte.valueOf((byte)(PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0)));
					this.dataWatcher.updateObject(8, Integer.valueOf(var12));
					this.setInvisible(this.isPotionActive(Potion.invisibility.id));
				}
			}

			this.potionsNeedUpdate = false;
		}

		var12 = this.dataWatcher.getWatchableObjectInt(8);
		boolean var13 = this.dataWatcher.getWatchableObjectByte(9) > 0;
		if(var12 > 0) {
			boolean var4 = false;
			if(!this.isInvisible()) {
				var4 = this.rand.nextBoolean();
			} else {
				var4 = this.rand.nextInt(15) == 0;
			}

			if(var13) {
				var4 &= this.rand.nextInt(5) == 0;
			}

			if(var4 && var12 > 0) {
				double var14 = (double)(var12 >> 16 & 255) / 255.0D;
				double var7 = (double)(var12 >> 8 & 255) / 255.0D;
				double var9 = (double)(var12 >> 0 & 255) / 255.0D;
				this.worldObj.spawnParticle(var13 ? "mobSpellAmbient" : "mobSpell", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - (double)this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, var14, var7, var9);
			}
		}

	}

	public void clearActivePotions() {
		Iterator var1 = this.activePotionsMap.keySet().iterator();

		while(var1.hasNext()) {
			Integer var2 = (Integer)var1.next();
			PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);
			if(!this.worldObj.isRemote) {
				var1.remove();
				this.onFinishedPotionEffect(var3);
			}
		}

	}

	public Collection getActivePotionEffects() {
		return this.activePotionsMap.values();
	}

	public boolean isPotionActive(int var1) {
		return this.activePotionsMap.containsKey(Integer.valueOf(var1));
	}

	public boolean isPotionActive(Potion var1) {
		return this.activePotionsMap.containsKey(Integer.valueOf(var1.id));
	}

	public PotionEffect getActivePotionEffect(Potion var1) {
		return (PotionEffect)this.activePotionsMap.get(Integer.valueOf(var1.id));
	}

	public void addPotionEffect(PotionEffect var1) {
		if(this.isPotionApplicable(var1)) {
			if(this.activePotionsMap.containsKey(Integer.valueOf(var1.getPotionID()))) {
				((PotionEffect)this.activePotionsMap.get(Integer.valueOf(var1.getPotionID()))).combine(var1);
				this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(Integer.valueOf(var1.getPotionID())));
			} else {
				this.activePotionsMap.put(Integer.valueOf(var1.getPotionID()), var1);
				this.onNewPotionEffect(var1);
			}

		}
	}

	public boolean isPotionApplicable(PotionEffect var1) {
		if(this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
			int var2 = var1.getPotionID();
			if(var2 == Potion.regeneration.id || var2 == Potion.poison.id) {
				return false;
			}
		}

		return true;
	}

	public boolean isEntityUndead() {
		return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	public void removePotionEffectClient(int var1) {
		this.activePotionsMap.remove(Integer.valueOf(var1));
	}

	public void removePotionEffect(int var1) {
		PotionEffect var2 = (PotionEffect)this.activePotionsMap.remove(Integer.valueOf(var1));
		if(var2 != null) {
			this.onFinishedPotionEffect(var2);
		}

	}

	protected void onNewPotionEffect(PotionEffect var1) {
		this.potionsNeedUpdate = true;
	}

	protected void onChangedPotionEffect(PotionEffect var1) {
		this.potionsNeedUpdate = true;
	}

	protected void onFinishedPotionEffect(PotionEffect var1) {
		this.potionsNeedUpdate = true;
	}

	public float getSpeedModifier() {
		float var1 = 1.0F;
		if(this.isPotionActive(Potion.moveSpeed)) {
			var1 *= 1.0F + 0.2F * (float)(this.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
		}

		if(this.isPotionActive(Potion.moveSlowdown)) {
			var1 *= 1.0F - 0.15F * (float)(this.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
		}

		if(var1 < 0.0F) {
			var1 = 0.0F;
		}

		return var1;
	}

	public void setPositionAndUpdate(double var1, double var3, double var5) {
		this.setLocationAndAngles(var1, var3, var5, this.rotationYaw, this.rotationPitch);
	}

	public boolean isChild() {
		return false;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	public void renderBrokenItemStack(ItemStack var1) {
		this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

		for(int var2 = 0; var2 < 5; ++var2) {
			Vec3 var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			var3.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
			var3.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
			Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
			var4.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
			var4.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
			var4 = var4.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
			this.worldObj.spawnParticle("iconcrack_" + var1.getItem().itemID, var4.xCoord, var4.yCoord, var4.zCoord, var3.xCoord, var3.yCoord + 0.05D, var3.zCoord);
		}

	}

	public int func_82143_as() {
		if(this.getAttackTarget() == null) {
			return 3;
		} else {
			int var1 = (int)((float)this.health - (float)this.getMaxHealth() * 0.33F);
			var1 -= (3 - this.worldObj.difficultySetting) * 4;
			if(var1 < 0) {
				var1 = 0;
			}

			return var1 + 3;
		}
	}

	public ItemStack getHeldItem() {
		return this.equipment[0];
	}

	public ItemStack getCurrentItemOrArmor(int var1) {
		return this.equipment[var1];
	}

	public ItemStack getCurrentArmor(int var1) {
		return this.equipment[var1 + 1];
	}

	public void setCurrentItemOrArmor(int var1, ItemStack var2) {
		this.equipment[var1] = var2;
	}

	public ItemStack[] getLastActiveItems() {
		return this.equipment;
	}

	protected void dropEquipment(boolean var1, int var2) {
		for(int var3 = 0; var3 < this.getLastActiveItems().length; ++var3) {
			ItemStack var4 = this.getCurrentItemOrArmor(var3);
			boolean var5 = this.equipmentDropChances[var3] > 1.0F;
			if(var4 != null && (var1 || var5) && this.rand.nextFloat() - (float)var2 * 0.01F < this.equipmentDropChances[var3]) {
				if(!var5 && var4.isItemStackDamageable()) {
					int var6 = Math.max(var4.getMaxDamage() - 25, 1);
					int var7 = var4.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(var6) + 1);
					if(var7 > var6) {
						var7 = var6;
					}

					if(var7 < 1) {
						var7 = 1;
					}

					var4.setItemDamage(var7);
				}

				this.entityDropItem(var4, 0.0F);
			}
		}

	}

	protected void addRandomArmor() {
		if(this.rand.nextFloat() < armorProbability[this.worldObj.difficultySetting]) {
			int var1 = this.rand.nextInt(2);
			float var2 = this.worldObj.difficultySetting == 3 ? 0.1F : 0.25F;
			if(this.rand.nextFloat() < 0.095F) {
				++var1;
			}

			if(this.rand.nextFloat() < 0.095F) {
				++var1;
			}

			if(this.rand.nextFloat() < 0.095F) {
				++var1;
			}

			for(int var3 = 3; var3 >= 0; --var3) {
				ItemStack var4 = this.getCurrentArmor(var3);
				if(var3 < 3 && this.rand.nextFloat() < var2) {
					break;
				}

				if(var4 == null) {
					Item var5 = getArmorItemForSlot(var3 + 1, var1);
					if(var5 != null) {
						this.setCurrentItemOrArmor(var3 + 1, new ItemStack(var5));
					}
				}
			}
		}

	}

	public void onItemPickup(Entity var1, int var2) {
		if(!var1.isDead && !this.worldObj.isRemote) {
			EntityTracker var3 = ((WorldServer)this.worldObj).getEntityTracker();
			if(var1 instanceof EntityItem) {
				var3.sendPacketToAllPlayersTrackingEntity(var1, new Packet22Collect(var1.entityId, this.entityId));
			}

			if(var1 instanceof EntityArrow) {
				var3.sendPacketToAllPlayersTrackingEntity(var1, new Packet22Collect(var1.entityId, this.entityId));
			}

			if(var1 instanceof EntityXPOrb) {
				var3.sendPacketToAllPlayersTrackingEntity(var1, new Packet22Collect(var1.entityId, this.entityId));
			}
		}

	}

	public static int getArmorPosition(ItemStack var0) {
		if(var0.itemID != Block.pumpkin.blockID && var0.itemID != Item.skull.itemID) {
			if(var0.getItem() instanceof ItemArmor) {
				switch(((ItemArmor)var0.getItem()).armorType) {
				case 0:
					return 4;
				case 1:
					return 3;
				case 2:
					return 2;
				case 3:
					return 1;
				}
			}

			return 0;
		} else {
			return 4;
		}
	}

	public static Item getArmorItemForSlot(int var0, int var1) {
		switch(var0) {
		case 4:
			if(var1 == 0) {
				return Item.helmetLeather;
			} else if(var1 == 1) {
				return Item.helmetGold;
			} else if(var1 == 2) {
				return Item.helmetChain;
			} else if(var1 == 3) {
				return Item.helmetIron;
			} else if(var1 == 4) {
				return Item.helmetDiamond;
			}
		case 3:
			if(var1 == 0) {
				return Item.plateLeather;
			} else if(var1 == 1) {
				return Item.plateGold;
			} else if(var1 == 2) {
				return Item.plateChain;
			} else if(var1 == 3) {
				return Item.plateIron;
			} else if(var1 == 4) {
				return Item.plateDiamond;
			}
		case 2:
			if(var1 == 0) {
				return Item.legsLeather;
			} else if(var1 == 1) {
				return Item.legsGold;
			} else if(var1 == 2) {
				return Item.legsChain;
			} else if(var1 == 3) {
				return Item.legsIron;
			} else if(var1 == 4) {
				return Item.legsDiamond;
			}
		case 1:
			if(var1 == 0) {
				return Item.bootsLeather;
			} else if(var1 == 1) {
				return Item.bootsGold;
			} else if(var1 == 2) {
				return Item.bootsChain;
			} else if(var1 == 3) {
				return Item.bootsIron;
			} else if(var1 == 4) {
				return Item.bootsDiamond;
			}
		default:
			return null;
		}
	}

	protected void func_82162_bC() {
		if(this.getHeldItem() != null && this.rand.nextFloat() < enchantmentProbability[this.worldObj.difficultySetting]) {
			EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), 5 + this.worldObj.difficultySetting * this.rand.nextInt(6));
		}

		for(int var1 = 0; var1 < 4; ++var1) {
			ItemStack var2 = this.getCurrentArmor(var1);
			if(var2 != null && this.rand.nextFloat() < armorEnchantmentProbability[this.worldObj.difficultySetting]) {
				EnchantmentHelper.addRandomEnchantment(this.rand, var2, 5 + this.worldObj.difficultySetting * this.rand.nextInt(6));
			}
		}

	}

	public void initCreature() {
	}

	private int getArmSwingAnimationEnd() {
		return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
	}

	public void swingItem() {
		if(!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
			this.swingProgressInt = -1;
			this.isSwingInProgress = true;
			if(this.worldObj instanceof WorldServer) {
				((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet18Animation(this, 1));
			}
		}

	}

	public boolean canBeSteered() {
		return false;
	}

	public final int getArrowCountInEntity() {
		return this.dataWatcher.getWatchableObjectByte(10);
	}

	public final void setArrowCountInEntity(int var1) {
		this.dataWatcher.updateObject(10, Byte.valueOf((byte)var1));
	}

	public EntityLiving func_94060_bK() {
		return (EntityLiving)(this.field_94063_bt.func_94550_c() != null ? this.field_94063_bt.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
	}

	public String getEntityName() {
		return this.func_94056_bM() ? this.func_94057_bL() : super.getEntityName();
	}

	public void func_94058_c(String var1) {
		this.dataWatcher.updateObject(5, var1);
	}

	public String func_94057_bL() {
		return this.dataWatcher.getWatchableObjectString(5);
	}

	public boolean func_94056_bM() {
		return this.dataWatcher.getWatchableObjectString(5).length() > 0;
	}

	public void func_94061_f(boolean var1) {
		this.dataWatcher.updateObject(6, Byte.valueOf((byte)(var1 ? 1 : 0)));
	}

	public boolean func_94062_bN() {
		return this.dataWatcher.getWatchableObjectByte(6) == 1;
	}

	public boolean func_94059_bO() {
		return this.func_94062_bN();
	}

	public void func_96120_a(int var1, float var2) {
		this.equipmentDropChances[var1] = var2;
	}

	public boolean canPickUpLoot() {
		return this.canPickUpLoot;
	}

	public void setCanPickUpLoot(boolean var1) {
		this.canPickUpLoot = var1;
	}

	public boolean func_104002_bU() {
		return this.persistenceRequired;
	}
}
