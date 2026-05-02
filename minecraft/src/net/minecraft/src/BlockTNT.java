package net.minecraft.src;

import java.util.Random;

public class BlockTNT extends Block {
	private Icon field_94393_a;
	private Icon field_94392_b;

	public BlockTNT(int var1) {
		super(var1, Material.tnt);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 0 ? this.field_94392_b : (var1 == 1 ? this.field_94393_a : this.blockIcon);
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		if(var1.isBlockIndirectlyGettingPowered(var2, var3, var4)) {
			this.onBlockDestroyedByPlayer(var1, var2, var3, var4, 1);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(var1.isBlockIndirectlyGettingPowered(var2, var3, var4)) {
			this.onBlockDestroyedByPlayer(var1, var2, var3, var4, 1);
			var1.setBlockToAir(var2, var3, var4);
		}

	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public void onBlockDestroyedByExplosion(World var1, int var2, int var3, int var4, Explosion var5) {
		if(!var1.isRemote) {
			EntityTNTPrimed var6 = new EntityTNTPrimed(var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), var5.func_94613_c());
			var6.fuse = var1.rand.nextInt(var6.fuse / 4) + var6.fuse / 8;
			var1.spawnEntityInWorld(var6);
		}
	}

	public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {
		this.func_94391_a(var1, var2, var3, var4, var5, (EntityLiving)null);
	}

	public void func_94391_a(World var1, int var2, int var3, int var4, int var5, EntityLiving var6) {
		if(!var1.isRemote) {
			if((var5 & 1) == 1) {
				EntityTNTPrimed var7 = new EntityTNTPrimed(var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), var6);
				var1.spawnEntityInWorld(var7);
				var1.playSoundAtEntity(var7, "random.fuse", 1.0F, 1.0F);
			}

		}
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var5.getCurrentEquippedItem() != null && var5.getCurrentEquippedItem().itemID == Item.flintAndSteel.itemID) {
			this.func_94391_a(var1, var2, var3, var4, 1, var5);
			var1.setBlockToAir(var2, var3, var4);
			return true;
		} else {
			return super.onBlockActivated(var1, var2, var3, var4, var5, var6, var7, var8, var9);
		}
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
		if(var5 instanceof EntityArrow && !var1.isRemote) {
			EntityArrow var6 = (EntityArrow)var5;
			if(var6.isBurning()) {
				this.func_94391_a(var1, var2, var3, var4, 1, var6.shootingEntity instanceof EntityLiving ? (EntityLiving)var6.shootingEntity : null);
				var1.setBlockToAir(var2, var3, var4);
			}
		}

	}

	public boolean canDropFromExplosion(Explosion var1) {
		return false;
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("tnt_side");
		this.field_94393_a = var1.registerIcon("tnt_top");
		this.field_94392_b = var1.registerIcon("tnt_bottom");
	}
}
