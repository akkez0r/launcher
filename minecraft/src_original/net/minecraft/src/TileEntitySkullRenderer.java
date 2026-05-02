package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer {
	public static TileEntitySkullRenderer skullRenderer;
	private ModelSkeletonHead field_82396_c = new ModelSkeletonHead(0, 0, 64, 32);
	private ModelSkeletonHead field_82395_d = new ModelSkeletonHead(0, 0, 64, 64);

	public void renderTileEntitySkullAt(TileEntitySkull var1, double var2, double var4, double var6, float var8) {
		this.func_82393_a((float)var2, (float)var4, (float)var6, var1.getBlockMetadata() & 7, (float)(var1.func_82119_b() * 360) / 16.0F, var1.getSkullType(), var1.getExtraType());
	}

	public void setTileEntityRenderer(TileEntityRenderer var1) {
		super.setTileEntityRenderer(var1);
		skullRenderer = this;
	}

	public void func_82393_a(float var1, float var2, float var3, int var4, float var5, int var6, String var7) {
		ModelSkeletonHead var8 = this.field_82396_c;
		switch(var6) {
		case 0:
		default:
			this.bindTextureByName("/mob/skeleton.png");
			break;
		case 1:
			this.bindTextureByName("/mob/skeleton_wither.png");
			break;
		case 2:
			this.bindTextureByName("/mob/zombie.png");
			var8 = this.field_82395_d;
			break;
		case 3:
			if(var7 != null && var7.length() > 0) {
				String var9 = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(var7) + ".png";
				if(!skullRenderer.tileEntityRenderer.renderEngine.hasImageData(var9)) {
					skullRenderer.tileEntityRenderer.renderEngine.obtainImageData(var9, new ImageBufferDownload());
				}

				this.bindTextureByURL(var9, "/mob/char.png");
			} else {
				this.bindTextureByName("/mob/char.png");
			}
			break;
		case 4:
			this.bindTextureByName("/mob/creeper.png");
		}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		if(var4 != 1) {
			switch(var4) {
			case 2:
				GL11.glTranslatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
				break;
			case 3:
				GL11.glTranslatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
				var5 = 180.0F;
				break;
			case 4:
				GL11.glTranslatef(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
				var5 = 270.0F;
				break;
			case 5:
			default:
				GL11.glTranslatef(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
				var5 = 90.0F;
			}
		} else {
			GL11.glTranslatef(var1 + 0.5F, var2, var3 + 0.5F);
		}

		float var10 = 1.0F / 16.0F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		var8.render((Entity)null, 0.0F, 0.0F, 0.0F, var5, 0.0F, var10);
		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
		this.renderTileEntitySkullAt((TileEntitySkull)var1, var2, var4, var6, var8);
	}
}
