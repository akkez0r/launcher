package net.minecraft.src;

public class ItemBucket extends Item {
	private int isFull;

	public ItemBucket(int var1, int var2) {
		super(var1);
		this.maxStackSize = 1;
		this.isFull = var2;
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		float var4 = 1.0F;
		double var5 = var3.prevPosX + (var3.posX - var3.prevPosX) * (double)var4;
		double var7 = var3.prevPosY + (var3.posY - var3.prevPosY) * (double)var4 + 1.62D - (double)var3.yOffset;
		double var9 = var3.prevPosZ + (var3.posZ - var3.prevPosZ) * (double)var4;
		boolean var11 = this.isFull == 0;
		MovingObjectPosition var12 = this.getMovingObjectPositionFromPlayer(var2, var3, var11);
		if(var12 == null) {
			return var1;
		} else {
			if(var12.typeOfHit == EnumMovingObjectType.TILE) {
				int var13 = var12.blockX;
				int var14 = var12.blockY;
				int var15 = var12.blockZ;
				if(!var2.canMineBlock(var3, var13, var14, var15)) {
					return var1;
				}

				if(this.isFull == 0) {
					if(!var3.canPlayerEdit(var13, var14, var15, var12.sideHit, var1)) {
						return var1;
					}

					if(var2.getBlockMaterial(var13, var14, var15) == Material.water && var2.getBlockMetadata(var13, var14, var15) == 0) {
						var2.setBlockToAir(var13, var14, var15);
						if(var3.capabilities.isCreativeMode) {
							return var1;
						}

						if(--var1.stackSize <= 0) {
							return new ItemStack(Item.bucketWater);
						}

						if(!var3.inventory.addItemStackToInventory(new ItemStack(Item.bucketWater))) {
							var3.dropPlayerItem(new ItemStack(Item.bucketWater.itemID, 1, 0));
						}

						return var1;
					}

					if(var2.getBlockMaterial(var13, var14, var15) == Material.lava && var2.getBlockMetadata(var13, var14, var15) == 0) {
						var2.setBlockToAir(var13, var14, var15);
						if(var3.capabilities.isCreativeMode) {
							return var1;
						}

						if(--var1.stackSize <= 0) {
							return new ItemStack(Item.bucketLava);
						}

						if(!var3.inventory.addItemStackToInventory(new ItemStack(Item.bucketLava))) {
							var3.dropPlayerItem(new ItemStack(Item.bucketLava.itemID, 1, 0));
						}

						return var1;
					}
				} else {
					if(this.isFull < 0) {
						return new ItemStack(Item.bucketEmpty);
					}

					if(var12.sideHit == 0) {
						--var14;
					}

					if(var12.sideHit == 1) {
						++var14;
					}

					if(var12.sideHit == 2) {
						--var15;
					}

					if(var12.sideHit == 3) {
						++var15;
					}

					if(var12.sideHit == 4) {
						--var13;
					}

					if(var12.sideHit == 5) {
						++var13;
					}

					if(!var3.canPlayerEdit(var13, var14, var15, var12.sideHit, var1)) {
						return var1;
					}

					if(this.tryPlaceContainedLiquid(var2, var5, var7, var9, var13, var14, var15) && !var3.capabilities.isCreativeMode) {
						return new ItemStack(Item.bucketEmpty);
					}
				}
			} else if(this.isFull == 0 && var12.entityHit instanceof EntityCow) {
				return new ItemStack(Item.bucketMilk);
			}

			return var1;
		}
	}

	public boolean tryPlaceContainedLiquid(World var1, double var2, double var4, double var6, int var8, int var9, int var10) {
		if(this.isFull <= 0) {
			return false;
		} else if(!var1.isAirBlock(var8, var9, var10) && var1.getBlockMaterial(var8, var9, var10).isSolid()) {
			return false;
		} else {
			if(var1.provider.isHellWorld && this.isFull == Block.waterMoving.blockID) {
				var1.playSoundEffect(var2 + 0.5D, var4 + 0.5D, var6 + 0.5D, "random.fizz", 0.5F, 2.6F + (var1.rand.nextFloat() - var1.rand.nextFloat()) * 0.8F);

				for(int var11 = 0; var11 < 8; ++var11) {
					var1.spawnParticle("largesmoke", (double)var8 + Math.random(), (double)var9 + Math.random(), (double)var10 + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			} else {
				var1.setBlock(var8, var9, var10, this.isFull, 0, 3);
			}

			return true;
		}
	}
}
