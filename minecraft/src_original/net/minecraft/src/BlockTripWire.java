package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BlockTripWire extends Block {
	public BlockTripWire(int var1) {
		super(var1, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
		this.setTickRandomly(true);
	}

	public int tickRate(World var1) {
		return 10;
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

	public int getRenderBlockPass() {
		return 1;
	}

	public int getRenderType() {
		return 30;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.silk.itemID;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.silk.itemID;
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		boolean var7 = (var6 & 2) == 2;
		boolean var8 = !var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4);
		if(var7 != var8) {
			this.dropBlockAsItem(var1, var2, var3, var4, var6, 0);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		boolean var6 = (var5 & 4) == 4;
		boolean var7 = (var5 & 2) == 2;
		if(!var7) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
		} else if(!var6) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 1.0F / 16.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
		}

	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		int var5 = var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) ? 0 : 2;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var5, 3);
		this.func_72149_e(var1, var2, var3, var4, var5);
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		this.func_72149_e(var1, var2, var3, var4, var6 | 1);
	}

	public void onBlockHarvested(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
		if(!var1.isRemote) {
			if(var6.getCurrentEquippedItem() != null && var6.getCurrentEquippedItem().itemID == Item.shears.itemID) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var5 | 8, 4);
			}

		}
	}

	private void func_72149_e(World var1, int var2, int var3, int var4, int var5) {
		for(int var6 = 0; var6 < 2; ++var6) {
			for(int var7 = 1; var7 < 42; ++var7) {
				int var8 = var2 + Direction.offsetX[var6] * var7;
				int var9 = var4 + Direction.offsetZ[var6] * var7;
				int var10 = var1.getBlockId(var8, var3, var9);
				if(var10 == Block.tripWireSource.blockID) {
					int var11 = var1.getBlockMetadata(var8, var3, var9) & 3;
					if(var11 == Direction.rotateOpposite[var6]) {
						Block.tripWireSource.func_72143_a(var1, var8, var3, var9, var10, var1.getBlockMetadata(var8, var3, var9), true, var7, var5);
					}
					break;
				}

				if(var10 != Block.tripWire.blockID) {
					break;
				}
			}
		}

	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if(!var1.isRemote) {
			if((var1.getBlockMetadata(var2, var3, var4) & 1) != 1) {
				this.updateTripWireState(var1, var2, var3, var4);
			}
		}
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			if((var1.getBlockMetadata(var2, var3, var4) & 1) == 1) {
				this.updateTripWireState(var1, var2, var3, var4);
			}
		}
	}

	private void updateTripWireState(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		boolean var6 = (var5 & 1) == 1;
		boolean var7 = false;
		List var8 = var1.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ));
		if(!var8.isEmpty()) {
			Iterator var9 = var8.iterator();

			while(var9.hasNext()) {
				Entity var10 = (Entity)var9.next();
				if(!var10.doesEntityNotTriggerPressurePlate()) {
					var7 = true;
					break;
				}
			}
		}

		if(var7 && !var6) {
			var5 |= 1;
		}

		if(!var7 && var6) {
			var5 &= -2;
		}

		if(var7 != var6) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var5, 3);
			this.func_72149_e(var1, var2, var3, var4, var5);
		}

		if(var7) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
		}

	}

	public static boolean func_72148_a(IBlockAccess var0, int var1, int var2, int var3, int var4, int var5) {
		int var6 = var1 + Direction.offsetX[var5];
		int var8 = var3 + Direction.offsetZ[var5];
		int var9 = var0.getBlockId(var6, var2, var8);
		boolean var10 = (var4 & 2) == 2;
		int var11;
		if(var9 == Block.tripWireSource.blockID) {
			var11 = var0.getBlockMetadata(var6, var2, var8);
			int var13 = var11 & 3;
			return var13 == Direction.rotateOpposite[var5];
		} else if(var9 == Block.tripWire.blockID) {
			var11 = var0.getBlockMetadata(var6, var2, var8);
			boolean var12 = (var11 & 2) == 2;
			return var10 == var12;
		} else {
			return false;
		}
	}
}
