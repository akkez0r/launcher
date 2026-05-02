package net.minecraft.src;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class CallableParticleScreenName implements Callable {
	final Minecraft theMinecraft;

	public CallableParticleScreenName(Minecraft var1) {
		this.theMinecraft = var1;
	}

	public String callParticleScreenName() {
		return this.theMinecraft.currentScreen.getClass().getCanonicalName();
	}

	public Object call() {
		return this.callParticleScreenName();
	}
}
