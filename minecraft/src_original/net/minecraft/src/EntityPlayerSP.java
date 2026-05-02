package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityPlayerSP extends EntityPlayer {
	public MovementInput movementInput;
	protected Minecraft mc;
	protected int sprintToggleTimer = 0;
	public int sprintingTicksLeft = 0;
	public float renderArmYaw;
	public float renderArmPitch;
	public float prevRenderArmYaw;
	public float prevRenderArmPitch;
	private MouseFilter field_71162_ch = new MouseFilter();
	private MouseFilter field_71160_ci = new MouseFilter();
	private MouseFilter field_71161_cj = new MouseFilter();
	public float timeInPortal;
	public float prevTimeInPortal;

	public EntityPlayerSP(Minecraft var1, World var2, Session var3, int var4) {
		super(var2);
		this.mc = var1;
		this.dimension = var4;
		if(var3 != null && var3.username != null && var3.username.length() > 0) {
			this.skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(var3.username) + ".png";
		}

		this.username = var3.username;
	}

	public void moveEntity(double var1, double var3, double var5) {
		super.moveEntity(var1, var3, var5);
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump;
		this.prevRenderArmYaw = this.renderArmYaw;
		this.prevRenderArmPitch = this.renderArmPitch;
		this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
		this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
	}

	protected boolean isClientWorld() {
		return true;
	}

	public void onLivingUpdate() {
		if(this.sprintingTicksLeft > 0) {
			--this.sprintingTicksLeft;
			if(this.sprintingTicksLeft == 0) {
				this.setSprinting(false);
			}
		}

		if(this.sprintToggleTimer > 0) {
			--this.sprintToggleTimer;
		}

		if(this.mc.playerController.enableEverythingIsScrewedUpMode()) {
			this.posX = this.posZ = 0.5D;
			this.posX = 0.0D;
			this.posZ = 0.0D;
			this.rotationYaw = (float)this.ticksExisted / 12.0F;
			this.rotationPitch = 10.0F;
			this.posY = 68.5D;
		} else {
			if(!this.mc.statFileWriter.hasAchievementUnlocked(AchievementList.openInventory)) {
				this.mc.guiAchievement.queueAchievementInformation(AchievementList.openInventory);
			}

			this.prevTimeInPortal = this.timeInPortal;
			if(this.inPortal) {
				if(this.mc.currentScreen != null) {
					this.mc.displayGuiScreen((GuiScreen)null);
				}

				if(this.timeInPortal == 0.0F) {
					this.mc.sndManager.playSoundFX("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
				}

				this.timeInPortal += 0.0125F;
				if(this.timeInPortal >= 1.0F) {
					this.timeInPortal = 1.0F;
				}

				this.inPortal = false;
			} else if(this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
				this.timeInPortal += 2.0F / 3.0F * 0.01F;
				if(this.timeInPortal > 1.0F) {
					this.timeInPortal = 1.0F;
				}
			} else {
				if(this.timeInPortal > 0.0F) {
					this.timeInPortal -= 0.05F;
				}

				if(this.timeInPortal < 0.0F) {
					this.timeInPortal = 0.0F;
				}
			}

			if(this.timeUntilPortal > 0) {
				--this.timeUntilPortal;
			}

			boolean var1 = this.movementInput.jump;
			float var2 = 0.8F;
			boolean var3 = this.movementInput.moveForward >= var2;
			this.movementInput.updatePlayerMoveState();
			if(this.isUsingItem()) {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}

			if(this.movementInput.sneak && this.ySize < 0.2F) {
				this.ySize = 0.2F;
			}

			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			boolean var4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
			if(this.onGround && !var3 && this.movementInput.moveForward >= var2 && !this.isSprinting() && var4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
				if(this.sprintToggleTimer == 0) {
					this.sprintToggleTimer = 7;
				} else {
					this.setSprinting(true);
					this.sprintToggleTimer = 0;
				}
			}

			if(this.isSneaking()) {
				this.sprintToggleTimer = 0;
			}

			if(this.isSprinting() && (this.movementInput.moveForward < var2 || this.isCollidedHorizontally || !var4)) {
				this.setSprinting(false);
			}

			if(this.capabilities.allowFlying && !var1 && this.movementInput.jump) {
				if(this.flyToggleTimer == 0) {
					this.flyToggleTimer = 7;
				} else {
					this.capabilities.isFlying = !this.capabilities.isFlying;
					this.sendPlayerAbilities();
					this.flyToggleTimer = 0;
				}
			}

			if(this.capabilities.isFlying) {
				if(this.movementInput.sneak) {
					this.motionY -= 0.15D;
				}

				if(this.movementInput.jump) {
					this.motionY += 0.15D;
				}
			}

			super.onLivingUpdate();
			if(this.onGround && this.capabilities.isFlying) {
				this.capabilities.isFlying = false;
				this.sendPlayerAbilities();
			}

		}
	}

	public float getFOVMultiplier() {
		float var1 = 1.0F;
		if(this.capabilities.isFlying) {
			var1 *= 1.1F;
		}

		var1 *= (this.landMovementFactor * this.getSpeedModifier() / this.speedOnGround + 1.0F) / 2.0F;
		if(this.isUsingItem() && this.getItemInUse().itemID == Item.bow.itemID) {
			int var2 = this.getItemInUseDuration();
			float var3 = (float)var2 / 20.0F;
			if(var3 > 1.0F) {
				var3 = 1.0F;
			} else {
				var3 *= var3;
			}

			var1 *= 1.0F - var3 * 0.15F;
		}

		return var1;
	}

	public void updateCloak() {
		this.cloakUrl = "http://skins.minecraft.net/MinecraftCloaks/" + StringUtils.stripControlCodes(this.username) + ".png";
	}

	public void closeScreen() {
		super.closeScreen();
		this.mc.displayGuiScreen((GuiScreen)null);
	}

	public void displayGUIEditSign(TileEntity var1) {
		if(var1 instanceof TileEntitySign) {
			this.mc.displayGuiScreen(new GuiEditSign((TileEntitySign)var1));
		} else if(var1 instanceof TileEntityCommandBlock) {
			this.mc.displayGuiScreen(new GuiCommandBlock((TileEntityCommandBlock)var1));
		}

	}

	public void displayGUIBook(ItemStack var1) {
		Item var2 = var1.getItem();
		if(var2 == Item.writtenBook) {
			this.mc.displayGuiScreen(new GuiScreenBook(this, var1, false));
		} else if(var2 == Item.writableBook) {
			this.mc.displayGuiScreen(new GuiScreenBook(this, var1, true));
		}

	}

	public void displayGUIChest(IInventory var1) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
	}

	public void displayGUIHopper(TileEntityHopper var1) {
		this.mc.displayGuiScreen(new GuiHopper(this.inventory, var1));
	}

	public void displayGUIHopperMinecart(EntityMinecartHopper var1) {
		this.mc.displayGuiScreen(new GuiHopper(this.inventory, var1));
	}

	public void displayGUIWorkbench(int var1, int var2, int var3) {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, var1, var2, var3));
	}

	public void displayGUIEnchantment(int var1, int var2, int var3, String var4) {
		this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, var1, var2, var3, var4));
	}

	public void displayGUIAnvil(int var1, int var2, int var3) {
		this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj, var1, var2, var3));
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
	}

	public void displayGUIBrewingStand(TileEntityBrewingStand var1) {
		this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, var1));
	}

	public void displayGUIBeacon(TileEntityBeacon var1) {
		this.mc.displayGuiScreen(new GuiBeacon(this.inventory, var1));
	}

	public void displayGUIDispenser(TileEntityDispenser var1) {
		this.mc.displayGuiScreen(new GuiDispenser(this.inventory, var1));
	}

	public void displayGUIMerchant(IMerchant var1, String var2) {
		this.mc.displayGuiScreen(new GuiMerchant(this.inventory, var1, this.worldObj, var2));
	}

	public void onCriticalHit(Entity var1) {
		this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, var1));
	}

	public void onEnchantmentCritical(Entity var1) {
		EntityCrit2FX var2 = new EntityCrit2FX(this.mc.theWorld, var1, "magicCrit");
		this.mc.effectRenderer.addEffect(var2);
	}

	public void onItemPickup(Entity var1, int var2) {
		this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
	}

	public boolean isSneaking() {
		return this.movementInput.sneak && !this.sleeping;
	}

	public void setHealth(int var1) {
		int var2 = this.getHealth() - var1;
		if(var2 <= 0) {
			this.setEntityHealth(var1);
			if(var2 < 0) {
				this.hurtResistantTime = this.maxHurtResistantTime / 2;
			}
		} else {
			this.lastDamage = var2;
			this.setEntityHealth(this.getHealth());
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.damageEntity(DamageSource.generic, var2);
			this.hurtTime = this.maxHurtTime = 10;
		}

	}

	public void addChatMessage(String var1) {
		this.mc.ingameGUI.getChatGUI().addTranslatedMessage(var1, new Object[0]);
	}

	public void addStat(StatBase var1, int var2) {
		if(var1 != null) {
			if(var1.isAchievement()) {
				Achievement var3 = (Achievement)var1;
				if(var3.parentAchievement == null || this.mc.statFileWriter.hasAchievementUnlocked(var3.parentAchievement)) {
					if(!this.mc.statFileWriter.hasAchievementUnlocked(var3)) {
						this.mc.guiAchievement.queueTakenAchievement(var3);
					}

					this.mc.statFileWriter.readStat(var1, var2);
				}
			} else {
				this.mc.statFileWriter.readStat(var1, var2);
			}

		}
	}

	private boolean isBlockTranslucent(int var1, int var2, int var3) {
		return this.worldObj.isBlockNormalCube(var1, var2, var3);
	}

	protected boolean pushOutOfBlocks(double var1, double var3, double var5) {
		int var7 = MathHelper.floor_double(var1);
		int var8 = MathHelper.floor_double(var3);
		int var9 = MathHelper.floor_double(var5);
		double var10 = var1 - (double)var7;
		double var12 = var5 - (double)var9;
		if(this.isBlockTranslucent(var7, var8, var9) || this.isBlockTranslucent(var7, var8 + 1, var9)) {
			boolean var14 = !this.isBlockTranslucent(var7 - 1, var8, var9) && !this.isBlockTranslucent(var7 - 1, var8 + 1, var9);
			boolean var15 = !this.isBlockTranslucent(var7 + 1, var8, var9) && !this.isBlockTranslucent(var7 + 1, var8 + 1, var9);
			boolean var16 = !this.isBlockTranslucent(var7, var8, var9 - 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 - 1);
			boolean var17 = !this.isBlockTranslucent(var7, var8, var9 + 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 + 1);
			byte var18 = -1;
			double var19 = 9999.0D;
			if(var14 && var10 < var19) {
				var19 = var10;
				var18 = 0;
			}

			if(var15 && 1.0D - var10 < var19) {
				var19 = 1.0D - var10;
				var18 = 1;
			}

			if(var16 && var12 < var19) {
				var19 = var12;
				var18 = 4;
			}

			if(var17 && 1.0D - var12 < var19) {
				var19 = 1.0D - var12;
				var18 = 5;
			}

			float var21 = 0.1F;
			if(var18 == 0) {
				this.motionX = (double)(-var21);
			}

			if(var18 == 1) {
				this.motionX = (double)var21;
			}

			if(var18 == 4) {
				this.motionZ = (double)(-var21);
			}

			if(var18 == 5) {
				this.motionZ = (double)var21;
			}
		}

		return false;
	}

	public void setSprinting(boolean var1) {
		super.setSprinting(var1);
		this.sprintingTicksLeft = var1 ? 600 : 0;
	}

	public void setXPStats(float var1, int var2, int var3) {
		this.experience = var1;
		this.experienceTotal = var2;
		this.experienceLevel = var3;
	}

	public void sendChatToPlayer(String var1) {
		this.mc.ingameGUI.getChatGUI().printChatMessage(var1);
	}

	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return var1 <= 0;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
	}

	public ItemStack getHeldItem() {
		return this.inventory.getCurrentItem();
	}

	public void playSound(String var1, float var2, float var3) {
		this.worldObj.playSound(this.posX, this.posY - (double)this.yOffset, this.posZ, var1, var2, var3, false);
	}
}
