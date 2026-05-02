package net.minecraft.src;

/**
 * Canonical CMC auth REST base ({@code cmc.api.base}), shared by skins, applet bootstrap, and similar.
 */
public final class CmcAuthBootstrap {
	static final String FALLBACK_AUTH_API_BASE = "https://auth.craviorsmp.com";

	private CmcAuthBootstrap() {
	}

	public static String resolveAuthApiBaseUrl() {
		String prop = System.getProperty("cmc.api.base");
		if(prop != null) {
			String t = prop.trim();
			if(t.length() > 0) {
				return trimTrailingSlash(t);
			}
		}

		return trimTrailingSlash(FALLBACK_AUTH_API_BASE);
	}

	static String trimTrailingSlash(String base) {
		int end = base.length();
		while(end > 1 && base.charAt(end - 1) == '/') {
			--end;
		}

		return base.substring(0, end);
	}
}
