package net.minecraft.src;

import java.util.List;

public class BlockPistonBase extends Block {
	private final boolean isSticky;
	private Icon innerTopIcon;
	private Icon bottomIcon;
	private Icon topIcon;

	public BlockPistonBase(int var1, boolean var2) {
		super(var1, Material.piston);
		this.isSticky = var2;
		this.setStepSound(soundStoneFootstep);
		this.setHardness(0.5F);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public Icon getPistonExtensionTexture() {
		return this.topIcon;
	}

	public void func_96479_b(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.setBlockBounds(var1, var2, var3, var4, var5, var6);
	}

	public Icon getIcon(int var1, int var2) {
		int var3 = getOrientation(var2);
		return var3 > 5 ? this.topIcon : (var1 == var3 ? (!isExtended(var2) && this.minX <= 0.0D && this.minY <= 0.0D && this.minZ <= 0.0D && this.maxX >= 1.0D && this.maxY >= 1.0D && this.maxZ >= 1.0D ? this.topIcon : this.innerTopIcon) : (var1 == Facing.oppositeSide[var3] ? this.bottomIcon : this.blockIcon));
	}

	public static Icon func_94496_b(String var0) {
		return var0 == "piston_side" ? Block.pistonBase.blockIcon : (var0 == "piston_top" ? Block.pistonBase.topIcon : (var0 == "piston_top_sticky" ? Block.pistonStickyBase.topIcon : (var0 == "piston_inner_top" ? Block.pistonBase.innerTopIcon : null)));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("piston_side");
		this.topIcon = var1.registerIcon(this.isSticky ? "piston_top_sticky" : "piston_top");
		this.innerTopIcon = var1.registerIcon("piston_inner_top");
		this.bottomIcon = var1.registerIcon("piston_bottom");
	}

	public int getRenderType() {
		return 16;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		return false;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = determineOrientation(var1, var2, var3, var4, var5);
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 2);
		if(!var1.isRemote) {
			this.updatePistonState(var1, var2, var3, var4);
		}

	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.isRemote) {
			this.updatePistonState(var1, var2, var3, var4);
		}

	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote && var1.getBlockTileEntity(var2, var3, var4) == null) {
			this.updatePistonState(var1, var2, var3, var4);
		}

	}

	private void updatePistonState(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		int var6 = getOrientation(var5);
		if(var6 != 7) {
			boolean var7 = this.isIndirectlyPowered(var1, var2, var3, var4, var6);
			if(var7 && !isExtended(var5)) {
				if(canExtend(var1, var2, var3, var4, var6)) {
					var1.addBlockEvent(var2, var3, var4, this.blockID, 0, var6);
				}
			} else if(!var7 && isExtended(var5)) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var6, 2);
				var1.addBlockEvent(var2, var3, var4, this.blockID, 1, var6);
			}

		}
	}

	private boolean isIndirectlyPowered(World var1, int var2, int var3, int var4, int var5) {
		return var5 != 0 && var1.getIndirectPowerOutput(var2, var3 - 1, var4, 0) ? true : (var5 != 1 && var1.getIndirectPowerOutput(var2, var3 + 1, var4, 1) ? true : (var5 != 2 && var1.getIndirectPowerOutput(var2, var3, var4 - 1, 2) ? true : (var5 != 3 && var1.getIndirectPowerOutput(var2, var3, var4 + 1, 3) ? true : (var5 != 5 && var1.getIndirectPowerOutput(var2 + 1, var3, var4, 5) ? true : (var5 != 4 && var1.getIndirectPowerOutput(var2 - 1, var3, var4, 4) ? true : (var1.getIndirectPowerOutput(var2, var3, var4, 0) ? true : (var1.getIndirectPowerOutput(var2, var3 + 2, var4, 1) ? true : (var1.getIndirectPowerOutput(var2, var3 + 1, var4 - 1, 2) ? true : (var1.getIndirectPowerOutput(var2, var3 + 1, var4 + 1, 3) ? true : (var1.getIndirectPowerOutput(var2 - 1, var3 + 1, var4, 4) ? true : var1.getIndirectPowerOutput(var2 + 1, var3 + 1, var4, 5)))))))))));
	}

	public boolean onBlockEventReceived(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(!var1.isRemote) {
			boolean var7 = this.isIndirectlyPowered(var1, var2, var3, var4, var6);
			if(var7 && var5 == 1) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var6 | 8, 2);
				return false;
			}

			if(!var7 && var5 == 0) {
				return false;
			}
		}

		if(var5 == 0) {
			if(!this.tryExtend(var1, var2, var3, var4, var6)) {
				return false;
			}

			var1.setBlockMetadataWithNotify(var2, var3, var4, var6 | 8, 2);
			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "tile.piston.out", 0.5F, var1.rand.nextFloat() * 0.25F + 0.6F);
		} else if(var5 == 1) {
			TileEntity var16 = var1.getBlockTileEntity(var2 + Facing.offsetsXForSide[var6], var3 + Facing.offsetsYForSide[var6], var4 + Facing.offsetsZForSide[var6]);
			if(var16 instanceof TileEntityPiston) {
				((TileEntityPiston)var16).clearPistonTileEntity();
			}

			var1.setBlock(var2, var3, var4, Block.pistonMoving.blockID, var6, 3);
			var1.setBlockTileEntity(var2, var3, var4, BlockPistonMoving.getTileEntity(this.blockID, var6, var6, false, true));
			if(this.isSticky) {
				int var8 = var2 + Facing.offsetsXForSide[var6] * 2;
				int var9 = var3 + Facing.offsetsYForSide[var6] * 2;
				int var10 = var4 + Facing.offsetsZForSide[var6] * 2;
				int var11 = var1.getBlockId(var8, var9, var10);
				int var12 = var1.getBlockMetadata(var8, var9, var10);
				boolean var13 = false;
				if(var11 == Block.pistonMoving.blockID) {
					TileEntity var14 = var1.getBlockTileEntity(var8, var9, var10);
					if(var14 instanceof TileEntityPiston) {
						TileEntityPiston var15 = (TileEntityPiston)var14;
						if(var15.getPistonOrientation() == var6 && var15.isExtending()) {
							var15.clearPistonTileEntity();
							var11 = var15.getStoredBlockID();
							var12 = var15.getBlockMetadata();
							var13 = true;
						}
					}
				}

				if(var13 || var11 <= 0 || !canPushBlock(var11, var1, var8, var9, var10, false) || Block.blocksList[var11].getMobilityFlag() != 0 && var11 != Block.pistonBase.blockID && var11 != Block.pistonStickyBase.blockID) {
					if(!var13) {
						var1.setBlockToAir(var2 + Facing.offsetsXForSide[var6], var3 + Facing.offsetsYForSide[var6], var4 + Facing.offsetsZForSide[var6]);
					}
				} else {
					var2 += Facing.offsetsXForSide[var6];
					var3 += Facing.offsetsYForSide[var6];
					var4 += Facing.offsetsZForSide[var6];
					var1.setBlock(var2, var3, var4, Block.pistonMoving.blockID, var12, 3);
					var1.setBlockTileEntity(var2, var3, var4, BlockPistonMoving.getTileEntity(var11, var12, var6, false, false));
					var1.setBlockToAir(var8, var9, var10);
				}
			} else {
				var1.setBlockToAir(var2 + Facing.offsetsXForSide[var6], var3 + Facing.offsetsYForSide[var6], var4 + Facing.offsetsZForSide[var6]);
			}

			var1.playSoundEffect((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, "tile.piston.in", 0.5F, var1.rand.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		if(isExtended(var5)) {
			switch(getOrientation(var5)) {
			case 0:
				this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 1:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 12.0F / 16.0F, 1.0F);
				break;
			case 2:
				this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
				break;
			case 3:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 12.0F / 16.0F);
				break;
			case 4:
				this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 5:
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 12.0F / 16.0F, 1.0F, 1.0F);
			}
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6, var7);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public static int getOrientation(int var0) {
		return var0 & 7;
	}

	public static boolean isExtended(int var0) {
		return (var0 & 8) != 0;
	}

	public static int determineOrientation(World var0, int var1, int var2, int var3, EntityLiving var4) {
		if(MathHelper.abs((float)var4.posX - (float)var1) < 2.0F && MathHelper.abs((float)var4.posZ - (float)var3) < 2.0F) {
			double var5 = var4.posY + 1.82D - (double)var4.yOffset;
			if(var5 - (double)var2 > 2.0D) {
				return 1;
			}

			if((double)var2 - var5 > 0.0D) {
				return 0;
			}
		}

		int var7 = MathHelper.floor_double((double)(var4.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
	}

	private static boolean canPushBlock(int var0, World var1, int var2, int var3, int var4, boolean var5) {
		if(var0 == Block.obsidian.blockID) {
			return false;
		} else {
			if(var0 != Block.pistonBase.blockID && var0 != Block.pistonStickyBase.blockID) {
				if(Block.blocksList[var0].getBlockHardness(var1, var2, var3, var4) == -1.0F) {
					return false;
				}

				if(Block.blocksList[var0].getMobilityFlag() == 2) {
					return false;
				}

				if(Block.blocksList[var0].getMobilityFlag() == 1) {
					if(!var5) {
						return false;
					}

					return true;
				}
			} else if(isExtended(var1.getBlockMetadata(var2, var3, var4))) {
				return false;
			}

			return !(Block.blocksList[var0] instanceof ITileEntityProvider);
		}
	}

	private static boolean canExtend(World var0, int var1, int var2, int var3, int var4) {
		int var5 = var1 + Facing.offsetsXForSide[var4];
		int var6 = var2 + Facing.offsetsYForSide[var4];
		int var7 = var3 + Facing.offsetsZForSide[var4];
		int var8 = 0;

		while(true) {
			if(var8 < 13) {
				if(var6 <= 0 || var6 >= 255) {
					return false;
				}

				int var9 = var0.getBlockId(var5, var6, var7);
				if(var9 != 0) {
					if(!canPushBlock(var9, var0, var5, var6, var7, true)) {
						return false;
					}

					if(Block.blocksList[var9].getMobilityFlag() != 1) {
						if(var8 == 12) {
							return false;
						}

						var5 += Facing.offsetsXForSide[var4];
						var6 += Facing.offsetsYForSide[var4];
						var7 += Facing.offsetsZForSide[var4];
						++var8;
						continue;
					}
				}
			}

			return true;
		}
	}

	private boolean tryExtend(World var1, int var2, int var3, int var4, int var5) {
		int var6 = var2 + Facing.offsetsXForSide[var5];
		int var7 = var3 + Facing.offsetsYForSide[var5];
		int var8 = var4 + Facing.offsetsZForSide[var5];
		int var9 = 0;

		while(true) {
			int var10;
			if(var9 < 13) {
				if(var7 <= 0 || var7 >= 255) {
					return false;
				}

				var10 = var1.getBlockId(var6, var7, var8);
				if(var10 != 0) {
					if(!canPushBlock(var10, var1, var6, var7, var8, true)) {
						return false;
					}

					if(Block.blocksList[var10].getMobilityFlag() != 1) {
						if(var9 == 12) {
							return false;
						}

						var6 += Facing.offsetsXForSide[var5];
						var7 += Facing.offsetsYForSide[var5];
						var8 += Facing.offsetsZForSide[var5];
						++var9;
						continue;
					}

					Block.blocksList[var10].dropBlockAsItem(var1, var6, var7, var8, var1.getBlockMetadata(var6, var7, var8), 0);
					var1.setBlockToAir(var6, var7, var8);
				}
			}

			var9 = var6;
			var10 = var7;
			int var11 = var8;
			int var12 = 0;

			int[] var13;
			int var14;
			int var15;
			int var16;
			for(var13 = new int[13]; var6 != var2 || var7 != var3 || var8 != var4; var8 = var16) {
				var14 = var6 - Facing.offsetsXForSide[var5];
				var15 = var7 - Facing.offsetsYForSide[var5];
				var16 = var8 - Facing.offsetsZForSide[var5];
				int var17 = var1.getBlockId(var14, var15, var16);
				int var18 = var1.getBlockMetadata(var14, var15, var16);
				if(var17 == this.blockID && var14 == var2 && var15 == var3 && var16 == var4) {
					var1.setBlock(var6, var7, var8, Block.pistonMoving.blockID, var5 | (this.isSticky ? 8 : 0), 4);
					var1.setBlockTileEntity(var6, var7, var8, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, var5 | (this.isSticky ? 8 : 0), var5, true, false));
				} else {
					var1.setBlock(var6, var7, var8, Block.pistonMoving.blockID, var18, 4);
					var1.setBlockTileEntity(var6, var7, var8, BlockPistonMoving.getTileEntity(var17, var18, var5, true, false));
				}

				var13[var12++] = var17;
				var6 = var14;
				var7 = var15;
			}

			var6 = var9;
			var7 = var10;
			var8 = var11;

			for(var12 = 0; var6 != var2 || var7 != var3 || var8 != var4; var8 = var16) {
				var14 = var6 - Facing.offsetsXForSide[var5];
				var15 = var7 - Facing.offsetsYForSide[var5];
				var16 = var8 - Facing.offsetsZForSide[var5];
				var1.notifyBlocksOfNeighborChange(var14, var15, var16, var13[var12++]);
				var6 = var14;
				var7 = var15;
			}

			return true;
		}
	}
}
