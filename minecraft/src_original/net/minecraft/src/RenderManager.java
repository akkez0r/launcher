package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.lwjgl.opengl.GL11;

public class RenderManager {
	private Map entityRenderMap = new HashMap();
	public static RenderManager instance = new RenderManager();
	private FontRenderer fontRenderer;
	public static double renderPosX;
	public static double renderPosY;
	public static double renderPosZ;
	public RenderEngine renderEngine;
	public ItemRenderer itemRenderer;
	public World worldObj;
	public EntityLiving livingPlayer;
	public EntityLiving field_96451_i;
	public float playerViewY;
	public float playerViewX;
	public GameSettings options;
	public double viewerPosX;
	public double viewerPosY;
	public double viewerPosZ;
	public static boolean field_85095_o = false;

	private RenderManager() {
		this.entityRenderMap.put(EntitySpider.class, new RenderSpider());
		this.entityRenderMap.put(EntityCaveSpider.class, new RenderSpider());
		this.entityRenderMap.put(EntityPig.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
		this.entityRenderMap.put(EntitySheep.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
		this.entityRenderMap.put(EntityCow.class, new RenderCow(new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityWolf.class, new RenderWolf(new ModelWolf(), new ModelWolf(), 0.5F));
		this.entityRenderMap.put(EntityChicken.class, new RenderChicken(new ModelChicken(), 0.3F));
		this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(new ModelOcelot(), 0.4F));
		this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish());
		this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper());
		this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman());
		this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan());
		this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton());
		this.entityRenderMap.put(EntityWitch.class, new RenderWitch());
		this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze());
		this.entityRenderMap.put(EntityZombie.class, new RenderZombie());
		this.entityRenderMap.put(EntitySlime.class, new RenderSlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
		this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube());
		this.entityRenderMap.put(EntityPlayer.class, new RenderPlayer());
		this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
		this.entityRenderMap.put(EntityGhast.class, new RenderGhast());
		this.entityRenderMap.put(EntitySquid.class, new RenderSquid(new ModelSquid(), 0.7F));
		this.entityRenderMap.put(EntityVillager.class, new RenderVillager());
		this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem());
		this.entityRenderMap.put(EntityLiving.class, new RenderLiving(new ModelBiped(), 0.5F));
		this.entityRenderMap.put(EntityBat.class, new RenderBat());
		this.entityRenderMap.put(EntityDragon.class, new RenderDragon());
		this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal());
		this.entityRenderMap.put(EntityWither.class, new RenderWither());
		this.entityRenderMap.put(Entity.class, new RenderEntity());
		this.entityRenderMap.put(EntityPainting.class, new RenderPainting());
		this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame());
		this.entityRenderMap.put(EntityArrow.class, new RenderArrow());
		this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(Item.snowball));
		this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(Item.enderPearl));
		this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(Item.eyeOfEnder));
		this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(Item.egg));
		this.entityRenderMap.put(EntityPotion.class, new RenderSnowball(Item.potion, 16384));
		this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(Item.expBottle));
		this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(Item.firework));
		this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(2.0F));
		this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(0.5F));
		this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull());
		this.entityRenderMap.put(EntityItem.class, new RenderItem());
		this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb());
		this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed());
		this.entityRenderMap.put(EntityFallingSand.class, new RenderFallingSand());
		this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart());
		this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner());
		this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart());
		this.entityRenderMap.put(EntityBoat.class, new RenderBoat());
		this.entityRenderMap.put(EntityFishHook.class, new RenderFish());
		this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt());
		Iterator var1 = this.entityRenderMap.values().iterator();

		while(var1.hasNext()) {
			Render var2 = (Render)var1.next();
			var2.setRenderManager(this);
		}

	}

	public Render getEntityClassRenderObject(Class var1) {
		Render var2 = (Render)this.entityRenderMap.get(var1);
		if(var2 == null && var1 != Entity.class) {
			var2 = this.getEntityClassRenderObject(var1.getSuperclass());
			this.entityRenderMap.put(var1, var2);
		}

		return var2;
	}

	public Render getEntityRenderObject(Entity var1) {
		return this.getEntityClassRenderObject(var1.getClass());
	}

	public void cacheActiveRenderInfo(World var1, RenderEngine var2, FontRenderer var3, EntityLiving var4, EntityLiving var5, GameSettings var6, float var7) {
		this.worldObj = var1;
		this.renderEngine = var2;
		this.options = var6;
		this.livingPlayer = var4;
		this.field_96451_i = var5;
		this.fontRenderer = var3;
		if(var4.isPlayerSleeping()) {
			int var8 = var1.getBlockId(MathHelper.floor_double(var4.posX), MathHelper.floor_double(var4.posY), MathHelper.floor_double(var4.posZ));
			if(var8 == Block.bed.blockID) {
				int var9 = var1.getBlockMetadata(MathHelper.floor_double(var4.posX), MathHelper.floor_double(var4.posY), MathHelper.floor_double(var4.posZ));
				int var10 = var9 & 3;
				this.playerViewY = (float)(var10 * 90 + 180);
				this.playerViewX = 0.0F;
			}
		} else {
			this.playerViewY = var4.prevRotationYaw + (var4.rotationYaw - var4.prevRotationYaw) * var7;
			this.playerViewX = var4.prevRotationPitch + (var4.rotationPitch - var4.prevRotationPitch) * var7;
		}

		if(var6.thirdPersonView == 2) {
			this.playerViewY += 180.0F;
		}

		this.viewerPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)var7;
		this.viewerPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)var7;
		this.viewerPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)var7;
	}

	public void renderEntity(Entity var1, float var2) {
		if(var1.ticksExisted == 0) {
			var1.lastTickPosX = var1.posX;
			var1.lastTickPosY = var1.posY;
			var1.lastTickPosZ = var1.posZ;
		}

		double var3 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var2;
		double var5 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var2;
		double var7 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var2;
		float var9 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2;
		int var10 = var1.getBrightnessForRender(var2);
		if(var1.isBurning()) {
			var10 = 15728880;
		}

		int var11 = var10 % 65536;
		int var12 = var10 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var11 / 1.0F, (float)var12 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderEntityWithPosYaw(var1, var3 - renderPosX, var5 - renderPosY, var7 - renderPosZ, var9, var2);
	}

	public void renderEntityWithPosYaw(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		Render var10 = null;

		try {
			var10 = this.getEntityRenderObject(var1);
			if(var10 != null && this.renderEngine != null) {
				if(field_85095_o && !var1.isInvisible()) {
					try {
						this.func_85094_b(var1, var2, var4, var6, var8, var9);
					} catch (Throwable var17) {
						throw new ReportedException(CrashReport.makeCrashReport(var17, "Rendering entity hitbox in world"));
					}
				}

				try {
					var10.doRender(var1, var2, var4, var6, var8, var9);
				} catch (Throwable var16) {
					throw new ReportedException(CrashReport.makeCrashReport(var16, "Rendering entity in world"));
				}

				try {
					var10.doRenderShadowAndFire(var1, var2, var4, var6, var8, var9);
				} catch (Throwable var15) {
					throw new ReportedException(CrashReport.makeCrashReport(var15, "Post-rendering entity in world"));
				}
			}

		} catch (Throwable var18) {
			CrashReport var12 = CrashReport.makeCrashReport(var18, "Rendering entity in world");
			CrashReportCategory var13 = var12.makeCategory("Entity being rendered");
			var1.func_85029_a(var13);
			CrashReportCategory var14 = var12.makeCategory("Renderer details");
			var14.addCrashSection("Assigned renderer", var10);
			var14.addCrashSection("Location", CrashReportCategory.func_85074_a(var2, var4, var6));
			var14.addCrashSection("Rotation", Float.valueOf(var8));
			var14.addCrashSection("Delta", Float.valueOf(var9));
			throw new ReportedException(var12);
		}
	}

	private void func_85094_b(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		Tessellator var10 = Tessellator.instance;
		var10.startDrawingQuads();
		var10.setColorRGBA(255, 255, 255, 32);
		double var11 = (double)(-var1.width / 2.0F);
		double var13 = (double)(-var1.width / 2.0F);
		double var15 = (double)(var1.width / 2.0F);
		double var17 = (double)(-var1.width / 2.0F);
		double var19 = (double)(-var1.width / 2.0F);
		double var21 = (double)(var1.width / 2.0F);
		double var23 = (double)(var1.width / 2.0F);
		double var25 = (double)(var1.width / 2.0F);
		double var27 = (double)var1.height;
		var10.addVertex(var2 + var11, var4 + var27, var6 + var13);
		var10.addVertex(var2 + var11, var4, var6 + var13);
		var10.addVertex(var2 + var15, var4, var6 + var17);
		var10.addVertex(var2 + var15, var4 + var27, var6 + var17);
		var10.addVertex(var2 + var23, var4 + var27, var6 + var25);
		var10.addVertex(var2 + var23, var4, var6 + var25);
		var10.addVertex(var2 + var19, var4, var6 + var21);
		var10.addVertex(var2 + var19, var4 + var27, var6 + var21);
		var10.addVertex(var2 + var15, var4 + var27, var6 + var17);
		var10.addVertex(var2 + var15, var4, var6 + var17);
		var10.addVertex(var2 + var23, var4, var6 + var25);
		var10.addVertex(var2 + var23, var4 + var27, var6 + var25);
		var10.addVertex(var2 + var19, var4 + var27, var6 + var21);
		var10.addVertex(var2 + var19, var4, var6 + var21);
		var10.addVertex(var2 + var11, var4, var6 + var13);
		var10.addVertex(var2 + var11, var4 + var27, var6 + var13);
		var10.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
	}

	public void set(World var1) {
		this.worldObj = var1;
	}

	public double getDistanceToCamera(double var1, double var3, double var5) {
		double var7 = var1 - this.viewerPosX;
		double var9 = var3 - this.viewerPosY;
		double var11 = var5 - this.viewerPosZ;
		return var7 * var7 + var9 * var9 + var11 * var11;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	public void updateIcons(IconRegister var1) {
		Iterator var2 = this.entityRenderMap.values().iterator();

		while(var2.hasNext()) {
			Render var3 = (Render)var2.next();
			var3.updateIcons(var1);
		}

	}
}
