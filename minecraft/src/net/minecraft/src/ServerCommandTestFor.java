package net.minecraft.src;

public class ServerCommandTestFor extends CommandBase {
	public String getCommandName() {
		return "testfor";
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length != 1) {
			throw new WrongUsageException("commands.testfor.usage", new Object[0]);
		} else if(!(var1 instanceof TileEntityCommandBlock)) {
			throw new CommandException("commands.testfor.failed", new Object[0]);
		} else {
			func_82359_c(var1, var2[0]);
		}
	}

	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 0;
	}
}
