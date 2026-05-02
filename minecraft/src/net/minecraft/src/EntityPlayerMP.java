package net.minecraft.src;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
	private StringTranslate translator = new StringTranslate("en_US");
	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public ItemInWorldManager theItemInWorldManager;
	public double managedPosX;
	public double managedPosZ;
	public final List loadedChunks = new LinkedList();
	public final List destroyedItemsNetCache = new LinkedList();
	private int lastHealth = -99999999;
	private int lastFoodLevel = -99999999;
	private boolean wasHungry = true;
	private int lastExperience = -99999999;
	private int initialInvulnerability = 60;
	private int renderDistance = 0;
	private int chatVisibility = 0;
	private boolean chatColours = true;
	private int currentWindowId = 0;
	public boolean playerInventoryBeingManipulated;
	public int ping;
	public boolean playerConqueredTheEnd = false;

	public EntityPlayerMP(MinecraftServer var1, World var2, String var3, ItemInWorldManager var4) {
		super(var2);
		var4.thisPlayerMP = this;
		this.theItemInWorldManager = var4;
		this.renderDistance = var1.getConfigurationManager().getViewDistance();
		ChunkCoordinates var5 = var2.getSpawnPoint();
		int var6 = var5.posX;
		int var7 = var5.posZ;
		int var8 = var5.posY;
		if(!var2.provider.hasNoSky && var2.getWorldInfo().getGameType() != EnumGameType.ADVENTURE) {
			int var9 = Math.max(5, var1.getSpawnProtectionSize() - 6);
			var6 += this.rand.nextInt(var9 * 2) - var9;
			var7 += this.rand.nextInt(var9 * 2) - var9;
			var8 = var2.getTopSolidOrLiquidBlock(var6, var7);
		}

		this.mcServer = var1;
		this.stepHeight = 0.0F;
		this.username = var3;
		this.yOffset = 0.0F;
		this.setLocationAndAngles((double)var6 + 0.5D, (double)var8, (double)var7 + 0.5D, 0.0F, 0.0F);

		while(!var2.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
			this.setPosition(this.posX, this.posY + 1.0D, this.posZ);
		}

	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		if(var1.hasKey("playerGameType")) {
			if(MinecraftServer.getServer().func_104056_am()) {
				this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
			} else {
				this.theItemInWorldManager.setGameType(EnumGameType.getByID(var1.getInteger("playerGameType")));
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
	}

	public void addExperienceLevel(int var1) {
		super.addExperienceLevel(var1);
		this.lastExperience = -1;
	}

	public void addSelfToInternalCraftingInventory() {
		this.openContainer.addCraftingToCrafters(this);
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public float getEyeHeight() {
		return 1.62F;
	}

	public void onUpdate() {
		this.theItemInWorldManager.updateBlockRemoving();
		--this.initialInvulnerability;
		this.openContainer.detectAndSendChanges();

		while(!this.destroyedItemsNetCache.isEmpty()) {
			int var1 = Math.min(this.destroyedItemsNetCache.size(), 127);
			int[] var2 = new int[var1];
			Iterator var3 = this.destroyedItemsNetCache.iterator();
			int var4 = 0;

			while(var3.hasNext() && var4 < var1) {
				var2[var4++] = ((Integer)var3.next()).intValue();
				var3.remove();
			}

			this.playerNetServerHandler.sendPacketToPlayer(new Packet29DestroyEntity(var2));
		}

		if(!this.loadedChunks.isEmpty()) {
			ArrayList var6 = new ArrayList();
			Iterator var7 = this.loadedChunks.iterator();
			ArrayList var8 = new ArrayList();

			while(var7.hasNext() && var6.size() < 5) {
				ChunkCoordIntPair var9 = (ChunkCoordIntPair)var7.next();
				var7.remove();
				if(var9 != null && this.worldObj.blockExists(var9.chunkXPos << 4, 0, var9.chunkZPos << 4)) {
					var6.add(this.worldObj.getChunkFromChunkCoords(var9.chunkXPos, var9.chunkZPos));
					var8.addAll(((WorldServer)this.worldObj).getAllTileEntityInBox(var9.chunkXPos * 16, 0, var9.chunkZPos * 16, var9.chunkXPos * 16 + 16, 256, var9.chunkZPos * 16 + 16));
				}
			}

			if(!var6.isEmpty()) {
				this.playerNetServerHandler.sendPacketToPlayer(new Packet56MapChunks(var6));
				Iterator var10 = var8.iterator();

				while(var10.hasNext()) {
					TileEntity var5 = (TileEntity)var10.next();
					this.sendTileEntityToPlayer(var5);
				}

				var10 = var6.iterator();

				while(var10.hasNext()) {
					Chunk var11 = (Chunk)var10.next();
					this.getServerForPlayer().getEntityTracker().func_85172_a(this, var11);
				}
			}
		}

	}

	public void setEntityHealth(int var1) {
		super.setEntityHealth(var1);
		Collection var2 = this.getWorldScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96638_f);
		Iterator var3 = var2.iterator();

		while(var3.hasNext()) {
			ScoreObjective var4 = (ScoreObjective)var3.next();
			this.getWorldScoreboard().func_96529_a(this.getEntityName(), var4).func_96651_a(Arrays.asList(new EntityPlayer[]{this}));
		}

	}

	public void onUpdateEntity() {
		try {
			super.onUpdate();

			for(int var1 = 0; var1 < this.inventory.getSizeInventory(); ++var1) {
				ItemStack var5 = this.inventory.getStackInSlot(var1);
				if(var5 != null && Item.itemsList[var5.itemID].isMap() && this.playerNetServerHandler.packetSize() <= 5) {
					Packet var6 = ((ItemMapBase)Item.itemsList[var5.itemID]).createMapDataPacket(var5, this.worldObj, this);
					if(var6 != null) {
						this.playerNetServerHandler.sendPacketToPlayer(var6);
					}
				}
			}

			if(this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
				this.playerNetServerHandler.sendPacketToPlayer(new Packet8UpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
				this.lastHealth = this.getHealth();
				this.lastFoodLevel = this.foodStats.getFoodLevel();
				this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
			}

			if(this.experienceTotal != this.lastExperience) {
				this.lastExperience = this.experienceTotal;
				this.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(this.experience, this.experienceTotal, this.experienceLevel));
			}

		} catch (Throwable var4) {
			CrashReport var2 = CrashReport.makeCrashReport(var4, "Ticking player");
			CrashReportCategory var3 = var2.makeCategory("Player being ticked");
			this.func_85029_a(var3);
			throw new ReportedException(var2);
		}
	}

	public void onDeath(DamageSource var1) {
		this.mcServer.getConfigurationManager().sendChatMsg(this.field_94063_bt.func_94546_b());
		if(!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			this.inventory.dropAllItems();
		}

		Collection var2 = this.worldObj.getScoreboard().func_96520_a(ScoreObjectiveCriteria.field_96642_c);
		Iterator var3 = var2.iterator();

		while(var3.hasNext()) {
			ScoreObjective var4 = (ScoreObjective)var3.next();
			Score var5 = this.getWorldScoreboard().func_96529_a(this.getEntityName(), var4);
			var5.func_96648_a();
		}

		EntityLiving var6 = this.func_94060_bK();
		if(var6 != null) {
			var6.addToPlayerScore(this, this.scoreValue);
		}

	}

	public boolean attackEntityFrom(DamageSource var1, int var2) {
		if(this.isEntityInvulnerable()) {
			return false;
		} else {
			boolean var3 = this.mcServer.isDedicatedServer() && this.mcServer.isPVPEnabled() && "fall".equals(var1.damageType);
			if(!var3 && this.initialInvulnerability > 0 && var1 != DamageSource.outOfWorld) {
				return false;
			} else {
				if(var1 instanceof EntityDamageSource) {
					Entity var4 = var1.getEntity();
					if(var4 instanceof EntityPlayer && !this.func_96122_a((EntityPlayer)var4)) {
						return false;
					}

					if(var4 instanceof EntityArrow) {
						EntityArrow var5 = (EntityArrow)var4;
						if(var5.shootingEntity instanceof EntityPlayer && !this.func_96122_a((EntityPlayer)var5.shootingEntity)) {
							return false;
						}
					}
				}

				return super.attackEntityFrom(var1, var2);
			}
		}
	}

	public boolean func_96122_a(EntityPlayer var1) {
		return !this.mcServer.isPVPEnabled() ? false : super.func_96122_a(var1);
	}

	public void travelToDimension(int var1) {
		if(this.dimension == 1 && var1 == 1) {
			this.triggerAchievement(AchievementList.theEnd2);
			this.worldObj.removeEntity(this);
			this.playerConqueredTheEnd = true;
			this.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(4, 0));
		} else {
			if(this.dimension == 1 && var1 == 0) {
				this.triggerAchievement(AchievementList.theEnd);
				ChunkCoordinates var2 = this.mcServer.worldServerForDimension(var1).getEntrancePortalLocation();
				if(var2 != null) {
					this.playerNetServerHandler.setPlayerLocation((double)var2.posX, (double)var2.posY, (double)var2.posZ, 0.0F, 0.0F);
				}

				var1 = 1;
			} else {
				this.triggerAchievement(AchievementList.portal);
			}

			this.mcServer.getConfigurationManager().transferPlayerToDimension(this, var1);
			this.lastExperience = -1;
			this.lastHealth = -1;
			this.lastFoodLevel = -1;
		}

	}

	private void sendTileEntityToPlayer(TileEntity var1) {
		if(var1 != null) {
			Packet var2 = var1.getDescriptionPacket();
			if(var2 != null) {
				this.playerNetServerHandler.sendPacketToPlayer(var2);
			}
		}

	}

	public void onItemPickup(Entity var1, int var2) {
		super.onItemPickup(var1, var2);
		this.openContainer.detectAndSendChanges();
	}

	public EnumStatus sleepInBedAt(int var1, int var2, int var3) {
		EnumStatus var4 = super.sleepInBedAt(var1, var2, var3);
		if(var4 == EnumStatus.OK) {
			Packet17Sleep var5 = new Packet17Sleep(this, 0, var1, var2, var3);
			this.getServerForPlayer().getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, var5);
			this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.playerNetServerHandler.sendPacketToPlayer(var5);
		}

		return var4;
	}

	public void wakeUpPlayer(boolean var1, boolean var2, boolean var3) {
		if(this.isPlayerSleeping()) {
			this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(this, 3));
		}

		super.wakeUpPlayer(var1, var2, var3);
		if(this.playerNetServerHandler != null) {
			this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		}

	}

	public void mountEntity(Entity var1) {
		super.mountEntity(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(this, this.ridingEntity));
		this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
	}

	protected void updateFallState(double var1, boolean var3) {
	}

	public void updateFlyingState(double var1, boolean var3) {
		super.updateFallState(var1, var3);
	}

	private void incrementWindowID() {
		this.currentWindowId = this.currentWindowId % 100 + 1;
	}

	public void displayGUIWorkbench(int var1, int var2, int var3) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9, true));
		this.openContainer = new ContainerWorkbench(this.inventory, this.worldObj, var1, var2, var3);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIEnchantment(int var1, int var2, int var3, String var4) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 4, var4 == null ? "" : var4, 9, var4 != null));
		this.openContainer = new ContainerEnchantment(this.inventory, this.worldObj, var1, var2, var3);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIAnvil(int var1, int var2, int var3) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 8, "Repairing", 9, true));
		this.openContainer = new ContainerRepair(this.inventory, this.worldObj, var1, var2, var3, this);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIChest(IInventory var1) {
		if(this.openContainer != this.inventoryContainer) {
			this.closeScreen();
		}

		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 0, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerChest(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIHopper(TileEntityHopper var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 9, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerHopper(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIHopperMinecart(EntityMinecartHopper var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 9, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerHopper(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 2, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerFurnace(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIDispenser(TileEntityDispenser var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, var1 instanceof TileEntityDropper ? 10 : 3, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerDispenser(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIBrewingStand(TileEntityBrewingStand var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 5, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerBrewingStand(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIBeacon(TileEntityBeacon var1) {
		this.incrementWindowID();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 7, var1.getInvName(), var1.getSizeInventory(), var1.isInvNameLocalized()));
		this.openContainer = new ContainerBeacon(this.inventory, var1);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
	}

	public void displayGUIMerchant(IMerchant var1, String var2) {
		this.incrementWindowID();
		this.openContainer = new ContainerMerchant(this.inventory, var1, this.worldObj);
		this.openContainer.windowId = this.currentWindowId;
		this.openContainer.addCraftingToCrafters(this);
		InventoryMerchant var3 = ((ContainerMerchant)this.openContainer).getMerchantInventory();
		this.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(this.currentWindowId, 6, var2 == null ? "" : var2, var3.getSizeInventory(), var2 != null));
		MerchantRecipeList var4 = var1.getRecipes(this);
		if(var4 != null) {
			try {
				ByteArrayOutputStream var5 = new ByteArrayOutputStream();
				DataOutputStream var6 = new DataOutputStream(var5);
				var6.writeInt(this.currentWindowId);
				var4.writeRecipiesToStream(var6);
				this.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload("MC|TrList", var5.toByteArray()));
			} catch (IOException var7) {
				var7.printStackTrace();
			}
		}

	}

	public void sendSlotContents(Container var1, int var2, ItemStack var3) {
		if(!(var1.getSlot(var2) instanceof SlotCrafting)) {
			if(!this.playerInventoryBeingManipulated) {
				this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(var1.windowId, var2, var3));
			}
		}
	}

	public void sendContainerToPlayer(Container var1) {
		this.sendContainerAndContentsToPlayer(var1, var1.getInventory());
	}

	public void sendContainerAndContentsToPlayer(Container var1, List var2) {
		this.playerNetServerHandler.sendPacketToPlayer(new Packet104WindowItems(var1.windowId, var2));
		this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
	}

	public void sendProgressBarUpdate(Container var1, int var2, int var3) {
		this.playerNetServerHandler.sendPacketToPlayer(new Packet105UpdateProgressbar(var1.windowId, var2, var3));
	}

	public void closeScreen() {
		this.playerNetServerHandler.sendPacketToPlayer(new Packet101CloseWindow(this.openContainer.windowId));
		this.closeInventory();
	}

	public void updateHeldItem() {
		if(!this.playerInventoryBeingManipulated) {
			this.playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
		}
	}

	public void closeInventory() {
		this.openContainer.onCraftGuiClosed(this);
		this.openContainer = this.inventoryContainer;
	}

	public void addStat(StatBase var1, int var2) {
		if(var1 != null) {
			if(!var1.isIndependent) {
				while(var2 > 100) {
					this.playerNetServerHandler.sendPacketToPlayer(new Packet200Statistic(var1.statId, 100));
					var2 -= 100;
				}

				this.playerNetServerHandler.sendPacketToPlayer(new Packet200Statistic(var1.statId, var2));
			}

		}
	}

	public void mountEntityAndWakeUp() {
		if(this.riddenByEntity != null) {
			this.riddenByEntity.mountEntity(this);
		}

		if(this.sleeping) {
			this.wakeUpPlayer(true, false, false);
		}

	}

	public void setPlayerHealthUpdated() {
		this.lastHealth = -99999999;
	}

	public void addChatMessage(String var1) {
		StringTranslate var2 = StringTranslate.getInstance();
		String var3 = var2.translateKey(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(var3));
	}

	protected void onItemUseFinish() {
		this.playerNetServerHandler.sendPacketToPlayer(new Packet38EntityStatus(this.entityId, (byte)9));
		super.onItemUseFinish();
	}

	public void setItemInUse(ItemStack var1, int var2) {
		super.setItemInUse(var1, var2);
		if(var1 != null && var1.getItem() != null && var1.getItem().getItemUseAction(var1) == EnumAction.eat) {
			this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(this, 5));
		}

	}

	public void clonePlayer(EntityPlayer var1, boolean var2) {
		super.clonePlayer(var1, var2);
		this.lastExperience = -1;
		this.lastHealth = -1;
		this.lastFoodLevel = -1;
		this.destroyedItemsNetCache.addAll(((EntityPlayerMP)var1).destroyedItemsNetCache);
	}

	protected void onNewPotionEffect(PotionEffect var1) {
		super.onNewPotionEffect(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.entityId, var1));
	}

	protected void onChangedPotionEffect(PotionEffect var1) {
		super.onChangedPotionEffect(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(this.entityId, var1));
	}

	protected void onFinishedPotionEffect(PotionEffect var1) {
		super.onFinishedPotionEffect(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet42RemoveEntityEffect(this.entityId, var1));
	}

	public void setPositionAndUpdate(double var1, double var3, double var5) {
		this.playerNetServerHandler.setPlayerLocation(var1, var3, var5, this.rotationYaw, this.rotationPitch);
	}

	public void onCriticalHit(Entity var1) {
		this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(var1, 6));
	}

	public void onEnchantmentCritical(Entity var1) {
		this.getServerForPlayer().getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet18Animation(var1, 7));
	}

	public void sendPlayerAbilities() {
		if(this.playerNetServerHandler != null) {
			this.playerNetServerHandler.sendPacketToPlayer(new Packet202PlayerAbilities(this.capabilities));
		}
	}

	public WorldServer getServerForPlayer() {
		return (WorldServer)this.worldObj;
	}

	public void setGameType(EnumGameType var1) {
		this.theItemInWorldManager.setGameType(var1);
		this.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(3, var1.getID()));
	}

	public void sendChatToPlayer(String var1) {
		this.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(var1));
	}

	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return "seed".equals(var2) && !this.mcServer.isDedicatedServer() ? true : (!"tell".equals(var2) && !"help".equals(var2) && !"me".equals(var2) ? this.mcServer.getConfigurationManager().areCommandsAllowed(this.username) : true);
	}

	public String getPlayerIP() {
		String var1 = this.playerNetServerHandler.netManager.getSocketAddress().toString();
		var1 = var1.substring(var1.indexOf("/") + 1);
		var1 = var1.substring(0, var1.indexOf(":"));
		return var1;
	}

	public void updateClientInfo(Packet204ClientInfo var1) {
		if(this.translator.getLanguageList().containsKey(var1.getLanguage())) {
			this.translator.setLanguage(var1.getLanguage(), false);
		}

		int var2 = 256 >> var1.getRenderDistance();
		if(var2 > 3 && var2 < 15) {
			this.renderDistance = var2;
		}

		this.chatVisibility = var1.getChatVisibility();
		this.chatColours = var1.getChatColours();
		if(this.mcServer.isSinglePlayer() && this.mcServer.getServerOwner().equals(this.username)) {
			this.mcServer.setDifficultyForAllWorlds(var1.getDifficulty());
		}

		this.setHideCape(1, !var1.getShowCape());
	}

	public StringTranslate getTranslator() {
		return this.translator;
	}

	public int getChatVisibility() {
		return this.chatVisibility;
	}

	public void requestTexturePackLoad(String var1, int var2) {
		String var3 = var1 + "\u0000" + var2;
		this.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload("MC|TPack", var3.getBytes()));
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ));
	}
}
