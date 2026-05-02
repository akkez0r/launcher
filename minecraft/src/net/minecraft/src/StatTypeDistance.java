package net.minecraft.src;

final class StatTypeDistance implements IStatType {
	public String format(int var1) {
		double var2 = (double)var1 / 100.0D;
		double var4 = var2 / 1000.0D;
		return var4 > 0.5D ? StatBase.getDecimalFormat().format(var4) + " km" : (var2 > 0.5D ? StatBase.getDecimalFormat().format(var2) + " m" : var1 + " cm");
	}
}
