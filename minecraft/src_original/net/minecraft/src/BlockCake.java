package net.minecraft.src;

import java.util.Random;

public class BlockCake extends Block {
	private Icon cakeTopIcon;
	private Icon cakeBottomIcon;
	private Icon field_94382_c;

	protected BlockCake(int var1) {
		super(var1, Material.cake);
		this.setTickRandomly(true);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		float var6 = 1.0F / 16.0F;
		float var7 = (float)(1 + var5 * 2) / 16.0F;
		float var8 = 0.5F;
		this.setBlockBounds(var7, 0.0F, var6, 1.0F - var6, var8, 1.0F - var6);
	}

	public void setBlockBoundsForItemRender() {
		float var1 = 1.0F / 16.0F;
		float var2 = 0.5F;
		this.setBlockBounds(var1, 0.0F, var1, 1.0F - var1, var2, 1.0F - var1);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		float var6 = 1.0F / 16.0F;
		float var7 = (float)(1 + var5 * 2) / 16.0F;
		float var8 = 0.5F;
		return AxisAlignedBB.getAABBPool().getAABB((double)((float)var2 + var7), (double)var3, (double)((float)var4 + var6), (double)((float)(var2 + 1) - var6), (double)((float)var3 + var8 - var6), (double)((float)(var4 + 1) - var6));
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		float var6 = 1.0F / 16.0F;
		float var7 = (float)(1 + var5 * 2) / 16.0F;
		float var8 = 0.5F;
		return AxisAlignedBB.getAABBPool().getAABB((double)((float)var2 + var7), (double)var3, (double)((float)var4 + var6), (double)((float)(var2 + 1) - var6), (double)((float)var3 + var8), (double)((float)(var4 + 1) - var6));
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.cakeTopIcon : (var1 == 0 ? this.cakeBottomIcon : (var2 > 0 && var1 == 4 ? this.field_94382_c : this.blockIcon));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("cake_side");
		this.field_94382_c = var1.registerIcon("cake_inner");
		this.cakeTopIcon = var1.registerIcon("cake_top");
		this.cakeBottomIcon = var1.registerIcon("cake_bottom");
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		this.eatCakeSlice(var1, var2, var3, var4, var5);
		return true;
	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		this.eatCakeSlice(var1, var2, var3, var4, var5);
	}

	private void eatCakeSlice(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		if(var5.canEat(false)) {
			var5.getFoodStats().addStats(2, 0.1F);
			int var6 = var1.getBlockMetadata(var2, var3, var4) + 1;
			if(var6 >= 6) {
				var1.setBlockToAir(var2, var3, var4);
			} else {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var6, 2);
			}
		}

	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return !super.canPlaceBlockAt(var1, var2, var3, var4) ? false : this.canBlockStay(var1, var2, var3, var4);
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!this.canBlockStay(var1, var2, var3, var4)) {
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public boolean canBlockStay(World var1, int var2, int var3, int var4) {
		return var1.getBlockMaterial(var2, var3 - 1, var4).isSolid();
	}

	public int quantityDropped(Random var1) {
		return 0;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return 0;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.cake.itemID;
	}
}
