package net.minecraft.src;

import java.util.List;

final class BehaviorDispenseArmor extends BehaviorDefaultDispenseItem {
	protected ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
		int var4 = var1.getXInt() + var3.getFrontOffsetX();
		int var5 = var1.getYInt() + var3.getFrontOffsetY();
		int var6 = var1.getZInt() + var3.getFrontOffsetZ();
		AxisAlignedBB var7 = AxisAlignedBB.getAABBPool().getAABB((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1));
		List var8 = var1.getWorld().selectEntitiesWithinAABB(EntityLiving.class, var7, new EntitySelectorArmoredMob(var2));
		if(var8.size() > 0) {
			EntityLiving var9 = (EntityLiving)var8.get(0);
			int var10 = var9 instanceof EntityPlayer ? 1 : 0;
			int var11 = EntityLiving.getArmorPosition(var2);
			ItemStack var12 = var2.copy();
			var12.stackSize = 1;
			var9.setCurrentItemOrArmor(var11 - var10, var12);
			var9.func_96120_a(var11, 2.0F);
			--var2.stackSize;
			return var2;
		} else {
			return super.dispenseStack(var1, var2);
		}
	}
}
