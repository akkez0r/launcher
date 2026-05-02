package net.minecraft.src;

import java.util.List;

public class EntityArrow extends Entity implements IProjectile {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private int inData = 0;
	private boolean inGround = false;
	public int canBePickedUp = 0;
	public int arrowShake = 0;
	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir = 0;
	private double damage = 2.0D;
	private int knockbackStrength;

	public EntityArrow(World var1) {
		super(var1);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
	}

	public EntityArrow(World var1, double var2, double var4, double var6) {
		super(var1);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
		this.setPosition(var2, var4, var6);
		this.yOffset = 0.0F;
	}

	public EntityArrow(World var1, EntityLiving var2, EntityLiving var3, float var4, float var5) {
		super(var1);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = var2;
		if(var2 instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}

		this.posY = var2.posY + (double)var2.getEyeHeight() - (double)0.1F;
		double var6 = var3.posX - var2.posX;
		double var8 = var3.boundingBox.minY + (double)(var3.height / 3.0F) - this.posY;
		double var10 = var3.posZ - var2.posZ;
		double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var10 * var10);
		if(var12 >= 1.0E-7D) {
			float var14 = (float)(Math.atan2(var10, var6) * 180.0D / (double)((float)Math.PI)) - 90.0F;
			float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / (double)((float)Math.PI)));
			double var16 = var6 / var12;
			double var18 = var10 / var12;
			this.setLocationAndAngles(var2.posX + var16, this.posY, var2.posZ + var18, var14, var15);
			this.yOffset = 0.0F;
			float var20 = (float)var12 * 0.2F;
			this.setThrowableHeading(var6, var8 + (double)var20, var10, var4, var5);
		}
	}

	public EntityArrow(World var1, EntityLiving var2, float var3) {
		super(var1);
		this.renderDistanceWeight = 10.0D;
		this.shootingEntity = var2;
		if(var2 instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}

		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, var2.rotationYaw, var2.rotationPitch);
		this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= (double)0.1F;
		this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, var3 * 1.5F, 1.0F);
	}

	protected void entityInit() {
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {
		float var9 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);
		var1 /= (double)var9;
		var3 /= (double)var9;
		var5 /= (double)var9;
		var1 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075F * (double)var8;
		var3 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075F * (double)var8;
		var5 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075F * (double)var8;
		var1 *= (double)var7;
		var3 *= (double)var7;
		var5 *= (double)var7;
		this.motionX = var1;
		this.motionY = var3;
		this.motionZ = var5;
		float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(var1, var5) * 180.0D / (double)((float)Math.PI));
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(var3, (double)var10) * 180.0D / (double)((float)Math.PI));
		this.ticksInGround = 0;
	}

	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.setPosition(var1, var3, var5);
		this.setRotation(var7, var8);
	}

	public void setVelocity(double var1, double var3, double var5) {
		this.motionX = var1;
		this.motionY = var3;
		this.motionZ = var5;
		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var7 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(var1, var5) * 180.0D / (double)((float)Math.PI));
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(var3, (double)var7) * 180.0D / (double)((float)Math.PI));
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}

	}

	public void onUpdate() {
		super.onUpdate();
		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)((float)Math.PI));
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var1) * 180.0D / (double)((float)Math.PI));
		}

		int var16 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
		if(var16 > 0) {
			Block.blocksList[var16].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
			AxisAlignedBB var2 = Block.blocksList[var16].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);
			if(var2 != null && var2.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if(this.arrowShake > 0) {
			--this.arrowShake;
		}

		if(this.inGround) {
			int var18 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			int var19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
			if(var18 == this.inTile && var19 == this.inData) {
				++this.ticksInGround;
				if(this.ticksInGround == 1200) {
					this.setDead();
				}

			} else {
				this.inGround = false;
				this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3 var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
			Vec3 var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition var4 = this.worldObj.rayTraceBlocks_do_do(var17, var3, false, true);
			var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
			var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if(var4 != null) {
				var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
			}

			Entity var5 = null;
			List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double var7 = 0.0D;

			int var9;
			float var11;
			for(var9 = 0; var9 < var6.size(); ++var9) {
				Entity var10 = (Entity)var6.get(var9);
				if(var10.canBeCollidedWith() && (var10 != this.shootingEntity || this.ticksInAir >= 5)) {
					var11 = 0.3F;
					AxisAlignedBB var12 = var10.boundingBox.expand((double)var11, (double)var11, (double)var11);
					MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);
					if(var13 != null) {
						double var14 = var17.distanceTo(var13.hitVec);
						if(var14 < var7 || var7 == 0.0D) {
							var5 = var10;
							var7 = var14;
						}
					}
				}
			}

			if(var5 != null) {
				var4 = new MovingObjectPosition(var5);
			}

			if(var4 != null && var4.entityHit != null && var4.entityHit instanceof EntityPlayer) {
				EntityPlayer var20 = (EntityPlayer)var4.entityHit;
				if(var20.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).func_96122_a(var20)) {
					var4 = null;
				}
			}

			float var21;
			float var27;
			if(var4 != null) {
				if(var4.entityHit != null) {
					var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					int var22 = MathHelper.ceiling_double_int((double)var21 * this.damage);
					if(this.getIsCritical()) {
						var22 += this.rand.nextInt(var22 / 2 + 2);
					}

					DamageSource var23 = null;
					if(this.shootingEntity == null) {
						var23 = DamageSource.causeArrowDamage(this, this);
					} else {
						var23 = DamageSource.causeArrowDamage(this, this.shootingEntity);
					}

					if(this.isBurning() && !(var4.entityHit instanceof EntityEnderman)) {
						var4.entityHit.setFire(5);
					}

					if(var4.entityHit.attackEntityFrom(var23, var22)) {
						if(var4.entityHit instanceof EntityLiving) {
							EntityLiving var25 = (EntityLiving)var4.entityHit;
							if(!this.worldObj.isRemote) {
								var25.setArrowCountInEntity(var25.getArrowCountInEntity() + 1);
							}

							if(this.knockbackStrength > 0) {
								var27 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
								if(var27 > 0.0F) {
									var4.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * (double)0.6F / (double)var27, 0.1D, this.motionZ * (double)this.knockbackStrength * (double)0.6F / (double)var27);
								}
							}

							if(this.shootingEntity != null) {
								EnchantmentThorns.func_92096_a(this.shootingEntity, var25, this.rand);
							}

							if(this.shootingEntity != null && var4.entityHit != this.shootingEntity && var4.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
							}
						}

						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						if(!(var4.entityHit instanceof EntityEnderman)) {
							this.setDead();
						}
					} else {
						this.motionX *= (double)-0.1F;
						this.motionY *= (double)-0.1F;
						this.motionZ *= (double)-0.1F;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.xTile = var4.blockX;
					this.yTile = var4.blockY;
					this.zTile = var4.blockZ;
					this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
					this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
					this.motionX = (double)((float)(var4.hitVec.xCoord - this.posX));
					this.motionY = (double)((float)(var4.hitVec.yCoord - this.posY));
					this.motionZ = (double)((float)(var4.hitVec.zCoord - this.posZ));
					var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					this.posX -= this.motionX / (double)var21 * (double)0.05F;
					this.posY -= this.motionY / (double)var21 * (double)0.05F;
					this.posZ -= this.motionZ / (double)var21 * (double)0.05F;
					this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
					this.setIsCritical(false);
					if(this.inTile != 0) {
						Block.blocksList[this.inTile].onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
					}
				}
			}

			if(this.getIsCritical()) {
				for(var9 = 0; var9 < 4; ++var9) {
					this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)var9 / 4.0D, this.posY + this.motionY * (double)var9 / 4.0D, this.posZ + this.motionZ * (double)var9 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)((float)Math.PI));

			for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var21) * 180.0D / (double)((float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			}

			while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float var24 = 0.99F;
			var11 = 0.05F;
			if(this.isInWater()) {
				for(int var26 = 0; var26 < 4; ++var26) {
					var27 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var27, this.posY - this.motionY * (double)var27, this.posZ - this.motionZ * (double)var27, this.motionX, this.motionY, this.motionZ);
				}

				var24 = 0.8F;
			}

			this.motionX *= (double)var24;
			this.motionY *= (double)var24;
			this.motionZ *= (double)var24;
			this.motionY -= (double)var11;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.doBlockCollisions();
		}
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		var1.setShort("xTile", (short)this.xTile);
		var1.setShort("yTile", (short)this.yTile);
		var1.setShort("zTile", (short)this.zTile);
		var1.setByte("inTile", (byte)this.inTile);
		var1.setByte("inData", (byte)this.inData);
		var1.setByte("shake", (byte)this.arrowShake);
		var1.setByte("inGround", (byte)(this.inGround ? 1 : 0));
		var1.setByte("pickup", (byte)this.canBePickedUp);
		var1.setDouble("damage", this.damage);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		this.xTile = var1.getShort("xTile");
		this.yTile = var1.getShort("yTile");
		this.zTile = var1.getShort("zTile");
		this.inTile = var1.getByte("inTile") & 255;
		this.inData = var1.getByte("inData") & 255;
		this.arrowShake = var1.getByte("shake") & 255;
		this.inGround = var1.getByte("inGround") == 1;
		if(var1.hasKey("damage")) {
			this.damage = var1.getDouble("damage");
		}

		if(var1.hasKey("pickup")) {
			this.canBePickedUp = var1.getByte("pickup");
		} else if(var1.hasKey("player")) {
			this.canBePickedUp = var1.getBoolean("player") ? 1 : 0;
		}

	}

	public void onCollideWithPlayer(EntityPlayer var1) {
		if(!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
			boolean var2 = this.canBePickedUp == 1 || this.canBePickedUp == 2 && var1.capabilities.isCreativeMode;
			if(this.canBePickedUp == 1 && !var1.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
				var2 = false;
			}

			if(var2) {
				this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				var1.onItemPickup(this, 1);
				this.setDead();
			}

		}
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void setDamage(double var1) {
		this.damage = var1;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setKnockbackStrength(int var1) {
		this.knockbackStrength = var1;
	}

	public boolean canAttackWithItem() {
		return false;
	}

	public void setIsCritical(boolean var1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(16);
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
		}

	}

	public boolean getIsCritical() {
		byte var1 = this.dataWatcher.getWatchableObjectByte(16);
		return (var1 & 1) != 0;
	}
}
