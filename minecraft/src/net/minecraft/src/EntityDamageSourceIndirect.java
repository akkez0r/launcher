package net.minecraft.src;

public class EntityDamageSourceIndirect extends EntityDamageSource {
	private Entity indirectEntity;

	public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
		super(var1, var2);
		this.indirectEntity = var3;
	}

	public Entity getSourceOfDamage() {
		return this.damageSourceEntity;
	}

	public Entity getEntity() {
		return this.indirectEntity;
	}

	public String getDeathMessage(EntityLiving var1) {
		String var2 = this.indirectEntity == null ? this.damageSourceEntity.getTranslatedEntityName() : this.indirectEntity.getTranslatedEntityName();
		ItemStack var3 = this.indirectEntity instanceof EntityLiving ? ((EntityLiving)this.indirectEntity).getHeldItem() : null;
		String var4 = "death.attack." + this.damageType;
		String var5 = var4 + ".item";
		return var3 != null && var3.hasDisplayName() && StatCollector.func_94522_b(var5) ? StatCollector.translateToLocalFormatted(var5, new Object[]{var1.getTranslatedEntityName(), var2, var3.getDisplayName()}) : StatCollector.translateToLocalFormatted(var4, new Object[]{var1.getTranslatedEntityName(), var2});
	}
}
