package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class ItemFirework extends Item {
	public ItemFirework(int var1) {
		super(var1);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		if(!var3.isRemote) {
			EntityFireworkRocket var11 = new EntityFireworkRocket(var3, (double)((float)var4 + var8), (double)((float)var5 + var9), (double)((float)var6 + var10), var1);
			var3.spawnEntityInWorld(var11);
			if(!var2.capabilities.isCreativeMode) {
				--var1.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
		if(var1.hasTagCompound()) {
			NBTTagCompound var5 = var1.getTagCompound().getCompoundTag("Fireworks");
			if(var5 != null) {
				if(var5.hasKey("Flight")) {
					var3.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + var5.getByte("Flight"));
				}

				NBTTagList var6 = var5.getTagList("Explosions");
				if(var6 != null && var6.tagCount() > 0) {
					for(int var7 = 0; var7 < var6.tagCount(); ++var7) {
						NBTTagCompound var8 = (NBTTagCompound)var6.tagAt(var7);
						ArrayList var9 = new ArrayList();
						ItemFireworkCharge.func_92107_a(var8, var9);
						if(var9.size() > 0) {
							for(int var10 = 1; var10 < var9.size(); ++var10) {
								var9.set(var10, "  " + (String)var9.get(var10));
							}

							var3.addAll(var9);
						}
					}
				}

			}
		}
	}
}
