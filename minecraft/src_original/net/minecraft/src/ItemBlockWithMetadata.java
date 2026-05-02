package net.minecraft.src;

public class ItemBlockWithMetadata extends ItemBlock {
	private Block theBlock;

	public ItemBlockWithMetadata(int var1, Block var2) {
		super(var1);
		this.theBlock = var2;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public Icon getIconFromDamage(int var1) {
		return this.theBlock.getIcon(2, var1);
	}

	public int getMetadata(int var1) {
		return var1;
	}
}
