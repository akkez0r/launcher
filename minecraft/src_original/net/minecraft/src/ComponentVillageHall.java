package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageHall extends ComponentVillage {
	private int averageGroundLevel = -1;

	public ComponentVillageHall(ComponentVillageStartPiece var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
		super(var1, var2);
		this.coordBaseMode = var5;
		this.boundingBox = var4;
	}

	public static ComponentVillageHall func_74906_a(ComponentVillageStartPiece var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(var3, var4, var5, 0, 0, 0, 9, 7, 11, var6);
		return canVillageGoDeeper(var8) && StructureComponent.findIntersecting(var1, var8) == null ? new ComponentVillageHall(var0, var7, var2, var8, var6) : null;
	}

	public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
		if(this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(var1, var3);
			if(this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
		}

		this.fillWithBlocks(var1, var3, 1, 1, 1, 7, 4, 4, 0, 0, false);
		this.fillWithBlocks(var1, var3, 2, 1, 6, 8, 4, 10, 0, 0, false);
		this.fillWithBlocks(var1, var3, 2, 0, 6, 8, 0, 10, Block.dirt.blockID, Block.dirt.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 6, 0, 6, var3);
		this.fillWithBlocks(var1, var3, 2, 1, 6, 2, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(var1, var3, 8, 1, 6, 8, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(var1, var3, 3, 1, 10, 7, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 0, 1, 7, 0, 4, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 0, 0, 0, 3, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 8, 0, 0, 8, 3, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 0, 0, 7, 1, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 0, 5, 7, 1, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 2, 0, 7, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 2, 5, 7, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 4, 1, 8, 4, 1, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 4, 4, 8, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 5, 2, 8, 5, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 0, 4, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 0, 4, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 8, 4, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 8, 4, 3, var3);
		int var4 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
		int var5 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 2);

		int var6;
		int var7;
		for(var6 = -1; var6 <= 2; ++var6) {
			for(var7 = 0; var7 <= 8; ++var7) {
				this.placeBlockAtCurrentPosition(var1, Block.stairsWoodOak.blockID, var4, var7, 4 + var6, var6, var3);
				this.placeBlockAtCurrentPosition(var1, Block.stairsWoodOak.blockID, var5, var7, 4 + var6, 5 - var6, var3);
			}
		}

		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 0, 2, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 0, 2, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 8, 2, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 8, 2, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 0, 2, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 0, 2, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 8, 2, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 8, 2, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 2, 2, 5, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 3, 2, 5, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 5, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 6, 2, 5, var3);
		this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 2, 1, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.pressurePlatePlanks.blockID, 0, 2, 2, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 1, 1, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.stairsWoodOak.blockID, this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 3), 2, 1, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.stairsWoodOak.blockID, this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 1), 1, 1, 3, var3);
		this.fillWithBlocks(var1, var3, 5, 0, 1, 7, 0, 3, Block.stoneDoubleSlab.blockID, Block.stoneDoubleSlab.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.stoneDoubleSlab.blockID, 0, 6, 1, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.stoneDoubleSlab.blockID, 0, 6, 1, 2, var3);
		this.placeBlockAtCurrentPosition(var1, 0, 0, 2, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, 0, 0, 2, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 2, 3, 1, var3);
		this.placeDoorAtCurrentPosition(var1, var3, var2, 2, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));
		if(this.getBlockIdAtCurrentPosition(var1, 2, 0, -1, var3) == 0 && this.getBlockIdAtCurrentPosition(var1, 2, -1, -1, var3) != 0) {
			this.placeBlockAtCurrentPosition(var1, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 2, 0, -1, var3);
		}

		this.placeBlockAtCurrentPosition(var1, 0, 0, 6, 1, 5, var3);
		this.placeBlockAtCurrentPosition(var1, 0, 0, 6, 2, 5, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 6, 3, 4, var3);
		this.placeDoorAtCurrentPosition(var1, var3, var2, 6, 1, 5, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

		for(var6 = 0; var6 < 5; ++var6) {
			for(var7 = 0; var7 < 9; ++var7) {
				this.clearCurrentPositionBlocksUpwards(var1, var7, 7, var6, var3);
				this.fillCurrentPositionBlocksDownwards(var1, Block.cobblestone.blockID, 0, var7, -1, var6, var3);
			}
		}

		this.spawnVillagers(var1, var3, 4, 1, 2, 2);
		return true;
	}

	protected int getVillagerType(int var1) {
		return var1 == 0 ? 4 : 0;
	}
}
