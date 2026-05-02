package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageHouse4_Garden extends ComponentVillage {
	private int averageGroundLevel = -1;
	private final boolean isRoofAccessible;

	public ComponentVillageHouse4_Garden(ComponentVillageStartPiece var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
		super(var1, var2);
		this.coordBaseMode = var5;
		this.boundingBox = var4;
		this.isRoofAccessible = var3.nextBoolean();
	}

	public static ComponentVillageHouse4_Garden func_74912_a(ComponentVillageStartPiece var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(var3, var4, var5, 0, 0, 0, 5, 6, 5, var6);
		return StructureComponent.findIntersecting(var1, var8) != null ? null : new ComponentVillageHouse4_Garden(var0, var7, var2, var8, var6);
	}

	public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
		if(this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(var1, var3);
			if(this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
		}

		this.fillWithBlocks(var1, var3, 0, 0, 0, 4, 0, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(var1, var3, 0, 4, 0, 4, 4, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 4, 1, 3, 4, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 1, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 2, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 0, 3, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 1, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 2, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cobblestone.blockID, 0, 4, 3, 4, var3);
		this.fillWithBlocks(var1, var3, 0, 1, 1, 0, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 4, 1, 1, 4, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(var1, var3, 1, 1, 4, 3, 3, 4, Block.planks.blockID, Block.planks.blockID, false);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 0, 2, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 2, 2, 4, var3);
		this.placeBlockAtCurrentPosition(var1, Block.thinGlass.blockID, 0, 4, 2, 2, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 1, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 1, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 1, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 2, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 3, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 3, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.planks.blockID, 0, 3, 1, 0, var3);
		if(this.getBlockIdAtCurrentPosition(var1, 2, 0, -1, var3) == 0 && this.getBlockIdAtCurrentPosition(var1, 2, -1, -1, var3) != 0) {
			this.placeBlockAtCurrentPosition(var1, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 2, 0, -1, var3);
		}

		this.fillWithBlocks(var1, var3, 1, 1, 1, 3, 3, 3, 0, 0, false);
		if(this.isRoofAccessible) {
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 0, 5, 0, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 1, 5, 0, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 2, 5, 0, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 3, 5, 0, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 4, 5, 0, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 0, 5, 4, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 1, 5, 4, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 2, 5, 4, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 3, 5, 4, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 4, 5, 4, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 4, 5, 1, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 4, 5, 2, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 4, 5, 3, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 0, 5, 1, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 0, 5, 2, var3);
			this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 0, 5, 3, var3);
		}

		int var4;
		if(this.isRoofAccessible) {
			var4 = this.getMetadataWithOffset(Block.ladder.blockID, 3);
			this.placeBlockAtCurrentPosition(var1, Block.ladder.blockID, var4, 3, 1, 3, var3);
			this.placeBlockAtCurrentPosition(var1, Block.ladder.blockID, var4, 3, 2, 3, var3);
			this.placeBlockAtCurrentPosition(var1, Block.ladder.blockID, var4, 3, 3, 3, var3);
			this.placeBlockAtCurrentPosition(var1, Block.ladder.blockID, var4, 3, 4, 3, var3);
		}

		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 2, 3, 1, var3);

		for(var4 = 0; var4 < 5; ++var4) {
			for(int var5 = 0; var5 < 5; ++var5) {
				this.clearCurrentPositionBlocksUpwards(var1, var5, 6, var4, var3);
				this.fillCurrentPositionBlocksDownwards(var1, Block.cobblestone.blockID, 0, var5, -1, var4, var3);
			}
		}

		this.spawnVillagers(var1, var3, 1, 1, 2, 1);
		return true;
	}
}
