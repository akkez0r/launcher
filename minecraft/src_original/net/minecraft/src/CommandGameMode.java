package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandGameMode extends CommandBase {
	public String getCommandName() {
		return "gamemode";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.gamemode.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length > 0) {
			EnumGameType var3 = this.getGameModeFromCommand(var1, var2[0]);
			EntityPlayerMP var4 = var2.length >= 2 ? func_82359_c(var1, var2[1]) : getCommandSenderAsPlayer(var1);
			var4.setGameType(var3);
			var4.fallDistance = 0.0F;
			String var5 = StatCollector.translateToLocal("gameMode." + var3.getName());
			if(var4 != var1) {
				notifyAdmins(var1, 1, "commands.gamemode.success.other", new Object[]{var4.getEntityName(), var5});
			} else {
				notifyAdmins(var1, 1, "commands.gamemode.success.self", new Object[]{var5});
			}

		} else {
			throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
		}
	}

	protected EnumGameType getGameModeFromCommand(ICommandSender var1, String var2) {
		return !var2.equalsIgnoreCase(EnumGameType.SURVIVAL.getName()) && !var2.equalsIgnoreCase("s") ? (!var2.equalsIgnoreCase(EnumGameType.CREATIVE.getName()) && !var2.equalsIgnoreCase("c") ? (!var2.equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) && !var2.equalsIgnoreCase("a") ? WorldSettings.getGameTypeById(parseIntBounded(var1, var2, 0, EnumGameType.values().length - 2)) : EnumGameType.ADVENTURE) : EnumGameType.CREATIVE) : EnumGameType.SURVIVAL;
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, new String[]{"survival", "creative", "adventure"}) : (var2.length == 2 ? getListOfStringsMatchingLastWord(var2, this.getListOfPlayerUsernames()) : null);
	}

	protected String[] getListOfPlayerUsernames() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;
	}
}
