package net.minecraft.src;

import java.util.List;

public class BlockAnvil extends BlockSand {
	public static final String[] statuses = new String[]{"intact", "slightlyDamaged", "veryDamaged"};
	private static final String[] anvilIconNames = new String[]{"anvil_top", "anvil_top_damaged_1", "anvil_top_damaged_2"};
	public int field_82521_b = 0;
	private Icon[] iconArray;

	protected BlockAnvil(int var1) {
		super(var1, Material.anvil);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public Icon getIcon(int var1, int var2) {
		if(this.field_82521_b == 3 && var1 == 1) {
			int var3 = (var2 >> 2) % this.iconArray.length;
			return this.iconArray[var3];
		} else {
			return this.blockIcon;
		}
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon("anvil_base");
		this.iconArray = new Icon[anvilIconNames.length];

		for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
			this.iconArray[var2] = var1.registerIcon(anvilIconNames[var2]);
		}

	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int var8 = var1.getBlockMetadata(var2, var3, var4) >> 2;
		++var7;
		var7 %= 4;
		if(var7 == 0) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 2 | var8 << 2, 2);
		}

		if(var7 == 1) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 3 | var8 << 2, 2);
		}

		if(var7 == 2) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 0 | var8 << 2, 2);
		}

		if(var7 == 3) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, 1 | var8 << 2, 2);
		}

	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			var5.displayGUIAnvil(var2, var3, var4);
			return true;
		}
	}

	public int getRenderType() {
		return 35;
	}

	public int damageDropped(int var1) {
		return var1 >> 2;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4) & 3;
		if(var5 != 3 && var5 != 1) {
			this.setBlockBounds(2.0F / 16.0F, 0.0F, 0.0F, 14.0F / 16.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 2.0F / 16.0F, 1.0F, 1.0F, 14.0F / 16.0F);
		}

	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
		var3.add(new ItemStack(var1, 1, 1));
		var3.add(new ItemStack(var1, 1, 2));
	}

	protected void onStartFalling(EntityFallingSand var1) {
		var1.setIsAnvil(true);
	}

	public void onFinishFalling(World var1, int var2, int var3, int var4, int var5) {
		var1.playAuxSFX(1022, var2, var3, var4, 0);
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return true;
	}
}
