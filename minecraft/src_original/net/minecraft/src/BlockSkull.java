package net.minecraft.src;

import java.util.Random;

public class BlockSkull extends BlockContainer {
	protected BlockSkull(int var1) {
		super(var1, Material.circuits);
		this.setBlockBounds(0.25F, 0.0F, 0.25F, 12.0F / 16.0F, 0.5F, 12.0F / 16.0F);
	}

	public int getRenderType() {
		return -1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4) & 7;
		switch(var5) {
		case 1:
		default:
			this.setBlockBounds(0.25F, 0.0F, 0.25F, 12.0F / 16.0F, 0.5F, 12.0F / 16.0F);
			break;
		case 2:
			this.setBlockBounds(0.25F, 0.25F, 0.5F, 12.0F / 16.0F, 12.0F / 16.0F, 1.0F);
			break;
		case 3:
			this.setBlockBounds(0.25F, 0.25F, 0.0F, 12.0F / 16.0F, 12.0F / 16.0F, 0.5F);
			break;
		case 4:
			this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 12.0F / 16.0F, 12.0F / 16.0F);
			break;
		case 5:
			this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 12.0F / 16.0F, 12.0F / 16.0F);
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 2);
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntitySkull();
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.skull.itemID;
	}

	public int getDamageValue(World var1, int var2, int var3, int var4) {
		TileEntity var5 = var1.getBlockTileEntity(var2, var3, var4);
		return var5 != null && var5 instanceof TileEntitySkull ? ((TileEntitySkull)var5).getSkullType() : super.getDamageValue(var1, var2, var3, var4);
	}

	public int damageDropped(int var1) {
		return var1;
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
	}

	public void onBlockHarvested(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
		if(var6.capabilities.isCreativeMode) {
			var5 |= 8;
			var1.setBlockMetadataWithNotify(var2, var3, var4, var5, 4);
		}

		super.onBlockHarvested(var1, var2, var3, var4, var5, var6);
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(!var1.isRemote) {
			if((var6 & 8) == 0) {
				ItemStack var7 = new ItemStack(Item.skull.itemID, 1, this.getDamageValue(var1, var2, var3, var4));
				TileEntitySkull var8 = (TileEntitySkull)var1.getBlockTileEntity(var2, var3, var4);
				if(var8.getSkullType() == 3 && var8.getExtraType() != null && var8.getExtraType().length() > 0) {
					var7.setTagCompound(new NBTTagCompound());
					var7.getTagCompound().setString("SkullOwner", var8.getExtraType());
				}

				this.dropBlockAsItem_do(var1, var2, var3, var4, var7);
			}

			super.breakBlock(var1, var2, var3, var4, var5, var6);
		}
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.skull.itemID;
	}

	public void makeWither(World var1, int var2, int var3, int var4, TileEntitySkull var5) {
		if(var5.getSkullType() == 1 && var3 >= 2 && var1.difficultySetting > 0 && !var1.isRemote) {
			int var6 = Block.slowSand.blockID;

			int var7;
			EntityWither var8;
			int var9;
			for(var7 = -2; var7 <= 0; ++var7) {
				if(var1.getBlockId(var2, var3 - 1, var4 + var7) == var6 && var1.getBlockId(var2, var3 - 1, var4 + var7 + 1) == var6 && var1.getBlockId(var2, var3 - 2, var4 + var7 + 1) == var6 && var1.getBlockId(var2, var3 - 1, var4 + var7 + 2) == var6 && this.func_82528_d(var1, var2, var3, var4 + var7, 1) && this.func_82528_d(var1, var2, var3, var4 + var7 + 1, 1) && this.func_82528_d(var1, var2, var3, var4 + var7 + 2, 1)) {
					var1.setBlockMetadataWithNotify(var2, var3, var4 + var7, 8, 2);
					var1.setBlockMetadataWithNotify(var2, var3, var4 + var7 + 1, 8, 2);
					var1.setBlockMetadataWithNotify(var2, var3, var4 + var7 + 2, 8, 2);
					var1.setBlock(var2, var3, var4 + var7, 0, 0, 2);
					var1.setBlock(var2, var3, var4 + var7 + 1, 0, 0, 2);
					var1.setBlock(var2, var3, var4 + var7 + 2, 0, 0, 2);
					var1.setBlock(var2, var3 - 1, var4 + var7, 0, 0, 2);
					var1.setBlock(var2, var3 - 1, var4 + var7 + 1, 0, 0, 2);
					var1.setBlock(var2, var3 - 1, var4 + var7 + 2, 0, 0, 2);
					var1.setBlock(var2, var3 - 2, var4 + var7 + 1, 0, 0, 2);
					if(!var1.isRemote) {
						var8 = new EntityWither(var1);
						var8.setLocationAndAngles((double)var2 + 0.5D, (double)var3 - 1.45D, (double)(var4 + var7) + 1.5D, 90.0F, 0.0F);
						var8.renderYawOffset = 90.0F;
						var8.func_82206_m();
						var1.spawnEntityInWorld(var8);
					}

					for(var9 = 0; var9 < 120; ++var9) {
						var1.spawnParticle("snowballpoof", (double)var2 + var1.rand.nextDouble(), (double)(var3 - 2) + var1.rand.nextDouble() * 3.9D, (double)(var4 + var7 + 1) + var1.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
					}

					var1.notifyBlockChange(var2, var3, var4 + var7, 0);
					var1.notifyBlockChange(var2, var3, var4 + var7 + 1, 0);
					var1.notifyBlockChange(var2, var3, var4 + var7 + 2, 0);
					var1.notifyBlockChange(var2, var3 - 1, var4 + var7, 0);
					var1.notifyBlockChange(var2, var3 - 1, var4 + var7 + 1, 0);
					var1.notifyBlockChange(var2, var3 - 1, var4 + var7 + 2, 0);
					var1.notifyBlockChange(var2, var3 - 2, var4 + var7 + 1, 0);
					return;
				}
			}

			for(var7 = -2; var7 <= 0; ++var7) {
				if(var1.getBlockId(var2 + var7, var3 - 1, var4) == var6 && var1.getBlockId(var2 + var7 + 1, var3 - 1, var4) == var6 && var1.getBlockId(var2 + var7 + 1, var3 - 2, var4) == var6 && var1.getBlockId(var2 + var7 + 2, var3 - 1, var4) == var6 && this.func_82528_d(var1, var2 + var7, var3, var4, 1) && this.func_82528_d(var1, var2 + var7 + 1, var3, var4, 1) && this.func_82528_d(var1, var2 + var7 + 2, var3, var4, 1)) {
					var1.setBlockMetadataWithNotify(var2 + var7, var3, var4, 8, 2);
					var1.setBlockMetadataWithNotify(var2 + var7 + 1, var3, var4, 8, 2);
					var1.setBlockMetadataWithNotify(var2 + var7 + 2, var3, var4, 8, 2);
					var1.setBlock(var2 + var7, var3, var4, 0, 0, 2);
					var1.setBlock(var2 + var7 + 1, var3, var4, 0, 0, 2);
					var1.setBlock(var2 + var7 + 2, var3, var4, 0, 0, 2);
					var1.setBlock(var2 + var7, var3 - 1, var4, 0, 0, 2);
					var1.setBlock(var2 + var7 + 1, var3 - 1, var4, 0, 0, 2);
					var1.setBlock(var2 + var7 + 2, var3 - 1, var4, 0, 0, 2);
					var1.setBlock(var2 + var7 + 1, var3 - 2, var4, 0, 0, 2);
					if(!var1.isRemote) {
						var8 = new EntityWither(var1);
						var8.setLocationAndAngles((double)(var2 + var7) + 1.5D, (double)var3 - 1.45D, (double)var4 + 0.5D, 0.0F, 0.0F);
						var8.func_82206_m();
						var1.spawnEntityInWorld(var8);
					}

					for(var9 = 0; var9 < 120; ++var9) {
						var1.spawnParticle("snowballpoof", (double)(var2 + var7 + 1) + var1.rand.nextDouble(), (double)(var3 - 2) + var1.rand.nextDouble() * 3.9D, (double)var4 + var1.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
					}

					var1.notifyBlockChange(var2 + var7, var3, var4, 0);
					var1.notifyBlockChange(var2 + var7 + 1, var3, var4, 0);
					var1.notifyBlockChange(var2 + var7 + 2, var3, var4, 0);
					var1.notifyBlockChange(var2 + var7, var3 - 1, var4, 0);
					var1.notifyBlockChange(var2 + var7 + 1, var3 - 1, var4, 0);
					var1.notifyBlockChange(var2 + var7 + 2, var3 - 1, var4, 0);
					var1.notifyBlockChange(var2 + var7 + 1, var3 - 2, var4, 0);
					return;
				}
			}
		}

	}

	private boolean func_82528_d(World var1, int var2, int var3, int var4, int var5) {
		if(var1.getBlockId(var2, var3, var4) != this.blockID) {
			return false;
		} else {
			TileEntity var6 = var1.getBlockTileEntity(var2, var3, var4);
			return var6 != null && var6 instanceof TileEntitySkull ? ((TileEntitySkull)var6).getSkullType() == var5 : false;
		}
	}

	public void registerIcons(IconRegister var1) {
	}

	public Icon getIcon(int var1, int var2) {
		return Block.slowSand.getBlockTextureFromSide(var1);
	}

	public String getItemIconName() {
		return ItemSkull.field_94587_a[0];
	}
}
