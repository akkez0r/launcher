package net.minecraft.src;

import java.util.Random;

public class BlockCactus extends Block {
	private Icon cactusTopIcon;
	private Icon cactusBottomIcon;

	protected BlockCactus(int var1) {
		super(var1, Material.cactus);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(var1.isAirBlock(var2, var3 + 1, var4)) {
			int var6;
			for(var6 = 1; var1.getBlockId(var2, var3 - var6, var4) == this.blockID; ++var6) {
			}

			if(var6 < 3) {
				int var7 = var1.getBlockMetadata(var2, var3, var4);
				if(var7 == 15) {
					var1.setBlock(var2, var3 + 1, var4, this.blockID);
					var1.setBlockMetadataWithNotify(var2, var3, var4, 0, 4);
					this.onNeighborBlockChange(var1, var2, var3 + 1, var4, this.blockID);
				} else {
					var1.setBlockMetadataWithNotify(var2, var3, var4, var7 + 1, 4);
				}
			}
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		float var5 = 1.0F / 16.0F;
		return AxisAlignedBB.getAABBPool().getAABB((double)((float)var2 + var5), (double)var3, (double)((float)var4 + var5), (double)((float)(var2 + 1) - var5), (double)((float)(var3 + 1) - var5), (double)((float)(var4 + 1) - var5));
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		float var5 = 1.0F / 16.0F;
		return AxisAlignedBB.getAABBPool().getAABB((double)((float)var2 + var5), (double)var3, (double)((float)var4 + var5), (double)((float)(var2 + 1) - var5), (double)(var3 + 1), (double)((float)(var4 + 1) - var5));
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.cactusTopIcon : (var1 == 0 ? this.cactusBottomIcon : this.blockIcon);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 13;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return !super.canPlaceBlockAt(var1, var2, var3, var4) ? false : this.canBlockStay(var1, var2, var3, var4);
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!this.canBlockStay(var1, var2, var3, var4)) {
			var1.destroyBlock(var2, var3, var4, true);
		}

	}

	public boolean canBlockStay(World var1, int var2, int var3, int var4) {
		if(var1.getBlockMaterial(var2 - 1, var3, var4).isSolid()) {
			return false;
		} else if(var1.getBlockMaterial(var2 + 1, var3, var4).isSolid()) {
			return false;
		} else if(var1.getBlockMaterial(var2, var3, var4 - 1).isSolid()) {
			return false;
		} else if(var1.getBlockMaterial(var2, var3, var4 + 1).isSolid()) {
			return false;
		} else {
			int var5 = var1.getBlockId(var2, var3 - 1, var4);
			return var5 == Block.cactus.blockID || var5 == Block.sand.blockID;
		}
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		var5.attackEntityFrom(DamageSource.cactus, 1);
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("cactus_side");
		this.cactusTopIcon = var1.registerIcon("cactus_top");
		this.cactusBottomIcon = var1.registerIcon("cactus_bottom");
	}
}
