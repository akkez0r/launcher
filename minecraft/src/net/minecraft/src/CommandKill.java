package net.minecraft.src;

public class CommandKill extends CommandBase {
	public String getCommandName() {
		return "kill";
	}

	public int getRequiredPermissionLevel() {
		return 0;
	}

	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayerMP var3 = getCommandSenderAsPlayer(var1);
		var3.attackEntityFrom(DamageSource.outOfWorld, 1000);
		var1.sendChatToPlayer("Ouch. That looks like it hurt.");
	}
}
