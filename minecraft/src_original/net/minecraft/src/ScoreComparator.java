package net.minecraft.src;

import java.util.Comparator;

final class ScoreComparator implements Comparator {
	public int func_96659_a(Score var1, Score var2) {
		return var1.func_96652_c() > var2.func_96652_c() ? 1 : (var1.func_96652_c() < var2.func_96652_c() ? -1 : 0);
	}

	public int compare(Object var1, Object var2) {
		return this.func_96659_a((Score)var1, (Score)var2);
	}
}
