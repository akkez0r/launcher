package net.minecraft.src;

import java.util.Iterator;
import net.minecraft.server.MinecraftServer;

public class ServerCommandManager extends CommandHandler implements IAdminCommand {
	public ServerCommandManager() {
		this.registerCommand(new CommandTime());
		this.registerCommand(new CommandGameMode());
		this.registerCommand(new CommandDifficulty());
		this.registerCommand(new CommandDefaultGameMode());
		this.registerCommand(new CommandKill());
		this.registerCommand(new CommandToggleDownfall());
		this.registerCommand(new CommandWeather());
		this.registerCommand(new CommandXP());
		this.registerCommand(new CommandServerTp());
		this.registerCommand(new CommandGive());
		this.registerCommand(new CommandEffect());
		this.registerCommand(new CommandEnchant());
		this.registerCommand(new CommandServerEmote());
		this.registerCommand(new CommandShowSeed());
		this.registerCommand(new CommandHelp());
		this.registerCommand(new CommandDebug());
		this.registerCommand(new CommandServerMessage());
		this.registerCommand(new CommandServerSay());
		this.registerCommand(new CommandSetSpawnpoint());
		this.registerCommand(new CommandGameRule());
		this.registerCommand(new CommandClearInventory());
		this.registerCommand(new ServerCommandTestFor());
		this.registerCommand(new ServerCommandScoreboard());
		if(MinecraftServer.getServer().isDedicatedServer()) {
			this.registerCommand(new CommandServerOp());
			this.registerCommand(new CommandServerDeop());
			this.registerCommand(new CommandServerStop());
			this.registerCommand(new CommandServerSaveAll());
			this.registerCommand(new CommandServerSaveOff());
			this.registerCommand(new CommandServerSaveOn());
			this.registerCommand(new CommandServerBanIp());
			this.registerCommand(new CommandServerPardonIp());
			this.registerCommand(new CommandServerBan());
			this.registerCommand(new CommandServerBanlist());
			this.registerCommand(new CommandServerPardon());
			this.registerCommand(new CommandServerKick());
			this.registerCommand(new CommandServerList());
			this.registerCommand(new CommandServerWhitelist());
		} else {
			this.registerCommand(new CommandServerPublishLocal());
		}

		CommandBase.setAdminCommander(this);
	}

	public void notifyAdmins(ICommandSender var1, int var2, String var3, Object... var4) {
		boolean var5 = true;
		if(var1 instanceof TileEntityCommandBlock && !MinecraftServer.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput")) {
			var5 = false;
		}

		if(var5) {
			Iterator var6 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();

			while(var6.hasNext()) {
				EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
				if(var7 != var1 && MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(var7.username)) {
					var7.sendChatToPlayer("" + EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "[" + var1.getCommandSenderName() + ": " + var7.translateString(var3, var4) + "]");
				}
			}
		}

		if(var1 != MinecraftServer.getServer()) {
			MinecraftServer.getServer().getLogAgent().logInfo("[" + var1.getCommandSenderName() + ": " + MinecraftServer.getServer().translateString(var3, var4) + "]");
		}

		if((var2 & 1) != 1) {
			var1.sendChatToPlayer(var1.translateString(var3, var4));
		}

	}
}
