package net.minecraft.src;

import java.util.List;

public class ItemSkull extends Item {
	private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
	public static final String[] field_94587_a = new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"};
	private Icon[] field_94586_c;

	public ItemSkull(int var1) {
		super(var1);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		if(var7 == 0) {
			return false;
		} else if(!var3.getBlockMaterial(var4, var5, var6).isSolid()) {
			return false;
		} else {
			if(var7 == 1) {
				++var5;
			}

			if(var7 == 2) {
				--var6;
			}

			if(var7 == 3) {
				++var6;
			}

			if(var7 == 4) {
				--var4;
			}

			if(var7 == 5) {
				++var4;
			}

			if(!var2.canPlayerEdit(var4, var5, var6, var7, var1)) {
				return false;
			} else if(!Block.skull.canPlaceBlockAt(var3, var4, var5, var6)) {
				return false;
			} else {
				var3.setBlock(var4, var5, var6, Block.skull.blockID, var7, 2);
				int var11 = 0;
				if(var7 == 1) {
					var11 = MathHelper.floor_double((double)(var2.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
				}

				TileEntity var12 = var3.getBlockTileEntity(var4, var5, var6);
				if(var12 != null && var12 instanceof TileEntitySkull) {
					String var13 = "";
					if(var1.hasTagCompound() && var1.getTagCompound().hasKey("SkullOwner")) {
						var13 = var1.getTagCompound().getString("SkullOwner");
					}

					((TileEntitySkull)var12).setSkullType(var1.getItemDamage(), var13);
					((TileEntitySkull)var12).setSkullRotation(var11);
					((BlockSkull)Block.skull).makeWither(var3, var4, var5, var6, (TileEntitySkull)var12);
				}

				--var1.stackSize;
				return true;
			}
		}
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		for(int var4 = 0; var4 < skullTypes.length; ++var4) {
			var3.add(new ItemStack(var1, 1, var4));
		}

	}

	public Icon getIconFromDamage(int var1) {
		if(var1 < 0 || var1 >= skullTypes.length) {
			var1 = 0;
		}

		return this.field_94586_c[var1];
	}

	public int getMetadata(int var1) {
		return var1;
	}

	public String getUnlocalizedName(ItemStack var1) {
		int var2 = var1.getItemDamage();
		if(var2 < 0 || var2 >= skullTypes.length) {
			var2 = 0;
		}

		return super.getUnlocalizedName() + "." + skullTypes[var2];
	}

	public String getItemDisplayName(ItemStack var1) {
		return var1.getItemDamage() == 3 && var1.hasTagCompound() && var1.getTagCompound().hasKey("SkullOwner") ? StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[]{var1.getTagCompound().getString("SkullOwner")}) : super.getItemDisplayName(var1);
	}

	public void registerIcons(IconRegister var1) {
		this.field_94586_c = new Icon[field_94587_a.length];

		for(int var2 = 0; var2 < field_94587_a.length; ++var2) {
			this.field_94586_c[var2] = var1.registerIcon(field_94587_a[var2]);
		}

	}
}
