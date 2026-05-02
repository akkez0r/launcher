package net.minecraft.src;

import java.util.List;

public class ItemBlock extends Item {
	private int blockID;
	private Icon field_94588_b;

	public ItemBlock(int var1) {
		super(var1);
		this.blockID = var1 + 256;
	}

	public int getBlockID() {
		return this.blockID;
	}

	public int getSpriteNumber() {
		return Block.blocksList[this.blockID].getItemIconName() != null ? 1 : 0;
	}

	public Icon getIconFromDamage(int var1) {
		return this.field_94588_b != null ? this.field_94588_b : Block.blocksList[this.blockID].getBlockTextureFromSide(1);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		int var11 = var3.getBlockId(var4, var5, var6);
		if(var11 == Block.snow.blockID && (var3.getBlockMetadata(var4, var5, var6) & 7) < 1) {
			var7 = 1;
		} else if(var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID) {
			if(var7 == 0) {
				--var5;
			}

			if(var7 == 1) {
				++var5;
			}

			if(var7 == 2) {
				--var6;
			}

			if(var7 == 3) {
				++var6;
			}

			if(var7 == 4) {
				--var4;
			}

			if(var7 == 5) {
				++var4;
			}
		}

		if(var1.stackSize == 0) {
			return false;
		} else if(!var2.canPlayerEdit(var4, var5, var6, var7, var1)) {
			return false;
		} else if(var5 == 255 && Block.blocksList[this.blockID].blockMaterial.isSolid()) {
			return false;
		} else if(var3.canPlaceEntityOnSide(this.blockID, var4, var5, var6, false, var7, var2, var1)) {
			Block var12 = Block.blocksList[this.blockID];
			int var13 = this.getMetadata(var1.getItemDamage());
			int var14 = Block.blocksList[this.blockID].onBlockPlaced(var3, var4, var5, var6, var7, var8, var9, var10, var13);
			if(var3.setBlock(var4, var5, var6, this.blockID, var14, 3)) {
				if(var3.getBlockId(var4, var5, var6) == this.blockID) {
					Block.blocksList[this.blockID].onBlockPlacedBy(var3, var4, var5, var6, var2, var1);
					Block.blocksList[this.blockID].onPostBlockPlaced(var3, var4, var5, var6, var14);
				}

				var3.playSoundEffect((double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), var12.stepSound.getPlaceSound(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
				--var1.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean canPlaceItemBlockOnSide(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6, ItemStack var7) {
		int var8 = var1.getBlockId(var2, var3, var4);
		if(var8 == Block.snow.blockID) {
			var5 = 1;
		} else if(var8 != Block.vine.blockID && var8 != Block.tallGrass.blockID && var8 != Block.deadBush.blockID) {
			if(var5 == 0) {
				--var3;
			}

			if(var5 == 1) {
				++var3;
			}

			if(var5 == 2) {
				--var4;
			}

			if(var5 == 3) {
				++var4;
			}

			if(var5 == 4) {
				--var2;
			}

			if(var5 == 5) {
				++var2;
			}
		}

		return var1.canPlaceEntityOnSide(this.getBlockID(), var2, var3, var4, false, var5, (Entity)null, var7);
	}

	public String getUnlocalizedName(ItemStack var1) {
		return Block.blocksList[this.blockID].getUnlocalizedName();
	}

	public String getUnlocalizedName() {
		return Block.blocksList[this.blockID].getUnlocalizedName();
	}

	public CreativeTabs getCreativeTab() {
		return Block.blocksList[this.blockID].getCreativeTabToDisplayOn();
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		Block.blocksList[this.blockID].getSubBlocks(var1, var2, var3);
	}

	public void registerIcons(IconRegister var1) {
		String var2 = Block.blocksList[this.blockID].getItemIconName();
		if(var2 != null) {
			this.field_94588_b = var1.registerIcon(var2);
		}

	}
}
