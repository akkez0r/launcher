package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public abstract class EntityHanging extends Entity {
	private int tickCounter1;
	public int hangingDirection;
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public EntityHanging(World var1) {
		super(var1);
		this.tickCounter1 = 0;
		this.hangingDirection = 0;
		this.yOffset = 0.0F;
		this.setSize(0.5F, 0.5F);
	}

	public EntityHanging(World var1, int var2, int var3, int var4, int var5) {
		this(var1);
		this.xPosition = var2;
		this.yPosition = var3;
		this.zPosition = var4;
	}

	protected void entityInit() {
	}

	public void setDirection(int var1) {
		this.hangingDirection = var1;
		this.prevRotationYaw = this.rotationYaw = (float)(var1 * 90);
		float var2 = (float)this.func_82329_d();
		float var3 = (float)this.func_82330_g();
		float var4 = (float)this.func_82329_d();
		if(var1 != 2 && var1 != 0) {
			var2 = 0.5F;
		} else {
			var4 = 0.5F;
			this.rotationYaw = this.prevRotationYaw = (float)(Direction.rotateOpposite[var1] * 90);
		}

		var2 /= 32.0F;
		var3 /= 32.0F;
		var4 /= 32.0F;
		float var5 = (float)this.xPosition + 0.5F;
		float var6 = (float)this.yPosition + 0.5F;
		float var7 = (float)this.zPosition + 0.5F;
		float var8 = 9.0F / 16.0F;
		if(var1 == 2) {
			var7 -= var8;
		}

		if(var1 == 1) {
			var5 -= var8;
		}

		if(var1 == 0) {
			var7 += var8;
		}

		if(var1 == 3) {
			var5 += var8;
		}

		if(var1 == 2) {
			var5 -= this.func_70517_b(this.func_82329_d());
		}

		if(var1 == 1) {
			var7 += this.func_70517_b(this.func_82329_d());
		}

		if(var1 == 0) {
			var5 += this.func_70517_b(this.func_82329_d());
		}

		if(var1 == 3) {
			var7 -= this.func_70517_b(this.func_82329_d());
		}

		var6 += this.func_70517_b(this.func_82330_g());
		this.setPosition((double)var5, (double)var6, (double)var7);
		float var9 = -0.03125F;
		this.boundingBox.setBounds((double)(var5 - var2 - var9), (double)(var6 - var3 - var9), (double)(var7 - var4 - var9), (double)(var5 + var2 + var9), (double)(var6 + var3 + var9), (double)(var7 + var4 + var9));
	}

	private float func_70517_b(int var1) {
		return var1 == 32 ? 0.5F : (var1 == 64 ? 0.5F : 0.0F);
	}

	public void onUpdate() {
		if(this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
			this.tickCounter1 = 0;
			if(!this.isDead && !this.onValidSurface()) {
				this.setDead();
				this.dropItemStack();
			}
		}

	}

	public boolean onValidSurface() {
		if(!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
			return false;
		} else {
			int var1 = Math.max(1, this.func_82329_d() / 16);
			int var2 = Math.max(1, this.func_82330_g() / 16);
			int var3 = this.xPosition;
			int var4 = this.yPosition;
			int var5 = this.zPosition;
			if(this.hangingDirection == 2) {
				var3 = MathHelper.floor_double(this.posX - (double)((float)this.func_82329_d() / 32.0F));
			}

			if(this.hangingDirection == 1) {
				var5 = MathHelper.floor_double(this.posZ - (double)((float)this.func_82329_d() / 32.0F));
			}

			if(this.hangingDirection == 0) {
				var3 = MathHelper.floor_double(this.posX - (double)((float)this.func_82329_d() / 32.0F));
			}

			if(this.hangingDirection == 3) {
				var5 = MathHelper.floor_double(this.posZ - (double)((float)this.func_82329_d() / 32.0F));
			}

			var4 = MathHelper.floor_double(this.posY - (double)((float)this.func_82330_g() / 32.0F));

			for(int var6 = 0; var6 < var1; ++var6) {
				for(int var7 = 0; var7 < var2; ++var7) {
					Material var8;
					if(this.hangingDirection != 2 && this.hangingDirection != 0) {
						var8 = this.worldObj.getBlockMaterial(this.xPosition, var4 + var7, var5 + var6);
					} else {
						var8 = this.worldObj.getBlockMaterial(var3 + var6, var4 + var7, this.zPosition);
					}

					if(!var8.isSolid()) {
						return false;
					}
				}
			}

			List var9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
			Iterator var10 = var9.iterator();

			Entity var11;
			do {
				if(!var10.hasNext()) {
					return true;
				}

				var11 = (Entity)var10.next();
			} while(!(var11 instanceof EntityHanging));

			return false;
		}
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean func_85031_j(Entity var1) {
		return var1 instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)var1), 0) : false;
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else {
			if(!this.isDead && !this.worldObj.isRemote) {
				this.setDead();
				this.setBeenAttacked();
				EntityPlayer var3 = null;
				if(var1.getEntity() instanceof EntityPlayer) {
					var3 = (EntityPlayer)var1.getEntity();
				}

				if(var3 != null && var3.capabilities.isCreativeMode) {
					return true;
				}

				this.dropItemStack();
			}

			return true;
		}
	}

	public void moveEntity(double var1, double var3, double var5) {
		if(!this.worldObj.isRemote && !this.isDead && var1 * var1 + var3 * var3 + var5 * var5 > 0.0D) {
			this.setDead();
			this.dropItemStack();
		}

	}

	public void addVelocity(double var1, double var3, double var5) {
		if(!this.worldObj.isRemote && !this.isDead && var1 * var1 + var3 * var3 + var5 * var5 > 0.0D) {
			this.setDead();
			this.dropItemStack();
		}

	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		var1.setByte("Direction", (byte)this.hangingDirection);
		var1.setInteger("TileX", this.xPosition);
		var1.setInteger("TileY", this.yPosition);
		var1.setInteger("TileZ", this.zPosition);
		switch(this.hangingDirection) {
		case 0:
			var1.setByte("Dir", (byte)2);
			break;
		case 1:
			var1.setByte("Dir", (byte)1);
			break;
		case 2:
			var1.setByte("Dir", (byte)0);
			break;
		case 3:
			var1.setByte("Dir", (byte)3);
		}

	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		if(var1.hasKey("Direction")) {
			this.hangingDirection = var1.getByte("Direction");
		} else {
			switch(var1.getByte("Dir")) {
			case 0:
				this.hangingDirection = 2;
				break;
			case 1:
				this.hangingDirection = 1;
				break;
			case 2:
				this.hangingDirection = 0;
				break;
			case 3:
				this.hangingDirection = 3;
			}
		}

		this.xPosition = var1.getInteger("TileX");
		this.yPosition = var1.getInteger("TileY");
		this.zPosition = var1.getInteger("TileZ");
		this.setDirection(this.hangingDirection);
	}

	public abstract int func_82329_d();

	public abstract int func_82330_g();

	public abstract void dropItemStack();
}
