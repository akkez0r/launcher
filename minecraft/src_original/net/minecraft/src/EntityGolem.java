package net.minecraft.src;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
	public EntityGolem(World var1) {
		super(var1);
	}

	protected void fall(float var1) {
	}

	protected String getLivingSound() {
		return "none";
	}

	protected String getHurtSound() {
		return "none";
	}

	protected String getDeathSound() {
		return "none";
	}

	public int getTalkInterval() {
		return 120;
	}

	protected boolean canDespawn() {
		return false;
	}
}
