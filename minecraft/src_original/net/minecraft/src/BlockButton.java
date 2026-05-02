package net.minecraft.src;

import java.util.List;
import java.util.Random;

public abstract class BlockButton extends Block {
	private final boolean sensible;

	protected BlockButton(int var1, boolean var2) {
		super(var1, Material.circuits);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.sensible = var2;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return null;
	}

	public int tickRate(World var1) {
		return this.sensible ? 30 : 20;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean canPlaceBlockOnSide(World var1, int var2, int var3, int var4, int var5) {
		return var5 == 2 && var1.isBlockNormalCube(var2, var3, var4 + 1) ? true : (var5 == 3 && var1.isBlockNormalCube(var2, var3, var4 - 1) ? true : (var5 == 4 && var1.isBlockNormalCube(var2 + 1, var3, var4) ? true : var5 == 5 && var1.isBlockNormalCube(var2 - 1, var3, var4)));
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var1.isBlockNormalCube(var2 - 1, var3, var4) ? true : (var1.isBlockNormalCube(var2 + 1, var3, var4) ? true : (var1.isBlockNormalCube(var2, var3, var4 - 1) ? true : var1.isBlockNormalCube(var2, var3, var4 + 1)));
	}

	public int onBlockPlaced(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
		int var10 = var1.getBlockMetadata(var2, var3, var4);
		int var11 = var10 & 8;
		var10 &= 7;
		if(var5 == 2 && var1.isBlockNormalCube(var2, var3, var4 + 1)) {
			var10 = 4;
		} else if(var5 == 3 && var1.isBlockNormalCube(var2, var3, var4 - 1)) {
			var10 = 3;
		} else if(var5 == 4 && var1.isBlockNormalCube(var2 + 1, var3, var4)) {
			var10 = 2;
		} else if(var5 == 5 && var1.isBlockNormalCube(var2 - 1, var3, var4)) {
			var10 = 1;
		} else {
			var10 = this.getOrientation(var1, var2, var3, var4);
		}

		return var10 + var11;
	}

	private int getOrientation(World var1, int var2, int var3, int var4) {
		return var1.isBlockNormalCube(var2 - 1, var3, var4) ? 1 : (var1.isBlockNormalCube(var2 + 1, var3, var4) ? 2 : (var1.isBlockNormalCube(var2, var3, var4 - 1) ? 3 : (var1.isBlockNormalCube(var2, var3, var4 + 1) ? 4 : 1)));
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(this.redundantCanPlaceBlockAt(var1, var2, var3, var4)) {
			int var6 = var1.getBlockMetadata(var2, var3, var4) & 7;
			boolean var7 = false;
			if(!var1.isBlockNormalCube(var2 - 1, var3, var4) && var6 == 1) {
				var7 = true;
			}

			if(!var1.isBlockNormalCube(var2 + 1, var3, var4) && var6 == 2) {
				var7 = true;
			}

			if(!var1.isBlockNormalCube(var2, var3, var4 - 1) && var6 == 3) {
				var7 = true;
			}

			if(!var1.isBlockNormalCube(var2, var3, var4 + 1) && var6 == 4) {
				var7 = true;
			}

			if(var7) {
				this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
				var1.setBlockToAir(var2, var3, var4);
			}
		}

	}

	private boolean redundantCanPlaceBlockAt(World var1, int var2, int var3, int var4) {
		if(!this.canPlaceBlockAt(var1, var2, var3, var4)) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
			return false;
		} else {
			return true;
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		this.func_82534_e(var5);
	}

	private void func_82534_e(int var1) {
		int var2 = var1 & 7;
		boolean var3 = (var1 & 8) > 0;
		float var4 = 6.0F / 16.0F;
		float var5 = 10.0F / 16.0F;
		float var6 = 3.0F / 16.0F;
		float var7 = 2.0F / 16.0F;
		if(var3) {
			var7 = 1.0F / 16.0F;
		}

		if(var2 == 1) {
			this.setBlockBounds(0.0F, var4, 0.5F - var6, var7, var5, 0.5F + var6);
		} else if(var2 == 2) {
			this.setBlockBounds(1.0F - var7, var4, 0.5F - var6, 1.0F, var5, 0.5F + var6);
		} else if(var2 == 3) {
			this.setBlockBounds(0.5F - var6, var4, 0.0F, 0.5F + var6, var5, var7);
		} else if(var2 == 4) {
			this.setBlockBounds(0.5F - var6, var4, 1.0F - var7, 0.5F + var6, var5, 1.0F);
		}

	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		int var10 = var1.getBlockMetadata(var2, var3, var4);
		int var11 = var10 & 7;
		int var12 = 8 - (var10 & 8);
		if(var12 == 0) {
			return true;
		} else {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var11 + var12, 3);
			var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
			this.func_82536_d(var1, var2, var3, var4, var11);
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
			return true;
		}
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		if((var6 & 8) > 0) {
			int var7 = var6 & 7;
			this.func_82536_d(var1, var2, var3, var4, var7);
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return (var1.getBlockMetadata(var2, var3, var4) & 8) > 0 ? 15 : 0;
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if((var6 & 8) == 0) {
			return 0;
		} else {
			int var7 = var6 & 7;
			return var7 == 5 && var5 == 1 ? 15 : (var7 == 4 && var5 == 2 ? 15 : (var7 == 3 && var5 == 3 ? 15 : (var7 == 2 && var5 == 4 ? 15 : (var7 == 1 && var5 == 5 ? 15 : 0))));
		}
	}

	public boolean canProvidePower() {
		return true;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			if((var6 & 8) != 0) {
				if(this.sensible) {
					this.func_82535_o(var1, var2, var3, var4);
				} else {
					var1.setBlockMetadataWithNotify(var2, var3, var4, var6 & 7, 3);
					int var7 = var6 & 7;
					this.func_82536_d(var1, var2, var3, var4, var7);
					var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
					var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
				}

			}
		}
	}

	public void setBlockBoundsForItemRender() {
		float var1 = 3.0F / 16.0F;
		float var2 = 2.0F / 16.0F;
		float var3 = 2.0F / 16.0F;
		this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if(!var1.isRemote) {
			if(this.sensible) {
				if((var1.getBlockMetadata(var2, var3, var4) & 8) == 0) {
					this.func_82535_o(var1, var2, var3, var4);
				}
			}
		}
	}

	private void func_82535_o(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		int var6 = var5 & 7;
		boolean var7 = (var5 & 8) != 0;
		this.func_82534_e(var5);
		List var9 = var1.getEntitiesWithinAABB(EntityArrow.class, AxisAlignedBB.getAABBPool().getAABB((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ));
		boolean var8 = !var9.isEmpty();
		if(var8 && !var7) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var6 | 8, 3);
			this.func_82536_d(var1, var2, var3, var4, var6);
			var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
		}

		if(!var8 && var7) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var6, 3);
			this.func_82536_d(var1, var2, var3, var4, var6);
			var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
		}

		if(var8) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
		}

	}

	private void func_82536_d(World var1, int var2, int var3, int var4, int var5) {
		var1.notifyBlocksOfNeighborChange(var2, var3, var4, this.blockID);
		if(var5 == 1) {
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
		} else if(var5 == 2) {
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
		} else if(var5 == 3) {
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
		} else if(var5 == 4) {
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
		} else {
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
		}

	}

	public void registerIcons(IconRegister var1) {
	}
}
