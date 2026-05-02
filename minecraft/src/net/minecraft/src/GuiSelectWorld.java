package net.minecraft.src;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class GuiSelectWorld extends GuiScreen {
	private final DateFormat dateFormatter = new SimpleDateFormat();
	protected GuiScreen parentScreen;
	protected String screenTitle = "Select world";
	private boolean selected = false;
	private int selectedWorld;
	private List saveList;
	private GuiWorldSlot worldSlotContainer;
	private String localizedWorldText;
	private String localizedMustConvertText;
	private String[] localizedGameModeText = new String[3];
	private boolean deleting;
	private GuiButton buttonDelete;
	private GuiButton buttonSelect;
	private GuiButton buttonRename;
	private GuiButton buttonRecreate;

	public GuiSelectWorld(GuiScreen var1) {
		this.parentScreen = var1;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.screenTitle = var1.translateKey("selectWorld.title");

		try {
			this.loadSaves();
		} catch (AnvilConverterException var3) {
			var3.printStackTrace();
			this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load words", var3.getMessage()));
			return;
		}

		this.localizedWorldText = var1.translateKey("selectWorld.world");
		this.localizedMustConvertText = var1.translateKey("selectWorld.conversion");
		this.localizedGameModeText[EnumGameType.SURVIVAL.getID()] = var1.translateKey("gameMode.survival");
		this.localizedGameModeText[EnumGameType.CREATIVE.getID()] = var1.translateKey("gameMode.creative");
		this.localizedGameModeText[EnumGameType.ADVENTURE.getID()] = var1.translateKey("gameMode.adventure");
		this.worldSlotContainer = new GuiWorldSlot(this);
		this.worldSlotContainer.registerScrollButtons(this.buttonList, 4, 5);
		this.initButtons();
	}

	private void loadSaves() throws AnvilConverterException {
		ISaveFormat var1 = this.mc.getSaveLoader();
		this.saveList = var1.getSaveList();
		Collections.sort(this.saveList);
		this.selectedWorld = -1;
	}

	protected String getSaveFileName(int var1) {
		return ((SaveFormatComparator)this.saveList.get(var1)).getFileName();
	}

	protected String getSaveName(int var1) {
		String var2 = ((SaveFormatComparator)this.saveList.get(var1)).getDisplayName();
		if(var2 == null || MathHelper.stringNullOrLengthZero(var2)) {
			StringTranslate var3 = StringTranslate.getInstance();
			var2 = var3.translateKey("selectWorld.world") + " " + (var1 + 1);
		}

		return var2;
	}

	public void initButtons() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, var1.translateKey("selectWorld.select")));
		this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, var1.translateKey("selectWorld.create")));
		this.buttonList.add(this.buttonRename = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, var1.translateKey("selectWorld.rename")));
		this.buttonList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, var1.translateKey("selectWorld.delete")));
		this.buttonList.add(this.buttonRecreate = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, var1.translateKey("selectWorld.recreate")));
		this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, var1.translateKey("gui.cancel")));
		this.buttonSelect.enabled = false;
		this.buttonDelete.enabled = false;
		this.buttonRename.enabled = false;
		this.buttonRecreate.enabled = false;
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id == 2) {
				String var2 = this.getSaveName(this.selectedWorld);
				if(var2 != null) {
					this.deleting = true;
					GuiYesNo var3 = getDeleteWorldScreen(this, var2, this.selectedWorld);
					this.mc.displayGuiScreen(var3);
				}
			} else if(var1.id == 1) {
				this.selectWorld(this.selectedWorld);
			} else if(var1.id == 3) {
				this.mc.displayGuiScreen(new GuiCreateWorld(this));
			} else if(var1.id == 6) {
				this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getSaveFileName(this.selectedWorld)));
			} else if(var1.id == 0) {
				this.mc.displayGuiScreen(this.parentScreen);
			} else if(var1.id == 7) {
				GuiCreateWorld var5 = new GuiCreateWorld(this);
				ISaveHandler var6 = this.mc.getSaveLoader().getSaveLoader(this.getSaveFileName(this.selectedWorld), false);
				WorldInfo var4 = var6.loadWorldInfo();
				var6.flush();
				var5.func_82286_a(var4);
				this.mc.displayGuiScreen(var5);
			} else {
				this.worldSlotContainer.actionPerformed(var1);
			}

		}
	}

	public void selectWorld(int var1) {
		this.mc.displayGuiScreen((GuiScreen)null);
		if(!this.selected) {
			this.selected = true;
			String var2 = this.getSaveFileName(var1);
			if(var2 == null) {
				var2 = "World" + var1;
			}

			String var3 = this.getSaveName(var1);
			if(var3 == null) {
				var3 = "World" + var1;
			}

			if(this.mc.getSaveLoader().canLoadWorld(var2)) {
				this.mc.launchIntegratedServer(var2, var3, (WorldSettings)null);
			}

		}
	}

	public void confirmClicked(boolean var1, int var2) {
		if(this.deleting) {
			this.deleting = false;
			if(var1) {
				ISaveFormat var3 = this.mc.getSaveLoader();
				var3.flushCache();
				var3.deleteWorldDirectory(this.getSaveFileName(var2));

				try {
					this.loadSaves();
				} catch (AnvilConverterException var5) {
					var5.printStackTrace();
				}
			}

			this.mc.displayGuiScreen(this);
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.worldSlotContainer.drawScreen(var1, var2, var3);
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
		super.drawScreen(var1, var2, var3);
	}

	public static GuiYesNo getDeleteWorldScreen(GuiScreen var0, String var1, int var2) {
		StringTranslate var3 = StringTranslate.getInstance();
		String var4 = var3.translateKey("selectWorld.deleteQuestion");
		String var5 = "\'" + var1 + "\' " + var3.translateKey("selectWorld.deleteWarning");
		String var6 = var3.translateKey("selectWorld.deleteButton");
		String var7 = var3.translateKey("gui.cancel");
		GuiYesNo var8 = new GuiYesNo(var0, var4, var5, var6, var7, var2);
		return var8;
	}

	static List getSize(GuiSelectWorld var0) {
		return var0.saveList;
	}

	static int onElementSelected(GuiSelectWorld var0, int var1) {
		return var0.selectedWorld = var1;
	}

	static int getSelectedWorld(GuiSelectWorld var0) {
		return var0.selectedWorld;
	}

	static GuiButton getSelectButton(GuiSelectWorld var0) {
		return var0.buttonSelect;
	}

	static GuiButton getRenameButton(GuiSelectWorld var0) {
		return var0.buttonDelete;
	}

	static GuiButton getDeleteButton(GuiSelectWorld var0) {
		return var0.buttonRename;
	}

	static GuiButton func_82312_f(GuiSelectWorld var0) {
		return var0.buttonRecreate;
	}

	static String func_82313_g(GuiSelectWorld var0) {
		return var0.localizedWorldText;
	}

	static DateFormat func_82315_h(GuiSelectWorld var0) {
		return var0.dateFormatter;
	}

	static String func_82311_i(GuiSelectWorld var0) {
		return var0.localizedMustConvertText;
	}

	static String[] func_82314_j(GuiSelectWorld var0) {
		return var0.localizedGameModeText;
	}
}
