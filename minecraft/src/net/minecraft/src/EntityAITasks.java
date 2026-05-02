package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityAITasks {
	private List taskEntries = new ArrayList();
	private List executingTaskEntries = new ArrayList();
	private final Profiler theProfiler;
	private int field_75778_d = 0;
	private int field_75779_e = 3;

	public EntityAITasks(Profiler var1) {
		this.theProfiler = var1;
	}

	public void addTask(int var1, EntityAIBase var2) {
		this.taskEntries.add(new EntityAITaskEntry(this, var1, var2));
	}

	public void removeTask(EntityAIBase var1) {
		Iterator var2 = this.taskEntries.iterator();

		while(var2.hasNext()) {
			EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
			EntityAIBase var4 = var3.action;
			if(var4 == var1) {
				if(this.executingTaskEntries.contains(var3)) {
					var4.resetTask();
					this.executingTaskEntries.remove(var3);
				}

				var2.remove();
			}
		}

	}

	public void onUpdateTasks() {
		ArrayList var1 = new ArrayList();
		Iterator var2;
		EntityAITaskEntry var3;
		if(this.field_75778_d++ % this.field_75779_e == 0) {
			var2 = this.taskEntries.iterator();

			label59:
			while(true) {
				while(true) {
					if(!var2.hasNext()) {
						break label59;
					}

					var3 = (EntityAITaskEntry)var2.next();
					boolean var4 = this.executingTaskEntries.contains(var3);
					if(!var4) {
						break;
					}

					if(!this.canUse(var3) || !this.canContinue(var3)) {
						var3.action.resetTask();
						this.executingTaskEntries.remove(var3);
						break;
					}
				}

				if(this.canUse(var3) && var3.action.shouldExecute()) {
					var1.add(var3);
					this.executingTaskEntries.add(var3);
				}
			}
		} else {
			var2 = this.executingTaskEntries.iterator();

			while(var2.hasNext()) {
				var3 = (EntityAITaskEntry)var2.next();
				if(!var3.action.continueExecuting()) {
					var3.action.resetTask();
					var2.remove();
				}
			}
		}

		this.theProfiler.startSection("goalStart");
		var2 = var1.iterator();

		while(var2.hasNext()) {
			var3 = (EntityAITaskEntry)var2.next();
			this.theProfiler.startSection(var3.action.getClass().getSimpleName());
			var3.action.startExecuting();
			this.theProfiler.endSection();
		}

		this.theProfiler.endSection();
		this.theProfiler.startSection("goalTick");
		var2 = this.executingTaskEntries.iterator();

		while(var2.hasNext()) {
			var3 = (EntityAITaskEntry)var2.next();
			var3.action.updateTask();
		}

		this.theProfiler.endSection();
	}

	private boolean canContinue(EntityAITaskEntry var1) {
		this.theProfiler.startSection("canContinue");
		boolean var2 = var1.action.continueExecuting();
		this.theProfiler.endSection();
		return var2;
	}

	private boolean canUse(EntityAITaskEntry var1) {
		this.theProfiler.startSection("canUse");
		Iterator var2 = this.taskEntries.iterator();

		while(var2.hasNext()) {
			EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
			if(var3 != var1) {
				if(var1.priority >= var3.priority) {
					if(this.executingTaskEntries.contains(var3) && !this.areTasksCompatible(var1, var3)) {
						this.theProfiler.endSection();
						return false;
					}
				} else if(this.executingTaskEntries.contains(var3) && !var3.action.isInterruptible()) {
					this.theProfiler.endSection();
					return false;
				}
			}
		}

		this.theProfiler.endSection();
		return true;
	}

	private boolean areTasksCompatible(EntityAITaskEntry var1, EntityAITaskEntry var2) {
		return (var1.action.getMutexBits() & var2.action.getMutexBits()) == 0;
	}
}
