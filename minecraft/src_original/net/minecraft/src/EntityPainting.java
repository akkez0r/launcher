package net.minecraft.src;

import java.util.ArrayList;

public class EntityPainting extends EntityHanging {
	public EnumArt art;

	public EntityPainting(World var1) {
		super(var1);
	}

	public EntityPainting(World var1, int var2, int var3, int var4, int var5) {
		super(var1, var2, var3, var4, var5);
		ArrayList var6 = new ArrayList();
		EnumArt[] var7 = EnumArt.values();
		int var8 = var7.length;

		for(int var9 = 0; var9 < var8; ++var9) {
			EnumArt var10 = var7[var9];
			this.art = var10;
			this.setDirection(var5);
			if(this.onValidSurface()) {
				var6.add(var10);
			}
		}

		if(!var6.isEmpty()) {
			this.art = (EnumArt)var6.get(this.rand.nextInt(var6.size()));
		}

		this.setDirection(var5);
	}

	public EntityPainting(World var1, int var2, int var3, int var4, int var5, String var6) {
		this(var1, var2, var3, var4, var5);
		EnumArt[] var7 = EnumArt.values();
		int var8 = var7.length;

		for(int var9 = 0; var9 < var8; ++var9) {
			EnumArt var10 = var7[var9];
			if(var10.title.equals(var6)) {
				this.art = var10;
				break;
			}
		}

		this.setDirection(var5);
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		var1.setString("Motive", this.art.title);
		super.writeEntityToNBT(var1);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		String var2 = var1.getString("Motive");
		EnumArt[] var3 = EnumArt.values();
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			EnumArt var6 = var3[var5];
			if(var6.title.equals(var2)) {
				this.art = var6;
			}
		}

		if(this.art == null) {
			this.art = EnumArt.Kebab;
		}

		super.readEntityFromNBT(var1);
	}

	public int func_82329_d() {
		return this.art.sizeX;
	}

	public int func_82330_g() {
		return this.art.sizeY;
	}

	public void dropItemStack() {
		this.entityDropItem(new ItemStack(Item.painting), 0.0F);
	}
}
