package net.minecraft.src;

import java.io.File;

public class SaveHandlerMP implements ISaveHandler {
	public WorldInfo loadWorldInfo() {
		return null;
	}

	public void checkSessionLock() throws MinecraftException {
	}

	public IChunkLoader getChunkLoader(WorldProvider var1) {
		return null;
	}

	public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) {
	}

	public void saveWorldInfo(WorldInfo var1) {
	}

	public IPlayerFileData getSaveHandler() {
		return null;
	}

	public void flush() {
	}

	public File getMapFileFromName(String var1) {
		return null;
	}

	public String getWorldDirectoryName() {
		return "none";
	}
}
