package net.minecraft.src;

import java.util.Random;

public class BiomeGenHills extends BiomeGenBase {
	private WorldGenerator theWorldGenerator = new WorldGenMinable(Block.silverfish.blockID, 8);

	protected BiomeGenHills(int var1) {
		super(var1);
	}

	public void decorate(World var1, Random var2, int var3, int var4) {
		super.decorate(var1, var2, var3, var4);
		int var5 = 3 + var2.nextInt(6);

		int var6;
		int var7;
		int var8;
		for(var6 = 0; var6 < var5; ++var6) {
			var7 = var3 + var2.nextInt(16);
			var8 = var2.nextInt(28) + 4;
			int var9 = var4 + var2.nextInt(16);
			int var10 = var1.getBlockId(var7, var8, var9);
			if(var10 == Block.stone.blockID) {
				var1.setBlock(var7, var8, var9, Block.oreEmerald.blockID, 0, 2);
			}
		}

		for(var5 = 0; var5 < 7; ++var5) {
			var6 = var3 + var2.nextInt(16);
			var7 = var2.nextInt(64);
			var8 = var4 + var2.nextInt(16);
			this.theWorldGenerator.generate(var1, var2, var6, var7, var8);
		}

	}
}
