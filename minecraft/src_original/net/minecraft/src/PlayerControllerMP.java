package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerControllerMP {
	private final Minecraft mc;
	private final NetClientHandler netClientHandler;
	private int currentBlockX = -1;
	private int currentBlockY = -1;
	private int currentblockZ = -1;
	private ItemStack field_85183_f = null;
	private float curBlockDamageMP = 0.0F;
	private float stepSoundTickCounter = 0.0F;
	private int blockHitDelay = 0;
	private boolean isHittingBlock = false;
	private EnumGameType currentGameType = EnumGameType.SURVIVAL;
	private int currentPlayerItem = 0;

	public PlayerControllerMP(Minecraft var1, NetClientHandler var2) {
		this.mc = var1;
		this.netClientHandler = var2;
	}

	public static void clickBlockCreative(Minecraft var0, PlayerControllerMP var1, int var2, int var3, int var4, int var5) {
		if(!var0.theWorld.extinguishFire(var0.thePlayer, var2, var3, var4, var5)) {
			var1.onPlayerDestroyBlock(var2, var3, var4, var5);
		}

	}

	public void setPlayerCapabilities(EntityPlayer var1) {
		this.currentGameType.configurePlayerCapabilities(var1.capabilities);
	}

	public boolean enableEverythingIsScrewedUpMode() {
		return false;
	}

	public void setGameType(EnumGameType var1) {
		this.currentGameType = var1;
		this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
	}

	public void flipPlayer(EntityPlayer var1) {
		var1.rotationYaw = -180.0F;
	}

	public boolean shouldDrawHUD() {
		return this.currentGameType.isSurvivalOrAdventure();
	}

	public boolean onPlayerDestroyBlock(int var1, int var2, int var3, int var4) {
		if(this.currentGameType.isAdventure() && !this.mc.thePlayer.canCurrentToolHarvestBlock(var1, var2, var3)) {
			return false;
		} else {
			WorldClient var5 = this.mc.theWorld;
			Block var6 = Block.blocksList[var5.getBlockId(var1, var2, var3)];
			if(var6 == null) {
				return false;
			} else {
				var5.playAuxSFX(2001, var1, var2, var3, var6.blockID + (var5.getBlockMetadata(var1, var2, var3) << 12));
				int var7 = var5.getBlockMetadata(var1, var2, var3);
				boolean var8 = var5.setBlockToAir(var1, var2, var3);
				if(var8) {
					var6.onBlockDestroyedByPlayer(var5, var1, var2, var3, var7);
				}

				this.currentBlockY = -1;
				if(!this.currentGameType.isCreative()) {
					ItemStack var9 = this.mc.thePlayer.getCurrentEquippedItem();
					if(var9 != null) {
						var9.onBlockDestroyed(var5, var6.blockID, var1, var2, var3, this.mc.thePlayer);
						if(var9.stackSize == 0) {
							this.mc.thePlayer.destroyCurrentEquippedItem();
						}
					}
				}

				return var8;
			}
		}
	}

	public void clickBlock(int var1, int var2, int var3, int var4) {
		if(!this.currentGameType.isAdventure() || this.mc.thePlayer.canCurrentToolHarvestBlock(var1, var2, var3)) {
			if(this.currentGameType.isCreative()) {
				this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4));
				clickBlockCreative(this.mc, this, var1, var2, var3, var4);
				this.blockHitDelay = 5;
			} else if(!this.isHittingBlock || !this.sameToolAndBlock(var1, var2, var3)) {
				if(this.isHittingBlock) {
					this.netClientHandler.addToSendQueue(new Packet14BlockDig(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, var4));
				}

				this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4));
				int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
				if(var5 > 0 && this.curBlockDamageMP == 0.0F) {
					Block.blocksList[var5].onBlockClicked(this.mc.theWorld, var1, var2, var3, this.mc.thePlayer);
				}

				if(var5 > 0 && Block.blocksList[var5].getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, var1, var2, var3) >= 1.0F) {
					this.onPlayerDestroyBlock(var1, var2, var3, var4);
				} else {
					this.isHittingBlock = true;
					this.currentBlockX = var1;
					this.currentBlockY = var2;
					this.currentblockZ = var3;
					this.field_85183_f = this.mc.thePlayer.getHeldItem();
					this.curBlockDamageMP = 0.0F;
					this.stepSoundTickCounter = 0.0F;
					this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
				}
			}

		}
	}

	public void resetBlockRemoving() {
		if(this.isHittingBlock) {
			this.netClientHandler.addToSendQueue(new Packet14BlockDig(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, -1));
		}

		this.isHittingBlock = false;
		this.curBlockDamageMP = 0.0F;
		this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, -1);
	}

	public void onPlayerDamageBlock(int var1, int var2, int var3, int var4) {
		this.syncCurrentPlayItem();
		if(this.blockHitDelay > 0) {
			--this.blockHitDelay;
		} else if(this.currentGameType.isCreative()) {
			this.blockHitDelay = 5;
			this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4));
			clickBlockCreative(this.mc, this, var1, var2, var3, var4);
		} else {
			if(this.sameToolAndBlock(var1, var2, var3)) {
				int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
				if(var5 == 0) {
					this.isHittingBlock = false;
					return;
				}

				Block var6 = Block.blocksList[var5];
				this.curBlockDamageMP += var6.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, var1, var2, var3);
				if(this.stepSoundTickCounter % 4.0F == 0.0F && var6 != null) {
					this.mc.sndManager.playSound(var6.stepSound.getStepSound(), (float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3 + 0.5F, (var6.stepSound.getVolume() + 1.0F) / 8.0F, var6.stepSound.getPitch() * 0.5F);
				}

				++this.stepSoundTickCounter;
				if(this.curBlockDamageMP >= 1.0F) {
					this.isHittingBlock = false;
					this.netClientHandler.addToSendQueue(new Packet14BlockDig(2, var1, var2, var3, var4));
					this.onPlayerDestroyBlock(var1, var2, var3, var4);
					this.curBlockDamageMP = 0.0F;
					this.stepSoundTickCounter = 0.0F;
					this.blockHitDelay = 5;
				}

				this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.entityId, this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
			} else {
				this.clickBlock(var1, var2, var3, var4);
			}

		}
	}

	public float getBlockReachDistance() {
		return this.currentGameType.isCreative() ? 5.0F : 4.5F;
	}

	public void updateController() {
		this.syncCurrentPlayItem();
		this.mc.sndManager.playRandomMusicIfReady();
	}

	private boolean sameToolAndBlock(int var1, int var2, int var3) {
		ItemStack var4 = this.mc.thePlayer.getHeldItem();
		boolean var5 = this.field_85183_f == null && var4 == null;
		if(this.field_85183_f != null && var4 != null) {
			var5 = var4.itemID == this.field_85183_f.itemID && ItemStack.areItemStackTagsEqual(var4, this.field_85183_f) && (var4.isItemStackDamageable() || var4.getItemDamage() == this.field_85183_f.getItemDamage());
		}

		return var1 == this.currentBlockX && var2 == this.currentBlockY && var3 == this.currentblockZ && var5;
	}

	private void syncCurrentPlayItem() {
		int var1 = this.mc.thePlayer.inventory.currentItem;
		if(var1 != this.currentPlayerItem) {
			this.currentPlayerItem = var1;
			this.netClientHandler.addToSendQueue(new Packet16BlockItemSwitch(this.currentPlayerItem));
		}

	}

	public boolean onPlayerRightClick(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, Vec3 var8) {
		this.syncCurrentPlayItem();
		float var9 = (float)var8.xCoord - (float)var4;
		float var10 = (float)var8.yCoord - (float)var5;
		float var11 = (float)var8.zCoord - (float)var6;
		boolean var12 = false;
		int var13;
		if(!var1.isSneaking() || var1.getHeldItem() == null) {
			var13 = var2.getBlockId(var4, var5, var6);
			if(var13 > 0 && Block.blocksList[var13].onBlockActivated(var2, var4, var5, var6, var1, var7, var9, var10, var11)) {
				var12 = true;
			}
		}

		if(!var12 && var3 != null && var3.getItem() instanceof ItemBlock) {
			ItemBlock var16 = (ItemBlock)var3.getItem();
			if(!var16.canPlaceItemBlockOnSide(var2, var4, var5, var6, var7, var1, var3)) {
				return false;
			}
		}

		this.netClientHandler.addToSendQueue(new Packet15Place(var4, var5, var6, var7, var1.inventory.getCurrentItem(), var9, var10, var11));
		if(var12) {
			return true;
		} else if(var3 == null) {
			return false;
		} else if(this.currentGameType.isCreative()) {
			var13 = var3.getItemDamage();
			int var14 = var3.stackSize;
			boolean var15 = var3.tryPlaceItemIntoWorld(var1, var2, var4, var5, var6, var7, var9, var10, var11);
			var3.setItemDamage(var13);
			var3.stackSize = var14;
			return var15;
		} else {
			return var3.tryPlaceItemIntoWorld(var1, var2, var4, var5, var6, var7, var9, var10, var11);
		}
	}

	public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet15Place(-1, -1, -1, 255, var1.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
		int var4 = var3.stackSize;
		ItemStack var5 = var3.useItemRightClick(var2, var1);
		if(var5 != var3 || var5 != null && var5.stackSize != var4) {
			var1.inventory.mainInventory[var1.inventory.currentItem] = var5;
			if(var5.stackSize == 0) {
				var1.inventory.mainInventory[var1.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public EntityClientPlayerMP func_78754_a(World var1) {
		return new EntityClientPlayerMP(this.mc, var1, this.mc.session, this.netClientHandler);
	}

	public void attackEntity(EntityPlayer var1, Entity var2) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet7UseEntity(var1.entityId, var2.entityId, 1));
		var1.attackTargetEntityWithCurrentItem(var2);
	}

	public boolean func_78768_b(EntityPlayer var1, Entity var2) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet7UseEntity(var1.entityId, var2.entityId, 0));
		return var1.interactWith(var2);
	}

	public ItemStack windowClick(int var1, int var2, int var3, int var4, EntityPlayer var5) {
		short var6 = var5.openContainer.getNextTransactionID(var5.inventory);
		ItemStack var7 = var5.openContainer.slotClick(var2, var3, var4, var5);
		this.netClientHandler.addToSendQueue(new Packet102WindowClick(var1, var2, var3, var4, var7, var6));
		return var7;
	}

	public void sendEnchantPacket(int var1, int var2) {
		this.netClientHandler.addToSendQueue(new Packet108EnchantItem(var1, var2));
	}

	public void sendSlotPacket(ItemStack var1, int var2) {
		if(this.currentGameType.isCreative()) {
			this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(var2, var1));
		}

	}

	public void func_78752_a(ItemStack var1) {
		if(this.currentGameType.isCreative() && var1 != null) {
			this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(-1, var1));
		}

	}

	public void onStoppedUsingItem(EntityPlayer var1) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet14BlockDig(5, 0, 0, 0, 255));
		var1.stopUsingItem();
	}

	public boolean func_78763_f() {
		return true;
	}

	public boolean isNotCreative() {
		return !this.currentGameType.isCreative();
	}

	public boolean isInCreativeMode() {
		return this.currentGameType.isCreative();
	}

	public boolean extendedReach() {
		return this.currentGameType.isCreative();
	}
}
