package net.minecraft.src;

public class ItemHangingEntity extends Item {
	private final Class hangingEntityClass;

	public ItemHangingEntity(int var1, Class var2) {
		super(var1);
		this.hangingEntityClass = var2;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		if(var7 == 0) {
			return false;
		} else if(var7 == 1) {
			return false;
		} else {
			int var11 = Direction.facingToDirection[var7];
			EntityHanging var12 = this.createHangingEntity(var3, var4, var5, var6, var11);
			if(!var2.canPlayerEdit(var4, var5, var6, var7, var1)) {
				return false;
			} else {
				if(var12 != null && var12.onValidSurface()) {
					if(!var3.isRemote) {
						var3.spawnEntityInWorld(var12);
					}

					--var1.stackSize;
				}

				return true;
			}
		}
	}

	private EntityHanging createHangingEntity(World var1, int var2, int var3, int var4, int var5) {
		return (EntityHanging)(this.hangingEntityClass == EntityPainting.class ? new EntityPainting(var1, var2, var3, var4, var5) : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(var1, var2, var3, var4, var5) : null));
	}
}
