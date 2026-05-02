package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableIsFeatureChunk implements Callable {
	final int field_85169_a;
	final int field_85167_b;
	final MapGenStructure theMapStructureGenerator;

	CallableIsFeatureChunk(MapGenStructure var1, int var2, int var3) {
		this.theMapStructureGenerator = var1;
		this.field_85169_a = var2;
		this.field_85167_b = var3;
	}

	public String func_85166_a() {
		return this.theMapStructureGenerator.canSpawnStructureAtCoords(this.field_85169_a, this.field_85167_b) ? "True" : "False";
	}

	public Object call() {
		return this.func_85166_a();
	}
}
