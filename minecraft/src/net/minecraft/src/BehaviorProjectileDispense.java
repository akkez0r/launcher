package net.minecraft.src;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
	public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		World var3 = var1.getWorld();
		IPosition var4 = BlockDispenser.getIPositionFromBlockSource(var1);
		EnumFacing var5 = BlockDispenser.getFacing(var1.getBlockMetadata());
		IProjectile var6 = this.getProjectileEntity(var3, var4);
		var6.setThrowableHeading((double)var5.getFrontOffsetX(), (double)((float)var5.getFrontOffsetY() + 0.1F), (double)var5.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
		var3.spawnEntityInWorld((Entity)var6);
		var2.splitStack(1);
		return var2;
	}

	protected void playDispenseSound(IBlockSource var1) {
		var1.getWorld().playAuxSFX(1002, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
	}

	protected abstract IProjectile getProjectileEntity(World var1, IPosition var2);

	protected float func_82498_a() {
		return 6.0F;
	}

	protected float func_82500_b() {
		return 1.1F;
	}
}
