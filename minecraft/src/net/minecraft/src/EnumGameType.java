package net.minecraft.src;

public enum EnumGameType {
	NOT_SET(-1, ""),
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure");

	int id;
	String name;

	private EnumGameType(int var3, String var4) {
		this.id = var3;
		this.name = var4;
	}

	public int getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void configurePlayerCapabilities(PlayerCapabilities var1) {
		if(this == CREATIVE) {
			var1.allowFlying = true;
			var1.isCreativeMode = true;
			var1.disableDamage = true;
		} else {
			var1.allowFlying = false;
			var1.isCreativeMode = false;
			var1.disableDamage = false;
			var1.isFlying = false;
		}

		var1.allowEdit = !this.isAdventure();
	}

	public boolean isAdventure() {
		return this == ADVENTURE;
	}

	public boolean isCreative() {
		return this == CREATIVE;
	}

	public boolean isSurvivalOrAdventure() {
		return this == SURVIVAL || this == ADVENTURE;
	}

	public static EnumGameType getByID(int var0) {
		EnumGameType[] var1 = values();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			EnumGameType var4 = var1[var3];
			if(var4.id == var0) {
				return var4;
			}
		}

		return SURVIVAL;
	}

	public static EnumGameType getByName(String var0) {
		EnumGameType[] var1 = values();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			EnumGameType var4 = var1[var3];
			if(var4.name.equals(var0)) {
				return var4;
			}
		}

		return SURVIVAL;
	}
}
