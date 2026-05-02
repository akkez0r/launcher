package net.minecraft.src;

public class EntityIronGolem extends EntityGolem {
	private int homeCheckTimer = 0;
	Village villageObj = null;
	private int attackTimer;
	private int holdRoseTick;

	public EntityIronGolem(World var1) {
		super(var1);
		this.texture = "/mob/villager_golem.png";
		this.setSize(1.4F, 2.9F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 0.25F, true));
		this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.22F, 32.0F));
		this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.16F, true));
		this.tasks.addTask(4, new EntityAIMoveTwardsRestriction(this, 0.16F));
		this.tasks.addTask(5, new EntityAILookAtVillager(this));
		this.tasks.addTask(6, new EntityAIWander(this, 0.16F));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 16.0F, 0, false, true, IMob.mobSelector));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	public boolean isAIEnabled() {
		return true;
	}

	protected void updateAITick() {
		if(--this.homeCheckTimer <= 0) {
			this.homeCheckTimer = 70 + this.rand.nextInt(50);
			this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
			if(this.villageObj == null) {
				this.detachHome();
			} else {
				ChunkCoordinates var1 = this.villageObj.getCenter();
				this.setHomeArea(var1.posX, var1.posY, var1.posZ, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
			}
		}

		super.updateAITick();
	}

	public int getMaxHealth() {
		return 100;
	}

	protected int decreaseAirSupply(int var1) {
		return var1;
	}

	protected void collideWithEntity(Entity var1) {
		if(var1 instanceof IMob && this.getRNG().nextInt(20) == 0) {
			this.setAttackTarget((EntityLiving)var1);
		}

		super.collideWithEntity(var1);
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.attackTimer > 0) {
			--this.attackTimer;
		}

		if(this.holdRoseTick > 0) {
			--this.holdRoseTick;
		}

		if(this.motionX * this.motionX + this.motionZ * this.motionZ > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
			int var1 = MathHelper.floor_double(this.posX);
			int var2 = MathHelper.floor_double(this.posY - (double)0.2F - (double)this.yOffset);
			int var3 = MathHelper.floor_double(this.posZ);
			int var4 = this.worldObj.getBlockId(var1, var2, var3);
			if(var4 > 0) {
				this.worldObj.spawnParticle("tilecrack_" + var4 + "_" + this.worldObj.getBlockMetadata(var1, var2, var3), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
			}
		}

	}

	public boolean canAttackClass(Class var1) {
		return this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(var1) ? false : super.canAttackClass(var1);
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setBoolean("PlayerCreated", this.isPlayerCreated());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.setPlayerCreated(var1.getBoolean("PlayerCreated"));
	}

	public boolean attackEntityAsMob(Entity var1) {
		this.attackTimer = 10;
		this.worldObj.setEntityState(this, (byte)4);
		boolean var2 = var1.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));
		if(var2) {
			var1.motionY += (double)0.4F;
		}

		this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
		return var2;
	}

	public void handleHealthUpdate(byte var1) {
		if(var1 == 4) {
			this.attackTimer = 10;
			this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
		} else if(var1 == 11) {
			this.holdRoseTick = 400;
		} else {
			super.handleHealthUpdate(var1);
		}

	}

	public Village getVillage() {
		return this.villageObj;
	}

	public int getAttackTimer() {
		return this.attackTimer;
	}

	public void setHoldingRose(boolean var1) {
		this.holdRoseTick = var1 ? 400 : 0;
		this.worldObj.setEntityState(this, (byte)11);
	}

	protected String getLivingSound() {
		return "none";
	}

	protected String getHurtSound() {
		return "mob.irongolem.hit";
	}

	protected String getDeathSound() {
		return "mob.irongolem.death";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(3);

		int var4;
		for(var4 = 0; var4 < var3; ++var4) {
			this.dropItem(Block.plantRed.blockID, 1);
		}

		var4 = 3 + this.rand.nextInt(3);

		for(int var5 = 0; var5 < var4; ++var5) {
			this.dropItem(Item.ingotIron.itemID, 1);
		}

	}

	public int getHoldRoseTick() {
		return this.holdRoseTick;
	}

	public boolean isPlayerCreated() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setPlayerCreated(boolean var1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(16);
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
		}

	}

	public void onDeath(DamageSource var1) {
		if(!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
			this.villageObj.setReputationForPlayer(this.attackingPlayer.getCommandSenderName(), -5);
		}

		super.onDeath(var1);
	}
}
