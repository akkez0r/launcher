package net.minecraft.src;

public class Vec3 {
	public static final Vec3Pool fakePool = new Vec3Pool(-1, -1);
	public final Vec3Pool myVec3LocalPool;
	public double xCoord;
	public double yCoord;
	public double zCoord;

	public static Vec3 createVectorHelper(double var0, double var2, double var4) {
		return new Vec3(fakePool, var0, var2, var4);
	}

	protected Vec3(Vec3Pool var1, double var2, double var4, double var6) {
		if(var2 == -0.0D) {
			var2 = 0.0D;
		}

		if(var4 == -0.0D) {
			var4 = 0.0D;
		}

		if(var6 == -0.0D) {
			var6 = 0.0D;
		}

		this.xCoord = var2;
		this.yCoord = var4;
		this.zCoord = var6;
		this.myVec3LocalPool = var1;
	}

	protected Vec3 setComponents(double var1, double var3, double var5) {
		this.xCoord = var1;
		this.yCoord = var3;
		this.zCoord = var5;
		return this;
	}

	public Vec3 subtract(Vec3 var1) {
		return this.myVec3LocalPool.getVecFromPool(var1.xCoord - this.xCoord, var1.yCoord - this.yCoord, var1.zCoord - this.zCoord);
	}

	public Vec3 normalize() {
		double var1 = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
		return var1 < 1.0E-4D ? this.myVec3LocalPool.getVecFromPool(0.0D, 0.0D, 0.0D) : this.myVec3LocalPool.getVecFromPool(this.xCoord / var1, this.yCoord / var1, this.zCoord / var1);
	}

	public double dotProduct(Vec3 var1) {
		return this.xCoord * var1.xCoord + this.yCoord * var1.yCoord + this.zCoord * var1.zCoord;
	}

	public Vec3 crossProduct(Vec3 var1) {
		return this.myVec3LocalPool.getVecFromPool(this.yCoord * var1.zCoord - this.zCoord * var1.yCoord, this.zCoord * var1.xCoord - this.xCoord * var1.zCoord, this.xCoord * var1.yCoord - this.yCoord * var1.xCoord);
	}

	public Vec3 addVector(double var1, double var3, double var5) {
		return this.myVec3LocalPool.getVecFromPool(this.xCoord + var1, this.yCoord + var3, this.zCoord + var5);
	}

	public double distanceTo(Vec3 var1) {
		double var2 = var1.xCoord - this.xCoord;
		double var4 = var1.yCoord - this.yCoord;
		double var6 = var1.zCoord - this.zCoord;
		return (double)MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
	}

	public double squareDistanceTo(Vec3 var1) {
		double var2 = var1.xCoord - this.xCoord;
		double var4 = var1.yCoord - this.yCoord;
		double var6 = var1.zCoord - this.zCoord;
		return var2 * var2 + var4 * var4 + var6 * var6;
	}

	public double squareDistanceTo(double var1, double var3, double var5) {
		double var7 = var1 - this.xCoord;
		double var9 = var3 - this.yCoord;
		double var11 = var5 - this.zCoord;
		return var7 * var7 + var9 * var9 + var11 * var11;
	}

	public double lengthVector() {
		return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
	}

	public Vec3 getIntermediateWithXValue(Vec3 var1, double var2) {
		double var4 = var1.xCoord - this.xCoord;
		double var6 = var1.yCoord - this.yCoord;
		double var8 = var1.zCoord - this.zCoord;
		if(var4 * var4 < (double)1.0E-7F) {
			return null;
		} else {
			double var10 = (var2 - this.xCoord) / var4;
			return var10 >= 0.0D && var10 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
		}
	}

	public Vec3 getIntermediateWithYValue(Vec3 var1, double var2) {
		double var4 = var1.xCoord - this.xCoord;
		double var6 = var1.yCoord - this.yCoord;
		double var8 = var1.zCoord - this.zCoord;
		if(var6 * var6 < (double)1.0E-7F) {
			return null;
		} else {
			double var10 = (var2 - this.yCoord) / var6;
			return var10 >= 0.0D && var10 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
		}
	}

	public Vec3 getIntermediateWithZValue(Vec3 var1, double var2) {
		double var4 = var1.xCoord - this.xCoord;
		double var6 = var1.yCoord - this.yCoord;
		double var8 = var1.zCoord - this.zCoord;
		if(var8 * var8 < (double)1.0E-7F) {
			return null;
		} else {
			double var10 = (var2 - this.zCoord) / var8;
			return var10 >= 0.0D && var10 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
		}
	}

	public String toString() {
		return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
	}

	public void rotateAroundX(float var1) {
		float var2 = MathHelper.cos(var1);
		float var3 = MathHelper.sin(var1);
		double var4 = this.xCoord;
		double var6 = this.yCoord * (double)var2 + this.zCoord * (double)var3;
		double var8 = this.zCoord * (double)var2 - this.yCoord * (double)var3;
		this.xCoord = var4;
		this.yCoord = var6;
		this.zCoord = var8;
	}

	public void rotateAroundY(float var1) {
		float var2 = MathHelper.cos(var1);
		float var3 = MathHelper.sin(var1);
		double var4 = this.xCoord * (double)var2 + this.zCoord * (double)var3;
		double var6 = this.yCoord;
		double var8 = this.zCoord * (double)var2 - this.xCoord * (double)var3;
		this.xCoord = var4;
		this.yCoord = var6;
		this.zCoord = var8;
	}

	public void rotateAroundZ(float var1) {
		float var2 = MathHelper.cos(var1);
		float var3 = MathHelper.sin(var1);
		double var4 = this.xCoord * (double)var2 + this.yCoord * (double)var3;
		double var6 = this.yCoord * (double)var2 - this.xCoord * (double)var3;
		double var8 = this.zCoord;
		this.xCoord = var4;
		this.yCoord = var6;
		this.zCoord = var8;
	}
}
