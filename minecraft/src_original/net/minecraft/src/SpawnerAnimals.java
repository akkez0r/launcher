package net.minecraft.src;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class SpawnerAnimals {
	private static HashMap eligibleChunksForSpawning = new HashMap();
	protected static final Class[] nightSpawnEntities = new Class[]{EntitySpider.class, EntityZombie.class, EntitySkeleton.class};

	protected static ChunkPosition getRandomSpawningPointInChunk(World var0, int var1, int var2) {
		Chunk var3 = var0.getChunkFromChunkCoords(var1, var2);
		int var4 = var1 * 16 + var0.rand.nextInt(16);
		int var5 = var2 * 16 + var0.rand.nextInt(16);
		int var6 = var0.rand.nextInt(var3 == null ? var0.getActualHeight() : var3.getTopFilledSegment() + 16 - 1);
		return new ChunkPosition(var4, var6, var5);
	}

	public static final int findChunksForSpawning(WorldServer var0, boolean var1, boolean var2, boolean var3) {
		if(!var1 && !var2) {
			return 0;
		} else {
			eligibleChunksForSpawning.clear();

			int var4;
			int var7;
			for(var4 = 0; var4 < var0.playerEntities.size(); ++var4) {
				EntityPlayer var5 = (EntityPlayer)var0.playerEntities.get(var4);
				int var6 = MathHelper.floor_double(var5.posX / 16.0D);
				var7 = MathHelper.floor_double(var5.posZ / 16.0D);
				byte var8 = 8;

				for(int var9 = -var8; var9 <= var8; ++var9) {
					for(int var10 = -var8; var10 <= var8; ++var10) {
						boolean var11 = var9 == -var8 || var9 == var8 || var10 == -var8 || var10 == var8;
						ChunkCoordIntPair var12 = new ChunkCoordIntPair(var9 + var6, var10 + var7);
						if(!var11) {
							eligibleChunksForSpawning.put(var12, Boolean.valueOf(false));
						} else if(!eligibleChunksForSpawning.containsKey(var12)) {
							eligibleChunksForSpawning.put(var12, Boolean.valueOf(true));
						}
					}
				}
			}

			var4 = 0;
			ChunkCoordinates var32 = var0.getSpawnPoint();
			EnumCreatureType[] var33 = EnumCreatureType.values();
			var7 = var33.length;

			label131:
			for(int var34 = 0; var34 < var7; ++var34) {
				EnumCreatureType var35 = var33[var34];
				if((!var35.getPeacefulCreature() || var2) && (var35.getPeacefulCreature() || var1) && (!var35.getAnimal() || var3) && var0.countEntities(var35.getCreatureClass()) <= var35.getMaxNumberOfCreature() * eligibleChunksForSpawning.size() / 256) {
					Iterator var36 = eligibleChunksForSpawning.keySet().iterator();

					label128:
					while(true) {
						int var13;
						int var14;
						int var15;
						do {
							do {
								ChunkCoordIntPair var37;
								do {
									if(!var36.hasNext()) {
										continue label131;
									}

									var37 = (ChunkCoordIntPair)var36.next();
								} while(((Boolean)eligibleChunksForSpawning.get(var37)).booleanValue());

								ChunkPosition var38 = getRandomSpawningPointInChunk(var0, var37.chunkXPos, var37.chunkZPos);
								var13 = var38.x;
								var14 = var38.y;
								var15 = var38.z;
							} while(var0.isBlockNormalCube(var13, var14, var15));
						} while(var0.getBlockMaterial(var13, var14, var15) != var35.getCreatureMaterial());

						int var16 = 0;

						for(int var17 = 0; var17 < 3; ++var17) {
							int var18 = var13;
							int var19 = var14;
							int var20 = var15;
							byte var21 = 6;
							SpawnListEntry var22 = null;

							for(int var23 = 0; var23 < 4; ++var23) {
								var18 += var0.rand.nextInt(var21) - var0.rand.nextInt(var21);
								var19 += var0.rand.nextInt(1) - var0.rand.nextInt(1);
								var20 += var0.rand.nextInt(var21) - var0.rand.nextInt(var21);
								if(canCreatureTypeSpawnAtLocation(var35, var0, var18, var19, var20)) {
									float var24 = (float)var18 + 0.5F;
									float var25 = (float)var19;
									float var26 = (float)var20 + 0.5F;
									if(var0.getClosestPlayer((double)var24, (double)var25, (double)var26, 24.0D) == null) {
										float var27 = var24 - (float)var32.posX;
										float var28 = var25 - (float)var32.posY;
										float var29 = var26 - (float)var32.posZ;
										float var30 = var27 * var27 + var28 * var28 + var29 * var29;
										if(var30 >= 576.0F) {
											if(var22 == null) {
												var22 = var0.spawnRandomCreature(var35, var18, var19, var20);
												if(var22 == null) {
													break;
												}
											}

											EntityLiving var39;
											try {
												var39 = (EntityLiving)var22.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{var0});
											} catch (Exception var31) {
												var31.printStackTrace();
												return var4;
											}

											var39.setLocationAndAngles((double)var24, (double)var25, (double)var26, var0.rand.nextFloat() * 360.0F, 0.0F);
											if(var39.getCanSpawnHere()) {
												++var16;
												var0.spawnEntityInWorld(var39);
												creatureSpecificInit(var39, var0, var24, var25, var26);
												if(var16 >= var39.getMaxSpawnedInChunk()) {
													continue label128;
												}
											}

											var4 += var16;
										}
									}
								}
							}
						}
					}
				}
			}

			return var4;
		}
	}

	public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType var0, World var1, int var2, int var3, int var4) {
		if(var0.getCreatureMaterial() == Material.water) {
			return var1.getBlockMaterial(var2, var3, var4).isLiquid() && var1.getBlockMaterial(var2, var3 - 1, var4).isLiquid() && !var1.isBlockNormalCube(var2, var3 + 1, var4);
		} else if(!var1.doesBlockHaveSolidTopSurface(var2, var3 - 1, var4)) {
			return false;
		} else {
			int var5 = var1.getBlockId(var2, var3 - 1, var4);
			return var5 != Block.bedrock.blockID && !var1.isBlockNormalCube(var2, var3, var4) && !var1.getBlockMaterial(var2, var3, var4).isLiquid() && !var1.isBlockNormalCube(var2, var3 + 1, var4);
		}
	}

	private static void creatureSpecificInit(EntityLiving var0, World var1, float var2, float var3, float var4) {
		var0.initCreature();
	}

	public static void performWorldGenSpawning(World var0, BiomeGenBase var1, int var2, int var3, int var4, int var5, Random var6) {
		List var7 = var1.getSpawnableList(EnumCreatureType.creature);
		if(!var7.isEmpty()) {
			while(var6.nextFloat() < var1.getSpawningChance()) {
				SpawnListEntry var8 = (SpawnListEntry)WeightedRandom.getRandomItem(var0.rand, (Collection)var7);
				int var9 = var8.minGroupCount + var6.nextInt(1 + var8.maxGroupCount - var8.minGroupCount);
				int var10 = var2 + var6.nextInt(var4);
				int var11 = var3 + var6.nextInt(var5);
				int var12 = var10;
				int var13 = var11;

				for(int var14 = 0; var14 < var9; ++var14) {
					boolean var15 = false;

					for(int var16 = 0; !var15 && var16 < 4; ++var16) {
						int var17 = var0.getTopSolidOrLiquidBlock(var10, var11);
						if(canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, var0, var10, var17, var11)) {
							float var18 = (float)var10 + 0.5F;
							float var19 = (float)var17;
							float var20 = (float)var11 + 0.5F;

							EntityLiving var21;
							try {
								var21 = (EntityLiving)var8.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{var0});
							} catch (Exception var23) {
								var23.printStackTrace();
								continue;
							}

							var21.setLocationAndAngles((double)var18, (double)var19, (double)var20, var6.nextFloat() * 360.0F, 0.0F);
							var0.spawnEntityInWorld(var21);
							creatureSpecificInit(var21, var0, var18, var19, var20);
							var15 = true;
						}

						var10 += var6.nextInt(5) - var6.nextInt(5);

						for(var11 += var6.nextInt(5) - var6.nextInt(5); var10 < var2 || var10 >= var2 + var4 || var11 < var3 || var11 >= var3 + var4; var11 = var13 + var6.nextInt(5) - var6.nextInt(5)) {
							var10 = var12 + var6.nextInt(5) - var6.nextInt(5);
						}
					}
				}
			}

		}
	}
}
