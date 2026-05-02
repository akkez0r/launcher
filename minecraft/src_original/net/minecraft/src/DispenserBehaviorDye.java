package net.minecraft.src;

final class DispenserBehaviorDye extends BehaviorDefaultDispenseItem {
	private boolean field_96461_b = true;

	protected ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		if(var2.getItemDamage() == 15) {
			EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
			World var4 = var1.getWorld();
			int var5 = var1.getXInt() + var3.getFrontOffsetX();
			int var6 = var1.getYInt() + var3.getFrontOffsetY();
			int var7 = var1.getZInt() + var3.getFrontOffsetZ();
			if(ItemDye.func_96604_a(var2, var4, var5, var6, var7)) {
				if(!var4.isRemote) {
					var4.playAuxSFX(2005, var5, var6, var7, 0);
				}
			} else {
				this.field_96461_b = false;
			}

			return var2;
		} else {
			return super.dispenseStack(var1, var2);
		}
	}

	protected void playDispenseSound(IBlockSource var1) {
		if(this.field_96461_b) {
			var1.getWorld().playAuxSFX(1000, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
		} else {
			var1.getWorld().playAuxSFX(1001, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
		}

	}
}
