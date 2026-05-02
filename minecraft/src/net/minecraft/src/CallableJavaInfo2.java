package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableJavaInfo2 implements Callable {
	final CrashReport theCrashReport;

	CallableJavaInfo2(CrashReport var1) {
		this.theCrashReport = var1;
	}

	public String getJavaVMInfoAsString() {
		return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
	}

	public Object call() {
		return this.getJavaVMInfoAsString();
	}
}
