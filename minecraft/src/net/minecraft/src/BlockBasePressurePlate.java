package net.minecraft.src;

import java.util.Random;

public abstract class BlockBasePressurePlate extends Block {
	private String pressurePlateIconName;

	protected BlockBasePressurePlate(int var1, String var2, Material var3) {
		super(var1, var3);
		this.pressurePlateIconName = var2;
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setTickRandomly(true);
		this.func_94353_c_(this.getMetaFromWeight(15));
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		this.func_94353_c_(var1.getBlockMetadata(var2, var3, var4));
	}

	protected void func_94353_c_(int var1) {
		boolean var2 = this.getPowerSupply(var1) > 0;
		float var3 = 1.0F / 16.0F;
		if(var2) {
			this.setBlockBounds(var3, 0.0F, var3, 1.0F - var3, 0.03125F, 1.0F - var3);
		} else {
			this.setBlockBounds(var3, 0.0F, var3, 1.0F - var3, 1.0F / 16.0F, 1.0F - var3);
		}

	}

	public int tickRate(World var1) {
		return 20;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess var1, int var2, int var3, int var4) {
		return true;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) || BlockFence.isIdAFence(var1.getBlockId(var2, var3 - 1, var4));
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		boolean var6 = false;
		if(!var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) && !BlockFence.isIdAFence(var1.getBlockId(var2, var3 - 1, var4))) {
			var6 = true;
		}

		if(var6) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			int var6 = this.getPowerSupply(var1.getBlockMetadata(var2, var3, var4));
			if(var6 > 0) {
				this.setStateIfMobInteractsWithPlate(var1, var2, var3, var4, var6);
			}

		}
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if(!var1.isRemote) {
			int var6 = this.getPowerSupply(var1.getBlockMetadata(var2, var3, var4));
			if(var6 == 0) {
				this.setStateIfMobInteractsWithPlate(var1, var2, var3, var4, var6);
			}

		}
	}

	protected void setStateIfMobInteractsWithPlate(World var1, int var2, int var3, int var4, int var5) {
		int var6 = this.getPlateState(var1, var2, var3, var4);
		boolean var7 = var5 > 0;
		boolean var8 = var6 > 0;
		if(var5 != var6) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, this.getMetaFromWeight(var6), 2);
			this.func_94354_b_(var1, var2, var3, var4);
			var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
		}

		if(!var8 && var7) {
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
		} else if(var8 && !var7) {
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
		}

		if(var8) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
		}

	}

	protected AxisAlignedBB getSensitiveAABB(int var1, int var2, int var3) {
		float var4 = 2.0F / 16.0F;
		return AxisAlignedBB.getAABBPool().getAABB((double)((float)var1 + var4), (double)var2, (double)((float)var3 + var4), (double)((float)(var1 + 1) - var4), (double)var2 + 0.25D, (double)((float)(var3 + 1) - var4));
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(this.getPowerSupply(var6) > 0) {
			this.func_94354_b_(var1, var2, var3, var4);
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	protected void func_94354_b_(World var1, int var2, int var3, int var4) {
		var1.notifyBlocksOfNeighborChange(var2, var3, var4, this.blockID);
		var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return this.getPowerSupply(var1.getBlockMetadata(var2, var3, var4));
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 1 ? this.getPowerSupply(var1.getBlockMetadata(var2, var3, var4)) : 0;
	}

	public boolean canProvidePower() {
		return true;
	}

	public void setBlockBoundsForItemRender() {
		float var1 = 0.5F;
		float var2 = 2.0F / 16.0F;
		float var3 = 0.5F;
		this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
	}

	public int getMobilityFlag() {
		return 1;
	}

	protected abstract int getPlateState(World var1, int var2, int var3, int var4);

	protected abstract int getPowerSupply(int var1);

	protected abstract int getMetaFromWeight(int var1);

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon(this.pressurePlateIconName);
	}
}
