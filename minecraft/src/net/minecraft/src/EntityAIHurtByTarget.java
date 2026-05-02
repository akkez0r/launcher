package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIHurtByTarget extends EntityAITarget {
	boolean field_75312_a;
	EntityLiving entityPathNavigate;

	public EntityAIHurtByTarget(EntityLiving var1, boolean var2) {
		super(var1, 16.0F, false);
		this.field_75312_a = var2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		return this.isSuitableTarget(this.taskOwner.getAITarget(), true);
	}

	public boolean continueExecuting() {
		return this.taskOwner.getAITarget() != null && this.taskOwner.getAITarget() != this.entityPathNavigate;
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
		this.entityPathNavigate = this.taskOwner.getAITarget();
		if(this.field_75312_a) {
			List var1 = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand((double)this.targetDistance, 10.0D, (double)this.targetDistance));
			Iterator var2 = var1.iterator();

			while(var2.hasNext()) {
				EntityLiving var3 = (EntityLiving)var2.next();
				if(this.taskOwner != var3 && var3.getAttackTarget() == null) {
					var3.setAttackTarget(this.taskOwner.getAITarget());
				}
			}
		}

		super.startExecuting();
	}

	public void resetTask() {
		if(this.taskOwner.getAttackTarget() != null && this.taskOwner.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.taskOwner.getAttackTarget()).capabilities.disableDamage) {
			super.resetTask();
		}

	}
}
