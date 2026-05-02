package net.minecraft.src;

public class RecipesCrafting {
	public void addRecipes(CraftingManager var1) {
		var1.addRecipe(new ItemStack(Block.chest), new Object[]{"###", "# #", "###", Character.valueOf('#'), Block.planks});
		var1.addRecipe(new ItemStack(Block.chestTrapped), new Object[]{"#-", Character.valueOf('#'), Block.chest, Character.valueOf('-'), Block.tripWireSource});
		var1.addRecipe(new ItemStack(Block.enderChest), new Object[]{"###", "#E#", "###", Character.valueOf('#'), Block.obsidian, Character.valueOf('E'), Item.eyeOfEnder});
		var1.addRecipe(new ItemStack(Block.furnaceIdle), new Object[]{"###", "# #", "###", Character.valueOf('#'), Block.cobblestone});
		var1.addRecipe(new ItemStack(Block.workbench), new Object[]{"##", "##", Character.valueOf('#'), Block.planks});
		var1.addRecipe(new ItemStack(Block.sandStone), new Object[]{"##", "##", Character.valueOf('#'), Block.sand});
		var1.addRecipe(new ItemStack(Block.sandStone, 4, 2), new Object[]{"##", "##", Character.valueOf('#'), Block.sandStone});
		var1.addRecipe(new ItemStack(Block.sandStone, 1, 1), new Object[]{"#", "#", Character.valueOf('#'), new ItemStack(Block.stoneSingleSlab, 1, 1)});
		var1.addRecipe(new ItemStack(Block.blockNetherQuartz, 1, 1), new Object[]{"#", "#", Character.valueOf('#'), new ItemStack(Block.stoneSingleSlab, 1, 7)});
		var1.addRecipe(new ItemStack(Block.blockNetherQuartz, 2, 2), new Object[]{"#", "#", Character.valueOf('#'), new ItemStack(Block.blockNetherQuartz, 1, 0)});
		var1.addRecipe(new ItemStack(Block.stoneBrick, 4), new Object[]{"##", "##", Character.valueOf('#'), Block.stone});
		var1.addRecipe(new ItemStack(Block.fenceIron, 16), new Object[]{"###", "###", Character.valueOf('#'), Item.ingotIron});
		var1.addRecipe(new ItemStack(Block.thinGlass, 16), new Object[]{"###", "###", Character.valueOf('#'), Block.glass});
		var1.addRecipe(new ItemStack(Block.redstoneLampIdle, 1), new Object[]{" R ", "RGR", " R ", Character.valueOf('R'), Item.redstone, Character.valueOf('G'), Block.glowStone});
		var1.addRecipe(new ItemStack(Block.beacon, 1), new Object[]{"GGG", "GSG", "OOO", Character.valueOf('G'), Block.glass, Character.valueOf('S'), Item.netherStar, Character.valueOf('O'), Block.obsidian});
		var1.addRecipe(new ItemStack(Block.netherBrick, 1), new Object[]{"NN", "NN", Character.valueOf('N'), Item.netherrackBrick});
	}
}
