package net.minecraft.src;

import java.util.Iterator;
import java.util.Random;

public class BlockBed extends BlockDirectional {
	public static final int[][] footBlockToHeadBlockMap = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
	private Icon[] field_94472_b;
	private Icon[] bedSideIcons;
	private Icon[] bedTopIcons;

	public BlockBed(int var1) {
		super(var1, Material.cloth);
		this.setBounds();
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			int var10 = var1.getBlockMetadata(var2, var3, var4);
			if(!isBlockHeadOfBed(var10)) {
				int var11 = getDirection(var10);
				var2 += footBlockToHeadBlockMap[var11][0];
				var4 += footBlockToHeadBlockMap[var11][1];
				if(var1.getBlockId(var2, var3, var4) != this.blockID) {
					return true;
				}

				var10 = var1.getBlockMetadata(var2, var3, var4);
			}

			if(var1.provider.canRespawnHere() && var1.getBiomeGenForCoords(var2, var4) != BiomeGenBase.hell) {
				if(isBedOccupied(var10)) {
					EntityPlayer var19 = null;
					Iterator var12 = var1.playerEntities.iterator();

					while(var12.hasNext()) {
						EntityPlayer var21 = (EntityPlayer)var12.next();
						if(var21.isPlayerSleeping()) {
							ChunkCoordinates var14 = var21.playerLocation;
							if(var14.posX == var2 && var14.posY == var3 && var14.posZ == var4) {
								var19 = var21;
							}
						}
					}

					if(var19 != null) {
						var5.addChatMessage("tile.bed.occupied");
						return true;
					}

					setBedOccupied(var1, var2, var3, var4, false);
				}

				EnumStatus var20 = var5.sleepInBedAt(var2, var3, var4);
				if(var20 == EnumStatus.OK) {
					setBedOccupied(var1, var2, var3, var4, true);
					return true;
				} else {
					if(var20 == EnumStatus.NOT_POSSIBLE_NOW) {
						var5.addChatMessage("tile.bed.noSleep");
					} else if(var20 == EnumStatus.NOT_SAFE) {
						var5.addChatMessage("tile.bed.notSafe");
					}

					return true;
				}
			} else {
				double var18 = (double)var2 + 0.5D;
				double var13 = (double)var3 + 0.5D;
				double var15 = (double)var4 + 0.5D;
				var1.setBlockToAir(var2, var3, var4);
				int var17 = getDirection(var10);
				var2 += footBlockToHeadBlockMap[var17][0];
				var4 += footBlockToHeadBlockMap[var17][1];
				if(var1.getBlockId(var2, var3, var4) == this.blockID) {
					var1.setBlockToAir(var2, var3, var4);
					var18 = (var18 + (double)var2 + 0.5D) / 2.0D;
					var13 = (var13 + (double)var3 + 0.5D) / 2.0D;
					var15 = (var15 + (double)var4 + 0.5D) / 2.0D;
				}

				var1.newExplosion((Entity)null, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), 5.0F, true, true);
				return true;
			}
		}
	}

	public Icon getIcon(int var1, int var2) {
		if(var1 == 0) {
			return Block.planks.getBlockTextureFromSide(var1);
		} else {
			int var3 = getDirection(var2);
			int var4 = Direction.bedDirection[var3][var1];
			int var5 = isBlockHeadOfBed(var2) ? 1 : 0;
			return (var5 != 1 || var4 != 2) && (var5 != 0 || var4 != 3) ? (var4 != 5 && var4 != 4 ? this.bedTopIcons[var5] : this.bedSideIcons[var5]) : this.field_94472_b[var5];
		}
	}

	public void registerIcons(IconRegister var1) {
		this.bedTopIcons = new Icon[]{var1.registerIcon("bed_feet_top"), var1.registerIcon("bed_head_top")};
		this.field_94472_b = new Icon[]{var1.registerIcon("bed_feet_end"), var1.registerIcon("bed_head_end")};
		this.bedSideIcons = new Icon[]{var1.registerIcon("bed_feet_side"), var1.registerIcon("bed_head_side")};
	}

	public int getRenderType() {
		return 14;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		this.setBounds();
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		int var7 = getDirection(var6);
		if(isBlockHeadOfBed(var6)) {
			if(var1.getBlockId(var2 - footBlockToHeadBlockMap[var7][0], var3, var4 - footBlockToHeadBlockMap[var7][1]) != this.blockID) {
				var1.setBlockToAir(var2, var3, var4);
			}
		} else if(var1.getBlockId(var2 + footBlockToHeadBlockMap[var7][0], var3, var4 + footBlockToHeadBlockMap[var7][1]) != this.blockID) {
			var1.setBlockToAir(var2, var3, var4);
			if(!var1.isRemote) {
				this.dropBlockAsItem(var1, var2, var3, var4, var6, 0);
			}
		}

	}

	public int idDropped(int var1, Random var2, int var3) {
		return isBlockHeadOfBed(var1) ? 0 : Item.bed.itemID;
	}

	private void setBounds() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 9.0F / 16.0F, 1.0F);
	}

	public static boolean isBlockHeadOfBed(int var0) {
		return (var0 & 8) != 0;
	}

	public static boolean isBedOccupied(int var0) {
		return (var0 & 4) != 0;
	}

	public static void setBedOccupied(World var0, int var1, int var2, int var3, boolean var4) {
		int var5 = var0.getBlockMetadata(var1, var2, var3);
		if(var4) {
			var5 |= 4;
		} else {
			var5 &= -5;
		}

		var0.setBlockMetadataWithNotify(var1, var2, var3, var5, 4);
	}

	public static ChunkCoordinates getNearestEmptyChunkCoordinates(World var0, int var1, int var2, int var3, int var4) {
		int var5 = var0.getBlockMetadata(var1, var2, var3);
		int var6 = BlockDirectional.getDirection(var5);

		for(int var7 = 0; var7 <= 1; ++var7) {
			int var8 = var1 - footBlockToHeadBlockMap[var6][0] * var7 - 1;
			int var9 = var3 - footBlockToHeadBlockMap[var6][1] * var7 - 1;
			int var10 = var8 + 2;
			int var11 = var9 + 2;

			for(int var12 = var8; var12 <= var10; ++var12) {
				for(int var13 = var9; var13 <= var11; ++var13) {
					if(var0.doesBlockHaveSolidTopSurface(var12, var2 - 1, var13) && var0.isAirBlock(var12, var2, var13) && var0.isAirBlock(var12, var2 + 1, var13)) {
						if(var4 <= 0) {
							return new ChunkCoordinates(var12, var2, var13);
						}

						--var4;
					}
				}
			}
		}

		return null;
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		if(!isBlockHeadOfBed(var5)) {
			super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, 0);
		}

	}

	public int getMobilityFlag() {
		return 1;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.bed.itemID;
	}

	public void onBlockHarvested(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
		if(var6.capabilities.isCreativeMode && isBlockHeadOfBed(var5)) {
			int var7 = getDirection(var5);
			var2 -= footBlockToHeadBlockMap[var7][0];
			var4 -= footBlockToHeadBlockMap[var7][1];
			if(var1.getBlockId(var2, var3, var4) == this.blockID) {
				var1.setBlockToAir(var2, var3, var4);
			}
		}

	}
}
