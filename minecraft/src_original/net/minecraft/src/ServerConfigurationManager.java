package net.minecraft.src;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;

public abstract class ServerConfigurationManager {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
	private final MinecraftServer mcServer;
	public final List playerEntityList = new ArrayList();
	private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
	private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));
	private Set ops = new HashSet();
	private Set whiteListedPlayers = new HashSet();
	private IPlayerFileData playerNBTManagerObj;
	private boolean whiteListEnforced;
	protected int maxPlayers;
	protected int viewDistance;
	private EnumGameType gameType;
	private boolean commandsAllowedForAll;
	private int playerPingIndex = 0;

	public ServerConfigurationManager(MinecraftServer var1) {
		this.mcServer = var1;
		this.bannedPlayers.setListActive(false);
		this.bannedIPs.setListActive(false);
		this.maxPlayers = 8;
	}

	public void initializeConnectionToPlayer(INetworkManager var1, EntityPlayerMP var2) {
		NBTTagCompound var3 = this.readPlayerDataFromFile(var2);
		var2.setWorld(this.mcServer.worldServerForDimension(var2.dimension));
		var2.theItemInWorldManager.setWorld((WorldServer)var2.worldObj);
		String var4 = "local";
		if(var1.getSocketAddress() != null) {
			var4 = var1.getSocketAddress().toString();
		}

		this.mcServer.getLogAgent().logInfo(var2.username + "[" + var4 + "] logged in with entity id " + var2.entityId + " at (" + var2.posX + ", " + var2.posY + ", " + var2.posZ + ")");
		WorldServer var5 = this.mcServer.worldServerForDimension(var2.dimension);
		ChunkCoordinates var6 = var5.getSpawnPoint();
		this.func_72381_a(var2, (EntityPlayerMP)null, var5);
		NetServerHandler var7 = new NetServerHandler(this.mcServer, var1, var2);
		var7.sendPacketToPlayer(new Packet1Login(var2.entityId, var5.getWorldInfo().getTerrainType(), var2.theItemInWorldManager.getGameType(), var5.getWorldInfo().isHardcoreModeEnabled(), var5.provider.dimensionId, var5.difficultySetting, var5.getHeight(), this.getMaxPlayers()));
		var7.sendPacketToPlayer(new Packet6SpawnPosition(var6.posX, var6.posY, var6.posZ));
		var7.sendPacketToPlayer(new Packet202PlayerAbilities(var2.capabilities));
		var7.sendPacketToPlayer(new Packet16BlockItemSwitch(var2.inventory.currentItem));
		this.func_96456_a((ServerScoreboard)var5.getScoreboard(), var2);
		this.updateTimeAndWeatherForPlayer(var2, var5);
		this.sendPacketToAllPlayers(new Packet3Chat(EnumChatFormatting.YELLOW + var2.getTranslatedEntityName() + EnumChatFormatting.YELLOW + " joined the game."));
		this.playerLoggedIn(var2);
		var7.setPlayerLocation(var2.posX, var2.posY, var2.posZ, var2.rotationYaw, var2.rotationPitch);
		this.mcServer.getNetworkThread().addPlayer(var7);
		var7.sendPacketToPlayer(new Packet4UpdateTime(var5.getTotalWorldTime(), var5.getWorldTime()));
		if(this.mcServer.getTexturePack().length() > 0) {
			var2.requestTexturePackLoad(this.mcServer.getTexturePack(), this.mcServer.textureSize());
		}

		Iterator var8 = var2.getActivePotionEffects().iterator();

		while(var8.hasNext()) {
			PotionEffect var9 = (PotionEffect)var8.next();
			var7.sendPacketToPlayer(new Packet41EntityEffect(var2.entityId, var9));
		}

		var2.addSelfToInternalCraftingInventory();
		if(var3 != null && var3.hasKey("Riding")) {
			Entity var10 = EntityList.createEntityFromNBT(var3.getCompoundTag("Riding"), var5);
			if(var10 != null) {
				var10.field_98038_p = true;
				var5.spawnEntityInWorld(var10);
				var2.mountEntity(var10);
				var10.field_98038_p = false;
			}
		}

	}

	protected void func_96456_a(ServerScoreboard var1, EntityPlayerMP var2) {
		HashSet var3 = new HashSet();
		Iterator var4 = var1.func_96525_g().iterator();

		while(var4.hasNext()) {
			ScorePlayerTeam var5 = (ScorePlayerTeam)var4.next();
			var2.playerNetServerHandler.sendPacketToPlayer(new Packet209SetPlayerTeam(var5, 0));
		}

		for(int var9 = 0; var9 < 3; ++var9) {
			ScoreObjective var10 = var1.func_96539_a(var9);
			if(var10 != null && !var3.contains(var10)) {
				List var6 = var1.func_96550_d(var10);
				Iterator var7 = var6.iterator();

				while(var7.hasNext()) {
					Packet var8 = (Packet)var7.next();
					var2.playerNetServerHandler.sendPacketToPlayer(var8);
				}

				var3.add(var10);
			}
		}

	}

	public void setPlayerManager(WorldServer[] var1) {
		this.playerNBTManagerObj = var1[0].getSaveHandler().getSaveHandler();
	}

	public void func_72375_a(EntityPlayerMP var1, WorldServer var2) {
		WorldServer var3 = var1.getServerForPlayer();
		if(var2 != null) {
			var2.getPlayerManager().removePlayer(var1);
		}

		var3.getPlayerManager().addPlayer(var1);
		var3.theChunkProviderServer.loadChunk((int)var1.posX >> 4, (int)var1.posZ >> 4);
	}

	public int getEntityViewDistance() {
		return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
	}

	public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP var1) {
		NBTTagCompound var2 = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
		NBTTagCompound var3;
		if(var1.getCommandSenderName().equals(this.mcServer.getServerOwner()) && var2 != null) {
			var1.readFromNBT(var2);
			var3 = var2;
			System.out.println("loading single player");
		} else {
			var3 = this.playerNBTManagerObj.readPlayerData(var1);
		}

		return var3;
	}

	protected void writePlayerData(EntityPlayerMP var1) {
		this.playerNBTManagerObj.writePlayerData(var1);
	}

	public void playerLoggedIn(EntityPlayerMP var1) {
		this.sendPacketToAllPlayers(new Packet201PlayerInfo(var1.username, true, 1000));
		this.playerEntityList.add(var1);
		WorldServer var2 = this.mcServer.worldServerForDimension(var1.dimension);
		var2.spawnEntityInWorld(var1);
		this.func_72375_a(var1, (WorldServer)null);

		for(int var3 = 0; var3 < this.playerEntityList.size(); ++var3) {
			EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntityList.get(var3);
			var1.playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(var4.username, true, var4.ping));
		}

	}

	public void serverUpdateMountedMovingPlayer(EntityPlayerMP var1) {
		var1.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(var1);
	}

	public void playerLoggedOut(EntityPlayerMP var1) {
		this.writePlayerData(var1);
		WorldServer var2 = var1.getServerForPlayer();
		if(var1.ridingEntity != null) {
			var2.removeEntity(var1.ridingEntity);
			System.out.println("removing player mount");
		}

		var2.removeEntity(var1);
		var2.getPlayerManager().removePlayer(var1);
		this.playerEntityList.remove(var1);
		this.sendPacketToAllPlayers(new Packet201PlayerInfo(var1.username, false, 9999));
	}

	public String allowUserToConnect(SocketAddress var1, String var2) {
		if(this.bannedPlayers.isBanned(var2)) {
			BanEntry var6 = (BanEntry)this.bannedPlayers.getBannedList().get(var2);
			String var7 = "You are banned from this server!\nReason: " + var6.getBanReason();
			if(var6.getBanEndDate() != null) {
				var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
			}

			return var7;
		} else if(!this.isAllowedToLogin(var2)) {
			return "You are not white-listed on this server!";
		} else {
			String var3 = var1.toString();
			var3 = var3.substring(var3.indexOf("/") + 1);
			var3 = var3.substring(0, var3.indexOf(":"));
			if(this.bannedIPs.isBanned(var3)) {
				BanEntry var4 = (BanEntry)this.bannedIPs.getBannedList().get(var3);
				String var5 = "Your IP address is banned from this server!\nReason: " + var4.getBanReason();
				if(var4.getBanEndDate() != null) {
					var5 = var5 + "\nYour ban will be removed on " + dateFormat.format(var4.getBanEndDate());
				}

				return var5;
			} else {
				return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
			}
		}
	}

	public EntityPlayerMP createPlayerForUser(String var1) {
		ArrayList var2 = new ArrayList();

		EntityPlayerMP var4;
		for(int var3 = 0; var3 < this.playerEntityList.size(); ++var3) {
			var4 = (EntityPlayerMP)this.playerEntityList.get(var3);
			if(var4.username.equalsIgnoreCase(var1)) {
				var2.add(var4);
			}
		}

		Iterator var5 = var2.iterator();

		while(var5.hasNext()) {
			var4 = (EntityPlayerMP)var5.next();
			var4.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
		}

		Object var6;
		if(this.mcServer.isDemo()) {
			var6 = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
		} else {
			var6 = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
		}

		return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), var1, (ItemInWorldManager)var6);
	}

	public EntityPlayerMP respawnPlayer(EntityPlayerMP var1, int var2, boolean var3) {
		var1.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(var1);
		var1.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(var1);
		var1.getServerForPlayer().getPlayerManager().removePlayer(var1);
		this.playerEntityList.remove(var1);
		this.mcServer.worldServerForDimension(var1.dimension).removePlayerEntityDangerously(var1);
		ChunkCoordinates var4 = var1.getBedLocation();
		boolean var5 = var1.isSpawnForced();
		var1.dimension = var2;
		Object var6;
		if(this.mcServer.isDemo()) {
			var6 = new DemoWorldManager(this.mcServer.worldServerForDimension(var1.dimension));
		} else {
			var6 = new ItemInWorldManager(this.mcServer.worldServerForDimension(var1.dimension));
		}

		EntityPlayerMP var7 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(var1.dimension), var1.username, (ItemInWorldManager)var6);
		var7.playerNetServerHandler = var1.playerNetServerHandler;
		var7.clonePlayer(var1, var3);
		var7.entityId = var1.entityId;
		WorldServer var8 = this.mcServer.worldServerForDimension(var1.dimension);
		this.func_72381_a(var7, var1, var8);
		ChunkCoordinates var9;
		if(var4 != null) {
			var9 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(var1.dimension), var4, var5);
			if(var9 != null) {
				var7.setLocationAndAngles((double)((float)var9.posX + 0.5F), (double)((float)var9.posY + 0.1F), (double)((float)var9.posZ + 0.5F), 0.0F, 0.0F);
				var7.setSpawnChunk(var4, var5);
			} else {
				var7.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(0, 0));
			}
		}

		var8.theChunkProviderServer.loadChunk((int)var7.posX >> 4, (int)var7.posZ >> 4);

		while(!var8.getCollidingBoundingBoxes(var7, var7.boundingBox).isEmpty()) {
			var7.setPosition(var7.posX, var7.posY + 1.0D, var7.posZ);
		}

		var7.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(var7.dimension, (byte)var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.worldObj.getHeight(), var7.theItemInWorldManager.getGameType()));
		var9 = var8.getSpawnPoint();
		var7.playerNetServerHandler.setPlayerLocation(var7.posX, var7.posY, var7.posZ, var7.rotationYaw, var7.rotationPitch);
		var7.playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(var9.posX, var9.posY, var9.posZ));
		var7.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(var7.experience, var7.experienceTotal, var7.experienceLevel));
		this.updateTimeAndWeatherForPlayer(var7, var8);
		var8.getPlayerManager().addPlayer(var7);
		var8.spawnEntityInWorld(var7);
		this.playerEntityList.add(var7);
		var7.addSelfToInternalCraftingInventory();
		var7.setEntityHealth(var7.getHealth());
		return var7;
	}

	public void transferPlayerToDimension(EntityPlayerMP var1, int var2) {
		int var3 = var1.dimension;
		WorldServer var4 = this.mcServer.worldServerForDimension(var1.dimension);
		var1.dimension = var2;
		WorldServer var5 = this.mcServer.worldServerForDimension(var1.dimension);
		var1.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(var1.dimension, (byte)var1.worldObj.difficultySetting, var5.getWorldInfo().getTerrainType(), var5.getHeight(), var1.theItemInWorldManager.getGameType()));
		var4.removePlayerEntityDangerously(var1);
		var1.isDead = false;
		this.transferEntityToWorld(var1, var3, var4, var5);
		this.func_72375_a(var1, var4);
		var1.playerNetServerHandler.setPlayerLocation(var1.posX, var1.posY, var1.posZ, var1.rotationYaw, var1.rotationPitch);
		var1.theItemInWorldManager.setWorld(var5);
		this.updateTimeAndWeatherForPlayer(var1, var5);
		this.syncPlayerInventory(var1);
		Iterator var6 = var1.getActivePotionEffects().iterator();

		while(var6.hasNext()) {
			PotionEffect var7 = (PotionEffect)var6.next();
			var1.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(var1.entityId, var7));
		}

	}

	public void transferEntityToWorld(Entity var1, int var2, WorldServer var3, WorldServer var4) {
		double var5 = var1.posX;
		double var7 = var1.posZ;
		double var9 = 8.0D;
		double var11 = var1.posX;
		double var13 = var1.posY;
		double var15 = var1.posZ;
		float var17 = var1.rotationYaw;
		var3.theProfiler.startSection("moving");
		if(var1.dimension == -1) {
			var5 /= var9;
			var7 /= var9;
			var1.setLocationAndAngles(var5, var1.posY, var7, var1.rotationYaw, var1.rotationPitch);
			if(var1.isEntityAlive()) {
				var3.updateEntityWithOptionalForce(var1, false);
			}
		} else if(var1.dimension == 0) {
			var5 *= var9;
			var7 *= var9;
			var1.setLocationAndAngles(var5, var1.posY, var7, var1.rotationYaw, var1.rotationPitch);
			if(var1.isEntityAlive()) {
				var3.updateEntityWithOptionalForce(var1, false);
			}
		} else {
			ChunkCoordinates var18;
			if(var2 == 1) {
				var18 = var4.getSpawnPoint();
			} else {
				var18 = var4.getEntrancePortalLocation();
			}

			var5 = (double)var18.posX;
			var1.posY = (double)var18.posY;
			var7 = (double)var18.posZ;
			var1.setLocationAndAngles(var5, var1.posY, var7, 90.0F, 0.0F);
			if(var1.isEntityAlive()) {
				var3.updateEntityWithOptionalForce(var1, false);
			}
		}

		var3.theProfiler.endSection();
		if(var2 != 1) {
			var3.theProfiler.startSection("placing");
			var5 = (double)MathHelper.clamp_int((int)var5, -29999872, 29999872);
			var7 = (double)MathHelper.clamp_int((int)var7, -29999872, 29999872);
			if(var1.isEntityAlive()) {
				var4.spawnEntityInWorld(var1);
				var1.setLocationAndAngles(var5, var1.posY, var7, var1.rotationYaw, var1.rotationPitch);
				var4.updateEntityWithOptionalForce(var1, false);
				var4.getDefaultTeleporter().placeInPortal(var1, var11, var13, var15, var17);
			}

			var3.theProfiler.endSection();
		}

		var1.setWorld(var4);
	}

	public void sendPlayerInfoToAllPlayers() {
		if(++this.playerPingIndex > 600) {
			this.playerPingIndex = 0;
		}

		if(this.playerPingIndex < this.playerEntityList.size()) {
			EntityPlayerMP var1 = (EntityPlayerMP)this.playerEntityList.get(this.playerPingIndex);
			this.sendPacketToAllPlayers(new Packet201PlayerInfo(var1.username, true, var1.ping));
		}

	}

	public void sendPacketToAllPlayers(Packet var1) {
		for(int var2 = 0; var2 < this.playerEntityList.size(); ++var2) {
			((EntityPlayerMP)this.playerEntityList.get(var2)).playerNetServerHandler.sendPacketToPlayer(var1);
		}

	}

	public void sendPacketToAllPlayersInDimension(Packet var1, int var2) {
		for(int var3 = 0; var3 < this.playerEntityList.size(); ++var3) {
			EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntityList.get(var3);
			if(var4.dimension == var2) {
				var4.playerNetServerHandler.sendPacketToPlayer(var1);
			}
		}

	}

	public String getPlayerListAsString() {
		String var1 = "";

		for(int var2 = 0; var2 < this.playerEntityList.size(); ++var2) {
			if(var2 > 0) {
				var1 = var1 + ", ";
			}

			var1 = var1 + ((EntityPlayerMP)this.playerEntityList.get(var2)).username;
		}

		return var1;
	}

	public String[] getAllUsernames() {
		String[] var1 = new String[this.playerEntityList.size()];

		for(int var2 = 0; var2 < this.playerEntityList.size(); ++var2) {
			var1[var2] = ((EntityPlayerMP)this.playerEntityList.get(var2)).username;
		}

		return var1;
	}

	public BanList getBannedPlayers() {
		return this.bannedPlayers;
	}

	public BanList getBannedIPs() {
		return this.bannedIPs;
	}

	public void addOp(String var1) {
		this.ops.add(var1.toLowerCase());
	}

	public void removeOp(String var1) {
		this.ops.remove(var1.toLowerCase());
	}

	public boolean isAllowedToLogin(String var1) {
		var1 = var1.trim().toLowerCase();
		return !this.whiteListEnforced || this.ops.contains(var1) || this.whiteListedPlayers.contains(var1);
	}

	public boolean areCommandsAllowed(String var1) {
		return this.ops.contains(var1.trim().toLowerCase()) || this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(var1) || this.commandsAllowedForAll;
	}

	public EntityPlayerMP getPlayerForUsername(String var1) {
		Iterator var2 = this.playerEntityList.iterator();

		EntityPlayerMP var3;
		do {
			if(!var2.hasNext()) {
				return null;
			}

			var3 = (EntityPlayerMP)var2.next();
		} while(!var3.username.equalsIgnoreCase(var1));

		return var3;
	}

	public List findPlayers(ChunkCoordinates var1, int var2, int var3, int var4, int var5, int var6, int var7, Map var8, String var9, String var10) {
		if(this.playerEntityList.isEmpty()) {
			return null;
		} else {
			Object var11 = new ArrayList();
			boolean var12 = var4 < 0;
			int var13 = var2 * var2;
			int var14 = var3 * var3;
			var4 = MathHelper.abs_int(var4);

			for(int var15 = 0; var15 < this.playerEntityList.size(); ++var15) {
				EntityPlayerMP var16 = (EntityPlayerMP)this.playerEntityList.get(var15);
				boolean var17;
				if(var9 != null) {
					var17 = var9.startsWith("!");
					if(var17) {
						var9 = var9.substring(1);
					}

					if(var17 == var9.equalsIgnoreCase(var16.getEntityName())) {
						continue;
					}
				}

				if(var10 != null) {
					var17 = var10.startsWith("!");
					if(var17) {
						var10 = var10.substring(1);
					}

					ScorePlayerTeam var18 = var16.getTeam();
					String var19 = var18 == null ? "" : var18.func_96661_b();
					if(var17 == var10.equalsIgnoreCase(var19)) {
						continue;
					}
				}

				if(var1 != null && (var2 > 0 || var3 > 0)) {
					float var20 = var1.getDistanceSquaredToChunkCoordinates(var16.getPlayerCoordinates());
					if(var2 > 0 && var20 < (float)var13 || var3 > 0 && var20 > (float)var14) {
						continue;
					}
				}

				if(this.func_96457_a(var16, var8) && (var5 == EnumGameType.NOT_SET.getID() || var5 == var16.theItemInWorldManager.getGameType().getID()) && (var6 <= 0 || var16.experienceLevel >= var6) && var16.experienceLevel <= var7) {
					((List)var11).add(var16);
				}
			}

			if(var1 != null) {
				Collections.sort((List)var11, new PlayerPositionComparator(var1));
			}

			if(var12) {
				Collections.reverse((List)var11);
			}

			if(var4 > 0) {
				var11 = ((List)var11).subList(0, Math.min(var4, ((List)var11).size()));
			}

			return (List)var11;
		}
	}

	private boolean func_96457_a(EntityPlayer var1, Map var2) {
		if(var2 != null && var2.size() != 0) {
			Iterator var3 = var2.entrySet().iterator();

			Entry var4;
			boolean var6;
			int var10;
			do {
				if(!var3.hasNext()) {
					return true;
				}

				var4 = (Entry)var3.next();
				String var5 = (String)var4.getKey();
				var6 = false;
				if(var5.endsWith("_min") && var5.length() > 4) {
					var6 = true;
					var5 = var5.substring(0, var5.length() - 4);
				}

				Scoreboard var7 = var1.getWorldScoreboard();
				ScoreObjective var8 = var7.getObjective(var5);
				if(var8 == null) {
					return false;
				}

				Score var9 = var1.getWorldScoreboard().func_96529_a(var1.getEntityName(), var8);
				var10 = var9.func_96652_c();
				if(var10 < ((Integer)var4.getValue()).intValue() && var6) {
					return false;
				}
			} while(var10 <= ((Integer)var4.getValue()).intValue() || var6);

			return false;
		} else {
			return true;
		}
	}

	public void sendToAllNear(double var1, double var3, double var5, double var7, int var9, Packet var10) {
		this.sendToAllNearExcept((EntityPlayer)null, var1, var3, var5, var7, var9, var10);
	}

	public void sendToAllNearExcept(EntityPlayer var1, double var2, double var4, double var6, double var8, int var10, Packet var11) {
		for(int var12 = 0; var12 < this.playerEntityList.size(); ++var12) {
			EntityPlayerMP var13 = (EntityPlayerMP)this.playerEntityList.get(var12);
			if(var13 != var1 && var13.dimension == var10) {
				double var14 = var2 - var13.posX;
				double var16 = var4 - var13.posY;
				double var18 = var6 - var13.posZ;
				if(var14 * var14 + var16 * var16 + var18 * var18 < var8 * var8) {
					var13.playerNetServerHandler.sendPacketToPlayer(var11);
				}
			}
		}

	}

	public void saveAllPlayerData() {
		for(int var1 = 0; var1 < this.playerEntityList.size(); ++var1) {
			this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(var1));
		}

	}

	public void addToWhiteList(String var1) {
		this.whiteListedPlayers.add(var1);
	}

	public void removeFromWhitelist(String var1) {
		this.whiteListedPlayers.remove(var1);
	}

	public Set getWhiteListedPlayers() {
		return this.whiteListedPlayers;
	}

	public Set getOps() {
		return this.ops;
	}

	public void loadWhiteList() {
	}

	public void updateTimeAndWeatherForPlayer(EntityPlayerMP var1, WorldServer var2) {
		var1.playerNetServerHandler.sendPacketToPlayer(new Packet4UpdateTime(var2.getTotalWorldTime(), var2.getWorldTime()));
		if(var2.isRaining()) {
			var1.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(1, 0));
		}

	}

	public void syncPlayerInventory(EntityPlayerMP var1) {
		var1.sendContainerToPlayer(var1.inventoryContainer);
		var1.setPlayerHealthUpdated();
		var1.playerNetServerHandler.sendPacketToPlayer(new Packet16BlockItemSwitch(var1.inventory.currentItem));
	}

	public int getCurrentPlayerCount() {
		return this.playerEntityList.size();
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public String[] getAvailablePlayerDat() {
		return this.mcServer.worldServers[0].getSaveHandler().getSaveHandler().getAvailablePlayerDat();
	}

	public boolean isWhiteListEnabled() {
		return this.whiteListEnforced;
	}

	public void setWhiteListEnabled(boolean var1) {
		this.whiteListEnforced = var1;
	}

	public List getPlayerList(String var1) {
		ArrayList var2 = new ArrayList();
		Iterator var3 = this.playerEntityList.iterator();

		while(var3.hasNext()) {
			EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
			if(var4.getPlayerIP().equals(var1)) {
				var2.add(var4);
			}
		}

		return var2;
	}

	public int getViewDistance() {
		return this.viewDistance;
	}

	public MinecraftServer getServerInstance() {
		return this.mcServer;
	}

	public NBTTagCompound getHostPlayerData() {
		return null;
	}

	public void setGameType(EnumGameType var1) {
		this.gameType = var1;
	}

	private void func_72381_a(EntityPlayerMP var1, EntityPlayerMP var2, World var3) {
		if(var2 != null) {
			var1.theItemInWorldManager.setGameType(var2.theItemInWorldManager.getGameType());
		} else if(this.gameType != null) {
			var1.theItemInWorldManager.setGameType(this.gameType);
		}

		var1.theItemInWorldManager.initializeGameType(var3.getWorldInfo().getGameType());
	}

	public void setCommandsAllowedForAll(boolean var1) {
		this.commandsAllowedForAll = var1;
	}

	public void removeAllPlayers() {
		while(!this.playerEntityList.isEmpty()) {
			((EntityPlayerMP)this.playerEntityList.get(0)).playerNetServerHandler.kickPlayerFromServer("Server closed");
		}

	}

	public void sendChatMsg(String var1) {
		this.mcServer.logInfo(var1);
		this.sendPacketToAllPlayers(new Packet3Chat(var1));
	}
}
