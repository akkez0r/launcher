package net.minecraft.src;

public class EntityTNTPrimed extends Entity {
	public int fuse;
	private EntityLiving tntPlacedBy;

	public EntityTNTPrimed(World var1) {
		super(var1);
		this.fuse = 0;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
	}

	public EntityTNTPrimed(World var1, double var2, double var4, double var6, EntityLiving var8) {
		this(var1);
		this.setPosition(var2, var4, var6);
		float var9 = (float)(Math.random() * (double)((float)Math.PI) * 2.0D);
		this.motionX = (double)(-((float)Math.sin((double)var9)) * 0.02F);
		this.motionY = (double)0.2F;
		this.motionZ = (double)(-((float)Math.cos((double)var9)) * 0.02F);
		this.fuse = 80;
		this.prevPosX = var2;
		this.prevPosY = var4;
		this.prevPosZ = var6;
		this.tntPlacedBy = var8;
	}

	protected void entityInit() {
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double)0.04F;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.98F;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)0.98F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
			this.motionY *= -0.5D;
		}

		if(this.fuse-- <= 0) {
			this.setDead();
			if(!this.worldObj.isRemote) {
				this.explode();
			}
		} else {
			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

	}

	private void explode() {
		float var1 = 4.0F;
		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, var1, true);
	}

	protected void writeEntityToNBT(NBTTagCompound var1) {
		var1.setByte("Fuse", (byte)this.fuse);
	}

	protected void readEntityFromNBT(NBTTagCompound var1) {
		this.fuse = var1.getByte("Fuse");
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public EntityLiving getTntPlacedBy() {
		return this.tntPlacedBy;
	}
}
