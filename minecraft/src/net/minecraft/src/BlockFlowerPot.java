package net.minecraft.src;

import java.util.Random;

public class BlockFlowerPot extends Block {
	public BlockFlowerPot(int var1) {
		super(var1, Material.circuits);
		this.setBlockBoundsForItemRender();
	}

	public void setBlockBoundsForItemRender() {
		float var1 = 6.0F / 16.0F;
		float var2 = var1 / 2.0F;
		this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var1, 0.5F + var2);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 33;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		ItemStack var10 = var5.inventory.getCurrentItem();
		if(var10 == null) {
			return false;
		} else if(var1.getBlockMetadata(var2, var3, var4) != 0) {
			return false;
		} else {
			int var11 = getMetaForPlant(var10);
			if(var11 > 0) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var11, 2);
				if(!var5.capabilities.isCreativeMode && --var10.stackSize <= 0) {
					var5.inventory.setInventorySlotContents(var5.inventory.currentItem, (ItemStack)null);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		ItemStack var5 = getPlantForMeta(var1.getBlockMetadata(var2, var3, var4));
		return var5 == null ? Item.flowerPot.itemID : var5.itemID;
	}

	public int getDamageValue(World var1, int var2, int var3, int var4) {
		ItemStack var5 = getPlantForMeta(var1.getBlockMetadata(var2, var3, var4));
		return var5 == null ? Item.flowerPot.itemID : var5.getItemDamage();
	}

	public boolean isFlowerPot() {
		return true;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return super.canPlaceBlockAt(var1, var2, var3, var4) && var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4);
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4)) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, var7);
		if(var5 > 0) {
			ItemStack var8 = getPlantForMeta(var5);
			if(var8 != null) {
				this.dropBlockAsItem_do(var1, var2, var3, var4, var8);
			}
		}

	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.flowerPot.itemID;
	}

	public static ItemStack getPlantForMeta(int var0) {
		switch(var0) {
		case 1:
			return new ItemStack(Block.plantRed);
		case 2:
			return new ItemStack(Block.plantYellow);
		case 3:
			return new ItemStack(Block.sapling, 1, 0);
		case 4:
			return new ItemStack(Block.sapling, 1, 1);
		case 5:
			return new ItemStack(Block.sapling, 1, 2);
		case 6:
			return new ItemStack(Block.sapling, 1, 3);
		case 7:
			return new ItemStack(Block.mushroomRed);
		case 8:
			return new ItemStack(Block.mushroomBrown);
		case 9:
			return new ItemStack(Block.cactus);
		case 10:
			return new ItemStack(Block.deadBush);
		case 11:
			return new ItemStack(Block.tallGrass, 1, 2);
		default:
			return null;
		}
	}

	public static int getMetaForPlant(ItemStack var0) {
		int var1 = var0.getItem().itemID;
		if(var1 == Block.plantRed.blockID) {
			return 1;
		} else if(var1 == Block.plantYellow.blockID) {
			return 2;
		} else if(var1 == Block.cactus.blockID) {
			return 9;
		} else if(var1 == Block.mushroomBrown.blockID) {
			return 8;
		} else if(var1 == Block.mushroomRed.blockID) {
			return 7;
		} else if(var1 == Block.deadBush.blockID) {
			return 10;
		} else {
			if(var1 == Block.sapling.blockID) {
				switch(var0.getItemDamage()) {
				case 0:
					return 3;
				case 1:
					return 4;
				case 2:
					return 5;
				case 3:
					return 6;
				}
			}

			if(var1 == Block.tallGrass.blockID) {
				switch(var0.getItemDamage()) {
				case 2:
					return 11;
				}
			}

			return 0;
		}
	}
}
