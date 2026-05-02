package net.minecraft.src;

public class ItemInWorldManager {
	public World theWorld;
	public EntityPlayerMP thisPlayerMP;
	private EnumGameType gameType = EnumGameType.NOT_SET;
	private boolean isDestroyingBlock;
	private int initialDamage;
	private int partiallyDestroyedBlockX;
	private int partiallyDestroyedBlockY;
	private int partiallyDestroyedBlockZ;
	private int curblockDamage;
	private boolean receivedFinishDiggingPacket;
	private int posX;
	private int posY;
	private int posZ;
	private int field_73093_n;
	private int durabilityRemainingOnBlock = -1;

	public ItemInWorldManager(World var1) {
		this.theWorld = var1;
	}

	public void setGameType(EnumGameType var1) {
		this.gameType = var1;
		var1.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
		this.thisPlayerMP.sendPlayerAbilities();
	}

	public EnumGameType getGameType() {
		return this.gameType;
	}

	public boolean isCreative() {
		return this.gameType.isCreative();
	}

	public void initializeGameType(EnumGameType var1) {
		if(this.gameType == EnumGameType.NOT_SET) {
			this.gameType = var1;
		}

		this.setGameType(this.gameType);
	}

	public void updateBlockRemoving() {
		++this.curblockDamage;
		int var1;
		float var4;
		int var5;
		if(this.receivedFinishDiggingPacket) {
			var1 = this.curblockDamage - this.field_73093_n;
			int var2 = this.theWorld.getBlockId(this.posX, this.posY, this.posZ);
			if(var2 == 0) {
				this.receivedFinishDiggingPacket = false;
			} else {
				Block var3 = Block.blocksList[var2];
				var4 = var3.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.posX, this.posY, this.posZ) * (float)(var1 + 1);
				var5 = (int)(var4 * 10.0F);
				if(var5 != this.durabilityRemainingOnBlock) {
					this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.posX, this.posY, this.posZ, var5);
					this.durabilityRemainingOnBlock = var5;
				}

				if(var4 >= 1.0F) {
					this.receivedFinishDiggingPacket = false;
					this.tryHarvestBlock(this.posX, this.posY, this.posZ);
				}
			}
		} else if(this.isDestroyingBlock) {
			var1 = this.theWorld.getBlockId(this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ);
			Block var6 = Block.blocksList[var1];
			if(var6 == null) {
				this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
				this.durabilityRemainingOnBlock = -1;
				this.isDestroyingBlock = false;
			} else {
				int var7 = this.curblockDamage - this.initialDamage;
				var4 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ) * (float)(var7 + 1);
				var5 = (int)(var4 * 10.0F);
				if(var5 != this.durabilityRemainingOnBlock) {
					this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, var5);
					this.durabilityRemainingOnBlock = var5;
				}
			}
		}

	}

	public void onBlockClicked(int var1, int var2, int var3, int var4) {
		if(!this.gameType.isAdventure() || this.thisPlayerMP.canCurrentToolHarvestBlock(var1, var2, var3)) {
			if(this.isCreative()) {
				if(!this.theWorld.extinguishFire((EntityPlayer)null, var1, var2, var3, var4)) {
					this.tryHarvestBlock(var1, var2, var3);
				}

			} else {
				this.theWorld.extinguishFire((EntityPlayer)null, var1, var2, var3, var4);
				this.initialDamage = this.curblockDamage;
				float var5 = 1.0F;
				int var6 = this.theWorld.getBlockId(var1, var2, var3);
				if(var6 > 0) {
					Block.blocksList[var6].onBlockClicked(this.theWorld, var1, var2, var3, this.thisPlayerMP);
					var5 = Block.blocksList[var6].getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, var1, var2, var3);
				}

				if(var6 > 0 && var5 >= 1.0F) {
					this.tryHarvestBlock(var1, var2, var3);
				} else {
					this.isDestroyingBlock = true;
					this.partiallyDestroyedBlockX = var1;
					this.partiallyDestroyedBlockY = var2;
					this.partiallyDestroyedBlockZ = var3;
					int var7 = (int)(var5 * 10.0F);
					this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, var1, var2, var3, var7);
					this.durabilityRemainingOnBlock = var7;
				}

			}
		}
	}

	public void uncheckedTryHarvestBlock(int var1, int var2, int var3) {
		if(var1 == this.partiallyDestroyedBlockX && var2 == this.partiallyDestroyedBlockY && var3 == this.partiallyDestroyedBlockZ) {
			int var4 = this.curblockDamage - this.initialDamage;
			int var5 = this.theWorld.getBlockId(var1, var2, var3);
			if(var5 != 0) {
				Block var6 = Block.blocksList[var5];
				float var7 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, var1, var2, var3) * (float)(var4 + 1);
				if(var7 >= 0.7F) {
					this.isDestroyingBlock = false;
					this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, var1, var2, var3, -1);
					this.tryHarvestBlock(var1, var2, var3);
				} else if(!this.receivedFinishDiggingPacket) {
					this.isDestroyingBlock = false;
					this.receivedFinishDiggingPacket = true;
					this.posX = var1;
					this.posY = var2;
					this.posZ = var3;
					this.field_73093_n = this.initialDamage;
				}
			}
		}

	}

	public void cancelDestroyingBlock(int var1, int var2, int var3) {
		this.isDestroyingBlock = false;
		this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
	}

	private boolean removeBlock(int var1, int var2, int var3) {
		Block var4 = Block.blocksList[this.theWorld.getBlockId(var1, var2, var3)];
		int var5 = this.theWorld.getBlockMetadata(var1, var2, var3);
		if(var4 != null) {
			var4.onBlockHarvested(this.theWorld, var1, var2, var3, var5, this.thisPlayerMP);
		}

		boolean var6 = this.theWorld.setBlockToAir(var1, var2, var3);
		if(var4 != null && var6) {
			var4.onBlockDestroyedByPlayer(this.theWorld, var1, var2, var3, var5);
		}

		return var6;
	}

	public boolean tryHarvestBlock(int var1, int var2, int var3) {
		if(this.gameType.isAdventure() && !this.thisPlayerMP.canCurrentToolHarvestBlock(var1, var2, var3)) {
			return false;
		} else {
			int var4 = this.theWorld.getBlockId(var1, var2, var3);
			int var5 = this.theWorld.getBlockMetadata(var1, var2, var3);
			this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, var1, var2, var3, var4 + (this.theWorld.getBlockMetadata(var1, var2, var3) << 12));
			boolean var6 = this.removeBlock(var1, var2, var3);
			if(this.isCreative()) {
				this.thisPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var1, var2, var3, this.theWorld));
			} else {
				ItemStack var7 = this.thisPlayerMP.getCurrentEquippedItem();
				boolean var8 = this.thisPlayerMP.canHarvestBlock(Block.blocksList[var4]);
				if(var7 != null) {
					var7.onBlockDestroyed(this.theWorld, var4, var1, var2, var3, this.thisPlayerMP);
					if(var7.stackSize == 0) {
						this.thisPlayerMP.destroyCurrentEquippedItem();
					}
				}

				if(var6 && var8) {
					Block.blocksList[var4].harvestBlock(this.theWorld, this.thisPlayerMP, var1, var2, var3, var5);
				}
			}

			return var6;
		}
	}

	public boolean tryUseItem(EntityPlayer var1, World var2, ItemStack var3) {
		int var4 = var3.stackSize;
		int var5 = var3.getItemDamage();
		ItemStack var6 = var3.useItemRightClick(var2, var1);
		if(var6 != var3 || var6 != null && (var6.stackSize != var4 || var6.getMaxItemUseDuration() > 0 || var6.getItemDamage() != var5)) {
			var1.inventory.mainInventory[var1.inventory.currentItem] = var6;
			if(this.isCreative()) {
				var6.stackSize = var4;
				if(var6.isItemStackDamageable()) {
					var6.setItemDamage(var5);
				}
			}

			if(var6.stackSize == 0) {
				var1.inventory.mainInventory[var1.inventory.currentItem] = null;
			}

			if(!var1.isUsingItem()) {
				((EntityPlayerMP)var1).sendContainerToPlayer(var1.inventoryContainer);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean activateBlockOrUseItem(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		int var11;
		if(!var1.isSneaking() || var1.getHeldItem() == null) {
			var11 = var2.getBlockId(var4, var5, var6);
			if(var11 > 0 && Block.blocksList[var11].onBlockActivated(var2, var4, var5, var6, var1, var7, var8, var9, var10)) {
				return true;
			}
		}

		if(var3 == null) {
			return false;
		} else if(this.isCreative()) {
			var11 = var3.getItemDamage();
			int var12 = var3.stackSize;
			boolean var13 = var3.tryPlaceItemIntoWorld(var1, var2, var4, var5, var6, var7, var8, var9, var10);
			var3.setItemDamage(var11);
			var3.stackSize = var12;
			return var13;
		} else {
			return var3.tryPlaceItemIntoWorld(var1, var2, var4, var5, var6, var7, var8, var9, var10);
		}
	}

	public void setWorld(WorldServer var1) {
		this.theWorld = var1;
	}
}
