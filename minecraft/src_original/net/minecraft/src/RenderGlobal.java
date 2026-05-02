package net.minecraft.src;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class RenderGlobal implements IWorldAccess {
	public List tileEntities = new ArrayList();
	private WorldClient theWorld;
	private final RenderEngine renderEngine;
	private List worldRenderersToUpdate = new ArrayList();
	private WorldRenderer[] sortedWorldRenderers;
	private WorldRenderer[] worldRenderers;
	private int renderChunksWide;
	private int renderChunksTall;
	private int renderChunksDeep;
	private int glRenderListBase;
	private Minecraft mc;
	private RenderBlocks globalRenderBlocks;
	private IntBuffer glOcclusionQueryBase;
	private boolean occlusionEnabled = false;
	private int cloudTickCounter = 0;
	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	private int minBlockX;
	private int minBlockY;
	private int minBlockZ;
	private int maxBlockX;
	private int maxBlockY;
	private int maxBlockZ;
	private Map damagedBlocks = new HashMap();
	private Icon[] destroyBlockIcons;
	private int renderDistance = -1;
	private int renderEntitiesStartupCounter = 2;
	private int countEntitiesTotal;
	private int countEntitiesRendered;
	private int countEntitiesHidden;
	int[] dummyBuf50k = new int['\uc350'];
	IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
	private int renderersLoaded;
	private int renderersBeingClipped;
	private int renderersBeingOccluded;
	private int renderersBeingRendered;
	private int renderersSkippingRenderPass;
	private int dummyRenderInt;
	private int worldRenderersCheckIndex;
	private List glRenderLists = new ArrayList();
	private RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
	double prevSortX = -9999.0D;
	double prevSortY = -9999.0D;
	double prevSortZ = -9999.0D;
	int frustumCheckOffset = 0;

	public RenderGlobal(Minecraft var1, RenderEngine var2) {
		this.mc = var1;
		this.renderEngine = var2;
		byte var3 = 34;
		byte var4 = 32;
		this.glRenderListBase = GLAllocation.generateDisplayLists(var3 * var3 * var4 * 3);
		this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();
		if(this.occlusionEnabled) {
			this.occlusionResult.clear();
			this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var3 * var3 * var4);
			this.glOcclusionQueryBase.clear();
			this.glOcclusionQueryBase.position(0);
			this.glOcclusionQueryBase.limit(var3 * var3 * var4);
			ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
		}

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator var5 = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte var7 = 64;
		int var8 = 256 / var7 + 2;
		float var6 = 16.0F;

		int var9;
		int var10;
		for(var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7) {
			for(var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7) {
				var5.startDrawingQuads();
				var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + 0));
				var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + 0));
				var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + var7));
				var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + var7));
				var5.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		var6 = -16.0F;
		var5.startDrawingQuads();

		for(var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7) {
			for(var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7) {
				var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + 0));
				var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + 0));
				var5.addVertex((double)(var9 + 0), (double)var6, (double)(var10 + var7));
				var5.addVertex((double)(var9 + var7), (double)var6, (double)(var10 + var7));
			}
		}

		var5.draw();
		GL11.glEndList();
	}

	private void renderStars() {
		Random var1 = new Random(10842L);
		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();

		for(int var3 = 0; var3 < 1500; ++var3) {
			double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
			double var10 = (double)(0.15F + var1.nextFloat() * 0.1F);
			double var12 = var4 * var4 + var6 * var6 + var8 * var8;
			if(var12 < 1.0D && var12 > 0.01D) {
				var12 = 1.0D / Math.sqrt(var12);
				var4 *= var12;
				var6 *= var12;
				var8 *= var12;
				double var14 = var4 * 100.0D;
				double var16 = var6 * 100.0D;
				double var18 = var8 * 100.0D;
				double var20 = Math.atan2(var4, var8);
				double var22 = Math.sin(var20);
				double var24 = Math.cos(var20);
				double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
				double var28 = Math.sin(var26);
				double var30 = Math.cos(var26);
				double var32 = var1.nextDouble() * Math.PI * 2.0D;
				double var34 = Math.sin(var32);
				double var36 = Math.cos(var32);

				for(int var38 = 0; var38 < 4; ++var38) {
					double var39 = 0.0D;
					double var41 = (double)((var38 & 2) - 1) * var10;
					double var43 = (double)((var38 + 1 & 2) - 1) * var10;
					double var47 = var41 * var36 - var43 * var34;
					double var49 = var43 * var36 + var41 * var34;
					double var53 = var47 * var28 + var39 * var30;
					double var55 = var39 * var28 - var47 * var30;
					double var57 = var55 * var22 - var49 * var24;
					double var61 = var49 * var22 + var55 * var24;
					var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
				}
			}
		}

		var2.draw();
	}

	public void setWorldAndLoadRenderers(WorldClient var1) {
		if(this.theWorld != null) {
			this.theWorld.removeWorldAccess(this);
		}

		this.prevSortX = -9999.0D;
		this.prevSortY = -9999.0D;
		this.prevSortZ = -9999.0D;
		RenderManager.instance.set(var1);
		this.theWorld = var1;
		this.globalRenderBlocks = new RenderBlocks(var1);
		if(var1 != null) {
			var1.addWorldAccess(this);
			this.loadRenderers();
		}

	}

	public void loadRenderers() {
		if(this.theWorld != null) {
			Block.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
			this.renderDistance = this.mc.gameSettings.renderDistance;
			int var1;
			if(this.worldRenderers != null) {
				for(var1 = 0; var1 < this.worldRenderers.length; ++var1) {
					this.worldRenderers[var1].stopRendering();
				}
			}

			var1 = 64 << 3 - this.renderDistance;
			if(var1 > 400) {
				var1 = 400;
			}

			this.renderChunksWide = var1 / 16 + 1;
			this.renderChunksTall = 16;
			this.renderChunksDeep = var1 / 16 + 1;
			this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
			this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
			int var2 = 0;
			int var3 = 0;
			this.minBlockX = 0;
			this.minBlockY = 0;
			this.minBlockZ = 0;
			this.maxBlockX = this.renderChunksWide;
			this.maxBlockY = this.renderChunksTall;
			this.maxBlockZ = this.renderChunksDeep;

			int var4;
			for(var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4) {
				((WorldRenderer)this.worldRenderersToUpdate.get(var4)).needsUpdate = false;
			}

			this.worldRenderersToUpdate.clear();
			this.tileEntities.clear();

			for(var4 = 0; var4 < this.renderChunksWide; ++var4) {
				for(int var5 = 0; var5 < this.renderChunksTall; ++var5) {
					for(int var6 = 0; var6 < this.renderChunksDeep; ++var6) {
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = new WorldRenderer(this.theWorld, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, this.glRenderListBase + var2);
						if(this.occlusionEnabled) {
							this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
						}

						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isWaitingOnOcclusionQuery = false;
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isVisible = true;
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isInFrustum = true;
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].chunkIndex = var3++;
						this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].markDirty();
						this.sortedWorldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4];
						this.worldRenderersToUpdate.add(this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4]);
						var2 += 3;
					}
				}
			}

			if(this.theWorld != null) {
				EntityLiving var7 = this.mc.renderViewEntity;
				if(var7 != null) {
					this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
					Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
				}
			}

			this.renderEntitiesStartupCounter = 2;
		}
	}

	public void renderEntities(Vec3 var1, ICamera var2, float var3) {
		if(this.renderEntitiesStartupCounter > 0) {
			--this.renderEntitiesStartupCounter;
		} else {
			this.theWorld.theProfiler.startSection("prepare");
			TileEntityRenderer.instance.cacheActiveRenderInfo(this.theWorld, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, var3);
			RenderManager.instance.cacheActiveRenderInfo(this.theWorld, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.pointedEntityLiving, this.mc.gameSettings, var3);
			this.countEntitiesTotal = 0;
			this.countEntitiesRendered = 0;
			this.countEntitiesHidden = 0;
			EntityLiving var4 = this.mc.renderViewEntity;
			RenderManager.renderPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)var3;
			RenderManager.renderPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)var3;
			RenderManager.renderPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)var3;
			TileEntityRenderer.staticPlayerX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)var3;
			TileEntityRenderer.staticPlayerY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)var3;
			TileEntityRenderer.staticPlayerZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)var3;
			this.mc.entityRenderer.enableLightmap((double)var3);
			this.theWorld.theProfiler.endStartSection("global");
			List var5 = this.theWorld.getLoadedEntityList();
			this.countEntitiesTotal = var5.size();

			int var6;
			Entity var7;
			for(var6 = 0; var6 < this.theWorld.weatherEffects.size(); ++var6) {
				var7 = (Entity)this.theWorld.weatherEffects.get(var6);
				++this.countEntitiesRendered;
				if(var7.isInRangeToRenderVec3D(var1)) {
					RenderManager.instance.renderEntity(var7, var3);
				}
			}

			this.theWorld.theProfiler.endStartSection("entities");

			for(var6 = 0; var6 < var5.size(); ++var6) {
				var7 = (Entity)var5.get(var6);
				if(var7.isInRangeToRenderVec3D(var1) && (var7.ignoreFrustumCheck || var2.isBoundingBoxInFrustum(var7.boundingBox) || var7.riddenByEntity == this.mc.thePlayer) && (var7 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping()) && this.theWorld.blockExists(MathHelper.floor_double(var7.posX), 0, MathHelper.floor_double(var7.posZ))) {
					++this.countEntitiesRendered;
					RenderManager.instance.renderEntity(var7, var3);
				}
			}

			this.theWorld.theProfiler.endStartSection("tileentities");
			RenderHelper.enableStandardItemLighting();

			for(var6 = 0; var6 < this.tileEntities.size(); ++var6) {
				TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(var6), var3);
			}

			this.mc.entityRenderer.disableLightmap((double)var3);
			this.theWorld.theProfiler.endSection();
		}
	}

	public String getDebugInfoRenders() {
		return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
	}

	public String getDebugInfoEntities() {
		return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
	}

	private void markRenderersForNewPosition(int var1, int var2, int var3) {
		var1 -= 8;
		var2 -= 8;
		var3 -= 8;
		this.minBlockX = Integer.MAX_VALUE;
		this.minBlockY = Integer.MAX_VALUE;
		this.minBlockZ = Integer.MAX_VALUE;
		this.maxBlockX = Integer.MIN_VALUE;
		this.maxBlockY = Integer.MIN_VALUE;
		this.maxBlockZ = Integer.MIN_VALUE;
		int var4 = this.renderChunksWide * 16;
		int var5 = var4 / 2;

		for(int var6 = 0; var6 < this.renderChunksWide; ++var6) {
			int var7 = var6 * 16;
			int var8 = var7 + var5 - var1;
			if(var8 < 0) {
				var8 -= var4 - 1;
			}

			var8 /= var4;
			var7 -= var8 * var4;
			if(var7 < this.minBlockX) {
				this.minBlockX = var7;
			}

			if(var7 > this.maxBlockX) {
				this.maxBlockX = var7;
			}

			for(int var9 = 0; var9 < this.renderChunksDeep; ++var9) {
				int var10 = var9 * 16;
				int var11 = var10 + var5 - var3;
				if(var11 < 0) {
					var11 -= var4 - 1;
				}

				var11 /= var4;
				var10 -= var11 * var4;
				if(var10 < this.minBlockZ) {
					this.minBlockZ = var10;
				}

				if(var10 > this.maxBlockZ) {
					this.maxBlockZ = var10;
				}

				for(int var12 = 0; var12 < this.renderChunksTall; ++var12) {
					int var13 = var12 * 16;
					if(var13 < this.minBlockY) {
						this.minBlockY = var13;
					}

					if(var13 > this.maxBlockY) {
						this.maxBlockY = var13;
					}

					WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
					boolean var15 = var14.needsUpdate;
					var14.setPosition(var7, var13, var10);
					if(!var15 && var14.needsUpdate) {
						this.worldRenderersToUpdate.add(var14);
					}
				}
			}
		}

	}

	public int sortAndRender(EntityLiving var1, int var2, double var3) {
		this.theWorld.theProfiler.startSection("sortchunks");

		for(int var5 = 0; var5 < 10; ++var5) {
			this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
			WorldRenderer var6 = this.worldRenderers[this.worldRenderersCheckIndex];
			if(var6.needsUpdate && !this.worldRenderersToUpdate.contains(var6)) {
				this.worldRenderersToUpdate.add(var6);
			}
		}

		if(this.mc.gameSettings.renderDistance != this.renderDistance) {
			this.loadRenderers();
		}

		if(var2 == 0) {
			this.renderersLoaded = 0;
			this.dummyRenderInt = 0;
			this.renderersBeingClipped = 0;
			this.renderersBeingOccluded = 0;
			this.renderersBeingRendered = 0;
			this.renderersSkippingRenderPass = 0;
		}

		double var33 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var3;
		double var7 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var3;
		double var9 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var3;
		double var11 = var1.posX - this.prevSortX;
		double var13 = var1.posY - this.prevSortY;
		double var15 = var1.posZ - this.prevSortZ;
		if(var11 * var11 + var13 * var13 + var15 * var15 > 16.0D) {
			this.prevSortX = var1.posX;
			this.prevSortY = var1.posY;
			this.prevSortZ = var1.posZ;
			this.markRenderersForNewPosition(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ));
			Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var1));
		}

		RenderHelper.disableStandardItemLighting();
		byte var17 = 0;
		int var34;
		if(this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && var2 == 0) {
			byte var18 = 0;
			int var19 = 16;
			this.checkOcclusionQueryResult(var18, var19);

			for(int var20 = var18; var20 < var19; ++var20) {
				this.sortedWorldRenderers[var20].isVisible = true;
			}

			this.theWorld.theProfiler.endStartSection("render");
			var34 = var17 + this.renderSortedRenderers(var18, var19, var2, var3);

			do {
				this.theWorld.theProfiler.endStartSection("occ");
				int var35 = var19;
				var19 *= 2;
				if(var19 > this.sortedWorldRenderers.length) {
					var19 = this.sortedWorldRenderers.length;
				}

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_FOG);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				this.theWorld.theProfiler.startSection("check");
				this.checkOcclusionQueryResult(var35, var19);
				this.theWorld.theProfiler.endSection();
				GL11.glPushMatrix();
				float var36 = 0.0F;
				float var21 = 0.0F;
				float var22 = 0.0F;

				for(int var23 = var35; var23 < var19; ++var23) {
					if(this.sortedWorldRenderers[var23].skipAllRenderPasses()) {
						this.sortedWorldRenderers[var23].isInFrustum = false;
					} else {
						if(!this.sortedWorldRenderers[var23].isInFrustum) {
							this.sortedWorldRenderers[var23].isVisible = true;
						}

						if(this.sortedWorldRenderers[var23].isInFrustum && !this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery) {
							float var24 = MathHelper.sqrt_float(this.sortedWorldRenderers[var23].distanceToEntitySquared(var1));
							int var25 = (int)(1.0F + var24 / 128.0F);
							if(this.cloudTickCounter % var25 == var23 % var25) {
								WorldRenderer var26 = this.sortedWorldRenderers[var23];
								float var27 = (float)((double)var26.posXMinus - var33);
								float var28 = (float)((double)var26.posYMinus - var7);
								float var29 = (float)((double)var26.posZMinus - var9);
								float var30 = var27 - var36;
								float var31 = var28 - var21;
								float var32 = var29 - var22;
								if(var30 != 0.0F || var31 != 0.0F || var32 != 0.0F) {
									GL11.glTranslatef(var30, var31, var32);
									var36 += var30;
									var21 += var31;
									var22 += var32;
								}

								this.theWorld.theProfiler.startSection("bb");
								ARBOcclusionQuery.glBeginQueryARB(GL15.GL_SAMPLES_PASSED, this.sortedWorldRenderers[var23].glOcclusionQuery);
								this.sortedWorldRenderers[var23].callOcclusionQueryList();
								ARBOcclusionQuery.glEndQueryARB(GL15.GL_SAMPLES_PASSED);
								this.theWorld.theProfiler.endSection();
								this.sortedWorldRenderers[var23].isWaitingOnOcclusionQuery = true;
							}
						}
					}
				}

				GL11.glPopMatrix();
				if(this.mc.gameSettings.anaglyph) {
					if(EntityRenderer.anaglyphField == 0) {
						GL11.glColorMask(false, true, true, true);
					} else {
						GL11.glColorMask(true, false, false, true);
					}
				} else {
					GL11.glColorMask(true, true, true, true);
				}

				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_FOG);
				this.theWorld.theProfiler.endStartSection("render");
				var34 += this.renderSortedRenderers(var35, var19, var2, var3);
			} while(var19 < this.sortedWorldRenderers.length);
		} else {
			this.theWorld.theProfiler.endStartSection("render");
			var34 = var17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, var2, var3);
		}

		this.theWorld.theProfiler.endSection();
		return var34;
	}

	private void checkOcclusionQueryResult(int var1, int var2) {
		for(int var3 = var1; var3 < var2; ++var3) {
			if(this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery) {
				this.occlusionResult.clear();
				ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, GL15.GL_QUERY_RESULT_AVAILABLE, this.occlusionResult);
				if(this.occlusionResult.get(0) != 0) {
					this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery = false;
					this.occlusionResult.clear();
					ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, GL15.GL_QUERY_RESULT, this.occlusionResult);
					this.sortedWorldRenderers[var3].isVisible = this.occlusionResult.get(0) != 0;
				}
			}
		}

	}

	private int renderSortedRenderers(int var1, int var2, int var3, double var4) {
		this.glRenderLists.clear();
		int var6 = 0;

		for(int var7 = var1; var7 < var2; ++var7) {
			if(var3 == 0) {
				++this.renderersLoaded;
				if(this.sortedWorldRenderers[var7].skipRenderPass[var3]) {
					++this.renderersSkippingRenderPass;
				} else if(!this.sortedWorldRenderers[var7].isInFrustum) {
					++this.renderersBeingClipped;
				} else if(this.occlusionEnabled && !this.sortedWorldRenderers[var7].isVisible) {
					++this.renderersBeingOccluded;
				} else {
					++this.renderersBeingRendered;
				}
			}

			if(!this.sortedWorldRenderers[var7].skipRenderPass[var3] && this.sortedWorldRenderers[var7].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[var7].isVisible)) {
				int var8 = this.sortedWorldRenderers[var7].getGLCallListForPass(var3);
				if(var8 >= 0) {
					this.glRenderLists.add(this.sortedWorldRenderers[var7]);
					++var6;
				}
			}
		}

		EntityLiving var19 = this.mc.renderViewEntity;
		double var20 = var19.lastTickPosX + (var19.posX - var19.lastTickPosX) * var4;
		double var10 = var19.lastTickPosY + (var19.posY - var19.lastTickPosY) * var4;
		double var12 = var19.lastTickPosZ + (var19.posZ - var19.lastTickPosZ) * var4;
		int var14 = 0;

		int var15;
		for(var15 = 0; var15 < this.allRenderLists.length; ++var15) {
			this.allRenderLists[var15].func_78421_b();
		}

		for(var15 = 0; var15 < this.glRenderLists.size(); ++var15) {
			WorldRenderer var16 = (WorldRenderer)this.glRenderLists.get(var15);
			int var17 = -1;

			for(int var18 = 0; var18 < var14; ++var18) {
				if(this.allRenderLists[var18].func_78418_a(var16.posXMinus, var16.posYMinus, var16.posZMinus)) {
					var17 = var18;
				}
			}

			if(var17 < 0) {
				var17 = var14++;
				this.allRenderLists[var17].func_78422_a(var16.posXMinus, var16.posYMinus, var16.posZMinus, var20, var10, var12);
			}

			this.allRenderLists[var17].func_78420_a(var16.getGLCallListForPass(var3));
		}

		this.renderAllRenderLists(var3, var4);
		return var6;
	}

	public void renderAllRenderLists(int var1, double var2) {
		this.mc.entityRenderer.enableLightmap(var2);

		for(int var4 = 0; var4 < this.allRenderLists.length; ++var4) {
			this.allRenderLists[var4].func_78419_a();
		}

		this.mc.entityRenderer.disableLightmap(var2);
	}

	public void updateClouds() {
		++this.cloudTickCounter;
		if(this.cloudTickCounter % 20 == 0) {
			Iterator var1 = this.damagedBlocks.values().iterator();

			while(var1.hasNext()) {
				DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
				int var3 = var2.getCreationCloudUpdateTick();
				if(this.cloudTickCounter - var3 > 400) {
					var1.remove();
				}
			}
		}

	}

	public void renderSky(float var1) {
		if(this.mc.theWorld.provider.dimensionId == 1) {
			GL11.glDisable(GL11.GL_FOG);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);
			this.renderEngine.bindTexture("/misc/tunnel.png");
			Tessellator var21 = Tessellator.instance;

			for(int var22 = 0; var22 < 6; ++var22) {
				GL11.glPushMatrix();
				if(var22 == 1) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if(var22 == 2) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				if(var22 == 3) {
					GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				}

				if(var22 == 4) {
					GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				}

				if(var22 == 5) {
					GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				}

				var21.startDrawingQuads();
				var21.setColorOpaque_I(2631720);
				var21.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
				var21.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
				var21.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
				var21.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
				var21.draw();
				GL11.glPopMatrix();
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		} else if(this.mc.theWorld.provider.isSurfaceWorld()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			Vec3 var2 = this.theWorld.getSkyColor(this.mc.renderViewEntity, var1);
			float var3 = (float)var2.xCoord;
			float var4 = (float)var2.yCoord;
			float var5 = (float)var2.zCoord;
			float var8;
			if(this.mc.gameSettings.anaglyph) {
				float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
				float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
				var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
				var3 = var6;
				var4 = var7;
				var5 = var8;
			}

			GL11.glColor3f(var3, var4, var5);
			Tessellator var23 = Tessellator.instance;
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glColor3f(var3, var4, var5);
			GL11.glCallList(this.glSkyList);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			float[] var24 = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(var1), var1);
			float var9;
			float var10;
			float var11;
			float var12;
			if(var24 != null) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glShadeModel(GL11.GL_SMOOTH);
				GL11.glPushMatrix();
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(MathHelper.sin(this.theWorld.getCelestialAngleRadians(var1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				var8 = var24[0];
				var9 = var24[1];
				var10 = var24[2];
				float var13;
				if(this.mc.gameSettings.anaglyph) {
					var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
					var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
					var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
					var8 = var11;
					var9 = var12;
					var10 = var13;
				}

				var23.startDrawing(6);
				var23.setColorRGBA_F(var8, var9, var10, var24[3]);
				var23.addVertex(0.0D, 100.0D, 0.0D);
				byte var26 = 16;
				var23.setColorRGBA_F(var24[0], var24[1], var24[2], 0.0F);

				for(int var27 = 0; var27 <= var26; ++var27) {
					var13 = (float)var27 * (float)Math.PI * 2.0F / (float)var26;
					float var14 = MathHelper.sin(var13);
					float var15 = MathHelper.cos(var13);
					var23.addVertex((double)(var14 * 120.0F), (double)(var15 * 120.0F), (double)(-var15 * 40.0F * var24[3]));
				}

				var23.draw();
				GL11.glPopMatrix();
				GL11.glShadeModel(GL11.GL_FLAT);
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glPushMatrix();
			var8 = 1.0F - this.theWorld.getRainStrength(var1);
			var9 = 0.0F;
			var10 = 0.0F;
			var11 = 0.0F;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
			GL11.glTranslatef(var9, var10, var11);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.theWorld.getCelestialAngle(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
			var12 = 30.0F;
			this.renderEngine.bindTexture("/environment/sun.png");
			var23.startDrawingQuads();
			var23.addVertexWithUV((double)(-var12), 100.0D, (double)(-var12), 0.0D, 0.0D);
			var23.addVertexWithUV((double)var12, 100.0D, (double)(-var12), 1.0D, 0.0D);
			var23.addVertexWithUV((double)var12, 100.0D, (double)var12, 1.0D, 1.0D);
			var23.addVertexWithUV((double)(-var12), 100.0D, (double)var12, 0.0D, 1.0D);
			var23.draw();
			var12 = 20.0F;
			this.renderEngine.bindTexture("/environment/moon_phases.png");
			int var28 = this.theWorld.getMoonPhase();
			int var29 = var28 % 4;
			int var30 = var28 / 4 % 2;
			float var16 = (float)(var29 + 0) / 4.0F;
			float var17 = (float)(var30 + 0) / 2.0F;
			float var18 = (float)(var29 + 1) / 4.0F;
			float var19 = (float)(var30 + 1) / 2.0F;
			var23.startDrawingQuads();
			var23.addVertexWithUV((double)(-var12), -100.0D, (double)var12, (double)var18, (double)var19);
			var23.addVertexWithUV((double)var12, -100.0D, (double)var12, (double)var16, (double)var19);
			var23.addVertexWithUV((double)var12, -100.0D, (double)(-var12), (double)var16, (double)var17);
			var23.addVertexWithUV((double)(-var12), -100.0D, (double)(-var12), (double)var18, (double)var17);
			var23.draw();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			float var20 = this.theWorld.getStarBrightness(var1) * var8;
			if(var20 > 0.0F) {
				GL11.glColor4f(var20, var20, var20, var20);
				GL11.glCallList(this.starGLCallList);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor3f(0.0F, 0.0F, 0.0F);
			double var25 = this.mc.thePlayer.getPosition(var1).yCoord - this.theWorld.getHorizon();
			if(var25 < 0.0D) {
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 12.0F, 0.0F);
				GL11.glCallList(this.glSkyList2);
				GL11.glPopMatrix();
				var10 = 1.0F;
				var11 = -((float)(var25 + 65.0D));
				var12 = -var10;
				var23.startDrawingQuads();
				var23.setColorRGBA_I(0, 255);
				var23.addVertex((double)(-var10), (double)var11, (double)var10);
				var23.addVertex((double)var10, (double)var11, (double)var10);
				var23.addVertex((double)var10, (double)var12, (double)var10);
				var23.addVertex((double)(-var10), (double)var12, (double)var10);
				var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
				var23.addVertex((double)var10, (double)var12, (double)(-var10));
				var23.addVertex((double)var10, (double)var11, (double)(-var10));
				var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
				var23.addVertex((double)var10, (double)var12, (double)(-var10));
				var23.addVertex((double)var10, (double)var12, (double)var10);
				var23.addVertex((double)var10, (double)var11, (double)var10);
				var23.addVertex((double)var10, (double)var11, (double)(-var10));
				var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
				var23.addVertex((double)(-var10), (double)var11, (double)var10);
				var23.addVertex((double)(-var10), (double)var12, (double)var10);
				var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
				var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
				var23.addVertex((double)(-var10), (double)var12, (double)var10);
				var23.addVertex((double)var10, (double)var12, (double)var10);
				var23.addVertex((double)var10, (double)var12, (double)(-var10));
				var23.draw();
			}

			if(this.theWorld.provider.isSkyColored()) {
				GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
			} else {
				GL11.glColor3f(var3, var4, var5);
			}

			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, -((float)(var25 - 16.0D)), 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
		}
	}

	public void renderClouds(float var1) {
		if(this.mc.theWorld.provider.isSurfaceWorld()) {
			if(this.mc.gameSettings.fancyGraphics) {
				this.renderCloudsFancy(var1);
			} else {
				GL11.glDisable(GL11.GL_CULL_FACE);
				float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)var1);
				byte var3 = 32;
				int var4 = 256 / var3;
				Tessellator var5 = Tessellator.instance;
				this.renderEngine.bindTexture("/environment/clouds.png");
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Vec3 var6 = this.theWorld.getCloudColour(var1);
				float var7 = (float)var6.xCoord;
				float var8 = (float)var6.yCoord;
				float var9 = (float)var6.zCoord;
				float var10;
				if(this.mc.gameSettings.anaglyph) {
					var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
					float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
					float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
					var7 = var10;
					var8 = var11;
					var9 = var12;
				}

				var10 = 0.5F / 1024.0F;
				double var24 = (double)((float)this.cloudTickCounter + var1);
				double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)var1 + var24 * (double)0.03F;
				double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)var1;
				int var17 = MathHelper.floor_double(var13 / 2048.0D);
				int var18 = MathHelper.floor_double(var15 / 2048.0D);
				var13 -= (double)(var17 * 2048);
				var15 -= (double)(var18 * 2048);
				float var19 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
				float var20 = (float)(var13 * (double)var10);
				float var21 = (float)(var15 * (double)var10);
				var5.startDrawingQuads();
				var5.setColorRGBA_F(var7, var8, var9, 0.8F);

				for(int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3) {
					for(int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3) {
						var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + var3), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
						var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + var3), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
						var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
						var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
					}
				}

				var5.draw();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
		}
	}

	public boolean hasCloudFog(double var1, double var3, double var5, float var7) {
		return false;
	}

	public void renderCloudsFancy(float var1) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)var1);
		Tessellator var3 = Tessellator.instance;
		float var4 = 12.0F;
		float var5 = 4.0F;
		double var6 = (double)((float)this.cloudTickCounter + var1);
		double var8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)var1 + var6 * (double)0.03F) / (double)var4;
		double var10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)var1) / (double)var4 + (double)0.33F;
		float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
		int var13 = MathHelper.floor_double(var8 / 2048.0D);
		int var14 = MathHelper.floor_double(var10 / 2048.0D);
		var8 -= (double)(var13 * 2048);
		var10 -= (double)(var14 * 2048);
		this.renderEngine.bindTexture("/environment/clouds.png");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Vec3 var15 = this.theWorld.getCloudColour(var1);
		float var16 = (float)var15.xCoord;
		float var17 = (float)var15.yCoord;
		float var18 = (float)var15.zCoord;
		float var19;
		float var20;
		float var21;
		if(this.mc.gameSettings.anaglyph) {
			var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
			var20 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
			var21 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
			var16 = var19;
			var17 = var20;
			var18 = var21;
		}

		var19 = (float)(var8 * 0.0D);
		var20 = (float)(var10 * 0.0D);
		var21 = 0.00390625F;
		var19 = (float)MathHelper.floor_double(var8) * var21;
		var20 = (float)MathHelper.floor_double(var10) * var21;
		float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
		float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
		byte var24 = 8;
		byte var25 = 4;
		float var26 = 1.0F / 1024.0F;
		GL11.glScalef(var4, 1.0F, var4);

		for(int var27 = 0; var27 < 2; ++var27) {
			if(var27 == 0) {
				GL11.glColorMask(false, false, false, false);
			} else if(this.mc.gameSettings.anaglyph) {
				if(EntityRenderer.anaglyphField == 0) {
					GL11.glColorMask(false, true, true, true);
				} else {
					GL11.glColorMask(true, false, false, true);
				}
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for(int var28 = -var25 + 1; var28 <= var25; ++var28) {
				for(int var29 = -var25 + 1; var29 <= var25; ++var29) {
					var3.startDrawingQuads();
					float var30 = (float)(var28 * var24);
					float var31 = (float)(var29 * var24);
					float var32 = var30 - var22;
					float var33 = var31 - var23;
					if(var12 > -var5 - 1.0F) {
						var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
						var3.setNormal(0.0F, -1.0F, 0.0F);
						var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
					}

					if(var12 <= var5 + 1.0F) {
						var3.setColorRGBA_F(var16, var17, var18, 0.8F);
						var3.setNormal(0.0F, 1.0F, 0.0F);
						var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
						var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
					}

					var3.setColorRGBA_F(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
					int var34;
					if(var28 > -1) {
						var3.setNormal(-1.0F, 0.0F, 0.0F);

						for(var34 = 0; var34 < var24; ++var34) {
							var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
						}
					}

					if(var28 <= 1) {
						var3.setNormal(1.0F, 0.0F, 0.0F);

						for(var34 = 0; var34 < var24; ++var34) {
							var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
						}
					}

					var3.setColorRGBA_F(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);
					if(var29 > -1) {
						var3.setNormal(0.0F, 0.0F, -1.0F);

						for(var34 = 0; var34 < var24; ++var34) {
							var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
						}
					}

					if(var29 <= 1) {
						var3.setNormal(0.0F, 0.0F, 1.0F);

						for(var34 = 0; var34 < var24; ++var34) {
							var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
							var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
						}
					}

					var3.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public boolean updateRenderers(EntityLiving var1, boolean var2) {
		byte var3 = 2;
		RenderSorter var4 = new RenderSorter(var1);
		WorldRenderer[] var5 = new WorldRenderer[var3];
		ArrayList var6 = null;
		int var7 = this.worldRenderersToUpdate.size();
		int var8 = 0;
		this.theWorld.theProfiler.startSection("nearChunksSearch");

		int var9;
		WorldRenderer var10;
		int var11;
		int var12;
		label140:
		for(var9 = 0; var9 < var7; ++var9) {
			var10 = (WorldRenderer)this.worldRenderersToUpdate.get(var9);
			if(var10 != null) {
				if(!var2) {
					if(var10.distanceToEntitySquared(var1) > 256.0F) {
						for(var11 = 0; var11 < var3 && (var5[var11] == null || var4.doCompare(var5[var11], var10) <= 0); ++var11) {
						}

						--var11;
						if(var11 <= 0) {
							continue;
						}

						var12 = var11;

						while(true) {
							--var12;
							if(var12 == 0) {
								var5[var11] = var10;
								continue label140;
							}

							var5[var12 - 1] = var5[var12];
						}
					}
				} else if(!var10.isInFrustum) {
					continue;
				}

				if(var6 == null) {
					var6 = new ArrayList();
				}

				++var8;
				var6.add(var10);
				this.worldRenderersToUpdate.set(var9, (Object)null);
			}
		}

		this.theWorld.theProfiler.endSection();
		this.theWorld.theProfiler.startSection("sort");
		if(var6 != null) {
			if(var6.size() > 1) {
				Collections.sort(var6, var4);
			}

			for(var9 = var6.size() - 1; var9 >= 0; --var9) {
				var10 = (WorldRenderer)var6.get(var9);
				var10.updateRenderer();
				var10.needsUpdate = false;
			}
		}

		this.theWorld.theProfiler.endSection();
		var9 = 0;
		this.theWorld.theProfiler.startSection("rebuild");

		int var16;
		for(var16 = var3 - 1; var16 >= 0; --var16) {
			WorldRenderer var17 = var5[var16];
			if(var17 != null) {
				if(!var17.isInFrustum && var16 != var3 - 1) {
					var5[var16] = null;
					var5[0] = null;
					break;
				}

				var5[var16].updateRenderer();
				var5[var16].needsUpdate = false;
				++var9;
			}
		}

		this.theWorld.theProfiler.endSection();
		this.theWorld.theProfiler.startSection("cleanup");
		var16 = 0;
		var11 = 0;

		for(var12 = this.worldRenderersToUpdate.size(); var16 != var12; ++var16) {
			WorldRenderer var13 = (WorldRenderer)this.worldRenderersToUpdate.get(var16);
			if(var13 != null) {
				boolean var14 = false;

				for(int var15 = 0; var15 < var3 && !var14; ++var15) {
					if(var13 == var5[var15]) {
						var14 = true;
					}
				}

				if(!var14) {
					if(var11 != var16) {
						this.worldRenderersToUpdate.set(var11, var13);
					}

					++var11;
				}
			}
		}

		this.theWorld.theProfiler.endSection();
		this.theWorld.theProfiler.startSection("trim");

		while(true) {
			--var16;
			if(var16 < var11) {
				this.theWorld.theProfiler.endSection();
				return var7 == var8 + var9;
			}

			this.worldRenderersToUpdate.remove(var16);
		}
	}

	public void drawBlockBreaking(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
		Tessellator var6 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)Minecraft.getSystemTime() / 100.0F) * 0.2F + 0.4F) * 0.5F);
		if(var3 != 0 && var4 != null) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float var7 = MathHelper.sin((float)Minecraft.getSystemTime() / 100.0F) * 0.2F + 0.8F;
			GL11.glColor4f(var7, var7, var7, MathHelper.sin((float)Minecraft.getSystemTime() / 200.0F) * 0.2F + 0.5F);
			this.renderEngine.bindTexture("/terrain.png");
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	public void drawBlockDamageTexture(Tessellator var1, EntityPlayer var2, float var3) {
		double var4 = var2.lastTickPosX + (var2.posX - var2.lastTickPosX) * (double)var3;
		double var6 = var2.lastTickPosY + (var2.posY - var2.lastTickPosY) * (double)var3;
		double var8 = var2.lastTickPosZ + (var2.posZ - var2.lastTickPosZ) * (double)var3;
		if(!this.damagedBlocks.isEmpty()) {
			GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
			this.renderEngine.bindTexture("/terrain.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glPolygonOffset(-3.0F, -3.0F);
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			var1.startDrawingQuads();
			var1.setTranslation(-var4, -var6, -var8);
			var1.disableColor();
			Iterator var10 = this.damagedBlocks.values().iterator();

			while(var10.hasNext()) {
				DestroyBlockProgress var11 = (DestroyBlockProgress)var10.next();
				double var12 = (double)var11.getPartialBlockX() - var4;
				double var14 = (double)var11.getPartialBlockY() - var6;
				double var16 = (double)var11.getPartialBlockZ() - var8;
				if(var12 * var12 + var14 * var14 + var16 * var16 > 1024.0D) {
					var10.remove();
				} else {
					int var18 = this.theWorld.getBlockId(var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ());
					Block var19 = var18 > 0 ? Block.blocksList[var18] : null;
					if(var19 == null) {
						var19 = Block.stone;
					}

					this.globalRenderBlocks.renderBlockUsingTexture(var19, var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ(), this.destroyBlockIcons[var11.getPartialBlockDamage()]);
				}
			}

			var1.draw();
			var1.setTranslation(0.0D, 0.0D, 0.0D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glPolygonOffset(0.0F, 0.0F);
			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(true);
			GL11.glPopMatrix();
		}

	}

	public void drawSelectionBox(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
		if(var3 == 0 && var2.typeOfHit == EnumMovingObjectType.TILE) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			float var6 = 0.002F;
			int var7 = this.theWorld.getBlockId(var2.blockX, var2.blockY, var2.blockZ);
			if(var7 > 0) {
				Block.blocksList[var7].setBlockBoundsBasedOnState(this.theWorld, var2.blockX, var2.blockY, var2.blockZ);
				double var8 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var5;
				double var10 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var5;
				double var12 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var5;
				this.drawOutlinedBoundingBox(Block.blocksList[var7].getSelectedBoundingBoxFromPool(this.theWorld, var2.blockX, var2.blockY, var2.blockZ).expand((double)var6, (double)var6, (double)var6).getOffsetBoundingBox(-var8, -var10, -var12));
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}

	}

	private void drawOutlinedBoundingBox(AxisAlignedBB var1) {
		Tessellator var2 = Tessellator.instance;
		var2.startDrawing(3);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.draw();
		var2.startDrawing(3);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.draw();
		var2.startDrawing(1);
		var2.addVertex(var1.minX, var1.minY, var1.minZ);
		var2.addVertex(var1.minX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.minZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
		var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
		var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
		var2.addVertex(var1.minX, var1.minY, var1.maxZ);
		var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
		var2.draw();
	}

	public void markBlocksForUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = MathHelper.bucketInt(var1, 16);
		int var8 = MathHelper.bucketInt(var2, 16);
		int var9 = MathHelper.bucketInt(var3, 16);
		int var10 = MathHelper.bucketInt(var4, 16);
		int var11 = MathHelper.bucketInt(var5, 16);
		int var12 = MathHelper.bucketInt(var6, 16);

		for(int var13 = var7; var13 <= var10; ++var13) {
			int var14 = var13 % this.renderChunksWide;
			if(var14 < 0) {
				var14 += this.renderChunksWide;
			}

			for(int var15 = var8; var15 <= var11; ++var15) {
				int var16 = var15 % this.renderChunksTall;
				if(var16 < 0) {
					var16 += this.renderChunksTall;
				}

				for(int var17 = var9; var17 <= var12; ++var17) {
					int var18 = var17 % this.renderChunksDeep;
					if(var18 < 0) {
						var18 += this.renderChunksDeep;
					}

					int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
					WorldRenderer var20 = this.worldRenderers[var19];
					if(var20 != null && !var20.needsUpdate) {
						this.worldRenderersToUpdate.add(var20);
						var20.markDirty();
					}
				}
			}
		}

	}

	public void markBlockForUpdate(int var1, int var2, int var3) {
		this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
	}

	public void markBlockForRenderUpdate(int var1, int var2, int var3) {
		this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
	}

	public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
		this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
	}

	public void clipRenderersByFrustum(ICamera var1, float var2) {
		for(int var3 = 0; var3 < this.worldRenderers.length; ++var3) {
			if(!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustumCheckOffset & 15) == 0)) {
				this.worldRenderers[var3].updateInFrustum(var1);
			}
		}

		++this.frustumCheckOffset;
	}

	public void playRecord(String var1, int var2, int var3, int var4) {
		ItemRecord var5 = ItemRecord.getRecord(var1);
		if(var1 != null && var5 != null) {
			this.mc.ingameGUI.setRecordPlayingMessage(var5.getRecordTitle());
		}

		this.mc.sndManager.playStreaming(var1, (float)var2, (float)var3, (float)var4);
	}

	public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
	}

	public void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
	}

	public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		try {
			this.doSpawnParticle(var1, var2, var4, var6, var8, var10, var12);
		} catch (Throwable var17) {
			CrashReport var15 = CrashReport.makeCrashReport(var17, "Exception while adding particle");
			CrashReportCategory var16 = var15.makeCategory("Particle being added");
			var16.addCrashSection("Name", var1);
			var16.addCrashSectionCallable("Position", new CallableParticlePositionInfo(this, var2, var4, var6));
			throw new ReportedException(var15);
		}
	}

	public EntityFX doSpawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		if(this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
			int var14 = this.mc.gameSettings.particleSetting;
			if(var14 == 1 && this.theWorld.rand.nextInt(3) == 0) {
				var14 = 2;
			}

			double var15 = this.mc.renderViewEntity.posX - var2;
			double var17 = this.mc.renderViewEntity.posY - var4;
			double var19 = this.mc.renderViewEntity.posZ - var6;
			Object var21 = null;
			EffectRenderer var10000;
			if(var1.equals("hugeexplosion")) {
				var10000 = this.mc.effectRenderer;
				var21 = new EntityHugeExplodeFX(this.theWorld, var2, var4, var6, var8, var10, var12);
				var10000.addEffect((EntityFX)var21);
			} else if(var1.equals("largeexplode")) {
				var10000 = this.mc.effectRenderer;
				var21 = new EntityLargeExplodeFX(this.renderEngine, this.theWorld, var2, var4, var6, var8, var10, var12);
				var10000.addEffect((EntityFX)var21);
			} else if(var1.equals("fireworksSpark")) {
				var10000 = this.mc.effectRenderer;
				var21 = new EntityFireworkSparkFX(this.theWorld, var2, var4, var6, var8, var10, var12, this.mc.effectRenderer);
				var10000.addEffect((EntityFX)var21);
			}

			if(var21 != null) {
				return (EntityFX)var21;
			} else {
				double var22 = 16.0D;
				if(var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22) {
					return null;
				} else if(var14 > 1) {
					return null;
				} else {
					if(var1.equals("bubble")) {
						var21 = new EntityBubbleFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("suspended")) {
						var21 = new EntitySuspendFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("depthsuspend")) {
						var21 = new EntityAuraFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("townaura")) {
						var21 = new EntityAuraFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("crit")) {
						var21 = new EntityCritFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("magicCrit")) {
						var21 = new EntityCritFX(this.theWorld, var2, var4, var6, var8, var10, var12);
						((EntityFX)var21).setRBGColorF(((EntityFX)var21).getRedColorF() * 0.3F, ((EntityFX)var21).getGreenColorF() * 0.8F, ((EntityFX)var21).getBlueColorF());
						((EntityFX)var21).nextTextureIndexX();
					} else if(var1.equals("smoke")) {
						var21 = new EntitySmokeFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("mobSpell")) {
						var21 = new EntitySpellParticleFX(this.theWorld, var2, var4, var6, 0.0D, 0.0D, 0.0D);
						((EntityFX)var21).setRBGColorF((float)var8, (float)var10, (float)var12);
					} else if(var1.equals("mobSpellAmbient")) {
						var21 = new EntitySpellParticleFX(this.theWorld, var2, var4, var6, 0.0D, 0.0D, 0.0D);
						((EntityFX)var21).setAlphaF(0.15F);
						((EntityFX)var21).setRBGColorF((float)var8, (float)var10, (float)var12);
					} else if(var1.equals("spell")) {
						var21 = new EntitySpellParticleFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("instantSpell")) {
						var21 = new EntitySpellParticleFX(this.theWorld, var2, var4, var6, var8, var10, var12);
						((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
					} else if(var1.equals("witchMagic")) {
						var21 = new EntitySpellParticleFX(this.theWorld, var2, var4, var6, var8, var10, var12);
						((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
						float var24 = this.theWorld.rand.nextFloat() * 0.5F + 0.35F;
						((EntityFX)var21).setRBGColorF(1.0F * var24, 0.0F * var24, 1.0F * var24);
					} else if(var1.equals("note")) {
						var21 = new EntityNoteFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("portal")) {
						var21 = new EntityPortalFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("enchantmenttable")) {
						var21 = new EntityEnchantmentTableParticleFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("explode")) {
						var21 = new EntityExplodeFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("flame")) {
						var21 = new EntityFlameFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("lava")) {
						var21 = new EntityLavaFX(this.theWorld, var2, var4, var6);
					} else if(var1.equals("footstep")) {
						var21 = new EntityFootStepFX(this.renderEngine, this.theWorld, var2, var4, var6);
					} else if(var1.equals("splash")) {
						var21 = new EntitySplashFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("largesmoke")) {
						var21 = new EntitySmokeFX(this.theWorld, var2, var4, var6, var8, var10, var12, 2.5F);
					} else if(var1.equals("cloud")) {
						var21 = new EntityCloudFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("reddust")) {
						var21 = new EntityReddustFX(this.theWorld, var2, var4, var6, (float)var8, (float)var10, (float)var12);
					} else if(var1.equals("snowballpoof")) {
						var21 = new EntityBreakingFX(this.theWorld, var2, var4, var6, Item.snowball, this.renderEngine);
					} else if(var1.equals("dripWater")) {
						var21 = new EntityDropParticleFX(this.theWorld, var2, var4, var6, Material.water);
					} else if(var1.equals("dripLava")) {
						var21 = new EntityDropParticleFX(this.theWorld, var2, var4, var6, Material.lava);
					} else if(var1.equals("snowshovel")) {
						var21 = new EntitySnowShovelFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("slime")) {
						var21 = new EntityBreakingFX(this.theWorld, var2, var4, var6, Item.slimeBall, this.renderEngine);
					} else if(var1.equals("heart")) {
						var21 = new EntityHeartFX(this.theWorld, var2, var4, var6, var8, var10, var12);
					} else if(var1.equals("angryVillager")) {
						var21 = new EntityHeartFX(this.theWorld, var2, var4 + 0.5D, var6, var8, var10, var12);
						((EntityFX)var21).setParticleTextureIndex(81);
						((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
					} else if(var1.equals("happyVillager")) {
						var21 = new EntityAuraFX(this.theWorld, var2, var4, var6, var8, var10, var12);
						((EntityFX)var21).setParticleTextureIndex(82);
						((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
					} else if(var1.startsWith("iconcrack_")) {
						int var27 = Integer.parseInt(var1.substring(var1.indexOf("_") + 1));
						var21 = new EntityBreakingFX(this.theWorld, var2, var4, var6, var8, var10, var12, Item.itemsList[var27], this.renderEngine);
					} else if(var1.startsWith("tilecrack_")) {
						String[] var28 = var1.split("_", 3);
						int var25 = Integer.parseInt(var28[1]);
						int var26 = Integer.parseInt(var28[2]);
						var21 = (new EntityDiggingFX(this.theWorld, var2, var4, var6, var8, var10, var12, Block.blocksList[var25], 0, var26, this.renderEngine)).applyRenderColor(var26);
					}

					if(var21 != null) {
						this.mc.effectRenderer.addEffect((EntityFX)var21);
					}

					return (EntityFX)var21;
				}
			}
		} else {
			return null;
		}
	}

	public void onEntityCreate(Entity var1) {
		var1.updateCloak();
		if(var1.skinUrl != null) {
			this.renderEngine.obtainImageData(var1.skinUrl, new ImageBufferDownload());
		}

		if(var1.cloakUrl != null) {
			this.renderEngine.obtainImageData(var1.cloakUrl, new ImageBufferDownload());
		}

	}

	public void onEntityDestroy(Entity var1) {
		if(var1.skinUrl != null) {
			this.renderEngine.releaseImageData(var1.skinUrl);
		}

		if(var1.cloakUrl != null) {
			this.renderEngine.releaseImageData(var1.cloakUrl);
		}

	}

	public void deleteAllDisplayLists() {
		GLAllocation.deleteDisplayLists(this.glRenderListBase);
	}

	public void broadcastSound(int var1, int var2, int var3, int var4, int var5) {
		Random var6 = this.theWorld.rand;
		switch(var1) {
		case 1013:
		case 1018:
			if(this.mc.renderViewEntity != null) {
				double var7 = (double)var2 - this.mc.renderViewEntity.posX;
				double var9 = (double)var3 - this.mc.renderViewEntity.posY;
				double var11 = (double)var4 - this.mc.renderViewEntity.posZ;
				double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
				double var15 = this.mc.renderViewEntity.posX;
				double var17 = this.mc.renderViewEntity.posY;
				double var19 = this.mc.renderViewEntity.posZ;
				if(var13 > 0.0D) {
					var15 += var7 / var13 * 2.0D;
					var17 += var9 / var13 * 2.0D;
					var19 += var11 / var13 * 2.0D;
				}

				if(var1 == 1013) {
					this.theWorld.playSound(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
				} else if(var1 == 1018) {
					this.theWorld.playSound(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
				}
			}
		default:
		}
	}

	public void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
		Random var7 = this.theWorld.rand;
		double var8;
		double var10;
		double var12;
		String var14;
		int var15;
		int var20;
		double var23;
		double var25;
		double var27;
		double var29;
		double var39;
		switch(var2) {
		case 1000:
			this.theWorld.playSound((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.0F, false);
			break;
		case 1001:
			this.theWorld.playSound((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.2F, false);
			break;
		case 1002:
			this.theWorld.playSound((double)var3, (double)var4, (double)var5, "random.bow", 1.0F, 1.2F, false);
			break;
		case 1003:
			if(Math.random() < 0.5D) {
				this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			} else {
				this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			}
			break;
		case 1004:
			this.theWorld.playSound((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F, false);
			break;
		case 1005:
			if(Item.itemsList[var6] instanceof ItemRecord) {
				this.theWorld.playRecord(((ItemRecord)Item.itemsList[var6]).recordName, var3, var4, var5);
			} else {
				this.theWorld.playRecord((String)null, var3, var4, var5);
			}
			break;
		case 1007:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.ghast.charge", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1008:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.ghast.fireball", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1009:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.ghast.fireball", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1010:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.zombie.wood", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1011:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.zombie.metal", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1012:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.zombie.woodbreak", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1014:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.wither.shoot", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1015:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.bat.takeoff", 0.05F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1016:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.zombie.infect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1017:
			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "mob.zombie.unfect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1020:
			this.theWorld.playSound((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1021:
			this.theWorld.playSound((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1022:
			this.theWorld.playSound((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 2000:
			int var33 = var6 % 3 - 1;
			int var9 = var6 / 3 % 3 - 1;
			var10 = (double)var3 + (double)var33 * 0.6D + 0.5D;
			var12 = (double)var4 + 0.5D;
			double var34 = (double)var5 + (double)var9 * 0.6D + 0.5D;

			for(int var36 = 0; var36 < 10; ++var36) {
				double var37 = var7.nextDouble() * 0.2D + 0.01D;
				double var38 = var10 + (double)var33 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var9 * 0.5D;
				var39 = var12 + (var7.nextDouble() - 0.5D) * 0.5D;
				var23 = var34 + (double)var9 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var33 * 0.5D;
				var25 = (double)var33 * var37 + var7.nextGaussian() * 0.01D;
				var27 = -0.03D + var7.nextGaussian() * 0.01D;
				var29 = (double)var9 * var37 + var7.nextGaussian() * 0.01D;
				this.spawnParticle("smoke", var38, var39, var23, var25, var27, var29);
			}

			return;
		case 2001:
			var20 = var6 & 4095;
			if(var20 > 0) {
				Block var40 = Block.blocksList[var20];
				this.mc.sndManager.playSound(var40.stepSound.getBreakSound(), (float)var3 + 0.5F, (float)var4 + 0.5F, (float)var5 + 0.5F, (var40.stepSound.getVolume() + 1.0F) / 2.0F, var40.stepSound.getPitch() * 0.8F);
			}

			this.mc.effectRenderer.addBlockDestroyEffects(var3, var4, var5, var6 & 4095, var6 >> 12 & 255);
			break;
		case 2002:
			var8 = (double)var3;
			var10 = (double)var4;
			var12 = (double)var5;
			var14 = "iconcrack_" + Item.potion.itemID;

			for(var15 = 0; var15 < 8; ++var15) {
				this.spawnParticle(var14, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
			}

			var15 = Item.potion.getColorFromDamage(var6);
			float var16 = (float)(var15 >> 16 & 255) / 255.0F;
			float var17 = (float)(var15 >> 8 & 255) / 255.0F;
			float var18 = (float)(var15 >> 0 & 255) / 255.0F;
			String var19 = "spell";
			if(Item.potion.isEffectInstant(var6)) {
				var19 = "instantSpell";
			}

			for(var20 = 0; var20 < 100; ++var20) {
				var39 = var7.nextDouble() * 4.0D;
				var23 = var7.nextDouble() * Math.PI * 2.0D;
				var25 = Math.cos(var23) * var39;
				var27 = 0.01D + var7.nextDouble() * 0.5D;
				var29 = Math.sin(var23) * var39;
				EntityFX var31 = this.doSpawnParticle(var19, var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if(var31 != null) {
					float var32 = 12.0F / 16.0F + var7.nextFloat() * 0.25F;
					var31.setRBGColorF(var16 * var32, var17 * var32, var18 * var32);
					var31.multiplyVelocity((float)var39);
				}
			}

			this.theWorld.playSound((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D, "random.glass", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 2003:
			var8 = (double)var3 + 0.5D;
			var10 = (double)var4;
			var12 = (double)var5 + 0.5D;
			var14 = "iconcrack_" + Item.eyeOfEnder.itemID;

			for(var15 = 0; var15 < 8; ++var15) {
				this.spawnParticle(var14, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
			}

			for(double var35 = 0.0D; var35 < Math.PI * 2.0D; var35 += Math.PI * 0.05D) {
				this.spawnParticle("portal", var8 + Math.cos(var35) * 5.0D, var10 - 0.4D, var12 + Math.sin(var35) * 5.0D, Math.cos(var35) * -5.0D, 0.0D, Math.sin(var35) * -5.0D);
				this.spawnParticle("portal", var8 + Math.cos(var35) * 5.0D, var10 - 0.4D, var12 + Math.sin(var35) * 5.0D, Math.cos(var35) * -7.0D, 0.0D, Math.sin(var35) * -7.0D);
			}

			return;
		case 2004:
			for(int var21 = 0; var21 < 20; ++var21) {
				double var22 = (double)var3 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				double var24 = (double)var4 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				double var26 = (double)var5 + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				this.theWorld.spawnParticle("smoke", var22, var24, var26, 0.0D, 0.0D, 0.0D);
				this.theWorld.spawnParticle("flame", var22, var24, var26, 0.0D, 0.0D, 0.0D);
			}

			return;
		case 2005:
			ItemDye.func_96603_a(this.theWorld, var3, var4, var5, var6);
		}

	}

	public void destroyBlockPartially(int var1, int var2, int var3, int var4, int var5) {
		if(var5 >= 0 && var5 < 10) {
			DestroyBlockProgress var6 = (DestroyBlockProgress)this.damagedBlocks.get(Integer.valueOf(var1));
			if(var6 == null || var6.getPartialBlockX() != var2 || var6.getPartialBlockY() != var3 || var6.getPartialBlockZ() != var4) {
				var6 = new DestroyBlockProgress(var1, var2, var3, var4);
				this.damagedBlocks.put(Integer.valueOf(var1), var6);
			}

			var6.setPartialBlockDamage(var5);
			var6.setCloudUpdateTick(this.cloudTickCounter);
		} else {
			this.damagedBlocks.remove(Integer.valueOf(var1));
		}

	}

	public void registerDestroyBlockIcons(IconRegister var1) {
		this.destroyBlockIcons = new Icon[10];

		for(int var2 = 0; var2 < this.destroyBlockIcons.length; ++var2) {
			this.destroyBlockIcons[var2] = var1.registerIcon("destroy_" + var2);
		}

	}
}
