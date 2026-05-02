package net.minecraft.src;

public class GuiVideoSettings extends GuiScreen {
	private GuiScreen parentGuiScreen;
	protected String screenTitle = "Video Settings";
	private GameSettings guiGameSettings;
	private boolean is64bit = false;
	private static EnumOptions[] videoOptions = new EnumOptions[]{EnumOptions.GRAPHICS, EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, EnumOptions.FRAMERATE_LIMIT, EnumOptions.ANAGLYPH, EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, EnumOptions.RENDER_CLOUDS, EnumOptions.PARTICLES, EnumOptions.USE_SERVER_TEXTURES, EnumOptions.USE_FULLSCREEN, EnumOptions.ENABLE_VSYNC};

	public GuiVideoSettings(GuiScreen var1, GameSettings var2) {
		this.parentGuiScreen = var1;
		this.guiGameSettings = var2;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.screenTitle = var1.translateKey("options.videoTitle");
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, var1.translateKey("gui.done")));
		this.is64bit = false;
		String[] var2 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
		String[] var3 = var2;
		int var4 = var2.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			String var6 = var3[var5];
			String var7 = System.getProperty(var6);
			if(var7 != null && var7.contains("64")) {
				this.is64bit = true;
				break;
			}
		}

		int var9 = 0;
		var4 = this.is64bit ? 0 : -15;
		EnumOptions[] var10 = videoOptions;
		int var11 = var10.length;

		for(int var12 = 0; var12 < var11; ++var12) {
			EnumOptions var8 = var10[var12];
			if(var8.getEnumFloat()) {
				this.buttonList.add(new GuiSlider(var8.returnEnumOrdinal(), this.width / 2 - 155 + var9 % 2 * 160, this.height / 7 + var4 + 24 * (var9 >> 1), var8, this.guiGameSettings.getKeyBinding(var8), this.guiGameSettings.getOptionFloatValue(var8)));
			} else {
				this.buttonList.add(new GuiSmallButton(var8.returnEnumOrdinal(), this.width / 2 - 155 + var9 % 2 * 160, this.height / 7 + var4 + 24 * (var9 >> 1), var8, this.guiGameSettings.getKeyBinding(var8)));
			}

			++var9;
		}

	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			int var2 = this.guiGameSettings.guiScale;
			if(var1.id < 100 && var1 instanceof GuiSmallButton) {
				this.guiGameSettings.setOptionValue(((GuiSmallButton)var1).returnEnumOptions(), 1);
				var1.displayString = this.guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(var1.id));
			}

			if(var1.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}

			if(this.guiGameSettings.guiScale != var2) {
				ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
				int var4 = var3.getScaledWidth();
				int var5 = var3.getScaledHeight();
				this.setWorldAndResolution(this.mc, var4, var5);
			}

		}
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, this.is64bit ? 20 : 5, 16777215);
		if(!this.is64bit && this.guiGameSettings.renderDistance == 0) {
			this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("options.farWarning1"), this.width / 2, this.height / 6 + 144 + 1, 11468800);
			this.drawCenteredString(this.fontRenderer, StatCollector.translateToLocal("options.farWarning2"), this.width / 2, this.height / 6 + 144 + 13, 11468800);
		}

		super.drawScreen(var1, var2, var3);
	}
}
