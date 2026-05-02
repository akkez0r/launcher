package net.minecraft.src;

final class EntitySelectorAlive implements IEntitySelector {
	public boolean isEntityApplicable(Entity var1) {
		return var1.isEntityAlive();
	}
}
