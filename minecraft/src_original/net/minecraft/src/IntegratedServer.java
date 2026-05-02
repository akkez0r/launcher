package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class IntegratedServer extends MinecraftServer {
	private final Minecraft mc;
	private final WorldSettings theWorldSettings;
	private final ILogAgent serverLogAgent = new LogAgent("Minecraft-Server", " [SERVER]", (new File(Minecraft.getMinecraftDir(), "output-server.log")).getAbsolutePath());
	private IntegratedServerListenThread theServerListeningThread;
	private boolean isGamePaused = false;
	private boolean isPublic;
	private ThreadLanServerPing lanServerPing;

	public IntegratedServer(Minecraft var1, String var2, String var3, WorldSettings var4) {
		super(new File(Minecraft.getMinecraftDir(), "saves"));
		this.setServerOwner(var1.session.username);
		this.setFolderName(var2);
		this.setWorldName(var3);
		this.setDemo(var1.isDemo());
		this.canCreateBonusChest(var4.isBonusChestEnabled());
		this.setBuildLimit(256);
		this.setConfigurationManager(new IntegratedPlayerList(this));
		this.mc = var1;
		this.theWorldSettings = var4;

		try {
			this.theServerListeningThread = new IntegratedServerListenThread(this);
		} catch (IOException var6) {
			throw new Error();
		}
	}

	protected void loadAllWorlds(String var1, String var2, long var3, WorldType var5, String var6) {
		this.convertMapIfNeeded(var1);
		this.worldServers = new WorldServer[3];
		this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
		ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(var1, true);

		for(int var8 = 0; var8 < this.worldServers.length; ++var8) {
			byte var9 = 0;
			if(var8 == 1) {
				var9 = -1;
			}

			if(var8 == 2) {
				var9 = 1;
			}

			if(var8 == 0) {
				if(this.isDemo()) {
					this.worldServers[var8] = new DemoWorldServer(this, var7, var2, var9, this.theProfiler, this.getLogAgent());
				} else {
					this.worldServers[var8] = new WorldServer(this, var7, var2, var9, this.theWorldSettings, this.theProfiler, this.getLogAgent());
				}
			} else {
				this.worldServers[var8] = new WorldServerMulti(this, var7, var2, var9, this.theWorldSettings, this.worldServers[0], this.theProfiler, this.getLogAgent());
			}

			this.worldServers[var8].addWorldAccess(new WorldManager(this, this.worldServers[var8]));
			this.getConfigurationManager().setPlayerManager(this.worldServers);
		}

		this.setDifficultyForAllWorlds(this.getDifficulty());
		this.initialWorldChunkLoad();
	}

	protected boolean startServer() throws IOException {
		this.serverLogAgent.logInfo("Starting integrated minecraft server version 1.5.2");
		this.setOnlineMode(false);
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		this.serverLogAgent.logInfo("Generating keypair");
		this.setKeyPair(CryptManager.createNewKeyPair());
		this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
		this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
		return true;
	}

	protected void tickIntegrated() {
		boolean var1 = this.isGamePaused;
		this.isGamePaused = this.theServerListeningThread.isGamePaused();
		if(!var1 && this.isGamePaused) {
			this.serverLogAgent.logInfo("Saving and pausing game...");
			this.getConfigurationManager().saveAllPlayerData();
			this.saveAllWorlds(false);
		}

		if(!this.isGamePaused) {
			super.tick();
		}

	}

	public boolean canStructuresSpawn() {
		return false;
	}

	public EnumGameType getGameType() {
		return this.theWorldSettings.getGameType();
	}

	public int getDifficulty() {
		return this.mc.gameSettings.difficulty;
	}

	public boolean isHardcore() {
		return this.theWorldSettings.getHardcoreEnabled();
	}

	protected File getDataDirectory() {
		return this.mc.mcDataDir;
	}

	public boolean isDedicatedServer() {
		return false;
	}

	public IntegratedServerListenThread getServerListeningThread() {
		return this.theServerListeningThread;
	}

	protected void finalTick(CrashReport var1) {
		this.mc.crashed(var1);
	}

	public CrashReport addServerInfoToCrashReport(CrashReport var1) {
		var1 = super.addServerInfoToCrashReport(var1);
		var1.func_85056_g().addCrashSectionCallable("Type", new CallableType3(this));
		var1.func_85056_g().addCrashSectionCallable("Is Modded", new CallableIsModded(this));
		return var1;
	}

	public void addServerStatsToSnooper(PlayerUsageSnooper var1) {
		super.addServerStatsToSnooper(var1);
		var1.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
	}

	public boolean isSnooperEnabled() {
		return Minecraft.getMinecraft().isSnooperEnabled();
	}

	public String shareToLAN(EnumGameType var1, boolean var2) {
		try {
			String var3 = this.theServerListeningThread.func_71755_c();
			this.getLogAgent().logInfo("Started on " + var3);
			this.isPublic = true;
			this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var3);
			this.lanServerPing.start();
			this.getConfigurationManager().setGameType(var1);
			this.getConfigurationManager().setCommandsAllowedForAll(var2);
			return var3;
		} catch (IOException var4) {
			return null;
		}
	}

	public ILogAgent getLogAgent() {
		return this.serverLogAgent;
	}

	public void stopServer() {
		super.stopServer();
		if(this.lanServerPing != null) {
			this.lanServerPing.interrupt();
			this.lanServerPing = null;
		}

	}

	public void initiateShutdown() {
		super.initiateShutdown();
		if(this.lanServerPing != null) {
			this.lanServerPing.interrupt();
			this.lanServerPing = null;
		}

	}

	public boolean getPublic() {
		return this.isPublic;
	}

	public void setGameType(EnumGameType var1) {
		this.getConfigurationManager().setGameType(var1);
	}

	public boolean isCommandBlockEnabled() {
		return true;
	}

	public NetworkListenThread getNetworkThread() {
		return this.getServerListeningThread();
	}
}
