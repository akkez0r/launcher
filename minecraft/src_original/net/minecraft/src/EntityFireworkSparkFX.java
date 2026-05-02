package net.minecraft.src;

public class EntityFireworkSparkFX extends EntityFX {
	private int field_92049_a = 160;
	private boolean field_92054_ax;
	private boolean field_92048_ay;
	private final EffectRenderer field_92047_az;
	private float field_92050_aA;
	private float field_92051_aB;
	private float field_92052_aC;
	private boolean field_92053_aD;

	public EntityFireworkSparkFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14) {
		super(var1, var2, var4, var6);
		this.motionX = var8;
		this.motionY = var10;
		this.motionZ = var12;
		this.field_92047_az = var14;
		this.particleScale *= 12.0F / 16.0F;
		this.particleMaxAge = 48 + this.rand.nextInt(12);
		this.noClip = false;
	}

	public void func_92045_e(boolean var1) {
		this.field_92054_ax = var1;
	}

	public void func_92043_f(boolean var1) {
		this.field_92048_ay = var1;
	}

	public void func_92044_a(int var1) {
		float var2 = (float)((var1 & 16711680) >> 16) / 255.0F;
		float var3 = (float)((var1 & '\uff00') >> 8) / 255.0F;
		float var4 = (float)((var1 & 255) >> 0) / 255.0F;
		float var5 = 1.0F;
		this.setRBGColorF(var2 * var5, var3 * var5, var4 * var5);
	}

	public void func_92046_g(int var1) {
		this.field_92050_aA = (float)((var1 & 16711680) >> 16) / 255.0F;
		this.field_92051_aB = (float)((var1 & '\uff00') >> 8) / 255.0F;
		this.field_92052_aC = (float)((var1 & 255) >> 0) / 255.0F;
		this.field_92053_aD = true;
	}

	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	public boolean canBePushed() {
		return false;
	}

	public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		if(!this.field_92048_ay || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
			super.renderParticle(var1, var2, var3, var4, var5, var6, var7);
		}

	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		if(this.particleAge > this.particleMaxAge / 2) {
			this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / (float)this.particleMaxAge);
			if(this.field_92053_aD) {
				this.particleRed += (this.field_92050_aA - this.particleRed) * 0.2F;
				this.particleGreen += (this.field_92051_aB - this.particleGreen) * 0.2F;
				this.particleBlue += (this.field_92052_aC - this.particleBlue) * 0.2F;
			}
		}

		this.setParticleTextureIndex(this.field_92049_a + (7 - this.particleAge * 8 / this.particleMaxAge));
		this.motionY -= 0.004D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.91F;
		this.motionY *= (double)0.91F;
		this.motionZ *= (double)0.91F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

		if(this.field_92054_ax && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
			EntityFireworkSparkFX var1 = new EntityFireworkSparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.field_92047_az);
			var1.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
			var1.particleAge = var1.particleMaxAge / 2;
			if(this.field_92053_aD) {
				var1.field_92053_aD = true;
				var1.field_92050_aA = this.field_92050_aA;
				var1.field_92051_aB = this.field_92051_aB;
				var1.field_92052_aC = this.field_92052_aC;
			}

			var1.field_92048_ay = this.field_92048_ay;
			this.field_92047_az.addEffect(var1);
		}

	}

	public int getBrightnessForRender(float var1) {
		return 15728880;
	}

	public float getBrightness(float var1) {
		return 1.0F;
	}
}
