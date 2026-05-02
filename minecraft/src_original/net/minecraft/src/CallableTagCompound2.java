package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableTagCompound2 implements Callable {
	final int field_82588_a;
	final NBTTagCompound theNBTTagCompound;

	CallableTagCompound2(NBTTagCompound var1, int var2) {
		this.theNBTTagCompound = var1;
		this.field_82588_a = var2;
	}

	public String func_82586_a() {
		return NBTBase.NBTTypes[this.field_82588_a];
	}

	public Object call() {
		return this.func_82586_a();
	}
}
