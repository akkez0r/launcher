package net.minecraft.src;

final class DispenserBehaviorEmptyBucket extends BehaviorDefaultDispenseItem {
	private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

	public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
		World var4 = var1.getWorld();
		int var5 = var1.getXInt() + var3.getFrontOffsetX();
		int var6 = var1.getYInt() + var3.getFrontOffsetY();
		int var7 = var1.getZInt() + var3.getFrontOffsetZ();
		Material var8 = var4.getBlockMaterial(var5, var6, var7);
		int var9 = var4.getBlockMetadata(var5, var6, var7);
		Item var10;
		if(Material.water.equals(var8) && var9 == 0) {
			var10 = Item.bucketWater;
		} else {
			if(!Material.lava.equals(var8) || var9 != 0) {
				return super.dispenseStack(var1, var2);
			}

			var10 = Item.bucketLava;
		}

		var4.setBlockToAir(var5, var6, var7);
		if(--var2.stackSize == 0) {
			var2.itemID = var10.itemID;
			var2.stackSize = 1;
		} else if(((TileEntityDispenser)var1.getBlockTileEntity()).addItem(new ItemStack(var10)) < 0) {
			this.defaultDispenserItemBehavior.dispense(var1, new ItemStack(var10));
		}

		return var2;
	}
}
