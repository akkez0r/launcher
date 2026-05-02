package net.minecraft.src;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class GuiContainer extends GuiScreen {
	protected static RenderItem itemRenderer = new RenderItem();
	protected int xSize = 176;
	protected int ySize = 166;
	public Container inventorySlots;
	protected int guiLeft;
	protected int guiTop;
	private Slot theSlot;
	private Slot clickedSlot = null;
	private boolean isRightMouseClick = false;
	private ItemStack draggedStack = null;
	private int field_85049_r = 0;
	private int field_85048_s = 0;
	private Slot returningStackDestSlot = null;
	private long returningStackTime = 0L;
	private ItemStack returningStack = null;
	private Slot field_92033_y = null;
	private long field_92032_z = 0L;
	protected final Set field_94077_p = new HashSet();
	protected boolean field_94076_q;
	private int field_94071_C = 0;
	private int field_94067_D = 0;
	private boolean field_94068_E = false;
	private int field_94069_F;
	private long field_94070_G = 0L;
	private Slot field_94072_H = null;
	private int field_94073_I = 0;
	private boolean field_94074_J;
	private ItemStack field_94075_K = null;

	public GuiContainer(Container var1) {
		this.inventorySlots = var1;
		this.field_94068_E = true;
	}

	public void initGui() {
		super.initGui();
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int var4 = this.guiLeft;
		int var5 = this.guiTop;
		this.drawGuiContainerBackgroundLayer(var3, var1, var2);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(var1, var2, var3);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var4, (float)var5, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		this.theSlot = null;
		short var6 = 240;
		short var7 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6 / 1.0F, (float)var7 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int var9;
		for(int var13 = 0; var13 < this.inventorySlots.inventorySlots.size(); ++var13) {
			Slot var15 = (Slot)this.inventorySlots.inventorySlots.get(var13);
			this.drawSlotInventory(var15);
			if(this.isMouseOverSlot(var15, var1, var2)) {
				this.theSlot = var15;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int var8 = var15.xDisplayPosition;
				var9 = var15.yDisplayPosition;
				this.drawGradientRect(var8, var9, var8 + 16, var9 + 16, -2130706433, -2130706433);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		this.drawGuiContainerForegroundLayer(var1, var2);
		InventoryPlayer var14 = this.mc.thePlayer.inventory;
		ItemStack var16 = this.draggedStack == null ? var14.getItemStack() : this.draggedStack;
		if(var16 != null) {
			byte var17 = 8;
			var9 = this.draggedStack == null ? 8 : 16;
			String var10 = null;
			if(this.draggedStack != null && this.isRightMouseClick) {
				var16 = var16.copy();
				var16.stackSize = MathHelper.ceiling_float_int((float)var16.stackSize / 2.0F);
			} else if(this.field_94076_q && this.field_94077_p.size() > 1) {
				var16 = var16.copy();
				var16.stackSize = this.field_94069_F;
				if(var16.stackSize == 0) {
					var10 = "" + EnumChatFormatting.YELLOW + "0";
				}
			}

			this.drawItemStack(var16, var1 - var4 - var17, var2 - var5 - var9, var10);
		}

		if(this.returningStack != null) {
			float var18 = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
			if(var18 >= 1.0F) {
				var18 = 1.0F;
				this.returningStack = null;
			}

			var9 = this.returningStackDestSlot.xDisplayPosition - this.field_85049_r;
			int var20 = this.returningStackDestSlot.yDisplayPosition - this.field_85048_s;
			int var11 = this.field_85049_r + (int)((float)var9 * var18);
			int var12 = this.field_85048_s + (int)((float)var20 * var18);
			this.drawItemStack(this.returningStack, var11, var12, (String)null);
		}

		GL11.glPopMatrix();
		if(var14.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
			ItemStack var19 = this.theSlot.getStack();
			this.drawItemStackTooltip(var19, var1, var2);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	private void drawItemStack(ItemStack var1, int var2, int var3, String var4) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRenderer.zLevel = 200.0F;
		itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var1, var2, var3);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var1, var2, var3 - (this.draggedStack == null ? 0 : 8), var4);
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}

	protected void drawItemStackTooltip(ItemStack var1, int var2, int var3) {
		List var4 = var1.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

		for(int var5 = 0; var5 < var4.size(); ++var5) {
			if(var5 == 0) {
				var4.set(var5, "\u00a7" + Integer.toHexString(var1.getRarity().rarityColor) + (String)var4.get(var5));
			} else {
				var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
			}
		}

		this.func_102021_a(var4, var2, var3);
	}

	protected void drawCreativeTabHoveringText(String var1, int var2, int var3) {
		this.func_102021_a(Arrays.asList(new String[]{var1}), var2, var3);
	}

	protected void func_102021_a(List var1, int var2, int var3) {
		if(!var1.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int var4 = 0;
			Iterator var5 = var1.iterator();

			while(var5.hasNext()) {
				String var6 = (String)var5.next();
				int var7 = this.fontRenderer.getStringWidth(var6);
				if(var7 > var4) {
					var4 = var7;
				}
			}

			int var14 = var2 + 12;
			int var15 = var3 - 12;
			int var8 = 8;
			if(var1.size() > 1) {
				var8 += 2 + (var1.size() - 1) * 10;
			}

			if(var14 + var4 > this.width) {
				var14 -= 28 + var4;
			}

			if(var15 + var8 + 6 > this.height) {
				var15 = this.height - var8 - 6;
			}

			this.zLevel = 300.0F;
			itemRenderer.zLevel = 300.0F;
			int var9 = -267386864;
			this.drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
			this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
			this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
			this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
			this.drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
			int var10 = 1347420415;
			int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
			this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
			this.drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
			this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
			this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

			for(int var12 = 0; var12 < var1.size(); ++var12) {
				String var13 = (String)var1.get(var12);
				this.fontRenderer.drawStringWithShadow(var13, var14, var15, -1);
				if(var12 == 0) {
					var15 += 2;
				}

				var15 += 10;
			}

			this.zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
	}

	protected abstract void drawGuiContainerBackgroundLayer(float var1, int var2, int var3);

	private void drawSlotInventory(Slot var1) {
		int var2 = var1.xDisplayPosition;
		int var3 = var1.yDisplayPosition;
		ItemStack var4 = var1.getStack();
		boolean var5 = false;
		boolean var6 = var1 == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
		ItemStack var7 = this.mc.thePlayer.inventory.getItemStack();
		String var8 = null;
		if(var1 == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && var4 != null) {
			var4 = var4.copy();
			var4.stackSize /= 2;
		} else if(this.field_94076_q && this.field_94077_p.contains(var1) && var7 != null) {
			if(this.field_94077_p.size() == 1) {
				return;
			}

			if(Container.func_94527_a(var1, var7, true) && this.inventorySlots.func_94531_b(var1)) {
				var4 = var7.copy();
				var5 = true;
				Container.func_94525_a(this.field_94077_p, this.field_94071_C, var4, var1.getStack() == null ? 0 : var1.getStack().stackSize);
				if(var4.stackSize > var4.getMaxStackSize()) {
					var8 = EnumChatFormatting.YELLOW + "" + var4.getMaxStackSize();
					var4.stackSize = var4.getMaxStackSize();
				}

				if(var4.stackSize > var1.getSlotStackLimit()) {
					var8 = EnumChatFormatting.YELLOW + "" + var1.getSlotStackLimit();
					var4.stackSize = var1.getSlotStackLimit();
				}
			} else {
				this.field_94077_p.remove(var1);
				this.func_94066_g();
			}
		}

		this.zLevel = 100.0F;
		itemRenderer.zLevel = 100.0F;
		if(var4 == null) {
			Icon var9 = var1.getBackgroundIconIndex();
			if(var9 != null) {
				GL11.glDisable(GL11.GL_LIGHTING);
				this.mc.renderEngine.bindTexture("/gui/items.png");
				this.drawTexturedModelRectFromIcon(var2, var3, var9, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				var6 = true;
			}
		}

		if(!var6) {
			if(var5) {
				drawRect(var2, var3, var2 + 16, var3 + 16, -2130706433);
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3, var8);
		}

		itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	private void func_94066_g() {
		ItemStack var1 = this.mc.thePlayer.inventory.getItemStack();
		if(var1 != null && this.field_94076_q) {
			this.field_94069_F = var1.stackSize;

			ItemStack var4;
			int var5;
			for(Iterator var2 = this.field_94077_p.iterator(); var2.hasNext(); this.field_94069_F -= var4.stackSize - var5) {
				Slot var3 = (Slot)var2.next();
				var4 = var1.copy();
				var5 = var3.getStack() == null ? 0 : var3.getStack().stackSize;
				Container.func_94525_a(this.field_94077_p, this.field_94071_C, var4, var5);
				if(var4.stackSize > var4.getMaxStackSize()) {
					var4.stackSize = var4.getMaxStackSize();
				}

				if(var4.stackSize > var3.getSlotStackLimit()) {
					var4.stackSize = var3.getSlotStackLimit();
				}
			}

		}
	}

	private Slot getSlotAtPosition(int var1, int var2) {
		for(int var3 = 0; var3 < this.inventorySlots.inventorySlots.size(); ++var3) {
			Slot var4 = (Slot)this.inventorySlots.inventorySlots.get(var3);
			if(this.isMouseOverSlot(var4, var1, var2)) {
				return var4;
			}
		}

		return null;
	}

	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		boolean var4 = var3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;
		Slot var5 = this.getSlotAtPosition(var1, var2);
		long var6 = Minecraft.getSystemTime();
		this.field_94074_J = this.field_94072_H == var5 && var6 - this.field_94070_G < 250L && this.field_94073_I == var3;
		this.field_94068_E = false;
		if(var3 == 0 || var3 == 1 || var4) {
			int var8 = this.guiLeft;
			int var9 = this.guiTop;
			boolean var10 = var1 < var8 || var2 < var9 || var1 >= var8 + this.xSize || var2 >= var9 + this.ySize;
			int var11 = -1;
			if(var5 != null) {
				var11 = var5.slotNumber;
			}

			if(var10) {
				var11 = -999;
			}

			if(this.mc.gameSettings.touchscreen && var10 && this.mc.thePlayer.inventory.getItemStack() == null) {
				this.mc.displayGuiScreen((GuiScreen)null);
				return;
			}

			if(var11 != -1) {
				if(this.mc.gameSettings.touchscreen) {
					if(var5 != null && var5.getHasStack()) {
						this.clickedSlot = var5;
						this.draggedStack = null;
						this.isRightMouseClick = var3 == 1;
					} else {
						this.clickedSlot = null;
					}
				} else if(!this.field_94076_q) {
					if(this.mc.thePlayer.inventory.getItemStack() == null) {
						if(var3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100) {
							this.handleMouseClick(var5, var11, var3, 3);
						} else {
							boolean var12 = var11 != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
							byte var13 = 0;
							if(var12) {
								this.field_94075_K = var5 != null && var5.getHasStack() ? var5.getStack() : null;
								var13 = 1;
							} else if(var11 == -999) {
								var13 = 4;
							}

							this.handleMouseClick(var5, var11, var3, var13);
						}

						this.field_94068_E = true;
					} else {
						this.field_94076_q = true;
						this.field_94067_D = var3;
						this.field_94077_p.clear();
						if(var3 == 0) {
							this.field_94071_C = 0;
						} else if(var3 == 1) {
							this.field_94071_C = 1;
						}
					}
				}
			}
		}

		this.field_94072_H = var5;
		this.field_94070_G = var6;
		this.field_94073_I = var3;
	}

	protected void func_85041_a(int var1, int var2, int var3, long var4) {
		Slot var6 = this.getSlotAtPosition(var1, var2);
		ItemStack var7 = this.mc.thePlayer.inventory.getItemStack();
		if(this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
			if(var3 == 0 || var3 == 1) {
				if(this.draggedStack == null) {
					if(var6 != this.clickedSlot) {
						this.draggedStack = this.clickedSlot.getStack().copy();
					}
				} else if(this.draggedStack.stackSize > 1 && var6 != null && Container.func_94527_a(var6, this.draggedStack, false)) {
					long var8 = Minecraft.getSystemTime();
					if(this.field_92033_y == var6) {
						if(var8 - this.field_92032_z > 500L) {
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
							this.handleMouseClick(var6, var6.slotNumber, 1, 0);
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
							this.field_92032_z = var8 + 750L;
							--this.draggedStack.stackSize;
						}
					} else {
						this.field_92033_y = var6;
						this.field_92032_z = var8;
					}
				}
			}
		} else if(this.field_94076_q && var6 != null && var7 != null && var7.stackSize > this.field_94077_p.size() && Container.func_94527_a(var6, var7, true) && var6.isItemValid(var7) && this.inventorySlots.func_94531_b(var6)) {
			this.field_94077_p.add(var6);
			this.func_94066_g();
		}

	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		Slot var4 = this.getSlotAtPosition(var1, var2);
		int var5 = this.guiLeft;
		int var6 = this.guiTop;
		boolean var7 = var1 < var5 || var2 < var6 || var1 >= var5 + this.xSize || var2 >= var6 + this.ySize;
		int var8 = -1;
		if(var4 != null) {
			var8 = var4.slotNumber;
		}

		if(var7) {
			var8 = -999;
		}

		Slot var10;
		Iterator var11;
		if(this.field_94074_J && var4 != null && var3 == 0 && this.inventorySlots.func_94530_a((ItemStack)null, var4)) {
			if(isShiftKeyDown()) {
				if(var4 != null && var4.inventory != null && this.field_94075_K != null) {
					var11 = this.inventorySlots.inventorySlots.iterator();

					while(var11.hasNext()) {
						var10 = (Slot)var11.next();
						if(var10 != null && var10.canTakeStack(this.mc.thePlayer) && var10.getHasStack() && var10.inventory == var4.inventory && Container.func_94527_a(var10, this.field_94075_K, true)) {
							this.handleMouseClick(var10, var10.slotNumber, var3, 1);
						}
					}
				}
			} else {
				this.handleMouseClick(var4, var8, var3, 6);
			}

			this.field_94074_J = false;
			this.field_94070_G = 0L;
		} else {
			if(this.field_94076_q && this.field_94067_D != var3) {
				this.field_94076_q = false;
				this.field_94077_p.clear();
				this.field_94068_E = true;
				return;
			}

			if(this.field_94068_E) {
				this.field_94068_E = false;
				return;
			}

			boolean var9;
			if(this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
				if(var3 == 0 || var3 == 1) {
					if(this.draggedStack == null && var4 != this.clickedSlot) {
						this.draggedStack = this.clickedSlot.getStack();
					}

					var9 = Container.func_94527_a(var4, this.draggedStack, false);
					if(var8 != -1 && this.draggedStack != null && var9) {
						this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, var3, 0);
						this.handleMouseClick(var4, var8, 0, 0);
						if(this.mc.thePlayer.inventory.getItemStack() != null) {
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, var3, 0);
							this.field_85049_r = var1 - var5;
							this.field_85048_s = var2 - var6;
							this.returningStackDestSlot = this.clickedSlot;
							this.returningStack = this.draggedStack;
							this.returningStackTime = Minecraft.getSystemTime();
						} else {
							this.returningStack = null;
						}
					} else if(this.draggedStack != null) {
						this.field_85049_r = var1 - var5;
						this.field_85048_s = var2 - var6;
						this.returningStackDestSlot = this.clickedSlot;
						this.returningStack = this.draggedStack;
						this.returningStackTime = Minecraft.getSystemTime();
					}

					this.draggedStack = null;
					this.clickedSlot = null;
				}
			} else if(this.field_94076_q && !this.field_94077_p.isEmpty()) {
				this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.field_94071_C), 5);
				var11 = this.field_94077_p.iterator();

				while(var11.hasNext()) {
					var10 = (Slot)var11.next();
					this.handleMouseClick(var10, var10.slotNumber, Container.func_94534_d(1, this.field_94071_C), 5);
				}

				this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.field_94071_C), 5);
			} else if(this.mc.thePlayer.inventory.getItemStack() != null) {
				if(var3 == this.mc.gameSettings.keyBindPickBlock.keyCode + 100) {
					this.handleMouseClick(var4, var8, var3, 3);
				} else {
					var9 = var8 != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
					if(var9) {
						this.field_94075_K = var4 != null && var4.getHasStack() ? var4.getStack() : null;
					}

					this.handleMouseClick(var4, var8, var3, var9 ? 1 : 0);
				}
			}
		}

		if(this.mc.thePlayer.inventory.getItemStack() == null) {
			this.field_94070_G = 0L;
		}

		this.field_94076_q = false;
	}

	private boolean isMouseOverSlot(Slot var1, int var2, int var3) {
		return this.isPointInRegion(var1.xDisplayPosition, var1.yDisplayPosition, 16, 16, var2, var3);
	}

	protected boolean isPointInRegion(int var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = this.guiLeft;
		int var8 = this.guiTop;
		var5 -= var7;
		var6 -= var8;
		return var5 >= var1 - 1 && var5 < var1 + var3 + 1 && var6 >= var2 - 1 && var6 < var2 + var4 + 1;
	}

	protected void handleMouseClick(Slot var1, int var2, int var3, int var4) {
		if(var1 != null) {
			var2 = var1.slotNumber;
		}

		this.mc.playerController.windowClick(this.inventorySlots.windowId, var2, var3, var4, this.mc.thePlayer);
	}

	protected void keyTyped(char var1, int var2) {
		if(var2 == 1 || var2 == this.mc.gameSettings.keyBindInventory.keyCode) {
			this.mc.thePlayer.closeScreen();
		}

		this.checkHotbarKeys(var2);
		if(this.theSlot != null && this.theSlot.getHasStack()) {
			if(var2 == this.mc.gameSettings.keyBindPickBlock.keyCode) {
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
			} else if(var2 == this.mc.gameSettings.keyBindDrop.keyCode) {
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
			}
		}

	}

	protected boolean checkHotbarKeys(int var1) {
		if(this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
			for(int var2 = 0; var2 < 9; ++var2) {
				if(var1 == 2 + var2) {
					this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, var2, 2);
					return true;
				}
			}
		}

		return false;
	}

	public void onGuiClosed() {
		if(this.mc.thePlayer != null) {
			this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void updateScreen() {
		super.updateScreen();
		if(!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
			this.mc.thePlayer.closeScreen();
		}

	}
}
