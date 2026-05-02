package net.minecraft.src;

import java.util.List;

public class EntityMinecartHopper extends EntityMinecartContainer implements Hopper {
	private boolean isBlocked = true;
	private int transferTicker = -1;

	public EntityMinecartHopper(World var1) {
		super(var1);
	}

	public EntityMinecartHopper(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6);
	}

	public int getMinecartType() {
		return 5;
	}

	public Block getDefaultDisplayTile() {
		return Block.hopperBlock;
	}

	public int getDefaultDisplayTileOffset() {
		return 1;
	}

	public int getSizeInventory() {
		return 5;
	}

	public boolean interact(EntityPlayer var1) {
		if(!this.worldObj.isRemote) {
			var1.displayGUIHopperMinecart(this);
		}

		return true;
	}

	public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
		boolean var5 = !var4;
		if(var5 != this.getBlocked()) {
			this.setBlocked(var5);
		}

	}

	public boolean getBlocked() {
		return this.isBlocked;
	}

	public void setBlocked(boolean var1) {
		this.isBlocked = var1;
	}

	public World getWorldObj() {
		return this.worldObj;
	}

	public double getXPos() {
		return this.posX;
	}

	public double getYPos() {
		return this.posY;
	}

	public double getZPos() {
		return this.posZ;
	}

	public void onUpdate() {
		super.onUpdate();
		if(!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
			--this.transferTicker;
			if(!this.canTransfer()) {
				this.setTransferTicker(0);
				if(this.func_96112_aD()) {
					this.setTransferTicker(4);
					this.onInventoryChanged();
				}
			}
		}

	}

	public boolean func_96112_aD() {
		if(TileEntityHopper.suckItemsIntoHopper(this)) {
			return true;
		} else {
			List var1 = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);
			if(var1.size() > 0) {
				TileEntityHopper.func_96114_a(this, (EntityItem)var1.get(0));
			}

			return false;
		}
	}

	public void killMinecart(DamageSource var1) {
		super.killMinecart(var1);
		this.dropItemWithOffset(Block.hopperBlock.blockID, 1, 0.0F);
	}

	protected void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("TransferCooldown", this.transferTicker);
	}

	protected void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.transferTicker = var1.getInteger("TransferCooldown");
	}

	public void setTransferTicker(int var1) {
		this.transferTicker = var1;
	}

	public boolean canTransfer() {
		return this.transferTicker > 0;
	}
}
