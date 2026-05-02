package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableMPL1 implements Callable {
	final WorldClient theWorldClient;

	CallableMPL1(WorldClient var1) {
		this.theWorldClient = var1;
	}

	public String getEntityCountAndList() {
		return WorldClient.getEntityList(this.theWorldClient).size() + " total; " + WorldClient.getEntityList(this.theWorldClient).toString();
	}

	public Object call() {
		return this.getEntityCountAndList();
	}
}
