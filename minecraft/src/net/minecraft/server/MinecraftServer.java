package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.minecraft.src.AnvilSaveConverter;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.CallableIsServerModded;
import net.minecraft.src.CallableServerMemoryStats;
import net.minecraft.src.CallableServerProfiler;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ConvertingProgressUpdate;
import net.minecraft.src.CrashReport;
import net.minecraft.src.DemoWorldServer;
import net.minecraft.src.DispenserBehaviors;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ILogAgent;
import net.minecraft.src.IPlayerUsage;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PlayerUsageSnooper;
import net.minecraft.src.Profiler;
import net.minecraft.src.RConConsoleSource;
import net.minecraft.src.ReportedException;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.StringUtils;
import net.minecraft.src.ThreadMinecraftServer;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WorldServerMulti;
import net.minecraft.src.WorldSettings;
import net.minecraft.src.WorldType;

public abstract class MinecraftServer implements ICommandSender, Runnable, IPlayerUsage {
	private static MinecraftServer mcServer = null;
	private final ISaveFormat anvilConverterForAnvilFile;
	private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this);
	private final File anvilFile;
	private final List tickables = new ArrayList();
	private final ICommandManager commandManager;
	public final Profiler theProfiler = new Profiler();
	private String hostname;
	private int serverPort = -1;
	public WorldServer[] worldServers;
	private ServerConfigurationManager serverConfigManager;
	private boolean serverRunning = true;
	private boolean serverStopped = false;
	private int tickCounter = 0;
	public String currentTask;
	public int percentDone;
	private boolean onlineMode;
	private boolean canSpawnAnimals;
	private boolean canSpawnNPCs;
	private boolean pvpEnabled;
	private boolean allowFlight;
	private String motd;
	private int buildLimit;
	private long lastSentPacketID;
	private long lastSentPacketSize;
	private long lastReceivedID;
	private long lastReceivedSize;
	public final long[] sentPacketCountArray = new long[100];
	public final long[] sentPacketSizeArray = new long[100];
	public final long[] receivedPacketCountArray = new long[100];
	public final long[] receivedPacketSizeArray = new long[100];
	public final long[] tickTimeArray = new long[100];
	public long[][] timeOfLastDimensionTick;
	private KeyPair serverKeyPair;
	private String serverOwner;
	private String folderName;
	private String worldName;
	private boolean isDemo;
	private boolean enableBonusChest;
	private boolean worldIsBeingDeleted;
	private String texturePack = "";
	private boolean serverIsRunning = false;
	private long timeOfLastWarning;
	private String userMessage;
	private boolean startProfiling;
	private boolean field_104057_T = false;

	public MinecraftServer(File var1) {
		mcServer = this;
		this.anvilFile = var1;
		this.commandManager = new ServerCommandManager();
		this.anvilConverterForAnvilFile = new AnvilSaveConverter(var1);
		this.registerDispenseBehaviors();
	}

	private void registerDispenseBehaviors() {
		DispenserBehaviors.func_96467_a();
	}

	protected abstract boolean startServer() throws IOException;

	protected void convertMapIfNeeded(String var1) {
		if(this.getActiveAnvilConverter().isOldMapFormat(var1)) {
			this.getLogAgent().logInfo("Converting map!");
			this.setUserMessage("menu.convertingLevel");
			this.getActiveAnvilConverter().convertMapFormat(var1, new ConvertingProgressUpdate(this));
		}

	}

	protected synchronized void setUserMessage(String var1) {
		this.userMessage = var1;
	}

	public synchronized String getUserMessage() {
		return this.userMessage;
	}

	protected void loadAllWorlds(String var1, String var2, long var3, WorldType var5, String var6) {
		this.convertMapIfNeeded(var1);
		this.setUserMessage("menu.loadingLevel");
		this.worldServers = new WorldServer[3];
		this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
		ISaveHandler var7 = this.anvilConverterForAnvilFile.getSaveLoader(var1, true);
		WorldInfo var9 = var7.loadWorldInfo();
		WorldSettings var8;
		if(var9 == null) {
			var8 = new WorldSettings(var3, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), var5);
			var8.func_82750_a(var6);
		} else {
			var8 = new WorldSettings(var9);
		}

		if(this.enableBonusChest) {
			var8.enableBonusChest();
		}

		for(int var10 = 0; var10 < this.worldServers.length; ++var10) {
			byte var11 = 0;
			if(var10 == 1) {
				var11 = -1;
			}

			if(var10 == 2) {
				var11 = 1;
			}

			if(var10 == 0) {
				if(this.isDemo()) {
					this.worldServers[var10] = new DemoWorldServer(this, var7, var2, var11, this.theProfiler, this.getLogAgent());
				} else {
					this.worldServers[var10] = new WorldServer(this, var7, var2, var11, var8, this.theProfiler, this.getLogAgent());
				}
			} else {
				this.worldServers[var10] = new WorldServerMulti(this, var7, var2, var11, var8, this.worldServers[0], this.theProfiler, this.getLogAgent());
			}

			this.worldServers[var10].addWorldAccess(new WorldManager(this, this.worldServers[var10]));
			if(!this.isSinglePlayer()) {
				this.worldServers[var10].getWorldInfo().setGameType(this.getGameType());
			}

			this.serverConfigManager.setPlayerManager(this.worldServers);
		}

		this.setDifficultyForAllWorlds(this.getDifficulty());
		this.initialWorldChunkLoad();
	}

	protected void initialWorldChunkLoad() {
		int var5 = 0;
		this.setUserMessage("menu.generatingTerrain");
		byte var6 = 0;
		this.getLogAgent().logInfo("Preparing start region for level " + var6);
		WorldServer var7 = this.worldServers[var6];
		ChunkCoordinates var8 = var7.getSpawnPoint();
		long var9 = System.currentTimeMillis();

		for(int var11 = -192; var11 <= 192 && this.isServerRunning(); var11 += 16) {
			for(int var12 = -192; var12 <= 192 && this.isServerRunning(); var12 += 16) {
				long var13 = System.currentTimeMillis();
				if(var13 - var9 > 1000L) {
					this.outputPercentRemaining("Preparing spawn area", var5 * 100 / 625);
					var9 = var13;
				}

				++var5;
				var7.theChunkProviderServer.loadChunk(var8.posX + var11 >> 4, var8.posZ + var12 >> 4);
			}
		}

		this.clearCurrentTask();
	}

	public abstract boolean canStructuresSpawn();

	public abstract EnumGameType getGameType();

	public abstract int getDifficulty();

	public abstract boolean isHardcore();

	protected void outputPercentRemaining(String var1, int var2) {
		this.currentTask = var1;
		this.percentDone = var2;
		this.getLogAgent().logInfo(var1 + ": " + var2 + "%");
	}

	protected void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	protected void saveAllWorlds(boolean var1) {
		if(!this.worldIsBeingDeleted) {
			WorldServer[] var2 = this.worldServers;
			int var3 = var2.length;

			for(int var4 = 0; var4 < var3; ++var4) {
				WorldServer var5 = var2[var4];
				if(var5 != null) {
					if(!var1) {
						this.getLogAgent().logInfo("Saving chunks for level \'" + var5.getWorldInfo().getWorldName() + "\'/" + var5.provider.getDimensionName());
					}

					try {
						var5.saveAllChunks(true, (IProgressUpdate)null);
					} catch (MinecraftException var7) {
						this.getLogAgent().logWarning(var7.getMessage());
					}
				}
			}

		}
	}

	public void stopServer() {
		if(!this.worldIsBeingDeleted) {
			this.getLogAgent().logInfo("Stopping server");
			if(this.getNetworkThread() != null) {
				this.getNetworkThread().stopListening();
			}

			if(this.serverConfigManager != null) {
				this.getLogAgent().logInfo("Saving players");
				this.serverConfigManager.saveAllPlayerData();
				this.serverConfigManager.removeAllPlayers();
			}

			this.getLogAgent().logInfo("Saving worlds");
			this.saveAllWorlds(false);

			for(int var1 = 0; var1 < this.worldServers.length; ++var1) {
				WorldServer var2 = this.worldServers[var1];
				var2.flush();
			}

			if(this.usageSnooper != null && this.usageSnooper.isSnooperRunning()) {
				this.usageSnooper.stopSnooper();
			}

		}
	}

	public String getServerHostname() {
		return this.hostname;
	}

	public void setHostname(String var1) {
		this.hostname = var1;
	}

	public boolean isServerRunning() {
		return this.serverRunning;
	}

	public void initiateShutdown() {
		this.serverRunning = false;
	}

	public void run() {
		try {
			if(this.startServer()) {
				long var1 = System.currentTimeMillis();

				for(long var50 = 0L; this.serverRunning; this.serverIsRunning = true) {
					long var5 = System.currentTimeMillis();
					long var7 = var5 - var1;
					if(var7 > 2000L && var1 - this.timeOfLastWarning >= 15000L) {
						this.getLogAgent().logWarning("Can\'t keep up! Did the system time change, or is the server overloaded?");
						var7 = 2000L;
						this.timeOfLastWarning = var1;
					}

					if(var7 < 0L) {
						this.getLogAgent().logWarning("Time ran backwards! Did the system time change?");
						var7 = 0L;
					}

					var50 += var7;
					var1 = var5;
					if(this.worldServers[0].areAllPlayersAsleep()) {
						this.tick();
						var50 = 0L;
					} else {
						while(var50 > 50L) {
							var50 -= 50L;
							this.tick();
						}
					}

					Thread.sleep(1L);
				}
			} else {
				this.finalTick((CrashReport)null);
			}
		} catch (Throwable var48) {
			var48.printStackTrace();
			this.getLogAgent().logSevereException("Encountered an unexpected exception " + var48.getClass().getSimpleName(), var48);
			CrashReport var2 = null;
			if(var48 instanceof ReportedException) {
				var2 = this.addServerInfoToCrashReport(((ReportedException)var48).getCrashReport());
			} else {
				var2 = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var48));
			}

			File var3 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
			if(var2.saveToFile(var3, this.getLogAgent())) {
				this.getLogAgent().logSevere("This crash report has been saved to: " + var3.getAbsolutePath());
			} else {
				this.getLogAgent().logSevere("We were unable to save this crash report to disk.");
			}

			this.finalTick(var2);
		} finally {
			try {
				this.stopServer();
				this.serverStopped = true;
			} catch (Throwable var46) {
				var46.printStackTrace();
			} finally {
				this.systemExitNow();
			}

		}

	}

	protected File getDataDirectory() {
		return new File(".");
	}

	protected void finalTick(CrashReport var1) {
	}

	protected void systemExitNow() {
	}

	public void tick() {
		long var1 = System.nanoTime();
		AxisAlignedBB.getAABBPool().cleanPool();
		++this.tickCounter;
		if(this.startProfiling) {
			this.startProfiling = false;
			this.theProfiler.profilingEnabled = true;
			this.theProfiler.clearProfiling();
		}

		this.theProfiler.startSection("root");
		this.updateTimeLightAndEntities();
		if(this.tickCounter % 900 == 0) {
			this.theProfiler.startSection("save");
			this.serverConfigManager.saveAllPlayerData();
			this.saveAllWorlds(true);
			this.theProfiler.endSection();
		}

		this.theProfiler.startSection("tallying");
		this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - var1;
		this.sentPacketCountArray[this.tickCounter % 100] = Packet.sentID - this.lastSentPacketID;
		this.lastSentPacketID = Packet.sentID;
		this.sentPacketSizeArray[this.tickCounter % 100] = Packet.sentSize - this.lastSentPacketSize;
		this.lastSentPacketSize = Packet.sentSize;
		this.receivedPacketCountArray[this.tickCounter % 100] = Packet.receivedID - this.lastReceivedID;
		this.lastReceivedID = Packet.receivedID;
		this.receivedPacketSizeArray[this.tickCounter % 100] = Packet.receivedSize - this.lastReceivedSize;
		this.lastReceivedSize = Packet.receivedSize;
		this.theProfiler.endSection();
		this.theProfiler.startSection("snooper");
		if(!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100) {
			this.usageSnooper.startSnooper();
		}

		if(this.tickCounter % 6000 == 0) {
			this.usageSnooper.addMemoryStatsToSnooper();
		}

		this.theProfiler.endSection();
		this.theProfiler.endSection();
	}

	public void updateTimeLightAndEntities() {
		this.theProfiler.startSection("levels");

		int var1;
		for(var1 = 0; var1 < this.worldServers.length; ++var1) {
			long var2 = System.nanoTime();
			if(var1 == 0 || this.getAllowNether()) {
				WorldServer var4 = this.worldServers[var1];
				this.theProfiler.startSection(var4.getWorldInfo().getWorldName());
				this.theProfiler.startSection("pools");
				var4.getWorldVec3Pool().clear();
				this.theProfiler.endSection();
				if(this.tickCounter % 20 == 0) {
					this.theProfiler.startSection("timeSync");
					this.serverConfigManager.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(var4.getTotalWorldTime(), var4.getWorldTime()), var4.provider.dimensionId);
					this.theProfiler.endSection();
				}

				this.theProfiler.startSection("tick");

				CrashReport var6;
				try {
					var4.tick();
				} catch (Throwable var8) {
					var6 = CrashReport.makeCrashReport(var8, "Exception ticking world");
					var4.addWorldInfoToCrashReport(var6);
					throw new ReportedException(var6);
				}

				try {
					var4.updateEntities();
				} catch (Throwable var7) {
					var6 = CrashReport.makeCrashReport(var7, "Exception ticking world entities");
					var4.addWorldInfoToCrashReport(var6);
					throw new ReportedException(var6);
				}

				this.theProfiler.endSection();
				this.theProfiler.startSection("tracker");
				var4.getEntityTracker().updateTrackedEntities();
				this.theProfiler.endSection();
				this.theProfiler.endSection();
			}

			this.timeOfLastDimensionTick[var1][this.tickCounter % 100] = System.nanoTime() - var2;
		}

		this.theProfiler.endStartSection("connection");
		this.getNetworkThread().networkTick();
		this.theProfiler.endStartSection("players");
		this.serverConfigManager.sendPlayerInfoToAllPlayers();
		this.theProfiler.endStartSection("tickables");

		for(var1 = 0; var1 < this.tickables.size(); ++var1) {
			((IUpdatePlayerListBox)this.tickables.get(var1)).update();
		}

		this.theProfiler.endSection();
	}

	public boolean getAllowNether() {
		return true;
	}

	public void startServerThread() {
		(new ThreadMinecraftServer(this, "Server thread")).start();
	}

	public File getFile(String var1) {
		return new File(this.getDataDirectory(), var1);
	}

	public void logInfo(String var1) {
		this.getLogAgent().logInfo(var1);
	}

	public void logWarning(String var1) {
		this.getLogAgent().logWarning(var1);
	}

	public WorldServer worldServerForDimension(int var1) {
		return var1 == -1 ? this.worldServers[1] : (var1 == 1 ? this.worldServers[2] : this.worldServers[0]);
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.serverPort;
	}

	public String getServerMOTD() {
		return this.motd;
	}

	public String getMinecraftVersion() {
		return "1.5.2";
	}

	public int getCurrentPlayerCount() {
		return this.serverConfigManager.getCurrentPlayerCount();
	}

	public int getMaxPlayers() {
		return this.serverConfigManager.getMaxPlayers();
	}

	public String[] getAllUsernames() {
		return this.serverConfigManager.getAllUsernames();
	}

	public String getPlugins() {
		return "";
	}

	public String executeCommand(String var1) {
		RConConsoleSource.consoleBuffer.resetLog();
		this.commandManager.executeCommand(RConConsoleSource.consoleBuffer, var1);
		return RConConsoleSource.consoleBuffer.getChatBuffer();
	}

	public boolean isDebuggingEnabled() {
		return false;
	}

	public void logSevere(String var1) {
		this.getLogAgent().logSevere(var1);
	}

	public void logDebug(String var1) {
		if(this.isDebuggingEnabled()) {
			this.getLogAgent().logInfo(var1);
		}

	}

	public String getServerModName() {
		return "vanilla";
	}

	public CrashReport addServerInfoToCrashReport(CrashReport var1) {
		var1.func_85056_g().addCrashSectionCallable("Profiler Position", new CallableIsServerModded(this));
		if(this.worldServers != null && this.worldServers.length > 0 && this.worldServers[0] != null) {
			var1.func_85056_g().addCrashSectionCallable("Vec3 Pool Size", new CallableServerProfiler(this));
		}

		if(this.serverConfigManager != null) {
			var1.func_85056_g().addCrashSectionCallable("Player Count", new CallableServerMemoryStats(this));
		}

		return var1;
	}

	public List getPossibleCompletions(ICommandSender var1, String var2) {
		ArrayList var3 = new ArrayList();
		if(var2.startsWith("/")) {
			var2 = var2.substring(1);
			boolean var10 = !var2.contains(" ");
			List var11 = this.commandManager.getPossibleCommands(var1, var2);
			if(var11 != null) {
				Iterator var12 = var11.iterator();

				while(var12.hasNext()) {
					String var13 = (String)var12.next();
					if(var10) {
						var3.add("/" + var13);
					} else {
						var3.add(var13);
					}
				}
			}

			return var3;
		} else {
			String[] var4 = var2.split(" ", -1);
			String var5 = var4[var4.length - 1];
			String[] var6 = this.serverConfigManager.getAllUsernames();
			int var7 = var6.length;

			for(int var8 = 0; var8 < var7; ++var8) {
				String var9 = var6[var8];
				if(CommandBase.doesStringStartWith(var5, var9)) {
					var3.add(var9);
				}
			}

			return var3;
		}
	}

	public static MinecraftServer getServer() {
		return mcServer;
	}

	public String getCommandSenderName() {
		return "Server";
	}

	public void sendChatToPlayer(String var1) {
		this.getLogAgent().logInfo(StringUtils.stripControlCodes(var1));
	}

	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return true;
	}

	public String translateString(String var1, Object... var2) {
		return StringTranslate.getInstance().translateKeyFormat(var1, var2);
	}

	public ICommandManager getCommandManager() {
		return this.commandManager;
	}

	public KeyPair getKeyPair() {
		return this.serverKeyPair;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int var1) {
		this.serverPort = var1;
	}

	public String getServerOwner() {
		return this.serverOwner;
	}

	public void setServerOwner(String var1) {
		this.serverOwner = var1;
	}

	public boolean isSinglePlayer() {
		return this.serverOwner != null;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public void setFolderName(String var1) {
		this.folderName = var1;
	}

	public void setWorldName(String var1) {
		this.worldName = var1;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public void setKeyPair(KeyPair var1) {
		this.serverKeyPair = var1;
	}

	public void setDifficultyForAllWorlds(int var1) {
		for(int var2 = 0; var2 < this.worldServers.length; ++var2) {
			WorldServer var3 = this.worldServers[var2];
			if(var3 != null) {
				if(var3.getWorldInfo().isHardcoreModeEnabled()) {
					var3.difficultySetting = 3;
					var3.setAllowedSpawnTypes(true, true);
				} else if(this.isSinglePlayer()) {
					var3.difficultySetting = var1;
					var3.setAllowedSpawnTypes(var3.difficultySetting > 0, true);
				} else {
					var3.difficultySetting = var1;
					var3.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
				}
			}
		}

	}

	protected boolean allowSpawnMonsters() {
		return true;
	}

	public boolean isDemo() {
		return this.isDemo;
	}

	public void setDemo(boolean var1) {
		this.isDemo = var1;
	}

	public void canCreateBonusChest(boolean var1) {
		this.enableBonusChest = var1;
	}

	public ISaveFormat getActiveAnvilConverter() {
		return this.anvilConverterForAnvilFile;
	}

	public void deleteWorldAndStopServer() {
		this.worldIsBeingDeleted = true;
		this.getActiveAnvilConverter().flushCache();

		for(int var1 = 0; var1 < this.worldServers.length; ++var1) {
			WorldServer var2 = this.worldServers[var1];
			if(var2 != null) {
				var2.flush();
			}
		}

		this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
		this.initiateShutdown();
	}

	public String getTexturePack() {
		return this.texturePack;
	}

	public void setTexturePack(String var1) {
		this.texturePack = var1;
	}

	public void addServerStatsToSnooper(PlayerUsageSnooper var1) {
		var1.addData("whitelist_enabled", Boolean.valueOf(false));
		var1.addData("whitelist_count", Integer.valueOf(0));
		var1.addData("players_current", Integer.valueOf(this.getCurrentPlayerCount()));
		var1.addData("players_max", Integer.valueOf(this.getMaxPlayers()));
		var1.addData("players_seen", Integer.valueOf(this.serverConfigManager.getAvailablePlayerDat().length));
		var1.addData("uses_auth", Boolean.valueOf(this.onlineMode));
		var1.addData("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
		var1.addData("avg_tick_ms", Integer.valueOf((int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D)));
		var1.addData("avg_sent_packet_count", Integer.valueOf((int)MathHelper.average(this.sentPacketCountArray)));
		var1.addData("avg_sent_packet_size", Integer.valueOf((int)MathHelper.average(this.sentPacketSizeArray)));
		var1.addData("avg_rec_packet_count", Integer.valueOf((int)MathHelper.average(this.receivedPacketCountArray)));
		var1.addData("avg_rec_packet_size", Integer.valueOf((int)MathHelper.average(this.receivedPacketSizeArray)));
		int var2 = 0;

		for(int var3 = 0; var3 < this.worldServers.length; ++var3) {
			if(this.worldServers[var3] != null) {
				WorldServer var4 = this.worldServers[var3];
				WorldInfo var5 = var4.getWorldInfo();
				var1.addData("world[" + var2 + "][dimension]", Integer.valueOf(var4.provider.dimensionId));
				var1.addData("world[" + var2 + "][mode]", var5.getGameType());
				var1.addData("world[" + var2 + "][difficulty]", Integer.valueOf(var4.difficultySetting));
				var1.addData("world[" + var2 + "][hardcore]", Boolean.valueOf(var5.isHardcoreModeEnabled()));
				var1.addData("world[" + var2 + "][generator_name]", var5.getTerrainType().getWorldTypeName());
				var1.addData("world[" + var2 + "][generator_version]", Integer.valueOf(var5.getTerrainType().getGeneratorVersion()));
				var1.addData("world[" + var2 + "][height]", Integer.valueOf(this.buildLimit));
				var1.addData("world[" + var2 + "][chunks_loaded]", Integer.valueOf(var4.getChunkProvider().getLoadedChunkCount()));
				++var2;
			}
		}

		var1.addData("worlds", Integer.valueOf(var2));
	}

	public void addServerTypeToSnooper(PlayerUsageSnooper var1) {
		var1.addData("singleplayer", Boolean.valueOf(this.isSinglePlayer()));
		var1.addData("server_brand", this.getServerModName());
		var1.addData("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
		var1.addData("dedicated", Boolean.valueOf(this.isDedicatedServer()));
	}

	public boolean isSnooperEnabled() {
		return true;
	}

	public int textureSize() {
		return 16;
	}

	public abstract boolean isDedicatedServer();

	public boolean isServerInOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean var1) {
		this.onlineMode = var1;
	}

	public boolean getCanSpawnAnimals() {
		return this.canSpawnAnimals;
	}

	public void setCanSpawnAnimals(boolean var1) {
		this.canSpawnAnimals = var1;
	}

	public boolean getCanSpawnNPCs() {
		return this.canSpawnNPCs;
	}

	public void setCanSpawnNPCs(boolean var1) {
		this.canSpawnNPCs = var1;
	}

	public boolean isPVPEnabled() {
		return this.pvpEnabled;
	}

	public void setAllowPvp(boolean var1) {
		this.pvpEnabled = var1;
	}

	public boolean isFlightAllowed() {
		return this.allowFlight;
	}

	public void setAllowFlight(boolean var1) {
		this.allowFlight = var1;
	}

	public abstract boolean isCommandBlockEnabled();

	public String getMOTD() {
		return this.motd;
	}

	public void setMOTD(String var1) {
		this.motd = var1;
	}

	public int getBuildLimit() {
		return this.buildLimit;
	}

	public void setBuildLimit(int var1) {
		this.buildLimit = var1;
	}

	public boolean isServerStopped() {
		return this.serverStopped;
	}

	public ServerConfigurationManager getConfigurationManager() {
		return this.serverConfigManager;
	}

	public void setConfigurationManager(ServerConfigurationManager var1) {
		this.serverConfigManager = var1;
	}

	public void setGameType(EnumGameType var1) {
		for(int var2 = 0; var2 < this.worldServers.length; ++var2) {
			getServer().worldServers[var2].getWorldInfo().setGameType(var1);
		}

	}

	public abstract NetworkListenThread getNetworkThread();

	public boolean serverIsInRunLoop() {
		return this.serverIsRunning;
	}

	public boolean getGuiEnabled() {
		return false;
	}

	public abstract String shareToLAN(EnumGameType var1, boolean var2);

	public int getTickCounter() {
		return this.tickCounter;
	}

	public void enableProfiling() {
		this.startProfiling = true;
	}

	public PlayerUsageSnooper getPlayerUsageSnooper() {
		return this.usageSnooper;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}

	public int getSpawnProtectionSize() {
		return 16;
	}

	public boolean func_96290_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		return false;
	}

	public abstract ILogAgent getLogAgent();

	public void func_104055_i(boolean var1) {
		this.field_104057_T = var1;
	}

	public boolean func_104056_am() {
		return this.field_104057_T;
	}

	public static ServerConfigurationManager getServerConfigurationManager(MinecraftServer var0) {
		return var0.serverConfigManager;
	}
}
