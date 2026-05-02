package net.minecraft.src;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class TextureStitched implements Icon {
	private final String textureName;
	protected Texture textureSheet;
	protected List textureList;
	private List listAnimationTuples;
	protected boolean rotated;
	protected int originX;
	protected int originY;
	private int width;
	private int height;
	private float minU;
	private float maxU;
	private float minV;
	private float maxV;
	private float widthNorm;
	private float heightNorm;
	protected int frameCounter = 0;
	protected int tickCounter = 0;

	public static TextureStitched makeTextureStitched(String var0) {
		return (TextureStitched)("clock".equals(var0) ? new TextureClock() : ("compass".equals(var0) ? new TextureCompass() : new TextureStitched(var0)));
	}

	protected TextureStitched(String var1) {
		this.textureName = var1;
	}

	public void init(Texture var1, List var2, int var3, int var4, int var5, int var6, boolean var7) {
		this.textureSheet = var1;
		this.textureList = var2;
		this.originX = var3;
		this.originY = var4;
		this.width = var5;
		this.height = var6;
		this.rotated = var7;
		float var8 = 0.01F / (float)var1.getWidth();
		float var9 = 0.01F / (float)var1.getHeight();
		this.minU = (float)var3 / (float)var1.getWidth() + var8;
		this.maxU = (float)(var3 + var5) / (float)var1.getWidth() - var8;
		this.minV = (float)var4 / (float)var1.getHeight() + var9;
		this.maxV = (float)(var4 + var6) / (float)var1.getHeight() - var9;
		this.widthNorm = (float)var5 / 16.0F;
		this.heightNorm = (float)var6 / 16.0F;
	}

	public void copyFrom(TextureStitched var1) {
		this.init(var1.textureSheet, var1.textureList, var1.originX, var1.originY, var1.width, var1.height, var1.rotated);
	}

	public int getOriginX() {
		return this.originX;
	}

	public int getOriginY() {
		return this.originY;
	}

	public float getMinU() {
		return this.minU;
	}

	public float getMaxU() {
		return this.maxU;
	}

	public float getInterpolatedU(double var1) {
		float var3 = this.maxU - this.minU;
		return this.minU + var3 * ((float)var1 / 16.0F);
	}

	public float getMinV() {
		return this.minV;
	}

	public float getMaxV() {
		return this.maxV;
	}

	public float getInterpolatedV(double var1) {
		float var3 = this.maxV - this.minV;
		return this.minV + var3 * ((float)var1 / 16.0F);
	}

	public String getIconName() {
		return this.textureName;
	}

	public int getSheetWidth() {
		return this.textureSheet.getWidth();
	}

	public int getSheetHeight() {
		return this.textureSheet.getHeight();
	}

	public void updateAnimation() {
		if(this.listAnimationTuples != null) {
			Tuple var1 = (Tuple)this.listAnimationTuples.get(this.frameCounter);
			++this.tickCounter;
			if(this.tickCounter >= ((Integer)var1.getSecond()).intValue()) {
				int var2 = ((Integer)var1.getFirst()).intValue();
				this.frameCounter = (this.frameCounter + 1) % this.listAnimationTuples.size();
				this.tickCounter = 0;
				var1 = (Tuple)this.listAnimationTuples.get(this.frameCounter);
				int var3 = ((Integer)var1.getFirst()).intValue();
				if(var2 != var3 && var3 >= 0 && var3 < this.textureList.size()) {
					this.textureSheet.func_104062_b(this.originX, this.originY, (Texture)this.textureList.get(var3));
				}
			}
		} else {
			int var4 = this.frameCounter;
			this.frameCounter = (this.frameCounter + 1) % this.textureList.size();
			if(var4 != this.frameCounter) {
				this.textureSheet.func_104062_b(this.originX, this.originY, (Texture)this.textureList.get(this.frameCounter));
			}
		}

	}

	public void readAnimationInfo(BufferedReader var1) {
		ArrayList var2 = new ArrayList();

		try {
			for(String var3 = var1.readLine(); var3 != null; var3 = var1.readLine()) {
				var3 = var3.trim();
				if(var3.length() > 0) {
					String[] var4 = var3.split(",");
					String[] var5 = var4;
					int var6 = var4.length;

					for(int var7 = 0; var7 < var6; ++var7) {
						String var8 = var5[var7];
						int var9 = var8.indexOf(42);
						if(var9 > 0) {
							Integer var10 = new Integer(var8.substring(0, var9));
							Integer var11 = new Integer(var8.substring(var9 + 1));
							var2.add(new Tuple(var10, var11));
						} else {
							var2.add(new Tuple(new Integer(var8), Integer.valueOf(1)));
						}
					}
				}
			}
		} catch (Exception var12) {
			System.err.println("Failed to read animation info for " + this.textureName + ": " + var12.getMessage());
		}

		if(!var2.isEmpty() && var2.size() < 600) {
			this.listAnimationTuples = var2;
		}

	}
}
