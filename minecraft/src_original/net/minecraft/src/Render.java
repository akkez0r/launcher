package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public abstract class Render {
	protected RenderManager renderManager;
	private ModelBase modelBase = new ModelBiped();
	protected RenderBlocks renderBlocks = new RenderBlocks();
	protected float shadowSize = 0.0F;
	protected float shadowOpaque = 1.0F;

	public abstract void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9);

	protected void loadTexture(String var1) {
		this.renderManager.renderEngine.bindTexture(var1);
	}

	protected boolean loadDownloadableImageTexture(String var1, String var2) {
		RenderEngine var3 = this.renderManager.renderEngine;
		int var4 = var3.getTextureForDownloadableImage(var1, var2);
		if(var4 >= 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var4);
			var3.resetBoundTexture();
			return true;
		} else {
			return false;
		}
	}

	private void renderEntityOnFire(Entity var1, double var2, double var4, double var6, float var8) {
		GL11.glDisable(GL11.GL_LIGHTING);
		Icon var9 = Block.fire.func_94438_c(0);
		Icon var10 = Block.fire.func_94438_c(1);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var2, (float)var4, (float)var6);
		float var11 = var1.width * 1.4F;
		GL11.glScalef(var11, var11, var11);
		this.loadTexture("/terrain.png");
		Tessellator var12 = Tessellator.instance;
		float var13 = 0.5F;
		float var14 = 0.0F;
		float var15 = var1.height / var11;
		float var16 = (float)(var1.posY - var1.boundingBox.minY);
		GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)var15) * 0.02F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var17 = 0.0F;
		int var18 = 0;
		var12.startDrawingQuads();

		while(var15 > 0.0F) {
			Icon var19;
			if(var18 % 2 == 0) {
				var19 = var9;
			} else {
				var19 = var10;
			}

			float var20 = var19.getMinU();
			float var21 = var19.getMinV();
			float var22 = var19.getMaxU();
			float var23 = var19.getMaxV();
			if(var18 / 2 % 2 == 0) {
				float var24 = var22;
				var22 = var20;
				var20 = var24;
			}

			var12.addVertexWithUV((double)(var13 - var14), (double)(0.0F - var16), (double)var17, (double)var22, (double)var23);
			var12.addVertexWithUV((double)(-var13 - var14), (double)(0.0F - var16), (double)var17, (double)var20, (double)var23);
			var12.addVertexWithUV((double)(-var13 - var14), (double)(1.4F - var16), (double)var17, (double)var20, (double)var21);
			var12.addVertexWithUV((double)(var13 - var14), (double)(1.4F - var16), (double)var17, (double)var22, (double)var21);
			var15 -= 0.45F;
			var16 -= 0.45F;
			var13 *= 0.9F;
			var17 += 0.03F;
			++var18;
		}

		var12.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private void renderShadow(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this.renderManager.renderEngine.bindTexture("%clamp%/misc/shadow.png");
		World var10 = this.getWorldFromRenderManager();
		GL11.glDepthMask(false);
		float var11 = this.shadowSize;
		if(var1 instanceof EntityLiving) {
			EntityLiving var12 = (EntityLiving)var1;
			var11 *= var12.getRenderSizeModifier();
			if(var12.isChild()) {
				var11 *= 0.5F;
			}
		}

		double var35 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var9;
		double var14 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var9 + (double)var1.getShadowSize();
		double var16 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var9;
		int var18 = MathHelper.floor_double(var35 - (double)var11);
		int var19 = MathHelper.floor_double(var35 + (double)var11);
		int var20 = MathHelper.floor_double(var14 - (double)var11);
		int var21 = MathHelper.floor_double(var14);
		int var22 = MathHelper.floor_double(var16 - (double)var11);
		int var23 = MathHelper.floor_double(var16 + (double)var11);
		double var24 = var2 - var35;
		double var26 = var4 - var14;
		double var28 = var6 - var16;
		Tessellator var30 = Tessellator.instance;
		var30.startDrawingQuads();

		for(int var31 = var18; var31 <= var19; ++var31) {
			for(int var32 = var20; var32 <= var21; ++var32) {
				for(int var33 = var22; var33 <= var23; ++var33) {
					int var34 = var10.getBlockId(var31, var32 - 1, var33);
					if(var34 > 0 && var10.getBlockLightValue(var31, var32, var33) > 3) {
						this.renderShadowOnBlock(Block.blocksList[var34], var2, var4 + (double)var1.getShadowSize(), var6, var31, var32, var33, var8, var11, var24, var26 + (double)var1.getShadowSize(), var28);
					}
				}
			}
		}

		var30.draw();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
	}

	private World getWorldFromRenderManager() {
		return this.renderManager.worldObj;
	}

	private void renderShadowOnBlock(Block var1, double var2, double var4, double var6, int var8, int var9, int var10, float var11, float var12, double var13, double var15, double var17) {
		Tessellator var19 = Tessellator.instance;
		if(var1.renderAsNormalBlock()) {
			double var20 = ((double)var11 - (var4 - ((double)var9 + var15)) / 2.0D) * 0.5D * (double)this.getWorldFromRenderManager().getLightBrightness(var8, var9, var10);
			if(var20 >= 0.0D) {
				if(var20 > 1.0D) {
					var20 = 1.0D;
				}

				var19.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var20);
				double var22 = (double)var8 + var1.getBlockBoundsMinX() + var13;
				double var24 = (double)var8 + var1.getBlockBoundsMaxX() + var13;
				double var26 = (double)var9 + var1.getBlockBoundsMinY() + var15 + 1.0D / 64.0D;
				double var28 = (double)var10 + var1.getBlockBoundsMinZ() + var17;
				double var30 = (double)var10 + var1.getBlockBoundsMaxZ() + var17;
				float var32 = (float)((var2 - var22) / 2.0D / (double)var12 + 0.5D);
				float var33 = (float)((var2 - var24) / 2.0D / (double)var12 + 0.5D);
				float var34 = (float)((var6 - var28) / 2.0D / (double)var12 + 0.5D);
				float var35 = (float)((var6 - var30) / 2.0D / (double)var12 + 0.5D);
				var19.addVertexWithUV(var22, var26, var28, (double)var32, (double)var34);
				var19.addVertexWithUV(var22, var26, var30, (double)var32, (double)var35);
				var19.addVertexWithUV(var24, var26, var30, (double)var33, (double)var35);
				var19.addVertexWithUV(var24, var26, var28, (double)var33, (double)var34);
			}
		}
	}

	public static void renderOffsetAABB(AxisAlignedBB var0, double var1, double var3, double var5) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator var7 = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		var7.startDrawingQuads();
		var7.setTranslation(var1, var3, var5);
		var7.setNormal(0.0F, 0.0F, -1.0F);
		var7.addVertex(var0.minX, var0.maxY, var0.minZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var7.addVertex(var0.maxX, var0.minY, var0.minZ);
		var7.addVertex(var0.minX, var0.minY, var0.minZ);
		var7.setNormal(0.0F, 0.0F, 1.0F);
		var7.addVertex(var0.minX, var0.minY, var0.maxZ);
		var7.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var7.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var7.setNormal(0.0F, -1.0F, 0.0F);
		var7.addVertex(var0.minX, var0.minY, var0.minZ);
		var7.addVertex(var0.maxX, var0.minY, var0.minZ);
		var7.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var7.addVertex(var0.minX, var0.minY, var0.maxZ);
		var7.setNormal(0.0F, 1.0F, 0.0F);
		var7.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var7.addVertex(var0.minX, var0.maxY, var0.minZ);
		var7.setNormal(-1.0F, 0.0F, 0.0F);
		var7.addVertex(var0.minX, var0.minY, var0.maxZ);
		var7.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var7.addVertex(var0.minX, var0.maxY, var0.minZ);
		var7.addVertex(var0.minX, var0.minY, var0.minZ);
		var7.setNormal(1.0F, 0.0F, 0.0F);
		var7.addVertex(var0.maxX, var0.minY, var0.minZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var7.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var7.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var7.setTranslation(0.0D, 0.0D, 0.0D);
		var7.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void renderAABB(AxisAlignedBB var0) {
		Tessellator var1 = Tessellator.instance;
		var1.startDrawingQuads();
		var1.addVertex(var0.minX, var0.maxY, var0.minZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var1.addVertex(var0.maxX, var0.minY, var0.minZ);
		var1.addVertex(var0.minX, var0.minY, var0.minZ);
		var1.addVertex(var0.minX, var0.minY, var0.maxZ);
		var1.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.minX, var0.minY, var0.minZ);
		var1.addVertex(var0.maxX, var0.minY, var0.minZ);
		var1.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var1.addVertex(var0.minX, var0.minY, var0.maxZ);
		var1.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var1.addVertex(var0.minX, var0.maxY, var0.minZ);
		var1.addVertex(var0.minX, var0.minY, var0.maxZ);
		var1.addVertex(var0.minX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.minX, var0.maxY, var0.minZ);
		var1.addVertex(var0.minX, var0.minY, var0.minZ);
		var1.addVertex(var0.maxX, var0.minY, var0.minZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.minZ);
		var1.addVertex(var0.maxX, var0.maxY, var0.maxZ);
		var1.addVertex(var0.maxX, var0.minY, var0.maxZ);
		var1.draw();
	}

	public void setRenderManager(RenderManager var1) {
		this.renderManager = var1;
	}

	public void doRenderShadowAndFire(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		if(this.renderManager.options.fancyGraphics && this.shadowSize > 0.0F && !var1.isInvisible()) {
			double var10 = this.renderManager.getDistanceToCamera(var1.posX, var1.posY, var1.posZ);
			float var12 = (float)((1.0D - var10 / 256.0D) * (double)this.shadowOpaque);
			if(var12 > 0.0F) {
				this.renderShadow(var1, var2, var4, var6, var12, var9);
			}
		}

		if(var1.canRenderOnFire()) {
			this.renderEntityOnFire(var1, var2, var4, var6, var9);
		}

	}

	public FontRenderer getFontRendererFromRenderManager() {
		return this.renderManager.getFontRenderer();
	}

	public void updateIcons(IconRegister var1) {
	}
}
