package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.server.MinecraftServer;

public class NetServerHandler extends NetHandler {
	public final INetworkManager netManager;
	private final MinecraftServer mcServer;
	public boolean connectionClosed = false;
	public EntityPlayerMP playerEntity;
	private int currentTicks;
	private int ticksForFloatKick;
	private boolean field_72584_h;
	private int keepAliveRandomID;
	private long keepAliveTimeSent;
	private static Random randomGenerator = new Random();
	private long ticksOfLastKeepAlive;
	private int chatSpamThresholdCount = 0;
	private int creativeItemCreationSpamThresholdTally = 0;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private boolean hasMoved = true;
	private IntHashMap field_72586_s = new IntHashMap();

	public NetServerHandler(MinecraftServer var1, INetworkManager var2, EntityPlayerMP var3) {
		this.mcServer = var1;
		this.netManager = var2;
		var2.setNetHandler(this);
		this.playerEntity = var3;
		var3.playerNetServerHandler = this;
	}

	public void networkTick() {
		this.field_72584_h = false;
		++this.currentTicks;
		this.mcServer.theProfiler.startSection("packetflow");
		this.netManager.processReadPackets();
		this.mcServer.theProfiler.endStartSection("keepAlive");
		if((long)this.currentTicks - this.ticksOfLastKeepAlive > 20L) {
			this.ticksOfLastKeepAlive = (long)this.currentTicks;
			this.keepAliveTimeSent = System.nanoTime() / 1000000L;
			this.keepAliveRandomID = randomGenerator.nextInt();
			this.sendPacketToPlayer(new Packet0KeepAlive(this.keepAliveRandomID));
		}

		if(this.chatSpamThresholdCount > 0) {
			--this.chatSpamThresholdCount;
		}

		if(this.creativeItemCreationSpamThresholdTally > 0) {
			--this.creativeItemCreationSpamThresholdTally;
		}

		this.mcServer.theProfiler.endStartSection("playerTick");
		this.mcServer.theProfiler.endSection();
	}

	public void kickPlayerFromServer(String var1) {
		if(!this.connectionClosed) {
			this.playerEntity.mountEntityAndWakeUp();
			this.sendPacketToPlayer(new Packet255KickDisconnect(var1));
			this.netManager.serverShutdown();
			this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(EnumChatFormatting.YELLOW + this.playerEntity.username + " left the game."));
			this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
			this.connectionClosed = true;
		}
	}

	public void handleFlying(Packet10Flying var1) {
		WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
		this.field_72584_h = true;
		if(!this.playerEntity.playerConqueredTheEnd) {
			double var3;
			if(!this.hasMoved) {
				var3 = var1.yPosition - this.lastPosY;
				if(var1.xPosition == this.lastPosX && var3 * var3 < 0.01D && var1.zPosition == this.lastPosZ) {
					this.hasMoved = true;
				}
			}

			if(this.hasMoved) {
				double var5;
				double var7;
				double var9;
				double var13;
				if(this.playerEntity.ridingEntity != null) {
					float var34 = this.playerEntity.rotationYaw;
					float var4 = this.playerEntity.rotationPitch;
					this.playerEntity.ridingEntity.updateRiderPosition();
					var5 = this.playerEntity.posX;
					var7 = this.playerEntity.posY;
					var9 = this.playerEntity.posZ;
					double var35 = 0.0D;
					var13 = 0.0D;
					if(var1.rotating) {
						var34 = var1.yaw;
						var4 = var1.pitch;
					}

					if(var1.moving && var1.yPosition == -999.0D && var1.stance == -999.0D) {
						if(Math.abs(var1.xPosition) > 1.0D || Math.abs(var1.zPosition) > 1.0D) {
							System.err.println(this.playerEntity.username + " was caught trying to crash the server with an invalid position.");
							this.kickPlayerFromServer("Nope!");
							return;
						}

						var35 = var1.xPosition;
						var13 = var1.zPosition;
					}

					this.playerEntity.onGround = var1.onGround;
					this.playerEntity.onUpdateEntity();
					this.playerEntity.moveEntity(var35, 0.0D, var13);
					this.playerEntity.setPositionAndRotation(var5, var7, var9, var34, var4);
					this.playerEntity.motionX = var35;
					this.playerEntity.motionZ = var13;
					if(this.playerEntity.ridingEntity != null) {
						var2.uncheckedUpdateEntity(this.playerEntity.ridingEntity, true);
					}

					if(this.playerEntity.ridingEntity != null) {
						this.playerEntity.ridingEntity.updateRiderPosition();
					}

					this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
					this.lastPosX = this.playerEntity.posX;
					this.lastPosY = this.playerEntity.posY;
					this.lastPosZ = this.playerEntity.posZ;
					var2.updateEntity(this.playerEntity);
					return;
				}

				if(this.playerEntity.isPlayerSleeping()) {
					this.playerEntity.onUpdateEntity();
					this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
					var2.updateEntity(this.playerEntity);
					return;
				}

				var3 = this.playerEntity.posY;
				this.lastPosX = this.playerEntity.posX;
				this.lastPosY = this.playerEntity.posY;
				this.lastPosZ = this.playerEntity.posZ;
				var5 = this.playerEntity.posX;
				var7 = this.playerEntity.posY;
				var9 = this.playerEntity.posZ;
				float var11 = this.playerEntity.rotationYaw;
				float var12 = this.playerEntity.rotationPitch;
				if(var1.moving && var1.yPosition == -999.0D && var1.stance == -999.0D) {
					var1.moving = false;
				}

				if(var1.moving) {
					var5 = var1.xPosition;
					var7 = var1.yPosition;
					var9 = var1.zPosition;
					var13 = var1.stance - var1.yPosition;
					if(!this.playerEntity.isPlayerSleeping() && (var13 > 1.65D || var13 < 0.1D)) {
						this.kickPlayerFromServer("Illegal stance");
						this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " had an illegal stance: " + var13);
						return;
					}

					if(Math.abs(var1.xPosition) > 3.2E7D || Math.abs(var1.zPosition) > 3.2E7D) {
						this.kickPlayerFromServer("Illegal position");
						return;
					}
				}

				if(var1.rotating) {
					var11 = var1.yaw;
					var12 = var1.pitch;
				}

				this.playerEntity.onUpdateEntity();
				this.playerEntity.ySize = 0.0F;
				this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
				if(!this.hasMoved) {
					return;
				}

				var13 = var5 - this.playerEntity.posX;
				double var15 = var7 - this.playerEntity.posY;
				double var17 = var9 - this.playerEntity.posZ;
				double var19 = Math.min(Math.abs(var13), Math.abs(this.playerEntity.motionX));
				double var21 = Math.min(Math.abs(var15), Math.abs(this.playerEntity.motionY));
				double var23 = Math.min(Math.abs(var17), Math.abs(this.playerEntity.motionZ));
				double var25 = var19 * var19 + var21 * var21 + var23 * var23;
				if(var25 > 100.0D && (!this.mcServer.isSinglePlayer() || !this.mcServer.getServerOwner().equals(this.playerEntity.username))) {
					this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + var19 + ", " + var21 + ", " + var23 + ")");
					this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
					return;
				}

				float var27 = 1.0F / 16.0F;
				boolean var28 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();
				if(this.playerEntity.onGround && !var1.onGround && var15 > 0.0D) {
					this.playerEntity.addExhaustion(0.2F);
				}

				this.playerEntity.moveEntity(var13, var15, var17);
				this.playerEntity.onGround = var1.onGround;
				this.playerEntity.addMovementStat(var13, var15, var17);
				double var29 = var15;
				var13 = var5 - this.playerEntity.posX;
				var15 = var7 - this.playerEntity.posY;
				if(var15 > -0.5D || var15 < 0.5D) {
					var15 = 0.0D;
				}

				var17 = var9 - this.playerEntity.posZ;
				var25 = var13 * var13 + var15 * var15 + var17 * var17;
				boolean var31 = false;
				if(var25 > 1.0D / 16.0D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
					var31 = true;
					this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " moved wrongly!");
				}

				this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
				boolean var32 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();
				if(var28 && (var31 || !var32) && !this.playerEntity.isPlayerSleeping()) {
					this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
					return;
				}

				AxisAlignedBB var33 = this.playerEntity.boundingBox.copy().expand((double)var27, (double)var27, (double)var27).addCoord(0.0D, -0.55D, 0.0D);
				if(!this.mcServer.isFlightAllowed() && !this.playerEntity.theItemInWorldManager.isCreative() && !var2.checkBlockCollision(var33)) {
					if(var29 >= -0.03125D) {
						++this.ticksForFloatKick;
						if(this.ticksForFloatKick > 80) {
							this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " was kicked for floating too long!");
							this.kickPlayerFromServer("Flying is not enabled on this server");
							return;
						}
					}
				} else {
					this.ticksForFloatKick = 0;
				}

				this.playerEntity.onGround = var1.onGround;
				this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
				this.playerEntity.updateFlyingState(this.playerEntity.posY - var3, var1.onGround);
			}

		}
	}

	public void setPlayerLocation(double var1, double var3, double var5, float var7, float var8) {
		this.hasMoved = false;
		this.lastPosX = var1;
		this.lastPosY = var3;
		this.lastPosZ = var5;
		this.playerEntity.setPositionAndRotation(var1, var3, var5, var7, var8);
		this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet13PlayerLookMove(var1, var3 + (double)1.62F, var3, var5, var7, var8, false));
	}

	public void handleBlockDig(Packet14BlockDig var1) {
		WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
		if(var1.status == 4) {
			this.playerEntity.dropOneItem(false);
		} else if(var1.status == 3) {
			this.playerEntity.dropOneItem(true);
		} else if(var1.status == 5) {
			this.playerEntity.stopUsingItem();
		} else {
			boolean var3 = false;
			if(var1.status == 0) {
				var3 = true;
			}

			if(var1.status == 1) {
				var3 = true;
			}

			if(var1.status == 2) {
				var3 = true;
			}

			int var4 = var1.xPosition;
			int var5 = var1.yPosition;
			int var6 = var1.zPosition;
			if(var3) {
				double var7 = this.playerEntity.posX - ((double)var4 + 0.5D);
				double var9 = this.playerEntity.posY - ((double)var5 + 0.5D) + 1.5D;
				double var11 = this.playerEntity.posZ - ((double)var6 + 0.5D);
				double var13 = var7 * var7 + var9 * var9 + var11 * var11;
				if(var13 > 36.0D) {
					return;
				}

				if(var5 >= this.mcServer.getBuildLimit()) {
					return;
				}
			}

			if(var1.status == 0) {
				if(!this.mcServer.func_96290_a(var2, var4, var5, var6, this.playerEntity)) {
					this.playerEntity.theItemInWorldManager.onBlockClicked(var4, var5, var6, var1.face);
				} else {
					this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
				}
			} else if(var1.status == 2) {
				this.playerEntity.theItemInWorldManager.uncheckedTryHarvestBlock(var4, var5, var6);
				if(var2.getBlockId(var4, var5, var6) != 0) {
					this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
				}
			} else if(var1.status == 1) {
				this.playerEntity.theItemInWorldManager.cancelDestroyingBlock(var4, var5, var6);
				if(var2.getBlockId(var4, var5, var6) != 0) {
					this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
				}
			}

		}
	}

	public void handlePlace(Packet15Place var1) {
		WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
		ItemStack var3 = this.playerEntity.inventory.getCurrentItem();
		boolean var4 = false;
		int var5 = var1.getXPosition();
		int var6 = var1.getYPosition();
		int var7 = var1.getZPosition();
		int var8 = var1.getDirection();
		if(var1.getDirection() == 255) {
			if(var3 == null) {
				return;
			}

			this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, var2, var3);
		} else if(var1.getYPosition() < this.mcServer.getBuildLimit() - 1 || var1.getDirection() != 1 && var1.getYPosition() < this.mcServer.getBuildLimit()) {
			if(this.hasMoved && this.playerEntity.getDistanceSq((double)var5 + 0.5D, (double)var6 + 0.5D, (double)var7 + 0.5D) < 64.0D && !this.mcServer.func_96290_a(var2, var5, var6, var7, this.playerEntity)) {
				this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, var2, var3, var5, var6, var7, var8, var1.getXOffset(), var1.getYOffset(), var1.getZOffset());
			}

			var4 = true;
		} else {
			this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat("" + EnumChatFormatting.GRAY + "Height limit for building is " + this.mcServer.getBuildLimit()));
			var4 = true;
		}

		if(var4) {
			this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
			if(var8 == 0) {
				--var6;
			}

			if(var8 == 1) {
				++var6;
			}

			if(var8 == 2) {
				--var7;
			}

			if(var8 == 3) {
				++var7;
			}

			if(var8 == 4) {
				--var5;
			}

			if(var8 == 5) {
				++var5;
			}

			this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
		}

		var3 = this.playerEntity.inventory.getCurrentItem();
		if(var3 != null && var3.stackSize == 0) {
			this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
			var3 = null;
		}

		if(var3 == null || var3.getMaxItemUseDuration() == 0) {
			this.playerEntity.playerInventoryBeingManipulated = true;
			this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
			Slot var9 = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
			this.playerEntity.openContainer.detectAndSendChanges();
			this.playerEntity.playerInventoryBeingManipulated = false;
			if(!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), var1.getItemStack())) {
				this.sendPacketToPlayer(new Packet103SetSlot(this.playerEntity.openContainer.windowId, var9.slotNumber, this.playerEntity.inventory.getCurrentItem()));
			}
		}

	}

	public void handleErrorMessage(String var1, Object[] var2) {
		this.mcServer.getLogAgent().logInfo(this.playerEntity.username + " lost connection: " + var1);
		this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(EnumChatFormatting.YELLOW + this.playerEntity.getTranslatedEntityName() + " left the game."));
		this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
		this.connectionClosed = true;
		if(this.mcServer.isSinglePlayer() && this.playerEntity.username.equals(this.mcServer.getServerOwner())) {
			this.mcServer.getLogAgent().logInfo("Stopping singleplayer server as player logged out");
			this.mcServer.initiateShutdown();
		}

	}

	public void unexpectedPacket(Packet var1) {
		this.mcServer.getLogAgent().logWarning(this.getClass() + " wasn\'t prepared to deal with a " + var1.getClass());
		this.kickPlayerFromServer("Protocol error, unexpected packet");
	}

	public void sendPacketToPlayer(Packet var1) {
		if(var1 instanceof Packet3Chat) {
			Packet3Chat var2 = (Packet3Chat)var1;
			int var3 = this.playerEntity.getChatVisibility();
			if(var3 == 2) {
				return;
			}

			if(var3 == 1 && !var2.getIsServer()) {
				return;
			}
		}

		try {
			this.netManager.addToSendQueue(var1);
		} catch (Throwable var5) {
			CrashReport var6 = CrashReport.makeCrashReport(var5, "Sending packet");
			CrashReportCategory var4 = var6.makeCategory("Packet being sent");
			var4.addCrashSectionCallable("Packet ID", new CallablePacketID(this, var1));
			var4.addCrashSectionCallable("Packet class", new CallablePacketClass(this, var1));
			throw new ReportedException(var6);
		}
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch var1) {
		if(var1.id >= 0 && var1.id < InventoryPlayer.getHotbarSize()) {
			this.playerEntity.inventory.currentItem = var1.id;
		} else {
			this.mcServer.getLogAgent().logWarning(this.playerEntity.username + " tried to set an invalid carried item");
		}
	}

	public void handleChat(Packet3Chat var1) {
		if(this.playerEntity.getChatVisibility() == 2) {
			this.sendPacketToPlayer(new Packet3Chat("Cannot send chat message."));
		} else {
			String var2 = var1.message;
			if(var2.length() > 100) {
				this.kickPlayerFromServer("Chat message too long");
			} else {
				var2 = var2.trim();

				for(int var3 = 0; var3 < var2.length(); ++var3) {
					if(!ChatAllowedCharacters.isAllowedCharacter(var2.charAt(var3))) {
						this.kickPlayerFromServer("Illegal characters in chat");
						return;
					}
				}

				if(var2.startsWith("/")) {
					this.handleSlashCommand(var2);
				} else {
					if(this.playerEntity.getChatVisibility() == 1) {
						this.sendPacketToPlayer(new Packet3Chat("Cannot send chat message."));
						return;
					}

					var2 = "<" + this.playerEntity.getTranslatedEntityName() + "> " + var2;
					this.mcServer.getLogAgent().logInfo(var2);
					this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(var2, false));
				}

				this.chatSpamThresholdCount += 20;
				if(this.chatSpamThresholdCount > 200 && !this.mcServer.getConfigurationManager().areCommandsAllowed(this.playerEntity.username)) {
					this.kickPlayerFromServer("disconnect.spam");
				}

			}
		}
	}

	private void handleSlashCommand(String var1) {
		this.mcServer.getCommandManager().executeCommand(this.playerEntity, var1);
	}

	public void handleAnimation(Packet18Animation var1) {
		if(var1.animate == 1) {
			this.playerEntity.swingItem();
		}

	}

	public void handleEntityAction(Packet19EntityAction var1) {
		if(var1.state == 1) {
			this.playerEntity.setSneaking(true);
		} else if(var1.state == 2) {
			this.playerEntity.setSneaking(false);
		} else if(var1.state == 4) {
			this.playerEntity.setSprinting(true);
		} else if(var1.state == 5) {
			this.playerEntity.setSprinting(false);
		} else if(var1.state == 3) {
			this.playerEntity.wakeUpPlayer(false, true, true);
			this.hasMoved = false;
		}

	}

	public void handleKickDisconnect(Packet255KickDisconnect var1) {
		this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
	}

	public int packetSize() {
		return this.netManager.packetSize();
	}

	public void handleUseEntity(Packet7UseEntity var1) {
		WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
		Entity var3 = var2.getEntityByID(var1.targetEntity);
		if(var3 != null) {
			boolean var4 = this.playerEntity.canEntityBeSeen(var3);
			double var5 = 36.0D;
			if(!var4) {
				var5 = 9.0D;
			}

			if(this.playerEntity.getDistanceSqToEntity(var3) < var5) {
				if(var1.isLeftClick == 0) {
					this.playerEntity.interactWith(var3);
				} else if(var1.isLeftClick == 1) {
					this.playerEntity.attackTargetEntityWithCurrentItem(var3);
				}
			}
		}

	}

	public void handleClientCommand(Packet205ClientCommand var1) {
		if(var1.forceRespawn == 1) {
			if(this.playerEntity.playerConqueredTheEnd) {
				this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, 0, true);
			} else if(this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
				if(this.mcServer.isSinglePlayer() && this.playerEntity.username.equals(this.mcServer.getServerOwner())) {
					this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
					this.mcServer.deleteWorldAndStopServer();
				} else {
					BanEntry var2 = new BanEntry(this.playerEntity.username);
					var2.setBanReason("Death in Hardcore");
					this.mcServer.getConfigurationManager().getBannedPlayers().put(var2);
					this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
				}
			} else {
				if(this.playerEntity.getHealth() > 0) {
					return;
				}

				this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, 0, false);
			}
		}

	}

	public boolean canProcessPacketsAsync() {
		return true;
	}

	public void handleRespawn(Packet9Respawn var1) {
	}

	public void handleCloseWindow(Packet101CloseWindow var1) {
		this.playerEntity.closeInventory();
	}

	public void handleWindowClick(Packet102WindowClick var1) {
		if(this.playerEntity.openContainer.windowId == var1.window_Id && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
			ItemStack var2 = this.playerEntity.openContainer.slotClick(var1.inventorySlot, var1.mouseClick, var1.holdingShift, this.playerEntity);
			if(ItemStack.areItemStacksEqual(var1.itemStack, var2)) {
				this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(var1.window_Id, var1.action, true));
				this.playerEntity.playerInventoryBeingManipulated = true;
				this.playerEntity.openContainer.detectAndSendChanges();
				this.playerEntity.updateHeldItem();
				this.playerEntity.playerInventoryBeingManipulated = false;
			} else {
				this.field_72586_s.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(var1.action));
				this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(var1.window_Id, var1.action, false));
				this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, false);
				ArrayList var3 = new ArrayList();

				for(int var4 = 0; var4 < this.playerEntity.openContainer.inventorySlots.size(); ++var4) {
					var3.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(var4)).getStack());
				}

				this.playerEntity.sendContainerAndContentsToPlayer(this.playerEntity.openContainer, var3);
			}
		}

	}

	public void handleEnchantItem(Packet108EnchantItem var1) {
		if(this.playerEntity.openContainer.windowId == var1.windowId && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
			this.playerEntity.openContainer.enchantItem(this.playerEntity, var1.enchantment);
			this.playerEntity.openContainer.detectAndSendChanges();
		}

	}

	public void handleCreativeSetSlot(Packet107CreativeSetSlot var1) {
		if(this.playerEntity.theItemInWorldManager.isCreative()) {
			boolean var2 = var1.slot < 0;
			ItemStack var3 = var1.itemStack;
			boolean var4 = var1.slot >= 1 && var1.slot < 36 + InventoryPlayer.getHotbarSize();
			boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null;
			boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;
			if(var4 && var5 && var6) {
				if(var3 == null) {
					this.playerEntity.inventoryContainer.putStackInSlot(var1.slot, (ItemStack)null);
				} else {
					this.playerEntity.inventoryContainer.putStackInSlot(var1.slot, var3);
				}

				this.playerEntity.inventoryContainer.setPlayerIsPresent(this.playerEntity, true);
			} else if(var2 && var5 && var6 && this.creativeItemCreationSpamThresholdTally < 200) {
				this.creativeItemCreationSpamThresholdTally += 20;
				EntityItem var7 = this.playerEntity.dropPlayerItem(var3);
				if(var7 != null) {
					var7.setAgeToCreativeDespawnTime();
				}
			}
		}

	}

	public void handleTransaction(Packet106Transaction var1) {
		Short var2 = (Short)this.field_72586_s.lookup(this.playerEntity.openContainer.windowId);
		if(var2 != null && var1.shortWindowId == var2.shortValue() && this.playerEntity.openContainer.windowId == var1.windowId && !this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity)) {
			this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, true);
		}

	}

	public void handleUpdateSign(Packet130UpdateSign var1) {
		WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
		if(var2.blockExists(var1.xPosition, var1.yPosition, var1.zPosition)) {
			TileEntity var3 = var2.getBlockTileEntity(var1.xPosition, var1.yPosition, var1.zPosition);
			if(var3 instanceof TileEntitySign) {
				TileEntitySign var4 = (TileEntitySign)var3;
				if(!var4.isEditable()) {
					this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
					return;
				}
			}

			int var6;
			int var8;
			for(var8 = 0; var8 < 4; ++var8) {
				boolean var5 = true;
				if(var1.signLines[var8].length() > 15) {
					var5 = false;
				} else {
					for(var6 = 0; var6 < var1.signLines[var8].length(); ++var6) {
						if(ChatAllowedCharacters.allowedCharacters.indexOf(var1.signLines[var8].charAt(var6)) < 0) {
							var5 = false;
						}
					}
				}

				if(!var5) {
					var1.signLines[var8] = "!?";
				}
			}

			if(var3 instanceof TileEntitySign) {
				var8 = var1.xPosition;
				int var9 = var1.yPosition;
				var6 = var1.zPosition;
				TileEntitySign var7 = (TileEntitySign)var3;
				System.arraycopy(var1.signLines, 0, var7.signText, 0, 4);
				var7.onInventoryChanged();
				var2.markBlockForUpdate(var8, var9, var6);
			}
		}

	}

	public void handleKeepAlive(Packet0KeepAlive var1) {
		if(var1.randomId == this.keepAliveRandomID) {
			int var2 = (int)(System.nanoTime() / 1000000L - this.keepAliveTimeSent);
			this.playerEntity.ping = (this.playerEntity.ping * 3 + var2) / 4;
		}

	}

	public boolean isServerHandler() {
		return true;
	}

	public void handlePlayerAbilities(Packet202PlayerAbilities var1) {
		this.playerEntity.capabilities.isFlying = var1.getFlying() && this.playerEntity.capabilities.allowFlying;
	}

	public void handleAutoComplete(Packet203AutoComplete var1) {
		StringBuilder var2 = new StringBuilder();

		String var4;
		for(Iterator var3 = this.mcServer.getPossibleCompletions(this.playerEntity, var1.getText()).iterator(); var3.hasNext(); var2.append(var4)) {
			var4 = (String)var3.next();
			if(var2.length() > 0) {
				var2.append("\u0000");
			}
		}

		this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet203AutoComplete(var2.toString()));
	}

	public void handleClientInfo(Packet204ClientInfo var1) {
		this.playerEntity.updateClientInfo(var1);
	}

	public void handleCustomPayload(Packet250CustomPayload var1) {
		DataInputStream var2;
		ItemStack var3;
		ItemStack var4;
		if("MC|BEdit".equals(var1.channel)) {
			try {
				var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
				var3 = Packet.readItemStack(var2);
				if(!ItemWritableBook.validBookTagPages(var3.getTagCompound())) {
					throw new IOException("Invalid book tag!");
				}

				var4 = this.playerEntity.inventory.getCurrentItem();
				if(var3 != null && var3.itemID == Item.writableBook.itemID && var3.itemID == var4.itemID) {
					var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
				}
			} catch (Exception var12) {
				var12.printStackTrace();
			}
		} else if("MC|BSign".equals(var1.channel)) {
			try {
				var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
				var3 = Packet.readItemStack(var2);
				if(!ItemEditableBook.validBookTagContents(var3.getTagCompound())) {
					throw new IOException("Invalid book tag!");
				}

				var4 = this.playerEntity.inventory.getCurrentItem();
				if(var3 != null && var3.itemID == Item.writtenBook.itemID && var4.itemID == Item.writableBook.itemID) {
					var4.setTagInfo("author", new NBTTagString("author", this.playerEntity.username));
					var4.setTagInfo("title", new NBTTagString("title", var3.getTagCompound().getString("title")));
					var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
					var4.itemID = Item.writtenBook.itemID;
				}
			} catch (Exception var11) {
				var11.printStackTrace();
			}
		} else {
			int var13;
			if("MC|TrSel".equals(var1.channel)) {
				try {
					var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
					var13 = var2.readInt();
					Container var15 = this.playerEntity.openContainer;
					if(var15 instanceof ContainerMerchant) {
						((ContainerMerchant)var15).setCurrentRecipeIndex(var13);
					}
				} catch (Exception var10) {
					var10.printStackTrace();
				}
			} else {
				int var17;
				if("MC|AdvCdm".equals(var1.channel)) {
					if(!this.mcServer.isCommandBlockEnabled()) {
						this.playerEntity.sendChatToPlayer(this.playerEntity.translateString("advMode.notEnabled", new Object[0]));
					} else if(this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
						try {
							var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
							var13 = var2.readInt();
							var17 = var2.readInt();
							int var5 = var2.readInt();
							String var6 = Packet.readString(var2, 256);
							TileEntity var7 = this.playerEntity.worldObj.getBlockTileEntity(var13, var17, var5);
							if(var7 != null && var7 instanceof TileEntityCommandBlock) {
								((TileEntityCommandBlock)var7).setCommand(var6);
								this.playerEntity.worldObj.markBlockForUpdate(var13, var17, var5);
								this.playerEntity.sendChatToPlayer("Command set: " + var6);
							}
						} catch (Exception var9) {
							var9.printStackTrace();
						}
					} else {
						this.playerEntity.sendChatToPlayer(this.playerEntity.translateString("advMode.notAllowed", new Object[0]));
					}
				} else if("MC|Beacon".equals(var1.channel)) {
					if(this.playerEntity.openContainer instanceof ContainerBeacon) {
						try {
							var2 = new DataInputStream(new ByteArrayInputStream(var1.data));
							var13 = var2.readInt();
							var17 = var2.readInt();
							ContainerBeacon var18 = (ContainerBeacon)this.playerEntity.openContainer;
							Slot var19 = var18.getSlot(0);
							if(var19.getHasStack()) {
								var19.decrStackSize(1);
								TileEntityBeacon var20 = var18.getBeacon();
								var20.setPrimaryEffect(var13);
								var20.setSecondaryEffect(var17);
								var20.onInventoryChanged();
							}
						} catch (Exception var8) {
							var8.printStackTrace();
						}
					}
				} else if("MC|ItemName".equals(var1.channel) && this.playerEntity.openContainer instanceof ContainerRepair) {
					ContainerRepair var14 = (ContainerRepair)this.playerEntity.openContainer;
					if(var1.data != null && var1.data.length >= 1) {
						String var16 = ChatAllowedCharacters.filerAllowedCharacters(new String(var1.data));
						if(var16.length() <= 30) {
							var14.updateItemName(var16);
						}
					} else {
						var14.updateItemName("");
					}
				}
			}
		}

	}
}
