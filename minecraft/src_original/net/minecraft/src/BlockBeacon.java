package net.minecraft.src;

public class BlockBeacon extends BlockContainer {
	private Icon theIcon;

	public BlockBeacon(int var1) {
		super(var1, Material.glass);
		this.setHardness(3.0F);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityBeacon();
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		if(var1.isRemote) {
			return true;
		} else {
			TileEntityBeacon var10 = (TileEntityBeacon)var1.getBlockTileEntity(var2, var3, var4);
			if(var10 != null) {
				var5.displayGUIBeacon(var10);
			}

			return true;
		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 34;
	}

	public void registerIcons(IconRegister var1) {
		super.registerIcons(var1);
		this.theIcon = var1.registerIcon("beacon");
	}

	public Icon getBeaconIcon() {
		return this.theIcon;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		super.onBlockPlacedBy(var1, var2, var3, var4, var5, var6);
		if(var6.hasDisplayName()) {
			((TileEntityBeacon)var1.getBlockTileEntity(var2, var3, var4)).func_94047_a(var6.getDisplayName());
		}

	}
}
