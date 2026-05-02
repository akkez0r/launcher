package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
	public ScoreHealthCriteria(String var1) {
		super(var1);
	}

	public int func_96635_a(List var1) {
		float var2 = 0.0F;

		int var5;
		float var6;
		for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += (float)var5 / var6) {
			EntityPlayer var4 = (EntityPlayer)var3.next();
			var5 = var4.getHealth();
			var6 = (float)var4.getMaxHealth();
			if(var5 < 0) {
				var5 = 0;
			}

			if((float)var5 > var6) {
				var5 = var4.getMaxHealth();
			}
		}

		if(var1.size() > 0) {
			var2 /= (float)var1.size();
		}

		return MathHelper.floor_float(var2 * 19.0F) + (var2 > 0.0F ? 1 : 0);
	}

	public boolean isReadOnly() {
		return true;
	}
}
