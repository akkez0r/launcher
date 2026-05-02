package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockLeaves extends BlockLeavesBase {
	public static final String[] LEAF_TYPES = new String[]{"oak", "spruce", "birch", "jungle"};
	public static final String[][] field_94396_b = new String[][]{{"leaves", "leaves_spruce", "leaves", "leaves_jungle"}, {"leaves_opaque", "leaves_spruce_opaque", "leaves_opaque", "leaves_jungle_opaque"}};
	private int field_94394_cP;
	private Icon[][] iconArray = new Icon[2][];
	int[] adjacentTreeBlocks;

	protected BlockLeaves(int var1) {
		super(var1, Material.leaves, false);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public int getBlockColor() {
		double var1 = 0.5D;
		double var3 = 1.0D;
		return ColorizerFoliage.getFoliageColor(var1, var3);
	}

	public int getRenderColor(int var1) {
		return (var1 & 3) == 1 ? ColorizerFoliage.getFoliageColorPine() : ((var1 & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : ColorizerFoliage.getFoliageColorBasic());
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		if((var5 & 3) == 1) {
			return ColorizerFoliage.getFoliageColorPine();
		} else if((var5 & 3) == 2) {
			return ColorizerFoliage.getFoliageColorBirch();
		} else {
			int var6 = 0;
			int var7 = 0;
			int var8 = 0;

			for(int var9 = -1; var9 <= 1; ++var9) {
				for(int var10 = -1; var10 <= 1; ++var10) {
					int var11 = var1.getBiomeGenForCoords(var2 + var10, var4 + var9).getBiomeFoliageColor();
					var6 += (var11 & 16711680) >> 16;
					var7 += (var11 & '\uff00') >> 8;
					var8 += var11 & 255;
				}
			}

			return (var6 / 9 & 255) << 16 | (var7 / 9 & 255) << 8 | var8 / 9 & 255;
		}
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		byte var7 = 1;
		int var8 = var7 + 1;
		if(var1.checkChunksExist(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
			for(int var9 = -var7; var9 <= var7; ++var9) {
				for(int var10 = -var7; var10 <= var7; ++var10) {
					for(int var11 = -var7; var11 <= var7; ++var11) {
						int var12 = var1.getBlockId(var2 + var9, var3 + var10, var4 + var11);
						if(var12 == Block.leaves.blockID) {
							int var13 = var1.getBlockMetadata(var2 + var9, var3 + var10, var4 + var11);
							var1.setBlockMetadataWithNotify(var2 + var9, var3 + var10, var4 + var11, var13 | 8, 4);
						}
					}
				}
			}
		}

	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			if((var6 & 8) != 0 && (var6 & 4) == 0) {
				byte var7 = 4;
				int var8 = var7 + 1;
				byte var9 = 32;
				int var10 = var9 * var9;
				int var11 = var9 / 2;
				if(this.adjacentTreeBlocks == null) {
					this.adjacentTreeBlocks = new int[var9 * var9 * var9];
				}

				int var12;
				if(var1.checkChunksExist(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
					var12 = -var7;

					label112:
					while(true) {
						int var13;
						int var14;
						int var15;
						if(var12 > var7) {
							var12 = 1;

							while(true) {
								if(var12 > 4) {
									break label112;
								}

								for(var13 = -var7; var13 <= var7; ++var13) {
									for(var14 = -var7; var14 <= var7; ++var14) {
										for(var15 = -var7; var15 <= var7; ++var15) {
											if(this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11] == var12 - 1) {
												if(this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
													this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
												}

												if(this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
													this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
												}

												if(this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] == -2) {
													this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] = var12;
												}

												if(this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] == -2) {
													this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] = var12;
												}

												if(this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] == -2) {
													this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] = var12;
												}

												if(this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] == -2) {
													this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] = var12;
												}
											}
										}
									}
								}

								++var12;
							}
						}

						for(var13 = -var7; var13 <= var7; ++var13) {
							for(var14 = -var7; var14 <= var7; ++var14) {
								var15 = var1.getBlockId(var2 + var12, var3 + var13, var4 + var14);
								if(var15 == Block.wood.blockID) {
									this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
								} else if(var15 == Block.leaves.blockID) {
									this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
								} else {
									this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
								}
							}
						}

						++var12;
					}
				}

				var12 = this.adjacentTreeBlocks[var11 * var10 + var11 * var9 + var11];
				if(var12 >= 0) {
					var1.setBlockMetadataWithNotify(var2, var3, var4, var6 & -9, 4);
				} else {
					this.removeLeaves(var1, var2, var3, var4);
				}
			}

		}
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(var1.canLightningStrikeAt(var2, var3 + 1, var4) && !var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) && var5.nextInt(15) == 1) {
			double var6 = (double)((float)var2 + var5.nextFloat());
			double var8 = (double)var3 - 0.05D;
			double var10 = (double)((float)var4 + var5.nextFloat());
			var1.spawnParticle("dripWater", var6, var8, var10, 0.0D, 0.0D, 0.0D);
		}

	}

	private void removeLeaves(World var1, int var2, int var3, int var4) {
		this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
		var1.setBlockToAir(var2, var3, var4);
	}

	public int quantityDropped(Random var1) {
		return var1.nextInt(20) == 0 ? 1 : 0;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.sapling.blockID;
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		if(!var1.isRemote) {
			int var8 = 20;
			if((var5 & 3) == 3) {
				var8 = 40;
			}

			if(var7 > 0) {
				var8 -= 2 << var7;
				if(var8 < 10) {
					var8 = 10;
				}
			}

			if(var1.rand.nextInt(var8) == 0) {
				int var9 = this.idDropped(var5, var1.rand, var7);
				this.dropBlockAsItem_do(var1, var2, var3, var4, new ItemStack(var9, 1, this.damageDropped(var5)));
			}

			var8 = 200;
			if(var7 > 0) {
				var8 -= 10 << var7;
				if(var8 < 40) {
					var8 = 40;
				}
			}

			if((var5 & 3) == 0 && var1.rand.nextInt(var8) == 0) {
				this.dropBlockAsItem_do(var1, var2, var3, var4, new ItemStack(Item.appleRed, 1, 0));
			}
		}

	}

	public void harvestBlock(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
		if(!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().itemID == Item.shears.itemID) {
			var2.addStat(StatList.mineBlockStatArray[this.blockID], 1);
			this.dropBlockAsItem_do(var1, var3, var4, var5, new ItemStack(Block.leaves.blockID, 1, var6 & 3));
		} else {
			super.harvestBlock(var1, var2, var3, var4, var5, var6);
		}

	}

	public int damageDropped(int var1) {
		return var1 & 3;
	}

	public boolean isOpaqueCube() {
		return !this.graphicsLevel;
	}

	public Icon getIcon(int var1, int var2) {
		return (var2 & 3) == 1 ? this.iconArray[this.field_94394_cP][1] : ((var2 & 3) == 3 ? this.iconArray[this.field_94394_cP][3] : this.iconArray[this.field_94394_cP][0]);
	}

	public void setGraphicsLevel(boolean var1) {
		this.graphicsLevel = var1;
		this.field_94394_cP = var1 ? 0 : 1;
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
		var3.add(new ItemStack(var1, 1, 1));
		var3.add(new ItemStack(var1, 1, 2));
		var3.add(new ItemStack(var1, 1, 3));
	}

	protected ItemStack createStackedBlock(int var1) {
		return new ItemStack(this.blockID, 1, var1 & 3);
	}

	public void registerIcons(IconRegister var1) {
		for(int var2 = 0; var2 < field_94396_b.length; ++var2) {
			this.iconArray[var2] = new Icon[field_94396_b[var2].length];

			for(int var3 = 0; var3 < field_94396_b[var2].length; ++var3) {
				this.iconArray[var2][var3] = var1.registerIcon(field_94396_b[var2][var3]);
			}
		}

	}
}
