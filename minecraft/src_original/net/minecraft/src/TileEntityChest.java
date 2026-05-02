package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class TileEntityChest extends TileEntity implements IInventory {
	private ItemStack[] chestContents = new ItemStack[36];
	public boolean adjacentChestChecked = false;
	public TileEntityChest adjacentChestZNeg;
	public TileEntityChest adjacentChestXPos;
	public TileEntityChest adjacentChestXNeg;
	public TileEntityChest adjacentChestZPosition;
	public float lidAngle;
	public float prevLidAngle;
	public int numUsingPlayers;
	private int ticksSinceSync;
	private int field_94046_i = -1;
	private String field_94045_s;

	public int getSizeInventory() {
		return 27;
	}

	public ItemStack getStackInSlot(int var1) {
		return this.chestContents[var1];
	}

	public ItemStack decrStackSize(int var1, int var2) {
		if(this.chestContents[var1] != null) {
			ItemStack var3;
			if(this.chestContents[var1].stackSize <= var2) {
				var3 = this.chestContents[var1];
				this.chestContents[var1] = null;
				this.onInventoryChanged();
				return var3;
			} else {
				var3 = this.chestContents[var1].splitStack(var2);
				if(this.chestContents[var1].stackSize == 0) {
					this.chestContents[var1] = null;
				}

				this.onInventoryChanged();
				return var3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int var1) {
		if(this.chestContents[var1] != null) {
			ItemStack var2 = this.chestContents[var1];
			this.chestContents[var1] = null;
			return var2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.chestContents[var1] = var2;
		if(var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
			var2.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return this.isInvNameLocalized() ? this.field_94045_s : "container.chest";
	}

	public boolean isInvNameLocalized() {
		return this.field_94045_s != null && this.field_94045_s.length() > 0;
	}

	public void func_94043_a(String var1) {
		this.field_94045_s = var1;
	}

	public void readFromNBT(NBTTagCompound var1) {
		super.readFromNBT(var1);
		NBTTagList var2 = var1.getTagList("Items");
		this.chestContents = new ItemStack[this.getSizeInventory()];
		if(var1.hasKey("CustomName")) {
			this.field_94045_s = var1.getString("CustomName");
		}

		for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
			int var5 = var4.getByte("Slot") & 255;
			if(var5 >= 0 && var5 < this.chestContents.length) {
				this.chestContents[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}

	}

	public void writeToNBT(NBTTagCompound var1) {
		super.writeToNBT(var1);
		NBTTagList var2 = new NBTTagList();

		for(int var3 = 0; var3 < this.chestContents.length; ++var3) {
			if(this.chestContents[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.chestContents[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		var1.setTag("Items", var2);
		if(this.isInvNameLocalized()) {
			var1.setString("CustomName", this.field_94045_s);
		}

	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		this.adjacentChestChecked = false;
	}

	private void func_90009_a(TileEntityChest var1, int var2) {
		if(var1.isInvalid()) {
			this.adjacentChestChecked = false;
		} else if(this.adjacentChestChecked) {
			switch(var2) {
			case 0:
				if(this.adjacentChestZPosition != var1) {
					this.adjacentChestChecked = false;
				}
				break;
			case 1:
				if(this.adjacentChestXNeg != var1) {
					this.adjacentChestChecked = false;
				}
				break;
			case 2:
				if(this.adjacentChestZNeg != var1) {
					this.adjacentChestChecked = false;
				}
				break;
			case 3:
				if(this.adjacentChestXPos != var1) {
					this.adjacentChestChecked = false;
				}
			}
		}

	}

	public void checkForAdjacentChests() {
		if(!this.adjacentChestChecked) {
			this.adjacentChestChecked = true;
			this.adjacentChestZNeg = null;
			this.adjacentChestXPos = null;
			this.adjacentChestXNeg = null;
			this.adjacentChestZPosition = null;
			if(this.func_94044_a(this.xCoord - 1, this.yCoord, this.zCoord)) {
				this.adjacentChestXNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
			}

			if(this.func_94044_a(this.xCoord + 1, this.yCoord, this.zCoord)) {
				this.adjacentChestXPos = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
			}

			if(this.func_94044_a(this.xCoord, this.yCoord, this.zCoord - 1)) {
				this.adjacentChestZNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
			}

			if(this.func_94044_a(this.xCoord, this.yCoord, this.zCoord + 1)) {
				this.adjacentChestZPosition = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
			}

			if(this.adjacentChestZNeg != null) {
				this.adjacentChestZNeg.func_90009_a(this, 0);
			}

			if(this.adjacentChestZPosition != null) {
				this.adjacentChestZPosition.func_90009_a(this, 2);
			}

			if(this.adjacentChestXPos != null) {
				this.adjacentChestXPos.func_90009_a(this, 1);
			}

			if(this.adjacentChestXNeg != null) {
				this.adjacentChestXNeg.func_90009_a(this, 3);
			}

		}
	}

	private boolean func_94044_a(int var1, int var2, int var3) {
		Block var4 = Block.blocksList[this.worldObj.getBlockId(var1, var2, var3)];
		return var4 != null && var4 instanceof BlockChest ? ((BlockChest)var4).isTrapped == this.func_98041_l() : false;
	}

	public void updateEntity() {
		super.updateEntity();
		this.checkForAdjacentChests();
		++this.ticksSinceSync;
		float var1;
		if(!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
			this.numUsingPlayers = 0;
			var1 = 5.0F;
			List var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)this.xCoord - var1), (double)((float)this.yCoord - var1), (double)((float)this.zCoord - var1), (double)((float)(this.xCoord + 1) + var1), (double)((float)(this.yCoord + 1) + var1), (double)((float)(this.zCoord + 1) + var1)));
			Iterator var3 = var2.iterator();

			label93:
			while(true) {
				IInventory var5;
				do {
					EntityPlayer var4;
					do {
						if(!var3.hasNext()) {
							break label93;
						}

						var4 = (EntityPlayer)var3.next();
					} while(!(var4.openContainer instanceof ContainerChest));

					var5 = ((ContainerChest)var4.openContainer).getLowerChestInventory();
				} while(var5 != this && (!(var5 instanceof InventoryLargeChest) || !((InventoryLargeChest)var5).isPartOfLargeChest(this)));

				++this.numUsingPlayers;
			}
		}

		this.prevLidAngle = this.lidAngle;
		var1 = 0.1F;
		double var11;
		if(this.numUsingPlayers > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
			double var8 = (double)this.xCoord + 0.5D;
			var11 = (double)this.zCoord + 0.5D;
			if(this.adjacentChestZPosition != null) {
				var11 += 0.5D;
			}

			if(this.adjacentChestXPos != null) {
				var8 += 0.5D;
			}

			this.worldObj.playSoundEffect(var8, (double)this.yCoord + 0.5D, var11, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
			float var9 = this.lidAngle;
			if(this.numUsingPlayers > 0) {
				this.lidAngle += var1;
			} else {
				this.lidAngle -= var1;
			}

			if(this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float var10 = 0.5F;
			if(this.lidAngle < var10 && var9 >= var10 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
				var11 = (double)this.xCoord + 0.5D;
				double var6 = (double)this.zCoord + 0.5D;
				if(this.adjacentChestZPosition != null) {
					var6 += 0.5D;
				}

				if(this.adjacentChestXPos != null) {
					var11 += 0.5D;
				}

				this.worldObj.playSoundEffect(var11, (double)this.yCoord + 0.5D, var6, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
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

	public void openChest() {
		if(this.numUsingPlayers < 0) {
			this.numUsingPlayers = 0;
		}

		++this.numUsingPlayers;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
	}

	public void closeChest() {
		if(this.getBlockType() != null && this.getBlockType() instanceof BlockChest) {
			--this.numUsingPlayers;
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
		}
	}

	public boolean isStackValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
		this.checkForAdjacentChests();
	}

	public int func_98041_l() {
		if(this.field_94046_i == -1) {
			if(this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
				return 0;
			}

			this.field_94046_i = ((BlockChest)this.getBlockType()).isTrapped;
		}

		return this.field_94046_i;
	}
}
