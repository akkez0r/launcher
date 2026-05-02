package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

public abstract class TexturePackImplementation implements ITexturePack {
	private final String texturePackID;
	private final String texturePackFileName;
	protected final File texturePackFile;
	protected String firstDescriptionLine;
	protected String secondDescriptionLine;
	private final ITexturePack field_98141_g;
	protected BufferedImage thumbnailImage;
	private int thumbnailTextureName = -1;

	protected TexturePackImplementation(String var1, File var2, String var3, ITexturePack var4) {
		this.texturePackID = var1;
		this.texturePackFileName = var3;
		this.texturePackFile = var2;
		this.field_98141_g = var4;
		this.loadThumbnailImage();
		this.loadDescription();
	}

	private static String trimStringToGUIWidth(String var0) {
		if(var0 != null && var0.length() > 34) {
			var0 = var0.substring(0, 34);
		}

		return var0;
	}

	private void loadThumbnailImage() {
		InputStream var1 = null;

		try {
			var1 = this.func_98137_a("/pack.png", false);
			this.thumbnailImage = ImageIO.read(var1);
		} catch (IOException var11) {
		} finally {
			try {
				if(var1 != null) {
					var1.close();
				}
			} catch (IOException var10) {
			}

		}

	}

	protected void loadDescription() {
		InputStream var1 = null;
		BufferedReader var2 = null;

		try {
			var1 = this.func_98139_b("/pack.txt");
			var2 = new BufferedReader(new InputStreamReader(var1));
			this.firstDescriptionLine = trimStringToGUIWidth(var2.readLine());
			this.secondDescriptionLine = trimStringToGUIWidth(var2.readLine());
		} catch (IOException var12) {
		} finally {
			try {
				if(var2 != null) {
					var2.close();
				}

				if(var1 != null) {
					var1.close();
				}
			} catch (IOException var11) {
			}

		}

	}

	public InputStream func_98137_a(String var1, boolean var2) throws IOException {
		try {
			return this.func_98139_b(var1);
		} catch (IOException var4) {
			if(this.field_98141_g != null && var2) {
				return this.field_98141_g.func_98137_a(var1, true);
			} else {
				throw var4;
			}
		}
	}

	public InputStream getResourceAsStream(String var1) throws IOException {
		return this.func_98137_a(var1, true);
	}

	protected abstract InputStream func_98139_b(String var1) throws IOException;

	public void deleteTexturePack(RenderEngine var1) {
		if(this.thumbnailImage != null && this.thumbnailTextureName != -1) {
			var1.deleteTexture(this.thumbnailTextureName);
		}

	}

	public void bindThumbnailTexture(RenderEngine var1) {
		if(this.thumbnailImage != null) {
			if(this.thumbnailTextureName == -1) {
				this.thumbnailTextureName = var1.allocateAndSetupTexture(this.thumbnailImage);
			}

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.thumbnailTextureName);
			var1.resetBoundTexture();
		} else {
			var1.bindTexture("/gui/unknown_pack.png");
		}

	}

	public boolean func_98138_b(String var1, boolean var2) {
		boolean var3 = this.func_98140_c(var1);
		return !var3 && var2 && this.field_98141_g != null ? this.field_98141_g.func_98138_b(var1, var2) : var3;
	}

	public abstract boolean func_98140_c(String var1);

	public String getTexturePackID() {
		return this.texturePackID;
	}

	public String getTexturePackFileName() {
		return this.texturePackFileName;
	}

	public String getFirstDescriptionLine() {
		return this.firstDescriptionLine;
	}

	public String getSecondDescriptionLine() {
		return this.secondDescriptionLine;
	}
}
