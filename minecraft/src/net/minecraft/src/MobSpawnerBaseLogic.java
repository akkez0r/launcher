package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class MobSpawnerBaseLogic {
	public int spawnDelay = 20;
	private String mobID = "Pig";
	private List minecartToSpawn = null;
	private WeightedRandomMinecart randomMinecart = null;
	public double field_98287_c;
	public double field_98284_d = 0.0D;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	private Entity field_98291_j;
	private int maxNearbyEntities = 6;
	private int activatingRangeFromPlayer = 16;
	private int spawnRange = 4;

	public String getEntityNameToSpawn() {
		if(this.getRandomMinecart() == null) {
			if(this.mobID.equals("Minecart")) {
				this.mobID = "MinecartRideable";
			}

			return this.mobID;
		} else {
			return this.getRandomMinecart().minecartName;
		}
	}

	public void setMobID(String var1) {
		this.mobID = var1;
	}

	public boolean canRun() {
		return this.getSpawnerWorld().getClosestPlayer((double)this.getSpawnerX() + 0.5D, (double)this.getSpawnerY() + 0.5D, (double)this.getSpawnerZ() + 0.5D, (double)this.activatingRangeFromPlayer) != null;
	}

	public void updateSpawner() {
		if(this.canRun()) {
			double var5;
			if(this.getSpawnerWorld().isRemote) {
				double var12 = (double)((float)this.getSpawnerX() + this.getSpawnerWorld().rand.nextFloat());
				double var13 = (double)((float)this.getSpawnerY() + this.getSpawnerWorld().rand.nextFloat());
				var5 = (double)((float)this.getSpawnerZ() + this.getSpawnerWorld().rand.nextFloat());
				this.getSpawnerWorld().spawnParticle("smoke", var12, var13, var5, 0.0D, 0.0D, 0.0D);
				this.getSpawnerWorld().spawnParticle("flame", var12, var13, var5, 0.0D, 0.0D, 0.0D);
				if(this.spawnDelay > 0) {
					--this.spawnDelay;
				}

				this.field_98284_d = this.field_98287_c;
				this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
			} else {
				if(this.spawnDelay == -1) {
					this.func_98273_j();
				}

				if(this.spawnDelay > 0) {
					--this.spawnDelay;
					return;
				}

				boolean var1 = false;
				int var2 = 0;

				while(true) {
					if(var2 >= this.spawnCount) {
						if(var1) {
							this.func_98273_j();
						}
						break;
					}

					Entity var3 = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
					if(var3 == null) {
						return;
					}

					int var4 = this.getSpawnerWorld().getEntitiesWithinAABB(var3.getClass(), AxisAlignedBB.getAABBPool().getAABB((double)this.getSpawnerX(), (double)this.getSpawnerY(), (double)this.getSpawnerZ(), (double)(this.getSpawnerX() + 1), (double)(this.getSpawnerY() + 1), (double)(this.getSpawnerZ() + 1)).expand((double)(this.spawnRange * 2), 4.0D, (double)(this.spawnRange * 2))).size();
					if(var4 >= this.maxNearbyEntities) {
						this.func_98273_j();
						return;
					}

					var5 = (double)this.getSpawnerX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
					double var7 = (double)(this.getSpawnerY() + this.getSpawnerWorld().rand.nextInt(3) - 1);
					double var9 = (double)this.getSpawnerZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
					EntityLiving var11 = var3 instanceof EntityLiving ? (EntityLiving)var3 : null;
					var3.setLocationAndAngles(var5, var7, var9, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
					if(var11 == null || var11.getCanSpawnHere()) {
						this.func_98265_a(var3);
						this.getSpawnerWorld().playAuxSFX(2004, this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), 0);
						if(var11 != null) {
							var11.spawnExplosionParticle();
						}

						var1 = true;
					}

					++var2;
				}
			}

		}
	}

	public Entity func_98265_a(Entity var1) {
		if(this.getRandomMinecart() != null) {
			NBTTagCompound var2 = new NBTTagCompound();
			var1.addEntityID(var2);
			Iterator var3 = this.getRandomMinecart().field_98222_b.getTags().iterator();

			while(var3.hasNext()) {
				NBTBase var4 = (NBTBase)var3.next();
				var2.setTag(var4.getName(), var4.copy());
			}

			var1.readFromNBT(var2);
			if(var1.worldObj != null) {
				var1.worldObj.spawnEntityInWorld(var1);
			}

			NBTTagCompound var10;
			for(Entity var9 = var1; var2.hasKey("Riding"); var2 = var10) {
				var10 = var2.getCompoundTag("Riding");
				Entity var5 = EntityList.createEntityByName(var10.getString("id"), this.getSpawnerWorld());
				if(var5 != null) {
					NBTTagCompound var6 = new NBTTagCompound();
					var5.addEntityID(var6);
					Iterator var7 = var10.getTags().iterator();

					while(var7.hasNext()) {
						NBTBase var8 = (NBTBase)var7.next();
						var6.setTag(var8.getName(), var8.copy());
					}

					var5.readFromNBT(var6);
					var5.setLocationAndAngles(var9.posX, var9.posY, var9.posZ, var9.rotationYaw, var9.rotationPitch);
					this.getSpawnerWorld().spawnEntityInWorld(var5);
					var9.mountEntity(var5);
				}

				var9 = var5;
			}
		} else if(var1 instanceof EntityLiving && var1.worldObj != null) {
			((EntityLiving)var1).initCreature();
			this.getSpawnerWorld().spawnEntityInWorld(var1);
		}

		return var1;
	}

	private void func_98273_j() {
		if(this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
			this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(var10003);
		}

		if(this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
			this.setRandomMinecart((WeightedRandomMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, (Collection)this.minecartToSpawn));
		}

		this.func_98267_a(1);
	}

	public void readFromNBT(NBTTagCompound var1) {
		this.mobID = var1.getString("EntityId");
		this.spawnDelay = var1.getShort("Delay");
		if(var1.hasKey("SpawnPotentials")) {
			this.minecartToSpawn = new ArrayList();
			NBTTagList var2 = var1.getTagList("SpawnPotentials");

			for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
				this.minecartToSpawn.add(new WeightedRandomMinecart(this, (NBTTagCompound)var2.tagAt(var3)));
			}
		} else {
			this.minecartToSpawn = null;
		}

		if(var1.hasKey("SpawnData")) {
			this.setRandomMinecart(new WeightedRandomMinecart(this, var1.getCompoundTag("SpawnData"), this.mobID));
		} else {
			this.setRandomMinecart((WeightedRandomMinecart)null);
		}

		if(var1.hasKey("MinSpawnDelay")) {
			this.minSpawnDelay = var1.getShort("MinSpawnDelay");
			this.maxSpawnDelay = var1.getShort("MaxSpawnDelay");
			this.spawnCount = var1.getShort("SpawnCount");
		}

		if(var1.hasKey("MaxNearbyEntities")) {
			this.maxNearbyEntities = var1.getShort("MaxNearbyEntities");
			this.activatingRangeFromPlayer = var1.getShort("RequiredPlayerRange");
		}

		if(var1.hasKey("SpawnRange")) {
			this.spawnRange = var1.getShort("SpawnRange");
		}

		if(this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
			this.field_98291_j = null;
		}

	}

	public void writeToNBT(NBTTagCompound var1) {
		var1.setString("EntityId", this.getEntityNameToSpawn());
		var1.setShort("Delay", (short)this.spawnDelay);
		var1.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
		var1.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
		var1.setShort("SpawnCount", (short)this.spawnCount);
		var1.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
		var1.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
		var1.setShort("SpawnRange", (short)this.spawnRange);
		if(this.getRandomMinecart() != null) {
			var1.setCompoundTag("SpawnData", (NBTTagCompound)this.getRandomMinecart().field_98222_b.copy());
		}

		if(this.getRandomMinecart() != null || this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
			NBTTagList var2 = new NBTTagList();
			if(this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
				Iterator var3 = this.minecartToSpawn.iterator();

				while(var3.hasNext()) {
					WeightedRandomMinecart var4 = (WeightedRandomMinecart)var3.next();
					var2.appendTag(var4.func_98220_a());
				}
			} else {
				var2.appendTag(this.getRandomMinecart().func_98220_a());
			}

			var1.setTag("SpawnPotentials", var2);
		}

	}

	public Entity func_98281_h() {
		if(this.field_98291_j == null) {
			Entity var1 = EntityList.createEntityByName(this.getEntityNameToSpawn(), (World)null);
			var1 = this.func_98265_a(var1);
			this.field_98291_j = var1;
		}

		return this.field_98291_j;
	}

	public boolean setDelayToMin(int var1) {
		if(var1 == 1 && this.getSpawnerWorld().isRemote) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	public WeightedRandomMinecart getRandomMinecart() {
		return this.randomMinecart;
	}

	public void setRandomMinecart(WeightedRandomMinecart var1) {
		this.randomMinecart = var1;
	}

	public abstract void func_98267_a(int var1);

	public abstract World getSpawnerWorld();

	public abstract int getSpawnerX();

	public abstract int getSpawnerY();

	public abstract int getSpawnerZ();
}
