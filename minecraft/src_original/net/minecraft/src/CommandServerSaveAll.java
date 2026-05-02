package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class CommandServerSaveAll extends CommandBase {
	public String getCommandName() {
		return "save-all";
	}

	public int getRequiredPermissionLevel() {
		return 4;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		MinecraftServer var3 = MinecraftServer.getServer();
		var1.sendChatToPlayer(var1.translateString("commands.save.start", new Object[0]));
		if(var3.getConfigurationManager() != null) {
			var3.getConfigurationManager().saveAllPlayerData();
		}

		try {
			int var4;
			WorldServer var5;
			boolean var6;
			for(var4 = 0; var4 < var3.worldServers.length; ++var4) {
				if(var3.worldServers[var4] != null) {
					var5 = var3.worldServers[var4];
					var6 = var5.canNotSave;
					var5.canNotSave = false;
					var5.saveAllChunks(true, (IProgressUpdate)null);
					var5.canNotSave = var6;
				}
			}

			if(var2.length > 0 && "flush".equals(var2[0])) {
				var1.sendChatToPlayer(var1.translateString("commands.save.flushStart", new Object[0]));

				for(var4 = 0; var4 < var3.worldServers.length; ++var4) {
					if(var3.worldServers[var4] != null) {
						var5 = var3.worldServers[var4];
						var6 = var5.canNotSave;
						var5.canNotSave = false;
						var5.func_104140_m();
						var5.canNotSave = var6;
					}
				}

				var1.sendChatToPlayer(var1.translateString("commands.save.flushEnd", new Object[0]));
			}
		} catch (MinecraftException var7) {
			notifyAdmins(var1, "commands.save.failed", new Object[]{var7.getMessage()});
			return;
		}

		notifyAdmins(var1, "commands.save.success", new Object[0]);
	}
}
