package net.minecraft.src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderEngine {
	private HashMap textureMap = new HashMap();
	private HashMap textureContentsMap = new HashMap();
	private IntHashMap textureNameToImageMap = new IntHashMap();
	private IntBuffer imageData = GLAllocation.createDirectIntBuffer(4194304);
	private Map urlToImageDataMap = new HashMap();
	private GameSettings options;
	private TexturePackList texturePack;
	private BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);
	private final TextureMap textureMapBlocks;
	private final TextureMap textureMapItems;
	private int boundTexture;

	public RenderEngine(TexturePackList var1, GameSettings var2) {
		this.texturePack = var1;
		this.options = var2;
		Graphics var3 = this.missingTextureImage.getGraphics();
		var3.setColor(Color.WHITE);
		var3.fillRect(0, 0, 64, 64);
		var3.setColor(Color.BLACK);
		int var4 = 10;
		int var5 = 0;

		while(var4 < 64) {
			String var6 = var5++ % 2 == 0 ? "missing" : "texture";
			var3.drawString(var6, 1, var4);
			var4 += var3.getFont().getSize();
			if(var5 % 2 == 0) {
				var4 += 5;
			}
		}

		var3.dispose();
		this.textureMapBlocks = new TextureMap(0, "terrain", "textures/blocks/", this.missingTextureImage);
		this.textureMapItems = new TextureMap(1, "items", "textures/items/", this.missingTextureImage);
	}

	public int[] getTextureContents(String var1) {
		ITexturePack var2 = this.texturePack.getSelectedTexturePack();
		int[] var3 = (int[])this.textureContentsMap.get(var1);
		if(var3 != null) {
			return var3;
		} else {
			try {
				InputStream var7 = var2.getResourceAsStream(var1);
				int[] var4;
				if(var7 == null) {
					var4 = this.getImageContentsAndAllocate(this.missingTextureImage);
				} else {
					var4 = this.getImageContentsAndAllocate(this.readTextureImage(var7));
				}

				this.textureContentsMap.put(var1, var4);
				return var4;
			} catch (IOException var6) {
				var6.printStackTrace();
				int[] var5 = this.getImageContentsAndAllocate(this.missingTextureImage);
				this.textureContentsMap.put(var1, var5);
				return var5;
			}
		}
	}

	private int[] getImageContentsAndAllocate(BufferedImage var1) {
		return this.getImageContents(var1, new int[var1.getWidth() * var1.getHeight()]);
	}

	private int[] getImageContents(BufferedImage var1, int[] var2) {
		int var3 = var1.getWidth();
		int var4 = var1.getHeight();
		var1.getRGB(0, 0, var3, var4, var2, 0, var3);
		return var2;
	}

	public void bindTexture(String var1) {
		this.bindTexture(this.getTexture(var1));
	}

	private void bindTexture(int var1) {
		if(var1 != this.boundTexture) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1);
			this.boundTexture = var1;
		}

	}

	public void resetBoundTexture() {
		this.boundTexture = -1;
	}

	private int getTexture(String var1) {
		if(var1.equals("/terrain.png")) {
			this.textureMapBlocks.getTexture().bindTexture(0);
			return this.textureMapBlocks.getTexture().getGlTextureId();
		} else if(var1.equals("/gui/items.png")) {
			this.textureMapItems.getTexture().bindTexture(0);
			return this.textureMapItems.getTexture().getGlTextureId();
		} else {
			Integer var2 = (Integer)this.textureMap.get(var1);
			if(var2 != null) {
				return var2.intValue();
			} else {
				String var8 = var1;

				try {
					int var3 = GLAllocation.generateTextureNames();
					boolean var9 = var1.startsWith("%blur%");
					if(var9) {
						var1 = var1.substring(6);
					}

					boolean var5 = var1.startsWith("%clamp%");
					if(var5) {
						var1 = var1.substring(7);
					}

					InputStream var6 = this.texturePack.getSelectedTexturePack().getResourceAsStream(var1);
					if(var6 == null) {
						this.setupTextureExt(this.missingTextureImage, var3, var9, var5);
					} else {
						this.setupTextureExt(this.readTextureImage(var6), var3, var9, var5);
					}

					this.textureMap.put(var8, Integer.valueOf(var3));
					return var3;
				} catch (Exception var7) {
					var7.printStackTrace();
					int var4 = GLAllocation.generateTextureNames();
					this.setupTexture(this.missingTextureImage, var4);
					this.textureMap.put(var1, Integer.valueOf(var4));
					return var4;
				}
			}
		}
	}

	public int allocateAndSetupTexture(BufferedImage var1) {
		int var2 = GLAllocation.generateTextureNames();
		this.setupTexture(var1, var2);
		this.textureNameToImageMap.addKey(var2, var1);
		return var2;
	}

	public void setupTexture(BufferedImage var1, int var2) {
		this.setupTextureExt(var1, var2, false, false);
	}

	public void setupTextureExt(BufferedImage var1, int var2, boolean var3, boolean var4) {
		this.bindTexture(var2);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		if(var3) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		}

		if(var4) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		int var5 = var1.getWidth();
		int var6 = var1.getHeight();
		int[] var7 = new int[var5 * var6];
		var1.getRGB(0, 0, var5, var6, var7, 0, var5);
		if(this.options != null && this.options.anaglyph) {
			var7 = this.colorToAnaglyph(var7);
		}

		this.imageData.clear();
		this.imageData.put(var7);
		this.imageData.position(0).limit(var7.length);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, var5, var6, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)this.imageData);
	}

	private int[] colorToAnaglyph(int[] var1) {
		int[] var2 = new int[var1.length];

		for(int var3 = 0; var3 < var1.length; ++var3) {
			int var4 = var1[var3] >> 24 & 255;
			int var5 = var1[var3] >> 16 & 255;
			int var6 = var1[var3] >> 8 & 255;
			int var7 = var1[var3] & 255;
			int var8 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
			int var9 = (var5 * 30 + var6 * 70) / 100;
			int var10 = (var5 * 30 + var7 * 70) / 100;
			var2[var3] = var4 << 24 | var8 << 16 | var9 << 8 | var10;
		}

		return var2;
	}

	public void createTextureFromBytes(int[] var1, int var2, int var3, int var4) {
		this.bindTexture(var4);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		if(this.options != null && this.options.anaglyph) {
			var1 = this.colorToAnaglyph(var1);
		}

		this.imageData.clear();
		this.imageData.put(var1);
		this.imageData.position(0).limit(var1.length);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, var2, var3, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)this.imageData);
	}

	public void deleteTexture(int var1) {
		this.textureNameToImageMap.removeObject(var1);
		GL11.glDeleteTextures(var1);
	}

	public int getTextureForDownloadableImage(String var1, String var2) {
		ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var3 != null && var3.image != null && !var3.textureSetupComplete) {
			if(var3.textureName < 0) {
				var3.textureName = this.allocateAndSetupTexture(var3.image);
			} else {
				this.setupTexture(var3.image, var3.textureName);
			}

			var3.textureSetupComplete = true;
		}

		return var3 != null && var3.textureName >= 0 ? var3.textureName : (var2 == null ? -1 : this.getTexture(var2));
	}

	public boolean hasImageData(String var1) {
		return this.urlToImageDataMap.containsKey(var1);
	}

	public ThreadDownloadImageData obtainImageData(String var1, IImageBuffer var2) {
		ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var3 == null) {
			this.urlToImageDataMap.put(var1, new ThreadDownloadImageData(var1, var2));
		} else {
			++var3.referenceCount;
		}

		return var3;
	}

	public void releaseImageData(String var1) {
		ThreadDownloadImageData var2 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var2 != null) {
			--var2.referenceCount;
			if(var2.referenceCount == 0) {
				if(var2.textureName >= 0) {
					this.deleteTexture(var2.textureName);
				}

				this.urlToImageDataMap.remove(var1);
			}
		}

	}

	public void updateDynamicTextures() {
		this.textureMapBlocks.updateAnimations();
		this.textureMapItems.updateAnimations();
	}

	public void refreshTextures() {
		ITexturePack var1 = this.texturePack.getSelectedTexturePack();
		this.refreshTextureMaps();
		Iterator var2 = this.textureNameToImageMap.getKeySet().iterator();

		BufferedImage var4;
		while(var2.hasNext()) {
			int var3 = ((Integer)var2.next()).intValue();
			var4 = (BufferedImage)this.textureNameToImageMap.lookup(var3);
			this.setupTexture(var4, var3);
		}

		ThreadDownloadImageData var10;
		for(var2 = this.urlToImageDataMap.values().iterator(); var2.hasNext(); var10.textureSetupComplete = false) {
			var10 = (ThreadDownloadImageData)var2.next();
		}

		var2 = this.textureMap.keySet().iterator();

		String var11;
		while(var2.hasNext()) {
			var11 = (String)var2.next();

			try {
				int var12 = ((Integer)this.textureMap.get(var11)).intValue();
				boolean var6 = var11.startsWith("%blur%");
				if(var6) {
					var11 = var11.substring(6);
				}

				boolean var7 = var11.startsWith("%clamp%");
				if(var7) {
					var11 = var11.substring(7);
				}

				BufferedImage var5 = this.readTextureImage(var1.getResourceAsStream(var11));
				this.setupTextureExt(var5, var12, var6, var7);
			} catch (IOException var9) {
				var9.printStackTrace();
			}
		}

		var2 = this.textureContentsMap.keySet().iterator();

		while(var2.hasNext()) {
			var11 = (String)var2.next();

			try {
				var4 = this.readTextureImage(var1.getResourceAsStream(var11));
				this.getImageContents(var4, (int[])this.textureContentsMap.get(var11));
			} catch (IOException var8) {
				var8.printStackTrace();
			}
		}

		Minecraft.getMinecraft().fontRenderer.readFontData();
		Minecraft.getMinecraft().standardGalacticFontRenderer.readFontData();
	}

	private BufferedImage readTextureImage(InputStream var1) throws IOException {
		BufferedImage var2 = ImageIO.read(var1);
		var1.close();
		return var2;
	}

	public void refreshTextureMaps() {
		this.textureMapBlocks.refreshTextures();
		this.textureMapItems.refreshTextures();
	}

	public Icon getMissingIcon(int var1) {
		switch(var1) {
		case 0:
			return this.textureMapBlocks.getMissingIcon();
		case 1:
		default:
			return this.textureMapItems.getMissingIcon();
		}
	}
}
