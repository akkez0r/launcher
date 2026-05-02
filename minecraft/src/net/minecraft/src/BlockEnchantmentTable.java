package net.minecraft.src;

import java.util.Random;

public class BlockEnchantmentTable extends BlockContainer {
	private Icon field_94461_a;
	private Icon field_94460_b;

	protected BlockEnchantmentTable(int var1) {
		super(var1, Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 12.0F / 16.0F, 1.0F);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		super.randomDisplayTick(var1, var2, var3, var4, var5);

		for(int var6 = var2 - 2; var6 <= var2 + 2; ++var6) {
			for(int var7 = var4 - 2; var7 <= var4 + 2; ++var7) {
				if(var6 > var2 - 2 && var6 < var2 + 2 && var7 == var4 - 1) {
					var7 = var4 + 2;
				}

				if(var5.nextInt(16) == 0) {
					for(int var8 = var3; var8 <= var3 + 1; ++var8) {
						if(var1.getBlockId(var6, var8, var7) == Block.bookShelf.blockID) {
							if(!var1.isAirBlock((var6 - var2) / 2 + var2, var8, (var7 - var4) / 2 + var4)) {
								break;
							}

							var1.spawnParticle("enchantmenttable", (double)var2 + 0.5D, (double)var3 + 2.0D, (double)var4 + 0.5D, (double)((float)(var6 - var2) + var5.nextFloat()) - 0.5D, (double)((float)(var8 - var3) - var5.nextFloat() - 1.0F), (double)((float)(var7 - var4) + var5.nextFloat()) - 0.5D);
						}
					}
				}
			}
		}

	}

	public boolean isOpaqueCube() {
		return false;
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 0 ? this.field_94460_b : (var1 == 1 ? this.field_94461_a : this.blockIcon);
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityEnchantmentTable();
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			TileEntityEnchantmentTable var10 = (TileEntityEnchantmentTable)var1.getBlockTileEntity(var2, var3, var4);
			var5.displayGUIEnchantment(var2, var3, var4, var10.func_94135_b() ? var10.func_94133_a() : null);
			return true;
		}
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		super.onBlockPlacedBy(var1, var2, var3, var4, var5, var6);
		if(var6.hasDisplayName()) {
			((TileEntityEnchantmentTable)var1.getBlockTileEntity(var2, var3, var4)).func_94134_a(var6.getDisplayName());
		}

	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("enchantment_side");
		this.field_94461_a = var1.registerIcon("enchantment_top");
		this.field_94460_b = var1.registerIcon("enchantment_bottom");
	}
}
