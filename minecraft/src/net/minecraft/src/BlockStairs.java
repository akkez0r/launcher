package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockStairs extends Block {
	private static final int[][] field_72159_a = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
	private final Block modelBlock;
	private final int modelBlockMetadata;
	private boolean field_72156_cr = false;
	private int field_72160_cs = 0;

	protected BlockStairs(int var1, Block var2, int var3) {
		super(var1, var2.blockMaterial);
		this.modelBlock = var2;
		this.modelBlockMetadata = var3;
		this.setHardness(var2.blockHardness);
		this.setResistance(var2.blockResistance / 3.0F);
		this.setStepSound(var2.stepSound);
		this.setLightOpacity(255);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		if(this.field_72156_cr) {
			this.setBlockBounds(0.5F * (float)(this.field_72160_cs % 2), 0.5F * (float)(this.field_72160_cs / 2 % 2), 0.5F * (float)(this.field_72160_cs / 4 % 2), 0.5F + 0.5F * (float)(this.field_72160_cs % 2), 0.5F + 0.5F * (float)(this.field_72160_cs / 2 % 2), 0.5F + 0.5F * (float)(this.field_72160_cs / 4 % 2));
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 10;
	}

	public void func_82541_d(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		if((var5 & 4) != 0) {
			this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	public static boolean isBlockStairsID(int var0) {
		return var0 > 0 && Block.blocksList[var0] instanceof BlockStairs;
	}

	private boolean func_82540_f(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockId(var2, var3, var4);
		return isBlockStairsID(var6) && var1.getBlockMetadata(var2, var3, var4) == var5;
	}

	public boolean func_82542_g(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		int var6 = var5 & 3;
		float var7 = 0.5F;
		float var8 = 1.0F;
		if((var5 & 4) != 0) {
			var7 = 0.0F;
			var8 = 0.5F;
		}

		float var9 = 0.0F;
		float var10 = 1.0F;
		float var11 = 0.0F;
		float var12 = 0.5F;
		boolean var13 = true;
		int var14;
		int var15;
		int var16;
		if(var6 == 0) {
			var9 = 0.5F;
			var12 = 1.0F;
			var14 = var1.getBlockId(var2 + 1, var3, var4);
			var15 = var1.getBlockMetadata(var2 + 1, var3, var4);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 3 && !this.func_82540_f(var1, var2, var3, var4 + 1, var5)) {
					var12 = 0.5F;
					var13 = false;
				} else if(var16 == 2 && !this.func_82540_f(var1, var2, var3, var4 - 1, var5)) {
					var11 = 0.5F;
					var13 = false;
				}
			}
		} else if(var6 == 1) {
			var10 = 0.5F;
			var12 = 1.0F;
			var14 = var1.getBlockId(var2 - 1, var3, var4);
			var15 = var1.getBlockMetadata(var2 - 1, var3, var4);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 3 && !this.func_82540_f(var1, var2, var3, var4 + 1, var5)) {
					var12 = 0.5F;
					var13 = false;
				} else if(var16 == 2 && !this.func_82540_f(var1, var2, var3, var4 - 1, var5)) {
					var11 = 0.5F;
					var13 = false;
				}
			}
		} else if(var6 == 2) {
			var11 = 0.5F;
			var12 = 1.0F;
			var14 = var1.getBlockId(var2, var3, var4 + 1);
			var15 = var1.getBlockMetadata(var2, var3, var4 + 1);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 1 && !this.func_82540_f(var1, var2 + 1, var3, var4, var5)) {
					var10 = 0.5F;
					var13 = false;
				} else if(var16 == 0 && !this.func_82540_f(var1, var2 - 1, var3, var4, var5)) {
					var9 = 0.5F;
					var13 = false;
				}
			}
		} else if(var6 == 3) {
			var14 = var1.getBlockId(var2, var3, var4 - 1);
			var15 = var1.getBlockMetadata(var2, var3, var4 - 1);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 1 && !this.func_82540_f(var1, var2 + 1, var3, var4, var5)) {
					var10 = 0.5F;
					var13 = false;
				} else if(var16 == 0 && !this.func_82540_f(var1, var2 - 1, var3, var4, var5)) {
					var9 = 0.5F;
					var13 = false;
				}
			}
		}

		this.setBlockBounds(var9, var7, var11, var10, var8, var12);
		return var13;
	}

	public boolean func_82544_h(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		int var6 = var5 & 3;
		float var7 = 0.5F;
		float var8 = 1.0F;
		if((var5 & 4) != 0) {
			var7 = 0.0F;
			var8 = 0.5F;
		}

		float var9 = 0.0F;
		float var10 = 0.5F;
		float var11 = 0.5F;
		float var12 = 1.0F;
		boolean var13 = false;
		int var14;
		int var15;
		int var16;
		if(var6 == 0) {
			var14 = var1.getBlockId(var2 - 1, var3, var4);
			var15 = var1.getBlockMetadata(var2 - 1, var3, var4);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 3 && !this.func_82540_f(var1, var2, var3, var4 - 1, var5)) {
					var11 = 0.0F;
					var12 = 0.5F;
					var13 = true;
				} else if(var16 == 2 && !this.func_82540_f(var1, var2, var3, var4 + 1, var5)) {
					var11 = 0.5F;
					var12 = 1.0F;
					var13 = true;
				}
			}
		} else if(var6 == 1) {
			var14 = var1.getBlockId(var2 + 1, var3, var4);
			var15 = var1.getBlockMetadata(var2 + 1, var3, var4);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var9 = 0.5F;
				var10 = 1.0F;
				var16 = var15 & 3;
				if(var16 == 3 && !this.func_82540_f(var1, var2, var3, var4 - 1, var5)) {
					var11 = 0.0F;
					var12 = 0.5F;
					var13 = true;
				} else if(var16 == 2 && !this.func_82540_f(var1, var2, var3, var4 + 1, var5)) {
					var11 = 0.5F;
					var12 = 1.0F;
					var13 = true;
				}
			}
		} else if(var6 == 2) {
			var14 = var1.getBlockId(var2, var3, var4 - 1);
			var15 = var1.getBlockMetadata(var2, var3, var4 - 1);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var11 = 0.0F;
				var12 = 0.5F;
				var16 = var15 & 3;
				if(var16 == 1 && !this.func_82540_f(var1, var2 - 1, var3, var4, var5)) {
					var13 = true;
				} else if(var16 == 0 && !this.func_82540_f(var1, var2 + 1, var3, var4, var5)) {
					var9 = 0.5F;
					var10 = 1.0F;
					var13 = true;
				}
			}
		} else if(var6 == 3) {
			var14 = var1.getBlockId(var2, var3, var4 + 1);
			var15 = var1.getBlockMetadata(var2, var3, var4 + 1);
			if(isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
				var16 = var15 & 3;
				if(var16 == 1 && !this.func_82540_f(var1, var2 - 1, var3, var4, var5)) {
					var13 = true;
				} else if(var16 == 0 && !this.func_82540_f(var1, var2 + 1, var3, var4, var5)) {
					var9 = 0.5F;
					var10 = 1.0F;
					var13 = true;
				}
			}
		}

		if(var13) {
			this.setBlockBounds(var9, var7, var11, var10, var8, var12);
		}

		return var13;
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		this.func_82541_d(var1, var2, var3, var4);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		boolean var8 = this.func_82542_g(var1, var2, var3, var4);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		if(var8 && this.func_82544_h(var1, var2, var3, var4)) {
			super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		}

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		this.modelBlock.randomDisplayTick(var1, var2, var3, var4, var5);
	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		this.modelBlock.onBlockClicked(var1, var2, var3, var4, var5);
	}

	public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {
		this.modelBlock.onBlockDestroyedByPlayer(var1, var2, var3, var4, var5);
	}

	public int getMixedBrightnessForBlock(IBlockAccess var1, int var2, int var3, int var4) {
		return this.modelBlock.getMixedBrightnessForBlock(var1, var2, var3, var4);
	}

	public float getBlockBrightness(IBlockAccess var1, int var2, int var3, int var4) {
		return this.modelBlock.getBlockBrightness(var1, var2, var3, var4);
	}

	public float getExplosionResistance(Entity var1) {
		return this.modelBlock.getExplosionResistance(var1);
	}

	public int getRenderBlockPass() {
		return this.modelBlock.getRenderBlockPass();
	}

	public Icon getIcon(int var1, int var2) {
		return this.modelBlock.getIcon(var1, this.modelBlockMetadata);
	}

	public int tickRate(World var1) {
		return this.modelBlock.tickRate(var1);
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return this.modelBlock.getSelectedBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public void velocityToAddToEntity(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6) {
		this.modelBlock.velocityToAddToEntity(var1, var2, var3, var4, var5, var6);
	}

	public boolean isCollidable() {
		return this.modelBlock.isCollidable();
	}

	public boolean canCollideCheck(int var1, boolean var2) {
		return this.modelBlock.canCollideCheck(var1, var2);
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return this.modelBlock.canPlaceBlockAt(var1, var2, var3, var4);
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		this.onNeighborBlockChange(var1, var2, var3, var4, 0);
		this.modelBlock.onBlockAdded(var1, var2, var3, var4);
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		this.modelBlock.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public void onEntityWalking(World var1, int var2, int var3, int var4, Entity var5) {
		this.modelBlock.onEntityWalking(var1, var2, var3, var4, var5);
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		this.modelBlock.updateTick(var1, var2, var3, var4, var5);
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		return this.modelBlock.onBlockActivated(var1, var2, var3, var4, var5, 0, 0.0F, 0.0F, 0.0F);
	}

	public void onBlockDestroyedByExplosion(World var1, int var2, int var3, int var4, Explosion var5) {
		this.modelBlock.onBlockDestroyedByExplosion(var1, var2, var3, var4, var5);
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int var8 = var1.getBlockMetadata(var2, var3, var4) & 4;
		if(var7 == 0) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 2 | var8, 2);
		}

		if(var7 == 1) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 1 | var8, 2);
		}

		if(var7 == 2) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 3 | var8, 2);
		}

		if(var7 == 3) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 0 | var8, 2);
		}

	}

	public int onBlockPlaced(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
		return var5 != 0 && (var5 == 1 || (double)var7 <= 0.5D) ? var9 : var9 | 4;
	}

	public MovingObjectPosition collisionRayTrace(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
		MovingObjectPosition[] var7 = new MovingObjectPosition[8];
		int var8 = var1.getBlockMetadata(var2, var3, var4);
		int var9 = var8 & 3;
		boolean var10 = (var8 & 4) == 4;
		int[] var11 = field_72159_a[var9 + (var10 ? 4 : 0)];
		this.field_72156_cr = true;

		int var14;
		int var15;
		int var16;
		for(int var12 = 0; var12 < 8; ++var12) {
			this.field_72160_cs = var12;
			int[] var13 = var11;
			var14 = var11.length;

			for(var15 = 0; var15 < var14; ++var15) {
				var16 = var13[var15];
				if(var16 == var12) {
				}
			}

			var7[var12] = super.collisionRayTrace(var1, var2, var3, var4, var5, var6);
		}

		int[] var21 = var11;
		int var23 = var11.length;

		for(var14 = 0; var14 < var23; ++var14) {
			var15 = var21[var14];
			var7[var15] = null;
		}

		MovingObjectPosition var22 = null;
		double var24 = 0.0D;
		MovingObjectPosition[] var25 = var7;
		var16 = var7.length;

		for(int var17 = 0; var17 < var16; ++var17) {
			MovingObjectPosition var18 = var25[var17];
			if(var18 != null) {
				double var19 = var18.hitVec.squareDistanceTo(var6);
				if(var19 > var24) {
					var22 = var18;
					var24 = var19;
				}
			}
		}

		return var22;
	}

	public void registerIcons(IconRegister var1) {
	}
}
