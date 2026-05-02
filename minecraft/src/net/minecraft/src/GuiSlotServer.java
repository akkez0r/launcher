package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

class GuiSlotServer extends GuiSlot {
	final GuiMultiplayer parentGui;

	public GuiSlotServer(GuiMultiplayer var1) {
		super(var1.mc, var1.width, var1.height, 32, var1.height - 64, 36);
		this.parentGui = var1;
	}

	protected int getSize() {
		return GuiMultiplayer.getInternetServerList(this.parentGui).countServers() + GuiMultiplayer.getListOfLanServers(this.parentGui).size() + 1;
	}

	protected void elementClicked(int var1, boolean var2) {
		if(var1 < GuiMultiplayer.getInternetServerList(this.parentGui).countServers() + GuiMultiplayer.getListOfLanServers(this.parentGui).size()) {
			int var3 = GuiMultiplayer.getSelectedServer(this.parentGui);
			GuiMultiplayer.getAndSetSelectedServer(this.parentGui, var1);
			ServerData var4 = GuiMultiplayer.getInternetServerList(this.parentGui).countServers() > var1 ? GuiMultiplayer.getInternetServerList(this.parentGui).getServerData(var1) : null;
			boolean var5 = GuiMultiplayer.getSelectedServer(this.parentGui) >= 0 && GuiMultiplayer.getSelectedServer(this.parentGui) < this.getSize() && (var4 == null || var4.field_82821_f == 61);
			boolean var6 = GuiMultiplayer.getSelectedServer(this.parentGui) < GuiMultiplayer.getInternetServerList(this.parentGui).countServers();
			GuiMultiplayer.getButtonSelect(this.parentGui).enabled = var5;
			GuiMultiplayer.getButtonEdit(this.parentGui).enabled = var6;
			GuiMultiplayer.getButtonDelete(this.parentGui).enabled = var6;
			if(var2 && var5) {
				GuiMultiplayer.func_74008_b(this.parentGui, var1);
			} else if(var6 && GuiScreen.isShiftKeyDown() && var3 >= 0 && var3 < GuiMultiplayer.getInternetServerList(this.parentGui).countServers()) {
				GuiMultiplayer.getInternetServerList(this.parentGui).swapServers(var3, GuiMultiplayer.getSelectedServer(this.parentGui));
			}

		}
	}

	protected boolean isSelected(int var1) {
		return var1 == GuiMultiplayer.getSelectedServer(this.parentGui);
	}

	protected int getContentHeight() {
		return this.getSize() * 36;
	}

	protected void drawBackground() {
		this.parentGui.drawDefaultBackground();
	}

	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		if(var1 < GuiMultiplayer.getInternetServerList(this.parentGui).countServers()) {
			this.func_77247_d(var1, var2, var3, var4, var5);
		} else if(var1 < GuiMultiplayer.getInternetServerList(this.parentGui).countServers() + GuiMultiplayer.getListOfLanServers(this.parentGui).size()) {
			this.func_77248_b(var1, var2, var3, var4, var5);
		} else {
			this.func_77249_c(var1, var2, var3, var4, var5);
		}

	}

	private void func_77248_b(int var1, int var2, int var3, int var4, Tessellator var5) {
		LanServer var6 = (LanServer)GuiMultiplayer.getListOfLanServers(this.parentGui).get(var1 - GuiMultiplayer.getInternetServerList(this.parentGui).countServers());
		this.parentGui.drawString(this.parentGui.fontRenderer, StatCollector.translateToLocal("lanServer.title"), var2 + 2, var3 + 1, 16777215);
		this.parentGui.drawString(this.parentGui.fontRenderer, var6.getServerMotd(), var2 + 2, var3 + 12, 8421504);
		if(this.parentGui.mc.gameSettings.hideServerAddress) {
			this.parentGui.drawString(this.parentGui.fontRenderer, StatCollector.translateToLocal("selectServer.hiddenAddress"), var2 + 2, var3 + 12 + 11, 3158064);
		} else {
			this.parentGui.drawString(this.parentGui.fontRenderer, var6.getServerIpPort(), var2 + 2, var3 + 12 + 11, 3158064);
		}

	}

	private void func_77249_c(int var1, int var2, int var3, int var4, Tessellator var5) {
		this.parentGui.drawCenteredString(this.parentGui.fontRenderer, StatCollector.translateToLocal("lanServer.scanning"), this.parentGui.width / 2, var3 + 1, 16777215);
		String var6;
		switch(GuiMultiplayer.getTicksOpened(this.parentGui) / 3 % 4) {
		case 0:
		default:
			var6 = "O o o";
			break;
		case 1:
		case 3:
			var6 = "o O o";
			break;
		case 2:
			var6 = "o o O";
		}

		this.parentGui.drawCenteredString(this.parentGui.fontRenderer, var6, this.parentGui.width / 2, var3 + 12, 8421504);
	}

	private void func_77247_d(int var1, int var2, int var3, int var4, Tessellator var5) {
		ServerData var6 = GuiMultiplayer.getInternetServerList(this.parentGui).getServerData(var1);
		Object var7 = GuiMultiplayer.getLock();
		synchronized(var7) {
			if(GuiMultiplayer.getThreadsPending() < 5 && !var6.field_78841_f) {
				var6.field_78841_f = true;
				var6.pingToServer = -2L;
				var6.serverMOTD = "";
				var6.populationInfo = "";
				GuiMultiplayer.increaseThreadsPending();
				(new ThreadPollServers(this, var6)).start();
			}
		}

		boolean var15 = var6.field_82821_f > 61;
		boolean var8 = var6.field_82821_f < 61;
		boolean var9 = var15 || var8;
		this.parentGui.drawString(this.parentGui.fontRenderer, var6.serverName, var2 + 2, var3 + 1, 16777215);
		this.parentGui.drawString(this.parentGui.fontRenderer, var6.serverMOTD, var2 + 2, var3 + 12, 8421504);
		this.parentGui.drawString(this.parentGui.fontRenderer, var6.populationInfo, var2 + 215 - this.parentGui.fontRenderer.getStringWidth(var6.populationInfo), var3 + 12, 8421504);
		if(var9) {
			String var10 = EnumChatFormatting.DARK_RED + var6.gameVersion;
			this.parentGui.drawString(this.parentGui.fontRenderer, var10, var2 + 200 - this.parentGui.fontRenderer.getStringWidth(var10), var3 + 1, 8421504);
		}

		if(!this.parentGui.mc.gameSettings.hideServerAddress && !var6.isHidingAddress()) {
			this.parentGui.drawString(this.parentGui.fontRenderer, var6.serverIP, var2 + 2, var3 + 12 + 11, 3158064);
		} else {
			this.parentGui.drawString(this.parentGui.fontRenderer, StatCollector.translateToLocal("selectServer.hiddenAddress"), var2 + 2, var3 + 12 + 11, 3158064);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.parentGui.mc.renderEngine.bindTexture("/gui/icons.png");
		byte var16 = 0;
		boolean var11 = false;
		String var12 = "";
		int var17;
		if(var9) {
			var12 = var15 ? "Client out of date!" : "Server out of date!";
			var17 = 5;
		} else if(var6.field_78841_f && var6.pingToServer != -2L) {
			if(var6.pingToServer < 0L) {
				var17 = 5;
			} else if(var6.pingToServer < 150L) {
				var17 = 0;
			} else if(var6.pingToServer < 300L) {
				var17 = 1;
			} else if(var6.pingToServer < 600L) {
				var17 = 2;
			} else if(var6.pingToServer < 1000L) {
				var17 = 3;
			} else {
				var17 = 4;
			}

			if(var6.pingToServer < 0L) {
				var12 = "(no connection)";
			} else {
				var12 = var6.pingToServer + "ms";
			}
		} else {
			var16 = 1;
			var17 = (int)(Minecraft.getSystemTime() / 100L + (long)(var1 * 2) & 7L);
			if(var17 > 4) {
				var17 = 8 - var17;
			}

			var12 = "Polling..";
		}

		this.parentGui.drawTexturedModalRect(var2 + 205, var3, 0 + var16 * 10, 176 + var17 * 8, 10, 8);
		byte var13 = 4;
		if(this.mouseX >= var2 + 205 - var13 && this.mouseY >= var3 - var13 && this.mouseX <= var2 + 205 + 10 + var13 && this.mouseY <= var3 + 8 + var13) {
			GuiMultiplayer.getAndSetLagTooltip(this.parentGui, var12);
		}

	}
}
