package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public abstract class EntityMinecart extends Entity {
	private boolean isInReverse;
	private final IUpdatePlayerListBox field_82344_g;
	private String entityName;
	private static final int[][][] matrix = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
	private int turnProgress;
	private double minecartX;
	private double minecartY;
	private double minecartZ;
	private double minecartYaw;
	private double minecartPitch;
	private double velocityX;
	private double velocityY;
	private double velocityZ;

	public EntityMinecart(World var1) {
		super(var1);
		this.isInReverse = false;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.7F);
		this.yOffset = this.height / 2.0F;
		this.field_82344_g = var1 != null ? var1.func_82735_a(this) : null;
	}

	public static EntityMinecart createMinecart(World var0, double var1, double var3, double var5, int var7) {
		switch(var7) {
		case 1:
			return new EntityMinecartChest(var0, var1, var3, var5);
		case 2:
			return new EntityMinecartFurnace(var0, var1, var3, var5);
		case 3:
			return new EntityMinecartTNT(var0, var1, var3, var5);
		case 4:
			return new EntityMinecartMobSpawner(var0, var1, var3, var5);
		case 5:
			return new EntityMinecartHopper(var0, var1, var3, var5);
		default:
			return new EntityMinecartEmpty(var0, var1, var3, var5);
		}
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Integer(0));
		this.dataWatcher.addObject(20, new Integer(0));
		this.dataWatcher.addObject(21, new Integer(6));
		this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
	}

	public AxisAlignedBB getCollisionBox(Entity var1) {
		return var1.canBePushed() ? var1.boundingBox : null;
	}

	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	public boolean canBePushed() {
		return true;
	}

	public EntityMinecart(World var1, double var2, double var4, double var6) {
		this(var1);
		this.setPosition(var2, var4 + (double)this.yOffset, var6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = var2;
		this.prevPosY = var4;
		this.prevPosZ = var6;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.0D - (double)0.3F;
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(!this.worldObj.isRemote && !this.isDead) {
			if(this.isEntityInvulnerable()) {
				return false;
			} else {
				this.setRollingDirection(-this.getRollingDirection());
				this.setRollingAmplitude(10);
				this.setBeenAttacked();
				this.setDamage(this.getDamage() + var2 * 10);
				boolean var3 = var1.getEntity() instanceof EntityPlayer && ((EntityPlayer)var1.getEntity()).capabilities.isCreativeMode;
				if(var3 || this.getDamage() > 40) {
					if(this.riddenByEntity != null) {
						this.riddenByEntity.mountEntity(this);
					}

					if(var3 && !this.isInvNameLocalized()) {
						this.setDead();
					} else {
						this.killMinecart(var1);
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}

	public void killMinecart(DamageSource var1) {
		this.setDead();
		ItemStack var2 = new ItemStack(Item.minecartEmpty, 1);
		if(this.entityName != null) {
			var2.setItemName(this.entityName);
		}

		this.entityDropItem(var2, 0.0F);
	}

	public void performHurtAnimation() {
		this.setRollingDirection(-this.getRollingDirection());
		this.setRollingAmplitude(10);
		this.setDamage(this.getDamage() + this.getDamage() * 10);
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void setDead() {
		super.setDead();
		if(this.field_82344_g != null) {
			this.field_82344_g.update();
		}

	}

	public void onUpdate() {
		if(this.field_82344_g != null) {
			this.field_82344_g.update();
		}

		if(this.getRollingAmplitude() > 0) {
			this.setRollingAmplitude(this.getRollingAmplitude() - 1);
		}

		if(this.getDamage() > 0) {
			this.setDamage(this.getDamage() - 1);
		}

		if(this.posY < -64.0D) {
			this.kill();
		}

		int var2;
		if(!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
			this.worldObj.theProfiler.startSection("portal");
			MinecraftServer var1 = ((WorldServer)this.worldObj).getMinecraftServer();
			var2 = this.getMaxInPortalTime();
			if(this.inPortal) {
				if(var1.getAllowNether()) {
					if(this.ridingEntity == null && this.field_82153_h++ >= var2) {
						this.field_82153_h = var2;
						this.timeUntilPortal = this.getPortalCooldown();
						byte var3;
						if(this.worldObj.provider.dimensionId == -1) {
							var3 = 0;
						} else {
							var3 = -1;
						}

						this.travelToDimension(var3);
					}

					this.inPortal = false;
				}
			} else {
				if(this.field_82153_h > 0) {
					this.field_82153_h -= 4;
				}

				if(this.field_82153_h < 0) {
					this.field_82153_h = 0;
				}
			}

			if(this.timeUntilPortal > 0) {
				--this.timeUntilPortal;
			}

			this.worldObj.theProfiler.endSection();
		}

		if(this.worldObj.isRemote) {
			if(this.turnProgress > 0) {
				double var19 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
				double var21 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
				double var5 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
				double var7 = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
				this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.turnProgress);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
				--this.turnProgress;
				this.setPosition(var19, var21, var5);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				this.setPosition(this.posX, this.posY, this.posZ);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.motionY -= (double)0.04F;
			int var18 = MathHelper.floor_double(this.posX);
			var2 = MathHelper.floor_double(this.posY);
			int var20 = MathHelper.floor_double(this.posZ);
			if(BlockRailBase.isRailBlockAt(this.worldObj, var18, var2 - 1, var20)) {
				--var2;
			}

			double var4 = 0.4D;
			double var6 = 1.0D / 128.0D;
			int var8 = this.worldObj.getBlockId(var18, var2, var20);
			if(BlockRailBase.isRailBlock(var8)) {
				int var9 = this.worldObj.getBlockMetadata(var18, var2, var20);
				this.updateOnTrack(var18, var2, var20, var4, var6, var8, var9);
				if(var8 == Block.railActivator.blockID) {
					this.onActivatorRailPass(var18, var2, var20, (var9 & 8) != 0);
				}
			} else {
				this.func_94088_b(var4);
			}

			this.doBlockCollisions();
			this.rotationPitch = 0.0F;
			double var22 = this.prevPosX - this.posX;
			double var11 = this.prevPosZ - this.posZ;
			if(var22 * var22 + var11 * var11 > 0.001D) {
				this.rotationYaw = (float)(Math.atan2(var11, var22) * 180.0D / Math.PI);
				if(this.isInReverse) {
					this.rotationYaw += 180.0F;
				}
			}

			double var13 = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
			if(var13 < -170.0D || var13 >= 170.0D) {
				this.rotationYaw += 180.0F;
				this.isInReverse = !this.isInReverse;
			}

			this.setRotation(this.rotationYaw, this.rotationPitch);
			List var15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)0.2F, 0.0D, (double)0.2F));
			if(var15 != null && !var15.isEmpty()) {
				for(int var16 = 0; var16 < var15.size(); ++var16) {
					Entity var17 = (Entity)var15.get(var16);
					if(var17 != this.riddenByEntity && var17.canBePushed() && var17 instanceof EntityMinecart) {
						var17.applyEntityCollision(this);
					}
				}
			}

			if(this.riddenByEntity != null && this.riddenByEntity.isDead) {
				if(this.riddenByEntity.ridingEntity == this) {
					this.riddenByEntity.ridingEntity = null;
				}

				this.riddenByEntity = null;
			}

		}
	}

	public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
	}

	protected void func_94088_b(double var1) {
		if(this.motionX < -var1) {
			this.motionX = -var1;
		}

		if(this.motionX > var1) {
			this.motionX = var1;
		}

		if(this.motionZ < -var1) {
			this.motionZ = -var1;
		}

		if(this.motionZ > var1) {
			this.motionZ = var1;
		}

		if(this.onGround) {
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(!this.onGround) {
			this.motionX *= (double)0.95F;
			this.motionY *= (double)0.95F;
			this.motionZ *= (double)0.95F;
		}

	}

	protected void updateOnTrack(int var1, int var2, int var3, double var4, double var6, int var8, int var9) {
		this.fallDistance = 0.0F;
		Vec3 var10 = this.func_70489_a(this.posX, this.posY, this.posZ);
		this.posY = (double)var2;
		boolean var11 = false;
		boolean var12 = false;
		if(var8 == Block.railPowered.blockID) {
			var11 = (var9 & 8) != 0;
			var12 = !var11;
		}

		if(((BlockRailBase)Block.blocksList[var8]).isPowered()) {
			var9 &= 7;
		}

		if(var9 >= 2 && var9 <= 5) {
			this.posY = (double)(var2 + 1);
		}

		if(var9 == 2) {
			this.motionX -= var6;
		}

		if(var9 == 3) {
			this.motionX += var6;
		}

		if(var9 == 4) {
			this.motionZ += var6;
		}

		if(var9 == 5) {
			this.motionZ -= var6;
		}

		int[][] var13 = matrix[var9];
		double var14 = (double)(var13[1][0] - var13[0][0]);
		double var16 = (double)(var13[1][2] - var13[0][2]);
		double var18 = Math.sqrt(var14 * var14 + var16 * var16);
		double var20 = this.motionX * var14 + this.motionZ * var16;
		if(var20 < 0.0D) {
			var14 = -var14;
			var16 = -var16;
		}

		double var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		if(var22 > 2.0D) {
			var22 = 2.0D;
		}

		this.motionX = var22 * var14 / var18;
		this.motionZ = var22 * var16 / var18;
		double var24;
		double var26;
		if(this.riddenByEntity != null) {
			var24 = this.riddenByEntity.motionX * this.riddenByEntity.motionX + this.riddenByEntity.motionZ * this.riddenByEntity.motionZ;
			var26 = this.motionX * this.motionX + this.motionZ * this.motionZ;
			if(var24 > 1.0E-4D && var26 < 0.01D) {
				this.motionX += this.riddenByEntity.motionX * 0.1D;
				this.motionZ += this.riddenByEntity.motionZ * 0.1D;
				var12 = false;
			}
		}

		if(var12) {
			var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if(var24 < 0.03D) {
				this.motionX *= 0.0D;
				this.motionY *= 0.0D;
				this.motionZ *= 0.0D;
			} else {
				this.motionX *= 0.5D;
				this.motionY *= 0.0D;
				this.motionZ *= 0.5D;
			}
		}

		var24 = 0.0D;
		var26 = (double)var1 + 0.5D + (double)var13[0][0] * 0.5D;
		double var28 = (double)var3 + 0.5D + (double)var13[0][2] * 0.5D;
		double var30 = (double)var1 + 0.5D + (double)var13[1][0] * 0.5D;
		double var32 = (double)var3 + 0.5D + (double)var13[1][2] * 0.5D;
		var14 = var30 - var26;
		var16 = var32 - var28;
		double var34;
		double var36;
		if(var14 == 0.0D) {
			this.posX = (double)var1 + 0.5D;
			var24 = this.posZ - (double)var3;
		} else if(var16 == 0.0D) {
			this.posZ = (double)var3 + 0.5D;
			var24 = this.posX - (double)var1;
		} else {
			var34 = this.posX - var26;
			var36 = this.posZ - var28;
			var24 = (var34 * var14 + var36 * var16) * 2.0D;
		}

		this.posX = var26 + var14 * var24;
		this.posZ = var28 + var16 * var24;
		this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);
		var34 = this.motionX;
		var36 = this.motionZ;
		if(this.riddenByEntity != null) {
			var34 *= 0.75D;
			var36 *= 0.75D;
		}

		if(var34 < -var4) {
			var34 = -var4;
		}

		if(var34 > var4) {
			var34 = var4;
		}

		if(var36 < -var4) {
			var36 = -var4;
		}

		if(var36 > var4) {
			var36 = var4;
		}

		this.moveEntity(var34, 0.0D, var36);
		if(var13[0][1] != 0 && MathHelper.floor_double(this.posX) - var1 == var13[0][0] && MathHelper.floor_double(this.posZ) - var3 == var13[0][2]) {
			this.setPosition(this.posX, this.posY + (double)var13[0][1], this.posZ);
		} else if(var13[1][1] != 0 && MathHelper.floor_double(this.posX) - var1 == var13[1][0] && MathHelper.floor_double(this.posZ) - var3 == var13[1][2]) {
			this.setPosition(this.posX, this.posY + (double)var13[1][1], this.posZ);
		}

		this.applyDrag();
		Vec3 var38 = this.func_70489_a(this.posX, this.posY, this.posZ);
		if(var38 != null && var10 != null) {
			double var39 = (var10.yCoord - var38.yCoord) * 0.05D;
			var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if(var22 > 0.0D) {
				this.motionX = this.motionX / var22 * (var22 + var39);
				this.motionZ = this.motionZ / var22 * (var22 + var39);
			}

			this.setPosition(this.posX, var38.yCoord, this.posZ);
		}

		int var45 = MathHelper.floor_double(this.posX);
		int var40 = MathHelper.floor_double(this.posZ);
		if(var45 != var1 || var40 != var3) {
			var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.motionX = var22 * (double)(var45 - var1);
			this.motionZ = var22 * (double)(var40 - var3);
		}

		if(var11) {
			double var41 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if(var41 > 0.01D) {
				double var43 = 0.06D;
				this.motionX += this.motionX / var41 * var43;
				this.motionZ += this.motionZ / var41 * var43;
			} else if(var9 == 1) {
				if(this.worldObj.isBlockNormalCube(var1 - 1, var2, var3)) {
					this.motionX = 0.02D;
				} else if(this.worldObj.isBlockNormalCube(var1 + 1, var2, var3)) {
					this.motionX = -0.02D;
				}
			} else if(var9 == 0) {
				if(this.worldObj.isBlockNormalCube(var1, var2, var3 - 1)) {
					this.motionZ = 0.02D;
				} else if(this.worldObj.isBlockNormalCube(var1, var2, var3 + 1)) {
					this.motionZ = -0.02D;
				}
			}
		}

	}

	protected void applyDrag() {
		if(this.riddenByEntity != null) {
			this.motionX *= (double)0.997F;
			this.motionY *= 0.0D;
			this.motionZ *= (double)0.997F;
		} else {
			this.motionX *= (double)0.96F;
			this.motionY *= 0.0D;
			this.motionZ *= (double)0.96F;
		}

	}

	public Vec3 func_70495_a(double var1, double var3, double var5, double var7) {
		int var9 = MathHelper.floor_double(var1);
		int var10 = MathHelper.floor_double(var3);
		int var11 = MathHelper.floor_double(var5);
		if(BlockRailBase.isRailBlockAt(this.worldObj, var9, var10 - 1, var11)) {
			--var10;
		}

		int var12 = this.worldObj.getBlockId(var9, var10, var11);
		if(!BlockRailBase.isRailBlock(var12)) {
			return null;
		} else {
			int var13 = this.worldObj.getBlockMetadata(var9, var10, var11);
			if(((BlockRailBase)Block.blocksList[var12]).isPowered()) {
				var13 &= 7;
			}

			var3 = (double)var10;
			if(var13 >= 2 && var13 <= 5) {
				var3 = (double)(var10 + 1);
			}

			int[][] var14 = matrix[var13];
			double var15 = (double)(var14[1][0] - var14[0][0]);
			double var17 = (double)(var14[1][2] - var14[0][2]);
			double var19 = Math.sqrt(var15 * var15 + var17 * var17);
			var15 /= var19;
			var17 /= var19;
			var1 += var15 * var7;
			var5 += var17 * var7;
			if(var14[0][1] != 0 && MathHelper.floor_double(var1) - var9 == var14[0][0] && MathHelper.floor_double(var5) - var11 == var14[0][2]) {
				var3 += (double)var14[0][1];
			} else if(var14[1][1] != 0 && MathHelper.floor_double(var1) - var9 == var14[1][0] && MathHelper.floor_double(var5) - var11 == var14[1][2]) {
				var3 += (double)var14[1][1];
			}

			return this.func_70489_a(var1, var3, var5);
		}
	}

	public Vec3 func_70489_a(double var1, double var3, double var5) {
		int var7 = MathHelper.floor_double(var1);
		int var8 = MathHelper.floor_double(var3);
		int var9 = MathHelper.floor_double(var5);
		if(BlockRailBase.isRailBlockAt(this.worldObj, var7, var8 - 1, var9)) {
			--var8;
		}

		int var10 = this.worldObj.getBlockId(var7, var8, var9);
		if(BlockRailBase.isRailBlock(var10)) {
			int var11 = this.worldObj.getBlockMetadata(var7, var8, var9);
			var3 = (double)var8;
			if(((BlockRailBase)Block.blocksList[var10]).isPowered()) {
				var11 &= 7;
			}

			if(var11 >= 2 && var11 <= 5) {
				var3 = (double)(var8 + 1);
			}

			int[][] var12 = matrix[var11];
			double var13 = 0.0D;
			double var15 = (double)var7 + 0.5D + (double)var12[0][0] * 0.5D;
			double var17 = (double)var8 + 0.5D + (double)var12[0][1] * 0.5D;
			double var19 = (double)var9 + 0.5D + (double)var12[0][2] * 0.5D;
			double var21 = (double)var7 + 0.5D + (double)var12[1][0] * 0.5D;
			double var23 = (double)var8 + 0.5D + (double)var12[1][1] * 0.5D;
			double var25 = (double)var9 + 0.5D + (double)var12[1][2] * 0.5D;
			double var27 = var21 - var15;
			double var29 = (var23 - var17) * 2.0D;
			double var31 = var25 - var19;
			if(var27 == 0.0D) {
				var1 = (double)var7 + 0.5D;
				var13 = var5 - (double)var9;
			} else if(var31 == 0.0D) {
				var5 = (double)var9 + 0.5D;
				var13 = var1 - (double)var7;
			} else {
				double var33 = var1 - var15;
				double var35 = var5 - var19;
				var13 = (var33 * var27 + var35 * var31) * 2.0D;
			}

			var1 = var15 + var27 * var13;
			var3 = var17 + var29 * var13;
			var5 = var19 + var31 * var13;
			if(var29 < 0.0D) {
				++var3;
			}

			if(var29 > 0.0D) {
				var3 += 0.5D;
			}

			return this.worldObj.getWorldVec3Pool().getVecFromPool(var1, var3, var5);
		} else {
			return null;
		}
	}

	protected void readEntityFromNBT(NBTTagCompound var1) {
		if(var1.getBoolean("CustomDisplayTile")) {
			this.setDisplayTile(var1.getInteger("DisplayTile"));
			this.setDisplayTileData(var1.getInteger("DisplayData"));
			this.setDisplayTileOffset(var1.getInteger("DisplayOffset"));
		}

		if(var1.hasKey("CustomName") && var1.getString("CustomName").length() > 0) {
			this.entityName = var1.getString("CustomName");
		}

	}

	protected void writeEntityToNBT(NBTTagCompound var1) {
		if(this.hasDisplayTile()) {
			var1.setBoolean("CustomDisplayTile", true);
			var1.setInteger("DisplayTile", this.getDisplayTile() == null ? 0 : this.getDisplayTile().blockID);
			var1.setInteger("DisplayData", this.getDisplayTileData());
			var1.setInteger("DisplayOffset", this.getDisplayTileOffset());
		}

		if(this.entityName != null && this.entityName.length() > 0) {
			var1.setString("CustomName", this.entityName);
		}

	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void applyEntityCollision(Entity var1) {
		if(!this.worldObj.isRemote) {
			if(var1 != this.riddenByEntity) {
				if(var1 instanceof EntityLiving && !(var1 instanceof EntityPlayer) && !(var1 instanceof EntityIronGolem) && this.getMinecartType() == 0 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D && this.riddenByEntity == null && var1.ridingEntity == null) {
					var1.mountEntity(this);
				}

				double var2 = var1.posX - this.posX;
				double var4 = var1.posZ - this.posZ;
				double var6 = var2 * var2 + var4 * var4;
				if(var6 >= (double)1.0E-4F) {
					var6 = (double)MathHelper.sqrt_double(var6);
					var2 /= var6;
					var4 /= var6;
					double var8 = 1.0D / var6;
					if(var8 > 1.0D) {
						var8 = 1.0D;
					}

					var2 *= var8;
					var4 *= var8;
					var2 *= (double)0.1F;
					var4 *= (double)0.1F;
					var2 *= (double)(1.0F - this.entityCollisionReduction);
					var4 *= (double)(1.0F - this.entityCollisionReduction);
					var2 *= 0.5D;
					var4 *= 0.5D;
					if(var1 instanceof EntityMinecart) {
						double var10 = var1.posX - this.posX;
						double var12 = var1.posZ - this.posZ;
						Vec3 var14 = this.worldObj.getWorldVec3Pool().getVecFromPool(var10, 0.0D, var12).normalize();
						Vec3 var15 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F)).normalize();
						double var16 = Math.abs(var14.dotProduct(var15));
						if(var16 < (double)0.8F) {
							return;
						}

						double var18 = var1.motionX + this.motionX;
						double var20 = var1.motionZ + this.motionZ;
						if(((EntityMinecart)var1).getMinecartType() == 2 && this.getMinecartType() != 2) {
							this.motionX *= (double)0.2F;
							this.motionZ *= (double)0.2F;
							this.addVelocity(var1.motionX - var2, 0.0D, var1.motionZ - var4);
							var1.motionX *= (double)0.95F;
							var1.motionZ *= (double)0.95F;
						} else if(((EntityMinecart)var1).getMinecartType() != 2 && this.getMinecartType() == 2) {
							var1.motionX *= (double)0.2F;
							var1.motionZ *= (double)0.2F;
							var1.addVelocity(this.motionX + var2, 0.0D, this.motionZ + var4);
							this.motionX *= (double)0.95F;
							this.motionZ *= (double)0.95F;
						} else {
							var18 /= 2.0D;
							var20 /= 2.0D;
							this.motionX *= (double)0.2F;
							this.motionZ *= (double)0.2F;
							this.addVelocity(var18 - var2, 0.0D, var20 - var4);
							var1.motionX *= (double)0.2F;
							var1.motionZ *= (double)0.2F;
							var1.addVelocity(var18 + var2, 0.0D, var20 + var4);
						}
					} else {
						this.addVelocity(-var2, 0.0D, -var4);
						var1.addVelocity(var2 / 4.0D, 0.0D, var4 / 4.0D);
					}
				}

			}
		}
	}

	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.minecartX = var1;
		this.minecartY = var3;
		this.minecartZ = var5;
		this.minecartYaw = (double)var7;
		this.minecartPitch = (double)var8;
		this.turnProgress = var9 + 2;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	public void setVelocity(double var1, double var3, double var5) {
		this.velocityX = this.motionX = var1;
		this.velocityY = this.motionY = var3;
		this.velocityZ = this.motionZ = var5;
	}

	public void setDamage(int var1) {
		this.dataWatcher.updateObject(19, Integer.valueOf(var1));
	}

	public int getDamage() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	public void setRollingAmplitude(int var1) {
		this.dataWatcher.updateObject(17, Integer.valueOf(var1));
	}

	public int getRollingAmplitude() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	public void setRollingDirection(int var1) {
		this.dataWatcher.updateObject(18, Integer.valueOf(var1));
	}

	public int getRollingDirection() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public abstract int getMinecartType();

	public Block getDisplayTile() {
		if(!this.hasDisplayTile()) {
			return this.getDefaultDisplayTile();
		} else {
			int var1 = this.getDataWatcher().getWatchableObjectInt(20) & '\uffff';
			return var1 > 0 && var1 < Block.blocksList.length ? Block.blocksList[var1] : null;
		}
	}

	public Block getDefaultDisplayTile() {
		return null;
	}

	public int getDisplayTileData() {
		return !this.hasDisplayTile() ? this.getDefaultDisplayTileData() : this.getDataWatcher().getWatchableObjectInt(20) >> 16;
	}

	public int getDefaultDisplayTileData() {
		return 0;
	}

	public int getDisplayTileOffset() {
		return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
	}

	public int getDefaultDisplayTileOffset() {
		return 6;
	}

	public void setDisplayTile(int var1) {
		this.getDataWatcher().updateObject(20, Integer.valueOf(var1 & '\uffff' | this.getDisplayTileData() << 16));
		this.setHasDisplayTile(true);
	}

	public void setDisplayTileData(int var1) {
		Block var2 = this.getDisplayTile();
		int var3 = var2 == null ? 0 : var2.blockID;
		this.getDataWatcher().updateObject(20, Integer.valueOf(var3 & '\uffff' | var1 << 16));
		this.setHasDisplayTile(true);
	}

	public void setDisplayTileOffset(int var1) {
		this.getDataWatcher().updateObject(21, Integer.valueOf(var1));
		this.setHasDisplayTile(true);
	}

	public boolean hasDisplayTile() {
		return this.getDataWatcher().getWatchableObjectByte(22) == 1;
	}

	public void setHasDisplayTile(boolean var1) {
		this.getDataWatcher().updateObject(22, Byte.valueOf((byte)(var1 ? 1 : 0)));
	}

	public void func_96094_a(String var1) {
		this.entityName = var1;
	}

	public String getEntityName() {
		return this.entityName != null ? this.entityName : super.getEntityName();
	}

	public boolean isInvNameLocalized() {
		return this.entityName != null;
	}

	public String func_95999_t() {
		return this.entityName;
	}
}
