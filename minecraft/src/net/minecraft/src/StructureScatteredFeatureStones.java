package net.minecraft.src;

import java.util.Random;

class StructureScatteredFeatureStones extends StructurePieceBlockSelector {
	private StructureScatteredFeatureStones() {
	}

	public void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5) {
		if(var1.nextFloat() < 0.4F) {
			this.selectedBlockId = Block.cobblestone.blockID;
		} else {
			this.selectedBlockId = Block.cobblestoneMossy.blockID;
		}

	}

	StructureScatteredFeatureStones(ComponentScatteredFeaturePieces2 var1) {
		this();
	}
}
