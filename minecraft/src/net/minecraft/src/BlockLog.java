package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockLog extends Block {
	public static final String[] woodType = new String[]{"oak", "spruce", "birch", "jungle"};
	public static final String[] treeTextureTypes = new String[]{"tree_side", "tree_spruce", "tree_birch", "tree_jungle"};
	private Icon[] iconArray;
	private Icon tree_top;

	protected BlockLog(int var1) {
		super(var1, Material.wood);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public int getRenderType() {
		return 31;
	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.wood.blockID;
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
		byte var7 = 4;
		int var8 = var7 + 1;
		if(var1.checkChunksExist(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
			for(int var9 = -var7; var9 <= var7; ++var9) {
				for(int var10 = -var7; var10 <= var7; ++var10) {
					for(int var11 = -var7; var11 <= var7; ++var11) {
						int var12 = var1.getBlockId(var2 + var9, var3 + var10, var4 + var11);
						if(var12 == Block.leaves.blockID) {
							int var13 = var1.getBlockMetadata(var2 + var9, var3 + var10, var4 + var11);
							if((var13 & 8) == 0) {
								var1.setBlockMetadataWithNotify(var2 + var9, var3 + var10, var4 + var11, var13 | 8, 4);
							}
						}
					}
				}
			}
		}

	}

	public int onBlockPlaced(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
		int var10 = var9 & 3;
		byte var11 = 0;
		switch(var5) {
		case 0:
		case 1:
			var11 = 0;
			break;
		case 2:
		case 3:
			var11 = 8;
			break;
		case 4:
		case 5:
			var11 = 4;
		}

		return var10 | var11;
	}

	public Icon getIcon(int var1, int var2) {
		int var3 = var2 & 12;
		int var4 = var2 & 3;
		return var3 != 0 || var1 != 1 && var1 != 0 ? (var3 != 4 || var1 != 5 && var1 != 4 ? (var3 != 8 || var1 != 2 && var1 != 3 ? this.iconArray[var4] : this.tree_top) : this.tree_top) : this.tree_top;
	}

	public int damageDropped(int var1) {
		return var1 & 3;
	}

	public static int limitToValidMetadata(int var0) {
		return var0 & 3;
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
		var3.add(new ItemStack(var1, 1, 1));
		var3.add(new ItemStack(var1, 1, 2));
		var3.add(new ItemStack(var1, 1, 3));
	}

	protected ItemStack createStackedBlock(int var1) {
		return new ItemStack(this.blockID, 1, limitToValidMetadata(var1));
	}

	public void registerIcons(IconRegister var1) {
		this.tree_top = var1.registerIcon("tree_top");
		this.iconArray = new Icon[treeTextureTypes.length];

		for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
			this.iconArray[var2] = var1.registerIcon(treeTextureTypes[var2]);
		}

	}
}
