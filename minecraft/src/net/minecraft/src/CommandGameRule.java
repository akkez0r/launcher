package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandGameRule extends CommandBase {
	public String getCommandName() {
		return "gamerule";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.gamerule.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		String var6;
		if(var2.length == 2) {
			var6 = var2[0];
			String var7 = var2[1];
			GameRules var8 = this.getGameRules();
			if(var8.hasRule(var6)) {
				var8.setOrCreateGameRule(var6, var7);
				notifyAdmins(var1, "commands.gamerule.success", new Object[0]);
			} else {
				notifyAdmins(var1, "commands.gamerule.norule", new Object[]{var6});
			}

		} else if(var2.length == 1) {
			var6 = var2[0];
			GameRules var4 = this.getGameRules();
			if(var4.hasRule(var6)) {
				String var5 = var4.getGameRuleStringValue(var6);
				var1.sendChatToPlayer(var6 + " = " + var5);
			} else {
				notifyAdmins(var1, "commands.gamerule.norule", new Object[]{var6});
			}

		} else if(var2.length == 0) {
			GameRules var3 = this.getGameRules();
			var1.sendChatToPlayer(joinNiceString(var3.getRules()));
		} else {
			throw new WrongUsageException("commands.gamerule.usage", new Object[0]);
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, this.getGameRules().getRules()) : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, new String[]{"true", "false"}) : null);
	}

	private GameRules getGameRules() {
		return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
	}
}
