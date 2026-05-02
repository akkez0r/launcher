package net.minecraft.src;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TexturePackFolder extends TexturePackImplementation {
	public TexturePackFolder(String var1, File var2, ITexturePack var3) {
		super(var1, var2, var2.getName(), var3);
	}

	protected InputStream func_98139_b(String var1) throws IOException {
		File var2 = new File(this.texturePackFile, var1.substring(1));
		if(!var2.exists()) {
			throw new FileNotFoundException(var1);
		} else {
			return new BufferedInputStream(new FileInputStream(var2));
		}
	}

	public boolean func_98140_c(String var1) {
		File var2 = new File(this.texturePackFile, var1);
		return var2.exists() && var2.isFile();
	}

	public boolean isCompatible() {
		File var1 = new File(this.texturePackFile, "textures/");
		boolean var2 = var1.exists() && var1.isDirectory();
		boolean var3 = this.func_98140_c("terrain.png") || this.func_98140_c("gui/items.png");
		return var2 || !var3;
	}
}
