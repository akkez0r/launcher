package net.minecraft.src;

public class EntityFX extends Entity {
	protected int particleTextureIndexX;
	protected int particleTextureIndexY;
	protected float particleTextureJitterX;
	protected float particleTextureJitterY;
	protected int particleAge;
	protected int particleMaxAge;
	protected float particleScale;
	protected float particleGravity;
	protected float particleRed;
	protected float particleGreen;
	protected float particleBlue;
	protected float particleAlpha;
	protected Icon particleIcon;
	public static double interpPosX;
	public static double interpPosY;
	public static double interpPosZ;

	protected EntityFX(World var1, double var2, double var4, double var6) {
		super(var1);
		this.particleAge = 0;
		this.particleMaxAge = 0;
		this.particleAlpha = 1.0F;
		this.particleIcon = null;
		this.setSize(0.2F, 0.2F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(var2, var4, var6);
		this.lastTickPosX = var2;
		this.lastTickPosY = var4;
		this.lastTickPosZ = var6;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
		this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
		this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
		this.particleAge = 0;
	}

	public EntityFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		this(var1, var2, var4, var6);
		this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		float var15 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = this.motionX / (double)var15 * (double)var14 * (double)0.4F;
		this.motionY = this.motionY / (double)var15 * (double)var14 * (double)0.4F + (double)0.1F;
		this.motionZ = this.motionZ / (double)var15 * (double)var14 * (double)0.4F;
	}

	public EntityFX multiplyVelocity(float var1) {
		this.motionX *= (double)var1;
		this.motionY = (this.motionY - (double)0.1F) * (double)var1 + (double)0.1F;
		this.motionZ *= (double)var1;
		return this;
	}

	public EntityFX multipleParticleScaleBy(float var1) {
		this.setSize(0.2F * var1, 0.2F * var1);
		this.particleScale *= var1;
		return this;
	}

	public void setRBGColorF(float var1, float var2, float var3) {
		this.particleRed = var1;
		this.particleGreen = var2;
		this.particleBlue = var3;
	}

	public void setAlphaF(float var1) {
		this.particleAlpha = var1;
	}

	public float getRedColorF() {
		return this.particleRed;
	}

	public float getGreenColorF() {
		return this.particleGreen;
	}

	public float getBlueColorF() {
		return this.particleBlue;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.motionY -= 0.04D * (double)this.particleGravity;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.98F;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)0.98F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}

	public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8 = (float)this.particleTextureIndexX / 16.0F;
		float var9 = var8 + 0.999F / 16.0F;
		float var10 = (float)this.particleTextureIndexY / 16.0F;
		float var11 = var10 + 0.999F / 16.0F;
		float var12 = 0.1F * this.particleScale;
		if(this.particleIcon != null) {
			var8 = this.particleIcon.getMinU();
			var9 = this.particleIcon.getMaxU();
			var10 = this.particleIcon.getMinV();
			var11 = this.particleIcon.getMaxV();
		}

		float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - interpPosX);
		float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - interpPosY);
		float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - interpPosZ);
		float var16 = 1.0F;
		var1.setColorRGBA_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, this.particleAlpha);
		var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var9, (double)var11);
		var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var9, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var8, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var8, (double)var11);
	}

	public int getFXLayer() {
		return 0;
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
	}

	public void setParticleIcon(RenderEngine var1, Icon var2) {
		if(this.getFXLayer() == 1) {
			this.particleIcon = var2;
		} else {
			if(this.getFXLayer() != 2) {
				throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
			}

			this.particleIcon = var2;
		}

	}

	public void setParticleTextureIndex(int var1) {
		if(this.getFXLayer() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.particleTextureIndexX = var1 % 16;
			this.particleTextureIndexY = var1 / 16;
		}
	}

	public void nextTextureIndexX() {
		++this.particleTextureIndexX;
	}

	public boolean canAttackWithItem() {
		return false;
	}

	public String toString() {
		return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
	}
}
