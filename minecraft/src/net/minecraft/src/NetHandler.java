package net.minecraft.src;

public abstract class NetHandler {
	public abstract boolean isServerHandler();

	public void handleMapChunk(Packet51MapChunk var1) {
	}

	public void unexpectedPacket(Packet var1) {
	}

	public void handleErrorMessage(String var1, Object[] var2) {
	}

	public void handleKickDisconnect(Packet255KickDisconnect var1) {
		this.unexpectedPacket(var1);
	}

	public void handleLogin(Packet1Login var1) {
		this.unexpectedPacket(var1);
	}

	public void handleFlying(Packet10Flying var1) {
		this.unexpectedPacket(var1);
	}

	public void handleMultiBlockChange(Packet52MultiBlockChange var1) {
		this.unexpectedPacket(var1);
	}

	public void handleBlockDig(Packet14BlockDig var1) {
		this.unexpectedPacket(var1);
	}

	public void handleBlockChange(Packet53BlockChange var1) {
		this.unexpectedPacket(var1);
	}

	public void handleNamedEntitySpawn(Packet20NamedEntitySpawn var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntity(Packet30Entity var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityTeleport(Packet34EntityTeleport var1) {
		this.unexpectedPacket(var1);
	}

	public void handlePlace(Packet15Place var1) {
		this.unexpectedPacket(var1);
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch var1) {
		this.unexpectedPacket(var1);
	}

	public void handleDestroyEntity(Packet29DestroyEntity var1) {
		this.unexpectedPacket(var1);
	}

	public void handleCollect(Packet22Collect var1) {
		this.unexpectedPacket(var1);
	}

	public void handleChat(Packet3Chat var1) {
		this.unexpectedPacket(var1);
	}

	public void handleVehicleSpawn(Packet23VehicleSpawn var1) {
		this.unexpectedPacket(var1);
	}

	public void handleAnimation(Packet18Animation var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityAction(Packet19EntityAction var1) {
		this.unexpectedPacket(var1);
	}

	public void handleClientProtocol(Packet2ClientProtocol var1) {
		this.unexpectedPacket(var1);
	}

	public void handleServerAuthData(Packet253ServerAuthData var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSharedKey(Packet252SharedKey var1) {
		this.unexpectedPacket(var1);
	}

	public void handleMobSpawn(Packet24MobSpawn var1) {
		this.unexpectedPacket(var1);
	}

	public void handleUpdateTime(Packet4UpdateTime var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSpawnPosition(Packet6SpawnPosition var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityVelocity(Packet28EntityVelocity var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityMetadata(Packet40EntityMetadata var1) {
		this.unexpectedPacket(var1);
	}

	public void handleAttachEntity(Packet39AttachEntity var1) {
		this.unexpectedPacket(var1);
	}

	public void handleUseEntity(Packet7UseEntity var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityStatus(Packet38EntityStatus var1) {
		this.unexpectedPacket(var1);
	}

	public void handleUpdateHealth(Packet8UpdateHealth var1) {
		this.unexpectedPacket(var1);
	}

	public void handleRespawn(Packet9Respawn var1) {
		this.unexpectedPacket(var1);
	}

	public void handleExplosion(Packet60Explosion var1) {
		this.unexpectedPacket(var1);
	}

	public void handleOpenWindow(Packet100OpenWindow var1) {
		this.unexpectedPacket(var1);
	}

	public void handleCloseWindow(Packet101CloseWindow var1) {
		this.unexpectedPacket(var1);
	}

	public void handleWindowClick(Packet102WindowClick var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSetSlot(Packet103SetSlot var1) {
		this.unexpectedPacket(var1);
	}

	public void handleWindowItems(Packet104WindowItems var1) {
		this.unexpectedPacket(var1);
	}

	public void handleUpdateSign(Packet130UpdateSign var1) {
		this.unexpectedPacket(var1);
	}

	public void handleUpdateProgressbar(Packet105UpdateProgressbar var1) {
		this.unexpectedPacket(var1);
	}

	public void handlePlayerInventory(Packet5PlayerInventory var1) {
		this.unexpectedPacket(var1);
	}

	public void handleTransaction(Packet106Transaction var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityPainting(Packet25EntityPainting var1) {
		this.unexpectedPacket(var1);
	}

	public void handleBlockEvent(Packet54PlayNoteBlock var1) {
		this.unexpectedPacket(var1);
	}

	public void handleStatistic(Packet200Statistic var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSleep(Packet17Sleep var1) {
		this.unexpectedPacket(var1);
	}

	public void handleGameEvent(Packet70GameEvent var1) {
		this.unexpectedPacket(var1);
	}

	public void handleWeather(Packet71Weather var1) {
		this.unexpectedPacket(var1);
	}

	public void handleMapData(Packet131MapData var1) {
		this.unexpectedPacket(var1);
	}

	public void handleDoorChange(Packet61DoorChange var1) {
		this.unexpectedPacket(var1);
	}

	public void handleServerPing(Packet254ServerPing var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityEffect(Packet41EntityEffect var1) {
		this.unexpectedPacket(var1);
	}

	public void handleRemoveEntityEffect(Packet42RemoveEntityEffect var1) {
		this.unexpectedPacket(var1);
	}

	public void handlePlayerInfo(Packet201PlayerInfo var1) {
		this.unexpectedPacket(var1);
	}

	public void handleKeepAlive(Packet0KeepAlive var1) {
		this.unexpectedPacket(var1);
	}

	public void handleExperience(Packet43Experience var1) {
		this.unexpectedPacket(var1);
	}

	public void handleCreativeSetSlot(Packet107CreativeSetSlot var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEntityExpOrb(Packet26EntityExpOrb var1) {
		this.unexpectedPacket(var1);
	}

	public void handleEnchantItem(Packet108EnchantItem var1) {
	}

	public void handleCustomPayload(Packet250CustomPayload var1) {
	}

	public void handleEntityHeadRotation(Packet35EntityHeadRotation var1) {
		this.unexpectedPacket(var1);
	}

	public void handleTileEntityData(Packet132TileEntityData var1) {
		this.unexpectedPacket(var1);
	}

	public void handlePlayerAbilities(Packet202PlayerAbilities var1) {
		this.unexpectedPacket(var1);
	}

	public void handleAutoComplete(Packet203AutoComplete var1) {
		this.unexpectedPacket(var1);
	}

	public void handleClientInfo(Packet204ClientInfo var1) {
		this.unexpectedPacket(var1);
	}

	public void handleLevelSound(Packet62LevelSound var1) {
		this.unexpectedPacket(var1);
	}

	public void handleBlockDestroy(Packet55BlockDestroy var1) {
		this.unexpectedPacket(var1);
	}

	public void handleClientCommand(Packet205ClientCommand var1) {
	}

	public void handleMapChunks(Packet56MapChunks var1) {
		this.unexpectedPacket(var1);
	}

	public boolean canProcessPacketsAsync() {
		return false;
	}

	public void handleSetObjective(Packet206SetObjective var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSetScore(Packet207SetScore var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSetDisplayObjective(Packet208SetDisplayObjective var1) {
		this.unexpectedPacket(var1);
	}

	public void handleSetPlayerTeam(Packet209SetPlayerTeam var1) {
		this.unexpectedPacket(var1);
	}

	public void handleWorldParticles(Packet63WorldParticles var1) {
		this.unexpectedPacket(var1);
	}
}
