package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class TextureCompass extends TextureStitched {
	public static TextureCompass compassTexture;
	public double currentAngle;
	public double angleDelta;

	public TextureCompass() {
		super("compass");
		compassTexture = this;
	}

	public void updateAnimation() {
		Minecraft var1 = Minecraft.getMinecraft();
		if(var1.theWorld != null && var1.thePlayer != null) {
			this.updateCompass(var1.theWorld, var1.thePlayer.posX, var1.thePlayer.posZ, (double)var1.thePlayer.rotationYaw, false, false);
		} else {
			this.updateCompass((World)null, 0.0D, 0.0D, 0.0D, true, false);
		}

	}

	public void updateCompass(World var1, double var2, double var4, double var6, boolean var8, boolean var9) {
		double var10 = 0.0D;
		if(var1 != null && !var8) {
			ChunkCoordinates var12 = var1.getSpawnPoint();
			double var13 = (double)var12.posX - var2;
			double var15 = (double)var12.posZ - var4;
			var6 %= 360.0D;
			var10 = -((var6 - 90.0D) * Math.PI / 180.0D - Math.atan2(var15, var13));
			if(!var1.provider.isSurfaceWorld()) {
				var10 = Math.random() * (double)((float)Math.PI) * 2.0D;
			}
		}

		if(var9) {
			this.currentAngle = var10;
		} else {
			double var17;
			for(var17 = var10 - this.currentAngle; var17 < -Math.PI; var17 += Math.PI * 2.0D) {
			}

			while(var17 >= Math.PI) {
				var17 -= Math.PI * 2.0D;
			}

			if(var17 < -1.0D) {
				var17 = -1.0D;
			}

			if(var17 > 1.0D) {
				var17 = 1.0D;
			}

			this.angleDelta += var17 * 0.1D;
			this.angleDelta *= 0.8D;
			this.currentAngle += this.angleDelta;
		}

		int var18;
		for(var18 = (int)((this.currentAngle / (Math.PI * 2.0D) + 1.0D) * (double)this.textureList.size()) % this.textureList.size(); var18 < 0; var18 = (var18 + this.textureList.size()) % this.textureList.size()) {
		}

		if(var18 != this.frameCounter) {
			this.frameCounter = var18;
			this.textureSheet.copyFrom(this.originX, this.originY, (Texture)this.textureList.get(this.frameCounter), this.rotated);
		}

	}
}
