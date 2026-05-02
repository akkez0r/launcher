package net.minecraft.src;

public class NpcMerchant implements IMerchant {
	private InventoryMerchant theMerchantInventory;
	private EntityPlayer customer;
	private MerchantRecipeList recipeList;

	public NpcMerchant(EntityPlayer var1) {
		this.customer = var1;
		this.theMerchantInventory = new InventoryMerchant(var1, this);
	}

	public EntityPlayer getCustomer() {
		return this.customer;
	}

	public void setCustomer(EntityPlayer var1) {
	}

	public MerchantRecipeList getRecipes(EntityPlayer var1) {
		return this.recipeList;
	}

	public void setRecipes(MerchantRecipeList var1) {
		this.recipeList = var1;
	}

	public void useRecipe(MerchantRecipe var1) {
	}
}
