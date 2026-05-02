package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemPotion extends Item {
	private HashMap effectCache = new HashMap();
	private static final Map field_77835_b = new LinkedHashMap();
	private Icon field_94591_c;
	private Icon field_94590_d;
	private Icon field_94592_ct;

	public ItemPotion(int var1) {
		super(var1);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabBrewing);
	}

	public List getEffects(ItemStack var1) {
		if(var1.hasTagCompound() && var1.getTagCompound().hasKey("CustomPotionEffects")) {
			ArrayList var6 = new ArrayList();
			NBTTagList var3 = var1.getTagCompound().getTagList("CustomPotionEffects");

			for(int var4 = 0; var4 < var3.tagCount(); ++var4) {
				NBTTagCompound var5 = (NBTTagCompound)var3.tagAt(var4);
				var6.add(PotionEffect.readCustomPotionEffectFromNBT(var5));
			}

			return var6;
		} else {
			List var2 = (List)this.effectCache.get(Integer.valueOf(var1.getItemDamage()));
			if(var2 == null) {
				var2 = PotionHelper.getPotionEffects(var1.getItemDamage(), false);
				this.effectCache.put(Integer.valueOf(var1.getItemDamage()), var2);
			}

			return var2;
		}
	}

	public List getEffects(int var1) {
		List var2 = (List)this.effectCache.get(Integer.valueOf(var1));
		if(var2 == null) {
			var2 = PotionHelper.getPotionEffects(var1, false);
			this.effectCache.put(Integer.valueOf(var1), var2);
		}

		return var2;
	}

	public ItemStack onEaten(ItemStack var1, World var2, EntityPlayer var3) {
		if(!var3.capabilities.isCreativeMode) {
			--var1.stackSize;
		}

		if(!var2.isRemote) {
			List var4 = this.getEffects(var1);
			if(var4 != null) {
				Iterator var5 = var4.iterator();

				while(var5.hasNext()) {
					PotionEffect var6 = (PotionEffect)var5.next();
					var3.addPotionEffect(new PotionEffect(var6));
				}
			}
		}

		if(!var3.capabilities.isCreativeMode) {
			if(var1.stackSize <= 0) {
				return new ItemStack(Item.glassBottle);
			}

			var3.inventory.addItemStackToInventory(new ItemStack(Item.glassBottle));
		}

		return var1;
	}

	public int getMaxItemUseDuration(ItemStack var1) {
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack var1) {
		return EnumAction.drink;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(isSplash(var1.getItemDamage())) {
			if(!var3.capabilities.isCreativeMode) {
				--var1.stackSize;
			}

			var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if(!var2.isRemote) {
				var2.spawnEntityInWorld(new EntityPotion(var2, var3, var1));
			}

			return var1;
		} else {
			var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
			return var1;
		}
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		return false;
	}

	public Icon getIconFromDamage(int var1) {
		return isSplash(var1) ? this.field_94591_c : this.field_94590_d;
	}

	public Icon getIconFromDamageForRenderPass(int var1, int var2) {
		return var2 == 0 ? this.field_94592_ct : super.getIconFromDamageForRenderPass(var1, var2);
	}

	public static boolean isSplash(int var0) {
		return (var0 & 16384) != 0;
	}

	public int getColorFromDamage(int var1) {
		return PotionHelper.func_77915_a(var1, false);
	}

	public int getColorFromItemStack(ItemStack var1, int var2) {
		return var2 > 0 ? 16777215 : this.getColorFromDamage(var1.getItemDamage());
	}

	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public boolean isEffectInstant(int var1) {
		List var2 = this.getEffects(var1);
		if(var2 != null && !var2.isEmpty()) {
			Iterator var3 = var2.iterator();

			PotionEffect var4;
			do {
				if(!var3.hasNext()) {
					return false;
				}

				var4 = (PotionEffect)var3.next();
			} while(!Potion.potionTypes[var4.getPotionID()].isInstant());

			return true;
		} else {
			return false;
		}
	}

	public String getItemDisplayName(ItemStack var1) {
		if(var1.getItemDamage() == 0) {
			return StatCollector.translateToLocal("item.emptyPotion.name").trim();
		} else {
			String var2 = "";
			if(isSplash(var1.getItemDamage())) {
				var2 = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
			}

			List var3 = Item.potion.getEffects(var1);
			String var4;
			if(var3 != null && !var3.isEmpty()) {
				var4 = ((PotionEffect)var3.get(0)).getEffectName();
				var4 = var4 + ".postfix";
				return var2 + StatCollector.translateToLocal(var4).trim();
			} else {
				var4 = PotionHelper.func_77905_c(var1.getItemDamage());
				return StatCollector.translateToLocal(var4).trim() + " " + super.getItemDisplayName(var1);
			}
		}
	}

	public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
		if(var1.getItemDamage() != 0) {
			List var5 = Item.potion.getEffects(var1);
			if(var5 != null && !var5.isEmpty()) {
				Iterator var9 = var5.iterator();

				while(var9.hasNext()) {
					PotionEffect var7 = (PotionEffect)var9.next();
					String var8 = StatCollector.translateToLocal(var7.getEffectName()).trim();
					if(var7.getAmplifier() > 0) {
						var8 = var8 + " " + StatCollector.translateToLocal("potion.potency." + var7.getAmplifier()).trim();
					}

					if(var7.getDuration() > 20) {
						var8 = var8 + " (" + Potion.getDurationString(var7) + ")";
					}

					if(Potion.potionTypes[var7.getPotionID()].isBadEffect()) {
						var3.add(EnumChatFormatting.RED + var8);
					} else {
						var3.add(EnumChatFormatting.GRAY + var8);
					}
				}
			} else {
				String var6 = StatCollector.translateToLocal("potion.empty").trim();
				var3.add(EnumChatFormatting.GRAY + var6);
			}

		}
	}

	public boolean hasEffect(ItemStack var1) {
		List var2 = this.getEffects(var1);
		return var2 != null && !var2.isEmpty();
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		super.getSubItems(var1, var2, var3);
		int var5;
		if(field_77835_b.isEmpty()) {
			for(int var4 = 0; var4 <= 15; ++var4) {
				for(var5 = 0; var5 <= 1; ++var5) {
					int var6;
					if(var5 == 0) {
						var6 = var4 | 8192;
					} else {
						var6 = var4 | 16384;
					}

					for(int var7 = 0; var7 <= 2; ++var7) {
						int var8 = var6;
						if(var7 != 0) {
							if(var7 == 1) {
								var8 = var6 | 32;
							} else if(var7 == 2) {
								var8 = var6 | 64;
							}
						}

						List var9 = PotionHelper.getPotionEffects(var8, false);
						if(var9 != null && !var9.isEmpty()) {
							field_77835_b.put(var9, Integer.valueOf(var8));
						}
					}
				}
			}
		}

		Iterator var10 = field_77835_b.values().iterator();

		while(var10.hasNext()) {
			var5 = ((Integer)var10.next()).intValue();
			var3.add(new ItemStack(var1, 1, var5));
		}

	}

	public void registerIcons(IconRegister var1) {
		this.field_94590_d = var1.registerIcon("potion");
		this.field_94591_c = var1.registerIcon("potion_splash");
		this.field_94592_ct = var1.registerIcon("potion_contents");
	}

	public static Icon func_94589_d(String var0) {
		return var0 == "potion" ? Item.potion.field_94590_d : (var0 == "potion_splash" ? Item.potion.field_94591_c : (var0 == "potion_contents" ? Item.potion.field_94592_ct : null));
	}
}
