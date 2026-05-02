package net.minecraft.src;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
	EntityTameable theEntityTameable;
	EntityLiving theTarget;

	public EntityAIOwnerHurtTarget(EntityTameable var1) {
		super(var1, 32.0F, false);
		this.theEntityTameable = var1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(!this.theEntityTameable.isTamed()) {
			return false;
		} else {
			EntityLiving var1 = this.theEntityTameable.getOwner();
			if(var1 == null) {
				return false;
			} else {
				this.theTarget = var1.getLastAttackingEntity();
				return this.isSuitableTarget(this.theTarget, false);
			}
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theTarget);
		super.startExecuting();
	}
}
