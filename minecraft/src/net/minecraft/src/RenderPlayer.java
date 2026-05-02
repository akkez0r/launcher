package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderPlayer extends RenderLiving {
	private ModelBiped modelBipedMain = (ModelBiped)this.mainModel;
	private ModelBiped modelArmorChestplate = new ModelBiped(1.0F);
	private ModelBiped modelArmor = new ModelBiped(0.5F);
	private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
	}

	protected void func_98191_a(EntityPlayer var1) {
		this.loadDownloadableImageTexture(var1.skinUrl, var1.getTexture());
	}

	protected int setArmorModel(EntityPlayer var1, int var2, float var3) {
		ItemStack var4 = var1.inventory.armorItemInSlot(3 - var2);
		if(var4 != null) {
			Item var5 = var4.getItem();
			if(var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + ".png");
				ModelBiped var7 = var2 == 2 ? this.modelArmor : this.modelArmorChestplate;
				var7.bipedHead.showModel = var2 == 0;
				var7.bipedHeadwear.showModel = var2 == 0;
				var7.bipedBody.showModel = var2 == 1 || var2 == 2;
				var7.bipedRightArm.showModel = var2 == 1;
				var7.bipedLeftArm.showModel = var2 == 1;
				var7.bipedRightLeg.showModel = var2 == 2 || var2 == 3;
				var7.bipedLeftLeg.showModel = var2 == 2 || var2 == 3;
				this.setRenderPassModel(var7);
				if(var7 != null) {
					var7.onGround = this.mainModel.onGround;
				}

				if(var7 != null) {
					var7.isRiding = this.mainModel.isRiding;
				}

				if(var7 != null) {
					var7.isChild = this.mainModel.isChild;
				}

				float var8 = 1.0F;
				if(var6.getArmorMaterial() == EnumArmorMaterial.CLOTH) {
					int var9 = var6.getColor(var4);
					float var10 = (float)(var9 >> 16 & 255) / 255.0F;
					float var11 = (float)(var9 >> 8 & 255) / 255.0F;
					float var12 = (float)(var9 & 255) / 255.0F;
					GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);
					if(var4.isItemEnchanted()) {
						return 31;
					}

					return 16;
				}

				GL11.glColor3f(var8, var8, var8);
				if(var4.isItemEnchanted()) {
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	protected void func_82439_b(EntityPlayer var1, int var2, float var3) {
		ItemStack var4 = var1.inventory.armorItemInSlot(3 - var2);
		if(var4 != null) {
			Item var5 = var4.getItem();
			if(var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + "_b.png");
				float var7 = 1.0F;
				GL11.glColor3f(var7, var7, var7);
			}
		}

	}

	public void renderPlayer(EntityPlayer var1, double var2, double var4, double var6, float var8, float var9) {
		float var10 = 1.0F;
		GL11.glColor3f(var10, var10, var10);
		ItemStack var11 = var1.inventory.getCurrentItem();
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = var11 != null ? 1 : 0;
		if(var11 != null && var1.getItemInUseCount() > 0) {
			EnumAction var12 = var11.getItemUseAction();
			if(var12 == EnumAction.block) {
				this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
			} else if(var12 == EnumAction.bow) {
				this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
			}
		}

		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = var1.isSneaking();
		double var14 = var4 - (double)var1.yOffset;
		if(var1.isSneaking() && !(var1 instanceof EntityPlayerSP)) {
			var14 -= 0.125D;
		}

		super.doRenderLiving(var1, var2, var14, var6, var8, var9);
		this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
	}

	protected void renderSpecials(EntityPlayer var1, float var2) {
		float var3 = 1.0F;
		GL11.glColor3f(var3, var3, var3);
		super.renderEquippedItems(var1, var2);
		super.renderArrowsStuckInEntity(var1, var2);
		ItemStack var4 = var1.inventory.armorItemInSlot(3);
		if(var4 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(1.0F / 16.0F);
			float var5;
			if(var4.getItem().itemID < 256) {
				if(RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType())) {
					var5 = 10.0F / 16.0F;
					GL11.glTranslatef(0.0F, -0.25F, 0.0F);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(var5, -var5, -var5);
				}

				this.renderManager.itemRenderer.renderItem(var1, var4, 0);
			} else if(var4.getItem().itemID == Item.skull.itemID) {
				var5 = 1.0625F;
				GL11.glScalef(var5, -var5, -var5);
				String var6 = "";
				if(var4.hasTagCompound() && var4.getTagCompound().hasKey("SkullOwner")) {
					var6 = var4.getTagCompound().getString("SkullOwner");
				}

				TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.getItemDamage(), var6);
			}

			GL11.glPopMatrix();
		}

		float var7;
		float var8;
		if(var1.username.equals("deadmau5") && this.loadDownloadableImageTexture(var1.skinUrl, (String)null)) {
			for(int var20 = 0; var20 < 2; ++var20) {
				float var23 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2 - (var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2);
				var7 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
				GL11.glPushMatrix();
				GL11.glRotatef(var23, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(var7, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(6.0F / 16.0F * (float)(var20 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -(6.0F / 16.0F), 0.0F);
				GL11.glRotatef(-var7, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-var23, 0.0F, 1.0F, 0.0F);
				var8 = 4.0F / 3.0F;
				GL11.glScalef(var8, var8, var8);
				this.modelBipedMain.renderEars(1.0F / 16.0F);
				GL11.glPopMatrix();
			}
		}

		float var11;
		if(this.loadDownloadableImageTexture(var1.cloakUrl, (String)null) && !var1.isInvisible() && !var1.getHideCape()) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 2.0F / 16.0F);
			double var21 = var1.field_71091_bM + (var1.field_71094_bP - var1.field_71091_bM) * (double)var2 - (var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2);
			double var24 = var1.field_71096_bN + (var1.field_71095_bQ - var1.field_71096_bN) * (double)var2 - (var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2);
			double var9 = var1.field_71097_bO + (var1.field_71085_bR - var1.field_71097_bO) * (double)var2 - (var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2);
			var11 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2;
			double var12 = (double)MathHelper.sin(var11 * (float)Math.PI / 180.0F);
			double var14 = (double)(-MathHelper.cos(var11 * (float)Math.PI / 180.0F));
			float var16 = (float)var24 * 10.0F;
			if(var16 < -6.0F) {
				var16 = -6.0F;
			}

			if(var16 > 32.0F) {
				var16 = 32.0F;
			}

			float var17 = (float)(var21 * var12 + var9 * var14) * 100.0F;
			float var18 = (float)(var21 * var14 - var9 * var12) * 100.0F;
			if(var17 < 0.0F) {
				var17 = 0.0F;
			}

			float var19 = var1.prevCameraYaw + (var1.cameraYaw - var1.prevCameraYaw) * var2;
			var16 += MathHelper.sin((var1.prevDistanceWalkedModified + (var1.distanceWalkedModified - var1.prevDistanceWalkedModified) * var2) * 6.0F) * 32.0F * var19;
			if(var1.isSneaking()) {
				var16 += 25.0F;
			}

			GL11.glRotatef(6.0F + var17 / 2.0F + var16, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var18 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var18 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(1.0F / 16.0F);
			GL11.glPopMatrix();
		}

		ItemStack var22 = var1.inventory.getCurrentItem();
		if(var22 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(1.0F / 16.0F);
			GL11.glTranslatef(-(1.0F / 16.0F), 7.0F / 16.0F, 1.0F / 16.0F);
			if(var1.fishEntity != null) {
				var22 = new ItemStack(Item.stick);
			}

			EnumAction var25 = null;
			if(var1.getItemInUseCount() > 0) {
				var25 = var22.getItemUseAction();
			}

			if(var22.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var22.itemID].getRenderType())) {
				var7 = 0.5F;
				GL11.glTranslatef(0.0F, 3.0F / 16.0F, -(5.0F / 16.0F));
				var7 *= 12.0F / 16.0F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-var7, -var7, var7);
			} else if(var22.itemID == Item.bow.itemID) {
				var7 = 10.0F / 16.0F;
				GL11.glTranslatef(0.0F, 2.0F / 16.0F, 5.0F / 16.0F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var7, -var7, var7);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if(Item.itemsList[var22.itemID].isFull3D()) {
				var7 = 10.0F / 16.0F;
				if(Item.itemsList[var22.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -(2.0F / 16.0F), 0.0F);
				}

				if(var1.getItemInUseCount() > 0 && var25 == EnumAction.block) {
					GL11.glTranslatef(0.05F, 0.0F, -0.1F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
				}

				GL11.glTranslatef(0.0F, 3.0F / 16.0F, 0.0F);
				GL11.glScalef(var7, -var7, var7);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var7 = 6.0F / 16.0F;
				GL11.glTranslatef(0.25F, 3.0F / 16.0F, -(3.0F / 16.0F));
				GL11.glScalef(var7, var7, var7);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			float var10;
			int var27;
			float var28;
			if(var22.getItem().requiresMultipleRenderPasses()) {
				for(var27 = 0; var27 <= 1; ++var27) {
					int var26 = var22.getItem().getColorFromItemStack(var22, var27);
					var28 = (float)(var26 >> 16 & 255) / 255.0F;
					var10 = (float)(var26 >> 8 & 255) / 255.0F;
					var11 = (float)(var26 & 255) / 255.0F;
					GL11.glColor4f(var28, var10, var11, 1.0F);
					this.renderManager.itemRenderer.renderItem(var1, var22, var27);
				}
			} else {
				var27 = var22.getItem().getColorFromItemStack(var22, 0);
				var8 = (float)(var27 >> 16 & 255) / 255.0F;
				var28 = (float)(var27 >> 8 & 255) / 255.0F;
				var10 = (float)(var27 & 255) / 255.0F;
				GL11.glColor4f(var8, var28, var10, 1.0F);
				this.renderManager.itemRenderer.renderItem(var1, var22, 0);
			}

			GL11.glPopMatrix();
		}

	}

	protected void renderPlayerScale(EntityPlayer var1, float var2) {
		float var3 = 15.0F / 16.0F;
		GL11.glScalef(var3, var3, var3);
	}

	protected void func_96450_a(EntityPlayer var1, double var2, double var4, double var6, String var8, float var9, double var10) {
		if(var10 < 100.0D) {
			Scoreboard var12 = var1.getWorldScoreboard();
			ScoreObjective var13 = var12.func_96539_a(2);
			if(var13 != null) {
				Score var14 = var12.func_96529_a(var1.getEntityName(), var13);
				if(var1.isPlayerSleeping()) {
					this.renderLivingLabel(var1, var14.func_96652_c() + " " + var13.getDisplayName(), var2, var4 - 1.5D, var6, 64);
				} else {
					this.renderLivingLabel(var1, var14.func_96652_c() + " " + var13.getDisplayName(), var2, var4, var6, 64);
				}

				var4 += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * var9);
			}
		}

		super.func_96449_a(var1, var2, var4, var6, var8, var9, var10);
	}

	public void renderFirstPersonArm(EntityPlayer var1) {
		float var2 = 1.0F;
		GL11.glColor3f(var2, var2, var2);
		this.modelBipedMain.onGround = 0.0F;
		this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F, var1);
		this.modelBipedMain.bipedRightArm.render(1.0F / 16.0F);
	}

	protected void renderPlayerSleep(EntityPlayer var1, double var2, double var4, double var6) {
		if(var1.isEntityAlive() && var1.isPlayerSleeping()) {
			super.renderLivingAt(var1, var2 + (double)var1.field_71079_bU, var4 + (double)var1.field_71082_cx, var6 + (double)var1.field_71089_bV);
		} else {
			super.renderLivingAt(var1, var2, var4, var6);
		}

	}

	protected void rotatePlayer(EntityPlayer var1, float var2, float var3, float var4) {
		if(var1.isEntityAlive() && var1.isPlayerSleeping()) {
			GL11.glRotatef(var1.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(var1), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.rotateCorpse(var1, var2, var3, var4);
		}

	}

	protected void func_96449_a(EntityLiving var1, double var2, double var4, double var6, String var8, float var9, double var10) {
		this.func_96450_a((EntityPlayer)var1, var2, var4, var6, var8, var9, var10);
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.renderPlayerScale((EntityPlayer)var1, var2);
	}

	protected void func_82408_c(EntityLiving var1, int var2, float var3) {
		this.func_82439_b((EntityPlayer)var1, var2, var3);
	}

	protected int shouldRenderPass(EntityLiving var1, int var2, float var3) {
		return this.setArmorModel((EntityPlayer)var1, var2, var3);
	}

	protected void renderEquippedItems(EntityLiving var1, float var2) {
		this.renderSpecials((EntityPlayer)var1, var2);
	}

	protected void rotateCorpse(EntityLiving var1, float var2, float var3, float var4) {
		this.rotatePlayer((EntityPlayer)var1, var2, var3, var4);
	}

	protected void renderLivingAt(EntityLiving var1, double var2, double var4, double var6) {
		this.renderPlayerSleep((EntityPlayer)var1, var2, var4, var6);
	}

	protected void func_98190_a(EntityLiving var1) {
		this.func_98191_a((EntityPlayer)var1);
	}

	public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}
}
