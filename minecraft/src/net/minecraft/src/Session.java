package net.minecraft.src;

public class Session {
	public String username;
	public String sessionId;
	public String cmcAccessToken;
	public String cmcUuid;

	public Session(String username, String sessionId) {
		this(username, sessionId, "", "");
	}

	public Session(String username, String sessionId, String cmcAccessToken) {
		this(username, sessionId, cmcAccessToken, "");
	}

	public Session(String username, String sessionId, String cmcAccessToken, String cmcUuid) {
		this.username = username;
		this.sessionId = sessionId;
		this.cmcAccessToken = cmcAccessToken != null ? cmcAccessToken : "";
		this.cmcUuid = cmcUuid != null ? cmcUuid : "";
	}
}
