package net.minecraft.src;

final class DispenserBehaviorMobEgg extends BehaviorDefaultDispenseItem {
	public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
		double var4 = var1.getX() + (double)var3.getFrontOffsetX();
		double var6 = (double)((float)var1.getYInt() + 0.2F);
		double var8 = var1.getZ() + (double)var3.getFrontOffsetZ();
		Entity var10 = ItemMonsterPlacer.spawnCreature(var1.getWorld(), var2.getItemDamage(), var4, var6, var8);
		if(var10 instanceof EntityLiving && var2.hasDisplayName()) {
			((EntityLiving)var10).func_94058_c(var2.getDisplayName());
		}

		var2.splitStack(1);
		return var2;
	}
}
