package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityFallingSand extends Entity {
	public int blockID;
	public int metadata;
	public int fallTime;
	public boolean shouldDropItem;
	private boolean isBreakingAnvil;
	private boolean isAnvil;
	private int fallHurtMax;
	private float fallHurtAmount;
	public NBTTagCompound fallingBlockTileEntityData;

	public EntityFallingSand(World var1) {
		super(var1);
		this.fallTime = 0;
		this.shouldDropItem = true;
		this.isBreakingAnvil = false;
		this.isAnvil = false;
		this.fallHurtMax = 40;
		this.fallHurtAmount = 2.0F;
		this.fallingBlockTileEntityData = null;
	}

	public EntityFallingSand(World var1, double var2, double var4, double var6, int var8) {
		this(var1, var2, var4, var6, var8, 0);
	}

	public EntityFallingSand(World var1, double var2, double var4, double var6, int var8, int var9) {
		super(var1);
		this.fallTime = 0;
		this.shouldDropItem = true;
		this.isBreakingAnvil = false;
		this.isAnvil = false;
		this.fallHurtMax = 40;
		this.fallHurtAmount = 2.0F;
		this.fallingBlockTileEntityData = null;
		this.blockID = var8;
		this.metadata = var9;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(var2, var4, var6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = var2;
		this.prevPosY = var4;
		this.prevPosZ = var6;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		if(this.blockID == 0) {
			this.setDead();
		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			++this.fallTime;
			this.motionY -= (double)0.04F;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.98F;
			this.motionY *= (double)0.98F;
			this.motionZ *= (double)0.98F;
			if(!this.worldObj.isRemote) {
				int var1 = MathHelper.floor_double(this.posX);
				int var2 = MathHelper.floor_double(this.posY);
				int var3 = MathHelper.floor_double(this.posZ);
				if(this.fallTime == 1) {
					if(this.worldObj.getBlockId(var1, var2, var3) != this.blockID) {
						this.setDead();
						return;
					}

					this.worldObj.setBlockToAir(var1, var2, var3);
				}

				if(this.onGround) {
					this.motionX *= (double)0.7F;
					this.motionZ *= (double)0.7F;
					this.motionY *= -0.5D;
					if(this.worldObj.getBlockId(var1, var2, var3) != Block.pistonMoving.blockID) {
						this.setDead();
						if(!this.isBreakingAnvil && this.worldObj.canPlaceEntityOnSide(this.blockID, var1, var2, var3, true, 1, (Entity)null, (ItemStack)null) && !BlockSand.canFallBelow(this.worldObj, var1, var2 - 1, var3) && this.worldObj.setBlock(var1, var2, var3, this.blockID, this.metadata, 3)) {
							if(Block.blocksList[this.blockID] instanceof BlockSand) {
								((BlockSand)Block.blocksList[this.blockID]).onFinishFalling(this.worldObj, var1, var2, var3, this.metadata);
							}

							if(this.fallingBlockTileEntityData != null && Block.blocksList[this.blockID] instanceof ITileEntityProvider) {
								TileEntity var4 = this.worldObj.getBlockTileEntity(var1, var2, var3);
								if(var4 != null) {
									NBTTagCompound var5 = new NBTTagCompound();
									var4.writeToNBT(var5);
									Iterator var6 = this.fallingBlockTileEntityData.getTags().iterator();

									while(var6.hasNext()) {
										NBTBase var7 = (NBTBase)var6.next();
										if(!var7.getName().equals("x") && !var7.getName().equals("y") && !var7.getName().equals("z")) {
											var5.setTag(var7.getName(), var7.copy());
										}
									}

									var4.readFromNBT(var5);
									var4.onInventoryChanged();
								}
							}
						} else if(this.shouldDropItem && !this.isBreakingAnvil) {
							this.entityDropItem(new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].damageDropped(this.metadata)), 0.0F);
						}
					}
				} else if(this.fallTime > 100 && !this.worldObj.isRemote && (var2 < 1 || var2 > 256) || this.fallTime > 600) {
					if(this.shouldDropItem) {
						this.entityDropItem(new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].damageDropped(this.metadata)), 0.0F);
					}

					this.setDead();
				}
			}

		}
	}

	protected void fall(float var1) {
		if(this.isAnvil) {
			int var2 = MathHelper.ceiling_float_int(var1 - 1.0F);
			if(var2 > 0) {
				ArrayList var3 = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
				DamageSource var4 = this.blockID == Block.anvil.blockID ? DamageSource.anvil : DamageSource.fallingBlock;
				Iterator var5 = var3.iterator();

				while(var5.hasNext()) {
					Entity var6 = (Entity)var5.next();
					var6.attackEntityFrom(var4, Math.min(MathHelper.floor_float((float)var2 * this.fallHurtAmount), this.fallHurtMax));
				}

				if(this.blockID == Block.anvil.blockID && (double)this.rand.nextFloat() < (double)0.05F + (double)var2 * 0.05D) {
					int var7 = this.metadata >> 2;
					int var8 = this.metadata & 3;
					++var7;
					if(var7 > 2) {
						this.isBreakingAnvil = true;
					} else {
						this.metadata = var8 | var7 << 2;
					}
				}
			}
		}

	}

	protected void writeEntityToNBT(NBTTagCompound var1) {
		var1.setByte("Tile", (byte)this.blockID);
		var1.setInteger("TileID", this.blockID);
		var1.setByte("Data", (byte)this.metadata);
		var1.setByte("Time", (byte)this.fallTime);
		var1.setBoolean("DropItem", this.shouldDropItem);
		var1.setBoolean("HurtEntities", this.isAnvil);
		var1.setFloat("FallHurtAmount", this.fallHurtAmount);
		var1.setInteger("FallHurtMax", this.fallHurtMax);
		if(this.fallingBlockTileEntityData != null) {
			var1.setCompoundTag("TileEntityData", this.fallingBlockTileEntityData);
		}

	}

	protected void readEntityFromNBT(NBTTagCompound var1) {
		if(var1.hasKey("TileID")) {
			this.blockID = var1.getInteger("TileID");
		} else {
			this.blockID = var1.getByte("Tile") & 255;
		}

		this.metadata = var1.getByte("Data") & 255;
		this.fallTime = var1.getByte("Time") & 255;
		if(var1.hasKey("HurtEntities")) {
			this.isAnvil = var1.getBoolean("HurtEntities");
			this.fallHurtAmount = var1.getFloat("FallHurtAmount");
			this.fallHurtMax = var1.getInteger("FallHurtMax");
		} else if(this.blockID == Block.anvil.blockID) {
			this.isAnvil = true;
		}

		if(var1.hasKey("DropItem")) {
			this.shouldDropItem = var1.getBoolean("DropItem");
		}

		if(var1.hasKey("TileEntityData")) {
			this.fallingBlockTileEntityData = var1.getCompoundTag("TileEntityData");
		}

		if(this.blockID == 0) {
			this.blockID = Block.sand.blockID;
		}

	}

	public float getShadowSize() {
		return 0.0F;
	}

	public World getWorld() {
		return this.worldObj;
	}

	public void setIsAnvil(boolean var1) {
		this.isAnvil = var1;
	}

	public boolean canRenderOnFire() {
		return false;
	}

	public void func_85029_a(CrashReportCategory var1) {
		super.func_85029_a(var1);
		var1.addCrashSection("Immitating block ID", Integer.valueOf(this.blockID));
		var1.addCrashSection("Immitating block data", Integer.valueOf(this.metadata));
	}
}
