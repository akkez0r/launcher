package net.minecraft.src;

import java.util.Iterator;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
	private final int maxItemsWeighted;

	protected BlockPressurePlateWeighted(int var1, String var2, Material var3, int var4) {
		super(var1, var2, var3);
		this.maxItemsWeighted = var4;
	}

	protected int getPlateState(World var1, int var2, int var3, int var4) {
		int var5 = 0;
		Iterator var6 = var1.getEntitiesWithinAABB(EntityItem.class, this.getSensitiveAABB(var2, var3, var4)).iterator();

		while(var6.hasNext()) {
			EntityItem var7 = (EntityItem)var6.next();
			var5 += var7.getEntityItem().stackSize;
			if(var5 >= this.maxItemsWeighted) {
				break;
			}
		}

		if(var5 <= 0) {
			return 0;
		} else {
			float var8 = (float)Math.min(this.maxItemsWeighted, var5) / (float)this.maxItemsWeighted;
			return MathHelper.ceiling_float_int(var8 * 15.0F);
		}
	}

	protected int getPowerSupply(int var1) {
		return var1;
	}

	protected int getMetaFromWeight(int var1) {
		return var1;
	}

	public int tickRate(World var1) {
		return 10;
	}
}
