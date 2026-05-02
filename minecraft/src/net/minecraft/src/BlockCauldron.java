package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockCauldron extends Block {
	private Icon field_94378_a;
	private Icon cauldronTopIcon;
	private Icon cauldronBottomIcon;

	public BlockCauldron(int var1) {
		super(var1, Material.iron);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.cauldronTopIcon : (var1 == 0 ? this.cauldronBottomIcon : this.blockIcon);
	}

	public void registerIcons(IconRegister var1) {
		this.field_94378_a = var1.registerIcon("cauldron_inner");
		this.cauldronTopIcon = var1.registerIcon("cauldron_top");
		this.cauldronBottomIcon = var1.registerIcon("cauldron_bottom");
		this.blockIcon = var1.registerIcon("cauldron_side");
	}

	public static Icon func_94375_b(String var0) {
		return var0 == "cauldron_inner" ? Block.cauldron.field_94378_a : (var0 == "cauldron_bottom" ? Block.cauldron.cauldronBottomIcon : null);
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 5.0F / 16.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		float var8 = 2.0F / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, var8, 1.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var8);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		this.setBlockBounds(1.0F - var8, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		this.setBlockBounds(0.0F, 0.0F, 1.0F - var8, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		this.setBlockBoundsForItemRender();
	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 24;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			ItemStack var10 = var5.inventory.getCurrentItem();
			if(var10 == null) {
				return true;
			} else {
				int var11 = var1.getBlockMetadata(var2, var3, var4);
				if(var10.itemID == Item.bucketWater.itemID) {
					if(var11 < 3) {
						if(!var5.capabilities.isCreativeMode) {
							var5.inventory.setInventorySlotContents(var5.inventory.currentItem, new ItemStack(Item.bucketEmpty));
						}

						var1.setBlockMetadataWithNotify(var2, var3, var4, 3, 2);
					}

					return true;
				} else {
					if(var10.itemID == Item.glassBottle.itemID) {
						if(var11 > 0) {
							ItemStack var12 = new ItemStack(Item.potion, 1, 0);
							if(!var5.inventory.addItemStackToInventory(var12)) {
								var1.spawnEntityInWorld(new EntityItem(var1, (double)var2 + 0.5D, (double)var3 + 1.5D, (double)var4 + 0.5D, var12));
							} else if(var5 instanceof EntityPlayerMP) {
								((EntityPlayerMP)var5).sendContainerToPlayer(var5.inventoryContainer);
							}

							--var10.stackSize;
							if(var10.stackSize <= 0) {
								var5.inventory.setInventorySlotContents(var5.inventory.currentItem, (ItemStack)null);
							}

							var1.setBlockMetadataWithNotify(var2, var3, var4, var11 - 1, 2);
						}
					} else if(var11 > 0 && var10.getItem() instanceof ItemArmor && ((ItemArmor)var10.getItem()).getArmorMaterial() == EnumArmorMaterial.CLOTH) {
						ItemArmor var13 = (ItemArmor)var10.getItem();
						var13.removeColor(var10);
						var1.setBlockMetadataWithNotify(var2, var3, var4, var11 - 1, 2);
						return true;
					}

					return true;
				}
			}
		}
	}

	public void fillWithRain(World var1, int var2, int var3, int var4) {
		if(var1.rand.nextInt(20) == 1) {
			int var5 = var1.getBlockMetadata(var2, var3, var4);
			if(var5 < 3) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var5 + 1, 2);
			}

		}
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.cauldron.itemID;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.cauldron.itemID;
	}
}
