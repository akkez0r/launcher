package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWitherSkull extends Render {
	ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

	private float func_82400_a(float var1, float var2, float var3) {
		float var4;
		for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		return var1 + var3 * var4;
	}

	public void func_82399_a(EntityWitherSkull var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		float var10 = this.func_82400_a(var1.prevRotationYaw, var1.rotationYaw, var9);
		float var11 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9;
		GL11.glTranslatef((float)var2, (float)var4, (float)var6);
		float var12 = 1.0F / 16.0F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		if(var1.isInvulnerable()) {
			this.loadTexture("/mob/wither_invul.png");
		} else {
			this.loadTexture("/mob/wither.png");
		}

		this.skeletonHeadModel.render(var1, 0.0F, 0.0F, 0.0F, var10, var11, var12);
		GL11.glPopMatrix();
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.func_82399_a((EntityWitherSkull)var1, var2, var4, var6, var8, var9);
	}
}
