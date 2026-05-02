package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityWitch extends EntityMob implements IRangedAttackMob {
	private static final int[] witchDrops = new int[]{Item.lightStoneDust.itemID, Item.sugar.itemID, Item.redstone.itemID, Item.spiderEye.itemID, Item.glassBottle.itemID, Item.gunpowder.itemID, Item.stick.itemID, Item.stick.itemID};
	private int witchAttackTimer = 0;

	public EntityWitch(World var1) {
		super(var1);
		this.texture = "/mob/villager/witch.png";
		this.moveSpeed = 0.25F;
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIArrowAttack(this, this.moveSpeed, 60, 10.0F));
		this.tasks.addTask(2, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(21, Byte.valueOf((byte)0));
	}

	protected String getLivingSound() {
		return "mob.witch.idle";
	}

	protected String getHurtSound() {
		return "mob.witch.hurt";
	}

	protected String getDeathSound() {
		return "mob.witch.death";
	}

	public void setAggressive(boolean var1) {
		this.getDataWatcher().updateObject(21, Byte.valueOf((byte)(var1 ? 1 : 0)));
	}

	public boolean getAggressive() {
		return this.getDataWatcher().getWatchableObjectByte(21) == 1;
	}

	public int getMaxHealth() {
		return 26;
	}

	public boolean isAIEnabled() {
		return true;
	}

	public void onLivingUpdate() {
		if(!this.worldObj.isRemote) {
			if(this.getAggressive()) {
				if(this.witchAttackTimer-- <= 0) {
					this.setAggressive(false);
					ItemStack var5 = this.getHeldItem();
					this.setCurrentItemOrArmor(0, (ItemStack)null);
					if(var5 != null && var5.itemID == Item.potion.itemID) {
						List var2 = Item.potion.getEffects(var5);
						if(var2 != null) {
							Iterator var3 = var2.iterator();

							while(var3.hasNext()) {
								PotionEffect var4 = (PotionEffect)var3.next();
								this.addPotionEffect(new PotionEffect(var4));
							}
						}
					}
				}
			} else {
				short var1 = -1;
				if(this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance)) {
					var1 = 16307;
				} else if(this.rand.nextFloat() < 0.05F && this.health < this.getMaxHealth()) {
					var1 = 16341;
				} else if(this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
					var1 = 16274;
				} else if(this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
					var1 = 16274;
				}

				if(var1 > -1) {
					this.setCurrentItemOrArmor(0, new ItemStack(Item.potion, 1, var1));
					this.witchAttackTimer = this.getHeldItem().getMaxItemUseDuration();
					this.setAggressive(true);
				}
			}

			if(this.rand.nextFloat() < 7.5E-4F) {
				this.worldObj.setEntityState(this, (byte)15);
			}
		}

		super.onLivingUpdate();
	}

	public void handleHealthUpdate(byte var1) {
		if(var1 == 15) {
			for(int var2 = 0; var2 < this.rand.nextInt(35) + 10; ++var2) {
				this.worldObj.spawnParticle("witchMagic", this.posX + this.rand.nextGaussian() * (double)0.13F, this.boundingBox.maxY + 0.5D + this.rand.nextGaussian() * (double)0.13F, this.posZ + this.rand.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
			}
		} else {
			super.handleHealthUpdate(var1);
		}

	}

	protected int applyPotionDamageCalculations(DamageSource var1, int var2) {
		var2 = super.applyPotionDamageCalculations(var1, var2);
		if(var1.getEntity() == this) {
			var2 = 0;
		}

		if(var1.isMagicDamage()) {
			var2 = (int)((double)var2 * 0.15D);
		}

		return var2;
	}

	public float getSpeedModifier() {
		float var1 = super.getSpeedModifier();
		if(this.getAggressive()) {
			var1 *= 12.0F / 16.0F;
		}

		return var1;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(3) + 1;

		for(int var4 = 0; var4 < var3; ++var4) {
			int var5 = this.rand.nextInt(3);
			int var6 = witchDrops[this.rand.nextInt(witchDrops.length)];
			if(var2 > 0) {
				var5 += this.rand.nextInt(var2 + 1);
			}

			for(int var7 = 0; var7 < var5; ++var7) {
				this.dropItem(var6, 1);
			}
		}

	}

	public void attackEntityWithRangedAttack(EntityLiving var1, float var2) {
		if(!this.getAggressive()) {
			EntityPotion var3 = new EntityPotion(this.worldObj, this, 32732);
			var3.rotationPitch -= -20.0F;
			double var4 = var1.posX + var1.motionX - this.posX;
			double var6 = var1.posY + (double)var1.getEyeHeight() - (double)1.1F - this.posY;
			double var8 = var1.posZ + var1.motionZ - this.posZ;
			float var10 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
			if(var10 >= 8.0F && !var1.isPotionActive(Potion.moveSlowdown)) {
				var3.setPotionDamage(32698);
			} else if(var1.getHealth() >= 8 && !var1.isPotionActive(Potion.poison)) {
				var3.setPotionDamage(32660);
			} else if(var10 <= 3.0F && !var1.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F) {
				var3.setPotionDamage(32696);
			}

			var3.setThrowableHeading(var4, var6 + (double)(var10 * 0.2F), var8, 12.0F / 16.0F, 8.0F);
			this.worldObj.spawnEntityInWorld(var3);
		}
	}
}
