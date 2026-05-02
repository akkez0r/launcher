package net.minecraft.src;

public class EntityChicken extends EntityAnimal {
	public boolean field_70885_d = false;
	public float field_70886_e = 0.0F;
	public float destPos = 0.0F;
	public float field_70884_g;
	public float field_70888_h;
	public float field_70889_i = 1.0F;
	public int timeUntilNextEgg;

	public EntityChicken(World var1) {
		super(var1);
		this.texture = "/mob/chicken.png";
		this.setSize(0.3F, 0.7F);
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		float var2 = 0.25F;
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		this.tasks.addTask(2, new EntityAIMate(this, var2));
		this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.seeds.itemID, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.28F));
		this.tasks.addTask(5, new EntityAIWander(this, var2));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 4;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_70888_h = this.field_70886_e;
		this.field_70884_g = this.destPos;
		this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.destPos < 0.0F) {
			this.destPos = 0.0F;
		}

		if(this.destPos > 1.0F) {
			this.destPos = 1.0F;
		}

		if(!this.onGround && this.field_70889_i < 1.0F) {
			this.field_70889_i = 1.0F;
		}

		this.field_70889_i = (float)((double)this.field_70889_i * 0.9D);
		if(!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		this.field_70886_e += this.field_70889_i * 2.0F;
		if(!this.isChild() && !this.worldObj.isRemote && --this.timeUntilNextEgg <= 0) {
			this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Item.egg.itemID, 1);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

	protected void fall(float var1) {
	}

	protected String getLivingSound() {
		return "mob.chicken.say";
	}

	protected String getHurtSound() {
		return "mob.chicken.hurt";
	}

	protected String getDeathSound() {
		return "mob.chicken.hurt";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.chicken.step", 0.15F, 1.0F);
	}

	protected int getDropItemId() {
		return Item.feather.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + var2);

		for(int var4 = 0; var4 < var3; ++var4) {
			this.dropItem(Item.feather.itemID, 1);
		}

		if(this.isBurning()) {
			this.dropItem(Item.chickenCooked.itemID, 1);
		} else {
			this.dropItem(Item.chickenRaw.itemID, 1);
		}

	}

	public EntityChicken spawnBabyAnimal(EntityAgeable var1) {
		return new EntityChicken(this.worldObj);
	}

	public boolean isBreedingItem(ItemStack var1) {
		return var1 != null && var1.getItem() instanceof ItemSeeds;
	}

	public EntityAgeable createChild(EntityAgeable var1) {
		return this.spawnBabyAnimal(var1);
	}
}
