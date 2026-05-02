package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

class GuiSlotLanguage extends GuiSlot {
	private ArrayList field_77251_g;
	private TreeMap field_77253_h;
	final GuiLanguage languageGui;

	public GuiSlotLanguage(GuiLanguage var1) {
		super(var1.mc, var1.width, var1.height, 32, var1.height - 65 + 4, 18);
		this.languageGui = var1;
		this.field_77253_h = StringTranslate.getInstance().getLanguageList();
		this.field_77251_g = new ArrayList();
		Iterator var2 = this.field_77253_h.keySet().iterator();

		while(var2.hasNext()) {
			String var3 = (String)var2.next();
			this.field_77251_g.add(var3);
		}

	}

	protected int getSize() {
		return this.field_77251_g.size();
	}

	protected void elementClicked(int var1, boolean var2) {
		StringTranslate.getInstance().setLanguage((String)this.field_77251_g.get(var1), false);
		this.languageGui.mc.fontRenderer.setUnicodeFlag(StringTranslate.getInstance().isUnicode());
		GuiLanguage.getGameSettings(this.languageGui).language = (String)this.field_77251_g.get(var1);
		this.languageGui.fontRenderer.setBidiFlag(StringTranslate.isBidirectional(GuiLanguage.getGameSettings(this.languageGui).language));
		GuiLanguage.getDoneButton(this.languageGui).displayString = StringTranslate.getInstance().translateKey("gui.done");
		GuiLanguage.getGameSettings(this.languageGui).saveOptions();
	}

	protected boolean isSelected(int var1) {
		return ((String)this.field_77251_g.get(var1)).equals(StringTranslate.getInstance().getCurrentLanguage());
	}

	protected int getContentHeight() {
		return this.getSize() * 18;
	}

	protected void drawBackground() {
		this.languageGui.drawDefaultBackground();
	}

	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		this.languageGui.fontRenderer.setBidiFlag(true);
		this.languageGui.drawCenteredString(this.languageGui.fontRenderer, (String)this.field_77253_h.get(this.field_77251_g.get(var1)), this.languageGui.width / 2, var3 + 1, 16777215);
		this.languageGui.fontRenderer.setBidiFlag(StringTranslate.isBidirectional(GuiLanguage.getGameSettings(this.languageGui).language));
	}
}
