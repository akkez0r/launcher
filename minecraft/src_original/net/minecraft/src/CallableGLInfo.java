package net.minecraft.src;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class CallableGLInfo implements Callable {
	final Minecraft mc;

	public CallableGLInfo(Minecraft var1) {
		this.mc = var1;
	}

	public String getTexturePack() {
		return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
	}

	public Object call() {
		return this.getTexturePack();
	}
}
