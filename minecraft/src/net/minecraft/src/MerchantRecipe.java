package net.minecraft.src;

public class MerchantRecipe {
	private ItemStack itemToBuy;
	private ItemStack secondItemToBuy;
	private ItemStack itemToSell;
	private int toolUses;
	private int maxTradeUses;

	public MerchantRecipe(NBTTagCompound var1) {
		this.readFromTags(var1);
	}

	public MerchantRecipe(ItemStack var1, ItemStack var2, ItemStack var3) {
		this.itemToBuy = var1;
		this.secondItemToBuy = var2;
		this.itemToSell = var3;
		this.maxTradeUses = 7;
	}

	public MerchantRecipe(ItemStack var1, ItemStack var2) {
		this(var1, (ItemStack)null, var2);
	}

	public MerchantRecipe(ItemStack var1, Item var2) {
		this(var1, new ItemStack(var2));
	}

	public ItemStack getItemToBuy() {
		return this.itemToBuy;
	}

	public ItemStack getSecondItemToBuy() {
		return this.secondItemToBuy;
	}

	public boolean hasSecondItemToBuy() {
		return this.secondItemToBuy != null;
	}

	public ItemStack getItemToSell() {
		return this.itemToSell;
	}

	public boolean hasSameIDsAs(MerchantRecipe var1) {
		return this.itemToBuy.itemID == var1.itemToBuy.itemID && this.itemToSell.itemID == var1.itemToSell.itemID ? this.secondItemToBuy == null && var1.secondItemToBuy == null || this.secondItemToBuy != null && var1.secondItemToBuy != null && this.secondItemToBuy.itemID == var1.secondItemToBuy.itemID : false;
	}

	public boolean hasSameItemsAs(MerchantRecipe var1) {
		return this.hasSameIDsAs(var1) && (this.itemToBuy.stackSize < var1.itemToBuy.stackSize || this.secondItemToBuy != null && this.secondItemToBuy.stackSize < var1.secondItemToBuy.stackSize);
	}

	public void incrementToolUses() {
		++this.toolUses;
	}

	public void func_82783_a(int var1) {
		this.maxTradeUses += var1;
	}

	public boolean func_82784_g() {
		return this.toolUses >= this.maxTradeUses;
	}

	public void func_82785_h() {
		this.toolUses = this.maxTradeUses;
	}

	public void readFromTags(NBTTagCompound var1) {
		NBTTagCompound var2 = var1.getCompoundTag("buy");
		this.itemToBuy = ItemStack.loadItemStackFromNBT(var2);
		NBTTagCompound var3 = var1.getCompoundTag("sell");
		this.itemToSell = ItemStack.loadItemStackFromNBT(var3);
		if(var1.hasKey("buyB")) {
			this.secondItemToBuy = ItemStack.loadItemStackFromNBT(var1.getCompoundTag("buyB"));
		}

		if(var1.hasKey("uses")) {
			this.toolUses = var1.getInteger("uses");
		}

		if(var1.hasKey("maxUses")) {
			this.maxTradeUses = var1.getInteger("maxUses");
		} else {
			this.maxTradeUses = 7;
		}

	}

	public NBTTagCompound writeToTags() {
		NBTTagCompound var1 = new NBTTagCompound();
		var1.setCompoundTag("buy", this.itemToBuy.writeToNBT(new NBTTagCompound("buy")));
		var1.setCompoundTag("sell", this.itemToSell.writeToNBT(new NBTTagCompound("sell")));
		if(this.secondItemToBuy != null) {
			var1.setCompoundTag("buyB", this.secondItemToBuy.writeToNBT(new NBTTagCompound("buyB")));
		}

		var1.setInteger("uses", this.toolUses);
		var1.setInteger("maxUses", this.maxTradeUses);
		return var1;
	}
}
