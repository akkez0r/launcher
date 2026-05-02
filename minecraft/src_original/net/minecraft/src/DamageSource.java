package net.minecraft.src;

public class DamageSource {
	public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
	public static DamageSource onFire = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
	public static DamageSource lava = (new DamageSource("lava")).setFireDamage();
	public static DamageSource inWall = (new DamageSource("inWall")).setDamageBypassesArmor();
	public static DamageSource drown = (new DamageSource("drown")).setDamageBypassesArmor();
	public static DamageSource starve = (new DamageSource("starve")).setDamageBypassesArmor();
	public static DamageSource cactus = new DamageSource("cactus");
	public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesArmor();
	public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
	public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesArmor();
	public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
	public static DamageSource wither = (new DamageSource("wither")).setDamageBypassesArmor();
	public static DamageSource anvil = new DamageSource("anvil");
	public static DamageSource fallingBlock = new DamageSource("fallingBlock");
	private boolean isUnblockable = false;
	private boolean isDamageAllowedInCreativeMode = false;
	private float hungerDamage = 0.3F;
	private boolean fireDamage;
	private boolean projectile;
	private boolean difficultyScaled;
	private boolean magicDamage = false;
	private boolean explosion = false;
	public String damageType;

	public static DamageSource causeMobDamage(EntityLiving var0) {
		return new EntityDamageSource("mob", var0);
	}

	public static DamageSource causePlayerDamage(EntityPlayer var0) {
		return new EntityDamageSource("player", var0);
	}

	public static DamageSource causeArrowDamage(EntityArrow var0, Entity var1) {
		return (new EntityDamageSourceIndirect("arrow", var0, var1)).setProjectile();
	}

	public static DamageSource causeFireballDamage(EntityFireball var0, Entity var1) {
		return var1 == null ? (new EntityDamageSourceIndirect("onFire", var0, var0)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("fireball", var0, var1)).setFireDamage().setProjectile();
	}

	public static DamageSource causeThrownDamage(Entity var0, Entity var1) {
		return (new EntityDamageSourceIndirect("thrown", var0, var1)).setProjectile();
	}

	public static DamageSource causeIndirectMagicDamage(Entity var0, Entity var1) {
		return (new EntityDamageSourceIndirect("indirectMagic", var0, var1)).setDamageBypassesArmor().setMagicDamage();
	}

	public static DamageSource causeThornsDamage(Entity var0) {
		return (new EntityDamageSource("thorns", var0)).setMagicDamage();
	}

	public static DamageSource setExplosionSource(Explosion var0) {
		return var0 != null && var0.func_94613_c() != null ? (new EntityDamageSource("explosion.player", var0.func_94613_c())).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
	}

	public boolean isProjectile() {
		return this.projectile;
	}

	public DamageSource setProjectile() {
		this.projectile = true;
		return this;
	}

	public boolean isExplosion() {
		return this.explosion;
	}

	public DamageSource setExplosion() {
		this.explosion = true;
		return this;
	}

	public boolean isUnblockable() {
		return this.isUnblockable;
	}

	public float getHungerDamage() {
		return this.hungerDamage;
	}

	public boolean canHarmInCreative() {
		return this.isDamageAllowedInCreativeMode;
	}

	protected DamageSource(String var1) {
		this.damageType = var1;
	}

	public Entity getSourceOfDamage() {
		return this.getEntity();
	}

	public Entity getEntity() {
		return null;
	}

	protected DamageSource setDamageBypassesArmor() {
		this.isUnblockable = true;
		this.hungerDamage = 0.0F;
		return this;
	}

	protected DamageSource setDamageAllowedInCreativeMode() {
		this.isDamageAllowedInCreativeMode = true;
		return this;
	}

	protected DamageSource setFireDamage() {
		this.fireDamage = true;
		return this;
	}

	public String getDeathMessage(EntityLiving var1) {
		EntityLiving var2 = var1.func_94060_bK();
		String var3 = "death.attack." + this.damageType;
		String var4 = var3 + ".player";
		return var2 != null && StatCollector.func_94522_b(var4) ? StatCollector.translateToLocalFormatted(var4, new Object[]{var1.getTranslatedEntityName(), var2.getTranslatedEntityName()}) : StatCollector.translateToLocalFormatted(var3, new Object[]{var1.getTranslatedEntityName()});
	}

	public boolean isFireDamage() {
		return this.fireDamage;
	}

	public String getDamageType() {
		return this.damageType;
	}

	public DamageSource setDifficultyScaled() {
		this.difficultyScaled = true;
		return this;
	}

	public boolean isDifficultyScaled() {
		return this.difficultyScaled;
	}

	public boolean isMagicDamage() {
		return this.magicDamage;
	}

	public DamageSource setMagicDamage() {
		this.magicDamage = true;
		return this;
	}
}
