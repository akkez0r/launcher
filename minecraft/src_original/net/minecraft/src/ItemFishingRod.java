package net.minecraft.src;

public class ItemFishingRod extends Item {
	private Icon theIcon;

	public ItemFishingRod(int var1) {
		super(var1);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	public boolean isFull3D() {
		return true;
	}

	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(var3.fishEntity != null) {
			int var4 = var3.fishEntity.catchFish();
			var1.damageItem(var4, var3);
			var3.swingItem();
		} else {
			var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if(!var2.isRemote) {
				var2.spawnEntityInWorld(new EntityFishHook(var2, var3));
			}

			var3.swingItem();
		}

		return var1;
	}

	public void registerIcons(IconRegister var1) {
		super.registerIcons(var1);
		this.theIcon = var1.registerIcon("fishingRod_empty");
	}

	public Icon func_94597_g() {
		return this.theIcon;
	}
}
