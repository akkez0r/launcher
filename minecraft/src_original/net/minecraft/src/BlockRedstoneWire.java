package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire extends Block {
	private boolean wiresProvidePower = true;
	private Set blocksNeedingUpdate = new HashSet();
	private Icon field_94413_c;
	private Icon field_94410_cO;
	private Icon field_94411_cP;
	private Icon field_94412_cQ;

	public BlockRedstoneWire(int var1) {
		super(var1, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F / 16.0F, 1.0F);
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

	public int getRenderType() {
		return 5;
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		return 8388608;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) || var1.getBlockId(var2, var3 - 1, var4) == Block.glowStone.blockID;
	}

	private void updateAndPropagateCurrentStrength(World var1, int var2, int var3, int var4) {
		this.calculateCurrentChanges(var1, var2, var3, var4, var2, var3, var4);
		ArrayList var5 = new ArrayList(this.blocksNeedingUpdate);
		this.blocksNeedingUpdate.clear();

		for(int var6 = 0; var6 < var5.size(); ++var6) {
			ChunkPosition var7 = (ChunkPosition)var5.get(var6);
			var1.notifyBlocksOfNeighborChange(var7.x, var7.y, var7.z, this.blockID);
		}

	}

	private void calculateCurrentChanges(World var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		int var8 = var1.getBlockMetadata(var2, var3, var4);
		byte var9 = 0;
		int var15 = this.getMaxCurrentStrength(var1, var5, var6, var7, var9);
		this.wiresProvidePower = false;
		int var10 = var1.getStrongestIndirectPower(var2, var3, var4);
		this.wiresProvidePower = true;
		if(var10 > 0 && var10 > var15 - 1) {
			var15 = var10;
		}

		int var11 = 0;

		for(int var12 = 0; var12 < 4; ++var12) {
			int var13 = var2;
			int var14 = var4;
			if(var12 == 0) {
				var13 = var2 - 1;
			}

			if(var12 == 1) {
				++var13;
			}

			if(var12 == 2) {
				var14 = var4 - 1;
			}

			if(var12 == 3) {
				++var14;
			}

			if(var13 != var5 || var14 != var7) {
				var11 = this.getMaxCurrentStrength(var1, var13, var3, var14, var11);
			}

			if(var1.isBlockNormalCube(var13, var3, var14) && !var1.isBlockNormalCube(var2, var3 + 1, var4)) {
				if((var13 != var5 || var14 != var7) && var3 >= var6) {
					var11 = this.getMaxCurrentStrength(var1, var13, var3 + 1, var14, var11);
				}
			} else if(!var1.isBlockNormalCube(var13, var3, var14) && (var13 != var5 || var14 != var7) && var3 <= var6) {
				var11 = this.getMaxCurrentStrength(var1, var13, var3 - 1, var14, var11);
			}
		}

		if(var11 > var15) {
			var15 = var11 - 1;
		} else if(var15 > 0) {
			--var15;
		} else {
			var15 = 0;
		}

		if(var10 > var15 - 1) {
			var15 = var10;
		}

		if(var8 != var15) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var15, 2);
			this.blocksNeedingUpdate.add(new ChunkPosition(var2, var3, var4));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2 - 1, var3, var4));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2 + 1, var3, var4));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2, var3 - 1, var4));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2, var3 + 1, var4));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2, var3, var4 - 1));
			this.blocksNeedingUpdate.add(new ChunkPosition(var2, var3, var4 + 1));
		}

	}

	private void notifyWireNeighborsOfNeighborChange(World var1, int var2, int var3, int var4) {
		if(var1.getBlockId(var2, var3, var4) == this.blockID) {
			var1.notifyBlocksOfNeighborChange(var2, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
		}
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		if(!var1.isRemote) {
			this.updateAndPropagateCurrentStrength(var1, var2, var3, var4);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3, var4);
			this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3, var4);
			this.notifyWireNeighborsOfNeighborChange(var1, var2, var3, var4 - 1);
			this.notifyWireNeighborsOfNeighborChange(var1, var2, var3, var4 + 1);
			if(var1.isBlockNormalCube(var2 - 1, var3, var4)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3 + 1, var4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3 - 1, var4);
			}

			if(var1.isBlockNormalCube(var2 + 1, var3, var4)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3 + 1, var4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3 - 1, var4);
			}

			if(var1.isBlockNormalCube(var2, var3, var4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 + 1, var4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 - 1, var4 - 1);
			}

			if(var1.isBlockNormalCube(var2, var3, var4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 + 1, var4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 - 1, var4 + 1);
			}

		}
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		super.breakBlock(var1, var2, var3, var4, var5, var6);
		if(!var1.isRemote) {
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			this.updateAndPropagateCurrentStrength(var1, var2, var3, var4);
			this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3, var4);
			this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3, var4);
			this.notifyWireNeighborsOfNeighborChange(var1, var2, var3, var4 - 1);
			this.notifyWireNeighborsOfNeighborChange(var1, var2, var3, var4 + 1);
			if(var1.isBlockNormalCube(var2 - 1, var3, var4)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3 + 1, var4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 - 1, var3 - 1, var4);
			}

			if(var1.isBlockNormalCube(var2 + 1, var3, var4)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3 + 1, var4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2 + 1, var3 - 1, var4);
			}

			if(var1.isBlockNormalCube(var2, var3, var4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 + 1, var4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 - 1, var4 - 1);
			}

			if(var1.isBlockNormalCube(var2, var3, var4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 + 1, var4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(var1, var2, var3 - 1, var4 + 1);
			}

		}
	}

	private int getMaxCurrentStrength(World var1, int var2, int var3, int var4, int var5) {
		if(var1.getBlockId(var2, var3, var4) != this.blockID) {
			return var5;
		} else {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			return var6 > var5 ? var6 : var5;
		}
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.isRemote) {
			boolean var6 = this.canPlaceBlockAt(var1, var2, var3, var4);
			if(var6) {
				this.updateAndPropagateCurrentStrength(var1, var2, var3, var4);
			} else {
				this.dropBlockAsItem(var1, var2, var3, var4, 0, 0);
				var1.setBlockToAir(var2, var3, var4);
			}

			super.onNeighborBlockChange(var1, var2, var3, var4, var5);
		}
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.redstone.itemID;
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return !this.wiresProvidePower ? 0 : this.isProvidingWeakPower(var1, var2, var3, var4, var5);
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(!this.wiresProvidePower) {
			return 0;
		} else {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			if(var6 == 0) {
				return 0;
			} else if(var5 == 1) {
				return var6;
			} else {
				boolean var7 = isPoweredOrRepeater(var1, var2 - 1, var3, var4, 1) || !var1.isBlockNormalCube(var2 - 1, var3, var4) && isPoweredOrRepeater(var1, var2 - 1, var3 - 1, var4, -1);
				boolean var8 = isPoweredOrRepeater(var1, var2 + 1, var3, var4, 3) || !var1.isBlockNormalCube(var2 + 1, var3, var4) && isPoweredOrRepeater(var1, var2 + 1, var3 - 1, var4, -1);
				boolean var9 = isPoweredOrRepeater(var1, var2, var3, var4 - 1, 2) || !var1.isBlockNormalCube(var2, var3, var4 - 1) && isPoweredOrRepeater(var1, var2, var3 - 1, var4 - 1, -1);
				boolean var10 = isPoweredOrRepeater(var1, var2, var3, var4 + 1, 0) || !var1.isBlockNormalCube(var2, var3, var4 + 1) && isPoweredOrRepeater(var1, var2, var3 - 1, var4 + 1, -1);
				if(!var1.isBlockNormalCube(var2, var3 + 1, var4)) {
					if(var1.isBlockNormalCube(var2 - 1, var3, var4) && isPoweredOrRepeater(var1, var2 - 1, var3 + 1, var4, -1)) {
						var7 = true;
					}

					if(var1.isBlockNormalCube(var2 + 1, var3, var4) && isPoweredOrRepeater(var1, var2 + 1, var3 + 1, var4, -1)) {
						var8 = true;
					}

					if(var1.isBlockNormalCube(var2, var3, var4 - 1) && isPoweredOrRepeater(var1, var2, var3 + 1, var4 - 1, -1)) {
						var9 = true;
					}

					if(var1.isBlockNormalCube(var2, var3, var4 + 1) && isPoweredOrRepeater(var1, var2, var3 + 1, var4 + 1, -1)) {
						var10 = true;
					}
				}

				return !var9 && !var8 && !var7 && !var10 && var5 >= 2 && var5 <= 5 ? var6 : (var5 == 2 && var9 && !var7 && !var8 ? var6 : (var5 == 3 && var10 && !var7 && !var8 ? var6 : (var5 == 4 && var7 && !var9 && !var10 ? var6 : (var5 == 5 && var8 && !var9 && !var10 ? var6 : 0))));
			}
		}
	}

	public boolean canProvidePower() {
		return this.wiresProvidePower;
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if(var6 > 0) {
			double var7 = (double)var2 + 0.5D + ((double)var5.nextFloat() - 0.5D) * 0.2D;
			double var9 = (double)((float)var3 + 1.0F / 16.0F);
			double var11 = (double)var4 + 0.5D + ((double)var5.nextFloat() - 0.5D) * 0.2D;
			float var13 = (float)var6 / 15.0F;
			float var14 = var13 * 0.6F + 0.4F;
			if(var6 == 0) {
				var14 = 0.0F;
			}

			float var15 = var13 * var13 * 0.7F - 0.5F;
			float var16 = var13 * var13 * 0.6F - 0.7F;
			if(var15 < 0.0F) {
				var15 = 0.0F;
			}

			if(var16 < 0.0F) {
				var16 = 0.0F;
			}

			var1.spawnParticle("reddust", var7, var9, var11, (double)var14, (double)var15, (double)var16);
		}

	}

	public static boolean isPowerProviderOrWire(IBlockAccess var0, int var1, int var2, int var3, int var4) {
		int var5 = var0.getBlockId(var1, var2, var3);
		if(var5 == Block.redstoneWire.blockID) {
			return true;
		} else if(var5 == 0) {
			return false;
		} else if(!Block.redstoneRepeaterIdle.func_94487_f(var5)) {
			return Block.blocksList[var5].canProvidePower() && var4 != -1;
		} else {
			int var6 = var0.getBlockMetadata(var1, var2, var3);
			return var4 == (var6 & 3) || var4 == Direction.rotateOpposite[var6 & 3];
		}
	}

	public static boolean isPoweredOrRepeater(IBlockAccess var0, int var1, int var2, int var3, int var4) {
		if(isPowerProviderOrWire(var0, var1, var2, var3, var4)) {
			return true;
		} else {
			int var5 = var0.getBlockId(var1, var2, var3);
			if(var5 == Block.redstoneRepeaterActive.blockID) {
				int var6 = var0.getBlockMetadata(var1, var2, var3);
				return var4 == (var6 & 3);
			} else {
				return false;
			}
		}
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.redstone.itemID;
	}

	public void registerIcons(IconRegister var1) {
		this.field_94413_c = var1.registerIcon("redstoneDust_cross");
		this.field_94410_cO = var1.registerIcon("redstoneDust_line");
		this.field_94411_cP = var1.registerIcon("redstoneDust_cross_overlay");
		this.field_94412_cQ = var1.registerIcon("redstoneDust_line_overlay");
		this.blockIcon = this.field_94413_c;
	}

	public static Icon func_94409_b(String var0) {
		return var0 == "redstoneDust_cross" ? Block.redstoneWire.field_94413_c : (var0 == "redstoneDust_line" ? Block.redstoneWire.field_94410_cO : (var0 == "redstoneDust_cross_overlay" ? Block.redstoneWire.field_94411_cP : (var0 == "redstoneDust_line_overlay" ? Block.redstoneWire.field_94412_cQ : null)));
	}
}
