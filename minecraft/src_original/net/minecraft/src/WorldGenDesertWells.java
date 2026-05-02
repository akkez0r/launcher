package net.minecraft.src;

import java.util.Random;

public class WorldGenDesertWells extends WorldGenerator {
	public boolean generate(World var1, Random var2, int var3, int var4, int var5) {
		while(var1.isAirBlock(var3, var4, var5) && var4 > 2) {
			--var4;
		}

		int var6 = var1.getBlockId(var3, var4, var5);
		if(var6 != Block.sand.blockID) {
			return false;
		} else {
			int var7;
			int var8;
			for(var7 = -2; var7 <= 2; ++var7) {
				for(var8 = -2; var8 <= 2; ++var8) {
					if(var1.isAirBlock(var3 + var7, var4 - 1, var5 + var8) && var1.isAirBlock(var3 + var7, var4 - 2, var5 + var8)) {
						return false;
					}
				}
			}

			for(var7 = -1; var7 <= 0; ++var7) {
				for(var8 = -2; var8 <= 2; ++var8) {
					for(int var9 = -2; var9 <= 2; ++var9) {
						var1.setBlock(var3 + var8, var4 + var7, var5 + var9, Block.sandStone.blockID, 0, 2);
					}
				}
			}

			var1.setBlock(var3, var4, var5, Block.waterMoving.blockID, 0, 2);
			var1.setBlock(var3 - 1, var4, var5, Block.waterMoving.blockID, 0, 2);
			var1.setBlock(var3 + 1, var4, var5, Block.waterMoving.blockID, 0, 2);
			var1.setBlock(var3, var4, var5 - 1, Block.waterMoving.blockID, 0, 2);
			var1.setBlock(var3, var4, var5 + 1, Block.waterMoving.blockID, 0, 2);

			for(var7 = -2; var7 <= 2; ++var7) {
				for(var8 = -2; var8 <= 2; ++var8) {
					if(var7 == -2 || var7 == 2 || var8 == -2 || var8 == 2) {
						var1.setBlock(var3 + var7, var4 + 1, var5 + var8, Block.sandStone.blockID, 0, 2);
					}
				}
			}

			var1.setBlock(var3 + 2, var4 + 1, var5, Block.stoneSingleSlab.blockID, 1, 2);
			var1.setBlock(var3 - 2, var4 + 1, var5, Block.stoneSingleSlab.blockID, 1, 2);
			var1.setBlock(var3, var4 + 1, var5 + 2, Block.stoneSingleSlab.blockID, 1, 2);
			var1.setBlock(var3, var4 + 1, var5 - 2, Block.stoneSingleSlab.blockID, 1, 2);

			for(var7 = -1; var7 <= 1; ++var7) {
				for(var8 = -1; var8 <= 1; ++var8) {
					if(var7 == 0 && var8 == 0) {
						var1.setBlock(var3 + var7, var4 + 4, var5 + var8, Block.sandStone.blockID, 0, 2);
					} else {
						var1.setBlock(var3 + var7, var4 + 4, var5 + var8, Block.stoneSingleSlab.blockID, 1, 2);
					}
				}
			}

			for(var7 = 1; var7 <= 3; ++var7) {
				var1.setBlock(var3 - 1, var4 + var7, var5 - 1, Block.sandStone.blockID, 0, 2);
				var1.setBlock(var3 - 1, var4 + var7, var5 + 1, Block.sandStone.blockID, 0, 2);
				var1.setBlock(var3 + 1, var4 + var7, var5 - 1, Block.sandStone.blockID, 0, 2);
				var1.setBlock(var3 + 1, var4 + var7, var5 + 1, Block.sandStone.blockID, 0, 2);
			}

			return true;
		}
	}
}
