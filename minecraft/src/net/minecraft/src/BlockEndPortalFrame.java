package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockEndPortalFrame extends Block {
	private Icon field_94400_a;
	private Icon field_94399_b;

	public BlockEndPortalFrame(int var1) {
		super(var1, Material.rock);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.field_94400_a : (var1 == 0 ? Block.whiteStone.getBlockTextureFromSide(var1) : this.blockIcon);
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("endframe_side");
		this.field_94400_a = var1.registerIcon("endframe_top");
		this.field_94399_b = var1.registerIcon("endframe_eye");
	}

	public Icon func_94398_p() {
		return this.field_94399_b;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 26;
	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 13.0F / 16.0F, 1.0F);
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 13.0F / 16.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		int var8 = var1.getBlockMetadata(var2, var3, var4);
		if(isEnderEyeInserted(var8)) {
			this.setBlockBounds(5.0F / 16.0F, 13.0F / 16.0F, 5.0F / 16.0F, 11.0F / 16.0F, 1.0F, 11.0F / 16.0F);
			super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		}

		this.setBlockBoundsForItemRender();
	}

	public static boolean isEnderEyeInserted(int var0) {
		return (var0 & 4) != 0;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return 0;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = ((MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 2);
	}
}
