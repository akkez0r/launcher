package net.minecraft.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class TexturePackCustom extends TexturePackImplementation {
	private ZipFile texturePackZipFile;

	public TexturePackCustom(String var1, File var2, ITexturePack var3) {
		super(var1, var2, var2.getName(), var3);
	}

	public void deleteTexturePack(RenderEngine var1) {
		super.deleteTexturePack(var1);

		try {
			if(this.texturePackZipFile != null) {
				this.texturePackZipFile.close();
			}
		} catch (IOException var3) {
		}

		this.texturePackZipFile = null;
	}

	protected InputStream func_98139_b(String var1) throws IOException {
		this.openTexturePackFile();
		ZipEntry var2 = this.texturePackZipFile.getEntry(var1.substring(1));
		if(var2 == null) {
			throw new FileNotFoundException(var1);
		} else {
			return this.texturePackZipFile.getInputStream(var2);
		}
	}

	public boolean func_98140_c(String var1) {
		try {
			this.openTexturePackFile();
			return this.texturePackZipFile.getEntry(var1.substring(1)) != null;
		} catch (Exception var3) {
			return false;
		}
	}

	private void openTexturePackFile() throws IOException, ZipException {
		if(this.texturePackZipFile == null) {
			this.texturePackZipFile = new ZipFile(this.texturePackFile);
		}
	}

	public boolean isCompatible() {
		try {
			this.openTexturePackFile();
			Enumeration var1 = this.texturePackZipFile.entries();

			while(var1.hasMoreElements()) {
				ZipEntry var2 = (ZipEntry)var1.nextElement();
				if(var2.getName().startsWith("textures/")) {
					return true;
				}
			}
		} catch (Exception var3) {
		}

		boolean var4 = this.func_98140_c("terrain.png") || this.func_98140_c("gui/items.png");
		return !var4;
	}
}
