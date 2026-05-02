package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandEnchant extends CommandBase {
	public String getCommandName() {
		return "enchant";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public String getCommandUsage(ICommandSender var1) {
		return var1.translateString("commands.enchant.usage", new Object[0]);
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length < 2) {
			throw new WrongUsageException("commands.enchant.usage", new Object[0]);
		} else {
			EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
			int var4 = parseIntBounded(var1, var2[1], 0, Enchantment.enchantmentsList.length - 1);
			int var5 = 1;
			ItemStack var6 = var3.getCurrentEquippedItem();
			if(var6 == null) {
				notifyAdmins(var1, "commands.enchant.noItem", new Object[0]);
			} else {
				Enchantment var7 = Enchantment.enchantmentsList[var4];
				if(var7 == null) {
					throw new NumberInvalidException("commands.enchant.notFound", new Object[]{Integer.valueOf(var4)});
				} else if(!var7.canApply(var6)) {
					notifyAdmins(var1, "commands.enchant.cantEnchant", new Object[0]);
				} else {
					if(var2.length >= 3) {
						var5 = parseIntBounded(var1, var2[2], var7.getMinLevel(), var7.getMaxLevel());
					}

					if(var6.hasTagCompound()) {
						NBTTagList var8 = var6.getEnchantmentTagList();
						if(var8 != null) {
							for(int var9 = 0; var9 < var8.tagCount(); ++var9) {
								short var10 = ((NBTTagCompound)var8.tagAt(var9)).getShort("id");
								if(Enchantment.enchantmentsList[var10] != null) {
									Enchantment var11 = Enchantment.enchantmentsList[var10];
									if(!var11.canApplyTogether(var7)) {
										notifyAdmins(var1, "commands.enchant.cantCombine", new Object[]{var7.getTranslatedName(var5), var11.getTranslatedName(((NBTTagCompound)var8.tagAt(var9)).getShort("lvl"))});
										return;
									}
								}
							}
						}
					}

					var6.addEnchantment(var7, var5);
					notifyAdmins(var1, "commands.enchant.success", new Object[0]);
				}
			}
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, this.getListOfPlayers()) : null;
	}

	protected String[] getListOfPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 0;
	}
}
