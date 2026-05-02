package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEntityName implements Callable {
	final Entity theEntity;

	CallableEntityName(Entity var1) {
		this.theEntity = var1;
	}

	public String callEntityName() {
		return this.theEntity.getEntityName();
	}

	public Object call() {
		return this.callEntityName();
	}
}
