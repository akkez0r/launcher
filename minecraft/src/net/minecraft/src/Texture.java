package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class Texture {
	private int glTextureId;
	private int textureId;
	private int textureType;
	private final int width;
	private final int height;
	private final int textureDepth;
	private final int textureFormat;
	private final int textureTarget;
	private final int textureMinFilter;
	private final int textureMagFilter;
	private final int textureWrap;
	private final boolean mipmapActive;
	private final String textureName;
	private Rect2i textureRect;
	private boolean transferred;
	private boolean autoCreate;
	private boolean textureNotModified;
	private ByteBuffer textureData;

	private Texture(String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
		this.textureName = var1;
		this.textureType = var2;
		this.width = var3;
		this.height = var4;
		this.textureDepth = var5;
		this.textureFormat = var7;
		this.textureMinFilter = var8;
		this.textureMagFilter = var9;
		this.textureWrap = var6;
		this.textureRect = new Rect2i(0, 0, var3, var4);
		if(var4 == 1 && var5 == 1) {
			this.textureTarget = 3552;
		} else if(var5 == 1) {
			this.textureTarget = 3553;
		} else {
			this.textureTarget = '\u806f';
		}

		this.mipmapActive = var8 != 9728 && var8 != 9729 || var9 != 9728 && var9 != 9729;
		if(var2 != 2) {
			this.glTextureId = GL11.glGenTextures();
			GL11.glBindTexture(this.textureTarget, this.glTextureId);
			GL11.glTexParameteri(this.textureTarget, GL11.GL_TEXTURE_MIN_FILTER, var8);
			GL11.glTexParameteri(this.textureTarget, GL11.GL_TEXTURE_MAG_FILTER, var9);
			GL11.glTexParameteri(this.textureTarget, GL11.GL_TEXTURE_WRAP_S, var6);
			GL11.glTexParameteri(this.textureTarget, GL11.GL_TEXTURE_WRAP_T, var6);
		} else {
			this.glTextureId = -1;
		}

		this.textureId = TextureManager.instance().getNextTextureId();
	}

	public Texture(String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, BufferedImage var9) {
		this(var1, var2, var3, var4, 1, var5, var6, var7, var8, var9);
	}

	public Texture(String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, BufferedImage var10) {
		this(var1, var2, var3, var4, var5, var6, var7, var8, var9);
		if(var10 == null) {
			if(var3 != -1 && var4 != -1) {
				byte[] var11 = new byte[var3 * var4 * var5 * 4];

				for(int var12 = 0; var12 < var11.length; ++var12) {
					var11[var12] = 0;
				}

				this.textureData = ByteBuffer.allocateDirect(var11.length);
				this.textureData.clear();
				this.textureData.put(var11);
				this.textureData.position(0).limit(var11.length);
				if(this.autoCreate) {
					this.uploadTexture();
				} else {
					this.textureNotModified = false;
				}
			} else {
				this.transferred = false;
			}
		} else {
			this.transferred = true;
			this.transferFromImage(var10);
			if(var2 != 2) {
				this.uploadTexture();
				this.autoCreate = false;
			}
		}

	}

	public final Rect2i getTextureRect() {
		return this.textureRect;
	}

	public void fillRect(Rect2i var1, int var2) {
		if(this.textureTarget != '\u806f') {
			Rect2i var3 = new Rect2i(0, 0, this.width, this.height);
			var3.intersection(var1);
			this.textureData.position(0);

			for(int var4 = var3.getRectY(); var4 < var3.getRectY() + var3.getRectHeight(); ++var4) {
				int var5 = var4 * this.width * 4;

				for(int var6 = var3.getRectX(); var6 < var3.getRectX() + var3.getRectWidth(); ++var6) {
					this.textureData.put(var5 + var6 * 4 + 0, (byte)(var2 >> 24 & 255));
					this.textureData.put(var5 + var6 * 4 + 1, (byte)(var2 >> 16 & 255));
					this.textureData.put(var5 + var6 * 4 + 2, (byte)(var2 >> 8 & 255));
					this.textureData.put(var5 + var6 * 4 + 3, (byte)(var2 >> 0 & 255));
				}
			}

			if(this.autoCreate) {
				this.uploadTexture();
			} else {
				this.textureNotModified = false;
			}

		}
	}

	public void writeImage(String var1) {
		BufferedImage var2 = new BufferedImage(this.width, this.height, 2);
		ByteBuffer var3 = this.getTextureData();
		byte[] var4 = new byte[this.width * this.height * 4];
		var3.position(0);
		var3.get(var4);

		for(int var5 = 0; var5 < this.width; ++var5) {
			for(int var6 = 0; var6 < this.height; ++var6) {
				int var7 = var6 * this.width * 4 + var5 * 4;
				byte var8 = 0;
				int var10 = var8 | (var4[var7 + 2] & 255) << 0;
				var10 |= (var4[var7 + 1] & 255) << 8;
				var10 |= (var4[var7 + 0] & 255) << 16;
				var10 |= (var4[var7 + 3] & 255) << 24;
				var2.setRGB(var5, var6, var10);
			}
		}

		this.textureData.position(this.width * this.height * 4);

		try {
			ImageIO.write(var2, "png", new File(Minecraft.getMinecraftDir(), var1));
		} catch (IOException var9) {
			var9.printStackTrace();
		}

	}

	public void copyFrom(int var1, int var2, Texture var3, boolean var4) {
		if(this.textureTarget != '\u806f') {
			ByteBuffer var5 = var3.getTextureData();
			this.textureData.position(0);
			var5.position(0);

			for(int var6 = 0; var6 < var3.getHeight(); ++var6) {
				int var7 = var2 + var6;
				int var8 = var6 * var3.getWidth() * 4;
				int var9 = var7 * this.width * 4;
				if(var4) {
					var7 = var2 + (var3.getHeight() - var6);
				}

				for(int var10 = 0; var10 < var3.getWidth(); ++var10) {
					int var11 = var9 + (var10 + var1) * 4;
					int var12 = var8 + var10 * 4;
					if(var4) {
						var11 = var1 + var10 * this.width * 4 + var7 * 4;
					}

					this.textureData.put(var11 + 0, var5.get(var12 + 0));
					this.textureData.put(var11 + 1, var5.get(var12 + 1));
					this.textureData.put(var11 + 2, var5.get(var12 + 2));
					this.textureData.put(var11 + 3, var5.get(var12 + 3));
				}
			}

			this.textureData.position(this.width * this.height * 4);
			if(this.autoCreate) {
				this.uploadTexture();
			} else {
				this.textureNotModified = false;
			}

		}
	}

	public void func_104062_b(int var1, int var2, Texture var3) {
		GL11.glBindTexture(this.textureTarget, this.glTextureId);
		GL11.glTexSubImage2D(this.textureTarget, 0, var1, var2, var3.getWidth(), var3.getHeight(), this.textureFormat, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)((ByteBuffer)var3.getTextureData().position(0)));
		this.textureNotModified = true;
	}

	public void transferFromImage(BufferedImage var1) {
		if(this.textureTarget != '\u806f') {
			int var2 = var1.getWidth();
			int var3 = var1.getHeight();
			if(var2 <= this.width && var3 <= this.height) {
				int[] var4 = new int[]{3, 0, 1, 2};
				int[] var5 = new int[]{3, 2, 1, 0};
				int[] var6 = this.textureFormat == '\u80e1' ? var5 : var4;
				int[] var7 = new int[this.width * this.height];
				int var8 = var1.getTransparency();
				var1.getRGB(0, 0, this.width, this.height, var7, 0, var2);
				byte[] var9 = new byte[this.width * this.height * 4];

				for(int var10 = 0; var10 < this.height; ++var10) {
					for(int var11 = 0; var11 < this.width; ++var11) {
						int var12 = var10 * this.width + var11;
						int var13 = var12 * 4;
						var9[var13 + var6[0]] = (byte)(var7[var12] >> 24 & 255);
						var9[var13 + var6[1]] = (byte)(var7[var12] >> 16 & 255);
						var9[var13 + var6[2]] = (byte)(var7[var12] >> 8 & 255);
						var9[var13 + var6[3]] = (byte)(var7[var12] >> 0 & 255);
					}
				}

				this.textureData = ByteBuffer.allocateDirect(var9.length);
				this.textureData.clear();
				this.textureData.put(var9);
				this.textureData.limit(var9.length);
				if(this.autoCreate) {
					this.uploadTexture();
				} else {
					this.textureNotModified = false;
				}

			} else {
				Minecraft.getMinecraft().getLogAgent().logWarning("transferFromImage called with a BufferedImage with dimensions (" + var2 + ", " + var3 + ") larger than the Texture dimensions (" + this.width + ", " + this.height + "). Ignoring.");
			}
		}
	}

	public int getTextureId() {
		return this.textureId;
	}

	public int getGlTextureId() {
		return this.glTextureId;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public String getTextureName() {
		return this.textureName;
	}

	public void bindTexture(int var1) {
		if(this.textureDepth == 1) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		} else {
			GL11.glEnable(GL12.GL_TEXTURE_3D);
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + var1);
		GL11.glBindTexture(this.textureTarget, this.glTextureId);
		if(!this.textureNotModified) {
			this.uploadTexture();
		}

	}

	public void uploadTexture() {
		this.textureData.flip();
		if(this.height != 1 && this.textureDepth != 1) {
			GL12.glTexImage3D(this.textureTarget, 0, this.textureFormat, this.width, this.height, this.textureDepth, 0, this.textureFormat, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)this.textureData);
		} else if(this.height != 1) {
			GL11.glTexImage2D(this.textureTarget, 0, this.textureFormat, this.width, this.height, 0, this.textureFormat, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)this.textureData);
		} else {
			GL11.glTexImage1D(this.textureTarget, 0, this.textureFormat, this.width, 0, this.textureFormat, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)this.textureData);
		}

		this.textureNotModified = true;
	}

	public ByteBuffer getTextureData() {
		return this.textureData;
	}
}
