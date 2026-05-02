package net.minecraft.src;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
	EntityTameable theDefendingTameable;
	EntityLiving theOwnerAttacker;

	public EntityAIOwnerHurtByTarget(EntityTameable var1) {
		super(var1, 32.0F, false);
		this.theDefendingTameable = var1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(!this.theDefendingTameable.isTamed()) {
			return false;
		} else {
			EntityLiving var1 = this.theDefendingTameable.getOwner();
			if(var1 == null) {
				return false;
			} else {
				this.theOwnerAttacker = var1.getAITarget();
				return this.isSuitableTarget(this.theOwnerAttacker, false);
			}
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theOwnerAttacker);
		super.startExecuting();
	}
}
