package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderFish extends Render {
	public void doRenderFishHook(EntityFishHook var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var2, (float)var4, (float)var6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		byte var10 = 1;
		byte var11 = 2;
		this.loadTexture("/particles.png");
		Tessellator var12 = Tessellator.instance;
		float var13 = (float)(var10 * 8 + 0) / 128.0F;
		float var14 = (float)(var10 * 8 + 8) / 128.0F;
		float var15 = (float)(var11 * 8 + 0) / 128.0F;
		float var16 = (float)(var11 * 8 + 8) / 128.0F;
		float var17 = 1.0F;
		float var18 = 0.5F;
		float var19 = 0.5F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		var12.startDrawingQuads();
		var12.setNormal(0.0F, 1.0F, 0.0F);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0D, (double)var13, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0D, (double)var14, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0D, (double)var14, (double)var15);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0D, (double)var13, (double)var15);
		var12.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		if(var1.angler != null) {
			float var20 = var1.angler.getSwingProgress(var9);
			float var21 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float)Math.PI);
			Vec3 var22 = var1.worldObj.getWorldVec3Pool().getVecFromPool(-0.5D, 0.03D, 0.8D);
			var22.rotateAroundX(-(var1.angler.prevRotationPitch + (var1.angler.rotationPitch - var1.angler.prevRotationPitch) * var9) * (float)Math.PI / 180.0F);
			var22.rotateAroundY(-(var1.angler.prevRotationYaw + (var1.angler.rotationYaw - var1.angler.prevRotationYaw) * var9) * (float)Math.PI / 180.0F);
			var22.rotateAroundY(var21 * 0.5F);
			var22.rotateAroundX(-var21 * 0.7F);
			double var23 = var1.angler.prevPosX + (var1.angler.posX - var1.angler.prevPosX) * (double)var9 + var22.xCoord;
			double var25 = var1.angler.prevPosY + (var1.angler.posY - var1.angler.prevPosY) * (double)var9 + var22.yCoord;
			double var27 = var1.angler.prevPosZ + (var1.angler.posZ - var1.angler.prevPosZ) * (double)var9 + var22.zCoord;
			double var29 = var1.angler != Minecraft.getMinecraft().thePlayer ? (double)var1.angler.getEyeHeight() : 0.0D;
			if(this.renderManager.options.thirdPersonView > 0 || var1.angler != Minecraft.getMinecraft().thePlayer) {
				float var31 = (var1.angler.prevRenderYawOffset + (var1.angler.renderYawOffset - var1.angler.prevRenderYawOffset) * var9) * (float)Math.PI / 180.0F;
				double var32 = (double)MathHelper.sin(var31);
				double var34 = (double)MathHelper.cos(var31);
				var23 = var1.angler.prevPosX + (var1.angler.posX - var1.angler.prevPosX) * (double)var9 - var34 * 0.35D - var32 * 0.85D;
				var25 = var1.angler.prevPosY + var29 + (var1.angler.posY - var1.angler.prevPosY) * (double)var9 - 0.45D;
				var27 = var1.angler.prevPosZ + (var1.angler.posZ - var1.angler.prevPosZ) * (double)var9 - var32 * 0.35D + var34 * 0.85D;
			}

			double var46 = var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var9;
			double var33 = var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var9 + 0.25D;
			double var35 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var9;
			double var37 = (double)((float)(var23 - var46));
			double var39 = (double)((float)(var25 - var33));
			double var41 = (double)((float)(var27 - var35));
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			var12.startDrawing(3);
			var12.setColorOpaque_I(0);
			byte var43 = 16;

			for(int var44 = 0; var44 <= var43; ++var44) {
				float var45 = (float)var44 / (float)var43;
				var12.addVertex(var2 + var37 * (double)var45, var4 + var39 * (double)(var45 * var45 + var45) * 0.5D + 0.25D, var6 + var41 * (double)var45);
			}

			var12.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.doRenderFishHook((EntityFishHook)var1, var2, var4, var6, var8, var9);
	}
}
