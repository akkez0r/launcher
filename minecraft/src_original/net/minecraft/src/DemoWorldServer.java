package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class DemoWorldServer extends WorldServer {
	private static final long demoWorldSeed = (long)"North Carolina".hashCode();
	public static final WorldSettings demoWorldSettings = (new WorldSettings(demoWorldSeed, EnumGameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();

	public DemoWorldServer(MinecraftServer var1, ISaveHandler var2, String var3, int var4, Profiler var5, ILogAgent var6) {
		super(var1, var2, var3, var4, demoWorldSettings, var5, var6);
	}
}
