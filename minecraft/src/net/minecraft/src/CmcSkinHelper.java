package net.minecraft.src;

import java.util.Locale;

/**
 * Loads the player's skin from the CMC auth service ({@code /skins/&lt;uuid&gt;.png}).
 * Legacy {@code skins.../MinecraftSkins/&lt;username&gt;} URLs are incompatible with CMC uploads; use UUID-based CMC paths only.
 */
public final class CmcSkinHelper {
	public static final String DEFAULT_PLAYER_SKIN_TEXTURE = "/mob/char.png";

	private CmcSkinHelper() {
	}

	public static String resolveSkinDownloadUrlFromSession(Session session) {
		String rawUuid = preferredCmcUuidRaw(session);
		String id = hyphenlessLowerHexUuid(rawUuid);
		if(id.length() == 0) {
			return null;
		}

		return CmcAuthBootstrap.trimTrailingSlash(resolveApiBase()) + "/skins/" + id + ".png";
	}

	/**
	 * Standalone launches may pass {@code -Dcmc.uuid} (see {@link net.minecraft.client.MinecraftApplet}) while the applet parameter
	 * or {@link Session#cmcUuid} is left empty; use the JVM property when the session has no usable id.
	 */
	private static String preferredCmcUuidRaw(Session session) {
		String fromSession = "";
		if(session != null && session.cmcUuid != null) {
			fromSession = session.cmcUuid.trim();
		}

		String fromJvm = "";
		String prop = System.getProperty("cmc.uuid");
		if(prop != null) {
			fromJvm = prop.trim();
		}

		return !fromSession.isEmpty() ? fromSession : fromJvm;
	}

	private static String resolveApiBase() {
		return CmcAuthBootstrap.resolveAuthApiBaseUrl();
	}

	static String hyphenlessLowerHexUuid(String raw) {
		if(raw == null) {
			return "";
		}

		String stripped = raw.trim().replace("-", "").toLowerCase(Locale.US);
		if(stripped.length() != 32) {
			return "";
		}

		for(int i = 0; i < stripped.length(); ++i) {
			char c = stripped.charAt(i);
			if((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
				return "";
			}
		}

		return stripped;
	}
}
