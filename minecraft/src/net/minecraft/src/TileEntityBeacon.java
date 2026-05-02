package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class TileEntityBeacon extends TileEntity implements IInventory {
	public static final Potion[][] effectsList = new Potion[][]{{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};
	private long field_82137_b;
	private float field_82138_c;
	private boolean isBeaconActive;
	private int levels = -1;
	private int primaryEffect;
	private int secondaryEffect;
	private ItemStack payment;
	private String field_94048_i;

	public void updateEntity() {
		if(this.worldObj.getTotalWorldTime() % 80L == 0L) {
			this.updateState();
			this.addEffectsToPlayers();
		}

	}

	private void addEffectsToPlayers() {
		if(this.isBeaconActive && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
			double var1 = (double)(this.levels * 10 + 10);
			byte var3 = 0;
			if(this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
				var3 = 1;
			}

			AxisAlignedBB var4 = AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(var1, var1, var1);
			var4.maxY = (double)this.worldObj.getHeight();
			List var5 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, var4);
			Iterator var6 = var5.iterator();

			EntityPlayer var7;
			while(var6.hasNext()) {
				var7 = (EntityPlayer)var6.next();
				var7.addPotionEffect(new PotionEffect(this.primaryEffect, 180, var3, true));
			}

			if(this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
				var6 = var5.iterator();

				while(var6.hasNext()) {
					var7 = (EntityPlayer)var6.next();
					var7.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true));
				}
			}
		}

	}

	private void updateState() {
		if(!this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord)) {
			this.isBeaconActive = false;
			this.levels = 0;
		} else {
			this.isBeaconActive = true;
			this.levels = 0;

			for(int var1 = 1; var1 <= 4; this.levels = var1++) {
				int var2 = this.yCoord - var1;
				if(var2 < 0) {
					break;
				}

				boolean var3 = true;

				for(int var4 = this.xCoord - var1; var4 <= this.xCoord + var1 && var3; ++var4) {
					for(int var5 = this.zCoord - var1; var5 <= this.zCoord + var1; ++var5) {
						int var6 = this.worldObj.getBlockId(var4, var2, var5);
						if(var6 != Block.blockEmerald.blockID && var6 != Block.blockGold.blockID && var6 != Block.blockDiamond.blockID && var6 != Block.blockIron.blockID) {
							var3 = false;
							break;
						}
					}
				}

				if(!var3) {
					break;
				}
			}

			if(this.levels == 0) {
				this.isBeaconActive = false;
			}
		}

	}

	public float func_82125_v_() {
		if(!this.isBeaconActive) {
			return 0.0F;
		} else {
			int var1 = (int)(this.worldObj.getTotalWorldTime() - this.field_82137_b);
			this.field_82137_b = this.worldObj.getTotalWorldTime();
			if(var1 > 1) {
				this.field_82138_c -= (float)var1 / 40.0F;
				if(this.field_82138_c < 0.0F) {
					this.field_82138_c = 0.0F;
				}
			}

			this.field_82138_c += 0.025F;
			if(this.field_82138_c > 1.0F) {
				this.field_82138_c = 1.0F;
			}

			return this.field_82138_c;
		}
	}

	public int getPrimaryEffect() {
		return this.primaryEffect;
	}

	public int getSecondaryEffect() {
		return this.secondaryEffect;
	}

	public int getLevels() {
		return this.levels;
	}

	public void setLevels(int var1) {
		this.levels = var1;
	}

	public void setPrimaryEffect(int var1) {
		this.primaryEffect = 0;

		for(int var2 = 0; var2 < this.levels && var2 < 3; ++var2) {
			Potion[] var3 = effectsList[var2];
			int var4 = var3.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				Potion var6 = var3[var5];
				if(var6.id == var1) {
					this.primaryEffect = var1;
					return;
				}
			}
		}

	}

	public void setSecondaryEffect(int var1) {
		this.secondaryEffect = 0;
		if(this.levels >= 4) {
			for(int var2 = 0; var2 < 4; ++var2) {
				Potion[] var3 = effectsList[var2];
				int var4 = var3.length;

				for(int var5 = 0; var5 < var4; ++var5) {
					Potion var6 = var3[var5];
					if(var6.id == var1) {
						this.secondaryEffect = var1;
						return;
					}
				}
			}
		}

	}

	public Packet getDescriptionPacket() {
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeToNBT(var1);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 3, var1);
	}

	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	public void readFromNBT(NBTTagCompound var1) {
		super.readFromNBT(var1);
		this.primaryEffect = var1.getInteger("Primary");
		this.secondaryEffect = var1.getInteger("Secondary");
		this.levels = var1.getInteger("Levels");
	}

	public void writeToNBT(NBTTagCompound var1) {
		super.writeToNBT(var1);
		var1.setInteger("Primary", this.primaryEffect);
		var1.setInteger("Secondary", this.secondaryEffect);
		var1.setInteger("Levels", this.levels);
	}

	public int getSizeInventory() {
		return 1;
	}

	public ItemStack getStackInSlot(int var1) {
		return var1 == 0 ? this.payment : null;
	}

	public ItemStack decrStackSize(int var1, int var2) {
		if(var1 == 0 && this.payment != null) {
			if(var2 >= this.payment.stackSize) {
				ItemStack var3 = this.payment;
				this.payment = null;
				return var3;
			} else {
				this.payment.stackSize -= var2;
				return new ItemStack(this.payment.itemID, var2, this.payment.getItemDamage());
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int var1) {
		if(var1 == 0 && this.payment != null) {
			ItemStack var2 = this.payment;
			this.payment = null;
			return var2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int var1, ItemStack var2) {
		if(var1 == 0) {
			this.payment = var2;
		}

	}

	public String getInvName() {
		return this.isInvNameLocalized() ? this.field_94048_i : "container.beacon";
	}

	public boolean isInvNameLocalized() {
		return this.field_94048_i != null && this.field_94048_i.length() > 0;
	}

	public void func_94047_a(String var1) {
		this.field_94048_i = var1;
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public boolean isStackValidForSlot(int var1, ItemStack var2) {
		return var2.itemID == Item.emerald.itemID || var2.itemID == Item.diamond.itemID || var2.itemID == Item.ingotGold.itemID || var2.itemID == Item.ingotIron.itemID;
	}
}
