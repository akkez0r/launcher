package net.minecraft.src;

public class EntityPig extends EntityAnimal {
	private final EntityAIControlledByPlayer aiControlledByPlayer;

	public EntityPig(World var1) {
		super(var1);
		this.texture = "/mob/pig.png";
		this.setSize(0.9F, 0.9F);
		this.getNavigator().setAvoidsWater(true);
		float var2 = 0.25F;
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.34F));
		this.tasks.addTask(3, new EntityAIMate(this, var2));
		this.tasks.addTask(4, new EntityAITempt(this, 0.3F, Item.carrotOnAStick.itemID, false));
		this.tasks.addTask(4, new EntityAITempt(this, 0.3F, Item.carrot.itemID, false));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 0.28F));
		this.tasks.addTask(6, new EntityAIWander(this, var2));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 10;
	}

	protected void updateAITasks() {
		super.updateAITasks();
	}

	public boolean canBeSteered() {
		ItemStack var1 = ((EntityPlayer)this.riddenByEntity).getHeldItem();
		return var1 != null && var1.itemID == Item.carrotOnAStick.itemID;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setBoolean("Saddle", this.getSaddled());
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.setSaddled(var1.getBoolean("Saddle"));
	}

	protected String getLivingSound() {
		return "mob.pig.say";
	}

	protected String getHurtSound() {
		return "mob.pig.say";
	}

	protected String getDeathSound() {
		return "mob.pig.death";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.pig.step", 0.15F, 1.0F);
	}

	public boolean interact(EntityPlayer var1) {
		if(super.interact(var1)) {
			return true;
		} else if(!this.getSaddled() || this.worldObj.isRemote || this.riddenByEntity != null && this.riddenByEntity != var1) {
			return false;
		} else {
			var1.mountEntity(this);
			return true;
		}
	}

	protected int getDropItemId() {
		return this.isBurning() ? Item.porkCooked.itemID : Item.porkRaw.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + var2);

		for(int var4 = 0; var4 < var3; ++var4) {
			if(this.isBurning()) {
				this.dropItem(Item.porkCooked.itemID, 1);
			} else {
				this.dropItem(Item.porkRaw.itemID, 1);
			}
		}

		if(this.getSaddled()) {
			this.dropItem(Item.saddle.itemID, 1);
		}

	}

	public boolean getSaddled() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setSaddled(boolean var1) {
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)1));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)0));
		}

	}

	public void onStruckByLightning(EntityLightningBolt var1) {
		if(!this.worldObj.isRemote) {
			EntityPigZombie var2 = new EntityPigZombie(this.worldObj);
			var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.worldObj.spawnEntityInWorld(var2);
			this.setDead();
		}
	}

	protected void fall(float var1) {
		super.fall(var1);
		if(var1 > 5.0F && this.riddenByEntity instanceof EntityPlayer) {
			((EntityPlayer)this.riddenByEntity).triggerAchievement(AchievementList.flyPig);
		}

	}

	public EntityPig spawnBabyAnimal(EntityAgeable var1) {
		return new EntityPig(this.worldObj);
	}

	public boolean isBreedingItem(ItemStack var1) {
		return var1 != null && var1.itemID == Item.carrot.itemID;
	}

	public EntityAIControlledByPlayer getAIControlledByPlayer() {
		return this.aiControlledByPlayer;
	}

	public EntityAgeable createChild(EntityAgeable var1) {
		return this.spawnBabyAnimal(var1);
	}
}
