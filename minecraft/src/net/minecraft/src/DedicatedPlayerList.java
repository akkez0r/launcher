package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import net.minecraft.server.MinecraftServer;

public class DedicatedPlayerList extends ServerConfigurationManager {
	private File opsList;
	private File whiteList;

	public DedicatedPlayerList(DedicatedServer var1) {
		super(var1);
		this.opsList = var1.getFile("ops.txt");
		this.whiteList = var1.getFile("white-list.txt");
		this.viewDistance = var1.getIntProperty("view-distance", 10);
		this.maxPlayers = var1.getIntProperty("max-players", 20);
		this.setWhiteListEnabled(var1.getBooleanProperty("white-list", false));
		if(!var1.isSinglePlayer()) {
			this.getBannedPlayers().setListActive(true);
			this.getBannedIPs().setListActive(true);
		}

		this.getBannedPlayers().loadBanList();
		this.getBannedPlayers().saveToFileWithHeader();
		this.getBannedIPs().loadBanList();
		this.getBannedIPs().saveToFileWithHeader();
		this.loadOpsList();
		this.readWhiteList();
		this.saveOpsList();
		if(!this.whiteList.exists()) {
			this.saveWhiteList();
		}

	}

	public void setWhiteListEnabled(boolean var1) {
		super.setWhiteListEnabled(var1);
		this.getDedicatedServerInstance().setProperty("white-list", Boolean.valueOf(var1));
		this.getDedicatedServerInstance().saveProperties();
	}

	public void addOp(String var1) {
		super.addOp(var1);
		this.saveOpsList();
	}

	public void removeOp(String var1) {
		super.removeOp(var1);
		this.saveOpsList();
	}

	public void removeFromWhitelist(String var1) {
		super.removeFromWhitelist(var1);
		this.saveWhiteList();
	}

	public void addToWhiteList(String var1) {
		super.addToWhiteList(var1);
		this.saveWhiteList();
	}

	public void loadWhiteList() {
		this.readWhiteList();
	}

	private void loadOpsList() {
		try {
			this.getOps().clear();
			BufferedReader var1 = new BufferedReader(new FileReader(this.opsList));
			String var2 = "";

			while(true) {
				var2 = var1.readLine();
				if(var2 == null) {
					var1.close();
					break;
				}

				this.getOps().add(var2.trim().toLowerCase());
			}
		} catch (Exception var3) {
			this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to load operators list: " + var3);
		}

	}

	private void saveOpsList() {
		try {
			PrintWriter var1 = new PrintWriter(new FileWriter(this.opsList, false));
			Iterator var2 = this.getOps().iterator();

			while(var2.hasNext()) {
				String var3 = (String)var2.next();
				var1.println(var3);
			}

			var1.close();
		} catch (Exception var4) {
			this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to save operators list: " + var4);
		}

	}

	private void readWhiteList() {
		try {
			this.getWhiteListedPlayers().clear();
			BufferedReader var1 = new BufferedReader(new FileReader(this.whiteList));
			String var2 = "";

			while(true) {
				var2 = var1.readLine();
				if(var2 == null) {
					var1.close();
					break;
				}

				this.getWhiteListedPlayers().add(var2.trim().toLowerCase());
			}
		} catch (Exception var3) {
			this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to load white-list: " + var3);
		}

	}

	private void saveWhiteList() {
		try {
			PrintWriter var1 = new PrintWriter(new FileWriter(this.whiteList, false));
			Iterator var2 = this.getWhiteListedPlayers().iterator();

			while(var2.hasNext()) {
				String var3 = (String)var2.next();
				var1.println(var3);
			}

			var1.close();
		} catch (Exception var4) {
			this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to save white-list: " + var4);
		}

	}

	public boolean isAllowedToLogin(String var1) {
		var1 = var1.trim().toLowerCase();
		return !this.isWhiteListEnabled() || this.areCommandsAllowed(var1) || this.getWhiteListedPlayers().contains(var1);
	}

	public DedicatedServer getDedicatedServerInstance() {
		return (DedicatedServer)super.getServerInstance();
	}

	public MinecraftServer getServerInstance() {
		return this.getDedicatedServerInstance();
	}
}
