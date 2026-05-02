package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockBrewingStand extends BlockContainer {
	private Random rand = new Random();
	private Icon theIcon;

	public BlockBrewingStand(int var1) {
		super(var1, Material.iron);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 25;
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityBrewingStand();
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		this.setBlockBounds(7.0F / 16.0F, 0.0F, 7.0F / 16.0F, 9.0F / 16.0F, 14.0F / 16.0F, 9.0F / 16.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		this.setBlockBoundsForItemRender();
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F / 16.0F, 1.0F);
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			TileEntityBrewingStand var10 = (TileEntityBrewingStand)var1.getBlockTileEntity(var2, var3, var4);
			if(var10 != null) {
				var5.displayGUIBrewingStand(var10);
			}

			return true;
		}
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		if(var6.hasDisplayName()) {
			((TileEntityBrewingStand)var1.getBlockTileEntity(var2, var3, var4)).func_94131_a(var6.getDisplayName());
		}

	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		double var6 = (double)((float)var2 + 0.4F + var5.nextFloat() * 0.2F);
		double var8 = (double)((float)var3 + 0.7F + var5.nextFloat() * 0.3F);
		double var10 = (double)((float)var4 + 0.4F + var5.nextFloat() * 0.2F);
		var1.spawnParticle("smoke", var6, var8, var10, 0.0D, 0.0D, 0.0D);
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		TileEntity var7 = var1.getBlockTileEntity(var2, var3, var4);
		if(var7 instanceof TileEntityBrewingStand) {
			TileEntityBrewingStand var8 = (TileEntityBrewingStand)var7;

			for(int var9 = 0; var9 < var8.getSizeInventory(); ++var9) {
				ItemStack var10 = var8.getStackInSlot(var9);
				if(var10 != null) {
					float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var12 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var13 = this.rand.nextFloat() * 0.8F + 0.1F;

					while(var10.stackSize > 0) {
						int var14 = this.rand.nextInt(21) + 10;
						if(var14 > var10.stackSize) {
							var14 = var10.stackSize;
						}

						var10.stackSize -= var14;
						EntityItem var15 = new EntityItem(var1, (double)((float)var2 + var11), (double)((float)var3 + var12), (double)((float)var4 + var13), new ItemStack(var10.itemID, var14, var10.getItemDamage()));
						float var16 = 0.05F;
						var15.motionX = (double)((float)this.rand.nextGaussian() * var16);
						var15.motionY = (double)((float)this.rand.nextGaussian() * var16 + 0.2F);
						var15.motionZ = (double)((float)this.rand.nextGaussian() * var16);
						var1.spawnEntityInWorld(var15);
					}
				}
			}
		}

		super.breakBlock(var1, var2, var3, var4, var5, var6);
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Item.brewingStand.itemID;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return Item.brewingStand.itemID;
	}

	public boolean hasComparatorInputOverride() {
		return true;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		return Container.calcRedstoneFromInventory((IInventory)var1.getBlockTileEntity(var2, var3, var4));
	}

	public void registerIcons(IconRegister var1) {
		super.registerIcons(var1);
		this.theIcon = var1.registerIcon("brewingStand_base");
	}

	public Icon getBrewingStandIcon() {
		return this.theIcon;
	}
}
