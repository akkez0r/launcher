package net.minecraft.src;

public class EntityCow extends EntityAnimal {
	public EntityCow(World var1) {
		super(var1);
		this.texture = "/mob/cow.png";
		this.setSize(0.9F, 1.3F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		this.tasks.addTask(2, new EntityAIMate(this, 0.2F));
		this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.wheat.itemID, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25F));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 10;
	}

	protected String getLivingSound() {
		return "mob.cow.say";
	}

	protected String getHurtSound() {
		return "mob.cow.hurt";
	}

	protected String getDeathSound() {
		return "mob.cow.hurt";
	}

	protected void playStepSound(int var1, int var2, int var3, int var4) {
		this.playSound("mob.cow.step", 0.15F, 1.0F);
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return Item.leather.itemID;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + var2);

		int var4;
		for(var4 = 0; var4 < var3; ++var4) {
			this.dropItem(Item.leather.itemID, 1);
		}

		var3 = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + var2);

		for(var4 = 0; var4 < var3; ++var4) {
			if(this.isBurning()) {
				this.dropItem(Item.beefCooked.itemID, 1);
			} else {
				this.dropItem(Item.beefRaw.itemID, 1);
			}
		}

	}

	public boolean interact(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.getCurrentItem();
		if(var2 != null && var2.itemID == Item.bucketEmpty.itemID) {
			if(--var2.stackSize <= 0) {
				var1.inventory.setInventorySlotContents(var1.inventory.currentItem, new ItemStack(Item.bucketMilk));
			} else if(!var1.inventory.addItemStackToInventory(new ItemStack(Item.bucketMilk))) {
				var1.dropPlayerItem(new ItemStack(Item.bucketMilk.itemID, 1, 0));
			}

			return true;
		} else {
			return super.interact(var1);
		}
	}

	public EntityCow spawnBabyAnimal(EntityAgeable var1) {
		return new EntityCow(this.worldObj);
	}

	public EntityAgeable createChild(EntityAgeable var1) {
		return this.spawnBabyAnimal(var1);
	}
}
