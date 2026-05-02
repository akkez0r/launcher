package net.minecraft.src;

public class BlockPoweredOre extends BlockOreStorage {
	public BlockPoweredOre(int var1) {
		super(var1);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public boolean canProvidePower() {
		return true;
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return 15;
	}
}
