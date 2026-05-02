package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

class PlayerUsageSnooperThread extends TimerTask {
	final PlayerUsageSnooper snooper;

	PlayerUsageSnooperThread(PlayerUsageSnooper var1) {
		this.snooper = var1;
	}

	public void run() {
		if(PlayerUsageSnooper.getStatsCollectorFor(this.snooper).isSnooperEnabled()) {
			Object var2 = PlayerUsageSnooper.getSyncLockFor(this.snooper);
			HashMap var1;
			synchronized(var2) {
				var1 = new HashMap(PlayerUsageSnooper.getDataMapFor(this.snooper));
				var1.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.getSelfCounterFor(this.snooper)));
			}

			HttpUtil.sendPost(PlayerUsageSnooper.getStatsCollectorFor(this.snooper).getLogAgent(), PlayerUsageSnooper.getServerUrlFor(this.snooper), (Map)var1, true);
		}
	}
}
