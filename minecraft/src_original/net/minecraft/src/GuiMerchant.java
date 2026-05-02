package net.minecraft.src;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiMerchant extends GuiContainer {
	private IMerchant theIMerchant;
	private GuiButtonMerchant nextRecipeButtonIndex;
	private GuiButtonMerchant previousRecipeButtonIndex;
	private int currentRecipeIndex = 0;
	private String field_94082_v;

	public GuiMerchant(InventoryPlayer var1, IMerchant var2, World var3, String var4) {
		super(new ContainerMerchant(var1, var2, var3));
		this.theIMerchant = var2;
		this.field_94082_v = var4 != null && var4.length() >= 1 ? var4 : StatCollector.translateToLocal("entity.Villager.name");
	}

	public void initGui() {
		super.initGui();
		int var1 = (this.width - this.xSize) / 2;
		int var2 = (this.height - this.ySize) / 2;
		this.buttonList.add(this.nextRecipeButtonIndex = new GuiButtonMerchant(1, var1 + 120 + 27, var2 + 24 - 1, true));
		this.buttonList.add(this.previousRecipeButtonIndex = new GuiButtonMerchant(2, var1 + 36 - 19, var2 + 24 - 1, false));
		this.nextRecipeButtonIndex.enabled = false;
		this.previousRecipeButtonIndex.enabled = false;
	}

	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		this.fontRenderer.drawString(this.field_94082_v, this.xSize / 2 - this.fontRenderer.getStringWidth(this.field_94082_v) / 2, 6, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	public void updateScreen() {
		super.updateScreen();
		MerchantRecipeList var1 = this.theIMerchant.getRecipes(this.mc.thePlayer);
		if(var1 != null) {
			this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < var1.size() - 1;
			this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
		}

	}

	protected void actionPerformed(GuiButton var1) {
		boolean var2 = false;
		if(var1 == this.nextRecipeButtonIndex) {
			++this.currentRecipeIndex;
			var2 = true;
		} else if(var1 == this.previousRecipeButtonIndex) {
			--this.currentRecipeIndex;
			var2 = true;
		}

		if(var2) {
			((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.currentRecipeIndex);
			ByteArrayOutputStream var3 = new ByteArrayOutputStream();
			DataOutputStream var4 = new DataOutputStream(var3);

			try {
				var4.writeInt(this.currentRecipeIndex);
				this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("MC|TrSel", var3.toByteArray()));
			} catch (Exception var6) {
				var6.printStackTrace();
			}
		}

	}

	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/gui/trading.png");
		int var4 = (this.width - this.xSize) / 2;
		int var5 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
		MerchantRecipeList var6 = this.theIMerchant.getRecipes(this.mc.thePlayer);
		if(var6 != null && !var6.isEmpty()) {
			int var7 = this.currentRecipeIndex;
			MerchantRecipe var8 = (MerchantRecipe)var6.get(var7);
			if(var8.func_82784_g()) {
				this.mc.renderEngine.bindTexture("/gui/trading.png");
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
				this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
				this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
			}
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		super.drawScreen(var1, var2, var3);
		MerchantRecipeList var4 = this.theIMerchant.getRecipes(this.mc.thePlayer);
		if(var4 != null && !var4.isEmpty()) {
			int var5 = (this.width - this.xSize) / 2;
			int var6 = (this.height - this.ySize) / 2;
			int var7 = this.currentRecipeIndex;
			MerchantRecipe var8 = (MerchantRecipe)var4.get(var7);
			GL11.glPushMatrix();
			ItemStack var9 = var8.getItemToBuy();
			ItemStack var10 = var8.getSecondItemToBuy();
			ItemStack var11 = var8.getItemToSell();
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glEnable(GL11.GL_LIGHTING);
			itemRenderer.zLevel = 100.0F;
			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
			if(var10 != null) {
				itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
				itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
			}

			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
			itemRenderer.zLevel = 0.0F;
			GL11.glDisable(GL11.GL_LIGHTING);
			if(this.isPointInRegion(36, 24, 16, 16, var1, var2)) {
				this.drawItemStackTooltip(var9, var1, var2);
			} else if(var10 != null && this.isPointInRegion(62, 24, 16, 16, var1, var2)) {
				this.drawItemStackTooltip(var10, var1, var2);
			} else if(this.isPointInRegion(120, 24, 16, 16, var1, var2)) {
				this.drawItemStackTooltip(var11, var1, var2);
			}

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
		}

	}

	public IMerchant getIMerchant() {
		return this.theIMerchant;
	}
}
