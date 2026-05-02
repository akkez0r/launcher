package net.minecraft.src;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui {
	public static final boolean isMacOs = Minecraft.getOs() == EnumOS.MACOS;
	protected Minecraft mc;
	public int width;
	public int height;
	protected List buttonList = new ArrayList();
	public boolean allowUserInput = false;
	protected FontRenderer fontRenderer;
	public GuiParticle guiParticles;
	private GuiButton selectedButton = null;
	private int eventButton = 0;
	private long field_85043_c = 0L;
	private int field_92018_d = 0;

	public void drawScreen(int var1, int var2, float var3) {
		for(int var4 = 0; var4 < this.buttonList.size(); ++var4) {
			GuiButton var5 = (GuiButton)this.buttonList.get(var4);
			var5.drawButton(this.mc, var1, var2);
		}

	}

	protected void keyTyped(char var1, int var2) {
		if(var2 == 1) {
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}

	}

	public static String getClipboardString() {
		try {
			Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
			if(var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return (String)var0.getTransferData(DataFlavor.stringFlavor);
			}
		} catch (Exception var1) {
		}

		return "";
	}

	public static void setClipboardString(String var0) {
		try {
			StringSelection var1 = new StringSelection(var0);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, (ClipboardOwner)null);
		} catch (Exception var2) {
		}

	}

	protected void mouseClicked(int var1, int var2, int var3) {
		if(var3 == 0) {
			for(int var4 = 0; var4 < this.buttonList.size(); ++var4) {
				GuiButton var5 = (GuiButton)this.buttonList.get(var4);
				if(var5.mousePressed(this.mc, var1, var2)) {
					this.selectedButton = var5;
					this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
					this.actionPerformed(var5);
				}
			}
		}

	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		if(this.selectedButton != null && var3 == 0) {
			this.selectedButton.mouseReleased(var1, var2);
			this.selectedButton = null;
		}

	}

	protected void func_85041_a(int var1, int var2, int var3, long var4) {
	}

	protected void actionPerformed(GuiButton var1) {
	}

	public void setWorldAndResolution(Minecraft var1, int var2, int var3) {
		this.guiParticles = new GuiParticle(var1);
		this.mc = var1;
		this.fontRenderer = var1.fontRenderer;
		this.width = var2;
		this.height = var3;
		this.buttonList.clear();
		this.initGui();
	}

	public void initGui() {
	}

	public void handleInput() {
		while(Mouse.next()) {
			this.handleMouseInput();
		}

		while(Keyboard.next()) {
			this.handleKeyboardInput();
		}

	}

	public void handleMouseInput() {
		int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if(Mouse.getEventButtonState()) {
			if(this.mc.gameSettings.touchscreen && this.field_92018_d++ > 0) {
				return;
			}

			this.eventButton = Mouse.getEventButton();
			this.field_85043_c = Minecraft.getSystemTime();
			this.mouseClicked(var1, var2, this.eventButton);
		} else if(Mouse.getEventButton() != -1) {
			if(this.mc.gameSettings.touchscreen && --this.field_92018_d > 0) {
				return;
			}

			this.eventButton = -1;
			this.mouseMovedOrUp(var1, var2, Mouse.getEventButton());
		} else if(this.eventButton != -1 && this.field_85043_c > 0L) {
			long var3 = Minecraft.getSystemTime() - this.field_85043_c;
			this.func_85041_a(var1, var2, this.eventButton, var3);
		}

	}

	public void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()) {
			int var1 = Keyboard.getEventKey();
			char var2 = Keyboard.getEventCharacter();
			if(var1 == 87) {
				this.mc.toggleFullscreen();
				return;
			}

			if(isMacOs && var1 == 28 && var2 == 0) {
				var1 = 29;
			}

			this.keyTyped(var2, var1);
		}

	}

	public void updateScreen() {
	}

	public void onGuiClosed() {
	}

	public void drawDefaultBackground() {
		this.drawWorldBackground(0);
	}

	public void drawWorldBackground(int var1) {
		if(this.mc.theWorld != null) {
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.drawBackground(var1);
		}

	}

	public void drawBackground(int var1) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator var2 = Tessellator.instance;
		this.mc.renderEngine.bindTexture("/gui/background.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var3 = 32.0F;
		var2.startDrawingQuads();
		var2.setColorOpaque_I(4210752);
		var2.addVertexWithUV(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / var3 + (float)var1));
		var2.addVertexWithUV((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / var3), (double)((float)this.height / var3 + (float)var1));
		var2.addVertexWithUV((double)this.width, 0.0D, 0.0D, (double)((float)this.width / var3), (double)var1);
		var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)var1);
		var2.draw();
	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	public void confirmClicked(boolean var1, int var2) {
	}

	public static boolean isCtrlKeyDown() {
		boolean var0 = Keyboard.isKeyDown(Keyboard.KEY_RETURN) && Keyboard.getEventCharacter() == 0;
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || isMacOs && (var0 || Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA));
	}

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
