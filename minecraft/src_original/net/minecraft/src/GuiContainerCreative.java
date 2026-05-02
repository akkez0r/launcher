package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiContainerCreative extends InventoryEffectRenderer {
	private static InventoryBasic inventory = new InventoryBasic("tmp", true, 45);
	private static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();
	private float currentScroll = 0.0F;
	private boolean isScrolling = false;
	private boolean wasClicking;
	private GuiTextField searchField;
	private List backupContainerSlots;
	private Slot field_74235_v = null;
	private boolean field_74234_w = false;
	private CreativeCrafting field_82324_x;

	public GuiContainerCreative(EntityPlayer var1) {
		super(new ContainerCreative(var1));
		var1.openContainer = this.inventorySlots;
		this.allowUserInput = true;
		var1.addStat(AchievementList.openInventory, 1);
		this.ySize = 136;
		this.xSize = 195;
	}

	public void updateScreen() {
		if(!this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
		}

	}

	protected void handleMouseClick(Slot var1, int var2, int var3, int var4) {
		this.field_74234_w = true;
		boolean var5 = var4 == 1;
		var4 = var2 == -999 && var4 == 0 ? 4 : var4;
		ItemStack var7;
		InventoryPlayer var11;
		if(var1 == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && var4 != 5) {
			var11 = this.mc.thePlayer.inventory;
			if(var11.getItemStack() != null) {
				if(var3 == 0) {
					this.mc.thePlayer.dropPlayerItem(var11.getItemStack());
					this.mc.playerController.func_78752_a(var11.getItemStack());
					var11.setItemStack((ItemStack)null);
				}

				if(var3 == 1) {
					var7 = var11.getItemStack().splitStack(1);
					this.mc.thePlayer.dropPlayerItem(var7);
					this.mc.playerController.func_78752_a(var7);
					if(var11.getItemStack().stackSize == 0) {
						var11.setItemStack((ItemStack)null);
					}
				}
			}
		} else {
			int var10;
			if(var1 == this.field_74235_v && var5) {
				for(var10 = 0; var10 < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++var10) {
					this.mc.playerController.sendSlotPacket((ItemStack)null, var10);
				}
			} else {
				ItemStack var6;
				if(selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
					if(var1 == this.field_74235_v) {
						this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
					} else if(var4 == 4 && var1 != null && var1.getHasStack()) {
						var6 = var1.decrStackSize(var3 == 0 ? 1 : var1.getStack().getMaxStackSize());
						this.mc.thePlayer.dropPlayerItem(var6);
						this.mc.playerController.func_78752_a(var6);
					} else if(var4 == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
						this.mc.thePlayer.dropPlayerItem(this.mc.thePlayer.inventory.getItemStack());
						this.mc.playerController.func_78752_a(this.mc.thePlayer.inventory.getItemStack());
						this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
					} else {
						this.mc.thePlayer.inventoryContainer.slotClick(var1 == null ? var2 : SlotCreativeInventory.func_75240_a((SlotCreativeInventory)var1).slotNumber, var3, var4, this.mc.thePlayer);
						this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
					}
				} else if(var4 != 5 && var1.inventory == inventory) {
					var11 = this.mc.thePlayer.inventory;
					var7 = var11.getItemStack();
					ItemStack var8 = var1.getStack();
					ItemStack var9;
					if(var4 == 2) {
						if(var8 != null && var3 >= 0 && var3 < 9) {
							var9 = var8.copy();
							var9.stackSize = var9.getMaxStackSize();
							this.mc.thePlayer.inventory.setInventorySlotContents(var3, var9);
							this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
						}

						return;
					}

					if(var4 == 3) {
						if(var11.getItemStack() == null && var1.getHasStack()) {
							var9 = var1.getStack().copy();
							var9.stackSize = var9.getMaxStackSize();
							var11.setItemStack(var9);
						}

						return;
					}

					if(var4 == 4) {
						if(var8 != null) {
							var9 = var8.copy();
							var9.stackSize = var3 == 0 ? 1 : var9.getMaxStackSize();
							this.mc.thePlayer.dropPlayerItem(var9);
							this.mc.playerController.func_78752_a(var9);
						}

						return;
					}

					if(var7 != null && var8 != null && var7.isItemEqual(var8)) {
						if(var3 == 0) {
							if(var5) {
								var7.stackSize = var7.getMaxStackSize();
							} else if(var7.stackSize < var7.getMaxStackSize()) {
								++var7.stackSize;
							}
						} else if(var7.stackSize <= 1) {
							var11.setItemStack((ItemStack)null);
						} else {
							--var7.stackSize;
						}
					} else if(var8 != null && var7 == null) {
						var11.setItemStack(ItemStack.copyItemStack(var8));
						var7 = var11.getItemStack();
						if(var5) {
							var7.stackSize = var7.getMaxStackSize();
						}
					} else {
						var11.setItemStack((ItemStack)null);
					}
				} else {
					this.inventorySlots.slotClick(var1 == null ? var2 : var1.slotNumber, var3, var4, this.mc.thePlayer);
					if(Container.func_94532_c(var3) == 2) {
						for(var10 = 0; var10 < 9; ++var10) {
							this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + var10).getStack(), 36 + var10);
						}
					} else if(var1 != null) {
						var6 = this.inventorySlots.getSlot(var1.slotNumber).getStack();
						this.mc.playerController.sendSlotPacket(var6, var1.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
					}
				}
			}
		}

	}

	public void initGui() {
		if(this.mc.playerController.isInCreativeMode()) {
			super.initGui();
			this.buttonList.clear();
			Keyboard.enableRepeatEvents(true);
			this.searchField = new GuiTextField(this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
			this.searchField.setMaxStringLength(15);
			this.searchField.setEnableBackgroundDrawing(false);
			this.searchField.setVisible(false);
			this.searchField.setTextColor(16777215);
			int var1 = selectedTabIndex;
			selectedTabIndex = -1;
			this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[var1]);
			this.field_82324_x = new CreativeCrafting(this.mc);
			this.mc.thePlayer.inventoryContainer.addCraftingToCrafters(this.field_82324_x);
		} else {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
		}

	}

	public void onGuiClosed() {
		super.onGuiClosed();
		if(this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
			this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_82324_x);
		}

		Keyboard.enableRepeatEvents(false);
	}

	protected void keyTyped(char var1, int var2) {
		if(selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
			if(GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
				this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
			} else {
				super.keyTyped(var1, var2);
			}

		} else {
			if(this.field_74234_w) {
				this.field_74234_w = false;
				this.searchField.setText("");
			}

			if(!this.checkHotbarKeys(var2)) {
				if(this.searchField.textboxKeyTyped(var1, var2)) {
					this.updateCreativeSearch();
				} else {
					super.keyTyped(var1, var2);
				}

			}
		}
	}

	private void updateCreativeSearch() {
		ContainerCreative var1 = (ContainerCreative)this.inventorySlots;
		var1.itemList.clear();
		Item[] var2 = Item.itemsList;
		int var3 = var2.length;

		int var4;
		for(var4 = 0; var4 < var3; ++var4) {
			Item var5 = var2[var4];
			if(var5 != null && var5.getCreativeTab() != null) {
				var5.getSubItems(var5.itemID, (CreativeTabs)null, var1.itemList);
			}
		}

		Enchantment[] var8 = Enchantment.enchantmentsList;
		var3 = var8.length;

		for(var4 = 0; var4 < var3; ++var4) {
			Enchantment var11 = var8[var4];
			if(var11 != null && var11.type != null) {
				Item.enchantedBook.func_92113_a(var11, var1.itemList);
			}
		}

		Iterator var9 = var1.itemList.iterator();
		String var10 = this.searchField.getText().toLowerCase();

		while(var9.hasNext()) {
			ItemStack var12 = (ItemStack)var9.next();
			boolean var13 = false;
			Iterator var6 = var12.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips).iterator();

			while(var6.hasNext()) {
				String var7 = (String)var6.next();
				if(var7.toLowerCase().contains(var10)) {
					var13 = true;
					break;
				}
			}

			if(!var13) {
				var9.remove();
			}
		}

		this.currentScroll = 0.0F;
		var1.scrollTo(0.0F);
	}

	protected void drawGuiContainerForegroundLayer(int var1, int var2) {
		CreativeTabs var3 = CreativeTabs.creativeTabArray[selectedTabIndex];
		if(var3.drawInForegroundOfTab()) {
			this.fontRenderer.drawString(var3.getTranslatedTabLabel(), 8, 6, 4210752);
		}

	}

	protected void mouseClicked(int var1, int var2, int var3) {
		if(var3 == 0) {
			int var4 = var1 - this.guiLeft;
			int var5 = var2 - this.guiTop;
			CreativeTabs[] var6 = CreativeTabs.creativeTabArray;
			int var7 = var6.length;

			for(int var8 = 0; var8 < var7; ++var8) {
				CreativeTabs var9 = var6[var8];
				if(this.func_74232_a(var9, var4, var5)) {
					return;
				}
			}
		}

		super.mouseClicked(var1, var2, var3);
	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		if(var3 == 0) {
			int var4 = var1 - this.guiLeft;
			int var5 = var2 - this.guiTop;
			CreativeTabs[] var6 = CreativeTabs.creativeTabArray;
			int var7 = var6.length;

			for(int var8 = 0; var8 < var7; ++var8) {
				CreativeTabs var9 = var6[var8];
				if(this.func_74232_a(var9, var4, var5)) {
					this.setCurrentCreativeTab(var9);
					return;
				}
			}
		}

		super.mouseMovedOrUp(var1, var2, var3);
	}

	private boolean needsScrollBars() {
		return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory() && ((ContainerCreative)this.inventorySlots).hasMoreThan1PageOfItemsInList();
	}

	private void setCurrentCreativeTab(CreativeTabs var1) {
		int var2 = selectedTabIndex;
		selectedTabIndex = var1.getTabIndex();
		ContainerCreative var3 = (ContainerCreative)this.inventorySlots;
		this.field_94077_p.clear();
		var3.itemList.clear();
		var1.displayAllReleventItems(var3.itemList);
		if(var1 == CreativeTabs.tabInventory) {
			Container var4 = this.mc.thePlayer.inventoryContainer;
			if(this.backupContainerSlots == null) {
				this.backupContainerSlots = var3.inventorySlots;
			}

			var3.inventorySlots = new ArrayList();

			for(int var5 = 0; var5 < var4.inventorySlots.size(); ++var5) {
				SlotCreativeInventory var6 = new SlotCreativeInventory(this, (Slot)var4.inventorySlots.get(var5), var5);
				var3.inventorySlots.add(var6);
				int var7;
				int var8;
				int var9;
				if(var5 >= 5 && var5 < 9) {
					var7 = var5 - 5;
					var8 = var7 / 2;
					var9 = var7 % 2;
					var6.xDisplayPosition = 9 + var8 * 54;
					var6.yDisplayPosition = 6 + var9 * 27;
				} else if(var5 >= 0 && var5 < 5) {
					var6.yDisplayPosition = -2000;
					var6.xDisplayPosition = -2000;
				} else if(var5 < var4.inventorySlots.size()) {
					var7 = var5 - 9;
					var8 = var7 % 9;
					var9 = var7 / 9;
					var6.xDisplayPosition = 9 + var8 * 18;
					if(var5 >= 36) {
						var6.yDisplayPosition = 112;
					} else {
						var6.yDisplayPosition = 54 + var9 * 18;
					}
				}
			}

			this.field_74235_v = new Slot(inventory, 0, 173, 112);
			var3.inventorySlots.add(this.field_74235_v);
		} else if(var2 == CreativeTabs.tabInventory.getTabIndex()) {
			var3.inventorySlots = this.backupContainerSlots;
			this.backupContainerSlots = null;
		}

		if(this.searchField != null) {
			if(var1 == CreativeTabs.tabAllSearch) {
				this.searchField.setVisible(true);
				this.searchField.setCanLoseFocus(false);
				this.searchField.setFocused(true);
				this.searchField.setText("");
				this.updateCreativeSearch();
			} else {
				this.searchField.setVisible(false);
				this.searchField.setCanLoseFocus(true);
				this.searchField.setFocused(false);
			}
		}

		this.currentScroll = 0.0F;
		var3.scrollTo(0.0F);
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		int var1 = Mouse.getEventDWheel();
		if(var1 != 0 && this.needsScrollBars()) {
			int var2 = ((ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5 + 1;
			if(var1 > 0) {
				var1 = 1;
			}

			if(var1 < 0) {
				var1 = -1;
			}

			this.currentScroll = (float)((double)this.currentScroll - (double)var1 / (double)var2);
			if(this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if(this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		boolean var4 = Mouse.isButtonDown(0);
		int var5 = this.guiLeft;
		int var6 = this.guiTop;
		int var7 = var5 + 175;
		int var8 = var6 + 18;
		int var9 = var7 + 14;
		int var10 = var8 + 112;
		if(!this.wasClicking && var4 && var1 >= var7 && var2 >= var8 && var1 < var9 && var2 < var10) {
			this.isScrolling = this.needsScrollBars();
		}

		if(!var4) {
			this.isScrolling = false;
		}

		this.wasClicking = var4;
		if(this.isScrolling) {
			this.currentScroll = ((float)(var2 - var8) - 7.5F) / ((float)(var10 - var8) - 15.0F);
			if(this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if(this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
		}

		super.drawScreen(var1, var2, var3);
		CreativeTabs[] var11 = CreativeTabs.creativeTabArray;
		int var12 = var11.length;

		for(int var13 = 0; var13 < var12; ++var13) {
			CreativeTabs var14 = var11[var13];
			if(this.renderCreativeInventoryHoveringText(var14, var1, var2)) {
				break;
			}
		}

		if(this.field_74235_v != null && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_74235_v.xDisplayPosition, this.field_74235_v.yDisplayPosition, 16, 16, var1, var2)) {
			this.drawCreativeTabHoveringText(StringTranslate.getInstance().translateKey("inventory.binSlot"), var1, var2);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		CreativeTabs var4 = CreativeTabs.creativeTabArray[selectedTabIndex];
		CreativeTabs[] var5 = CreativeTabs.creativeTabArray;
		int var6 = var5.length;

		int var7;
		for(var7 = 0; var7 < var6; ++var7) {
			CreativeTabs var8 = var5[var7];
			this.mc.renderEngine.bindTexture("/gui/allitems.png");
			if(var8.getTabIndex() != selectedTabIndex) {
				this.renderCreativeTab(var8);
			}
		}

		this.mc.renderEngine.bindTexture("/gui/creative_inv/" + var4.getBackgroundImageName());
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.searchField.drawTextBox();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int var9 = this.guiLeft + 175;
		var6 = this.guiTop + 18;
		var7 = var6 + 112;
		this.mc.renderEngine.bindTexture("/gui/allitems.png");
		if(var4.shouldHidePlayerInventory()) {
			this.drawTexturedModalRect(var9, var6 + (int)((float)(var7 - var6 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		}

		this.renderCreativeTab(var4);
		if(var4 == CreativeTabs.tabInventory) {
			GuiInventory.drawPlayerOnGui(this.mc, this.guiLeft + 43, this.guiTop + 45, 20, (float)(this.guiLeft + 43 - var2), (float)(this.guiTop + 45 - 30 - var3));
		}

	}

	protected boolean func_74232_a(CreativeTabs var1, int var2, int var3) {
		int var4 = var1.getTabColumn();
		int var5 = 28 * var4;
		byte var6 = 0;
		if(var4 == 5) {
			var5 = this.xSize - 28 + 2;
		} else if(var4 > 0) {
			var5 += var4;
		}

		int var7;
		if(var1.isTabInFirstRow()) {
			var7 = var6 - 32;
		} else {
			var7 = var6 + this.ySize;
		}

		return var2 >= var5 && var2 <= var5 + 28 && var3 >= var7 && var3 <= var7 + 32;
	}

	protected boolean renderCreativeInventoryHoveringText(CreativeTabs var1, int var2, int var3) {
		int var4 = var1.getTabColumn();
		int var5 = 28 * var4;
		byte var6 = 0;
		if(var4 == 5) {
			var5 = this.xSize - 28 + 2;
		} else if(var4 > 0) {
			var5 += var4;
		}

		int var7;
		if(var1.isTabInFirstRow()) {
			var7 = var6 - 32;
		} else {
			var7 = var6 + this.ySize;
		}

		if(this.isPointInRegion(var5 + 3, var7 + 3, 23, 27, var2, var3)) {
			this.drawCreativeTabHoveringText(var1.getTranslatedTabLabel(), var2, var3);
			return true;
		} else {
			return false;
		}
	}

	protected void renderCreativeTab(CreativeTabs var1) {
		boolean var2 = var1.getTabIndex() == selectedTabIndex;
		boolean var3 = var1.isTabInFirstRow();
		int var4 = var1.getTabColumn();
		int var5 = var4 * 28;
		int var6 = 0;
		int var7 = this.guiLeft + 28 * var4;
		int var8 = this.guiTop;
		byte var9 = 32;
		if(var2) {
			var6 += 32;
		}

		if(var4 == 5) {
			var7 = this.guiLeft + this.xSize - 28;
		} else if(var4 > 0) {
			var7 += var4;
		}

		if(var3) {
			var8 -= 28;
		} else {
			var6 += 64;
			var8 += this.ySize - 4;
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		this.drawTexturedModalRect(var7, var8, var5, var6, 28, var9);
		this.zLevel = 100.0F;
		itemRenderer.zLevel = 100.0F;
		var7 += 6;
		var8 += 8 + (var3 ? 1 : -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		ItemStack var10 = new ItemStack(var1.getTabIconItem());
		itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var7, var8);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var7, var8);
		GL11.glDisable(GL11.GL_LIGHTING);
		itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
		}

		if(var1.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
		}

	}

	public int func_74230_h() {
		return selectedTabIndex;
	}

	static InventoryBasic getInventory() {
		return inventory;
	}
}
