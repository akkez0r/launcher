package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ItemStack {
	public int stackSize;
	public int animationsToGo;
	public int itemID;
	public NBTTagCompound stackTagCompound;
	private int itemDamage;
	private EntityItemFrame itemFrame;

	public ItemStack(Block var1) {
		this((Block)var1, 1);
	}

	public ItemStack(Block var1, int var2) {
		this(var1.blockID, var2, 0);
	}

	public ItemStack(Block var1, int var2, int var3) {
		this(var1.blockID, var2, var3);
	}

	public ItemStack(Item var1) {
		this(var1.itemID, 1, 0);
	}

	public ItemStack(Item var1, int var2) {
		this(var1.itemID, var2, 0);
	}

	public ItemStack(Item var1, int var2, int var3) {
		this(var1.itemID, var2, var3);
	}

	public ItemStack(int var1, int var2, int var3) {
		this.stackSize = 0;
		this.itemFrame = null;
		this.itemID = var1;
		this.stackSize = var2;
		this.itemDamage = var3;
		if(this.itemDamage < 0) {
			this.itemDamage = 0;
		}

	}

	public static ItemStack loadItemStackFromNBT(NBTTagCompound var0) {
		ItemStack var1 = new ItemStack();
		var1.readFromNBT(var0);
		return var1.getItem() != null ? var1 : null;
	}

	private ItemStack() {
		this.stackSize = 0;
		this.itemFrame = null;
	}

	public ItemStack splitStack(int var1) {
		ItemStack var2 = new ItemStack(this.itemID, var1, this.itemDamage);
		if(this.stackTagCompound != null) {
			var2.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
		}

		this.stackSize -= var1;
		return var2;
	}

	public Item getItem() {
		return Item.itemsList[this.itemID];
	}

	public Icon getIconIndex() {
		return this.getItem().getIconIndex(this);
	}

	public int getItemSpriteNumber() {
		return this.getItem().getSpriteNumber();
	}

	public boolean tryPlaceItemIntoWorld(EntityPlayer var1, World var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9) {
		boolean var10 = this.getItem().onItemUse(this, var1, var2, var3, var4, var5, var6, var7, var8, var9);
		if(var10) {
			var1.addStat(StatList.objectUseStats[this.itemID], 1);
		}

		return var10;
	}

	public float getStrVsBlock(Block var1) {
		return this.getItem().getStrVsBlock(this, var1);
	}

	public ItemStack useItemRightClick(World var1, EntityPlayer var2) {
		return this.getItem().onItemRightClick(this, var1, var2);
	}

	public ItemStack onFoodEaten(World var1, EntityPlayer var2) {
		return this.getItem().onEaten(this, var1, var2);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound var1) {
		var1.setShort("id", (short)this.itemID);
		var1.setByte("Count", (byte)this.stackSize);
		var1.setShort("Damage", (short)this.itemDamage);
		if(this.stackTagCompound != null) {
			var1.setTag("tag", this.stackTagCompound);
		}

		return var1;
	}

	public void readFromNBT(NBTTagCompound var1) {
		this.itemID = var1.getShort("id");
		this.stackSize = var1.getByte("Count");
		this.itemDamage = var1.getShort("Damage");
		if(this.itemDamage < 0) {
			this.itemDamage = 0;
		}

		if(var1.hasKey("tag")) {
			this.stackTagCompound = var1.getCompoundTag("tag");
		}

	}

	public int getMaxStackSize() {
		return this.getItem().getItemStackLimit();
	}

	public boolean isStackable() {
		return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
	}

	public boolean isItemStackDamageable() {
		return Item.itemsList[this.itemID].getMaxDamage() > 0;
	}

	public boolean getHasSubtypes() {
		return Item.itemsList[this.itemID].getHasSubtypes();
	}

	public boolean isItemDamaged() {
		return this.isItemStackDamageable() && this.itemDamage > 0;
	}

	public int getItemDamageForDisplay() {
		return this.itemDamage;
	}

	public int getItemDamage() {
		return this.itemDamage;
	}

	public void setItemDamage(int var1) {
		this.itemDamage = var1;
		if(this.itemDamage < 0) {
			this.itemDamage = 0;
		}

	}

	public int getMaxDamage() {
		return Item.itemsList[this.itemID].getMaxDamage();
	}

	public boolean attemptDamageItem(int var1, Random var2) {
		if(!this.isItemStackDamageable()) {
			return false;
		} else {
			if(var1 > 0) {
				int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
				int var4 = 0;

				for(int var5 = 0; var3 > 0 && var5 < var1; ++var5) {
					if(EnchantmentDurability.negateDamage(this, var3, var2)) {
						++var4;
					}
				}

				var1 -= var4;
				if(var1 <= 0) {
					return false;
				}
			}

			this.itemDamage += var1;
			return this.itemDamage > this.getMaxDamage();
		}
	}

	public void damageItem(int var1, EntityLiving var2) {
		if(!(var2 instanceof EntityPlayer) || !((EntityPlayer)var2).capabilities.isCreativeMode) {
			if(this.isItemStackDamageable()) {
				if(this.attemptDamageItem(var1, var2.getRNG())) {
					var2.renderBrokenItemStack(this);
					if(var2 instanceof EntityPlayer) {
						((EntityPlayer)var2).addStat(StatList.objectBreakStats[this.itemID], 1);
					}

					--this.stackSize;
					if(this.stackSize < 0) {
						this.stackSize = 0;
					}

					this.itemDamage = 0;
				}

			}
		}
	}

	public void hitEntity(EntityLiving var1, EntityPlayer var2) {
		boolean var3 = Item.itemsList[this.itemID].hitEntity(this, var1, var2);
		if(var3) {
			var2.addStat(StatList.objectUseStats[this.itemID], 1);
		}

	}

	public void onBlockDestroyed(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
		boolean var7 = Item.itemsList[this.itemID].onBlockDestroyed(this, var1, var2, var3, var4, var5, var6);
		if(var7) {
			var6.addStat(StatList.objectUseStats[this.itemID], 1);
		}

	}

	public int getDamageVsEntity(Entity var1) {
		return Item.itemsList[this.itemID].getDamageVsEntity(var1);
	}

	public boolean canHarvestBlock(Block var1) {
		return Item.itemsList[this.itemID].canHarvestBlock(var1);
	}

	public boolean interactWith(EntityLiving var1) {
		return Item.itemsList[this.itemID].itemInteractionForEntity(this, var1);
	}

	public ItemStack copy() {
		ItemStack var1 = new ItemStack(this.itemID, this.stackSize, this.itemDamage);
		if(this.stackTagCompound != null) {
			var1.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
		}

		return var1;
	}

	public static boolean areItemStackTagsEqual(ItemStack var0, ItemStack var1) {
		return var0 == null && var1 == null ? true : (var0 != null && var1 != null ? (var0.stackTagCompound == null && var1.stackTagCompound != null ? false : var0.stackTagCompound == null || var0.stackTagCompound.equals(var1.stackTagCompound)) : false);
	}

	public static boolean areItemStacksEqual(ItemStack var0, ItemStack var1) {
		return var0 == null && var1 == null ? true : (var0 != null && var1 != null ? var0.isItemStackEqual(var1) : false);
	}

	private boolean isItemStackEqual(ItemStack var1) {
		return this.stackSize != var1.stackSize ? false : (this.itemID != var1.itemID ? false : (this.itemDamage != var1.itemDamage ? false : (this.stackTagCompound == null && var1.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(var1.stackTagCompound))));
	}

	public boolean isItemEqual(ItemStack var1) {
		return this.itemID == var1.itemID && this.itemDamage == var1.itemDamage;
	}

	public String getItemName() {
		return Item.itemsList[this.itemID].getUnlocalizedName(this);
	}

	public static ItemStack copyItemStack(ItemStack var0) {
		return var0 == null ? null : var0.copy();
	}

	public String toString() {
		return this.stackSize + "x" + Item.itemsList[this.itemID].getUnlocalizedName() + "@" + this.itemDamage;
	}

	public void updateAnimation(World var1, Entity var2, int var3, boolean var4) {
		if(this.animationsToGo > 0) {
			--this.animationsToGo;
		}

		Item.itemsList[this.itemID].onUpdate(this, var1, var2, var3, var4);
	}

	public void onCrafting(World var1, EntityPlayer var2, int var3) {
		var2.addStat(StatList.objectCraftStats[this.itemID], var3);
		Item.itemsList[this.itemID].onCreated(this, var1, var2);
	}

	public int getMaxItemUseDuration() {
		return this.getItem().getMaxItemUseDuration(this);
	}

	public EnumAction getItemUseAction() {
		return this.getItem().getItemUseAction(this);
	}

	public void onPlayerStoppedUsing(World var1, EntityPlayer var2, int var3) {
		this.getItem().onPlayerStoppedUsing(this, var1, var2, var3);
	}

	public boolean hasTagCompound() {
		return this.stackTagCompound != null;
	}

	public NBTTagCompound getTagCompound() {
		return this.stackTagCompound;
	}

	public NBTTagList getEnchantmentTagList() {
		return this.stackTagCompound == null ? null : (NBTTagList)this.stackTagCompound.getTag("ench");
	}

	public void setTagCompound(NBTTagCompound var1) {
		this.stackTagCompound = var1;
	}

	public String getDisplayName() {
		String var1 = this.getItem().getItemDisplayName(this);
		if(this.stackTagCompound != null && this.stackTagCompound.hasKey("display")) {
			NBTTagCompound var2 = this.stackTagCompound.getCompoundTag("display");
			if(var2.hasKey("Name")) {
				var1 = var2.getString("Name");
			}
		}

		return var1;
	}

	public void setItemName(String var1) {
		if(this.stackTagCompound == null) {
			this.stackTagCompound = new NBTTagCompound("tag");
		}

		if(!this.stackTagCompound.hasKey("display")) {
			this.stackTagCompound.setCompoundTag("display", new NBTTagCompound());
		}

		this.stackTagCompound.getCompoundTag("display").setString("Name", var1);
	}

	public boolean hasDisplayName() {
		return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display") ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name"));
	}

	public List getTooltip(EntityPlayer var1, boolean var2) {
		ArrayList var3 = new ArrayList();
		Item var4 = Item.itemsList[this.itemID];
		String var5 = this.getDisplayName();
		if(this.hasDisplayName()) {
			var5 = EnumChatFormatting.ITALIC + var5 + EnumChatFormatting.RESET;
		}

		if(var2) {
			String var6 = "";
			if(var5.length() > 0) {
				var5 = var5 + " (";
				var6 = ")";
			}

			if(this.getHasSubtypes()) {
				var5 = var5 + String.format("#%04d/%d%s", new Object[]{Integer.valueOf(this.itemID), Integer.valueOf(this.itemDamage), var6});
			} else {
				var5 = var5 + String.format("#%04d%s", new Object[]{Integer.valueOf(this.itemID), var6});
			}
		} else if(!this.hasDisplayName() && this.itemID == Item.map.itemID) {
			var5 = var5 + " #" + this.itemDamage;
		}

		var3.add(var5);
		var4.addInformation(this, var1, var3, var2);
		if(this.hasTagCompound()) {
			NBTTagList var10 = this.getEnchantmentTagList();
			if(var10 != null) {
				for(int var7 = 0; var7 < var10.tagCount(); ++var7) {
					short var8 = ((NBTTagCompound)var10.tagAt(var7)).getShort("id");
					short var9 = ((NBTTagCompound)var10.tagAt(var7)).getShort("lvl");
					if(Enchantment.enchantmentsList[var8] != null) {
						var3.add(Enchantment.enchantmentsList[var8].getTranslatedName(var9));
					}
				}
			}

			if(this.stackTagCompound.hasKey("display")) {
				NBTTagCompound var11 = this.stackTagCompound.getCompoundTag("display");
				if(var11.hasKey("color")) {
					if(var2) {
						var3.add("Color: #" + Integer.toHexString(var11.getInteger("color")).toUpperCase());
					} else {
						var3.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
					}
				}

				if(var11.hasKey("Lore")) {
					NBTTagList var12 = var11.getTagList("Lore");
					if(var12.tagCount() > 0) {
						for(int var13 = 0; var13 < var12.tagCount(); ++var13) {
							var3.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + ((NBTTagString)var12.tagAt(var13)).data);
						}
					}
				}
			}
		}

		if(var2 && this.isItemDamaged()) {
			var3.add("Durability: " + (this.getMaxDamage() - this.getItemDamageForDisplay()) + " / " + this.getMaxDamage());
		}

		return var3;
	}

	public boolean hasEffect() {
		return this.getItem().hasEffect(this);
	}

	public EnumRarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isItemEnchantable() {
		return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
	}

	public void addEnchantment(Enchantment var1, int var2) {
		if(this.stackTagCompound == null) {
			this.setTagCompound(new NBTTagCompound());
		}

		if(!this.stackTagCompound.hasKey("ench")) {
			this.stackTagCompound.setTag("ench", new NBTTagList("ench"));
		}

		NBTTagList var3 = (NBTTagList)this.stackTagCompound.getTag("ench");
		NBTTagCompound var4 = new NBTTagCompound();
		var4.setShort("id", (short)var1.effectId);
		var4.setShort("lvl", (short)((byte)var2));
		var3.appendTag(var4);
	}

	public boolean isItemEnchanted() {
		return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench");
	}

	public void setTagInfo(String var1, NBTBase var2) {
		if(this.stackTagCompound == null) {
			this.setTagCompound(new NBTTagCompound());
		}

		this.stackTagCompound.setTag(var1, var2);
	}

	public boolean func_82835_x() {
		return this.getItem().func_82788_x();
	}

	public boolean isOnItemFrame() {
		return this.itemFrame != null;
	}

	public void setItemFrame(EntityItemFrame var1) {
		this.itemFrame = var1;
	}

	public EntityItemFrame getItemFrame() {
		return this.itemFrame;
	}

	public int getRepairCost() {
		return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost") ? this.stackTagCompound.getInteger("RepairCost") : 0;
	}

	public void setRepairCost(int var1) {
		if(!this.hasTagCompound()) {
			this.stackTagCompound = new NBTTagCompound("tag");
		}

		this.stackTagCompound.setInteger("RepairCost", var1);
	}
}
