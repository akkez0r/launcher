package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class CommandToggleDownfall extends CommandBase {
	public String getCommandName() {
		return "toggledownfall";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		this.toggleDownfall();
		notifyAdmins(var1, "commands.downfall.success", new Object[0]);
	}

	protected void toggleDownfall() {
		MinecraftServer.getServer().worldServers[0].toggleRain();
		MinecraftServer.getServer().worldServers[0].getWorldInfo().setThundering(true);
	}
}
