package net.minecraft.src;

public class ItemCarrotOnAStick extends Item {
	public ItemCarrotOnAStick(int var1) {
		super(var1);
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxStackSize(1);
		this.setMaxDamage(25);
	}

	public boolean isFull3D() {
		return true;
	}

	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(var3.isRiding() && var3.ridingEntity instanceof EntityPig) {
			EntityPig var4 = (EntityPig)var3.ridingEntity;
			if(var4.getAIControlledByPlayer().isControlledByPlayer() && var1.getMaxDamage() - var1.getItemDamage() >= 7) {
				var4.getAIControlledByPlayer().boostSpeed();
				var1.damageItem(7, var3);
				if(var1.stackSize == 0) {
					ItemStack var5 = new ItemStack(Item.fishingRod);
					var5.setTagCompound(var1.stackTagCompound);
					return var5;
				}
			}
		}

		return var1;
	}
}
