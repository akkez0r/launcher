package net.minecraft.src;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class EntityPlayer extends EntityLiving implements ICommandSender {
	public InventoryPlayer inventory = new InventoryPlayer(this);
	private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
	public Container inventoryContainer;
	public Container openContainer;
	protected FoodStats foodStats = new FoodStats();
	protected int flyToggleTimer = 0;
	public byte field_71098_bD = 0;
	public float prevCameraYaw;
	public float cameraYaw;
	public String username;
	public int xpCooldown = 0;
	public double field_71091_bM;
	public double field_71096_bN;
	public double field_71097_bO;
	public double field_71094_bP;
	public double field_71095_bQ;
	public double field_71085_bR;
	protected boolean sleeping;
	public ChunkCoordinates playerLocation;
	private int sleepTimer;
	public float field_71079_bU;
	public float field_71082_cx;
	public float field_71089_bV;
	private ChunkCoordinates spawnChunk;
	private boolean spawnForced;
	private ChunkCoordinates startMinecartRidingCoordinate;
	public PlayerCapabilities capabilities = new PlayerCapabilities();
	public int experienceLevel;
	public int experienceTotal;
	public float experience;
	private ItemStack itemInUse;
	private int itemInUseCount;
	protected float speedOnGround = 0.1F;
	protected float speedInAir = 0.02F;
	private int field_82249_h = 0;
	public EntityFishHook fishEntity = null;

	public EntityPlayer(World var1) {
		super(var1);
		this.inventoryContainer = new ContainerPlayer(this.inventory, !var1.isRemote, this);
		this.openContainer = this.inventoryContainer;
		this.yOffset = 1.62F;
		ChunkCoordinates var2 = var1.getSpawnPoint();
		this.setLocationAndAngles((double)var2.posX + 0.5D, (double)(var2.posY + 1), (double)var2.posZ + 0.5D, 0.0F, 0.0F);
		this.entityType = "humanoid";
		this.field_70741_aB = 180.0F;
		this.fireResistance = 20;
		this.texture = "/mob/char.png";
	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
		this.dataWatcher.addObject(18, Integer.valueOf(0));
	}

	public ItemStack getItemInUse() {
		return this.itemInUse;
	}

	public int getItemInUseCount() {
		return this.itemInUseCount;
	}

	public boolean isUsingItem() {
		return this.itemInUse != null;
	}

	public int getItemInUseDuration() {
		return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
	}

	public void stopUsingItem() {
		if(this.itemInUse != null) {
			this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
		}

		this.clearItemInUse();
	}

	public void clearItemInUse() {
		this.itemInUse = null;
		this.itemInUseCount = 0;
		if(!this.worldObj.isRemote) {
			this.setEating(false);
		}

	}

	public boolean isBlocking() {
		return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemUseAction(this.itemInUse) == EnumAction.block;
	}

	public void onUpdate() {
		if(this.itemInUse != null) {
			ItemStack var1 = this.inventory.getCurrentItem();
			if(var1 == this.itemInUse) {
				if(this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
					this.updateItemUse(var1, 5);
				}

				if(--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
					this.onItemUseFinish();
				}
			} else {
				this.clearItemInUse();
			}
		}

		if(this.xpCooldown > 0) {
			--this.xpCooldown;
		}

		if(this.isPlayerSleeping()) {
			++this.sleepTimer;
			if(this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if(!this.worldObj.isRemote) {
				if(!this.isInBed()) {
					this.wakeUpPlayer(true, true, false);
				} else if(this.worldObj.isDaytime()) {
					this.wakeUpPlayer(false, true, true);
				}
			}
		} else if(this.sleepTimer > 0) {
			++this.sleepTimer;
			if(this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.onUpdate();
		if(!this.worldObj.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
			this.closeScreen();
			this.openContainer = this.inventoryContainer;
		}

		if(this.isBurning() && this.capabilities.disableDamage) {
			this.extinguish();
		}

		this.field_71091_bM = this.field_71094_bP;
		this.field_71096_bN = this.field_71095_bQ;
		this.field_71097_bO = this.field_71085_bR;
		double var9 = this.posX - this.field_71094_bP;
		double var3 = this.posY - this.field_71095_bQ;
		double var5 = this.posZ - this.field_71085_bR;
		double var7 = 10.0D;
		if(var9 > var7) {
			this.field_71091_bM = this.field_71094_bP = this.posX;
		}

		if(var5 > var7) {
			this.field_71097_bO = this.field_71085_bR = this.posZ;
		}

		if(var3 > var7) {
			this.field_71096_bN = this.field_71095_bQ = this.posY;
		}

		if(var9 < -var7) {
			this.field_71091_bM = this.field_71094_bP = this.posX;
		}

		if(var5 < -var7) {
			this.field_71097_bO = this.field_71085_bR = this.posZ;
		}

		if(var3 < -var7) {
			this.field_71096_bN = this.field_71095_bQ = this.posY;
		}

		this.field_71094_bP += var9 * 0.25D;
		this.field_71085_bR += var5 * 0.25D;
		this.field_71095_bQ += var3 * 0.25D;
		this.addStat(StatList.minutesPlayedStat, 1);
		if(this.ridingEntity == null) {
			this.startMinecartRidingCoordinate = null;
		}

		if(!this.worldObj.isRemote) {
			this.foodStats.onUpdate(this);
		}

	}

	public int getMaxInPortalTime() {
		return this.capabilities.disableDamage ? 0 : 80;
	}

	public int getPortalCooldown() {
		return 10;
	}

	public void playSound(String var1, float var2, float var3) {
		this.worldObj.playSoundToNearExcept(this, var1, var2, var3);
	}

	protected void updateItemUse(ItemStack var1, int var2) {
		if(var1.getItemUseAction() == EnumAction.drink) {
			this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(var1.getItemUseAction() == EnumAction.eat) {
			for(int var3 = 0; var3 < var2; ++var3) {
				Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				var4.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
				var4.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
				Vec3 var5 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
				var5.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
				var5.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
				var5 = var5.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
				this.worldObj.spawnParticle("iconcrack_" + var1.getItem().itemID, var5.xCoord, var5.yCoord, var5.zCoord, var4.xCoord, var4.yCoord + 0.05D, var4.zCoord);
			}

			this.playSound("random.eat", 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}

	}

	protected void onItemUseFinish() {
		if(this.itemInUse != null) {
			this.updateItemUse(this.itemInUse, 16);
			int var1 = this.itemInUse.stackSize;
			ItemStack var2 = this.itemInUse.onFoodEaten(this.worldObj, this);
			if(var2 != this.itemInUse || var2 != null && var2.stackSize != var1) {
				this.inventory.mainInventory[this.inventory.currentItem] = var2;
				if(var2.stackSize == 0) {
					this.inventory.mainInventory[this.inventory.currentItem] = null;
				}
			}

			this.clearItemInUse();
		}

	}

	public void handleHealthUpdate(byte var1) {
		if(var1 == 9) {
			this.onItemUseFinish();
		} else {
			super.handleHealthUpdate(var1);
		}

	}

	protected boolean isMovementBlocked() {
		return this.getHealth() <= 0 || this.isPlayerSleeping();
	}

	protected void closeScreen() {
		this.openContainer = this.inventoryContainer;
	}

	public void mountEntity(Entity var1) {
		if(this.ridingEntity == var1) {
			this.unmountEntity(var1);
			if(this.ridingEntity != null) {
				this.ridingEntity.riddenByEntity = null;
			}

			this.ridingEntity = null;
		} else {
			super.mountEntity(var1);
		}
	}

	public void updateRidden() {
		double var1 = this.posX;
		double var3 = this.posY;
		double var5 = this.posZ;
		float var7 = this.rotationYaw;
		float var8 = this.rotationPitch;
		super.updateRidden();
		this.prevCameraYaw = this.cameraYaw;
		this.cameraYaw = 0.0F;
		this.addMountedMovementStat(this.posX - var1, this.posY - var3, this.posZ - var5);
		if(this.ridingEntity instanceof EntityPig) {
			this.rotationPitch = var8;
			this.rotationYaw = var7;
			this.renderYawOffset = ((EntityPig)this.ridingEntity).renderYawOffset;
		}

	}

	public void preparePlayerToSpawn() {
		this.yOffset = 1.62F;
		this.setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
		this.setEntityHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	protected void updateEntityActionState() {
		this.updateArmSwingProgress();
	}

	public void onLivingUpdate() {
		if(this.flyToggleTimer > 0) {
			--this.flyToggleTimer;
		}

		if(this.worldObj.difficultySetting == 0 && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 * 12 == 0) {
			this.heal(1);
		}

		this.inventory.decrementAnimations();
		this.prevCameraYaw = this.cameraYaw;
		super.onLivingUpdate();
		this.landMovementFactor = this.capabilities.getWalkSpeed();
		this.jumpMovementFactor = this.speedInAir;
		if(this.isSprinting()) {
			this.landMovementFactor = (float)((double)this.landMovementFactor + (double)this.capabilities.getWalkSpeed() * 0.3D);
			this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
		}

		float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float var2 = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
		if(var1 > 0.1F) {
			var1 = 0.1F;
		}

		if(!this.onGround || this.getHealth() <= 0) {
			var1 = 0.0F;
		}

		if(this.onGround || this.getHealth() <= 0) {
			var2 = 0.0F;
		}

		this.cameraYaw += (var1 - this.cameraYaw) * 0.4F;
		this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
		if(this.getHealth() > 0) {
			List var3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 0.5D, 1.0D));
			if(var3 != null) {
				for(int var4 = 0; var4 < var3.size(); ++var4) {
					Entity var5 = (Entity)var3.get(var4);
					if(!var5.isDead) {
						this.collideWithPlayer(var5);
					}
				}
			}
		}

	}

	private void collideWithPlayer(Entity var1) {
		var1.onCollideWithPlayer(this);
	}

	public int getScore() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public void setScore(int var1) {
		this.dataWatcher.updateObject(18, Integer.valueOf(var1));
	}

	public void addScore(int var1) {
		int var2 = this.getScore();
		this.dataWatcher.updateObject(18, Integer.valueOf(var2 + var1));
	}

	public void onDeath(DamageSource var1) {
		super.onDeath(var1);
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionY = (double)0.1F;
		if(this.username.equals("Notch")) {
			this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
		}

		if(!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			this.inventory.dropAllItems();
		}

		if(var1 != null) {
			this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
			this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
		} else {
			this.motionX = this.motionZ = 0.0D;
		}

		this.yOffset = 0.1F;
		this.addStat(StatList.deathsStat, 1);
	}

	public void addToPlayerScore(Entity var1, int var2) {
		this.addScore(var2);
		Collection var3 = this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96640_e);
		if(var1 instanceof EntityPlayer) {
			this.addStat(StatList.playerKillsStat, 1);
			var3.addAll(this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96639_d));
		} else {
			this.addStat(StatList.mobKillsStat, 1);
		}

		Iterator var4 = var3.iterator();

		while(var4.hasNext()) {
			ScoreObjective var5 = (ScoreObjective)var4.next();
			Score var6 = this.getWorldScoreboard().func_96529_a(this.getEntityName(), var5);
			var6.func_96648_a();
		}

	}

	public EntityItem dropOneItem(boolean var1) {
		return this.dropPlayerItemWithRandomChoice(this.inventory.decrStackSize(this.inventory.currentItem, var1 && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1), false);
	}

	public EntityItem dropPlayerItem(ItemStack var1) {
		return this.dropPlayerItemWithRandomChoice(var1, false);
	}

	public EntityItem dropPlayerItemWithRandomChoice(ItemStack var1, boolean var2) {
		if(var1 == null) {
			return null;
		} else {
			EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.posY - (double)0.3F + (double)this.getEyeHeight(), this.posZ, var1);
			var3.delayBeforeCanPickup = 40;
			float var4 = 0.1F;
			float var5;
			if(var2) {
				var5 = this.rand.nextFloat() * 0.5F;
				float var6 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				var3.motionX = (double)(-MathHelper.sin(var6) * var5);
				var3.motionZ = (double)(MathHelper.cos(var6) * var5);
				var3.motionY = (double)0.2F;
			} else {
				var4 = 0.3F;
				var3.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * var4);
				var3.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * var4);
				var3.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
				var4 = 0.02F;
				var5 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				var4 *= this.rand.nextFloat();
				var3.motionX += Math.cos((double)var5) * (double)var4;
				var3.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				var3.motionZ += Math.sin((double)var5) * (double)var4;
			}

			this.joinEntityItemWithWorld(var3);
			this.addStat(StatList.dropStat, 1);
			return var3;
		}
	}

	protected void joinEntityItemWithWorld(EntityItem var1) {
		this.worldObj.spawnEntityInWorld(var1);
	}

	public float getCurrentPlayerStrVsBlock(Block var1, boolean var2) {
		float var3 = this.inventory.getStrVsBlock(var1);
		if(var3 > 1.0F) {
			int var4 = EnchantmentHelper.getEfficiencyModifier(this);
			ItemStack var5 = this.inventory.getCurrentItem();
			if(var4 > 0 && var5 != null) {
				float var6 = (float)(var4 * var4 + 1);
				if(!var5.canHarvestBlock(var1) && var3 <= 1.0F) {
					var3 += var6 * 0.08F;
				} else {
					var3 += var6;
				}
			}
		}

		if(this.isPotionActive(Potion.digSpeed)) {
			var3 *= 1.0F + (float)(this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if(this.isPotionActive(Potion.digSlowdown)) {
			var3 *= 1.0F - (float)(this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
		}

		if(this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
			var3 /= 5.0F;
		}

		if(!this.onGround) {
			var3 /= 5.0F;
		}

		return var3;
	}

	public boolean canHarvestBlock(Block var1) {
		return this.inventory.canHarvestBlock(var1);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		NBTTagList var2 = var1.getTagList("Inventory");
		this.inventory.readFromNBT(var2);
		this.inventory.currentItem = var1.getInteger("SelectedItemSlot");
		this.sleeping = var1.getBoolean("Sleeping");
		this.sleepTimer = var1.getShort("SleepTimer");
		this.experience = var1.getFloat("XpP");
		this.experienceLevel = var1.getInteger("XpLevel");
		this.experienceTotal = var1.getInteger("XpTotal");
		this.setScore(var1.getInteger("Score"));
		if(this.sleeping) {
			this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			this.wakeUpPlayer(true, true, false);
		}

		if(var1.hasKey("SpawnX") && var1.hasKey("SpawnY") && var1.hasKey("SpawnZ")) {
			this.spawnChunk = new ChunkCoordinates(var1.getInteger("SpawnX"), var1.getInteger("SpawnY"), var1.getInteger("SpawnZ"));
			this.spawnForced = var1.getBoolean("SpawnForced");
		}

		this.foodStats.readNBT(var1);
		this.capabilities.readCapabilitiesFromNBT(var1);
		if(var1.hasKey("EnderItems")) {
			NBTTagList var3 = var1.getTagList("EnderItems");
			this.theInventoryEnderChest.loadInventoryFromNBT(var3);
		}

	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		var1.setInteger("SelectedItemSlot", this.inventory.currentItem);
		var1.setBoolean("Sleeping", this.sleeping);
		var1.setShort("SleepTimer", (short)this.sleepTimer);
		var1.setFloat("XpP", this.experience);
		var1.setInteger("XpLevel", this.experienceLevel);
		var1.setInteger("XpTotal", this.experienceTotal);
		var1.setInteger("Score", this.getScore());
		if(this.spawnChunk != null) {
			var1.setInteger("SpawnX", this.spawnChunk.posX);
			var1.setInteger("SpawnY", this.spawnChunk.posY);
			var1.setInteger("SpawnZ", this.spawnChunk.posZ);
			var1.setBoolean("SpawnForced", this.spawnForced);
		}

		this.foodStats.writeNBT(var1);
		this.capabilities.writeCapabilitiesToNBT(var1);
		var1.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
	}

	public void displayGUIChest(IInventory var1) {
	}

	public void displayGUIHopper(TileEntityHopper var1) {
	}

	public void displayGUIHopperMinecart(EntityMinecartHopper var1) {
	}

	public void displayGUIEnchantment(int var1, int var2, int var3, String var4) {
	}

	public void displayGUIAnvil(int var1, int var2, int var3) {
	}

	public void displayGUIWorkbench(int var1, int var2, int var3) {
	}

	public float getEyeHeight() {
		return 0.12F;
	}

	protected void resetHeight() {
		this.yOffset = 1.62F;
	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else if(this.capabilities.disableDamage && !var1.canHarmInCreative()) {
			return false;
		} else {
			this.entityAge = 0;
			if(this.getHealth() <= 0) {
				return false;
			} else {
				if(this.isPlayerSleeping() && !this.worldObj.isRemote) {
					this.wakeUpPlayer(true, true, false);
				}

				if(var1.isDifficultyScaled()) {
					if(this.worldObj.difficultySetting == 0) {
						var2 = 0;
					}

					if(this.worldObj.difficultySetting == 1) {
						var2 = var2 / 2 + 1;
					}

					if(this.worldObj.difficultySetting == 3) {
						var2 = var2 * 3 / 2;
					}
				}

				if(var2 == 0) {
					return false;
				} else {
					Entity var3 = var1.getEntity();
					if(var3 instanceof EntityArrow && ((EntityArrow)var3).shootingEntity != null) {
						var3 = ((EntityArrow)var3).shootingEntity;
					}

					if(var3 instanceof EntityLiving) {
						this.alertWolves((EntityLiving)var3, false);
					}

					this.addStat(StatList.damageTakenStat, var2);
					return super.attackEntityFrom(var1, var2);
				}
			}
		}
	}

	public boolean func_96122_a(EntityPlayer var1) {
		ScorePlayerTeam var2 = this.getTeam();
		ScorePlayerTeam var3 = var1.getTeam();
		return var2 != var3 ? true : (var2 != null ? var2.func_96665_g() : true);
	}

	protected void alertWolves(EntityLiving var1, boolean var2) {
		if(!(var1 instanceof EntityCreeper) && !(var1 instanceof EntityGhast)) {
			if(var1 instanceof EntityWolf) {
				EntityWolf var3 = (EntityWolf)var1;
				if(var3.isTamed() && this.username.equals(var3.getOwnerName())) {
					return;
				}
			}

			if(!(var1 instanceof EntityPlayer) || this.func_96122_a((EntityPlayer)var1)) {
				List var6 = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getAABBPool().getAABB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
				Iterator var4 = var6.iterator();

				while(true) {
					EntityWolf var5;
					do {
						do {
							do {
								do {
									if(!var4.hasNext()) {
										return;
									}

									var5 = (EntityWolf)var4.next();
								} while(!var5.isTamed());
							} while(var5.getEntityToAttack() != null);
						} while(!this.username.equals(var5.getOwnerName()));
					} while(var2 && var5.isSitting());

					var5.setSitting(false);
					var5.setTarget(var1);
				}
			}
		}
	}

	protected void damageArmor(int var1) {
		this.inventory.damageArmor(var1);
	}

	public int getTotalArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	public float func_82243_bO() {
		int var1 = 0;
		ItemStack[] var2 = this.inventory.armorInventory;
		int var3 = var2.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];
			if(var5 != null) {
				++var1;
			}
		}

		return (float)var1 / (float)this.inventory.armorInventory.length;
	}

	protected void damageEntity(DamageSource var1, int var2) {
		if(!this.isEntityInvulnerable()) {
			if(!var1.isUnblockable() && this.isBlocking()) {
				var2 = 1 + var2 >> 1;
			}

			var2 = this.applyArmorCalculations(var1, var2);
			var2 = this.applyPotionDamageCalculations(var1, var2);
			this.addExhaustion(var1.getHungerDamage());
			int var3 = this.getHealth();
			this.setEntityHealth(this.getHealth() - var2);
			this.field_94063_bt.func_94547_a(var1, var3, var2);
		}
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
	}

	public void displayGUIDispenser(TileEntityDispenser var1) {
	}

	public void displayGUIEditSign(TileEntity var1) {
	}

	public void displayGUIBrewingStand(TileEntityBrewingStand var1) {
	}

	public void displayGUIBeacon(TileEntityBeacon var1) {
	}

	public void displayGUIMerchant(IMerchant var1, String var2) {
	}

	public void displayGUIBook(ItemStack var1) {
	}

	public boolean interactWith(Entity var1) {
		if(var1.interact(this)) {
			return true;
		} else {
			ItemStack var2 = this.getCurrentEquippedItem();
			if(var2 != null && var1 instanceof EntityLiving) {
				if(this.capabilities.isCreativeMode) {
					var2 = var2.copy();
				}

				if(var2.interactWith((EntityLiving)var1)) {
					if(var2.stackSize <= 0 && !this.capabilities.isCreativeMode) {
						this.destroyCurrentEquippedItem();
					}

					return true;
				}
			}

			return false;
		}
	}

	public ItemStack getCurrentEquippedItem() {
		return this.inventory.getCurrentItem();
	}

	public void destroyCurrentEquippedItem() {
		this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
	}

	public double getYOffset() {
		return (double)(this.yOffset - 0.5F);
	}

	public void attackTargetEntityWithCurrentItem(Entity var1) {
		if(var1.canAttackWithItem()) {
			if(!var1.func_85031_j(this)) {
				int var2 = this.inventory.getDamageVsEntity(var1);
				if(this.isPotionActive(Potion.damageBoost)) {
					var2 += 3 << this.getActivePotionEffect(Potion.damageBoost).getAmplifier();
				}

				if(this.isPotionActive(Potion.weakness)) {
					var2 -= 2 << this.getActivePotionEffect(Potion.weakness).getAmplifier();
				}

				int var3 = 0;
				int var4 = 0;
				if(var1 instanceof EntityLiving) {
					var4 = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLiving)var1);
					var3 += EnchantmentHelper.getKnockbackModifier(this, (EntityLiving)var1);
				}

				if(this.isSprinting()) {
					++var3;
				}

				if(var2 > 0 || var4 > 0) {
					boolean var5 = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && var1 instanceof EntityLiving;
					if(var5 && var2 > 0) {
						var2 += this.rand.nextInt(var2 / 2 + 2);
					}

					var2 += var4;
					boolean var6 = false;
					int var7 = EnchantmentHelper.getFireAspectModifier(this);
					if(var1 instanceof EntityLiving && var7 > 0 && !var1.isBurning()) {
						var6 = true;
						var1.setFire(1);
					}

					boolean var8 = var1.attackEntityFrom(DamageSource.causePlayerDamage(this), var2);
					if(var8) {
						if(var3 > 0) {
							var1.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)var3 * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)var3 * 0.5F));
							this.motionX *= 0.6D;
							this.motionZ *= 0.6D;
							this.setSprinting(false);
						}

						if(var5) {
							this.onCriticalHit(var1);
						}

						if(var4 > 0) {
							this.onEnchantmentCritical(var1);
						}

						if(var2 >= 18) {
							this.triggerAchievement(AchievementList.overkill);
						}

						this.setLastAttackingEntity(var1);
						if(var1 instanceof EntityLiving) {
							EnchantmentThorns.func_92096_a(this, (EntityLiving)var1, this.rand);
						}
					}

					ItemStack var9 = this.getCurrentEquippedItem();
					Object var10 = var1;
					if(var1 instanceof EntityDragonPart) {
						IEntityMultiPart var11 = ((EntityDragonPart)var1).entityDragonObj;
						if(var11 != null && var11 instanceof EntityLiving) {
							var10 = (EntityLiving)var11;
						}
					}

					if(var9 != null && var10 instanceof EntityLiving) {
						var9.hitEntity((EntityLiving)var10, this);
						if(var9.stackSize <= 0) {
							this.destroyCurrentEquippedItem();
						}
					}

					if(var1 instanceof EntityLiving) {
						if(var1.isEntityAlive()) {
							this.alertWolves((EntityLiving)var1, true);
						}

						this.addStat(StatList.damageDealtStat, var2);
						if(var7 > 0 && var8) {
							var1.setFire(var7 * 4);
						} else if(var6) {
							var1.extinguish();
						}
					}

					this.addExhaustion(0.3F);
				}

			}
		}
	}

	public void onCriticalHit(Entity var1) {
	}

	public void onEnchantmentCritical(Entity var1) {
	}

	public void respawnPlayer() {
	}

	public void setDead() {
		super.setDead();
		this.inventoryContainer.onCraftGuiClosed(this);
		if(this.openContainer != null) {
			this.openContainer.onCraftGuiClosed(this);
		}

	}

	public boolean isEntityInsideOpaqueBlock() {
		return !this.sleeping && super.isEntityInsideOpaqueBlock();
	}

	public boolean func_71066_bF() {
		return false;
	}

	public EnumStatus sleepInBedAt(int var1, int var2, int var3) {
		if(!this.worldObj.isRemote) {
			if(this.isPlayerSleeping() || !this.isEntityAlive()) {
				return EnumStatus.OTHER_PROBLEM;
			}

			if(!this.worldObj.provider.isSurfaceWorld()) {
				return EnumStatus.NOT_POSSIBLE_HERE;
			}

			if(this.worldObj.isDaytime()) {
				return EnumStatus.NOT_POSSIBLE_NOW;
			}

			if(Math.abs(this.posX - (double)var1) > 3.0D || Math.abs(this.posY - (double)var2) > 2.0D || Math.abs(this.posZ - (double)var3) > 3.0D) {
				return EnumStatus.TOO_FAR_AWAY;
			}

			double var4 = 8.0D;
			double var6 = 5.0D;
			List var8 = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB((double)var1 - var4, (double)var2 - var6, (double)var3 - var4, (double)var1 + var4, (double)var2 + var6, (double)var3 + var4));
			if(!var8.isEmpty()) {
				return EnumStatus.NOT_SAFE;
			}
		}

		this.setSize(0.2F, 0.2F);
		this.yOffset = 0.2F;
		if(this.worldObj.blockExists(var1, var2, var3)) {
			int var9 = this.worldObj.getBlockMetadata(var1, var2, var3);
			int var5 = BlockBed.getDirection(var9);
			float var10 = 0.5F;
			float var7 = 0.5F;
			switch(var5) {
			case 0:
				var7 = 0.9F;
				break;
			case 1:
				var10 = 0.1F;
				break;
			case 2:
				var7 = 0.1F;
				break;
			case 3:
				var10 = 0.9F;
			}

			this.func_71013_b(var5);
			this.setPosition((double)((float)var1 + var10), (double)((float)var2 + 15.0F / 16.0F), (double)((float)var3 + var7));
		} else {
			this.setPosition((double)((float)var1 + 0.5F), (double)((float)var2 + 15.0F / 16.0F), (double)((float)var3 + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = new ChunkCoordinates(var1, var2, var3);
		this.motionX = this.motionZ = this.motionY = 0.0D;
		if(!this.worldObj.isRemote) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		return EnumStatus.OK;
	}

	private void func_71013_b(int var1) {
		this.field_71079_bU = 0.0F;
		this.field_71089_bV = 0.0F;
		switch(var1) {
		case 0:
			this.field_71089_bV = -1.8F;
			break;
		case 1:
			this.field_71079_bU = 1.8F;
			break;
		case 2:
			this.field_71089_bV = 1.8F;
			break;
		case 3:
			this.field_71079_bU = -1.8F;
		}

	}

	public void wakeUpPlayer(boolean var1, boolean var2, boolean var3) {
		this.setSize(0.6F, 1.8F);
		this.resetHeight();
		ChunkCoordinates var4 = this.playerLocation;
		ChunkCoordinates var5 = this.playerLocation;
		if(var4 != null && this.worldObj.getBlockId(var4.posX, var4.posY, var4.posZ) == Block.bed.blockID) {
			BlockBed.setBedOccupied(this.worldObj, var4.posX, var4.posY, var4.posZ, false);
			var5 = BlockBed.getNearestEmptyChunkCoordinates(this.worldObj, var4.posX, var4.posY, var4.posZ, 0);
			if(var5 == null) {
				var5 = new ChunkCoordinates(var4.posX, var4.posY + 1, var4.posZ);
			}

			this.setPosition((double)((float)var5.posX + 0.5F), (double)((float)var5.posY + this.yOffset + 0.1F), (double)((float)var5.posZ + 0.5F));
		}

		this.sleeping = false;
		if(!this.worldObj.isRemote && var2) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		if(var1) {
			this.sleepTimer = 0;
		} else {
			this.sleepTimer = 100;
		}

		if(var3) {
			this.setSpawnChunk(this.playerLocation, false);
		}

	}

	private boolean isInBed() {
		return this.worldObj.getBlockId(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ) == Block.bed.blockID;
	}

	public static ChunkCoordinates verifyRespawnCoordinates(World var0, ChunkCoordinates var1, boolean var2) {
		IChunkProvider var3 = var0.getChunkProvider();
		var3.loadChunk(var1.posX - 3 >> 4, var1.posZ - 3 >> 4);
		var3.loadChunk(var1.posX + 3 >> 4, var1.posZ - 3 >> 4);
		var3.loadChunk(var1.posX - 3 >> 4, var1.posZ + 3 >> 4);
		var3.loadChunk(var1.posX + 3 >> 4, var1.posZ + 3 >> 4);
		if(var0.getBlockId(var1.posX, var1.posY, var1.posZ) == Block.bed.blockID) {
			ChunkCoordinates var8 = BlockBed.getNearestEmptyChunkCoordinates(var0, var1.posX, var1.posY, var1.posZ, 0);
			return var8;
		} else {
			Material var4 = var0.getBlockMaterial(var1.posX, var1.posY, var1.posZ);
			Material var5 = var0.getBlockMaterial(var1.posX, var1.posY + 1, var1.posZ);
			boolean var6 = !var4.isSolid() && !var4.isLiquid();
			boolean var7 = !var5.isSolid() && !var5.isLiquid();
			return var2 && var6 && var7 ? var1 : null;
		}
	}

	public float getBedOrientationInDegrees() {
		if(this.playerLocation != null) {
			int var1 = this.worldObj.getBlockMetadata(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ);
			int var2 = BlockBed.getDirection(var1);
			switch(var2) {
			case 0:
				return 90.0F;
			case 1:
				return 0.0F;
			case 2:
				return 270.0F;
			case 3:
				return 180.0F;
			}
		}

		return 0.0F;
	}

	public boolean isPlayerSleeping() {
		return this.sleeping;
	}

	public boolean isPlayerFullyAsleep() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	protected boolean getHideCape(int var1) {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1 << var1) != 0;
	}

	protected void setHideCape(int var1, boolean var2) {
		byte var3 = this.dataWatcher.getWatchableObjectByte(16);
		if(var2) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var3 | 1 << var1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(var3 & ~(1 << var1))));
		}

	}

	public void addChatMessage(String var1) {
	}

	public ChunkCoordinates getBedLocation() {
		return this.spawnChunk;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setSpawnChunk(ChunkCoordinates var1, boolean var2) {
		if(var1 != null) {
			this.spawnChunk = new ChunkCoordinates(var1);
			this.spawnForced = var2;
		} else {
			this.spawnChunk = null;
			this.spawnForced = false;
		}

	}

	public void triggerAchievement(StatBase var1) {
		this.addStat(var1, 1);
	}

	public void addStat(StatBase var1, int var2) {
	}

	protected void jump() {
		super.jump();
		this.addStat(StatList.jumpStat, 1);
		if(this.isSprinting()) {
			this.addExhaustion(0.8F);
		} else {
			this.addExhaustion(0.2F);
		}

	}

	public void moveEntityWithHeading(float var1, float var2) {
		double var3 = this.posX;
		double var5 = this.posY;
		double var7 = this.posZ;
		if(this.capabilities.isFlying && this.ridingEntity == null) {
			double var9 = this.motionY;
			float var11 = this.jumpMovementFactor;
			this.jumpMovementFactor = this.capabilities.getFlySpeed();
			super.moveEntityWithHeading(var1, var2);
			this.motionY = var9 * 0.6D;
			this.jumpMovementFactor = var11;
		} else {
			super.moveEntityWithHeading(var1, var2);
		}

		this.addMovementStat(this.posX - var3, this.posY - var5, this.posZ - var7);
	}

	public void addMovementStat(double var1, double var3, double var5) {
		if(this.ridingEntity == null) {
			int var7;
			if(this.isInsideOfMaterial(Material.water)) {
				var7 = Math.round(MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
				if(var7 > 0) {
					this.addStat(StatList.distanceDoveStat, var7);
					this.addExhaustion(0.015F * (float)var7 * 0.01F);
				}
			} else if(this.isInWater()) {
				var7 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
				if(var7 > 0) {
					this.addStat(StatList.distanceSwumStat, var7);
					this.addExhaustion(0.015F * (float)var7 * 0.01F);
				}
			} else if(this.isOnLadder()) {
				if(var3 > 0.0D) {
					this.addStat(StatList.distanceClimbedStat, (int)Math.round(var3 * 100.0D));
				}
			} else if(this.onGround) {
				var7 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
				if(var7 > 0) {
					this.addStat(StatList.distanceWalkedStat, var7);
					if(this.isSprinting()) {
						this.addExhaustion(10.0F * 0.01F * (float)var7 * 0.01F);
					} else {
						this.addExhaustion(0.01F * (float)var7 * 0.01F);
					}
				}
			} else {
				var7 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
				if(var7 > 25) {
					this.addStat(StatList.distanceFlownStat, var7);
				}
			}

		}
	}

	private void addMountedMovementStat(double var1, double var3, double var5) {
		if(this.ridingEntity != null) {
			int var7 = Math.round(MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
			if(var7 > 0) {
				if(this.ridingEntity instanceof EntityMinecart) {
					this.addStat(StatList.distanceByMinecartStat, var7);
					if(this.startMinecartRidingCoordinate == null) {
						this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
					} else if((double)this.startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000000.0D) {
						this.addStat(AchievementList.onARail, 1);
					}
				} else if(this.ridingEntity instanceof EntityBoat) {
					this.addStat(StatList.distanceByBoatStat, var7);
				} else if(this.ridingEntity instanceof EntityPig) {
					this.addStat(StatList.distanceByPigStat, var7);
				}
			}
		}

	}

	protected void fall(float var1) {
		if(!this.capabilities.allowFlying) {
			if(var1 >= 2.0F) {
				this.addStat(StatList.distanceFallenStat, (int)Math.round((double)var1 * 100.0D));
			}

			super.fall(var1);
		}
	}

	public void onKillEntity(EntityLiving var1) {
		if(var1 instanceof IMob) {
			this.triggerAchievement(AchievementList.killEnemy);
		}

	}

	public void setInWeb() {
		if(!this.capabilities.isFlying) {
			super.setInWeb();
		}

	}

	public Icon getItemIcon(ItemStack var1, int var2) {
		Icon var3 = super.getItemIcon(var1, var2);
		if(var1.itemID == Item.fishingRod.itemID && this.fishEntity != null) {
			var3 = Item.fishingRod.func_94597_g();
		} else {
			if(var1.getItem().requiresMultipleRenderPasses()) {
				return var1.getItem().getIconFromDamageForRenderPass(var1.getItemDamage(), var2);
			}

			if(this.itemInUse != null && var1.itemID == Item.bow.itemID) {
				int var4 = var1.getMaxItemUseDuration() - this.itemInUseCount;
				if(var4 >= 18) {
					return Item.bow.getItemIconForUseDuration(2);
				}

				if(var4 > 13) {
					return Item.bow.getItemIconForUseDuration(1);
				}

				if(var4 > 0) {
					return Item.bow.getItemIconForUseDuration(0);
				}
			}
		}

		return var3;
	}

	public ItemStack getCurrentArmor(int var1) {
		return this.inventory.armorItemInSlot(var1);
	}

	protected void addRandomArmor() {
	}

	protected void func_82162_bC() {
	}

	public void addExperience(int var1) {
		this.addScore(var1);
		int var2 = Integer.MAX_VALUE - this.experienceTotal;
		if(var1 > var2) {
			var1 = var2;
		}

		this.experience += (float)var1 / (float)this.xpBarCap();

		for(this.experienceTotal += var1; this.experience >= 1.0F; this.experience /= (float)this.xpBarCap()) {
			this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
			this.addExperienceLevel(1);
		}

	}

	public void addExperienceLevel(int var1) {
		this.experienceLevel += var1;
		if(this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experience = 0.0F;
			this.experienceTotal = 0;
		}

		if(var1 > 0 && this.experienceLevel % 5 == 0 && (float)this.field_82249_h < (float)this.ticksExisted - 100.0F) {
			float var2 = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
			this.worldObj.playSoundAtEntity(this, "random.levelup", var2 * (12.0F / 16.0F), 1.0F);
			this.field_82249_h = this.ticksExisted;
		}

	}

	public int xpBarCap() {
		return this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7 : (this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3 : 17);
	}

	public void addExhaustion(float var1) {
		if(!this.capabilities.disableDamage) {
			if(!this.worldObj.isRemote) {
				this.foodStats.addExhaustion(var1);
			}

		}
	}

	public FoodStats getFoodStats() {
		return this.foodStats;
	}

	public boolean canEat(boolean var1) {
		return (var1 || this.foodStats.needFood()) && !this.capabilities.disableDamage;
	}

	public boolean shouldHeal() {
		return this.getHealth() > 0 && this.getHealth() < this.getMaxHealth();
	}

	public void setItemInUse(ItemStack var1, int var2) {
		if(var1 != this.itemInUse) {
			this.itemInUse = var1;
			this.itemInUseCount = var2;
			if(!this.worldObj.isRemote) {
				this.setEating(true);
			}

		}
	}

	public boolean canCurrentToolHarvestBlock(int var1, int var2, int var3) {
		if(this.capabilities.allowEdit) {
			return true;
		} else {
			int var4 = this.worldObj.getBlockId(var1, var2, var3);
			if(var4 > 0) {
				Block var5 = Block.blocksList[var4];
				if(var5.blockMaterial.isAlwaysHarvested()) {
					return true;
				}

				if(this.getCurrentEquippedItem() != null) {
					ItemStack var6 = this.getCurrentEquippedItem();
					if(var6.canHarvestBlock(var5) || var6.getStrVsBlock(var5) > 1.0F) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean canPlayerEdit(int var1, int var2, int var3, int var4, ItemStack var5) {
		return this.capabilities.allowEdit ? true : (var5 != null ? var5.func_82835_x() : false);
	}

	protected int getExperiencePoints(EntityPlayer var1) {
		if(this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			return 0;
		} else {
			int var2 = this.experienceLevel * 7;
			return var2 > 100 ? 100 : var2;
		}
	}

	protected boolean isPlayer() {
		return true;
	}

	public String getEntityName() {
		return this.username;
	}

	public boolean func_94062_bN() {
		return super.func_94062_bN();
	}

	public boolean func_94059_bO() {
		return true;
	}

	public boolean canPickUpLoot() {
		return false;
	}

	public void clonePlayer(EntityPlayer var1, boolean var2) {
		if(var2) {
			this.inventory.copyInventory(var1.inventory);
			this.health = var1.health;
			this.foodStats = var1.foodStats;
			this.experienceLevel = var1.experienceLevel;
			this.experienceTotal = var1.experienceTotal;
			this.experience = var1.experience;
			this.setScore(var1.getScore());
			this.teleportDirection = var1.teleportDirection;
		} else if(this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			this.inventory.copyInventory(var1.inventory);
			this.experienceLevel = var1.experienceLevel;
			this.experienceTotal = var1.experienceTotal;
			this.experience = var1.experience;
			this.setScore(var1.getScore());
		}

		this.theInventoryEnderChest = var1.theInventoryEnderChest;
	}

	protected boolean canTriggerWalking() {
		return !this.capabilities.isFlying;
	}

	public void sendPlayerAbilities() {
	}

	public void setGameType(EnumGameType var1) {
	}

	public String getCommandSenderName() {
		return this.username;
	}

	public StringTranslate getTranslator() {
		return StringTranslate.getInstance();
	}

	public String translateString(String var1, Object... var2) {
		return this.getTranslator().translateKeyFormat(var1, var2);
	}

	public InventoryEnderChest getInventoryEnderChest() {
		return this.theInventoryEnderChest;
	}

	public ItemStack getCurrentItemOrArmor(int var1) {
		return var1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[var1 - 1];
	}

	public ItemStack getHeldItem() {
		return this.inventory.getCurrentItem();
	}

	public void setCurrentItemOrArmor(int var1, ItemStack var2) {
		this.inventory.armorInventory[var1] = var2;
	}

	public boolean func_98034_c(EntityPlayer var1) {
		if(!this.isInvisible()) {
			return false;
		} else {
			ScorePlayerTeam var2 = this.getTeam();
			return var2 == null || var1 == null || var1.getTeam() != var2 || !var2.func_98297_h();
		}
	}

	public ItemStack[] getLastActiveItems() {
		return this.inventory.armorInventory;
	}

	public boolean getHideCape() {
		return this.getHideCape(1);
	}

	public boolean func_96092_aw() {
		return !this.capabilities.isFlying;
	}

	public Scoreboard getWorldScoreboard() {
		return this.worldObj.getScoreboard();
	}

	public ScorePlayerTeam getTeam() {
		return this.getWorldScoreboard().getPlayersTeam(this.username);
	}

	public String getTranslatedEntityName() {
		return ScorePlayerTeam.func_96667_a(this.getTeam(), this.username);
	}
}
