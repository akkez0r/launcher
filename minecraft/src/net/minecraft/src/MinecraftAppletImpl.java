package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import org.lwjgl.LWJGLException;

public class MinecraftAppletImpl extends Minecraft {
	final MinecraftApplet mainFrame;

	public MinecraftAppletImpl(MinecraftApplet var1, Canvas var2, MinecraftApplet var3, int var4, int var5, boolean var6) {
		super(var2, var3, var4, var5, var6);
		this.mainFrame = var1;
	}

	public void displayCrashReportInternal(CrashReport var1) {
		this.mainFrame.removeAll();
		this.mainFrame.setLayout(new BorderLayout());
		this.mainFrame.add(new PanelCrashReport(var1), "Center");
		this.mainFrame.validate();
	}

	public void startGame() throws LWJGLException {
		this.mcDataDir = getMinecraftDir();
		this.gameSettings = new GameSettings(this, this.mcDataDir);
		if(this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0 && this.mainFrame.getParent() != null && this.mainFrame.getParent().getParent() != null) {
			this.mainFrame.getParent().getParent().setSize(this.gameSettings.overrideWidth, this.gameSettings.overrideHeight);
		}

		super.startGame();
	}
}
