package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

class PlayerInstance {
	private final List playersInChunk;
	private final ChunkCoordIntPair chunkLocation;
	private short[] locationOfBlockChange;
	private int numberOfTilesToUpdate;
	private int field_73260_f;
	final PlayerManager thePlayerManager;

	public PlayerInstance(PlayerManager var1, int var2, int var3) {
		this.thePlayerManager = var1;
		this.playersInChunk = new ArrayList();
		this.locationOfBlockChange = new short[64];
		this.numberOfTilesToUpdate = 0;
		this.chunkLocation = new ChunkCoordIntPair(var2, var3);
		var1.getWorldServer().theChunkProviderServer.loadChunk(var2, var3);
	}

	public void addPlayerToChunkWatchingList(EntityPlayerMP var1) {
		if(this.playersInChunk.contains(var1)) {
			throw new IllegalStateException("Failed to add player. " + var1 + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
		} else {
			this.playersInChunk.add(var1);
			var1.loadedChunks.add(this.chunkLocation);
		}
	}

	public void sendThisChunkToPlayer(EntityPlayerMP var1) {
		if(this.playersInChunk.contains(var1)) {
			var1.playerNetServerHandler.sendPacketToPlayer(new Packet51MapChunk(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), true, 0));
			this.playersInChunk.remove(var1);
			var1.loadedChunks.remove(this.chunkLocation);
			if(this.playersInChunk.isEmpty()) {
				long var2 = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
				PlayerManager.getChunkWatchers(this.thePlayerManager).remove(var2);
				if(this.numberOfTilesToUpdate > 0) {
					PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).remove(this);
				}

				this.thePlayerManager.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
			}

		}
	}

	public void flagChunkForUpdate(int var1, int var2, int var3) {
		if(this.numberOfTilesToUpdate == 0) {
			PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).add(this);
		}

		this.field_73260_f |= 1 << (var2 >> 4);
		if(this.numberOfTilesToUpdate < 64) {
			short var4 = (short)(var1 << 12 | var3 << 8 | var2);

			for(int var5 = 0; var5 < this.numberOfTilesToUpdate; ++var5) {
				if(this.locationOfBlockChange[var5] == var4) {
					return;
				}
			}

			this.locationOfBlockChange[this.numberOfTilesToUpdate++] = var4;
		}

	}

	public void sendToAllPlayersWatchingChunk(Packet var1) {
		for(int var2 = 0; var2 < this.playersInChunk.size(); ++var2) {
			EntityPlayerMP var3 = (EntityPlayerMP)this.playersInChunk.get(var2);
			if(!var3.loadedChunks.contains(this.chunkLocation)) {
				var3.playerNetServerHandler.sendPacketToPlayer(var1);
			}
		}

	}

	public void sendChunkUpdate() {
		if(this.numberOfTilesToUpdate != 0) {
			int var1;
			int var2;
			int var3;
			if(this.numberOfTilesToUpdate == 1) {
				var1 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
				var2 = this.locationOfBlockChange[0] & 255;
				var3 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
				this.sendToAllPlayersWatchingChunk(new Packet53BlockChange(var1, var2, var3, PlayerManager.getWorldServer(this.thePlayerManager)));
				if(PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var1, var2, var3)) {
					this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var1, var2, var3));
				}
			} else {
				int var4;
				if(this.numberOfTilesToUpdate == 64) {
					var1 = this.chunkLocation.chunkXPos * 16;
					var2 = this.chunkLocation.chunkZPos * 16;
					this.sendToAllPlayersWatchingChunk(new Packet51MapChunk(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.field_73260_f));

					for(var3 = 0; var3 < 16; ++var3) {
						if((this.field_73260_f & 1 << var3) != 0) {
							var4 = var3 << 4;
							List var5 = PlayerManager.getWorldServer(this.thePlayerManager).getAllTileEntityInBox(var1, var4, var2, var1 + 16, var4 + 16, var2 + 16);

							for(int var6 = 0; var6 < var5.size(); ++var6) {
								this.sendTileToAllPlayersWatchingChunk((TileEntity)var5.get(var6));
							}
						}
					}
				} else {
					this.sendToAllPlayersWatchingChunk(new Packet52MultiBlockChange(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, this.locationOfBlockChange, this.numberOfTilesToUpdate, PlayerManager.getWorldServer(this.thePlayerManager)));

					for(var1 = 0; var1 < this.numberOfTilesToUpdate; ++var1) {
						var2 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[var1] >> 12 & 15);
						var3 = this.locationOfBlockChange[var1] & 255;
						var4 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[var1] >> 8 & 15);
						if(PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var2, var3, var4)) {
							this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var2, var3, var4));
						}
					}
				}
			}

			this.numberOfTilesToUpdate = 0;
			this.field_73260_f = 0;
		}
	}

	private void sendTileToAllPlayersWatchingChunk(TileEntity var1) {
		if(var1 != null) {
			Packet var2 = var1.getDescriptionPacket();
			if(var2 != null) {
				this.sendToAllPlayersWatchingChunk(var2);
			}
		}

	}

	static ChunkCoordIntPair getChunkLocation(PlayerInstance var0) {
		return var0.chunkLocation;
	}

	static List getPlayersInChunk(PlayerInstance var0) {
		return var0.playersInChunk;
	}
}
