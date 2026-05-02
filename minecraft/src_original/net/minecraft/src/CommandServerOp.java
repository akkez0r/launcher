package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerOp extends CommandBase {
	public String getCommandName() {
		return "op";
	}

	public int getRequiredPermissionLevel() {
		return 3;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.op.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length == 1 && var2[0].length() > 0) {
			MinecraftServer.getServer().getConfigurationManager().addOp(var2[0]);
			notifyAdmins(var1, "commands.op.success", new Object[]{var2[0]});
		} else {
			throw new WrongUsageException("commands.op.usage", new Object[0]);
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		if(var2.length == 1) {
			String var3 = var2[var2.length - 1];
			ArrayList var4 = new ArrayList();
			String[] var5 = MinecraftServer.getServer().getAllUsernames();
			int var6 = var5.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				String var8 = var5[var7];
				if(!MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(var8) && doesStringStartWith(var3, var8)) {
					var4.add(var8);
				}
			}

			return var4;
		} else {
			return null;
		}
	}
}
