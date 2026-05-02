package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameSettings {
	private static final String[] RENDER_DISTANCES = new String[]{"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
	private static final String[] DIFFICULTIES = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
	private static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
	private static final String[] CHAT_VISIBILITIES = new String[]{"options.chat.visibility.full", "options.chat.visibility.system", "options.chat.visibility.hidden"};
	private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] LIMIT_FRAMERATES = new String[]{"performance.max", "performance.balanced", "performance.powersaver"};
	private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	public float musicVolume = 1.0F;
	public float soundVolume = 1.0F;
	public float mouseSensitivity = 0.5F;
	public boolean invertMouse = false;
	public int renderDistance = 0;
	public boolean viewBobbing = true;
	public boolean anaglyph = false;
	public boolean advancedOpengl = false;
	public int limitFramerate = 1;
	public boolean fancyGraphics = true;
	public int ambientOcclusion = 2;
	public boolean clouds = true;
	public String skin = "Default";
	public int chatVisibility = 0;
	public boolean chatColours = true;
	public boolean chatLinks = true;
	public boolean chatLinksPrompt = true;
	public float chatOpacity = 1.0F;
	public boolean serverTextures = true;
	public boolean snooperEnabled = true;
	public boolean fullScreen = false;
	public boolean enableVsync = true;
	public boolean hideServerAddress = false;
	public boolean advancedItemTooltips = false;
	public boolean pauseOnLostFocus = true;
	public boolean showCape = true;
	public boolean touchscreen = false;
	public int overrideWidth = 0;
	public int overrideHeight = 0;
	public boolean heldItemTooltips = true;
	public float chatScale = 1.0F;
	public float chatWidth = 1.0F;
	public float chatHeightUnfocused = 0.44366196F;
	public float chatHeightFocused = 1.0F;
	public KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
	public KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
	public KeyBinding keyBindBack = new KeyBinding("key.back", 31);
	public KeyBinding keyBindRight = new KeyBinding("key.right", 32);
	public KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
	public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
	public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
	public KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
	public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
	public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100);
	public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99);
	public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15);
	public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98);
	public KeyBinding keyBindCommand = new KeyBinding("key.command", 53);
	public KeyBinding[] keyBindings = new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand};
	protected Minecraft mc;
	private File optionsFile;
	public int difficulty = 2;
	public boolean hideGUI = false;
	public int thirdPersonView = 0;
	public boolean showDebugInfo = false;
	public boolean showDebugProfilerChart = false;
	public String lastServer = "";
	public boolean noclip = false;
	public boolean smoothCamera = false;
	public boolean debugCamEnable = false;
	public float noclipRate = 1.0F;
	public float debugCamRate = 1.0F;
	public float fovSetting = 0.0F;
	public float gammaSetting = 0.0F;
	public int guiScale = 0;
	public int particleSetting = 0;
	public String language = "en_US";

	public GameSettings(Minecraft var1, File var2) {
		this.mc = var1;
		this.optionsFile = new File(var2, "options.txt");
		this.loadOptions();
	}

	public GameSettings() {
	}

	public String getKeyBindingDescription(int var1) {
		StringTranslate var2 = StringTranslate.getInstance();
		return var2.translateKey(this.keyBindings[var1].keyDescription);
	}

	public String getOptionDisplayString(int var1) {
		int var2 = this.keyBindings[var1].keyCode;
		return getKeyDisplayString(var2);
	}

	public static String getKeyDisplayString(int var0) {
		return var0 < 0 ? StatCollector.translateToLocalFormatted("key.mouseButton", new Object[]{Integer.valueOf(var0 + 101)}) : Keyboard.getKeyName(var0);
	}

	public static boolean isKeyDown(KeyBinding var0) {
		return var0.keyCode < 0 ? Mouse.isButtonDown(var0.keyCode + 100) : Keyboard.isKeyDown(var0.keyCode);
	}

	public void setKeyBinding(int var1, int var2) {
		this.keyBindings[var1].keyCode = var2;
		this.saveOptions();
	}

	public void setOptionFloatValue(EnumOptions var1, float var2) {
		if(var1 == EnumOptions.MUSIC) {
			this.musicVolume = var2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(var1 == EnumOptions.SOUND) {
			this.soundVolume = var2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(var1 == EnumOptions.SENSITIVITY) {
			this.mouseSensitivity = var2;
		}

		if(var1 == EnumOptions.FOV) {
			this.fovSetting = var2;
		}

		if(var1 == EnumOptions.GAMMA) {
			this.gammaSetting = var2;
		}

		if(var1 == EnumOptions.CHAT_OPACITY) {
			this.chatOpacity = var2;
			this.mc.ingameGUI.getChatGUI().func_96132_b();
		}

		if(var1 == EnumOptions.CHAT_HEIGHT_FOCUSED) {
			this.chatHeightFocused = var2;
			this.mc.ingameGUI.getChatGUI().func_96132_b();
		}

		if(var1 == EnumOptions.CHAT_HEIGHT_UNFOCUSED) {
			this.chatHeightUnfocused = var2;
			this.mc.ingameGUI.getChatGUI().func_96132_b();
		}

		if(var1 == EnumOptions.CHAT_WIDTH) {
			this.chatWidth = var2;
			this.mc.ingameGUI.getChatGUI().func_96132_b();
		}

		if(var1 == EnumOptions.CHAT_SCALE) {
			this.chatScale = var2;
			this.mc.ingameGUI.getChatGUI().func_96132_b();
		}

	}

	public void setOptionValue(EnumOptions var1, int var2) {
		if(var1 == EnumOptions.INVERT_MOUSE) {
			this.invertMouse = !this.invertMouse;
		}

		if(var1 == EnumOptions.RENDER_DISTANCE) {
			this.renderDistance = this.renderDistance + var2 & 3;
		}

		if(var1 == EnumOptions.GUI_SCALE) {
			this.guiScale = this.guiScale + var2 & 3;
		}

		if(var1 == EnumOptions.PARTICLES) {
			this.particleSetting = (this.particleSetting + var2) % 3;
		}

		if(var1 == EnumOptions.VIEW_BOBBING) {
			this.viewBobbing = !this.viewBobbing;
		}

		if(var1 == EnumOptions.RENDER_CLOUDS) {
			this.clouds = !this.clouds;
		}

		if(var1 == EnumOptions.ADVANCED_OPENGL) {
			this.advancedOpengl = !this.advancedOpengl;
			this.mc.renderGlobal.loadRenderers();
		}

		if(var1 == EnumOptions.ANAGLYPH) {
			this.anaglyph = !this.anaglyph;
			this.mc.renderEngine.refreshTextures();
		}

		if(var1 == EnumOptions.FRAMERATE_LIMIT) {
			this.limitFramerate = (this.limitFramerate + var2 + 3) % 3;
		}

		if(var1 == EnumOptions.DIFFICULTY) {
			this.difficulty = this.difficulty + var2 & 3;
		}

		if(var1 == EnumOptions.GRAPHICS) {
			this.fancyGraphics = !this.fancyGraphics;
			this.mc.renderGlobal.loadRenderers();
		}

		if(var1 == EnumOptions.AMBIENT_OCCLUSION) {
			this.ambientOcclusion = (this.ambientOcclusion + var2) % 3;
			this.mc.renderGlobal.loadRenderers();
		}

		if(var1 == EnumOptions.CHAT_VISIBILITY) {
			this.chatVisibility = (this.chatVisibility + var2) % 3;
		}

		if(var1 == EnumOptions.CHAT_COLOR) {
			this.chatColours = !this.chatColours;
		}

		if(var1 == EnumOptions.CHAT_LINKS) {
			this.chatLinks = !this.chatLinks;
		}

		if(var1 == EnumOptions.CHAT_LINKS_PROMPT) {
			this.chatLinksPrompt = !this.chatLinksPrompt;
		}

		if(var1 == EnumOptions.USE_SERVER_TEXTURES) {
			this.serverTextures = !this.serverTextures;
		}

		if(var1 == EnumOptions.SNOOPER_ENABLED) {
			this.snooperEnabled = !this.snooperEnabled;
		}

		if(var1 == EnumOptions.SHOW_CAPE) {
			this.showCape = !this.showCape;
		}

		if(var1 == EnumOptions.TOUCHSCREEN) {
			this.touchscreen = !this.touchscreen;
		}

		if(var1 == EnumOptions.USE_FULLSCREEN) {
			this.fullScreen = !this.fullScreen;
			if(this.mc.isFullScreen() != this.fullScreen) {
				this.mc.toggleFullscreen();
			}
		}

		if(var1 == EnumOptions.ENABLE_VSYNC) {
			this.enableVsync = !this.enableVsync;
			Display.setVSyncEnabled(this.enableVsync);
		}

		this.saveOptions();
	}

	public float getOptionFloatValue(EnumOptions var1) {
		return var1 == EnumOptions.FOV ? this.fovSetting : (var1 == EnumOptions.GAMMA ? this.gammaSetting : (var1 == EnumOptions.MUSIC ? this.musicVolume : (var1 == EnumOptions.SOUND ? this.soundVolume : (var1 == EnumOptions.SENSITIVITY ? this.mouseSensitivity : (var1 == EnumOptions.CHAT_OPACITY ? this.chatOpacity : (var1 == EnumOptions.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (var1 == EnumOptions.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (var1 == EnumOptions.CHAT_SCALE ? this.chatScale : (var1 == EnumOptions.CHAT_WIDTH ? this.chatWidth : 0.0F)))))))));
	}

	public boolean getOptionOrdinalValue(EnumOptions var1) {
		switch(EnumOptionsHelper.enumOptionsMappingHelperArray[var1.ordinal()]) {
		case 1:
			return this.invertMouse;
		case 2:
			return this.viewBobbing;
		case 3:
			return this.anaglyph;
		case 4:
			return this.advancedOpengl;
		case 5:
			return this.clouds;
		case 6:
			return this.chatColours;
		case 7:
			return this.chatLinks;
		case 8:
			return this.chatLinksPrompt;
		case 9:
			return this.serverTextures;
		case 10:
			return this.snooperEnabled;
		case 11:
			return this.fullScreen;
		case 12:
			return this.enableVsync;
		case 13:
			return this.showCape;
		case 14:
			return this.touchscreen;
		default:
			return false;
		}
	}

	private static String getTranslation(String[] var0, int var1) {
		if(var1 < 0 || var1 >= var0.length) {
			var1 = 0;
		}

		StringTranslate var2 = StringTranslate.getInstance();
		return var2.translateKey(var0[var1]);
	}

	public String getKeyBinding(EnumOptions var1) {
		StringTranslate var2 = StringTranslate.getInstance();
		String var3 = var2.translateKey(var1.getEnumString()) + ": ";
		if(var1.getEnumFloat()) {
			float var5 = this.getOptionFloatValue(var1);
			return var1 == EnumOptions.SENSITIVITY ? (var5 == 0.0F ? var3 + var2.translateKey("options.sensitivity.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.sensitivity.max") : var3 + (int)(var5 * 200.0F) + "%")) : (var1 == EnumOptions.FOV ? (var5 == 0.0F ? var3 + var2.translateKey("options.fov.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.fov.max") : var3 + (int)(70.0F + var5 * 40.0F))) : (var1 == EnumOptions.GAMMA ? (var5 == 0.0F ? var3 + var2.translateKey("options.gamma.min") : (var5 == 1.0F ? var3 + var2.translateKey("options.gamma.max") : var3 + "+" + (int)(var5 * 100.0F) + "%")) : (var1 == EnumOptions.CHAT_OPACITY ? var3 + (int)(var5 * 90.0F + 10.0F) + "%" : (var1 == EnumOptions.CHAT_HEIGHT_UNFOCUSED ? var3 + GuiNewChat.func_96130_b(var5) + "px" : (var1 == EnumOptions.CHAT_HEIGHT_FOCUSED ? var3 + GuiNewChat.func_96130_b(var5) + "px" : (var1 == EnumOptions.CHAT_WIDTH ? var3 + GuiNewChat.func_96128_a(var5) + "px" : (var5 == 0.0F ? var3 + var2.translateKey("options.off") : var3 + (int)(var5 * 100.0F) + "%")))))));
		} else if(var1.getEnumBoolean()) {
			boolean var4 = this.getOptionOrdinalValue(var1);
			return var4 ? var3 + var2.translateKey("options.on") : var3 + var2.translateKey("options.off");
		} else {
			return var1 == EnumOptions.RENDER_DISTANCE ? var3 + getTranslation(RENDER_DISTANCES, this.renderDistance) : (var1 == EnumOptions.DIFFICULTY ? var3 + getTranslation(DIFFICULTIES, this.difficulty) : (var1 == EnumOptions.GUI_SCALE ? var3 + getTranslation(GUISCALES, this.guiScale) : (var1 == EnumOptions.CHAT_VISIBILITY ? var3 + getTranslation(CHAT_VISIBILITIES, this.chatVisibility) : (var1 == EnumOptions.PARTICLES ? var3 + getTranslation(PARTICLES, this.particleSetting) : (var1 == EnumOptions.FRAMERATE_LIMIT ? var3 + getTranslation(LIMIT_FRAMERATES, this.limitFramerate) : (var1 == EnumOptions.AMBIENT_OCCLUSION ? var3 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion) : (var1 == EnumOptions.GRAPHICS ? (this.fancyGraphics ? var3 + var2.translateKey("options.graphics.fancy") : var3 + var2.translateKey("options.graphics.fast")) : var3)))))));
		}
	}

	public void loadOptions() {
		try {
			if(!this.optionsFile.exists()) {
				return;
			}

			BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));
			String var2 = "";

			while(true) {
				var2 = var1.readLine();
				if(var2 == null) {
					KeyBinding.resetKeyBindingArrayAndHash();
					var1.close();
					break;
				}

				try {
					String[] var3 = var2.split(":");
					if(var3[0].equals("music")) {
						this.musicVolume = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("sound")) {
						this.soundVolume = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("mouseSensitivity")) {
						this.mouseSensitivity = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("fov")) {
						this.fovSetting = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("gamma")) {
						this.gammaSetting = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("invertYMouse")) {
						this.invertMouse = var3[1].equals("true");
					}

					if(var3[0].equals("viewDistance")) {
						this.renderDistance = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("guiScale")) {
						this.guiScale = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("particles")) {
						this.particleSetting = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("bobView")) {
						this.viewBobbing = var3[1].equals("true");
					}

					if(var3[0].equals("anaglyph3d")) {
						this.anaglyph = var3[1].equals("true");
					}

					if(var3[0].equals("advancedOpengl")) {
						this.advancedOpengl = var3[1].equals("true");
					}

					if(var3[0].equals("fpsLimit")) {
						this.limitFramerate = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("difficulty")) {
						this.difficulty = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("fancyGraphics")) {
						this.fancyGraphics = var3[1].equals("true");
					}

					if(var3[0].equals("ao")) {
						if(var3[1].equals("true")) {
							this.ambientOcclusion = 2;
						} else if(var3[1].equals("false")) {
							this.ambientOcclusion = 0;
						} else {
							this.ambientOcclusion = Integer.parseInt(var3[1]);
						}
					}

					if(var3[0].equals("clouds")) {
						this.clouds = var3[1].equals("true");
					}

					if(var3[0].equals("skin")) {
						this.skin = var3[1];
					}

					if(var3[0].equals("lastServer") && var3.length >= 2) {
						this.lastServer = var3[1];
					}

					if(var3[0].equals("lang") && var3.length >= 2) {
						this.language = var3[1];
					}

					if(var3[0].equals("chatVisibility")) {
						this.chatVisibility = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("chatColors")) {
						this.chatColours = var3[1].equals("true");
					}

					if(var3[0].equals("chatLinks")) {
						this.chatLinks = var3[1].equals("true");
					}

					if(var3[0].equals("chatLinksPrompt")) {
						this.chatLinksPrompt = var3[1].equals("true");
					}

					if(var3[0].equals("chatOpacity")) {
						this.chatOpacity = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("serverTextures")) {
						this.serverTextures = var3[1].equals("true");
					}

					if(var3[0].equals("snooperEnabled")) {
						this.snooperEnabled = var3[1].equals("true");
					}

					if(var3[0].equals("fullscreen")) {
						this.fullScreen = var3[1].equals("true");
					}

					if(var3[0].equals("enableVsync")) {
						this.enableVsync = var3[1].equals("true");
					}

					if(var3[0].equals("hideServerAddress")) {
						this.hideServerAddress = var3[1].equals("true");
					}

					if(var3[0].equals("advancedItemTooltips")) {
						this.advancedItemTooltips = var3[1].equals("true");
					}

					if(var3[0].equals("pauseOnLostFocus")) {
						this.pauseOnLostFocus = var3[1].equals("true");
					}

					if(var3[0].equals("showCape")) {
						this.showCape = var3[1].equals("true");
					}

					if(var3[0].equals("touchscreen")) {
						this.touchscreen = var3[1].equals("true");
					}

					if(var3[0].equals("overrideHeight")) {
						this.overrideHeight = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("overrideWidth")) {
						this.overrideWidth = Integer.parseInt(var3[1]);
					}

					if(var3[0].equals("heldItemTooltips")) {
						this.heldItemTooltips = var3[1].equals("true");
					}

					if(var3[0].equals("chatHeightFocused")) {
						this.chatHeightFocused = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("chatHeightUnfocused")) {
						this.chatHeightUnfocused = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("chatScale")) {
						this.chatScale = this.parseFloat(var3[1]);
					}

					if(var3[0].equals("chatWidth")) {
						this.chatWidth = this.parseFloat(var3[1]);
					}

					for(int var4 = 0; var4 < this.keyBindings.length; ++var4) {
						if(var3[0].equals("key_" + this.keyBindings[var4].keyDescription)) {
							this.keyBindings[var4].keyCode = Integer.parseInt(var3[1]);
						}
					}
				} catch (Exception var5) {
					this.mc.getLogAgent().logWarning("Skipping bad option: " + var2);
				}
			}
		} catch (Exception var6) {
			this.mc.getLogAgent().logWarning("Failed to load options");
			var6.printStackTrace();
		}

	}

	private float parseFloat(String var1) {
		return var1.equals("true") ? 1.0F : (var1.equals("false") ? 0.0F : Float.parseFloat(var1));
	}

	public void saveOptions() {
		try {
			PrintWriter var1 = new PrintWriter(new FileWriter(this.optionsFile));
			var1.println("music:" + this.musicVolume);
			var1.println("sound:" + this.soundVolume);
			var1.println("invertYMouse:" + this.invertMouse);
			var1.println("mouseSensitivity:" + this.mouseSensitivity);
			var1.println("fov:" + this.fovSetting);
			var1.println("gamma:" + this.gammaSetting);
			var1.println("viewDistance:" + this.renderDistance);
			var1.println("guiScale:" + this.guiScale);
			var1.println("particles:" + this.particleSetting);
			var1.println("bobView:" + this.viewBobbing);
			var1.println("anaglyph3d:" + this.anaglyph);
			var1.println("advancedOpengl:" + this.advancedOpengl);
			var1.println("fpsLimit:" + this.limitFramerate);
			var1.println("difficulty:" + this.difficulty);
			var1.println("fancyGraphics:" + this.fancyGraphics);
			var1.println("ao:" + this.ambientOcclusion);
			var1.println("clouds:" + this.clouds);
			var1.println("skin:" + this.skin);
			var1.println("lastServer:" + this.lastServer);
			var1.println("lang:" + this.language);
			var1.println("chatVisibility:" + this.chatVisibility);
			var1.println("chatColors:" + this.chatColours);
			var1.println("chatLinks:" + this.chatLinks);
			var1.println("chatLinksPrompt:" + this.chatLinksPrompt);
			var1.println("chatOpacity:" + this.chatOpacity);
			var1.println("serverTextures:" + this.serverTextures);
			var1.println("snooperEnabled:" + this.snooperEnabled);
			var1.println("fullscreen:" + this.fullScreen);
			var1.println("enableVsync:" + this.enableVsync);
			var1.println("hideServerAddress:" + this.hideServerAddress);
			var1.println("advancedItemTooltips:" + this.advancedItemTooltips);
			var1.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
			var1.println("showCape:" + this.showCape);
			var1.println("touchscreen:" + this.touchscreen);
			var1.println("overrideWidth:" + this.overrideWidth);
			var1.println("overrideHeight:" + this.overrideHeight);
			var1.println("heldItemTooltips:" + this.heldItemTooltips);
			var1.println("chatHeightFocused:" + this.chatHeightFocused);
			var1.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
			var1.println("chatScale:" + this.chatScale);
			var1.println("chatWidth:" + this.chatWidth);

			for(int var2 = 0; var2 < this.keyBindings.length; ++var2) {
				var1.println("key_" + this.keyBindings[var2].keyDescription + ":" + this.keyBindings[var2].keyCode);
			}

			var1.close();
		} catch (Exception var3) {
			this.mc.getLogAgent().logWarning("Failed to save options");
			var3.printStackTrace();
		}

		this.sendSettingsToServer();
	}

	public void sendSettingsToServer() {
		if(this.mc.thePlayer != null) {
			this.mc.thePlayer.sendQueue.addToSendQueue(new Packet204ClientInfo(this.language, this.renderDistance, this.chatVisibility, this.chatColours, this.difficulty, this.showCape));
		}

	}

	public boolean shouldRenderClouds() {
		return this.renderDistance < 2 && this.clouds;
	}
}
