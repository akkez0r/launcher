package net.minecraft.src;

public class ItemSword extends Item {
	private int weaponDamage;
	private final EnumToolMaterial toolMaterial;

	public ItemSword(int var1, EnumToolMaterial var2) {
		super(var1);
		this.toolMaterial = var2;
		this.maxStackSize = 1;
		this.setMaxDamage(var2.getMaxUses());
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.weaponDamage = 4 + var2.getDamageVsEntity();
	}

	public int func_82803_g() {
		return this.toolMaterial.getDamageVsEntity();
	}

	public float getStrVsBlock(ItemStack var1, Block var2) {
		if(var2.blockID == Block.web.blockID) {
			return 15.0F;
		} else {
			Material var3 = var2.blockMaterial;
			return var3 != Material.plants && var3 != Material.vine && var3 != Material.coral && var3 != Material.leaves && var3 != Material.pumpkin ? 1.0F : 1.5F;
		}
	}

	public boolean hitEntity(ItemStack var1, EntityLiving var2, EntityLiving var3) {
		var1.damageItem(1, var3);
		return true;
	}

	public boolean onBlockDestroyed(ItemStack var1, World var2, int var3, int var4, int var5, int var6, EntityLiving var7) {
		if((double)Block.blocksList[var3].getBlockHardness(var2, var4, var5, var6) != 0.0D) {
			var1.damageItem(2, var7);
		}

		return true;
	}

	public int getDamageVsEntity(Entity var1) {
		return this.weaponDamage;
	}

	public boolean isFull3D() {
		return true;
	}

	public EnumAction getItemUseAction(ItemStack var1) {
		return EnumAction.block;
	}

	public int getMaxItemUseDuration(ItemStack var1) {
		return 72000;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
		return var1;
	}

	public boolean canHarvestBlock(Block var1) {
		return var1.blockID == Block.web.blockID;
	}

	public int getItemEnchantability() {
		return this.toolMaterial.getEnchantability();
	}

	public String getToolMaterialName() {
		return this.toolMaterial.toString();
	}

	public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
		return this.toolMaterial.getToolCraftingMaterial() == var2.itemID ? true : super.getIsRepairable(var1, var2);
	}
}
