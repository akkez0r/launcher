package net.minecraft.src;

import java.util.Random;

public class BlockDoor extends Block {
	private static final String[] doorIconNames = new String[]{"doorWood_lower", "doorWood_upper", "doorIron_lower", "doorIron_upper"};
	private final int doorTypeForIcon;
	private Icon[] iconArray;

	protected BlockDoor(int var1, Material var2) {
		super(var1, var2);
		if(var2 == Material.iron) {
			this.doorTypeForIcon = 2;
		} else {
			this.doorTypeForIcon = 0;
		}

		float var3 = 0.5F;
		float var4 = 1.0F;
		this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
	}

	public Icon getIcon(int var1, int var2) {
		return this.iconArray[this.doorTypeForIcon];
	}

	public Icon getBlockTexture(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(var5 != 1 && var5 != 0) {
			int var6 = this.getFullMetadata(var1, var2, var3, var4);
			int var7 = var6 & 3;
			boolean var8 = (var6 & 4) != 0;
			boolean var9 = false;
			boolean var10 = (var6 & 8) != 0;
			if(var8) {
				if(var7 == 0 && var5 == 2) {
					var9 = !var9;
				} else if(var7 == 1 && var5 == 5) {
					var9 = !var9;
				} else if(var7 == 2 && var5 == 3) {
					var9 = !var9;
				} else if(var7 == 3 && var5 == 4) {
					var9 = !var9;
				}
			} else {
				if(var7 == 0 && var5 == 5) {
					var9 = !var9;
				} else if(var7 == 1 && var5 == 3) {
					var9 = !var9;
				} else if(var7 == 2 && var5 == 4) {
					var9 = !var9;
				} else if(var7 == 3 && var5 == 2) {
					var9 = !var9;
				}

				if((var6 & 16) != 0) {
					var9 = !var9;
				}
			}

			return this.iconArray[this.doorTypeForIcon + (var9 ? doorIconNames.length : 0) + (var10 ? 1 : 0)];
		} else {
			return this.iconArray[this.doorTypeForIcon];
		}
	}

	public void registerIcons(IconRegister var1) {
		this.iconArray = new Icon[doorIconNames.length * 2];

		for(int var2 = 0; var2 < doorIconNames.length; ++var2) {
			this.iconArray[var2] = var1.registerIcon(doorIconNames[var2]);
			this.iconArray[var2 + doorIconNames.length] = new IconFlipped(this.iconArray[var2], true, false);
		}

	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = this.getFullMetadata(var1, var2, var3, var4);
		return (var5 & 4) != 0;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 7;
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.getSelectedBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		this.setDoorRotation(this.getFullMetadata(var1, var2, var3, var4));
	}

	public int getDoorOrientation(IBlockAccess var1, int var2, int var3, int var4) {
		return this.getFullMetadata(var1, var2, var3, var4) & 3;
	}

	public boolean isDoorOpen(IBlockAccess var1, int var2, int var3, int var4) {
		return (this.getFullMetadata(var1, var2, var3, var4) & 4) != 0;
	}

	private void setDoorRotation(int var1) {
		float var2 = 3.0F / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int var3 = var1 & 3;
		boolean var4 = (var1 & 4) != 0;
		boolean var5 = (var1 & 16) != 0;
		if(var3 == 0) {
			if(var4) {
				if(!var5) {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
			}
		} else if(var3 == 1) {
			if(var4) {
				if(!var5) {
					this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
			}
		} else if(var3 == 2) {
			if(var4) {
				if(!var5) {
					this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
				}
			} else {
				this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		} else if(var3 == 3) {
			if(var4) {
				if(!var5) {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
			}
		}

	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(this.blockMaterial == Material.iron) {
			return true;
		} else {
			int var10 = this.getFullMetadata(var1, var2, var3, var4);
			int var11 = var10 & 7;
			var11 ^= 4;
			if((var10 & 8) == 0) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var11, 2);
				var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
			} else {
				var1.setBlockMetadataWithNotify(var2, var3 - 1, var4, var11, 2);
				var1.markBlockRangeForRenderUpdate(var2, var3 - 1, var4, var2, var3, var4);
			}

			var1.playAuxSFXAtEntity(var5, 1003, var2, var3, var4, 0);
			return true;
		}
	}

	public void onPoweredBlockChange(World var1, int var2, int var3, int var4, boolean var5) {
		int var6 = this.getFullMetadata(var1, var2, var3, var4);
		boolean var7 = (var6 & 4) != 0;
		if(var7 != var5) {
			int var8 = var6 & 7;
			var8 ^= 4;
			if((var6 & 8) == 0) {
				var1.setBlockMetadataWithNotify(var2, var3, var4, var8, 2);
				var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
			} else {
				var1.setBlockMetadataWithNotify(var2, var3 - 1, var4, var8, 2);
				var1.markBlockRangeForRenderUpdate(var2, var3 - 1, var4, var2, var3, var4);
			}

			var1.playAuxSFXAtEntity((EntityPlayer)null, 1003, var2, var3, var4, 0);
		}
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if((var6 & 8) == 0) {
			boolean var7 = false;
			if(var1.getBlockId(var2, var3 + 1, var4) != this.blockID) {
				var1.setBlockToAir(var2, var3, var4);
				var7 = true;
			}

			if(!var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4)) {
				var1.setBlockToAir(var2, var3, var4);
				var7 = true;
				if(var1.getBlockId(var2, var3 + 1, var4) == this.blockID) {
					var1.setBlockToAir(var2, var3 + 1, var4);
				}
			}

			if(var7) {
				if(!var1.isRemote) {
					this.dropBlockAsItem(var1, var2, var3, var4, var6, 0);
				}
			} else {
				boolean var8 = var1.isBlockIndirectlyGettingPowered(var2, var3, var4) || var1.isBlockIndirectlyGettingPowered(var2, var3 + 1, var4);
				if((var8 || var5 > 0 && Block.blocksList[var5].canProvidePower()) && var5 != this.blockID) {
					this.onPoweredBlockChange(var1, var2, var3, var4, var8);
				}
			}
		} else {
			if(var1.getBlockId(var2, var3 - 1, var4) != this.blockID) {
				var1.setBlockToAir(var2, var3, var4);
			}

			if(var5 > 0 && var5 != this.blockID) {
				this.onNeighborBlockChange(var1, var2, var3 - 1, var4, var5);
			}
		}

	}

	public int idDropped(int var1, Random var2, int var3) {
		return (var1 & 8) != 0 ? 0 : (this.blockMaterial == Material.iron ? Item.doorIron.itemID : Item.doorWood.itemID);
	}

	public MovingObjectPosition collisionRayTrace(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		return super.collisionRayTrace(var1, var2, var3, var4, var5, var6);
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var3 >= 255 ? false : var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4) && super.canPlaceBlockAt(var1, var2, var3, var4) && super.canPlaceBlockAt(var1, var2, var3 + 1, var4);
	}

	public int getMobilityFlag() {
		return 1;
	}

	public int getFullMetadata(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		boolean var6 = (var5 & 8) != 0;
		int var7;
		int var8;
		if(var6) {
			var7 = var1.getBlockMetadata(var2, var3 - 1, var4);
			var8 = var5;
		} else {
			var7 = var5;
			var8 = var1.getBlockMetadata(var2, var3 + 1, var4);
		}

		boolean var9 = (var8 & 1) != 0;
		return var7 & 7 | (var6 ? 8 : 0) | (var9 ? 16 : 0);
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return this.blockMaterial == Material.iron ? Item.doorIron.itemID : Item.doorWood.itemID;
	}

	public void onBlockHarvested(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
		if(var6.capabilities.isCreativeMode && (var5 & 8) != 0 && var1.getBlockId(var2, var3 - 1, var4) == this.blockID) {
			var1.setBlockToAir(var2, var3 - 1, var4);
		}

	}
}
