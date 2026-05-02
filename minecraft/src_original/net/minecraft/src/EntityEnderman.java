package net.minecraft.src;

public class EntityEnderman extends EntityMob {
	private static boolean[] carriableBlocks = new boolean[256];
	private int teleportDelay = 0;
	private int field_70826_g = 0;
	private boolean field_104003_g;

	public EntityEnderman(World var1) {
		super(var1);
		this.texture = "/mob/enderman.png";
		this.moveSpeed = 0.2F;
		this.setSize(0.6F, 2.9F);
		this.stepHeight = 1.0F;
	}

	public int getMaxHealth() {
		return 40;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte)0));
		this.dataWatcher.addObject(17, new Byte((byte)0));
		this.dataWatcher.addObject(18, new Byte((byte)0));
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setShort("carried", (short)this.getCarried());
		var1.setShort("carriedData", (short)this.getCarryingData());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.setCarried(var1.getShort("carried"));
		this.setCarryingData(var1.getShort("carriedData"));
	}

	protected Entity findPlayerToAttack() {
		EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);
		if(var1 != null) {
			if(this.shouldAttackPlayer(var1)) {
				this.field_104003_g = true;
				if(this.field_70826_g == 0) {
					this.worldObj.playSoundAtEntity(var1, "mob.endermen.stare", 1.0F, 1.0F);
				}

				if(this.field_70826_g++ == 5) {
					this.field_70826_g = 0;
					this.setScreaming(true);
					return var1;
				}
			} else {
				this.field_70826_g = 0;
			}
		}

		return null;
	}

	private boolean shouldAttackPlayer(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.armorInventory[3];
		if(var2 != null && var2.itemID == Block.pumpkin.blockID) {
			return false;
		} else {
			Vec3 var3 = var1.getLook(1.0F).normalize();
			Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - var1.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - (var1.posY + (double)var1.getEyeHeight()), this.posZ - var1.posZ);
			double var5 = var4.lengthVector();
			var4 = var4.normalize();
			double var7 = var3.dotProduct(var4);
			return var7 > 1.0D - 0.025D / var5 ? var1.canEntityBeSeen(this) : false;
		}
	}

	public void onLivingUpdate() {
		if(this.isWet()) {
			this.attackEntityFrom(DamageSource.drown, 1);
		}

		this.moveSpeed = this.entityToAttack != null ? 6.5F : 0.3F;
		int var1;
		if(!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
			int var2;
			int var3;
			int var4;
			if(this.getCarried() == 0) {
				if(this.rand.nextInt(20) == 0) {
					var1 = MathHelper.floor_double(this.posX - 2.0D + this.rand.nextDouble() * 4.0D);
					var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 3.0D);
					var3 = MathHelper.floor_double(this.posZ - 2.0D + this.rand.nextDouble() * 4.0D);
					var4 = this.worldObj.getBlockId(var1, var2, var3);
					if(carriableBlocks[var4]) {
						this.setCarried(this.worldObj.getBlockId(var1, var2, var3));
						this.setCarryingData(this.worldObj.getBlockMetadata(var1, var2, var3));
						this.worldObj.setBlock(var1, var2, var3, 0);
					}
				}
			} else if(this.rand.nextInt(2000) == 0) {
				var1 = MathHelper.floor_double(this.posX - 1.0D + this.rand.nextDouble() * 2.0D);
				var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 2.0D);
				var3 = MathHelper.floor_double(this.posZ - 1.0D + this.rand.nextDouble() * 2.0D);
				var4 = this.worldObj.getBlockId(var1, var2, var3);
				int var5 = this.worldObj.getBlockId(var1, var2 - 1, var3);
				if(var4 == 0 && var5 > 0 && Block.blocksList[var5].renderAsNormalBlock()) {
					this.worldObj.setBlock(var1, var2, var3, this.getCarried(), this.getCarryingData(), 3);
					this.setCarried(0);
				}
			}
		}

		for(var1 = 0; var1 < 2; ++var1) {
			this.worldObj.spawnParticle("portal", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
		}

		if(this.worldObj.isDaytime() && !this.worldObj.isRemote) {
			float var6 = this.getBrightness(1.0F);
			if(var6 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (var6 - 0.4F) * 2.0F) {
				this.entityToAttack = null;
				this.setScreaming(false);
				this.field_104003_g = false;
				this.teleportRandomly();
			}
		}

		if(this.isWet() || this.isBurning()) {
			this.entityToAttack = null;
			this.setScreaming(false);
			this.field_104003_g = false;
			this.teleportRandomly();
		}

		if(this.isScreaming() && !this.field_104003_g && this.rand.nextInt(100) == 0) {
			this.setScreaming(false);
		}

		this.isJumping = false;
		if(this.entityToAttack != null) {
			this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
		}

		if(!this.worldObj.isRemote && this.isEntityAlive()) {
			if(this.entityToAttack != null) {
				if(this.entityToAttack instanceof EntityPlayer && this.shouldAttackPlayer((EntityPlayer)this.entityToAttack)) {
					this.moveStrafing = this.moveForward = 0.0F;
					this.moveSpeed = 0.0F;
					if(this.entityToAttack.getDistanceSqToEntity(this) < 16.0D) {
						this.teleportRandomly();
					}

					this.teleportDelay = 0;
				} else if(this.entityToAttack.getDistanceSqToEntity(this) > 256.0D && this.teleportDelay++ >= 30 && this.teleportToEntity(this.entityToAttack)) {
					this.teleportDelay = 0;
				}
			} else {
				this.setScreaming(false);
				this.teleportDelay = 0;
			}
		}

		super.onLivingUpdate();
	}

	protected boolean teleportRandomly() {
		double var1 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
		double var3 = this.posY + (double)(this.rand.nextInt(64) - 32);
		double var5 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
		return this.teleportTo(var1, var3, var5);
	}

	protected boolean teleportToEntity(Entity var1) {
		Vec3 var2 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - var1.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - var1.posY + (double)var1.getEyeHeight(), this.posZ - var1.posZ);
		var2 = var2.normalize();
		double var3 = 16.0D;
		double var5 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.xCoord * var3;
		double var7 = this.posY + (double)(this.rand.nextInt(16) - 8) - var2.yCoord * var3;
		double var9 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.zCoord * var3;
		return this.teleportTo(var5, var7, var9);
	}

	protected boolean teleportTo(double var1, double var3, double var5) {
		double var7 = this.posX;
		double var9 = this.posY;
		double var11 = this.posZ;
		this.posX = var1;
		this.posY = var3;
		this.posZ = var5;
		boolean var13 = false;
		int var14 = MathHelper.floor_double(this.posX);
		int var15 = MathHelper.floor_double(this.posY);
		int var16 = MathHelper.floor_double(this.posZ);
		int var18;
		if(this.worldObj.blockExists(var14, var15, var16)) {
			boolean var17 = false;

			while(true) {
				while(!var17 && var15 > 0) {
					var18 = this.worldObj.getBlockId(var14, var15 - 1, var16);
					if(var18 != 0 && Block.blocksList[var18].blockMaterial.blocksMovement()) {
						var17 = true;
					} else {
						--this.posY;
						--var15;
					}
				}

				if(var17) {
					this.setPosition(this.posX, this.posY, this.posZ);
					if(this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox)) {
						var13 = true;
					}
				}
				break;
			}
		}

		if(!var13) {
			this.setPosition(var7, var9, var11);
			return false;
		} else {
			short var30 = 128;

			for(var18 = 0; var18 < var30; ++var18) {
				double var19 = (double)var18 / ((double)var30 - 1.0D);
				float var21 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float var22 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				float var23 = (this.rand.nextFloat() - 0.5F) * 0.2F;
				double var24 = var7 + (this.posX - var7) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
				double var26 = var9 + (this.posY - var9) * var19 + this.rand.nextDouble() * (double)this.height;
				double var28 = var11 + (this.posZ - var11) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
				this.worldObj.spawnParticle("portal", var24, var26, var28, (double)var21, (double)var22, (double)var23);
			}

			this.worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
			this.playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	protected String getLivingSound() {
		return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	protected String getHurtSound() {
		return "mob.endermen.hit";
	}

	protected String getDeathSound() {
		return "mob.endermen.death";
	}

	protected int getDropItemId() {
		return Item.enderPearl.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.getDropItemId();
		if(var3 > 0) {
			int var4 = this.rand.nextInt(2 + var2);

			for(int var5 = 0; var5 < var4; ++var5) {
				this.dropItem(var3, 1);
			}
		}

	}

	public void setCarried(int var1) {
		this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var1 & 255)));
	}

	public int getCarried() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public void setCarryingData(int var1) {
		this.dataWatcher.updateObject(17, Byte.valueOf((byte)(var1 & 255)));
	}

	public int getCarryingData() {
		return this.dataWatcher.getWatchableObjectByte(17);
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else {
			this.setScreaming(true);
			if(var1 instanceof EntityDamageSource && var1.getEntity() instanceof EntityPlayer) {
				this.field_104003_g = true;
			}

			if(var1 instanceof EntityDamageSourceIndirect) {
				this.field_104003_g = false;

				for(int var3 = 0; var3 < 64; ++var3) {
					if(this.teleportRandomly()) {
						return true;
					}
				}

				return false;
			} else {
				return super.attackEntityFrom(var1, var2);
			}
		}
	}

	public boolean isScreaming() {
		return this.dataWatcher.getWatchableObjectByte(18) > 0;
	}

	public void setScreaming(boolean var1) {
		this.dataWatcher.updateObject(18, Byte.valueOf((byte)(var1 ? 1 : 0)));
	}

	public int getAttackStrength(Entity var1) {
		return 7;
	}

	static {
		carriableBlocks[Block.grass.blockID] = true;
		carriableBlocks[Block.dirt.blockID] = true;
		carriableBlocks[Block.sand.blockID] = true;
		carriableBlocks[Block.gravel.blockID] = true;
		carriableBlocks[Block.plantYellow.blockID] = true;
		carriableBlocks[Block.plantRed.blockID] = true;
		carriableBlocks[Block.mushroomBrown.blockID] = true;
		carriableBlocks[Block.mushroomRed.blockID] = true;
		carriableBlocks[Block.tnt.blockID] = true;
		carriableBlocks[Block.cactus.blockID] = true;
		carriableBlocks[Block.blockClay.blockID] = true;
		carriableBlocks[Block.pumpkin.blockID] = true;
		carriableBlocks[Block.melon.blockID] = true;
		carriableBlocks[Block.mycelium.blockID] = true;
	}
}
