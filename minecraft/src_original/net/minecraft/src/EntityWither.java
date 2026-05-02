package net.minecraft.src;

import java.util.List;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob {
	private float[] field_82220_d = new float[2];
	private float[] field_82221_e = new float[2];
	private float[] field_82217_f = new float[2];
	private float[] field_82218_g = new float[2];
	private int[] field_82223_h = new int[2];
	private int[] field_82224_i = new int[2];
	private int field_82222_j;
	private static final IEntitySelector attackEntitySelector = new EntityWitherAttackFilter();

	public EntityWither(World var1) {
		super(var1);
		this.setEntityHealth(this.getMaxHealth());
		this.texture = "/mob/wither.png";
		this.setSize(0.9F, 4.0F);
		this.isImmuneToFire = true;
		this.moveSpeed = 0.6F;
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIArrowAttack(this, this.moveSpeed, 40, 20.0F));
		this.tasks.addTask(5, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 30.0F, 0, false, false, attackEntitySelector));
		this.experienceValue = 50;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Integer(100));
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(0));
		this.dataWatcher.addObject(19, new Integer(0));
		this.dataWatcher.addObject(20, new Integer(0));
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("Invul", this.func_82212_n());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.func_82215_s(var1.getInteger("Invul"));
		this.dataWatcher.updateObject(16, Integer.valueOf(this.health));
	}

	public float getShadowSize() {
		return this.height / 8.0F;
	}

	protected String getLivingSound() {
		return "mob.wither.idle";
	}

	protected String getHurtSound() {
		return "mob.wither.hurt";
	}

	protected String getDeathSound() {
		return "mob.wither.death";
	}

	public String getTexture() {
		int var1 = this.func_82212_n();
		return var1 > 0 && (var1 > 80 || var1 / 5 % 2 != 1) ? "/mob/wither_invul.png" : "/mob/wither.png";
	}

	public void onLivingUpdate() {
		if(!this.worldObj.isRemote) {
			this.dataWatcher.updateObject(16, Integer.valueOf(this.health));
		}

		this.motionY *= (double)0.6F;
		double var4;
		double var6;
		double var8;
		if(!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
			Entity var1 = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
			if(var1 != null) {
				if(this.posY < var1.posY || !this.isArmored() && this.posY < var1.posY + 5.0D) {
					if(this.motionY < 0.0D) {
						this.motionY = 0.0D;
					}

					this.motionY += (0.5D - this.motionY) * (double)0.6F;
				}

				double var2 = var1.posX - this.posX;
				var4 = var1.posZ - this.posZ;
				var6 = var2 * var2 + var4 * var4;
				if(var6 > 9.0D) {
					var8 = (double)MathHelper.sqrt_double(var6);
					this.motionX += (var2 / var8 * 0.5D - this.motionX) * (double)0.6F;
					this.motionZ += (var4 / var8 * 0.5D - this.motionZ) * (double)0.6F;
				}
			}
		}

		if(this.motionX * this.motionX + this.motionZ * this.motionZ > (double)0.05F) {
			this.rotationYaw = (float)Math.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
		}

		super.onLivingUpdate();

		int var20;
		for(var20 = 0; var20 < 2; ++var20) {
			this.field_82218_g[var20] = this.field_82221_e[var20];
			this.field_82217_f[var20] = this.field_82220_d[var20];
		}

		int var21;
		for(var20 = 0; var20 < 2; ++var20) {
			var21 = this.getWatchedTargetId(var20 + 1);
			Entity var3 = null;
			if(var21 > 0) {
				var3 = this.worldObj.getEntityByID(var21);
			}

			if(var3 != null) {
				var4 = this.func_82214_u(var20 + 1);
				var6 = this.func_82208_v(var20 + 1);
				var8 = this.func_82213_w(var20 + 1);
				double var10 = var3.posX - var4;
				double var12 = var3.posY + (double)var3.getEyeHeight() - var6;
				double var14 = var3.posZ - var8;
				double var16 = (double)MathHelper.sqrt_double(var10 * var10 + var14 * var14);
				float var18 = (float)(Math.atan2(var14, var10) * 180.0D / (double)((float)Math.PI)) - 90.0F;
				float var19 = (float)(-(Math.atan2(var12, var16) * 180.0D / (double)((float)Math.PI)));
				this.field_82220_d[var20] = this.func_82204_b(this.field_82220_d[var20], var19, 40.0F);
				this.field_82221_e[var20] = this.func_82204_b(this.field_82221_e[var20], var18, 10.0F);
			} else {
				this.field_82221_e[var20] = this.func_82204_b(this.field_82221_e[var20], this.renderYawOffset, 10.0F);
			}
		}

		boolean var22 = this.isArmored();

		for(var21 = 0; var21 < 3; ++var21) {
			double var23 = this.func_82214_u(var21);
			double var5 = this.func_82208_v(var21);
			double var7 = this.func_82213_w(var21);
			this.worldObj.spawnParticle("smoke", var23 + this.rand.nextGaussian() * (double)0.3F, var5 + this.rand.nextGaussian() * (double)0.3F, var7 + this.rand.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
			if(var22 && this.worldObj.rand.nextInt(4) == 0) {
				this.worldObj.spawnParticle("mobSpell", var23 + this.rand.nextGaussian() * (double)0.3F, var5 + this.rand.nextGaussian() * (double)0.3F, var7 + this.rand.nextGaussian() * (double)0.3F, (double)0.7F, (double)0.7F, 0.5D);
			}
		}

		if(this.func_82212_n() > 0) {
			for(var21 = 0; var21 < 3; ++var21) {
				this.worldObj.spawnParticle("mobSpell", this.posX + this.rand.nextGaussian() * 1.0D, this.posY + (double)(this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian() * 1.0D, (double)0.7F, (double)0.7F, (double)0.9F);
			}
		}

	}

	protected void updateAITasks() {
		int var1;
		if(this.func_82212_n() > 0) {
			var1 = this.func_82212_n() - 1;
			if(var1 <= 0) {
				this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
				this.worldObj.func_82739_e(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
			}

			this.func_82215_s(var1);
			if(this.ticksExisted % 10 == 0) {
				this.heal(10);
			}

		} else {
			super.updateAITasks();

			int var12;
			for(var1 = 1; var1 < 3; ++var1) {
				if(this.ticksExisted >= this.field_82223_h[var1 - 1]) {
					this.field_82223_h[var1 - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
					if(this.worldObj.difficultySetting >= 2) {
						int var10001 = var1 - 1;
						int var10003 = this.field_82224_i[var1 - 1];
						this.field_82224_i[var10001] = this.field_82224_i[var1 - 1] + 1;
						if(var10003 > 15) {
							float var2 = 10.0F;
							float var3 = 5.0F;
							double var4 = MathHelper.getRandomDoubleInRange(this.rand, this.posX - (double)var2, this.posX + (double)var2);
							double var6 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - (double)var3, this.posY + (double)var3);
							double var8 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - (double)var2, this.posZ + (double)var2);
							this.func_82209_a(var1 + 1, var4, var6, var8, true);
							this.field_82224_i[var1 - 1] = 0;
						}
					}

					var12 = this.getWatchedTargetId(var1);
					if(var12 > 0) {
						Entity var14 = this.worldObj.getEntityByID(var12);
						if(var14 != null && var14.isEntityAlive() && this.getDistanceSqToEntity(var14) <= 900.0D && this.canEntityBeSeen(var14)) {
							this.func_82216_a(var1 + 1, (EntityLiving)var14);
							this.field_82223_h[var1 - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
							this.field_82224_i[var1 - 1] = 0;
						} else {
							this.func_82211_c(var1, 0);
						}
					} else {
						List var13 = this.worldObj.selectEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(20.0D, 8.0D, 20.0D), attackEntitySelector);

						for(int var16 = 0; var16 < 10 && !var13.isEmpty(); ++var16) {
							EntityLiving var5 = (EntityLiving)var13.get(this.rand.nextInt(var13.size()));
							if(var5 != this && var5.isEntityAlive() && this.canEntityBeSeen(var5)) {
								if(var5 instanceof EntityPlayer) {
									if(!((EntityPlayer)var5).capabilities.disableDamage) {
										this.func_82211_c(var1, var5.entityId);
									}
								} else {
									this.func_82211_c(var1, var5.entityId);
								}
								break;
							}

							var13.remove(var5);
						}
					}
				}
			}

			if(this.getAttackTarget() != null) {
				this.func_82211_c(0, this.getAttackTarget().entityId);
			} else {
				this.func_82211_c(0, 0);
			}

			if(this.field_82222_j > 0) {
				--this.field_82222_j;
				if(this.field_82222_j == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
					var1 = MathHelper.floor_double(this.posY);
					var12 = MathHelper.floor_double(this.posX);
					int var15 = MathHelper.floor_double(this.posZ);
					boolean var17 = false;

					for(int var18 = -1; var18 <= 1; ++var18) {
						for(int var19 = -1; var19 <= 1; ++var19) {
							for(int var7 = 0; var7 <= 3; ++var7) {
								int var20 = var12 + var18;
								int var9 = var1 + var7;
								int var10 = var15 + var19;
								int var11 = this.worldObj.getBlockId(var20, var9, var10);
								if(var11 > 0 && var11 != Block.bedrock.blockID && var11 != Block.endPortal.blockID && var11 != Block.endPortalFrame.blockID) {
									var17 = this.worldObj.destroyBlock(var20, var9, var10, true) || var17;
								}
							}
						}
					}

					if(var17) {
						this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
					}
				}
			}

			if(this.ticksExisted % 20 == 0) {
				this.heal(1);
			}

		}
	}

	public void func_82206_m() {
		this.func_82215_s(220);
		this.setEntityHealth(this.getMaxHealth() / 3);
	}

	public void setInWeb() {
	}

	public int getTotalArmorValue() {
		return 4;
	}

	private double func_82214_u(int var1) {
		if(var1 <= 0) {
			return this.posX;
		} else {
			float var2 = (this.renderYawOffset + (float)(180 * (var1 - 1))) / 180.0F * (float)Math.PI;
			float var3 = MathHelper.cos(var2);
			return this.posX + (double)var3 * 1.3D;
		}
	}

	private double func_82208_v(int var1) {
		return var1 <= 0 ? this.posY + 3.0D : this.posY + 2.2D;
	}

	private double func_82213_w(int var1) {
		if(var1 <= 0) {
			return this.posZ;
		} else {
			float var2 = (this.renderYawOffset + (float)(180 * (var1 - 1))) / 180.0F * (float)Math.PI;
			float var3 = MathHelper.sin(var2);
			return this.posZ + (double)var3 * 1.3D;
		}
	}

	private float func_82204_b(float var1, float var2, float var3) {
		float var4 = MathHelper.wrapAngleTo180_float(var2 - var1);
		if(var4 > var3) {
			var4 = var3;
		}

		if(var4 < -var3) {
			var4 = -var3;
		}

		return var1 + var4;
	}

	private void func_82216_a(int var1, EntityLiving var2) {
		this.func_82209_a(var1, var2.posX, var2.posY + (double)var2.getEyeHeight() * 0.5D, var2.posZ, var1 == 0 && this.rand.nextFloat() < 0.001F);
	}

	private void func_82209_a(int var1, double var2, double var4, double var6, boolean var8) {
		this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
		double var9 = this.func_82214_u(var1);
		double var11 = this.func_82208_v(var1);
		double var13 = this.func_82213_w(var1);
		double var15 = var2 - var9;
		double var17 = var4 - var11;
		double var19 = var6 - var13;
		EntityWitherSkull var21 = new EntityWitherSkull(this.worldObj, this, var15, var17, var19);
		if(var8) {
			var21.setInvulnerable(true);
		}

		var21.posY = var11;
		var21.posX = var9;
		var21.posZ = var13;
		this.worldObj.spawnEntityInWorld(var21);
	}

	public void attackEntityWithRangedAttack(EntityLiving var1, float var2) {
		this.func_82216_a(0, var1);
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else if(var1 == DamageSource.drown) {
			return false;
		} else if(this.func_82212_n() > 0) {
			return false;
		} else {
			Entity var3;
			if(this.isArmored()) {
				var3 = var1.getSourceOfDamage();
				if(var3 instanceof EntityArrow) {
					return false;
				}
			}

			var3 = var1.getEntity();
			if(var3 != null && !(var3 instanceof EntityPlayer) && var3 instanceof EntityLiving && ((EntityLiving)var3).getCreatureAttribute() == this.getCreatureAttribute()) {
				return false;
			} else {
				if(this.field_82222_j <= 0) {
					this.field_82222_j = 20;
				}

				for(int var4 = 0; var4 < this.field_82224_i.length; ++var4) {
					this.field_82224_i[var4] += 3;
				}

				return super.attackEntityFrom(var1, var2);
			}
		}
	}

	protected void dropFewItems(boolean var1, int var2) {
		this.dropItem(Item.netherStar.itemID, 1);
	}

	protected void despawnEntity() {
		this.entityAge = 0;
	}

	public int getBrightnessForRender(float var1) {
		return 15728880;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public int getBossHealth() {
		return this.dataWatcher.getWatchableObjectInt(16);
	}

	protected void fall(float var1) {
	}

	public void addPotionEffect(PotionEffect var1) {
	}

	protected boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 300;
	}

	public float func_82207_a(int var1) {
		return this.field_82221_e[var1];
	}

	public float func_82210_r(int var1) {
		return this.field_82220_d[var1];
	}

	public int func_82212_n() {
		return this.dataWatcher.getWatchableObjectInt(20);
	}

	public void func_82215_s(int var1) {
		this.dataWatcher.updateObject(20, Integer.valueOf(var1));
	}

	public int getWatchedTargetId(int var1) {
		return this.dataWatcher.getWatchableObjectInt(17 + var1);
	}

	public void func_82211_c(int var1, int var2) {
		this.dataWatcher.updateObject(17 + var1, Integer.valueOf(var2));
	}

	public boolean isArmored() {
		return this.getBossHealth() <= this.getMaxHealth() / 2;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	public void mountEntity(Entity var1) {
		this.ridingEntity = null;
	}
}
