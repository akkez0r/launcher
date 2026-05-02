package net.minecraft.src;

public class WorldInfo {
	private long randomSeed;
	private WorldType terrainType = WorldType.DEFAULT;
	private String generatorOptions = "";
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long totalTime;
	private long worldTime;
	private long lastTimePlayed;
	private long sizeOnDisk;
	private NBTTagCompound playerTag;
	private int dimension;
	private String levelName;
	private int saveVersion;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private EnumGameType theGameType;
	private boolean mapFeaturesEnabled;
	private boolean hardcore;
	private boolean allowCommands;
	private boolean initialized;
	private GameRules theGameRules = new GameRules();

	protected WorldInfo() {
	}

	public WorldInfo(NBTTagCompound var1) {
		this.randomSeed = var1.getLong("RandomSeed");
		if(var1.hasKey("generatorName")) {
			String var2 = var1.getString("generatorName");
			this.terrainType = WorldType.parseWorldType(var2);
			if(this.terrainType == null) {
				this.terrainType = WorldType.DEFAULT;
			} else if(this.terrainType.isVersioned()) {
				int var3 = 0;
				if(var1.hasKey("generatorVersion")) {
					var3 = var1.getInteger("generatorVersion");
				}

				this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(var3);
			}

			if(var1.hasKey("generatorOptions")) {
				this.generatorOptions = var1.getString("generatorOptions");
			}
		}

		this.theGameType = EnumGameType.getByID(var1.getInteger("GameType"));
		if(var1.hasKey("MapFeatures")) {
			this.mapFeaturesEnabled = var1.getBoolean("MapFeatures");
		} else {
			this.mapFeaturesEnabled = true;
		}

		this.spawnX = var1.getInteger("SpawnX");
		this.spawnY = var1.getInteger("SpawnY");
		this.spawnZ = var1.getInteger("SpawnZ");
		this.totalTime = var1.getLong("Time");
		if(var1.hasKey("DayTime")) {
			this.worldTime = var1.getLong("DayTime");
		} else {
			this.worldTime = this.totalTime;
		}

		this.lastTimePlayed = var1.getLong("LastPlayed");
		this.sizeOnDisk = var1.getLong("SizeOnDisk");
		this.levelName = var1.getString("LevelName");
		this.saveVersion = var1.getInteger("version");
		this.rainTime = var1.getInteger("rainTime");
		this.raining = var1.getBoolean("raining");
		this.thunderTime = var1.getInteger("thunderTime");
		this.thundering = var1.getBoolean("thundering");
		this.hardcore = var1.getBoolean("hardcore");
		if(var1.hasKey("initialized")) {
			this.initialized = var1.getBoolean("initialized");
		} else {
			this.initialized = true;
		}

		if(var1.hasKey("allowCommands")) {
			this.allowCommands = var1.getBoolean("allowCommands");
		} else {
			this.allowCommands = this.theGameType == EnumGameType.CREATIVE;
		}

		if(var1.hasKey("Player")) {
			this.playerTag = var1.getCompoundTag("Player");
			this.dimension = this.playerTag.getInteger("Dimension");
		}

		if(var1.hasKey("GameRules")) {
			this.theGameRules.readGameRulesFromNBT(var1.getCompoundTag("GameRules"));
		}

	}

	public WorldInfo(WorldSettings var1, String var2) {
		this.randomSeed = var1.getSeed();
		this.theGameType = var1.getGameType();
		this.mapFeaturesEnabled = var1.isMapFeaturesEnabled();
		this.levelName = var2;
		this.hardcore = var1.getHardcoreEnabled();
		this.terrainType = var1.getTerrainType();
		this.generatorOptions = var1.func_82749_j();
		this.allowCommands = var1.areCommandsAllowed();
		this.initialized = false;
	}

	public WorldInfo(WorldInfo var1) {
		this.randomSeed = var1.randomSeed;
		this.terrainType = var1.terrainType;
		this.generatorOptions = var1.generatorOptions;
		this.theGameType = var1.theGameType;
		this.mapFeaturesEnabled = var1.mapFeaturesEnabled;
		this.spawnX = var1.spawnX;
		this.spawnY = var1.spawnY;
		this.spawnZ = var1.spawnZ;
		this.totalTime = var1.totalTime;
		this.worldTime = var1.worldTime;
		this.lastTimePlayed = var1.lastTimePlayed;
		this.sizeOnDisk = var1.sizeOnDisk;
		this.playerTag = var1.playerTag;
		this.dimension = var1.dimension;
		this.levelName = var1.levelName;
		this.saveVersion = var1.saveVersion;
		this.rainTime = var1.rainTime;
		this.raining = var1.raining;
		this.thunderTime = var1.thunderTime;
		this.thundering = var1.thundering;
		this.hardcore = var1.hardcore;
		this.allowCommands = var1.allowCommands;
		this.initialized = var1.initialized;
		this.theGameRules = var1.theGameRules;
	}

	public NBTTagCompound getNBTTagCompound() {
		NBTTagCompound var1 = new NBTTagCompound();
		this.updateTagCompound(var1, this.playerTag);
		return var1;
	}

	public NBTTagCompound cloneNBTCompound(NBTTagCompound var1) {
		NBTTagCompound var2 = new NBTTagCompound();
		this.updateTagCompound(var2, var1);
		return var2;
	}

	private void updateTagCompound(NBTTagCompound var1, NBTTagCompound var2) {
		var1.setLong("RandomSeed", this.randomSeed);
		var1.setString("generatorName", this.terrainType.getWorldTypeName());
		var1.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
		var1.setString("generatorOptions", this.generatorOptions);
		var1.setInteger("GameType", this.theGameType.getID());
		var1.setBoolean("MapFeatures", this.mapFeaturesEnabled);
		var1.setInteger("SpawnX", this.spawnX);
		var1.setInteger("SpawnY", this.spawnY);
		var1.setInteger("SpawnZ", this.spawnZ);
		var1.setLong("Time", this.totalTime);
		var1.setLong("DayTime", this.worldTime);
		var1.setLong("SizeOnDisk", this.sizeOnDisk);
		var1.setLong("LastPlayed", System.currentTimeMillis());
		var1.setString("LevelName", this.levelName);
		var1.setInteger("version", this.saveVersion);
		var1.setInteger("rainTime", this.rainTime);
		var1.setBoolean("raining", this.raining);
		var1.setInteger("thunderTime", this.thunderTime);
		var1.setBoolean("thundering", this.thundering);
		var1.setBoolean("hardcore", this.hardcore);
		var1.setBoolean("allowCommands", this.allowCommands);
		var1.setBoolean("initialized", this.initialized);
		var1.setCompoundTag("GameRules", this.theGameRules.writeGameRulesToNBT());
		if(var2 != null) {
			var1.setCompoundTag("Player", var2);
		}

	}

	public long getSeed() {
		return this.randomSeed;
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public long getWorldTotalTime() {
		return this.totalTime;
	}

	public long getWorldTime() {
		return this.worldTime;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	public NBTTagCompound getPlayerNBTTagCompound() {
		return this.playerTag;
	}

	public int getDimension() {
		return this.dimension;
	}

	public void setSpawnX(int var1) {
		this.spawnX = var1;
	}

	public void setSpawnY(int var1) {
		this.spawnY = var1;
	}

	public void setSpawnZ(int var1) {
		this.spawnZ = var1;
	}

	public void incrementTotalWorldTime(long var1) {
		this.totalTime = var1;
	}

	public void setWorldTime(long var1) {
		this.worldTime = var1;
	}

	public void setSpawnPosition(int var1, int var2, int var3) {
		this.spawnX = var1;
		this.spawnY = var2;
		this.spawnZ = var3;
	}

	public String getWorldName() {
		return this.levelName;
	}

	public void setWorldName(String var1) {
		this.levelName = var1;
	}

	public int getSaveVersion() {
		return this.saveVersion;
	}

	public void setSaveVersion(int var1) {
		this.saveVersion = var1;
	}

	public long getLastTimePlayed() {
		return this.lastTimePlayed;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean var1) {
		this.thundering = var1;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int var1) {
		this.thunderTime = var1;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean var1) {
		this.raining = var1;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int var1) {
		this.rainTime = var1;
	}

	public EnumGameType getGameType() {
		return this.theGameType;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}

	public void setGameType(EnumGameType var1) {
		this.theGameType = var1;
	}

	public boolean isHardcoreModeEnabled() {
		return this.hardcore;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public void setTerrainType(WorldType var1) {
		this.terrainType = var1;
	}

	public String getGeneratorOptions() {
		return this.generatorOptions;
	}

	public boolean areCommandsAllowed() {
		return this.allowCommands;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setServerInitialized(boolean var1) {
		this.initialized = var1;
	}

	public GameRules getGameRulesInstance() {
		return this.theGameRules;
	}

	public void addToCrashReport(CrashReportCategory var1) {
		var1.addCrashSectionCallable("Level seed", new CallableLevelSeed(this));
		var1.addCrashSectionCallable("Level generator", new CallableLevelGenerator(this));
		var1.addCrashSectionCallable("Level generator options", new CallableLevelGeneratorOptions(this));
		var1.addCrashSectionCallable("Level spawn location", new CallableLevelSpawnLocation(this));
		var1.addCrashSectionCallable("Level time", new CallableLevelTime(this));
		var1.addCrashSectionCallable("Level dimension", new CallableLevelDimension(this));
		var1.addCrashSectionCallable("Level storage version", new CallableLevelStorageVersion(this));
		var1.addCrashSectionCallable("Level weather", new CallableLevelWeather(this));
		var1.addCrashSectionCallable("Level game mode", new CallableLevelGamemode(this));
	}

	static WorldType getTerrainTypeOfWorld(WorldInfo var0) {
		return var0.terrainType;
	}

	static boolean getMapFeaturesEnabled(WorldInfo var0) {
		return var0.mapFeaturesEnabled;
	}

	static String getWorldGeneratorOptions(WorldInfo var0) {
		return var0.generatorOptions;
	}

	static int getSpawnXCoordinate(WorldInfo var0) {
		return var0.spawnX;
	}

	static int getSpawnYCoordinate(WorldInfo var0) {
		return var0.spawnY;
	}

	static int getSpawnZCoordinate(WorldInfo var0) {
		return var0.spawnZ;
	}

	static long func_85126_g(WorldInfo var0) {
		return var0.totalTime;
	}

	static long getWorldTime(WorldInfo var0) {
		return var0.worldTime;
	}

	static int func_85122_i(WorldInfo var0) {
		return var0.dimension;
	}

	static int getSaveVersion(WorldInfo var0) {
		return var0.saveVersion;
	}

	static int getRainTime(WorldInfo var0) {
		return var0.rainTime;
	}

	static boolean getRaining(WorldInfo var0) {
		return var0.raining;
	}

	static int getThunderTime(WorldInfo var0) {
		return var0.thunderTime;
	}

	static boolean getThundering(WorldInfo var0) {
		return var0.thundering;
	}

	static EnumGameType getGameType(WorldInfo var0) {
		return var0.theGameType;
	}

	static boolean func_85117_p(WorldInfo var0) {
		return var0.hardcore;
	}

	static boolean func_85131_q(WorldInfo var0) {
		return var0.allowCommands;
	}
}
