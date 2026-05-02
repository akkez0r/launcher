package net.minecraft.src;

public class BlockPumpkin extends BlockDirectional {
	private boolean blockType;
	private Icon field_94474_b;
	private Icon field_94475_c;

	protected BlockPumpkin(int var1, boolean var2) {
		super(var1, Material.pumpkin);
		this.setTickRandomly(true);
		this.blockType = var2;
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public Icon getIcon(int var1, int var2) {
		return var1 == 1 ? this.field_94474_b : (var1 == 0 ? this.field_94474_b : (var2 == 2 && var1 == 2 ? this.field_94475_c : (var2 == 3 && var1 == 5 ? this.field_94475_c : (var2 == 0 && var1 == 3 ? this.field_94475_c : (var2 == 1 && var1 == 4 ? this.field_94475_c : this.blockIcon)))));
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		if(var1.getBlockId(var2, var3 - 1, var4) == Block.blockSnow.blockID && var1.getBlockId(var2, var3 - 2, var4) == Block.blockSnow.blockID) {
			if(!var1.isRemote) {
				var1.setBlock(var2, var3, var4, 0, 0, 2);
				var1.setBlock(var2, var3 - 1, var4, 0, 0, 2);
				var1.setBlock(var2, var3 - 2, var4, 0, 0, 2);
				EntitySnowman var9 = new EntitySnowman(var1);
				var9.setLocationAndAngles((double)var2 + 0.5D, (double)var3 - 1.95D, (double)var4 + 0.5D, 0.0F, 0.0F);
				var1.spawnEntityInWorld(var9);
				var1.notifyBlockChange(var2, var3, var4, 0);
				var1.notifyBlockChange(var2, var3 - 1, var4, 0);
				var1.notifyBlockChange(var2, var3 - 2, var4, 0);
			}

			for(int var10 = 0; var10 < 120; ++var10) {
				var1.spawnParticle("snowshovel", (double)var2 + var1.rand.nextDouble(), (double)(var3 - 2) + var1.rand.nextDouble() * 2.5D, (double)var4 + var1.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			}
		} else if(var1.getBlockId(var2, var3 - 1, var4) == Block.blockIron.blockID && var1.getBlockId(var2, var3 - 2, var4) == Block.blockIron.blockID) {
			boolean var5 = var1.getBlockId(var2 - 1, var3 - 1, var4) == Block.blockIron.blockID && var1.getBlockId(var2 + 1, var3 - 1, var4) == Block.blockIron.blockID;
			boolean var6 = var1.getBlockId(var2, var3 - 1, var4 - 1) == Block.blockIron.blockID && var1.getBlockId(var2, var3 - 1, var4 + 1) == Block.blockIron.blockID;
			if(var5 || var6) {
				var1.setBlock(var2, var3, var4, 0, 0, 2);
				var1.setBlock(var2, var3 - 1, var4, 0, 0, 2);
				var1.setBlock(var2, var3 - 2, var4, 0, 0, 2);
				if(var5) {
					var1.setBlock(var2 - 1, var3 - 1, var4, 0, 0, 2);
					var1.setBlock(var2 + 1, var3 - 1, var4, 0, 0, 2);
				} else {
					var1.setBlock(var2, var3 - 1, var4 - 1, 0, 0, 2);
					var1.setBlock(var2, var3 - 1, var4 + 1, 0, 0, 2);
				}

				EntityIronGolem var7 = new EntityIronGolem(var1);
				var7.setPlayerCreated(true);
				var7.setLocationAndAngles((double)var2 + 0.5D, (double)var3 - 1.95D, (double)var4 + 0.5D, 0.0F, 0.0F);
				var1.spawnEntityInWorld(var7);

				for(int var8 = 0; var8 < 120; ++var8) {
					var1.spawnParticle("snowballpoof", (double)var2 + var1.rand.nextDouble(), (double)(var3 - 2) + var1.rand.nextDouble() * 3.9D, (double)var4 + var1.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
				}

				var1.notifyBlockChange(var2, var3, var4, 0);
				var1.notifyBlockChange(var2, var3 - 1, var4, 0);
				var1.notifyBlockChange(var2, var3 - 2, var4, 0);
				if(var5) {
					var1.notifyBlockChange(var2 - 1, var3 - 1, var4, 0);
					var1.notifyBlockChange(var2 + 1, var3 - 1, var4, 0);
				} else {
					var1.notifyBlockChange(var2, var3 - 1, var4 - 1, 0);
					var1.notifyBlockChange(var2, var3 - 1, var4 + 1, 0);
				}
			}
		}

	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		return (var5 == 0 || Block.blocksList[var5].blockMaterial.isReplaceable()) && var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4);
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
		int var7 = MathHelper.floor_double((double)(var5.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		var1.setBlockMetadataWithNotify(var2, var3, var4, var7, 2);
	}

	public void registerIcons(IconRegister var1) {
		this.field_94475_c = var1.registerIcon(this.blockType ? "pumpkin_jack" : "pumpkin_face");
		this.field_94474_b = var1.registerIcon("pumpkin_top");
		this.blockIcon = var1.registerIcon("pumpkin_side");
	}
}
