package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EntityTracker {
	private final WorldServer theWorld;
	private Set trackedEntities = new HashSet();
	private IntHashMap trackedEntityIDs = new IntHashMap();
	private int entityViewDistance;

	public EntityTracker(WorldServer var1) {
		this.theWorld = var1;
		this.entityViewDistance = var1.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
	}

	public void addEntityToTracker(Entity var1) {
		if(var1 instanceof EntityPlayerMP) {
			this.addEntityToTracker(var1, 512, 2);
			EntityPlayerMP var2 = (EntityPlayerMP)var1;
			Iterator var3 = this.trackedEntities.iterator();

			while(var3.hasNext()) {
				EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
				if(var4.myEntity != var2) {
					var4.tryStartWachingThis(var2);
				}
			}
		} else if(var1 instanceof EntityFishHook) {
			this.addEntityToTracker(var1, 64, 5, true);
		} else if(var1 instanceof EntityArrow) {
			this.addEntityToTracker(var1, 64, 20, false);
		} else if(var1 instanceof EntitySmallFireball) {
			this.addEntityToTracker(var1, 64, 10, false);
		} else if(var1 instanceof EntityFireball) {
			this.addEntityToTracker(var1, 64, 10, false);
		} else if(var1 instanceof EntitySnowball) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityEnderPearl) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityEnderEye) {
			this.addEntityToTracker(var1, 64, 4, true);
		} else if(var1 instanceof EntityEgg) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityPotion) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityExpBottle) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityFireworkRocket) {
			this.addEntityToTracker(var1, 64, 10, true);
		} else if(var1 instanceof EntityItem) {
			this.addEntityToTracker(var1, 64, 20, true);
		} else if(var1 instanceof EntityMinecart) {
			this.addEntityToTracker(var1, 80, 3, true);
		} else if(var1 instanceof EntityBoat) {
			this.addEntityToTracker(var1, 80, 3, true);
		} else if(var1 instanceof EntitySquid) {
			this.addEntityToTracker(var1, 64, 3, true);
		} else if(var1 instanceof EntityWither) {
			this.addEntityToTracker(var1, 80, 3, false);
		} else if(var1 instanceof EntityBat) {
			this.addEntityToTracker(var1, 80, 3, false);
		} else if(var1 instanceof IAnimals) {
			this.addEntityToTracker(var1, 80, 3, true);
		} else if(var1 instanceof EntityDragon) {
			this.addEntityToTracker(var1, 160, 3, true);
		} else if(var1 instanceof EntityTNTPrimed) {
			this.addEntityToTracker(var1, 160, 10, true);
		} else if(var1 instanceof EntityFallingSand) {
			this.addEntityToTracker(var1, 160, 20, true);
		} else if(var1 instanceof EntityPainting) {
			this.addEntityToTracker(var1, 160, Integer.MAX_VALUE, false);
		} else if(var1 instanceof EntityXPOrb) {
			this.addEntityToTracker(var1, 160, 20, true);
		} else if(var1 instanceof EntityEnderCrystal) {
			this.addEntityToTracker(var1, 256, Integer.MAX_VALUE, false);
		} else if(var1 instanceof EntityItemFrame) {
			this.addEntityToTracker(var1, 160, Integer.MAX_VALUE, false);
		}

	}

	public void addEntityToTracker(Entity var1, int var2, int var3) {
		this.addEntityToTracker(var1, var2, var3, false);
	}

	public void addEntityToTracker(Entity var1, int var2, int var3, boolean var4) {
		if(var2 > this.entityViewDistance) {
			var2 = this.entityViewDistance;
		}

		try {
			if(this.trackedEntityIDs.containsItem(var1.entityId)) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			EntityTrackerEntry var5 = new EntityTrackerEntry(var1, var2, var3, var4);
			this.trackedEntities.add(var5);
			this.trackedEntityIDs.addKey(var1.entityId, var5);
			var5.sendEventsToPlayers(this.theWorld.playerEntities);
		} catch (Throwable var11) {
			CrashReport var6 = CrashReport.makeCrashReport(var11, "Adding entity to track");
			CrashReportCategory var7 = var6.makeCategory("Entity To Track");
			var7.addCrashSection("Tracking range", var2 + " blocks");
			var7.addCrashSectionCallable("Update interval", new CallableEntityTracker(this, var3));
			var1.func_85029_a(var7);
			CrashReportCategory var8 = var6.makeCategory("Entity That Is Already Tracked");
			((EntityTrackerEntry)this.trackedEntityIDs.lookup(var1.entityId)).myEntity.func_85029_a(var8);

			try {
				throw new ReportedException(var6);
			} catch (ReportedException var10) {
				System.err.println("\"Silently\" catching entity tracking error.");
				var10.printStackTrace();
			}
		}

	}

	public void removeEntityFromAllTrackingPlayers(Entity var1) {
		if(var1 instanceof EntityPlayerMP) {
			EntityPlayerMP var2 = (EntityPlayerMP)var1;
			Iterator var3 = this.trackedEntities.iterator();

			while(var3.hasNext()) {
				EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
				var4.removeFromWatchingList(var2);
			}
		}

		EntityTrackerEntry var5 = (EntityTrackerEntry)this.trackedEntityIDs.removeObject(var1.entityId);
		if(var5 != null) {
			this.trackedEntities.remove(var5);
			var5.informAllAssociatedPlayersOfItemDestruction();
		}

	}

	public void updateTrackedEntities() {
		ArrayList var1 = new ArrayList();
		Iterator var2 = this.trackedEntities.iterator();

		while(var2.hasNext()) {
			EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
			var3.sendLocationToAllClients(this.theWorld.playerEntities);
			if(var3.playerEntitiesUpdated && var3.myEntity instanceof EntityPlayerMP) {
				var1.add((EntityPlayerMP)var3.myEntity);
			}
		}

		for(int var6 = 0; var6 < var1.size(); ++var6) {
			EntityPlayerMP var7 = (EntityPlayerMP)var1.get(var6);
			Iterator var4 = this.trackedEntities.iterator();

			while(var4.hasNext()) {
				EntityTrackerEntry var5 = (EntityTrackerEntry)var4.next();
				if(var5.myEntity != var7) {
					var5.tryStartWachingThis(var7);
				}
			}
		}

	}

	public void sendPacketToAllPlayersTrackingEntity(Entity var1, Packet var2) {
		EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(var1.entityId);
		if(var3 != null) {
			var3.sendPacketToAllTrackingPlayers(var2);
		}

	}

	public void sendPacketToAllAssociatedPlayers(Entity var1, Packet var2) {
		EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(var1.entityId);
		if(var3 != null) {
			var3.sendPacketToAllAssociatedPlayers(var2);
		}

	}

	public void removePlayerFromTrackers(EntityPlayerMP var1) {
		Iterator var2 = this.trackedEntities.iterator();

		while(var2.hasNext()) {
			EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
			var3.removePlayerFromTracker(var1);
		}

	}

	public void func_85172_a(EntityPlayerMP var1, Chunk var2) {
		Iterator var3 = this.trackedEntities.iterator();

		while(var3.hasNext()) {
			EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
			if(var4.myEntity != var1 && var4.myEntity.chunkCoordX == var2.xPosition && var4.myEntity.chunkCoordZ == var2.zPosition) {
				var4.tryStartWachingThis(var1);
			}
		}

	}
}
