package net.minecraft.src;

public class ItemShears extends Item {
	public ItemShears(int var1) {
		super(var1);
		this.setMaxStackSize(1);
		this.setMaxDamage(238);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	public boolean onBlockDestroyed(ItemStack var1, World var2, int var3, int var4, int var5, int var6, EntityLiving var7) {
		if(var3 != Block.leaves.blockID && var3 != Block.web.blockID && var3 != Block.tallGrass.blockID && var3 != Block.vine.blockID && var3 != Block.tripWire.blockID) {
			return super.onBlockDestroyed(var1, var2, var3, var4, var5, var6, var7);
		} else {
			var1.damageItem(1, var7);
			return true;
		}
	}

	public boolean canHarvestBlock(Block var1) {
		return var1.blockID == Block.web.blockID || var1.blockID == Block.redstoneWire.blockID || var1.blockID == Block.tripWire.blockID;
	}

	public float getStrVsBlock(ItemStack var1, Block var2) {
		return var2.blockID != Block.web.blockID && var2.blockID != Block.leaves.blockID ? (var2.blockID == Block.cloth.blockID ? 5.0F : super.getStrVsBlock(var1, var2)) : 15.0F;
	}
}
