package net.minecraft.src;

class EntityMinecartMobSpawnerLogic extends MobSpawnerBaseLogic {
	final EntityMinecartMobSpawner spawnerMinecart;

	EntityMinecartMobSpawnerLogic(EntityMinecartMobSpawner var1) {
		this.spawnerMinecart = var1;
	}

	public void func_98267_a(int var1) {
		this.spawnerMinecart.worldObj.setEntityState(this.spawnerMinecart, (byte)var1);
	}

	public World getSpawnerWorld() {
		return this.spawnerMinecart.worldObj;
	}

	public int getSpawnerX() {
		return MathHelper.floor_double(this.spawnerMinecart.posX);
	}

	public int getSpawnerY() {
		return MathHelper.floor_double(this.spawnerMinecart.posY);
	}

	public int getSpawnerZ() {
		return MathHelper.floor_double(this.spawnerMinecart.posZ);
	}
}
