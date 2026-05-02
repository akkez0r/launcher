package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class CommandDefaultGameMode extends CommandGameMode {
	public String getCommandName() {
		return "defaultgamemode";
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.defaultgamemode.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length > 0) {
			EnumGameType var3 = this.getGameModeFromCommand(var1, var2[0]);
			this.setGameType(var3);
			String var4 = StatCollector.translateToLocal("gameMode." + var3.getName());
			notifyAdmins(var1, "commands.defaultgamemode.success", new Object[]{var4});
		} else {
			throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
		}
	}

	protected void setGameType(EnumGameType var1) {
		MinecraftServer.getServer().setGameType(var1);
	}
}
