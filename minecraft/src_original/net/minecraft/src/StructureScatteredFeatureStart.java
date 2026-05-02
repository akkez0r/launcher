package net.minecraft.src;

import java.util.Random;

public class StructureScatteredFeatureStart extends StructureStart {
	public StructureScatteredFeatureStart(World var1, Random var2, int var3, int var4) {
		BiomeGenBase var5 = var1.getBiomeGenForCoords(var3 * 16 + 8, var4 * 16 + 8);
		if(var5 != BiomeGenBase.jungle && var5 != BiomeGenBase.jungleHills) {
			if(var5 == BiomeGenBase.swampland) {
				ComponentScatteredFeatureSwampHut var7 = new ComponentScatteredFeatureSwampHut(var2, var3 * 16, var4 * 16);
				this.components.add(var7);
			} else {
				ComponentScatteredFeatureDesertPyramid var8 = new ComponentScatteredFeatureDesertPyramid(var2, var3 * 16, var4 * 16);
				this.components.add(var8);
			}
		} else {
			ComponentScatteredFeatureJunglePyramid var6 = new ComponentScatteredFeatureJunglePyramid(var2, var3 * 16, var4 * 16);
			this.components.add(var6);
		}

		this.updateBoundingBox();
	}
}
