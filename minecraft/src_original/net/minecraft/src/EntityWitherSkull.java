package net.minecraft.src;

public class EntityWitherSkull extends EntityFireball {
	public EntityWitherSkull(World var1) {
		super(var1);
		this.setSize(5.0F / 16.0F, 5.0F / 16.0F);
	}

	public EntityWitherSkull(World var1, EntityLiving var2, double var3, double var5, double var7) {
		super(var1, var2, var3, var5, var7);
		this.setSize(5.0F / 16.0F, 5.0F / 16.0F);
	}

	protected float getMotionFactor() {
		return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
	}

	public EntityWitherSkull(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		super(var1, var2, var4, var6, var8, var10, var12);
		this.setSize(5.0F / 16.0F, 5.0F / 16.0F);
	}

	public boolean isBurning() {
		return false;
	}

	public float func_82146_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6) {
		float var7 = super.func_82146_a(var1, var2, var3, var4, var5, var6);
		if(this.isInvulnerable() && var6 != Block.bedrock && var6 != Block.endPortal && var6 != Block.endPortalFrame) {
			var7 = Math.min(0.8F, var7);
		}

		return var7;
	}

	protected void onImpact(MovingObjectPosition var1) {
		if(!this.worldObj.isRemote) {
			if(var1.entityHit != null) {
				if(this.shootingEntity != null) {
					if(var1.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8) && !var1.entityHit.isEntityAlive()) {
						this.shootingEntity.heal(5);
					}
				} else {
					var1.entityHit.attackEntityFrom(DamageSource.magic, 5);
				}

				if(var1.entityHit instanceof EntityLiving) {
					byte var2 = 0;
					if(this.worldObj.difficultySetting > 1) {
						if(this.worldObj.difficultySetting == 2) {
							var2 = 10;
						} else if(this.worldObj.difficultySetting == 3) {
							var2 = 40;
						}
					}

					if(var2 > 0) {
						((EntityLiving)var1.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * var2, 1));
					}
				}
			}

			this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
			this.setDead();
		}

	}

	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		return false;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
	}

	public boolean isInvulnerable() {
		return this.dataWatcher.getWatchableObjectByte(10) == 1;
	}

	public void setInvulnerable(boolean var1) {
		this.dataWatcher.updateObject(10, Byte.valueOf((byte)(var1 ? 1 : 0)));
	}
}
