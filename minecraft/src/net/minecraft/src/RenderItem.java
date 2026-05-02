package net.minecraft.src;

import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderItem extends Render {
	private RenderBlocks itemRenderBlocks = new RenderBlocks();
	private Random random = new Random();
	public boolean renderWithColor = true;
	public float zLevel = 0.0F;
	public static boolean renderInFrame = false;

	public RenderItem() {
		this.shadowSize = 0.15F;
		this.shadowOpaque = 12.0F / 16.0F;
	}

	public void doRenderItem(EntityItem var1, double var2, double var4, double var6, float var8, float var9) {
		this.random.setSeed(187L);
		ItemStack var10 = var1.getEntityItem();
		if(var10.getItem() != null) {
			GL11.glPushMatrix();
			float var11 = MathHelper.sin(((float)var1.age + var9) / 10.0F + var1.hoverStart) * 0.1F + 0.1F;
			float var12 = (((float)var1.age + var9) / 20.0F + var1.hoverStart) * (180.0F / (float)Math.PI);
			byte var13 = 1;
			if(var1.getEntityItem().stackSize > 1) {
				var13 = 2;
			}

			if(var1.getEntityItem().stackSize > 5) {
				var13 = 3;
			}

			if(var1.getEntityItem().stackSize > 20) {
				var13 = 4;
			}

			if(var1.getEntityItem().stackSize > 40) {
				var13 = 5;
			}

			GL11.glTranslatef((float)var2, (float)var4 + var11, (float)var6);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			int var17;
			float var18;
			float var19;
			float var20;
			if(var10.getItemSpriteNumber() == 0 && Block.blocksList[var10.itemID] != null && RenderBlocks.renderItemIn3d(Block.blocksList[var10.itemID].getRenderType())) {
				Block var22 = Block.blocksList[var10.itemID];
				GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
				if(renderInFrame) {
					GL11.glScalef(1.25F, 1.25F, 1.25F);
					GL11.glTranslatef(0.0F, 0.05F, 0.0F);
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				this.loadTexture("/terrain.png");
				float var24 = 0.25F;
				int var25 = var22.getRenderType();
				if(var25 == 1 || var25 == 19 || var25 == 12 || var25 == 2) {
					var24 = 0.5F;
				}

				GL11.glScalef(var24, var24, var24);

				for(var17 = 0; var17 < var13; ++var17) {
					GL11.glPushMatrix();
					if(var17 > 0) {
						var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
						var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
						var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var24;
						GL11.glTranslatef(var18, var19, var20);
					}

					var18 = 1.0F;
					this.itemRenderBlocks.renderBlockAsItem(var22, var10.getItemDamage(), var18);
					GL11.glPopMatrix();
				}
			} else {
				float var16;
				if(var10.getItem().requiresMultipleRenderPasses()) {
					if(renderInFrame) {
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					} else {
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					this.loadTexture("/gui/items.png");

					for(int var14 = 0; var14 <= 1; ++var14) {
						this.random.setSeed(187L);
						Icon var15 = var10.getItem().getIconFromDamageForRenderPass(var10.getItemDamage(), var14);
						var16 = 1.0F;
						if(this.renderWithColor) {
							var17 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, var14);
							var18 = (float)(var17 >> 16 & 255) / 255.0F;
							var19 = (float)(var17 >> 8 & 255) / 255.0F;
							var20 = (float)(var17 & 255) / 255.0F;
							GL11.glColor4f(var18 * var16, var19 * var16, var20 * var16, 1.0F);
							this.renderDroppedItem(var1, var15, var13, var9, var18 * var16, var19 * var16, var20 * var16);
						} else {
							this.renderDroppedItem(var1, var15, var13, var9, 1.0F, 1.0F, 1.0F);
						}
					}
				} else {
					if(renderInFrame) {
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					} else {
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					Icon var21 = var10.getIconIndex();
					if(var10.getItemSpriteNumber() == 0) {
						this.loadTexture("/terrain.png");
					} else {
						this.loadTexture("/gui/items.png");
					}

					if(this.renderWithColor) {
						int var23 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, 0);
						var16 = (float)(var23 >> 16 & 255) / 255.0F;
						float var26 = (float)(var23 >> 8 & 255) / 255.0F;
						var18 = (float)(var23 & 255) / 255.0F;
						var19 = 1.0F;
						this.renderDroppedItem(var1, var21, var13, var9, var16 * var19, var26 * var19, var18 * var19);
					} else {
						this.renderDroppedItem(var1, var21, var13, var9, 1.0F, 1.0F, 1.0F);
					}
				}
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	private void renderDroppedItem(EntityItem var1, Icon var2, int var3, float var4, float var5, float var6, float var7) {
		Tessellator var8 = Tessellator.instance;
		if(var2 == null) {
			var2 = this.renderManager.renderEngine.getMissingIcon(var1.getEntityItem().getItemSpriteNumber());
		}

		float var9 = var2.getMinU();
		float var10 = var2.getMaxU();
		float var11 = var2.getMinV();
		float var12 = var2.getMaxV();
		float var13 = 1.0F;
		float var14 = 0.5F;
		float var15 = 0.25F;
		float var17;
		if(this.renderManager.options.fancyGraphics) {
			GL11.glPushMatrix();
			if(renderInFrame) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GL11.glRotatef((((float)var1.age + var4) / 20.0F + var1.hoverStart) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			}

			float var16 = 1.0F / 16.0F;
			var17 = 7.0F / 320.0F;
			ItemStack var18 = var1.getEntityItem();
			int var19 = var18.stackSize;
			byte var24;
			if(var19 < 2) {
				var24 = 1;
			} else if(var19 < 16) {
				var24 = 2;
			} else if(var19 < 32) {
				var24 = 3;
			} else {
				var24 = 4;
			}

			GL11.glTranslatef(-var14, -var15, -((var16 + var17) * (float)var24 / 2.0F));

			for(int var20 = 0; var20 < var24; ++var20) {
				GL11.glTranslatef(0.0F, 0.0F, var16 + var17);
				if(var18.getItemSpriteNumber() == 0 && Block.blocksList[var18.itemID] != null) {
					this.loadTexture("/terrain.png");
				} else {
					this.loadTexture("/gui/items.png");
				}

				GL11.glColor4f(var5, var6, var7, 1.0F);
				ItemRenderer.renderItemIn2D(var8, var10, var11, var9, var12, var2.getSheetWidth(), var2.getSheetHeight(), var16);
				if(var18 != null && var18.hasEffect()) {
					GL11.glDepthFunc(GL11.GL_EQUAL);
					GL11.glDisable(GL11.GL_LIGHTING);
					this.renderManager.renderEngine.bindTexture("%blur%/misc/glint.png");
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
					float var21 = 0.76F;
					GL11.glColor4f(0.5F * var21, 0.25F * var21, 0.8F * var21, 1.0F);
					GL11.glMatrixMode(GL11.GL_TEXTURE);
					GL11.glPushMatrix();
					float var22 = 2.0F / 16.0F;
					GL11.glScalef(var22, var22, var22);
					float var23 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
					GL11.glTranslatef(var23, 0.0F, 0.0F);
					GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
					GL11.glScalef(var22, var22, var22);
					var23 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
					GL11.glTranslatef(-var23, 0.0F, 0.0F);
					GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
					GL11.glPopMatrix();
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDepthFunc(GL11.GL_LEQUAL);
				}
			}

			GL11.glPopMatrix();
		} else {
			for(int var25 = 0; var25 < var3; ++var25) {
				GL11.glPushMatrix();
				if(var25 > 0) {
					var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var27 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					GL11.glTranslatef(var17, var26, var27);
				}

				if(!renderInFrame) {
					GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				}

				GL11.glColor4f(var5, var6, var7, 1.0F);
				var8.startDrawingQuads();
				var8.setNormal(0.0F, 1.0F, 0.0F);
				var8.addVertexWithUV((double)(0.0F - var14), (double)(0.0F - var15), 0.0D, (double)var9, (double)var12);
				var8.addVertexWithUV((double)(var13 - var14), (double)(0.0F - var15), 0.0D, (double)var10, (double)var12);
				var8.addVertexWithUV((double)(var13 - var14), (double)(1.0F - var15), 0.0D, (double)var10, (double)var11);
				var8.addVertexWithUV((double)(0.0F - var14), (double)(1.0F - var15), 0.0D, (double)var9, (double)var11);
				var8.draw();
				GL11.glPopMatrix();
			}
		}

	}

	public void renderItemIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
		int var6 = var3.itemID;
		int var7 = var3.getItemDamage();
		Icon var8 = var3.getIconIndex();
		float var12;
		float var13;
		float var18;
		if(var3.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.blocksList[var6].getRenderType())) {
			var2.bindTexture("/terrain.png");
			Block var15 = Block.blocksList[var6];
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(var4 - 2), (float)(var5 + 3), -3.0F + this.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			int var17 = Item.itemsList[var6].getColorFromItemStack(var3, 0);
			var18 = (float)(var17 >> 16 & 255) / 255.0F;
			var12 = (float)(var17 >> 8 & 255) / 255.0F;
			var13 = (float)(var17 & 255) / 255.0F;
			if(this.renderWithColor) {
				GL11.glColor4f(var18, var12, var13, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			this.itemRenderBlocks.useInventoryTint = this.renderWithColor;
			this.itemRenderBlocks.renderBlockAsItem(var15, var7, 1.0F);
			this.itemRenderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		} else {
			int var9;
			if(Item.itemsList[var6].requiresMultipleRenderPasses()) {
				GL11.glDisable(GL11.GL_LIGHTING);
				var2.bindTexture("/gui/items.png");

				for(var9 = 0; var9 <= 1; ++var9) {
					Icon var10 = Item.itemsList[var6].getIconFromDamageForRenderPass(var7, var9);
					int var11 = Item.itemsList[var6].getColorFromItemStack(var3, var9);
					var12 = (float)(var11 >> 16 & 255) / 255.0F;
					var13 = (float)(var11 >> 8 & 255) / 255.0F;
					float var14 = (float)(var11 & 255) / 255.0F;
					if(this.renderWithColor) {
						GL11.glColor4f(var12, var13, var14, 1.0F);
					}

					this.renderIcon(var4, var5, var10, 16, 16);
				}

				GL11.glEnable(GL11.GL_LIGHTING);
			} else {
				GL11.glDisable(GL11.GL_LIGHTING);
				if(var3.getItemSpriteNumber() == 0) {
					var2.bindTexture("/terrain.png");
				} else {
					var2.bindTexture("/gui/items.png");
				}

				if(var8 == null) {
					var8 = var2.getMissingIcon(var3.getItemSpriteNumber());
				}

				var9 = Item.itemsList[var6].getColorFromItemStack(var3, 0);
				float var16 = (float)(var9 >> 16 & 255) / 255.0F;
				var18 = (float)(var9 >> 8 & 255) / 255.0F;
				var12 = (float)(var9 & 255) / 255.0F;
				if(this.renderWithColor) {
					GL11.glColor4f(var16, var18, var12, 1.0F);
				}

				this.renderIcon(var4, var5, var8, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void renderItemAndEffectIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
		if(var3 != null) {
			this.renderItemIntoGUI(var1, var2, var3, var4, var5);
			if(var3.hasEffect()) {
				GL11.glDepthFunc(GL11.GL_GREATER);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				var2.bindTexture("%blur%/misc/glint.png");
				this.zLevel -= 50.0F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				this.renderGlint(var4 * 431278612 + var5 * 32178161, var4 - 2, var5 - 2, 20, 20);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				this.zLevel += 50.0F;
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

		}
	}

	private void renderGlint(int var1, int var2, int var3, int var4, int var5) {
		for(int var6 = 0; var6 < 2; ++var6) {
			if(var6 == 0) {
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			if(var6 == 1) {
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			float var9 = (float)(Minecraft.getSystemTime() % (long)(3000 + var6 * 1873)) / (3000.0F + (float)(var6 * 1873)) * 256.0F;
			float var10 = 0.0F;
			Tessellator var11 = Tessellator.instance;
			float var12 = 4.0F;
			if(var6 == 1) {
				var12 = -1.0F;
			}

			var11.startDrawingQuads();
			var11.addVertexWithUV((double)(var2 + 0), (double)(var3 + var5), (double)this.zLevel, (double)((var9 + (float)var5 * var12) * var7), (double)((var10 + (float)var5) * var8));
			var11.addVertexWithUV((double)(var2 + var4), (double)(var3 + var5), (double)this.zLevel, (double)((var9 + (float)var4 + (float)var5 * var12) * var7), (double)((var10 + (float)var5) * var8));
			var11.addVertexWithUV((double)(var2 + var4), (double)(var3 + 0), (double)this.zLevel, (double)((var9 + (float)var4) * var7), (double)((var10 + 0.0F) * var8));
			var11.addVertexWithUV((double)(var2 + 0), (double)(var3 + 0), (double)this.zLevel, (double)((var9 + 0.0F) * var7), (double)((var10 + 0.0F) * var8));
			var11.draw();
		}

	}

	public void renderItemOverlayIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
		this.renderItemOverlayIntoGUI(var1, var2, var3, var4, var5, (String)null);
	}

	public void renderItemOverlayIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5, String var6) {
		if(var3 != null) {
			if(var3.stackSize > 1 || var6 != null) {
				String var7 = var6 == null ? String.valueOf(var3.stackSize) : var6;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				var1.drawStringWithShadow(var7, var4 + 19 - 2 - var1.getStringWidth(var7), var5 + 6 + 3, 16777215);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			if(var3.isItemDamaged()) {
				int var12 = (int)Math.round(13.0D - (double)var3.getItemDamageForDisplay() * 13.0D / (double)var3.getMaxDamage());
				int var8 = (int)Math.round(255.0D - (double)var3.getItemDamageForDisplay() * 255.0D / (double)var3.getMaxDamage());
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Tessellator var9 = Tessellator.instance;
				int var10 = 255 - var8 << 16 | var8 << 8;
				int var11 = (255 - var8) / 4 << 16 | 16128;
				this.renderQuad(var9, var4 + 2, var5 + 13, 13, 2, 0);
				this.renderQuad(var9, var4 + 2, var5 + 13, 12, 1, var11);
				this.renderQuad(var9, var4 + 2, var5 + 13, var12, 1, var10);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

		}
	}

	private void renderQuad(Tessellator var1, int var2, int var3, int var4, int var5, int var6) {
		var1.startDrawingQuads();
		var1.setColorOpaque_I(var6);
		var1.addVertex((double)(var2 + 0), (double)(var3 + 0), 0.0D);
		var1.addVertex((double)(var2 + 0), (double)(var3 + var5), 0.0D);
		var1.addVertex((double)(var2 + var4), (double)(var3 + var5), 0.0D);
		var1.addVertex((double)(var2 + var4), (double)(var3 + 0), 0.0D);
		var1.draw();
	}

	public void renderIcon(int var1, int var2, Icon var3, int var4, int var5) {
		Tessellator var6 = Tessellator.instance;
		var6.startDrawingQuads();
		var6.addVertexWithUV((double)(var1 + 0), (double)(var2 + var5), (double)this.zLevel, (double)var3.getMinU(), (double)var3.getMaxV());
		var6.addVertexWithUV((double)(var1 + var4), (double)(var2 + var5), (double)this.zLevel, (double)var3.getMaxU(), (double)var3.getMaxV());
		var6.addVertexWithUV((double)(var1 + var4), (double)(var2 + 0), (double)this.zLevel, (double)var3.getMaxU(), (double)var3.getMinV());
		var6.addVertexWithUV((double)(var1 + 0), (double)(var2 + 0), (double)this.zLevel, (double)var3.getMinU(), (double)var3.getMinV());
		var6.draw();
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.doRenderItem((EntityItem)var1, var2, var4, var6, var8, var9);
	}
}
