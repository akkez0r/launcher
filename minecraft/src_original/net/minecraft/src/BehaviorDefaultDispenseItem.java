package net.minecraft.src;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
	public final ItemStack dispense(IBlockSource var1, ItemStack var2) {
		ItemStack var3 = this.dispenseStack(var1, var2);
		this.playDispenseSound(var1);
		this.spawnDispenseParticles(var1, BlockDispenser.getFacing(var1.getBlockMetadata()));
		return var3;
	}

	protected ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
		EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
		IPosition var4 = BlockDispenser.getIPositionFromBlockSource(var1);
		ItemStack var5 = var2.splitStack(1);
		doDispense(var1.getWorld(), var5, 6, var3, var4);
		return var2;
	}

	public static void doDispense(World var0, ItemStack var1, int var2, EnumFacing var3, IPosition var4) {
		double var5 = var4.getX();
		double var7 = var4.getY();
		double var9 = var4.getZ();
		EntityItem var11 = new EntityItem(var0, var5, var7 - 0.3D, var9, var1);
		double var12 = var0.rand.nextDouble() * 0.1D + 0.2D;
		var11.motionX = (double)var3.getFrontOffsetX() * var12;
		var11.motionY = (double)0.2F;
		var11.motionZ = (double)var3.getFrontOffsetZ() * var12;
		var11.motionX += var0.rand.nextGaussian() * (double)0.0075F * (double)var2;
		var11.motionY += var0.rand.nextGaussian() * (double)0.0075F * (double)var2;
		var11.motionZ += var0.rand.nextGaussian() * (double)0.0075F * (double)var2;
		var0.spawnEntityInWorld(var11);
	}

	protected void playDispenseSound(IBlockSource var1) {
		var1.getWorld().playAuxSFX(1000, var1.getXInt(), var1.getYInt(), var1.getZInt(), 0);
	}

	protected void spawnDispenseParticles(IBlockSource var1, EnumFacing var2) {
		var1.getWorld().playAuxSFX(2000, var1.getXInt(), var1.getYInt(), var1.getZInt(), this.func_82488_a(var2));
	}

	private int func_82488_a(EnumFacing var1) {
		return var1.getFrontOffsetX() + 1 + (var1.getFrontOffsetZ() + 1) * 3;
	}
}
