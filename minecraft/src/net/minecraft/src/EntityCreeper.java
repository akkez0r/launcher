package net.minecraft.src;

public class EntityCreeper extends EntityMob {
	private int lastActiveTime;
	private int timeSinceIgnited;
	private int fuseTime = 30;
	private int explosionRadius = 3;

	public EntityCreeper(World var1) {
		super(var1);
		this.texture = "/mob/creeper.png";
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAICreeperSwell(this));
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 0.25F, 0.3F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.25F, false));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int func_82143_as() {
		return this.getAttackTarget() == null ? 3 : 3 + (this.health - 1);
	}

	protected void fall(float var1) {
		super.fall(var1);
		this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + var1 * 1.5F);
		if(this.timeSinceIgnited > this.fuseTime - 5) {
			this.timeSinceIgnited = this.fuseTime - 5;
		}

	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)-1));
		this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		if(this.dataWatcher.getWatchableObjectByte(17) == 1) {
			var1.setBoolean("powered", true);
		}

		var1.setShort("Fuse", (short)this.fuseTime);
		var1.setByte("ExplosionRadius", (byte)this.explosionRadius);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte)(var1.getBoolean("powered") ? 1 : 0)));
		if(var1.hasKey("Fuse")) {
			this.fuseTime = var1.getShort("Fuse");
		}

		if(var1.hasKey("ExplosionRadius")) {
			this.explosionRadius = var1.getByte("ExplosionRadius");
		}

	}

	public void onUpdate() {
		if(this.isEntityAlive()) {
			this.lastActiveTime = this.timeSinceIgnited;
			int var1 = this.getCreeperState();
			if(var1 > 0 && this.timeSinceIgnited == 0) {
				this.playSound("random.fuse", 1.0F, 0.5F);
			}

			this.timeSinceIgnited += var1;
			if(this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if(this.timeSinceIgnited >= this.fuseTime) {
				this.timeSinceIgnited = this.fuseTime;
				if(!this.worldObj.isRemote) {
					boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
					if(this.getPowered()) {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(this.explosionRadius * 2), var2);
					} else {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, var2);
					}

					this.setDead();
				}
			}
		}

		super.onUpdate();
	}

	protected String getHurtSound() {
		return "mob.creeper.say";
	}

	protected String getDeathSound() {
		return "mob.creeper.death";
	}

	public void onDeath(DamageSource var1) {
		super.onDeath(var1);
		if(var1.getEntity() instanceof EntitySkeleton) {
			int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
			this.dropItem(var2, 1);
		}

	}

	public boolean attackEntityAsMob(Entity var1) {
		return true;
	}

	public boolean getPowered() {
		return this.dataWatcher.getWatchableObjectByte(17) == 1;
	}

	public float getCreeperFlashIntensity(float var1) {
		return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * var1) / (float)(this.fuseTime - 2);
	}

	protected int getDropItemId() {
		return Item.gunpowder.itemID;
	}

	public int getCreeperState() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public void setCreeperState(int var1) {
		this.dataWatcher.updateObject(16, Byte.valueOf((byte)var1));
	}

	public void onStruckByLightning(EntityLightningBolt var1) {
		super.onStruckByLightning(var1);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte)1));
	}
}
