package net.minecraft.src;

public class TileEntityEnderChest extends TileEntity {
	public float lidAngle;
	public float prevLidAngle;
	public int numUsingPlayers;
	private int ticksSinceSync;

	public void updateEntity() {
		super.updateEntity();
		if(++this.ticksSinceSync % 20 * 4 == 0) {
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Block.enderChest.blockID, 1, this.numUsingPlayers);
		}

		this.prevLidAngle = this.lidAngle;
		float var1 = 0.1F;
		double var4;
		if(this.numUsingPlayers > 0 && this.lidAngle == 0.0F) {
			double var2 = (double)this.xCoord + 0.5D;
			var4 = (double)this.zCoord + 0.5D;
			this.worldObj.playSoundEffect(var2, (double)this.yCoord + 0.5D, var4, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
			float var8 = this.lidAngle;
			if(this.numUsingPlayers > 0) {
				this.lidAngle += var1;
			} else {
				this.lidAngle -= var1;
			}

			if(this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float var3 = 0.5F;
			if(this.lidAngle < var3 && var8 >= var3) {
				var4 = (double)this.xCoord + 0.5D;
				double var6 = (double)this.zCoord + 0.5D;
				this.worldObj.playSoundEffect(var4, (double)this.yCoord + 0.5D, var6, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if(this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}

	}

	public boolean receiveClientEvent(int var1, int var2) {
		if(var1 == 1) {
			this.numUsingPlayers = var2;
			return true;
		} else {
			return super.receiveClientEvent(var1, var2);
		}
	}

	public void invalidate() {
		this.updateContainingBlockInfo();
		super.invalidate();
	}

	public void openChest() {
		++this.numUsingPlayers;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Block.enderChest.blockID, 1, this.numUsingPlayers);
	}

	public void closeChest() {
		--this.numUsingPlayers;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Block.enderChest.blockID, 1, this.numUsingPlayers);
	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
}
