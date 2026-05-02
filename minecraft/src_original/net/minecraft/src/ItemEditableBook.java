package net.minecraft.src;

import java.util.List;

public class ItemEditableBook extends Item {
	public ItemEditableBook(int var1) {
		super(var1);
		this.setMaxStackSize(1);
	}

	public static boolean validBookTagContents(NBTTagCompound var0) {
		if(!ItemWritableBook.validBookTagPages(var0)) {
			return false;
		} else if(!var0.hasKey("title")) {
			return false;
		} else {
			String var1 = var0.getString("title");
			return var1 != null && var1.length() <= 16 ? var0.hasKey("author") : false;
		}
	}

	public String getItemDisplayName(ItemStack var1) {
		if(var1.hasTagCompound()) {
			NBTTagCompound var2 = var1.getTagCompound();
			NBTTagString var3 = (NBTTagString)var2.getTag("title");
			if(var3 != null) {
				return var3.toString();
			}
		}

		return super.getItemDisplayName(var1);
	}

	public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
		if(var1.hasTagCompound()) {
			NBTTagCompound var5 = var1.getTagCompound();
			NBTTagString var6 = (NBTTagString)var5.getTag("author");
			if(var6 != null) {
				var3.add(EnumChatFormatting.GRAY + String.format(StatCollector.translateToLocalFormatted("book.byAuthor", new Object[]{var6.data}), new Object[0]));
			}
		}

	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		var3.displayGUIBook(var1);
		return var1;
	}

	public boolean getShareTag() {
		return true;
	}

	public boolean hasEffect(ItemStack var1) {
		return true;
	}
}
