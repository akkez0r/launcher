package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render {
	private final RenderBlocks renderBlocksInstance = new RenderBlocks();
	private Icon field_94147_f;

	public void updateIcons(IconRegister var1) {
		this.field_94147_f = var1.registerIcon("itemframe_back");
	}

	public void func_82404_a(EntityItemFrame var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		float var10 = (float)(var1.posX - var2) - 0.5F;
		float var11 = (float)(var1.posY - var4) - 0.5F;
		float var12 = (float)(var1.posZ - var6) - 0.5F;
		int var13 = var1.xPosition + Direction.offsetX[var1.hangingDirection];
		int var14 = var1.yPosition;
		int var15 = var1.zPosition + Direction.offsetZ[var1.hangingDirection];
		GL11.glTranslatef((float)var13 - var10, (float)var14 - var11, (float)var15 - var12);
		this.renderFrameItemAsBlock(var1);
		this.func_82402_b(var1);
		GL11.glPopMatrix();
	}

	private void renderFrameItemAsBlock(EntityItemFrame var1) {
		GL11.glPushMatrix();
		this.renderManager.renderEngine.bindTexture("/terrain.png");
		GL11.glRotatef(var1.rotationYaw, 0.0F, 1.0F, 0.0F);
		Block var2 = Block.planks;
		float var3 = 1.0F / 16.0F;
		float var4 = 12.0F / 16.0F;
		float var5 = var4 / 2.0F;
		GL11.glPushMatrix();
		this.renderBlocksInstance.overrideBlockBounds(0.0D, (double)(0.5F - var5 + 1.0F / 16.0F), (double)(0.5F - var5 + 1.0F / 16.0F), (double)(var3 * 0.5F), (double)(0.5F + var5 - 1.0F / 16.0F), (double)(0.5F + var5 - 1.0F / 16.0F));
		this.renderBlocksInstance.setOverrideBlockTexture(this.field_94147_f);
		this.renderBlocksInstance.renderBlockAsItem(var2, 0, 1.0F);
		this.renderBlocksInstance.clearOverrideBlockTexture();
		this.renderBlocksInstance.unlockBlockBounds();
		GL11.glPopMatrix();
		this.renderBlocksInstance.setOverrideBlockTexture(Block.planks.getIcon(1, 2));
		GL11.glPushMatrix();
		this.renderBlocksInstance.overrideBlockBounds(0.0D, (double)(0.5F - var5), (double)(0.5F - var5), (double)(var3 + 1.0E-4F), (double)(var3 + 0.5F - var5), (double)(0.5F + var5));
		this.renderBlocksInstance.renderBlockAsItem(var2, 0, 1.0F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		this.renderBlocksInstance.overrideBlockBounds(0.0D, (double)(0.5F + var5 - var3), (double)(0.5F - var5), (double)(var3 + 1.0E-4F), (double)(0.5F + var5), (double)(0.5F + var5));
		this.renderBlocksInstance.renderBlockAsItem(var2, 0, 1.0F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		this.renderBlocksInstance.overrideBlockBounds(0.0D, (double)(0.5F - var5), (double)(0.5F - var5), (double)var3, (double)(0.5F + var5), (double)(var3 + 0.5F - var5));
		this.renderBlocksInstance.renderBlockAsItem(var2, 0, 1.0F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		this.renderBlocksInstance.overrideBlockBounds(0.0D, (double)(0.5F - var5), (double)(0.5F + var5 - var3), (double)var3, (double)(0.5F + var5), (double)(0.5F + var5));
		this.renderBlocksInstance.renderBlockAsItem(var2, 0, 1.0F);
		GL11.glPopMatrix();
		this.renderBlocksInstance.unlockBlockBounds();
		this.renderBlocksInstance.clearOverrideBlockTexture();
		GL11.glPopMatrix();
	}

	private void func_82402_b(EntityItemFrame var1) {
		ItemStack var2 = var1.getDisplayedItem();
		if(var2 != null) {
			EntityItem var3 = new EntityItem(var1.worldObj, 0.0D, 0.0D, 0.0D, var2);
			var3.getEntityItem().stackSize = 1;
			var3.hoverStart = 0.0F;
			GL11.glPushMatrix();
			GL11.glTranslatef(-0.453125F * (float)Direction.offsetX[var1.hangingDirection], -0.18F, -0.453125F * (float)Direction.offsetZ[var1.hangingDirection]);
			GL11.glRotatef(180.0F + var1.rotationYaw, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef((float)(-90 * var1.getRotation()), 0.0F, 0.0F, 1.0F);
			switch(var1.getRotation()) {
			case 1:
				GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
				break;
			case 2:
				GL11.glTranslatef(0.0F, -0.32F, 0.0F);
				break;
			case 3:
				GL11.glTranslatef(0.16F, -0.16F, 0.0F);
			}

			if(var3.getEntityItem().getItem() == Item.map) {
				this.renderManager.renderEngine.bindTexture("/misc/mapbg.png");
				Tessellator var4 = Tessellator.instance;
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(0.00390625F, 0.00390625F, 0.00390625F);
				GL11.glTranslatef(-65.0F, -107.0F, -3.0F);
				GL11.glNormal3f(0.0F, 0.0F, -1.0F);
				var4.startDrawingQuads();
				byte var5 = 7;
				var4.addVertexWithUV((double)(0 - var5), (double)(128 + var5), 0.0D, 0.0D, 1.0D);
				var4.addVertexWithUV((double)(128 + var5), (double)(128 + var5), 0.0D, 1.0D, 1.0D);
				var4.addVertexWithUV((double)(128 + var5), (double)(0 - var5), 0.0D, 1.0D, 0.0D);
				var4.addVertexWithUV((double)(0 - var5), (double)(0 - var5), 0.0D, 0.0D, 0.0D);
				var4.draw();
				MapData var6 = Item.map.getMapData(var3.getEntityItem(), var1.worldObj);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				if(var6 != null) {
					this.renderManager.itemRenderer.mapItemRenderer.renderMap((EntityPlayer)null, this.renderManager.renderEngine, var6);
				}
			} else {
				TextureCompass var9;
				if(var3.getEntityItem().getItem() == Item.compass) {
					var9 = TextureCompass.compassTexture;
					double var10 = var9.currentAngle;
					double var7 = var9.angleDelta;
					var9.currentAngle = 0.0D;
					var9.angleDelta = 0.0D;
					var9.updateCompass(var1.worldObj, var1.posX, var1.posZ, (double)MathHelper.wrapAngleTo180_float((float)(180 + var1.hangingDirection * 90)), false, true);
					var9.currentAngle = var10;
					var9.angleDelta = var7;
				}

				RenderItem.renderInFrame = true;
				RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				RenderItem.renderInFrame = false;
				if(var3.getEntityItem().getItem() == Item.compass) {
					var9 = TextureCompass.compassTexture;
					var9.updateAnimation();
				}
			}

			GL11.glPopMatrix();
		}
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.func_82404_a((EntityItemFrame)var1, var2, var4, var6, var8, var9);
	}
}
