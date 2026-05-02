package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderSnowball extends Render {
	private Item field_94151_a;
	private int field_94150_f;

	public RenderSnowball(Item var1, int var2) {
		this.field_94151_a = var1;
		this.field_94150_f = var2;
	}

	public RenderSnowball(Item var1) {
		this(var1, 0);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		Icon var10 = this.field_94151_a.getIconFromDamage(this.field_94150_f);
		if(var10 != null) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)var2, (float)var4, (float)var6);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			this.loadTexture("/gui/items.png");
			Tessellator var11 = Tessellator.instance;
			if(var10 == ItemPotion.func_94589_d("potion_splash")) {
				int var12 = PotionHelper.func_77915_a(((EntityPotion)var1).getPotionDamage(), false);
				float var13 = (float)(var12 >> 16 & 255) / 255.0F;
				float var14 = (float)(var12 >> 8 & 255) / 255.0F;
				float var15 = (float)(var12 & 255) / 255.0F;
				GL11.glColor3f(var13, var14, var15);
				GL11.glPushMatrix();
				this.func_77026_a(var11, ItemPotion.func_94589_d("potion_contents"));
				GL11.glPopMatrix();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
			}

			this.func_77026_a(var11, var10);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	private void func_77026_a(Tessellator var1, Icon var2) {
		float var3 = var2.getMinU();
		float var4 = var2.getMaxU();
		float var5 = var2.getMinV();
		float var6 = var2.getMaxV();
		float var7 = 1.0F;
		float var8 = 0.5F;
		float var9 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		var1.startDrawingQuads();
		var1.setNormal(0.0F, 1.0F, 0.0F);
		var1.addVertexWithUV((double)(0.0F - var8), (double)(0.0F - var9), 0.0D, (double)var3, (double)var6);
		var1.addVertexWithUV((double)(var7 - var8), (double)(0.0F - var9), 0.0D, (double)var4, (double)var6);
		var1.addVertexWithUV((double)(var7 - var8), (double)(var7 - var9), 0.0D, (double)var4, (double)var5);
		var1.addVertexWithUV((double)(0.0F - var8), (double)(var7 - var9), 0.0D, (double)var3, (double)var5);
		var1.draw();
	}
}
