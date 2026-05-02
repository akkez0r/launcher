package net.minecraft.src;

class TileEntityMobSpawnerLogic extends MobSpawnerBaseLogic {
	final TileEntityMobSpawner mobSpawnerEntity;

	TileEntityMobSpawnerLogic(TileEntityMobSpawner var1) {
		this.mobSpawnerEntity = var1;
	}

	public void func_98267_a(int var1) {
		this.mobSpawnerEntity.worldObj.addBlockEvent(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord, Block.mobSpawner.blockID, var1, 0);
	}

	public World getSpawnerWorld() {
		return this.mobSpawnerEntity.worldObj;
	}

	public int getSpawnerX() {
		return this.mobSpawnerEntity.xCoord;
	}

	public int getSpawnerY() {
		return this.mobSpawnerEntity.yCoord;
	}

	public int getSpawnerZ() {
		return this.mobSpawnerEntity.zCoord;
	}

	public void setRandomMinecart(WeightedRandomMinecart var1) {
		super.setRandomMinecart(var1);
		if(this.getSpawnerWorld() != null) {
			this.getSpawnerWorld().markBlockForUpdate(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord);
		}

	}
}
