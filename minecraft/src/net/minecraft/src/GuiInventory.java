package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiInventory extends InventoryEffectRenderer {
	private float xSize_lo;
	private float ySize_lo;

	public GuiInventory(EntityPlayer var1) {
		super(var1.inventoryContainer);
		this.allowUserInput = true;
		var1.addStat(AchievementList.openInventory, 1);
	}

	public void updateScreen() {
		if(this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		}

	}

	public void initGui() {
		this.buttonList.clear();
		if(this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		} else {
			super.initGui();
		}

	}

	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 86, 16, 4210752);
	}

	public void drawScreen(int var1, int var2, float var3) {
		super.drawScreen(var1, var2, var3);
		this.xSize_lo = (float)var1;
		this.ySize_lo = (float)var2;
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/gui/inventory.png");
		int var4 = this.guiLeft;
		int var5 = this.guiTop;
		this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
		drawPlayerOnGui(this.mc, var4 + 51, var5 + 75, 30, (float)(var4 + 51) - this.xSize_lo, (float)(var5 + 75 - 50) - this.ySize_lo);
	}

	public static void drawPlayerOnGui(Minecraft var0, int var1, int var2, int var3, float var4, float var5) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var1, (float)var2, 50.0F);
		GL11.glScalef((float)(-var3), (float)var3, (float)var3);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float var6 = var0.thePlayer.renderYawOffset;
		float var7 = var0.thePlayer.rotationYaw;
		float var8 = var0.thePlayer.rotationPitch;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(var5 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		var0.thePlayer.renderYawOffset = (float)Math.atan((double)(var4 / 40.0F)) * 20.0F;
		var0.thePlayer.rotationYaw = (float)Math.atan((double)(var4 / 40.0F)) * 40.0F;
		var0.thePlayer.rotationPitch = -((float)Math.atan((double)(var5 / 40.0F))) * 20.0F;
		var0.thePlayer.rotationYawHead = var0.thePlayer.rotationYaw;
		GL11.glTranslatef(0.0F, var0.thePlayer.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(var0.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		var0.thePlayer.renderYawOffset = var6;
		var0.thePlayer.rotationYaw = var7;
		var0.thePlayer.rotationPitch = var8;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
		}

		if(var1.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
		}

	}
}
