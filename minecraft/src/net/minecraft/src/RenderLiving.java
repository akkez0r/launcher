package net.minecraft.src;

import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderLiving extends Render {
	protected ModelBase mainModel;
	protected ModelBase renderPassModel;

	public RenderLiving(ModelBase var1, float var2) {
		this.mainModel = var1;
		this.shadowSize = var2;
	}

	public void setRenderPassModel(ModelBase var1) {
		this.renderPassModel = var1;
	}

	private float interpolateRotation(float var1, float var2, float var3) {
		float var4;
		for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		return var1 + var3 * var4;
	}

	public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.mainModel.onGround = this.renderSwingProgress(var1, var9);
		if(this.renderPassModel != null) {
			this.renderPassModel.onGround = this.mainModel.onGround;
		}

		this.mainModel.isRiding = var1.isRiding();
		if(this.renderPassModel != null) {
			this.renderPassModel.isRiding = this.mainModel.isRiding;
		}

		this.mainModel.isChild = var1.isChild();
		if(this.renderPassModel != null) {
			this.renderPassModel.isChild = this.mainModel.isChild;
		}

		try {
			float var10 = this.interpolateRotation(var1.prevRenderYawOffset, var1.renderYawOffset, var9);
			float var11 = this.interpolateRotation(var1.prevRotationYawHead, var1.rotationYawHead, var9);
			float var12 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9;
			this.renderLivingAt(var1, var2, var4, var6);
			float var13 = this.handleRotationFloat(var1, var9);
			this.rotateCorpse(var1, var13, var10, var9);
			float var14 = 1.0F / 16.0F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(var1, var9);
			GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
			float var15 = var1.prevLimbYaw + (var1.limbYaw - var1.prevLimbYaw) * var9;
			float var16 = var1.limbSwing - var1.limbYaw * (1.0F - var9);
			if(var1.isChild()) {
				var16 *= 3.0F;
			}

			if(var15 > 1.0F) {
				var15 = 1.0F;
			}

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			this.mainModel.setLivingAnimations(var1, var16, var15, var9);
			this.renderModel(var1, var16, var15, var13, var11 - var10, var12, var14);

			int var18;
			float var19;
			float var20;
			float var22;
			for(int var17 = 0; var17 < 4; ++var17) {
				var18 = this.shouldRenderPass(var1, var17, var9);
				if(var18 > 0) {
					this.renderPassModel.setLivingAnimations(var1, var16, var15, var9);
					this.renderPassModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);
					if((var18 & 240) == 16) {
						this.func_82408_c(var1, var17, var9);
						this.renderPassModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);
					}

					if((var18 & 15) == 15) {
						var19 = (float)var1.ticksExisted + var9;
						this.loadTexture("%blur%/misc/glint.png");
						GL11.glEnable(GL11.GL_BLEND);
						var20 = 0.5F;
						GL11.glColor4f(var20, var20, var20, 1.0F);
						GL11.glDepthFunc(GL11.GL_EQUAL);
						GL11.glDepthMask(false);

						for(int var21 = 0; var21 < 2; ++var21) {
							GL11.glDisable(GL11.GL_LIGHTING);
							var22 = 0.76F;
							GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
							GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
							GL11.glMatrixMode(GL11.GL_TEXTURE);
							GL11.glLoadIdentity();
							float var23 = var19 * (0.001F + (float)var21 * 0.003F) * 20.0F;
							float var24 = 1.0F / 3.0F;
							GL11.glScalef(var24, var24, var24);
							GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
							GL11.glTranslatef(0.0F, var23, 0.0F);
							GL11.glMatrixMode(GL11.GL_MODELVIEW);
							this.renderPassModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);
						}

						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glMatrixMode(GL11.GL_TEXTURE);
						GL11.glDepthMask(true);
						GL11.glLoadIdentity();
						GL11.glMatrixMode(GL11.GL_MODELVIEW);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glDepthFunc(GL11.GL_LEQUAL);
					}

					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
				}
			}

			GL11.glDepthMask(true);
			this.renderEquippedItems(var1, var9);
			float var26 = var1.getBrightness(var9);
			var18 = this.getColorMultiplier(var1, var26, var9);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
			if((var18 >> 24 & 255) > 0 || var1.hurtTime > 0 || var1.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				if(var1.hurtTime > 0 || var1.deathTime > 0) {
					GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
					this.mainModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);

					for(int var27 = 0; var27 < 4; ++var27) {
						if(this.inheritRenderPass(var1, var27, var9) >= 0) {
							GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);
						}
					}
				}

				if((var18 >> 24 & 255) > 0) {
					var19 = (float)(var18 >> 16 & 255) / 255.0F;
					var20 = (float)(var18 >> 8 & 255) / 255.0F;
					float var28 = (float)(var18 & 255) / 255.0F;
					var22 = (float)(var18 >> 24 & 255) / 255.0F;
					GL11.glColor4f(var19, var20, var28, var22);
					this.mainModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);

					for(int var29 = 0; var29 < 4; ++var29) {
						if(this.inheritRenderPass(var1, var29, var9) >= 0) {
							GL11.glColor4f(var19, var20, var28, var22);
							this.renderPassModel.render(var1, var16, var15, var13, var11 - var10, var12, var14);
						}
					}
				}

				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		} catch (Exception var25) {
			var25.printStackTrace();
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		this.passSpecialRender(var1, var2, var4, var6);
	}

	protected void renderModel(EntityLiving var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		this.func_98190_a(var1);
		if(!var1.isInvisible()) {
			this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
		} else if(!var1.func_98034_c(Minecraft.getMinecraft().thePlayer)) {
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
			this.mainModel.render(var1, var2, var3, var4, var5, var6, var7);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glPopMatrix();
			GL11.glDepthMask(true);
		} else {
			this.mainModel.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
		}

	}

	protected void func_98190_a(EntityLiving var1) {
		this.loadTexture(var1.getTexture());
	}

	protected void renderLivingAt(EntityLiving var1, double var2, double var4, double var6) {
		GL11.glTranslatef((float)var2, (float)var4, (float)var6);
	}

	protected void rotateCorpse(EntityLiving var1, float var2, float var3, float var4) {
		GL11.glRotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
		if(var1.deathTime > 0) {
			float var5 = ((float)var1.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
			var5 = MathHelper.sqrt_float(var5);
			if(var5 > 1.0F) {
				var5 = 1.0F;
			}

			GL11.glRotatef(var5 * this.getDeathMaxRotation(var1), 0.0F, 0.0F, 1.0F);
		}

	}

	protected float renderSwingProgress(EntityLiving var1, float var2) {
		return var1.getSwingProgress(var2);
	}

	protected float handleRotationFloat(EntityLiving var1, float var2) {
		return (float)var1.ticksExisted + var2;
	}

	protected void renderEquippedItems(EntityLiving var1, float var2) {
	}

	protected void renderArrowsStuckInEntity(EntityLiving var1, float var2) {
		int var3 = var1.getArrowCountInEntity();
		if(var3 > 0) {
			EntityArrow var4 = new EntityArrow(var1.worldObj, var1.posX, var1.posY, var1.posZ);
			Random var5 = new Random((long)var1.entityId);
			RenderHelper.disableStandardItemLighting();

			for(int var6 = 0; var6 < var3; ++var6) {
				GL11.glPushMatrix();
				ModelRenderer var7 = this.mainModel.getRandomModelBox(var5);
				ModelBox var8 = (ModelBox)var7.cubeList.get(var5.nextInt(var7.cubeList.size()));
				var7.postRender(1.0F / 16.0F);
				float var9 = var5.nextFloat();
				float var10 = var5.nextFloat();
				float var11 = var5.nextFloat();
				float var12 = (var8.posX1 + (var8.posX2 - var8.posX1) * var9) / 16.0F;
				float var13 = (var8.posY1 + (var8.posY2 - var8.posY1) * var10) / 16.0F;
				float var14 = (var8.posZ1 + (var8.posZ2 - var8.posZ1) * var11) / 16.0F;
				GL11.glTranslatef(var12, var13, var14);
				var9 = var9 * 2.0F - 1.0F;
				var10 = var10 * 2.0F - 1.0F;
				var11 = var11 * 2.0F - 1.0F;
				var9 *= -1.0F;
				var10 *= -1.0F;
				var11 *= -1.0F;
				float var15 = MathHelper.sqrt_float(var9 * var9 + var11 * var11);
				var4.prevRotationYaw = var4.rotationYaw = (float)(Math.atan2((double)var9, (double)var11) * 180.0D / (double)((float)Math.PI));
				var4.prevRotationPitch = var4.rotationPitch = (float)(Math.atan2((double)var10, (double)var15) * 180.0D / (double)((float)Math.PI));
				double var16 = 0.0D;
				double var18 = 0.0D;
				double var20 = 0.0D;
				float var22 = 0.0F;
				this.renderManager.renderEntityWithPosYaw(var4, var16, var18, var20, var22, var2);
				GL11.glPopMatrix();
			}

			RenderHelper.enableStandardItemLighting();
		}

	}

	protected int inheritRenderPass(EntityLiving var1, int var2, float var3) {
		return this.shouldRenderPass(var1, var2, var3);
	}

	protected int shouldRenderPass(EntityLiving var1, int var2, float var3) {
		return -1;
	}

	protected void func_82408_c(EntityLiving var1, int var2, float var3) {
	}

	protected float getDeathMaxRotation(EntityLiving var1) {
		return 90.0F;
	}

	protected int getColorMultiplier(EntityLiving var1, float var2, float var3) {
		return 0;
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
	}

	protected void passSpecialRender(EntityLiving var1, double var2, double var4, double var6) {
		if(Minecraft.isGuiEnabled() && var1 != this.renderManager.livingPlayer && !var1.func_98034_c(Minecraft.getMinecraft().thePlayer) && (var1.func_94059_bO() || var1.func_94056_bM() && var1 == this.renderManager.field_96451_i)) {
			float var8 = 1.6F;
			float var9 = (float)(1.0D / 60.0D) * var8;
			double var10 = var1.getDistanceSqToEntity(this.renderManager.livingPlayer);
			float var12 = var1.isSneaking() ? 32.0F : 64.0F;
			if(var10 < (double)(var12 * var12)) {
				String var13 = var1.getTranslatedEntityName();
				if(var1.isSneaking()) {
					FontRenderer var14 = this.getFontRendererFromRenderManager();
					GL11.glPushMatrix();
					GL11.glTranslatef((float)var2 + 0.0F, (float)var4 + var1.height + 0.5F, (float)var6);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-var9, -var9, var9);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					Tessellator var15 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					var15.startDrawingQuads();
					int var16 = var14.getStringWidth(var13) / 2;
					var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
					var15.addVertex((double)(-var16 - 1), -1.0D, 0.0D);
					var15.addVertex((double)(-var16 - 1), 8.0D, 0.0D);
					var15.addVertex((double)(var16 + 1), 8.0D, 0.0D);
					var15.addVertex((double)(var16 + 1), -1.0D, 0.0D);
					var15.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(true);
					var14.drawString(var13, -var14.getStringWidth(var13) / 2, 0, 553648127);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				} else {
					this.func_96449_a(var1, var2, var4, var6, var13, var9, var10);
				}
			}
		}

	}

	protected void func_96449_a(EntityLiving var1, double var2, double var4, double var6, String var8, float var9, double var10) {
		if(var1.isPlayerSleeping()) {
			this.renderLivingLabel(var1, var8, var2, var4 - 1.5D, var6, 64);
		} else {
			this.renderLivingLabel(var1, var8, var2, var4, var6, 64);
		}

	}

	protected void renderLivingLabel(EntityLiving var1, String var2, double var3, double var5, double var7, int var9) {
		double var10 = var1.getDistanceSqToEntity(this.renderManager.livingPlayer);
		if(var10 <= (double)(var9 * var9)) {
			FontRenderer var12 = this.getFontRendererFromRenderManager();
			float var13 = 1.6F;
			float var14 = (float)(1.0D / 60.0D) * var13;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)var3 + 0.0F, (float)var5 + var1.height + 0.5F, (float)var7);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-var14, -var14, var14);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator var15 = Tessellator.instance;
			byte var16 = 0;
			if(var2.equals("deadmau5")) {
				var16 = -10;
			}

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			var15.startDrawingQuads();
			int var17 = var12.getStringWidth(var2) / 2;
			var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			var15.addVertex((double)(-var17 - 1), (double)(-1 + var16), 0.0D);
			var15.addVertex((double)(-var17 - 1), (double)(8 + var16), 0.0D);
			var15.addVertex((double)(var17 + 1), (double)(8 + var16), 0.0D);
			var15.addVertex((double)(var17 + 1), (double)(-1 + var16), 0.0D);
			var15.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			var12.drawString(var2, -var12.getStringWidth(var2) / 2, var16, 553648127);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			var12.drawString(var2, -var12.getStringWidth(var2) / 2, var16, -1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.doRenderLiving((EntityLiving)var1, var2, var4, var6, var8, var9);
	}
}
