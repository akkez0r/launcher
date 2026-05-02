package net.minecraft.src;

import java.util.Random;

public class WorldGenTrees extends WorldGenerator {
	private final int minTreeHeight;
	private final boolean vinesGrow;
	private final int metaWood;
	private final int metaLeaves;

	public WorldGenTrees(boolean var1) {
		this(var1, 4, 0, 0, false);
	}

	public WorldGenTrees(boolean var1, int var2, int var3, int var4, boolean var5) {
		super(var1);
		this.minTreeHeight = var2;
		this.metaWood = var3;
		this.metaLeaves = var4;
		this.vinesGrow = var5;
	}

	public boolean generate(World var1, Random var2, int var3, int var4, int var5) {
		int var6 = var2.nextInt(3) + this.minTreeHeight;
		boolean var7 = true;
		if(var4 >= 1 && var4 + var6 + 1 <= 256) {
			int var8;
			byte var9;
			int var11;
			int var12;
			for(var8 = var4; var8 <= var4 + 1 + var6; ++var8) {
				var9 = 1;
				if(var8 == var4) {
					var9 = 0;
				}

				if(var8 >= var4 + 1 + var6 - 2) {
					var9 = 2;
				}

				for(int var10 = var3 - var9; var10 <= var3 + var9 && var7; ++var10) {
					for(var11 = var5 - var9; var11 <= var5 + var9 && var7; ++var11) {
						if(var8 >= 0 && var8 < 256) {
							var12 = var1.getBlockId(var10, var8, var11);
							if(var12 != 0 && var12 != Block.leaves.blockID && var12 != Block.grass.blockID && var12 != Block.dirt.blockID && var12 != Block.wood.blockID) {
								var7 = false;
							}
						} else {
							var7 = false;
						}
					}
				}
			}

			if(!var7) {
				return false;
			} else {
				var8 = var1.getBlockId(var3, var4 - 1, var5);
				if((var8 == Block.grass.blockID || var8 == Block.dirt.blockID) && var4 < 256 - var6 - 1) {
					this.setBlock(var1, var3, var4 - 1, var5, Block.dirt.blockID);
					var9 = 3;
					byte var19 = 0;

					int var13;
					int var14;
					int var15;
					for(var11 = var4 - var9 + var6; var11 <= var4 + var6; ++var11) {
						var12 = var11 - (var4 + var6);
						var13 = var19 + 1 - var12 / 2;

						for(var14 = var3 - var13; var14 <= var3 + var13; ++var14) {
							var15 = var14 - var3;

							for(int var16 = var5 - var13; var16 <= var5 + var13; ++var16) {
								int var17 = var16 - var5;
								if(Math.abs(var15) != var13 || Math.abs(var17) != var13 || var2.nextInt(2) != 0 && var12 != 0) {
									int var18 = var1.getBlockId(var14, var11, var16);
									if(var18 == 0 || var18 == Block.leaves.blockID) {
										this.setBlockAndMetadata(var1, var14, var11, var16, Block.leaves.blockID, this.metaLeaves);
									}
								}
							}
						}
					}

					for(var11 = 0; var11 < var6; ++var11) {
						var12 = var1.getBlockId(var3, var4 + var11, var5);
						if(var12 == 0 || var12 == Block.leaves.blockID) {
							this.setBlockAndMetadata(var1, var3, var4 + var11, var5, Block.wood.blockID, this.metaWood);
							if(this.vinesGrow && var11 > 0) {
								if(var2.nextInt(3) > 0 && var1.isAirBlock(var3 - 1, var4 + var11, var5)) {
									this.setBlockAndMetadata(var1, var3 - 1, var4 + var11, var5, Block.vine.blockID, 8);
								}

								if(var2.nextInt(3) > 0 && var1.isAirBlock(var3 + 1, var4 + var11, var5)) {
									this.setBlockAndMetadata(var1, var3 + 1, var4 + var11, var5, Block.vine.blockID, 2);
								}

								if(var2.nextInt(3) > 0 && var1.isAirBlock(var3, var4 + var11, var5 - 1)) {
									this.setBlockAndMetadata(var1, var3, var4 + var11, var5 - 1, Block.vine.blockID, 1);
								}

								if(var2.nextInt(3) > 0 && var1.isAirBlock(var3, var4 + var11, var5 + 1)) {
									this.setBlockAndMetadata(var1, var3, var4 + var11, var5 + 1, Block.vine.blockID, 4);
								}
							}
						}
					}

					if(this.vinesGrow) {
						for(var11 = var4 - 3 + var6; var11 <= var4 + var6; ++var11) {
							var12 = var11 - (var4 + var6);
							var13 = 2 - var12 / 2;

							for(var14 = var3 - var13; var14 <= var3 + var13; ++var14) {
								for(var15 = var5 - var13; var15 <= var5 + var13; ++var15) {
									if(var1.getBlockId(var14, var11, var15) == Block.leaves.blockID) {
										if(var2.nextInt(4) == 0 && var1.getBlockId(var14 - 1, var11, var15) == 0) {
											this.growVines(var1, var14 - 1, var11, var15, 8);
										}

										if(var2.nextInt(4) == 0 && var1.getBlockId(var14 + 1, var11, var15) == 0) {
											this.growVines(var1, var14 + 1, var11, var15, 2);
										}

										if(var2.nextInt(4) == 0 && var1.getBlockId(var14, var11, var15 - 1) == 0) {
											this.growVines(var1, var14, var11, var15 - 1, 1);
										}

										if(var2.nextInt(4) == 0 && var1.getBlockId(var14, var11, var15 + 1) == 0) {
											this.growVines(var1, var14, var11, var15 + 1, 4);
										}
									}
								}
							}
						}

						if(var2.nextInt(5) == 0 && var6 > 5) {
							for(var11 = 0; var11 < 2; ++var11) {
								for(var12 = 0; var12 < 4; ++var12) {
									if(var2.nextInt(4 - var11) == 0) {
										var13 = var2.nextInt(3);
										this.setBlockAndMetadata(var1, var3 + Direction.offsetX[Direction.rotateOpposite[var12]], var4 + var6 - 5 + var11, var5 + Direction.offsetZ[Direction.rotateOpposite[var12]], Block.cocoaPlant.blockID, var13 << 2 | var12);
									}
								}
							}
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private void growVines(World var1, int var2, int var3, int var4, int var5) {
		this.setBlockAndMetadata(var1, var2, var3, var4, Block.vine.blockID, var5);
		int var6 = 4;

		while(true) {
			--var3;
			if(var1.getBlockId(var2, var3, var4) != 0 || var6 <= 0) {
				return;
			}

			this.setBlockAndMetadata(var1, var2, var3, var4, Block.vine.blockID, var5);
			--var6;
		}
	}
}
