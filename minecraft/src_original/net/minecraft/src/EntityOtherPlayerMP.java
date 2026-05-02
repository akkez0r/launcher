package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityOtherPlayerMP extends EntityPlayer {
	private boolean isItemInUse = false;
	private int otherPlayerMPPosRotationIncrements;
	private double otherPlayerMPX;
	private double otherPlayerMPY;
	private double otherPlayerMPZ;
	private double otherPlayerMPYaw;
	private double otherPlayerMPPitch;

	public EntityOtherPlayerMP(World var1, String var2) {
		super(var1);
		this.username = var2;
		this.yOffset = 0.0F;
		this.stepHeight = 0.0F;
		if(var2 != null && var2.length() > 0) {
			this.skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(var2) + ".png";
		}

		this.noClip = true;
		this.field_71082_cx = 0.25F;
		this.renderDistanceWeight = 10.0D;
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		return true;
	}

	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.otherPlayerMPX = var1;
		this.otherPlayerMPY = var3;
		this.otherPlayerMPZ = var5;
		this.otherPlayerMPYaw = (double)var7;
		this.otherPlayerMPPitch = (double)var8;
		this.otherPlayerMPPosRotationIncrements = var9;
	}

	public void updateCloak() {
		this.cloakUrl = "http://skins.minecraft.net/MinecraftCloaks/" + StringUtils.stripControlCodes(this.username) + ".png";
	}

	public void onUpdate() {
		this.field_71082_cx = 0.0F;
		super.onUpdate();
		this.prevLimbYaw = this.limbYaw;
		double var1 = this.posX - this.prevPosX;
		double var3 = this.posZ - this.prevPosZ;
		float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3) * 4.0F;
		if(var5 > 1.0F) {
			var5 = 1.0F;
		}

		this.limbYaw += (var5 - this.limbYaw) * 0.4F;
		this.limbSwing += this.limbYaw;
		if(!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
			ItemStack var6 = this.inventory.mainInventory[this.inventory.currentItem];
			this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], Item.itemsList[var6.itemID].getMaxItemUseDuration(var6));
			this.isItemInUse = true;
		} else if(this.isItemInUse && !this.isEating()) {
			this.clearItemInUse();
			this.isItemInUse = false;
		}

	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void onLivingUpdate() {
		super.updateEntityActionState();
		if(this.otherPlayerMPPosRotationIncrements > 0) {
			double var1 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
			double var3 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
			double var5 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;

			double var7;
			for(var7 = this.otherPlayerMPYaw - (double)this.rotationYaw; var7 < -180.0D; var7 += 360.0D) {
			}

			while(var7 >= 180.0D) {
				var7 -= 360.0D;
			}

			this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.otherPlayerMPPosRotationIncrements);
			this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
			--this.otherPlayerMPPosRotationIncrements;
			this.setPosition(var1, var3, var5);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		this.prevCameraYaw = this.cameraYaw;
		float var9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float var2 = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
		if(var9 > 0.1F) {
			var9 = 0.1F;
		}

		if(!this.onGround || this.getHealth() <= 0) {
			var9 = 0.0F;
		}

		if(this.onGround || this.getHealth() <= 0) {
			var2 = 0.0F;
		}

		this.cameraYaw += (var9 - this.cameraYaw) * 0.4F;
		this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
	}

	public void setCurrentItemOrArmor(int var1, ItemStack var2) {
		if(var1 == 0) {
			this.inventory.mainInventory[this.inventory.currentItem] = var2;
		} else {
			this.inventory.armorInventory[var1 - 1] = var2;
		}

	}

	public float getEyeHeight() {
		return 1.82F;
	}

	public void sendChatToPlayer(String var1) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(var1);
	}

	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return false;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
	}
}
