package net.minecraft.src;

import java.util.Random;

public class BlockGrass extends Block {
	private Icon iconGrassTop;
	private Icon iconSnowSide;
	private Icon iconGrassSideOverlay;

	protected BlockGrass(int var1) {
		super(var1, Material.grass);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.iconGrassTop : (var1 == 0 ? Block.dirt.getBlockTextureFromSide(var1) : this.blockIcon);
	}

	public Icon getBlockTexture(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(var5 == 1) {
			return this.iconGrassTop;
		} else if(var5 == 0) {
			return Block.dirt.getBlockTextureFromSide(var5);
		} else {
			Material var6 = var1.getBlockMaterial(var2, var3 + 1, var4);
			return var6 != Material.snow && var6 != Material.craftedSnow ? this.blockIcon : this.iconSnowSide;
		}
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("grass_side");
		this.iconGrassTop = var1.registerIcon("grass_top");
		this.iconSnowSide = var1.registerIcon("snow_side");
		this.iconGrassSideOverlay = var1.registerIcon("grass_side_overlay");
	}

	public int getBlockColor() {
		double var1 = 0.5D;
		double var3 = 1.0D;
		return ColorizerGrass.getGrassColor(var1, var3);
	}

	public int getRenderColor(int var1) {
		return this.getBlockColor();
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = 0;
		int var6 = 0;
		int var7 = 0;

		for(int var8 = -1; var8 <= 1; ++var8) {
			for(int var9 = -1; var9 <= 1; ++var9) {
				int var10 = var1.getBiomeGenForCoords(var2 + var9, var4 + var8).getBiomeGrassColor();
				var5 += (var10 & 16711680) >> 16;
				var6 += (var10 & '\uff00') >> 8;
				var7 += var10 & 255;
			}
		}

		return (var5 / 9 & 255) << 16 | (var6 / 9 & 255) << 8 | var7 / 9 & 255;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			if(var1.getBlockLightValue(var2, var3 + 1, var4) < 4 && Block.lightOpacity[var1.getBlockId(var2, var3 + 1, var4)] > 2) {
				var1.setBlock(var2, var3, var4, Block.dirt.blockID);
			} else if(var1.getBlockLightValue(var2, var3 + 1, var4) >= 9) {
				for(int var6 = 0; var6 < 4; ++var6) {
					int var7 = var2 + var5.nextInt(3) - 1;
					int var8 = var3 + var5.nextInt(5) - 3;
					int var9 = var4 + var5.nextInt(3) - 1;
					int var10 = var1.getBlockId(var7, var8 + 1, var9);
					if(var1.getBlockId(var7, var8, var9) == Block.dirt.blockID && var1.getBlockLightValue(var7, var8 + 1, var9) >= 4 && Block.lightOpacity[var10] <= 2) {
						var1.setBlock(var7, var8, var9, Block.grass.blockID);
					}
				}
			}

		}
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.dirt.idDropped(0, var2, var3);
	}

	public static Icon getIconSideOverlay() {
		return Block.grass.iconGrassSideOverlay;
	}
}
