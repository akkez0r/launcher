package net.minecraft.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TexturePackDefault extends TexturePackImplementation {
	public TexturePackDefault() {
		super("default", (File)null, "Default", (ITexturePack)null);
	}

	protected void loadDescription() {
		this.firstDescriptionLine = "The default look of Minecraft";
	}

	public boolean func_98140_c(String var1) {
		return TexturePackDefault.class.getResourceAsStream(var1) != null;
	}

	public boolean isCompatible() {
		return true;
	}

	protected InputStream func_98139_b(String var1) throws IOException {
		InputStream var2 = TexturePackDefault.class.getResourceAsStream(var1);
		if(var2 == null) {
			throw new FileNotFoundException(var1);
		} else {
			return var2;
		}
	}
}
