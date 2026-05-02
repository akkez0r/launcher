package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandEffect extends CommandBase {
	public String getCommandName() {
		return "effect";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.effect.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length >= 2) {
			EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
			int var4 = parseIntWithMin(var1, var2[1], 1);
			int var5 = 600;
			int var6 = 30;
			int var7 = 0;
			if(var4 >= 0 && var4 < Potion.potionTypes.length && Potion.potionTypes[var4] != null) {
				if(var2.length >= 3) {
					var6 = parseIntBounded(var1, var2[2], 0, 1000000);
					if(Potion.potionTypes[var4].isInstant()) {
						var5 = var6;
					} else {
						var5 = var6 * 20;
					}
				} else if(Potion.potionTypes[var4].isInstant()) {
					var5 = 1;
				}

				if(var2.length >= 4) {
					var7 = parseIntBounded(var1, var2[3], 0, 255);
				}

				if(var6 == 0) {
					if(!var3.isPotionActive(var4)) {
						throw new CommandException("commands.effect.failure.notActive", new Object[]{StatCollector.translateToLocal(Potion.potionTypes[var4].getName()), var3.getEntityName()});
					}

					var3.removePotionEffect(var4);
					notifyAdmins(var1, "commands.effect.success.removed", new Object[]{StatCollector.translateToLocal(Potion.potionTypes[var4].getName()), var3.getEntityName()});
				} else {
					PotionEffect var8 = new PotionEffect(var4, var5, var7);
					var3.addPotionEffect(var8);
					notifyAdmins(var1, "commands.effect.success", new Object[]{StatCollector.translateToLocal(var8.getEffectName()), Integer.valueOf(var4), Integer.valueOf(var7), var3.getEntityName(), Integer.valueOf(var6)});
				}

			} else {
				throw new NumberInvalidException("commands.effect.notFound", new Object[]{Integer.valueOf(var4)});
			}
		} else {
			throw new WrongUsageException("commands.effect.usage", new Object[0]);
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, this.getAllUsernames()) : null;
	}

	protected String[] getAllUsernames() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 0;
	}
}
