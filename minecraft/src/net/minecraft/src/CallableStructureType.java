package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableStructureType implements Callable {
	final MapGenStructure theMapStructureGenerator;

	CallableStructureType(MapGenStructure var1) {
		this.theMapStructureGenerator = var1;
	}

	public String callStructureType() {
		return this.theMapStructureGenerator.getClass().getCanonicalName();
	}

	public Object call() {
		return this.callStructureType();
	}
}
