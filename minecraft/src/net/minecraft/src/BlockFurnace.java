package net.minecraft.src;

import java.util.Random;

public class BlockFurnace extends BlockContainer {
	private final Random furnaceRand = new Random();
	private final boolean isActive;
	private static boolean keepFurnaceInventory = false;
	private Icon furnaceIconTop;
	private Icon furnaceIconFront;

	protected BlockFurnace(int var1, boolean var2) {
		super(var1, Material.rock);
		this.isActive = var2;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.furnaceIdle.blockID;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		this.setDefaultDirection(var1, var2, var3, var4);
	}

	private void setDefaultDirection(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote) {
			int var5 = var1.getBlockId(var2, var3, var4 - 1);
			int var6 = var1.getBlockId(var2, var3, var4 + 1);
			int var7 = var1.getBlockId(var2 - 1, var3, var4);
			int var8 = var1.getBlockId(var2 + 1, var3, var4);
			byte var9 = 3;
			if(Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6]) {
				var9 = 3;
			}

			if(Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5]) {
				var9 = 2;
			}

			if(Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8]) {
				var9 = 5;
			}

			if(Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7]) {
				var9 = 4;
			}

			var1.setBlockMetadataWithNotify(var2, var3, var4, var9, 2);
		}
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.furnaceIconTop : (var1 == 0 ? this.furnaceIconTop : (var1 != var2 ? this.blockIcon : this.furnaceIconFront));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("furnace_side");
		this.furnaceIconFront = var1.registerIcon(this.isActive ? "furnace_front_lit" : "furnace_front");
		this.furnaceIconTop = var1.registerIcon("furnace_top");
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(this.isActive) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			float var7 = (float)var2 + 0.5F;
			float var8 = (float)var3 + 0.0F + var5.nextFloat() * 6.0F / 16.0F;
			float var9 = (float)var4 + 0.5F;
			float var10 = 0.52F;
			float var11 = var5.nextFloat() * 0.6F - 0.3F;
			if(var6 == 4) {
				var1.spawnParticle("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 5) {
				var1.spawnParticle("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 2) {
				var1.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 3) {
				var1.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			TileEntityFurnace var10 = (TileEntityFurnace)var1.getBlockTileEntity(var2, var3, var4);
			if(var10 != null) {
				var5.displayGUIFurnace(var10);
			}

			return true;
		}
	}

	public static void updateFurnaceBlockState(boolean var0, World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		TileEntity var6 = var1.getBlockTileEntity(var2, var3, var4);
		keepFurnaceInventory = true;
		if(var0) {
			var1.setBlock(var2, var3, var4, Block.furnaceBurning.blockID);
		} else {
			var1.setBlock(var2, var3, var4, Block.furnaceIdle.blockID);
		}

		keepFurnaceInventory = false;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var5, 2);
		if(var6 != null) {
			var6.validate();
			var1.setBlockTileEntity(var2, var3, var4, var6);
		}

	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityFurnace();
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(var7 == 0) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 2, 2);
		}

		if(var7 == 1) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 5, 2);
		}

		if(var7 == 2) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 3, 2);
		}

		if(var7 == 3) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 4, 2);
		}

		if(var6.hasDisplayName()) {
			((TileEntityFurnace)var1.getBlockTileEntity(var2, var3, var4)).func_94129_a(var6.getDisplayName());
		}

	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(!keepFurnaceInventory) {
			TileEntityFurnace var7 = (TileEntityFurnace)var1.getBlockTileEntity(var2, var3, var4);
			if(var7 != null) {
				for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
					ItemStack var9 = var7.getStackInSlot(var8);
					if(var9 != null) {
						float var10 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float var11 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float var12 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

						while(var9.stackSize > 0) {
							int var13 = this.furnaceRand.nextInt(21) + 10;
							if(var13 > var9.stackSize) {
								var13 = var9.stackSize;
							}

							var9.stackSize -= var13;
							EntityItem var14 = new EntityItem(var1, (double)((float)var2 + var10), (double)((float)var3 + var11), (double)((float)var4 + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
							if(var9.hasTagCompound()) {
								var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
							}

							float var15 = 0.05F;
							var14.motionX = (double)((float)this.furnaceRand.nextGaussian() * var15);
							var14.motionY = (double)((float)this.furnaceRand.nextGaussian() * var15 + 0.2F);
							var14.motionZ = (double)((float)this.furnaceRand.nextGaussian() * var15);
							var1.spawnEntityInWorld(var14);
						}
					}
				}

				var1.func_96440_m(var2, var3, var4, var5);
			}
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public boolean hasComparatorInputOverride() {
		return true;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		return Container.calcRedstoneFromInventory((IInventory)var1.getBlockTileEntity(var2, var3, var4));
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Block.furnaceIdle.blockID;
	}
}
