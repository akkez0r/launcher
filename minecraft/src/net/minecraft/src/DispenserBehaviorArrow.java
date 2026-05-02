package net.minecraft.src;

final class DispenserBehaviorArrow extends BehaviorProjectileDispense {
	protected IProjectile getProjectileEntity(World var1, IPosition var2) {
		EntityArrow var3 = new EntityArrow(var1, var2.getX(), var2.getY(), var2.getZ());
		var3.canBePickedUp = 1;
		return var3;
	}
}
