package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageWoodHut extends ComponentVillage {
	private int averageGroundLevel = -1;
	private final boolean isTallHouse;
	private final int tablePosition;

	public ComponentVillageWoodHut(ComponentVillageStartPiece var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
		super(var1, var2);
		this.coordBaseMode = var5;
		this.boundingBox = var4;
		this.isTallHouse = var3.nextBoolean();
		this.tablePosition = var3.nextInt(3);
	}

	public static ComponentVillageWoodHut func_74908_a(ComponentVillageStartPiece var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(var3, var4, var5, 0, 0, 0, 4, 6, 5, var6);
		return canVillageGoDeeper(var8) && StructureComponent.findIntersecting(var1, var8) == null ? new ComponentVillageWoodHut(var0, var7, var2, var8, var6) : null;
	}

	public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
		if(this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(var1, var3);
			if(this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
		}

		this.fillWithBlocks(var1, var3, 1, 1, 1, 3, 5, 4, 0, 0, false);
		this.fillWithBlocks(var1, var3, 0, 0, 0, 3, 0, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 0, 1, 2, 0, 3, Block.dirt.blockID, Block.dirt.blockID, false);
		if(this.isTallHouse) {
			this.fillWithBlocks(var1, var3, 1, 4, 1, 2, 4, 3, Block.wood.blockID, Block.wood.blockID, false);
		} else {
			this.fillWithBlocks(var1, var3, 1, 5, 1, 2, 5, 3, Block.wood.blockID, Block.wood.blockID, false);
		}

		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 1, 4, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 2, 4, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 1, 4, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 2, 4, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 0, 4, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 0, 4, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 0, 4, 3, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 3, 4, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 3, 4, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.wood.blockID, 0, 3, 4, 3, var3);
		this.fillWithBlocks(var1, var3, 0, 1, 0, 0, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(var1, var3, 3, 1, 0, 3, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 1, 4, 0, 3, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(var1, var3, 3, 1, 4, 3, 3, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 1, 1, 0, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 3, 1, 1, 3, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 1, 0, 2, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 1, 4, 2, 3, 4, Block.planks.blockID, Block.planks.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 0, 2, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 3, 2, 2, var3);
		if(this.tablePosition > 0) {
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, this.tablePosition, 1, 3, var3);
			this.placeBlockAtCurrentPosition(var1, Block.pressurePlatePlanks.blockID, 0, this.tablePosition, 2, 3, var3);
		}

		this.placeBlockAtCurrentPosition(var1, 0, 0, 1, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, 0, 0, 1, 2, 0, var3);
		this.placeDoorAtCurrentPosition(var1, var3, var2, 1, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));
		if(this.getBlockIdAtCurrentPosition(var1, 1, 0, -1, var3) == 0 && this.getBlockIdAtCurrentPosition(var1, 1, -1, -1, var3) != 0) {
			this.placeBlockAtCurrentPosition(var1, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 1, 0, -1, var3);
		}

		for(int var4 = 0; var4 < 5; ++var4) {
			for(int var5 = 0; var5 < 4; ++var5) {
				this.clearCurrentPositionBlocksUpwards(var1, var5, 6, var4, var3);
				this.fillCurrentPositionBlocksDownwards(var1, Block.cobblestone.blockID, 0, var5, -1, var4, var3);
			}
		}

		this.spawnVillagers(var1, var3, 1, 1, 2, 1);
		return true;
	}
}
