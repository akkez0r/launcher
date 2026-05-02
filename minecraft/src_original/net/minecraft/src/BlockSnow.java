package net.minecraft.src;

import java.util.Random;

public class BlockSnow extends Block {
	protected BlockSnow(int var1) {
		super(var1, Material.snow);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F / 16.0F, 1.0F);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockBoundsForSnowDepth(0);
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("snow");
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4) & 7;
		float var6 = 2.0F / 16.0F;
		return AxisAlignedBB.getAABBPool().getAABB((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)((float)var3 + (float)var5 * var6), (double)var4 + this.maxZ);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBoundsForSnowDepth(0);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		this.setBlockBoundsForSnowDepth(var1.getBlockMetadata(var2, var3, var4));
	}

	protected void setBlockBoundsForSnowDepth(int var1) {
		int var2 = var1 & 7;
		float var3 = (float)(2 * (1 + var2)) / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3 - 1, var4);
		return var5 == 0 ? false : (var5 == this.blockID && (var1.getBlockMetadata(var2, var3 - 1, var4) & 7) == 7 ? true : (var5 != Block.leaves.blockID && !Block.blocksList[var5].isOpaqueCube() ? false : var1.getBlockMaterial(var2, var3 - 1, var4).blocksMovement()));
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		this.canSnowStay(var1, var2, var3, var4);
	}

	private boolean canSnowStay(World var1, int var2, int var3, int var4) {
		if(!this.canPlaceBlockAt(var1, var2, var3, var4)) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
			return false;
		} else {
			return true;
		}
	}

	public void harvestBlock(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
		int var7 = Item.snowball.itemID;
		int var8 = var6 & 7;
		this.dropBlockAsItem_do(var1, var3, var4, var5, new ItemStack(var7, var8 + 1, 0));
		var1.setBlockToAir(var3, var4, var5);
		var2.addStat(StatList.mineBlockStatArray[this.blockID], 1);
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.snowball.itemID;
	}

	public int quantityDropped(Random var1) {
		return 0;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(var1.getSavedLightValue(EnumSkyBlock.Block, var2, var3, var4) > 11) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 1 ? true : super.shouldSideBeRendered(var1, var2, var3, var4, var5);
	}
}
