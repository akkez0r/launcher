package net.minecraft.src;

import java.util.Iterator;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess {
	private MinecraftServer mcServer;
	private WorldServer theWorldServer;

	public WorldManager(MinecraftServer var1, WorldServer var2) {
		this.mcServer = var1;
		this.theWorldServer = var2;
	}

	public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
	}

	public void onEntityCreate(Entity var1) {
		this.theWorldServer.getEntityTracker().addEntityToTracker(var1);
	}

	public void onEntityDestroy(Entity var1) {
		this.theWorldServer.getEntityTracker().removeEntityFromAllTrackingPlayers(var1);
	}

	public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
		this.mcServer.getConfigurationManager().sendToAllNear(var2, var4, var6, var8 > 1.0F ? (double)(16.0F * var8) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(var1, var2, var4, var6, var8, var9));
	}

	public void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
		this.mcServer.getConfigurationManager().sendToAllNearExcept(var1, var3, var5, var7, var9 > 1.0F ? (double)(16.0F * var9) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(var2, var3, var5, var7, var9, var10));
	}

	public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
	}

	public void markBlockForUpdate(int var1, int var2, int var3) {
		this.theWorldServer.getPlayerManager().flagChunkForUpdate(var1, var2, var3);
	}

	public void markBlockForRenderUpdate(int var1, int var2, int var3) {
	}

	public void playRecord(String var1, int var2, int var3, int var4) {
	}

	public void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
		this.mcServer.getConfigurationManager().sendToAllNearExcept(var1, (double)var3, (double)var4, (double)var5, 64.0D, this.theWorldServer.provider.dimensionId, new Packet61DoorChange(var2, var3, var4, var5, var6, false));
	}

	public void broadcastSound(int var1, int var2, int var3, int var4, int var5) {
		this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet61DoorChange(var1, var2, var3, var4, var5, true));
	}

	public void destroyBlockPartially(int var1, int var2, int var3, int var4, int var5) {
		Iterator var6 = this.mcServer.getConfigurationManager().playerEntityList.iterator();

		while(var6.hasNext()) {
			EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
			if(var7 != null && var7.worldObj == this.theWorldServer && var7.entityId != var1) {
				double var8 = (double)var2 - var7.posX;
				double var10 = (double)var3 - var7.posY;
				double var12 = (double)var4 - var7.posZ;
				if(var8 * var8 + var10 * var10 + var12 * var12 < 1024.0D) {
					var7.playerNetServerHandler.sendPacketToPlayer(new Packet55BlockDestroy(var1, var2, var3, var4, var5));
				}
			}
		}

	}
}
