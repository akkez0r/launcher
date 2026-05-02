package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MerchantRecipeList extends ArrayList {
	public MerchantRecipeList() {
	}

	public MerchantRecipeList(NBTTagCompound var1) {
		this.readRecipiesFromTags(var1);
	}

	public MerchantRecipe canRecipeBeUsed(ItemStack var1, ItemStack var2, int var3) {
		if(var3 > 0 && var3 < this.size()) {
			MerchantRecipe var6 = (MerchantRecipe)this.get(var3);
			return var1.itemID != var6.getItemToBuy().itemID || (var2 != null || var6.hasSecondItemToBuy()) && (!var6.hasSecondItemToBuy() || var2 == null || var6.getSecondItemToBuy().itemID != var2.itemID) || var1.stackSize < var6.getItemToBuy().stackSize || var6.hasSecondItemToBuy() && var2.stackSize < var6.getSecondItemToBuy().stackSize ? null : var6;
		} else {
			for(int var4 = 0; var4 < this.size(); ++var4) {
				MerchantRecipe var5 = (MerchantRecipe)this.get(var4);
				if(var1.itemID == var5.getItemToBuy().itemID && var1.stackSize >= var5.getItemToBuy().stackSize && (!var5.hasSecondItemToBuy() && var2 == null || var5.hasSecondItemToBuy() && var2 != null && var5.getSecondItemToBuy().itemID == var2.itemID && var2.stackSize >= var5.getSecondItemToBuy().stackSize)) {
					return var5;
				}
			}

			return null;
		}
	}

	public void addToListWithCheck(MerchantRecipe var1) {
		for(int var2 = 0; var2 < this.size(); ++var2) {
			MerchantRecipe var3 = (MerchantRecipe)this.get(var2);
			if(var1.hasSameIDsAs(var3)) {
				if(var1.hasSameItemsAs(var3)) {
					this.set(var2, var1);
				}

				return;
			}
		}

		this.add(var1);
	}

	public void writeRecipiesToStream(DataOutputStream var1) throws IOException {
		var1.writeByte((byte)(this.size() & 255));

		for(int var2 = 0; var2 < this.size(); ++var2) {
			MerchantRecipe var3 = (MerchantRecipe)this.get(var2);
			Packet.writeItemStack(var3.getItemToBuy(), var1);
			Packet.writeItemStack(var3.getItemToSell(), var1);
			ItemStack var4 = var3.getSecondItemToBuy();
			var1.writeBoolean(var4 != null);
			if(var4 != null) {
				Packet.writeItemStack(var4, var1);
			}

			var1.writeBoolean(var3.func_82784_g());
		}

	}

	public static MerchantRecipeList readRecipiesFromStream(DataInputStream var0) throws IOException {
		MerchantRecipeList var1 = new MerchantRecipeList();
		int var2 = var0.readByte() & 255;

		for(int var3 = 0; var3 < var2; ++var3) {
			ItemStack var4 = Packet.readItemStack(var0);
			ItemStack var5 = Packet.readItemStack(var0);
			ItemStack var6 = null;
			if(var0.readBoolean()) {
				var6 = Packet.readItemStack(var0);
			}

			boolean var7 = var0.readBoolean();
			MerchantRecipe var8 = new MerchantRecipe(var4, var6, var5);
			if(var7) {
				var8.func_82785_h();
			}

			var1.add(var8);
		}

		return var1;
	}

	public void readRecipiesFromTags(NBTTagCompound var1) {
		NBTTagList var2 = var1.getTagList("Recipes");

		for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
			this.add(new MerchantRecipe(var4));
		}

	}

	public NBTTagCompound getRecipiesAsTags() {
		NBTTagCompound var1 = new NBTTagCompound();
		NBTTagList var2 = new NBTTagList("Recipes");

		for(int var3 = 0; var3 < this.size(); ++var3) {
			MerchantRecipe var4 = (MerchantRecipe)this.get(var3);
			var2.appendTag(var4.writeToTags());
		}

		var1.setTag("Recipes", var2);
		return var1;
	}
}
