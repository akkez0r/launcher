package net.minecraft.src;

public class EntitySpider extends EntityMob {
	public EntitySpider(World var1) {
		super(var1);
		this.texture = "/mob/spider.png";
		this.setSize(1.4F, 0.9F);
		this.moveSpeed = 0.8F;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte)0));
	}

	public void onUpdate() {
		super.onUpdate();
		if(!this.worldObj.isRemote) {
			this.setBesideClimbableBlock(this.isCollidedHorizontally);
		}

	}

	public int getMaxHealth() {
		return 16;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.75D - 0.5D;
	}

	protected Entity findPlayerToAttack() {
		float var1 = this.getBrightness(1.0F);
		if(var1 < 0.5F) {
			double var2 = 16.0D;
			return this.worldObj.getClosestVulnerablePlayerToEntity(this, var2);
		} else {
			return null;
		}
	}

	protected String getLivingSound() {
		return "mob.spider.say";
	}

	protected String getHurtSound() {
		return "mob.spider.say";
	}

	protected String getDeathSound() {
		return "mob.spider.death";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.spider.step", 0.15F, 1.0F);
	}

	protected void attackEntity(Entity var1, float var2) {
		float var3 = this.getBrightness(1.0F);
		if(var3 > 0.5F && this.rand.nextInt(100) == 0) {
			this.entityToAttack = null;
		} else {
			if(var2 > 2.0F && var2 < 6.0F && this.rand.nextInt(10) == 0) {
				if(this.onGround) {
					double var4 = var1.posX - this.posX;
					double var6 = var1.posZ - this.posZ;
					float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
					this.motionX = var4 / (double)var8 * 0.5D * (double)0.8F + this.motionX * (double)0.2F;
					this.motionZ = var6 / (double)var8 * 0.5D * (double)0.8F + this.motionZ * (double)0.2F;
					this.motionY = (double)0.4F;
				}
			} else {
				super.attackEntity(var1, var2);
			}

		}
	}

	protected int getDropItemId() {
		return Item.silk.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		super.dropFewItems(var1, var2);
		if(var1 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + var2) > 0)) {
			this.dropItem(Item.spiderEye.itemID, 1);
		}

	}

	public boolean isOnLadder() {
		return this.isBesideClimbableBlock();
	}

	public void setInWeb() {
	}

	public float spiderScaleAmount() {
		return 1.0F;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	public boolean isPotionApplicable(PotionEffect var1) {
		return var1.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(var1);
	}

	public boolean isBesideClimbableBlock() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setBesideClimbableBlock(boolean var1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(16);
		if(var1) {
			var2 = (byte)(var2 | 1);
		} else {
			var2 &= -2;
		}

		this.dataWatcher.updateObject(16, Byte.valueOf(var2));
	}

	public void initCreature() {
		if(this.worldObj.rand.nextInt(100) == 0) {
			EntitySkeleton var1 = new EntitySkeleton(this.worldObj);
			var1.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
			var1.initCreature();
			this.worldObj.spawnEntityInWorld(var1);
			var1.mountEntity(this);
		}

	}
}
