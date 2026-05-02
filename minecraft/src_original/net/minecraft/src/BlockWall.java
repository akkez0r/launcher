package net.minecraft.src;

import java.util.List;

public class BlockWall extends Block {
	public static final String[] types = new String[]{"normal", "mossy"};

	public BlockWall(int var1, Block var2) {
		super(var1, var2.blockMaterial);
		this.setHardness(var2.blockHardness);
		this.setResistance(var2.blockResistance / 3.0F);
		this.setStepSound(var2.stepSound);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public Icon getIcon(int var1, int var2) {
		return var2 == 1 ? Block.cobblestoneMossy.getBlockTextureFromSide(var1) : Block.cobblestone.getBlockTextureFromSide(var1);
	}

	public int getRenderType() {
		return 32;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess var1, int var2, int var3, int var4) {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		boolean var5 = this.canConnectWallTo(var1, var2, var3, var4 - 1);
		boolean var6 = this.canConnectWallTo(var1, var2, var3, var4 + 1);
		boolean var7 = this.canConnectWallTo(var1, var2 - 1, var3, var4);
		boolean var8 = this.canConnectWallTo(var1, var2 + 1, var3, var4);
		float var9 = 0.25F;
		float var10 = 12.0F / 16.0F;
		float var11 = 0.25F;
		float var12 = 12.0F / 16.0F;
		float var13 = 1.0F;
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

		if(var5 && var6 && !var7 && !var8) {
			var13 = 13.0F / 16.0F;
			var9 = 5.0F / 16.0F;
			var10 = 11.0F / 16.0F;
		} else if(!var5 && !var6 && var7 && var8) {
			var13 = 13.0F / 16.0F;
			var11 = 5.0F / 16.0F;
			var12 = 11.0F / 16.0F;
		}

		this.setBlockBounds(var9, 0.0F, var11, var10, var13, var12);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		this.maxY = 1.5D;
		return super.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public boolean canConnectWallTo(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		if(var5 != this.blockID && var5 != Block.fenceGate.blockID) {
			Block var6 = Block.blocksList[var5];
			return var6 != null && var6.blockMaterial.isOpaque() && var6.renderAsNormalBlock() ? var6.blockMaterial != Material.pumpkin : false;
		} else {
			return true;
		}
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
		var3.add(new ItemStack(var1, 1, 1));
	}

	public int damageDropped(int var1) {
		return var1;
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 0 ? super.shouldSideBeRendered(var1, var2, var3, var4, var5) : true;
	}

	public void registerIcons(IconRegister var1) {
	}
}
