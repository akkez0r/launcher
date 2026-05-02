package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;

public class TextureManager {
	private static TextureManager instance;
	private int nextTextureID = 0;
	private final HashMap texturesMap = new HashMap();
	private final HashMap mapNameToId = new HashMap();

	public static void init() {
		instance = new TextureManager();
	}

	public static TextureManager instance() {
		return instance;
	}

	public int getNextTextureId() {
		return this.nextTextureID++;
	}

	public void registerTexture(String var1, Texture var2) {
		this.mapNameToId.put(var1, Integer.valueOf(var2.getTextureId()));
		if(!this.texturesMap.containsKey(Integer.valueOf(var2.getTextureId()))) {
			this.texturesMap.put(Integer.valueOf(var2.getTextureId()), var2);
		}

	}

	public void registerTexture(Texture var1) {
		if(this.texturesMap.containsValue(var1)) {
			Minecraft.getMinecraft().getLogAgent().logWarning("TextureManager.registerTexture called, but this texture has already been registered. ignoring.");
		} else {
			this.texturesMap.put(Integer.valueOf(var1.getTextureId()), var1);
		}
	}

	public Stitcher createStitcher(String var1) {
		int var2 = Minecraft.getGLMaximumTextureSize();
		return new Stitcher(var1, var2, var2, true);
	}

	public List createTexture(String var1) {
		ArrayList var2 = new ArrayList();
		ITexturePack var3 = Minecraft.getMinecraft().texturePackList.getSelectedTexturePack();

		try {
			BufferedImage var9 = ImageIO.read(var3.getResourceAsStream("/" + var1));
			int var10 = var9.getHeight();
			int var11 = var9.getWidth();
			String var12 = this.getBasename(var1);
			if(this.hasAnimationTxt(var1, var3)) {
				int var13 = var11;
				int var14 = var11;
				int var15 = var10 / var11;

				for(int var16 = 0; var16 < var15; ++var16) {
					Texture var17 = this.makeTexture(var12, 2, var13, var14, 10496, 6408, 9728, 9728, false, var9.getSubimage(0, var14 * var16, var13, var14));
					var2.add(var17);
				}
			} else if(var11 == var10) {
				var2.add(this.makeTexture(var12, 2, var11, var10, 10496, 6408, 9728, 9728, false, var9));
			} else {
				Minecraft.getMinecraft().getLogAgent().logWarning("TextureManager.createTexture: Skipping " + var1 + " because of broken aspect ratio and not animation");
			}

			return var2;
		} catch (FileNotFoundException var18) {
			Minecraft.getMinecraft().getLogAgent().logWarning("TextureManager.createTexture called for file " + var1 + ", but that file does not exist. Ignoring.");
		} catch (IOException var19) {
			Minecraft.getMinecraft().getLogAgent().logWarning("TextureManager.createTexture encountered an IOException when trying to read file " + var1 + ". Ignoring.");
		}

		return var2;
	}

	private String getBasename(String var1) {
		File var2 = new File(var1);
		return var2.getName().substring(0, var2.getName().lastIndexOf(46));
	}

	private boolean hasAnimationTxt(String var1, ITexturePack var2) {
		String var3 = "/" + var1.substring(0, var1.lastIndexOf(46)) + ".txt";
		boolean var4 = var2.func_98138_b("/" + var1, false);
		return Minecraft.getMinecraft().texturePackList.getSelectedTexturePack().func_98138_b(var3, !var4);
	}

	public Texture makeTexture(String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, BufferedImage var10) {
		Texture var11 = new Texture(var1, var2, var3, var4, var5, var6, var7, var8, var10);
		this.registerTexture(var11);
		return var11;
	}

	public Texture createEmptyTexture(String var1, int var2, int var3, int var4, int var5) {
		return this.makeTexture(var1, var2, var3, var4, 10496, var5, 9728, 9728, false, (BufferedImage)null);
	}
}
