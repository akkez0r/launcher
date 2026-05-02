package net.minecraft.src;

import java.util.List;

public class BlockFence extends Block {
	private final String field_94464_a;

	public BlockFence(int var1, String var2, Material var3) {
		super(var1, var3);
		this.field_94464_a = var2;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		boolean var8 = this.canConnectFenceTo(var1, var2, var3, var4 - 1);
		boolean var9 = this.canConnectFenceTo(var1, var2, var3, var4 + 1);
		boolean var10 = this.canConnectFenceTo(var1, var2 - 1, var3, var4);
		boolean var11 = this.canConnectFenceTo(var1, var2 + 1, var3, var4);
		float var12 = 6.0F / 16.0F;
		float var13 = 10.0F / 16.0F;
		float var14 = 6.0F / 16.0F;
		float var15 = 10.0F / 16.0F;
		if(var8) {
			var14 = 0.0F;
		}

		if(var9) {
			var15 = 1.0F;
		}

		if(var8 || var9) {
			this.setBlockBounds(var12, 0.0F, var14, var13, 1.5F, var15);
			super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		}

		var14 = 6.0F / 16.0F;
		var15 = 10.0F / 16.0F;
		if(var10) {
			var12 = 0.0F;
		}

		if(var11) {
			var13 = 1.0F;
		}

		if(var10 || var11 || !var8 && !var9) {
			this.setBlockBounds(var12, 0.0F, var14, var13, 1.5F, var15);
			super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
		}

		if(var8) {
			var14 = 0.0F;
		}

		if(var9) {
			var15 = 1.0F;
		}

		this.setBlockBounds(var12, 0.0F, var14, var13, 1.0F, var15);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		boolean var5 = this.canConnectFenceTo(var1, var2, var3, var4 - 1);
		boolean var6 = this.canConnectFenceTo(var1, var2, var3, var4 + 1);
		boolean var7 = this.canConnectFenceTo(var1, var2 - 1, var3, var4);
		boolean var8 = this.canConnectFenceTo(var1, var2 + 1, var3, var4);
		float var9 = 6.0F / 16.0F;
		float var10 = 10.0F / 16.0F;
		float var11 = 6.0F / 16.0F;
		float var12 = 10.0F / 16.0F;
		if(var5) {
			var11 = 0.0F;
		}

		if(var6) {
			var12 = 1.0F;
		}

		if(var7) {
			var9 = 0.0F;
		}

		if(var8) {
			var10 = 1.0F;
		}

		this.setBlockBounds(var9, 0.0F, var11, var10, 1.0F, var12);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess var1, int var2, int var3, int var4) {
		return false;
	}

	public int getRenderType() {
		return 11;
	}

	public boolean canConnectFenceTo(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		if(var5 != this.blockID && var5 != Block.fenceGate.blockID) {
			Block var6 = Block.blocksList[var5];
			return var6 != null && var6.blockMaterial.isOpaque() && var6.renderAsNormalBlock() ? var6.blockMaterial != Material.pumpkin : false;
		} else {
			return true;
		}
	}

	public static boolean isIdAFence(int var0) {
		return var0 == Block.fence.blockID || var0 == Block.netherFence.blockID;
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return true;
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon(this.field_94464_a);
	}
}
