package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageTorch extends ComponentVillage {
	private int averageGroundLevel = -1;

	public ComponentVillageTorch(ComponentVillageStartPiece var1, int var2, Random var3, StructureBoundingBox var4, int var5) {
		super(var1, var2);
		this.coordBaseMode = var5;
		this.boundingBox = var4;
	}

	public static StructureBoundingBox func_74904_a(ComponentVillageStartPiece var0, List var1, Random var2, int var3, int var4, int var5, int var6) {
		StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(var3, var4, var5, 0, 0, 0, 3, 4, 2, var6);
		return StructureComponent.findIntersecting(var1, var7) != null ? null : var7;
	}

	public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
		if(this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(var1, var3);
			if(this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
		}

		this.fillWithBlocks(var1, var3, 0, 0, 0, 2, 3, 1, 0, 0, false);
		this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 1, 0, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 1, 1, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.fence.blockID, 0, 1, 2, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.cloth.blockID, 15, 1, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 0, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 1, 3, 1, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 2, 3, 0, var3);
		this.placeBlockAtCurrentPosition(var1, Block.torchWood.blockID, 0, 1, 3, -1, var3);
		return true;
	}
}
