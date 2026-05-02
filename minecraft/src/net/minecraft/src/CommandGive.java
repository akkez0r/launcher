package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandGive extends CommandBase {
	public String getCommandName() {
		return "give";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.give.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length >= 2) {
			EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
			int var4 = parseIntWithMin(var1, var2[1], 1);
			int var5 = 1;
			int var6 = 0;
			if(Item.itemsList[var4] == null) {
				throw new NumberInvalidException("commands.give.notFound", new Object[]{Integer.valueOf(var4)});
			} else {
				if(var2.length >= 3) {
					var5 = parseIntBounded(var1, var2[2], 1, 64);
				}

				if(var2.length >= 4) {
					var6 = parseInt(var1, var2[3]);
				}

				ItemStack var7 = new ItemStack(var4, var5, var6);
				EntityItem var8 = var3.dropPlayerItem(var7);
				var8.delayBeforeCanPickup = 0;
				notifyAdmins(var1, "commands.give.success", new Object[]{Item.itemsList[var4].func_77653_i(var7), Integer.valueOf(var4), Integer.valueOf(var5), var3.getEntityName()});
			}
		} else {
			throw new WrongUsageException("commands.give.usage", new Object[0]);
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, this.getPlayers()) : null;
	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 0;
	}
}
