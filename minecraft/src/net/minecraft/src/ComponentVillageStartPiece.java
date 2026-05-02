package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class ComponentVillageStartPiece extends ComponentVillageWell {
	public final WorldChunkManager worldChunkMngr;
	public final boolean inDesert;
	public final int terrainType;
	public StructureVillagePieceWeight structVillagePieceWeight;
	public ArrayList structureVillageWeightedPieceList;
	public ArrayList field_74932_i = new ArrayList();
	public ArrayList field_74930_j = new ArrayList();

	public ComponentVillageStartPiece(WorldChunkManager var1, int var2, Random var3, int var4, int var5, ArrayList var6, int var7) {
		super((ComponentVillageStartPiece)null, 0, var3, var4, var5);
		this.worldChunkMngr = var1;
		this.structureVillageWeightedPieceList = var6;
		this.terrainType = var7;
		BiomeGenBase var8 = var1.getBiomeGenAt(var4, var5);
		this.inDesert = var8 == BiomeGenBase.desert || var8 == BiomeGenBase.desertHills;
		this.startPiece = this;
	}

	public WorldChunkManager getWorldChunkManager() {
		return this.worldChunkMngr;
	}
}
