package net.minecraft.src;

import java.util.Arrays;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerMessage extends CommandBase {
	public List getCommandAliases() {
		return Arrays.asList(new String[]{"w", "msg"});
	}

	public String getCommandName() {
		return "tell";
	}

	public int getRequiredPermissionLevel() {
		return 0;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length < 2) {
			throw new WrongUsageException("commands.message.usage", new Object[0]);
		} else {
			EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
			if(var3 == null) {
				throw new PlayerNotFoundException();
			} else if(var3 == var1) {
				throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
			} else {
				String var4 = func_82361_a(var1, var2, 1, !(var1 instanceof EntityPlayer));
				var3.sendChatToPlayer(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + var3.translateString("commands.message.display.incoming", new Object[]{var1.getCommandSenderName(), var4}));
				var1.sendChatToPlayer(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + var1.translateString("commands.message.display.outgoing", new Object[]{var3.getCommandSenderName(), var4}));
			}
		}
	}

	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 0;
	}
}
