package net.minecraft.src;

public class ItemArmor extends Item {
	private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
	private static final String[] field_94606_cu = new String[]{"helmetCloth_overlay", "chestplateCloth_overlay", "leggingsCloth_overlay", "bootsCloth_overlay"};
	public static final String[] field_94603_a = new String[]{"slot_empty_helmet", "slot_empty_chestplate", "slot_empty_leggings", "slot_empty_boots"};
	private static final IBehaviorDispenseItem field_96605_cw = new BehaviorDispenseArmor();
	public final int armorType;
	public final int damageReduceAmount;
	public final int renderIndex;
	private final EnumArmorMaterial material;
	private Icon field_94605_cw;
	private Icon field_94604_cx;

	public ItemArmor(int var1, EnumArmorMaterial var2, int var3, int var4) {
		super(var1);
		this.material = var2;
		this.armorType = var4;
		this.renderIndex = var3;
		this.damageReduceAmount = var2.getDamageReductionAmount(var4);
		this.setMaxDamage(var2.getDurability(var4));
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabCombat);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, field_96605_cw);
	}

	public int getColorFromItemStack(ItemStack var1, int var2) {
		if(var2 > 0) {
			return 16777215;
		} else {
			int var3 = this.getColor(var1);
			if(var3 < 0) {
				var3 = 16777215;
			}

			return var3;
		}
	}

	public boolean requiresMultipleRenderPasses() {
		return this.material == EnumArmorMaterial.CLOTH;
	}

	public int getItemEnchantability() {
		return this.material.getEnchantability();
	}

	public EnumArmorMaterial getArmorMaterial() {
		return this.material;
	}

	public boolean hasColor(ItemStack var1) {
		return this.material != EnumArmorMaterial.CLOTH ? false : (!var1.hasTagCompound() ? false : (!var1.getTagCompound().hasKey("display") ? false : var1.getTagCompound().getCompoundTag("display").hasKey("color")));
	}

	public int getColor(ItemStack var1) {
		if(this.material != EnumArmorMaterial.CLOTH) {
			return -1;
		} else {
			NBTTagCompound var2 = var1.getTagCompound();
			if(var2 == null) {
				return 10511680;
			} else {
				NBTTagCompound var3 = var2.getCompoundTag("display");
				return var3 == null ? 10511680 : (var3.hasKey("color") ? var3.getInteger("color") : 10511680);
			}
		}
	}

	public Icon getIconFromDamageForRenderPass(int var1, int var2) {
		return var2 == 1 ? this.field_94605_cw : super.getIconFromDamageForRenderPass(var1, var2);
	}

	public void removeColor(ItemStack var1) {
		if(this.material == EnumArmorMaterial.CLOTH) {
			NBTTagCompound var2 = var1.getTagCompound();
			if(var2 != null) {
				NBTTagCompound var3 = var2.getCompoundTag("display");
				if(var3.hasKey("color")) {
					var3.removeTag("color");
				}

			}
		}
	}

	public void func_82813_b(ItemStack var1, int var2) {
		if(this.material != EnumArmorMaterial.CLOTH) {
			throw new UnsupportedOperationException("Can\'t dye non-leather!");
		} else {
			NBTTagCompound var3 = var1.getTagCompound();
			if(var3 == null) {
				var3 = new NBTTagCompound();
				var1.setTagCompound(var3);
			}

			NBTTagCompound var4 = var3.getCompoundTag("display");
			if(!var3.hasKey("display")) {
				var3.setCompoundTag("display", var4);
			}

			var4.setInteger("color", var2);
		}
	}

	public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
		return this.material.getArmorCraftingMaterial() == var2.itemID ? true : super.getIsRepairable(var1, var2);
	}

	public void registerIcons(IconRegister var1) {
		super.registerIcons(var1);
		if(this.material == EnumArmorMaterial.CLOTH) {
			this.field_94605_cw = var1.registerIcon(field_94606_cu[this.armorType]);
		}

		this.field_94604_cx = var1.registerIcon(field_94603_a[this.armorType]);
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		int var4 = EntityLiving.getArmorPosition(var1) - 1;
		ItemStack var5 = var3.getCurrentArmor(var4);
		if(var5 == null) {
			var3.setCurrentItemOrArmor(var4, var1.copy());
			var1.stackSize = 0;
		}

		return var1;
	}

	public static Icon func_94602_b(int var0) {
		switch(var0) {
		case 0:
			return Item.helmetDiamond.field_94604_cx;
		case 1:
			return Item.plateDiamond.field_94604_cx;
		case 2:
			return Item.legsDiamond.field_94604_cx;
		case 3:
			return Item.bootsDiamond.field_94604_cx;
		default:
			return null;
		}
	}

	static int[] getMaxDamageArray() {
		return maxDamageArray;
	}
}
