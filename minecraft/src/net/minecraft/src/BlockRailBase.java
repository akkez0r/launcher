package net.minecraft.src;

import java.util.Random;

public abstract class BlockRailBase extends Block {
	protected final boolean isPowered;

	public static final boolean isRailBlockAt(World var0, int var1, int var2, int var3) {
		return isRailBlock(var0.getBlockId(var1, var2, var3));
	}

	public static final boolean isRailBlock(int var0) {
		return var0 == Block.rail.blockID || var0 == Block.railPowered.blockID || var0 == Block.railDetector.blockID || var0 == Block.railActivator.blockID;
	}

	protected BlockRailBase(int var1, boolean var2) {
		super(var1, Material.circuits);
		this.isPowered = var2;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F / 16.0F, 1.0F);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}

	public boolean isPowered() {
		return this.isPowered;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public MovingObjectPosition collisionRayTrace(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.collisionRayTrace(var1, var2, var3, var4, var5, var6);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		if(var5 >= 2 && var5 <= 5) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 10.0F / 16.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F / 16.0F, 1.0F);
		}

	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 9;
	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4);
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote) {
			this.refreshTrackShape(var1, var2, var3, var4, true);
			if(this.isPowered) {
				this.onNeighborBlockChange(var1, var2, var3, var4, this.blockID);
			}
		}

	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.isRemote) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			int var7 = var6;
			if(this.isPowered) {
				var7 = var6 & 7;
			}

			boolean var8 = false;
			if(!var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4)) {
				var8 = true;
			}

			if(var7 == 2 && !var1.doesBlockHaveSolidTopSurface(var2 + 1, var3, var4)) {
				var8 = true;
			}

			if(var7 == 3 && !var1.doesBlockHaveSolidTopSurface(var2 - 1, var3, var4)) {
				var8 = true;
			}

			if(var7 == 4 && !var1.doesBlockHaveSolidTopSurface(var2, var3, var4 - 1)) {
				var8 = true;
			}

			if(var7 == 5 && !var1.doesBlockHaveSolidTopSurface(var2, var3, var4 + 1)) {
				var8 = true;
			}

			if(var8) {
				this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
				var1.setBlockToAir(var2, var3, var4);
			} else {
				this.func_94358_a(var1, var2, var3, var4, var6, var7, var5);
			}

		}
	}

	protected void func_94358_a(World var1, int var2, int var3, int var4, int var5, int var6, int var7) {
	}

	protected void refreshTrackShape(World var1, int var2, int var3, int var4, boolean var5) {
		if(!var1.isRemote) {
			(new BlockBaseRailLogic(this, var1, var2, var3, var4)).func_94511_a(var1.isBlockIndirectlyGettingPowered(var2, var3, var4), var5);
		}
	}

	public int getMobilityFlag() {
		return 0;
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = var6;
		if(this.isPowered) {
			var7 = var6 & 7;
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
		if(var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5) {
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, var5);
		}

		if(this.isPowered) {
			var1.notifyBlocksOfNeighborChange(var2, var3, var4, var5);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, var5);
		}

	}
}
