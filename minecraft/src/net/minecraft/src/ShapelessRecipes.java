package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessRecipes implements IRecipe {
	private final ItemStack recipeOutput;
	private final List recipeItems;

	public ShapelessRecipes(ItemStack var1, List var2) {
		this.recipeOutput = var1;
		this.recipeItems = var2;
	}

	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	public boolean matches(InventoryCrafting var1, World var2) {
		ArrayList var3 = new ArrayList(this.recipeItems);

		for(int var4 = 0; var4 < 3; ++var4) {
			for(int var5 = 0; var5 < 3; ++var5) {
				ItemStack var6 = var1.getStackInRowAndColumn(var5, var4);
				if(var6 != null) {
					boolean var7 = false;
					Iterator var8 = var3.iterator();

					while(var8.hasNext()) {
						ItemStack var9 = (ItemStack)var8.next();
						if(var6.itemID == var9.itemID && (var9.getItemDamage() == Short.MAX_VALUE || var6.getItemDamage() == var9.getItemDamage())) {
							var7 = true;
							var3.remove(var9);
							break;
						}
					}

					if(!var7) {
						return false;
					}
				}
			}
		}

		return var3.isEmpty();
	}

	public ItemStack getCraftingResult(InventoryCrafting var1) {
		return this.recipeOutput.copy();
	}

	public int getRecipeSize() {
		return this.recipeItems.size();
	}
}
