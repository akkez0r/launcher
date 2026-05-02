package net.minecraft.src;

class ThreadOnlineScreen extends Thread {
	final GuiScreenOnlineServers field_98173_a;

	ThreadOnlineScreen(GuiScreenOnlineServers var1) {
		this.field_98173_a = var1;
	}

	public void run() {
		McoClient var1 = new McoClient(GuiScreenOnlineServers.func_96177_a(this.field_98173_a).session);

		try {
			GuiScreenOnlineServers.func_98081_a(this.field_98173_a, var1.func_96379_c());
		} catch (Exception var3) {
			GuiScreenOnlineServers.func_98081_a(this.field_98173_a, 0);
		}

	}
}
