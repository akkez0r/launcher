package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.IllegalFormatException;
import java.util.Properties;
import java.util.TreeMap;

public class StringTranslate {
	private static StringTranslate instance = new StringTranslate("en_US");
	private Properties translateTable = new Properties();
	private TreeMap languageList;
	private TreeMap field_94521_d = new TreeMap();
	private String currentLanguage;
	private boolean isUnicode;

	public StringTranslate(String var1) {
		this.loadLanguageList();
		this.setLanguage(var1, false);
	}

	public static StringTranslate getInstance() {
		return instance;
	}

	private void loadLanguageList() {
		TreeMap var1 = new TreeMap();

		try {
			BufferedReader var2 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/languages.txt"), "UTF-8"));

			for(String var3 = var2.readLine(); var3 != null; var3 = var2.readLine()) {
				String[] var4 = var3.trim().split("=");
				if(var4 != null && var4.length == 2) {
					var1.put(var4[0], var4[1]);
				}
			}
		} catch (IOException var5) {
			var5.printStackTrace();
			return;
		}

		this.languageList = var1;
		this.languageList.put("en_US", "English (US)");
	}

	public TreeMap getLanguageList() {
		return this.languageList;
	}

	private void loadLanguage(Properties var1, String var2) throws IOException {
		BufferedReader var3 = null;
		if(this.field_94521_d.containsKey(var2)) {
			var3 = new BufferedReader(new FileReader((File)this.field_94521_d.get(var2)));
		} else {
			var3 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/" + var2 + ".lang"), "UTF-8"));
		}

		for(String var4 = var3.readLine(); var4 != null; var4 = var3.readLine()) {
			var4 = var4.trim();
			if(!var4.startsWith("#")) {
				String[] var5 = var4.split("=");
				if(var5 != null && var5.length == 2) {
					var1.setProperty(var5[0], var5[1]);
				}
			}
		}

	}

	public synchronized void setLanguage(String var1, boolean var2) {
		if(var2 || !var1.equals(this.currentLanguage)) {
			Properties var3 = new Properties();

			try {
				this.loadLanguage(var3, "en_US");
			} catch (IOException var9) {
			}

			this.isUnicode = false;
			if(!"en_US".equals(var1)) {
				try {
					this.loadLanguage(var3, var1);
					Enumeration var4 = var3.propertyNames();

					label49:
					while(true) {
						while(true) {
							Object var6;
							do {
								if(!var4.hasMoreElements() || this.isUnicode) {
									break label49;
								}

								Object var5 = var4.nextElement();
								var6 = var3.get(var5);
							} while(var6 == null);

							String var7 = var6.toString();

							for(int var8 = 0; var8 < var7.length(); ++var8) {
								if(var7.charAt(var8) >= 256) {
									this.isUnicode = true;
									break;
								}
							}
						}
					}
				} catch (IOException var10) {
					var10.printStackTrace();
					return;
				}
			}

			this.currentLanguage = var1;
			this.translateTable = var3;
		}
	}

	public String getCurrentLanguage() {
		return this.currentLanguage;
	}

	public boolean isUnicode() {
		return this.isUnicode;
	}

	public synchronized String translateKey(String var1) {
		return this.translateTable.getProperty(var1, var1);
	}

	public synchronized String translateKeyFormat(String var1, Object... var2) {
		String var3 = this.translateTable.getProperty(var1, var1);

		try {
			return String.format(var3, var2);
		} catch (IllegalFormatException var5) {
			return "Format error: " + var3;
		}
	}

	public synchronized boolean containsTranslateKey(String var1) {
		return this.translateTable.containsKey(var1);
	}

	public synchronized String translateNamedKey(String var1) {
		return this.translateTable.getProperty(var1 + ".name", "");
	}

	public static boolean isBidirectional(String var0) {
		return "ar_SA".equals(var0) || "he_IL".equals(var0);
	}

	public synchronized void func_94519_a(String var1, File var2) {
		int var3 = var1.indexOf(46);
		if(var3 > 0) {
			var1 = var1.substring(0, var3);
		}

		this.field_94521_d.put(var1, var2);
		if(var1.contains(this.currentLanguage)) {
			this.setLanguage(this.currentLanguage, true);
		}

	}
}
