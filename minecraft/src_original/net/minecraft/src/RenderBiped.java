package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderBiped extends RenderLiving {
	protected ModelBiped modelBipedMain;
	protected float field_77070_b;
	protected ModelBiped field_82423_g;
	protected ModelBiped field_82425_h;
	private static final String[] bipedArmorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

	public RenderBiped(ModelBiped var1, float var2) {
		this(var1, var2, 1.0F);
	}

	public RenderBiped(ModelBiped var1, float var2, float var3) {
		super(var1, var2);
		this.modelBipedMain = var1;
		this.field_77070_b = var3;
		this.func_82421_b();
	}

	protected void func_82421_b() {
		this.field_82423_g = new ModelBiped(1.0F);
		this.field_82425_h = new ModelBiped(0.5F);
	}

	protected int shouldRenderPass(EntityLiving var1, int var2, float var3) {
		ItemStack var4 = var1.getCurrentArmor(3 - var2);
		if(var4 != null) {
			Item var5 = var4.getItem();
			if(var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.loadTexture("/armor/" + bipedArmorFilenamePrefix[var6.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + ".png");
				ModelBiped var7 = var2 == 2 ? this.field_82425_h : this.field_82423_g;
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

	protected void func_82408_c(EntityLiving var1, int var2, float var3) {
		ItemStack var4 = var1.getCurrentArmor(3 - var2);
		if(var4 != null) {
			Item var5 = var4.getItem();
			if(var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.loadTexture("/armor/" + bipedArmorFilenamePrefix[var6.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + "_b.png");
				float var7 = 1.0F;
				GL11.glColor3f(var7, var7, var7);
			}
		}

	}

	public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		float var10 = 1.0F;
		GL11.glColor3f(var10, var10, var10);
		ItemStack var11 = var1.getHeldItem();
		this.func_82420_a(var1, var11);
		double var12 = var4 - (double)var1.yOffset;
		if(var1.isSneaking() && !(var1 instanceof EntityPlayerSP)) {
			var12 -= 0.125D;
		}

		super.doRenderLiving(var1, var2, var12, var6, var8, var9);
		this.field_82423_g.aimedBow = this.field_82425_h.aimedBow = this.modelBipedMain.aimedBow = false;
		this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = false;
		this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = 0;
	}

	protected void func_82420_a(EntityLiving var1, ItemStack var2) {
		this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = var2 != null ? 1 : 0;
		this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = var1.isSneaking();
	}

	protected void renderEquippedItems(EntityLiving var1, float var2) {
		float var3 = 1.0F;
		GL11.glColor3f(var3, var3, var3);
		super.renderEquippedItems(var1, var2);
		ItemStack var4 = var1.getHeldItem();
		ItemStack var5 = var1.getCurrentArmor(3);
		float var6;
		if(var5 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(1.0F / 16.0F);
			if(var5.getItem().itemID < 256) {
				if(RenderBlocks.renderItemIn3d(Block.blocksList[var5.itemID].getRenderType())) {
					var6 = 10.0F / 16.0F;
					GL11.glTranslatef(0.0F, -0.25F, 0.0F);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(var6, -var6, -var6);
				}

				this.renderManager.itemRenderer.renderItem(var1, var5, 0);
			} else if(var5.getItem().itemID == Item.skull.itemID) {
				var6 = 1.0625F;
				GL11.glScalef(var6, -var6, -var6);
				String var7 = "";
				if(var5.hasTagCompound() && var5.getTagCompound().hasKey("SkullOwner")) {
					var7 = var5.getTagCompound().getString("SkullOwner");
				}

				TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var5.getItemDamage(), var7);
			}

			GL11.glPopMatrix();
		}

		if(var4 != null) {
			GL11.glPushMatrix();
			if(this.mainModel.isChild) {
				var6 = 0.5F;
				GL11.glTranslatef(0.0F, 10.0F / 16.0F, 0.0F);
				GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
				GL11.glScalef(var6, var6, var6);
			}

			this.modelBipedMain.bipedRightArm.postRender(1.0F / 16.0F);
			GL11.glTranslatef(-(1.0F / 16.0F), 7.0F / 16.0F, 1.0F / 16.0F);
			if(var4.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType())) {
				var6 = 0.5F;
				GL11.glTranslatef(0.0F, 3.0F / 16.0F, -(5.0F / 16.0F));
				var6 *= 12.0F / 16.0F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-var6, -var6, var6);
			} else if(var4.itemID == Item.bow.itemID) {
				var6 = 10.0F / 16.0F;
				GL11.glTranslatef(0.0F, 2.0F / 16.0F, 5.0F / 16.0F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var6, -var6, var6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if(Item.itemsList[var4.itemID].isFull3D()) {
				var6 = 10.0F / 16.0F;
				if(Item.itemsList[var4.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -(2.0F / 16.0F), 0.0F);
				}

				this.func_82422_c();
				GL11.glScalef(var6, -var6, var6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var6 = 6.0F / 16.0F;
				GL11.glTranslatef(0.25F, 3.0F / 16.0F, -(3.0F / 16.0F));
				GL11.glScalef(var6, var6, var6);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(var1, var4, 0);
			if(var4.getItem().requiresMultipleRenderPasses()) {
				this.renderManager.itemRenderer.renderItem(var1, var4, 1);
			}

			GL11.glPopMatrix();
		}

	}

	protected void func_82422_c() {
		GL11.glTranslatef(0.0F, 3.0F / 16.0F, 0.0F);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.doRenderLiving((EntityLiving)var1, var2, var4, var6, var8, var9);
	}
}
