package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockRedstoneTorch extends BlockTorch {
	private boolean torchActive = false;
	private static Map redstoneUpdateInfoCache = new HashMap();

	private boolean checkForBurnout(World var1, int var2, int var3, int var4, boolean var5) {
		if(!redstoneUpdateInfoCache.containsKey(var1)) {
			redstoneUpdateInfoCache.put(var1, new ArrayList());
		}

		List var6 = (List)redstoneUpdateInfoCache.get(var1);
		if(var5) {
			var6.add(new RedstoneUpdateInfo(var2, var3, var4, var1.getTotalWorldTime()));
		}

		int var7 = 0;

		for(int var8 = 0; var8 < var6.size(); ++var8) {
			RedstoneUpdateInfo var9 = (RedstoneUpdateInfo)var6.get(var8);
			if(var9.x == var2 && var9.y == var3 && var9.z == var4) {
				++var7;
				if(var7 >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	protected BlockRedstoneTorch(int var1, boolean var2) {
		super(var1);
		this.torchActive = var2;
		this.setTickRandomly(true);
		this.setCreativeTab((CreativeTabs)null);
	}

	public int tickRate(World var1) {
		return 2;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		if(var1.getBlockMetadata(var2, var3, var4) == 0) {
			super.onBlockAdded(var1, var2, var3, var4);
		}

		if(this.torchActive) {
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
		}

	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(this.torchActive) {
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
		}

	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(!this.torchActive) {
			return 0;
		} else {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			return var6 == 5 && var5 == 1 ? 0 : (var6 == 3 && var5 == 3 ? 0 : (var6 == 4 && var5 == 2 ? 0 : (var6 == 1 && var5 == 5 ? 0 : (var6 == 2 && var5 == 4 ? 0 : 15))));
		}
	}

	private boolean isIndirectlyPowered(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		return var5 == 5 && var1.getIndirectPowerOutput(var2, var3 - 1, var4, 0) ? true : (var5 == 3 && var1.getIndirectPowerOutput(var2, var3, var4 - 1, 2) ? true : (var5 == 4 && var1.getIndirectPowerOutput(var2, var3, var4 + 1, 3) ? true : (var5 == 1 && var1.getIndirectPowerOutput(var2 - 1, var3, var4, 4) ? true : var5 == 2 && var1.getIndirectPowerOutput(var2 + 1, var3, var4, 5))));
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		boolean var6 = this.isIndirectlyPowered(var1, var2, var3, var4);
		List var7 = (List)redstoneUpdateInfoCache.get(var1);

		while(var7 != null && !var7.isEmpty() && var1.getTotalWorldTime() - ((RedstoneUpdateInfo)var7.get(0)).updateTime > 60L) {
			var7.remove(0);
		}

		if(this.torchActive) {
			if(var6) {
				var1.setBlock(var2, var3, var4, Block.torchRedstoneIdle.blockID, var1.getBlockMetadata(var2, var3, var4), 3);
				if(this.checkForBurnout(var1, var2, var3, var4, true)) {
					var1.playSoundEffect((double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), "random.fizz", 0.5F, 2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F);

					for(int var8 = 0; var8 < 5; ++var8) {
						double var9 = (double)var2 + var5.nextDouble() * 0.6D + 0.2D;
						double var11 = (double)var3 + var5.nextDouble() * 0.6D + 0.2D;
						double var13 = (double)var4 + var5.nextDouble() * 0.6D + 0.2D;
						var1.spawnParticle("smoke", var9, var11, var13, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		} else if(!var6 && !this.checkForBurnout(var1, var2, var3, var4, false)) {
			var1.setBlock(var2, var3, var4, Block.torchRedstoneActive.blockID, var1.getBlockMetadata(var2, var3, var4), 3);
		}

	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!this.func_94397_d(var1, var2, var3, var4, var5)) {
			boolean var6 = this.isIndirectlyPowered(var1, var2, var3, var4);
			if(this.torchActive && var6 || !this.torchActive && !var6) {
				var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
			}

		}
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 0 ? this.isProvidingWeakPower(var1, var2, var3, var4, var5) : 0;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.torchRedstoneActive.blockID;
	}

	public boolean canProvidePower() {
		return true;
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(this.torchActive) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			double var7 = (double)((float)var2 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2D;
			double var9 = (double)((float)var3 + 0.7F) + (double)(var5.nextFloat() - 0.5F) * 0.2D;
			double var11 = (double)((float)var4 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2D;
			double var13 = (double)0.22F;
			double var15 = (double)0.27F;
			if(var6 == 1) {
				var1.spawnParticle("reddust", var7 - var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
			} else if(var6 == 2) {
				var1.spawnParticle("reddust", var7 + var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
			} else if(var6 == 3) {
				var1.spawnParticle("reddust", var7, var9 + var13, var11 - var15, 0.0D, 0.0D, 0.0D);
			} else if(var6 == 4) {
				var1.spawnParticle("reddust", var7, var9 + var13, var11 + var15, 0.0D, 0.0D, 0.0D);
			} else {
				var1.spawnParticle("reddust", var7, var9, var11, 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Block.torchRedstoneActive.blockID;
	}

	public boolean isAssociatedBlockID(int var1) {
		return var1 == Block.torchRedstoneIdle.blockID || var1 == Block.torchRedstoneActive.blockID;
	}

	public void registerIcons(IconRegister var1) {
		if(this.torchActive) {
			this.blockIcon = var1.registerIcon("redtorch_lit");
		} else {
			this.blockIcon = var1.registerIcon("redtorch");
		}

	}
}
