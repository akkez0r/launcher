package net.minecraft.src;

import java.util.Random;

public class BlockDispenser extends BlockContainer {
	public static final IRegistry dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
	protected Random random = new Random();
	protected Icon furnaceTopIcon;
	protected Icon furnaceFrontIcon;
	protected Icon field_96473_e;

	protected BlockDispenser(int var1) {
		super(var1, Material.rock);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public int tickRate(World var1) {
		return 4;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		this.setDispenserDefaultDirection(var1, var2, var3, var4);
	}

	private void setDispenserDefaultDirection(World var1, int var2, int var3, int var4) {
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
		int var3 = var2 & 7;
		return var1 == var3 ? (var3 != 1 && var3 != 0 ? this.furnaceFrontIcon : this.field_96473_e) : (var3 != 1 && var3 != 0 ? (var1 != 1 && var1 != 0 ? this.blockIcon : this.furnaceTopIcon) : this.furnaceTopIcon);
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("furnace_side");
		this.furnaceTopIcon = var1.registerIcon("furnace_top");
		this.furnaceFrontIcon = var1.registerIcon("dispenser_front");
		this.field_96473_e = var1.registerIcon("dispenser_front_vertical");
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			TileEntityDispenser var10 = (TileEntityDispenser)var1.getBlockTileEntity(var2, var3, var4);
			if(var10 != null) {
				var5.displayGUIDispenser(var10);
			}

			return true;
		}
	}

	protected void dispense(World var1, int var2, int var3, int var4) {
		BlockSourceImpl var5 = new BlockSourceImpl(var1, var2, var3, var4);
		TileEntityDispenser var6 = (TileEntityDispenser)var5.getBlockTileEntity();
		if(var6 != null) {
			int var7 = var6.getRandomStackFromInventory();
			if(var7 < 0) {
				var1.playAuxSFX(1001, var2, var3, var4, 0);
			} else {
				ItemStack var8 = var6.getStackInSlot(var7);
				IBehaviorDispenseItem var9 = this.getBehaviorForItemStack(var8);
				if(var9 != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
					ItemStack var10 = var9.dispense(var5, var8);
					var6.setInventorySlotContents(var7, var10.stackSize == 0 ? null : var10);
				}
			}

		}
	}

	protected IBehaviorDispenseItem getBehaviorForItemStack(ItemStack var1) {
		return (IBehaviorDispenseItem)dispenseBehaviorRegistry.func_82594_a(var1.getItem());
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		boolean var6 = var1.isBlockIndirectlyGettingPowered(var2, var3, var4) || var1.isBlockIndirectlyGettingPowered(var2, var3 + 1, var4);
		int var7 = var1.getBlockMetadata(var2, var3, var4);
		boolean var8 = (var7 & 8) != 0;
		if(var6 && !var8) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, this.tickRate(var1));
			var1.setBlockMetadataWithNotify(var2, var3, var4, var7 | 8, 4);
		} else if(!var6 && var8) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var7 & -9, 4);
		}

	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.isRemote) {
			this.dispense(var1, var2, var3, var4);
		}

	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityDispenser();
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = BlockPistonBase.determineOrientation(var1, var2, var3, var4, var5);
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 2);
		if(var6.hasDisplayName()) {
			((TileEntityDispenser)var1.getBlockTileEntity(var2, var3, var4)).setCustomName(var6.getDisplayName());
		}

	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		TileEntityDispenser var7 = (TileEntityDispenser)var1.getBlockTileEntity(var2, var3, var4);
		if(var7 != null) {
			for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
				ItemStack var9 = var7.getStackInSlot(var8);
				if(var9 != null) {
					float var10 = this.random.nextFloat() * 0.8F + 0.1F;
					float var11 = this.random.nextFloat() * 0.8F + 0.1F;
					float var12 = this.random.nextFloat() * 0.8F + 0.1F;

					while(var9.stackSize > 0) {
						int var13 = this.random.nextInt(21) + 10;
						if(var13 > var9.stackSize) {
							var13 = var9.stackSize;
						}

						var9.stackSize -= var13;
						EntityItem var14 = new EntityItem(var1, (double)((float)var2 + var10), (double)((float)var3 + var11), (double)((float)var4 + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
						if(var9.hasTagCompound()) {
							var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
						}

						float var15 = 0.05F;
						var14.motionX = (double)((float)this.random.nextGaussian() * var15);
						var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.2F);
						var14.motionZ = (double)((float)this.random.nextGaussian() * var15);
						var1.spawnEntityInWorld(var14);
					}
				}
			}

			var1.func_96440_m(var2, var3, var4, var5);
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public static IPosition getIPositionFromBlockSource(IBlockSource var0) {
		EnumFacing var1 = getFacing(var0.getBlockMetadata());
		double var2 = var0.getX() + 0.7D * (double)var1.getFrontOffsetX();
		double var4 = var0.getY() + 0.7D * (double)var1.getFrontOffsetY();
		double var6 = var0.getZ() + 0.7D * (double)var1.getFrontOffsetZ();
		return new PositionImpl(var2, var4, var6);
	}

	public static EnumFacing getFacing(int var0) {
		return EnumFacing.getFront(var0 & 7);
	}

	public boolean hasComparatorInputOverride() {
		return true;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		return Container.calcRedstoneFromInventory((IInventory)var1.getBlockTileEntity(var2, var3, var4));
	}
}
