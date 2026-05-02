package net.minecraft.src;

import java.util.Iterator;
import java.util.Random;

public class BlockChest extends BlockContainer {
	private final Random random = new Random();
	public final int isTrapped;

	protected BlockChest(int var1, int var2) {
		super(var1, Material.wood);
		this.isTrapped = var2;
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 14.0F / 16.0F, 15.0F / 16.0F);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 22;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		if(var1.getBlockId(var2, var3, var4 - 1) == this.blockID) {
			this.setBlockBounds(1.0F / 16.0F, 0.0F, 0.0F, 15.0F / 16.0F, 14.0F / 16.0F, 15.0F / 16.0F);
		} else if(var1.getBlockId(var2, var3, var4 + 1) == this.blockID) {
			this.setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 14.0F / 16.0F, 1.0F);
		} else if(var1.getBlockId(var2 - 1, var3, var4) == this.blockID) {
			this.setBlockBounds(0.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 14.0F / 16.0F, 15.0F / 16.0F);
		} else if(var1.getBlockId(var2 + 1, var3, var4) == this.blockID) {
			this.setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 1.0F, 14.0F / 16.0F, 15.0F / 16.0F);
		} else {
			this.setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 14.0F / 16.0F, 15.0F / 16.0F);
		}

	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		this.unifyAdjacentChests(var1, var2, var3, var4);
		int var5 = var1.getBlockId(var2, var3, var4 - 1);
		int var6 = var1.getBlockId(var2, var3, var4 + 1);
		int var7 = var1.getBlockId(var2 - 1, var3, var4);
		int var8 = var1.getBlockId(var2 + 1, var3, var4);
		if(var5 == this.blockID) {
			this.unifyAdjacentChests(var1, var2, var3, var4 - 1);
		}

		if(var6 == this.blockID) {
			this.unifyAdjacentChests(var1, var2, var3, var4 + 1);
		}

		if(var7 == this.blockID) {
			this.unifyAdjacentChests(var1, var2 - 1, var3, var4);
		}

		if(var8 == this.blockID) {
			this.unifyAdjacentChests(var1, var2 + 1, var3, var4);
		}

	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = var1.getBlockId(var2, var3, var4 - 1);
		int var8 = var1.getBlockId(var2, var3, var4 + 1);
		int var9 = var1.getBlockId(var2 - 1, var3, var4);
		int var10 = var1.getBlockId(var2 + 1, var3, var4);
		byte var11 = 0;
		int var12 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(var12 == 0) {
			var11 = 2;
		}

		if(var12 == 1) {
			var11 = 5;
		}

		if(var12 == 2) {
			var11 = 3;
		}

		if(var12 == 3) {
			var11 = 4;
		}

		if(var7 != this.blockID && var8 != this.blockID && var9 != this.blockID && var10 != this.blockID) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var11, 3);
		} else {
			if((var7 == this.blockID || var8 == this.blockID) && (var11 == 4 || var11 == 5)) {
				if(var7 == this.blockID) {
					var1.setBlockMetadataWithNotify(var2, var3, var4 - 1, var11, 3);
				} else {
					var1.setBlockMetadataWithNotify(var2, var3, var4 + 1, var11, 3);
				}

				var1.setBlockMetadataWithNotify(var2, var3, var4, var11, 3);
			}

			if((var9 == this.blockID || var10 == this.blockID) && (var11 == 2 || var11 == 3)) {
				if(var9 == this.blockID) {
					var1.setBlockMetadataWithNotify(var2 - 1, var3, var4, var11, 3);
				} else {
					var1.setBlockMetadataWithNotify(var2 + 1, var3, var4, var11, 3);
				}

				var1.setBlockMetadataWithNotify(var2, var3, var4, var11, 3);
			}
		}

		if(var6.hasDisplayName()) {
			((TileEntityChest)var1.getBlockTileEntity(var2, var3, var4)).func_94043_a(var6.getDisplayName());
		}

	}

	public void unifyAdjacentChests(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote) {
			int var5 = var1.getBlockId(var2, var3, var4 - 1);
			int var6 = var1.getBlockId(var2, var3, var4 + 1);
			int var7 = var1.getBlockId(var2 - 1, var3, var4);
			int var8 = var1.getBlockId(var2 + 1, var3, var4);
			boolean var9 = true;
			int var10;
			int var11;
			boolean var12;
			byte var13;
			int var14;
			if(var5 != this.blockID && var6 != this.blockID) {
				if(var7 != this.blockID && var8 != this.blockID) {
					var13 = 3;
					if(Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6]) {
						var13 = 3;
					}

					if(Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5]) {
						var13 = 2;
					}

					if(Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8]) {
						var13 = 5;
					}

					if(Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7]) {
						var13 = 4;
					}
				} else {
					var10 = var1.getBlockId(var7 == this.blockID ? var2 - 1 : var2 + 1, var3, var4 - 1);
					var11 = var1.getBlockId(var7 == this.blockID ? var2 - 1 : var2 + 1, var3, var4 + 1);
					var13 = 3;
					var12 = true;
					if(var7 == this.blockID) {
						var14 = var1.getBlockMetadata(var2 - 1, var3, var4);
					} else {
						var14 = var1.getBlockMetadata(var2 + 1, var3, var4);
					}

					if(var14 == 2) {
						var13 = 2;
					}

					if((Block.opaqueCubeLookup[var5] || Block.opaqueCubeLookup[var10]) && !Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var11]) {
						var13 = 3;
					}

					if((Block.opaqueCubeLookup[var6] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var10]) {
						var13 = 2;
					}
				}
			} else {
				var10 = var1.getBlockId(var2 - 1, var3, var5 == this.blockID ? var4 - 1 : var4 + 1);
				var11 = var1.getBlockId(var2 + 1, var3, var5 == this.blockID ? var4 - 1 : var4 + 1);
				var13 = 5;
				var12 = true;
				if(var5 == this.blockID) {
					var14 = var1.getBlockMetadata(var2, var3, var4 - 1);
				} else {
					var14 = var1.getBlockMetadata(var2, var3, var4 + 1);
				}

				if(var14 == 4) {
					var13 = 4;
				}

				if((Block.opaqueCubeLookup[var7] || Block.opaqueCubeLookup[var10]) && !Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var11]) {
					var13 = 5;
				}

				if((Block.opaqueCubeLookup[var8] || Block.opaqueCubeLookup[var11]) && !Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var10]) {
					var13 = 4;
				}
			}

			var1.setBlockMetadataWithNotify(var2, var3, var4, var13, 3);
		}
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		int var5 = 0;
		if(var1.getBlockId(var2 - 1, var3, var4) == this.blockID) {
			++var5;
		}

		if(var1.getBlockId(var2 + 1, var3, var4) == this.blockID) {
			++var5;
		}

		if(var1.getBlockId(var2, var3, var4 - 1) == this.blockID) {
			++var5;
		}

		if(var1.getBlockId(var2, var3, var4 + 1) == this.blockID) {
			++var5;
		}

		return var5 > 1 ? false : (this.isThereANeighborChest(var1, var2 - 1, var3, var4) ? false : (this.isThereANeighborChest(var1, var2 + 1, var3, var4) ? false : (this.isThereANeighborChest(var1, var2, var3, var4 - 1) ? false : !this.isThereANeighborChest(var1, var2, var3, var4 + 1))));
	}

	private boolean isThereANeighborChest(World var1, int var2, int var3, int var4) {
		return var1.getBlockId(var2, var3, var4) != this.blockID ? false : (var1.getBlockId(var2 - 1, var3, var4) == this.blockID ? true : (var1.getBlockId(var2 + 1, var3, var4) == this.blockID ? true : (var1.getBlockId(var2, var3, var4 - 1) == this.blockID ? true : var1.getBlockId(var2, var3, var4 + 1) == this.blockID)));
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		super.onNeighborBlockChange(var1, var2, var3, var4, var5);
		TileEntityChest var6 = (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4);
		if(var6 != null) {
			var6.updateContainingBlockInfo();
		}

	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		TileEntityChest var7 = (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4);
		if(var7 != null) {
			for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
				ItemStack var9 = var7.getStackInSlot(var8);
				if(var9 != null) {
					float var10 = this.random.nextFloat() * 0.8F + 0.1F;
					float var11 = this.random.nextFloat() * 0.8F + 0.1F;

					EntityItem var14;
					for(float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; var1.spawnEntityInWorld(var14)) {
						int var13 = this.random.nextInt(21) + 10;
						if(var13 > var9.stackSize) {
							var13 = var9.stackSize;
						}

						var9.stackSize -= var13;
						var14 = new EntityItem(var1, (double)((float)var2 + var10), (double)((float)var3 + var11), (double)((float)var4 + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
						float var15 = 0.05F;
						var14.motionX = (double)((float)this.random.nextGaussian() * var15);
						var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.2F);
						var14.motionZ = (double)((float)this.random.nextGaussian() * var15);
						if(var9.hasTagCompound()) {
							var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
						}
					}
				}
			}

			var1.func_96440_m(var2, var3, var4, var5);
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			IInventory var10 = this.getInventory(var1, var2, var3, var4);
			if(var10 != null) {
				var5.displayGUIChest(var10);
			}

			return true;
		}
	}

	public IInventory getInventory(World var1, int var2, int var3, int var4) {
		Object var5 = (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4);
		if(var5 == null) {
			return null;
		} else if(var1.isBlockNormalCube(var2, var3 + 1, var4)) {
			return null;
		} else if(isOcelotBlockingChest(var1, var2, var3, var4)) {
			return null;
		} else if(var1.getBlockId(var2 - 1, var3, var4) != this.blockID || !var1.isBlockNormalCube(var2 - 1, var3 + 1, var4) && !isOcelotBlockingChest(var1, var2 - 1, var3, var4)) {
			if(var1.getBlockId(var2 + 1, var3, var4) != this.blockID || !var1.isBlockNormalCube(var2 + 1, var3 + 1, var4) && !isOcelotBlockingChest(var1, var2 + 1, var3, var4)) {
				if(var1.getBlockId(var2, var3, var4 - 1) != this.blockID || !var1.isBlockNormalCube(var2, var3 + 1, var4 - 1) && !isOcelotBlockingChest(var1, var2, var3, var4 - 1)) {
					if(var1.getBlockId(var2, var3, var4 + 1) != this.blockID || !var1.isBlockNormalCube(var2, var3 + 1, var4 + 1) && !isOcelotBlockingChest(var1, var2, var3, var4 + 1)) {
						if(var1.getBlockId(var2 - 1, var3, var4) == this.blockID) {
							var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var1.getBlockTileEntity(var2 - 1, var3, var4), (IInventory)var5);
						}

						if(var1.getBlockId(var2 + 1, var3, var4) == this.blockID) {
							var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)var1.getBlockTileEntity(var2 + 1, var3, var4));
						}

						if(var1.getBlockId(var2, var3, var4 - 1) == this.blockID) {
							var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4 - 1), (IInventory)var5);
						}

						if(var1.getBlockId(var2, var3, var4 + 1) == this.blockID) {
							var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4 + 1));
						}

						return (IInventory)var5;
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public TileEntity createNewTileEntity(World var1) {
		TileEntityChest var2 = new TileEntityChest();
		return var2;
	}

	public boolean canProvidePower() {
		return this.isTrapped == 1;
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(!this.canProvidePower()) {
			return 0;
		} else {
			int var6 = ((TileEntityChest)var1.getBlockTileEntity(var2, var3, var4)).numUsingPlayers;
			return MathHelper.clamp_int(var6, 0, 15);
		}
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 1 ? this.isProvidingWeakPower(var1, var2, var3, var4, var5) : 0;
	}

	private static boolean isOcelotBlockingChest(World var0, int var1, int var2, int var3) {
		Iterator var4 = var0.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getAABBPool().getAABB((double)var1, (double)(var2 + 1), (double)var3, (double)(var1 + 1), (double)(var2 + 2), (double)(var3 + 1))).iterator();

		EntityOcelot var6;
		do {
			if(!var4.hasNext()) {
				return false;
			}

			EntityOcelot var5 = (EntityOcelot)var4.next();
			var6 = (EntityOcelot)var5;
		} while(!var6.isSitting());

		return true;
	}

	public boolean hasComparatorInputOverride() {
		return true;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		return Container.calcRedstoneFromInventory(this.getInventory(var1, var2, var3, var4));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("wood");
	}
}
