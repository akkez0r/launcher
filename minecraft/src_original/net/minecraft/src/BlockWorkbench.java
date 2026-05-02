package net.minecraft.src;

public class BlockWorkbench extends Block {
	private Icon workbenchIconTop;
	private Icon workbenchIconFront;

	protected BlockWorkbench(int var1) {
		super(var1, Material.wood);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.workbenchIconTop : (var1 == 0 ? Block.planks.getBlockTextureFromSide(var1) : (var1 != 2 && var1 != 4 ? this.blockIcon : this.workbenchIconFront));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("workbench_side");
		this.workbenchIconTop = var1.registerIcon("workbench_top");
		this.workbenchIconFront = var1.registerIcon("workbench_front");
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			var5.displayGUIWorkbench(var2, var3, var4);
			return true;
		}
	}
}
