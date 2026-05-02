package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.server.MinecraftServer;

public class WorldServer extends World {
	private final MinecraftServer mcServer;
	private final EntityTracker theEntityTracker;
	private final PlayerManager thePlayerManager;
	private Set field_73064_N;
	private TreeSet pendingTickListEntries;
	public ChunkProviderServer theChunkProviderServer;
	public boolean canNotSave;
	private boolean allPlayersSleeping;
	private int updateEntityTick = 0;
	private final Teleporter field_85177_Q;
	private ServerBlockEventList[] blockEventCache = new ServerBlockEventList[]{new ServerBlockEventList((ServerBlockEvent)null), new ServerBlockEventList((ServerBlockEvent)null)};
	private int blockEventCacheIndex = 0;
	private static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.stick.itemID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.planks.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.wood.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Item.axeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.axeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.pickaxeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.pickaxeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 2, 3, 5), new WeightedRandomChestContent(Item.bread.itemID, 0, 2, 3, 3)};
	private ArrayList field_94579_S = new ArrayList();
	private IntHashMap entityIdMap;

	public WorldServer(MinecraftServer var1, ISaveHandler var2, String var3, int var4, WorldSettings var5, Profiler var6, ILogAgent var7) {
		super(var2, var3, var5, WorldProvider.getProviderForDimension(var4), var6, var7);
		this.mcServer = var1;
		this.theEntityTracker = new EntityTracker(this);
		this.thePlayerManager = new PlayerManager(this, var1.getConfigurationManager().getViewDistance());
		if(this.entityIdMap == null) {
			this.entityIdMap = new IntHashMap();
		}

		if(this.field_73064_N == null) {
			this.field_73064_N = new HashSet();
		}

		if(this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}

		this.field_85177_Q = new Teleporter(this);
		this.worldScoreboard = new ServerScoreboard(var1);
		ScoreboardSaveData var8 = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
		if(var8 == null) {
			var8 = new ScoreboardSaveData();
			this.mapStorage.setData("scoreboard", var8);
		}

		var8.func_96499_a(this.worldScoreboard);
		((ServerScoreboard)this.worldScoreboard).func_96547_a(var8);
	}

	public void tick() {
		super.tick();
		if(this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3) {
			this.difficultySetting = 3;
		}

		this.provider.worldChunkMgr.cleanupCache();
		if(this.areAllPlayersAsleep()) {
			boolean var1 = false;
			if(this.spawnHostileMobs && this.difficultySetting >= 1) {
			}

			if(!var1) {
				long var2 = this.worldInfo.getWorldTime() + 24000L;
				this.worldInfo.setWorldTime(var2 - var2 % 24000L);
				this.wakeAllPlayers();
			}
		}

		this.theProfiler.startSection("mobSpawner");
		if(this.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
			SpawnerAnimals.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
		}

		this.theProfiler.endStartSection("chunkSource");
		this.chunkProvider.unloadQueuedChunks();
		int var4 = this.calculateSkylightSubtracted(1.0F);
		if(var4 != this.skylightSubtracted) {
			this.skylightSubtracted = var4;
		}

		this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);
		this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
		this.theProfiler.endStartSection("tickPending");
		this.tickUpdates(false);
		this.theProfiler.endStartSection("tickTiles");
		this.tickBlocksAndAmbiance();
		this.theProfiler.endStartSection("chunkMap");
		this.thePlayerManager.updatePlayerInstances();
		this.theProfiler.endStartSection("village");
		this.villageCollectionObj.tick();
		this.villageSiegeObj.tick();
		this.theProfiler.endStartSection("portalForcer");
		this.field_85177_Q.removeStalePortalLocations(this.getTotalWorldTime());
		this.theProfiler.endSection();
		this.sendAndApplyBlockEvents();
	}

	public SpawnListEntry spawnRandomCreature(EnumCreatureType var1, int var2, int var3, int var4) {
		List var5 = this.getChunkProvider().getPossibleCreatures(var1, var2, var3, var4);
		return var5 != null && !var5.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, (Collection)var5) : null;
	}

	public void updateAllPlayersSleepingFlag() {
		this.allPlayersSleeping = !this.playerEntities.isEmpty();
		Iterator var1 = this.playerEntities.iterator();

		while(var1.hasNext()) {
			EntityPlayer var2 = (EntityPlayer)var1.next();
			if(!var2.isPlayerSleeping()) {
				this.allPlayersSleeping = false;
				break;
			}
		}

	}

	protected void wakeAllPlayers() {
		this.allPlayersSleeping = false;
		Iterator var1 = this.playerEntities.iterator();

		while(var1.hasNext()) {
			EntityPlayer var2 = (EntityPlayer)var1.next();
			if(var2.isPlayerSleeping()) {
				var2.wakeUpPlayer(false, false, true);
			}
		}

		this.resetRainAndThunder();
	}

	private void resetRainAndThunder() {
		this.worldInfo.setRainTime(0);
		this.worldInfo.setRaining(false);
		this.worldInfo.setThunderTime(0);
		this.worldInfo.setThundering(false);
	}

	public boolean areAllPlayersAsleep() {
		if(this.allPlayersSleeping && !this.isRemote) {
			Iterator var1 = this.playerEntities.iterator();

			EntityPlayer var2;
			do {
				if(!var1.hasNext()) {
					return true;
				}

				var2 = (EntityPlayer)var1.next();
			} while(var2.isPlayerFullyAsleep());

			return false;
		} else {
			return false;
		}
	}

	public void setSpawnLocation() {
		if(this.worldInfo.getSpawnY() <= 0) {
			this.worldInfo.setSpawnY(64);
		}

		int var1 = this.worldInfo.getSpawnX();
		int var2 = this.worldInfo.getSpawnZ();
		int var3 = 0;

		while(this.getFirstUncoveredBlock(var1, var2) == 0) {
			var1 += this.rand.nextInt(8) - this.rand.nextInt(8);
			var2 += this.rand.nextInt(8) - this.rand.nextInt(8);
			++var3;
			if(var3 == 10000) {
				break;
			}
		}

		this.worldInfo.setSpawnX(var1);
		this.worldInfo.setSpawnZ(var2);
	}

	protected void tickBlocksAndAmbiance() {
		super.tickBlocksAndAmbiance();
		int var1 = 0;
		int var2 = 0;
		Iterator var3 = this.activeChunkSet.iterator();

		while(var3.hasNext()) {
			ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
			int var5 = var4.chunkXPos * 16;
			int var6 = var4.chunkZPos * 16;
			this.theProfiler.startSection("getChunk");
			Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
			this.moodSoundAndLightCheck(var5, var6, var7);
			this.theProfiler.endStartSection("tickChunk");
			var7.updateSkylight();
			this.theProfiler.endStartSection("thunder");
			int var8;
			int var9;
			int var10;
			int var11;
			if(this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
				this.updateLCG = this.updateLCG * 3 + 1013904223;
				var8 = this.updateLCG >> 2;
				var9 = var5 + (var8 & 15);
				var10 = var6 + (var8 >> 8 & 15);
				var11 = this.getPrecipitationHeight(var9, var10);
				if(this.canLightningStrikeAt(var9, var11, var10)) {
					this.addWeatherEffect(new EntityLightningBolt(this, (double)var9, (double)var11, (double)var10));
				}
			}

			this.theProfiler.endStartSection("iceandsnow");
			int var13;
			if(this.rand.nextInt(16) == 0) {
				this.updateLCG = this.updateLCG * 3 + 1013904223;
				var8 = this.updateLCG >> 2;
				var9 = var8 & 15;
				var10 = var8 >> 8 & 15;
				var11 = this.getPrecipitationHeight(var9 + var5, var10 + var6);
				if(this.isBlockFreezableNaturally(var9 + var5, var11 - 1, var10 + var6)) {
					this.setBlock(var9 + var5, var11 - 1, var10 + var6, Block.ice.blockID);
				}

				if(this.isRaining() && this.canSnowAt(var9 + var5, var11, var10 + var6)) {
					this.setBlock(var9 + var5, var11, var10 + var6, Block.snow.blockID);
				}

				if(this.isRaining()) {
					BiomeGenBase var12 = this.getBiomeGenForCoords(var9 + var5, var10 + var6);
					if(var12.canSpawnLightningBolt()) {
						var13 = this.getBlockId(var9 + var5, var11 - 1, var10 + var6);
						if(var13 != 0) {
							Block.blocksList[var13].fillWithRain(this, var9 + var5, var11 - 1, var10 + var6);
						}
					}
				}
			}

			this.theProfiler.endStartSection("tickTiles");
			ExtendedBlockStorage[] var19 = var7.getBlockStorageArray();
			var9 = var19.length;

			for(var10 = 0; var10 < var9; ++var10) {
				ExtendedBlockStorage var20 = var19[var10];
				if(var20 != null && var20.getNeedsRandomTick()) {
					for(int var21 = 0; var21 < 3; ++var21) {
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						var13 = this.updateLCG >> 2;
						int var14 = var13 & 15;
						int var15 = var13 >> 8 & 15;
						int var16 = var13 >> 16 & 15;
						int var17 = var20.getExtBlockID(var14, var16, var15);
						++var2;
						Block var18 = Block.blocksList[var17];
						if(var18 != null && var18.getTickRandomly()) {
							++var1;
							var18.updateTick(this, var14 + var5, var16 + var20.getYLocation(), var15 + var6, this.rand);
						}
					}
				}
			}

			this.theProfiler.endSection();
		}

	}

	public boolean isBlockTickScheduled(int var1, int var2, int var3, int var4) {
		NextTickListEntry var5 = new NextTickListEntry(var1, var2, var3, var4);
		return this.field_94579_S.contains(var5);
	}

	public void scheduleBlockUpdate(int var1, int var2, int var3, int var4, int var5) {
		this.func_82740_a(var1, var2, var3, var4, var5, 0);
	}

	public void func_82740_a(int var1, int var2, int var3, int var4, int var5, int var6) {
		NextTickListEntry var7 = new NextTickListEntry(var1, var2, var3, var4);
		byte var8 = 0;
		if(this.scheduledUpdatesAreImmediate && var4 > 0) {
			if(Block.blocksList[var4].func_82506_l()) {
				if(this.checkChunksExist(var7.xCoord - var8, var7.yCoord - var8, var7.zCoord - var8, var7.xCoord + var8, var7.yCoord + var8, var7.zCoord + var8)) {
					int var9 = this.getBlockId(var7.xCoord, var7.yCoord, var7.zCoord);
					if(var9 == var7.blockID && var9 > 0) {
						Block.blocksList[var9].updateTick(this, var7.xCoord, var7.yCoord, var7.zCoord, this.rand);
					}
				}

				return;
			}

			var5 = 1;
		}

		if(this.checkChunksExist(var1 - var8, var2 - var8, var3 - var8, var1 + var8, var2 + var8, var3 + var8)) {
			if(var4 > 0) {
				var7.setScheduledTime((long)var5 + this.worldInfo.getWorldTotalTime());
				var7.func_82753_a(var6);
			}

			if(!this.field_73064_N.contains(var7)) {
				this.field_73064_N.add(var7);
				this.pendingTickListEntries.add(var7);
			}
		}

	}

	public void scheduleBlockUpdateFromLoad(int var1, int var2, int var3, int var4, int var5, int var6) {
		NextTickListEntry var7 = new NextTickListEntry(var1, var2, var3, var4);
		var7.func_82753_a(var6);
		if(var4 > 0) {
			var7.setScheduledTime((long)var5 + this.worldInfo.getWorldTotalTime());
		}

		if(!this.field_73064_N.contains(var7)) {
			this.field_73064_N.add(var7);
			this.pendingTickListEntries.add(var7);
		}

	}

	public void updateEntities() {
		if(this.playerEntities.isEmpty()) {
			if(this.updateEntityTick++ >= 1200) {
				return;
			}
		} else {
			this.resetUpdateEntityTick();
		}

		super.updateEntities();
	}

	public void resetUpdateEntityTick() {
		this.updateEntityTick = 0;
	}

	public boolean tickUpdates(boolean var1) {
		int var2 = this.pendingTickListEntries.size();
		if(var2 != this.field_73064_N.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if(var2 > 1000) {
				var2 = 1000;
			}

			this.theProfiler.startSection("cleaning");

			NextTickListEntry var4;
			for(int var3 = 0; var3 < var2; ++var3) {
				var4 = (NextTickListEntry)this.pendingTickListEntries.first();
				if(!var1 && var4.scheduledTime > this.worldInfo.getWorldTotalTime()) {
					break;
				}

				this.pendingTickListEntries.remove(var4);
				this.field_73064_N.remove(var4);
				this.field_94579_S.add(var4);
			}

			this.theProfiler.endSection();
			this.theProfiler.startSection("ticking");
			Iterator var14 = this.field_94579_S.iterator();

			while(var14.hasNext()) {
				var4 = (NextTickListEntry)var14.next();
				var14.remove();
				byte var5 = 0;
				if(this.checkChunksExist(var4.xCoord - var5, var4.yCoord - var5, var4.zCoord - var5, var4.xCoord + var5, var4.yCoord + var5, var4.zCoord + var5)) {
					int var6 = this.getBlockId(var4.xCoord, var4.yCoord, var4.zCoord);
					if(var6 > 0 && Block.isAssociatedBlockID(var6, var4.blockID)) {
						try {
							Block.blocksList[var6].updateTick(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
						} catch (Throwable var13) {
							CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while ticking a block");
							CrashReportCategory var9 = var8.makeCategory("Block being ticked");

							int var10;
							try {
								var10 = this.getBlockMetadata(var4.xCoord, var4.yCoord, var4.zCoord);
							} catch (Throwable var12) {
								var10 = -1;
							}

							CrashReportCategory.func_85068_a(var9, var4.xCoord, var4.yCoord, var4.zCoord, var6, var10);
							throw new ReportedException(var8);
						}
					}
				} else {
					this.scheduleBlockUpdate(var4.xCoord, var4.yCoord, var4.zCoord, var4.blockID, 0);
				}
			}

			this.theProfiler.endSection();
			this.field_94579_S.clear();
			return !this.pendingTickListEntries.isEmpty();
		}
	}

	public List getPendingBlockUpdates(Chunk var1, boolean var2) {
		ArrayList var3 = null;
		ChunkCoordIntPair var4 = var1.getChunkCoordIntPair();
		int var5 = (var4.chunkXPos << 4) - 2;
		int var6 = var5 + 16 + 2;
		int var7 = (var4.chunkZPos << 4) - 2;
		int var8 = var7 + 16 + 2;

		for(int var9 = 0; var9 < 2; ++var9) {
			Iterator var10;
			if(var9 == 0) {
				var10 = this.pendingTickListEntries.iterator();
			} else {
				var10 = this.field_94579_S.iterator();
				if(!this.field_94579_S.isEmpty()) {
					System.out.println(this.field_94579_S.size());
				}
			}

			while(var10.hasNext()) {
				NextTickListEntry var11 = (NextTickListEntry)var10.next();
				if(var11.xCoord >= var5 && var11.xCoord < var6 && var11.zCoord >= var7 && var11.zCoord < var8) {
					if(var2) {
						this.field_73064_N.remove(var11);
						var10.remove();
					}

					if(var3 == null) {
						var3 = new ArrayList();
					}

					var3.add(var11);
				}
			}
		}

		return var3;
	}

	public void updateEntityWithOptionalForce(Entity var1, boolean var2) {
		if(!this.mcServer.getCanSpawnAnimals() && (var1 instanceof EntityAnimal || var1 instanceof EntityWaterMob)) {
			var1.setDead();
		}

		if(!this.mcServer.getCanSpawnNPCs() && var1 instanceof INpc) {
			var1.setDead();
		}

		if(!(var1.riddenByEntity instanceof EntityPlayer)) {
			super.updateEntityWithOptionalForce(var1, var2);
		}

	}

	public void uncheckedUpdateEntity(Entity var1, boolean var2) {
		super.updateEntityWithOptionalForce(var1, var2);
	}

	protected IChunkProvider createChunkProvider() {
		IChunkLoader var1 = this.saveHandler.getChunkLoader(this.provider);
		this.theChunkProviderServer = new ChunkProviderServer(this, var1, this.provider.createChunkGenerator());
		return this.theChunkProviderServer;
	}

	public List getAllTileEntityInBox(int var1, int var2, int var3, int var4, int var5, int var6) {
		ArrayList var7 = new ArrayList();

		for(int var8 = 0; var8 < this.loadedTileEntityList.size(); ++var8) {
			TileEntity var9 = (TileEntity)this.loadedTileEntityList.get(var8);
			if(var9.xCoord >= var1 && var9.yCoord >= var2 && var9.zCoord >= var3 && var9.xCoord < var4 && var9.yCoord < var5 && var9.zCoord < var6) {
				var7.add(var9);
			}
		}

		return var7;
	}

	public boolean canMineBlock(EntityPlayer var1, int var2, int var3, int var4) {
		return !this.mcServer.func_96290_a(this, var2, var3, var4, var1);
	}

	protected void initialize(WorldSettings var1) {
		if(this.entityIdMap == null) {
			this.entityIdMap = new IntHashMap();
		}

		if(this.field_73064_N == null) {
			this.field_73064_N = new HashSet();
		}

		if(this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}

		this.createSpawnPosition(var1);
		super.initialize(var1);
	}

	protected void createSpawnPosition(WorldSettings var1) {
		if(!this.provider.canRespawnHere()) {
			this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
		} else {
			this.findingSpawnPoint = true;
			WorldChunkManager var2 = this.provider.worldChunkMgr;
			List var3 = var2.getBiomesToSpawnIn();
			Random var4 = new Random(this.getSeed());
			ChunkPosition var5 = var2.findBiomePosition(0, 0, 256, var3, var4);
			int var6 = 0;
			int var7 = this.provider.getAverageGroundLevel();
			int var8 = 0;
			if(var5 != null) {
				var6 = var5.x;
				var8 = var5.z;
			} else {
				this.getWorldLogAgent().logWarning("Unable to find spawn biome");
			}

			int var9 = 0;

			while(!this.provider.canCoordinateBeSpawn(var6, var8)) {
				var6 += var4.nextInt(64) - var4.nextInt(64);
				var8 += var4.nextInt(64) - var4.nextInt(64);
				++var9;
				if(var9 == 1000) {
					break;
				}
			}

			this.worldInfo.setSpawnPosition(var6, var7, var8);
			this.findingSpawnPoint = false;
			if(var1.isBonusChestEnabled()) {
				this.createBonusChest();
			}

		}
	}

	protected void createBonusChest() {
		WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(bonusChestContent, 10);

		for(int var2 = 0; var2 < 10; ++var2) {
			int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
			int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
			int var5 = this.getTopSolidOrLiquidBlock(var3, var4) + 1;
			if(var1.generate(this, this.rand, var3, var5, var4)) {
				break;
			}
		}

	}

	public ChunkCoordinates getEntrancePortalLocation() {
		return this.provider.getEntrancePortalLocation();
	}

	public void saveAllChunks(boolean var1, IProgressUpdate var2) throws MinecraftException {
		if(this.chunkProvider.canSave()) {
			if(var2 != null) {
				var2.displayProgressMessage("Saving level");
			}

			this.saveLevel();
			if(var2 != null) {
				var2.resetProgresAndWorkingMessage("Saving chunks");
			}

			this.chunkProvider.saveChunks(var1, var2);
		}
	}

	public void func_104140_m() {
		if(this.chunkProvider.canSave()) {
			this.chunkProvider.func_104112_b();
		}
	}

	protected void saveLevel() throws MinecraftException {
		this.checkSessionLock();
		this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
		this.mapStorage.saveAllData();
	}

	protected void obtainEntitySkin(Entity var1) {
		super.obtainEntitySkin(var1);
		this.entityIdMap.addKey(var1.entityId, var1);
		Entity[] var2 = var1.getParts();
		if(var2 != null) {
			for(int var3 = 0; var3 < var2.length; ++var3) {
				this.entityIdMap.addKey(var2[var3].entityId, var2[var3]);
			}
		}

	}

	protected void releaseEntitySkin(Entity var1) {
		super.releaseEntitySkin(var1);
		this.entityIdMap.removeObject(var1.entityId);
		Entity[] var2 = var1.getParts();
		if(var2 != null) {
			for(int var3 = 0; var3 < var2.length; ++var3) {
				this.entityIdMap.removeObject(var2[var3].entityId);
			}
		}

	}

	public Entity getEntityByID(int var1) {
		return (Entity)this.entityIdMap.lookup(var1);
	}

	public boolean addWeatherEffect(Entity var1) {
		if(super.addWeatherEffect(var1)) {
			this.mcServer.getConfigurationManager().sendToAllNear(var1.posX, var1.posY, var1.posZ, 512.0D, this.provider.dimensionId, new Packet71Weather(var1));
			return true;
		} else {
			return false;
		}
	}

	public void setEntityState(Entity var1, byte var2) {
		Packet38EntityStatus var3 = new Packet38EntityStatus(var1.entityId, var2);
		this.getEntityTracker().sendPacketToAllAssociatedPlayers(var1, var3);
	}

	public Explosion newExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
		Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8);
		var11.isFlaming = var9;
		var11.isSmoking = var10;
		var11.doExplosionA();
		var11.doExplosionB(false);
		if(!var10) {
			var11.affectedBlockPositions.clear();
		}

		Iterator var12 = this.playerEntities.iterator();

		while(var12.hasNext()) {
			EntityPlayer var13 = (EntityPlayer)var12.next();
			if(var13.getDistanceSq(var2, var4, var6) < 4096.0D) {
				((EntityPlayerMP)var13).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(var2, var4, var6, var8, var11.affectedBlockPositions, (Vec3)var11.func_77277_b().get(var13)));
			}
		}

		return var11;
	}

	public void addBlockEvent(int var1, int var2, int var3, int var4, int var5, int var6) {
		BlockEventData var7 = new BlockEventData(var1, var2, var3, var4, var5, var6);
		Iterator var8 = this.blockEventCache[this.blockEventCacheIndex].iterator();

		BlockEventData var9;
		do {
			if(!var8.hasNext()) {
				this.blockEventCache[this.blockEventCacheIndex].add(var7);
				return;
			}

			var9 = (BlockEventData)var8.next();
		} while(!var9.equals(var7));

	}

	private void sendAndApplyBlockEvents() {
		while(!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
			int var1 = this.blockEventCacheIndex;
			this.blockEventCacheIndex ^= 1;
			Iterator var2 = this.blockEventCache[var1].iterator();

			while(var2.hasNext()) {
				BlockEventData var3 = (BlockEventData)var2.next();
				if(this.onBlockEventReceived(var3)) {
					this.mcServer.getConfigurationManager().sendToAllNear((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0D, this.provider.dimensionId, new Packet54PlayNoteBlock(var3.getX(), var3.getY(), var3.getZ(), var3.getBlockID(), var3.getEventID(), var3.getEventParameter()));
				}
			}

			this.blockEventCache[var1].clear();
		}

	}

	private boolean onBlockEventReceived(BlockEventData var1) {
		int var2 = this.getBlockId(var1.getX(), var1.getY(), var1.getZ());
		return var2 == var1.getBlockID() ? Block.blocksList[var2].onBlockEventReceived(this, var1.getX(), var1.getY(), var1.getZ(), var1.getEventID(), var1.getEventParameter()) : false;
	}

	public void flush() {
		this.saveHandler.flush();
	}

	protected void updateWeather() {
		boolean var1 = this.isRaining();
		super.updateWeather();
		if(var1 != this.isRaining()) {
			if(var1) {
				this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(2, 0));
			} else {
				this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(1, 0));
			}
		}

	}

	public MinecraftServer getMinecraftServer() {
		return this.mcServer;
	}

	public EntityTracker getEntityTracker() {
		return this.theEntityTracker;
	}

	public PlayerManager getPlayerManager() {
		return this.thePlayerManager;
	}

	public Teleporter getDefaultTeleporter() {
		return this.field_85177_Q;
	}
}
