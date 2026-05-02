package net.minecraft.src;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class World implements IBlockAccess {
	public boolean scheduledUpdatesAreImmediate = false;
	public List loadedEntityList = new ArrayList();
	protected List unloadedEntityList = new ArrayList();
	public List loadedTileEntityList = new ArrayList();
	private List addedTileEntityList = new ArrayList();
	private List entityRemoval = new ArrayList();
	public List playerEntities = new ArrayList();
	public List weatherEffects = new ArrayList();
	private long cloudColour = 16777215L;
	public int skylightSubtracted = 0;
	protected int updateLCG = (new Random()).nextInt();
	protected final int DIST_HASH_MAGIC = 1013904223;
	protected float prevRainingStrength;
	protected float rainingStrength;
	protected float prevThunderingStrength;
	protected float thunderingStrength;
	public int lastLightningBolt = 0;
	public int difficultySetting;
	public Random rand = new Random();
	public final WorldProvider provider;
	protected List worldAccesses = new ArrayList();
	protected IChunkProvider chunkProvider;
	protected final ISaveHandler saveHandler;
	protected WorldInfo worldInfo;
	public boolean findingSpawnPoint;
	public MapStorage mapStorage;
	public final VillageCollection villageCollectionObj;
	protected final VillageSiege villageSiegeObj = new VillageSiege(this);
	public final Profiler theProfiler;
	private final Vec3Pool vecPool = new Vec3Pool(300, 2000);
	private final Calendar theCalendar = Calendar.getInstance();
	protected Scoreboard worldScoreboard = new Scoreboard();
	private final ILogAgent worldLogAgent;
	private ArrayList collidingBoundingBoxes = new ArrayList();
	private boolean scanningTileEntities;
	protected boolean spawnHostileMobs = true;
	protected boolean spawnPeacefulMobs = true;
	protected Set activeChunkSet = new HashSet();
	private int ambientTickCountdown = this.rand.nextInt(12000);
	int[] lightUpdateBlockList = new int[-Short.MIN_VALUE];
	public boolean isRemote = false;

	public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {
		if(this.blockExists(var1, 0, var2)) {
			Chunk var3 = this.getChunkFromBlockCoords(var1, var2);
			if(var3 != null) {
				return var3.getBiomeGenForWorldCoords(var1 & 15, var2 & 15, this.provider.worldChunkMgr);
			}
		}

		return this.provider.worldChunkMgr.getBiomeGenAt(var1, var2);
	}

	public WorldChunkManager getWorldChunkManager() {
		return this.provider.worldChunkMgr;
	}

	public World(ISaveHandler var1, String var2, WorldProvider var3, WorldSettings var4, Profiler var5, ILogAgent var6) {
		this.saveHandler = var1;
		this.theProfiler = var5;
		this.worldInfo = new WorldInfo(var4, var2);
		this.provider = var3;
		this.mapStorage = new MapStorage(var1);
		this.worldLogAgent = var6;
		VillageCollection var7 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
		if(var7 == null) {
			this.villageCollectionObj = new VillageCollection(this);
			this.mapStorage.setData("villages", this.villageCollectionObj);
		} else {
			this.villageCollectionObj = var7;
			this.villageCollectionObj.func_82566_a(this);
		}

		var3.registerWorld(this);
		this.chunkProvider = this.createChunkProvider();
		this.calculateInitialSkylight();
		this.calculateInitialWeather();
	}

	public World(ISaveHandler var1, String var2, WorldSettings var3, WorldProvider var4, Profiler var5, ILogAgent var6) {
		this.saveHandler = var1;
		this.theProfiler = var5;
		this.mapStorage = new MapStorage(var1);
		this.worldLogAgent = var6;
		this.worldInfo = var1.loadWorldInfo();
		if(var4 != null) {
			this.provider = var4;
		} else if(this.worldInfo != null && this.worldInfo.getDimension() != 0) {
			this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension());
		} else {
			this.provider = WorldProvider.getProviderForDimension(0);
		}

		if(this.worldInfo == null) {
			this.worldInfo = new WorldInfo(var3, var2);
		} else {
			this.worldInfo.setWorldName(var2);
		}

		this.provider.registerWorld(this);
		this.chunkProvider = this.createChunkProvider();
		if(!this.worldInfo.isInitialized()) {
			try {
				this.initialize(var3);
			} catch (Throwable var11) {
				CrashReport var8 = CrashReport.makeCrashReport(var11, "Exception initializing level");

				try {
					this.addWorldInfoToCrashReport(var8);
				} catch (Throwable var10) {
				}

				throw new ReportedException(var8);
			}

			this.worldInfo.setServerInitialized(true);
		}

		VillageCollection var7 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
		if(var7 == null) {
			this.villageCollectionObj = new VillageCollection(this);
			this.mapStorage.setData("villages", this.villageCollectionObj);
		} else {
			this.villageCollectionObj = var7;
			this.villageCollectionObj.func_82566_a(this);
		}

		this.calculateInitialSkylight();
		this.calculateInitialWeather();
	}

	protected abstract IChunkProvider createChunkProvider();

	protected void initialize(WorldSettings var1) {
		this.worldInfo.setServerInitialized(true);
	}

	public void setSpawnLocation() {
		this.setSpawnLocation(8, 64, 8);
	}

	public int getFirstUncoveredBlock(int var1, int var2) {
		int var3;
		for(var3 = 63; !this.isAirBlock(var1, var3 + 1, var2); ++var3) {
		}

		return this.getBlockId(var1, var3, var2);
	}

	public int getBlockId(int var1, int var2, int var3) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			if(var2 < 0) {
				return 0;
			} else if(var2 >= 256) {
				return 0;
			} else {
				Chunk var4 = null;

				try {
					var4 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
					return var4.getBlockID(var1 & 15, var2, var3 & 15);
				} catch (Throwable var8) {
					CrashReport var6 = CrashReport.makeCrashReport(var8, "Exception getting block type in world");
					CrashReportCategory var7 = var6.makeCategory("Requested block coordinates");
					var7.addCrashSection("Found chunk", Boolean.valueOf(var4 == null));
					var7.addCrashSection("Location", CrashReportCategory.getLocationInfo(var1, var2, var3));
					throw new ReportedException(var6);
				}
			}
		} else {
			return 0;
		}
	}

	public boolean isAirBlock(int var1, int var2, int var3) {
		return this.getBlockId(var1, var2, var3) == 0;
	}

	public boolean blockHasTileEntity(int var1, int var2, int var3) {
		int var4 = this.getBlockId(var1, var2, var3);
		return Block.blocksList[var4] != null && Block.blocksList[var4].hasTileEntity();
	}

	public int blockGetRenderType(int var1, int var2, int var3) {
		int var4 = this.getBlockId(var1, var2, var3);
		return Block.blocksList[var4] != null ? Block.blocksList[var4].getRenderType() : -1;
	}

	public boolean blockExists(int var1, int var2, int var3) {
		return var2 >= 0 && var2 < 256 ? this.chunkExists(var1 >> 4, var3 >> 4) : false;
	}

	public boolean doChunksNearChunkExist(int var1, int var2, int var3, int var4) {
		return this.checkChunksExist(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4);
	}

	public boolean checkChunksExist(int var1, int var2, int var3, int var4, int var5, int var6) {
		if(var5 >= 0 && var2 < 256) {
			var1 >>= 4;
			var3 >>= 4;
			var4 >>= 4;
			var6 >>= 4;

			for(int var7 = var1; var7 <= var4; ++var7) {
				for(int var8 = var3; var8 <= var6; ++var8) {
					if(!this.chunkExists(var7, var8)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean chunkExists(int var1, int var2) {
		return this.chunkProvider.chunkExists(var1, var2);
	}

	public Chunk getChunkFromBlockCoords(int var1, int var2) {
		return this.getChunkFromChunkCoords(var1 >> 4, var2 >> 4);
	}

	public Chunk getChunkFromChunkCoords(int var1, int var2) {
		return this.chunkProvider.provideChunk(var1, var2);
	}

	public boolean setBlock(int var1, int var2, int var3, int var4, int var5, int var6) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			if(var2 < 0) {
				return false;
			} else if(var2 >= 256) {
				return false;
			} else {
				Chunk var7 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				int var8 = 0;
				if((var6 & 1) != 0) {
					var8 = var7.getBlockID(var1 & 15, var2, var3 & 15);
				}

				boolean var9 = var7.setBlockIDWithMetadata(var1 & 15, var2, var3 & 15, var4, var5);
				this.theProfiler.startSection("checkLight");
				this.updateAllLightTypes(var1, var2, var3);
				this.theProfiler.endSection();
				if(var9) {
					if((var6 & 2) != 0 && (!this.isRemote || (var6 & 4) == 0)) {
						this.markBlockForUpdate(var1, var2, var3);
					}

					if(!this.isRemote && (var6 & 1) != 0) {
						this.notifyBlockChange(var1, var2, var3, var8);
						Block var10 = Block.blocksList[var4];
						if(var10 != null && var10.hasComparatorInputOverride()) {
							this.func_96440_m(var1, var2, var3, var4);
						}
					}
				}

				return var9;
			}
		} else {
			return false;
		}
	}

	public Material getBlockMaterial(int var1, int var2, int var3) {
		int var4 = this.getBlockId(var1, var2, var3);
		return var4 == 0 ? Material.air : Block.blocksList[var4].blockMaterial;
	}

	public int getBlockMetadata(int var1, int var2, int var3) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			if(var2 < 0) {
				return 0;
			} else if(var2 >= 256) {
				return 0;
			} else {
				Chunk var4 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				var1 &= 15;
				var3 &= 15;
				return var4.getBlockMetadata(var1, var2, var3);
			}
		} else {
			return 0;
		}
	}

	public boolean setBlockMetadataWithNotify(int var1, int var2, int var3, int var4, int var5) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			if(var2 < 0) {
				return false;
			} else if(var2 >= 256) {
				return false;
			} else {
				Chunk var6 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				int var7 = var1 & 15;
				int var8 = var3 & 15;
				boolean var9 = var6.setBlockMetadata(var7, var2, var8, var4);
				if(var9) {
					int var10 = var6.getBlockID(var7, var2, var8);
					if((var5 & 2) != 0 && (!this.isRemote || (var5 & 4) == 0)) {
						this.markBlockForUpdate(var1, var2, var3);
					}

					if(!this.isRemote && (var5 & 1) != 0) {
						this.notifyBlockChange(var1, var2, var3, var10);
						Block var11 = Block.blocksList[var10];
						if(var11 != null && var11.hasComparatorInputOverride()) {
							this.func_96440_m(var1, var2, var3, var10);
						}
					}
				}

				return var9;
			}
		} else {
			return false;
		}
	}

	public boolean setBlockToAir(int var1, int var2, int var3) {
		return this.setBlock(var1, var2, var3, 0, 0, 3);
	}

	public boolean destroyBlock(int var1, int var2, int var3, boolean var4) {
		int var5 = this.getBlockId(var1, var2, var3);
		if(var5 > 0) {
			int var6 = this.getBlockMetadata(var1, var2, var3);
			this.playAuxSFX(2001, var1, var2, var3, var5 + (var6 << 12));
			if(var4) {
				Block.blocksList[var5].dropBlockAsItem(this, var1, var2, var3, var6, 0);
			}

			return this.setBlock(var1, var2, var3, 0, 0, 3);
		} else {
			return false;
		}
	}

	public boolean setBlock(int var1, int var2, int var3, int var4) {
		return this.setBlock(var1, var2, var3, var4, 0, 3);
	}

	public void markBlockForUpdate(int var1, int var2, int var3) {
		for(int var4 = 0; var4 < this.worldAccesses.size(); ++var4) {
			((IWorldAccess)this.worldAccesses.get(var4)).markBlockForUpdate(var1, var2, var3);
		}

	}

	public void notifyBlockChange(int var1, int var2, int var3, int var4) {
		this.notifyBlocksOfNeighborChange(var1, var2, var3, var4);
	}

	public void markBlocksDirtyVertical(int var1, int var2, int var3, int var4) {
		int var5;
		if(var3 > var4) {
			var5 = var4;
			var4 = var3;
			var3 = var5;
		}

		if(!this.provider.hasNoSky) {
			for(var5 = var3; var5 <= var4; ++var5) {
				this.updateLightByType(EnumSkyBlock.Sky, var1, var5, var2);
			}
		}

		this.markBlockRangeForRenderUpdate(var1, var3, var2, var1, var4, var2);
	}

	public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
		for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
			((IWorldAccess)this.worldAccesses.get(var7)).markBlockRangeForRenderUpdate(var1, var2, var3, var4, var5, var6);
		}

	}

	public void notifyBlocksOfNeighborChange(int var1, int var2, int var3, int var4) {
		this.notifyBlockOfNeighborChange(var1 - 1, var2, var3, var4);
		this.notifyBlockOfNeighborChange(var1 + 1, var2, var3, var4);
		this.notifyBlockOfNeighborChange(var1, var2 - 1, var3, var4);
		this.notifyBlockOfNeighborChange(var1, var2 + 1, var3, var4);
		this.notifyBlockOfNeighborChange(var1, var2, var3 - 1, var4);
		this.notifyBlockOfNeighborChange(var1, var2, var3 + 1, var4);
	}

	public void notifyBlocksOfNeighborChange(int var1, int var2, int var3, int var4, int var5) {
		if(var5 != 4) {
			this.notifyBlockOfNeighborChange(var1 - 1, var2, var3, var4);
		}

		if(var5 != 5) {
			this.notifyBlockOfNeighborChange(var1 + 1, var2, var3, var4);
		}

		if(var5 != 0) {
			this.notifyBlockOfNeighborChange(var1, var2 - 1, var3, var4);
		}

		if(var5 != 1) {
			this.notifyBlockOfNeighborChange(var1, var2 + 1, var3, var4);
		}

		if(var5 != 2) {
			this.notifyBlockOfNeighborChange(var1, var2, var3 - 1, var4);
		}

		if(var5 != 3) {
			this.notifyBlockOfNeighborChange(var1, var2, var3 + 1, var4);
		}

	}

	public void notifyBlockOfNeighborChange(int var1, int var2, int var3, int var4) {
		if(!this.isRemote) {
			int var5 = this.getBlockId(var1, var2, var3);
			Block var6 = Block.blocksList[var5];
			if(var6 != null) {
				try {
					var6.onNeighborBlockChange(this, var1, var2, var3, var4);
				} catch (Throwable var13) {
					CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while updating neighbours");
					CrashReportCategory var9 = var8.makeCategory("Block being updated");

					int var10;
					try {
						var10 = this.getBlockMetadata(var1, var2, var3);
					} catch (Throwable var12) {
						var10 = -1;
					}

					var9.addCrashSectionCallable("Source block type", new CallableLvl1(this, var4));
					CrashReportCategory.func_85068_a(var9, var1, var2, var3, var5, var10);
					throw new ReportedException(var8);
				}
			}

		}
	}

	public boolean isBlockTickScheduled(int var1, int var2, int var3, int var4) {
		return false;
	}

	public boolean canBlockSeeTheSky(int var1, int var2, int var3) {
		return this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4).canBlockSeeTheSky(var1 & 15, var2, var3 & 15);
	}

	public int getFullBlockLightValue(int var1, int var2, int var3) {
		if(var2 < 0) {
			return 0;
		} else {
			if(var2 >= 256) {
				var2 = 255;
			}

			return this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4).getBlockLightValue(var1 & 15, var2, var3 & 15, 0);
		}
	}

	public int getBlockLightValue(int var1, int var2, int var3) {
		return this.getBlockLightValue_do(var1, var2, var3, true);
	}

	public int getBlockLightValue_do(int var1, int var2, int var3, boolean var4) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			if(var4) {
				int var5 = this.getBlockId(var1, var2, var3);
				if(Block.useNeighborBrightness[var5]) {
					int var6 = this.getBlockLightValue_do(var1, var2 + 1, var3, false);
					int var7 = this.getBlockLightValue_do(var1 + 1, var2, var3, false);
					int var8 = this.getBlockLightValue_do(var1 - 1, var2, var3, false);
					int var9 = this.getBlockLightValue_do(var1, var2, var3 + 1, false);
					int var10 = this.getBlockLightValue_do(var1, var2, var3 - 1, false);
					if(var7 > var6) {
						var6 = var7;
					}

					if(var8 > var6) {
						var6 = var8;
					}

					if(var9 > var6) {
						var6 = var9;
					}

					if(var10 > var6) {
						var6 = var10;
					}

					return var6;
				}
			}

			if(var2 < 0) {
				return 0;
			} else {
				if(var2 >= 256) {
					var2 = 255;
				}

				Chunk var11 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				var1 &= 15;
				var3 &= 15;
				return var11.getBlockLightValue(var1, var2, var3, this.skylightSubtracted);
			}
		} else {
			return 15;
		}
	}

	public int getHeightValue(int var1, int var2) {
		if(var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
			if(!this.chunkExists(var1 >> 4, var2 >> 4)) {
				return 0;
			} else {
				Chunk var3 = this.getChunkFromChunkCoords(var1 >> 4, var2 >> 4);
				return var3.getHeightValue(var1 & 15, var2 & 15);
			}
		} else {
			return 0;
		}
	}

	public int getChunkHeightMapMinimum(int var1, int var2) {
		if(var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
			if(!this.chunkExists(var1 >> 4, var2 >> 4)) {
				return 0;
			} else {
				Chunk var3 = this.getChunkFromChunkCoords(var1 >> 4, var2 >> 4);
				return var3.heightMapMinimum;
			}
		} else {
			return 0;
		}
	}

	public int getSkyBlockTypeBrightness(EnumSkyBlock var1, int var2, int var3, int var4) {
		if(this.provider.hasNoSky && var1 == EnumSkyBlock.Sky) {
			return 0;
		} else {
			if(var3 < 0) {
				var3 = 0;
			}

			if(var3 >= 256) {
				return var1.defaultLightValue;
			} else if(var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
				int var5 = var2 >> 4;
				int var6 = var4 >> 4;
				if(!this.chunkExists(var5, var6)) {
					return var1.defaultLightValue;
				} else if(Block.useNeighborBrightness[this.getBlockId(var2, var3, var4)]) {
					int var12 = this.getSavedLightValue(var1, var2, var3 + 1, var4);
					int var8 = this.getSavedLightValue(var1, var2 + 1, var3, var4);
					int var9 = this.getSavedLightValue(var1, var2 - 1, var3, var4);
					int var10 = this.getSavedLightValue(var1, var2, var3, var4 + 1);
					int var11 = this.getSavedLightValue(var1, var2, var3, var4 - 1);
					if(var8 > var12) {
						var12 = var8;
					}

					if(var9 > var12) {
						var12 = var9;
					}

					if(var10 > var12) {
						var12 = var10;
					}

					if(var11 > var12) {
						var12 = var11;
					}

					return var12;
				} else {
					Chunk var7 = this.getChunkFromChunkCoords(var5, var6);
					return var7.getSavedLightValue(var1, var2 & 15, var3, var4 & 15);
				}
			} else {
				return var1.defaultLightValue;
			}
		}
	}

	public int getSavedLightValue(EnumSkyBlock var1, int var2, int var3, int var4) {
		if(var3 < 0) {
			var3 = 0;
		}

		if(var3 >= 256) {
			var3 = 255;
		}

		if(var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
			int var5 = var2 >> 4;
			int var6 = var4 >> 4;
			if(!this.chunkExists(var5, var6)) {
				return var1.defaultLightValue;
			} else {
				Chunk var7 = this.getChunkFromChunkCoords(var5, var6);
				return var7.getSavedLightValue(var1, var2 & 15, var3, var4 & 15);
			}
		} else {
			return var1.defaultLightValue;
		}
	}

	public void setLightValue(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
		if(var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
			if(var3 >= 0) {
				if(var3 < 256) {
					if(this.chunkExists(var2 >> 4, var4 >> 4)) {
						Chunk var6 = this.getChunkFromChunkCoords(var2 >> 4, var4 >> 4);
						var6.setLightValue(var1, var2 & 15, var3, var4 & 15, var5);

						for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
							((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(var2, var3, var4);
						}

					}
				}
			}
		}
	}

	public void markBlockForRenderUpdate(int var1, int var2, int var3) {
		for(int var4 = 0; var4 < this.worldAccesses.size(); ++var4) {
			((IWorldAccess)this.worldAccesses.get(var4)).markBlockForRenderUpdate(var1, var2, var3);
		}

	}

	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		int var5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, var1, var2, var3);
		int var6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, var1, var2, var3);
		if(var6 < var4) {
			var6 = var4;
		}

		return var5 << 20 | var6 << 4;
	}

	public float getBrightness(int var1, int var2, int var3, int var4) {
		int var5 = this.getBlockLightValue(var1, var2, var3);
		if(var5 < var4) {
			var5 = var4;
		}

		return this.provider.lightBrightnessTable[var5];
	}

	public float getLightBrightness(int var1, int var2, int var3) {
		return this.provider.lightBrightnessTable[this.getBlockLightValue(var1, var2, var3)];
	}

	public boolean isDaytime() {
		return this.skylightSubtracted < 4;
	}

	public MovingObjectPosition rayTraceBlocks(Vec3 var1, Vec3 var2) {
		return this.rayTraceBlocks_do_do(var1, var2, false, false);
	}

	public MovingObjectPosition rayTraceBlocks_do(Vec3 var1, Vec3 var2, boolean var3) {
		return this.rayTraceBlocks_do_do(var1, var2, var3, false);
	}

	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 var1, Vec3 var2, boolean var3, boolean var4) {
		if(!Double.isNaN(var1.xCoord) && !Double.isNaN(var1.yCoord) && !Double.isNaN(var1.zCoord)) {
			if(!Double.isNaN(var2.xCoord) && !Double.isNaN(var2.yCoord) && !Double.isNaN(var2.zCoord)) {
				int var5 = MathHelper.floor_double(var2.xCoord);
				int var6 = MathHelper.floor_double(var2.yCoord);
				int var7 = MathHelper.floor_double(var2.zCoord);
				int var8 = MathHelper.floor_double(var1.xCoord);
				int var9 = MathHelper.floor_double(var1.yCoord);
				int var10 = MathHelper.floor_double(var1.zCoord);
				int var11 = this.getBlockId(var8, var9, var10);
				int var12 = this.getBlockMetadata(var8, var9, var10);
				Block var13 = Block.blocksList[var11];
				if((!var4 || var13 == null || var13.getCollisionBoundingBoxFromPool(this, var8, var9, var10) != null) && var11 > 0 && var13.canCollideCheck(var12, var3)) {
					MovingObjectPosition var14 = var13.collisionRayTrace(this, var8, var9, var10, var1, var2);
					if(var14 != null) {
						return var14;
					}
				}

				var11 = 200;

				while(var11-- >= 0) {
					if(Double.isNaN(var1.xCoord) || Double.isNaN(var1.yCoord) || Double.isNaN(var1.zCoord)) {
						return null;
					}

					if(var8 == var5 && var9 == var6 && var10 == var7) {
						return null;
					}

					boolean var39 = true;
					boolean var40 = true;
					boolean var41 = true;
					double var15 = 999.0D;
					double var17 = 999.0D;
					double var19 = 999.0D;
					if(var5 > var8) {
						var15 = (double)var8 + 1.0D;
					} else if(var5 < var8) {
						var15 = (double)var8 + 0.0D;
					} else {
						var39 = false;
					}

					if(var6 > var9) {
						var17 = (double)var9 + 1.0D;
					} else if(var6 < var9) {
						var17 = (double)var9 + 0.0D;
					} else {
						var40 = false;
					}

					if(var7 > var10) {
						var19 = (double)var10 + 1.0D;
					} else if(var7 < var10) {
						var19 = (double)var10 + 0.0D;
					} else {
						var41 = false;
					}

					double var21 = 999.0D;
					double var23 = 999.0D;
					double var25 = 999.0D;
					double var27 = var2.xCoord - var1.xCoord;
					double var29 = var2.yCoord - var1.yCoord;
					double var31 = var2.zCoord - var1.zCoord;
					if(var39) {
						var21 = (var15 - var1.xCoord) / var27;
					}

					if(var40) {
						var23 = (var17 - var1.yCoord) / var29;
					}

					if(var41) {
						var25 = (var19 - var1.zCoord) / var31;
					}

					boolean var33 = false;
					byte var42;
					if(var21 < var23 && var21 < var25) {
						if(var5 > var8) {
							var42 = 4;
						} else {
							var42 = 5;
						}

						var1.xCoord = var15;
						var1.yCoord += var29 * var21;
						var1.zCoord += var31 * var21;
					} else if(var23 < var25) {
						if(var6 > var9) {
							var42 = 0;
						} else {
							var42 = 1;
						}

						var1.xCoord += var27 * var23;
						var1.yCoord = var17;
						var1.zCoord += var31 * var23;
					} else {
						if(var7 > var10) {
							var42 = 2;
						} else {
							var42 = 3;
						}

						var1.xCoord += var27 * var25;
						var1.yCoord += var29 * var25;
						var1.zCoord = var19;
					}

					Vec3 var34 = this.getWorldVec3Pool().getVecFromPool(var1.xCoord, var1.yCoord, var1.zCoord);
					var8 = (int)(var34.xCoord = (double)MathHelper.floor_double(var1.xCoord));
					if(var42 == 5) {
						--var8;
						++var34.xCoord;
					}

					var9 = (int)(var34.yCoord = (double)MathHelper.floor_double(var1.yCoord));
					if(var42 == 1) {
						--var9;
						++var34.yCoord;
					}

					var10 = (int)(var34.zCoord = (double)MathHelper.floor_double(var1.zCoord));
					if(var42 == 3) {
						--var10;
						++var34.zCoord;
					}

					int var35 = this.getBlockId(var8, var9, var10);
					int var36 = this.getBlockMetadata(var8, var9, var10);
					Block var37 = Block.blocksList[var35];
					if((!var4 || var37 == null || var37.getCollisionBoundingBoxFromPool(this, var8, var9, var10) != null) && var35 > 0 && var37.canCollideCheck(var36, var3)) {
						MovingObjectPosition var38 = var37.collisionRayTrace(this, var8, var9, var10, var1, var2);
						if(var38 != null) {
							return var38;
						}
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void playSoundAtEntity(Entity var1, String var2, float var3, float var4) {
		if(var1 != null && var2 != null) {
			for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
				((IWorldAccess)this.worldAccesses.get(var5)).playSound(var2, var1.posX, var1.posY - (double)var1.yOffset, var1.posZ, var3, var4);
			}

		}
	}

	public void playSoundToNearExcept(EntityPlayer var1, String var2, float var3, float var4) {
		if(var1 != null && var2 != null) {
			for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
				((IWorldAccess)this.worldAccesses.get(var5)).playSoundToNearExcept(var1, var2, var1.posX, var1.posY - (double)var1.yOffset, var1.posZ, var3, var4);
			}

		}
	}

	public void playSoundEffect(double var1, double var3, double var5, String var7, float var8, float var9) {
		if(var7 != null) {
			for(int var10 = 0; var10 < this.worldAccesses.size(); ++var10) {
				((IWorldAccess)this.worldAccesses.get(var10)).playSound(var7, var1, var3, var5, var8, var9);
			}

		}
	}

	public void playSound(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
	}

	public void playRecord(String var1, int var2, int var3, int var4) {
		for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
			((IWorldAccess)this.worldAccesses.get(var5)).playRecord(var1, var2, var3, var4);
		}

	}

	public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		for(int var14 = 0; var14 < this.worldAccesses.size(); ++var14) {
			((IWorldAccess)this.worldAccesses.get(var14)).spawnParticle(var1, var2, var4, var6, var8, var10, var12);
		}

	}

	public boolean addWeatherEffect(Entity var1) {
		this.weatherEffects.add(var1);
		return true;
	}

	public boolean spawnEntityInWorld(Entity var1) {
		int var2 = MathHelper.floor_double(var1.posX / 16.0D);
		int var3 = MathHelper.floor_double(var1.posZ / 16.0D);
		boolean var4 = var1.field_98038_p;
		if(var1 instanceof EntityPlayer) {
			var4 = true;
		}

		if(!var4 && !this.chunkExists(var2, var3)) {
			return false;
		} else {
			if(var1 instanceof EntityPlayer) {
				EntityPlayer var5 = (EntityPlayer)var1;
				this.playerEntities.add(var5);
				this.updateAllPlayersSleepingFlag();
			}

			this.getChunkFromChunkCoords(var2, var3).addEntity(var1);
			this.loadedEntityList.add(var1);
			this.obtainEntitySkin(var1);
			return true;
		}
	}

	protected void obtainEntitySkin(Entity var1) {
		for(int var2 = 0; var2 < this.worldAccesses.size(); ++var2) {
			((IWorldAccess)this.worldAccesses.get(var2)).onEntityCreate(var1);
		}

	}

	protected void releaseEntitySkin(Entity var1) {
		for(int var2 = 0; var2 < this.worldAccesses.size(); ++var2) {
			((IWorldAccess)this.worldAccesses.get(var2)).onEntityDestroy(var1);
		}

	}

	public void removeEntity(Entity var1) {
		if(var1.riddenByEntity != null) {
			var1.riddenByEntity.mountEntity((Entity)null);
		}

		if(var1.ridingEntity != null) {
			var1.mountEntity((Entity)null);
		}

		var1.setDead();
		if(var1 instanceof EntityPlayer) {
			this.playerEntities.remove(var1);
			this.updateAllPlayersSleepingFlag();
		}

	}

	public void removePlayerEntityDangerously(Entity var1) {
		var1.setDead();
		if(var1 instanceof EntityPlayer) {
			this.playerEntities.remove(var1);
			this.updateAllPlayersSleepingFlag();
		}

		int var2 = var1.chunkCoordX;
		int var3 = var1.chunkCoordZ;
		if(var1.addedToChunk && this.chunkExists(var2, var3)) {
			this.getChunkFromChunkCoords(var2, var3).removeEntity(var1);
		}

		this.loadedEntityList.remove(var1);
		this.releaseEntitySkin(var1);
	}

	public void addWorldAccess(IWorldAccess var1) {
		this.worldAccesses.add(var1);
	}

	public void removeWorldAccess(IWorldAccess var1) {
		this.worldAccesses.remove(var1);
	}

	public List getCollidingBoundingBoxes(Entity var1, AxisAlignedBB var2) {
		this.collidingBoundingBoxes.clear();
		int var3 = MathHelper.floor_double(var2.minX);
		int var4 = MathHelper.floor_double(var2.maxX + 1.0D);
		int var5 = MathHelper.floor_double(var2.minY);
		int var6 = MathHelper.floor_double(var2.maxY + 1.0D);
		int var7 = MathHelper.floor_double(var2.minZ);
		int var8 = MathHelper.floor_double(var2.maxZ + 1.0D);

		for(int var9 = var3; var9 < var4; ++var9) {
			for(int var10 = var7; var10 < var8; ++var10) {
				if(this.blockExists(var9, 64, var10)) {
					for(int var11 = var5 - 1; var11 < var6; ++var11) {
						Block var12 = Block.blocksList[this.getBlockId(var9, var11, var10)];
						if(var12 != null) {
							var12.addCollisionBoxesToList(this, var9, var11, var10, var2, this.collidingBoundingBoxes, var1);
						}
					}
				}
			}
		}

		double var14 = 0.25D;
		List var15 = this.getEntitiesWithinAABBExcludingEntity(var1, var2.expand(var14, var14, var14));

		for(int var16 = 0; var16 < var15.size(); ++var16) {
			AxisAlignedBB var13 = ((Entity)var15.get(var16)).getBoundingBox();
			if(var13 != null && var13.intersectsWith(var2)) {
				this.collidingBoundingBoxes.add(var13);
			}

			var13 = var1.getCollisionBox((Entity)var15.get(var16));
			if(var13 != null && var13.intersectsWith(var2)) {
				this.collidingBoundingBoxes.add(var13);
			}
		}

		return this.collidingBoundingBoxes;
	}

	public List getCollidingBlockBounds(AxisAlignedBB var1) {
		this.collidingBoundingBoxes.clear();
		int var2 = MathHelper.floor_double(var1.minX);
		int var3 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var4 = MathHelper.floor_double(var1.minY);
		int var5 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var6 = MathHelper.floor_double(var1.minZ);
		int var7 = MathHelper.floor_double(var1.maxZ + 1.0D);

		for(int var8 = var2; var8 < var3; ++var8) {
			for(int var9 = var6; var9 < var7; ++var9) {
				if(this.blockExists(var8, 64, var9)) {
					for(int var10 = var4 - 1; var10 < var5; ++var10) {
						Block var11 = Block.blocksList[this.getBlockId(var8, var10, var9)];
						if(var11 != null) {
							var11.addCollisionBoxesToList(this, var8, var10, var9, var1, this.collidingBoundingBoxes, (Entity)null);
						}
					}
				}
			}
		}

		return this.collidingBoundingBoxes;
	}

	public int calculateSkylightSubtracted(float var1) {
		float var2 = this.getCelestialAngle(var1);
		float var3 = 1.0F - (MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);
		if(var3 < 0.0F) {
			var3 = 0.0F;
		}

		if(var3 > 1.0F) {
			var3 = 1.0F;
		}

		var3 = 1.0F - var3;
		var3 = (float)((double)var3 * (1.0D - (double)(this.getRainStrength(var1) * 5.0F) / 16.0D));
		var3 = (float)((double)var3 * (1.0D - (double)(this.getWeightedThunderStrength(var1) * 5.0F) / 16.0D));
		var3 = 1.0F - var3;
		return (int)(var3 * 11.0F);
	}

	public float getSunBrightness(float var1) {
		float var2 = this.getCelestialAngle(var1);
		float var3 = 1.0F - (MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.2F);
		if(var3 < 0.0F) {
			var3 = 0.0F;
		}

		if(var3 > 1.0F) {
			var3 = 1.0F;
		}

		var3 = 1.0F - var3;
		var3 = (float)((double)var3 * (1.0D - (double)(this.getRainStrength(var1) * 5.0F) / 16.0D));
		var3 = (float)((double)var3 * (1.0D - (double)(this.getWeightedThunderStrength(var1) * 5.0F) / 16.0D));
		return var3 * 0.8F + 0.2F;
	}

	public Vec3 getSkyColor(Entity var1, float var2) {
		float var3 = this.getCelestialAngle(var2);
		float var4 = MathHelper.cos(var3 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(var4 < 0.0F) {
			var4 = 0.0F;
		}

		if(var4 > 1.0F) {
			var4 = 1.0F;
		}

		int var5 = MathHelper.floor_double(var1.posX);
		int var6 = MathHelper.floor_double(var1.posZ);
		BiomeGenBase var7 = this.getBiomeGenForCoords(var5, var6);
		float var8 = var7.getFloatTemperature();
		int var9 = var7.getSkyColorByTemp(var8);
		float var10 = (float)(var9 >> 16 & 255) / 255.0F;
		float var11 = (float)(var9 >> 8 & 255) / 255.0F;
		float var12 = (float)(var9 & 255) / 255.0F;
		var10 *= var4;
		var11 *= var4;
		var12 *= var4;
		float var13 = this.getRainStrength(var2);
		float var14;
		float var15;
		if(var13 > 0.0F) {
			var14 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.6F;
			var15 = 1.0F - var13 * (12.0F / 16.0F);
			var10 = var10 * var15 + var14 * (1.0F - var15);
			var11 = var11 * var15 + var14 * (1.0F - var15);
			var12 = var12 * var15 + var14 * (1.0F - var15);
		}

		var14 = this.getWeightedThunderStrength(var2);
		if(var14 > 0.0F) {
			var15 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.2F;
			float var16 = 1.0F - var14 * (12.0F / 16.0F);
			var10 = var10 * var16 + var15 * (1.0F - var16);
			var11 = var11 * var16 + var15 * (1.0F - var16);
			var12 = var12 * var16 + var15 * (1.0F - var16);
		}

		if(this.lastLightningBolt > 0) {
			var15 = (float)this.lastLightningBolt - var2;
			if(var15 > 1.0F) {
				var15 = 1.0F;
			}

			var15 *= 0.45F;
			var10 = var10 * (1.0F - var15) + 0.8F * var15;
			var11 = var11 * (1.0F - var15) + 0.8F * var15;
			var12 = var12 * (1.0F - var15) + 1.0F * var15;
		}

		return this.getWorldVec3Pool().getVecFromPool((double)var10, (double)var11, (double)var12);
	}

	public float getCelestialAngle(float var1) {
		return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), var1);
	}

	public int getMoonPhase() {
		return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
	}

	public float getCelestialAngleRadians(float var1) {
		float var2 = this.getCelestialAngle(var1);
		return var2 * (float)Math.PI * 2.0F;
	}

	public Vec3 getCloudColour(float var1) {
		float var2 = this.getCelestialAngle(var1);
		float var3 = MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(var3 < 0.0F) {
			var3 = 0.0F;
		}

		if(var3 > 1.0F) {
			var3 = 1.0F;
		}

		float var4 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
		float var5 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
		float var6 = (float)(this.cloudColour & 255L) / 255.0F;
		float var7 = this.getRainStrength(var1);
		float var8;
		float var9;
		if(var7 > 0.0F) {
			var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
			var9 = 1.0F - var7 * 0.95F;
			var4 = var4 * var9 + var8 * (1.0F - var9);
			var5 = var5 * var9 + var8 * (1.0F - var9);
			var6 = var6 * var9 + var8 * (1.0F - var9);
		}

		var4 *= var3 * 0.9F + 0.1F;
		var5 *= var3 * 0.9F + 0.1F;
		var6 *= var3 * 0.85F + 0.15F;
		var8 = this.getWeightedThunderStrength(var1);
		if(var8 > 0.0F) {
			var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
			float var10 = 1.0F - var8 * 0.95F;
			var4 = var4 * var10 + var9 * (1.0F - var10);
			var5 = var5 * var10 + var9 * (1.0F - var10);
			var6 = var6 * var10 + var9 * (1.0F - var10);
		}

		return this.getWorldVec3Pool().getVecFromPool((double)var4, (double)var5, (double)var6);
	}

	public Vec3 getFogColor(float var1) {
		float var2 = this.getCelestialAngle(var1);
		return this.provider.getFogColor(var2, var1);
	}

	public int getPrecipitationHeight(int var1, int var2) {
		return this.getChunkFromBlockCoords(var1, var2).getPrecipitationHeight(var1 & 15, var2 & 15);
	}

	public int getTopSolidOrLiquidBlock(int var1, int var2) {
		Chunk var3 = this.getChunkFromBlockCoords(var1, var2);
		int var4 = var3.getTopFilledSegment() + 15;
		var1 &= 15;

		for(var2 &= 15; var4 > 0; --var4) {
			int var5 = var3.getBlockID(var1, var4, var2);
			if(var5 != 0 && Block.blocksList[var5].blockMaterial.blocksMovement() && Block.blocksList[var5].blockMaterial != Material.leaves) {
				return var4 + 1;
			}
		}

		return -1;
	}

	public float getStarBrightness(float var1) {
		float var2 = this.getCelestialAngle(var1);
		float var3 = 1.0F - (MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.25F);
		if(var3 < 0.0F) {
			var3 = 0.0F;
		}

		if(var3 > 1.0F) {
			var3 = 1.0F;
		}

		return var3 * var3 * 0.5F;
	}

	public void scheduleBlockUpdate(int var1, int var2, int var3, int var4, int var5) {
	}

	public void func_82740_a(int var1, int var2, int var3, int var4, int var5, int var6) {
	}

	public void scheduleBlockUpdateFromLoad(int var1, int var2, int var3, int var4, int var5, int var6) {
	}

	public void updateEntities() {
		this.theProfiler.startSection("entities");
		this.theProfiler.startSection("global");

		int var1;
		Entity var2;
		CrashReport var4;
		CrashReportCategory var5;
		for(var1 = 0; var1 < this.weatherEffects.size(); ++var1) {
			var2 = (Entity)this.weatherEffects.get(var1);

			try {
				++var2.ticksExisted;
				var2.onUpdate();
			} catch (Throwable var8) {
				var4 = CrashReport.makeCrashReport(var8, "Ticking entity");
				var5 = var4.makeCategory("Entity being ticked");
				if(var2 == null) {
					var5.addCrashSection("Entity", "~~NULL~~");
				} else {
					var2.func_85029_a(var5);
				}

				throw new ReportedException(var4);
			}

			if(var2.isDead) {
				this.weatherEffects.remove(var1--);
			}
		}

		this.theProfiler.endStartSection("remove");
		this.loadedEntityList.removeAll(this.unloadedEntityList);

		int var3;
		int var13;
		for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
			var2 = (Entity)this.unloadedEntityList.get(var1);
			var3 = var2.chunkCoordX;
			var13 = var2.chunkCoordZ;
			if(var2.addedToChunk && this.chunkExists(var3, var13)) {
				this.getChunkFromChunkCoords(var3, var13).removeEntity(var2);
			}
		}

		for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
			this.releaseEntitySkin((Entity)this.unloadedEntityList.get(var1));
		}

		this.unloadedEntityList.clear();
		this.theProfiler.endStartSection("regular");

		for(var1 = 0; var1 < this.loadedEntityList.size(); ++var1) {
			var2 = (Entity)this.loadedEntityList.get(var1);
			if(var2.ridingEntity != null) {
				if(!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2) {
					continue;
				}

				var2.ridingEntity.riddenByEntity = null;
				var2.ridingEntity = null;
			}

			this.theProfiler.startSection("tick");
			if(!var2.isDead) {
				try {
					this.updateEntity(var2);
				} catch (Throwable var7) {
					var4 = CrashReport.makeCrashReport(var7, "Ticking entity");
					var5 = var4.makeCategory("Entity being ticked");
					var2.func_85029_a(var5);
					throw new ReportedException(var4);
				}
			}

			this.theProfiler.endSection();
			this.theProfiler.startSection("remove");
			if(var2.isDead) {
				var3 = var2.chunkCoordX;
				var13 = var2.chunkCoordZ;
				if(var2.addedToChunk && this.chunkExists(var3, var13)) {
					this.getChunkFromChunkCoords(var3, var13).removeEntity(var2);
				}

				this.loadedEntityList.remove(var1--);
				this.releaseEntitySkin(var2);
			}

			this.theProfiler.endSection();
		}

		this.theProfiler.endStartSection("tileEntities");
		this.scanningTileEntities = true;
		Iterator var14 = this.loadedTileEntityList.iterator();

		while(var14.hasNext()) {
			TileEntity var9 = (TileEntity)var14.next();
			if(!var9.isInvalid() && var9.func_70309_m() && this.blockExists(var9.xCoord, var9.yCoord, var9.zCoord)) {
				try {
					var9.updateEntity();
				} catch (Throwable var6) {
					var4 = CrashReport.makeCrashReport(var6, "Ticking tile entity");
					var5 = var4.makeCategory("Tile entity being ticked");
					var9.func_85027_a(var5);
					throw new ReportedException(var4);
				}
			}

			if(var9.isInvalid()) {
				var14.remove();
				if(this.chunkExists(var9.xCoord >> 4, var9.zCoord >> 4)) {
					Chunk var11 = this.getChunkFromChunkCoords(var9.xCoord >> 4, var9.zCoord >> 4);
					if(var11 != null) {
						var11.removeChunkBlockTileEntity(var9.xCoord & 15, var9.yCoord, var9.zCoord & 15);
					}
				}
			}
		}

		this.scanningTileEntities = false;
		if(!this.entityRemoval.isEmpty()) {
			this.loadedTileEntityList.removeAll(this.entityRemoval);
			this.entityRemoval.clear();
		}

		this.theProfiler.endStartSection("pendingTileEntities");
		if(!this.addedTileEntityList.isEmpty()) {
			for(int var10 = 0; var10 < this.addedTileEntityList.size(); ++var10) {
				TileEntity var12 = (TileEntity)this.addedTileEntityList.get(var10);
				if(!var12.isInvalid()) {
					if(!this.loadedTileEntityList.contains(var12)) {
						this.loadedTileEntityList.add(var12);
					}

					if(this.chunkExists(var12.xCoord >> 4, var12.zCoord >> 4)) {
						Chunk var15 = this.getChunkFromChunkCoords(var12.xCoord >> 4, var12.zCoord >> 4);
						if(var15 != null) {
							var15.setChunkBlockTileEntity(var12.xCoord & 15, var12.yCoord, var12.zCoord & 15, var12);
						}
					}

					this.markBlockForUpdate(var12.xCoord, var12.yCoord, var12.zCoord);
				}
			}

			this.addedTileEntityList.clear();
		}

		this.theProfiler.endSection();
		this.theProfiler.endSection();
	}

	public void addTileEntity(Collection var1) {
		if(this.scanningTileEntities) {
			this.addedTileEntityList.addAll(var1);
		} else {
			this.loadedTileEntityList.addAll(var1);
		}

	}

	public void updateEntity(Entity var1) {
		this.updateEntityWithOptionalForce(var1, true);
	}

	public void updateEntityWithOptionalForce(Entity var1, boolean var2) {
		int var3 = MathHelper.floor_double(var1.posX);
		int var4 = MathHelper.floor_double(var1.posZ);
		byte var5 = 32;
		if(!var2 || this.checkChunksExist(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5)) {
			var1.lastTickPosX = var1.posX;
			var1.lastTickPosY = var1.posY;
			var1.lastTickPosZ = var1.posZ;
			var1.prevRotationYaw = var1.rotationYaw;
			var1.prevRotationPitch = var1.rotationPitch;
			if(var2 && var1.addedToChunk) {
				if(var1.ridingEntity != null) {
					var1.updateRidden();
				} else {
					++var1.ticksExisted;
					var1.onUpdate();
				}
			}

			this.theProfiler.startSection("chunkCheck");
			if(Double.isNaN(var1.posX) || Double.isInfinite(var1.posX)) {
				var1.posX = var1.lastTickPosX;
			}

			if(Double.isNaN(var1.posY) || Double.isInfinite(var1.posY)) {
				var1.posY = var1.lastTickPosY;
			}

			if(Double.isNaN(var1.posZ) || Double.isInfinite(var1.posZ)) {
				var1.posZ = var1.lastTickPosZ;
			}

			if(Double.isNaN((double)var1.rotationPitch) || Double.isInfinite((double)var1.rotationPitch)) {
				var1.rotationPitch = var1.prevRotationPitch;
			}

			if(Double.isNaN((double)var1.rotationYaw) || Double.isInfinite((double)var1.rotationYaw)) {
				var1.rotationYaw = var1.prevRotationYaw;
			}

			int var6 = MathHelper.floor_double(var1.posX / 16.0D);
			int var7 = MathHelper.floor_double(var1.posY / 16.0D);
			int var8 = MathHelper.floor_double(var1.posZ / 16.0D);
			if(!var1.addedToChunk || var1.chunkCoordX != var6 || var1.chunkCoordY != var7 || var1.chunkCoordZ != var8) {
				if(var1.addedToChunk && this.chunkExists(var1.chunkCoordX, var1.chunkCoordZ)) {
					this.getChunkFromChunkCoords(var1.chunkCoordX, var1.chunkCoordZ).removeEntityAtIndex(var1, var1.chunkCoordY);
				}

				if(this.chunkExists(var6, var8)) {
					var1.addedToChunk = true;
					this.getChunkFromChunkCoords(var6, var8).addEntity(var1);
				} else {
					var1.addedToChunk = false;
				}
			}

			this.theProfiler.endSection();
			if(var2 && var1.addedToChunk && var1.riddenByEntity != null) {
				if(!var1.riddenByEntity.isDead && var1.riddenByEntity.ridingEntity == var1) {
					this.updateEntity(var1.riddenByEntity);
				} else {
					var1.riddenByEntity.ridingEntity = null;
					var1.riddenByEntity = null;
				}
			}

		}
	}

	public boolean checkNoEntityCollision(AxisAlignedBB var1) {
		return this.checkNoEntityCollision(var1, (Entity)null);
	}

	public boolean checkNoEntityCollision(AxisAlignedBB var1, Entity var2) {
		List var3 = this.getEntitiesWithinAABBExcludingEntity((Entity)null, var1);

		for(int var4 = 0; var4 < var3.size(); ++var4) {
			Entity var5 = (Entity)var3.get(var4);
			if(!var5.isDead && var5.preventEntitySpawning && var5 != var2) {
				return false;
			}
		}

		return true;
	}

	public boolean checkBlockCollision(AxisAlignedBB var1) {
		int var2 = MathHelper.floor_double(var1.minX);
		int var3 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var4 = MathHelper.floor_double(var1.minY);
		int var5 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var6 = MathHelper.floor_double(var1.minZ);
		int var7 = MathHelper.floor_double(var1.maxZ + 1.0D);
		if(var1.minX < 0.0D) {
			--var2;
		}

		if(var1.minY < 0.0D) {
			--var4;
		}

		if(var1.minZ < 0.0D) {
			--var6;
		}

		for(int var8 = var2; var8 < var3; ++var8) {
			for(int var9 = var4; var9 < var5; ++var9) {
				for(int var10 = var6; var10 < var7; ++var10) {
					Block var11 = Block.blocksList[this.getBlockId(var8, var9, var10)];
					if(var11 != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAnyLiquid(AxisAlignedBB var1) {
		int var2 = MathHelper.floor_double(var1.minX);
		int var3 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var4 = MathHelper.floor_double(var1.minY);
		int var5 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var6 = MathHelper.floor_double(var1.minZ);
		int var7 = MathHelper.floor_double(var1.maxZ + 1.0D);
		if(var1.minX < 0.0D) {
			--var2;
		}

		if(var1.minY < 0.0D) {
			--var4;
		}

		if(var1.minZ < 0.0D) {
			--var6;
		}

		for(int var8 = var2; var8 < var3; ++var8) {
			for(int var9 = var4; var9 < var5; ++var9) {
				for(int var10 = var6; var10 < var7; ++var10) {
					Block var11 = Block.blocksList[this.getBlockId(var8, var9, var10)];
					if(var11 != null && var11.blockMaterial.isLiquid()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isBoundingBoxBurning(AxisAlignedBB var1) {
		int var2 = MathHelper.floor_double(var1.minX);
		int var3 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var4 = MathHelper.floor_double(var1.minY);
		int var5 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var6 = MathHelper.floor_double(var1.minZ);
		int var7 = MathHelper.floor_double(var1.maxZ + 1.0D);
		if(this.checkChunksExist(var2, var4, var6, var3, var5, var7)) {
			for(int var8 = var2; var8 < var3; ++var8) {
				for(int var9 = var4; var9 < var5; ++var9) {
					for(int var10 = var6; var10 < var7; ++var10) {
						int var11 = this.getBlockId(var8, var9, var10);
						if(var11 == Block.fire.blockID || var11 == Block.lavaMoving.blockID || var11 == Block.lavaStill.blockID) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean handleMaterialAcceleration(AxisAlignedBB var1, Material var2, Entity var3) {
		int var4 = MathHelper.floor_double(var1.minX);
		int var5 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var6 = MathHelper.floor_double(var1.minY);
		int var7 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var8 = MathHelper.floor_double(var1.minZ);
		int var9 = MathHelper.floor_double(var1.maxZ + 1.0D);
		if(!this.checkChunksExist(var4, var6, var8, var5, var7, var9)) {
			return false;
		} else {
			boolean var10 = false;
			Vec3 var11 = this.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);

			for(int var12 = var4; var12 < var5; ++var12) {
				for(int var13 = var6; var13 < var7; ++var13) {
					for(int var14 = var8; var14 < var9; ++var14) {
						Block var15 = Block.blocksList[this.getBlockId(var12, var13, var14)];
						if(var15 != null && var15.blockMaterial == var2) {
							double var16 = (double)((float)(var13 + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(var12, var13, var14)));
							if((double)var7 >= var16) {
								var10 = true;
								var15.velocityToAddToEntity(this, var12, var13, var14, var3, var11);
							}
						}
					}
				}
			}

			if(var11.lengthVector() > 0.0D && var3.func_96092_aw()) {
				var11 = var11.normalize();
				double var18 = 0.014D;
				var3.motionX += var11.xCoord * var18;
				var3.motionY += var11.yCoord * var18;
				var3.motionZ += var11.zCoord * var18;
			}

			return var10;
		}
	}

	public boolean isMaterialInBB(AxisAlignedBB var1, Material var2) {
		int var3 = MathHelper.floor_double(var1.minX);
		int var4 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var5 = MathHelper.floor_double(var1.minY);
		int var6 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var7 = MathHelper.floor_double(var1.minZ);
		int var8 = MathHelper.floor_double(var1.maxZ + 1.0D);

		for(int var9 = var3; var9 < var4; ++var9) {
			for(int var10 = var5; var10 < var6; ++var10) {
				for(int var11 = var7; var11 < var8; ++var11) {
					Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];
					if(var12 != null && var12.blockMaterial == var2) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAABBInMaterial(AxisAlignedBB var1, Material var2) {
		int var3 = MathHelper.floor_double(var1.minX);
		int var4 = MathHelper.floor_double(var1.maxX + 1.0D);
		int var5 = MathHelper.floor_double(var1.minY);
		int var6 = MathHelper.floor_double(var1.maxY + 1.0D);
		int var7 = MathHelper.floor_double(var1.minZ);
		int var8 = MathHelper.floor_double(var1.maxZ + 1.0D);

		for(int var9 = var3; var9 < var4; ++var9) {
			for(int var10 = var5; var10 < var6; ++var10) {
				for(int var11 = var7; var11 < var8; ++var11) {
					Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];
					if(var12 != null && var12.blockMaterial == var2) {
						int var13 = this.getBlockMetadata(var9, var10, var11);
						double var14 = (double)(var10 + 1);
						if(var13 < 8) {
							var14 = (double)(var10 + 1) - (double)var13 / 8.0D;
						}

						if(var14 >= var1.minY) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9) {
		return this.newExplosion(var1, var2, var4, var6, var8, false, var9);
	}

	public Explosion newExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
		Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8);
		var11.isFlaming = var9;
		var11.isSmoking = var10;
		var11.doExplosionA();
		var11.doExplosionB(true);
		return var11;
	}

	public float getBlockDensity(Vec3 var1, AxisAlignedBB var2) {
		double var3 = 1.0D / ((var2.maxX - var2.minX) * 2.0D + 1.0D);
		double var5 = 1.0D / ((var2.maxY - var2.minY) * 2.0D + 1.0D);
		double var7 = 1.0D / ((var2.maxZ - var2.minZ) * 2.0D + 1.0D);
		int var9 = 0;
		int var10 = 0;

		for(float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3)) {
			for(float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5)) {
				for(float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7)) {
					double var14 = var2.minX + (var2.maxX - var2.minX) * (double)var11;
					double var16 = var2.minY + (var2.maxY - var2.minY) * (double)var12;
					double var18 = var2.minZ + (var2.maxZ - var2.minZ) * (double)var13;
					if(this.rayTraceBlocks(this.getWorldVec3Pool().getVecFromPool(var14, var16, var18), var1) == null) {
						++var9;
					}

					++var10;
				}
			}
		}

		return (float)var9 / (float)var10;
	}

	public boolean extinguishFire(EntityPlayer var1, int var2, int var3, int var4, int var5) {
		if(var5 == 0) {
			--var3;
		}

		if(var5 == 1) {
			++var3;
		}

		if(var5 == 2) {
			--var4;
		}

		if(var5 == 3) {
			++var4;
		}

		if(var5 == 4) {
			--var2;
		}

		if(var5 == 5) {
			++var2;
		}

		if(this.getBlockId(var2, var3, var4) == Block.fire.blockID) {
			this.playAuxSFXAtEntity(var1, 1004, var2, var3, var4, 0);
			this.setBlockToAir(var2, var3, var4);
			return true;
		} else {
			return false;
		}
	}

	public String getDebugLoadedEntities() {
		return "All: " + this.loadedEntityList.size();
	}

	public String getProviderName() {
		return this.chunkProvider.makeString();
	}

	public TileEntity getBlockTileEntity(int var1, int var2, int var3) {
		if(var2 >= 0 && var2 < 256) {
			TileEntity var4 = null;
			int var5;
			TileEntity var6;
			if(this.scanningTileEntities) {
				for(var5 = 0; var5 < this.addedTileEntityList.size(); ++var5) {
					var6 = (TileEntity)this.addedTileEntityList.get(var5);
					if(!var6.isInvalid() && var6.xCoord == var1 && var6.yCoord == var2 && var6.zCoord == var3) {
						var4 = var6;
						break;
					}
				}
			}

			if(var4 == null) {
				Chunk var7 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				if(var7 != null) {
					var4 = var7.getChunkBlockTileEntity(var1 & 15, var2, var3 & 15);
				}
			}

			if(var4 == null) {
				for(var5 = 0; var5 < this.addedTileEntityList.size(); ++var5) {
					var6 = (TileEntity)this.addedTileEntityList.get(var5);
					if(!var6.isInvalid() && var6.xCoord == var1 && var6.yCoord == var2 && var6.zCoord == var3) {
						var4 = var6;
						break;
					}
				}
			}

			return var4;
		} else {
			return null;
		}
	}

	public void setBlockTileEntity(int var1, int var2, int var3, TileEntity var4) {
		if(var4 != null && !var4.isInvalid()) {
			if(this.scanningTileEntities) {
				var4.xCoord = var1;
				var4.yCoord = var2;
				var4.zCoord = var3;
				Iterator var5 = this.addedTileEntityList.iterator();

				while(var5.hasNext()) {
					TileEntity var6 = (TileEntity)var5.next();
					if(var6.xCoord == var1 && var6.yCoord == var2 && var6.zCoord == var3) {
						var6.invalidate();
						var5.remove();
					}
				}

				this.addedTileEntityList.add(var4);
			} else {
				this.loadedTileEntityList.add(var4);
				Chunk var7 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
				if(var7 != null) {
					var7.setChunkBlockTileEntity(var1 & 15, var2, var3 & 15, var4);
				}
			}
		}

	}

	public void removeBlockTileEntity(int var1, int var2, int var3) {
		TileEntity var4 = this.getBlockTileEntity(var1, var2, var3);
		if(var4 != null && this.scanningTileEntities) {
			var4.invalidate();
			this.addedTileEntityList.remove(var4);
		} else {
			if(var4 != null) {
				this.addedTileEntityList.remove(var4);
				this.loadedTileEntityList.remove(var4);
			}

			Chunk var5 = this.getChunkFromChunkCoords(var1 >> 4, var3 >> 4);
			if(var5 != null) {
				var5.removeChunkBlockTileEntity(var1 & 15, var2, var3 & 15);
			}
		}

	}

	public void markTileEntityForDespawn(TileEntity var1) {
		this.entityRemoval.add(var1);
	}

	public boolean isBlockOpaqueCube(int var1, int var2, int var3) {
		Block var4 = Block.blocksList[this.getBlockId(var1, var2, var3)];
		return var4 == null ? false : var4.isOpaqueCube();
	}

	public boolean isBlockNormalCube(int var1, int var2, int var3) {
		return Block.isNormalCube(this.getBlockId(var1, var2, var3));
	}

	public boolean func_85174_u(int var1, int var2, int var3) {
		int var4 = this.getBlockId(var1, var2, var3);
		if(var4 != 0 && Block.blocksList[var4] != null) {
			AxisAlignedBB var5 = Block.blocksList[var4].getCollisionBoundingBoxFromPool(this, var1, var2, var3);
			return var5 != null && var5.getAverageEdgeLength() >= 1.0D;
		} else {
			return false;
		}
	}

	public boolean doesBlockHaveSolidTopSurface(int var1, int var2, int var3) {
		Block var4 = Block.blocksList[this.getBlockId(var1, var2, var3)];
		return this.isBlockTopFacingSurfaceSolid(var4, this.getBlockMetadata(var1, var2, var3));
	}

	public boolean isBlockTopFacingSurfaceSolid(Block var1, int var2) {
		return var1 == null ? false : (var1.blockMaterial.isOpaque() && var1.renderAsNormalBlock() ? true : (var1 instanceof BlockStairs ? (var2 & 4) == 4 : (var1 instanceof BlockHalfSlab ? (var2 & 8) == 8 : (var1 instanceof BlockHopper ? true : (var1 instanceof BlockSnow ? (var2 & 7) == 7 : false)))));
	}

	public boolean isBlockNormalCubeDefault(int var1, int var2, int var3, boolean var4) {
		if(var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
			Chunk var5 = this.chunkProvider.provideChunk(var1 >> 4, var3 >> 4);
			if(var5 != null && !var5.isEmpty()) {
				Block var6 = Block.blocksList[this.getBlockId(var1, var2, var3)];
				return var6 == null ? false : var6.blockMaterial.isOpaque() && var6.renderAsNormalBlock();
			} else {
				return var4;
			}
		} else {
			return var4;
		}
	}

	public void calculateInitialSkylight() {
		int var1 = this.calculateSkylightSubtracted(1.0F);
		if(var1 != this.skylightSubtracted) {
			this.skylightSubtracted = var1;
		}

	}

	public void setAllowedSpawnTypes(boolean var1, boolean var2) {
		this.spawnHostileMobs = var1;
		this.spawnPeacefulMobs = var2;
	}

	public void tick() {
		this.updateWeather();
	}

	private void calculateInitialWeather() {
		if(this.worldInfo.isRaining()) {
			this.rainingStrength = 1.0F;
			if(this.worldInfo.isThundering()) {
				this.thunderingStrength = 1.0F;
			}
		}

	}

	protected void updateWeather() {
		if(!this.provider.hasNoSky) {
			int var1 = this.worldInfo.getThunderTime();
			if(var1 <= 0) {
				if(this.worldInfo.isThundering()) {
					this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
				} else {
					this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
				}
			} else {
				--var1;
				this.worldInfo.setThunderTime(var1);
				if(var1 <= 0) {
					this.worldInfo.setThundering(!this.worldInfo.isThundering());
				}
			}

			int var2 = this.worldInfo.getRainTime();
			if(var2 <= 0) {
				if(this.worldInfo.isRaining()) {
					this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
				} else {
					this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
				}
			} else {
				--var2;
				this.worldInfo.setRainTime(var2);
				if(var2 <= 0) {
					this.worldInfo.setRaining(!this.worldInfo.isRaining());
				}
			}

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

	public void toggleRain() {
		this.worldInfo.setRainTime(1);
	}

	protected void setActivePlayerChunksAndCheckLight() {
		this.activeChunkSet.clear();
		this.theProfiler.startSection("buildList");

		int var1;
		EntityPlayer var2;
		int var3;
		int var4;
		for(var1 = 0; var1 < this.playerEntities.size(); ++var1) {
			var2 = (EntityPlayer)this.playerEntities.get(var1);
			var3 = MathHelper.floor_double(var2.posX / 16.0D);
			var4 = MathHelper.floor_double(var2.posZ / 16.0D);
			byte var5 = 7;

			for(int var6 = -var5; var6 <= var5; ++var6) {
				for(int var7 = -var5; var7 <= var5; ++var7) {
					this.activeChunkSet.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
				}
			}
		}

		this.theProfiler.endSection();
		if(this.ambientTickCountdown > 0) {
			--this.ambientTickCountdown;
		}

		this.theProfiler.startSection("playerCheckLight");
		if(!this.playerEntities.isEmpty()) {
			var1 = this.rand.nextInt(this.playerEntities.size());
			var2 = (EntityPlayer)this.playerEntities.get(var1);
			var3 = MathHelper.floor_double(var2.posX) + this.rand.nextInt(11) - 5;
			var4 = MathHelper.floor_double(var2.posY) + this.rand.nextInt(11) - 5;
			int var8 = MathHelper.floor_double(var2.posZ) + this.rand.nextInt(11) - 5;
			this.updateAllLightTypes(var3, var4, var8);
		}

		this.theProfiler.endSection();
	}

	protected void moodSoundAndLightCheck(int var1, int var2, Chunk var3) {
		this.theProfiler.endStartSection("moodSound");
		if(this.ambientTickCountdown == 0 && !this.isRemote) {
			this.updateLCG = this.updateLCG * 3 + 1013904223;
			int var4 = this.updateLCG >> 2;
			int var5 = var4 & 15;
			int var6 = var4 >> 8 & 15;
			int var7 = var4 >> 16 & 127;
			int var8 = var3.getBlockID(var5, var7, var6);
			var5 += var1;
			var6 += var2;
			if(var8 == 0 && this.getFullBlockLightValue(var5, var7, var6) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, var5, var7, var6) <= 0) {
				EntityPlayer var9 = this.getClosestPlayer((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, 8.0D);
				if(var9 != null && var9.getDistanceSq((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D) > 4.0D) {
					this.playSoundEffect((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
					this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
				}
			}
		}

		this.theProfiler.endStartSection("checkLight");
		var3.enqueueRelightChecks();
	}

	protected void tickBlocksAndAmbiance() {
		this.setActivePlayerChunksAndCheckLight();
	}

	public boolean isBlockFreezable(int var1, int var2, int var3) {
		return this.canBlockFreeze(var1, var2, var3, false);
	}

	public boolean isBlockFreezableNaturally(int var1, int var2, int var3) {
		return this.canBlockFreeze(var1, var2, var3, true);
	}

	public boolean canBlockFreeze(int var1, int var2, int var3, boolean var4) {
		BiomeGenBase var5 = this.getBiomeGenForCoords(var1, var3);
		float var6 = var5.getFloatTemperature();
		if(var6 > 0.15F) {
			return false;
		} else {
			if(var2 >= 0 && var2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, var1, var2, var3) < 10) {
				int var7 = this.getBlockId(var1, var2, var3);
				if((var7 == Block.waterStill.blockID || var7 == Block.waterMoving.blockID) && this.getBlockMetadata(var1, var2, var3) == 0) {
					if(!var4) {
						return true;
					}

					boolean var8 = true;
					if(var8 && this.getBlockMaterial(var1 - 1, var2, var3) != Material.water) {
						var8 = false;
					}

					if(var8 && this.getBlockMaterial(var1 + 1, var2, var3) != Material.water) {
						var8 = false;
					}

					if(var8 && this.getBlockMaterial(var1, var2, var3 - 1) != Material.water) {
						var8 = false;
					}

					if(var8 && this.getBlockMaterial(var1, var2, var3 + 1) != Material.water) {
						var8 = false;
					}

					if(!var8) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public boolean canSnowAt(int var1, int var2, int var3) {
		BiomeGenBase var4 = this.getBiomeGenForCoords(var1, var3);
		float var5 = var4.getFloatTemperature();
		if(var5 > 0.15F) {
			return false;
		} else {
			if(var2 >= 0 && var2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, var1, var2, var3) < 10) {
				int var6 = this.getBlockId(var1, var2 - 1, var3);
				int var7 = this.getBlockId(var1, var2, var3);
				if(var7 == 0 && Block.snow.canPlaceBlockAt(this, var1, var2, var3) && var6 != 0 && var6 != Block.ice.blockID && Block.blocksList[var6].blockMaterial.blocksMovement()) {
					return true;
				}
			}

			return false;
		}
	}

	public void updateAllLightTypes(int var1, int var2, int var3) {
		if(!this.provider.hasNoSky) {
			this.updateLightByType(EnumSkyBlock.Sky, var1, var2, var3);
		}

		this.updateLightByType(EnumSkyBlock.Block, var1, var2, var3);
	}

	private int computeLightValue(int var1, int var2, int var3, EnumSkyBlock var4) {
		if(var4 == EnumSkyBlock.Sky && this.canBlockSeeTheSky(var1, var2, var3)) {
			return 15;
		} else {
			int var5 = this.getBlockId(var1, var2, var3);
			int var6 = var4 == EnumSkyBlock.Sky ? 0 : Block.lightValue[var5];
			int var7 = Block.lightOpacity[var5];
			if(var7 >= 15 && Block.lightValue[var5] > 0) {
				var7 = 1;
			}

			if(var7 < 1) {
				var7 = 1;
			}

			if(var7 >= 15) {
				return 0;
			} else if(var6 >= 14) {
				return var6;
			} else {
				for(int var8 = 0; var8 < 6; ++var8) {
					int var9 = var1 + Facing.offsetsXForSide[var8];
					int var10 = var2 + Facing.offsetsYForSide[var8];
					int var11 = var3 + Facing.offsetsZForSide[var8];
					int var12 = this.getSavedLightValue(var4, var9, var10, var11) - var7;
					if(var12 > var6) {
						var6 = var12;
					}

					if(var6 >= 14) {
						return var6;
					}
				}

				return var6;
			}
		}
	}

	public void updateLightByType(EnumSkyBlock var1, int var2, int var3, int var4) {
		if(this.doChunksNearChunkExist(var2, var3, var4, 17)) {
			int var5 = 0;
			int var6 = 0;
			this.theProfiler.startSection("getBrightness");
			int var7 = this.getSavedLightValue(var1, var2, var3, var4);
			int var8 = this.computeLightValue(var2, var3, var4, var1);
			int var9;
			int var10;
			int var11;
			int var12;
			int var13;
			int var14;
			int var15;
			int var16;
			int var17;
			if(var8 > var7) {
				this.lightUpdateBlockList[var6++] = 133152;
			} else if(var8 < var7) {
				this.lightUpdateBlockList[var6++] = 133152 | var7 << 18;

				label90:
				while(true) {
					do {
						do {
							do {
								if(var5 >= var6) {
									var5 = 0;
									break label90;
								}

								var9 = this.lightUpdateBlockList[var5++];
								var10 = (var9 & 63) - 32 + var2;
								var11 = (var9 >> 6 & 63) - 32 + var3;
								var12 = (var9 >> 12 & 63) - 32 + var4;
								var13 = var9 >> 18 & 15;
								var14 = this.getSavedLightValue(var1, var10, var11, var12);
							} while(var14 != var13);

							this.setLightValue(var1, var10, var11, var12, 0);
						} while(var13 <= 0);

						var15 = MathHelper.abs_int(var10 - var2);
						var16 = MathHelper.abs_int(var11 - var3);
						var17 = MathHelper.abs_int(var12 - var4);
					} while(var15 + var16 + var17 >= 17);

					for(int var18 = 0; var18 < 6; ++var18) {
						int var19 = var10 + Facing.offsetsXForSide[var18];
						int var20 = var11 + Facing.offsetsYForSide[var18];
						int var21 = var12 + Facing.offsetsZForSide[var18];
						int var22 = Math.max(1, Block.lightOpacity[this.getBlockId(var19, var20, var21)]);
						var14 = this.getSavedLightValue(var1, var19, var20, var21);
						if(var14 == var13 - var22 && var6 < this.lightUpdateBlockList.length) {
							this.lightUpdateBlockList[var6++] = var19 - var2 + 32 | var20 - var3 + 32 << 6 | var21 - var4 + 32 << 12 | var13 - var22 << 18;
						}
					}
				}
			}

			this.theProfiler.endSection();
			this.theProfiler.startSection("checkedPosition < toCheckCount");

			while(var5 < var6) {
				var9 = this.lightUpdateBlockList[var5++];
				var10 = (var9 & 63) - 32 + var2;
				var11 = (var9 >> 6 & 63) - 32 + var3;
				var12 = (var9 >> 12 & 63) - 32 + var4;
				var13 = this.getSavedLightValue(var1, var10, var11, var12);
				var14 = this.computeLightValue(var10, var11, var12, var1);
				if(var14 != var13) {
					this.setLightValue(var1, var10, var11, var12, var14);
					if(var14 > var13) {
						var15 = Math.abs(var10 - var2);
						var16 = Math.abs(var11 - var3);
						var17 = Math.abs(var12 - var4);
						boolean var23 = var6 < this.lightUpdateBlockList.length - 6;
						if(var15 + var16 + var17 < 17 && var23) {
							if(this.getSavedLightValue(var1, var10 - 1, var11, var12) < var14) {
								this.lightUpdateBlockList[var6++] = var10 - 1 - var2 + 32 + (var11 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
							}

							if(this.getSavedLightValue(var1, var10 + 1, var11, var12) < var14) {
								this.lightUpdateBlockList[var6++] = var10 + 1 - var2 + 32 + (var11 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
							}

							if(this.getSavedLightValue(var1, var10, var11 - 1, var12) < var14) {
								this.lightUpdateBlockList[var6++] = var10 - var2 + 32 + (var11 - 1 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
							}

							if(this.getSavedLightValue(var1, var10, var11 + 1, var12) < var14) {
								this.lightUpdateBlockList[var6++] = var10 - var2 + 32 + (var11 + 1 - var3 + 32 << 6) + (var12 - var4 + 32 << 12);
							}

							if(this.getSavedLightValue(var1, var10, var11, var12 - 1) < var14) {
								this.lightUpdateBlockList[var6++] = var10 - var2 + 32 + (var11 - var3 + 32 << 6) + (var12 - 1 - var4 + 32 << 12);
							}

							if(this.getSavedLightValue(var1, var10, var11, var12 + 1) < var14) {
								this.lightUpdateBlockList[var6++] = var10 - var2 + 32 + (var11 - var3 + 32 << 6) + (var12 + 1 - var4 + 32 << 12);
							}
						}
					}
				}
			}

			this.theProfiler.endSection();
		}
	}

	public boolean tickUpdates(boolean var1) {
		return false;
	}

	public List getPendingBlockUpdates(Chunk var1, boolean var2) {
		return null;
	}

	public List getEntitiesWithinAABBExcludingEntity(Entity var1, AxisAlignedBB var2) {
		return this.getEntitiesWithinAABBExcludingEntity(var1, var2, (IEntitySelector)null);
	}

	public List getEntitiesWithinAABBExcludingEntity(Entity var1, AxisAlignedBB var2, IEntitySelector var3) {
		ArrayList var4 = new ArrayList();
		int var5 = MathHelper.floor_double((var2.minX - 2.0D) / 16.0D);
		int var6 = MathHelper.floor_double((var2.maxX + 2.0D) / 16.0D);
		int var7 = MathHelper.floor_double((var2.minZ - 2.0D) / 16.0D);
		int var8 = MathHelper.floor_double((var2.maxZ + 2.0D) / 16.0D);

		for(int var9 = var5; var9 <= var6; ++var9) {
			for(int var10 = var7; var10 <= var8; ++var10) {
				if(this.chunkExists(var9, var10)) {
					this.getChunkFromChunkCoords(var9, var10).getEntitiesWithinAABBForEntity(var1, var2, var4, var3);
				}
			}
		}

		return var4;
	}

	public List getEntitiesWithinAABB(Class var1, AxisAlignedBB var2) {
		return this.selectEntitiesWithinAABB(var1, var2, (IEntitySelector)null);
	}

	public List selectEntitiesWithinAABB(Class var1, AxisAlignedBB var2, IEntitySelector var3) {
		int var4 = MathHelper.floor_double((var2.minX - 2.0D) / 16.0D);
		int var5 = MathHelper.floor_double((var2.maxX + 2.0D) / 16.0D);
		int var6 = MathHelper.floor_double((var2.minZ - 2.0D) / 16.0D);
		int var7 = MathHelper.floor_double((var2.maxZ + 2.0D) / 16.0D);
		ArrayList var8 = new ArrayList();

		for(int var9 = var4; var9 <= var5; ++var9) {
			for(int var10 = var6; var10 <= var7; ++var10) {
				if(this.chunkExists(var9, var10)) {
					this.getChunkFromChunkCoords(var9, var10).getEntitiesOfTypeWithinAAAB(var1, var2, var8, var3);
				}
			}
		}

		return var8;
	}

	public Entity findNearestEntityWithinAABB(Class var1, AxisAlignedBB var2, Entity var3) {
		List var4 = this.getEntitiesWithinAABB(var1, var2);
		Entity var5 = null;
		double var6 = Double.MAX_VALUE;

		for(int var8 = 0; var8 < var4.size(); ++var8) {
			Entity var9 = (Entity)var4.get(var8);
			if(var9 != var3) {
				double var10 = var3.getDistanceSqToEntity(var9);
				if(var10 <= var6) {
					var5 = var9;
					var6 = var10;
				}
			}
		}

		return var5;
	}

	public abstract Entity getEntityByID(int var1);

	public List getLoadedEntityList() {
		return this.loadedEntityList;
	}

	public void updateTileEntityChunkAndDoNothing(int var1, int var2, int var3, TileEntity var4) {
		if(this.blockExists(var1, var2, var3)) {
			this.getChunkFromBlockCoords(var1, var3).setChunkModified();
		}

	}

	public int countEntities(Class var1) {
		int var2 = 0;

		for(int var3 = 0; var3 < this.loadedEntityList.size(); ++var3) {
			Entity var4 = (Entity)this.loadedEntityList.get(var3);
			if((!(var4 instanceof EntityLiving) || !((EntityLiving)var4).func_104002_bU()) && var1.isAssignableFrom(var4.getClass())) {
				++var2;
			}
		}

		return var2;
	}

	public void addLoadedEntities(List var1) {
		this.loadedEntityList.addAll(var1);

		for(int var2 = 0; var2 < var1.size(); ++var2) {
			this.obtainEntitySkin((Entity)var1.get(var2));
		}

	}

	public void unloadEntities(List var1) {
		this.unloadedEntityList.addAll(var1);
	}

	public boolean canPlaceEntityOnSide(int var1, int var2, int var3, int var4, boolean var5, int var6, Entity var7, ItemStack var8) {
		int var9 = this.getBlockId(var2, var3, var4);
		Block var10 = Block.blocksList[var9];
		Block var11 = Block.blocksList[var1];
		AxisAlignedBB var12 = var11.getCollisionBoundingBoxFromPool(this, var2, var3, var4);
		if(var5) {
			var12 = null;
		}

		if(var12 != null && !this.checkNoEntityCollision(var12, var7)) {
			return false;
		} else {
			if(var10 != null && (var10 == Block.waterMoving || var10 == Block.waterStill || var10 == Block.lavaMoving || var10 == Block.lavaStill || var10 == Block.fire || var10.blockMaterial.isReplaceable())) {
				var10 = null;
			}

			return var10 != null && var10.blockMaterial == Material.circuits && var11 == Block.anvil ? true : var1 > 0 && var10 == null && var11.canPlaceBlockOnSide(this, var2, var3, var4, var6, var8);
		}
	}

	public PathEntity getPathEntityToEntity(Entity var1, Entity var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7) {
		this.theProfiler.startSection("pathfind");
		int var8 = MathHelper.floor_double(var1.posX);
		int var9 = MathHelper.floor_double(var1.posY + 1.0D);
		int var10 = MathHelper.floor_double(var1.posZ);
		int var11 = (int)(var3 + 16.0F);
		int var12 = var8 - var11;
		int var13 = var9 - var11;
		int var14 = var10 - var11;
		int var15 = var8 + var11;
		int var16 = var9 + var11;
		int var17 = var10 + var11;
		ChunkCache var18 = new ChunkCache(this, var12, var13, var14, var15, var16, var17, 0);
		PathEntity var19 = (new PathFinder(var18, var4, var5, var6, var7)).createEntityPathTo(var1, var2, var3);
		this.theProfiler.endSection();
		return var19;
	}

	public PathEntity getEntityPathToXYZ(Entity var1, int var2, int var3, int var4, float var5, boolean var6, boolean var7, boolean var8, boolean var9) {
		this.theProfiler.startSection("pathfind");
		int var10 = MathHelper.floor_double(var1.posX);
		int var11 = MathHelper.floor_double(var1.posY);
		int var12 = MathHelper.floor_double(var1.posZ);
		int var13 = (int)(var5 + 8.0F);
		int var14 = var10 - var13;
		int var15 = var11 - var13;
		int var16 = var12 - var13;
		int var17 = var10 + var13;
		int var18 = var11 + var13;
		int var19 = var12 + var13;
		ChunkCache var20 = new ChunkCache(this, var14, var15, var16, var17, var18, var19, 0);
		PathEntity var21 = (new PathFinder(var20, var6, var7, var8, var9)).createEntityPathTo(var1, var2, var3, var4, var5);
		this.theProfiler.endSection();
		return var21;
	}

	public int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4) {
		int var5 = this.getBlockId(var1, var2, var3);
		return var5 == 0 ? 0 : Block.blocksList[var5].isProvidingStrongPower(this, var1, var2, var3, var4);
	}

	public int getBlockPowerInput(int var1, int var2, int var3) {
		byte var4 = 0;
		int var5 = Math.max(var4, this.isBlockProvidingPowerTo(var1, var2 - 1, var3, 0));
		if(var5 >= 15) {
			return var5;
		} else {
			var5 = Math.max(var5, this.isBlockProvidingPowerTo(var1, var2 + 1, var3, 1));
			if(var5 >= 15) {
				return var5;
			} else {
				var5 = Math.max(var5, this.isBlockProvidingPowerTo(var1, var2, var3 - 1, 2));
				if(var5 >= 15) {
					return var5;
				} else {
					var5 = Math.max(var5, this.isBlockProvidingPowerTo(var1, var2, var3 + 1, 3));
					if(var5 >= 15) {
						return var5;
					} else {
						var5 = Math.max(var5, this.isBlockProvidingPowerTo(var1 - 1, var2, var3, 4));
						if(var5 >= 15) {
							return var5;
						} else {
							var5 = Math.max(var5, this.isBlockProvidingPowerTo(var1 + 1, var2, var3, 5));
							return var5 >= 15 ? var5 : var5;
						}
					}
				}
			}
		}
	}

	public boolean getIndirectPowerOutput(int var1, int var2, int var3, int var4) {
		return this.getIndirectPowerLevelTo(var1, var2, var3, var4) > 0;
	}

	public int getIndirectPowerLevelTo(int var1, int var2, int var3, int var4) {
		if(this.isBlockNormalCube(var1, var2, var3)) {
			return this.getBlockPowerInput(var1, var2, var3);
		} else {
			int var5 = this.getBlockId(var1, var2, var3);
			return var5 == 0 ? 0 : Block.blocksList[var5].isProvidingWeakPower(this, var1, var2, var3, var4);
		}
	}

	public boolean isBlockIndirectlyGettingPowered(int var1, int var2, int var3) {
		return this.getIndirectPowerLevelTo(var1, var2 - 1, var3, 0) > 0 ? true : (this.getIndirectPowerLevelTo(var1, var2 + 1, var3, 1) > 0 ? true : (this.getIndirectPowerLevelTo(var1, var2, var3 - 1, 2) > 0 ? true : (this.getIndirectPowerLevelTo(var1, var2, var3 + 1, 3) > 0 ? true : (this.getIndirectPowerLevelTo(var1 - 1, var2, var3, 4) > 0 ? true : this.getIndirectPowerLevelTo(var1 + 1, var2, var3, 5) > 0))));
	}

	public int getStrongestIndirectPower(int var1, int var2, int var3) {
		int var4 = 0;

		for(int var5 = 0; var5 < 6; ++var5) {
			int var6 = this.getIndirectPowerLevelTo(var1 + Facing.offsetsXForSide[var5], var2 + Facing.offsetsYForSide[var5], var3 + Facing.offsetsZForSide[var5], var5);
			if(var6 >= 15) {
				return 15;
			}

			if(var6 > var4) {
				var4 = var6;
			}
		}

		return var4;
	}

	public EntityPlayer getClosestPlayerToEntity(Entity var1, double var2) {
		return this.getClosestPlayer(var1.posX, var1.posY, var1.posZ, var2);
	}

	public EntityPlayer getClosestPlayer(double var1, double var3, double var5, double var7) {
		double var9 = -1.0D;
		EntityPlayer var11 = null;

		for(int var12 = 0; var12 < this.playerEntities.size(); ++var12) {
			EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);
			double var14 = var13.getDistanceSq(var1, var3, var5);
			if((var7 < 0.0D || var14 < var7 * var7) && (var9 == -1.0D || var14 < var9)) {
				var9 = var14;
				var11 = var13;
			}
		}

		return var11;
	}

	public EntityPlayer getClosestVulnerablePlayerToEntity(Entity var1, double var2) {
		return this.getClosestVulnerablePlayer(var1.posX, var1.posY, var1.posZ, var2);
	}

	public EntityPlayer getClosestVulnerablePlayer(double var1, double var3, double var5, double var7) {
		double var9 = -1.0D;
		EntityPlayer var11 = null;

		for(int var12 = 0; var12 < this.playerEntities.size(); ++var12) {
			EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);
			if(!var13.capabilities.disableDamage && var13.isEntityAlive()) {
				double var14 = var13.getDistanceSq(var1, var3, var5);
				double var16 = var7;
				if(var13.isSneaking()) {
					var16 = var7 * (double)0.8F;
				}

				if(var13.isInvisible()) {
					float var18 = var13.func_82243_bO();
					if(var18 < 0.1F) {
						var18 = 0.1F;
					}

					var16 *= (double)(0.7F * var18);
				}

				if((var7 < 0.0D || var14 < var16 * var16) && (var9 == -1.0D || var14 < var9)) {
					var9 = var14;
					var11 = var13;
				}
			}
		}

		return var11;
	}

	public EntityPlayer getPlayerEntityByName(String var1) {
		for(int var2 = 0; var2 < this.playerEntities.size(); ++var2) {
			if(var1.equals(((EntityPlayer)this.playerEntities.get(var2)).username)) {
				return (EntityPlayer)this.playerEntities.get(var2);
			}
		}

		return null;
	}

	public void sendQuittingDisconnectingPacket() {
	}

	public void checkSessionLock() throws MinecraftException {
		this.saveHandler.checkSessionLock();
	}

	public void func_82738_a(long var1) {
		this.worldInfo.incrementTotalWorldTime(var1);
	}

	public long getSeed() {
		return this.worldInfo.getSeed();
	}

	public long getTotalWorldTime() {
		return this.worldInfo.getWorldTotalTime();
	}

	public long getWorldTime() {
		return this.worldInfo.getWorldTime();
	}

	public void setWorldTime(long var1) {
		this.worldInfo.setWorldTime(var1);
	}

	public ChunkCoordinates getSpawnPoint() {
		return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
	}

	public void setSpawnLocation(int var1, int var2, int var3) {
		this.worldInfo.setSpawnPosition(var1, var2, var3);
	}

	public void joinEntityInSurroundings(Entity var1) {
		int var2 = MathHelper.floor_double(var1.posX / 16.0D);
		int var3 = MathHelper.floor_double(var1.posZ / 16.0D);
		byte var4 = 2;

		for(int var5 = var2 - var4; var5 <= var2 + var4; ++var5) {
			for(int var6 = var3 - var4; var6 <= var3 + var4; ++var6) {
				this.getChunkFromChunkCoords(var5, var6);
			}
		}

		if(!this.loadedEntityList.contains(var1)) {
			this.loadedEntityList.add(var1);
		}

	}

	public boolean canMineBlock(EntityPlayer var1, int var2, int var3, int var4) {
		return true;
	}

	public void setEntityState(Entity var1, byte var2) {
	}

	public IChunkProvider getChunkProvider() {
		return this.chunkProvider;
	}

	public void addBlockEvent(int var1, int var2, int var3, int var4, int var5, int var6) {
		if(var4 > 0) {
			Block.blocksList[var4].onBlockEventReceived(this, var1, var2, var3, var5, var6);
		}

	}

	public ISaveHandler getSaveHandler() {
		return this.saveHandler;
	}

	public WorldInfo getWorldInfo() {
		return this.worldInfo;
	}

	public GameRules getGameRules() {
		return this.worldInfo.getGameRulesInstance();
	}

	public void updateAllPlayersSleepingFlag() {
	}

	public float getWeightedThunderStrength(float var1) {
		return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * var1) * this.getRainStrength(var1);
	}

	public float getRainStrength(float var1) {
		return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * var1;
	}

	public void setRainStrength(float var1) {
		this.prevRainingStrength = var1;
		this.rainingStrength = var1;
	}

	public boolean isThundering() {
		return (double)this.getWeightedThunderStrength(1.0F) > 0.9D;
	}

	public boolean isRaining() {
		return (double)this.getRainStrength(1.0F) > 0.2D;
	}

	public boolean canLightningStrikeAt(int var1, int var2, int var3) {
		if(!this.isRaining()) {
			return false;
		} else if(!this.canBlockSeeTheSky(var1, var2, var3)) {
			return false;
		} else if(this.getPrecipitationHeight(var1, var3) > var2) {
			return false;
		} else {
			BiomeGenBase var4 = this.getBiomeGenForCoords(var1, var3);
			return var4.getEnableSnow() ? false : var4.canSpawnLightningBolt();
		}
	}

	public boolean isBlockHighHumidity(int var1, int var2, int var3) {
		BiomeGenBase var4 = this.getBiomeGenForCoords(var1, var3);
		return var4.isHighHumidity();
	}

	public void setItemData(String var1, WorldSavedData var2) {
		this.mapStorage.setData(var1, var2);
	}

	public WorldSavedData loadItemData(Class var1, String var2) {
		return this.mapStorage.loadData(var1, var2);
	}

	public int getUniqueDataId(String var1) {
		return this.mapStorage.getUniqueDataId(var1);
	}

	public void func_82739_e(int var1, int var2, int var3, int var4, int var5) {
		for(int var6 = 0; var6 < this.worldAccesses.size(); ++var6) {
			((IWorldAccess)this.worldAccesses.get(var6)).broadcastSound(var1, var2, var3, var4, var5);
		}

	}

	public void playAuxSFX(int var1, int var2, int var3, int var4, int var5) {
		this.playAuxSFXAtEntity((EntityPlayer)null, var1, var2, var3, var4, var5);
	}

	public void playAuxSFXAtEntity(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
		try {
			for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
				((IWorldAccess)this.worldAccesses.get(var7)).playAuxSFX(var1, var2, var3, var4, var5, var6);
			}

		} catch (Throwable var10) {
			CrashReport var8 = CrashReport.makeCrashReport(var10, "Playing level event");
			CrashReportCategory var9 = var8.makeCategory("Level event being played");
			var9.addCrashSection("Block coordinates", CrashReportCategory.getLocationInfo(var3, var4, var5));
			var9.addCrashSection("Event source", var1);
			var9.addCrashSection("Event type", Integer.valueOf(var2));
			var9.addCrashSection("Event data", Integer.valueOf(var6));
			throw new ReportedException(var8);
		}
	}

	public int getHeight() {
		return 256;
	}

	public int getActualHeight() {
		return this.provider.hasNoSky ? 128 : 256;
	}

	public IUpdatePlayerListBox func_82735_a(EntityMinecart var1) {
		return null;
	}

	public Random setRandomSeed(int var1, int var2, int var3) {
		long var4 = (long)var1 * 341873128712L + (long)var2 * 132897987541L + this.getWorldInfo().getSeed() + (long)var3;
		this.rand.setSeed(var4);
		return this.rand;
	}

	public ChunkPosition findClosestStructure(String var1, int var2, int var3, int var4) {
		return this.getChunkProvider().findClosestStructure(this, var1, var2, var3, var4);
	}

	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	public double getHorizon() {
		return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0D : 63.0D;
	}

	public CrashReportCategory addWorldInfoToCrashReport(CrashReport var1) {
		CrashReportCategory var2 = var1.makeCategoryDepth("Affected level", 1);
		var2.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
		var2.addCrashSectionCallable("All players", new CallableLvl2(this));
		var2.addCrashSectionCallable("Chunk stats", new CallableLvl3(this));

		try {
			this.worldInfo.addToCrashReport(var2);
		} catch (Throwable var4) {
			var2.addCrashSectionThrowable("Level Data Unobtainable", var4);
		}

		return var2;
	}

	public void destroyBlockInWorldPartially(int var1, int var2, int var3, int var4, int var5) {
		for(int var6 = 0; var6 < this.worldAccesses.size(); ++var6) {
			IWorldAccess var7 = (IWorldAccess)this.worldAccesses.get(var6);
			var7.destroyBlockPartially(var1, var2, var3, var4, var5);
		}

	}

	public Vec3Pool getWorldVec3Pool() {
		return this.vecPool;
	}

	public Calendar getCurrentDate() {
		if(this.getTotalWorldTime() % 600L == 0L) {
			this.theCalendar.setTimeInMillis(System.currentTimeMillis());
		}

		return this.theCalendar;
	}

	public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
	}

	public Scoreboard getScoreboard() {
		return this.worldScoreboard;
	}

	public void func_96440_m(int var1, int var2, int var3, int var4) {
		for(int var5 = 0; var5 < 4; ++var5) {
			int var6 = var1 + Direction.offsetX[var5];
			int var7 = var3 + Direction.offsetZ[var5];
			int var8 = this.getBlockId(var6, var2, var7);
			if(var8 != 0) {
				Block var9 = Block.blocksList[var8];
				if(Block.redstoneComparatorIdle.func_94487_f(var8)) {
					var9.onNeighborBlockChange(this, var6, var2, var7, var4);
				} else if(Block.isNormalCube(var8)) {
					var6 += Direction.offsetX[var5];
					var7 += Direction.offsetZ[var5];
					var8 = this.getBlockId(var6, var2, var7);
					var9 = Block.blocksList[var8];
					if(Block.redstoneComparatorIdle.func_94487_f(var8)) {
						var9.onNeighborBlockChange(this, var6, var2, var7, var4);
					}
				}
			}
		}

	}

	public ILogAgent getWorldLogAgent() {
		return this.worldLogAgent;
	}
}
