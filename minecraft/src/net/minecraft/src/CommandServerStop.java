package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class CommandServerStop extends CommandBase {
	public String getCommandName() {
		return "stop";
	}

	public int getRequiredPermissionLevel() {
		return 4;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		notifyAdmins(var1, "commands.stop.start", new Object[0]);
		MinecraftServer.getServer().initiateShutdown();
	}
}
