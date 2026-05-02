package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class ItemMonsterPlacer extends Item {
	private Icon theIcon;

	public ItemMonsterPlacer(int var1) {
		super(var1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public String getItemDisplayName(ItemStack var1) {
		String var2 = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
		String var3 = EntityList.getStringFromID(var1.getItemDamage());
		if(var3 != null) {
			var2 = var2 + " " + StatCollector.translateToLocal("entity." + var3 + ".name");
		}

		return var2;
	}

	public int getColorFromItemStack(ItemStack var1, int var2) {
		EntityEggInfo var3 = (EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(var1.getItemDamage()));
		return var3 != null ? (var2 == 0 ? var3.primaryColor : var3.secondaryColor) : 16777215;
	}

	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public Icon getIconFromDamageForRenderPass(int var1, int var2) {
		return var2 > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(var1, var2);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		if(var3.isRemote) {
			return true;
		} else {
			int var11 = var3.getBlockId(var4, var5, var6);
			var4 += Facing.offsetsXForSide[var7];
			var5 += Facing.offsetsYForSide[var7];
			var6 += Facing.offsetsZForSide[var7];
			double var12 = 0.0D;
			if(var7 == 1 && Block.blocksList[var11] != null && Block.blocksList[var11].getRenderType() == 11) {
				var12 = 0.5D;
			}

			Entity var14 = spawnCreature(var3, var1.getItemDamage(), (double)var4 + 0.5D, (double)var5 + var12, (double)var6 + 0.5D);
			if(var14 != null) {
				if(var14 instanceof EntityLiving && var1.hasDisplayName()) {
					((EntityLiving)var14).func_94058_c(var1.getDisplayName());
				}

				if(!var2.capabilities.isCreativeMode) {
					--var1.stackSize;
				}
			}

			return true;
		}
	}

	public static Entity spawnCreature(World var0, int var1, double var2, double var4, double var6) {
		if(!EntityList.entityEggs.containsKey(Integer.valueOf(var1))) {
			return null;
		} else {
			Entity var8 = null;

			for(int var9 = 0; var9 < 1; ++var9) {
				var8 = EntityList.createEntityByID(var1, var0);
				if(var8 != null && var8 instanceof EntityLiving) {
					EntityLiving var10 = (EntityLiving)var8;
					var8.setLocationAndAngles(var2, var4, var6, MathHelper.wrapAngleTo180_float(var0.rand.nextFloat() * 360.0F), 0.0F);
					var10.rotationYawHead = var10.rotationYaw;
					var10.renderYawOffset = var10.rotationYaw;
					var10.initCreature();
					var0.spawnEntityInWorld(var8);
					var10.playLivingSound();
				}
			}

			return var8;
		}
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		Iterator var4 = EntityList.entityEggs.values().iterator();

		while(var4.hasNext()) {
			EntityEggInfo var5 = (EntityEggInfo)var4.next();
			var3.add(new ItemStack(var1, 1, var5.spawnedID));
		}

	}

	public void registerIcons(IconRegister var1) {
		super.registerIcons(var1);
		this.theIcon = var1.registerIcon("monsterPlacer_overlay");
	}
}
