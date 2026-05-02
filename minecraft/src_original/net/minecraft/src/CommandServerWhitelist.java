package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerWhitelist extends CommandBase {
	public String getCommandName() {
		return "whitelist";
	}

	public int getRequiredPermissionLevel() {
		return 3;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.whitelist.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length >= 1) {
			if(var2[0].equals("on")) {
				MinecraftServer.getServer().getConfigurationManager().setWhiteListEnabled(true);
				notifyAdmins(var1, "commands.whitelist.enabled", new Object[0]);
				return;
			}

			if(var2[0].equals("off")) {
				MinecraftServer.getServer().getConfigurationManager().setWhiteListEnabled(false);
				notifyAdmins(var1, "commands.whitelist.disabled", new Object[0]);
				return;
			}

			if(var2[0].equals("list")) {
				var1.sendChatToPlayer(var1.translateString("commands.whitelist.list", new Object[]{Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getWhiteListedPlayers().size()), Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat().length)}));
				var1.sendChatToPlayer(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getWhiteListedPlayers().toArray(new String[0])));
				return;
			}

			if(var2[0].equals("add")) {
				if(var2.length < 2) {
					throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
				}

				MinecraftServer.getServer().getConfigurationManager().addToWhiteList(var2[1]);
				notifyAdmins(var1, "commands.whitelist.add.success", new Object[]{var2[1]});
				return;
			}

			if(var2[0].equals("remove")) {
				if(var2.length < 2) {
					throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
				}

				MinecraftServer.getServer().getConfigurationManager().removeFromWhitelist(var2[1]);
				notifyAdmins(var1, "commands.whitelist.remove.success", new Object[]{var2[1]});
				return;
			}

			if(var2[0].equals("reload")) {
				MinecraftServer.getServer().getConfigurationManager().loadWhiteList();
				notifyAdmins(var1, "commands.whitelist.reloaded", new Object[0]);
				return;
			}
		}

		throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		if(var2.length == 1) {
			return getListOfStringsMatchingLastWord(var2, new String[]{"on", "off", "list", "add", "remove", "reload"});
		} else {
			if(var2.length == 2) {
				if(var2[0].equals("add")) {
					String[] var3 = MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat();
					ArrayList var4 = new ArrayList();
					String var5 = var2[var2.length - 1];
					String[] var6 = var3;
					int var7 = var3.length;

					for(int var8 = 0; var8 < var7; ++var8) {
						String var9 = var6[var8];
						if(doesStringStartWith(var5, var9) && !MinecraftServer.getServer().getConfigurationManager().getWhiteListedPlayers().contains(var9)) {
							var4.add(var9);
						}
					}

					return var4;
				}

				if(var2[0].equals("remove")) {
					return getListOfStringsFromIterableMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getWhiteListedPlayers());
				}
			}

			return null;
		}
	}
}
