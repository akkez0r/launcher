package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentHelper {
	private static final Random enchantmentRand = new Random();
	private static final EnchantmentModifierDamage enchantmentModifierDamage = new EnchantmentModifierDamage((Empty3)null);
	private static final EnchantmentModifierLiving enchantmentModifierLiving = new EnchantmentModifierLiving((Empty3)null);

	public static int getEnchantmentLevel(int var0, ItemStack var1) {
		if(var1 == null) {
			return 0;
		} else {
			NBTTagList var2 = var1.getEnchantmentTagList();
			if(var2 == null) {
				return 0;
			} else {
				for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
					short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
					short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");
					if(var4 == var0) {
						return var5;
					}
				}

				return 0;
			}
		}
	}

	public static Map getEnchantments(ItemStack var0) {
		LinkedHashMap var1 = new LinkedHashMap();
		NBTTagList var2 = var0.itemID == Item.enchantedBook.itemID ? Item.enchantedBook.func_92110_g(var0) : var0.getEnchantmentTagList();
		if(var2 != null) {
			for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
				short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
				short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");
				var1.put(Integer.valueOf(var4), Integer.valueOf(var5));
			}
		}

		return var1;
	}

	public static void setEnchantments(Map var0, ItemStack var1) {
		NBTTagList var2 = new NBTTagList();
		Iterator var3 = var0.keySet().iterator();

		while(var3.hasNext()) {
			int var4 = ((Integer)var3.next()).intValue();
			NBTTagCompound var5 = new NBTTagCompound();
			var5.setShort("id", (short)var4);
			var5.setShort("lvl", (short)((Integer)var0.get(Integer.valueOf(var4))).intValue());
			var2.appendTag(var5);
			if(var1.itemID == Item.enchantedBook.itemID) {
				Item.enchantedBook.func_92115_a(var1, new EnchantmentData(var4, ((Integer)var0.get(Integer.valueOf(var4))).intValue()));
			}
		}

		if(var2.tagCount() > 0) {
			if(var1.itemID != Item.enchantedBook.itemID) {
				var1.setTagInfo("ench", var2);
			}
		} else if(var1.hasTagCompound()) {
			var1.getTagCompound().removeTag("ench");
		}

	}

	public static int getMaxEnchantmentLevel(int var0, ItemStack[] var1) {
		if(var1 == null) {
			return 0;
		} else {
			int var2 = 0;
			ItemStack[] var3 = var1;
			int var4 = var1.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				ItemStack var6 = var3[var5];
				int var7 = getEnchantmentLevel(var0, var6);
				if(var7 > var2) {
					var2 = var7;
				}
			}

			return var2;
		}
	}

	private static void applyEnchantmentModifier(IEnchantmentModifier var0, ItemStack var1) {
		if(var1 != null) {
			NBTTagList var2 = var1.getEnchantmentTagList();
			if(var2 != null) {
				for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
					short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
					short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");
					if(Enchantment.enchantmentsList[var4] != null) {
						var0.calculateModifier(Enchantment.enchantmentsList[var4], var5);
					}
				}

			}
		}
	}

	private static void applyEnchantmentModifierArray(IEnchantmentModifier var0, ItemStack[] var1) {
		ItemStack[] var2 = var1;
		int var3 = var1.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];
			applyEnchantmentModifier(var0, var5);
		}

	}

	public static int getEnchantmentModifierDamage(ItemStack[] var0, DamageSource var1) {
		enchantmentModifierDamage.damageModifier = 0;
		enchantmentModifierDamage.source = var1;
		applyEnchantmentModifierArray(enchantmentModifierDamage, var0);
		if(enchantmentModifierDamage.damageModifier > 25) {
			enchantmentModifierDamage.damageModifier = 25;
		}

		return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
	}

	public static int getEnchantmentModifierLiving(EntityLiving var0, EntityLiving var1) {
		enchantmentModifierLiving.livingModifier = 0;
		enchantmentModifierLiving.entityLiving = var1;
		applyEnchantmentModifier(enchantmentModifierLiving, var0.getHeldItem());
		return enchantmentModifierLiving.livingModifier > 0 ? 1 + enchantmentRand.nextInt(enchantmentModifierLiving.livingModifier) : 0;
	}

	public static int getKnockbackModifier(EntityLiving var0, EntityLiving var1) {
		return getEnchantmentLevel(Enchantment.knockback.effectId, var0.getHeldItem());
	}

	public static int getFireAspectModifier(EntityLiving var0) {
		return getEnchantmentLevel(Enchantment.fireAspect.effectId, var0.getHeldItem());
	}

	public static int getRespiration(EntityLiving var0) {
		return getMaxEnchantmentLevel(Enchantment.respiration.effectId, var0.getLastActiveItems());
	}

	public static int getEfficiencyModifier(EntityLiving var0) {
		return getEnchantmentLevel(Enchantment.efficiency.effectId, var0.getHeldItem());
	}

	public static boolean getSilkTouchModifier(EntityLiving var0) {
		return getEnchantmentLevel(Enchantment.silkTouch.effectId, var0.getHeldItem()) > 0;
	}

	public static int getFortuneModifier(EntityLiving var0) {
		return getEnchantmentLevel(Enchantment.fortune.effectId, var0.getHeldItem());
	}

	public static int getLootingModifier(EntityLiving var0) {
		return getEnchantmentLevel(Enchantment.looting.effectId, var0.getHeldItem());
	}

	public static boolean getAquaAffinityModifier(EntityLiving var0) {
		return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, var0.getLastActiveItems()) > 0;
	}

	public static int func_92098_i(EntityLiving var0) {
		return getMaxEnchantmentLevel(Enchantment.thorns.effectId, var0.getLastActiveItems());
	}

	public static ItemStack func_92099_a(Enchantment var0, EntityLiving var1) {
		ItemStack[] var2 = var1.getLastActiveItems();
		int var3 = var2.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];
			if(var5 != null && getEnchantmentLevel(var0.effectId, var5) > 0) {
				return var5;
			}
		}

		return null;
	}

	public static int calcItemStackEnchantability(Random var0, int var1, int var2, ItemStack var3) {
		Item var4 = var3.getItem();
		int var5 = var4.getItemEnchantability();
		if(var5 <= 0) {
			return 0;
		} else {
			if(var2 > 15) {
				var2 = 15;
			}

			int var6 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
			return var1 == 0 ? Math.max(var6 / 3, 1) : (var1 == 1 ? var6 * 2 / 3 + 1 : Math.max(var6, var2 * 2));
		}
	}

	public static ItemStack addRandomEnchantment(Random var0, ItemStack var1, int var2) {
		List var3 = buildEnchantmentList(var0, var1, var2);
		boolean var4 = var1.itemID == Item.book.itemID;
		if(var4) {
			var1.itemID = Item.enchantedBook.itemID;
		}

		if(var3 != null) {
			Iterator var5 = var3.iterator();

			while(var5.hasNext()) {
				EnchantmentData var6 = (EnchantmentData)var5.next();
				if(var4) {
					Item.enchantedBook.func_92115_a(var1, var6);
				} else {
					var1.addEnchantment(var6.enchantmentobj, var6.enchantmentLevel);
				}
			}
		}

		return var1;
	}

	public static List buildEnchantmentList(Random var0, ItemStack var1, int var2) {
		Item var3 = var1.getItem();
		int var4 = var3.getItemEnchantability();
		if(var4 <= 0) {
			return null;
		} else {
			var4 /= 2;
			var4 = 1 + var0.nextInt((var4 >> 1) + 1) + var0.nextInt((var4 >> 1) + 1);
			int var5 = var4 + var2;
			float var6 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
			int var7 = (int)((float)var5 * (1.0F + var6) + 0.5F);
			if(var7 < 1) {
				var7 = 1;
			}

			ArrayList var8 = null;
			Map var9 = mapEnchantmentData(var7, var1);
			if(var9 != null && !var9.isEmpty()) {
				EnchantmentData var10 = (EnchantmentData)WeightedRandom.getRandomItem(var0, var9.values());
				if(var10 != null) {
					var8 = new ArrayList();
					var8.add(var10);

					for(int var11 = var7; var0.nextInt(50) <= var11; var11 >>= 1) {
						Iterator var12 = var9.keySet().iterator();

						while(var12.hasNext()) {
							Integer var13 = (Integer)var12.next();
							boolean var14 = true;
							Iterator var15 = var8.iterator();

							while(var15.hasNext()) {
								EnchantmentData var16 = (EnchantmentData)var15.next();
								if(!var16.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[var13.intValue()])) {
									var14 = false;
									break;
								}
							}

							if(!var14) {
								var12.remove();
							}
						}

						if(!var9.isEmpty()) {
							EnchantmentData var17 = (EnchantmentData)WeightedRandom.getRandomItem(var0, var9.values());
							var8.add(var17);
						}
					}
				}
			}

			return var8;
		}
	}

	public static Map mapEnchantmentData(int var0, ItemStack var1) {
		Item var2 = var1.getItem();
		HashMap var3 = null;
		boolean var4 = var1.itemID == Item.book.itemID;
		Enchantment[] var5 = Enchantment.enchantmentsList;
		int var6 = var5.length;

		for(int var7 = 0; var7 < var6; ++var7) {
			Enchantment var8 = var5[var7];
			if(var8 != null && (var8.type.canEnchantItem(var2) || var4)) {
				for(int var9 = var8.getMinLevel(); var9 <= var8.getMaxLevel(); ++var9) {
					if(var0 >= var8.getMinEnchantability(var9) && var0 <= var8.getMaxEnchantability(var9)) {
						if(var3 == null) {
							var3 = new HashMap();
						}

						var3.put(Integer.valueOf(var8.effectId), new EnchantmentData(var8, var9));
					}
				}
			}
		}

		return var3;
	}
}
