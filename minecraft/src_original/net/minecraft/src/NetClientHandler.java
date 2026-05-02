package net.minecraft.src;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class NetClientHandler extends NetHandler {
	private boolean disconnected = false;
	private INetworkManager netManager;
	public String field_72560_a;
	private Minecraft mc;
	private WorldClient worldClient;
	private boolean doneLoadingTerrain = false;
	public MapStorage mapStorage = new MapStorage((ISaveHandler)null);
	private Map playerInfoMap = new HashMap();
	public List playerInfoList = new ArrayList();
	public int currentServerMaxPlayers = 20;
	private GuiScreen field_98183_l = null;
	Random rand = new Random();

	public NetClientHandler(Minecraft var1, String var2, int var3) throws IOException {
		this.mc = var1;
		Socket var4 = new Socket(InetAddress.getByName(var2), var3);
		this.netManager = new TcpConnection(var1.getLogAgent(), var4, "Client", this);
	}

	public NetClientHandler(Minecraft var1, String var2, int var3, GuiScreen var4) throws IOException {
		this.mc = var1;
		this.field_98183_l = var4;
		Socket var5 = new Socket(InetAddress.getByName(var2), var3);
		this.netManager = new TcpConnection(var1.getLogAgent(), var5, "Client", this);
	}

	public NetClientHandler(Minecraft var1, IntegratedServer var2) throws IOException {
		this.mc = var1;
		this.netManager = new MemoryConnection(var1.getLogAgent(), this);
		var2.getServerListeningThread().func_71754_a((MemoryConnection)this.netManager, var1.session.username);
	}

	public void cleanup() {
		if(this.netManager != null) {
			this.netManager.wakeThreads();
		}

		this.netManager = null;
		this.worldClient = null;
	}

	public void processReadPackets() {
		if(!this.disconnected && this.netManager != null) {
			this.netManager.processReadPackets();
		}

		if(this.netManager != null) {
			this.netManager.wakeThreads();
		}

	}

	public void handleServerAuthData(Packet253ServerAuthData var1) {
		String var2 = var1.getServerId().trim();
		PublicKey var3 = var1.getPublicKey();
		SecretKey var4 = CryptManager.createNewSharedKey();
		if(!"-".equals(var2)) {
			String var5 = (new BigInteger(CryptManager.getServerIdHash(var2, var3, var4))).toString(16);
			String var6 = this.sendSessionRequest(this.mc.session.username, this.mc.session.sessionId, var5);
			if(!"ok".equalsIgnoreCase(var6)) {
				this.netManager.networkShutdown("disconnect.loginFailedInfo", new Object[]{var6});
				return;
			}
		}

		this.addToSendQueue(new Packet252SharedKey(var4, var3, var1.getVerifyToken()));
	}

	private String sendSessionRequest(String var1, String var2, String var3) {
		try {
			URL var4 = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + urlEncode(var1) + "&sessionId=" + urlEncode(var2) + "&serverId=" + urlEncode(var3));
			BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.openStream()));
			String var6 = var5.readLine();
			var5.close();
			return var6;
		} catch (IOException var7) {
			return var7.toString();
		}
	}

	private static String urlEncode(String var0) throws IOException {
		return URLEncoder.encode(var0, "UTF-8");
	}

	public void handleSharedKey(Packet252SharedKey var1) {
		this.addToSendQueue(new Packet205ClientCommand(0));
	}

	public void handleLogin(Packet1Login var1) {
		this.mc.playerController = new PlayerControllerMP(this.mc, this);
		this.mc.statFileWriter.readStat(StatList.joinMultiplayerStat, 1);
		this.worldClient = new WorldClient(this, new WorldSettings(0L, var1.gameType, false, var1.hardcoreMode, var1.terrainType), var1.dimension, var1.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent());
		this.worldClient.isRemote = true;
		this.mc.loadWorld(this.worldClient);
		this.mc.thePlayer.dimension = var1.dimension;
		this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
		this.mc.thePlayer.entityId = var1.clientEntityId;
		this.currentServerMaxPlayers = var1.maxPlayers;
		this.mc.playerController.setGameType(var1.gameType);
		this.mc.gameSettings.sendSettingsToServer();
	}

	public void handleVehicleSpawn(Packet23VehicleSpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		Object var8 = null;
		if(var1.type == 10) {
			var8 = EntityMinecart.createMinecart(this.worldClient, var2, var4, var6, var1.throwerEntityId);
		} else if(var1.type == 90) {
			Entity var9 = this.getEntityByID(var1.throwerEntityId);
			if(var9 instanceof EntityPlayer) {
				var8 = new EntityFishHook(this.worldClient, var2, var4, var6, (EntityPlayer)var9);
			}

			var1.throwerEntityId = 0;
		} else if(var1.type == 60) {
			var8 = new EntityArrow(this.worldClient, var2, var4, var6);
		} else if(var1.type == 61) {
			var8 = new EntitySnowball(this.worldClient, var2, var4, var6);
		} else if(var1.type == 71) {
			var8 = new EntityItemFrame(this.worldClient, (int)var2, (int)var4, (int)var6, var1.throwerEntityId);
			var1.throwerEntityId = 0;
		} else if(var1.type == 65) {
			var8 = new EntityEnderPearl(this.worldClient, var2, var4, var6);
		} else if(var1.type == 72) {
			var8 = new EntityEnderEye(this.worldClient, var2, var4, var6);
		} else if(var1.type == 76) {
			var8 = new EntityFireworkRocket(this.worldClient, var2, var4, var6, (ItemStack)null);
		} else if(var1.type == 63) {
			var8 = new EntityLargeFireball(this.worldClient, var2, var4, var6, (double)var1.speedX / 8000.0D, (double)var1.speedY / 8000.0D, (double)var1.speedZ / 8000.0D);
			var1.throwerEntityId = 0;
		} else if(var1.type == 64) {
			var8 = new EntitySmallFireball(this.worldClient, var2, var4, var6, (double)var1.speedX / 8000.0D, (double)var1.speedY / 8000.0D, (double)var1.speedZ / 8000.0D);
			var1.throwerEntityId = 0;
		} else if(var1.type == 66) {
			var8 = new EntityWitherSkull(this.worldClient, var2, var4, var6, (double)var1.speedX / 8000.0D, (double)var1.speedY / 8000.0D, (double)var1.speedZ / 8000.0D);
			var1.throwerEntityId = 0;
		} else if(var1.type == 62) {
			var8 = new EntityEgg(this.worldClient, var2, var4, var6);
		} else if(var1.type == 73) {
			var8 = new EntityPotion(this.worldClient, var2, var4, var6, var1.throwerEntityId);
			var1.throwerEntityId = 0;
		} else if(var1.type == 75) {
			var8 = new EntityExpBottle(this.worldClient, var2, var4, var6);
			var1.throwerEntityId = 0;
		} else if(var1.type == 1) {
			var8 = new EntityBoat(this.worldClient, var2, var4, var6);
		} else if(var1.type == 50) {
			var8 = new EntityTNTPrimed(this.worldClient, var2, var4, var6, (EntityLiving)null);
		} else if(var1.type == 51) {
			var8 = new EntityEnderCrystal(this.worldClient, var2, var4, var6);
		} else if(var1.type == 2) {
			var8 = new EntityItem(this.worldClient, var2, var4, var6);
		} else if(var1.type == 70) {
			var8 = new EntityFallingSand(this.worldClient, var2, var4, var6, var1.throwerEntityId & '\uffff', var1.throwerEntityId >> 16);
			var1.throwerEntityId = 0;
		}

		if(var8 != null) {
			((Entity)var8).serverPosX = var1.xPosition;
			((Entity)var8).serverPosY = var1.yPosition;
			((Entity)var8).serverPosZ = var1.zPosition;
			((Entity)var8).rotationPitch = (float)(var1.pitch * 360) / 256.0F;
			((Entity)var8).rotationYaw = (float)(var1.yaw * 360) / 256.0F;
			Entity[] var12 = ((Entity)var8).getParts();
			if(var12 != null) {
				int var10 = var1.entityId - ((Entity)var8).entityId;

				for(int var11 = 0; var11 < var12.length; ++var11) {
					var12[var11].entityId += var10;
				}
			}

			((Entity)var8).entityId = var1.entityId;
			this.worldClient.addEntityToWorld(var1.entityId, (Entity)var8);
			if(var1.throwerEntityId > 0) {
				if(var1.type == 60) {
					Entity var13 = this.getEntityByID(var1.throwerEntityId);
					if(var13 instanceof EntityLiving) {
						EntityArrow var14 = (EntityArrow)var8;
						var14.shootingEntity = var13;
					}
				}

				((Entity)var8).setVelocity((double)var1.speedX / 8000.0D, (double)var1.speedY / 8000.0D, (double)var1.speedZ / 8000.0D);
			}
		}

	}

	public void handleEntityExpOrb(Packet26EntityExpOrb var1) {
		EntityXPOrb var2 = new EntityXPOrb(this.worldClient, (double)var1.posX, (double)var1.posY, (double)var1.posZ, var1.xpValue);
		var2.serverPosX = var1.posX;
		var2.serverPosY = var1.posY;
		var2.serverPosZ = var1.posZ;
		var2.rotationYaw = 0.0F;
		var2.rotationPitch = 0.0F;
		var2.entityId = var1.entityId;
		this.worldClient.addEntityToWorld(var1.entityId, var2);
	}

	public void handleWeather(Packet71Weather var1) {
		double var2 = (double)var1.posX / 32.0D;
		double var4 = (double)var1.posY / 32.0D;
		double var6 = (double)var1.posZ / 32.0D;
		EntityLightningBolt var8 = null;
		if(var1.isLightningBolt == 1) {
			var8 = new EntityLightningBolt(this.worldClient, var2, var4, var6);
		}

		if(var8 != null) {
			var8.serverPosX = var1.posX;
			var8.serverPosY = var1.posY;
			var8.serverPosZ = var1.posZ;
			var8.rotationYaw = 0.0F;
			var8.rotationPitch = 0.0F;
			var8.entityId = var1.entityID;
			this.worldClient.addWeatherEffect(var8);
		}

	}

	public void handleEntityPainting(Packet25EntityPainting var1) {
		EntityPainting var2 = new EntityPainting(this.worldClient, var1.xPosition, var1.yPosition, var1.zPosition, var1.direction, var1.title);
		this.worldClient.addEntityToWorld(var1.entityId, var2);
	}

	public void handleEntityVelocity(Packet28EntityVelocity var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			var2.setVelocity((double)var1.motionX / 8000.0D, (double)var1.motionY / 8000.0D, (double)var1.motionZ / 8000.0D);
		}
	}

	public void handleEntityMetadata(Packet40EntityMetadata var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null && var1.getMetadata() != null) {
			var2.getDataWatcher().updateWatchedObjectsFromList(var1.getMetadata());
		}

	}

	public void handleNamedEntitySpawn(Packet20NamedEntitySpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		float var8 = (float)(var1.rotation * 360) / 256.0F;
		float var9 = (float)(var1.pitch * 360) / 256.0F;
		EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.mc.theWorld, var1.name);
		var10.prevPosX = var10.lastTickPosX = (double)(var10.serverPosX = var1.xPosition);
		var10.prevPosY = var10.lastTickPosY = (double)(var10.serverPosY = var1.yPosition);
		var10.prevPosZ = var10.lastTickPosZ = (double)(var10.serverPosZ = var1.zPosition);
		int var11 = var1.currentItem;
		if(var11 == 0) {
			var10.inventory.mainInventory[var10.inventory.currentItem] = null;
		} else {
			var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(var11, 1, 0);
		}

		var10.setPositionAndRotation(var2, var4, var6, var8, var9);
		this.worldClient.addEntityToWorld(var1.entityId, var10);
		List var12 = var1.getWatchedMetadata();
		if(var12 != null) {
			var10.getDataWatcher().updateWatchedObjectsFromList(var12);
		}

	}

	public void handleEntityTeleport(Packet34EntityTeleport var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			var2.serverPosX = var1.xPosition;
			var2.serverPosY = var1.yPosition;
			var2.serverPosZ = var1.zPosition;
			double var3 = (double)var2.serverPosX / 32.0D;
			double var5 = (double)var2.serverPosY / 32.0D + 1.0D / 64.0D;
			double var7 = (double)var2.serverPosZ / 32.0D;
			float var9 = (float)(var1.yaw * 360) / 256.0F;
			float var10 = (float)(var1.pitch * 360) / 256.0F;
			var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
		}
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch var1) {
		if(var1.id >= 0 && var1.id < InventoryPlayer.getHotbarSize()) {
			this.mc.thePlayer.inventory.currentItem = var1.id;
		}

	}

	public void handleEntity(Packet30Entity var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			var2.serverPosX += var1.xPosition;
			var2.serverPosY += var1.yPosition;
			var2.serverPosZ += var1.zPosition;
			double var3 = (double)var2.serverPosX / 32.0D;
			double var5 = (double)var2.serverPosY / 32.0D;
			double var7 = (double)var2.serverPosZ / 32.0D;
			float var9 = var1.rotating ? (float)(var1.yaw * 360) / 256.0F : var2.rotationYaw;
			float var10 = var1.rotating ? (float)(var1.pitch * 360) / 256.0F : var2.rotationPitch;
			var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
		}
	}

	public void handleEntityHeadRotation(Packet35EntityHeadRotation var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			float var3 = (float)(var1.headRotationYaw * 360) / 256.0F;
			var2.setRotationYawHead(var3);
		}
	}

	public void handleDestroyEntity(Packet29DestroyEntity var1) {
		for(int var2 = 0; var2 < var1.entityId.length; ++var2) {
			this.worldClient.removeEntityFromWorld(var1.entityId[var2]);
		}

	}

	public void handleFlying(Packet10Flying var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		double var3 = var2.posX;
		double var5 = var2.posY;
		double var7 = var2.posZ;
		float var9 = var2.rotationYaw;
		float var10 = var2.rotationPitch;
		if(var1.moving) {
			var3 = var1.xPosition;
			var5 = var1.yPosition;
			var7 = var1.zPosition;
		}

		if(var1.rotating) {
			var9 = var1.yaw;
			var10 = var1.pitch;
		}

		var2.ySize = 0.0F;
		var2.motionX = var2.motionY = var2.motionZ = 0.0D;
		var2.setPositionAndRotation(var3, var5, var7, var9, var10);
		var1.xPosition = var2.posX;
		var1.yPosition = var2.boundingBox.minY;
		var1.zPosition = var2.posZ;
		var1.stance = var2.posY;
		this.netManager.addToSendQueue(var1);
		if(!this.doneLoadingTerrain) {
			this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
			this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
			this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
			this.doneLoadingTerrain = true;
			this.mc.displayGuiScreen((GuiScreen)null);
		}

	}

	public void handleMultiBlockChange(Packet52MultiBlockChange var1) {
		int var2 = var1.xPosition * 16;
		int var3 = var1.zPosition * 16;
		if(var1.metadataArray != null) {
			DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var1.metadataArray));

			try {
				for(int var5 = 0; var5 < var1.size; ++var5) {
					short var6 = var4.readShort();
					short var7 = var4.readShort();
					int var8 = var7 >> 4 & 4095;
					int var9 = var7 & 15;
					int var10 = var6 >> 12 & 15;
					int var11 = var6 >> 8 & 15;
					int var12 = var6 & 255;
					this.worldClient.setBlockAndMetadataAndInvalidate(var10 + var2, var12, var11 + var3, var8, var9);
				}
			} catch (IOException var13) {
			}

		}
	}

	public void handleMapChunk(Packet51MapChunk var1) {
		if(var1.includeInitialize) {
			if(var1.yChMin == 0) {
				this.worldClient.doPreChunk(var1.xCh, var1.zCh, false);
				return;
			}

			this.worldClient.doPreChunk(var1.xCh, var1.zCh, true);
		}

		this.worldClient.invalidateBlockReceiveRegion(var1.xCh << 4, 0, var1.zCh << 4, (var1.xCh << 4) + 15, 256, (var1.zCh << 4) + 15);
		Chunk var2 = this.worldClient.getChunkFromChunkCoords(var1.xCh, var1.zCh);
		if(var1.includeInitialize && var2 == null) {
			this.worldClient.doPreChunk(var1.xCh, var1.zCh, true);
			var2 = this.worldClient.getChunkFromChunkCoords(var1.xCh, var1.zCh);
		}

		if(var2 != null) {
			var2.fillChunk(var1.getCompressedChunkData(), var1.yChMin, var1.yChMax, var1.includeInitialize);
			this.worldClient.markBlockRangeForRenderUpdate(var1.xCh << 4, 0, var1.zCh << 4, (var1.xCh << 4) + 15, 256, (var1.zCh << 4) + 15);
			if(!var1.includeInitialize || !(this.worldClient.provider instanceof WorldProviderSurface)) {
				var2.resetRelightChecks();
			}
		}

	}

	public void handleBlockChange(Packet53BlockChange var1) {
		this.worldClient.setBlockAndMetadataAndInvalidate(var1.xPosition, var1.yPosition, var1.zPosition, var1.type, var1.metadata);
	}

	public void handleKickDisconnect(Packet255KickDisconnect var1) {
		this.netManager.networkShutdown("disconnect.kicked", new Object[0]);
		this.disconnected = true;
		this.mc.loadWorld((WorldClient)null);
		if(this.field_98183_l != null) {
			this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.disconnected", "disconnect.genericReason", new Object[]{var1.reason}));
		} else {
			this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.disconnected", "disconnect.genericReason", new Object[]{var1.reason}));
		}

	}

	public void handleErrorMessage(String var1, Object[] var2) {
		if(!this.disconnected) {
			this.disconnected = true;
			this.mc.loadWorld((WorldClient)null);
			if(this.field_98183_l != null) {
				this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.lost", var1, var2));
			} else {
				this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", var1, var2));
			}

		}
	}

	public void quitWithPacket(Packet var1) {
		if(!this.disconnected) {
			this.netManager.addToSendQueue(var1);
			this.netManager.serverShutdown();
		}
	}

	public void addToSendQueue(Packet var1) {
		if(!this.disconnected) {
			this.netManager.addToSendQueue(var1);
		}
	}

	public void handleCollect(Packet22Collect var1) {
		Entity var2 = this.getEntityByID(var1.collectedEntityId);
		Object var3 = (EntityLiving)this.getEntityByID(var1.collectorEntityId);
		if(var3 == null) {
			var3 = this.mc.thePlayer;
		}

		if(var2 != null) {
			if(var2 instanceof EntityXPOrb) {
				this.worldClient.playSoundAtEntity(var2, "random.orb", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			} else {
				this.worldClient.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			}

			this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var2, (Entity)var3, -0.5F));
			this.worldClient.removeEntityFromWorld(var1.collectedEntityId);
		}

	}

	public void handleChat(Packet3Chat var1) {
		this.mc.ingameGUI.getChatGUI().printChatMessage(var1.message);
	}

	public void handleAnimation(Packet18Animation var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			if(var1.animate == 1) {
				EntityLiving var3 = (EntityLiving)var2;
				var3.swingItem();
			} else if(var1.animate == 2) {
				var2.performHurtAnimation();
			} else if(var1.animate == 3) {
				EntityPlayer var4 = (EntityPlayer)var2;
				var4.wakeUpPlayer(false, false, false);
			} else if(var1.animate != 4) {
				if(var1.animate == 6) {
					this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, var2));
				} else if(var1.animate == 7) {
					EntityCrit2FX var5 = new EntityCrit2FX(this.mc.theWorld, var2, "magicCrit");
					this.mc.effectRenderer.addEffect(var5);
				} else if(var1.animate == 5 && var2 instanceof EntityOtherPlayerMP) {
				}
			}

		}
	}

	public void handleSleep(Packet17Sleep var1) {
		Entity var2 = this.getEntityByID(var1.entityID);
		if(var2 != null) {
			if(var1.field_73622_e == 0) {
				EntityPlayer var3 = (EntityPlayer)var2;
				var3.sleepInBedAt(var1.bedX, var1.bedY, var1.bedZ);
			}

		}
	}

	public void disconnect() {
		this.disconnected = true;
		this.netManager.wakeThreads();
		this.netManager.networkShutdown("disconnect.closed", new Object[0]);
	}

	public void handleMobSpawn(Packet24MobSpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		float var8 = (float)(var1.yaw * 360) / 256.0F;
		float var9 = (float)(var1.pitch * 360) / 256.0F;
		EntityLiving var10 = (EntityLiving)EntityList.createEntityByID(var1.type, this.mc.theWorld);
		var10.serverPosX = var1.xPosition;
		var10.serverPosY = var1.yPosition;
		var10.serverPosZ = var1.zPosition;
		var10.rotationYawHead = (float)(var1.headYaw * 360) / 256.0F;
		Entity[] var11 = var10.getParts();
		if(var11 != null) {
			int var12 = var1.entityId - var10.entityId;

			for(int var13 = 0; var13 < var11.length; ++var13) {
				var11[var13].entityId += var12;
			}
		}

		var10.entityId = var1.entityId;
		var10.setPositionAndRotation(var2, var4, var6, var8, var9);
		var10.motionX = (double)((float)var1.velocityX / 8000.0F);
		var10.motionY = (double)((float)var1.velocityY / 8000.0F);
		var10.motionZ = (double)((float)var1.velocityZ / 8000.0F);
		this.worldClient.addEntityToWorld(var1.entityId, var10);
		List var14 = var1.getMetadata();
		if(var14 != null) {
			var10.getDataWatcher().updateWatchedObjectsFromList(var14);
		}

	}

	public void handleUpdateTime(Packet4UpdateTime var1) {
		this.mc.theWorld.func_82738_a(var1.worldAge);
		this.mc.theWorld.setWorldTime(var1.time);
	}

	public void handleSpawnPosition(Packet6SpawnPosition var1) {
		this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(var1.xPosition, var1.yPosition, var1.zPosition), true);
		this.mc.theWorld.getWorldInfo().setSpawnPosition(var1.xPosition, var1.yPosition, var1.zPosition);
	}

	public void handleAttachEntity(Packet39AttachEntity var1) {
		Object var2 = this.getEntityByID(var1.entityId);
		Entity var3 = this.getEntityByID(var1.vehicleEntityId);
		if(var1.entityId == this.mc.thePlayer.entityId) {
			var2 = this.mc.thePlayer;
			if(var3 instanceof EntityBoat) {
				((EntityBoat)var3).func_70270_d(false);
			}
		} else if(var3 instanceof EntityBoat) {
			((EntityBoat)var3).func_70270_d(true);
		}

		if(var2 != null) {
			((Entity)var2).mountEntity(var3);
		}
	}

	public void handleEntityStatus(Packet38EntityStatus var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 != null) {
			var2.handleHealthUpdate(var1.entityStatus);
		}

	}

	private Entity getEntityByID(int var1) {
		return (Entity)(var1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(var1));
	}

	public void handleUpdateHealth(Packet8UpdateHealth var1) {
		this.mc.thePlayer.setHealth(var1.healthMP);
		this.mc.thePlayer.getFoodStats().setFoodLevel(var1.food);
		this.mc.thePlayer.getFoodStats().setFoodSaturationLevel(var1.foodSaturation);
	}

	public void handleExperience(Packet43Experience var1) {
		this.mc.thePlayer.setXPStats(var1.experience, var1.experienceTotal, var1.experienceLevel);
	}

	public void handleRespawn(Packet9Respawn var1) {
		if(var1.respawnDimension != this.mc.thePlayer.dimension) {
			this.doneLoadingTerrain = false;
			Scoreboard var2 = this.worldClient.getScoreboard();
			this.worldClient = new WorldClient(this, new WorldSettings(0L, var1.gameType, false, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), var1.terrainType), var1.respawnDimension, var1.difficulty, this.mc.mcProfiler, this.mc.getLogAgent());
			this.worldClient.func_96443_a(var2);
			this.worldClient.isRemote = true;
			this.mc.loadWorld(this.worldClient);
			this.mc.thePlayer.dimension = var1.respawnDimension;
			this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
		}

		this.mc.setDimensionAndSpawnPlayer(var1.respawnDimension);
		this.mc.playerController.setGameType(var1.gameType);
	}

	public void handleExplosion(Packet60Explosion var1) {
		Explosion var2 = new Explosion(this.mc.theWorld, (Entity)null, var1.explosionX, var1.explosionY, var1.explosionZ, var1.explosionSize);
		var2.affectedBlockPositions = var1.chunkPositionRecords;
		var2.doExplosionB(true);
		this.mc.thePlayer.motionX += (double)var1.getPlayerVelocityX();
		this.mc.thePlayer.motionY += (double)var1.getPlayerVelocityY();
		this.mc.thePlayer.motionZ += (double)var1.getPlayerVelocityZ();
	}

	public void handleOpenWindow(Packet100OpenWindow var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		switch(var1.inventoryType) {
		case 0:
			var2.displayGUIChest(new InventoryBasic(var1.windowTitle, var1.useProvidedWindowTitle, var1.slotsCount));
			var2.openContainer.windowId = var1.windowId;
			break;
		case 1:
			var2.displayGUIWorkbench(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
			var2.openContainer.windowId = var1.windowId;
			break;
		case 2:
			TileEntityFurnace var4 = new TileEntityFurnace();
			if(var1.useProvidedWindowTitle) {
				var4.func_94129_a(var1.windowTitle);
			}

			var2.displayGUIFurnace(var4);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 3:
			TileEntityDispenser var7 = new TileEntityDispenser();
			if(var1.useProvidedWindowTitle) {
				var7.setCustomName(var1.windowTitle);
			}

			var2.displayGUIDispenser(var7);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 4:
			var2.displayGUIEnchantment(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ), var1.useProvidedWindowTitle ? var1.windowTitle : null);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 5:
			TileEntityBrewingStand var5 = new TileEntityBrewingStand();
			if(var1.useProvidedWindowTitle) {
				var5.func_94131_a(var1.windowTitle);
			}

			var2.displayGUIBrewingStand(var5);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 6:
			var2.displayGUIMerchant(new NpcMerchant(var2), var1.useProvidedWindowTitle ? var1.windowTitle : null);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 7:
			TileEntityBeacon var8 = new TileEntityBeacon();
			var2.displayGUIBeacon(var8);
			if(var1.useProvidedWindowTitle) {
				var8.func_94047_a(var1.windowTitle);
			}

			var2.openContainer.windowId = var1.windowId;
			break;
		case 8:
			var2.displayGUIAnvil(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
			var2.openContainer.windowId = var1.windowId;
			break;
		case 9:
			TileEntityHopper var3 = new TileEntityHopper();
			if(var1.useProvidedWindowTitle) {
				var3.setInventoryName(var1.windowTitle);
			}

			var2.displayGUIHopper(var3);
			var2.openContainer.windowId = var1.windowId;
			break;
		case 10:
			TileEntityDropper var6 = new TileEntityDropper();
			if(var1.useProvidedWindowTitle) {
				var6.setCustomName(var1.windowTitle);
			}

			var2.displayGUIDispenser(var6);
			var2.openContainer.windowId = var1.windowId;
		}

	}

	public void handleSetSlot(Packet103SetSlot var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		if(var1.windowId == -1) {
			var2.inventory.setItemStack(var1.myItemStack);
		} else {
			boolean var3 = false;
			if(this.mc.currentScreen instanceof GuiContainerCreative) {
				GuiContainerCreative var4 = (GuiContainerCreative)this.mc.currentScreen;
				var3 = var4.func_74230_h() != CreativeTabs.tabInventory.getTabIndex();
			}

			if(var1.windowId == 0 && var1.itemSlot >= 36 && var1.itemSlot < 45) {
				ItemStack var5 = var2.inventoryContainer.getSlot(var1.itemSlot).getStack();
				if(var1.myItemStack != null && (var5 == null || var5.stackSize < var1.myItemStack.stackSize)) {
					var1.myItemStack.animationsToGo = 5;
				}

				var2.inventoryContainer.putStackInSlot(var1.itemSlot, var1.myItemStack);
			} else if(var1.windowId == var2.openContainer.windowId && (var1.windowId != 0 || !var3)) {
				var2.openContainer.putStackInSlot(var1.itemSlot, var1.myItemStack);
			}
		}

	}

	public void handleTransaction(Packet106Transaction var1) {
		Container var2 = null;
		EntityClientPlayerMP var3 = this.mc.thePlayer;
		if(var1.windowId == 0) {
			var2 = var3.inventoryContainer;
		} else if(var1.windowId == var3.openContainer.windowId) {
			var2 = var3.openContainer;
		}

		if(var2 != null && !var1.accepted) {
			this.addToSendQueue(new Packet106Transaction(var1.windowId, var1.shortWindowId, true));
		}

	}

	public void handleWindowItems(Packet104WindowItems var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		if(var1.windowId == 0) {
			var2.inventoryContainer.putStacksInSlots(var1.itemStack);
		} else if(var1.windowId == var2.openContainer.windowId) {
			var2.openContainer.putStacksInSlots(var1.itemStack);
		}

	}

	public void handleUpdateSign(Packet130UpdateSign var1) {
		boolean var2 = false;
		if(this.mc.theWorld.blockExists(var1.xPosition, var1.yPosition, var1.zPosition)) {
			TileEntity var3 = this.mc.theWorld.getBlockTileEntity(var1.xPosition, var1.yPosition, var1.zPosition);
			if(var3 instanceof TileEntitySign) {
				TileEntitySign var4 = (TileEntitySign)var3;
				if(var4.isEditable()) {
					for(int var5 = 0; var5 < 4; ++var5) {
						var4.signText[var5] = var1.signLines[var5];
					}

					var4.onInventoryChanged();
				}

				var2 = true;
			}
		}

		if(!var2 && this.mc.thePlayer != null) {
			this.mc.thePlayer.sendChatToPlayer("Unable to locate sign at " + var1.xPosition + ", " + var1.yPosition + ", " + var1.zPosition);
		}

	}

	public void handleTileEntityData(Packet132TileEntityData var1) {
		if(this.mc.theWorld.blockExists(var1.xPosition, var1.yPosition, var1.zPosition)) {
			TileEntity var2 = this.mc.theWorld.getBlockTileEntity(var1.xPosition, var1.yPosition, var1.zPosition);
			if(var2 != null) {
				if(var1.actionType == 1 && var2 instanceof TileEntityMobSpawner) {
					var2.readFromNBT(var1.customParam1);
				} else if(var1.actionType == 2 && var2 instanceof TileEntityCommandBlock) {
					var2.readFromNBT(var1.customParam1);
				} else if(var1.actionType == 3 && var2 instanceof TileEntityBeacon) {
					var2.readFromNBT(var1.customParam1);
				} else if(var1.actionType == 4 && var2 instanceof TileEntitySkull) {
					var2.readFromNBT(var1.customParam1);
				}
			}
		}

	}

	public void handleUpdateProgressbar(Packet105UpdateProgressbar var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		this.unexpectedPacket(var1);
		if(var2.openContainer != null && var2.openContainer.windowId == var1.windowId) {
			var2.openContainer.updateProgressBar(var1.progressBar, var1.progressBarValue);
		}

	}

	public void handlePlayerInventory(Packet5PlayerInventory var1) {
		Entity var2 = this.getEntityByID(var1.entityID);
		if(var2 != null) {
			var2.setCurrentItemOrArmor(var1.slot, var1.getItemSlot());
		}

	}

	public void handleCloseWindow(Packet101CloseWindow var1) {
		this.mc.thePlayer.func_92015_f();
	}

	public void handleBlockEvent(Packet54PlayNoteBlock var1) {
		this.mc.theWorld.addBlockEvent(var1.xLocation, var1.yLocation, var1.zLocation, var1.blockId, var1.instrumentType, var1.pitch);
	}

	public void handleBlockDestroy(Packet55BlockDestroy var1) {
		this.mc.theWorld.destroyBlockInWorldPartially(var1.getEntityId(), var1.getPosX(), var1.getPosY(), var1.getPosZ(), var1.getDestroyedStage());
	}

	public void handleMapChunks(Packet56MapChunks var1) {
		for(int var2 = 0; var2 < var1.getNumberOfChunkInPacket(); ++var2) {
			int var3 = var1.getChunkPosX(var2);
			int var4 = var1.getChunkPosZ(var2);
			this.worldClient.doPreChunk(var3, var4, true);
			this.worldClient.invalidateBlockReceiveRegion(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
			Chunk var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);
			if(var5 == null) {
				this.worldClient.doPreChunk(var3, var4, true);
				var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);
			}

			if(var5 != null) {
				var5.fillChunk(var1.getChunkCompressedData(var2), var1.field_73590_a[var2], var1.field_73588_b[var2], true);
				this.worldClient.markBlockRangeForRenderUpdate(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
				if(!(this.worldClient.provider instanceof WorldProviderSurface)) {
					var5.resetRelightChecks();
				}
			}
		}

	}

	public boolean canProcessPacketsAsync() {
		return this.mc != null && this.mc.theWorld != null && this.mc.thePlayer != null && this.worldClient != null;
	}

	public void handleGameEvent(Packet70GameEvent var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		int var3 = var1.eventType;
		int var4 = var1.gameMode;
		if(var3 >= 0 && var3 < Packet70GameEvent.clientMessage.length && Packet70GameEvent.clientMessage[var3] != null) {
			var2.addChatMessage(Packet70GameEvent.clientMessage[var3]);
		}

		if(var3 == 1) {
			this.worldClient.getWorldInfo().setRaining(true);
			this.worldClient.setRainStrength(0.0F);
		} else if(var3 == 2) {
			this.worldClient.getWorldInfo().setRaining(false);
			this.worldClient.setRainStrength(1.0F);
		} else if(var3 == 3) {
			this.mc.playerController.setGameType(EnumGameType.getByID(var4));
		} else if(var3 == 4) {
			this.mc.displayGuiScreen(new GuiWinGame());
		} else if(var3 == 5) {
			GameSettings var5 = this.mc.gameSettings;
			if(var4 == 0) {
				this.mc.displayGuiScreen(new GuiScreenDemo());
			} else if(var4 == 101) {
				this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.movement", new Object[]{Keyboard.getKeyName(var5.keyBindForward.keyCode), Keyboard.getKeyName(var5.keyBindLeft.keyCode), Keyboard.getKeyName(var5.keyBindBack.keyCode), Keyboard.getKeyName(var5.keyBindRight.keyCode)});
			} else if(var4 == 102) {
				this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.jump", new Object[]{Keyboard.getKeyName(var5.keyBindJump.keyCode)});
			} else if(var4 == 103) {
				this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.inventory", new Object[]{Keyboard.getKeyName(var5.keyBindInventory.keyCode)});
			}
		} else if(var3 == 6) {
			this.worldClient.playSound(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, "random.successful_hit", 0.18F, 0.45F, false);
		}

	}

	public void handleMapData(Packet131MapData var1) {
		if(var1.itemID == Item.map.itemID) {
			ItemMap.getMPMapData(var1.uniqueID, this.mc.theWorld).updateMPMapData(var1.itemData);
		} else {
			this.mc.getLogAgent().logWarning("Unknown itemid: " + var1.uniqueID);
		}

	}

	public void handleDoorChange(Packet61DoorChange var1) {
		if(var1.getRelativeVolumeDisabled()) {
			this.mc.theWorld.func_82739_e(var1.sfxID, var1.posX, var1.posY, var1.posZ, var1.auxData);
		} else {
			this.mc.theWorld.playAuxSFX(var1.sfxID, var1.posX, var1.posY, var1.posZ, var1.auxData);
		}

	}

	public void handleStatistic(Packet200Statistic var1) {
		this.mc.thePlayer.incrementStat(StatList.getOneShotStat(var1.statisticId), var1.amount);
	}

	public void handleEntityEffect(Packet41EntityEffect var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 instanceof EntityLiving) {
			PotionEffect var3 = new PotionEffect(var1.effectId, var1.duration, var1.effectAmplifier);
			var3.setPotionDurationMax(var1.isDurationMax());
			((EntityLiving)var2).addPotionEffect(var3);
		}
	}

	public void handleRemoveEntityEffect(Packet42RemoveEntityEffect var1) {
		Entity var2 = this.getEntityByID(var1.entityId);
		if(var2 instanceof EntityLiving) {
			((EntityLiving)var2).removePotionEffectClient(var1.effectId);
		}
	}

	public boolean isServerHandler() {
		return false;
	}

	public void handlePlayerInfo(Packet201PlayerInfo var1) {
		GuiPlayerInfo var2 = (GuiPlayerInfo)this.playerInfoMap.get(var1.playerName);
		if(var2 == null && var1.isConnected) {
			var2 = new GuiPlayerInfo(var1.playerName);
			this.playerInfoMap.put(var1.playerName, var2);
			this.playerInfoList.add(var2);
		}

		if(var2 != null && !var1.isConnected) {
			this.playerInfoMap.remove(var1.playerName);
			this.playerInfoList.remove(var2);
		}

		if(var1.isConnected && var2 != null) {
			var2.responseTime = var1.ping;
		}

	}

	public void handleKeepAlive(Packet0KeepAlive var1) {
		this.addToSendQueue(new Packet0KeepAlive(var1.randomId));
	}

	public void handlePlayerAbilities(Packet202PlayerAbilities var1) {
		EntityClientPlayerMP var2 = this.mc.thePlayer;
		var2.capabilities.isFlying = var1.getFlying();
		var2.capabilities.isCreativeMode = var1.isCreativeMode();
		var2.capabilities.disableDamage = var1.getDisableDamage();
		var2.capabilities.allowFlying = var1.getAllowFlying();
		var2.capabilities.setFlySpeed(var1.getFlySpeed());
		var2.capabilities.setPlayerWalkSpeed(var1.getWalkSpeed());
	}

	public void handleAutoComplete(Packet203AutoComplete var1) {
		String[] var2 = var1.getText().split("\u0000");
		if(this.mc.currentScreen instanceof GuiChat) {
			GuiChat var3 = (GuiChat)this.mc.currentScreen;
			var3.func_73894_a(var2);
		}

	}

	public void handleLevelSound(Packet62LevelSound var1) {
		this.mc.theWorld.playSound(var1.getEffectX(), var1.getEffectY(), var1.getEffectZ(), var1.getSoundName(), var1.getVolume(), var1.getPitch(), false);
	}

	public void handleCustomPayload(Packet250CustomPayload var1) {
		if("MC|TPack".equals(var1.channel)) {
			String[] var2 = (new String(var1.data)).split("\u0000");
			String var3 = var2[0];
			if(var2[1].equals("16")) {
				if(this.mc.texturePackList.getAcceptsTextures()) {
					this.mc.texturePackList.requestDownloadOfTexture(var3);
				} else if(this.mc.texturePackList.func_77300_f()) {
					this.mc.displayGuiScreen(new GuiYesNo(new NetClientWebTextures(this, var3), StringTranslate.getInstance().translateKey("multiplayer.texturePrompt.line1"), StringTranslate.getInstance().translateKey("multiplayer.texturePrompt.line2"), 0));
				}
			}
		} else if("MC|TrList".equals(var1.channel)) {
			DataInputStream var8 = new DataInputStream(new ByteArrayInputStream(var1.data));

			try {
				int var9 = var8.readInt();
				GuiScreen var4 = this.mc.currentScreen;
				if(var4 != null && var4 instanceof GuiMerchant && var9 == this.mc.thePlayer.openContainer.windowId) {
					IMerchant var5 = ((GuiMerchant)var4).getIMerchant();
					MerchantRecipeList var6 = MerchantRecipeList.readRecipiesFromStream(var8);
					var5.setRecipes(var6);
				}
			} catch (IOException var7) {
				var7.printStackTrace();
			}
		}

	}

	public void handleSetObjective(Packet206SetObjective var1) {
		Scoreboard var2 = this.worldClient.getScoreboard();
		ScoreObjective var3;
		if(var1.change == 0) {
			var3 = var2.func_96535_a(var1.objectiveName, ScoreObjectiveCriteria.field_96641_b);
			var3.setDisplayName(var1.objectiveDisplayName);
		} else {
			var3 = var2.getObjective(var1.objectiveName);
			if(var1.change == 1) {
				var2.func_96519_k(var3);
			} else if(var1.change == 2) {
				var3.setDisplayName(var1.objectiveDisplayName);
			}
		}

	}

	public void handleSetScore(Packet207SetScore var1) {
		Scoreboard var2 = this.worldClient.getScoreboard();
		ScoreObjective var3 = var2.getObjective(var1.scoreName);
		if(var1.updateOrRemove == 0) {
			Score var4 = var2.func_96529_a(var1.itemName, var3);
			var4.func_96647_c(var1.value);
		} else if(var1.updateOrRemove == 1) {
			var2.func_96515_c(var1.itemName);
		}

	}

	public void handleSetDisplayObjective(Packet208SetDisplayObjective var1) {
		Scoreboard var2 = this.worldClient.getScoreboard();
		if(var1.scoreName.length() == 0) {
			var2.func_96530_a(var1.scoreboardPosition, (ScoreObjective)null);
		} else {
			ScoreObjective var3 = var2.getObjective(var1.scoreName);
			var2.func_96530_a(var1.scoreboardPosition, var3);
		}

	}

	public void handleSetPlayerTeam(Packet209SetPlayerTeam var1) {
		Scoreboard var2 = this.worldClient.getScoreboard();
		ScorePlayerTeam var3;
		if(var1.mode == 0) {
			var3 = var2.func_96527_f(var1.teamName);
		} else {
			var3 = var2.func_96508_e(var1.teamName);
		}

		if(var1.mode == 0 || var1.mode == 2) {
			var3.func_96664_a(var1.teamDisplayName);
			var3.func_96666_b(var1.teamPrefix);
			var3.func_96662_c(var1.teamSuffix);
			var3.func_98298_a(var1.friendlyFire);
		}

		Iterator var4;
		String var5;
		if(var1.mode == 0 || var1.mode == 3) {
			var4 = var1.playerNames.iterator();

			while(var4.hasNext()) {
				var5 = (String)var4.next();
				var2.func_96521_a(var5, var3);
			}
		}

		if(var1.mode == 4) {
			var4 = var1.playerNames.iterator();

			while(var4.hasNext()) {
				var5 = (String)var4.next();
				var2.removePlayerFromTeam(var5, var3);
			}
		}

		if(var1.mode == 1) {
			var2.func_96511_d(var3);
		}

	}

	public void handleWorldParticles(Packet63WorldParticles var1) {
		for(int var2 = 0; var2 < var1.getQuantity(); ++var2) {
			double var3 = this.rand.nextGaussian() * (double)var1.getOffsetX();
			double var5 = this.rand.nextGaussian() * (double)var1.getOffsetY();
			double var7 = this.rand.nextGaussian() * (double)var1.getOffsetZ();
			double var9 = this.rand.nextGaussian() * (double)var1.getSpeed();
			double var11 = this.rand.nextGaussian() * (double)var1.getSpeed();
			double var13 = this.rand.nextGaussian() * (double)var1.getSpeed();
			this.worldClient.spawnParticle(var1.getParticleName(), var1.getPositionX() + var3, var1.getPositionY() + var5, var1.getPositionZ() + var7, var9, var11, var13);
		}

	}

	public INetworkManager getNetManager() {
		return this.netManager;
	}
}
