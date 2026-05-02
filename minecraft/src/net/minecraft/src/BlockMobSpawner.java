package net.minecraft.src;

import java.util.Random;

public class BlockMobSpawner extends BlockContainer {
	protected BlockMobSpawner(int var1) {
		super(var1, Material.rock);
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityMobSpawner();
	}

	public int idDropped(int var1, Random var2, int var3) {
		return 0;
	}

	public int quantityDropped(Random var1) {
		return 0;
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, var7);
		int var8 = 15 + var1.rand.nextInt(15) + var1.rand.nextInt(15);
		this.dropXpOnBlockBreak(var1, var2, var3, var4, var8);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return 0;
	}
}
