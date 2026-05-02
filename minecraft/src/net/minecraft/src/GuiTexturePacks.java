package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;

public class GuiTexturePacks extends GuiScreen {
	protected GuiScreen guiScreen;
	private int refreshTimer = -1;
	private String fileLocation = "";
	private GuiTexturePackSlot guiTexturePackSlot;
	private GameSettings field_96146_n;

	public GuiTexturePacks(GuiScreen var1, GameSettings var2) {
		this.guiScreen = var1;
		this.field_96146_n = var2;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, var1.translateKey("texturePack.openFolder")));
		this.buttonList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, var1.translateKey("gui.done")));
		this.mc.texturePackList.updateAvaliableTexturePacks();
		this.fileLocation = (new File(Minecraft.getMinecraftDir(), "texturepacks")).getAbsolutePath();
		this.guiTexturePackSlot = new GuiTexturePackSlot(this);
		this.guiTexturePackSlot.registerScrollButtons(this.buttonList, 7, 8);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id == 5) {
				if(Minecraft.getOs() == EnumOS.MACOS) {
					try {
						this.mc.getLogAgent().logInfo(this.fileLocation);
						Runtime.getRuntime().exec(new String[]{"/usr/bin/open", this.fileLocation});
						return;
					} catch (IOException var7) {
						var7.printStackTrace();
					}
				} else if(Minecraft.getOs() == EnumOS.WINDOWS) {
					String var2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[]{this.fileLocation});

					try {
						Runtime.getRuntime().exec(var2);
						return;
					} catch (IOException var6) {
						var6.printStackTrace();
					}
				}

				boolean var8 = false;

				try {
					Class var3 = Class.forName("java.awt.Desktop");
					Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
					var3.getMethod("browse", new Class[]{URI.class}).invoke(var4, new Object[]{(new File(Minecraft.getMinecraftDir(), "texturepacks")).toURI()});
				} catch (Throwable var5) {
					var5.printStackTrace();
					var8 = true;
				}

				if(var8) {
					this.mc.getLogAgent().logInfo("Opening via system class!");
					Sys.openURL("file://" + this.fileLocation);
				}
			} else if(var1.id == 6) {
				this.mc.displayGuiScreen(this.guiScreen);
			} else {
				this.guiTexturePackSlot.actionPerformed(var1);
			}

		}
	}

	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		super.mouseMovedOrUp(var1, var2, var3);
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.guiTexturePackSlot.drawScreen(var1, var2, var3);
		if(this.refreshTimer <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.refreshTimer += 20;
		}

		StringTranslate var4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.title"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.drawScreen(var1, var2, var3);
	}

	public void updateScreen() {
		super.updateScreen();
		--this.refreshTimer;
	}

	static Minecraft func_73950_a(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73955_b(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73958_c(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73951_d(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73952_e(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73962_f(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73959_g(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73957_h(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73956_i(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73953_j(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_73961_k(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_96143_l(GuiTexturePacks var0) {
		return var0.mc;
	}

	static Minecraft func_96142_m(GuiTexturePacks var0) {
		return var0.mc;
	}

	static FontRenderer func_73954_n(GuiTexturePacks var0) {
		return var0.fontRenderer;
	}

	static FontRenderer func_96145_o(GuiTexturePacks var0) {
		return var0.fontRenderer;
	}

	static FontRenderer func_96144_p(GuiTexturePacks var0) {
		return var0.fontRenderer;
	}
}
