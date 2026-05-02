package net.minecraft.src;

import java.util.Random;

public abstract class BlockRedstoneLogic extends BlockDirectional {
	protected final boolean isRepeaterPowered;

	protected BlockRedstoneLogic(int var1, boolean var2) {
		super(var1, Material.circuits);
		this.isRepeaterPowered = var2;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F / 16.0F, 1.0F);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return !var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) ? false : super.canPlaceBlockAt(var1, var2, var3, var4);
	}

	public boolean canBlockStay(World var1, int var2, int var3, int var4) {
		return !var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) ? false : super.canBlockStay(var1, var2, var3, var4);
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if(!this.func_94476_e(var1, var2, var3, var4, var6)) {
			boolean var7 = this.func_94478_d(var1, var2, var3, var4, var6);
			if(this.isRepeaterPowered && !var7) {
				var1.setBlock(var2, var3, var4, this.func_94484_i().blockID, var6, 2);
			} else if(!this.isRepeaterPowered) {
				var1.setBlock(var2, var3, var4, this.func_94485_e().blockID, var6, 2);
				if(!var7) {
					var1.func_82740_a(var2, var3, var4, this.func_94485_e().blockID, this.func_94486_g(var6), -1);
				}
			}
		}

	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 0 ? (this.isRepeaterPowered ? Block.torchRedstoneActive.getBlockTextureFromSide(var1) : Block.torchRedstoneIdle.getBlockTextureFromSide(var1)) : (var1 == 1 ? this.blockIcon : Block.stoneDoubleSlab.getBlockTextureFromSide(1));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon(this.isRepeaterPowered ? "repeater_lit" : "repeater");
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 != 0 && var5 != 1;
	}

	public int getRenderType() {
		return 36;
	}

	protected boolean func_96470_c(int var1) {
		return this.isRepeaterPowered;
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return this.isProvidingWeakPower(var1, var2, var3, var4, var5);
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if(!this.func_96470_c(var6)) {
			return 0;
		} else {
			int var7 = getDirection(var6);
			return var7 == 0 && var5 == 3 ? this.func_94480_d(var1, var2, var3, var4, var6) : (var7 == 1 && var5 == 4 ? this.func_94480_d(var1, var2, var3, var4, var6) : (var7 == 2 && var5 == 2 ? this.func_94480_d(var1, var2, var3, var4, var6) : (var7 == 3 && var5 == 5 ? this.func_94480_d(var1, var2, var3, var4, var6) : 0)));
		}
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!this.canBlockStay(var1, var2, var3, var4)) {
			this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4), 0);
			var1.setBlockToAir(var2, var3, var4);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
		} else {
			this.func_94479_f(var1, var2, var3, var4, var5);
		}
	}

	protected void func_94479_f(World var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if(!this.func_94476_e(var1, var2, var3, var4, var6)) {
			boolean var7 = this.func_94478_d(var1, var2, var3, var4, var6);
			if((this.isRepeaterPowered && !var7 || !this.isRepeaterPowered && var7) && !var1.isBlockTickScheduled(var2, var3, var4, this.blockID)) {
				byte var8 = -1;
				if(this.func_83011_d(var1, var2, var3, var4, var6)) {
					var8 = -3;
				} else if(this.isRepeaterPowered) {
					var8 = -2;
				}

				var1.func_82740_a(var2, var3, var4, this.blockID, this.func_94481_j_(var6), var8);
			}
		}

	}

	public boolean func_94476_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return false;
	}

	protected boolean func_94478_d(World var1, int var2, int var3, int var4, int var5) {
		return this.getInputStrength(var1, var2, var3, var4, var5) > 0;
	}

	protected int getInputStrength(World var1, int var2, int var3, int var4, int var5) {
		int var6 = getDirection(var5);
		int var7 = var2 + Direction.offsetX[var6];
		int var8 = var4 + Direction.offsetZ[var6];
		int var9 = var1.getIndirectPowerLevelTo(var7, var3, var8, Direction.directionToFacing[var6]);
		return var9 >= 15 ? var9 : Math.max(var9, var1.getBlockId(var7, var3, var8) == Block.redstoneWire.blockID ? var1.getBlockMetadata(var7, var3, var8) : 0);
	}

	protected int func_94482_f(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		int var6 = getDirection(var5);
		switch(var6) {
		case 0:
		case 2:
			return Math.max(this.func_94488_g(var1, var2 - 1, var3, var4, 4), this.func_94488_g(var1, var2 + 1, var3, var4, 5));
		case 1:
		case 3:
			return Math.max(this.func_94488_g(var1, var2, var3, var4 + 1, 3), this.func_94488_g(var1, var2, var3, var4 - 1, 2));
		default:
			return 0;
		}
	}

	protected int func_94488_g(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockId(var2, var3, var4);
		return this.func_94477_d(var6) ? (var6 == Block.redstoneWire.blockID ? var1.getBlockMetadata(var2, var3, var4) : var1.isBlockProvidingPowerTo(var2, var3, var4, var5)) : 0;
	}

	public boolean canProvidePower() {
		return true;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = ((MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 3);
		boolean var8 = this.func_94478_d(var1, var2, var3, var4, var7);
		if(var8) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID, 1);
		}

	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		this.func_94483_i_(var1, var2, var3, var4);
	}

	protected void func_94483_i_(World var1, int var2, int var3, int var4) {
		int var5 = getDirection(var1.getBlockMetadata(var2, var3, var4));
		if(var5 == 1) {
			var1.notifyBlockOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID, 4);
		}

		if(var5 == 3) {
			var1.notifyBlockOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID, 5);
		}

		if(var5 == 2) {
			var1.notifyBlockOfNeighborChange(var2, var3, var4 + 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID, 2);
		}

		if(var5 == 0) {
			var1.notifyBlockOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID, 3);
		}

	}

	public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {
		if(this.isRepeaterPowered) {
			var1.notifyBlocksOfNeighborChange(var2 + 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2 - 1, var3, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 + 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3, var4 - 1, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this.blockID);
			var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this.blockID);
		}

		super.onBlockDestroyedByPlayer(var1, var2, var3, var4, var5);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	protected boolean func_94477_d(int var1) {
		Block var2 = Block.blocksList[var1];
		return var2 != null && var2.canProvidePower();
	}

	protected int func_94480_d(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return 15;
	}

	public static boolean isRedstoneRepeaterBlockID(int var0) {
		return Block.redstoneRepeaterIdle.func_94487_f(var0) || Block.redstoneComparatorIdle.func_94487_f(var0);
	}

	public boolean func_94487_f(int var1) {
		return var1 == this.func_94485_e().blockID || var1 == this.func_94484_i().blockID;
	}

	public boolean func_83011_d(World var1, int var2, int var3, int var4, int var5) {
		int var6 = getDirection(var5);
		if(isRedstoneRepeaterBlockID(var1.getBlockId(var2 - Direction.offsetX[var6], var3, var4 - Direction.offsetZ[var6]))) {
			int var7 = var1.getBlockMetadata(var2 - Direction.offsetX[var6], var3, var4 - Direction.offsetZ[var6]);
			int var8 = getDirection(var7);
			return var8 != var6;
		} else {
			return false;
		}
	}

	protected int func_94486_g(int var1) {
		return this.func_94481_j_(var1);
	}

	protected abstract int func_94481_j_(int var1);

	protected abstract BlockRedstoneLogic func_94485_e();

	protected abstract BlockRedstoneLogic func_94484_i();

	public boolean isAssociatedBlockID(int var1) {
		return this.func_94487_f(var1);
	}
}
