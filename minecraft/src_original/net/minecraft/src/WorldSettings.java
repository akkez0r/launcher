package net.minecraft.src;

public final class WorldSettings {
	private final long seed;
	private final EnumGameType theGameType;
	private final boolean mapFeaturesEnabled;
	private final boolean hardcoreEnabled;
	private final WorldType terrainType;
	private boolean commandsAllowed;
	private boolean bonusChestEnabled;
	private String field_82751_h;

	public WorldSettings(long var1, EnumGameType var3, boolean var4, boolean var5, WorldType var6) {
		this.field_82751_h = "";
		this.seed = var1;
		this.theGameType = var3;
		this.mapFeaturesEnabled = var4;
		this.hardcoreEnabled = var5;
		this.terrainType = var6;
	}

	public WorldSettings(WorldInfo var1) {
		this(var1.getSeed(), var1.getGameType(), var1.isMapFeaturesEnabled(), var1.isHardcoreModeEnabled(), var1.getTerrainType());
	}

	public WorldSettings enableBonusChest() {
		this.bonusChestEnabled = true;
		return this;
	}

	public WorldSettings enableCommands() {
		this.commandsAllowed = true;
		return this;
	}

	public WorldSettings func_82750_a(String var1) {
		this.field_82751_h = var1;
		return this;
	}

	public boolean isBonusChestEnabled() {
		return this.bonusChestEnabled;
	}

	public long getSeed() {
		return this.seed;
	}

	public EnumGameType getGameType() {
		return this.theGameType;
	}

	public boolean getHardcoreEnabled() {
		return this.hardcoreEnabled;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public boolean areCommandsAllowed() {
		return this.commandsAllowed;
	}

	public static EnumGameType getGameTypeById(int var0) {
		return EnumGameType.getByID(var0);
	}

	public String func_82749_j() {
		return this.field_82751_h;
	}
}
