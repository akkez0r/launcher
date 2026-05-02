package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandDifficulty extends CommandBase {
	private static final String[] difficulties = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};

	public String getCommandName() {
		return "difficulty";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.difficulty.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length > 0) {
			int var3 = this.getDifficultyForName(var1, var2[0]);
			MinecraftServer.getServer().setDifficultyForAllWorlds(var3);
			String var4 = StatCollector.translateToLocal(difficulties[var3]);
			notifyAdmins(var1, "commands.difficulty.success", new Object[]{var4});
		} else {
			throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
		}
	}

	protected int getDifficultyForName(ICommandSender var1, String var2) {
		return !var2.equalsIgnoreCase("peaceful") && !var2.equalsIgnoreCase("p") ? (!var2.equalsIgnoreCase("easy") && !var2.equalsIgnoreCase("e") ? (!var2.equalsIgnoreCase("normal") && !var2.equalsIgnoreCase("n") ? (!var2.equalsIgnoreCase("hard") && !var2.equalsIgnoreCase("h") ? parseIntBounded(var1, var2, 0, 3) : 3) : 2) : 1) : 0;
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
	}
}
