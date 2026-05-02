package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Bidi;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class FontRenderer {
	private int[] charWidth = new int[256];
	public int FONT_HEIGHT = 9;
	public Random fontRandom = new Random();
	private byte[] glyphWidth = new byte[65536];
	private int[] colorCode = new int[32];
	private final String fontTextureName;
	private final RenderEngine renderEngine;
	private float posX;
	private float posY;
	private boolean unicodeFlag;
	private boolean bidiFlag;
	private float red;
	private float blue;
	private float green;
	private float alpha;
	private int textColor;
	private boolean randomStyle = false;
	private boolean boldStyle = false;
	private boolean italicStyle = false;
	private boolean underlineStyle = false;
	private boolean strikethroughStyle = false;

	FontRenderer() {
		this.renderEngine = null;
		this.fontTextureName = null;
	}

	public FontRenderer(GameSettings var1, String var2, RenderEngine var3, boolean var4) {
		this.fontTextureName = var2;
		this.renderEngine = var3;
		this.unicodeFlag = var4;
		this.readFontData();
		var3.bindTexture(var2);

		for(int var5 = 0; var5 < 32; ++var5) {
			int var6 = (var5 >> 3 & 1) * 85;
			int var7 = (var5 >> 2 & 1) * 170 + var6;
			int var8 = (var5 >> 1 & 1) * 170 + var6;
			int var9 = (var5 >> 0 & 1) * 170 + var6;
			if(var5 == 6) {
				var7 += 85;
			}

			if(var1.anaglyph) {
				int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
				int var11 = (var7 * 30 + var8 * 70) / 100;
				int var12 = (var7 * 30 + var9 * 70) / 100;
				var7 = var10;
				var8 = var11;
				var9 = var12;
			}

			if(var5 >= 16) {
				var7 /= 4;
				var8 /= 4;
				var9 /= 4;
			}

			this.colorCode[var5] = (var7 & 255) << 16 | (var8 & 255) << 8 | var9 & 255;
		}

	}

	public void readFontData() {
		this.readGlyphSizes();
		this.readFontTexture(this.fontTextureName);
	}

	private void readFontTexture(String var1) {
		BufferedImage var2;
		try {
			var2 = ImageIO.read(RenderEngine.class.getResourceAsStream(var1));
		} catch (IOException var15) {
			throw new RuntimeException(var15);
		}

		int var3 = var2.getWidth();
		int var4 = var2.getHeight();
		int[] var5 = new int[var3 * var4];
		var2.getRGB(0, 0, var3, var4, var5, 0, var3);

		for(int var6 = 0; var6 < 256; ++var6) {
			int var7 = var6 % 16;
			int var8 = var6 / 16;

			int var9;
			for(var9 = 7; var9 >= 0; --var9) {
				int var10 = var7 * 8 + var9;
				boolean var11 = true;

				for(int var12 = 0; var12 < 8 && var11; ++var12) {
					int var13 = (var8 * 8 + var12) * var3;
					int var14 = var5[var10 + var13] & 255;
					if(var14 > 0) {
						var11 = false;
					}
				}

				if(!var11) {
					break;
				}
			}

			if(var6 == 32) {
				var9 = 2;
			}

			this.charWidth[var6] = var9 + 2;
		}

	}

	private void readGlyphSizes() {
		try {
			InputStream var1 = Minecraft.getMinecraft().texturePackList.getSelectedTexturePack().getResourceAsStream("/font/glyph_sizes.bin");
			var1.read(this.glyphWidth);
		} catch (IOException var2) {
			throw new RuntimeException(var2);
		}
	}

	private float renderCharAtPos(int var1, char var2, boolean var3) {
		return var2 == 32 ? 4.0F : (var1 > 0 && !this.unicodeFlag ? this.renderDefaultChar(var1 + 32, var3) : this.renderUnicodeChar(var2, var3));
	}

	private float renderDefaultChar(int var1, boolean var2) {
		float var3 = (float)(var1 % 16 * 8);
		float var4 = (float)(var1 / 16 * 8);
		float var5 = var2 ? 1.0F : 0.0F;
		this.renderEngine.bindTexture(this.fontTextureName);
		float var6 = (float)this.charWidth[var1] - 0.01F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(var3 / 128.0F, var4 / 128.0F);
		GL11.glVertex3f(this.posX + var5, this.posY, 0.0F);
		GL11.glTexCoord2f(var3 / 128.0F, (var4 + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX - var5, this.posY + 7.99F, 0.0F);
		GL11.glTexCoord2f((var3 + var6) / 128.0F, var4 / 128.0F);
		GL11.glVertex3f(this.posX + var6 + var5, this.posY, 0.0F);
		GL11.glTexCoord2f((var3 + var6) / 128.0F, (var4 + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX + var6 - var5, this.posY + 7.99F, 0.0F);
		GL11.glEnd();
		return (float)this.charWidth[var1];
	}

	private void loadGlyphTexture(int var1) {
		String var2 = String.format("/font/glyph_%02X.png", new Object[]{Integer.valueOf(var1)});
		this.renderEngine.bindTexture(var2);
	}

	private float renderUnicodeChar(char var1, boolean var2) {
		if(this.glyphWidth[var1] == 0) {
			return 0.0F;
		} else {
			int var3 = var1 / 256;
			this.loadGlyphTexture(var3);
			int var4 = this.glyphWidth[var1] >>> 4;
			int var5 = this.glyphWidth[var1] & 15;
			float var6 = (float)var4;
			float var7 = (float)(var5 + 1);
			float var8 = (float)(var1 % 16 * 16) + var6;
			float var9 = (float)((var1 & 255) / 16 * 16);
			float var10 = var7 - var6 - 0.02F;
			float var11 = var2 ? 1.0F : 0.0F;
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glTexCoord2f(var8 / 256.0F, var9 / 256.0F);
			GL11.glVertex3f(this.posX + var11, this.posY, 0.0F);
			GL11.glTexCoord2f(var8 / 256.0F, (var9 + 15.98F) / 256.0F);
			GL11.glVertex3f(this.posX - var11, this.posY + 7.99F, 0.0F);
			GL11.glTexCoord2f((var8 + var10) / 256.0F, var9 / 256.0F);
			GL11.glVertex3f(this.posX + var10 / 2.0F + var11, this.posY, 0.0F);
			GL11.glTexCoord2f((var8 + var10) / 256.0F, (var9 + 15.98F) / 256.0F);
			GL11.glVertex3f(this.posX + var10 / 2.0F - var11, this.posY + 7.99F, 0.0F);
			GL11.glEnd();
			return (var7 - var6) / 2.0F + 1.0F;
		}
	}

	public int drawStringWithShadow(String var1, int var2, int var3, int var4) {
		return this.drawString(var1, var2, var3, var4, true);
	}

	public int drawString(String var1, int var2, int var3, int var4) {
		return this.drawString(var1, var2, var3, var4, false);
	}

	public int drawString(String var1, int var2, int var3, int var4, boolean var5) {
		this.resetStyles();
		if(this.bidiFlag) {
			var1 = this.bidiReorder(var1);
		}

		int var6;
		if(var5) {
			var6 = this.renderString(var1, var2 + 1, var3 + 1, var4, true);
			var6 = Math.max(var6, this.renderString(var1, var2, var3, var4, false));
		} else {
			var6 = this.renderString(var1, var2, var3, var4, false);
		}

		return var6;
	}

	private String bidiReorder(String var1) {
		if(var1 != null && Bidi.requiresBidi(var1.toCharArray(), 0, var1.length())) {
			Bidi var2 = new Bidi(var1, -2);
			byte[] var3 = new byte[var2.getRunCount()];
			String[] var4 = new String[var3.length];

			int var7;
			for(int var5 = 0; var5 < var3.length; ++var5) {
				int var6 = var2.getRunStart(var5);
				var7 = var2.getRunLimit(var5);
				int var8 = var2.getRunLevel(var5);
				String var9 = var1.substring(var6, var7);
				var3[var5] = (byte)var8;
				var4[var5] = var9;
			}

			String[] var11 = (String[])var4.clone();
			Bidi.reorderVisually(var3, 0, var4, 0, var3.length);
			StringBuilder var12 = new StringBuilder();

			for(var7 = 0; var7 < var4.length; ++var7) {
				byte var13 = var3[var7];

				int var14;
				for(var14 = 0; var14 < var11.length; ++var14) {
					if(var11[var14].equals(var4[var7])) {
						var13 = var3[var14];
						break;
					}
				}

				if((var13 & 1) == 0) {
					var12.append(var4[var7]);
				} else {
					for(var14 = var4[var7].length() - 1; var14 >= 0; --var14) {
						char var10 = var4[var7].charAt(var14);
						if(var10 == 40) {
							var10 = 41;
						} else if(var10 == 41) {
							var10 = 40;
						}

						var12.append(var10);
					}
				}
			}

			return var12.toString();
		} else {
			return var1;
		}
	}

	private void resetStyles() {
		this.randomStyle = false;
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	private void renderStringAtPos(String var1, boolean var2) {
		for(int var3 = 0; var3 < var1.length(); ++var3) {
			char var4 = var1.charAt(var3);
			int var5;
			int var6;
			if(var4 == 167 && var3 + 1 < var1.length()) {
				var5 = "0123456789abcdefklmnor".indexOf(var1.toLowerCase().charAt(var3 + 1));
				if(var5 < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					if(var5 < 0 || var5 > 15) {
						var5 = 15;
					}

					if(var2) {
						var5 += 16;
					}

					var6 = this.colorCode[var5];
					this.textColor = var6;
					GL11.glColor4f((float)(var6 >> 16) / 255.0F, (float)(var6 >> 8 & 255) / 255.0F, (float)(var6 & 255) / 255.0F, this.alpha);
				} else if(var5 == 16) {
					this.randomStyle = true;
				} else if(var5 == 17) {
					this.boldStyle = true;
				} else if(var5 == 18) {
					this.strikethroughStyle = true;
				} else if(var5 == 19) {
					this.underlineStyle = true;
				} else if(var5 == 20) {
					this.italicStyle = true;
				} else if(var5 == 21) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
				}

				++var3;
			} else {
				var5 = ChatAllowedCharacters.allowedCharacters.indexOf(var4);
				if(this.randomStyle && var5 > 0) {
					do {
						var6 = this.fontRandom.nextInt(ChatAllowedCharacters.allowedCharacters.length());
					} while(this.charWidth[var5 + 32] != this.charWidth[var6 + 32]);

					var5 = var6;
				}

				float var11 = this.unicodeFlag ? 0.5F : 1.0F;
				boolean var7 = (var5 <= 0 || this.unicodeFlag) && var2;
				if(var7) {
					this.posX -= var11;
					this.posY -= var11;
				}

				float var8 = this.renderCharAtPos(var5, var4, this.italicStyle);
				if(var7) {
					this.posX += var11;
					this.posY += var11;
				}

				if(this.boldStyle) {
					this.posX += var11;
					if(var7) {
						this.posX -= var11;
						this.posY -= var11;
					}

					this.renderCharAtPos(var5, var4, this.italicStyle);
					this.posX -= var11;
					if(var7) {
						this.posX += var11;
						this.posY += var11;
					}

					++var8;
				}

				Tessellator var9;
				if(this.strikethroughStyle) {
					var9 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					var9.startDrawingQuads();
					var9.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
					var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
					var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
					var9.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
					var9.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				if(this.underlineStyle) {
					var9 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					var9.startDrawingQuads();
					int var10 = this.underlineStyle ? -1 : 0;
					var9.addVertex((double)(this.posX + (float)var10), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
					var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
					var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
					var9.addVertex((double)(this.posX + (float)var10), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
					var9.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				this.posX += (float)((int)var8);
			}
		}

	}

	private int renderStringAligned(String var1, int var2, int var3, int var4, int var5, boolean var6) {
		if(this.bidiFlag) {
			var1 = this.bidiReorder(var1);
			int var7 = this.getStringWidth(var1);
			var2 = var2 + var4 - var7;
		}

		return this.renderString(var1, var2, var3, var5, var6);
	}

	private int renderString(String var1, int var2, int var3, int var4, boolean var5) {
		if(var1 == null) {
			return 0;
		} else {
			if((var4 & -67108864) == 0) {
				var4 |= -16777216;
			}

			if(var5) {
				var4 = (var4 & 16579836) >> 2 | var4 & -16777216;
			}

			this.red = (float)(var4 >> 16 & 255) / 255.0F;
			this.blue = (float)(var4 >> 8 & 255) / 255.0F;
			this.green = (float)(var4 & 255) / 255.0F;
			this.alpha = (float)(var4 >> 24 & 255) / 255.0F;
			GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
			this.posX = (float)var2;
			this.posY = (float)var3;
			this.renderStringAtPos(var1, var5);
			return (int)this.posX;
		}
	}

	public int getStringWidth(String var1) {
		if(var1 == null) {
			return 0;
		} else {
			int var2 = 0;
			boolean var3 = false;

			for(int var4 = 0; var4 < var1.length(); ++var4) {
				char var5 = var1.charAt(var4);
				int var6 = this.getCharWidth(var5);
				if(var6 < 0 && var4 < var1.length() - 1) {
					++var4;
					var5 = var1.charAt(var4);
					if(var5 != 108 && var5 != 76) {
						if(var5 == 114 || var5 == 82) {
							var3 = false;
						}
					} else {
						var3 = true;
					}

					var6 = 0;
				}

				var2 += var6;
				if(var3) {
					++var2;
				}
			}

			return var2;
		}
	}

	public int getCharWidth(char var1) {
		if(var1 == 167) {
			return -1;
		} else if(var1 == 32) {
			return 4;
		} else {
			int var2 = ChatAllowedCharacters.allowedCharacters.indexOf(var1);
			if(var2 >= 0 && !this.unicodeFlag) {
				return this.charWidth[var2 + 32];
			} else if(this.glyphWidth[var1] != 0) {
				int var3 = this.glyphWidth[var1] >>> 4;
				int var4 = this.glyphWidth[var1] & 15;
				if(var4 > 7) {
					var4 = 15;
					var3 = 0;
				}

				++var4;
				return (var4 - var3) / 2 + 1;
			} else {
				return 0;
			}
		}
	}

	public String trimStringToWidth(String var1, int var2) {
		return this.trimStringToWidth(var1, var2, false);
	}

	public String trimStringToWidth(String var1, int var2, boolean var3) {
		StringBuilder var4 = new StringBuilder();
		int var5 = 0;
		int var6 = var3 ? var1.length() - 1 : 0;
		int var7 = var3 ? -1 : 1;
		boolean var8 = false;
		boolean var9 = false;

		for(int var10 = var6; var10 >= 0 && var10 < var1.length() && var5 < var2; var10 += var7) {
			char var11 = var1.charAt(var10);
			int var12 = this.getCharWidth(var11);
			if(var8) {
				var8 = false;
				if(var11 != 108 && var11 != 76) {
					if(var11 == 114 || var11 == 82) {
						var9 = false;
					}
				} else {
					var9 = true;
				}
			} else if(var12 < 0) {
				var8 = true;
			} else {
				var5 += var12;
				if(var9) {
					++var5;
				}
			}

			if(var5 > var2) {
				break;
			}

			if(var3) {
				var4.insert(0, var11);
			} else {
				var4.append(var11);
			}
		}

		return var4.toString();
	}

	private String trimStringNewline(String var1) {
		while(var1 != null && var1.endsWith("\n")) {
			var1 = var1.substring(0, var1.length() - 1);
		}

		return var1;
	}

	public void drawSplitString(String var1, int var2, int var3, int var4, int var5) {
		this.resetStyles();
		this.textColor = var5;
		var1 = this.trimStringNewline(var1);
		this.renderSplitString(var1, var2, var3, var4, false);
	}

	private void renderSplitString(String var1, int var2, int var3, int var4, boolean var5) {
		List var6 = this.listFormattedStringToWidth(var1, var4);

		for(Iterator var7 = var6.iterator(); var7.hasNext(); var3 += this.FONT_HEIGHT) {
			String var8 = (String)var7.next();
			this.renderStringAligned(var8, var2, var3, var4, this.textColor, var5);
		}

	}

	public int splitStringWidth(String var1, int var2) {
		return this.FONT_HEIGHT * this.listFormattedStringToWidth(var1, var2).size();
	}

	public void setUnicodeFlag(boolean var1) {
		this.unicodeFlag = var1;
	}

	public boolean getUnicodeFlag() {
		return this.unicodeFlag;
	}

	public void setBidiFlag(boolean var1) {
		this.bidiFlag = var1;
	}

	public List listFormattedStringToWidth(String var1, int var2) {
		return Arrays.asList(this.wrapFormattedStringToWidth(var1, var2).split("\n"));
	}

	String wrapFormattedStringToWidth(String var1, int var2) {
		int var3 = this.sizeStringToWidth(var1, var2);
		if(var1.length() <= var3) {
			return var1;
		} else {
			String var4 = var1.substring(0, var3);
			char var5 = var1.charAt(var3);
			boolean var6 = var5 == 32 || var5 == 10;
			String var7 = getFormatFromString(var4) + var1.substring(var3 + (var6 ? 1 : 0));
			return var4 + "\n" + this.wrapFormattedStringToWidth(var7, var2);
		}
	}

	private int sizeStringToWidth(String var1, int var2) {
		int var3 = var1.length();
		int var4 = 0;
		int var5 = 0;
		int var6 = -1;

		for(boolean var7 = false; var5 < var3; ++var5) {
			char var8 = var1.charAt(var5);
			switch(var8) {
			case '\n':
				--var5;
				break;
			case ' ':
				var6 = var5;
			default:
				var4 += this.getCharWidth(var8);
				if(var7) {
					++var4;
				}
				break;
			case '\u00a7':
				if(var5 < var3 - 1) {
					++var5;
					char var9 = var1.charAt(var5);
					if(var9 != 108 && var9 != 76) {
						if(var9 == 114 || var9 == 82 || isFormatColor(var9)) {
							var7 = false;
						}
					} else {
						var7 = true;
					}
				}
			}

			if(var8 == 10) {
				++var5;
				var6 = var5;
				break;
			}

			if(var4 > var2) {
				break;
			}
		}

		return var5 != var3 && var6 != -1 && var6 < var5 ? var6 : var5;
	}

	private static boolean isFormatColor(char var0) {
		return var0 >= 48 && var0 <= 57 || var0 >= 97 && var0 <= 102 || var0 >= 65 && var0 <= 70;
	}

	private static boolean isFormatSpecial(char var0) {
		return var0 >= 107 && var0 <= 111 || var0 >= 75 && var0 <= 79 || var0 == 114 || var0 == 82;
	}

	private static String getFormatFromString(String var0) {
		String var1 = "";
		int var2 = -1;
		int var3 = var0.length();

		while(true) {
			var2 = var0.indexOf(167, var2 + 1);
			if(var2 == -1) {
				return var1;
			}

			if(var2 < var3 - 1) {
				char var4 = var0.charAt(var2 + 1);
				if(isFormatColor(var4)) {
					var1 = "\u00a7" + var4;
				} else if(isFormatSpecial(var4)) {
					var1 = var1 + "\u00a7" + var4;
				}
			}
		}
	}

	public boolean getBidiFlag() {
		return this.bidiFlag;
	}
}
