package net.minecraft.client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import net.minecraft.src.CanvasMinecraftApplet;
import net.minecraft.src.MinecraftAppletImpl;
import net.minecraft.src.Session;

public class MinecraftApplet extends Applet {
	private Canvas mcCanvas;
	private Minecraft mc;
	private Thread mcThread = null;

	public void init() {
		this.mcCanvas = new CanvasMinecraftApplet(this);
		boolean var1 = "true".equalsIgnoreCase(this.getParameter("fullscreen"));
		this.mc = new MinecraftAppletImpl(this, this.mcCanvas, this, this.getWidth(), this.getHeight(), var1);
		this.mc.minecraftUri = this.getDocumentBase().getHost();
		if(this.getDocumentBase().getPort() > 0) {
			this.mc.minecraftUri = this.mc.minecraftUri + ":" + this.getDocumentBase().getPort();
		}

		if(this.getParameter("username") != null && this.getParameter("sessionid") != null) {
			String token = sanitizeCmcAccessTokenParameter(this.getParameter("accessToken"));
			if(token.isEmpty()) {
				token = sanitizeCmcAccessTokenParameter(this.getParameter("sessionToken"));
			}
			if(token.isEmpty()) {
				token = sanitizeCmcAccessTokenParameter(System.getProperty("cmc.accessToken"));
			}

			String uuid = sanitizeCmcUuidParameter(this.getParameter("uuid"));
			if(uuid.isEmpty()) {
				uuid = sanitizeCmcUuidParameter(System.getProperty("cmc.uuid"));
			}

			this.mc.session = new Session(this.getParameter("username"), this.getParameter("sessionid"), token, uuid);
			this.mc.getLogAgent().logInfo("Setting user: " + this.mc.session.username);
			System.out.println("(Session ID is " + this.mc.session.sessionId + ")");
		} else {
			this.mc.session = new Session("Player", "", "", "");
		}

		this.mc.setDemo("true".equals(this.getParameter("demo")));
		if(this.getParameter("server") != null && this.getParameter("port") != null) {
			this.mc.setServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
		}

		this.mc.hideQuitButton = !"true".equals(this.getParameter("stand-alone"));
		this.setLayout(new BorderLayout());
		this.add(this.mcCanvas, "Center");
		this.mcCanvas.setFocusable(true);
		this.mcCanvas.setFocusTraversalKeysEnabled(false);
		this.validate();
	}

	public void startMainThread() {
		if(this.mcThread == null) {
			this.mcThread = new Thread(this.mc, "Minecraft main thread");
			this.mcThread.start();
		}
	}

	public void start() {
		if(this.mc != null) {
			this.mc.isGamePaused = false;
		}

	}

	public void stop() {
		if(this.mc != null) {
			this.mc.isGamePaused = true;
		}

	}

	public void destroy() {
		this.shutdown();
	}

	public void shutdown() {
		if(this.mcThread != null) {
			this.mc.shutdown();

			try {
				this.mcThread.join(10000L);
			} catch (InterruptedException var4) {
				try {
					this.mc.shutdownMinecraftApplet();
				} catch (Exception var3) {
					var3.printStackTrace();
				}
			}

			this.mcThread = null;
		}
	}

	private static String sanitizeCmcAccessTokenParameter(String raw) {
		if(raw == null) {
			return "";
		}

		String trimmed = raw.trim();
		if(trimmed.isEmpty() || trimmed.equals("-")) {
			return "";
		}

		return trimmed;
	}

	private static String sanitizeCmcUuidParameter(String raw) {
		if(raw == null) {
			return "";
		}

		String trimmed = raw.trim();
		if(trimmed.isEmpty() || trimmed.equals("-")) {
			return "";
		}

		return trimmed;
	}
}
