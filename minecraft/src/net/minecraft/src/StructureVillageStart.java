package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class StructureVillageStart extends StructureStart {
	private boolean hasMoreThanTwoComponents = false;

	public StructureVillageStart(World var1, Random var2, int var3, int var4, int var5) {
		ArrayList var6 = StructureVillagePieces.getStructureVillageWeightedPieceList(var2, var5);
		ComponentVillageStartPiece var7 = new ComponentVillageStartPiece(var1.getWorldChunkManager(), 0, var2, (var3 << 4) + 2, (var4 << 4) + 2, var6, var5);
		this.components.add(var7);
		var7.buildComponent(var7, this.components, var2);
		ArrayList var8 = var7.field_74930_j;
		ArrayList var9 = var7.field_74932_i;

		int var10;
		while(!var8.isEmpty() || !var9.isEmpty()) {
			StructureComponent var11;
			if(var8.isEmpty()) {
				var10 = var2.nextInt(var9.size());
				var11 = (StructureComponent)var9.remove(var10);
				var11.buildComponent(var7, this.components, var2);
			} else {
				var10 = var2.nextInt(var8.size());
				var11 = (StructureComponent)var8.remove(var10);
				var11.buildComponent(var7, this.components, var2);
			}
		}

		this.updateBoundingBox();
		var10 = 0;
		Iterator var13 = this.components.iterator();

		while(var13.hasNext()) {
			StructureComponent var12 = (StructureComponent)var13.next();
			if(!(var12 instanceof ComponentVillageRoadPiece)) {
				++var10;
			}
		}

		this.hasMoreThanTwoComponents = var10 > 2;
	}

	public boolean isSizeableStructure() {
		return this.hasMoreThanTwoComponents;
	}
}
