package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class GuiDisconnected extends GuiScreen {
	private String errorMessage;
	private String errorDetail;
	private Object[] field_74247_c;
	private List field_74245_d;
	private final GuiScreen field_98095_n;

	public GuiDisconnected(GuiScreen var1, String var2, String var3, Object... var4) {
		StringTranslate var5 = StringTranslate.getInstance();
		this.field_98095_n = var1;
		this.errorMessage = var5.translateKey(var2);
		this.errorDetail = var3;
		this.field_74247_c = var4;
	}

	protected void keyTyped(char var1, int var2) {
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.toMenu")));
		if(this.field_74247_c != null) {
			this.field_74245_d = this.fontRenderer.listFormattedStringToWidth(var1.translateKeyFormat(this.errorDetail, this.field_74247_c), this.width - 50);
		} else {
			this.field_74245_d = this.fontRenderer.listFormattedStringToWidth(var1.translateKey(this.errorDetail), this.width - 50);
		}

	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.mc.displayGuiScreen(this.field_98095_n);
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 11184810);
		int var4 = this.height / 2 - 30;
		if(this.field_74245_d != null) {
			for(Iterator var5 = this.field_74245_d.iterator(); var5.hasNext(); var4 += this.fontRenderer.FONT_HEIGHT) {
				String var6 = (String)var5.next();
				this.drawCenteredString(this.fontRenderer, var6, this.width / 2, var4, 16777215);
			}
		}

		super.drawScreen(var1, var2, var3);
	}
}
