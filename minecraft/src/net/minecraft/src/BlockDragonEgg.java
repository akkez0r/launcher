package net.minecraft.src;

import java.util.Random;

public class BlockDragonEgg extends Block {
	public BlockDragonEgg(int var1) {
		super(var1, Material.dragonEgg);
		this.setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 1.0F, 15.0F / 16.0F);
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		this.fallIfPossible(var1, var2, var3, var4);
	}

	private void fallIfPossible(World var1, int var2, int var3, int var4) {
		if(BlockSand.canFallBelow(var1, var2, var3 - 1, var4) && var3 >= 0) {
			byte var5 = 32;
			if(!BlockSand.fallInstantly && var1.checkChunksExist(var2 - var5, var3 - var5, var4 - var5, var2 + var5, var3 + var5, var4 + var5)) {
				EntityFallingSand var6 = new EntityFallingSand(var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), this.blockID);
				var1.spawnEntityInWorld(var6);
			} else {
				var1.setBlockToAir(var2, var3, var4);

				while(BlockSand.canFallBelow(var1, var2, var3 - 1, var4) && var3 > 0) {
					--var3;
				}

				if(var3 > 0) {
					var1.setBlock(var2, var3, var4, this.blockID, 0, 2);
				}
			}
		}

	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		this.teleportNearby(var1, var2, var3, var4);
		return true;
	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		this.teleportNearby(var1, var2, var3, var4);
	}

	private void teleportNearby(World var1, int var2, int var3, int var4) {
		if(var1.getBlockId(var2, var3, var4) == this.blockID) {
			for(int var5 = 0; var5 < 1000; ++var5) {
				int var6 = var2 + var1.rand.nextInt(16) - var1.rand.nextInt(16);
				int var7 = var3 + var1.rand.nextInt(8) - var1.rand.nextInt(8);
				int var8 = var4 + var1.rand.nextInt(16) - var1.rand.nextInt(16);
				if(var1.getBlockId(var6, var7, var8) == 0) {
					if(!var1.isRemote) {
						var1.setBlock(var6, var7, var8, this.blockID, var1.getBlockMetadata(var2, var3, var4), 2);
						var1.setBlockToAir(var2, var3, var4);
					} else {
						short var9 = 128;

						for(int var10 = 0; var10 < var9; ++var10) {
							double var11 = var1.rand.nextDouble();
							float var13 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
							float var14 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
							float var15 = (var1.rand.nextFloat() - 0.5F) * 0.2F;
							double var16 = (double)var6 + (double)(var2 - var6) * var11 + (var1.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
							double var18 = (double)var7 + (double)(var3 - var7) * var11 + var1.rand.nextDouble() * 1.0D - 0.5D;
							double var20 = (double)var8 + (double)(var4 - var8) * var11 + (var1.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
							var1.spawnParticle("portal", var16, var18, var20, (double)var13, (double)var14, (double)var15);
						}
					}

					return;
				}
			}

		}
	}

	public int tickRate(World var1) {
		return 5;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return true;
	}

	public int getRenderType() {
		return 27;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return 0;
	}
}
