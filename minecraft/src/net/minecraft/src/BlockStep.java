package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockStep extends BlockHalfSlab {
	public static final String[] blockStepTypes = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
	private Icon theIcon;

	public BlockStep(int var1, boolean var2) {
		super(var1, var2, Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public Icon getIcon(int var1, int var2) {
		int var3 = var2 & 7;
		if(this.isDoubleSlab && (var2 & 8) != 0) {
			var1 = 1;
		}

		return var3 == 0 ? (var1 != 1 && var1 != 0 ? this.theIcon : this.blockIcon) : (var3 == 1 ? Block.sandStone.getBlockTextureFromSide(var1) : (var3 == 2 ? Block.planks.getBlockTextureFromSide(var1) : (var3 == 3 ? Block.cobblestone.getBlockTextureFromSide(var1) : (var3 == 4 ? Block.brick.getBlockTextureFromSide(var1) : (var3 == 5 ? Block.stoneBrick.getIcon(var1, 0) : (var3 == 6 ? Block.netherBrick.getBlockTextureFromSide(1) : (var3 == 7 ? Block.blockNetherQuartz.getBlockTextureFromSide(var1) : this.blockIcon)))))));
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("stoneslab_top");
		this.theIcon = var1.registerIcon("stoneslab_side");
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.stoneSingleSlab.blockID;
	}

	protected ItemStack createStackedBlock(int var1) {
		return new ItemStack(Block.stoneSingleSlab.blockID, 2, var1 & 7);
	}

	public String getFullSlabName(int var1) {
		if(var1 < 0 || var1 >= blockStepTypes.length) {
			var1 = 0;
		}

		return super.getUnlocalizedName() + "." + blockStepTypes[var1];
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		if(var1 != Block.stoneDoubleSlab.blockID) {
			for(int var4 = 0; var4 <= 7; ++var4) {
				if(var4 != 2) {
					var3.add(new ItemStack(var1, 1, var4));
				}
			}

		}
	}
}
