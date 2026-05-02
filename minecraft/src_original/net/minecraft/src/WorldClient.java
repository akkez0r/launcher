package net.minecraft.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class WorldClient extends World {
	private NetClientHandler sendQueue;
	private ChunkProviderClient clientChunkProvider;
	private IntHashMap entityHashSet = new IntHashMap();
	private Set entityList = new HashSet();
	private Set entitySpawnQueue = new HashSet();
	private final Minecraft mc = Minecraft.getMinecraft();
	private final Set previousActiveChunkSet = new HashSet();

	public WorldClient(NetClientHandler var1, WorldSettings var2, int var3, int var4, Profiler var5, ILogAgent var6) {
		super(new SaveHandlerMP(), "MpServer", (WorldProvider)WorldProvider.getProviderForDimension(var3), (WorldSettings)var2, var5, var6);
		this.sendQueue = var1;
		this.difficultySetting = var4;
		this.setSpawnLocation(8, 64, 8);
		this.mapStorage = var1.mapStorage;
	}

	public void tick() {
		super.tick();
		this.func_82738_a(this.getTotalWorldTime() + 1L);
		this.setWorldTime(this.getWorldTime() + 1L);
		this.theProfiler.startSection("reEntryProcessing");

		for(int var1 = 0; var1 < 10 && !this.entitySpawnQueue.isEmpty(); ++var1) {
			Entity var2 = (Entity)this.entitySpawnQueue.iterator().next();
			this.entitySpawnQueue.remove(var2);
			if(!this.loadedEntityList.contains(var2)) {
				this.spawnEntityInWorld(var2);
			}
		}

		this.theProfiler.endStartSection("connection");
		this.sendQueue.processReadPackets();
		this.theProfiler.endStartSection("chunkCache");
		this.clientChunkProvider.unloadQueuedChunks();
		this.theProfiler.endStartSection("tiles");
		this.tickBlocksAndAmbiance();
		this.theProfiler.endSection();
	}

	public void invalidateBlockReceiveRegion(int var1, int var2, int var3, int var4, int var5, int var6) {
	}

	protected IChunkProvider createChunkProvider() {
		this.clientChunkProvider = new ChunkProviderClient(this);
		return this.clientChunkProvider;
	}

	protected void tickBlocksAndAmbiance() {
		super.tickBlocksAndAmbiance();
		this.previousActiveChunkSet.retainAll(this.activeChunkSet);
		if(this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
			this.previousActiveChunkSet.clear();
		}

		int var1 = 0;
		Iterator var2 = this.activeChunkSet.iterator();

		while(var2.hasNext()) {
			ChunkCoordIntPair var3 = (ChunkCoordIntPair)var2.next();
			if(!this.previousActiveChunkSet.contains(var3)) {
				int var4 = var3.chunkXPos * 16;
				int var5 = var3.chunkZPos * 16;
				this.theProfiler.startSection("getChunk");
				Chunk var6 = this.getChunkFromChunkCoords(var3.chunkXPos, var3.chunkZPos);
				this.moodSoundAndLightCheck(var4, var5, var6);
				this.theProfiler.endSection();
				this.previousActiveChunkSet.add(var3);
				++var1;
				if(var1 >= 10) {
					return;
				}
			}
		}

	}

	public void doPreChunk(int var1, int var2, boolean var3) {
		if(var3) {
			this.clientChunkProvider.loadChunk(var1, var2);
		} else {
			this.clientChunkProvider.unloadChunk(var1, var2);
		}

		if(!var3) {
			this.markBlockRangeForRenderUpdate(var1 * 16, 0, var2 * 16, var1 * 16 + 15, 256, var2 * 16 + 15);
		}

	}

	public boolean spawnEntityInWorld(Entity var1) {
		boolean var2 = super.spawnEntityInWorld(var1);
		this.entityList.add(var1);
		if(!var2) {
			this.entitySpawnQueue.add(var1);
		}

		return var2;
	}

	public void removeEntity(Entity var1) {
		super.removeEntity(var1);
		this.entityList.remove(var1);
	}

	protected void obtainEntitySkin(Entity var1) {
		super.obtainEntitySkin(var1);
		if(this.entitySpawnQueue.contains(var1)) {
			this.entitySpawnQueue.remove(var1);
		}

	}

	protected void releaseEntitySkin(Entity var1) {
		super.releaseEntitySkin(var1);
		if(this.entityList.contains(var1)) {
			if(var1.isEntityAlive()) {
				this.entitySpawnQueue.add(var1);
			} else {
				this.entityList.remove(var1);
			}
		}

	}

	public void addEntityToWorld(int var1, Entity var2) {
		Entity var3 = this.getEntityByID(var1);
		if(var3 != null) {
			this.removeEntity(var3);
		}

		this.entityList.add(var2);
		var2.entityId = var1;
		if(!this.spawnEntityInWorld(var2)) {
			this.entitySpawnQueue.add(var2);
		}

		this.entityHashSet.addKey(var1, var2);
	}

	public Entity getEntityByID(int var1) {
		return (Entity)(var1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : (Entity)this.entityHashSet.lookup(var1));
	}

	public Entity removeEntityFromWorld(int var1) {
		Entity var2 = (Entity)this.entityHashSet.removeObject(var1);
		if(var2 != null) {
			this.entityList.remove(var2);
			this.removeEntity(var2);
		}

		return var2;
	}

	public boolean setBlockAndMetadataAndInvalidate(int var1, int var2, int var3, int var4, int var5) {
		this.invalidateBlockReceiveRegion(var1, var2, var3, var1, var2, var3);
		return super.setBlock(var1, var2, var3, var4, var5, 3);
	}

	public void sendQuittingDisconnectingPacket() {
		this.sendQueue.quitWithPacket(new Packet255KickDisconnect("Quitting"));
	}

	public IUpdatePlayerListBox func_82735_a(EntityMinecart var1) {
		return new SoundUpdaterMinecart(this.mc.sndManager, var1, this.mc.thePlayer);
	}

	protected void updateWeather() {
		if(!this.provider.hasNoSky) {
			this.prevRainingStrength = this.rainingStrength;
			if(this.worldInfo.isRaining()) {
				this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
			} else {
				this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
			}

			if(this.rainingStrength < 0.0F) {
				this.rainingStrength = 0.0F;
			}

			if(this.rainingStrength > 1.0F) {
				this.rainingStrength = 1.0F;
			}

			this.prevThunderingStrength = this.thunderingStrength;
			if(this.worldInfo.isThundering()) {
				this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
			} else {
				this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
			}

			if(this.thunderingStrength < 0.0F) {
				this.thunderingStrength = 0.0F;
			}

			if(this.thunderingStrength > 1.0F) {
				this.thunderingStrength = 1.0F;
			}

		}
	}

	public void doVoidFogParticles(int var1, int var2, int var3) {
		byte var4 = 16;
		Random var5 = new Random();

		for(int var6 = 0; var6 < 1000; ++var6) {
			int var7 = var1 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
			int var8 = var2 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
			int var9 = var3 + this.rand.nextInt(var4) - this.rand.nextInt(var4);
			int var10 = this.getBlockId(var7, var8, var9);
			if(var10 == 0 && this.rand.nextInt(8) > var8 && this.provider.getWorldHasVoidParticles()) {
				this.spawnParticle("depthsuspend", (double)((float)var7 + this.rand.nextFloat()), (double)((float)var8 + this.rand.nextFloat()), (double)((float)var9 + this.rand.nextFloat()), 0.0D, 0.0D, 0.0D);
			} else if(var10 > 0) {
				Block.blocksList[var10].randomDisplayTick(this, var7, var8, var9, var5);
			}
		}

	}

	public void removeAllEntities() {
		this.loadedEntityList.removeAll(this.unloadedEntityList);

		int var1;
		Entity var2;
		int var3;
		int var4;
		for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
			var2 = (Entity)this.unloadedEntityList.get(var1);
			var3 = var2.chunkCoordX;
			var4 = var2.chunkCoordZ;
			if(var2.addedToChunk && this.chunkExists(var3, var4)) {
				this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
			}
		}

		for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
			this.releaseEntitySkin((Entity)this.unloadedEntityList.get(var1));
		}

		this.unloadedEntityList.clear();

		for(var1 = 0; var1 < this.loadedEntityList.size(); ++var1) {
			var2 = (Entity)this.loadedEntityList.get(var1);
			if(var2.ridingEntity != null) {
				if(!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2) {
					continue;
				}

				var2.ridingEntity.riddenByEntity = null;
				var2.ridingEntity = null;
			}

			if(var2.isDead) {
				var3 = var2.chunkCoordX;
				var4 = var2.chunkCoordZ;
				if(var2.addedToChunk && this.chunkExists(var3, var4)) {
					this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
				}

				this.loadedEntityList.remove(var1--);
				this.releaseEntitySkin(var2);
			}
		}

	}

	public CrashReportCategory addWorldInfoToCrashReport(CrashReport var1) {
		CrashReportCategory var2 = super.addWorldInfoToCrashReport(var1);
		var2.addCrashSectionCallable("Forced entities", new CallableMPL1(this));
		var2.addCrashSectionCallable("Retry entities", new CallableMPL2(this));
		return var2;
	}

	public void playSound(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
		float var11 = 16.0F;
		if(var8 > 1.0F) {
			var11 *= var8;
		}

		double var12 = this.mc.renderViewEntity.getDistanceSq(var1, var3, var5);
		if(var12 < (double)(var11 * var11)) {
			if(var10 && var12 > 100.0D) {
				double var14 = Math.sqrt(var12) / 40.0D;
				this.mc.sndManager.func_92070_a(var7, (float)var1, (float)var3, (float)var5, var8, var9, (int)Math.round(var14 * 20.0D));
			} else {
				this.mc.sndManager.playSound(var7, (float)var1, (float)var3, (float)var5, var8, var9);
			}
		}

	}

	public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
		this.mc.effectRenderer.addEffect(new EntityFireworkStarterFX(this, var1, var3, var5, var7, var9, var11, this.mc.effectRenderer, var13));
	}

	public void func_96443_a(Scoreboard var1) {
		this.worldScoreboard = var1;
	}

	static Set getEntityList(WorldClient var0) {
		return var0.entityList;
	}

	static Set getEntitySpawnQueue(WorldClient var0) {
		return var0.entitySpawnQueue;
	}
}
