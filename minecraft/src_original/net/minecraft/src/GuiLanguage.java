package net.minecraft.src;

public class GuiLanguage extends GuiScreen {
	protected GuiScreen parentGui;
	private int updateTimer = -1;
	private GuiSlotLanguage languageList;
	private final GameSettings theGameSettings;
	private GuiSmallButton doneButton;

	public GuiLanguage(GuiScreen var1, GameSettings var2) {
		this.parentGui = var1;
		this.theGameSettings = var2;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.add(this.doneButton = new GuiSmallButton(6, this.width / 2 - 75, this.height - 38, var1.translateKey("gui.done")));
		this.languageList = new GuiSlotLanguage(this);
		this.languageList.registerScrollButtons(this.buttonList, 7, 8);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			switch(var1.id) {
			case 5:
				break;
			case 6:
				this.mc.displayGuiScreen(this.parentGui);
				break;
			default:
				this.languageList.actionPerformed(var1);
			}

		}
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.languageList.drawScreen(var1, var2, var3);
		if(this.updateTimer <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.updateTimer += 20;
		}

		StringTranslate var4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, var4.translateKey("options.language"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.fontRenderer, "(" + var4.translateKey("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.drawScreen(var1, var2, var3);
	}

	public void updateScreen() {
		super.updateScreen();
		--this.updateTimer;
	}

	static GameSettings getGameSettings(GuiLanguage var0) {
		return var0.theGameSettings;
	}

	static GuiSmallButton getDoneButton(GuiLanguage var0) {
		return var0.doneButton;
	}
}
