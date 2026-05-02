package net.minecraft.src;

import java.util.List;
import java.util.Random;

abstract class ComponentVillage extends StructureComponent {
	private int villagersSpawned;
	protected ComponentVillageStartPiece startPiece;

	protected ComponentVillage(ComponentVillageStartPiece var1, int var2) {
		super(var2);
		this.startPiece = var1;
	}

	protected StructureComponent getNextComponentNN(ComponentVillageStartPiece var1, List var2, Random var3, int var4, int var5) {
		switch(this.coordBaseMode) {
		case 0:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 1, this.getComponentType());
		case 1:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.minZ - 1, 2, this.getComponentType());
		case 2:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX - 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 1, this.getComponentType());
		case 3:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.minZ - 1, 2, this.getComponentType());
		default:
			return null;
		}
	}

	protected StructureComponent getNextComponentPP(ComponentVillageStartPiece var1, List var2, Random var3, int var4, int var5) {
		switch(this.coordBaseMode) {
		case 0:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 3, this.getComponentType());
		case 1:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
		case 2:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.maxX + 1, this.boundingBox.minY + var4, this.boundingBox.minZ + var5, 3, this.getComponentType());
		case 3:
			return StructureVillagePieces.getNextStructureComponent(var1, var2, var3, this.boundingBox.minX + var5, this.boundingBox.minY + var4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
		default:
			return null;
		}
	}

	protected int getAverageGroundLevel(World var1, StructureBoundingBox var2) {
		int var3 = 0;
		int var4 = 0;

		for(int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5) {
			for(int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6) {
				if(var2.isVecInside(var6, 64, var5)) {
					var3 += Math.max(var1.getTopSolidOrLiquidBlock(var6, var5), var1.provider.getAverageGroundLevel());
					++var4;
				}
			}
		}

		if(var4 == 0) {
			return -1;
		} else {
			return var3 / var4;
		}
	}

	protected static boolean canVillageGoDeeper(StructureBoundingBox var0) {
		return var0 != null && var0.minY > 10;
	}

	protected void spawnVillagers(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6) {
		if(this.villagersSpawned < var6) {
			for(int var7 = this.villagersSpawned; var7 < var6; ++var7) {
				int var8 = this.getXWithOffset(var3 + var7, var5);
				int var9 = this.getYWithOffset(var4);
				int var10 = this.getZWithOffset(var3 + var7, var5);
				if(!var2.isVecInside(var8, var9, var10)) {
					break;
				}

				++this.villagersSpawned;
				EntityVillager var11 = new EntityVillager(var1, this.getVillagerType(var7));
				var11.setLocationAndAngles((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
				var1.spawnEntityInWorld(var11);
			}

		}
	}

	protected int getVillagerType(int var1) {
		return 0;
	}

	protected int getBiomeSpecificBlock(int var1, int var2) {
		if(this.startPiece.inDesert) {
			if(var1 == Block.wood.blockID) {
				return Block.sandStone.blockID;
			}

			if(var1 == Block.cobblestone.blockID) {
				return Block.sandStone.blockID;
			}

			if(var1 == Block.planks.blockID) {
				return Block.sandStone.blockID;
			}

			if(var1 == Block.stairsWoodOak.blockID) {
				return Block.stairsSandStone.blockID;
			}

			if(var1 == Block.stairsCobblestone.blockID) {
				return Block.stairsSandStone.blockID;
			}

			if(var1 == Block.gravel.blockID) {
				return Block.sandStone.blockID;
			}
		}

		return var1;
	}

	protected int getBiomeSpecificBlockMetadata(int var1, int var2) {
		if(this.startPiece.inDesert) {
			if(var1 == Block.wood.blockID) {
				return 0;
			}

			if(var1 == Block.cobblestone.blockID) {
				return 0;
			}

			if(var1 == Block.planks.blockID) {
				return 2;
			}
		}

		return var2;
	}

	protected void placeBlockAtCurrentPosition(World var1, int var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
		int var8 = this.getBiomeSpecificBlock(var2, var3);
		int var9 = this.getBiomeSpecificBlockMetadata(var2, var3);
		super.placeBlockAtCurrentPosition(var1, var8, var9, var4, var5, var6, var7);
	}

	protected void fillWithBlocks(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11) {
		int var12 = this.getBiomeSpecificBlock(var9, 0);
		int var13 = this.getBiomeSpecificBlockMetadata(var9, 0);
		int var14 = this.getBiomeSpecificBlock(var10, 0);
		int var15 = this.getBiomeSpecificBlockMetadata(var10, 0);
		super.fillWithMetadataBlocks(var1, var2, var3, var4, var5, var6, var7, var8, var12, var13, var14, var15, var11);
	}

	protected void fillCurrentPositionBlocksDownwards(World var1, int var2, int var3, int var4, int var5, int var6, StructureBoundingBox var7) {
		int var8 = this.getBiomeSpecificBlock(var2, var3);
		int var9 = this.getBiomeSpecificBlockMetadata(var2, var3);
		super.fillCurrentPositionBlocksDownwards(var1, var8, var9, var4, var5, var6, var7);
	}
}
