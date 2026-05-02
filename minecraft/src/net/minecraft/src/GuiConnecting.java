package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class GuiConnecting extends GuiScreen {
	private NetClientHandler clientHandler;
	private boolean cancelled = false;
	private final GuiScreen field_98098_c;

	public GuiConnecting(GuiScreen var1, Minecraft var2, ServerData var3) {
		this.mc = var2;
		this.field_98098_c = var1;
		ServerAddress var4 = ServerAddress.func_78860_a(var3.serverIP);
		var2.loadWorld((WorldClient)null);
		var2.setServerData(var3);
		this.spawnNewServerThread(var4.getIP(), var4.getPort());
	}

	public GuiConnecting(GuiScreen var1, Minecraft var2, String var3, int var4) {
		this.mc = var2;
		this.field_98098_c = var1;
		var2.loadWorld((WorldClient)null);
		this.spawnNewServerThread(var3, var4);
	}

	private void spawnNewServerThread(String var1, int var2) {
		this.mc.getLogAgent().logInfo("Connecting to " + var1 + ", " + var2);
		(new ThreadConnectToServer(this, var1, var2)).start();
	}

	public void updateScreen() {
		if(this.clientHandler != null) {
			this.clientHandler.processReadPackets();
		}

	}

	protected void keyTyped(char var1, int var2) {
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.cancelled = true;
			if(this.clientHandler != null) {
				this.clientHandler.disconnect();
			}

			this.mc.displayGuiScreen(this.field_98098_c);
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		StringTranslate var4 = StringTranslate.getInstance();
		if(this.clientHandler == null) {
			this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
		} else {
			this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, this.clientHandler.field_72560_a, this.width / 2, this.height / 2 - 10, 16777215);
		}

		super.drawScreen(var1, var2, var3);
	}

	static NetClientHandler setNetClientHandler(GuiConnecting var0, NetClientHandler var1) {
		return var0.clientHandler = var1;
	}

	static Minecraft func_74256_a(GuiConnecting var0) {
		return var0.mc;
	}

	static boolean isCancelled(GuiConnecting var0) {
		return var0.cancelled;
	}

	static Minecraft func_74254_c(GuiConnecting var0) {
		return var0.mc;
	}

	static NetClientHandler getNetClientHandler(GuiConnecting var0) {
		return var0.clientHandler;
	}

	static GuiScreen func_98097_e(GuiConnecting var0) {
		return var0.field_98098_c;
	}

	static Minecraft func_74250_f(GuiConnecting var0) {
		return var0.mc;
	}

	static Minecraft func_74251_g(GuiConnecting var0) {
		return var0.mc;
	}

	static Minecraft func_98096_h(GuiConnecting var0) {
		return var0.mc;
	}
}
