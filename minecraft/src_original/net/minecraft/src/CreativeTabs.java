package net.minecraft.src;

import java.util.List;

public class CreativeTabs {
	public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
	public static final CreativeTabs tabBlock = new CreativeTabBlock(0, "buildingBlocks");
	public static final CreativeTabs tabDecorations = new CreativeTabDeco(1, "decorations");
	public static final CreativeTabs tabRedstone = new CreativeTabRedstone(2, "redstone");
	public static final CreativeTabs tabTransport = new CreativeTabTransport(3, "transportation");
	public static final CreativeTabs tabMisc = new CreativeTabMisc(4, "misc");
	public static final CreativeTabs tabAllSearch = (new CreativeTabSearch(5, "search")).setBackgroundImageName("search.png");
	public static final CreativeTabs tabFood = new CreativeTabFood(6, "food");
	public static final CreativeTabs tabTools = new CreativeTabTools(7, "tools");
	public static final CreativeTabs tabCombat = new CreativeTabCombat(8, "combat");
	public static final CreativeTabs tabBrewing = new CreativeTabBrewing(9, "brewing");
	public static final CreativeTabs tabMaterials = new CreativeTabMaterial(10, "materials");
	public static final CreativeTabs tabInventory = (new CreativeTabInventory(11, "inventory")).setBackgroundImageName("survival_inv.png").setNoScrollbar().setNoTitle();
	private final int tabIndex;
	private final String tabLabel;
	private String backgroundImageName = "list_items.png";
	private boolean hasScrollbar = true;
	private boolean drawTitle = true;

	public CreativeTabs(int var1, String var2) {
		this.tabIndex = var1;
		this.tabLabel = var2;
		creativeTabArray[var1] = this;
	}

	public int getTabIndex() {
		return this.tabIndex;
	}

	public String getTabLabel() {
		return this.tabLabel;
	}

	public String getTranslatedTabLabel() {
		return StringTranslate.getInstance().translateKey("itemGroup." + this.getTabLabel());
	}

	public Item getTabIconItem() {
		return Item.itemsList[this.getTabIconItemIndex()];
	}

	public int getTabIconItemIndex() {
		return 1;
	}

	public String getBackgroundImageName() {
		return this.backgroundImageName;
	}

	public CreativeTabs setBackgroundImageName(String var1) {
		this.backgroundImageName = var1;
		return this;
	}

	public boolean drawInForegroundOfTab() {
		return this.drawTitle;
	}

	public CreativeTabs setNoTitle() {
		this.drawTitle = false;
		return this;
	}

	public boolean shouldHidePlayerInventory() {
		return this.hasScrollbar;
	}

	public CreativeTabs setNoScrollbar() {
		this.hasScrollbar = false;
		return this;
	}

	public int getTabColumn() {
		return this.tabIndex % 6;
	}

	public boolean isTabInFirstRow() {
		return this.tabIndex < 6;
	}

	public void displayAllReleventItems(List var1) {
		Item[] var2 = Item.itemsList;
		int var3 = var2.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			Item var5 = var2[var4];
			if(var5 != null && var5.getCreativeTab() == this) {
				var5.getSubItems(var5.itemID, this, var1);
			}
		}

	}

	public void func_92116_a(List var1, EnumEnchantmentType... var2) {
		Enchantment[] var3 = Enchantment.enchantmentsList;
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			Enchantment var6 = var3[var5];
			if(var6 != null && var6.type != null) {
				boolean var7 = false;

				for(int var8 = 0; var8 < var2.length && !var7; ++var8) {
					if(var6.type == var2[var8]) {
						var7 = true;
					}
				}

				if(var7) {
					var1.add(Item.enchantedBook.func_92111_a(new EnchantmentData(var6, var6.getMaxLevel())));
				}
			}
		}

	}
}
