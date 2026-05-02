package net.minecraft.src;

final class DispenserBehaviorFire extends BehaviorDefaultDispenseItem {
	private boolean field_96466_b = true;

	protected ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
		World var4 = var1.getWorld();
		int var5 = var1.getXInt() + var3.getFrontOffsetX();
		int var6 = var1.getYInt() + var3.getFrontOffsetY();
		int var7 = var1.getZInt() + var3.getFrontOffsetZ();
		if(var4.isAirBlock(var5, var6, var7)) {
			var4.setBlock(var5, var6, var7, Block.fire.blockID);
			if(var2.attemptDamageItem(1, var4.rand)) {
				var2.stackSize = 0;
			}
		} else if(var4.getBlockId(var5, var6, var7) == Block.tnt.blockID) {
			Block.tnt.onBlockDestroyedByPlayer(var4, var5, var6, var7, 1);
			var4.setBlockToAir(var5, var6, var7);
		} else {
			this.field_96466_b = false;
		}

		return var2;
	}

	protected void playDispenseSound(IBlockSource var1) {
		if(this.field_96466_b) {
			var1.getWorld().playAuxSFX(1000, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
		} else {
			var1.getWorld().playAuxSFX(1001, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
		}

	}
}
