package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChunkProviderServer implements IChunkProvider {
	private Set chunksToUnload = new HashSet();
	private Chunk defaultEmptyChunk;
	private IChunkProvider currentChunkProvider;
	private IChunkLoader currentChunkLoader;
	public boolean loadChunkOnProvideRequest = true;
	private LongHashMap loadedChunkHashMap = new LongHashMap();
	private List loadedChunks = new ArrayList();
	private WorldServer worldObj;

	public ChunkProviderServer(WorldServer var1, IChunkLoader var2, IChunkProvider var3) {
		this.defaultEmptyChunk = new EmptyChunk(var1, 0, 0);
		this.worldObj = var1;
		this.currentChunkLoader = var2;
		this.currentChunkProvider = var3;
	}

	public boolean chunkExists(int var1, int var2) {
		return this.loadedChunkHashMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
	}

	public void unloadChunksIfNotNearSpawn(int var1, int var2) {
		if(this.worldObj.provider.canRespawnHere()) {
			ChunkCoordinates var3 = this.worldObj.getSpawnPoint();
			int var4 = var1 * 16 + 8 - var3.posX;
			int var5 = var2 * 16 + 8 - var3.posZ;
			short var6 = 128;
			if(var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6) {
				this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(var1, var2)));
			}
		} else {
			this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(var1, var2)));
		}

	}

	public void unloadAllChunks() {
		Iterator var1 = this.loadedChunks.iterator();

		while(var1.hasNext()) {
			Chunk var2 = (Chunk)var1.next();
			this.unloadChunksIfNotNearSpawn(var2.xPosition, var2.zPosition);
		}

	}

	public Chunk loadChunk(int var1, int var2) {
		long var3 = ChunkCoordIntPair.chunkXZ2Int(var1, var2);
		this.chunksToUnload.remove(Long.valueOf(var3));
		Chunk var5 = (Chunk)this.loadedChunkHashMap.getValueByKey(var3);
		if(var5 == null) {
			var5 = this.safeLoadChunk(var1, var2);
			if(var5 == null) {
				if(this.currentChunkProvider == null) {
					var5 = this.defaultEmptyChunk;
				} else {
					try {
						var5 = this.currentChunkProvider.provideChunk(var1, var2);
					} catch (Throwable var9) {
						CrashReport var7 = CrashReport.makeCrashReport(var9, "Exception generating new chunk");
						CrashReportCategory var8 = var7.makeCategory("Chunk to be generated");
						var8.addCrashSection("Location", String.format("%d,%d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2)}));
						var8.addCrashSection("Position hash", Long.valueOf(var3));
						var8.addCrashSection("Generator", this.currentChunkProvider.makeString());
						throw new ReportedException(var7);
					}
				}
			}

			this.loadedChunkHashMap.add(var3, var5);
			this.loadedChunks.add(var5);
			if(var5 != null) {
				var5.onChunkLoad();
			}

			var5.populateChunk(this, this, var1, var2);
		}

		return var5;
	}

	public Chunk provideChunk(int var1, int var2) {
		Chunk var3 = (Chunk)this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
		return var3 == null ? (!this.worldObj.findingSpawnPoint && !this.loadChunkOnProvideRequest ? this.defaultEmptyChunk : this.loadChunk(var1, var2)) : var3;
	}

	private Chunk safeLoadChunk(int var1, int var2) {
		if(this.currentChunkLoader == null) {
			return null;
		} else {
			try {
				Chunk var3 = this.currentChunkLoader.loadChunk(this.worldObj, var1, var2);
				if(var3 != null) {
					var3.lastSaveTime = this.worldObj.getTotalWorldTime();
					if(this.currentChunkProvider != null) {
						this.currentChunkProvider.recreateStructures(var1, var2);
					}
				}

				return var3;
			} catch (Exception var4) {
				var4.printStackTrace();
				return null;
			}
		}
	}

	private void safeSaveExtraChunkData(Chunk var1) {
		if(this.currentChunkLoader != null) {
			try {
				this.currentChunkLoader.saveExtraChunkData(this.worldObj, var1);
			} catch (Exception var3) {
				var3.printStackTrace();
			}

		}
	}

	private void safeSaveChunk(Chunk var1) {
		if(this.currentChunkLoader != null) {
			try {
				var1.lastSaveTime = this.worldObj.getTotalWorldTime();
				this.currentChunkLoader.saveChunk(this.worldObj, var1);
			} catch (IOException var3) {
				var3.printStackTrace();
			} catch (MinecraftException var4) {
				var4.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider var1, int var2, int var3) {
		Chunk var4 = this.provideChunk(var2, var3);
		if(!var4.isTerrainPopulated) {
			var4.isTerrainPopulated = true;
			if(this.currentChunkProvider != null) {
				this.currentChunkProvider.populate(var1, var2, var3);
				var4.setChunkModified();
			}
		}

	}

	public boolean saveChunks(boolean var1, IProgressUpdate var2) {
		int var3 = 0;

		for(int var4 = 0; var4 < this.loadedChunks.size(); ++var4) {
			Chunk var5 = (Chunk)this.loadedChunks.get(var4);
			if(var1) {
				this.safeSaveExtraChunkData(var5);
			}

			if(var5.needsSaving(var1)) {
				this.safeSaveChunk(var5);
				var5.isModified = false;
				++var3;
				if(var3 == 24 && !var1) {
					return false;
				}
			}
		}

		return true;
	}

	public void func_104112_b() {
		if(this.currentChunkLoader != null) {
			this.currentChunkLoader.saveExtraData();
		}

	}

	public boolean unloadQueuedChunks() {
		if(!this.worldObj.canNotSave) {
			for(int var1 = 0; var1 < 100; ++var1) {
				if(!this.chunksToUnload.isEmpty()) {
					Long var2 = (Long)this.chunksToUnload.iterator().next();
					Chunk var3 = (Chunk)this.loadedChunkHashMap.getValueByKey(var2.longValue());
					var3.onChunkUnload();
					this.safeSaveChunk(var3);
					this.safeSaveExtraChunkData(var3);
					this.chunksToUnload.remove(var2);
					this.loadedChunkHashMap.remove(var2.longValue());
					this.loadedChunks.remove(var3);
				}
			}

			if(this.currentChunkLoader != null) {
				this.currentChunkLoader.chunkTick();
			}
		}

		return this.currentChunkProvider.unloadQueuedChunks();
	}

	public boolean canSave() {
		return !this.worldObj.canNotSave;
	}

	public String makeString() {
		return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements() + " Drop: " + this.chunksToUnload.size();
	}

	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4) {
		return this.currentChunkProvider.getPossibleCreatures(var1, var2, var3, var4);
	}

	public ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5) {
		return this.currentChunkProvider.findClosestStructure(var1, var2, var3, var4, var5);
	}

	public int getLoadedChunkCount() {
		return this.loadedChunkHashMap.getNumHashElements();
	}

	public void recreateStructures(int var1, int var2) {
	}
}
