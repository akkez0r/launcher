package net.minecraft.src;

class EntityAIAvoidEntitySelector implements IEntitySelector {
	final EntityAIAvoidEntity entityAvoiderAI;

	EntityAIAvoidEntitySelector(EntityAIAvoidEntity var1) {
		this.entityAvoiderAI = var1;
	}

	public boolean isEntityApplicable(Entity var1) {
		return var1.isEntityAlive() && EntityAIAvoidEntity.func_98217_a(this.entityAvoiderAI).getEntitySenses().canSee(var1);
	}
}
