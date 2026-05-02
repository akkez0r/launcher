package net.minecraft.src;

import net.minecraft.client.Minecraft;

class NetClientWebTextures extends GuiScreen {
	final String texturePackName;
	final NetClientHandler netClientHandlerWebTextures;

	NetClientWebTextures(NetClientHandler var1, String var2) {
		this.netClientHandlerWebTextures = var1;
		this.texturePackName = var2;
	}

	public void confirmClicked(boolean var1, int var2) {
		this.mc = Minecraft.getMinecraft();
		if(this.mc.getServerData() != null) {
			this.mc.getServerData().setAcceptsTextures(var1);
			ServerList.func_78852_b(this.mc.getServerData());
		}

		if(var1) {
			this.mc.texturePackList.requestDownloadOfTexture(this.texturePackName);
		}

		this.mc.displayGuiScreen((GuiScreen)null);
	}
}
