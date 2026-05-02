package net.minecraft.src;

public class BlockJukeBox extends BlockContainer {
	private Icon theIcon;

	protected BlockJukeBox(int var1) {
		super(var1, Material.wood);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.theIcon : this.blockIcon;
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.getBlockMetadata(var2, var3, var4) == 0) {
			return false;
		} else {
			this.ejectRecord(var1, var2, var3, var4);
			return true;
		}
	}

	public void insertRecord(World var1, int var2, int var3, int var4, ItemStack var5) {
		if(!var1.isRemote) {
			TileEntityRecordPlayer var6 = (TileEntityRecordPlayer)var1.getBlockTileEntity(var2, var3, var4);
			if(var6 != null) {
				var6.func_96098_a(var5.copy());
				var1.setBlockMetadataWithNotify(var2, var3, var4, 1, 2);
			}
		}
	}

	public void ejectRecord(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote) {
			TileEntityRecordPlayer var5 = (TileEntityRecordPlayer)var1.getBlockTileEntity(var2, var3, var4);
			if(var5 != null) {
				ItemStack var6 = var5.func_96097_a();
				if(var6 != null) {
					var1.playAuxSFX(1005, var2, var3, var4, 0);
					var1.playRecord((String)null, var2, var3, var4);
					var5.func_96098_a((ItemStack)null);
					var1.setBlockMetadataWithNotify(var2, var3, var4, 0, 2);
					float var7 = 0.7F;
					double var8 = (double)(var1.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.5D;
					double var10 = (double)(var1.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.2D + 0.6D;
					double var12 = (double)(var1.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.5D;
					ItemStack var14 = var6.copy();
					EntityItem var15 = new EntityItem(var1, (double)var2 + var8, (double)var3 + var10, (double)var4 + var12, var14);
					var15.delayBeforeCanPickup = 10;
					var1.spawnEntityInWorld(var15);
				}
			}
		}
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		this.ejectRecord(var1, var2, var3, var4);
		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		if(!var1.isRemote) {
			super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, 0);
		}
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityRecordPlayer();
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("musicBlock");
		this.theIcon = var1.registerIcon("jukebox_top");
	}

	public boolean hasComparatorInputOverride() {
		return true;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		ItemStack var6 = ((TileEntityRecordPlayer)var1.getBlockTileEntity(var2, var3, var4)).func_96097_a();
		return var6 == null ? 0 : var6.itemID + 1 - Item.record13.itemID;
	}
}
