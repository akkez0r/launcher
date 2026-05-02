package net.minecraft.src;

public class ItemCloth extends ItemBlock {
	public ItemCloth(int var1) {
		super(var1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public Icon getIconFromDamage(int var1) {
		return Block.cloth.getIcon(2, BlockCloth.getBlockFromDye(var1));
	}

	public int getMetadata(int var1) {
		return var1;
	}

	public String getUnlocalizedName(ItemStack var1) {
		return super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[BlockCloth.getBlockFromDye(var1.getItemDamage())];
	}
}
