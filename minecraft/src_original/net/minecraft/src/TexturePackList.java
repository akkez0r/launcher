package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class TexturePackList {
	private static final ITexturePack defaultTexturePack = new TexturePackDefault();
	private final Minecraft mc;
	private final File texturePackDir;
	private final File mpTexturePackFolder;
	private List availableTexturePacks = new ArrayList();
	private Map texturePackCache = new HashMap();
	private ITexturePack selectedTexturePack;
	private boolean isDownloading;

	public TexturePackList(File var1, Minecraft var2) {
		this.mc = var2;
		this.texturePackDir = new File(var1, "texturepacks");
		this.mpTexturePackFolder = new File(var1, "texturepacks-mp-cache");
		this.createTexturePackDirs();
		this.updateAvaliableTexturePacks();
	}

	private void createTexturePackDirs() {
		if(!this.texturePackDir.isDirectory()) {
			this.texturePackDir.delete();
			this.texturePackDir.mkdirs();
		}

		if(!this.mpTexturePackFolder.isDirectory()) {
			this.mpTexturePackFolder.delete();
			this.mpTexturePackFolder.mkdirs();
		}

	}

	public boolean setTexturePack(ITexturePack var1) {
		if(var1 == this.selectedTexturePack) {
			return false;
		} else {
			this.isDownloading = false;
			this.selectedTexturePack = var1;
			this.mc.gameSettings.skin = var1.getTexturePackFileName();
			this.mc.gameSettings.saveOptions();
			return true;
		}
	}

	public void requestDownloadOfTexture(String var1) {
		String var2 = var1.substring(var1.lastIndexOf("/") + 1);
		if(var2.contains("?")) {
			var2 = var2.substring(0, var2.indexOf("?"));
		}

		if(var2.endsWith(".zip")) {
			File var3 = new File(this.mpTexturePackFolder, var2);
			this.downloadTexture(var1, var3);
		}
	}

	private void downloadTexture(String var1, File var2) {
		HashMap var3 = new HashMap();
		GuiProgress var4 = new GuiProgress();
		var3.put("X-Minecraft-Username", this.mc.session.username);
		var3.put("X-Minecraft-Version", "1.5.2");
		var3.put("X-Minecraft-Supported-Resolutions", "16");
		this.isDownloading = true;
		this.mc.displayGuiScreen(var4);
		HttpUtil.downloadTexturePack(var2, var1, new TexturePackDownloadSuccess(this), var3, 10000000, var4);
	}

	public boolean getIsDownloading() {
		return this.isDownloading;
	}

	public void onDownloadFinished() {
		this.isDownloading = false;
		this.updateAvaliableTexturePacks();
		this.mc.scheduleTexturePackRefresh();
	}

	public void updateAvaliableTexturePacks() {
		ArrayList var1 = new ArrayList();
		this.selectedTexturePack = defaultTexturePack;
		var1.add(defaultTexturePack);
		Iterator var2 = this.getTexturePackDirContents().iterator();

		while(var2.hasNext()) {
			File var3 = (File)var2.next();
			String var4 = this.generateTexturePackID(var3);
			if(var4 != null) {
				Object var5 = (ITexturePack)this.texturePackCache.get(var4);
				if(var5 == null) {
					var5 = var3.isDirectory() ? new TexturePackFolder(var4, var3, defaultTexturePack) : new TexturePackCustom(var4, var3, defaultTexturePack);
					this.texturePackCache.put(var4, var5);
				}

				if(((ITexturePack)var5).getTexturePackFileName().equals(this.mc.gameSettings.skin)) {
					this.selectedTexturePack = (ITexturePack)var5;
				}

				var1.add(var5);
			}
		}

		this.availableTexturePacks.removeAll(var1);
		var2 = this.availableTexturePacks.iterator();

		while(var2.hasNext()) {
			ITexturePack var6 = (ITexturePack)var2.next();
			var6.deleteTexturePack(this.mc.renderEngine);
			this.texturePackCache.remove(var6.getTexturePackID());
		}

		this.availableTexturePacks = var1;
	}

	private String generateTexturePackID(File var1) {
		return var1.isFile() && var1.getName().toLowerCase().endsWith(".zip") ? var1.getName() + ":" + var1.length() + ":" + var1.lastModified() : (var1.isDirectory() && (new File(var1, "pack.txt")).exists() ? var1.getName() + ":folder:" + var1.lastModified() : null);
	}

	private List getTexturePackDirContents() {
		return this.texturePackDir.exists() && this.texturePackDir.isDirectory() ? Arrays.asList(this.texturePackDir.listFiles()) : Collections.emptyList();
	}

	public List availableTexturePacks() {
		return Collections.unmodifiableList(this.availableTexturePacks);
	}

	public ITexturePack getSelectedTexturePack() {
		return this.selectedTexturePack;
	}

	public boolean func_77300_f() {
		if(!this.mc.gameSettings.serverTextures) {
			return false;
		} else {
			ServerData var1 = this.mc.getServerData();
			return var1 == null ? true : var1.func_78840_c();
		}
	}

	public boolean getAcceptsTextures() {
		if(!this.mc.gameSettings.serverTextures) {
			return false;
		} else {
			ServerData var1 = this.mc.getServerData();
			return var1 == null ? false : var1.getAcceptsTextures();
		}
	}

	static boolean isDownloading(TexturePackList var0) {
		return var0.isDownloading;
	}

	static ITexturePack setSelectedTexturePack(TexturePackList var0, ITexturePack var1) {
		return var0.selectedTexturePack = var1;
	}

	static String generateTexturePackID(TexturePackList var0, File var1) {
		return var0.generateTexturePackID(var1);
	}

	static ITexturePack func_98143_h() {
		return defaultTexturePack;
	}

	static Minecraft getMinecraft(TexturePackList var0) {
		return var0.mc;
	}
}
