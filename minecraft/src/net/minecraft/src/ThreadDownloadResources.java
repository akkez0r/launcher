package net.minecraft.src;

import java.io.File;
import net.minecraft.client.Minecraft;

public class ThreadDownloadResources extends Thread {
	public File resourcesFolder;
	private Minecraft mc;

	public ThreadDownloadResources(File var1, Minecraft var2) {
		this.mc = var2;
		this.setName("Resource download thread");
		this.setDaemon(true);
		this.resourcesFolder = new File(var1, "resources/");
		if(!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
		}
	}

	public void run() {
		try {
			this.loadResource(this.resourcesFolder, "");
		} catch (Exception var13) {
			var13.printStackTrace();
		}

	}

	public void reloadResources() {
		this.loadResource(this.resourcesFolder, "");
	}

	private void loadResource(File var1, String var2) {
		File[] var3 = var1.listFiles();
		if(var3 == null) {
			return;
		}

		for(int var4 = 0; var4 < var3.length; ++var4) {
			if(var3[var4].isDirectory()) {
				this.loadResource(var3[var4], var2 + var3[var4].getName() + "/");
			} else {
				try {
					this.mc.installResource(var2 + var3[var4].getName(), var3[var4]);
				} catch (Exception var6) {
					this.mc.getLogAgent().logWarning("Failed to add " + var2 + var3[var4].getName() + " in resources");
				}
			}
		}

	}

	public void closeMinecraft() {
	}
}
